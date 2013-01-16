package org.joshy.gfx.node.shape;

import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 18, 2010
 * Time: 2:04:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class Rectangle extends Shape {

    private double x;
    private double y;
    private double width;
    private double height;
    private double arcWidth;
    private double arcHeight;

    public Rectangle() {
        super();
        width = 100.0;
        height = 100.0;
        x = 0.0;
        y = 0.0;
    }

    public Rectangle(double x, double y, double w, double h) {
        this.setX(x);
        this.setY(y);
        this.setWidth(w);
        this.setHeight(h);
    }

    public double getWidth() {
        return width;
    }

    public Rectangle setWidth(double width) {
        this.width = width;
        setDrawingDirty();
        return this;
    }

    public double getHeight() {
        return height;
    }

    public Rectangle setHeight(double height) {
        this.height = height;
        setDrawingDirty();
        return this;
    }

    public double getY() {
        return y;
    }

    public Rectangle setY(double y) {
        this.y = y;
        setDrawingDirty();
        return this;
    }

    public double getX() {
        return x;
    }

    public Rectangle setX(double x) {
        this.x = x;
        setDrawingDirty();
        return this;
    }

    @Override
    public Bounds getVisualBounds() {
        return new Bounds(getTranslateX()+getX(),getTranslateY()+getY(),getWidth(),getHeight());
    }

    @Override
    public Bounds getInputBounds() {
        return getVisualBounds();
    }

    @Override
    public void draw(GFX g) {
        g.setPaint(getFill());
        if(arcWidth > 0 || arcHeight > 0) {
            g.fillRoundRect(x,y,width,height,arcWidth,arcHeight);
        } else {
            g.fillRect(x,y,width,height);
        }
        g.setPaint(getStroke());
        if(getStrokeWidth() > 0) {
            g.setStrokeWidth(getStrokeWidth());
            if(arcWidth > 0 || arcHeight > 0) {
                g.drawRoundRect(x,y,width,height,arcWidth,arcHeight);
            } else {
                g.drawRect(x,y,width,height);
            }
            g.setStrokeWidth(1);
        }
    }

    public Rectangle setArcWidth(double arcWidth) {
        this.arcWidth = arcWidth;
        return this;
    }

    public Rectangle setArcHeight(double arcHeight) {
        this.arcHeight = arcHeight;
        return this;
    }
}
