package org.joshy.gfx.draw;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 26, 2010
 * Time: 9:49:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class Transform {
    public static final Transform Y_AXIS = new Transform(0.0,1.0,0.0);
    private double x;
    private double y;
    private double z;
    public static final Transform Z_AXIS = new Transform(0,0,1);

    public Transform(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
