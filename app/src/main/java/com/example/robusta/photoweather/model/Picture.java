package com.example.robusta.photoweather.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by robusta on 8/26/18.
 */
@Entity
public class Picture {

    @PrimaryKey
    @NonNull
    private String id = "";
    private String imageFilePath;

    public Picture(String id, String imageFilePath) {
        this.id = id;
        this.imageFilePath = imageFilePath;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        this.imageFilePath = imageFilePath;
    }
}
