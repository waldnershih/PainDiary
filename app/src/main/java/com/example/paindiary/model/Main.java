package com.example.paindiary.model;

import com.google.gson.annotations.SerializedName;

public class Main {

    @SerializedName("humidity")
    public float humidity;

    @SerializedName("pressure")
    public float pressure;

    @SerializedName("temp")
    public float temp;

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getPressure() {
        return pressure;
    }

    public void setPressure(float pressure) {
        this.pressure = pressure;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }



}
