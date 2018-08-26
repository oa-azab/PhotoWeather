package com.example.robusta.photoweather.ui.camera;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.robusta.photoweather.R;
import com.example.robusta.photoweather.local.DataStore;
import com.example.robusta.photoweather.model.Forecast;
import com.example.robusta.photoweather.model.Picture;
import com.example.robusta.photoweather.remote.ApiService;
import com.example.robusta.photoweather.util.AppExecutors;
import com.example.robusta.photoweather.util.BitmapUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by robusta on 8/26/18.
 */

public class CameraPresenter implements CameraContract.Presenter {

    private static final String TAG = "CameraPresenter";
    private CameraContract.View view;
    private ApiService darkSkyService;
    private FusedLocationProviderClient fusedLocationClient;


    public CameraPresenter(CameraContract.View view) {
        this.view = view;
    }

    @Override
    public void start() {
        createRetrofit();
    }

    @Override
    public void stop() {

    }

    @Override
    public void addWeather() {
        getCurrentLocation();
    }

    @Override
    public void savePicture(Bitmap weatherPicture) {
        SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
        String pictureName = s.format(new Date()) + ".png";

        File directory = new File(Environment.getExternalStorageDirectory(), "Weather Pictures");
        if (!directory.exists()) directory.mkdirs();
        File pictureFile = new File(directory, pictureName);
        final Picture picture = new Picture(UUID.randomUUID().toString(), pictureFile.getPath());

        BitmapUtil.saveBitmap(weatherPicture, pictureFile.getPath());

        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                DataStore.getInstance(view.getViewContext())
                        .pictureDao()
                        .insert(picture);
            }
        });
    }

    private void createRetrofit() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(view.getViewContext().getString(R.string.dark_sky_base_url)).addConverterFactory(ScalarsConverterFactory.create()).build();
        darkSkyService = retrofit.create(ApiService.class);
    }

    private void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(view.getViewContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(view.getViewContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "[getCurrentLocation] somehow this called without asking for permission");
            return;
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(view.getViewContext());
        Task<Location> task = fusedLocationClient.getLastLocation();
        task.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "[getCurrentLocation] success");
                    Location location = task.getResult();

                    if (location == null) {
                        Log.e(TAG, "[getCurrentLocation] location is NULL");
                        return;
                    }

                    // getLocationAddress(location);
                    getForecast(location.getLatitude(), location.getLongitude());
                } else {
                    Log.d(TAG, "[getCurrentLocation] failure");
                }
            }
        });
    }

    private void parseAndViewForecast(final String response) {
        try {

            JSONObject root = new JSONObject(response);
            JSONObject currently = root.getJSONObject("currently");

            Gson gson = new Gson();
            final Forecast currentForecast = gson.fromJson(currently.toString(), Forecast.class);
            view.showWeatherCard(
                    "Temperature: " + currentForecast.getTemperature(),
                    currentForecast.getSummary());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void getForecast(double latitude, double longitude) {
        Call<String> call = darkSkyService.getForecast(
                view.getViewContext().getString(R.string.dark_sky_secret_key),
                latitude, longitude,
                "ca", "minutely");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d(TAG, "[getForecast] onResponse");
                Log.d(TAG, "[getForecast] response: " + response.body());
                String resBody = response.body();
                parseAndViewForecast(resBody);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "[getForecast] onFailure");
                // view.showErrorView();
            }
        });
    }

}
