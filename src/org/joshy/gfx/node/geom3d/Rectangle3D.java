package org.joshy.gfx.node.geom3d;

import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: Apr 16, 2010
* Time: 7:03:18 PM
* To change this template use File | Settings | File Templates.
*/
public class Rectangle3D extends Shape3D {
    private double width;
    private double height;
    private FlatColor fill;

    @Override
    public void draw(GFX g) {
        GLAutoDrawable drawable = g.getDrawable();
        GL2 gl = drawable.getGL().getGL2();
        double x = getTranslateX();
        double y = getTranslateY();
        double width = getWidth();
        double height = getHeight();

        gl.glColor4d(fill.getRed(),fill.getGreen(),fill.getBlue(),1.0*getOpacity());
        //gl.glColor4d(1.0,0.0,0.0,1.0*getOpacity());//c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
        gl.glRotated(getRotation(),getRotationAxis().getX(),getRotationAxis().getY(),getRotationAxis().getZ());
        gl.glTranslated( x,  y, getTranslateZ());
        /*
        gl.glBegin(GL.GL_LINE_STRIP);
        gl.glVertex2f(0, 0);
        gl.glVertex2d(width, 0);
        gl.glVertex2d(width, height);
        gl.glVertex2d(0, height);
        gl.glVertex2d(0, 0);
        gl.glEnd();*/
        
        gl.glBegin(GL2.GL_QUADS);
        gl.glVertex3d(0, 0, 0);
        gl.glVertex3d(width, 0, 0);
        gl.glVertex3d(width, height, 0);
        gl.glVertex3d(0, height, 0);
        gl.glEnd();

        gl.glTranslated(-x, -y, -getTranslateZ());
        gl.glRotated(-getRotation(),getRotationAxis().getX(),getRotationAxis().getY(),getRotationAxis().getZ());

    }

    @Override
    public Bounds getVisualBounds() {
        return new Bounds(0,0,100,100);
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

    public Rectangle3D setWidth(int width) {
        this.width = width;
        return this;
    }

    public Rectangle3D setHeight(int height) {
        this.height = height;
        return this;
    }

    public Rectangle3D setFill(FlatColor fill) {
        this.fill = fill;
        return this;
    }
}
