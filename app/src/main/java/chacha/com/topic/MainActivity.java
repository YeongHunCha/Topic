package chacha.com.topic;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference;

    private long profileCount;
    private boolean existSameEmail;

    private RecyclerView.Adapter mAdapter;

    private ArrayList<User> userList;
    private ArrayList<Idea> ideaList;
    private ArrayList<String> ideaIdList;
    private ArrayList<String> hearts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userList = new ArrayList<>();
        ideaList = new ArrayList<>();
        ideaIdList = new ArrayList<>();
        hearts = new ArrayList<>();
        existSameEmail = false;

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

        //Firebase에 user가 있는지 체크하고 없으면 생성
        mDatabaseReference = mFirebaseDatabase.getReference("user");
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                profileCount = dataSnapshot.child("profile").getChildrenCount();

                //profile에 아무것도 없으면 addUser호출
                if(profileCount==0){
                    addUser();
                }else{
                    //profile에 user들이 존재할 때 현재 유저가 존재하는지 조사
                    checkExistSameEmail();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //RecyclerView 셋업
        RecyclerView rv = (RecyclerView)findViewById(R.id.rv);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(MainActivity.this, 1, false);
        rv.setLayoutManager(lm);
        rv.setHasFixedSize(true);
        mAdapter = new IdeaAdapter(ideaList, ideaIdList, MainActivity.this);
        rv.setAdapter(mAdapter);

        //floatingBtn
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.btnAddPost);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WritingActivity.class));
            }
        });

        //content load from firebase
        loadContents();
    }

    public void addUser(){  //현재 유저를 firebase user/profile카테고리에 삽입

        mDatabaseReference = mFirebaseDatabase.getReference("user").child("profile").push();
        HashMap<String, String> userData= new HashMap<String, String>();
        if(mFirebaseUser.getDisplayName()==null){
            userData.put("Name", mFirebaseUser.getEmail());
        }else{
            userData.put("Name", mFirebaseUser.getDisplayName());
        }
        userData.put("Email", mFirebaseUser.getEmail());
        if(mFirebaseUser.getPhotoUrl()==null){
            userData.put("PhotoUrl", "");
        }
        else{
            userData.put("PhotoUrl", String.valueOf(mFirebaseUser.getPhotoUrl()));
        }
        mDatabaseReference.setValue(userData);
    }

    public void checkExistSameEmail(){ //현재 유저가 firebase에 있는지 체크

        mDatabaseReference = mFirebaseDatabase.getReference("user").child("profile");
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                userList.add(user);
                for(User e : userList){
                    if(e.Email.equals(mFirebaseUser.getEmail())){
                        existSameEmail=true;
                        break;
                    }
                }
                //같은 이메일이 존재하지 않을 경우 firebase에 현재유저의 정보를 저장하는 메소드 호출
                if(!existSameEmail&&profileCount==userList.size()){
                    addUser();
                    return;
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {            }
            @Override
            public void onCancelled(DatabaseError databaseError) {            }
        });
    }

    public void loadContents() {
        mDatabaseReference = mFirebaseDatabase.getReference("subject");
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Idea idea = dataSnapshot.child("content").getValue(Idea.class);
                String ideaId = dataSnapshot.getKey();
                ideaList.add(idea);
                ideaIdList.add(ideaId);
                mAdapter.notifyDataSetChanged();
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
