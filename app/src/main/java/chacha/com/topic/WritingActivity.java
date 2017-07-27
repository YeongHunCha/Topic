package chacha.com.topic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class WritingActivity extends AppCompatActivity {
    private String TAG = "WritingActivity";
    private Button btnAddPhoto;
    private Button btnConfirm;
    private Button btnCancel;
    private EditText etIdea;
    private ImageView ivIdea;

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
    private FirebaseDatabase mFirebaseDatabase= FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference = FirebaseStorage.getInstance().getReference();
    private Uri uri=null;

    private ArrayList<User> userList;

    private String currentUserName = "";
    private String currentUserPhotoUrl = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

        Log.d(TAG, "@@ mF.getEmail : "+mFirebaseUser.getEmail());

        userList = new ArrayList<>();
        callProfile();

        btnAddPhoto = (Button)findViewById(R.id.btnAddPhoto);
        btnConfirm = (Button)findViewById(R.id.btnConfirm);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        etIdea = (EditText)findViewById(R.id.etIdea);
        ivIdea = (ImageView)findViewById(R.id.ivIdea);

        mDatabaseReference = mFirebaseDatabase.getReference("subject");
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressBar pb  = (ProgressBar)findViewById(R.id.pb);
                pb.setVisibility(View.VISIBLE);
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                final String strDate = sdf.format(c.getTime());
                final String idea = etIdea.getText().toString();
                if(uri==null){
                    HashMap<String, String> post = new HashMap<String, String>();
                    post.put("ContentImageUrl", "");
                    post.put("Content", idea);
                    post.put("ProfileName", currentUserName);
                    post.put("ProfilePhoto", currentUserPhotoUrl);
                    post.put("Writer", mFirebaseUser.getEmail());
                    mDatabaseReference.child(strDate).child("content").setValue(post);
                    finish();
                } else {
                    mStorageReference.child("subject").child(mFirebaseUser.getEmail()).child(uri.getLastPathSegment()).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //noinspection VisibleForTests
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            HashMap<String, String> post = new HashMap<String, String>();
                            post.put("ContentImageUrl", String.valueOf(downloadUrl));
                            post.put("Content", idea);
                            post.put("ProfileName", currentUserName);
                            post.put("ProfilePhoto", currentUserPhotoUrl);
                            post.put("Writer", mFirebaseUser.getEmail());
                            mDatabaseReference.child(strDate).child("content").setValue(post);
                            pb.setVisibility(View.GONE);
                            finish();
                        }
                    });
                }
            }
        });
        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            uri = data.getData();
            Picasso.with(WritingActivity.this).load(uri).fit().centerCrop().into(ivIdea);
        }
    }

    public void callProfile(){
        mDatabaseReference = mFirebaseDatabase.getReference("user").child("profile");
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                userList.add(user);
                for(User e : userList){
                    Log.d(TAG, "@@ e.email: "+e.Email);
                    if(mFirebaseUser.getEmail().equals(e.Email)){
                        currentUserName = e.Name;
                        currentUserPhotoUrl = e.PhotoUrl;
                        Log.d(TAG, "@@ currentName : " +currentUserName);
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
