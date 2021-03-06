package com.example.gulls.cartbuddy;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadActivity extends AppCompatActivity implements ImagePickerCallback {
    private static String TAG = "ImagePickerFragment";
    private static int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    private static String BASE_URL = "http://connexus-gulls.appspot.com/api";

    private ChosenImage chosenImage;

//    private ListView lvResults;

    private Button btPickImageSingle;
    private Button btPickImageMultiple;
    private Button btTakePicture;

    private Button btUpload;

    private String streamId;

    private String pickerPath;

    private String tags;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        Intent intent = getIntent();
        if (intent != null) {
            String streamName = intent.getStringExtra("UploadImage");
            Log.d(TAG, streamName);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(BASE_URL + "/streams?name=" + streamName)
                    .build();
            try (Response response = client.newCall(request).execute()){
                JSONArray jsonArray = new JSONArray(response.body().string());
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                String id = jsonObject.getString("id");
                Log.d(TAG, id);
                streamId = id;
            }
            catch (Exception e) {
                e.printStackTrace();
            }


        }
        btPickImageSingle = (Button) findViewById(R.id.btGallerySingleImage);
        btPickImageSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageSingle();
            }
        });

        btUpload = (Button) findViewById(R.id.upload_btn);
        btUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Uploading...");
                // get tags
                tags = ((EditText)findViewById(R.id.tags)).getText().toString();
                Log.d(TAG, "Tags: " + tags);
                OkHttpClient client = new OkHttpClient();
                Request getUploadUrlRequest = new Request.Builder()
                        .url(BASE_URL + "/streams/" + streamId + "/photos/upload")
                        .build();

                Log.d(TAG, BASE_URL + "/streams/" + streamId + "/photos/upload");
                try (Response response = client.newCall(getUploadUrlRequest).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    String url = response.body().string();
                    Uri uri = Uri.parse(Uri.decode(chosenImage.getQueryUri()));
                    Log.d(TAG, url);

                    // build the image upload respones

                    MediaType formMedia = MediaType.parse("multipart/form-data; charset=utf-8");
                    Log.d(TAG, uri.toString());
//                    String type = getContentResolver().getType(Uri.parse(chosenImage.getQueryUri()));
//                    Log.d(TAG, type);
//                    int count = getContentResolver().query(Uri.parse(chosenImage.getQueryUri()), null, null, null, null).getCount();
//                    Log.d(TAG, String.valueOf(count));
                    InputStream is = getContentResolver().openInputStream(Uri.parse(chosenImage.getQueryUri()));
                    byte[] imgData = new byte[is.available()];
                    is.read(imgData);
//                    File file = new File(is);
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("stream-id", streamId)
                            .addFormDataPart("tags", tags)
                            .addFormDataPart("image", chosenImage.getDisplayName(), RequestBody.create(MediaType.parse("image/jpeg"), imgData))
                            .build();
                    Request uploadRequest = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();
                    Response uploadResponse = client.newCall(uploadRequest).execute();
                    if (!uploadResponse.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }
                    Log.d(TAG, uploadResponse.body().string());
                    Toast.makeText(UploadActivity.this, "Uploaded successfully!",
                            Toast.LENGTH_LONG).show();
//                    uploadResponse.close();


                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private ImagePicker imagePicker;

    public void pickImageSingle() {
        imagePicker = new ImagePicker(this);
        imagePicker.shouldGenerateMetadata(true);
        imagePicker.shouldGenerateThumbnails(true);
        imagePicker.setImagePickerCallback(this);
        imagePicker.pickImage();
    }

    public void pickImageMultiple() {
        imagePicker = new ImagePicker(this);
        imagePicker.allowMultiple();
        imagePicker.shouldGenerateMetadata(true);
        imagePicker.shouldGenerateThumbnails(true);
        imagePicker.setImagePickerCallback(this);
        imagePicker.pickImage();
    }

    private CameraImagePicker cameraPicker;

    public void takePicture() {
        cameraPicker = new CameraImagePicker(this);
        cameraPicker.shouldGenerateMetadata(true);
        cameraPicker.shouldGenerateThumbnails(true);
        cameraPicker.setImagePickerCallback(this);
        pickerPath = cameraPicker.pickImage();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Picker.PICK_IMAGE_DEVICE) {
                if (imagePicker == null) {
                    imagePicker = new ImagePicker(this);
                    imagePicker.setImagePickerCallback(this);
                }
                imagePicker.submit(data);
            } else if (requestCode == Picker.PICK_IMAGE_CAMERA) {
                if (cameraPicker == null) {
                    cameraPicker = new CameraImagePicker(this);
                    cameraPicker.setImagePickerCallback(this);
                    cameraPicker.reinitialize(pickerPath);
                }
                cameraPicker.submit(data);
            }
        }
    }

    @Override
    public void onImagesChosen(List<ChosenImage> images) {
        Log.d(TAG, images.toString());
        ImageView imgView = (ImageView) findViewById(R.id.chosenImage);
        imgView.setImageURI(Uri.parse(images.get(0).getQueryUri()));

        chosenImage = images.get(0);

        btUpload.setVisibility(View.VISIBLE);

//        MediaResultsAdapter adapter = new MediaResultsAdapter(images, getActivity());
//        lvResults.setAdapter(adapter);
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // You have to save path in case your activity is killed.
        // In such a scenario, you will need to re-initialize the CameraImagePicker
        outState.putString("picker_path", pickerPath);
        super.onSaveInstanceState(outState);
    }


}

