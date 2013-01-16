package org.joshy.gfx.util;

import org.joshy.gfx.draw.*;
import org.joshy.gfx.draw.Image;
import org.joshy.gfx.stage.swing.SwingImage;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Feb 1, 2010
 * Time: 2:51:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class GraphicsUtil {
    public static void fillLeftArrow(GFX g, double xoff, double yoff, double width) {
        g.translate(xoff,yoff);
        double[] points = new double[] {0,width/2, width,0, width,width};
        g.fillPolygon(points);
        g.translate(-xoff,-yoff);
    }

    public static void fillRightArrow(GFX g, double xoff, double yoff, double width) {
        g.translate(xoff,yoff);
        double[] points = new double[] {width,width/2, 0,0, 0,width};
        g.fillPolygon(points);
        g.translate(-xoff,-yoff);
    }

    public static void fillUpArrow(GFX g, double xoff, double yoff, double width) {
        g.translate(xoff,yoff);
        double[] points = new double[] {width/2,0,  width,width, 0,width};
        g.fillPolygon(points);
        g.translate(-xoff,-yoff);
    }

    public static void fillDownArrow(GFX g, double xoff, double yoff, double width) {
        g.translate(xoff,yoff);
        double[] points = new double[] {0,0, width,0, width/2,width};
        g.fillPolygon(points);
        g.translate(-xoff,-yoff);
    }

    public static java.awt.Color toAWT(FlatColor color) {
        return new Color((float)color.getRed(),(float)color.getGreen(),(float)color.getBlue(),(float)color.getAlpha());
    }

    public static FlatColor randomColor() {
        return FlatColor.hsb(Math.random()*360.0,1,1);
    }

    public static BufferedImage toAWT(Image bitmap) {
        return ((SwingImage)bitmap).buffer;
    }
}
