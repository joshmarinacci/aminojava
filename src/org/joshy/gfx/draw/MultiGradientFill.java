package org.joshy.gfx.draw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The base class for all of the gradient paints.
 */
public abstract class MultiGradientFill implements Paint {
    protected List<Stop> stops = new ArrayList<Stop>();


    public MultiGradientFill addStop(double position, FlatColor color) {
        Stop stop = new Stop(position, color);
        stop.fill = this;
        stops.add(stop);
        sortStops();
        return this;
    }

    public MultiGradientFill addStop(Stop stop) {
        stop.fill = this;
        this.stops.add(stop);
        sortStops();
        return this;
    }

    public MultiGradientFill removeStop(Stop stop) {
        stop.fill = null;
        this.stops.remove(stop);
        sortStops();
        return this;
    }

    private void sortStops() {
        Collections.sort(this.stops);
    }

    public abstract MultiGradientFill translate(double x, double y);

    public int getStopCount() {
        return stops.size();
    }

    public List<Stop> getStops() {
        return stops;
    }

    public boolean isFirst(Stop stop) {
        return (stops.indexOf(stop) == 0);
    }

    public boolean isLast(Stop stop) {
        return (stops.indexOf(stop) == stops.size()-1);
    }

    public static class Stop implements Comparable<MultiGradientFill.Stop> {
        private double position;
        private FlatColor color;
        MultiGradientFill fill;

        public Stop(double position, FlatColor color) {
            this.position = position;
            this.color = color;
        }

        public Stop duplicate() {
            return new Stop(position,color);
        }

        public double getPosition() {
            return position;
        }

        public FlatColor getColor() {
            return color;
        }

        public void setPosition(double position) {
            this.position = position;
            if(fill != null) {
                this.fill.sortStops();
            }
        }

        public void setColor(FlatColor color) {
            this.color = color;
        }

        @Override
        public int compareTo(Stop stop) {
            if(position < stop.position) return -1;
            if(position > stop.position) return 1;
            return 0;
        }
    }
}
