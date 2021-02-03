package com.jrcoder.weatherforecast.retrofit2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jrcoder.weatherforecast.R;
import com.jrcoder.weatherforecast.models.forecast.WeatherForecast;
import com.jrcoder.weatherforecast.models.weather.CurrentWeather;
import com.jrcoder.weatherforecast.utils.SpHelper;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.jrcoder.weatherforecast.utils.SpHelper.KeyList.KEY_CITY_NAME;

public class RetrofitClient {

    public static final String TAG = RetrofitClient.class.getSimpleName();
    private final Context context;
    private final DataClient mDataClient;
    private final String API_KEY;

    @SuppressLint("StaticFieldLeak")
    private static RetrofitClient instance;

    private RetrofitClient(Context context) {
        this.context = context;
        this.API_KEY = context.getResources().getString(R.string.API_KEY);
        OkHttpClient builder = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(builder)
                .build();
        mDataClient = mRetrofit.create(DataClient.class);
    }

    public static RetrofitClient getInstance(Context context) {
        if (instance == null) {
            instance = new RetrofitClient(context);
        }
        return instance;
    }

    /**
     * Hàm rút gọn thao tác request lên api bằng retrofit
     *
     * @param call     interface của retrofit
     * @param callback hàm trả về kết quả sau khi request lên server
     * @param <T>      Custom object
     * @see com.jrcoder.weatherforecast.models
     */
    private <T> void requestAPI(@NonNull Call<T> call, @NonNull ICallbackRequestAPI<T> callback) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                if (response.body() != null) {
                    callback.onResponse(response.body());
                } else {
                    callback.onFailure("Can't download data from server!");
                    Log.e(TAG, "Method [requestAPI]: response.body() == NULL");
                }
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                callback.onFailure(t.getMessage());
                Log.e(TAG, "Method [requestAPI]: " + t.getMessage());
            }
        });
    }

    public RetrofitClient getCurrentWeatherByCityID(int cityID, @NonNull ICurrentWeather callback){
        requestAPI(mDataClient.getCurrentWeatherByCityID(cityID, API_KEY), new ICallbackRequestAPI<CurrentWeather>() {
            @Override
            public void onResponse(@NonNull CurrentWeather data) {
                if(data.getCod() == 200){
                    if(callback != null)
                        callback.onCurrentWeatherCompleted(data, "Success");
                }else{
                    if(callback != null)
                        callback.onCurrentWeatherCompleted(null,data.getMessage());
                }
            }

            @Override
            public void onFailure(String message) {
                Log.e(TAG, message);
                if(callback != null)
                    callback.onCurrentWeatherCompleted(null,message);
            }
        });

        return this;
    }

    public RetrofitClient getCurrentWeatherByCityName(@NonNull ICurrentWeather callback){
        final String cityName = SpHelper.getInstance(context).getValueString(KEY_CITY_NAME);
        if(cityName.equals("")){
            getCurrentWeatherByLocation(callback);
        }else{
            requestAPI(mDataClient.getCurrentWeatherByCityName(cityName, API_KEY), new ICallbackRequestAPI<CurrentWeather>() {
                @Override
                public void onResponse(@NonNull CurrentWeather data) {
                    if(data.getCod() == 200){
                        if(callback != null)
                            callback.onCurrentWeatherCompleted(data, "Success");
                    }else{
                        if(callback != null)
                            callback.onCurrentWeatherCompleted(null,data.getMessage());
                    }
                }

                @Override
                public void onFailure(String message) {
                    Log.e(TAG, message);
                    if(callback != null)
                        callback.onCurrentWeatherCompleted(null,message);
                }
            });
        }
        return this;
    }

    public RetrofitClient getCurrentWeatherByLocation(@NonNull ICurrentWeather callback){
        final double latitude = Double.parseDouble(SpHelper.getInstance(context).getValueString(SpHelper.KeyList.KEY_LATITUDE));
        final double longitude = Double.parseDouble(SpHelper.getInstance(context).getValueString(SpHelper.KeyList.KEY_LONGITUDE));

        requestAPI(mDataClient.getCurrentWeatherByLocation(latitude, longitude, API_KEY),
                new ICallbackRequestAPI<CurrentWeather>() {
            @Override
            public void onResponse(@NonNull CurrentWeather data) {
                if(data.getCod() == 200){
                    if(callback != null)
                        callback.onCurrentWeatherCompleted(data, "Success");
                }else{
                    if(callback != null)
                        callback.onCurrentWeatherCompleted(null,data.getMessage());
                }
            }

            @Override
            public void onFailure(String message) {
                Log.e(TAG, message);
                if(callback != null)
                    callback.onCurrentWeatherCompleted(null,message);
            }
        });
        return this;
    }

    public RetrofitClient get5DayWeatherForecastByLocation(@NonNull IMultiWeatherForecast callback){
        final double latitude = Double.parseDouble(SpHelper.getInstance(context).getValueString(SpHelper.KeyList.KEY_LATITUDE));
        final double longitude = Double.parseDouble(SpHelper.getInstance(context).getValueString(SpHelper.KeyList.KEY_LONGITUDE));
        requestAPI(mDataClient.getWeatherForecastByLocation(latitude, longitude, API_KEY),
                new ICallbackRequestAPI<WeatherForecast>() {
                    @Override
                    public void onResponse(@NonNull WeatherForecast data) {
                        if(data.getCod().equals("200")){
                            if(callback != null)
                                callback.onMultiWeatherForecastCompleted(data, "Success");
                        }else{
                            if(callback != null)
                                callback.onMultiWeatherForecastCompleted(null,data.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        Log.e(TAG, message);
                        if(callback != null)
                            callback.onMultiWeatherForecastCompleted(null,message);
                    }
                });
        return this;
    }

    public RetrofitClient get5DayWeatherForecastByCityID(int cityID,@NonNull IMultiWeatherForecast callback){
        requestAPI(mDataClient.getWeatherForecastByCityID(cityID, API_KEY),
                new ICallbackRequestAPI<WeatherForecast>() {
                    @Override
                    public void onResponse(@NonNull WeatherForecast data) {
                        if(data.getCod().equals("200")){
                            if(callback != null)
                                callback.onMultiWeatherForecastCompleted(data, "Success");
                        }else{
                            if(callback != null)
                                callback.onMultiWeatherForecastCompleted(null,data.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        Log.e(TAG, message);
                        if(callback != null)
                            callback.onMultiWeatherForecastCompleted(null,message);
                    }
                });
        return this;
    }

    public RetrofitClient get5DayWeatherForecastByCityName(@NonNull IMultiWeatherForecast callback){
        final String cityName = SpHelper.getInstance(context).getValueString(KEY_CITY_NAME);
        if(cityName.equals("")){
            get5DayWeatherForecastByLocation(callback);
        }else{
            requestAPI(mDataClient.getWeatherForecastByCityName(cityName, API_KEY),
                    new ICallbackRequestAPI<WeatherForecast>() {
                        @Override
                        public void onResponse(@NonNull WeatherForecast data) {
                            if(data.getCod().equals("200")){
                                if(callback != null)
                                    callback.onMultiWeatherForecastCompleted(data, "Success");
                            }else{
                                if(callback != null)
                                    callback.onMultiWeatherForecastCompleted(null,data.getMessage());

                            }
                        }

                        @Override
                        public void onFailure(String message) {
                            Log.e(TAG, message);
                            if(callback != null)
                                callback.onMultiWeatherForecastCompleted(null,message);
                        }
                    });
        }

        return this;
    }

    /**
     * Interface trả về kết quả sau khi request
     *
     * @param <T> custom object
     */
    private interface ICallbackRequestAPI<T> {
        /**
         * Nếu thành công trả về trong đây
         *
         * @param data dữ liệu sau khi thành công
         */
        void onResponse(@NonNull T data);

        /**
         * Thất bại trả về tin nhắn trong đây
         *
         * @param message tin nhắn thất bại
         */
        void onFailure(String message);
    }


    public interface ICurrentWeather{
        void onCurrentWeatherCompleted(@Nullable CurrentWeather currentWeather, @NonNull String message);
    }

    public interface IMultiWeatherForecast{
        void onMultiWeatherForecastCompleted(@Nullable WeatherForecast weatherForecast, @NonNull String message);
    }

}