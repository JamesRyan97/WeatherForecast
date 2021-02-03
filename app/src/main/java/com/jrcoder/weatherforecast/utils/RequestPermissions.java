package com.jrcoder.weatherforecast.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class RequestPermissions {
    public static final String TAG = RequestPermissions.class.getSimpleName();

    public final int REQUEST_PERMISSION_CODE = 9204;
    private final Activity activity;
    private final IGrantPermissionResults callback;

    public RequestPermissions(@NonNull Activity activity, @NonNull IGrantPermissionResults callback) {
        this.activity = activity;
        this.callback = callback;
    }

    /**
     * Request permissions
     *
     * @param listPermissions List of permissions to license  {@see AndroidManifest.xml }
     */
    public void sendRequest(ArrayList<String> listPermissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> listRequest = new ArrayList<>();
            for (String permission : listPermissions) {
                if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    listRequest.add(permission);
                }
            }

            if (listRequest.size() > 0) {
                String[] permissions = new String[listRequest.size()];
                ActivityCompat.requestPermissions(activity, listRequest.toArray(permissions), REQUEST_PERMISSION_CODE);
            } else {
                callback.onGrantPermission(true, listPermissions, new ArrayList<>());
            }
        } else {
            callback.onGrantPermission(true, listPermissions, new ArrayList<>());
        }
    }

    /**
     * Check the list of permissions approved or not
     *
     * @param listPermissions List of permissions to license  {@see AndroidManifest.xml }
     * @return Authorized or not
     */
    public boolean checkPermissionGrant(ArrayList<String> listPermissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : listPermissions) {
                if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Call from Activity#onRequestPermissionsResult
     *
     * @param requestCode  The request code passed in {@link ActivityCompat#requestPermissions(Activity, String[], int)}.
     * @param permissions  The requested permissions.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ArrayList<String> permissionsAllowed = new ArrayList<>();
        ArrayList<String> permissionsDenied = new ArrayList<>();
        if (requestCode == REQUEST_PERMISSION_CODE) {
            //Check if permissions are approved.
            for (int i = 0; i < grantResults.length; i++) {
                final String permissionName = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    permissionsDenied.add(permissionName);
                } else {
                    permissionsAllowed.add(permissionName);
                }
            }
            callback.onGrantPermission(permissionsDenied.size() == 0, permissionsAllowed, permissionsDenied);
        }
    }

    public interface IGrantPermissionResults {
        void onGrantPermission(boolean isGrant,
                               ArrayList<String> permissionsAllowed,
                               ArrayList<String> permissionsDenied);

    }

}