package com.example.gulls.cartbuddy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = "MAIN";
    final String serverUrl = "";
    Intent intent;
    ListView listView;
    ArrayList<Deal> deals;
    DealAdapter dealAdapter;


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
                case R.id.navigation_recent:
                    intent = new Intent(MainActivity.this, RecentActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_nearby:
                    intent = new Intent(MainActivity.this, NearbyActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_upload:
                    intent = new Intent(MainActivity.this, UploadActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };


     void getDeals(String url, final Context context){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONArray dealsJson = new JSONArray(response);
                            deals.clear();
                            for (int i = 0; i < dealsJson.length(); i++) {
                                JSONObject deal = dealsJson.getJSONObject(i);
                                Deal d = new Deal(deal.getString("title"), deal.getString("photoUrl"), deal.getString("des"));
                                deals.add(d);
                            }
                            dealAdapter = new DealAdapter(context, deals);
                            listView.setAdapter(dealAdapter);
                        }catch(JSONException e){
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
        listView = (ListView) findViewById(R.id.list_view);
//        getDeals(serverUrl, MainActivity.this);
        deals = new ArrayList<>();
        deals.add(new Deal("t1","http://i.imgur.com/DvpvklR.png", "d1"));
        deals.add(new Deal("t2","http://i.imgur.com/DvpvklR.png", "d2"));
        deals.add(new Deal("t3","http://i.imgur.com/DvpvklR.png", "d3"));
        listView.setAdapter(new DealAdapter(MainActivity.this, deals));
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onClick(View view) {

    }
}
