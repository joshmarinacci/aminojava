package org.joshy.gfx.stage;

import org.joshy.gfx.draw.TransformNode;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.Parent;
import org.joshy.gfx.node.control.ScrollPane;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

public class EventPublisher {
    protected Parent parent;

    public EventPublisher(Parent parent) {
        this.parent = parent;
    }

    protected Node findTopNode(double x, double y) {
        return findTopNode(parent,x,y);
    }

    public static Node findTopNode(Parent parent, double x, double y) {
        if(parent instanceof ScrollPane) {
            if(!((ScrollPane)parent).getInputBounds().contains(x,y)) {
                return null;
            }
        }

        if(parent instanceof Node) {
            if(parent instanceof TransformNode) {
                TransformNode tn = (TransformNode) parent;
                AffineTransform at = AffineTransform.getTranslateInstance(0,0);
                at.translate(tn.getTranslateX(),tn.getTranslateY());
                at.rotate(Math.toRadians(tn.getRotate()));
                at.scale(tn.getScaleX(),tn.getScaleY());
                Point2D pt = null;
                try {
                    pt = at.inverseTransform(new Point2D.Double(x,y), null);
                } catch (NoninvertibleTransformException e) {
                    e.printStackTrace();
                }
                x = pt.getX();
                y = pt.getY();

            } else {
                x -= ((Node)parent).getTranslateX();
                y -= ((Node)parent).getTranslateY();
            }
            if(!((Node)parent).isVisible()) return null;
        }

        for(Node node : parent.reverseChildren()) {
            if(node instanceof Parent) {
                Node pc = findTopNode(((Parent)node),x,y);
                if(pc != null) return pc;
            }
            if(node.getInputBounds() != null && node.getInputBounds().contains(x,y) && node.isVisible()) {
                return node;
            }
        }
        return null;
    }

    public static Point2D.Double convertSceneToNode(double x, double y, Node node) {
        if(node == null) return new Point2D.Double(x,y);
        Point2D.Double point = null;
        if(node.getParent() instanceof Node) {
            point = convertSceneToNode(x,y, (Node) node.getParent());
            if(node instanceof TransformNode) {
                TransformNode tn = (TransformNode) node;
                AffineTransform at = AffineTransform.getTranslateInstance(0,0);
                at.translate(tn.getTranslateX(),tn.getTranslateY());
                at.rotate(Math.toRadians(tn.getRotate()));
                Point2D pt = null;
//                try {
                    pt = at.transform(point, null);
//                } catch (NoninvertibleTransformException e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
//                u.p("converted point: " + point + " to " + pt);
                point = new Point2D.Double(pt.getX(),pt.getY());
            } else {
                point.x -= node.getTranslateX();
                point.y -= node.getTranslateY();
            }
        } else {
            point = new Point2D.Double(x,y);
        }
        return point;
    }
}
