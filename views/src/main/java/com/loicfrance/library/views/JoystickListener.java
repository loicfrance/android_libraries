package com.loicfrance.library.views;

import android.view.MotionEvent;

import java.util.EventListener;

/**
 * Created by Loic France on 02/05/2017.
 */

public interface JoystickListener extends EventListener {
    /**
     * Called when the thumb position on the joystick has changed.
     * @param joy {@link Joystick} instance that triggered the event.
     * @param x x position of the thumb, divided by the x scale, relative to the center of the joystick.
     * @param y y position of the thumb, divided by the y scale, relative to the center of the joystick.
     * @return true if the position should be automatically modified.
     *         If you want to modify it by yourself, return false
     */
    boolean onMove(Joystick joy, float x, float y);

    /**
     * Called when a {@link android.view.MotionEvent#ACTION_DOWN} or
     * {@link android.view.MotionEvent#ACTION_UP} event happens.
     * @param joy {@link Joystick} instance that received the event.
     * @param evt associated event
     */
    void onPressChange(Joystick joy, MotionEvent evt);
}
