package org.joshy.gfx.node.shape;

import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Oct 28, 2010
 * Time: 3:13:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class Arc extends Shape {
    private double startAngle;
    private double endAngle;
    private boolean closed;
    private double x;
    private double y;
    private double radius;

    public Arc(double cx, double cy) {
        this.x = cx;
        this.y = cy;
    }

    @Override
    public void draw(GFX g) {
        if(isClosed()) {
            g.setPaint(getFill());
            g.fillArc(x,y,radius,startAngle,endAngle);
        }
        g.setPaint(getStroke());
        if(getStrokeWidth() > 0) {
            g.setStrokeWidth(getStrokeWidth());
            //g.drawArc(x,y,radius,startAngle,endAngle);
            g.setStrokeWidth(1);
        }
    }

    @Override
    public Bounds getVisualBounds() {
        return new Bounds(0,0,100,100);
    }

    @Override
    public Bounds getInputBounds() {
        return getVisualBounds();
    }

    public Arc setStartAngle(double startAngle) {
        this.startAngle = startAngle;
        return this;
    }

    public Arc setEndAngle(double endAngle) {
        this.endAngle = endAngle;
        return this;
    }

    public Arc setClosed(boolean closed) {
        this.closed = closed;
        return this;
    }

    public Arc setRadius(double radius) {
        this.radius = radius;
        return this;
    }

    public boolean isClosed() {
        return closed;
    }
}
