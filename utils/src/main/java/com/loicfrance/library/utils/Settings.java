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
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

/**
 * a useful static class with methods and listeners on Preferences, to use with an instance of
 * {@link SettingsSet} via {@link #setSettingsSet(SettingsSet)}.
 */
public class Settings {
//##################################################################################################
//#                                    private static variables                                    #
//##################################################################################################
    private static SharedPreferences prefs;
    private static SettingsSet sSet;
    private static List<OnSettingChangeListener> listeners = new ArrayList<>();
    private static List<String[]> listenersPreferences = new ArrayList<>();
    private static SharedPreferences.Editor editor;

    private Settings() {
        throw new RuntimeException(this.getClass().getName() + " class cannot be instantiated");
    }

//##################################################################################################
//#                                    private static functions                                    #
//##################################################################################################

    private static void beforeChange(String preference, Object oldValue) {
        for (int i = 0; i < listeners.size(); i++) {
            String[] prefs = listenersPreferences.get(i);
            int j = 0;
            boolean sent = false;
            while (!sent && j < prefs.length) {
                if (prefs[j].equals(preference)) {
                    listeners.get(i).beforeSettingChange(preference, oldValue);
                    sent = true;
                }
                j++;
            }
        }
    }

    private static void afterChange(String preference, Object newValue) {
        for(int i=0; i< listeners.size(); i++) {
            String[] prefs = listenersPreferences.get(i);
            int j=0;
            boolean sent = false;
            while(!sent && j< prefs.length) {
                if(prefs[j].equals(preference)) {
                    listeners.get(i).afterSettingChange(preference, newValue);
                    sent = true;
                }
                j++;
            }
        }
    }
//##################################################################################################
//#                                    public static functions                                     #
//##################################################################################################

//_______________________________________listeners functions________________________________________
//**************************************************************************************************
    public static void addOnSettingChangeListener(String[] preferences,
                                                  OnSettingChangeListener listener) {
        listeners.add(listener);
        listenersPreferences.add(preferences);
    }

    public static void addOnSettingChangeListener(String preference,
                                                  OnSettingChangeListener listener) {
        addOnSettingChangeListener(new String[]{preference}, listener);
    }

    public static void removeOnSettingChangeListener(OnSettingChangeListener listener) {
        int i = listeners.indexOf(listener);
        if(i >= 0) {
            listeners.remove(i);
            listenersPreferences.remove(i);
        }
    }

//________________________________________preference setter_________________________________________
//**************************************************************************************************
    public static void set(String key, Object value) {
        Object oldValue = get(key);
        LogD.d("SETTINGS", key + " : " + oldValue + " -> " + value);
        beforeChange(key, oldValue);
        editor = prefs.edit();
        switch (sSet.getType(key)) {
            case BOOLEAN:
                editor.putBoolean(key, (boolean) value);
                break;
            case FLOAT:
                editor.putFloat(key, (float) value);
                break;
            case INT:
                editor.putInt(key, (int) value);
                break;
            case LONG:
                editor.putLong(key, (long) value);
                break;
            case STRING:
                editor.putString(key, (String) value);
                break;
            default:
                LogD.e("SETTINGS", "preference " + key + "does not have a good type" +
                        "or is not registered in the settings set");
                break;
        }
        editor.apply();
        afterChange(key, value);
    }

//________________________________________preference getters________________________________________
//**************************************************************************************************

    /**
     * returns the saved preference value for the given key, or the default value
     * if the preference does not exists.<br/>
     * Do not forget to call the {@link #init(Context, SettingsSet)} method to initialize the
     * {@link Settings} class.
     * @param key preference key
     * @param def default value
     * @return preference value for key, or default value if unknown
     */
    public static String getString(String key, String def) {
        if (prefs == null) return def;
        else return prefs.getString(key, def);
    }
    
    /**
     * returns the saved preference value for the given key, or the default value
     * if the preference does not exists.<br/>
     * The default value is defined in the
     * {@link SettingsSet} instance specified in the {@link #init(Context, SettingsSet)}
     * function parameters, or via the {@link #setSettingsSet(SettingsSet)} function.<br/>
     * Do not forget to call the {@link #init(Context, SettingsSet)} method to initialize the
     * {@link Settings} class.
     * @param key preference key
     * @return preference value for key, or default value if unknown
     */
    public static String getString(String key) {
        return getString(key, (String) sSet.getDefault(key));
    }

    /**
     * returns the saved preference value for the given key, or the default value
     * if the preference does not exists.<br/>
     * Do not forget to call the {@link #init(Context, SettingsSet)} method to initialize the
     * {@link Settings} class.
     * @param key preference key
     * @param def default value
     * @return preference value for key, or default value if unknown
     */
    public static int getInt(String key, int def) {
        if (prefs == null) return def;
        else return prefs.getInt(key, def);
    }

