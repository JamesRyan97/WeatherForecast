package com.jrcoder.weatherforecast.views;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import com.jrcoder.weatherforecast.R;
import com.jrcoder.weatherforecast.adapters.AWeatherForecast;
import com.jrcoder.weatherforecast.databinding.ActivityMainBinding;
import com.jrcoder.weatherforecast.models.ObjWeatherForecast;
import com.jrcoder.weatherforecast.models.forecast.ListForecast;
import com.jrcoder.weatherforecast.models.forecast.WeatherForecast;
import com.jrcoder.weatherforecast.models.weather.CurrentWeather;
import com.jrcoder.weatherforecast.utils.RequestPermissions;
import com.jrcoder.weatherforecast.views.rootBase.BaseAppCompatActivity;
import com.jrcoder.weatherforecast.retrofit2.RetrofitClient;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity
        extends BaseAppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener,
                    RequestPermissions.IGrantPermissionResults,
                    BaseAppCompatActivity.IGetLocation,
                    RetrofitClient.IMultiWeatherForecast,
                    RetrofitClient.ICurrentWeather{

    public static final String TAG = MainActivity.class.getSimpleName();
    private final Context context = this;


    private ActivityMainBinding mView;
    private RequestPermissions requestPermissions;

    private ArrayList<ObjWeatherForecast> mListWeatherForecast;
    private AWeatherForecast adapter;

    @Override
    protected View initLayout() {
        setBlackTextColorStatusBar(false);
        mView = ActivityMainBinding.inflate(getLayoutInflater());

        return mView.getRoot();
    }

    @Override
    protected void initVariables() {
        requestPermissions = new RequestPermissions(this,this);
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        if(requestPermissions.checkPermissionGrant(permissions)){
            getCurrentLocation(this);
        }else{
            requestPermissions.sendRequest(permissions);
        }
    }



    @Override
    protected void main(@Nullable Bundle savedInstanceState) {
        setupRecyclerViewWeatherForecast();
    }

    private void getWeather() {
        RetrofitClient.getInstance(context).getCurrentWeatherByCityName(this).get5DayWeatherForecastByCityName(this);
    }

    private void setupRecyclerViewWeatherForecast(){
        mListWeatherForecast = new ArrayList<>();
        adapter = new AWeatherForecast(context,mListWeatherForecast);
        mView.rvWeatherForecast.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL,false));
        mView.rvWeatherForecast.setAdapter(adapter);
        mView.rvWeatherForecast.setHasFixedSize(true);
    }


    @Override
    protected void initAction() {
        mView.refreshMain.setOnRefreshListener(this);
        mView.imvRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mView.refreshMain.setRefreshing(true);
                getWeather();
            }
        });
    }

    @Override
    public void onRefresh() {
        getWeather();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        requestPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    public void onGrantPermission(boolean isGrant, ArrayList<String> permissionsAllowed, ArrayList<String> permissionsDenied) {
        if(isGrant){
            getCurrentLocation(this);
        }
    }

    @Override
    public void onGetLocationCompleted(Location location) {
        getWeather();
    }

    private void dismissRefresh(){
        new Handler(Looper.getMainLooper()).postDelayed(() -> mView.refreshMain.setRefreshing(false),1000);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCurrentWeatherCompleted(CurrentWeather currentWeather, @NonNull String message) {
        dismissRefresh();
        if(currentWeather != null){
            mView.tvTemperature.setText(String.valueOf((int)currentWeather.getMain().getTemp()));
            mView.tvLocationName.setText(currentWeather.getName());
            mView.tvWeatherName.setText(currentWeather.getWeather().get(0).getDescription());
            mView.tvHumidity.setText(String.format(getResources().getString(R.string.humidity),currentWeather.getMain().getHumidity()) + "%");
            mView.tvSunsetTime.setText(currentWeather.getSys().getTime(true));
            mView.tvSunriseTime.setText(currentWeather.getSys().getTime(false));
            mView.tvWind.setText(currentWeather.getWind().getSpeed() +" m/s");
            currentWeather.getWeather().get(0).setIconToImageView(context,mView.imvWeather);
        }else{
            showCustomToast(message, Gravity.CENTER);
        }
    }

    @Override
    public void onMultiWeatherForecastCompleted(WeatherForecast weatherForecast, @NonNull String message) {
        dismissRefresh();
        if(weatherForecast != null){
            mListWeatherForecast.clear();
            adapter.removeDataSource();
            ArrayList<String> listDate = new ArrayList<>();
            for(ListForecast listForecast : weatherForecast.getList()){
                if(!listDate.contains(listForecast.getDate()) && !listForecast.isCurrentDay()){
                    listDate.add(listForecast.getDate());
                    mListWeatherForecast.add(new ObjWeatherForecast(listForecast.getWeather().get(0).getIcon(),
                            listForecast.getWeather().get(0).getDescription(),
                            listForecast.getDtTxt(),
                            listForecast.getMain().getTempMax(),
                            listForecast.getMain().getTempMin()));
                }
            }
            adapter.setDataSource(mListWeatherForecast);
            mView.tvTitleNumberDayForecast.setText(String.format(getString(R.string.d_day_forecast),mListWeatherForecast.size()));
        }else{
            showCustomToast(message, Gravity.CENTER);
        }
    }

}