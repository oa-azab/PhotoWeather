package com.example.robusta.photoweather.ui.main;

import android.content.Context;

/**
 * Created by robusta on 8/26/18.
 */

public interface MainContract {

    interface View {
        Context getViewContext();

        void showRequestPermissions();

        void showMainView();
    }

    interface Presenter {
        void start();

        void onTakePictureClicked();

        void onPermissionsGranted();

        void onPermissionsDenied();
    }
}
