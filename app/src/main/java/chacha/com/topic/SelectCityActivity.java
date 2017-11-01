package chacha.com.topic;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SelectCityActivity extends AppCompatActivity {

    private RecyclerView rv;
    private RecyclerView.LayoutManager lm;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<City> cityList;
    private City city;

    private Toolbar toolbar;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        iv = (ImageView) findViewById(R.id.iv_bg);
        Glide.with(this).load(R.drawable.paris).fitCenter().into(iv);
        toolbar.setTitle("Select City");
        setSupportActionBar(toolbar);

        cityList = new ArrayList<>();

        city = new City("Paris", "https://img-wishbeen.akamaized.net/plan/1446775789905_6672156239_89c77d53d8_o.jpg");
        cityList.add(city);
        city = new City("Praha", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRlCaH7jIEvVs4PZMtnm8wlfebFahNdMaNmcjJQ2TJkla5HUA53Bg");
        cityList.add(city);
        city = new City("Ronda", "https://d1ez3020z2uu9b.cloudfront.net/imagecache/blog-photos/721.jpg");
        cityList.add(city);
        city = new City("London", "http://atom.mu/wp-content/uploads/2017/01/London-Expat-Explore-Xmas-2017.jpg");
        cityList.add(city);
        city = new City("Barcelona", "http://www.myappletravel.com/web/wp-content/uploads/2017/07/city-barcelona-1-1.jpg");
        cityList.add(city);
        city = new City("Honkong", "http://wednesdayjournal.net/PEG/13855402354258.jpg");
        cityList.add(city);
        city = new City("Osaka", "https://www.kbrockstar.com/wp-content/uploads/2015/11/%EC%98%A4%EC%82%AC%EC%B9%B4%EC%97%AC%ED%96%89%EC%BD%94%EC%8A%A4_021.png");
        cityList.add(city);
        city = new City("Bangkok", "https://img-wishbeen.akamaized.net/plan/1464347057414_best-bangkok-old-city-restaurants.jpg");
        cityList.add(city);
        city = new City("Singapore", "https://t1.daumcdn.net/thumb/R1280x0/?fname=http://t1.daumcdn.net/brunch/service/user/1RPm/image/SDBPqiRPvCjdKsHbxTwZ-28Q6Zc.jpg");
        cityList.add(city);
        city = new City("Oulu", "http://taxari.com/wp-content/uploads/2015/01/Talvi.jpg");
        cityList.add(city);


        mAdapter = new CityAdapter(SelectCityActivity.this, cityList);
        rv = (RecyclerView) findViewById(R.id.rv);
        lm = new GridLayoutManager(SelectCityActivity.this, 2);
        rv.setLayoutManager(lm);
        rv.setAdapter(mAdapter);
        rv.setNestedScrollingEnabled(false);
//        mAdapter.notifyDataSetChanged();
    }
}
