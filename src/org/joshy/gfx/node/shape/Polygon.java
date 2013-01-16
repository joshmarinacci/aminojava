package org.joshy.gfx.node.shape;

import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.shape.Shape;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Polygon extends Shape {
    private List<Point2D.Double> points = new ArrayList<Point2D.Double>();
    private boolean closed = true;

    @Override
    public void draw(GFX g) {
        g.setPaint(getFill());
        double[] points = getPointsAsDouble();
        g.fillPolygon(points);
        g.setPaint(getStroke());
        if(getStrokeWidth() > 0) {
            g.setStrokeWidth(getStrokeWidth());
            g.drawPolygon(points,true);
            g.setStrokeWidth(1);
        }
    }

    private double[] getPointsAsDouble() {
        double[] ds = new double[points.size()*2];
        for(int i=0; i<points.size();i++){
            ds[i*2]=points.get(i).x;
            ds[i*2+1]=points.get(i).y;
        }
        return ds;
    }

    @Override
    public Bounds getVisualBounds() {
        return new Bounds(0,0,0,0);
    }

    @Override
    public Bounds getInputBounds() {
        return getVisualBounds();
    }

    public Polygon addPoint(double x, double y) {
        points.add(new Point2D.Double(x,y));
        return this;
    }

    public Polygon setClosed(boolean closed) {
        this.closed = closed;
        return this;
    }
}
