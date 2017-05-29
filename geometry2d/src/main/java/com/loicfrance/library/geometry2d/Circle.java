package com.loicfrance.library.geometry2d;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by rfrance on 21/11/2015.
 */
public class Circle extends Shape {
    private float radius;
    public Circle(Vec2 center, float radius) {
        super(center);
    }

    public float getRadius() {
        return radius;
    }
    public void setRadius(float radius) {
        this.radius = radius;
    }
    @Override
    public void rotateRadians(float degrees) {}

    @Override
    public void grow(float factor) {
        radius *= factor;
    }

    @Override
    public void render(Canvas canvas, Paint paint) {
        canvas.drawCircle(center.x, center.y, radius, paint);
    }

    @Override
    public boolean cross(Shape other, float margin) {
        if(other instanceof Circle) {
            Circle c = (Circle) other;
            float distance = Maths2D.distance(c.center, center);
            return distance-margin < c.radius + radius &&
                    radius < distance+margin + c.radius && // the other circle is not inside this circle
                    c.radius < distance+margin + radius; // this circle is not inside the other circle
        }
        else if(other instanceof Line) {

            Line line = (Line) other;
            Vec2 p0 = line.getP0();
            Vec2 p1 = line.getP1();
            boolean result;
            //if the circle contains the 2 points, it cannot cross the segment. if it contains only one of the points it cross it
            if(contains(p0)) {
                result = !contains(p1);
            }
            else if(contains(p1)) {
                result = !contains(p0);
            }
            //if it does not contains any of the line's end points...
            else {
                Vec2 AB = Maths2D.translation(p0, p1);
                Vec2 AC = Maths2D.translation(p0, center);
                Vec2 u = AB.getUnitVec();
                Vec2 I = (new Vec2(u)).mul(Maths2D.dotProd(u, AC));
                I.add(p0);

                result = line.contains(I) && Maths2D.distance(center, I) < radius+margin;
            }
            return result;
        }
        else if(other instanceof Point) {
            return Maths2D.distance(center, other.center) < radius+margin;
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
                    ((Ray) other).getLine(Maths2D.distance(center, other.center)+radius),
                    margin);
        }
        else return other.cross(this, margin);
    }

    @Override
    public boolean contains(Vec2 point) {
        return Maths2D.distance(center, point) < radius;
    }

    @Override
    public RectF getRect() {
        return new RectF(
                center.x - radius,
                center.y - radius,
                center.x + radius,
                center.y + radius);
    }
}
