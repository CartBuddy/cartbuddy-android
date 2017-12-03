package com.example.gulls.cartbuddy;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class CreateDealActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_deal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.checklist_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("CartBuddy");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        Button cameraBtn = (Button) findViewById(R.id.action_camera);
        cameraBtn.setOnClickListener(this);
        Button selectBtn = (Button) findViewById(R.id.action_select_from_local);
        selectBtn.setOnClickListener(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_send,menu);
        Drawable icon = menu.getItem(0).getIcon();
        icon.mutate();
        icon.setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
        return super.onCreateOptionsMenu(menu);
    }

    private void upload(){
        Toast.makeText(CreateDealActivity.this, "Thanks for sharing!", Toast.LENGTH_LONG).show();
    }

    //click send icon -> send deal to server;
    //please fill the upload() function;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_upload:
                upload();
        }
        return super.onOptionsItemSelected(item);
    }

    //take a picture button and select local photo button
    //please fill the useCamera and selectPhoto function

    private void useCamera(){
        Toast.makeText(CreateDealActivity.this, "Take a picture using camera", Toast.LENGTH_LONG).show();
    }
    private void selectPhoto(){
        Toast.makeText(CreateDealActivity.this, "Select a photo from your album", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.action_camera:
                useCamera();
            case R.id.action_select_from_local:
                selectPhoto();
        }
    }
}
