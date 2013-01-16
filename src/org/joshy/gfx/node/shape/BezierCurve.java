package org.joshy.gfx.node.shape;

import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.shape.Shape;

import java.awt.geom.Path2D;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Oct 28, 2010
 * Time: 3:55:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class BezierCurve extends Shape {
    private double cx1;
    private double cy1;
    private double cx2;
    private double cy2;
    private double x1;
    private double y1;
    private double x2;
    private double y2;

    public BezierCurve(double cx1, double cy1, double x1, double y1, double x2, double y2, double cx2, double cy2) {
        this.cx1 = cx1;
        this.cy1 = cy1;
        this.cx2 = cx2;
        this.cy2 = cy2;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public void draw(GFX g) {
        Path2D.Double path = new Path2D.Double();
        path.moveTo(x1,y1);
        path.curveTo(cx1,cy1, cx2,cy2, x2,y2);
        //g.setPaint(getFill());
        //g.fillPath(path);
        g.setPaint(getStroke());
        if(getStrokeWidth() > 0) {
            g.setStrokeWidth(getStrokeWidth());
            g.drawPath(path);
            g.setStrokeWidth(1);
        }
    }

    @Override
    public Bounds getVisualBounds() {
        return new Bounds(0,0,0,0);
    }

    @Override
    public Bounds getInputBounds() {
        return getVisualBounds();
    }
}

