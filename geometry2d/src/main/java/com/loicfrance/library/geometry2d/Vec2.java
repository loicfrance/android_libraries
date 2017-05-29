package com.loicfrance.library.geometry2d;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rfrance on 21/11/2015.
 */
public class Vec2 {
    public static final Vec2 ZERO = new Vec2(0f,0f);
    public static final Vec2 X = new Vec2(1f,0f);
    public static final Vec2 Y = new Vec2(0f,1f);
    public float x, y;

    public Vec2(float x, float y) {
        this.x = x; this.y = y;
    }
    public Vec2(Vec2 src) {
        this.x = src.x; this.y = src.y;
    }
    public Vec2() {
        this.x = this.y = 0;
    }
    public Vec2 set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }
    public Vec2 set(Vec2 src) {
        if(src == null) set(0f,0f);
        else set(src.x, src.y);
        return this;
    }
    public Vec2 add(Vec2 other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }
    public Vec2 add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }
    public Vec2 subtract(Vec2 other) {
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }
    public Vec2 mul(float factor) {
        x*= factor;
        y*= factor;
        return this;
    }
    public float squareMagnitude() {
        return x*x+y*y;
    }
    public float magnitude() {
        return (float) Math.sqrt(squareMagnitude());
    }
    public Vec2 getUnitVec() {
        return new Vec2(this).mul(1f/magnitude());
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Vec2 &&
                ((Vec2)o).x == x &&
                ((Vec2)o).y == y);
    }
    @Override
    public int hashCode() {

        return 37* Float.floatToIntBits(x) + Float.floatToIntBits(y);
    }
    @Override
    public String toString() {
        return "("+ x + ", " + y + ')';
    }

//__________________________________________________________________________________________________getXYArray
    public static float[] getXYXYArray(Vec2... v) {
        float[] result = new float[v.length*2];
        for(int i=0; i< v.length; i++) {
            result[2*i] = v[i].x;
            result[2*i+1] = v[i].y;
        }
        return result;
    }
    public static float[] getXXYYArray(Vec2... v) {
        float[] result = new float[v.length*2];
        for(int i=0; i< v.length; i++) {
            result[i] = v[i].x;
            result[v.length+i] = v[i].y;
        }
        return result;
    }



}
