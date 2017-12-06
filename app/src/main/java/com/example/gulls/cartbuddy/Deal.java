package com.example.gulls.cartbuddy;

/**
 * Created by Yuanrui on 11/29/17.
 */
import android.location.Location;

import com.squareup.moshi.Json;

import java.util.Date;

public class Deal {
    public String id;
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
    public double lat = 0.0;
    public double lon = 0.0;
    public double distance = 0.0;
    public String location;
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
