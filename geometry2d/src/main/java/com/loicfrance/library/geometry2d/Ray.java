package com.loicfrance.library.geometry2d;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by rfrance on 22/11/2015.
 */
public class Ray extends Shape{
    private float angle;


    public Ray(Vec2 start, float radians) {
        super(start);
        this.angle = radians;
    }

    public Line getLine(float length) {
        Vec2 end = new Vec2(
                (float) Math.cos(angle)*length + center.x,
                (float) Math.sin(angle)*length + center.y);
        Line result =  new Line(center, end);
        return result;
    }
    @Override
    public void rotateRadians(float radians) {
        angle += radians;
    }

    @Override
    public void grow(float factor) { }

    @Override
    public void render(Canvas canvas, Paint paint) {
        float length = canvas.getWidth() + canvas.getHeight();
        float endX = (float) (Math.cos(angle)*length);
        float endY = (float) (Math.sin(angle)*length);
        canvas.drawLine(center.x, center.y, endX, endY, paint);
    }

    @Override
    public boolean cross(Shape other, float margin) {
        if(other instanceof Ray) {
            return getLine(Float.MAX_VALUE).cross(other, margin);
        }
        else return other.cross(this, margin);
    }

    @Override
    public boolean contains(Vec2 point) {
        return getLine(Maths2D.distance(center, point)).contains(point);
    }

    @Override
    public RectF getRect() {
        return getLine(Float.MAX_VALUE-center.x-center.y).getRect();
    }
}
