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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Loic France on 02/05/2017.
 */

public class Joystick extends View {

    private static final Drawable DEFAULT_THUMB = new ShapeDrawable(new ArcShape(0, 360));
    private static final Drawable DEFAULT_BACKGROUND = new ShapeDrawable(new ArcShape(0,360));

    private Drawable thumb;
    private Drawable background;
    private float scaleX = 10;
    private float scaleY = 10;
    private float thumbX = 0;
    private float thumbY = 0;
    private float minX = -1;
    private float minY = -1;
    private float maxX = 1;
    private float maxY = 1;
    private float maxDist = 1;
    private float minAngle = 0;
    private float maxAngle = (float)(2*Math.PI);

    private JoystickListener listener;

    public Joystick(Context context) {
        this(context, DEFAULT_THUMB, DEFAULT_BACKGROUND);
    }
    public Joystick(Context context, Drawable thumb, Drawable background) {
        super(context);
        this.thumb = thumb;
        this.background = background;
        this.listener = null;
    }
//##################################################################################################
//#                                 properties getters & setters                                   #
//##################################################################################################
    public Drawable getThumb() {
        return thumb;
    }

    public void setThumb(Drawable thumb) {
        this.thumb = thumb;
    }

    public Drawable getBackground() {
        return background;
    }

    public void setBackground(Drawable background) {
        this.background = background;
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public float getMinX() {
        return minX;
    }

    public void setMinX(float minX) {
        this.minX = minX;
    }

    public float getMinY() {
        return minY;
    }

    public void setMinY(float minY) {
        this.minY = minY;
    }

    public float getMaxX() {
        return maxX;
    }

    public void setMaxX(float maxX) {
        this.maxX = maxX;
    }

    public float getMaxY() {
        return maxY;
    }

    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }

    public float getMaxDist() {
        return maxDist;
    }

    public void setMaxDist(float maxDist) {
        this.maxDist = maxDist;
    }

    @FloatRange(from=0, to=2*Math.PI)
    public float getMinAngle() {
        return minAngle;
    }

    public void setMinAngle(@FloatRange(from=0, to=2*Math.PI) float minAngle) {
        this.minAngle = minAngle % (float)(2*Math.PI);
    }

    @FloatRange(from=0, to=2*Math.PI)
    public float getMaxAngle() {
        return maxAngle;
    }

    public void setMaxAngle(@FloatRange(from=0, to=2*Math.PI) float maxAngle) {
        this.maxAngle = maxAngle;
    }

    public JoystickListener getJoystickListener() {
        return listener;
    }

    public void setJoystickListener(@Nullable JoystickListener listener) {
        this.listener = listener;
    }
    public float getThumbX() {
        return thumbX;
    }
    public float getThumbY() {
        return thumbY;
    }
//##################################################################################################
//#                                  high-level getters & setters                                  #
//##################################################################################################
    public float getThumbAngle() {
        return (float) Math.atan2(thumbY, thumbX);
    }
    public void setThumbAngle(float radians) {
        float d = this.getThumbDistance();
        setThumbXY((float)(d*Math.cos(radians)), (float)(d*Math.sin(radians)));
    }
    public float getThumbDistance() {
        return (float) Math.sqrt(thumbX*thumbX + thumbY*thumbY);
    }
    public void setThumbDistance(float distance) {
        float angle = getThumbAngle();
        setThumbXY((float)(distance*Math.cos(angle)), (float)(distance*Math.sin(angle)));
    }
    public void setThumbX(float x) {
        this.setThumbXY(x, this.thumbY);
    }
    public void setThumbY(float y) {
        this.setThumbXY(this.thumbX, y);
    }
    public void setThumbXY(float x, float y) {
        //clamp x and y in limits
        if(x < minX) x = minX;
        else if(x > maxX) x = maxX;
        if(y < minY) y = minY;
        else if(y > maxY) y = maxY;
        double d = Math.sqrt((x*x+y*y));
        //clamp distance below limit
        if(d > maxDist) {
            double factor = maxDist/d;
            x *= factor;
            y *= factor;
        }
        //clamp angle in limit
        float angle = (float) (Math.atan2(y, x) % 2*Math.PI);
        if(angle < minAngle || angle > maxAngle) {
            if(Math.abs(angle - minAngle) < Math.abs(angle - maxAngle)) {
                angle = minAngle;
            } else {
                angle = maxAngle;
            }
            x = (int) (d*Math.cos(angle));
            y = (int) (d*Math.sin(angle));
        }
        //if a modification occurred, update
        if(thumbX != x || thumbY != y) {
            //modify coordinates
            thumbX = x;
            thumbY = y;
            //refresh view
            Rect rect = thumb.copyBounds();
            float centerX = (rect.left + rect.right)/2f;
            float centerY = (rect.top + rect.bottom)/2f;
            //calculate refresh rectangle
            Rect rect2 = new Rect(rect);
            rect.inset((int)(x - centerX), (int)(y - centerY));
            rect2.union(rect);
            thumb.setBounds(rect);
            invalidate(rect2);
            //notify listener
            if(listener != null) listener.onMove(this, x, y);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_MOVE :
                setThumbXY(event.getX(), event.getY());
                return true;
            case MotionEvent.ACTION_DOWN :
            case MotionEvent.ACTION_UP :
                if(listener != null)
                    listener.onPressChange(this, event);
                return true;
            default : break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        background.draw(canvas);
        thumb.draw(canvas);
    }
}
