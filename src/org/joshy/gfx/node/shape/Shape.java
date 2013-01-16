package org.joshy.gfx.node.shape;

import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Paint;
import org.joshy.gfx.node.Node;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Feb 8, 2010
 * Time: 2:53:37 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Shape extends Node {
    protected Paint fill = FlatColor.GRAY;
    protected Paint stroke = FlatColor.BLACK;
    protected double strokeWidth = 1;

    public Shape() {
        fill = new FlatColor(0,0,0,1);
    }

    public Shape setFill(Paint fillPaint) {
        this.fill = fillPaint;
        return this;
    }

    public Shape setStroke(Paint strokePaint) {
        this.stroke = strokePaint;
        return this;
    }

    protected Paint getFill() {
        return this.fill;
    }

    protected Paint getStroke() {
        return this.stroke;
    }

    public Shape setStrokeWidth(double strokeWidth) {
        this.strokeWidth = strokeWidth;
        return this;
    }

    public double getStrokeWidth() {
        return strokeWidth;
    }
}