    /**
     * returns the saved preference value for the given key, or the default value
     * if the preference does not exists.<br/>
     * The default value is defined in the
     * {@link SettingsSet} instance specified in the {@link #init(Context, SettingsSet)}
     * function parameters, or via the {@link #setSettingsSet(SettingsSet)} function.<br/>
     * Do not forget to call the {@link #init(Context, SettingsSet)} method to initialize the
     * {@link Settings} class.
     * @param key preference key
     * @return preference value for key, or default value if unknown
     */
    public static int getInt(String key) {
        return getInt(key, (int) sSet.getDefault(key));
    }

    /**
     * returns the saved preference value for the given key, or the default value
     * if the preference does not exists.<br/>
     * Do not forget to call the {@link #init(Context, SettingsSet)} method to initialize the
     * {@link Settings} class.
     * @param key preference key
     * @param def default value
     * @return preference value for key, or default value if unknown
     */
    public static float getFloat(String key, float def) {
        if(prefs == null) return def;
        else return prefs.getFloat(key, def);
    }

    /**
     * returns the saved preference value for the given key, or the default value
     * if the preference does not exists.<br/>
     * The default value is defined in the
     * {@link SettingsSet} instance specified in the {@link #init(Context, SettingsSet)}
     * function parameters, or via the {@link #setSettingsSet(SettingsSet)} function.<br/>
     * Do not forget to call the {@link #init(Context, SettingsSet)} method to initialize the
     * {@link Settings} class.
     * @param key preference key
     * @return preference value for key, or default value if unknown
     */
    public static float getFloat(String key) {
        return getFloat(key, (float) sSet.getDefault(key));
    }

    /**
     * returns the saved preference value for the given key, or the default value
     * if the preference does not exists.<br/>
     * Do not forget to call the {@link #init(Context, SettingsSet)} method to initialize the
     * {@link Settings} class.
     * @param key preference key
     * @param def default value
     * @return preference value for key, or default value if unknown
     */
    public static long getLong(String key, long def) {
        if(prefs == null) return def;
        else return prefs.getLong(key, def);
    }

    /**
     * returns the saved preference value for the given key, or the default value
     * if the preference does not exists.<br/>
     * The default value is defined in the
     * {@link SettingsSet} instance specified in the {@link #init(Context, SettingsSet)}
     * function parameters, or via the {@link #setSettingsSet(SettingsSet)} function.<br/>
     * Do not forget to call the {@link #init(Context, SettingsSet)} method to initialize the
     * {@link Settings} class.
     * @param key preference key
     * @return preference value for key, or default value if unknown
     */
    public static long getLong(String key) {
        return getLong(key, (long) sSet.getDefault(key));
    }

    /**
     * returns the saved preference value for the given key, or the default value
     * if the preference does not exists.<br/>
     * Do not forget to call the {@link #init(Context, SettingsSet)} method to initialize the
     * {@link Settings} class.
     * @param key preference key
     * @param def default value
     * @return preference value for key, or default value if unknown
     */
    public static boolean getBool(String key, boolean def) {
        if(prefs == null) return def;
        else return prefs.getBoolean(key, def);
    }

    /**
     * returns the saved preference value for the given key, or the default value
     * if the preference does not exists.<br/>
     * The default value is defined in the
     * {@link SettingsSet} instance specified in the {@link #init(Context, SettingsSet)}
     * function parameters, or via the {@link #setSettingsSet(SettingsSet)} function.<br/>
     * Do not forget to call the {@link #init(Context, SettingsSet)} method to initialize the
     * {@link Settings} class.
     * @param key preference key
     * @return preference value for key, or default value if unknown
     */
    public static boolean getBool(String key) {
        return getBool(key, (boolean) sSet.getDefault(key));
    }
    
    /**
     * returns the saved preference value for the given key, or the default value
     * if the preference does not exists.<br/>
     * Do not forget to call the {@link #init(Context, SettingsSet)} method to initialize the
     * {@link Settings} class.
     * @param key preference key
     * @param def default value
     * @return preference value for key, or default value if unknown
     */
    public static Object get(String key, Object def) {
        if (prefs == null) return def;
        switch (sSet.getType(key)) {
            case BOOLEAN : return prefs.getBoolean(key, (boolean) def);
            case FLOAT   : return prefs.getFloat  (key, (float)   def);
            case INT     : return prefs.getInt    (key, (int)     def);
            case LONG    : return prefs.getLong   (key, (long)    def);
            case STRING  : return prefs.getString (key, (String)  def);
            default:
                LogD.e("SETTINGS", "preference " + key + "does not have a good type");
        }
        return null;
    }

