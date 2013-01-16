package org.joshy.gfx.draw;

import org.joshy.gfx.Core;
import org.joshy.gfx.stage.jogl.JOGLGridNine;
import org.joshy.gfx.stage.swing.SwingGridNine;

import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 18, 2010
 * Time: 3:12:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class GridNine {
    private double top;
    private double right;
    private double bottom;
    private double left;
    private boolean flipx;


    public static GridNine create(BufferedImage image, double top, double right, double bottom, double left) {
        return create(image,top,right,bottom,left,false);
    }

    public static GridNine create(BufferedImage image, double top, double right, double bottom, double left, boolean flipx) {
        if(Core.getShared().isUseJOGL()) {
            return new JOGLGridNine(image, top, right, bottom, left, flipx);
        } else {
            return new SwingGridNine(image, top, right, bottom, left, flipx);
        }
    }

    protected GridNine(double top, double right, double bottom, double left, boolean flipx) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
        this.flipx = flipx;
    }


    public double getTop() {
        return top;
    }

    public double getRight() {
        return right;
    }

    public double getBottom() {
        return bottom;
    }

    public double getLeft() {
        return left;
    }

    public boolean isFlipX() {
        return flipx;
    }
}
