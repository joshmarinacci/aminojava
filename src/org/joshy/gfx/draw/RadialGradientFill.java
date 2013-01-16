package org.joshy.gfx.draw;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: 1/15/11
 * Time: 12:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class RadialGradientFill extends MultiGradientFill {
    private double centerX;
    private double centerY;
    private double radius;


    public RadialGradientFill setCenterX(double x) {
        this.centerX = x;
        return this;
    }

    public RadialGradientFill setCenterY(double y) {
        this.centerY = y;
        return this;
    }

    public RadialGradientFill setRadius(double radius) {
        this.radius = radius;
        return this;
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public double getRadius() {
        return radius;
    }


    @Override
    public Paint duplicate() {
        RadialGradientFill rgf = new RadialGradientFill();
        rgf.centerX = centerX;
        rgf.centerY = centerY;
        rgf.radius = radius;
        for(Stop s : stops) {
            rgf.addStop(s.duplicate());
        }
        return rgf;
    }

    @Override
    public MultiGradientFill translate(double x, double y) {
        RadialGradientFill rgf = new RadialGradientFill();
        rgf.centerX = centerX+x;
        rgf.centerY = centerY+y;
        rgf.radius = radius;
        for(Stop s : stops) {
            rgf.addStop(s.duplicate());
        }
        return rgf;
    }
}
