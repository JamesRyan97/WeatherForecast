package com.jrcoder.weatherforecast.views.rootBase;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jrcoder.weatherforecast.utils.SpHelper;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/*
 *  Date created: 2021/1/18
 *  Last updated: 2021/1/18
 *  Name of project: Guarantee
 *  Description: BaseAppCompatActivity
 *  Auth: @TrongLE
 *  Mail: trong.le@alo360.com
 */

public abstract class BaseAppCompatActivity extends AppCompatActivity {
    public static final String TAG = BaseAppCompatActivity.class.getSimpleName();


    /**
     * Danh sách status của toast
     */
    public static class ToastStatus{
        public static final int STATUS_SUCCESS = 1;
        public static final int STATUS_DEFAULT = 0;
        public static final int STATUS_FAIL = 2;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(initLayout());

        initVariables();
        main(savedInstanceState);
        initAction();
    }

    /**
     * Initialize the root layout
     * @return root view
     */
    protected abstract View initLayout();

    /**
     * Variable initialization
     */
    protected abstract void initVariables();

    /**
     * The main function contains the functions of the application
     * @param savedInstanceState if the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.
     *     <b><i>Note: Otherwise it is null.</i></b>
     */
    protected abstract void main(@Nullable Bundle savedInstanceState);

    /**
     * Contains key press or interactive functions
     */
    protected abstract void initAction();

    /**
     * Cài đặt màu cho text trên status bar <item name="android:windowActivityTransitions">true</item>
     * @param isResetDefault nếu false set màu đen cho text ngược lại reset lại mặc định trong style.xml
     */
    public void setBlackTextColorStatusBar(boolean isResetDefault){
        //Cài đặt màu đen cho chữ trên status bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(isResetDefault){
                getWindow().getDecorView().setSystemUiVisibility(0);
            }else{
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

    /**
     * Set status bar color
     * @param resColor id of color
     */
    public void setStatusBarColor(int resColor){
        getWindow().setStatusBarColor(getResources().getColor(resColor));
    }


    /**
     * init toolbar
     * @param toolbar toolbar view
     */
    public void initToolbar(@NonNull Toolbar toolbar){
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    /**
     * init toolbar
     * @param toolbar toolbar view
     * @param resId navigation icon
     */
    public void initToolbar(@NonNull Toolbar toolbar, @DrawableRes int resId){
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(resId);
        toolbar.setNavigationOnClickListener(v -> finish());
    }


    /**
     * Show the view for the specified duration.
     * @param message message
     * @param gravity Set the location at which the notification should appear on the screen.
     * @see Gravity
     */
    public void showCustomToast(@NonNull String message, int gravity){
        Toast toast = new Toast(this);
        RelativeLayout rlMain = initCustomToastView();
        TextView tvContent = (TextView) rlMain.getChildAt(0);
        if(tvContent != null){
            tvContent.setText(message);
            tvContent.setTextColor(Color.WHITE);
        }
        toast.setView(rlMain);
        toast.setGravity(gravity,0,0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();

    }

    /**
     * implementation 'com.google.android.gms:play-services-location:17.1.0'
     * Get location of the device
     */
    public void getCurrentLocation(IGetLocation callback) {

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            showCustomToast("Please grant permission!", Gravity.CENTER);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener((Activity) this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        //Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            try {
                                Geocoder geocoder = new Geocoder(BaseAppCompatActivity.this, Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                final String cityName = addresses.get(0).getSubAdminArea();
                                SpHelper.getInstance(BaseAppCompatActivity.this).setValueString(SpHelper.KeyList.KEY_CITY_NAME, String.valueOf(cityName));
                            } catch (IOException e) {
                                Log.e(TAG, e.getMessage());
                                e.printStackTrace();
                            }

                            SpHelper.getInstance(BaseAppCompatActivity.this).setValueString(SpHelper.KeyList.KEY_LATITUDE, String.valueOf(location.getLatitude()));
                            SpHelper.getInstance(BaseAppCompatActivity.this).setValueString(SpHelper.KeyList.KEY_LONGITUDE, String.valueOf(location.getLongitude()));
                        }

                        if(callback != null){
                            callback.onGetLocationCompleted(location);
                        }
                    }
                });
    }


    /**
     * Khởi tạo giao diện cho toast
     * @return view
     */
    private RelativeLayout initCustomToastView(){
        RelativeLayout rlMain = new RelativeLayout(this);

        TextView tvContent = new TextView(this);
        tvContent.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tvContent.setTextSize(15);

        RelativeLayout.LayoutParams lpContent = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpContent.setMargins(30,25,30,25);
        lpContent.addRule(RelativeLayout.CENTER_IN_PARENT);
        rlMain.addView(tvContent,lpContent);

        RelativeLayout.LayoutParams lpMain = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpMain.setMargins(2,2,2,2);
        rlMain.setLayoutParams(lpMain);

        int startColor = Color.rgb(15,32,39);
        int centerColor = Color.rgb(32,58,67);
        int endColor = Color.rgb(44,83,100);

        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setColors(new int[]{startColor,centerColor,endColor});
        gradientDrawable.setAlpha(204);
        gradientDrawable.setShape(GradientDrawable.RECTANGLE);
        gradientDrawable.setStroke(1, Color.WHITE);
        gradientDrawable.setCornerRadius(18);

        rlMain.setBackground(gradientDrawable);

        return rlMain;
    }

    /**
     * Show the view for the specified duration.
     * @param resID the resource identifier of the string resource to be displayed
     * @param gravity Set the location at which the notification should appear on the screen.
     * @see Gravity
     */
    public void showCustomToast(@StringRes int resID, int gravity){
        Toast toast = new Toast(this);
        RelativeLayout rlMain = initCustomToastView();
        TextView tvContent = (TextView) rlMain.getChildAt(0);
        if(tvContent != null){
            tvContent.setText(resID);
            tvContent.setTextColor(Color.WHITE);
        }
        toast.setView(rlMain);
        toast.setGravity(gravity,0,0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Show the view for the specified duration.
     * @param message message
     * @param gravity Set the location at which the notification should appear on the screen.
     *                @see Gravity
     * @param status set the status for the toast
     *               @see ToastStatus
     *
     */
    public void showCustomToast(@NonNull String message, int gravity, int status){
        Toast toast = new Toast(this);
        RelativeLayout rlMain = initCustomToastView();
        TextView tvContent = (TextView) rlMain.getChildAt(0);
        if(tvContent != null){
            tvContent.setText(message);
            int iconID = -1;
            switch (status){
                case ToastStatus.STATUS_DEFAULT:
                    tvContent.setTextColor(Color.WHITE);
                    break;
//                case ToastStatus.STATUS_FAIL:
//                    tvContent.setTextColor(Color.RED);
//                    iconID = R.drawable.ic_baseline_error_outline_18;
//                    break;
//                case ToastStatus.STATUS_SUCCESS:
//                    tvContent.setTextColor(Color.GREEN);
//                    iconID = R.drawable.ic_baseline_check_circle_outline_18;
//                    break;
            }

            if(iconID != -1){
                tvContent.setCompoundDrawablesWithIntrinsicBounds(iconID,0,0,0);
                tvContent.setCompoundDrawablePadding(10);
            }
        }
        toast.setView(rlMain);
        toast.setGravity(gravity,0,0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public interface IGetLocation{
        void onGetLocationCompleted(Location location);
    }

}
