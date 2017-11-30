package com.example.gulls.cartbuddy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

// rank with votes
public class PopularActivity extends MainActivity {
    private final String TAG = "POPULAR";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_popular:
                    Toast.makeText(PopularActivity.this, "Hello", Toast.LENGTH_LONG).show();
                    return true;
                case R.id.navigation_home:
                    intent = new Intent(PopularActivity.this, MainActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_recent:
                    intent = new Intent(PopularActivity.this, RecentActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_nearby:
                    intent = new Intent(PopularActivity.this, NearbyActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_upload:
                    intent = new Intent(PopularActivity.this, UploadActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular);
        listView = (ListView) findViewById(R.id.list_view);
//        getDeals(serverUrl, PopularActivity.this);
        deals = new ArrayList<>();
        deals.add(new Deal("t1","http://i.imgur.com/DvpvklR.png", "d1"));
        deals.add(new Deal("t2","http://i.imgur.com/DvpvklR.png", "d2"));
        deals.add(new Deal("t3","http://i.imgur.com/DvpvklR.png", "d3"));

        listView.setAdapter(new DealAdapter(PopularActivity.this, deals));
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.getMenu().getItem(1).setChecked(true);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }
}
