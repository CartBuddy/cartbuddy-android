package com.example.gulls.cartbuddy;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by wentingsong on 2017/12/1.
 */

public class ImagePickerFragment extends Fragment implements ImagePickerCallback {
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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_picker, null);

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED
                ) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        streamId = "ahBzfmNvbm5leHVzLWd1bGxzchMLEgZTdHJlYW0YgICAgPjChAoM";

//        lvResults = (ListView) view.findViewById(R.id.lvResults);
        btPickImageSingle = (Button) view.findViewById(R.id.btGallerySingleImage);
        btPickImageSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImageSingle();
            }
        });
//        btPickImageMultiple = (Button) view.findViewById(R.id.btGalleryMultipleImages);
//        btPickImageMultiple.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                pickImageMultiple();
//            }
//        });
        btTakePicture = (Button) view.findViewById(R.id.btCameraImage);
        btTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        btUpload = (Button) view.findViewById(R.id.upload_btn);
        btUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Uploading...");
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
                    String type = getActivity().getContentResolver().getType(Uri.parse(chosenImage.getQueryUri()));
                    Log.d(TAG, type);
                    int count = getActivity().getContentResolver().query(Uri.parse(chosenImage.getQueryUri()), null, null, null, null).getCount();
                    Log.d(TAG, String.valueOf(count));
                    InputStream is = getActivity().getContentResolver().openInputStream(Uri.parse(chosenImage.getQueryUri()));
                    byte[] imgData = new byte[is.available()];
                    is.read(imgData);
//                    File file = new File(is);
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("stream-id", streamId)
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
//                    Log.d(TAG, response.body().string());
//                    uploadResponse.close();


                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
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
        ImageView imgView = (ImageView) getView().findViewById(R.id.chosenImage);
        imgView.setImageURI(Uri.parse(images.get(0).getQueryUri()));

        chosenImage = images.get(0);

        btUpload.setVisibility(View.VISIBLE);

//        MediaResultsAdapter adapter = new MediaResultsAdapter(images, getActivity());
//        lvResults.setAdapter(adapter);
    }

    @Override
    public void onError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // You have to save path in case your activity is killed.
        // In such a scenario, you will need to re-initialize the CameraImagePicker
        outState.putString("picker_path", pickerPath);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("picker_path")) {
                pickerPath = savedInstanceState.getString("picker_path");
            }
        }
    }
}
