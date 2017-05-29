package com.loicfrance.library.geometry2d;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import java.io.IOException;

/**
 * Created by rfrance on 21/11/2015.
 */
public class PolyLine extends Shape {
    protected Vec2[] points;

    public PolyLine(Vec2... points) {
        super();
        Vec2 center = new Vec2();
        for(Vec2 point : points) {
            center.add(point);
        }
        center.mul(1f / points.length);
        moveTo(center);

        this.points = new Vec2[points.length];
        for(int i=0; i< points.length; i++) {
            this.points[i] = new Vec2(points[i]).subtract(center);
        }
    }

    public Path getPath() {
        Path path = new Path();
        path.moveTo(center.x+points[0].x, center.y+points[0].y);
        for(int i=1; i< points.length; i++) {
            path.lineTo(points[i].x+center.x, points[i].y+center.y);
        }
        return path;
    }
    public Vec2 getPoint(int index) {
        return new Vec2(points[index]).add(center);
    }
    public void removePoints(int... indices) {
        Vec2[] newPoints = new Vec2[points.length-indices.length];
        int length = indices[0];
        int lastPos = 0;
        if(length > 0) {
            System.arraycopy(points, 0, newPoints, lastPos, length);
        }
        for(int i = 1; i< indices.length; i++) {
            length = indices[i] - indices[i-1];
            if(length > 0) {
                System.arraycopy(points, indices[i - 1] + 1, newPoints, lastPos, length);
                lastPos += length;
            }
        }
        points = newPoints;
    }
    public void setPoint(int index, Vec2 v) {
        points[index].set(v);
    }
    public int getPointsNumber() {
        return points.length;
    }
    public Line[] getLines() {
        Line[] result = new Line[points.length-1];
        for(int i=0; i< points.length-1; i++) {
            result[i] = new Line(getPoint(i), getPoint(i + 1));
        }
        return result;
    }

    @Override
    public void rotateRadians(float radians) {
        Maths2D.rotate(radians, points);
    }

    @Override
    public void grow(float factor) {
        for(Vec2 v : points) {
            v.mul(factor);
        }
    }

    @Override
    public void render(Canvas canvas, Paint paint) {
        canvas.drawPath(getPath(), paint);
    }

    @Override
    public boolean cross(Shape other, float margin) {
        return false;
    }

    @Override
    public boolean contains(Vec2 point) {
        return false;
    }

    @Override
    public RectF getRect() {
        RectF result = new RectF();
        for(Vec2 p : points) {
            if(result.left > p.x+center.x) result.left = p.x;
            else if(result.right < p.x) result.right = p.x;
            if(result.top > p.y) result.top = p.y;
            else if(result.bottom < p.y) result.bottom = p.y;
        }
        result.left += center.x;
        result.right += center.x;
        result.top += center.y;
        result.bottom += center.y;
        return result;
    }
}
