package org.joshy.gfx.node.layout;

import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.control.Control;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 19, 2010
 * Time: 8:33:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class FlowPanel extends Panel {

    @Override
    public void doPrefLayout() {
        for(Node child : children) {
            if(child instanceof Control) {
                Control control = (Control) child;
                control.doPrefLayout();
            }
        }
    }

    @Override
    public void doLayout() {
        double x = 0;
        double y = 0;
        for(Node child : children) {
            if(child instanceof Control) {
                Control control = (Control) child;
                control.doLayout();
            }
            Bounds bounds = child.getVisualBounds();
            if(x+bounds.getWidth() > getWidth()) {
                x = 0;
                y+=bounds.getHeight();
            }
            child.setTranslateX(x);
            child.setTranslateY(y);
            x+=bounds.getWidth();
        }
    }

}
