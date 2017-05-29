package com.loicfrance.library.utils;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by Loïc France on 14/03/2015.
 * A class that uses the Log class but before giving it anything, it first check if the app is in debug mode.
 * Don't forget to call <b>{@code LogD.setDebug(BuildConfig.DEBUG)}</b> if you are in debug mode,
 * because if you don't, nothing will be printed in the console.
 */
public final class LogD {
    private static boolean debug = false;
    /**
     * a call to this function is only necessary in debug mode (default: non debug).
     * @param debug should be equal to {@code BuildConfig.DEBUG}.
     *
     */
    public static void setDebug(boolean debug) { LogD.debug = debug; }
    public static void d(String tag, String msg) { if(debug) Log.d(tag, msg); }
    public static void e(String tag, String msg) { if(debug) Log.e(tag, msg); }
    public static void i(String tag, String msg) { if(debug) Log.i(tag, msg); }
    public static void v(String tag, String msg) { if(debug) Log.v(tag, msg); }
    public static void w(String tag, String msg) { if(debug) Log.w(tag, msg); }
    public static void w(String tag, String msg, Throwable Tr) { if(debug) Log.w(tag, msg, Tr); }
    public static void w(String tag, Throwable Tr) { if(debug) Log.w(tag, Tr); }
    public static void toast(Context context, String msg, boolean duration_long) {
        if(debug) Toast.makeText(context, msg, (duration_long? Toast.LENGTH_LONG : Toast.LENGTH_SHORT)).show();
    }
    public static boolean isDebugging() { return debug; }
}