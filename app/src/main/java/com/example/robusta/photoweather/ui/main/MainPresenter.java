package com.example.robusta.photoweather.ui.main;

import android.content.Intent;
import android.util.Log;

import com.example.robusta.photoweather.local.LocalDataStore;
import com.example.robusta.photoweather.model.Picture;
import com.example.robusta.photoweather.ui.camera.CameraActivity;
import com.example.robusta.photoweather.ui.detail.DetailActivity;
import com.example.robusta.photoweather.ui.main.adapter.PictureAdapter;

import java.util.List;

import static com.example.robusta.photoweather.util.Consts.EXTRA_PICTURE_ID;

/**
 * Created by robusta on 8/26/18.
 */

public class MainPresenter implements MainContract.Presenter {

    private static final String TAG = "MainPresenter";
    private MainContract.View view;
    private LocalDataStore localDataStore;

    private PictureAdapter adapter;

    public MainPresenter(MainContract.View view) {
        this.view = view;
        localDataStore = new LocalDataStore(view.getViewContext());
        adapter = new PictureAdapter(callback);
        view.setAdapter(adapter);
    }

    @Override
    public void start() {
        localDataStore.loadData(new LocalDataStore.LoadDataCallback() {
            @Override
            public void onDataLoaded(List<Picture> data) {
                view.showEmptyState(data.size() == 0);
                if (data.size() > 0) {
                    adapter.updateData(data);
                }
            }
        });
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

    private PictureAdapter.PictureCallback callback = new PictureAdapter.PictureCallback() {
        @Override
        public void onPictureClicked(Picture picture) {
            Intent detailIntent = new Intent(view.getViewContext(), DetailActivity.class);
            detailIntent.putExtra(EXTRA_PICTURE_ID, picture.getId());
            view.getViewContext().startActivity(detailIntent);
        }
    };
}
