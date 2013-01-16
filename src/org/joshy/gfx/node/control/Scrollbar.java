package org.joshy.gfx.node.control;

import org.joshy.gfx.SkinManager;
import org.joshy.gfx.css.BoxPainter;
import org.joshy.gfx.css.CSSSkin;
import org.joshy.gfx.css.SizeInfo;
import org.joshy.gfx.css.StyleInfo;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.ChangedEvent;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.MouseEvent;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.util.GraphicsUtil;

import java.awt.geom.Point2D;


/**
 * A scrollbar that can be either vertical or horizontal. It can optionally draw the thumb
 * with a proportional size.  Styling can be done with CSS.
 *
 */
public class Scrollbar extends Control {
    private double min = 0;
    private double max = 100;
    private double value = 0;
    private double smallScroll = 10;
    private double largeScroll = 20;

    protected boolean vertical = false;
    double arrowLength = 15;
    double thumbLength = 20;
    private boolean thumbPressed = false;
    private boolean isProportional = false;
    private double span = 0;
    private Point2D.Double startPX;
    private double offPX;
    private StyleInfo styleInfo;
    private SizeInfo sizeInfo;
    private BoxPainter boxPainter;
    private StyleInfo leftArrowStyleInfo;
    private SizeInfo leftArrowSizeInfo;
    private BoxPainter leftArrowPainter;
    private StyleInfo rightArrowStyleInfo;
    private SizeInfo rightArrowSizeInfo;
    private BoxPainter rightArrowPainter;
    private StyleInfo thumbStyleInfo;
    private SizeInfo thumbSizeInfo;
    private BoxPainter thumbPainter;
    private StyleInfo trackStyleInfo;
    private SizeInfo trackSizeInfo;
    private BoxPainter trackPainter;

    public Scrollbar() {
        this(false);
    }

    public Scrollbar(boolean vertical) {
        this.vertical = vertical;
        if(vertical) {
            setHeight(100);
            setWidth(20);
        } else {
            setHeight(20);
            setWidth(100);
        }
        EventBus.getSystem().addListener(this, MouseEvent.MouseAll, new Callback<MouseEvent>(){
            public void call(MouseEvent event) {
                processInput(event);
            }
        });
    }

    public void incrementValue(double amount) {
        setValue(getValue()+amount);
    }

    private void processInput(MouseEvent event) {
        double ex = event.getX();
        double ey = event.getY();

        if(event.getType() == MouseEvent.MousePressed) {
            Bounds startArrowBounds = getStartArrowBounds();
            if(startArrowBounds.contains(event.getX(),event.getY())) {
                setValue(getValue()-smallScroll);
                return;
            }
            Bounds endArrowBounds = getEndArrowBounds();
            if(endArrowBounds.contains(event.getX(),event.getY())) {
                setValue(getValue()+smallScroll);
                return;
            }

            Bounds trackBounds = getTrackBounds();
            Bounds thumbBounds = calculateThumbBounds();
            if(thumbBounds.contains(ex,ey)) {
                thumbPressed = true;
                if(isVertical()) {
                    offPX = event.getY() - calculateThumbBounds().getY();
                } else {
                    offPX = event.getX() - calculateThumbBounds().getX();
                }
                startPX = new Point2D.Double(ex,ey);
            } else {
                if(trackBounds.contains(ex,ey)) {
                    if(ex < thumbBounds.getX()) {
                        setValue(getValue()-largeScroll);
                    }
                    if(ex > thumbBounds.getX()+thumbBounds.getWidth()) {
                        setValue(getValue()+largeScroll);
                    }
                    if(ey < thumbBounds.getY()) {
                        setValue(getValue()-largeScroll);
                    }
                    if(ey > thumbBounds.getY()+thumbBounds.getHeight()) {
                        setValue(getValue()+largeScroll);
                    }
                }
            }
        }

        if(event.getType() == MouseEvent.MouseDragged) {
            if(thumbPressed) {
                Point2D currentPX = new Point2D.Double(event.getX(),event.getY());
                double value = pxToValue(currentPX,offPX);
                setValue(value);
            }
        }
        if(event.getType() == MouseEvent.MouseReleased) {
            thumbPressed = false;
        }
        
    }

