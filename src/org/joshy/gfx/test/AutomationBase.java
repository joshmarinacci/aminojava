package org.joshy.gfx.test;

import org.joshy.gfx.Core;
import org.joshy.gfx.event.*;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.NodeUtils;
import org.joshy.gfx.node.Parent;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.stage.EventPublisher;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.stage.swing.SwingStage;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Oct 25, 2010
 * Time: 12:08:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class AutomationBase {
    private static List<Callback> queue = new ArrayList<Callback>();
    private static Parent root;

    private static class DoLater {
        private Callback callback;
        private DoLater next;

        public void setCallback(Callback callback) {
            this.callback = callback;
        }

        public void setNext(DoLater dl) {
            this.next = dl;
        }

        public void invoke() {
            Core.getShared().defer(new Runnable(){
                @Override
                public void run() {
                    try {
                        if(callback != null) {
                            callback.call(null);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(next != null) {
                        next.invoke();
                    }
                }
            });
        }
    }

    protected static void processAndQuit(final Stage stage) throws Exception {
        root = ((SwingStage)stage).getRoot();
        DoLater doLater = new DoLater();
        final DoLater first = doLater;
        for(Callback c : queue) {
            doLater.setCallback(c);
            DoLater dl = new DoLater();
            doLater.setNext(dl);
            doLater = dl;
        }
        Core.getShared().defer(new Runnable(){
            @Override
            public void run() {
                try {
                    first.invoke();
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
    }

    protected static void type(String s) {
        for(final char ch : s.toCharArray()) {
            queue.add(new Callback(){
                @Override
                public void call(Object event) throws Exception {
                    Node node = Core.getShared().getFocusManager().findFocusedNode(root);
                    //u.p("focused node = " + node);
//                    u.p("typing: " + ch);
                    KeyEvent evt = new KeyEvent(
                            KeyEvent.KeyPressed,
                            KeyEvent.getKeyCodeFromChar(ch),
                            node,
                            ch,
                            false,false,false,false);
                    EventBus.getSystem().publish(evt);
                }
            });
        }
    }

    protected static void click(final String s) {
        queue.add(new Callback(){
            @Override
            public void call(Object event) throws Exception {
                Node node = findNode((Node)root,s.substring(1));
//                u.p("found the node: "+ node);
                Point2D ptx = NodeUtils.convertToScene(node, 0, 0);
//                u.p("pt = " + ptx);
                Point2D.Double pt = EventPublisher.convertSceneToNode(ptx.getX(), ptx.getY(), node);
                Event e = new MouseEvent(MouseEvent.MousePressed,pt.getX(),pt.getY(),node);
                EventBus.getSystem().publish(e);
            }
        });
        queue.add(new Callback(){
            @Override
            public void call(Object event) throws Exception {
                Node node = findNode((Node)root,s.substring(1));
//                u.p("found the node: "+ node);
                Point2D ptx = NodeUtils.convertToScene(node, 0, 0);
//                u.p("pt = " + ptx);
                Point2D.Double pt = EventPublisher.convertSceneToNode(ptx.getX(), ptx.getY(), node);
                Event e = new MouseEvent(MouseEvent.MouseReleased,pt.getX(),pt.getY(),node);
                EventBus.getSystem().publish(e);
            }
        });
    }

    private static Node findNode(Node root, String s) {
//        u.p("looking at: " + root + " for " + s);
        if(root instanceof Control) {
            if(s.equals(((Control)root).getId())) {
                return (Control)root;
            }
        }
        if(root instanceof Parent) {
            for(Node n : ((Parent)root).children()) {
                Node res = findNode(n, s);
                if(res != null) return res;
            }
        }
        return null;
    }

    protected static void click(final double x, final double y) {
        queue.add(new Callback(){
            @Override
            public void call(Object event) throws Exception {
                Node node = EventPublisher.findTopNode(root, x, y);
                Point2D.Double pt = EventPublisher.convertSceneToNode(x, y, node);
                Event e = new MouseEvent(MouseEvent.MousePressed,pt.getX(),pt.getY(),node);
                EventBus.getSystem().publish(e);
            }
        });
        queue.add(new Callback(){
            @Override
            public void call(Object event) throws Exception {
                Node node = EventPublisher.findTopNode(root, x, y);
                Event e = new MouseEvent(MouseEvent.MouseReleased,x,y,node);
                EventBus.getSystem().publish(e);
            }
        });
    }


}
