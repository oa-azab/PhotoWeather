package com.example.robusta.photoweather.ui.main;

import android.content.Intent;
import android.util.Log;

import com.example.robusta.photoweather.ui.camera.CameraActivity;

/**
 * Created by robusta on 8/26/18.
 */

public class MainPresenter implements MainContract.Presenter {

    private static final String TAG = "MainPresenter";
    private MainContract.View view;

    public MainPresenter(MainContract.View view) {
        this.view = view;
    }

    @Override
    public void start() {

    }

    @Override
    public void onTakePictureClicked() {
        Log.d(TAG, "[onTakePictureClicked]");
        Intent cameraIntent = new Intent(view.getViewContext(), CameraActivity.class);
        view.getViewContext().startActivity(cameraIntent);
    }

    @Override
    public void onPermissionsGranted() {
        view.showMainView();
    }

    @Override
    public void onPermissionsDenied() {
        view.showRequestPermissions();
    }
}
