package org.joshy.gfx.node;

import java.awt.geom.Point2D;

/**
 * Represents the bounds of an object. It is immutable once created.
 * It has convience methods for the various things you might want to
 * calculate with the bounds.
 */
public final class Bounds {
    private double height;
    private double width;
    private double y;
    private double x;

    public Bounds(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Bounds(Point2D startPoint, Point2D endPoint) {
        if(startPoint.getX() < endPoint.getX()) {
            this.x = startPoint.getX();
            this.width = endPoint.getX()-this.x;
        } else {
            this.x = endPoint.getX();
            this.width = startPoint.getX()-this.x;
        }
        if(startPoint.getY() < endPoint.getY()) {
            this.y = startPoint.getY();
            this.height = endPoint.getY()-this.y;
        } else {
            this.y = endPoint.getY();
            this.height = startPoint.getY()-this.y;
        }
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public double getY() {
        return y;
    }

    public double getX() {
        return x;
    }

    @Override
    public String toString() {
        return "Bounds ["+x+","+y+" "+width+"x"+height+"]";
    }

    public boolean contains(double x, double y) {
        if(x >= this.x && x <= this.x + this.width) {
            if(y >= this.y && y <= this.y + this.height) {
                return true;
            }
        }
        return false;
    }

    public boolean contains(Point2D point) {
        return contains(point.getX(),point.getY());
    }

    public Bounds union(Bounds bounds) {
        double minx = Math.min(getX(),bounds.getX());
        double miny = Math.min(getY(),bounds.getY());
        double maxx2 = Math.max(getX()+getWidth(),bounds.getX()+bounds.getWidth());
        double maxy2 = Math.max(getY()+getHeight(),bounds.getY()+bounds.getHeight());
        return new Bounds(minx, miny, maxx2-minx, maxy2-miny);
    }

    public boolean intersects(Bounds bounds) {
        double x = this.getX();
        double x2 = getX()+getWidth();
        double bx = bounds.getX();
        double bx2 = bounds.getX()+bounds.getWidth();
        double y = getY();
        double y2 = getY()+getHeight();
        double by = bounds.getY();
        double by2 = bounds.getY()+bounds.getHeight();
        if((bx >= x && bx <= x2) || (bx2 >=x && bx2 <= x2)) {
            if((by >= y && by <= y2) ||(by2 >=y && by2 <= y2)) {
                return true;
            }
        }
        return false;
    }

    public double getX2() {
        return getX()+getWidth();
    }

    public double getY2() {
        return getY()+getHeight();
    }

    public double getCenterY() {
        return getY()+getHeight()/2.0;
    }

    public double getCenterX() {
        return getX()+getWidth()/2.0;
    }
}
