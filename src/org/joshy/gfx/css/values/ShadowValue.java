package org.joshy.gfx.css.values;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Aug 6, 2010
 * Time: 5:19:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class ShadowValue extends BaseValue {
    private int color;
    private int xoffset;
    private int yoffset;
    private int blurRadius;

    public ShadowValue(String color, String xoff, String yoff, String radius) {
        this.color = Integer.parseInt(color.substring(1),16);
        this.xoffset = Integer.parseInt(xoff.substring(0,xoff.length()-2));
        this.yoffset = Integer.parseInt(yoff.substring(0,yoff.length()-2));
        this.blurRadius = Integer.parseInt(radius.substring(0,radius.length()-2));
    }

    @Override
    public String asString() {
        return "shadow value with stuff";
    }

    public int getXoffset() {
        return xoffset;
    }

    public int getYoffset() {
        return yoffset;
    }

    public int getBlurRadius() {
        return blurRadius;
    }

    public int getColor() {
        return color;
    }
}
