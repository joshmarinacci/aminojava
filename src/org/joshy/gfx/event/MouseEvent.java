package org.joshy.gfx.event;

import java.awt.geom.Point2D;

import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.NodeUtils;

/**
* Created by IntelliJ IDEA.
* User: josh
* Date: Jan 20, 2010
* Time: 10:15:35 AM
* To change this template use File | Settings | File Templates.
*/
public class MouseEvent extends Event {
    public static final EventType MouseMoved = new EventType("MouseMoved");
    public static final EventType MousePressed = new EventType("MousePressed");
    public static final EventType MouseDragged = new EventType("MouseDragged");
    public static final EventType MouseDraggedRaw = new EventType("MouseDraggedRaw");
    public static final EventType MouseReleased = new EventType("MouseReleased");
    public static final EventType MouseEntered = new EventType("MouseEntered");
    public static final EventType MouseExited = new EventType("MouseExited");
    public static final EventType OpenContextMenu = new EventType("OpenContextMenu");
    
    //public static final EventType MouseClicked = new EventType("MousePressed");
    public static final EventType MouseAll = new EventType("MouseAll") {
        @Override
        public boolean matches(EventType type) {
            if(type == MouseMoved) return true;
            if(type == MouseEntered) return true;
            if(type == MouseExited) return true;
            if(type == MousePressed) return true;
            if(type == MouseDragged) return true;
            if(type == MouseDraggedRaw) return true;
            if(type == MouseReleased) return true;
            if(type == OpenContextMenu) return true;
            return super.matches(type);
        }
    };

    private final double x;
    private final double y;
    private boolean shiftPressed;
    private boolean altPressed;
    private boolean controlPressed;
    private boolean commandPressed;
    private int button;


    public MouseEvent(EventType type, double x, double y, Node node) {
        super(type);
        this.x = x;
        this.y = y;
        this.source = node;

    }

    public MouseEvent(EventType type, double x, double y, Node node, int button, boolean shiftPressed, boolean altPressed, boolean controlPressed, boolean commandPressed) {
        this(type,x,y,node);
        this.button = button;
        this.shiftPressed = shiftPressed;
        this.altPressed = altPressed;
        this.controlPressed = controlPressed;
        this.commandPressed = commandPressed;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }


    @Override
    public String toString() {
        return type+ ":(" + x + "," + y +") source = " + source;
    }

    public boolean isShiftPressed() {
        return shiftPressed;
    }

    public boolean isAltPressed() {
        return altPressed;
    }

    public Point2D getPointInNodeCoords(Node node) {
        Node source = (Node) this.source;
        Point2D pt = NodeUtils.convertToScene(source,x,y);
        pt = NodeUtils.convertFromScene(node,pt);
        return pt;
    }

    public Point2D getPointInSceneCoords() {
        Node source = (Node) this.source;
        Point2D pt = NodeUtils.convertToScene(source,x,y);
        return pt;
    }

    public Point2D getPointInScreenCoords() {
        Node source = (Node) this.source;
        Point2D pt = NodeUtils.convertToScreen(source, x,y);
        return pt;
    }

    public boolean isControlPressed() {
        return controlPressed;
    }

    public int getButton() {
        return button;
    }

    public boolean isCommandPressed() {
        return commandPressed;
    }
}
