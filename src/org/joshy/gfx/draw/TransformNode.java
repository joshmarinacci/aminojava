package org.joshy.gfx.draw;

import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.layout.AbstractPane;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Oct 27, 2010
 * Time: 9:58:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class TransformNode extends AbstractPane {
    private Node content;
    private double scaleX = 1.0;
    private double scaleY = 1.0;
    private double rotate = 0.0;
    private Transform axis = Transform.Z_AXIS;

    public TransformNode setContent(Node content) {
        this.content = content;
        content.setParent(this);
        return this;
    }

    public TransformNode setScaleX(double scaleX) {
        this.scaleX = scaleX;
        setDrawingDirty();
        return this;
    }

    public TransformNode setScaleY(double scaleY) {
        this.scaleY = scaleY;
        setDrawingDirty();
        return this;
    }

    public TransformNode setRotate(double rotate) {
        this.rotate = rotate;
        setDrawingDirty();
        return this;
    }

    @Override
    public void draw(GFX g) {
//        g.translate(getTranslateX(),getTranslateY());
        g.scale(scaleX,scaleY);
        g.rotate(rotate,axis);
        g.translate(content.getTranslateX(),content.getTranslateY(),content.getTranslateZ());
        content.draw(g);
        g.translate(-content.getTranslateX(),-content.getTranslateY(),-content.getTranslateZ());
        g.rotate(-rotate,axis);
        g.scale(1/scaleX,1/scaleY);
//        g.translate(-getTranslateX(),-getTranslateY());
    }

    public TransformNode setAxis(Transform axis) {
        this.axis = axis;
        return this;
    }

    @Override
    public Bounds getVisualBounds() {
        return new Bounds(0,0,0,0);
    }

    @Override
    public void doPrefLayout() {
        if(content instanceof Control) {
            ((Control)content).doPrefLayout();
        }
    }

    @Override
    public void doLayout() {
        if(content instanceof Control) {
            ((Control)content).doLayout();
        }
    }

    @Override
    public Bounds getInputBounds() {
        return getVisualBounds();
    }

    @Override
    public Iterable<? extends Node> children() {
        List<Node> childs = new ArrayList<Node>();
        childs.add(content);
        return childs;
    }

    @Override
    public Iterable<? extends Node> reverseChildren() {
        List<Node> childs = new ArrayList<Node>();
        childs.add(content);
        return childs;
    }

    public double getRotate() {
        return rotate;
    }

    public double getScaleX() {
        return scaleX;
    }

    public double getScaleY() {
        return scaleY;
    }
}
