package com.example.gulls.cartbuddy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class NearbyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);
        Toast.makeText(NearbyActivity.this, "Hello", Toast.LENGTH_LONG).show();
    }
}
