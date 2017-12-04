package com.example.gulls.cartbuddy;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.graphics.Color;

public class NearbyActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,View.OnClickListener {
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    private double lat = 0.0, lon = 0.0;

    final private String noImageUrl =  "https://www.built.co.uk/c.3624292/a/img/no_image_available.jpeg?resizeid=2&resizeh=350&resizew=350";
    private final String TAG = "NEARBY";
    final String serverUrl = "https://cartbuddy.benfu.me/deals?sort=recent";

    private Intent intent;
    private ListView listView;
    private ArrayList<Deal> deals = new ArrayList<>();
    private MaterialSearchView searchView;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_popular:
                    intent = new Intent(NearbyActivity.this, PopularActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_home:
                    intent = new Intent(NearbyActivity.this, MainActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_profile:
                    intent = new Intent(NearbyActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_checklist:
                    intent = new Intent(NearbyActivity.this, ChecklistActivity.class);
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
                            JSONArray dealsJson = new JSONArray(response);
                            for (int i = 0; i < dealsJson.length(); i++) {
                                JSONObject deal = dealsJson.getJSONObject(i);
                                Deal d = new Deal();
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

                                //???
                                String[] location = deal.getString("location").split(",");
                                if (location.length == 2){
                                    d.lat = Double.valueOf(location[0]);
                                    d.lon = Double.valueOf(location[1]);
                                    double distance = distance(Double.valueOf(location[0]), Double.valueOf(location[1]), lat, lon);
                                    d.distance = distance;
                                }
                                //

                                deals.add(d);
                            }
                            Collections.sort(deals, new Comparator<Deal>() {
                                public int compare(Deal d1, Deal d2) {
                                    if (d1.distance < d2.distance) return -1;
                                    else if (d1.distance > d2.distance) return 1;
                                    else return 0;
                                }
                            });
                            listView.setAdapter(new DealAdapter(NearbyActivity.this, deals));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(NearbyActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        //Location
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else
            Toast.makeText(this, "Not connected...", Toast.LENGTH_LONG).show();

        //all deals
        listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(new DealAdapter(NearbyActivity.this, deals));
        getDeals(serverUrl);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                String dealId = deals.get(position).id;
                Intent intent = new Intent(NearbyActivity.this, ViewSingleDealActivity.class);
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
                listView = (ListView) findViewById(R.id.list_view);
                listView.setAdapter(new DealAdapter(NearbyActivity.this, deals));
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

                    listView.setAdapter(new DealAdapter(NearbyActivity.this, lstFound));

                } else {
                    listView.setAdapter(new DealAdapter(NearbyActivity.this, deals));

                }
                return true;
            }

        });

        //navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.getMenu().getItem(2).setChecked(true);
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

    public static double distance(double lat1, double lon1, double lat2, double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters
        distance = Math.pow(distance, 2);
        return Math.sqrt(distance);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Do not have permission...", Toast.LENGTH_LONG).show();
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            lon = mLastLocation.getLongitude();
            lat = mLastLocation.getLatitude();
//            Toast.makeText(ViewNearbyStreamsActivity.this, "Latitude: " + String.valueOf(mLastLocation.getLatitude()) + "Longitude: " +
//                    String.valueOf(mLastLocation.getLongitude()), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection suspended...", Toast.LENGTH_LONG).show();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Failed to connect...", Toast.LENGTH_LONG).show();
    }
}
