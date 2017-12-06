package com.example.gulls.cartbuddy;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.service.carrier.CarrierMessagingService;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.graphics.Color;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    final private String noImageUrl =  "https://www.built.co.uk/c.3624292/a/img/no_image_available.jpeg?resizeid=2&resizeh=350&resizew=350";
    private final String TAG = "MAIN";
    final String serverUrl = "https://cartbuddy.benfu.me/deals?sort=recent";

    private GeoDataClient geoDataClient;

    private Intent intent;
    private ListView listView;
    private ArrayList<Deal> deals = new ArrayList<>();
    private MaterialSearchView searchView;
    private ArrayList<Deal> shownDeals = new ArrayList<>();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
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


    private void getDeals(String url) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            shownDeals.clear();
                            JSONArray dealsJson = new JSONArray(response);
                            for (int i = 0; i < dealsJson.length(); i++) {
                                JSONObject deal = dealsJson.getJSONObject(i);
                                final Deal d = new Deal();
                                d.id = deal.getString("id");
                                if(deal.getString("title").equals("null")) {
                                    d.title = "Great deal!";
                                }else {
                                    d.title = deal.getString("title");
                                }
                                if (deal.getString("photoUrls").equals("null")){
                                    d.photoUrl = noImageUrl;
                                }else {
                                    d.photoUrl = deal.getJSONArray("photoUrls").get(0).toString();
                                }
                                d.likes = Integer.valueOf(deal.getString("numLikes"));
                                d.date = deal.getString("createdAt");
                                if (d.date.length() > 10) {
                                    d.date = d.date.substring(0, 10);
                                }


                                //location
                                d.placeId = deal.getString("placeId");

                                if (deal.getString("location").equals("null")) {
                                    d.locationStr = "Not available";
                                }
                                else {
                                    JSONObject jsonObject = deal.getJSONObject("location");
                                    d.lat = Double.valueOf(jsonObject.getString("x"));
                                    d.lon = Double.valueOf(jsonObject.getString("y"));
                                    d.locationStr = getCompleteAddressString(d.lat, d.lon);
                                }

                                deals.add(d);
                                shownDeals.add(d);
                            }
                            listView.setAdapter(new DealAdapter(MainActivity.this, shownDeals));
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

        geoDataClient = Places.getGeoDataClient(this, null);

        //all deals
        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(new DealAdapter(MainActivity.this, shownDeals));
        getDeals(serverUrl);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                String dealId = shownDeals.get(position).id;
                Intent intent = new Intent(MainActivity.this, ViewSingleDealActivity.class);
                intent.putExtra("ID", dealId);
                startActivity(intent);
            }
        });

        //toolbar & search
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("CartBuddy");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

                //If closed Search View , lstView will return default
                shownDeals.clear();
                for (Deal d : deals) {
                    shownDeals.add(d);
                }
                listView = (ListView) findViewById(R.id.list_view);
                listView.setAdapter(new DealAdapter(MainActivity.this, shownDeals));
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                shownDeals.clear();
                if (newText != null && !newText.isEmpty()) {
                    ArrayList<Deal> lstFound = new ArrayList<>();
                    for (Deal item : deals) {
                        if (item.title.toLowerCase().contains(newText.toLowerCase())){
                            lstFound.add(item);
                            shownDeals.add(item);
                        }
                    }

                    listView.setAdapter(new DealAdapter(MainActivity.this, shownDeals));

                } else {
                    for(Deal d : deals) {
                        shownDeals.add(d);
                    }
                    listView.setAdapter(new DealAdapter(MainActivity.this, shownDeals));

                }
                return true;
            }

        });

        //floating button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent uploadIntent = new Intent(MainActivity.this, CreateDealActivity.class);
                startActivity(uploadIntent);
            }
        });

        //navigation
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

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    Log.w("locality",returnedAddress.getLocality());
                    Log.w("name", returnedAddress.getFeatureName());
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString().trim();
                Log.w("Current loction address", strReturnedAddress.toString());
            } else {
                Log.w("Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("Current loction address", "Canont get Address!");
        }
        return strAdd;
    }


    @Override
    public void onClick(View view) {

    }
}
