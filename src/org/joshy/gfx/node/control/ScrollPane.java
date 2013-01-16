package org.joshy.gfx.node.control;

import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.*;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.layout.AbstractPane;
import org.joshy.gfx.node.layout.Container;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.util.u;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A scrollpane is a panel which can have one large child element. It will render
 * scrollbars on the sides to let the user pan around the child. The scroll bars
 * will be visible as needed, or can be hidden or made to always be visible by setting
 * the policy properties.
 *
 *
 * Certain controls are scrolling aware, meaning they implement the ScrollingAware interface.
 * These controls implement their ow panning mechanism to draw more efficiently. They can also
 * resize their contents base on the size of the scroll pane as desired.  Currently, lists and
 * tables are scrolling aware.
 */
public class ScrollPane extends AbstractPane {
    private Scrollbar vscroll;
    private Scrollbar hscroll;
    private Node content;
    private Container contentWrapper;
    private double vscrollValue;
    private double hscrollValue;
    private VisiblePolicy horizontalVisiblePolicy = VisiblePolicy.Always;
    private VisiblePolicy verticalVisiblePolicy = VisiblePolicy.Always;

    public ScrollPane() {
        vscroll = new Scrollbar(true);
        vscroll.setProportional(true);
        vscroll.setParent(this);
        hscroll = new Scrollbar(false);
        hscroll.setProportional(true);
        hscroll.setParent(this);
        contentWrapper = new Container() {
            @Override
            public void doPrefLayout() {
                for(Node n : children()) {
                    if(n instanceof Control) {
                        ((Control)n).doPrefLayout();
                    }
                }
            }

            @Override
            public void doLayout() {
                for(Node n : children()) {
                    if(n instanceof Control) {
                        ((Control)n).doLayout();
                    }
                }
            }

            @Override
            public void draw(GFX g) {
                g.setPaint(FlatColor.BLACK);
                g.fillRect(0,0,getWidth(),getHeight());
                for(Node child : children) {
                    g.translate(child.getTranslateX(),child.getTranslateY());
                    child.draw(g);
                    g.translate(-child.getTranslateX(),-child.getTranslateY());
                }
                this.drawingDirty = false;
            }
        };
        contentWrapper.setParent(this);
        EventBus.getSystem().addListener(vscroll,ChangedEvent.DoubleChanged, new Callback<ChangedEvent>() {
            public void call(ChangedEvent event) {
                setVScrollValue(-(Double)event.getValue(), false);
            }
        });
        EventBus.getSystem().addListener(hscroll,ChangedEvent.DoubleChanged, new Callback<ChangedEvent>() {
            public void call(ChangedEvent event) {
                hscrollValue = -(Double)event.getValue();
                if(content instanceof ScrollingAware) {
                    ((ScrollingAware)content).setScrollX(hscrollValue);
                } else {
                    contentWrapper.setTranslateX(hscrollValue);
                }
                setDrawingDirty();
            }
        });
        EventBus.getSystem().addListener(Scope.Container, contentWrapper, ScrollEvent.ScrollAll, new Callback<ScrollEvent>(){
            @Override
            public void call(ScrollEvent event) {
                if(ScrollEvent.ScrollVertical == event.getType()) {
                    if(event.getAmount() > 0) {
                        vscroll.incrementValue(vscroll.getSmallScrollAmount());
                    } else {
                        vscroll.incrementValue(-vscroll.getSmallScrollAmount());
                    }
                }
                if(ScrollEvent.ScrollHorizontal == event.getType()) {
                    if(event.getAmount() > 0) {
                        hscroll.incrementValue(hscroll.getSmallScrollAmount());
                    } else {
                        hscroll.incrementValue(-hscroll.getSmallScrollAmount());
                    }
                }
            }
        });
        setContent(new Panel());
    }

    public ScrollPane(Control content) {
        this();
        setContent(content);
    }

