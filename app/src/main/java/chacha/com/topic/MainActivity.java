package chacha.com.topic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();

    private RecyclerView.Adapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //로그인이 되어 있지 않으면 로그인 페이지로 이동
        if(mFirebaseUser==null){
            startActivity(new Intent(this, LoginActivity.class));
        }

        //로그아웃 기능
        Button btn = (Button)findViewById(R.id.btnLogout);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        ArrayList<String> sList = new ArrayList<>();
        sList.add("1");
        sList.add("2");
        sList.add("3");

        //RecyclerView 셋업
        RecyclerView rv = (RecyclerView)findViewById(R.id.rv);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(MainActivity.this, 1, false);
        rv.setLayoutManager(lm);
        rv.setHasFixedSize(true);
        mAdapter = new IdeaAdapter(this, sList);
        rv.setAdapter(mAdapter);
    }
}
