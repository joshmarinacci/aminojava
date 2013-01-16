package org.joshy.gfx.sidehatch;

import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.MouseEvent;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.Parent;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.control.Label;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.localization.Localization;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Nov 30, 2010
 * Time: 2:50:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class ControlBoundsOverlay extends Node {
    private InspectorEditor editor;
    private Stage stage;
    private Control target;
    private Point2D cursor;
    private DecimalFormat format;

    public ControlBoundsOverlay(InspectorEditor ie, Stage s) {
        format = new DecimalFormat();
        format.setMaximumFractionDigits(3);
        this.editor = ie;
        this.stage = s;
        EventBus.getSystem().addListener(MouseEvent.MouseMoved, new Callback<MouseEvent>(){
            @Override
            public void call(MouseEvent event) throws Exception {
                if(event.getSource() instanceof Control) {
                    Control control = (Control) event.getSource();
                    if(control.getParent().getStage() == stage) {
                        target = (Control) event.getSource();
                        cursor = event.getPointInSceneCoords();
                        setDrawingDirty();
                    }
                }
            }
        });
    }

    @Override
    public void draw(GFX g) {
        Stage s = this.getParent().getStage();
        //draw overlay
        if(editor.tintCheckbox.isSelected()) {
            g.setPaint(FlatColor.RED.deriveWithAlpha(0.3));
            g.fillRect(0,0,s.getWidth(),s.getHeight());
        }

        //draw node bounds
        Node root = s.getContent();
        draw(g,root);

        //draw floating indicator
        if(target != null) {
            drawIndicator(g);
        }
    }

    private void drawIndicator(GFX g) {
        g.translate(cursor.getX()+20,cursor.getY());
        g.setPaint(new FlatColor(0x202020).deriveWithAlpha(0.8));
        g.fillRoundRect(0,0,200,100,10,10);
        double col2 = 75;

        g.setPaint(new FlatColor(0xd0d0d0));
        g.drawText("class:",Font.DEFAULT,10,20);
        g.setPaint(FlatColor.WHITE);
        g.drawText(target.getClass().getSimpleName(), Font.DEFAULT,col2,20);

        g.setPaint(new FlatColor(0xd0d0d0));
        g.drawText("X , Y:",Font.DEFAULT,10,35);
        g.setPaint(FlatColor.WHITE);
        g.drawText(format.format(target.getTranslateX())+" , "+format.format(target.getTranslateY()),Font.DEFAULT,col2,35);

        g.setPaint(new FlatColor(0xd0d0d0));
        g.drawText("W x H:",Font.DEFAULT,10,50);
        g.setPaint(FlatColor.WHITE);
        g.drawText(format.format(target.getWidth())+" x "+format.format(target.getHeight()),Font.DEFAULT,col2,50);

        Localization.KeyString ks = getKeyString(target);
        g.setPaint(new FlatColor(0xc0c0c0));
        g.drawText("string key: ",Font.DEFAULT,10,65);
        if(ks != null) {
            g.setPaint(FlatColor.WHITE);
            g.drawText(ks.getPrefix() + "." + ks.getKeyname(),Font.DEFAULT,col2,65);
        } else {
            g.setPaint(new FlatColor(0xff5050));
            g.drawText("missing key!",Font.DEFAULT,col2,65);
        }
        g.translate(-cursor.getX()-20,-cursor.getY());
    }

    private Localization.KeyString getKeyString(Control target) {
        if(target instanceof Button) {
            Button b = (Button) target;
            if(b.getText() instanceof Localization.KeyString) {
                return (Localization.KeyString) b.getText();
            }
        }
        if(target instanceof Label) {
            Label l = (Label) target;
            if(l.getText() instanceof Localization.KeyString) {
                return (Localization.KeyString) l.getText();
            }
        }
        return null;
    }

    private void draw(GFX g, Node root) {
        if(root instanceof Control) {
            Control c = (Control) root;
            Bounds b = c.getLayoutBounds();
            if(editor.layoutboundsCheckbox.isSelected()) {
                g.setPaint(FlatColor.GREEN);
                g.drawRect(b.getX(),b.getY(),b.getWidth()-1,b.getHeight()-1);
            }
            if(editor.visualboundsCheckbox.isSelected()) {
                g.setPaint(FlatColor.PURPLE);
                Bounds vb = c.getVisualBounds();
                g.drawRect(vb.getX(),vb.getY(),vb.getWidth()-1,vb.getHeight()-1);
            }
            if(editor.inputboundsCheckbox.isSelected()) {
                g.setPaint(FlatColor.RED);
                Bounds ib = c.getInputBounds();
                g.drawRect(ib.getX(),ib.getY(),ib.getWidth()-1,ib.getHeight()-1);
            }
            g.translate(b.getX(),b.getY());
            if(c instanceof Parent) {
                Parent p = (Parent) c;
                for(Node n : p.children()) {
                    draw(g,n);
                }
            }
            g.translate(-b.getX(),-b.getY());
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
}
