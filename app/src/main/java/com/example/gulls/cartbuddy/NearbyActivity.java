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

public class NearbyActivity extends MainActivity {
    private final String TAG = "NEARBY";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_nearby:
                    Toast.makeText(NearbyActivity.this, "Hello", Toast.LENGTH_LONG).show();
                    return true;
                case R.id.navigation_home:
                    intent = new Intent(NearbyActivity.this, MainActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_recent:
                    intent = new Intent(NearbyActivity.this, RecentActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_popular:
                    intent = new Intent(NearbyActivity.this, PopularActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_upload:
                    intent = new Intent(NearbyActivity.this, UploadActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        listView = (ListView) findViewById(R.id.list_view);
//        getDeals(serverUrl, PopularActivity.this);
        deals = new ArrayList<>();
        deals.add(new Deal("t1","http://i.imgur.com/DvpvklR.png", "d1"));
        deals.add(new Deal("t2","http://i.imgur.com/DvpvklR.png", "d2"));
        deals.add(new Deal("t3","http://i.imgur.com/DvpvklR.png", "d3"));

        listView.setAdapter(new DealAdapter(NearbyActivity.this, deals));
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.getMenu().getItem(3).setChecked(true);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }
}
