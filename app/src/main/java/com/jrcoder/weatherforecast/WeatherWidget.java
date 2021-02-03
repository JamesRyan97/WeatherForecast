package com.jrcoder.weatherforecast;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.jrcoder.weatherforecast.models.weather.CurrentWeather;
import com.jrcoder.weatherforecast.retrofit2.RetrofitClient;

/**
 * Implementation of App Widget functionality.
 */
public class WeatherWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        if(appWidgetManager == null) return;
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);

        // Register an onClickListener
        Intent intent = new Intent(context, WeatherWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        Bundle bundle = new Bundle();
        bundle.putInt(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetId);
        intent.putExtra("data", bundle);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.imvRefresh, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);

        RetrofitClient.getInstance(context).getCurrentWeatherByCityName(new RetrofitClient.ICurrentWeather() {
            @Override
            public void onCurrentWeatherCompleted(@Nullable CurrentWeather currentWeather, @NonNull String message) {
                if(currentWeather != null){
                    Log.d("CheckApp", currentWeather.getName());
                    final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
                    views.setTextViewText(R.id.tvLocationName, currentWeather.getName());
                    views.setTextViewText(R.id.tvSunsetTime, currentWeather.getSys().getTime(true));
                    views.setTextViewText(R.id.tvSunriseTime, currentWeather.getSys().getTime(false));
                    views.setTextViewText(R.id.tvWind, currentWeather.getWind().getSpeed() +" m/s");
                    views.setTextViewText(R.id.tvHumidity, String.format(context.getResources().getString(R.string.humidity),currentWeather.getMain().getHumidity()) + "%");
                    views.setTextViewText(R.id.tvWeatherName, currentWeather.getWeather().get(0).getDescription());
                    views.setTextViewText(R.id.tvTemperature,String.valueOf((int)currentWeather.getMain().getTemp()));
                    // Instruct the widget manager to update the widget
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                }
            }
        });
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)){
            Bundle bundle = intent.getBundleExtra("data");
            if(bundle == null) return;
            int appWidgetId = bundle.getInt(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}