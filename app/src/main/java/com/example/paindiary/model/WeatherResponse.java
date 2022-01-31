package com.example.paindiary.model;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {

    @SerializedName("main")
    public Main main;

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }
}

