package org.joshy.gfx.css.values;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Jul 31, 2010
 * Time: 5:00:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class GradientStopValue extends BaseValue {
    private int color;
    private double offset;

    public GradientStopValue(String hex, String per) {
        this.color = Integer.parseInt(hex.substring(1),16);
        this.offset = Double.parseDouble(per);
    }

    @Override
    public String asString() {
        return "gradient stop: " + color + " " + offset + "%";
    }

    public int getColor() {
        return color;
    }
}