    private double pxToValue(Point2D point, double offPX) {
        double px = point.getX() - offPX;
        if(isVertical()) {
            px = point.getY() - offPX;
        }
        px = px - arrowLength;
        double length = 0;
        if(vertical) {
            length = getHeight();
        } else {
            length = getWidth();
        }
        double fraction = px / ((length-arrowLength*2)*(1.0-span));
        if(span >= 1.0) {
            fraction = 0;
        }
        return getMin() + (getMax()-getMin())*fraction;
    }

    private Bounds getTrackBounds() {
        if(vertical) {
            return new Bounds(0,arrowLength,getWidth(),getHeight()-arrowLength*2);
        } else {
            return new Bounds(arrowLength,0,getWidth()-arrowLength*2,getHeight());
        }
    }

    private Bounds getEndArrowBounds() {
        if(vertical) {
            return new Bounds(0, getHeight()-arrowLength, getWidth(), arrowLength);
        } else {
            return new Bounds(getWidth()-arrowLength, 0, arrowLength, height);
        }
    }

    private Bounds getStartArrowBounds() {
        if(vertical) {
            return new Bounds(0,0, getWidth(), arrowLength);
        } else {
            return new Bounds(0,0, arrowLength, height);
        }
    }

    @Override
    public void doSkins() {
        cssSkin = SkinManager.getShared().getCSSSkin();
        styleInfo = cssSkin.getStyleInfo(this, null);
        leftArrowStyleInfo = cssSkin.getStyleInfo(this,null,"left-arrow");
        rightArrowStyleInfo = cssSkin.getStyleInfo(this,null,"right-arrow");
        thumbStyleInfo = cssSkin.getStyleInfo(this,null,"thumb");
        trackStyleInfo = cssSkin.getStyleInfo(this,null,"track");
        setLayoutDirty();
    }

    @Override
    public void doPrefLayout() {
        sizeInfo = cssSkin.getSizeInfo(this,styleInfo,"");
        setWidth(sizeInfo.width);
        setHeight(sizeInfo.height);
        leftArrowSizeInfo = cssSkin.getSizeInfo(this,leftArrowStyleInfo,"","left-arrow");
        rightArrowSizeInfo = cssSkin.getSizeInfo(this,rightArrowStyleInfo,"","right-arrow");
        thumbSizeInfo = cssSkin.getSizeInfo(this,thumbStyleInfo,"","thumb");
        trackSizeInfo = cssSkin.getSizeInfo(this,trackStyleInfo,"","track");
    }

    @Override
    public void doLayout() {
        sizeInfo.width = getWidth();
        sizeInfo.height = getHeight();
        boxPainter = cssSkin.createBoxPainter(this, styleInfo, sizeInfo, "", CSSSkin.State.None);
        leftArrowPainter = cssSkin.createBoxPainter(this, leftArrowStyleInfo, leftArrowSizeInfo, "", CSSSkin.State.None, "left-arrow");
        rightArrowPainter = cssSkin.createBoxPainter(this, rightArrowStyleInfo, rightArrowSizeInfo, "", CSSSkin.State.None, "right-arrow");
        thumbPainter = cssSkin.createBoxPainter(this, thumbStyleInfo, thumbSizeInfo, "", CSSSkin.State.None, "thumb");
        trackPainter = cssSkin.createBoxPainter(this, trackStyleInfo, trackSizeInfo, "", CSSSkin.State.None, "track");
    }

