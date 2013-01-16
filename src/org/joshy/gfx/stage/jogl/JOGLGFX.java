package org.joshy.gfx.stage.jogl;

import com.sun.opengl.util.texture.TextureCoords;
import java.awt.Shape;
import org.joshy.gfx.draw.*;
import org.joshy.gfx.node.Bounds;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import java.awt.geom.Path2D;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 21, 2010
 * Time: 4:11:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class JOGLGFX extends GFX {
    private GLAutoDrawable drawable;
    private Paint paint;
    private GL2 gl;
    private double tx;
    private double ty;
    private static final boolean TEXT_ENABLED = true;

    public JOGLGFX(GLAutoDrawable drawable) {
        this.drawable = drawable;
        gl = drawable.getGL().getGL2();
    }

    @Override
    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    @Override
    public void dispose() {
        // not needed
    }

    @Override
    public void drawRect(double x, double y, double width, double height) {
        setPaint();
        gl.glTranslatef((float) x, (float) y, 0);
        gl.glBegin(GL.GL_LINE_STRIP);
        gl.glVertex2f(0, 0);
        gl.glVertex2d(width, 0);
        gl.glVertex2d(width, height);
        gl.glVertex2d(0, height);
        gl.glVertex2d(0, 0);
        gl.glEnd();
        gl.glTranslatef(-(float) x, -(float) y, 0);
    }


    @Override
    public void fillRect(double x, double y, double width, double height) {
        gl.glPushMatrix();
        gl.glTranslatef((float) x, (float) y, 0f);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        setPaint();

        if (paint instanceof JOGLPatternPaint) {
            JOGLPatternPaint pp = (JOGLPatternPaint) paint;
            pp.initialize();
            TextureCoords tc = pp.texture.getImageTexCoords();

            //gl.glPushMatrix();
            //set bg color to white
            gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

            //enable the texture
            pp.texture.enable();
            gl.glEnable(GL2.GL_ALPHA_TEST);
            gl.glAlphaFunc(GL.GL_GREATER, 0);
            gl.glEnable(GL.GL_TEXTURE_2D);
            gl.glTexEnvf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
            pp.texture.bind();

            double tw = tc.right() * width / ((double) pp.image.getWidth());
            double th = tc.bottom() * height / ((double) pp.image.getHeight());
            //draw the real rect
            gl.glBegin(GL2.GL_QUADS);
            gl.glTexCoord2d(tc.left(), tc.top());
            gl.glVertex3d(0, 0, 0);
            gl.glTexCoord2d(tw, tc.top());
            gl.glVertex3d(width, 0, 0);
            gl.glTexCoord2d(tw, th);
            gl.glVertex3d(width, height, 0);
            gl.glTexCoord2d(tc.left(), th);
            gl.glVertex3d(0, height, 0);
            gl.glEnd();

            //cleanup
            pp.texture.disable();

        } else {

            gl.glBegin(GL2.GL_QUADS);
            gl.glVertex3d(0, 0, 0);
            gl.glVertex3d(width, 0, 0);
            gl.glVertex3d(width, height, 0);
            gl.glVertex3d(0, height, 0);
            gl.glEnd();
        }

        //full cleanup
        gl.glPopMatrix();
    }

    @Override
    public void drawPolygon(double[] points) {
        setPaint();
        gl.glBegin(GL.GL_LINE_STRIP);
        for (int i = 0; i < points.length; i += 2) {
            gl.glVertex2d(points[i], points[i + 1]);
        }
        //repeat the last point
        gl.glVertex2d(points[0], points[1]);
        gl.glEnd();
    }

    @Override
    public void drawPolygon(double[] points, boolean closed) {
        drawPolygon(points);
    }

    @Override
    public void fillPolygon(double[] points) {
        setPaint();
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glBegin(GL.GL_TRIANGLE_FAN);
        for (int i = 0; i < points.length; i += 2) {
            gl.glVertex2d(points[i], points[i + 1]);
        }
        //repeat the last point
        gl.glVertex2d(points[0], points[1]);
        gl.glEnd();
    }

    @Override
    public void fillRoundRect(double x, double y, double w, double h, double arcWidth, double arcHeight) {
        double aw = arcWidth / 2.0;
        double ah = arcHeight / 2.0;
        double rw = w - arcWidth;
        double rh = h - arcHeight;
        translate(x + aw, y + rh + ah);
        int s = 10; //sides per arc
        int qs = 4; //number of quadrants
        double[] p2 = new double[s * 2 * qs + 2 + 2];
        p2[0] = 0;
        p2[1] = 0;
        double xoff = 0;
        double yoff = 0;
        for (int q = 0; q < qs; q++) {
            xoff = 0;
            yoff = 0;
            if (q == 0) xoff = rw;
            if (q == 1) xoff = rw;
            if (q == 1) yoff = -rh;
            if (q == 2) yoff = -rh;
            for (int i = 0; i < s; i++) {
                int n = (i + 1) * 2 + (q * s) * 2;
                p2[n] = Math.sin(Math.toRadians(i * 10 + 90 * q)) * aw + xoff;
                p2[n + 1] = Math.cos(Math.toRadians(i * 10 + 90 * q)) * ah + yoff;
            }
        }
        p2[p2.length - 2] = rw;
        p2[p2.length - 1] = ah;
        fillFan(p2);
        translate(-x - aw, -y - rh - ah);
    }
    
    @Override
    public void fillRoundRectAltered(double x, double y, double width, double height, double ul, double ur, double lr, double ll) {
    }

    @Override
    public void fillCustomRoundRect(double x, double y, double w, double h, double ulw, double ulh, double urw, double urh, double lrw, double lrh, double llw, double llh) {

    }

    @Override
    public void drawCustomRoundRect(double x, double y, double w, double h, double ulw, double ulh, double urw, double urh, double lrw, double lrh, double llw, double llh) {
        
    }

    @Override
    public void drawPath(Path2D.Double path) {
    }

    @Override
    public void fillPath(Path2D.Double path) {
    }

    @Override
    public void setSmoothImage(boolean smooth) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setMask(Shape shape) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ImageBuffer createBuffer(int i, int i1) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void draw(ImageBuffer buf, double x, double y) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setStrokeWidth(double strokeWidth) {
        
    }

    @Override
    public double getOpacity() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setOpacity(double opacity) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setPureStrokes(boolean pureStrokes) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public void drawRoundRect(double x, double y, double w, double h, double arcWidth, double arcHeight) {
        double aw = arcWidth / 2.0;
        double ah = arcHeight / 2.0;
        double rw = w - arcWidth;
        double rh = h - arcHeight;
        translate(x + aw, y + rh + ah);
        int s = 10; //sides per arc
        int qs = 4; //number of quadrants
        double[] p2 = new double[s * 2 * qs + 2];
        //p2[0]=0;
        //p2[1]=0;
        double xoff = 0;
        double yoff = 0;
        for (int q = 0; q < qs; q++) {
            xoff = 0;
            yoff = 0;
            if (q == 0) xoff = rw;
            if (q == 1) xoff = rw;
            if (q == 1) yoff = -rh;
            if (q == 2) yoff = -rh;
            for (int i = 0; i < s; i++) {
                int n = (i) * 2 + (q * s) * 2;
                p2[n] = Math.sin(Math.toRadians(i * 10 + 90 * q)) * aw + xoff;
                p2[n + 1] = Math.cos(Math.toRadians(i * 10 + 90 * q)) * ah + yoff;
            }
        }
        p2[p2.length - 2] = rw;
        p2[p2.length - 1] = ah;
        drawPolygon(p2);
        translate(-x - aw, -y - rh - ah);
    }

    @Override
    public void fillCircle(double cx, double cy, double radius) {
        setPaint();
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glVertex2d(cx, cy);
        for (int i = 0; i <= 360; i += 5) {
            gl.glVertex2d
                    (cx + Math.sin(Math.toRadians(i)) * radius,
                            (cy + Math.cos(Math.toRadians(i)) * radius)
                    );
        }
        gl.glVertex2d(cx, cy);
        gl.glEnd();
    }

    @Override
    public void fillArc(double cx, double cy, double radius, double angleStart, double angleEnd) {
        setPaint();
        gl.glBegin(GL2.GL_TRIANGLE_FAN);
        gl.glVertex2d(cx, cy);
        for (int i = (int) angleStart; i <= angleEnd; i += 5) {
            gl.glVertex2d
                    (cx + Math.sin(Math.toRadians(i)) * radius,
                            (cy + Math.cos(Math.toRadians(i)) * radius)
                    );
        }
        gl.glVertex2d(cx, cy);
        gl.glEnd();
    }

    @Override
    public void drawGridNine(GridNine gridNine, double x, double y, double width, double height) {
        //u.p("JOGL:drawGridNine");

        JOGLGridNine g9 = (JOGLGridNine) gridNine;

        //init the g9 if needed (only done the first time)
        if (!g9.initialized) g9.initialize();

        TextureCoords tc = g9.texture.getImageTexCoords();

        gl.glPushMatrix();
        //translate
        //gl.glTranslatef( 100, 100, 0);
        //set bg color to white
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        //enable the texture
        g9.texture.enable();
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL2.GL_ALPHA_TEST);
        gl.glAlphaFunc(GL.GL_GREATER, 0);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glTexEnvf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
        //gl.glTexEnvi(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
        g9.texture.bind();

        //draw a rectangle
        gl.glTranslated(x, y, 0);

        //upper left quadrant
        double sx0 = 0;
        double sx1 = g9.getLeft();
        double sx2 = width - g9.getRight();
        double sx3 = width;
        double sy0 = 0;
        double sy1 = g9.getTop();
        double sy2 = height - g9.getBottom();
        double sy3 = height;

        double ix0 = 0;
        double ix1 = g9.getLeft() / g9.texture.getWidth();
        double ix2 = 1.0 - g9.getRight() / g9.texture.getWidth();
        double ix3 = 1.0;
        double iy0 = 0;
        double iy1 = g9.getTop() / g9.texture.getHeight();
        double iy2 = 1.0 - g9.getBottom() / g9.texture.getHeight();
        double iy3 = 1.0;

        if (gridNine.isFlipX()) {
            double[] temp = new double[4];
            temp[0] = sx0;
            temp[1] = sx1;
            temp[2] = sx2;
            temp[3] = sx3;
            sx0 = temp[3];
            sx1 = temp[2];
            sx2 = temp[1];
            sx3 = temp[0];
        }

        imageRect(sx0, sy0, sx1, sy1, ix0, iy0, ix1, iy1);
        imageRect(sx1, sy0, sx2, sy1, ix1, iy0, ix2, iy1);
        imageRect(sx2, sy0, sx3, sy1, ix2, iy0, ix3, iy1);

        imageRect(sx0, sy1, sx1, sy2, ix0, iy1, ix1, iy2);
        imageRect(sx1, sy1, sx2, sy2, ix1, iy1, ix2, iy2);
        imageRect(sx2, sy1, sx3, sy2, ix2, iy1, ix3, iy2);

        imageRect(sx0, sy2, sx1, sy3, ix0, iy2, ix1, iy3);
        imageRect(sx1, sy2, sx2, sy3, ix1, iy2, ix2, iy3);
        imageRect(sx2, sy2, sx3, sy3, ix2, iy2, ix3, iy3);

        g9.texture.disable();
        gl.glPopMatrix();
    }

    private void imageRect(
            double sx, double sy, double sx2, double sy2,
            double ix, double iy, double ix2, double iy2
    ) {
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2d(ix, iy);
        gl.glVertex3d(sx, sy, 0);
        gl.glTexCoord2d(ix2, iy);
        gl.glVertex3d(sx2, sy, 0);
        gl.glTexCoord2d(ix2, iy2);
        gl.glVertex3d(sx2, sy2, 0);
        gl.glTexCoord2d(ix, iy2);
        gl.glVertex3d(sx, sy2, 0);
        gl.glEnd();
    }

    @Override
    public void drawLine(double x, double y, double x2, double y2) {
        setPaint();
        gl.glBegin(GL.GL_LINE_STRIP);
        gl.glVertex2d(x, y);
        gl.glVertex2d(x2, y2);
        gl.glEnd();
    }

    @Override
    public void translate(double x, double y) {
        tx += x;
        ty += y;
        gl.glTranslated(x, y, 0);
    }

    @Override
    public void scale(double scaleX, double scaleY) {
        gl.glScaled(scaleX,scaleY,1.0);
    }

    @Override
    public void translate(double translateX, double translateY, double translateZ) {
        tx += translateX;
        ty += translateY;
        gl.glTranslated(translateX, translateY, translateZ);
    }

    @Override
    public void rotate(double rotation, Transform rotationAxis) {
        gl.glRotated(rotation, rotationAxis.getX(), rotationAxis.getY(), rotationAxis.getZ());
    }

    @Override
    public void push() {
        gl.glPushMatrix();
    }

    @Override
    public void pop() {
        gl.glPopMatrix();
    }

    @Override
    public boolean isGL() {
        return true;
    }

    @Override
    public GLAutoDrawable getDrawable() {
        return drawable;
    }

    @Override
    public void fillOval(double x, double y, double width, double height) {
    }
    
    @Override
    public void drawOval(double x, double y, double width, double height) {
    }


    @Override
    public void setClipRect(Bounds bounds) {
        if(bounds != null) {
            gl.glScissor((int) (tx+bounds.getX()),(int)(ty+bounds.getY()),(int)bounds.getWidth(),(int)bounds.getHeight());
            gl.glEnable(GL.GL_SCISSOR_TEST);
        } else {
            gl.glDisable(GL.GL_SCISSOR_TEST);
        }

    }

    @Override
    public Bounds getClipRect() {
        return null;
    }

    @Override
    public void drawText(String text, org.joshy.gfx.draw.Font fnt, double x, double y) {
        if(!TEXT_ENABLED) return;
        JOGLFont font = (JOGLFont) fnt;
        //first time init
        if (!font.initialized) {
            font.initialize();
        }

        if(font.PACKED_TEXT) {
            gl.glTranslated(x,y,0);
            if (paint instanceof FlatColor) {
                FlatColor c = (FlatColor) paint;
                font.packedFont.setColor(c.getRed(),c.getGreen(),c.getBlue());
            }

            font.packedFont.drawText(gl,text);
            gl.glTranslated(-x,-y,0);
            return;
        }

        //startValue
        font.tr.beginRendering(drawable.getWidth(), drawable.getHeight());

        //set the color
        setPaint();

        //draw the real text
        font.tr.draw(text, (int) (x + tx), drawable.getHeight() - (int) (y + ty));

        //clean up
        font.tr.endRendering();
    }

    @Override
    public void drawImage(Image img, double x, double y) {
        JOGLImage image = (JOGLImage) img;
        image.initialize();
        TextureCoords tc = image.texture.getImageTexCoords();

        gl.glPushMatrix();
        //translate
        //set bg color to white
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        //enable the texture
        image.texture.enable();
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_DST_ALPHA);
        gl.glEnable(GL2.GL_ALPHA_TEST);
        gl.glAlphaFunc(GL.GL_GREATER, 0);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glTexEnvf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
        image.texture.bind();

        //draw a rectangle
        gl.glTranslated(x, y, 0);
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2d(tc.left(), tc.top());
        gl.glVertex3d(0, 0, 0);
        gl.glTexCoord2d(tc.right(), tc.top());
        gl.glVertex3d(image.texture.getWidth(), 0, 0);
        gl.glTexCoord2d(tc.right(), tc.bottom());
        gl.glVertex3d(image.texture.getWidth(), image.texture.getHeight(), 0);
        gl.glTexCoord2d(tc.left(), tc.bottom());
        gl.glVertex3d(0, image.texture.getHeight(), 0);
        gl.glEnd();

        image.texture.disable();
        gl.glPopMatrix();
    }

    @Override
    public void drawImage(Image image, double dx, double dy, double dw, double dh, double sx, double sy, double sw, double sh) {
        
    }

    private void setPaint() {
        java.awt.Color cl = java.awt.Color.BLACK;
        if (paint instanceof FlatColor) {
            FlatColor c = (FlatColor) paint;
            gl.glColor4d(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
        }
    }

    private void println(String s) {
        System.out.println(s);
    }

    public void drawFan(double[] points) {
        setPaint();
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glBegin(GL.GL_LINE_STRIP);
        for (int i = 0; i < points.length; i += 2) {
            gl.glVertex2d(points[i], points[i + 1]);
        }
        //repeat the last point
        //gl.glVertex2d(points[0],points[1]);
        gl.glEnd();
    }

    public void fillFan(double[] points) {
        setPaint();

        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glBegin(GL.GL_TRIANGLE_FAN);
        for (int i = 0; i < points.length; i += 2) {
            gl.glVertex2d(points[i], points[i + 1]);
        }
        //repeat the last point
        gl.glVertex2d(points[0], points[1]);
        gl.glEnd();
    }
}
