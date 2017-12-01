package com.example.gulls.cartbuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class ViewSingleDealActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_single_deal);
        Intent intent = getIntent();
        if (intent != null) {
            Toast.makeText(ViewSingleDealActivity.this, intent.getStringExtra("ID"), Toast.LENGTH_LONG).show();
        }
    }
}
