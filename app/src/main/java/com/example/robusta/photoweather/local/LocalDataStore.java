package com.example.robusta.photoweather.local;

import android.content.Context;

import com.example.robusta.photoweather.model.Picture;
import com.example.robusta.photoweather.util.AppExecutors;

import java.util.List;

/**
 * Created by robusta on 8/26/18.
 */

public class LocalDataStore {

    private PictureDao pictureDao;
    private AppExecutors appExecutors;

    public LocalDataStore(Context context) {
        appExecutors = AppExecutors.getInstance();
        DataStore dataStore = DataStore.getInstance(context);
        pictureDao = dataStore.pictureDao();
    }

    public void loadData(final LoadDataCallback callback) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final List<Picture> result = pictureDao.getAll();
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onDataLoaded(result);
                    }
                });
            }
        });
    }

    public void loadPicture(final String pictureId, final LoadItemCallback callback) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final Picture picture = pictureDao.getPicture(pictureId);
                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        callback.onItemLoaded(picture);
                    }
                });
            }
        });
    }

    public interface LoadDataCallback {
        void onDataLoaded(List<Picture> data);
    }

    public interface LoadItemCallback {
        void onItemLoaded(Picture picture);
    }
}