    @Override
    public void draw(GFX g) {
        if(!isVisible()) return;
        boxPainter.draw(g,styleInfo,sizeInfo,"");

        Bounds leftArrowBounds = new Bounds(0,0,arrowLength,getHeight());
        Bounds rightArrowBounds = new Bounds(getWidth()-arrowLength,0,arrowLength,getHeight());
        Bounds thumbBounds = calculateThumbBounds();
        if(isVertical()) {
            leftArrowBounds = new Bounds(0,0,getWidth(),arrowLength);
            rightArrowBounds = new Bounds(0, getHeight()-arrowLength,getWidth(),arrowLength);
        }

        Bounds trackBounds = getTrackBounds();
        trackSizeInfo.width= trackBounds.getWidth();
        trackSizeInfo.height = trackBounds.getHeight();
        g.translate(trackBounds.getX(),trackBounds.getY());
        trackPainter.draw(g,trackStyleInfo,trackSizeInfo,"");
        g.translate(-trackBounds.getX(),-trackBounds.getY());

        leftArrowSizeInfo.width = leftArrowBounds.getWidth();
        leftArrowSizeInfo.height = leftArrowBounds.getHeight();
        leftArrowPainter.draw(g,leftArrowStyleInfo,leftArrowSizeInfo,"");

        g.translate(rightArrowBounds.getX(),rightArrowBounds.getY());
        rightArrowSizeInfo.width = rightArrowBounds.getWidth();
        rightArrowSizeInfo.height = rightArrowBounds.getHeight();
        rightArrowPainter.draw(g,rightArrowStyleInfo,rightArrowSizeInfo,"");
        g.translate(-rightArrowBounds.getX(),-rightArrowBounds.getY());

        g.translate(thumbBounds.getX(),thumbBounds.getY());
        thumbSizeInfo.width = thumbBounds.getWidth();
        thumbSizeInfo.height = thumbBounds.getHeight();
        thumbPainter.draw(g,thumbStyleInfo,thumbSizeInfo,"");
        g.translate(-thumbBounds.getX(),-thumbBounds.getY());

        //draw the arrows
        g.setPaint(new FlatColor(0x303030));
        if(isVertical()) {
            GraphicsUtil.fillUpArrow(g,2,2,10);
            GraphicsUtil.fillDownArrow(g,2,getHeight()-2-10,10);
        } else {
            GraphicsUtil.fillLeftArrow(g,2,2,10);
            GraphicsUtil.fillRightArrow(g,getWidth()-2-10,2,10);
        }
    }

    Bounds calculateThumbBounds() {
        double diff = getMax()-getMin();
        double valueFraction = 0;
        if(diff > 0) {
            valueFraction = getValue() / diff;
        }
        double tl = thumbLength;
        if(isProportional()) {
            if(isVertical()){
                tl = span * (getHeight() - arrowLength - arrowLength);
            } else {
                tl = span * (getWidth()  - arrowLength - arrowLength);
            }
        }
        if(tl < thumbLength) {
            tl = thumbLength;
        }
        if(vertical) {
            double thumbY = (getHeight() -arrowLength-arrowLength-tl)*valueFraction + arrowLength;
            return new Bounds(0,thumbY,getWidth(), tl);
        } else {
            double thumbX = (getWidth() -arrowLength-arrowLength-tl)*valueFraction + arrowLength;
            return new Bounds(thumbX,0,tl,getHeight());
        }
    }

    @Override
    public Bounds getVisualBounds() {
        return new Bounds(getTranslateX(),getTranslateY(), getWidth(), getHeight());
    }

    @Override
    public Bounds getInputBounds() {
        return getVisualBounds();
    }

    public double getMin() {
        return min;
    }

    public void setMin(double min) {
        this.min = min;
        setDrawingDirty();
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
        setValue(getValue());
        setDrawingDirty();
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        if(value < getMin()) value = getMin();
        if(value > getMax()) value = getMax();
        if(this.value != value) {
            this.value = value;
            EventBus.getSystem().publish(new ChangedEvent(ChangedEvent.DoubleChanged, this.value, this));
            setDrawingDirty();
        }
    }

    public void setSpan(double value) {
        if(value > 1) {
            value = 1;
        }
        this.span = value;
        setDrawingDirty();
    }

    public boolean isVertical() {
        return vertical;
    }

    public void setVertical(boolean vertical) {
        this.vertical = vertical;
        setDrawingDirty();
    }

    public boolean isProportional() {
        return isProportional;
    }

    public void setProportional(boolean proportional) {
        isProportional = proportional;
    }

    public double getSpan() {
        return span;
    }

    public double getSmallScrollAmount() {
        return smallScroll;
    }

}
