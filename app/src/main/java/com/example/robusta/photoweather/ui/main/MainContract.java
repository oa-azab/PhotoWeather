package com.example.robusta.photoweather.ui.main;

import android.content.Context;

import com.example.robusta.photoweather.model.Picture;
import com.example.robusta.photoweather.ui.main.adapter.PictureAdapter;

/**
 * Created by robusta on 8/26/18.
 */

public interface MainContract {

    interface View {
        Context getViewContext();

        void showRequestPermissions();

        void showMainView();

        void showEmptyState(boolean show);

        void setAdapter(PictureAdapter adapter);
    }

    interface Presenter {
        void start();

        void onTakePictureClicked();

        void onPermissionsGranted();

        void onPermissionsDenied();
    }
}
