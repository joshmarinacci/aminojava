package org.joshy.gfx.sidehatch;

import org.joshy.gfx.node.control.Checkbox;
import org.joshy.gfx.node.layout.VFlexBox;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Nov 30, 2010
 * Time: 3:05:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class InspectorEditor extends VFlexBox {
    Checkbox tintCheckbox;
    Checkbox layoutboundsCheckbox;
    Checkbox visualboundsCheckbox;
    Checkbox inputboundsCheckbox;

    public InspectorEditor() {
        tintCheckbox = new Checkbox("tint");
        add(tintCheckbox);
        layoutboundsCheckbox = new Checkbox("Layout Bounds");
        add(layoutboundsCheckbox);
        visualboundsCheckbox = new Checkbox("Visual Bounds");
        add(visualboundsCheckbox);
        inputboundsCheckbox = new Checkbox("Input Bounds");
        add(inputboundsCheckbox);
    }
}
