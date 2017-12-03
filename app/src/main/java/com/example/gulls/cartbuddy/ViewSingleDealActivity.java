package com.example.gulls.cartbuddy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ViewSingleDealActivity extends AppCompatActivity implements View.OnClickListener{
    final private String noImageUrl =  "https://www.built.co.uk/c.3624292/a/img/no_image_available.jpeg?resizeid=2&resizeh=350&resizew=350";
    private final String TAG = "POPULAR";
    final String serverUrl = "https://cartbuddy.benfu.me/deals/";
    Deal deal = new Deal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        deal.likes += 1;
        Button voteButton = (Button) findViewById(R.id.vote_btn);
        voteButton.setText(String.valueOf(deal.likes));
        //???
//        /id/likes
//                "patch"
//        {
//            "mode":"++"
//        }
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.vote_btn) {
            voteBtnHandler();
        }
    }
}
