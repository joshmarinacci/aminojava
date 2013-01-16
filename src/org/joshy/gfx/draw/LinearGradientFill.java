package org.joshy.gfx.draw;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: 1/15/11
 * Time: 4:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class LinearGradientFill extends MultiGradientFill{

    public static enum Snap {None, Start, Middle, End};

    private double startX;
    private double startY;
    private double endX;
    private double endY;
    private Snap startXSnapped = Snap.None;
    private Snap startYSnapped = Snap.None;
    private Snap endXSnapped = Snap.None;
    private Snap endYSnapped = Snap.None;

    public LinearGradientFill setStartX(double startX) {
        this.startX = startX;
        return this;
    }

    public LinearGradientFill setStartY(double startY) {
        this.startY = startY;
        return this;
    }

    public LinearGradientFill setEndX(double endX) {
        this.endX = endX;
        return this;
    }

    public LinearGradientFill setEndY(double endY) {
        this.endY = endY;
        return this;
    }

    public LinearGradientFill setStartXSnapped(Snap snap) {
        this.startXSnapped = snap;
        return this;
    }
    public LinearGradientFill setStartYSnapped(Snap x) {
        this.startYSnapped = x;
        return this;
    }
    public LinearGradientFill setEndXSnapped(Snap x) {
        this.endXSnapped = x;
        return this;
    }
    public LinearGradientFill setEndYSnapped(Snap x) {
        this.endYSnapped = x;
        return this;
    }
    public Snap getStartXSnapped() {
        return startXSnapped;
    }

    public Snap getStartYSnapped() {
        return startYSnapped;
    }

    public Snap getEndXSnapped() {
        return endXSnapped;
    }

    public Snap getEndYSnapped() {
        return endYSnapped;
    }



    @Override
    public Paint duplicate() {
        LinearGradientFill grad = new LinearGradientFill();
        grad.startX = startX;
        grad.endX = endX;
        grad.startY = startY;
        grad.endY = endY;
        grad.startXSnapped = startXSnapped;
        grad.startYSnapped = startYSnapped;
        grad.endXSnapped = endXSnapped;
        grad.endYSnapped = endYSnapped;
        for(Stop s : stops) {
            grad.addStop(s.duplicate());
        }
        return grad;
    }

    public double getStartX() {
        return startX;
    }

    public double getStartY() {
        return startY;
    }

    public double getEndX() {
        return endX;
    }

    public double getEndY() {
        return endY;
    }

    @Override
    public MultiGradientFill translate(double x, double y) {
        LinearGradientFill grad = new LinearGradientFill();
        grad.startX = startX+x;
        grad.endX = endX+x;
        grad.startY = startY+y;
        grad.endY = endY+y;
        for(Stop s : stops) {
            grad.addStop(s.duplicate());
        }
        return grad;
    }
}
