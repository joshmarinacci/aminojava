package org.joshy.gfx.node.control;

import org.joshy.gfx.draw.*;
import org.joshy.gfx.event.ActionEvent;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.layout.Container;

/**
 * A disclosure panel is a panel which contains one child along with a disclosure button
 * (usually a + or a triangle). When the user clicks on the button the child control will
 * be show or hidden.  The panel will resize itself to the size of the child, causing
 * the proper reflow when the user shows/hides the child.
 */
public class DisclosurePanel extends Container {
    private Control title;
    private Control content;
    private Button button;
    private boolean open;
    private Position pos = Position.Top;
    public enum Position { Top,Bottom,Left,Right }

    public DisclosurePanel() {
        this.button = new Button("+") {
            @Override
            public void draw(GFX g) {
                g.setPaint(FlatColor.BLACK);
                switch (pos) {
                    case Top:
                        if(isOpen()) drawTriangleDown(g); else  drawTriangleRight(g);
                        break;
                    case Bottom:
                        break;
                    case Left:
                        break;
                    case Right:
                        if(isOpen()) drawTriangleLeft(g); else  drawTriangleDown(g);
                        break;
                }
            }
        };
        add(button);

        EventBus.getSystem().addListener(button, ActionEvent.Action, new Callback<ActionEvent>(){
            @Override
            public void call(ActionEvent event) throws Exception {
                setOpen(!isOpen());
            }
        });
    }

    public DisclosurePanel setTitle(Control title) {
        this.title = title;
        add(title);
        return this;
    }

    public DisclosurePanel setContent(Control content) {
        this.content = content;
        add(content);
        return this;
    }

    public DisclosurePanel setPosition(Position pos) {
        this.pos = pos;
        return this;
    }

    @Override
    public void doPrefLayout() {
        super.doPrefLayout();
        double w1 = 0;
        double w2 = 0;
        switch(pos) {
            case Top:
                w1 = button.getLayoutBounds().getWidth()+title.getLayoutBounds().getWidth();
                w2 = 0;
                double h1 = button.getLayoutBounds().getHeight();
                if(isOpen()) {
                    w2 = content.getLayoutBounds().getWidth();
                    h1+=content.getLayoutBounds().getHeight();
                }
                setWidth(Math.max(w1,w2));
                setHeight(h1);
                break;
            case Right:
                w1 = button.getLayoutBounds().getWidth();
                w2 = title.getLayoutBounds().getHeight();
                h1 = button.getLayoutBounds().getHeight() + title.getLayoutBounds().getWidth();
                if(isOpen()) {
                    double w3 = content.getLayoutBounds().getWidth();
                    w1 += w3;
                    w2 += w2;
                }
                setWidth(Math.max(w1,w2));
                setHeight(h1);
                break;
        }
    }

    @Override
    public void doLayout() {
        Bounds bb = button.getLayoutBounds();
        Bounds tb = title.getLayoutBounds();
        content.setVisible(isOpen());
        switch(pos) {
            case Top:
                button.setTranslateX(0);
                button.setTranslateY(0);

                title.setTranslateX(bb.getX2());
                title.setTranslateY(0);
                double y = bb.getY2();
                y = Math.max(y,tb.getY2());
                content.setTranslateX(0);
                content.setTranslateY(y);
                content.setWidth(getWidth());
                break;
            case Right:
                double i = Math.max(bb.getWidth(), tb.getHeight());
                button.setTranslateX(getWidth()-bb.getWidth());
                button.setTranslateY(0);
                title.setTranslateX(getWidth() - tb.getHeight());
                title.setTranslateY(bb.getY2());
                content.setTranslateX(0);
                content.setTranslateY(0);
                content.setWidth(getWidth()-i);
                content.setHeight(getHeight());
                break;
        }

        super.doLayout();
    }

    @Override
    public void draw(GFX g) {
        Bounds bb = button.getLayoutBounds();
        Bounds tb = title.getLayoutBounds();
        g.setPaint(new FlatColor(0xcccccc));
        double cw = 0;
        double ch = 0;
        switch(pos) {
            case Top:
                cw = getWidth();
                ch = Math.max(bb.getHeight(),tb.getHeight());
                MultiGradientFill gradTop = new LinearGradientFill()
                        .setStartX(0).setEndX(0).setStartY(0).setEndY(ch)
                        .addStop(0,new FlatColor(0xebebeb))
                        .addStop(1,new FlatColor(0xa1a1a1));
                g.setPaint(gradTop);
                g.fillRect(0,0,cw,ch);
                g.setPaint(FlatColor.GRAY);
                g.drawRect(0,0,cw,ch);
                break;
            case Right:
                cw = Math.max(bb.getWidth(),tb.getHeight());
                MultiGradientFill gradRight = new LinearGradientFill()
                        .setStartX(0).setEndX(cw)
                        .setStartY(0).setEndY(0)
                        .addStop(0,new FlatColor(0xebebeb))
                        .addStop(1,new FlatColor(0xa1a1a1));
                g.push();
                g.translate(getWidth()-cw,0);
                g.setPaint(gradRight);
                g.fillRect(0,0, cw,getHeight());
                g.setPaint(FlatColor.GRAY);
                g.drawRect(0, 0, cw, getHeight());
                g.pop();
                break;
        }
        for(Control child : controlChildren()) {
            if(!child.isVisible()) continue;
            if(child == title && pos == DisclosurePanel.Position.Right) {
                g.push();
                g.translate(child.getTranslateX(), child.getTranslateY());
                g.rotate(90, Transform.Z_AXIS);
                g.translate(0,-child.getLayoutBounds().getHeight());
                child.draw(g);
                g.pop();
            } else {
                g.translate(child.getTranslateX(),child.getTranslateY());
                child.draw(g);
                g.translate(-child.getTranslateX(), -child.getTranslateY());
            }
        }
    }

    private void drawTriangleDown(GFX g) {
        g.translate(5,10+3);
        double[] points = new double[]{0,0, 14,0, 7, 9};
        g.fillPolygon(points);
        g.translate(-5,-10-3);
    }
    private void drawTriangleRight(GFX g) {
        g.translate(5+3,10);
        double[] points = new double[]{0,0, 9,7, 0,14};
        g.fillPolygon(points);
        g.translate(-5-3,-10);
    }
    private void drawTriangleLeft(GFX g) {
        g.translate(5+3,10);
        double[] points = new double[]{9,0, 0,7, 9,14};
        g.fillPolygon(points);
        g.translate(-5-3,-10);
    }

    public boolean isOpen() {
        return open;
    }

    public DisclosurePanel setOpen(boolean open) {
        this.open = open;
        setLayoutDirty();
        button.setText(isOpen()?"+":"+");
        return this;
    }
}
