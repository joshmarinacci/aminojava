package org.joshy.gfx.text;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.awt.AWTTextureIO;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.*;

public abstract class JFont {
    protected Texture texture;
    protected double red;
    protected double green;
    protected double blue;
    protected Font font;
    protected double vectorThreshold;
    protected double size;
    private boolean vector = false;

    public JFont(String name, double size, boolean vector, Font font) {
        this.font = font;
        this.size = size;
        this.vectorThreshold = 40;
        this.vector = vector;
    }

    public void drawTexture(GL2 gl) {
        init();
        texture.enable();

        //set color to blend with
        //gl.glColor3d(0,0,0); // green
        gl.glColor3d(red, green, blue);
        gl.glEnable(GL.GL_BLEND); //turn on blending
        //gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glBlendFunc(GL2.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
        texture.bind();
        //gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
        gl.glBegin(GL2.GL_QUADS);
        TextureCoords tc = texture.getImageTexCoords();
        double x = 256;
        double y = 256;
        double h = 256;
        double w = 256;
        double z = 0;
        boolean flip = true;
        if (flip) {
            gl.glTexCoord2d(tc.left(), tc.bottom());
            gl.glVertex3d(-w / 2 + x, h / 2 + y, z);
            gl.glTexCoord2d(tc.right(), tc.bottom());
            gl.glVertex3d(w / 2 + x, h / 2 + y, z);
            gl.glTexCoord2d(tc.right(), tc.top());
            gl.glVertex3d(w / 2 + x, -h / 2 + y, z);
            gl.glTexCoord2d(tc.left(), tc.top());
            gl.glVertex3d(-w / 2 + x, -h / 2 + y, z);
        } else {
            gl.glTexCoord2d(tc.left(), tc.top());
            gl.glVertex3d(-w / 2 + x, h / 2, z);
            gl.glTexCoord2d(tc.right(), tc.top());
            gl.glVertex3d(w / 2 + x, h / 2, z);
            gl.glTexCoord2d(tc.right(), tc.bottom());
            gl.glVertex3d(w / 2 + x, -h / 2, z);
            gl.glTexCoord2d(tc.left(), tc.bottom());
            gl.glVertex3d(-w / 2 + x, -h / 2, z);
        }
        gl.glEnd();

        texture.disable();
    }

    protected abstract void init();

    protected void p(String s) {
        System.out.println(s);
    }

    public void setColor(double r, double g, double b) {
        this.red = r;
        this.green = g;
        this.blue = b;
    }

    public void drawText(GL2 gl, String s) {
        init();
        if(font.getSize2D() > vectorThreshold || vector) {
            drawVector(gl,s);
            return;
        }
        texture.enable();
        gl.glColor3d(red, green, blue);
        gl.glEnable(GL.GL_BLEND);


        //gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        //for some reason this function looks better than the previous one
        gl.glBlendFunc(GL2.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
        
        texture.bind();

        char[] chars = s.toCharArray();
        TextureCoords tc = texture.getImageTexCoords();
        double x = 2;
//        p("------");
        for (int i = 0; i < chars.length; i++) {
            gl.glBegin(GL2.GL_QUADS);
            int ch = chars[i] - ' ';

            Dimension2D dim = getCharLayoutDimension(ch);
            //p("advance = " + dim.getWidth());

            //doing ortho, so quad size is same as pixel size
            double h = dim.getHeight();
            double w = dim.getWidth();
            double y = -getMaxAscent() - 2;

            //calculate the sub texture coords for the grid point
            Rectangle2D bounds = getCharTextureBounds(ch);
            double tx1 = bounds.getX();
            double ty1 = bounds.getY();
            double tx2 = bounds.getX() + bounds.getWidth();
            double ty2 = bounds.getY() + bounds.getHeight();

            //draw the quad
            double z = 3;
            gl.glTexCoord2d(tx1, ty1);
            gl.glVertex3d(x    , y, z);
            gl.glTexCoord2d(tx2, ty1);
            gl.glVertex3d(x + w, y, z);
            gl.glTexCoord2d(tx2, ty2);
            gl.glVertex3d(x + w, y+h, z);
            gl.glTexCoord2d(tx1, ty2);
            gl.glVertex3d(x    , y+h, z);
            gl.glEnd();
            x += dim.getWidth();
        }
        texture.disable();
    }

    protected abstract void drawVector(GL2 gl, String text);


    abstract Dimension2D getCharLayoutDimension(int ch);

    abstract Rectangle2D getCharTextureBounds(int ch);

    abstract int getMaxAscent();

}
