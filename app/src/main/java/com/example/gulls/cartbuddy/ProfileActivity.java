package com.example.gulls.cartbuddy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.gulls.cartbuddy.profile.ProfileFragmentPagerAdapter;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = ProfileActivity.class.getName();
    private User user = UserSession.getSession().getActiveUser();
    private OkHttpClient httpClient = HttpClient.getClient();
    private String baseUrl;
    private Intent intent;

    private List<Deal> userDeals;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    intent = new Intent(ProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_nearby:
                    intent = new Intent(ProfileActivity.this, NearbyActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_popular:
                    intent = new Intent(ProfileActivity.this, PopularActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_checklist:
                    intent = new Intent(ProfileActivity.this, ChecklistActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        baseUrl = getString(R.string.base_url);

        //navigation
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.getMenu().getItem(4).setChecked(true);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // tab view
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new ProfileFragmentPagerAdapter(getSupportFragmentManager(), ProfileActivity.this));
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        ((TextView)findViewById(R.id.profile_username)).setText(user.username);
    }

    @Override
    public void onStart() {
        super.onStart();

        Request request = new Request.Builder()
                .url(baseUrl + "/deals?user_id=" + user.id)
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
                        throw new IOException("Unexpected response code: " + response.code());
                    }
                    String dealsStr = responseBody.string();
                    Log.d(TAG, dealsStr);
                    Moshi moshi = new Moshi.Builder().build();
                    Type type = Types.newParameterizedType(List.class, Deal.class);
                    JsonAdapter<List<Deal>> jsonAdapter = moshi.adapter(type);
                    userDeals = jsonAdapter.fromJson(dealsStr);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ((TextView)findViewById(R.id.num_deals)).setText(userDeals.size() + " Deals Posted");
                        }
                    });
                }
            }
        });
    }


}
