package org.joshy.gfx.node.layout;

import org.joshy.gfx.css.CSSMatcher;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Oct 5, 2010
 * Time: 2:03:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class TitlePanel extends Panel {
    private String title = "Untitled Panel";

    protected void drawSelf(GFX g) {
        if(fill != null) {
            g.setPaint(fill);
            g.fillRect(0,0,getWidth(),getHeight());
            g.setPaint(borderColor);
            g.drawRect(0,0,getWidth(),getHeight());
            return;
        }


        Bounds bounds = new Bounds(0,0,getWidth(),getHeight());
        CSSMatcher matcher = new CSSMatcher(this);
        cssSkin.drawBackground(g,matcher,bounds);
        cssSkin.drawBorder(g,matcher,bounds);

        double tw = Font.DEFAULT.calculateWidth(title);
        double th = Font.DEFAULT.calculateHeight(title);
        Bounds textBounds = new Bounds(styleInfo.margin.getLeft()+10,styleInfo.margin.getTop()-th/2, tw+6, th);
        matcher.pseudoElement = "title";
        cssSkin.drawBackground(g,matcher,textBounds);
        cssSkin.drawBorder(g,matcher,textBounds);
        cssSkin.drawText(g,matcher,textBounds,title);
        return;
    }

}
