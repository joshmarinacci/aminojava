package org.joshy.gfx.text;

import com.sun.opengl.util.texture.awt.AWTTextureIO;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellator;
import javax.media.opengl.glu.GLUtessellatorCallback;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphMetrics;
import java.awt.font.GlyphVector;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class PackedFont extends JFont {
    private ArrayList<Rectangle2D> textureMetrics;
    private int maxAscent;
    private BufferedImage buffer;

    public PackedFont(String name, double size, boolean vector, Font font) {
        super(name, size, vector, font);
    }




    @Override
    protected void init() {
        if(texture == null) {
            System.out.println("generating texture");
            buffer = new BufferedImage(256,256,BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = buffer.createGraphics();

            //turn on text AA
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS , RenderingHints.VALUE_FRACTIONALMETRICS_OFF);

            //clear background
            g.setPaint(new Color(255, 255, 255, 0));
            g.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
            g.setPaint(Color.BLACK);
            g.drawRect(0,0,buffer.getWidth()-1,buffer.getHeight()-1);

            //set the color and jogltext.font
            g.setPaint(new Color(255, 255, 255));
            g.setFont(font);

            textureMetrics = new ArrayList<Rectangle2D>();
            char[] charSet = new char[((int)'~') - ((int)' ') + 1];
            int count = 0;
            for(char ch = ' '; ch <= '~'; ch++) {
                charSet[count] = ch;
                count++;
            }
            FontRenderContext frc = g.getFontRenderContext();

            FontMetrics fontMetrics = g.getFontMetrics(font);
            maxAscent = fontMetrics.getMaxAscent();
            float maxDescent = fontMetrics.getMaxDescent();
//            p("max height = " + maxAscent + " max descent = " + maxDescent);

            float maxHeight = maxAscent + maxDescent + 5;

            GlyphVector vector = font.createGlyphVector(frc, charSet);
            g.translate(0,maxHeight);
            float totalAdvance = 0;
            float y = 0;
            for(int i=0; i<vector.getNumGlyphs(); i++) {
                GlyphMetrics metrics = vector.getGlyphMetrics(i);
                float advance = metrics.getAdvance();
                if(totalAdvance + advance > 256) {
                    g.translate(-totalAdvance,maxHeight);
                    totalAdvance = 0;
                    y+=maxHeight;
                }

                Rectangle2D pt = new Rectangle2D.Double(totalAdvance,y+5,advance,maxHeight);
//                p(charSet[i] + " pt = " + pt + " advance = " + advance);
                totalAdvance += advance;
                textureMetrics.add(pt);
                Shape bounds = vector.getGlyphVisualBounds(i);
                //g.draw(bounds);
                Shape glyphShape = vector.getGlyphOutline(i);
                g.fill(glyphShape);
            }

            g.dispose();
            texture = AWTTextureIO.newTexture(buffer,false);
        }

    }

    @Override
    Dimension2D getCharLayoutDimension(int ch) {
        final Rectangle2D r =  textureMetrics.get(ch);
        return new Dimension2D() {
            @Override
            public double getWidth() {
                return r.getWidth();
            }
            @Override
            public double getHeight() {
                return r.getHeight();
            }
            @Override
            public void setSize(double width, double height) {
                // no op
            }
        };
    }

    @Override
    Rectangle2D getCharTextureBounds(int ch) {
        Rectangle2D r =  textureMetrics.get(ch);
        return new Rectangle2D.Double(
                r.getX()/256.0,
                r.getY()/256.0,
                r.getWidth()/256.0,
                r.getHeight()/256.0);
    }

    @Override
    int getMaxAscent() {
        return maxAscent;
    }

    //double angle = 0;

    @Override
    protected void drawVector(GL2 gl, String text) {
        //angle+=1;
        //tessTest(gl);
        //lineVector(gl, text);
        fillVector(gl, text);
    }

    private void fillVector(GL2 gl, String text) {
        //create a tesselator
        GLUtessellator tobj = GLU.gluNewTess();
        GLU glu = new GLU();
        TessCallback tessCallback = new TessCallback(gl,glu);
        GLU.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);
        GLU.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);
        GLU.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);
        GLU.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);


        Graphics2D g = buffer.createGraphics();
        //System.out.println("filling vector using the font: " + font.getFontName() + " size = " + font.getSize2D());
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
        gl.glColor3d(red, green, blue);
        //gl.glColor3d(0.0, 0.0, 0.0);
        //gl.glRotated(angle, 0.0f, 1.0f, 0.0f);

        //gl.glBegin(GL.GL_LINE_STRIP);

        for(int i=0; i<vector.getNumGlyphs(); i++) {
            Shape glyphShape = vector.getGlyphOutline(i);
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

    private void lineVector(GL2 gl, String text) {
        Graphics2D g = buffer.createGraphics();
        g.setFont(new Font("Arial", Font.PLAIN,(int)this.size));
        FontRenderContext frc = g.getFontRenderContext();
        GlyphVector vector = g.getFont().createGlyphVector(frc,text);
        gl.glPushMatrix();
        p("setting color to " + red + " " + green + " " + blue);
        gl.glColor3d(red, green, blue);
        //gl.glColor3d(1.0, 0.0, 0.0);
        //gl.glRotated(angle, 0.0f, 1.0f, 0.0f);
        gl.glBegin(GL.GL_LINE_STRIP);

        for(int i=0; i<vector.getNumGlyphs(); i++) {
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
                if(type == PathIterator.SEG_LINETO) {
                    gl.glVertex2d(coords[0],coords[1]);
                    px = coords[0];
                    py = coords[1];
                }
                if(type == PathIterator.SEG_MOVETO) {
                    px = coords[0];
                    py = coords[1];
                    mx = px;
                    my = py;
                    gl.glBegin(GL.GL_LINE_STRIP);
                    gl.glVertex2d(mx,my);
                }
                if(type == PathIterator.SEG_CLOSE) {
                    gl.glVertex2d(mx,my);
                    gl.glEnd();
                }
                pointCount++;
                it.next();
            }
        }
        gl.glPopMatrix();
        g.dispose();
    }

    private void tessTest(GL2 gl) {
        //tesselation test
        double rect[][] = new double[][]
        { // [4][3] in C; reverse here
        {  50.0, 50.0, 0.0 },
        { 200.0, 50.0, 0.0 },
        { 200.0, 200.0, 0.0 },
        {  50.0, 200.0, 0.0 },
        { 100.0, 100.0, 0.0 },
        {   0.0, 100.0, 0.0 }
        };
        double tri[][] = new double[][]
        {// [3][3]
        { 75.0, 75.0, 0.0 },
        { 125.0, 175.0, 0.0 },
        { 175.0, 75.0, 0.0 } };

        //tesselation stuff
        GLUtessellator tobj = GLU.gluNewTess();
        GLU glu = new GLU();
        TessCallback tessCallback = new TessCallback(gl,glu);
        GLU.gluTessCallback(tobj, GLU.GLU_TESS_VERTEX, tessCallback);
        GLU.gluTessCallback(tobj, GLU.GLU_TESS_BEGIN, tessCallback);
        GLU.gluTessCallback(tobj, GLU.GLU_TESS_END, tessCallback);
        GLU.gluTessCallback(tobj, GLU.GLU_TESS_ERROR, tessCallback);

        //draw rect with a triangle hole in it
        GLU.gluTessBeginPolygon(tobj,null);
        GLU.gluTessBeginContour(tobj);
        for(int i=0; i<rect.length; i++) {
            GLU.gluTessVertex(tobj, rect[i], 0, rect[i]);
        }
        /*
        GLU.gluTessVertex(tobj, rect[0], 0, rect[0]);
        GLU.gluTessVertex(tobj, rect[1], 0, rect[1]);
        GLU.gluTessVertex(tobj, rect[2], 0, rect[2]);
        GLU.gluTessVertex(tobj, rect[3], 0, rect[3]);*/
        GLU.gluTessEndContour(tobj);
        /*
        GLU.gluTessBeginContour(tobj);
        GLU.gluTessVertex(tobj, tri[0], 0, tri[0]);
        GLU.gluTessVertex(tobj, tri[1], 0, tri[1]);
        GLU.gluTessVertex(tobj, tri[2], 0, tri[2]);
        GLU.gluTessEndContour(tobj);*/
        GLU.gluTessEndPolygon(tobj);
        GLU.gluDeleteTess(tobj);
    }

    private class TessCallback implements GLUtessellatorCallback {
        private GL2 gl;
        private GLU glu;

        private TessCallback(GL2 gl, GLU glu) {
            this.gl = gl;
            this.glu = glu;
        }

        public void begin(int type) {
            gl.glBegin(type);
        }

        public void beginData(int i, Object o) {
        }

        public void edgeFlag(boolean b) {
        }

        public void edgeFlagData(boolean b, Object o) {
        }

        public void vertex(Object vertexData) {
            double[] pointer;
            if (vertexData instanceof double[])
            {
                pointer = (double[]) vertexData;
                if (pointer.length == 6) gl.glColor3dv(pointer, 3);
                gl.glVertex3dv(pointer, 0);
            }
        }

        public void vertexData(Object o, Object o1) {
        }

        public void end() {
            gl.glEnd();
        }

        public void endData(Object o) {
        }

        public void combine(double[] coords, Object[] data, float[] weight, Object[] outData) {
            double[] vertex = new double[6];
            int i;

            vertex[0] = coords[0];
            vertex[1] = coords[1];
            vertex[2] = coords[2];
            for (i = 3; i < 6/* 7OutOfBounds from C! */; i++)
                vertex[i] = weight[0] //
                        * ((double[]) data[0])[i] + weight[1]
                        * ((double[]) data[1])[i] + weight[2]
                        * ((double[]) data[2])[i] + weight[3]
                        * ((double[]) data[3])[i];
            outData[0] = vertex;
        }

        public void combineData(double[] doubles, Object[] objects, float[] floats, Object[] objects1, Object o) {
        }

        public void error(int errnum) {
            String estring;

            estring = glu.gluErrorString(errnum);
            System.err.println("Tessellation Error: " + estring);
            System.exit(0);
        }

        public void errorData(int i, Object o) {
        }
    }


    private void p(int type, double[] coords) {
        switch(type) {
            case PathIterator.SEG_MOVETO: p("moveto"); break;
            case PathIterator.SEG_LINETO: p("lineto"); break;
            case PathIterator.SEG_QUADTO: p("quadto"); break;
            case PathIterator.SEG_CUBICTO: p("cubicto"); break;
            case PathIterator.SEG_CLOSE: p("close"); break;
        }
    }

}
