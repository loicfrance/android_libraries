package com.loicfrance.library.utils;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import static android.support.v4.graphics.ColorUtils.calculateContrast;

/**
 * Created by Loic France on 28/11/2015.
 * Contains some functions about colors
 */
public final class ColorTools {

    public static int textColor(int backgroundColor) {
        return calculateContrast(Color.BLACK, backgroundColor)
                > calculateContrast(Color.WHITE, backgroundColor) ?
                Color.BLACK :
                Color.WHITE;
    }
    public static int getColor(Context context, int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getResources().getColor(resId, null);
        } else return context.getResources().getColor(resId);
    }
}
