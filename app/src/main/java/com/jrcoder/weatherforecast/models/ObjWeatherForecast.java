package com.jrcoder.weatherforecast.models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ObjWeatherForecast {
    public static final String TAG = ObjWeatherForecast.class.getSimpleName();

    private String icon;
    private String weatherName;
    private String date;
    private float termMax;
    private float termMin;

    public ObjWeatherForecast() {
    }

    public ObjWeatherForecast(String icon, String weatherName, String date, float termMax, float termMin) {
        this.icon = icon;
        this.weatherName = weatherName;
        this.date = date;
        this.termMax = termMax;
        this.termMin = termMin;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getWeatherName() {
        return weatherName;
    }

    public void setWeatherName(String weatherName) {
        this.weatherName = weatherName;
    }


    public void setDate(String date) {
        this.date = date;
    }

    public float getTermMax() {
        return termMax;
    }

    public void setTermMax(float termMax) {
        this.termMax = termMax;
    }

    public float getTermMin() {
        return termMin;
    }

    public void setTermMin(float termMin) {
        this.termMin = termMin;
    }

    public void setIconToImageView(@NonNull final Context context, @NonNull ImageView imageView){
        final String url = "https://openweathermap.org/img/wn/%s@2x.png";
        Glide.with(context).load(String.format(url, getIcon())).into(imageView);
    }

    @SuppressLint("SimpleDateFormat")
    public String getDate(){
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("EEE, MMM d, yyyy");
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(dateFormat1.parse(date));
            return dateFormat2.format(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            return date;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public boolean isCurrentDay(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d, yyyy");
        Calendar calendar = Calendar.getInstance();
        return getDate().equals(dateFormat.format(calendar.getTime()));
    }
}
