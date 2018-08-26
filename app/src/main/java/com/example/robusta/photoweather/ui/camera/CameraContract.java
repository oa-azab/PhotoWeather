package com.example.robusta.photoweather.ui.camera;

import android.content.Context;

/**
 * Created by robusta on 8/26/18.
 */

public interface CameraContract {

    interface View {
        Context getViewContext();
    }

    interface Presenter {
        void start();

        void stop();
    }
}
