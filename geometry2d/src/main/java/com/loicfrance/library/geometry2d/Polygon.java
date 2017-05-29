package com.loicfrance.library.geometry2d;

import android.annotation.TargetApi;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;

/**
 * Created by rfrance on 21/11/2015.
 */
public class Polygon extends PolyLine {

    public Polygon(Vec2... points) {
        super(points);
    }

    public static Polygon Regular(Vec2 center, int sides, float startRadians, float radius) {
        double angle = startRadians;
        Vec2[] points = new Vec2[sides];
        for(Vec2 p : points) {
            p = new Vec2((float) (radius* Math.cos(angle)), (float) (radius* Math.sin(angle)));
        }
        Polygon p = new Polygon(points);

        return p;
    }
    @Override
    public Path getPath() {
        Path p = super.getPath();
        p.close();
        return p;
    }

    @Override
    public boolean contains(Vec2 point) {
        Line[] lines = new Line[4];
        RectF r = getRect();
        float width = r.width();
        float height = r.height();
        Vec2 endPoint = new Vec2(center);
        endPoint.x -= width+1; //+1 : just to make sure that it really crosses the lines
        lines[0] = new Line(center, endPoint);
        endPoint.x += 2*width+2;
        lines[2] = new Line(center, endPoint);
        endPoint.x = center.x;
        endPoint.y -= height+1;
        lines[1] = new Line(center, endPoint);
        endPoint.y += 2*height+2;
        lines[3] = new Line(center, endPoint);
        boolean result = true;
        for(Line l : lines) {
            result = cross(l, 0);
            if(!result) break;
        }
        return result;
    }

    @Override
    public Line[] getLines() {
        Line[] result = new Line[points.length];
        for(int i=0; i< points.length-1; i++) {
            result[i] = new Line(getPoint(i), getPoint(i + 1));
        }
        result[points.length-1] = new Line(
                getPoint(points.length - 2), getPoint(points.length - 1));
        return result;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean isConvex() {
        return getPath().isConvex();
    }
}
