package org.joshy.gfx.stage.swing;

import org.joshy.gfx.draw.Paint;
import org.joshy.gfx.draw.PatternPaint;

import javax.imageio.ImageIO;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 27, 2010
 * Time: 11:05:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class SwingPatternPaint extends PatternPaint {
    BufferedImage image;
    private Point2D start = new Point2D.Double(0,0);
    private Point2D end;
    private String relativeURL;

    public SwingPatternPaint(File file) throws IOException {
        this(ImageIO.read(file));
        relativeURL = file.getName();
    }

    public SwingPatternPaint(URL resource, String relativeURL) throws IOException {
        this(ImageIO.read(resource));
        this.relativeURL = relativeURL;
    }

    private SwingPatternPaint(BufferedImage img) {
        this.image = img;
        this.start = new Point2D.Double(0,0);
        this.end = new Point2D.Double(image.getWidth(),image.getHeight());
    }

    public SwingPatternPaint(BufferedImage image, Point2D start, Point2D end) {
        this.image = image;
        this.start = start;
        this.end = end;
    }

    public SwingPatternPaint(BufferedImage img, String relativeURL) {
        this.image = img;
        this.relativeURL = relativeURL;
        this.start = new Point2D.Double(0,0);
        this.end = new Point2D.Double(image.getWidth(),image.getHeight());
    }

    public SwingPatternPaint(BufferedImage img, int width, int height) {
        this.image = img;
        this.relativeURL = null;
        this.start = new Point2D.Double(0,0);
        this.end = new Point2D.Double(width,height);
    }

    @Override
    public Paint duplicate() {
        SwingPatternPaint paint = new SwingPatternPaint(image, getStart(), getEnd());
        paint.relativeURL = this.relativeURL;
        return paint;
    }

    @Override
    public Point2D getStart() {
        return start;
    }

    @Override
    public Point2D getEnd() {
        return end;
    }

    @Override
    public PatternPaint deriveNewStart(Point2D newPoint) {
        SwingPatternPaint p = new SwingPatternPaint(this.image);
        p.start = newPoint;
        p.end = this.end;
        p.relativeURL = this.relativeURL;
        return p;
    }

    @Override
    public PatternPaint deriveNewEnd(Point2D newPoint) {
        SwingPatternPaint p = new SwingPatternPaint(this.image);
        p.start = this.start;
        p.end = newPoint;
        p.relativeURL = this.relativeURL;
        return p;
    }

    @Override
    public BufferedImage getImage() {
        return image;
    }

    @Override
    public String getRelativeURL() {
        return this.relativeURL;
    }
}
