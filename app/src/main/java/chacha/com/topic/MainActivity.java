package chacha.com.topic;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private String TAG = "MainActivity";
    private BackPressCloseHandler backPressCloseHandler;

    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference;
    private DatabaseReference userDatabaseReference = mFirebaseDatabase.getReference();

    private long profileCount;
    private boolean existSameEmail;

    private NestedScrollView nsv;
    private RecyclerView rv;
    private RecyclerView.LayoutManager lm;
    private RecyclerView.Adapter mAdapter;

    //set profile
    private View v;
    private TextView tvName;
    private TextView tvEmail;
    private ImageView ivProfilePhoto;

    private ArrayList<User> userList;
    private ArrayList<Idea> ideaList;
    private ArrayList<String> ideaIdList;

    private String sort = "Content/createdAt";
    private ChildEventListener cel;
    private Query query;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Backpress Handler
        backPressCloseHandler = new BackPressCloseHandler(this);

        // navigation view
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView iv = (ImageView) findViewById(R.id.iv_bg);
        Glide.with(this).load(R.drawable.paris).fitCenter().into(iv);
        toolbar.setTitle("Paris");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        userList = new ArrayList<>();
        ideaList = new ArrayList<>();
        ideaIdList = new ArrayList<>();
        existSameEmail = false;

        //set profile
        v = navigationView.getHeaderView(0);
        tvName = (TextView) v.findViewById(R.id.tv_profile_name);
        tvEmail = (TextView) v.findViewById(R.id.tv_profile_email);
        ivProfilePhoto = (ImageView) v.findViewById(R.id.iv_profile_photo);

        //RecyclerView 셋업
        nsv = (NestedScrollView) findViewById(R.id.nsv);

        rv = (RecyclerView) findViewById(R.id.rv);
        lm = new LinearLayoutManager(MainActivity.this, 1, false);
        rv.setLayoutManager(lm);
        rv.setHasFixedSize(true);
        mAdapter = new IdeaAdapter(ideaList, ideaIdList, MainActivity.this);
        rv.setAdapter(mAdapter);
        rv.setNestedScrollingEnabled(false);

        //floatingBtn
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btnAddPost);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nsv.scrollTo(0, 0);
                startActivity(new Intent(MainActivity.this, WritingActivity.class));
            }
        });

        //로그인이 되어 있지 않으면 로그인 페이지로 이동
        if (mFirebaseUser == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            //Firebase에 user가 있는지 체크하고 없으면 생성
            mDatabaseReference = mFirebaseDatabase.getReference("user");
            mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    profileCount = dataSnapshot.child("profile").getChildrenCount();

                    //profile에 아무것도 없으면 addUser호출
                    if (profileCount == 0) {
                        addUser();
                    } else {
                        //profile에 user들이 존재할 때 현재 유저가 존재하는지 조사
                        checkExistSameEmail();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //set profile
            userDatabaseReference = mFirebaseDatabase.getReference("user").child("profile").child(mFirebaseUser.getUid());
            getProfile(userDatabaseReference);

            //content load from firebase
            loadContents("Content/timeStamp");
        }
    }

    public void addUser() {  //현재 유저를 firebase user/profile카테고리에 삽입

        mDatabaseReference = mFirebaseDatabase.getReference("user").child("profile").child(mFirebaseUser.getUid());
        HashMap<String, String> userData = new HashMap<String, String>();
        if (mFirebaseUser.getDisplayName() == null) {
            userData.put("Name", "");
        } else {
            userData.put("Name", mFirebaseUser.getDisplayName());
        }
        userData.put("Email", mFirebaseUser.getEmail());
        if (mFirebaseUser.getPhotoUrl() == null) {
            userData.put("PhotoUrl", "");
        } else {
            userData.put("PhotoUrl", String.valueOf(mFirebaseUser.getPhotoUrl()));
        }
        mDatabaseReference.setValue(userData);
    }

    public void checkExistSameEmail() { //현재 유저가 firebase에 있는지 체크

        mDatabaseReference = mFirebaseDatabase.getReference("user").child("profile");
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                userList.add(user);
                for (User e : userList) {
                    if (e.Email.equals(mFirebaseUser.getEmail())) {
                        existSameEmail = true;
                        break;
                    }
                }
                //같은 이메일이 존재하지 않을 경우 firebase에 현재유저의 정보를 저장하는 메소드 호출
                if (!existSameEmail && profileCount == userList.size()) {
                    addUser();
                    return;
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

    /*
     *   navigation view method
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
            backPressCloseHandler.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_past_topic) {

        } else if (id == R.id.nav_priority_all) {
            sort = "Content/createdAt";
            Toast.makeText(this, "최신 글을 우선 정렬합니다.", Toast.LENGTH_SHORT).show();
            query.removeEventListener(cel);
            loadContents(sort);
        } else if (id == R.id.nav_priority_rank) {
            sort = "Content/loveCount";
            query.removeEventListener(cel);
            Toast.makeText(this, "인기있는 글을 우선 정렬합니다.", Toast.LENGTH_SHORT).show();
            loadContents(sort);
        } else if (id == R.id.nav_profile_edit) {
            startActivity(new Intent(this, ProfileEditActivity.class));
        } else if (id == R.id.nav_profile_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final AlertDialog dialog = builder.setMessage("로그아웃 하시겠습니까?")
                    .setPositiveButton("네, 로그아웃하겠습니다.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mFirebaseAuth.signOut();
                            finish();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            Toast.makeText(MainActivity.this, "로그아웃 하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("취소", null)
                    .create();


            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#ec407a"));
                }
            });
            dialog.show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadContents(String sort) {
        ideaList.clear();
        ideaIdList.clear();
        nsv.scrollTo(0, 0);
        mDatabaseReference = mFirebaseDatabase.getReference("Cities").child("Paris");
        query = mDatabaseReference.orderByChild(sort);
        cel = query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Idea idea = dataSnapshot.child("Content").getValue(Idea.class);
                String ideaId = dataSnapshot.getKey();
                ideaList.add(0, idea);
                ideaIdList.add(0, ideaId);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Idea idea = dataSnapshot.child("Content").getValue(Idea.class);
                int index = ideaIdList.indexOf(dataSnapshot.getKey());
                ideaList.set(index, idea);
                mAdapter.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                int index = ideaIdList.indexOf(dataSnapshot.getKey());
                ideaList.remove(index);
                ideaIdList.remove(index);
                mAdapter.notifyItemRemoved(index);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getProfile(DatabaseReference ref) {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    if (TextUtils.isEmpty(user.Name)) {
                        tvName.setText("* 이름 없음. 프로필을 수정 해주세요!");
                    } else {
                        tvName.setText(user.Name);
                    }
                    tvEmail.setText(mFirebaseUser.getEmail());
                    if (TextUtils.isEmpty(user.PhotoUrl)) {
                        Glide.with(MainActivity.this).load(R.drawable.userplaceholder).fitCenter().into(ivProfilePhoto);
                    } else {
                        Glide.with(MainActivity.this).load(user.PhotoUrl).fitCenter().into(ivProfilePhoto);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
