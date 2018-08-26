package com.example.robusta.photoweather.local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.robusta.photoweather.model.Picture;

import java.util.List;

/**
 * Created by robusta on 8/26/18.
 */

@Dao
public interface PictureDao {

    @Query("SELECT * FROM picture")
    List<Picture> getAll();

    @Query("SELECT * FROM picture WHERE id= :id")
    Picture getPicture(String id);

    @Insert
    void insert(Picture picture);

}
