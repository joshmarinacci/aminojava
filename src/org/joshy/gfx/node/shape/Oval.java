package org.joshy.gfx.node.shape;

import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Feb 1, 2010
 * Time: 11:35:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class Oval extends Shape {
    
    private double width = 100;
    private double height = 100;

    public Oval() {
    }

    public Oval(double w, double h) {
        this.setWidth(w);
        this.setHeight(h);
    }

    public Oval setWidth(double width) {
        this.width = width;
        return this;
    }

    public Oval setHeight(double height) {
        this.height = height;
        return this;
    }

    @Override
    public void draw(GFX g) {
        g.setPaint(getFill());
        g.fillOval(0,0,getWidth(),getHeight());
        g.setPaint(getStroke());
        if(getStrokeWidth() > 0) {
            g.setStrokeWidth(getStrokeWidth());
            g.drawOval(0,0,getWidth(),getHeight());
            g.setStrokeWidth(1);
        }
    }

    @Override
    public Bounds getVisualBounds() {
        return new Bounds(getTranslateX(),getTranslateY(),getWidth(),getHeight());
    }

    @Override
    public Bounds getInputBounds() {
        return getVisualBounds();
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

}