    public Scrollbar getVerticalScrollBar() {
        return vscroll;
    }

    private void setVScrollValue(double vsv, boolean updateScrollbar) {
        vscrollValue = vsv;
        if(content instanceof ScrollingAware) {
            ((ScrollingAware)content).setScrollY(vscrollValue);
        } else {
            contentWrapper.setTranslateY(vscrollValue);
        }
        if(updateScrollbar) {
            vscroll.setValue(-vscrollValue);
        }
        setDrawingDirty();
    }

    private void setHScrollValue(double hsv, boolean updateScrollbar) {
        hscrollValue = hsv;
        if(content instanceof ScrollingAware) {
            ((ScrollingAware)content).setScrollX(hscrollValue);
        } else {
            contentWrapper.setTranslateX(hscrollValue);
        }
        if(updateScrollbar) {
            hscroll.setValue(-hscrollValue);
        }
        setDrawingDirty();
    }

    @Override
    public void doPrefLayout() {
        vscroll.doPrefLayout();
        hscroll.doPrefLayout();
        for(Node n : children()) {
            if(n instanceof Control) {
                ((Control)n).doPrefLayout();
            }
        }
        if(getPrefWidth() != CALCULATED) {
            setWidth(getPrefWidth());
        }
        if(getPrefHeight() != CALCULATED) {
            setHeight(getPrefHeight()) ;
        }
    }

    @Override
    public void doLayout() {
        if(horizontalVisiblePolicy == VisiblePolicy.Never) {
            hscroll.setVisible(false);
        }
        if(horizontalVisiblePolicy == VisiblePolicy.Always) {
            hscroll.setVisible(true);
        }
        if(horizontalVisiblePolicy == VisiblePolicy.WhenNeeded) {
            if(hscroll.getSpan() == 1) {
                hscroll.setVisible(false);
            } else {
                hscroll.setVisible(true);
            }
        }
        double vInset = hscroll.getHeight();
        if(!hscroll.isVisible()) {
            vInset = 0;
        }

        if(verticalVisiblePolicy == VisiblePolicy.Never) {
            vscroll.setVisible(false);
        }
        if(verticalVisiblePolicy == VisiblePolicy.Always) {
            vscroll.setVisible(true);
        }
        
        double hInset = vscroll.getWidth();
        if(!vscroll.isVisible()) {
            hInset = 0;
        }


        // the main pref layout pass
        vscroll.setTranslateX(getWidth()-vscroll.getWidth());
        vscroll.setHeight(getHeight()-vInset);
/*        if(hscroll.isVisible()) {
            vscroll.setHeight(getHeight()-hscroll.getHeight());
        } else {
            vscroll.setHeight(getHeight());
        }*/
        hscroll.setTranslateY(getHeight()-vInset);
        hscroll.setWidth(getWidth()-hInset);


        Bounds cBounds = content.getVisualBounds();
        if(content instanceof ScrollingAware) {
            ScrollingAware sa = (ScrollingAware) content;
            cBounds = new Bounds(0,0,
                    sa.getFullWidth(getWidth()-hInset,getHeight()-vInset),
                    sa.getFullHeight(getWidth()-hInset,getHeight()-vInset));
            if(sa instanceof Control) {
                Control control = (Control) sa;
                control.setWidth(getWidth()-hInset);
                control.setHeight(getHeight()-vInset);
            }
        }
        
        hscroll.setMin(0);
        double hmax =  cBounds.getWidth()-getWidth()+hInset;
        if(hmax < 0) {
            hscroll.setMax(0);
            hscroll.setSpan(1);
        } else {
            hscroll.setMax(hmax);
            hscroll.setSpan(getWidth()/cBounds.getWidth());
        }



        double vmax = cBounds.getHeight()-getHeight();
        if(hscroll.isVisible()) {
            vmax +=hscroll.getHeight();
        }
        vscroll.setMin(0);
        if(vmax < 0) {
            vscroll.setMax(0);
            vscroll.setSpan(1);
        } else {
            vscroll.setMax(vmax);
            vscroll.setSpan(getHeight()/cBounds.getHeight());
        }

        if(verticalVisiblePolicy == VisiblePolicy.WhenNeeded) {
            if(vscroll.getSpan() == 1) {
                vscroll.setVisible(false);
            } else {
                vscroll.setVisible(true);
            }
        }

        // the main pref layout pass
        for(Node n : children()) {
            if(n instanceof Control) {
                ((Control)n).doLayout();
            }
        }
    }

