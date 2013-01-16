package org.joshy.gfx.draw.effects;

import org.joshy.gfx.draw.Effect;
import org.joshy.gfx.draw.ImageBuffer;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: Aug 5, 2010
* Time: 11:41:55 PM
* To change this template use File | Settings | File Templates.
*/
public class BlurEffect extends Effect {
    float[] matrix = {
        0.111f, 0.111f, 0.111f,
        0.111f, 0.111f, 0.111f,
        0.111f, 0.111f, 0.111f,
    };
    private int radius;

    public BlurEffect(int radius) {
        this.radius = radius;
        matrix = new float[radius*radius];
        for(int i=0; i<radius*radius; i++) {
            matrix[i] = 1f/((float)(radius*radius));
        }
    }

    @Override
    public void apply(ImageBuffer buf) {
        if(radius == 0) return;
        BufferedImageOp op = new ConvolveOp( new Kernel(radius, radius, matrix) );
        BufferedImage blurredImage = op.filter(buf.buf,null);
        buf.buf = blurredImage;
    }
}
