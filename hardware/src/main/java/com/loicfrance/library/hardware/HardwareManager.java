package com.loicfrance.library.hardware;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.RequiresPermission;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.loicfrance.library.utils.LogD;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;


/**
 * Created by Loic France on 15/07/2015.
 */
@SuppressWarnings("WeakerAccess") //remove "access can be private" warning
public class HardwareManager {
    public enum SOUND_MODE {
        SILENT(0), VIBRATE(1), NORMAL(2);
        private int n;
        SOUND_MODE(int n) { this.n = n; }
    }

    private static boolean lightState;
    private static CameraDevice camDevice;
    private static Camera camera;
    private static Camera.Parameters params;
    private static KeyguardManager kgMng;

    public static void turnLight(boolean state) {
        if (state != lightState) {
            lightState = state;
            if (camera == null) camera = Camera.open();
            params = camera.getParameters();
            params.setFlashMode(state ? Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            if (state) camera.startPreview();
            else {
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        }
    }

    public static boolean getLightState() {
        return lightState;
    }

    public static void launchCamera(Context context) {
        Intent intent;
        if (kgMng == null) kgMng = (KeyguardManager)
                context.getSystemService(Context.KEYGUARD_SERVICE);
        if (kgMng.inKeyguardRestrictedInputMode() &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
            LogD.d("HardwareManager", "device locked, launching locked mode camera");
        } else {
            intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            LogD.d("HardwareManager", "device unlocked, launching normal mode camera");
        }
        turnLight(false);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
    public static boolean hasFeature(Context context, String feature) {
        return context.getPackageManager().hasSystemFeature(feature);
    }
    public static boolean isLocked(Context context) {
        return ((KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE))
                .inKeyguardRestrictedInputMode();
    }

    public static SOUND_MODE getSoundMode(Context context) {
        switch (((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT : return SOUND_MODE.SILENT;
            case AudioManager.RINGER_MODE_VIBRATE : return SOUND_MODE.VIBRATE;
            case AudioManager.RINGER_MODE_NORMAL:default:return SOUND_MODE.NORMAL;
        }
    }

    public static void setSoundMode(Context context, SOUND_MODE soundMode) {
        ((AudioManager) context.getSystemService(Context.AUDIO_SERVICE)).setRingerMode(
                soundMode==SOUND_MODE.SILENT  ? AudioManager.RINGER_MODE_SILENT :
                soundMode==SOUND_MODE.VIBRATE ? AudioManager.RINGER_MODE_VIBRATE:
                                                AudioManager.RINGER_MODE_NORMAL);
    }

    /**
     * Overall orientation of the screen.  May be one of
     * {@link Configuration#ORIENTATION_LANDSCAPE}, {@link Configuration#ORIENTATION_PORTRAIT}.
     */
    public static int getDeviceOrientation(Context context) {
        return context.getResources().getConfiguration().orientation;
    }
    public static Point getScreenDimensions(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(size);
        }
        else {
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            size.x = metrics.widthPixels;
            size.y = metrics.heightPixels;
        }
        return size;
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static Point getScreenUsableDimensions(Context context) {
        Configuration config = context.getResources().getConfiguration();
        return new Point(config.screenWidthDp, config.screenHeightDp);
    }

    /**
     * sets the users pref screen brightness.
     * needs permission {@code android.permission.WRITE_SETTINGS}.
     *
     * @param b brightness within [0, 255]
     */
    public static void setScreenBrightness(Context context, int b) {
        Settings.System.putInt(context.getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, b);
    }

    public static void setWindowBrightness(Activity activity, int b) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.screenBrightness = b / 255f;
        activity.getWindow().setAttributes(lp);
    }

    public static int getWindowBrightness(Activity activity) {
        return (int) (activity.getWindow().getAttributes().screenBrightness * 255);
    }

    public static int getScreenBrightNess(Context context) {
        try {
            return Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }
    @RequiresPermission(Manifest.permission.VIBRATE)
    public static void vibrate(Context context, long millis) {
        Vibrator v = getVibrator(context);
        if (v != null) v.vibrate(millis);
    }
    public static Vibrator getVibrator(Context context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        return v!= null && Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB && v.hasVibrator()?
                v : null;
    }

    public static boolean canVibrate(Context context) { return getVibrator(context) != null; }

    public static int getAPIVersionCode() {
        return Build.VERSION.SDK_INT;
    }

    public static boolean checkAPIVersion(int minAPI) {
        return getAPIVersionCode() > minAPI;
    }

    /**
     *
     * @param networkId one of
     *                  {@link ConnectivityManager#TYPE_MOBILE},
     *                  {@link ConnectivityManager#TYPE_WIFI},
     *                  {@link ConnectivityManager#TYPE_BLUETOOTH},
     *                  ...
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    public static boolean canUseNetwork(Context context, int networkId) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm != null && cm.getNetworkInfo(networkId) != null;
    }
    public static String getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ignored) {
        }
        return "02:00:00:00:00:00";
    }

    public enum Condition {

        HARD_SIM("hard:sim_card"),

        HARD_VIBRATE("hard:vibrate"),

        HARD_CAMERA("hard:camera"),

        HARD_FLASHLIGHT("hard:flashLight"),

        SOFT_PLANE("soft:plane"),

        SOFT_PORT_LAND_SWITCH("soft:orient_switch");

        String name;
        Condition(String name) {
            this.name = name;
        }
        public static Condition get(String name) {
            for(Condition cond : values())
                if(cond.name.equals(name)) return cond;
            return null;
        }
        public static boolean verify(Context context, @RequiresPermission Condition condition) {
            if(condition == null) return true;
            switch (condition) {
                case HARD_SIM:
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);  //gets the current TelephonyManager
                    return !(tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT);
                case HARD_VIBRATE:
                    return canVibrate(context);
                case HARD_CAMERA:
                    return hasFeature(context, PackageManager.FEATURE_CAMERA)
                        || hasFeature(context, PackageManager.FEATURE_CAMERA_FRONT);
                case HARD_FLASHLIGHT:
                    return  hasFeature(context, PackageManager.FEATURE_CAMERA_FLASH);
                case SOFT_PLANE:
                    return !checkAPIVersion(Build.VERSION_CODES.JELLY_BEAN_MR1);
                case SOFT_PORT_LAND_SWITCH:
                    return checkAPIVersion(Build.VERSION_CODES.HONEYCOMB_MR2) &&
                            hasFeature(context, PackageManager.FEATURE_SCREEN_LANDSCAPE) &&
                            hasFeature(context, PackageManager.FEATURE_SCREEN_PORTRAIT);
            }
            return true;
        }
    }
}
