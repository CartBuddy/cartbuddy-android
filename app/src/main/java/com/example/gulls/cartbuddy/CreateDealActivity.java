package com.example.gulls.cartbuddy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.moshi.JsonReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSource;

public class CreateDealActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener{
    private static final String TAG = CreateDealActivity.class.getName();

    private static final String xAppId = "7b82427d";
    private static final String xAppKey = "9e275d051b50fc891da0052012591509";

    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;

    GoogleApiClient mGoogleApiClient;
    PlaceDetectionClient placeDetectionClient;
    OkHttpClient httpClient = HttpClient.getClient();
    Location mLastLocation;
    private double lat = 0.0;
    private double lon = 0.0;
    private List<String> foods;

    private String nutritionixApiUrl;
    private String nutritionixInstantUrl;

    ArrayAdapter<String> foodAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_deal);

        // Request permissions
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d(TAG, "Show rationale");
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_FINE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else {
            Log.d(TAG, "Permissions already granted");
            getCurrentPlace();
        }

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

        nutritionixApiUrl = getString(R.string.nutritionix_api_url).toString();
        nutritionixInstantUrl = nutritionixApiUrl + "/search/instant";

        Toolbar toolbar = (Toolbar) findViewById(R.id.checklist_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("CartBuddy");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        Button cameraBtn = (Button) findViewById(R.id.action_camera);
        cameraBtn.setOnClickListener(this);
        Button selectBtn = (Button) findViewById(R.id.action_select_from_local);
        selectBtn.setOnClickListener(this);

        // place picker
        Button placeBtn = findViewById(R.id.button_place);
        placeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(CreateDealActivity.this), PLACE_PICKER_REQUEST);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        // autocomplete
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autocomplete_category);
        foods = new ArrayList<>();
        foodAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, foods);
        autoCompleteTextView.setAdapter(foodAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Here");
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                View v = getCurrentFocus();
                if (v != null) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count < 2) {
                    return;
                }
                HttpUrl url = HttpUrl.parse(nutritionixInstantUrl)
                        .newBuilder()
                        .addQueryParameter("query", s.toString())
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("x-app-id", xAppId)
                        .addHeader("x-app-key", xAppKey)
                        .get()
                        .build();

                httpClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try (ResponseBody responseBody = response.body()) {
                            if (!response.isSuccessful()) {
                                throw new IOException("Unexpected code: " + response.code());
                            }

                            List<String> commonFoods = new ArrayList<>();
                            List<String> brandedFoods = new ArrayList<>();
                            List<String> curList = commonFoods;
//                            String responseStr = responseBody.string();
                            BufferedSource jsonSource = responseBody.source();
//                            Log.d(TAG, responseStr);
                            JsonReader reader = JsonReader.of(jsonSource);
                            reader.beginObject();
                            while (reader.hasNext()) {
                                String name = reader.nextName();
                                Log.d(TAG, name);
                                if (name.equals("common")) {
                                    curList = commonFoods;
                                }
                                else if (name.equals("branded")) {
                                    curList = brandedFoods;
                                }
                                reader.beginArray();
                                while (reader.hasNext()) {
                                    reader.beginObject();
                                    while (reader.hasNext()) {
                                        name = reader.nextName();
//                                        Log.d(TAG, name);
                                        if (name.equals("food_name")) {
                                            curList.add(reader.nextString());
                                        }
                                        else {
                                            reader.skipValue();
                                        }

                                    }
                                    reader.endObject();
                                }
                                reader.endArray();
                            }
                            reader.endObject();
                            Log.d(TAG, String.valueOf(commonFoods.size()));
                            Log.d(TAG, String.valueOf(brandedFoods.size()));
                            commonFoods.addAll(brandedFoods);

                            foods.clear();
                            foods.addAll(commonFoods);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    foodAdapter.clear();
                                    foodAdapter.addAll(foods);
                                    foodAdapter.notifyDataSetChanged();
                                }
                            });

                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentPlace();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.


                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public void getCurrentPlace() {
        // detect and set the current place
        placeDetectionClient = Places.getPlaceDetectionClient(this, null);
        Task<PlaceLikelihoodBufferResponse> placeResult = placeDetectionClient.getCurrentPlace(null);
        placeResult.addOnCompleteListener(new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                    Log.i(TAG, String.format("Place '%s' has likelihood: %g",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));
                }
                Place closestPlace = likelyPlaces.get(0).getPlace();
                updatePlace(closestPlace);
                likelyPlaces.release();
            }
        });
    }

    /**
     * Update the current place displayed in the UI.
     * @param place
     */
    public void updatePlace(Place place) {
        TextView textPlaceName = findViewById(R.id.text_place_name);
        TextView textPlaceAddress = findViewById(R.id.text_place_address);
        textPlaceName.setText(place.getName());
        textPlaceAddress.setText(place.getAddress());
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

                updatePlace(place);
            }
            else {
                Log.d(TAG, "Place error: " + resultCode);
            }
        }
    }
}
