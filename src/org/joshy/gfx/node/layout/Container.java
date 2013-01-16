package org.joshy.gfx.node.layout;

import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.Parent;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.stage.Stage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Container extends Control implements Parent {
    protected List<Node> children;
    protected boolean skinDirty = true;
    
    public Container() {
        //then more
        children = new ArrayList<Node>();
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
    public Container add(Node node) {
        node.setParent(this);
        children.add(node);
        setSkinDirty();
        setLayoutDirty();
        setDrawingDirty();
        return this;
    }

    public void remove(Node node) {
        node.setParent(null);
        children.remove(node);
        setSkinDirty();
        setLayoutDirty();
        setDrawingDirty();
    }

    public Iterable<? extends Node> children() {
        return children;
    }

    public Iterable<? extends Node> reverseChildren() {
        ArrayList<Node> nodes = new ArrayList<Node>(children);
        Collections.reverse(nodes);
        return nodes;
    }

    // the main skins pass
    public void doSkins() {
        skinsDirty = false;
    }

    // the main layout pass
    @Override
    public void doPrefLayout() {
        for(Node n : children()) {
            if(n instanceof Control) {
                ((Control)n).doPrefLayout();
            }
        }
    }
    
    public void doLayout() {
        for(Node n : children()) {
            if(n instanceof Control) {
                ((Control)n).doLayout();
            }
        }
    }

    public Iterable<? extends Control> controlChildren() {
        List<Control> controls = new ArrayList<Control>();
        for(Node n : children()) {
            if(n instanceof Control) {
                controls.add((Control) n);
            }
        }
        return controls;
    }

    public Stage getStage() {
        return getParent().getStage();
    }

    public void removeAll() {
        for(Node n : children()) {
            n.setParent(null);
        }
        children.clear();
        setSkinDirty();
        setLayoutDirty();
        setDrawingDirty();
    }
}
