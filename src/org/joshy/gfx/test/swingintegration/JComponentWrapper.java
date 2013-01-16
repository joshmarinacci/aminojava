package org.joshy.gfx.test.swingintegration;

import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.NodeUtils;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.layout.Container;
import org.joshy.gfx.stage.AWTEventPublisher;
import org.joshy.gfx.stage.swing.SwingGFX;
import org.joshy.gfx.util.PerformanceTracker;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Nov 15, 2010
 * Time: 8:15:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class JComponentWrapper extends JComponent {
    private Control contentNode;
    private Container contentLayer;
    private AWTEventPublisher publisher;
    private boolean skinsDirty;
    private boolean layoutDirty;
    private boolean drawingDirty;

    public JComponentWrapper() {

        contentLayer = new org.joshy.gfx.node.layout.Container() {
            @Override
            protected void setSkinDirty() {
                super.setSkinDirty();
                JComponentWrapper.this.skinsDirty = true;
            }

            @Override
            public void setSkinDirty(Node node) {
                super.setSkinDirty(node);
                JComponentWrapper.this.repaint();
                JComponentWrapper.this.skinsDirty = true;
            }

            @Override
            protected void setLayoutDirty() {
                super.setLayoutDirty();
                JComponentWrapper.this.layoutDirty = true;
            }

            @Override
            public void setDrawingDirty(Node node) {
                super.setDrawingDirty(node);
                JComponentWrapper.this.repaint();
                JComponentWrapper.this.drawingDirty = true;
            }
            @Override
            public void setLayoutDirty(Node node) {
                super.setLayoutDirty(node);
                JComponentWrapper.this.repaint();
                JComponentWrapper.this.layoutDirty = true;
            }

            @Override
            public void doPrefLayout() {
                for(Node n : children()) {
                    if(n instanceof Control) {
                        Control c = (Control) n;
                        c.doPrefLayout();
                    }
                }
            }

            @Override
            public void doLayout() {
                for(Node n : children()) {
                    if(n instanceof Control) {
                        Control c = (Control) n;
                        c.setWidth(getWidth());
                        c.setHeight(getHeight());
                        c.doLayout();
                    }
                }
            }

            @Override
            public void draw(GFX g) {
                g.setPaint(FlatColor.RED);
                g.fillRect(0,0,getWidth(),getHeight());
                for(Node child : children) {
                    g.translate(child.getTranslateX(),child.getTranslateY());
                    child.draw(g);
                    g.translate(-child.getTranslateX(),-child.getTranslateY());
                }
                this.drawingDirty = false;
            }
        };

        publisher = new AWTEventPublisher(contentLayer);
        this.addMouseListener(publisher);
        this.addMouseMotionListener(publisher);
        this.addMouseWheelListener(publisher);
        this.addKeyListener(publisher);
        this.setFocusTraversalKeysEnabled(false);
    }

    public void setContent(Control content) {
        this.contentNode = content;
        this.contentLayer.removeAll();
        contentLayer.add(contentNode);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        this.skinsDirty = true;
        this.layoutDirty = true;
        this.drawingDirty = true;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        if(skinsDirty) {
            PerformanceTracker.getInstance().skinStart();
            NodeUtils.doSkins(contentLayer);
            PerformanceTracker.getInstance().skinEnd();
            skinsDirty = false;
        }
        
        if(layoutDirty) {
            PerformanceTracker.getInstance().layoutStart();
            contentLayer.setWidth(getWidth());
            contentLayer.setHeight(getHeight());
            contentLayer.doPrefLayout();
            
            contentLayer.setWidth(getWidth());
            contentLayer.setHeight(getHeight());
            contentLayer.doLayout();
            
            layoutDirty = false;
            PerformanceTracker.getInstance().layoutEnd();
        }

        PerformanceTracker.getInstance().drawStart();
        GFX gfx = new SwingGFX((Graphics2D) graphics);
        contentLayer.draw(gfx);
        gfx.dispose();
        PerformanceTracker.getInstance().drawEnd();
        drawingDirty = false;
    }
}
