package org.joshy.gfx.node.shape;

import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Oct 29, 2010
 * Time: 6:37:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class Line extends Shape {
    private double x1;
    private double y1;
    private double x2;
    private double y2;

    public Line(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public void setX1(double x1) {
        this.x1 = x1;
    }

    @Override
    public void draw(GFX g) {
        g.setPaint(getStroke());
        g.setStrokeWidth(getStrokeWidth());
        g.drawLine(x1,y1,x2,y2);
        g.setStrokeWidth(1);
    }

    @Override
    public Bounds getVisualBounds() {
        return new Bounds(0,0,0,0);
    }

    @Override
    public Bounds getInputBounds() {
        return new Bounds(0,0,0,0);
    }

    public void setY1(double y1) {
        this.y1 = y1;
    }

    public void setX2(double x2) {
        this.x2 = x2;
    }

    public void setY2(double y2) {
        this.y2 = y2;
    }
}
