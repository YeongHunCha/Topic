package chacha.com.topic;

import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileEditActivity extends AppCompatActivity {
    String TAG = "ProfileEditActivity";

    private RelativeLayout rl_photo;
    private ImageView iv_profile_photo;
    private TextView tv_email;
    private EditText et_name;
    private Button btn_cancel;
    private Button btn_confirm;
    private ProgressBar pb;

    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
    private StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
    User user;

    private Uri uri=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        rl_photo = (RelativeLayout)findViewById(R.id.rl_photo);
        iv_profile_photo = (ImageView)findViewById(R.id.iv_profile_photo_edit);
        tv_email = (TextView)findViewById(R.id.tv_email);
        et_name = (EditText)findViewById(R.id.et_name);
        btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_confirm = (Button)findViewById(R.id.btn_confirm);
        pb = (ProgressBar)findViewById(R.id.pb);

        mDatabaseReference = mFirebaseDatabase.getReference("user").child("profile").child(mFirebaseUser.getUid());
        callProfile(mDatabaseReference);

        rl_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                if(uri==null){
                    User user = new User(mFirebaseUser.getEmail(), et_name.getText().toString(), "");
                    mDatabaseReference.setValue(user.toMap());
                    finish();
                } else {
                    mStorageReference.child("Profile").child(mFirebaseUser.getUid()).child(uri.getLastPathSegment()).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //noinspection VisibleForTests
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            User user = new User(mFirebaseUser.getEmail(), et_name.getText().toString(), String.valueOf(downloadUrl));
                            mDatabaseReference.setValue(user.toMap());
                            pb.setVisibility(View.GONE);
                            finish();
                        }
                    });
                }
            }
        });
    }

    public void callProfile(DatabaseReference ref){
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                et_name.setText(user.Name);
                tv_email.setText(user.Email);
                if(TextUtils.isEmpty(user.PhotoUrl)){
                    Glide.with(ProfileEditActivity.this).load(R.drawable.userplaceholder).fitCenter().into(iv_profile_photo);
                } else {
                    Glide.with(ProfileEditActivity.this).load(user.PhotoUrl).fitCenter().into(iv_profile_photo);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            uri = data.getData();
            Picasso.with(ProfileEditActivity.this).load(uri).fit().centerCrop().into(iv_profile_photo);
        }
    }

}
