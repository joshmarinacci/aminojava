package org.joshy.gfx.node;

import org.joshy.gfx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public interface Parent {
    public void setSkinDirty(Node node);
    public void setDrawingDirty(Node node);
    public void setLayoutDirty(Node node);

    public Iterable<? extends Node> children();
    public Iterable<? extends Node> reverseChildren();
    public Stage getStage();
}
