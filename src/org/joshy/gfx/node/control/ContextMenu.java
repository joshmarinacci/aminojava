package org.joshy.gfx.node.control;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.joshy.gfx.event.AminoAction;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.ChangedEvent;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.NodeUtils;
import org.joshy.gfx.node.layout.Container;
import org.joshy.gfx.stage.Stage;


public class ContextMenu extends PopupMenu {
    private static ContextMenu sharedContextMenu;
    private static Container sharedLayer;

    private List<AminoAction> items;
    ListModel mdl = new ListModel<CharSequence>() {
        public CharSequence get(int i) {
            return items.get(i).getDisplayName();
        }
        public int size() {
            return items.size();
        }
    };


    public ContextMenu() {
        items = new ArrayList<AminoAction>();
        setModel(mdl);
        setCallback(new Callback<ChangedEvent>() {
            public void call(ChangedEvent event) throws Exception {
                int i = ((Integer)event.getValue());
                AminoAction action = getAction(i);
                action.execute();
            }
        });

    }

    public void addActions(AminoAction... actions) {
        Collections.addAll(items, actions);
    }

    public AminoAction getAction(int i) {
        return items.get(i);
    }

    public void show(Node node, double x, double y) {
        if(sharedContextMenu != null) {
            sharedContextMenu.setVisible(false);
            sharedLayer.remove(sharedContextMenu);
        }
        Stage stage = node.getParent().getStage();
        sharedLayer = stage.getPopupLayer();
        Point2D pt = NodeUtils.convertToScene(node, x, y);
        pt = NodeUtils.convertFromScene(sharedLayer,pt);
        setTranslateX(pt.getX()+2);
        setTranslateY(pt.getY());
        setVisible(true);
        sharedContextMenu = this;
        sharedLayer.add(this);
    }

    public static void hideAll() {
        if(sharedContextMenu != null) {
            sharedContextMenu.setVisible(false);
            sharedLayer.remove(sharedContextMenu);
        }
    }

    public void show(Node canvas, Point2D pointInNodeCoords) {
        show(canvas, pointInNodeCoords.getX(),pointInNodeCoords.getY());
    }
}

