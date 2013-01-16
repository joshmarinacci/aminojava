package org.joshy.gfx.node.layout;

import org.joshy.gfx.css.CSSMatcher;
import org.joshy.gfx.css.CSSSkin;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.control.Control;

import java.util.HashMap;
import java.util.Map;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: Aug 28, 2010
* Time: 6:51:21 PM
* To change this template use File | Settings | File Templates.
*/
public abstract class FlexBox extends Panel {
    protected int spacing;

    public enum Align { Stretch, Right, Bottom, Top, Left, Baseline };
    protected Align align = Align.Top;
    protected static final double NONE = 0;
    protected Map<Control,Double> spaceMap = new HashMap<Control,Double>();

    public FlexBox() {
    }

    @Override
    public void doSkins() {
        super.doSkins();
        CSSMatcher matcher = CSSSkin.createMatcher(this, CSSSkin.State.None);
        spacing = cssSkin.getCSSSet().findIntegerValue(matcher,"spacing");
    }

    public FlexBox setBoxAlign(Align baseline) {
        align = baseline;
        return this;
    }
    public FlexBox add(Control control, double flex) {
        spaceMap.put(control,flex);
        super.add(control);
        return this;
    }

    @Override
    public Panel add(Node ... nodes) {
        for(Node n : nodes) {
            if(n instanceof Control){
                this.spaceMap.put((Control)n,NONE);
            }
        }
        super.add(nodes);
        return this;
    }

    @Override
    public FlexBox add(Node control) {
        if(control instanceof Control) {
            this.spaceMap.put((Control)control,NONE);
        }
        super.add(control);
        return this;
    }
}
