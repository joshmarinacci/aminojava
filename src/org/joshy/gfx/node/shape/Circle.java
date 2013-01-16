package org.joshy.gfx.node.shape;

import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Oct 28, 2010
 * Time: 3:12:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class Circle extends Shape {
    private double x = 0;
    private double y = 0;
    private double radius = 10;

    public Circle(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void draw(GFX g) {
        g.setPaint(getFill());
        g.fillOval(x-radius,y-radius,radius*2,radius*2);
        g.setPaint(getStroke());
        if(getStrokeWidth() > 0) {
            g.setStrokeWidth(getStrokeWidth());
            g.drawOval(x-radius,y-radius,radius*2,radius*2);
            g.setStrokeWidth(1);
        }
    }

    @Override
    public Bounds getVisualBounds() {
        return new Bounds(x-radius,y-radius,radius*2,radius*2);
    }

    @Override
    public Bounds getInputBounds() {
        return getVisualBounds();
    }

    public Circle setRadius(double radius) {
        this.radius = radius;
        return this;
    }
}
