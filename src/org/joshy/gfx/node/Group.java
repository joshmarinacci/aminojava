package org.joshy.gfx.node;

import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 19, 2010
 * Time: 7:59:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class Group extends Node implements Parent {
    protected List<Node> children;
    protected boolean skinDirty = true;
    protected boolean layoutDirty = true;

    public Group() {
        children = new ArrayList<Node>();
    }

    @Override
    public void draw(GFX g) {
        if(!isVisible()) return;
        for(Node child : children) {
            g.translate(child.getTranslateX(),child.getTranslateY(),child.getTranslateZ());
            child.draw(g);
            g.translate(-child.getTranslateX(),-child.getTranslateY(),-child.getTranslateZ());
        }
        this.drawingDirty = false;
    }

    @Override
    public Bounds getVisualBounds() {
        return new Bounds(0,0,120,120);
    }

    @Override
    public Bounds getInputBounds() {
        return new Bounds(0,0,120,120);
    }

    public void setSkinDirty(Node node) {
        setSkinDirty();
    }
    public void setLayoutDirty(Node node) {
        setLayoutDirty();
    }
    public void setDrawingDirty(Node node) {
        setDrawingDirty();
    }


    protected void setSkinDirty() {
        this.skinDirty= true;
        if(getParent() != null) {
            getParent().setSkinDirty(this);
        }
    }
    protected void setLayoutDirty() {
        this.layoutDirty = true;
        if(getParent() != null) {
            getParent().setLayoutDirty(this);
        }
    }

    public Group add(Node node) {
        node.setParent(this);
        children.add(node);
        setSkinDirty();
        setLayoutDirty();
        setDrawingDirty();
        return this;
    }
    public Group add(Node ... nodes) {
        for(Node n : nodes) {
            add(n);
        }
        return this;
    }

    public int getChildCount() {
        return children.size();
    }

    public void remove(Node node) {
        node.setParent(null);
        children.remove(node);
        setSkinDirty();
        setLayoutDirty();
        setDrawingDirty();
    }

    public Stage getStage() {
        return getParent().getStage();
    }

    public Iterable<? extends Node> children() {
        return children;
    }

    public Iterable<? extends Node> reverseChildren() {
        ArrayList<Node> nodes = new ArrayList<Node>(children);
        Collections.reverse(nodes);
        return nodes;
    }

}
