/*
 * Copyright 2018 RichardFrance
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
     */
    void onMove(Joystick joy, float x, float y);

    /**
     * Called when a {@link android.view.MotionEvent#ACTION_DOWN} or
     * {@link android.view.MotionEvent#ACTION_UP} event happens.
     * @param joy {@link Joystick} instance that received the event.
     * @param evt associated event
     */
    void onPressChange(Joystick joy, MotionEvent evt);
}
