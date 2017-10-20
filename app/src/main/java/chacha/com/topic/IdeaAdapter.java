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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    private ArrayList<String> hearts;
    private Context mContext;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
    private FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabaseReference;
    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
    private StorageReference mStorageReference;
    private AlertDialog dialog;


    public IdeaAdapter(ArrayList<Idea> ideaList,ArrayList<String> idealIdList, ArrayList<String> hearts, Context mContext) {
        this.ideaList = ideaList;
        this.ideaIdList = idealIdList;
        this.hearts = hearts;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_idea, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position1) {
        final int position = holder.getAdapterPosition();
        if(mFirebaseUser.getEmail().equals(ideaList.get(position).writer)){
            holder.ib_menu.setVisibility(View.VISIBLE);
            holder.ib_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
                    View mView =  LayoutInflater.from(mContext).inflate(R.layout.dialog, null);
                    Button btn_edit = (Button)mView.findViewById(R.id.btn_edit);
                    Button btn_delete = (Button)mView.findViewById(R.id.btn_delete);

                    btn_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Idea idea = ideaList.get(position);
                            String id = ideaIdList.get(position);
                            mDatabaseReference = mFirebaseDatabase.getReference("Subject").child(id);
                            mDatabaseReference.removeValue();
                            if(!idea.contentImageUrl.equals("")){
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
        }

        holder.tvContent.setText(ideaList.get(position).content);

        holder.tvProfileName.setText(ideaList.get(position).profileName);

        if(ideaList.get(position).contentImageUrl==null|| TextUtils.isEmpty(ideaList.get(position).contentImageUrl)){
            Glide.with(mContext).load("").fitCenter().into(holder.ivContent);
        }else{
            Glide
                    .with(mContext)
                    .load(ideaList.get(position).contentImageUrl)
//                    .thumbnail( 0.1f )
                    .fitCenter()
                    .into(holder.ivContent);
        }


        if(ideaList.get(position).profilePhoto==null||TextUtils.isEmpty(ideaList.get(position).profilePhoto)){
            Glide.with(mContext).load(R.drawable.userplaceholder).fitCenter().into(holder.ivProfilePhoto);
        }else{
            Glide.with(mContext).load(ideaList.get(position).profilePhoto).into(holder.ivProfilePhoto);
        }

        //좋아요 뷰
        holder.btnHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 테스트 후 중복해서 좋아요를 누르지 못하는 코드 작성하기
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                final String strDate = sdf.format(c.getTime());
                mDatabaseReference = mFirebaseDatabase.getReference("Subject").child(ideaIdList.get(position)).child("lover").child(strDate);
                mDatabaseReference.setValue(mFirebaseUser.getEmail());
            }
        });

        holder.tvContentHeart.setText(hearts.get(position));
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
        private ImageButton ib_menu;

        public ViewHolder(View itemView) {
            super(itemView);
            tvProfileName = (TextView)itemView.findViewById(R.id.tvProfileName);
            tvContentHeart = (TextView)itemView.findViewById(R.id.tvContentHeart);
            tvContent = (TextView)itemView.findViewById(R.id.tvContent);
            ivProfilePhoto = (ImageView)itemView.findViewById(R.id.ivProfilePhoto);
            ivContent = (ImageView)itemView.findViewById(R.id.ivContent);
            btnHeart = (Button)itemView.findViewById(R.id.btnHeart);
            ib_menu = (ImageButton)itemView.findViewById(R.id.ib_menu);
        }
    }
}
