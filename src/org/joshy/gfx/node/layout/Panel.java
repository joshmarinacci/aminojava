package org.joshy.gfx.node.layout;

import org.joshy.gfx.SkinManager;
import org.joshy.gfx.css.BoxPainter;
import org.joshy.gfx.css.CSSSkin;
import org.joshy.gfx.css.SizeInfo;
import org.joshy.gfx.css.StyleInfo;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.util.u;

public class Panel extends Container {
    protected FlatColor fill = null;
    protected FlatColor borderColor = FlatColor.BLACK;
    private Callback<Panel> callback;
    protected StyleInfo styleInfo;
    protected SizeInfo sizeInfo;
    protected BoxPainter boxPainter;

    public Panel() {
        setSkinDirty();
    }

    public Panel onDoLayout(Callback<Panel> callback) {
        this.callback = callback;
        return this;
    }

    @Override
    public void doSkins() {
        cssSkin = SkinManager.getShared().getCSSSkin();
        styleInfo = cssSkin.getStyleInfo(this, null);
        setLayoutDirty();
    }
    
    @Override
    public void doPrefLayout() {
        sizeInfo = cssSkin.getSizeInfo(this,styleInfo,"");

        super.doPrefLayout();
        if(prefWidth != CALCULATED) {
            setWidth(prefWidth);
            sizeInfo.width = prefWidth;
        } else {
            setWidth(sizeInfo.width);
        }
        if(prefHeight != CALCULATED) {
            setHeight(prefHeight);
            sizeInfo.height = prefHeight;
        } else {
            setHeight(sizeInfo.height);
        }
    }

    @Override
    public void doLayout() {
        if(callback != null) {
            try {
                callback.call(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            super.doLayout();
        }
        boxPainter = cssSkin.createBoxPainter(this, styleInfo, sizeInfo, "", CSSSkin.State.None);
    }


    @Override
    public void draw(GFX g) {
        if(!isVisible()) return;
        g.setOpacity(getOpacity());
        drawSelf(g);
        for(Node child : children) {
            if(!child.isVisible()) continue;
            g.translate(child.getTranslateX(),child.getTranslateY());
            child.draw(g);
            g.translate(-child.getTranslateX(),-child.getTranslateY());
        }
        this.drawingDirty = false;
        g.setOpacity(1.0);
    }

    protected void drawSelf(GFX g) {
        if(fill != null) {
            g.setPaint(fill);
            g.fillRect(0,0,getWidth(),getHeight());
            g.setPaint(borderColor);
            g.drawRect(0,0,getWidth(),getHeight());
            return;
        }
        if(boxPainter == null) {
            u.p("Panel invoked in an improper way.  this = " + this);
        }
        boxPainter.draw(g, styleInfo, sizeInfo, "");
        return;
    }

    public Panel setFill(FlatColor fill) {
        this.fill = fill;
        return this;
    }

    public Panel add(Node ... nodes) {
        for(Node node : nodes) {
            this.add(node);
        }
        return this;
    }

}
