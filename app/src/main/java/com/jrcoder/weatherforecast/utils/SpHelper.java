package com.jrcoder.weatherforecast.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class SpHelper {
    public static final String TAG = SpHelper.class.getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private static SpHelper instance;

    private final SharedPreferences sharedPref;

    public static class KeyList{
        public static final String KEY_LATITUDE = "Latitude";
        public static final String KEY_LONGITUDE = "Longitude";
        public static final String KEY_CITY_NAME = "CityName";

    }

    /**
     * @param context A Context of the application package implementing this class.
     */
    private SpHelper(Context context) {
        this.sharedPref = context.getSharedPreferences(SpHelper.class.getSimpleName() + "Data", Context.MODE_PRIVATE);
    }

    public static SpHelper getInstance(@NonNull Context context) {
        if (instance == null)
            instance = new SpHelper(context);
        return instance;
    }

    /**
     * Set data for the key
     *
     * @param key   Key needs to set data
     * @param value Value to set
     */
    public void setValueString(String key, String value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key);
        editor.putString(key, value == null ? "" : value);
        editor.apply();
    }

    /**
     * Get string data from SharedPreferences
     *
     * @param key Key needs to get data
     * @return string value
     */
    public String getValueString(String key) {
        if(key.equals(KeyList.KEY_LATITUDE) || key.equals(KeyList.KEY_LONGITUDE)){
            return sharedPref.getString(key, "0");
        }
        return sharedPref.getString(key, "");
    }

    /**
     * Set data for the key
     *
     * @param key   Key needs to set data
     * @param value Value to set
     */
    public void setValueBool(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key);
        editor.putBoolean(key, value);
        editor.apply();
    }

    /**
     * Get boolean data from SharedPreferences
     *
     * @param key Key needs to get data
     * @return boolean value
     */
    public boolean getValueBool(String key) {
        return sharedPref.getBoolean(key, false);
    }

    /**
     * Set data for the key
     *
     * @param key   Key needs to set data
     * @param value Value to set
     */
    public void setValueInt(String key, int value) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key);
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * Get integer data from SharedPreferences
     *
     * @param key Key needs to get data
     * @return integer value
     */
    public int getValueInt(String key) {
        return sharedPref.getInt(key, 0);
    }


    /**
     * Delete data of 1 key
     *
     * @param key Key needs to remove data
     */
    public void removeData(String key) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key);
        editor.apply();
    }

    /**
     * Delete all data or just account data
     */
    public void removeAllData() {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }
}
