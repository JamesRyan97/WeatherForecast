package com.jrcoder.weatherforecast.retrofit2;

import com.jrcoder.weatherforecast.models.forecast.WeatherForecast;
import com.jrcoder.weatherforecast.models.weather.CurrentWeather;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DataClient {

    @GET("/data/2.5/forecast?&lang=vi&units=metric")
    Call<WeatherForecast> getWeatherForecastByCityID(@Query("id") int cityID, @Query("appid") String apiKey);

    @GET("/data/2.5/forecast?&lang=vi&units=metric")
    Call<WeatherForecast> getWeatherForecastByLocation(@Query("lat") double latitude, @Query("lon") double longitude, @Query("appid") String apiKey);

    @GET("/data/2.5/forecast?&lang=vi&units=metric")
    Call<WeatherForecast> getWeatherForecastByCityName(@Query("q") String cityName, @Query("appid") String apiKey);

    @GET("/data/2.5/weather?&lang=vi&units=metric")
    Call<CurrentWeather> getCurrentWeatherByCityID(@Query("id") int cityID, @Query("appid") String apiKey);

    @GET("/data/2.5/weather?&lang=vi&units=metric")
    Call<CurrentWeather> getCurrentWeatherByCityName(@Query("q") String cityName, @Query("appid") String apiKey);

    @GET("/data/2.5/weather?&lang=vi&units=metric")
    Call<CurrentWeather> getCurrentWeatherByLocation(@Query("lat") double latitude, @Query("lon") double longitude, @Query("appid") String apiKey);

}
