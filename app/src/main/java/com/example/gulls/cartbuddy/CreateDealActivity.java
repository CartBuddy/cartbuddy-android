package com.example.gulls.cartbuddy;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.TimeUnit;

public class CreateDealActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    private double lat = 0.0;
    private double lon = 0.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_deal);

        //Location
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else {
            Toast.makeText(this, "Not connected...", Toast.LENGTH_LONG).show();
        }

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


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Do not have permission...", Toast.LENGTH_LONG).show();
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            lon = mLastLocation.getLongitude();
            lat = mLastLocation.getLatitude();
            Toast.makeText(CreateDealActivity.this, "Latitude: " + String.valueOf(mLastLocation.getLatitude()) + "Longitude: " +
                    String.valueOf(mLastLocation.getLongitude()), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection suspended...", Toast.LENGTH_LONG).show();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Failed to connect...", Toast.LENGTH_LONG).show();
    }
}
