package com.example.gulls.cartbuddy;

/**
 * Created by Yuanrui on 11/29/17.
 */
import android.location.Location;

import com.google.android.gms.location.places.Place;
import com.squareup.moshi.Json;

import java.util.Date;

public class Deal {
    public static class Location {
        public double x;
        public double y;

        public Location(double lat, double lng) {
            x = lat;
            y = lng;
        }

        @Override
        public String toString() {
            if (Double.compare(x, 0) == 0 && Double.compare(y, 0) == 0) {
                return "Not available";
            }
            return "x:" + x + ",y:" + y;
        }
    }
    public String id;
    @Json(name = "userId")
    public String user;
    @Json(name = "photoUrls")
    public String[] photoUrls;

    public String photoUrl;
    public String title;
    public String description;
    @Json(name = "numLikes")
    public int likes;
    //createdAt
    public String date;
    public String category;

//    public String location;
    public Location location;

    public transient double lat = 0.0;
    public transient double lon = 0.0;

    public double distance = 0.0;

    public String placeId;
    public transient Place place;

    public Deal(){

    }

    public Deal(String id, String title, String photoUrl, int likes, String date){
        this.id = id;
        this.title = title;
        this.photoUrl = photoUrl;
        this.likes = likes;
        this.date = date;
    }
    public Deal(String title, String photoUrl, String description){
        this.title = title;
        this.photoUrl = photoUrl;
        this.description = description;
    }
}
