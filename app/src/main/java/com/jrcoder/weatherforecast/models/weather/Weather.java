package com.jrcoder.weatherforecast.models.weather;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Weather {
    public static final String TAG = Weather.class.getSimpleName();
    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("main")
    @Expose
    private String main;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("icon")
    @Expose
    private String icon;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIconToImageView(@NonNull final Context context,@NonNull ImageView imageView){
        final String url = "https://openweathermap.org/img/wn/%s@2x.png";
        Glide.with(context).load(String.format(url, getIcon())).into(imageView);
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
