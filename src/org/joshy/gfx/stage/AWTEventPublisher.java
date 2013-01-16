package org.joshy.gfx.stage;

import org.joshy.gfx.Core;
import org.joshy.gfx.event.Event;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.ScrollEvent;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.Parent;
import org.joshy.gfx.util.OSUtil;
import org.joshy.gfx.util.u;

import java.awt.event.*;
import java.awt.geom.Point2D;

public class AWTEventPublisher extends EventPublisher
        implements MouseListener,
        MouseMotionListener,
        MouseWheelListener,
        KeyListener,
        WindowListener,
        WindowStateListener {
    private Node hoverNode;

    public AWTEventPublisher(Parent parent) {
        super(parent);
    }


    public void mouseClicked(MouseEvent e) {

    }
    public void mouseMoved(MouseEvent e) {
        logEvent("MouseMoved",e);
        Node node = findTopNode(e.getX(),e.getY());
        if(node != hoverNode) {
            if(hoverNode != null) {
                Point2D point = convertSceneToNode(e.getX(),e.getY(),hoverNode);
                org.joshy.gfx.event.MouseEvent evt = new org.joshy.gfx.event.MouseEvent(
                        org.joshy.gfx.event.MouseEvent.MouseExited, point.getX(), point.getY(), hoverNode,
                        e.getButton(),
                        e.isShiftDown(),
                        e.isAltDown(),
                        e.isControlDown()
                        ,e.isMetaDown()
                );
                EventBus.getSystem().publish(evt);
            }
            hoverNode = node;
            if(hoverNode != null) {
                Point2D point = convertSceneToNode(e.getX(),e.getY(),hoverNode);
                org.joshy.gfx.event.MouseEvent evt = new org.joshy.gfx.event.MouseEvent(
                        org.joshy.gfx.event.MouseEvent.MouseEntered, point.getX(), point.getY(), hoverNode,
                        e.getButton(),
                        e.isShiftDown(),
                        e.isAltDown(),
                        e.isControlDown()
                        ,e.isMetaDown()
                );
                EventBus.getSystem().publish(evt);
            }
        }
        Point2D point = convertSceneToNode(e.getX(),e.getY(),node);
        org.joshy.gfx.event.MouseEvent evt = new org.joshy.gfx.event.MouseEvent(
                org.joshy.gfx.event.MouseEvent.MouseMoved, point.getX(), point.getY(), node,
                e.getButton(),
                e.isShiftDown(),
                e.isAltDown(),
                e.isControlDown()
                ,e.isMetaDown()
        );
        EventBus.getSystem().publish(evt);
    }


    public void mousePressed(MouseEvent e) {
        logEvent("MousePressed",e);
        Node node = findTopNode(e.getX(),e.getY());
        Point2D point = convertSceneToNode(e.getX(),e.getY(), node);
        org.joshy.gfx.event.MouseEvent evt = toEvent(e, point, node, org.joshy.gfx.event.MouseEvent.MousePressed);
        EventBus.getSystem().setPressedNode(node);
        EventBus.getSystem().publish(evt);
        if(evt.isControlPressed() && OSUtil.isMac() || evt.getButton() == 3 || e.isPopupTrigger()) {
            org.joshy.gfx.event.MouseEvent evt2 = toEvent(e, point, node, org.joshy.gfx.event.MouseEvent.OpenContextMenu);
            EventBus.getSystem().publish(evt2);
        }
    }
    private org.joshy.gfx.event.MouseEvent toEvent(MouseEvent e, Point2D point, Node node, Event.EventType type) {
        boolean meta = e.isMetaDown();
        if(e.isPopupTrigger()) {
            meta = false;
        }

        org.joshy.gfx.event.MouseEvent evt = new org.joshy.gfx.event.MouseEvent(
                type
                ,point.getX(), point.getY()
                ,node
                ,e.getButton()
                ,e.isShiftDown()
                ,e.isAltDown()
                ,e.isControlDown()
                ,meta
        );
        return evt;
    }
    private void logEvent(String s, java.awt.event.InputEvent e) {
        //u.p("AWTEventPublisher: " + s + " " + e);
    }

    public void mouseDragged(MouseEvent e) {
        logEvent("MouseDragged",e);
        //send event to the pressedNode
        publishMouseEvent(e.getX(),e.getY(),EventBus.getSystem().getPressedNode(),
                org.joshy.gfx.event.MouseEvent.MouseDragged,
                e.getButton(),e.isShiftDown(),e.isAltDown(), e.isControlDown(),e.isMetaDown()
        );
        
        // also send event to the top node under the cursor if it's not the pressedNode
        Node node = findTopNode(e.getX(),e.getY());
        if(EventBus.getSystem().getPressedNode() != node) {
            publishMouseEvent(e.getX(),e.getY(),node,org.joshy.gfx.event.MouseEvent.MouseDraggedRaw,
                e.getButton(),e.isShiftDown(),e.isAltDown(),e.isControlDown(),e.isMetaDown());
        }        
    }

    public void mouseReleased(MouseEvent e) {
        logEvent("MouseReleased",e);
        //send event to the pressedNode
        publishMouseEvent(e.getX(),e.getY(),EventBus.getSystem().getPressedNode(),org.joshy.gfx.event.MouseEvent.MouseReleased,e.getButton(),e.isShiftDown(),e.isAltDown(),e.isControlDown(),e.isMetaDown());
        // also send event to the top node under the cursor if it's not the pressedNode
        Node node = findTopNode(e.getX(),e.getY());
        if(node != EventBus.getSystem().getPressedNode()) {
            publishMouseEvent(e.getX(),e.getY(),node,org.joshy.gfx.event.MouseEvent.MouseReleased,e.getButton(),e.isShiftDown(),e.isAltDown(),e.isControlDown(),e.isMetaDown());
        }
        EventBus.getSystem().setPressedNode(null);
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    private void publishMouseEvent(int x, int y, Node node, Event.EventType eventType, int button, boolean shiftDown, boolean altDown, boolean controlDown, boolean commandDown) {
        Point2D point = convertSceneToNode(x,y, node);
        org.joshy.gfx.event.MouseEvent evt = new org.joshy.gfx.event.MouseEvent(
                eventType, point.getX(), point.getY(), node,
                button, shiftDown, altDown, controlDown, commandDown);
        EventBus.getSystem().publish(evt);
    }

    public void mouseWheelMoved(MouseWheelEvent e) {

        Event.EventType type = ScrollEvent.ScrollVertical;
        Node node = findTopNode(e.getX(),e.getY());
        int rotation = e.getWheelRotation();
        if(e.isShiftDown()) {
            type = ScrollEvent.ScrollHorizontal;
        }
        EventBus.getSystem().publish(new ScrollEvent(type,node,rotation));
    }
    
    public void keyTyped(KeyEvent e) {
        logEvent("KeyTyped",e);
        Node node = Core.getShared().getFocusManager().findFocusedNode(parent);
        org.joshy.gfx.event.KeyEvent evt = new org.joshy.gfx.event.KeyEvent(
                org.joshy.gfx.event.KeyEvent.KeyTyped,
                org.joshy.gfx.event.KeyEvent.getKeyCodeFromAWT(e.getKeyCode()),
                node,
                e.getKeyChar(),
                e.isShiftDown(),
                e.isControlDown(),
                e.isAltDown(),
                e.isMetaDown()
                );
        EventBus.getSystem().publish(evt);
    }

    public void keyPressed(KeyEvent e) {
        logEvent("KeyPressed",e);
        Node node = Core.getShared().getFocusManager().findFocusedNode(parent);
        org.joshy.gfx.event.KeyEvent evt = new org.joshy.gfx.event.KeyEvent(
                org.joshy.gfx.event.KeyEvent.KeyPressed,
                org.joshy.gfx.event.KeyEvent.getKeyCodeFromAWT(e.getKeyCode()),
                node,
                e.getKeyChar(),
                e.isShiftDown(),
                e.isControlDown(),
                e.isAltDown(),
                e.isMetaDown()
                );
        EventBus.getSystem().publish(evt);
    }

    public void keyReleased(KeyEvent e) {
        Node node = Core.getShared().getFocusManager().findFocusedNode(parent);
        org.joshy.gfx.event.KeyEvent evt = new org.joshy.gfx.event.KeyEvent(
                org.joshy.gfx.event.KeyEvent.KeyReleased,
                org.joshy.gfx.event.KeyEvent.getKeyCodeFromAWT(e.getKeyCode()),
                node,
                e.getKeyChar(),
                e.isShiftDown(),
                e.isControlDown(),
                e.isAltDown(),
                e.isMetaDown()
                );
        EventBus.getSystem().publish(evt);
    }

    public void windowOpened(WindowEvent e) {
        //u.p("window opened");
    }

    public void windowClosing(WindowEvent e) {
//        u.p("window is closing");
        EventBus.getSystem().publish(new org.joshy.gfx.event.WindowEvent(org.joshy.gfx.event.WindowEvent.Closing, parent.getStage()));
    }

    public void windowClosed(WindowEvent e) {
//        u.p("window is closed");
    }

    public void windowIconified(WindowEvent e) {
//        u.p("window iconified");
    }

    public void windowDeiconified(WindowEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowActivated(WindowEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void windowDeactivated(WindowEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void windowStateChanged(WindowEvent windowEvent) {
        u.p("window stage changed: " + windowEvent);
    }
}
