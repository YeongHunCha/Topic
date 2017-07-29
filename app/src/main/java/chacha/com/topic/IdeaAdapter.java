package chacha.com.topic;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


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


    public IdeaAdapter(ArrayList<Idea> ideaList,ArrayList<String> idealIdList, Context mContext) {
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
        if(mFirebaseUser.getEmail().equals(ideaList.get(position).Writer)){
            holder.btn_edit.setVisibility(View.VISIBLE);
            holder.btn_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, EditContentActivity.class);
                    Idea idea = ideaList.get(position);
                    intent.putExtra("Content", idea.Content);
                    intent.putExtra("ContentImageUrl", idea.ContentImageUrl);
                    intent.putExtra("Id", ideaIdList.get(position));
                    intent.putExtra("Idea", idea);
                    mContext.startActivity(intent);
                }
            });
            holder.btn_delete.setVisibility(View.VISIBLE);
            holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Idea idea = ideaList.get(position);
                    String id = ideaIdList.get(position);
                    mDatabaseReference = mFirebaseDatabase.getReference("subject").child(id);
                    mDatabaseReference.removeValue();
                    if(!idea.ContentImageUrl.equals("")){
                        mStorageReference = mFirebaseStorage.getReferenceFromUrl(idea.ContentImageUrl);
                        mStorageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(mContext, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        holder.tvContent.setText(ideaList.get(position).Content);
        holder.tvContentHeart.setText("12345");
        holder.tvProfileName.setText(ideaList.get(position).ProfileName);

        if(ideaList.get(position).ContentImageUrl==null|| TextUtils.isEmpty(ideaList.get(position).ContentImageUrl)){
            Glide.with(mContext).load("").fitCenter().into(holder.ivContent);
        }else{
            Glide
                    .with(mContext)
                    .load(ideaList.get(position).ContentImageUrl)
                    .thumbnail( 0.1f )
                    .into(holder.ivContent);
        }

        if(ideaList.get(position).ProfilePhoto==null||TextUtils.isEmpty(ideaList.get(position).ProfilePhoto)){
            Glide.with(mContext).load(R.drawable.userplaceholder).fitCenter().into(holder.ivProfilePhoto);
        }else{
            Glide.with(mContext).load(ideaList.get(position).ProfilePhoto).into(holder.ivProfilePhoto);
        }


        holder.btnHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "좋아요!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ideaList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvProfileName;
        private TextView tvContentHeart;
        private TextView tvContent;
        private ImageView ivProfilePhoto;
        private ImageView ivContent;
        private Button btnHeart;
        private Button btnComment;
        private Button btn_edit;
        private Button btn_delete;

        public ViewHolder(View itemView) {
            super(itemView);
            tvProfileName = (TextView)itemView.findViewById(R.id.tvProfileName);
            tvContentHeart = (TextView)itemView.findViewById(R.id.tvContentHeart);
            tvContent = (TextView)itemView.findViewById(R.id.tvContent);
            ivProfilePhoto = (ImageView)itemView.findViewById(R.id.ivProfilePhoto);
            ivContent = (ImageView)itemView.findViewById(R.id.ivContent);
            btnHeart = (Button)itemView.findViewById(R.id.btnHeart);
            btnComment = (Button)itemView.findViewById(R.id.btnComment);
            btn_edit = (Button)itemView.findViewById(R.id.btn_edit);
            btn_delete = (Button)itemView.findViewById(R.id.btn_delete);
        }
    }
}
