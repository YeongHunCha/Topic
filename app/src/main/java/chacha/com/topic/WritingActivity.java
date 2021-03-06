package chacha.com.topic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writing);

        btnAddPhoto = (Button)findViewById(R.id.btnAddPhoto);
        btnConfirm = (Button)findViewById(R.id.btnConfirm);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        etIdea = (EditText)findViewById(R.id.etIdea);
        ivIdea = (ImageView)findViewById(R.id.ivIdea);

        mDatabaseReference = mFirebaseDatabase.getReference("Cities").child(Singleton.getInstance().getCity()).push().child("Content");
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
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                final String strDate = sdf.format(c.getTime());
                final String timeStamp = String.valueOf(Long.parseLong("99999999999999")-Long.parseLong(strDate));
                final String content = etIdea.getText().toString();
                if(uri==null){
                    Idea idea = new Idea(content, "", mFirebaseUser.getUid(), strDate, timeStamp);
                    mDatabaseReference.setValue(idea.toMap());
                    finish();
                } else {
                    mStorageReference.child("Contents").child(mFirebaseUser.getEmail()).child(uri.getLastPathSegment()).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //noinspection VisibleForTests
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Idea idea = new Idea(content, String.valueOf(downloadUrl), mFirebaseUser.getUid(), strDate, timeStamp);
                            mDatabaseReference.setValue(idea.toMap());
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
}
