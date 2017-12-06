package com.loicfrance.library.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Loic France on 01/10/2016.
 *
 * simple class with static methods to easily request permissions
 */

@SuppressWarnings("WeakerAccess") //remove "access can be private" warning
public final class PermissionsManager {
    /**
     *
     * @param context the active context of the application
     * @param permission the permission you want to know if it has been granted or not
     * @return true if the permission has been granted, false otherwise
     */
    public static boolean isPermissionGranted(Context context, @NonNull String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static String[] getGrantedPermissions(Context context, @NonNull String[] permissions) {
        List<String> granted = new ArrayList<>(permissions.length);
        for (String permission : permissions) {
            if (isPermissionGranted(context, permission)) {
                granted.add(permission);
            }
        }
        String[] result = new String[granted.size()];
        granted.toArray(result);
        return result;
    }

    /**
     * requests the specified permissions. The
     * {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback#onRequestPermissionsResult(int, String[], int[])
     * onRequestPermissionsResult(int, String[], int[])}
     * function is called by the system once the user has made his choice
     * @param context the active permission of the application
     * @param permissions the permissions you need
     * @param requestId the id that will be used by the system when calling the
     *                   {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback#onRequestPermissionsResult(int, String[], int[])
     *                   onRequestPermissionsResult(int, String[], int[])}
     *                   function
     */
    @TargetApi(23)
    public static void requestPermissions(Activity context, @NonNull String[] permissions, int requestId) {
        List<String> perm = new ArrayList<>();
        for(String p : permissions) {
            if(!isPermissionGranted(context, p)) {
                perm.add(p);
            }
        }
        if(perm.isEmpty()) {
            return;
        }
        String[] array = new String[perm.size()];
        perm.toArray(array);
        ActivityCompat.requestPermissions(context, array, requestId);
    }
    /**
     * requests all permissions needed in your applications at once if needed, by calling
     * the function  {@link #requestPermissions(Activity, String[], int)} with all
     * not granted permissions requested by your application.
     * @param activity the active permission of the application
     * @param requestId the id that will be used by the system when calling the
     *                   {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback#onRequestPermissionsResult(int, String[], int[])
     *                   onRequestPermissionsResult(int, String[], int[])}
     *                   function
     */
    @TargetApi(23)
    public static void requestAllPermissions(Activity activity, int requestId) {
        List<String> permissions = new ArrayList<>();
        try {
            PackageInfo pi = activity.getPackageManager().getPackageInfo(activity.getPackageName(), PackageManager.GET_PERMISSIONS);
            for(int i=0; i<pi.requestedPermissions.length; i++) {
                if(ContextCompat.checkSelfPermission(activity, pi.requestedPermissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    permissions.add(pi.requestedPermissions[i]);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(!permissions.isEmpty()) {
            requestPermissions(activity, permissions.toArray(new String[permissions.size()]), requestId);
        }
    }
}
