package org.joshy.gfx.stage.swing;

import org.joshy.gfx.draw.Image;

import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 21, 2010
 * Time: 8:47:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class SwingImage extends Image {
    public BufferedImage buffer;

    public SwingImage(BufferedImage buffer) {
        super();
        this.buffer = buffer;
    }

    @Override
    public int getWidth() {
        return buffer.getWidth();
    }

    @Override
    public int getHeight() {
        return buffer.getHeight();
    }
}
