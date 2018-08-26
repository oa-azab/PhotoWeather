package com.example.robusta.photoweather.ui.camera;

/**
 * Created by robusta on 8/26/18.
 */

public class CameraPresenter implements CameraContract.Presenter {

    private CameraContract.View view;

    public CameraPresenter(CameraContract.View view) {
        this.view = view;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }
}
