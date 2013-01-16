package org.joshy.gfx.node.shape;

import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.draw.Paint;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Dec 9, 2010
 * Time: 8:01:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class Text extends Node {
    protected Paint fill = FlatColor.GRAY;
    private String text;
    private Font font = Font.DEFAULT;

    public Text setFill(Paint fillPaint) {
        this.fill = fillPaint;
        return this;
    }
    protected Paint getFill() {
        return this.fill;
    }

    @Override
    public void draw(GFX g) {
        g.setPaint(getFill());
        if(getText() != null) {
            g.drawText(getText(),getFont(),0,0);
        }
    }

    @Override
    public Bounds getVisualBounds() {
        return new Bounds(0,0,0,0);
    }

    @Override
    public Bounds getInputBounds() {
        return getVisualBounds();
    }

    public String getText() {
        return text;
    }

    public Font getFont() {
        return font;
    }

    public Text setText(String text) {
        this.text = text;
        return this;
    }

    public Text setFont(Font font) {
        this.font = font;
        return this;
    }
}
