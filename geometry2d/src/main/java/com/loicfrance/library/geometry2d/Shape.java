package com.loicfrance.library.geometry2d;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by rfrance on 21/11/2015.
 */
public abstract class Shape {

    protected Vec2 center;
//__________________________________________________________________________________________________Constructors
//-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-# 3
    protected Shape(Vec2 center) {
        this.center = new Vec2(center);
    }
    protected Shape(float x, float y) {
        this.center = new Vec2(x,y);
    }
    protected Shape() {
        this.center = new Vec2();
    }
//__________________________________________________________________________________________________functions
//-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-# 2

//__ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __move
    public void move(Vec2 delta) {
        center.add(delta);
    }
    public final void move(float x, float y) {
        center.add(x, y);
    }
//__ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __ __moveTo
    public void moveTo(Vec2 newCenter) {
        center.set(newCenter);
    }
    public Vec2 getCenter() {
        return new Vec2(center);
    }

    public void rotateDegrees(float degrees) {
        rotateRadians((float) Math.toRadians(degrees));
    }

//__________________________________________________________________________________________________abstract functions
//-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-# 6
    public abstract void rotateRadians(float radians);
    public abstract void grow(float factor);
    public abstract void render(Canvas canvas, Paint paint);
    public abstract boolean cross(Shape other, float margin);
    public abstract boolean contains(Vec2 point);
    public abstract RectF getRect();
}
