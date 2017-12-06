package com.example.gulls.cartbuddy;

/**
 * Created by Yuanrui on 11/29/17.
 */

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

/**
 * Created by Yuanrui on 10/25/17.
 */

public class DealAdapter extends BaseAdapter {
    private Context mContext;
    private GeoDataClient geoDataClient;
    private PlaceDetectionClient placeDetectionClient;
    private ArrayList<Deal> deals;
    private ArrayList<String> titles;
    private ArrayList<String> imageUrls;
    private ArrayList<String> likes;
    private ArrayList<String> dates;
    private ArrayList<String> placeIds;
    private ArrayList<String> locationStrs;

    public DealAdapter(Context c,  ArrayList<Deal> deals) {
        Log.d("Adapter", "constructor");
        mContext = c;
        geoDataClient = Places.getGeoDataClient(c, null);
        placeDetectionClient = Places.getPlaceDetectionClient(c, null);
        this.deals = deals;
        titles = new ArrayList<>();
        imageUrls = new ArrayList<>();
        likes = new ArrayList<>();
        dates = new ArrayList<>();
        locationStrs = new ArrayList<>();
        placeIds = new ArrayList<>();

        for (Deal d : deals) {
            titles.add(d.title);
            imageUrls.add(d.photoUrl);
            likes.add(String.valueOf(d.likes));
            dates.add(d.date);
            locationStrs.add(d.locationStr);
            placeIds.add(d.placeId);
        }
    }

    @Override
    public int getCount() {
        return deals.size();
    }

    @Override
    public Object getItem(int position) {
        return deals.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("DealAdapter", "getView");
        titles.clear();
        imageUrls.clear();
        likes.clear();
        dates.clear();
        locationStrs.clear();
        placeIds.clear();
        for (Deal d : deals) {
            titles.add(d.title);
            imageUrls.add(d.photoUrl);
            likes.add(String.valueOf(d.likes));
            dates.add(d.date);
            locationStrs.add(d.locationStr);
            placeIds.add(d.placeId);
        }

        Deal deal = deals.get(position);

        View grid = null;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            grid = new View(mContext);
            grid = inflater.inflate(R.layout.single_list, null);
        }else {
            grid = (View) convertView;
        }
        TextView titleView = (TextView) grid.findViewById(R.id.list_title);
        ImageView imageView = (ImageView) grid.findViewById(R.id.list_image);
        TextView voteView = (TextView) grid.findViewById(R.id.list_votes);
        TextView dateView = (TextView) grid.findViewById(R.id.list_date);
        final TextView locationView = (TextView) grid.findViewById(R.id.list_location);

//        titleView.setText(deal.title);
//        Picasso.with(mContext).load(deal.photoUrl).fit().centerCrop().into(imageView);
//        voteView.setText(deal.likes);
//        dateView.setText(deal.date);

        titleView.setText(titles.get(position));
        Picasso.with(mContext).load(imageUrls.get(position)).fit().centerCrop().into(imageView);
        voteView.setText(likes.get(position));
        dateView.setText(dates.get(position));

        Log.d("DealAdapter", placeIds.get(position));
        if (placeIds.get(position) != null) {
            geoDataClient.getPlaceById(placeIds.get(position)).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                    if (task.isSuccessful()) {
                        PlaceBufferResponse places = task.getResult();
                        if (places.getCount() > 0) {
                            Place place = places.get(0);
                            locationView.setText(place.getName());
                            places.release();
                        }
                        else {
                            locationView.setText("Location not available");
                            places.release();
                        }

                    }
                }
            });
        }
        else {
            locationView.setText("Location not available");
        }

        // show distance



//        locationView.setText(locationStrs.get(position).toString());
        return grid;
    }
}