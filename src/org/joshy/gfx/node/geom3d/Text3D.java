package org.joshy.gfx.node.geom3d;

import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.geom3d.TessCallback;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: Apr 17, 2010
* Time: 10:13:36 AM
* To change this template use File | Settings | File Templates.
*/
public class Text3D extends Shape3D {
    private double opacity;
    private FlatColor fill = FlatColor.BLACK;

    public Text3D() {
        buffer = new BufferedImage(256,256,BufferedImage.TYPE_INT_ARGB);
    }

    @Override
    public void draw(GFX g) {
        GLAutoDrawable drawable = g.getDrawable();
        GL2 gl = drawable.getGL().getGL2();



        double x = getTranslateX();
        double y = getTranslateY();

        gl.glColor4d(1.0,0.0,0.0,1.0*getOpacity());//c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
        gl.glRotated(getRotation(),getRotationAxis().getX(),getRotationAxis().getY(),getRotationAxis().getZ());
        gl.glTranslated( x,  y, getTranslateZ());

        //Font font = Font.name("Arial").size(100).resolve();

        //g.drawText("abc",font,50,200);
        fillVector(gl,"You Spin Me");

        gl.glTranslated(-x, -y, -getTranslateZ());
        gl.glRotated(-getRotation(),getRotationAxis().getX(),getRotationAxis().getY(),getRotationAxis().getZ());
    }

    @Override
    public Bounds getVisualBounds() {
        return new Bounds(0, 0, 300, 300);
    }

    @Override
    public Bounds getInputBounds() {
        return getVisualBounds();
    }
    private BufferedImage buffer;

    private void fillVector(GL2 gl, String text) {
        //create a tesselator
        GLUtessellator tobj = GLU.gluNewTess();
        GLU glu = new GLU();
        TessCallback tessCallback = new TessCallback(gl,glu);
        GLU.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);
        GLU.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);
        GLU.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);
        GLU.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);


        Font fontx = Font.name("Arial").size(100).resolve();
        java.awt.Font font = fontx.getAWTFont();
        Graphics2D g = buffer.createGraphics();
        System.out.println("filling vector using the font: " + font.getFontName() + " size = " + font.getSize2D());
        g.setFont(font);
/*        try {
        Font fnt = Font.createFont(Font.TRUETYPE_FONT, new File("goudy.ttf"));
        g.setFont(fnt.deriveFont(60.0f));
    } catch (FontFormatException e) {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    } catch (IOException e) {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    }*/
        //g.setFont(font.deriveFont((int)this.size));
        FontRenderContext frc = g.getFontRenderContext();
        GlyphVector vector = g.getFont().createGlyphVector(frc,text);
        gl.glPushMatrix();
        //gl.glColor3d(red, green, blue);
        gl.glColor4d(fill.getRed(),fill.getGreen(),fill.getBlue(),getOpacity());
        //gl.glColor3d(0.0, 0.0, 0.0);
        //gl.glRotated(angle, 0.0f, 1.0f, 0.0f);

        //gl.glBegin(GL.GL_LINE_STRIP);

        for(int i=0; i<vector.getNumGlyphs(); i++) {
            java.awt.Shape glyphShape = vector.getGlyphOutline(i);
            PathIterator it = glyphShape.getPathIterator(new AffineTransform());
            it = new FlatteningPathIterator(it,0.5);
            double[] coords = new double[6];
            double px = 0;
            double py = 0;
            double mx = 0;
            double my = 0;
            int pointCount = 0;
            GLU.gluTessBeginPolygon(tobj,null);
            while(true) {
                if(it.isDone()) break;
                int type = it.currentSegment(coords);
                //p(type, coords);
                if(type == PathIterator.SEG_MOVETO) {
                    px = coords[0];
                    py = coords[1];
                    mx = px;
                    my = py;
                    //gl.glBegin(GL.GL_LINE_STRIP);
                    //gl.glVertex2d(mx,my);
                    GLU.gluTessBeginContour(tobj);
                    double[] point = new double[3];
                    point[0] = mx;
                    point[1] = my;
                    point[2] = 0;
                    GLU.gluTessVertex(tobj, point, 0, point);
                }
                if(type == PathIterator.SEG_LINETO) {
                    //gl.glVertex2d(coords[0],coords[1]);
                    double[] point = new double[3];
                    point[0] = coords[0];
                    point[1] = coords[1];
                    point[2] = 0;
                    GLU.gluTessVertex(tobj, point, 0, point);
                    px = coords[0];
                    py = coords[1];
                }
                if(type == PathIterator.SEG_CLOSE) {
                    //gl.glVertex2d(mx,my);
                    //gl.glEnd();
                    double[] point = new double[3];
                    point[0] = mx;
                    point[1] = my;
                    point[2] = 0;
                    GLU.gluTessVertex(tobj, point, 0, point);
                    GLU.gluTessEndContour(tobj);
                }
                pointCount++;
                it.next();
            }
            GLU.gluTessEndPolygon(tobj);
        }

        /*for(int i=0; i<rect.length; i++) {
            GLU.gluTessVertex(tobj, rect[i], 0, rect[i]);
        } */
        //end tesselation
        GLU.gluDeleteTess(tobj);


        gl.glPopMatrix();
        g.dispose();
    }

    public void setFill(FlatColor fill) {
        this.fill = fill;
    }
}
