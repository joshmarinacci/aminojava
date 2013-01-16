package org.joshy.gfx.stage.jogl;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.awt.AWTTextureIO;
import org.joshy.gfx.draw.Paint;
import org.joshy.gfx.draw.PatternPaint;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 27, 2010
 * Time: 11:05:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class JOGLPatternPaint extends PatternPaint {
    BufferedImage image;
    private boolean initialized;
    Texture texture;
    private String relativeURL;

    public JOGLPatternPaint(File file) throws IOException {
        this.image = ImageIO.read(file);
        this.relativeURL = file.getName();
        initialized = false;
    }

    public JOGLPatternPaint(URL resource, String relativeURL) throws IOException {
        this.image = ImageIO.read(resource);
        this.relativeURL = relativeURL;
        initialized = false;
    }
    public JOGLPatternPaint(BufferedImage img) {
        this.image = img;
        initialized = false;
    }

    public JOGLPatternPaint(BufferedImage img, String relativeURL) {
        this.image = img;
        initialized = false;
        this.relativeURL = relativeURL;
    }

    public void initialize() {
        if(!initialized) {
            initialized = true;
            texture = AWTTextureIO.newTexture(image,true);
            texture.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
            texture.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
            texture.setTexParameteri(GL.GL_TEXTURE_WRAP_S,GL.GL_REPEAT);
            texture.setTexParameteri(GL.GL_TEXTURE_WRAP_T,GL.GL_REPEAT);
        }
    }

    @Override
    public Paint duplicate() {
        return new JOGLPatternPaint(image);
    }

    @Override
    public Point2D getStart() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Point2D getEnd() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PatternPaint deriveNewStart(Point2D pt) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PatternPaint deriveNewEnd(Point2D pt) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public BufferedImage getImage() {
        return this.image;
    }

    @Override
    public String getRelativeURL() {
        return this.relativeURL;
    }
}
