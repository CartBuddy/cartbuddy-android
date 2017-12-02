package com.example.gulls.cartbuddy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import android.graphics.Color;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "MAIN";
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
                case R.id.navigation_home:
                    Toast.makeText(MainActivity.this, "Hello", Toast.LENGTH_LONG).show();
                    return true;
                case R.id.navigation_popular:
                    intent = new Intent(MainActivity.this, PopularActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_nearby:
                    intent = new Intent(MainActivity.this, NearbyActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_profile:
                    intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_checklist:
                    intent = new Intent(MainActivity.this, ChecklistActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };


    void getDeals(String url, final Context context) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray dealsJson = new JSONArray(response);
                            deals.clear();
                            for (int i = 0; i < dealsJson.length(); i++) {
                                JSONObject deal = dealsJson.getJSONObject(i);
                                Deal d = new Deal(deal.getString("id"), deal.getString("title"), deal.getString("photoUrl"), Integer.valueOf(deal.getString("likes")), deal.getString("date"));
                                deals.add(d);
                            }
                            listView.setAdapter(new DealAdapter(context, deals));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("CartBuddy");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        listView = (ListView) findViewById(R.id.list_view);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                String dealId = deals.get(position).id;
                Intent intent = new Intent(MainActivity.this, ViewSingleDealActivity.class);
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
        deals.add(new Deal("6","t1", "http://i.imgur.com/DvpvklR.png", 10, "2017-1-1"));
        deals.add(new Deal("7","t1", "http://i.imgur.com/DvpvklR.png", 10, "2017-1-1"));
        deals.add(new Deal("8","t1", "http://i.imgur.com/DvpvklR.png", 10, "2017-1-1"));
        deals.add(new Deal("9","t1", "http://i.imgur.com/DvpvklR.png", 10, "2017-1-1"));
        deals.add(new Deal("10","t1", "http://i.imgur.com/DvpvklR.png", 10, "2017-1-1"));
        deals.add(new Deal("11","t1", "http://i.imgur.com/DvpvklR.png", 10, "2017-1-1"));
        deals.add(new Deal("12","t1", "http://i.imgur.com/DvpvklR.png", 10, "2017-1-1"));
        deals.add(new Deal("13","t1", "http://i.imgur.com/DvpvklR.png", 10, "2017-1-1"));
        deals.add(new Deal("14","t1", "http://i.imgur.com/DvpvklR.png", 10, "2017-1-1"));
        deals.add(new Deal("15","t1", "http://i.imgur.com/DvpvklR.png", 10, "2017-1-1"));
        deals.add(new Deal("16","t1", "http://i.imgur.com/DvpvklR.png", 10, "2017-1-1"));
        deals.add(new Deal("17","t1", "http://i.imgur.com/DvpvklR.png", 10, "2017-1-1"));
        deals.add(new Deal("18","t1", "http://i.imgur.com/DvpvklR.png", 10, "2017-1-1"));
        deals.add(new Deal("19","t1", "http://i.imgur.com/DvpvklR.png", 10, "2017-1-1"));
        deals.add(new Deal("20","t1", "http://i.imgur.com/DvpvklR.png", 10, "2017-1-1"));

        listView.setAdapter(new DealAdapter(MainActivity.this, deals));
        searchView = (MaterialSearchView) findViewById(R.id.search_view);

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

                //If closed Search View , lstView will return default
                listView = (ListView) findViewById(R.id.list_view);
                listView.setAdapter(new DealAdapter(MainActivity.this, deals));
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

                    listView.setAdapter(new DealAdapter(MainActivity.this, lstFound));

                } else {
                    listView.setAdapter(new DealAdapter(MainActivity.this, deals));

                }
                return true;
            }

        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public void onClick(View view) {

    }
}
