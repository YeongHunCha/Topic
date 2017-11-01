package chacha.com.topic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cha on 2017-10-26.
 */

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {
    private static final int RESULT_OK = -1;
    Activity context;
    private ArrayList<City> cityList;
    Bundle extra;
    Intent intent;

    public CityAdapter(Activity context, ArrayList<City> cityList) {
        this.context = context;
        this.cityList = cityList;
    }

    public void onBindViewHolder(ViewHolder holder, final int position) {
        Glide.with(context).load(cityList.get(position).cityPhoto).centerCrop().into(holder.ivCity);
        holder.tvCity.setText(cityList.get(position).cityName);
        holder.rlCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extra = new Bundle();
                intent = new Intent();
                extra.putString("city", cityList.get(position).cityName);
                intent.putExtras(extra);
                context.setResult(RESULT_OK, intent);
                context.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCity;
        private ImageView ivCity;
        private RelativeLayout rlCity;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCity = (TextView) itemView.findViewById(R.id.tvCity);
            ivCity = (ImageView) itemView.findViewById(R.id.ivCity);
            rlCity = (RelativeLayout) itemView.findViewById(R.id.rlCity);

        }
    }
}