    public void setContent(Node node) {
        content = node;
        contentWrapper.add(content);
        if(node instanceof ScrollingAware) {
            ((ScrollingAware)node).setScrollParent(this);
        }
    }

    public Iterable<? extends Node> children() {
        List<Node> childs = new ArrayList<Node>();
        childs.add(contentWrapper);
        childs.add(hscroll);
        childs.add(vscroll);
        return childs;
    }

    public Iterable<? extends Node> reverseChildren() {
        List<Node> childs = (List<Node>) children();
        Collections.reverse(childs);
        return childs;
    }

    @Override
    public void draw(GFX g) {
        Bounds oldClip = g.getClipRect();
        g.setClipRect(new Bounds(0,0,getWidth(),getHeight()));
        g.setPaint(FlatColor.WHITE);
        g.fillRect(0,0,getWidth(),getHeight());
        for(Node child : children()) {
            g.translate(child.getTranslateX(),child.getTranslateY());
            child.draw(g);
            g.translate(-child.getTranslateX(),-child.getTranslateY());
        }
        g.setClipRect(oldClip);
        this.drawingDirty = false;
    }

    public void scrollToShow(Bounds bounds) {
        double w = getWidth();
        double h = getHeight();

        //vertical adjustments
        //calc if we need to scroll down to reveal more at the bottom
        // (subtract off the value of the horiz scrollbar width
        double vInset = 20;
        if(!hscroll.isVisible()) {
            vInset = 0;
        }
        double diff = bounds.getY()+bounds.getHeight() - (h-vInset-vscrollValue);
        //calc if we need to scroll up to reveal more on top
        double diff2 = h + bounds.getY() - (getHeight()-vscrollValue);
        if(diff > 0) {
            setVScrollValue(vscrollValue -diff,true);
        }
        if(diff2 <= 0) {
            setVScrollValue(vscrollValue - diff2,true);
        }
        //horizontal adjustments
        //calc if we need to scroll to reveal more on the right
        //(subtract off the width of the vert scrollbar)
        diff = bounds.getX()+bounds.getWidth()-(w-20-hscrollValue);
        //calc if we need to scroll to reveal more on the left
        diff2 = w + bounds.getX() - (getWidth()-hscrollValue);
        if(diff > 0) {
            setHScrollValue(hscrollValue - diff,true);
        }
        if(diff2 <= 0) {
            setHScrollValue(hscrollValue - diff2, true);
        }
    }

    public Scrollbar getHorizontalScrollBar() {
        return hscroll;
    }

    public ScrollPane setHorizontalVisiblePolicy(VisiblePolicy horizontalVisiblePolicy) {
        this.horizontalVisiblePolicy = horizontalVisiblePolicy;
        setDrawingDirty();
        return this;
    }

    public ScrollPane setVerticalVisiblePolicy(VisiblePolicy verticalVisiblePolicy) {
        this.verticalVisiblePolicy = verticalVisiblePolicy;
        setDrawingDirty();
        return this;
    }

    public interface ScrollingAware {
        public double getFullWidth(double width, double height);
        public double getFullHeight(double width, double height);
        public void setScrollX(double value);
        public void setScrollY(double value);
        public void setScrollParent(ScrollPane scrollPane);
    }

    public enum VisiblePolicy {
        Never, Always, WhenNeeded
    }
}
