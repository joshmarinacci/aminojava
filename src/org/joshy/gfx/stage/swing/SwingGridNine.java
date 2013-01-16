package org.joshy.gfx.stage.swing;

import org.joshy.gfx.draw.GridNine;

import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 20, 2010
 * Time: 5:43:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class SwingGridNine extends GridNine {
    private BufferedImage img;

    public SwingGridNine(BufferedImage img, double top, double right, double bottom, double left, boolean flipx) {
        super(top, right, bottom, left, flipx);
        this.img = img;
    }
    public BufferedImage getImage() {
        return img;
    }
}
