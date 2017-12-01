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

    private final String TAG = "POPULAR";
    final String serverUrl = "";
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
            ImageView imageView = (ImageView) findViewById(R.id.image);
            Picasso.with(ViewSingleDealActivity.this).load(deal.photoUrl).fit().centerCrop().into(imageView);
            TextView titleView = (TextView)findViewById(R.id.deal_title);
            titleView.setText(deal.title);
            Toast.makeText(ViewSingleDealActivity.this, titleView.getText(), Toast.LENGTH_LONG).show();
            TextView desView = (TextView)findViewById(R.id.des);
            desView.setText(deal.description);
            Button voteButton = (Button) findViewById(R.id.vote_btn);
            voteButton.setText(String.valueOf(deal.likes));
            TextView dateView = (TextView)findViewById(R.id.date);
            dateView.setText(deal.date);
            findViewById(R.id.vote_btn).setOnClickListener(this);
        }
    }

    private void getDealbyId(String url) {
//        // Instantiate the RequestQueue.
//        RequestQueue queue = Volley.newRequestQueue(this);
//        // Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONArray tmp = new JSONArray(response);
//                            JSONObject dealJson = tmp.getJSONObject(0);
//                            deal.photoUrl = dealJson.getString("imageUrl");
//                            deal.title = dealJson.getString("title");
//                            deal.description = dealJson.getString("des");
//                            deal.date = dealJson.getString("date");
//                            deal.likes = Integer.valueOf(dealJson.getString("likes"))
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(ViewSingleDealActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//        // Add the request to the RequestQueue.
//        queue.add(stringRequest);

        //mock data
        deal.photoUrl = "http://i.imgur.com/DvpvklR.png";
        deal.title = "test";
        deal.description = "hahaha";
        deal.date = "2017-1-1";
        deal.likes = 1;
    }
    private void voteBtnHandler(){
        // need to post new likes
        deal.likes += 1;
        Button voteButton = (Button) findViewById(R.id.vote_btn);
        voteButton.setText(String.valueOf(deal.likes));
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.vote_btn) {
            voteBtnHandler();
        }
    }
}
