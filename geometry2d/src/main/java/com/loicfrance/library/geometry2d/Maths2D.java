package com.loicfrance.library.geometry2d;

import android.graphics.RectF;

/**
 * Created by rfrance on 21/11/2015.
 */
public class Maths2D {

//__________________________________________________________________________________________________squareDistance
    public static float squareDistance(float x1, float y1, float x2, float y2) {
        return (float) (Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
    public static float squareDistance(Vec2 v1, Vec2 v2) {
        return squareDistance(v1.x, v1.y, v2.x, v2.y);
    }
//__________________________________________________________________________________________________distance
    public static float distance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(squareDistance(x1, y1, x2, y2));
    }
    public static float distance(Vec2 v1, Vec2 v2) {
        return distance(v1.x, v1.y, v2.x, v2.y);
    }
//__________________________________________________________________________________________________translation
    public static Vec2 translation(Vec2 from, Vec2 to) {
        return new Vec2(to.x - from.x, to.y - from.y);
    }
    public static Vec2 translation(float fromX, float fromY, float toX, float toY) {
        return new Vec2(toX - fromX, toY - fromY);
    }
    public static float X_translation(Vec2 from, Vec2 to) {
        return to.x - from.x;
    }
    public static float Y_translation(Vec2 from, Vec2 to) {
        return to.y - from.y;
    }
//__________________________________________________________________________________________________scalProd
    public static float dotProd(float x1, float y1, float x2, float y2) {
        return x1*x2 + y1*y2;
    }
    public static float dotProd(Vec2 v1, Vec2 v2) {
        return dotProd(v1.x, v1.y, v2.x, v2.y);
    }
//__________________________________________________________________________________________________dotProd
    public static float vecProd(float x1, float y1, float x2, float y2) {
        return x1*y2-x2*y1;
    }
    public static float vecProd(Vec2 v1, Vec2 v2) {
        return dotProd(v1.x, v1.y, v2.x, v2.y);
    }
//__________________________________________________________________________________________________sum
    public static Vec2 sum(float... xyxy) {
        if(xyxy.length%2 == 1) throw new ArithmeticException("cannot create vector from odd number of floats");
        Vec2 result = new Vec2();
        for(int i=0; i< xyxy.length/2; i++) {
            result.x += xyxy[2*i];
            result.y += xyxy[2*i+1];
        }
        return result;
    }
    public static Vec2 sum(Vec2... v) {
        Vec2 result = new Vec2();
        for(Vec2 vect : v) {
            result.add(vect);
        }
        return result;
    }
//__________________________________________________________________________________________________rotate
    public static void rotate(Vec2 anchor, double radians, Vec2... pts) {
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double x2, y2;
        for(Vec2 p : pts) {
            p.subtract(anchor);
            x2 = p.x * cos - p.y * sin;
            y2 = p.x * sin + p.y * cos;
            p.x = (float) (x2 + anchor.x);
            p.y = (float) (y2 + anchor.y);
        }
    }

    /**
     * same as {@code rotate(Vec2.ZERO, radians, pts)}, but a little bit faster.
     * Prefer this method if the anchor point is (0,0)
     */
    public static void rotate(double radians, Vec2... pts) {
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        float x2, y2;
        for(Vec2 p : pts) {
            x2 = (float) (p.x * cos - p.y * sin);
            y2 = (float) (p.x * sin + p.y * cos);
            p.x = x2;
            p.y = y2;
        }
    }
    public static void moveRect(RectF rect, Vec2 d) {
        rect.left += d.x;
        rect.top += d.y;
        rect.right += d.x;
        rect.bottom += d.y;
    }

    public static double[] getPolarCoordinates(float x, float y) {
        double r = Math.sqrt(x * x + y * y);
        return new double[]{
                r, Math.atan2(y,x)
        };
    }
    public static float[] getCartesianCoordinates(double r, double theta) {
        return new float[] {
                (float) (Math.cos(theta)*r),
                (float) (Math.sin(theta)*r)
        };
    }

}
