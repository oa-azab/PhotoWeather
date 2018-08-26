package com.example.robusta.photoweather.local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.robusta.photoweather.model.Picture;

/**
 * Created by robusta on 8/26/18.
 */

@Database(entities = {Picture.class}, version = 1)
public abstract class DataStore extends RoomDatabase {

    private static DataStore INSTANCE;

    public abstract PictureDao pictureDao();

    private static final Object sLock = new Object();

    public static DataStore getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        DataStore.class, "Pictures.db")
                        .build();
            }
            return INSTANCE;
        }
    }
}
