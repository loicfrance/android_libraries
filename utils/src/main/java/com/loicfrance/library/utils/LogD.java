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

import android.content.Context;
import android.support.annotation.IntDef;
import android.util.Log;
import android.widget.Toast;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;


/**
 * Created by Loïc France on 14/03/2015.
 * A class that uses the Log class but before giving it anything, it first check if the app is in debug mode.
 * Don't forget to call <b>{@code LogD.setDebug(BuildConfig.DEBUG)}</b> if you are in debug mode,
 * because if you don't, nothing will be printed in the console.
 */
public final class LogD {
    private static boolean debug = false;
    private static int toastDuration = Toast.LENGTH_SHORT;
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
    public static void toast(Context context, String msg) {
        if(debug) Toast.makeText(context, msg, toastDuration).show();
    }
    public static boolean isDebugging() { return debug; }

    @Retention(SOURCE)
    @IntDef({Toast.LENGTH_LONG, Toast.LENGTH_SHORT})
    private @interface TOAST_LENGTH {}
    public static void setToastDefaultDuration(@TOAST_LENGTH int duration) {
        toastDuration = duration;
    }
}
