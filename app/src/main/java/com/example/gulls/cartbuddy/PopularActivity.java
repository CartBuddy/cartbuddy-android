package com.example.gulls.cartbuddy;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

// rank with votes
public class PopularActivity extends MainActivity {
    private final String TAG = "POPULAR";
    final String serverUrl = "";
    Intent intent;
    ListView listView;
    ArrayList<Deal> deals;
    MaterialSearchView searchView;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("CartBuddy");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        listView = (ListView) findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                String dealId = deals.get(position).id;
                Intent intent = new Intent(PopularActivity.this, ViewSingleDealActivity.class);
                intent.putExtra("ID", dealId);
                startActivity(intent);
            }
        });
//        getDeals(serverUrl, MainActivity.this);
        deals = new ArrayList<>();
        deals.add(new Deal("1","t1", "http://i.imgur.com/DvpvklR.png", 10, "2017-1-1"));
        deals.add(new Deal("2","t1", "http://i.imgur.com/DvpvklR.png", 10, "2017-1-1"));
        deals.add(new Deal("3","t1", "http://i.imgur.com/DvpvklR.png", 10, "2017-1-1"));
        deals.add(new Deal("4","t1", "http://i.imgur.com/DvpvklR.png", 10, "2017-1-1"));
        deals.add(new Deal("5","t1", "http://i.imgur.com/DvpvklR.png", 10, "2017-1-1"));

        listView.setAdapter(new DealAdapter(PopularActivity.this, deals));
        searchView = (MaterialSearchView) findViewById(R.id.search_view);

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

                //If closed Search View , lstView will return default
                listView = (ListView) findViewById(R.id.list_view);
                listView.setAdapter(new DealAdapter(PopularActivity.this, deals));
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && !newText.isEmpty()) {
                    ArrayList<Deal> lstFound = new ArrayList<>();
                    for (Deal item : deals) {
                        if (item.title.toLowerCase().contains(newText.toLowerCase()))
                            lstFound.add(item);
                    }

                    listView.setAdapter(new DealAdapter(PopularActivity.this, lstFound));

                } else {
                    listView.setAdapter(new DealAdapter(PopularActivity.this, deals));

                }
                return true;
            }

        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.getMenu().getItem(1).setChecked(true);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }


}
