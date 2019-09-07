/*
 * Copyright 2018 Loic France
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom
 * the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.loicfrance.library.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Loic France on 01/10/2016.
 *
 * simple class with static methods to easily request permissions
 */

@SuppressWarnings("WeakerAccess") //remove "access can be private" warning
public final class PermissionsManager {
    /**
     * @param context the active context of the application
     * @param permission the permission you want to know if it has been granted or not
     * @return true if the permission has been granted, false otherwise
     */
    public static boolean isPermissionGranted(Context context, @NonNull String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * @param context the active context of the application
     * @param permissions the permissions to test, or null if all the permissions needed by the
     *                    applications have to be tested
     * @return an array containing the permissions from the second argument that are already granted
     */
    public static String[] getGrantedPermissions(Context context, @Nullable String[] permissions) {
        if(permissions == null) permissions = getRequiredPermissions(context);
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
     * @param context the active context of the application
     * @param permissions the permissions to test, or null if all the permissions needed by the
     *                    applications have to be tested
     * @return an array containing the permissions from the second argument that are not yet granted
     */
    public static String[] getNotGrantedPermissions(Context context, @Nullable String[] permissions) {
        if(permissions == null) permissions = getRequiredPermissions(context);
        List<String> granted = new ArrayList<>(permissions.length);
        for (String permission : permissions) {
            if (!isPermissionGranted(context, permission)) {
                granted.add(permission);
            }
        }
        String[] result = new String[granted.size()];
        granted.toArray(result);
        return result;
    }

    /**
     * @param context the active context of the application
     * @return an array containing all the permissions required by the application.
     */
    public static String[] getRequiredPermissions(Context context) {
        List<String> permissions = new ArrayList<>();
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            Collections.addAll(permissions, pi.requestedPermissions);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String[] result = new String[permissions.size()];
        permissions.toArray(result);
        return result;
    }

    /**
     * requests the specified permissions if needed. The
     * {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback#onRequestPermissionsResult(int, String[], int[])
     * onRequestPermissionsResult(int, String[], int[])}
     * function is called by the system once the user has made his choice
     * @param context the active permission of the application
     * @param permissions the permissions you need, or null for all the permissions needed
     *                   by the application
     * @param requestId the id that will be used by the system when calling the
     *                   {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback#onRequestPermissionsResult(int, String[], int[])
     *                   onRequestPermissionsResult(int, String[], int[])}
     *                   function
     * @return {@code true} if at least one permission
     */
    @TargetApi(23)
    public static boolean requestPermissions(Activity context, @Nullable String[] permissions, int requestId) {
        String[] array = getNotGrantedPermissions(context, permissions);

        if(array.length == 0) return false;

        ActivityCompat.requestPermissions(context, array, requestId);
        return true;
    }
}
