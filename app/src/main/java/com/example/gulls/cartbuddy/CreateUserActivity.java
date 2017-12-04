package com.example.gulls.cartbuddy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Rfc3339DateJsonAdapter;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CreateUserActivity extends AppCompatActivity {
    private static final String TAG = CreateUserActivity.class.getName();
    private OkHttpClient httpClient = HttpClient.getClient();

    private String urlUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        urlUsers = getString(R.string.base_url) + "/users";

        Button button = (Button) findViewById(R.id.create_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                EditText username = findViewById(R.id.username);
                user.username = username.getText().toString();
                user.email = getIntent().getStringExtra("email");
                user.joinedOn = new Date();
                // serialize to JSON
                final Moshi moshi = new Moshi.Builder()
                        .add(Date.class, new Rfc3339DateJsonAdapter())
                        .build();
                final JsonAdapter<User> jsonAdapter = moshi.adapter(User.class);

                String postData = jsonAdapter.toJson(user);
                Log.d(TAG, postData);
                Request request = new Request.Builder()
                        .url(urlUsers)
                        .post(RequestBody.create(MediaType.parse("application/json"), postData))
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
                            String urlUser = responseBody.string();
                            // last part of url is the id
                            String id = urlUser.substring(urlUser.lastIndexOf('/') + 1, urlUser.length());
                            Request request = new Request.Builder()
                                    .url(urlUsers + "/" + id)
                                    .build();

                            response = httpClient.newCall(request).execute();
                            String responseJson = response.body().string();
                            Log.d(TAG, responseJson);
                            User user = jsonAdapter.fromJson(responseJson);
                            UserSession.getSession().setActiveUser(user);
                            Intent intent = new Intent(CreateUserActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
    }
}
