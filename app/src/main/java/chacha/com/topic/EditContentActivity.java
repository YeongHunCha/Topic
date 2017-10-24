package chacha.com.topic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditContentActivity extends AppCompatActivity {
    String TAG = "EditContentActivity";
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference;

    private String id;
    private Idea idea;
    private EditText etEditContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_content);

        Intent intent = getIntent();
        idea = (Idea)intent.getSerializableExtra("Idea");
        id = intent.getExtras().getString("Id");

        etEditContent = (EditText)findViewById(R.id.etEditContent);
        etEditContent.setText(idea.content);

        ImageView ivContent = (ImageView)findViewById(R.id.ivContent);
        Glide.with(getApplicationContext()).load(idea.contentImageUrl).centerCrop().into(ivContent);

        Button btnConfirm = (Button)findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View Fv) {
                idea.content = etEditContent.getText().toString();
                mDatabaseReference = mFirebaseDatabase.getReference("Cities").child("Paris").child(id).child("Content");
                mDatabaseReference.updateChildren(idea.toMap());
                finish();
            }
        });

        Button btnCancel = (Button)findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
