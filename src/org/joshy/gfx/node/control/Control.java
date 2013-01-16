package org.joshy.gfx.node.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.css.CSSSkin;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;

import java.util.HashSet;
import java.util.Set;

/**
 * The base of all UI controls. It defines the layout contract, and
 * size values. It also installs two prefab CSS classes each for the control and
 * it's parent classes.  For example, the Checkbox class inherits from button
 * which inherits from control, so it would have these css classes:
 *
 * -class-org.joshy.gfx.node.control.Control
 * -class-Control
 * -class-org.joshy.gfx.node.control.Button
 * -class-Button
 * -class-org.joshy.gfx.node.control.Checkbox
 * -class-Checkbox
 *
 *
 * This allows style to be shared between subclasses, including when you create
 * anonymous subclasses like this:
 *
 * Button b = new Button() {
 *    //do something custom
 * };
 * 
 */
public abstract class Control extends Node {
    public static final double CALCULATED = -1;

    protected double width = 0;
    protected double height = 0;
    protected double prefWidth = CALCULATED;
    protected double prefHeight = CALCULATED;
    private boolean enabled = true;
    
    public boolean skinsDirty;
    protected boolean layoutDirty;
    protected CSSSkin cssSkin;
    private String id;
    protected Set<String> cssClasses = new HashSet<String>();

    protected Control() {
        setSkinDirty();
        populateCSSClasses();
    }

    private void populateCSSClasses() {
        Class clz = this.getClass();
        while(true) {
            cssClasses.add("-class-"+clz.getName().replace(".","-"));
            cssClasses.add("-class-"+clz.getSimpleName().replace(".","-"));
            if(clz == Control.class) break;
            clz = clz.getSuperclass();
        }
    }

    public Control setWidth(double width) {
        this.width = width;
        setLayoutDirty();
        setDrawingDirty();
        return this;
    }

    public Control setHeight(double height) {
        this.height = height;
        setLayoutDirty();
        setDrawingDirty();
        return this;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public Control setPrefWidth(double width) {
        this.prefWidth = width;
        return this;
    }

    public Control setPrefHeight(double height) {
        this.prefHeight = height;
        return this;
    }

    @Override
    public Bounds getInputBounds() {
        return getVisualBounds();
    }

    @Override
    public Bounds getVisualBounds() {
        return new Bounds(getTranslateX(),getTranslateY(),getWidth(),getHeight());
    }

    /** returns the bounds to be used for layout. All layouts and panels should use
     * these bounds, not the visual bounds. Visual bounds are only used for repainting.
     *
      * @return
     */
    public Bounds getLayoutBounds() {
        return getVisualBounds();
    }

    /** returns the baseline of this control, in the coordinate system returned by
     * getLayoutBounds().  A control with no particular baseline should just return
     * the height.
     *
     * @return
     */
    public double getBaseline() {
        return getLayoutBounds().getHeight();
    }

    protected void setLayoutDirty() {
        Core.getShared().assertGUIThread();
        this.layoutDirty = true;
        if(getParent() != null) {
            getParent().setLayoutDirty(this);
        }
    }

    protected void setSkinDirty() {
        Core.getShared().assertGUIThread();
        this.skinsDirty = true;
        if(getParent() != null) {
            getParent().setSkinDirty(this);
        }
    }

    /** the control should calculate it's layout bounds,
     * caching as much info as possible so that drawing can be fast.
     */
    public abstract void doLayout();

    /** do pref layout
     *
     */
    public abstract void doPrefLayout();

    /** the control should load up it's skins, caching as much
     * info as possible so that drawing and layout can be fast.
     */
    public abstract void doSkins();

    public String getId() {
        return id;
    }

    public Control setId(String id) {
        this.id = id;
        return this;
    }

    public Set<String> getCSSClasses() {
        return this.cssClasses;
    }

    public double getPrefWidth() {
        return prefWidth;
    }

    public double getPrefHeight() {
        return prefHeight;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Control setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public Control addCSSClass(String clss) {
        this.cssClasses.add(clss);
        return this;
    }

    public boolean hasCSSClass(String clss) {
        return cssClasses.contains(clss);
    }

    public boolean isSkinDirty() {
        return skinsDirty;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        setLayoutDirty();
    }
}


