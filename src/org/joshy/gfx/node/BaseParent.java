package org.joshy.gfx.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BaseParent implements Parent {
    private boolean drawingDirty = false;
    private boolean layoutDirty = false;
    private List<Node> children;
    private boolean skinDirty = true;

    public BaseParent() {
        children = new ArrayList<Node>();
    }

    public void setSkinDirty(Node node) {
        this.skinDirty = true;
    }

    public void setDrawingDirty(Node node) {
        this.drawingDirty = true;
    }

    public void setLayoutDirty(Node node) {
        this.layoutDirty = true;
    }

    public void add(Node node) {
        node.setParent(this);
        children.add(node);
        setDrawingDirty(node);
        setLayoutDirty(node);
    }

    public void remove(Node node) {
        node.setParent(null);
        children.remove(node);
        setDrawingDirty(node);
        setLayoutDirty(node);
    }

    public Iterable<? extends Node> children() {
        return Collections.unmodifiableList(children);
    }

    public Iterable<? extends Node> reverseChildren() {
        ArrayList<Node> nodes = new ArrayList<Node>(children);
        Collections.reverse(nodes);
        return nodes;
    }


}
