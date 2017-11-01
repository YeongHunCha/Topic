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
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class EditContentActivity extends AppCompatActivity {
    String TAG = "EditContentActivity";
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference;
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
    private StorageReference mStorageReference = mFirebaseStorage.getReference();

    private String id;
    private Idea idea;
    private EditText etEditContent;
    private ImageView ivContent;
    private ProgressBar pb;

    private Uri uri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_content);

        Intent intent = getIntent();
        idea = (Idea) intent.getSerializableExtra("Idea");
        id = intent.getExtras().getString("Id");

        etEditContent = (EditText) findViewById(R.id.etEditContent);
        etEditContent.setText(idea.content);
        pb = (ProgressBar) findViewById(R.id.pb);

        ivContent = (ImageView) findViewById(R.id.ivContent);
        Glide.with(getApplicationContext()).load(idea.contentImageUrl).centerCrop().into(ivContent);

        ivContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });
        Button btnConfirm = (Button) findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                //기존 이미지 삭제
                idea.content = etEditContent.getText().toString();
                mDatabaseReference = mFirebaseDatabase.getReference("Cities").child(Singleton.getInstance().getCity()).child(id).child("Content");
                if(uri==null){
                    mDatabaseReference.updateChildren(idea.toMap());
                    pb.setVisibility(View.GONE);
                    finish();
                } else {
                    if (idea.contentImageUrl.contains("https://firebasestorage.googleapis.com/v0/b/topic-5b5c9.appspot.com/o/Contents")) {
                        mStorageReference = mFirebaseStorage.getReferenceFromUrl(idea.contentImageUrl);
                        mStorageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditContentActivity.this, "이전 사진이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Log.d(TAG, "It's not on firebaseStorage");
                    }

                    //재 업로드
                    mFirebaseStorage.getReference().child("Contents").child(mFirebaseUser.getEmail()).child(uri.getLastPathSegment()).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //noinspection VisibleForTests
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            idea.contentImageUrl = String.valueOf(downloadUrl);
                            mDatabaseReference.updateChildren(idea.toMap());
                            pb.setVisibility(View.GONE);
                            finish();
                        }
                    });
                }

            }
        });

        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            uri = data.getData();
            Picasso.with(EditContentActivity.this).load(uri).fit().centerCrop().into(ivContent);
        }
    }
}
