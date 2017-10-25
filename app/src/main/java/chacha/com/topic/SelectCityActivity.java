package chacha.com.topic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

public class SelectCityActivity extends AppCompatActivity {

    private RecyclerView rv;
    private RecyclerView.LayoutManager lm;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<String> cityList;
    private City city;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);

        cityList = new ArrayList<>();
        cityList.add("Paris");
        cityList.add("Praha");
        cityList.add("Ronda");
        cityList.add("Rondon");
        cityList.add("Rondon");
        cityList.add("Rondon");
        cityList.add("Rondon");

        mAdapter = new CityAdapter(SelectCityActivity.this, cityList);
        rv = (RecyclerView) findViewById(R.id.rv);
        lm = new LinearLayoutManager(SelectCityActivity.this, 1, false);
        rv.setLayoutManager(lm);
        rv.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }
}
