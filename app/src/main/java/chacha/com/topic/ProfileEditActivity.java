package chacha.com.topic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileEditActivity extends AppCompatActivity {

    private RelativeLayout rl_photo;
    private ImageView iv_profile_photo;
    private TextView tv_email;
    private EditText et_name;
    private Button btn_cancel;
    private Button btn_confirm;
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

        rl_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileEditActivity.this, "photo", Toast.LENGTH_SHORT).show();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileEditActivity.this, "cancel", Toast.LENGTH_SHORT).show();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileEditActivity.this, "confirm", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