    /**
     * returns the saved preference value for the given key, or the default value
     * if the preference does not exists.<br/>
     * The default value is defined in the
     * {@link SettingsSet} instance specified in the {@link #init(Context, SettingsSet)}
     * function parameters, or via the {@link #setSettingsSet(SettingsSet)} function.<br/>
     * Do not forget to call the {@link #init(Context, SettingsSet)} method to initialize the
     * {@link Settings} class.
     * @param key preference key
     * @return preference value for key, or default value if unknown
     */
    public static Object get(String key) {
        return get(key, sSet.getDefault(key));
    }

    /**
     * sets the specified preference to its default value from the associated
     * {@link SettingsSet} instance.
     * @param key prefernce key
     */
    public static void reset(String key) {
        set(key, sSet.getDefault(key));
    }

    /**
     * sets all preferences to their default values from the associated {@link SettingsSet}
     * instance.
     */
    public static void resetAll() {
        for (String pref : sSet.getAllKeysArray()) {
            set(pref, sSet.getDefault(pref));
        }
    }

//__________________________________________Init functions__________________________________________
//**************************************************************************************************

    /**
     * Initializes the {@link Settings} class by linking it to the preferences and the specified
     * {@link SettingsSet} instance, containing all the preference keys informations.
     * @param context the {@link Context} instance used to get the preferences.
     * @param sSet the {@link SettingsSet} instance containing all the preference keys.
     * @return true is everything is ready to be used, false if an error occured somewhere
     */
    public static boolean init(@NonNull Context context, @NonNull SettingsSet sSet) {
        setSettingsSet(sSet);
        createPref(context);
        return isPrefsCreated();
    }

    /**
     * Returns the {@link SettingsSet} instance associated to the {@link Settings} class. <br/>
     * Remember that the {@link Settings} class will not work completely unless you specify it a
     * {@link SettingsSet} instance to work with. You can specify one using the
     * {@link #setSettingsSet(SettingsSet)} function, or using the
     * {@link #init(Context, SettingsSet)} function to do it in the same time as linking with the
     * preferences.
     * @return the {@link SettingsSet} instance associated to the {@link Settings} class,
     * or null if not set.
     * @see #setSettingsSet(SettingsSet)
     * @see #init(Context, SettingsSet)
     */
    public static SettingsSet getSettingsSet() {
        return sSet;
    }

    /**
     * Sets the {@link SettingsSet} instance for the {@link Settings} class to work with.<br/>
     * This function must be called before the {@link #createPref(Context)} function.<br/>
     * Those two function are automatically called in the {@link #init(Context, SettingsSet)}
     * function.<br/>
     * You can use the {@link #init(Context, SettingsSet)} function to initialize it in the same
     * time as linking with the preferences.
     * @param sSet the object the class will use to get all preferences keys, types
     *             and default values.
     */
    public static void setSettingsSet(@NonNull SettingsSet sSet) {
        Settings.sSet = sSet;
    }

    /**
     * Links the {@link Settings} class with the preferences.<br/> This function must be called after
     * a {@link SettingsSet} instance has been linked to the {@link Settings} class.<br/>
     * This function is automatically called in the {@link #init(Context, SettingsSet)} function.
     * @param context the {@link Context} class to take the preferences from.
     */
    public static void createPref(Context context) {
        if (sSet != null) prefs = sSet.getPreferences(context);
    }

    /**
     * Checks ig the Settings class as well been initialized.
     * @return true if the Settings are ready to be used, false otherwise.
     */
    public static boolean isPrefsCreated() {
        return sSet != null && prefs != null;
    }

    /**
     * prints the values of all preferences in the log.<br/>
     * The used format is the following :<br/>
     * {@code *key* = *value*}
     */
    public static void printAll() {
        for(String key : sSet.getAllKeysArray()) {
            LogD.d("SETTINGS", key + " = " + get(key));
        }
    }

    /**
     * An enumeration to define the different types of data that can be used in preference values.
     */
    public enum TYPE {
        UNKNOWN, BOOLEAN, INT, LONG, FLOAT, STRING
    }

    /**
     * A Listener interface used to be notified when a preference has changes.
     */
    public interface OnSettingChangeListener extends EventListener {
        /**
         * called just before a preference is changed using the {@link Settings} class. <br/>
         * Only the old value is given in argument, the new value will be sent using the
         * {@link #afterSettingChange(String, Object)} function.
         * @param preference the key of the preference changed.
         * @param oldValue the old value of the preference, before it changes.
         */
        void beforeSettingChange(String preference, Object oldValue);

        /**
         * called just after a preference has changed using the {@link Settings} class. <br/>
         * Only the new value is given in argument, the old value ihas been sent using the
         * {@link #beforeSettingChange(String, Object)} function.
         * @param preference the key of the preference changed.
         * @param newValue the new value of the preference, after it has changed.
         */
        void afterSettingChange(String preference, Object newValue);
    }

}
