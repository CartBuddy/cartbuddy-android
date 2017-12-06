package com.example.gulls.cartbuddy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public class ViewSingleDealActivity extends AppCompatActivity implements View.OnClickListener{
    OkHttpClient client = HttpClient.getClient();

    final private String noImageUrl =  "https://www.built.co.uk/c.3624292/a/img/no_image_available.jpeg?resizeid=2&resizeh=350&resizew=350";
    private final String TAG = "POPULAR";
    final String serverUrl = "https://cartbuddy.benfu.me/deals/";

    private GeoDataClient geoDataClient;

    private String dealUrl = "";
    Deal deal = new Deal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        geoDataClient = Places.getGeoDataClient(this, null);

        setContentView(R.layout.activity_view_single_deal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("CartBuddy");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        Intent intent = getIntent();
        if (intent != null) {
            getDealbyId(serverUrl + intent.getStringExtra("ID"));
        }
        findViewById(R.id.vote_btn).setOnClickListener(this);

    }



    private void getDealbyId(String url) {
        dealUrl = url;
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject dealJson = new JSONObject(response);
                            deal.id = dealJson.getString("id");

                            //title
                            if(dealJson.getString("title").equals("null")) {
                                deal.title = "Great deal!";
                            }else {
                                deal.title = dealJson.getString("title");
                            }

                            TextView titleView = (TextView)findViewById(R.id.deal_title);
                            titleView.setText(deal.title);

                            //photo
                            if (dealJson.getString("photoUrls").equals("null")){
                                deal.photoUrl = noImageUrl;
                            }else {
                                deal.photoUrl = dealJson.getJSONArray("photoUrls").get(0).toString();
                            }
                            ImageView imageView = (ImageView) findViewById(R.id.image);
                            Picasso.with(ViewSingleDealActivity.this).load(deal.photoUrl).fit().centerCrop().into(imageView);

                            //des
                            if(dealJson.getString("description").equals("null")) {
                                deal.description = "No further description";
                            }else {
                                deal.description = dealJson.getString("description");
                            }
                            TextView desView = (TextView) findViewById(R.id.des);
                            desView.setText(deal.description);

                            //likes
                            deal.likes = Integer.valueOf(dealJson.getString("numLikes"));
                            Button voteButton = (Button) findViewById(R.id.vote_btn);
                            voteButton.setText(String.valueOf(deal.likes));

                            //date
                            deal.date = dealJson.getString("createdAt");
                            if (deal.date.length() > 10) {
                                deal.date = deal.date.substring(0, 10);
                            }
                            TextView dateView = (TextView)findViewById(R.id.date);
                            dateView.setText(deal.date);


                            //location
                            //location
//                            deal.placeId = dealJson.getString("placeId");
//                            if (dealJson.getString("location").equals("null")) {
//                                deal.location = new Deal.Location(0, 0);
//                            }
//                            else {
//                                JSONObject jsonObject = dealJson.getJSONObject("location");
//                                deal.lat = Double.valueOf(jsonObject.getString("x"));
//                                deal.lon = Double.valueOf(jsonObject.getString("y"));
//                                deal.location = new Deal.Location(deal.lat, deal.lon);
//                            }

                            if (dealJson.getString("location").equals("null")) {
                                deal.locationStr = "Not available";
                            }else {
                                JSONObject jsonObject = dealJson.getJSONObject("location");
                                deal.lat = Double.valueOf(jsonObject.getString("x"));
                                deal.lon = Double.valueOf(jsonObject.getString("y"));
                                deal.locationStr = getCompleteAddressString(deal.lat, deal.lon);
                            }
                            TextView locationView = (TextView) findViewById(R.id.location);
                            locationView.setText(deal.locationStr);
//                            geoDataClient.getPlaceById(deal.placeId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
//                                @Override
//                                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
//                                    if (task.isSuccessful()) {
//                                        PlaceBufferResponse places = task.getResult();
//                                        if (places.getCount() > 0) {
//                                            Place place = places.get(0);
//                                            TextView locationView = (TextView)findViewById(R.id.location);
//                                            locationView.setText(deal.location.toString());
//                                            locationView.setText(place.getName());
//                                            places.release();
//                                        }
//                                        places.release();
//                                    }
//                                }
//                            });


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ViewSingleDealActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    private void voteBtnHandler(){
        //????
        String patchBody = "{ \"mode\": \"++\" }";
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(dealUrl + "/likes")
                .patch(RequestBody.create(MediaType.parse("application/json"), patchBody))
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                try (
                    ResponseBody responseBody = response.body()
                ) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    String numLikesNewStr = responseBody.string();
                    int numLikesNew = Integer.parseInt(numLikesNewStr);
                    deal.likes = numLikesNew;

                    ViewSingleDealActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateUi();
                        }
                    });

                }
            }
        });
    }

    public void updateUi() {
        Button voteButton = (Button) findViewById(R.id.vote_btn);
        voteButton.setText(String.valueOf(deal.likes));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.vote_btn) {
            voteBtnHandler();
        }
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
}
