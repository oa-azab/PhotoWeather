package com.example.robusta.photoweather.ui.camera;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by robusta on 8/26/18.
 */

public interface CameraContract {

    interface View {
        Context getViewContext();

        void showWeatherCard(String temperature, String summary);

        void finishView();
    }

    interface Presenter {
        void start();

        void stop();

        void addWeather();

        void savePicture(Bitmap weatherPicture);
    }
}
