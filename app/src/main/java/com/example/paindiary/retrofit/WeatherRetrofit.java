package com.example.paindiary.retrofit;

import com.example.paindiary.service.WeatherService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherRetrofit {
    private static Retrofit retrofit;
    private static final String BASE_URL = "https://api.openweathermap.org/";

    public static WeatherService getRetrofitService(){
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(WeatherService.class);
    }
}
