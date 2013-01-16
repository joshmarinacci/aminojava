package org.joshy.gfx.draw;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 18, 2010
 * Time: 2:08:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class FlatColor implements Paint {
    private double red;
    private double green;
    private double blue;
    private double alpha;
    public static final FlatColor WHITE = new FlatColor(0xFFFFFF);
    public static final FlatColor BLACK = new FlatColor(0x000000);
    public static final FlatColor RED = new FlatColor(0xff0000);
    public static final FlatColor BLUE = new FlatColor(0x0000ff);
    public static final FlatColor GREEN = new FlatColor(0x00ff00);
    public static final FlatColor YELLOW = new FlatColor(0xffff00);
    public static final FlatColor PURPLE = new FlatColor(0xff00ff);
    public static final FlatColor GRAY = new FlatColor(0xa0a0a0);
    public static final FlatColor WHITE_TRANSPARENT = new FlatColor(0x00FFFFFF,true);
    public static final FlatColor BLACK_TRANSPARENT = new FlatColor(0x00000000,true);

    public FlatColor(String srgb) {
        if(srgb.startsWith("#")) {
            srgb = srgb.substring(1);
        }

        alpha = 1;
        if(srgb.length() == 8) {
            String alphaString = srgb.substring(0,2);
            srgb = srgb.substring(2);
            alpha = ((double)(Integer.valueOf(alphaString,16)+1))/256;
        }
        int rgb = Integer.valueOf(srgb,16);
        Color c = new Color(rgb,false);
        float[]comps = c.getComponents(null);
        red = comps[0];
        green = comps[1];
        blue = comps[2];
    }

    public FlatColor(int rgb) {
        Color c = new Color(rgb,false);
        float[]comps = c.getComponents(null);
        red = comps[0];
        green = comps[1];
        blue = comps[2];
        alpha = 1;

    }
    public FlatColor(int argb, boolean hasAlpha) {
        Color c = new Color(argb,hasAlpha);
        float[]comps = c.getComponents(null);
        red = comps[0];
        green = comps[1];
        blue = comps[2];
        alpha = comps[3];
    }
    public FlatColor(int rgb, double alpha) {
        Color c = new Color(rgb,false);
        float[]comps = c.getComponents(null);
        red = comps[0];
        green = comps[1];
        blue = comps[2];
        this.alpha = alpha;
    }
    
    public FlatColor(double red, double green, double blue, double alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public FlatColor(String s, double alpha) {
        this(s);
        this.alpha = alpha; 
    }

    public int getRGBA() {
        return new java.awt.Color((float) getRed(),(float) getGreen(),(float) getBlue(),(float) getAlpha()).getRGB();
    }

    public double getRed() {
        return red;
    }

    public double getGreen() {
        return green;
    }

    public double getBlue() {
        return blue;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getHue() {
        java.awt.Color col = new java.awt.Color((float) getRed(), (float) getGreen(), (float) getBlue(), (float) getAlpha());
        float[] comps = java.awt.Color.RGBtoHSB(col.getRed(), col.getGreen(), col.getBlue(), null);
        return comps[0]*360;
    }
    public double getSaturation() {
        java.awt.Color col = new java.awt.Color((float) getRed(), (float) getGreen(), (float) getBlue(), (float) getAlpha());
        float[] comps = java.awt.Color.RGBtoHSB(col.getRed(), col.getGreen(), col.getBlue(), null);
        return comps[1];
    }
    public double getBrightness() {
        java.awt.Color col = new java.awt.Color((float) getRed(), (float) getGreen(), (float) getBlue(), (float) getAlpha());
        float[] comps = java.awt.Color.RGBtoHSB(col.getRed(), col.getGreen(), col.getBlue(), null);
        return comps[2];
    }

    @Override
    public String toString() {
        return "FlatColor{" + Integer.toHexString(getRGBA())+"}";
    }

    /**
     * 
     */
    public static FlatColor hsb(double hueAngle, double saturation, double value) {
        int rgb = Color.HSBtoRGB((float) hueAngle/360, (float) saturation, (float) value);
        return new FlatColor(rgb);
    }
    public static FlatColor hsb(double hueAngle, double saturation, double value, double alpha) {
        int rgb = Color.HSBtoRGB((float) hueAngle/360, (float) saturation, (float) value);
        return new FlatColor(rgb,alpha);
    }

    public FlatColor deriveWithAlpha(double alpha) {
        return new FlatColor(this.red,this.green,this.blue,alpha);
    }

    public static FlatColor fromRGBInts(int r, int g, int b) {
        return new FlatColor(r/255.0,g/255.0,b/255.0, 1.0);
    }

    @Override
    public Paint duplicate() {
        return  new FlatColor(red,green,blue,alpha);
    }
}
