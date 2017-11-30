package com.example.gulls.cartbuddy;

/**
 * Created by Yuanrui on 11/29/17.
 */
import android.location.Location;

import java.util.Date;

public class Deal {
    public int id;
    public String user;
    public String photoUrl;
    public String title;
    public String description;
    public int likes;
    public Date date;
    public String category;
    public Location location;
    public Deal(){

    }
    public Deal(String title, String photoUrl, String description){
        this.title = title;
        this.photoUrl = photoUrl;
        this.description = description;
    }
}
