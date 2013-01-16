package org.joshy.gfx.node.control;

import org.joshy.gfx.css.CSSMatcher;
import org.joshy.gfx.css.CSSSkin;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;

/**
 * A basic link button. It's a selectable button with custom styling.
 * The button only 'looks' like a hyperlink (ie, has underlined text). To make
 * it actually open the user's browser when you click on it, you must add an
 * event handler like so:
 * Button button = new Button("a button")
 *      .onClicked(new Callback<ActionEvent>() {
 *          public void call(ActionEvent event) {
 *              OSUtil.openBrowser("http://www.eatmorepixels.com");
 *          }
 *      });
 */

public class Linkbutton extends Button {
    public Linkbutton(CharSequence text) {
        super(text);
        selectable = false;
    }

    @Override
    public void draw(GFX g) {
        if(!isVisible()) return;
        if(cssSkin != null) {
            if(styleInfo == null) {
                doPrefLayout();
            }
            CSSMatcher matcher = cssSkin.createMatcher(this, CSSSkin.State.None);
            if(isHovered()) {
                matcher = cssSkin.createMatcher(this, CSSSkin.State.Hover);
            }
            if(isPressed()) {
                matcher = cssSkin.createMatcher(this, CSSSkin.State.Pressed);
            }
            Bounds bounds = new Bounds(0,0,getWidth(),getHeight());
            cssSkin.drawBackground(g, matcher, bounds);
            cssSkin.drawBorder(g, matcher, bounds);
            cssSkin.drawText(g, matcher, bounds, getText().toString());
            return;
        }
    }

}
