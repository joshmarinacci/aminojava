package org.joshy.gfx.node.control;

import org.joshy.gfx.SkinManager;
import org.joshy.gfx.css.*;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.ChangedEvent;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.MouseEvent;
import org.joshy.gfx.node.Bounds;

import java.util.Date;

/**
* A PopupMenu is a popup control that can be used for context menus or dropdowns.
* It takes a ListModel for the list of items but draws them with a menu like
 * style by default. It fires ChangedEvents when the selected item changes. 
*/
public class PopupMenu extends Control {
    private ListModel model;
    private int hoverRow = -1;
    private long openTime;
    private ListView.TextRenderer textRenderer;
    private StyleInfo styleInfo;
    private StyleInfo itemStyleInfo;
    private StyleInfo selectedItemStyleInfo;
    private SizeInfo sizeInfo;
    private SizeInfo itemSizeInfo;
    private SizeInfo selectedItemSizeInfo;
    private BoxPainter boxPainter;
    private BoxPainter itemPainter;
    private BoxPainter selectedItemPainter;

    public PopupMenu() {
        setVisible(true);
        EventBus.getSystem().addListener(this, MouseEvent.MouseAll, new Callback<MouseEvent>(){
            public void call(MouseEvent event) {
                processMouse(event);
            }
        });
    }
    public PopupMenu(ListModel model, Callback<ChangedEvent> callback) {
        setVisible(true);
        this.model = model;
        EventBus.getSystem().addListener(this, ChangedEvent.IntegerChanged,callback);
        EventBus.getSystem().addListener(this, MouseEvent.MouseAll, new Callback<MouseEvent>(){
            public void call(MouseEvent event) {
                processMouse(event);
            }
        });
    }

    public void setModel(ListModel model) {
        this.model = model;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        openTime = new Date().getTime();
    }

    private void processMouse(MouseEvent event) {
        long currentTime = new Date().getTime();
        if(event.getType() == MouseEvent.MouseDragged || event.getType() == MouseEvent.MouseDraggedRaw || event.getType() == MouseEvent.MouseMoved) {
            hoverRow = (int)(event.getY()/rowHeight);
            setDrawingDirty();
        }
        if(event.getType() == MouseEvent.MouseReleased) {
            hoverRow = (int)(event.getY()/rowHeight);
            //if click open, do nothing
//            if(currentTime - openTime < 500) {
//                u.p("did a click open");
//            } else {
//                u.p("closing");
                //else fire selection and hide
                if(hoverRow >= 0 && hoverRow < model.size()) {
                    fireSelection(hoverRow);
                }
                setDrawingDirty();
                setVisible(false);
//            }
        }
    }

    private void fireSelection(int row) {
        EventBus.getSystem().publish(new ChangedEvent(ChangedEvent.IntegerChanged,(Integer)row,this));
    }

    double rowHeight = 25;
    static double spacer = 5;
    double arc = 10;

    @Override
    public void doSkins() {
        cssSkin = SkinManager.getShared().getCSSSkin();
        styleInfo = cssSkin.getStyleInfo(this, null);
        itemStyleInfo = cssSkin.getStyleInfo(this,null,"item");
        selectedItemStyleInfo = cssSkin.getStyleInfo(this,null,"selected-item");
        setLayoutDirty();
    }

    @Override
    public void doPrefLayout() {
        sizeInfo = cssSkin.getSizeInfo(this,styleInfo,"");
        itemSizeInfo = cssSkin.getSizeInfo(this,itemStyleInfo,"","item");
        selectedItemSizeInfo = cssSkin.getSizeInfo(this,selectedItemStyleInfo,"","selected-item");
    }

    @Override
    public void doLayout() {
        setHeight( rowHeight * model.size() + spacer*2);
        double maxWidth = 20;
        for(int row = 0; row<getModel().size(); row++) {
            Object o = getModel().get(row);
            String s =o.toString();
            if(textRenderer != null) {
                s = textRenderer.toString(null,s,row);
            }
            maxWidth = Math.max(maxWidth,itemStyleInfo.font.calculateWidth(s)+15);
        }
        setWidth(maxWidth);
        sizeInfo.width = getWidth();
        sizeInfo.height = getHeight();
        boxPainter = cssSkin.createBoxPainter(this, styleInfo, sizeInfo, "", CSSSkin.State.None);
        itemPainter = cssSkin.createBoxPainter(this, itemStyleInfo, itemSizeInfo, "", CSSSkin.State.None, "item");
        selectedItemPainter = cssSkin.createBoxPainter(this, selectedItemStyleInfo, selectedItemSizeInfo, "", CSSSkin.State.None, "selected-item");
    }

    @Override
    public void draw(GFX g) {
        if(!isVisible()) return;

        //Bounds bounds = new Bounds(0,0,getWidth(),getHeight());
        CSSMatcher matcher = new CSSMatcher(this);

        boxPainter.draw(g,styleInfo,sizeInfo,"");

        for(int i=0; i<model.size(); i++) {
            Object o = model.get(i);
            double rowy = i*rowHeight;
            Bounds itemBounds = new Bounds(1, rowy + spacer, getWidth() - 2, rowHeight);
            matcher.pseudoElement = "item";
            if(i == hoverRow) {
                matcher.pseudoElement = "selected-item";
                selectedItemSizeInfo.width= itemBounds.getWidth();
                selectedItemSizeInfo.height = itemBounds.getHeight();
                g.translate(itemBounds.getX(),itemBounds.getY());
                selectedItemPainter.draw(g,selectedItemStyleInfo,selectedItemSizeInfo,"");
                g.translate(-itemBounds.getX(),-itemBounds.getY());
            } else {
                itemSizeInfo.width= itemBounds.getWidth();
                itemSizeInfo.height = itemBounds.getHeight();
                g.translate(itemBounds.getX(),itemBounds.getY());
                itemPainter.draw(g,itemStyleInfo,itemSizeInfo,"");
                g.translate(-itemBounds.getX(),-itemBounds.getY());
            }
            int col = cssSkin.getCSSSet().findColorValue(matcher, "color");
            g.setPaint(new FlatColor(col));
            drawText(g, o, rowy, i);
        }
    }

    private void drawText(GFX g, Object o, double rowy, int i) {
        String s = o.toString();
        if(textRenderer != null) {
            s = textRenderer.toString(null,o,i);
        }
        Font.drawCenteredVertically(g, s, cssSkin.getDefaultFont(),
                3,rowy+spacer,getWidth(),rowHeight,true);
    }


    public ListModel getModel() {
        return model;
    }

    public void setCallback(Callback<ChangedEvent> callback) {
        EventBus.getSystem().addListener(this, ChangedEvent.IntegerChanged,callback);
    }

    public void setTextRenderer(ListView.TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
    }
}
