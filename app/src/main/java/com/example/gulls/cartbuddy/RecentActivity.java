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

public class RecentActivity extends MainActivity {
    private final String TAG = "RECENT";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_recent:
                    Toast.makeText(RecentActivity.this, "Hello", Toast.LENGTH_LONG).show();
                    return true;
                case R.id.navigation_home:
                    intent = new Intent(RecentActivity.this, MainActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_popular:
                    intent = new Intent(RecentActivity.this, PopularActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_nearby:
                    intent = new Intent(RecentActivity.this, NearbyActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_upload:
                    intent = new Intent(RecentActivity.this, UploadActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent);
        listView = (ListView) findViewById(R.id.list_view);

//        getDeals(serverUrl, RecentActivity.this);
        deals = new ArrayList<>();
        deals.add(new Deal("t1", "http://i.imgur.com/DvpvklR.png", "d1"));
        deals.add(new Deal("t2", "http://i.imgur.com/DvpvklR.png", "d2"));
        deals.add(new Deal("t3", "http://i.imgur.com/DvpvklR.png", "d3"));

        listView.setAdapter(new DealAdapter(RecentActivity.this, deals));
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.getMenu().getItem(2).setChecked(true);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
