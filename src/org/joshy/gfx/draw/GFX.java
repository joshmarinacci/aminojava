package org.joshy.gfx.draw;

import java.awt.Shape;
import org.joshy.gfx.node.Bounds;

import javax.media.opengl.GLAutoDrawable;
import java.awt.geom.Path2D;


/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 18, 2010
 * Time: 2:07:30 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class GFX {
    
    public abstract void setPaint(Paint paint);
    public abstract void setStrokeWidth(double strokeWidth);

    public abstract double getOpacity();
    public abstract void setOpacity(double opacity);

    public abstract void setPureStrokes(boolean pureStrokes);

    public abstract void drawRect(double x, double y, double width, double height);
    public abstract void fillRect(double x, double y, double width, double height);

    public abstract void drawRoundRect(double x, double y, double w, double h, double aw, double ah);
    public abstract void fillRoundRect(double x, double y, double w, double h, double aw, double ah);

    public abstract void drawPolygon(double[]points);
    public abstract void drawPolygon(double[]points, boolean closed);
    public abstract void fillPolygon(double[] points);

    public abstract void fillCircle(double x, double y, double radius);

    public abstract void fillArc(double cx, double cy, double radius, double angleStart, double angleEnd);

    public abstract void drawOval(double x, double y, double width, double height);
    public abstract void fillOval(double x, double y, double width, double height);

    public abstract void drawLine(double x, double y, double x2, double y2);
    public abstract void drawText(String text, Font font, double x, double y);

    public abstract void drawImage(Image image, double x, double y);
    public abstract void drawImage(Image image,
                                   double dx, double dy, double dw, double dh,
                                   double sx, double sy, double sw, double sh
                                   );
    public abstract void drawGridNine(GridNine g9, double x, double y, double width, double height);

    public abstract void translate(double x, double y);
    public abstract void translate(double translateX, double translateY, double translateZ);
    public abstract void scale(double scaleX, double scaleY);
    public abstract void rotate(double rotation, Transform rotationAxis);
    public abstract void push();
    public abstract void pop();

    public abstract boolean isGL();
    public abstract GLAutoDrawable getDrawable();

    public abstract void setClipRect(Bounds bounds);
    public abstract Bounds getClipRect();

    public abstract void dispose();
    public abstract void fillRoundRectAltered(double x, double y, double width, double height, double ul, double ur, double lr, double ll);
    public abstract void fillCustomRoundRect(double x, double y, double w, double h, double ulw, double ulh, double urw, double urh, double lrw, double lrh, double llw, double llh);
    public abstract void drawCustomRoundRect(double x, double y, double w, double h, double ulw, double ulh, double urw, double urh, double lrw, double lrh, double llw, double llh);


    public abstract void drawPath(Path2D.Double path);
    public abstract void fillPath(Path2D.Double path);

    public abstract void setSmoothImage(boolean smooth);
    public abstract void setMask(Shape shape);

    public abstract ImageBuffer createBuffer(int i, int i1);

    public abstract void draw(ImageBuffer buf, double x, double y);

}
