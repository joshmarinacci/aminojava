package org.joshy.gfx.css.values;

import java.util.ArrayList;
import java.util.List;

/** Represents a linear gradient specifed with CSS
 */
public class LinearGradientValue extends BaseValue{
    private String pos1;
    private String pos2;
    private ArrayList<GradientStopValue> stops;

    public LinearGradientValue(String pos1, String pos2) {
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.stops = new ArrayList<GradientStopValue>();
    }

    @Override
    public String asString() {
        return "linear-gradient: " + pos1 + " " + pos2 + " stops = " + stops.size();
    }

    public void addStops(List<GradientStopValue> gradientStopValues) {
        this.stops.addAll(gradientStopValues);
    }

    public GradientStopValue getStop(int i) {
        return stops.get(i);
    }

    public String getPosition1() {
        return pos1;
    }
}
