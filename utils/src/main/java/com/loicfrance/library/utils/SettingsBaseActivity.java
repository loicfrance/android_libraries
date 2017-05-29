package com.loicfrance.library.utils;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;

public class SettingsBaseActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Settings.createPref(this);
    }

    protected void addCompoundButtonPref(CompoundButton cb, final String preference) {
        cb.setChecked(Settings.getBool(preference));
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Settings.set(preference, isChecked);
            }
        });
    }

    protected void addSeekbarPref(SeekBar sb, final String preference, final boolean changeOnRelease) {
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

    protected void addEditTextPref(EditText et, final String preference, final Settings.TYPE prefType) {
        switch (prefType) {
            case FLOAT  : et.setText (Float.toString   (Settings.getFloat  (preference))); break;
            case INT    : et.setText (Integer.toString (Settings.getInt    (preference))); break;
            case LONG   : et.setText (Long.toString    (Settings.getLong   (preference))); break;
            case STRING : et.setText (                  Settings.getString (preference)) ; break;
        }
        et.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence cs, int s, int c, int a) { }
            @Override public void onTextChanged    (CharSequence cs, int s, int b, int c) { }
            @Override public void afterTextChanged(Editable s) {
                switch (prefType) {
                    case FLOAT  : Settings.set(preference, Float.parseFloat(s.toString())); break;
                    case INT    : Settings.set(preference, Integer.parseInt(s.toString())); break;
                    case STRING : Settings.set(preference, s.toString()); break;
                    case LONG   : Settings.set(preference, Long.parseLong(s.toString())); break;
                }
            }
        });
    }
}