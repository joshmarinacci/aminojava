package org.joshy.gfx.node.shape;

import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;

import java.awt.geom.Path2D;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Oct 28, 2010
 * Time: 3:12:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class PathShape extends Shape {
    private Path2D.Double path;
    private boolean closed;

    public PathShape(Path2D.Double path) {
        this.path = path;
    }

    @Override
    public void draw(GFX g) {
        //Path2D.Double path = new Path2D.Double();
//        path.moveTo(x1,y1);
//        path.curveTo(cx1,cy1, cx2,cy2, x2,y2);
        g.setPaint(getFill());
        g.fillPath(path);
        g.setPaint(getStroke());
        if(getStrokeWidth() > 0) {
            g.setStrokeWidth(getStrokeWidth());
            g.drawPath(path);
            g.setStrokeWidth(1);
        }
    }

    @Override
    public Bounds getVisualBounds() {
        return new Bounds(0,0,0,0);
    }

    @Override
    public Bounds getInputBounds() {
        return getVisualBounds();
    }

    public PathShape setClosed(boolean closed) {
        this.closed = closed;
        return this;
    }
}
