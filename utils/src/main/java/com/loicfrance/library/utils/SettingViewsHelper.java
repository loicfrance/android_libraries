package com.loicfrance.library.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;

import java.util.Locale;

/**
 * Created by Loic France on 07/04/2018.
 */
public class SettingViewsHelper {

    public static void addCompoundButtonPref(CompoundButton cb, final String preference) {
        cb.setChecked(Settings.getBool(preference));
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Settings.set(preference, isChecked);
            }
        });
    }

    public static void addSeekbarPref(SeekBar sb, final String preference, final boolean changeOnRelease) {
        sb.setProgress(Settings.getInt(preference));
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                if (!changeOnRelease) Settings.set(preference, progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) {
                LogD.d("Settings Activity", "stop moving seekbar");
                if (changeOnRelease) Settings.set(preference, seekBar.getProgress());
            }
        });
    }

    public static void addEditTextPref(EditText et, final String preference, final Settings.TYPE prefType) {
        switch (prefType) {
            case FLOAT  : et.setText (String.format(Locale.getDefault(), "%f", Settings.getFloat  (preference))); break;
            case INT    : et.setText (String.format(Locale.getDefault(), "%d", Settings.getInt    (preference))); break;
            case LONG   : et.setText (String.format(Locale.getDefault(), "%d", Settings.getLong   (preference))); break;
            case STRING : et.setText (Settings.getString (preference)) ; break;
        }
        et.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence cs, int s, int c, int a) { }
            @Override public void onTextChanged    (CharSequence cs, int s, int b, int c) { }
            @Override public void afterTextChanged(Editable s) {
                try {
                    Object value = null;
                    switch (prefType) {
                        case FLOAT  : value = Float.parseFloat(s.toString()); break;
                        case INT    : value = Integer.parseInt(s.toString()); break;
                        case STRING : value = s.toString(); break;
                        case LONG   : value = Long.parseLong(s.toString()); break;
                    }
                    if(value != null) Settings.set(preference, value);
                } catch (NumberFormatException ignored) { }
            }
        });
    }
}
