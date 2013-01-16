package org.joshy.gfx.node.control;

import org.joshy.gfx.SkinManager;
import org.joshy.gfx.css.BoxPainter;
import org.joshy.gfx.css.CSSSkin;
import org.joshy.gfx.css.SizeInfo;
import org.joshy.gfx.css.StyleInfo;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.MouseEvent;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.NodeUtils;
import org.joshy.gfx.stage.Stage;

import java.awt.geom.Point2D;

/**
 * Tooltip is a small popup window that appears when a user hovers the mouse
 * over the target control.
 */
public class Tooltip extends Control {
    private Control target;
    private String text;
    private StyleInfo styleInfo;
    private SizeInfo sizeInfo;
    private BoxPainter boxPainter;

    public Tooltip(Control target, String text) {
        this.target = target;
        setText(text);
        EventBus.getSystem().addListener(target, MouseEvent.MouseAll, new Callback<MouseEvent>(){
            @Override
            public void call(MouseEvent event) throws Exception {
                if(MouseEvent.MouseEntered == event.getType()) {
                    if(event.getSource() instanceof Control) {
                        Control source = (Control) event.getSource();
                        Stage stage = source.getParent().getStage();
                        stage.getPopupLayer().add(Tooltip.this);
                        Bounds bounds = source.getVisualBounds();
                        Point2D pt = NodeUtils.convertToScene(source, bounds.getWidth(), bounds.getHeight());
                        setTranslateX(pt.getX());
                        setTranslateY(pt.getY());
                        setVisible(true);
                    }
                }
                if(MouseEvent.MouseExited == event.getType()) {
                    if(event.getSource() instanceof Control) {
                        Stage stage = ((Control)event.getSource()).getParent().getStage();
                        stage.getPopupLayer().remove(Tooltip.this);
                        setVisible(false);
                    }
                }
            }
        });
    }


    @Override
    public void doSkins() {
        cssSkin = SkinManager.getShared().getCSSSkin();
        styleInfo = cssSkin.getStyleInfo(this, null);
        setLayoutDirty();
    }

    @Override
    public void doPrefLayout() {
        sizeInfo = cssSkin.getSizeInfo(this,styleInfo,text);
        if(prefWidth != CALCULATED) {
            setWidth(prefWidth);
            sizeInfo.width = prefWidth;
        } else {
            setWidth(sizeInfo.width);
        }
        setHeight(sizeInfo.height);
    }

    @Override
    public void doLayout() {
        sizeInfo.width = getWidth();
        sizeInfo.height = getHeight();
        boxPainter = cssSkin.createBoxPainter(this,styleInfo,sizeInfo,text, CSSSkin.State.None);
    }

    @Override
    public void draw(GFX g) {
        if(!isVisible()) return;
        if(sizeInfo == null) {
            doPrefLayout();
        }
        boxPainter.draw(g, styleInfo, sizeInfo, text);        
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
