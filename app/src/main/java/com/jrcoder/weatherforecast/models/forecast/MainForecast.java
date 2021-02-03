package com.jrcoder.weatherforecast.models.forecast;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jrcoder.weatherforecast.models.weather.Main;

public class MainForecast extends Main {
    public static final String TAG = MainForecast.class.getSimpleName();

    @SerializedName("sea_level")
    @Expose
    private int seaLevel;
    @SerializedName("grnd_level")
    @Expose
    private int grndLevel;
    @SerializedName("temp_kf")
    @Expose
    private float tempKf;


    public int getSeaLevel() {
        return seaLevel;
    }

    public void setSeaLevel(int seaLevel) {
        this.seaLevel = seaLevel;
    }

    public int getGrndLevel() {
        return grndLevel;
    }

    public void setGrndLevel(int grndLevel) {
        this.grndLevel = grndLevel;
    }

    public float getTempKf() {
        return tempKf;
    }

    public void setTempKf(float tempKf) {
        this.tempKf = tempKf;
    }
}
