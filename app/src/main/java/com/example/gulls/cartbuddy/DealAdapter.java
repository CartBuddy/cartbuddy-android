package com.example.gulls.cartbuddy;

/**
 * Created by Yuanrui on 11/29/17.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import com.squareup.picasso.Picasso;

/**
 * Created by Yuanrui on 10/25/17.
 */

public class DealAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Deal> deals;
    private ArrayList<String> titles;
    private ArrayList<String> imageUrls;
    private ArrayList<String> likes;
    private ArrayList<String> dates;


    public DealAdapter(Context c,  ArrayList<Deal> deals) {
        mContext = c;
        this.deals = deals;
        titles = new ArrayList<>();
        imageUrls = new ArrayList<>();
        likes = new ArrayList<>();
        dates = new ArrayList<>();
        for (Deal d : deals) {
            titles.add(d.title);
            imageUrls.add(d.photoUrl);
            likes.add(String.valueOf(d.likes));
            dates.add(d.date);
        }
    }

    public int getCount() {
        return deals.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
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
        titleView.setText(titles.get(position));
        Picasso.with(mContext).load(imageUrls.get(position)).fit().centerCrop().into(imageView);
        voteView.setText(likes.get(position));
        dateView.setText(dates.get(position));
        return grid;
    }
}