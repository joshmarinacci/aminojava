package org.joshy.gfx.node;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.GFX;

public abstract class Node {
    private Parent parent;
    protected boolean drawingDirty = true;
    private double translateZ;
    private boolean visible = true;
    private double opacity = 1.0;

    public void setParent(Parent parent) {
        this.parent = parent;
        setDrawingDirty();
    }

    public abstract void draw(GFX g);

    protected void setDrawingDirty() {
        Core.getShared().assertGUIThread();
        this.drawingDirty = true;
        if(parent != null) {
            parent.setDrawingDirty(this);
        }
    }

    private double translateX;

    public double getTranslateX() {
        return this.translateX;
    }

    public Node setTranslateX(double translateX) {
        this.translateX = translateX;
        setDrawingDirty();
        return this;
    }
    

    private double translateY;

    public double getTranslateY() {
        return translateY;
    }

    public Node setTranslateY(double translateY) {
        this.translateY = translateY;
        setDrawingDirty();
        return this;
    }
    
    public abstract Bounds getVisualBounds();


    public abstract Bounds getInputBounds();

    public Parent getParent() {
        return parent;
    }

    public void setTranslateZ(double translateZ) {
        this.translateZ = translateZ;
        setDrawingDirty();
    }

    public double getTranslateZ() {
        return translateZ;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        setDrawingDirty();
    }


    public void setOpacity(double opacity) {
        this.opacity = opacity;
        setDrawingDirty();
    }

    public double getOpacity() {
        return this.opacity;
    }
}
