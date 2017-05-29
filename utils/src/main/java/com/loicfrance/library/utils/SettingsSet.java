package com.loicfrance.library.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Loic France on 23/09/2015.
 * the pseudo-interface class to use with Settings.
 * Inside, put all of your preference keys as String constants.
 *
 * Example :
 * <pre>{@code

public class MySettingsSet extends SettingsSet {

    public static final String CONDITIONS_ACCEPTED = "conditions_accepted";
    public static final String HAIR_COLOR          = "color";
    public static final String WEIGHT              = "weight";
    public static final String SIZE                = "size";
    public static final String NAME                = "name";
    public static final String AGE                 = "age";

    @Override
    public Settings.TYPE getType(String s) {
        switch(s) {
        case CONDITIONS_ACCEPTED : return Settings.TYPE.BOOLEAN;
        case HAIR_COLOR          : return Settings.TYPE.STRING;
        case WEIGHT              : return Settings.TYPE.FLOAT;
        case SIZE                : return Settings.TYPE.FLOAT;
        case NAME                : return Settings.TYPE.STRING;
        case AGE                 : return Settings.TYPE.INT;
        default : return Settings.TYPE.UNKNOWN;
        }
    }

    @Override
    public Object getDefault(String s) {
        switch(s) {
        case CONDITIONS_ACCEPTED : return false;
        case HAIR_COLOR          : return "black";
        case WEIGHT              : return 60.0;
        case SIZE                : return 185.0;
        case NAME                : return "Luc";
        case AGE                 : return 25;
        default : return null;
        }
    }

    @Override
    public String[] getAllKeysArray() {
        return new String[] {
            CONDITIONS_ACCEPTED,
            HAIR_COLOR,
            WEIGHT,
            SIZE,
            NAME,
            AGE
        };
    }
}
 * }
 * </pre>
 */
public abstract class SettingsSet {
    public abstract Settings.TYPE getType(String pref);
    public abstract Object getDefault(String pref);
    public abstract String[] getAllKeysArray();
    public SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
