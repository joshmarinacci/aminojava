package org.joshy.gfx.test.painter;

import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.draw.Image;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.MouseEvent;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.control.Control;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Feb 19, 2010
 * Time: 8:55:37 PM
 * To change this template use File | Settings | File Templates.
 */
class CanvasNode extends Control {
    private TileManager manager;
    private int offsetX;
    private int offsetY;

    
    CanvasNode(TileManager manager) {
        this.manager = manager;
        EventBus.getSystem().addListener(this,MouseEvent.MouseAll, new Callback<MouseEvent>() {
            double prevx = -1;
            double prevy = -1;

            public void call(MouseEvent event) {
                if (MouseEvent.MousePressed == event.getType()) {
                    prevx = event.getX();
                    prevy = event.getY();
                }
                if (MouseEvent.MouseDragged == event.getType()) {
                    if (event.isShiftPressed()) {
                        setOffsetX(getOffsetX() + (int)(event.getX()-prevx));
                        setOffsetY(getOffsetY() + (int)(event.getY()-prevy));
                        prevx = event.getX();
                        prevy = event.getY();
                    } else {
                        fillBrushAt(event.getX(), event.getY());
                    }
                }
            }
        });
    }

    @Override
    public void doSkins() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
    @Override
    public void doPrefLayout() {
        //noop
    }

    @Override
    public void doLayout() {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public void draw(GFX g) {
        Bounds clip = g.getClipRect();
        g.setPaint(FlatColor.WHITE);
        g.fillRect(0,0,clip.getWidth(),clip.getHeight());
        double startxd = -offsetX / 256.0;
        double startyd = -offsetY / 256.0;
        int startx = ((int) Math.floor(startxd)) * 256;
        int starty = ((int) Math.floor(startyd)) * 256;

        for (int y = starty; y < starty+clip.getHeight()+256; y += 256) {
            for (int x = startx; x < startx+clip.getWidth()+256; x += 256) {
                Image image = manager.getImage(x / 256, y / 256);
                g.drawImage(image, x + offsetX, y + offsetY);
            }
        }
    }

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
        setDrawingDirty();
    }

    public void fillBrushAt(double x, double y) {
        manager.fillBrushAt(x - offsetX, y - offsetY);
        setDrawingDirty();
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
        setDrawingDirty();
    }
}
