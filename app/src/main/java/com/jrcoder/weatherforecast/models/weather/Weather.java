package com.jrcoder.weatherforecast.models.weather;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jrcoder.weatherforecast.R;

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
        return description.substring(0, 1).toUpperCase() + description.substring(1).toLowerCase();
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

    public int getDrawableID(){
        if(getIcon().equals("01d")){
            return R.drawable.icon_01d;
        }else if(getIcon().equals("01n")){
            return R.drawable.icon_01n;
        }else if(getIcon().equals("02d")){
            return R.drawable.icon_02d;
        }else if(getIcon().equals("02n")){
            return R.drawable.icon_02n;
        }else if(getIcon().equals("03d")){
            return R.drawable.icon_03d;
        }else if(getIcon().equals("03n")){
            return R.drawable.icon_03n;
        }else if(getIcon().equals("04d")){
            return R.drawable.icon_04d;
        }else if(getIcon().equals("04n")){
            return R.drawable.icon_04n;
        }else if(getIcon().equals("09d")){
            return R.drawable.icon_09d;
        }else if(getIcon().equals("09n")){
            return R.drawable.icon_09n;
        }else if(getIcon().equals("10d")){
            return R.drawable.icon_10d;
        }else if(getIcon().equals("10n")){
            return R.drawable.icon_10n;
        }else if(getIcon().equals("11d")){
            return R.drawable.icon_11d;
        }else if(getIcon().equals("11n")){
            return R.drawable.icon_11n;
        }else if(getIcon().equals("13d")){
            return R.drawable.icon_13d;
        }else if(getIcon().equals("13n")){
            return R.drawable.icon_13n;
        }else if(getIcon().equals("50d")){
            return R.drawable.icon_50d;
        }else if(getIcon().equals("50n")){
            return R.drawable.icon_50n;
        }else{
            return R.drawable.icon_02d;
        }
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
