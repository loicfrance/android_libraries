package com.loicfrance.library.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

/**
 * Created by Loic France on 23/09/2015.
 * <br/><br/>
 * An abstract class needed to use the {@link Settings} class
 * Inside, put all of your preference keys as String constants, and fill the methods to return
 * the needed information for each preference : its type (
 * {@link Settings.TYPE#BOOLEAN}, {@link Settings.TYPE#INT}, {@link Settings.TYPE#FLOAT},
 * {@link Settings.TYPE#LONG}, {@link Settings.TYPE#STRING}), and its default value.
 *<br/><br/>
 * You also have to provide an array containing all the preferences in a String array.
 *<br/><br/>
 * It is possible to override the method : {@link #getPreferences(Context)} which returns
 * the default shared preferences, if you want to provide other preferences.
 *<br/><br/>
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
    /**
     * @param pref the preference the type is needed for
     * @return the type of the specified preference, one of the {@link Settings.TYPE} enum constants.
     */
    public abstract @NonNull Settings.TYPE getType(@NonNull String pref);

    /**
     * @param pref the prefrence the default value is needed for
     * @return the default value for the specified preference
     */
    public abstract Object getDefault(@NonNull String pref);

    /**
     * @return an array containing all the preferences names
     */
    public abstract @NonNull String[] getAllKeysArray();

    /**
     * @param context the context of the application
     * @return the {@link SharedPreferences} instance to be used by the application.
     */
    public SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
