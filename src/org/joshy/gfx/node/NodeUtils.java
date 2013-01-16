package org.joshy.gfx.node;

import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.stage.Stage;

import java.awt.geom.Point2D;

public class NodeUtils {
    public static Point2D convertToScene(Node node, double x, double y) {
        x+= node.getTranslateX();
        y+= node.getTranslateY();
        if(node.getParent() instanceof Node) {
            return convertToScene((Node) node.getParent(),x,y);
        }
        return new Point2D.Double(x,y);
    }

    public static Point2D convertToScene(Node node, Point2D pt) {
        return convertToScene(node, pt.getX(), pt.getY());
    }

    public static Bounds convertToScene(Node node, Bounds bounds) {
        Point2D pt = convertToScene(node, bounds.getX(), bounds.getY());
        return new Bounds(pt.getX(),pt.getY(),bounds.getWidth(),bounds.getHeight());
    }

    public static Point2D convertFromScene(Node node, Point2D point2D) {
        double x = point2D.getX();
        double y=  point2D.getY();
        x -= node.getTranslateX();
        y -= node.getTranslateY();
        if(node.getParent() instanceof Node) {
            return convertFromScene((Node)node.getParent(),new Point2D.Double(x,y));
        }
        return new Point2D.Double(x,y);
    }
    
    /*************************************************************************
     * Converts the given point from node coordinate space to screen coordinate space.
     * @param node the reference node. 
     * @param point2D the point in node coordinate space.
     * @return the point in screen coordinates.
     ************************************************************************/
    
    public static Point2D convertToScreen(Node node, double x, double y) {
	    Point2D pt = NodeUtils.convertToScene(node, x, y);
	    Stage stage = node.getParent().getStage();
	    pt = new Point2D.Double(pt.getX()+stage.getX(),pt.getY()+stage.getY());
	    return pt;
    }
    

    public static void doSkins(Control control) {
        if(control.isSkinDirty()) {
            if(control instanceof Parent) {
                for(Node n : ((Parent)control).children()) {
                    if(n instanceof Control) {
                        doSkins((Control) n);
                    }
                }
            }
            control.doSkins();
            control.skinsDirty = false;
        }
    }

}
