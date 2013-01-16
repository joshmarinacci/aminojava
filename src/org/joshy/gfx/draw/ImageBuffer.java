package org.joshy.gfx.draw;

import org.joshy.gfx.stage.swing.SwingGFX;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Aug 5, 2010
 * Time: 11:25:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImageBuffer {
    private int width;
    private int height;
    public BufferedImage buf;

    public ImageBuffer(int width, int height) {
        this.width = width;
        this.height = height;
        buf = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
    }

    public void apply(Effect effect) {
        effect.apply(this);
        //To change body of created methods use File | Settings | File Templates.
    }

    public void clear() {
        Graphics2D g2 = buf.createGraphics();
        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(0,0,buf.getWidth(),buf.getHeight());
        g2.setComposite(AlphaComposite.SrcOver);
        g2.dispose();
    }

    public GFX getGFX() {
        return new SwingGFX(buf.createGraphics());
    }
}
