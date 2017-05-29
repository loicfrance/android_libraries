package com.loicfrance.library.geometry2d;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by rfrance on 21/11/2015.
 */
public class Point extends Shape {
    @Override
    public void rotateRadians(float radians) { }

    @Override
    public void grow(float factor) { }

    @Override
    public void render(Canvas canvas, Paint paint) {
        canvas.drawPoint(center.x, center.y, paint);
    }

    @Override
    public boolean cross(Shape other, float margin) {
        if(other instanceof Point) {
            return Maths2D.distance(center, other.center) < margin;
        }
        else if(other instanceof PolyLine) {
            boolean result = false;
            for(Line l : ((PolyLine) other).getLines()) {
                result = l.cross(this, margin);
                if(result) break;
            }
            return result;
        }
        else if(other instanceof Ray) {
            return ((Ray)other)
                    .getLine(Maths2D.distance(center, other.center))
                    .cross(this, margin);
        }
        else return other.cross(this, margin);
    }

    @Override
    public boolean contains(Vec2 point) {
        return center.equals(point);
    }

    @Override
    public RectF getRect() {
        return new RectF(center.x, center.y, center.x, center.y);
    }
}
