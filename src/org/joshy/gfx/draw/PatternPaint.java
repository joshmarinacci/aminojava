package org.joshy.gfx.draw;

import org.joshy.gfx.Core;
import org.joshy.gfx.stage.jogl.JOGLPatternPaint;
import org.joshy.gfx.stage.swing.SwingPatternPaint;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 26, 2010
 * Time: 9:16:24 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class PatternPaint implements Paint {

    protected PatternPaint() {
    }

    public static PatternPaint create(File file) throws IOException {
        if(Core.getShared().isUseJOGL()) {
            return new JOGLPatternPaint(file);
        } else {
            return new SwingPatternPaint(file);
        }
    }

    public static PatternPaint create(URL resource, String relativeURL) throws IOException {
        if(Core.getShared().isUseJOGL()) {
            return new JOGLPatternPaint(resource, relativeURL);
        } else {
            return new SwingPatternPaint(resource,relativeURL);
        }
    }

    public static PatternPaint create(BufferedImage img, String relativeURL) throws IOException {
        if(Core.getShared().isUseJOGL()) {
            return new JOGLPatternPaint(img, relativeURL);
        } else {
            return new SwingPatternPaint(img,relativeURL);
        }
    }

    public static PatternPaint create(BufferedImage img, int width, int height) throws IOException {
        if(Core.getShared().isUseJOGL()) {
            return new JOGLPatternPaint(img, null);
        } else {
            return new SwingPatternPaint(img,width,height);
        }
    }

    public abstract Point2D getStart();
    public abstract Point2D getEnd();

    public abstract PatternPaint deriveNewStart(Point2D pt);
    public abstract PatternPaint deriveNewEnd(Point2D pt);

    public abstract BufferedImage getImage();
    public abstract String getRelativeURL();
}
