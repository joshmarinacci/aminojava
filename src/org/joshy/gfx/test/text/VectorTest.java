package org.joshy.gfx.test.text;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.stage.Stage;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;

public class VectorTest implements Runnable {
    private BufferedImage buff;
    private Graphics2D g;

    public static void main(String ... args) throws Exception {
        Core.setUseJOGL(true);
        Core.init();
        Core.getShared().defer(new VectorTest());
    }

    public void run() {
        buff = new BufferedImage(100,100,BufferedImage.TYPE_INT_ARGB);
        g = buff.createGraphics();
        Stage stage = Stage.createStage();
        org.joshy.gfx.node.layout.Panel panel = new org.joshy.gfx.node.layout.Panel();
        panel.add(new GLNode());
        stage.setContent(panel);
    }
    public void p(int type, double[] coords) {
        switch(type) {
            case PathIterator.SEG_MOVETO: p("moveto"); break;
            case PathIterator.SEG_LINETO: p("lineto"); break;
            case PathIterator.SEG_QUADTO: p("quadto"); break;
            case PathIterator.SEG_CUBICTO: p("cubicto"); break;
            case PathIterator.SEG_CLOSE: p("close"); break;
        }
    }

    public static void p(String s) {
        //System.out.println(s);
    }

    private class GLNode extends Node {
        @Override
        public void draw(GFX g) {
            if (g.isGL()) {
                GLAutoDrawable drawable = g.getDrawable();
                GL2 gl = drawable.getGL().getGL2();
                Graphics2D g2 = buff.createGraphics();
                drawVector(gl,g2);
                g2.dispose();
            }
        }

        @Override
        public Bounds getVisualBounds() {
            return new Bounds(0, 0, 300, 300);
        }

        @Override
        public Bounds getInputBounds() {
            return getVisualBounds();
        }
        
        private void drawVector(GL2 gl, Graphics2D g) {

            gl.glPushMatrix();
            gl.glColor3d(0.5, 1.0, 0.5);
            gl.glTranslated(200, 200, 0);
            gl.glBegin(GL.GL_LINE_STRIP);
            gl.glVertex2f(0, 0);

            gl.glVertex2d(50,50);
            gl.glVertex2d(0, 0);
            gl.glEnd();
            gl.glPopMatrix();



            String text = "&";
            g.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN,200));
            FontRenderContext frc = g.getFontRenderContext();
            GlyphVector vector = g.getFont().createGlyphVector(frc,text);
            p("--------------");
            gl.glPushMatrix();
            gl.glColor3d(1.0, 0.0, 0.0);
            gl.glTranslated(200, 200, 0);
            gl.glBegin(GL.GL_LINE_STRIP);

            for(int i=0; i<vector.getNumGlyphs(); i++) {
                p("--");
                Shape glyphShape = vector.getGlyphOutline(i);
                PathIterator it = glyphShape.getPathIterator(new AffineTransform());
                it = new FlatteningPathIterator(it,0.5);
                double[] coords = new double[6];
                double px = 0;
                double py = 0;
                double mx = 0;
                double my = 0;
                int pointCount = 0;
                while(true) {
                    if(it.isDone()) break;
                    int type = it.currentSegment(coords);
                    p(type,coords);
                    if(type == PathIterator.SEG_LINETO) {
                        //g.drawLine((int)px,(int)py, (int)coords[0], (int)coords[1]);
                        gl.glVertex2d(coords[0],coords[1]);
                        px = coords[0];
                        py = coords[1];
                    }
                    if(type == PathIterator.SEG_MOVETO) {
                        px = coords[0];
                        py = coords[1];
                        mx = px;
                        my = py;
                        gl.glVertex2d(mx,my);
                    }
                    if(type == PathIterator.SEG_CLOSE) {
                        //g.drawLine((int)px,(int)py, (int)mx, (int)my);
                        gl.glVertex2d(mx,my);
                    }
                    pointCount++;
                    it.next();
                }
                p("-- count = " + pointCount);
            }
            gl.glEnd();
            gl.glPopMatrix();

            p("--------------");

        }

    }
}
