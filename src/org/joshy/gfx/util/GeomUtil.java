package org.joshy.gfx.util;

import java.awt.geom.Point2D;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: May 14, 2010
 * Time: 4:43:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class GeomUtil {
    /**
     * calculate the point created by starting at the point point, then moving
     * dist along the line at the angle angle, in degrees.
     */
    public static Point2D calcPoint(Point2D point, double angle, double dist) {
        return new Point2D.Double(
                point.getX()+Math.sin(Math.toRadians(angle))*dist,
                point.getY()+Math.cos(Math.toRadians(angle))*dist
        );
    }

    /**
     * snap angle to the nearest 45 degree axis
     * @param angle the angle in degrees
    */
    public static double snapTo45(double angle) {
        angle = (angle+360) % 360; //make positive
        long iangle = Math.round(angle / 45); //round to nearest octant
        return iangle * 45.0;
    }

    /**
     *  calculate the angle of the line formed by the two points, in degrees
     */
    public static double calcAngle(Point2D point1, Point2D point2) {
        return Math.toDegrees(Math.atan2(point2.getX()-point1.getX(),point2.getY()-point1.getY()));
    }

    public static Point2D subtract(Point2D point1, Point2D point2) {
        return new Point2D.Double(point1.getX()-point2.getX(),point1.getY()-point2.getY());
    }
}
