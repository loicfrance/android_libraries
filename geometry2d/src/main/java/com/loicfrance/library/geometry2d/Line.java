package com.loicfrance.library.geometry2d;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by rfrance on 21/11/2015.
 */
public class Line extends Shape {
    private float angle;
    private float length;

    public Line(Vec2 p0, Vec2 p1) {
        super((p0.x+p1.x)/2f, (p0.y + p1.y)/2f);
        this.length = Maths2D.distance(p0, p1);
        this.angle = (float) Math.atan2(p1.y - p0.y, p1.x - p0.x);
    }

    public float getAngle() {
        return angle;
    }

    public Line setAngle(float angle) {
        this.angle = angle;
        return this;
    }

    public float getLength() {
        return length;
    }

    public Line setLength(float length) {
        this.length = length;
        return this;
    }

    public Vec2 getP0() {
        Vec2 p0 = new Vec2(center);
        p0.x -= (float) (Math.cos(angle)*length/2f);
        p0.y -= (float) (Math.sin(angle)*length/2f);
        return p0;
    }
    public Vec2 getP1() {
        Vec2 p1 = new Vec2(center);
        p1.x += (float) (Math.cos(angle)*length/2f);
        p1.y += (float) (Math.sin(angle)*length/2f);
        return p1;
    }
    public Vec2 getUnitVec() {
        Vec2 res = new Vec2(Vec2.X);
        Maths2D.rotate(angle, res);
        return res;
    }

    @Override
    public void rotateRadians(float radians) {
        angle += radians;
    }

    @Override
    public void grow(float factor) {
        length *= factor;
    }

    @Override
    public void render(Canvas canvas, Paint paint) {
        float dX = (float) (Math.cos(angle)*length/2f);
        float dY = (float) (Math.sin(angle)*length/2f);
        canvas.drawLine(center.x-dX, center.y-dY, center.x+dX, center.y+dY, paint);
    }

    @Override
    public boolean cross(Shape other, float margin) {
        if( other instanceof Line) {
            Vec2 A = getP0(),
                 B = getP1(),
                 C = ((Line) other).getP0(),
                 D = ((Line) other).getP1();
            Vec2 I = Maths2D.translation(A, B),
                 J = Maths2D.translation(C, D);

            float divider = I.x * J.x - I.y*J.y;
            if(divider == 0f) {
                //parallel lines
                return false;
            }
            //pos1 = -(-Ix*Ay +Ix*By + Iy*Ax - Iy*Bx) / divider
            float pos1 =
                    -(
                            -I.x * A.y +
                                    I.x * B.y +
                                    I.y * A.x -
                                    I.y * B.x)
                            / divider;
            //pos2 = -(Ax*Jy-Cx*Jy-Jx*Ay+Jx*Cy) / divider
            float pos2 =
                    -(
                            A.x * J.y -
                                    B.x * J.y -
                                    J.x * A.y +
                                    J.x * B.y)
                            / divider;
            float m = margin/divider;
            return (pos1+m >= 0 && pos1-m <= 1 && pos2+m >= 0 && pos2-m <= 1);
        }
        else if(other instanceof Point) {
            Circle c = new Circle(other.center, 0);
            return c.cross(this, margin);
        }
        else if(other instanceof PolyLine) {
            boolean result = false;
            for(Line l : ((PolyLine) other).getLines()) {
                result = cross(l, margin);
                if(result) break;
            }
            return result;
        }
        else if(other instanceof Ray) {
            return cross(
                    ((Ray) other).getLine(Maths2D.distance(center, other.center) + length),
                    margin);
        }
        else return other.cross(this, margin);
    }

    @Override
    public boolean contains(Vec2 point) {
        Vec2 t = Maths2D.translation(center, point);
        Vec2 vec = getUnitVec();

        vec.mul(Maths2D.distance(center, point));
        boolean result = false;
        if(t.equals(vec)) result =  true;
        else {
            vec.mul(-1);
            result = t.equals(vec);
        }
        return result;
    }

    @Override
    public RectF getRect() {
        float dX = (float) (Math.cos(angle)*length/2f);
        float dY = (float) (Math.sin(angle)*length/2f);
        return new RectF(center.x-dX, center.y-dY, center.x+dX, center.y+dY);
    }
}
