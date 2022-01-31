package com.example.paindiary.service;

import com.example.paindiary.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {

    @GET("data/2.5/weather?")
    Call<WeatherResponse> getCurrentWeatherData(
            @Query ("q") String city_name,
            @Query ("appid") String app_id,
            @Query ("units") String units
    );
}
