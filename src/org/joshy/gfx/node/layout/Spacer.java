package org.joshy.gfx.node.layout;

import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.control.Control;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: Aug 28, 2010
* Time: 6:53:18 PM
* To change this template use File | Settings | File Templates.
*/
public class Spacer extends Control {
    @Override
    public void doLayout() {
        setWidth(0);
        setHeight(0);
    }

    @Override
    public void doPrefLayout() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void doSkins() {
    }
    @Override
    public void draw(GFX g) {
    }
}
