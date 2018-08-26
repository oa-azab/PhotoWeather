package com.example.robusta.photoweather.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by robusta on 8/26/18.
 */

public interface ApiService {

    @GET("forecast/{key}/{latitude},{longitude}")
    Call<String> getForecast(
            @Path("key") String key,
            @Path("latitude") double lat,
            @Path("longitude") double lng,
            @Query("units") String unit,
            @Query("exclude") String exclude);
    
}
