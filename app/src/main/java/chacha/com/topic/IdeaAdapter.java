package chacha.com.topic;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by Cha on 2017-07-08.
 */

public class IdeaAdapter extends RecyclerView.Adapter<IdeaAdapter.ViewHolder> {

    String TAG = "IdeaAdapter";
    private ArrayList<Idea> ideaList;
    private ArrayList<String> ideaIdList;
    private Context mContext;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference;
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
    private StorageReference mStorageReference;
    private AlertDialog dialog;
    private User user;

    public IdeaAdapter(ArrayList<Idea> ideaList, ArrayList<String> idealIdList, Context mContext) {
        this.ideaList = ideaList;
        this.ideaIdList = idealIdList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_idea, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (mFirebaseUser.getUid().equals(ideaList.get(position).uid)) {
            holder.ib_menu.setVisibility(View.VISIBLE);
            holder.ib_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                    View mView = LayoutInflater.from(mContext).inflate(R.layout.dialog, null);
                    Button btn_edit = (Button) mView.findViewById(R.id.btn_edit);
                    Button btn_delete = (Button) mView.findViewById(R.id.btn_delete);

                    btn_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Idea idea = ideaList.get(position);
                            String id = ideaIdList.get(position);
                            mDatabaseReference = mFirebaseDatabase.getReference("Cities").child("Paris").child(id);
                            mDatabaseReference.removeValue();
                            if (!idea.contentImageUrl.equals("")) {
                                mStorageReference = mFirebaseStorage.getReferenceFromUrl(idea.contentImageUrl);
                                mStorageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
//                                        Toast.makeText(mContext, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            Toast.makeText(mContext, "삭제하였습니다.", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });

                    btn_edit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(mContext, EditContentActivity.class);
                            Idea idea = ideaList.get(position);
                            intent.putExtra("Id", ideaIdList.get(position));
                            intent.putExtra("Idea", idea);
                            mContext.startActivity(intent);
                            dialog.dismiss();
                        }
                    });

                    mBuilder.setView(mView);
                    dialog = mBuilder.create();
                    dialog.show();
                }
            });
        } else {
            holder.ib_menu.setVisibility(View.GONE);
            Log.d(TAG, "@@ else position : " + String.valueOf(position));
        }

        if (TextUtils.isEmpty(ideaList.get(position).content)) {
            holder.tvContent.setVisibility(View.GONE);
        } else {
            holder.tvContent.setVisibility(View.VISIBLE);
            holder.tvContent.setText(ideaList.get(position).content);
        }

        if (TextUtils.isEmpty(ideaList.get(position).contentImageUrl)) {
            Glide.with(mContext).load("").fitCenter().into(holder.ivContent);
        } else {
            Glide
                    .with(mContext)
                    .load(ideaList.get(position).contentImageUrl)
//                    .thumbnail( 0.1f )
                    .fitCenter()
                    .into(holder.ivContent);
        }

        mDatabaseReference = mFirebaseDatabase.getReference("user").child("profile").child(ideaList.get(position).uid);
        callProfile(mDatabaseReference, holder);

        //좋아요 뷰
        holder.btnHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseReference = mFirebaseDatabase.getReference("Cities").child("Paris").child(ideaIdList.get(position)).child("Content");
                clickedLove(mDatabaseReference);
            }
        });
        holder.tvContentHeart.setText(String.valueOf(ideaList.get(position).loveCount));


    }

    @Override
    public int getItemCount() {
        return ideaList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvProfileName;
        private TextView tvContentHeart;
        private TextView tvContent;
        private ImageView ivProfilePhoto;
        private ImageView ivContent;
        private Button btnHeart;
        private ImageButton ib_menu;

        public ViewHolder(View itemView) {
            super(itemView);
            tvProfileName = (TextView) itemView.findViewById(R.id.tvProfileName);
            tvContentHeart = (TextView) itemView.findViewById(R.id.tvContentHeart);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            ivProfilePhoto = (ImageView) itemView.findViewById(R.id.ivProfilePhoto);
            ivContent = (ImageView) itemView.findViewById(R.id.ivContent);
            btnHeart = (Button) itemView.findViewById(R.id.btnHeart);
            ib_menu = (ImageButton) itemView.findViewById(R.id.ib_menu);
        }
    }

    public void clickedLove(DatabaseReference ref) {
        ref.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Idea idea = mutableData.getValue(Idea.class);
                if (idea == null) {
                    return Transaction.success(mutableData);
                }
                if (idea.loves.containsKey(mFirebaseUser.getUid())) {
                    idea.loveCount = idea.loveCount - 1;
                    idea.descCount = idea.descCount + 1;
                    idea.loves.remove(mFirebaseUser.getUid());
//                    Toast.makeText(mContext, "좋아요!", Toast.LENGTH_SHORT).show();
                } else {
                    idea.loveCount = idea.loveCount + 1;
                    idea.descCount = idea.descCount - 1;
                    idea.loves.put(mFirebaseUser.getUid(), true);
//                    Toast.makeText(mContext, "좋아요 취소", Toast.LENGTH_SHORT).show();
                }
                mutableData.setValue(idea);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d(TAG, "@@ loveTransaction:onComplete:" + databaseError);
            }
        });
    }

    public void callProfile(DatabaseReference ref, final ViewHolder holder) {

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);

                if(user!=null){
                    if (TextUtils.isEmpty(user.Name)) {
                        holder.tvProfileName.setText(user.Email);
                    } else {
                        holder.tvProfileName.setText(user.Name);
                    }
                    if (TextUtils.isEmpty(user.PhotoUrl)) {
                        Glide.with(mContext).load(R.drawable.userplaceholder).fitCenter().into(holder.ivProfilePhoto);
                    } else {
                        Glide.with(mContext).load(user.PhotoUrl).into(holder.ivProfilePhoto);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
