package org.joshy.gfx.node.layout;

import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.Parent;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.stage.Stage;

public abstract class AbstractPane extends Control implements Parent {
    public Stage getStage() {
        return getParent().getStage();
    }

    public void doSkins() {
        for(Node n : children()) {
            if(n instanceof Control) {
                ((Control)n).doSkins();
            }
        }
    }

    public void setSkinDirty(Node node) {
        setSkinDirty();
    }

    public void setDrawingDirty(Node node) {
        setDrawingDirty();
    }

    public void setLayoutDirty(Node node) {
        setLayoutDirty();
    }
}
