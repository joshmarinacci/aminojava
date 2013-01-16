package org.joshy.gfx.node.layout;

import org.joshy.gfx.css.CSSSkin;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Insets;
import org.joshy.gfx.node.control.Control;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: Aug 28, 2010
* Time: 6:52:09 PM
* To change this template use File | Settings | File Templates.
*/
public class HFlexBox extends FlexBox {
    private Insets insets;

    public HFlexBox() { }
    
    @Override
    public void doPrefLayout() {
        insets = styleInfo.calcContentInsets();

        //do shrink to fit unless a preferred width has been set
        double totalWidth = 0;
        double maxHeight = 0;
        for(Control c : controlChildren()) {
            if(!c.isVisible()) continue;
            c.doPrefLayout();
            Bounds bounds = c.getLayoutBounds();
            totalWidth += bounds.getWidth();
            totalWidth += spacing;
            maxHeight = Math.max(maxHeight,bounds.getHeight());
        }
        totalWidth -= spacing; //take off the last one
        if(getPrefWidth() == CALCULATED) {
            setWidth(totalWidth+insets.getLeft()+insets.getRight());
        } else {
            setWidth(getPrefWidth());
        }
        if(getPrefHeight() == CALCULATED) {
            setHeight(maxHeight+insets.getTop()+insets.getBottom());
        } else {
            setHeight(getPrefHeight());
        }
        sizeInfo = cssSkin.getSizeInfo(this,styleInfo,"");
    }

    double maxBaseline = 0;
    @Override
    public void doLayout() {
        if(insets == null) doPrefLayout();

        //calc total width & flex with children at their preferred width
        double totalWidth = 0;
        double totalFlex = 0;
        maxBaseline = 0;
        for(Control c : controlChildren()) {
            if(!c.isVisible()) continue;
            Bounds bounds = c.getLayoutBounds();
            totalWidth += bounds.getWidth();
            totalWidth += spacing;
            totalFlex += spaceMap.get(c);
            maxBaseline = Math.max(maxBaseline,c.getBaseline());
        }
        totalWidth -= spacing; //take off the last one

        double totalExcess = getWidth()-totalWidth-insets.getLeft()-insets.getRight();

        //max baseline == height from top of hbox to baseline of all nodes.
        //now set each control to be c.y=baseline-c.height
        double x = 0;
        for(Control c : controlChildren()) {
            if(!c.isVisible()) continue;            
            Bounds bounds = c.getLayoutBounds();
            //position x
            c.setTranslateX(x+insets.getLeft());
            //set the width
            double flex = spaceMap.get(c);
            if(totalFlex > 0) {
                c.setWidth(c.getWidth()+flex/totalFlex*totalExcess);
            }
            //update running total
            x = x + c.getWidth();
            x = x + spacing;

            //position y
            if(align == Align.Top) {
                c.setTranslateY(0+insets.getTop());
                c.setHeight(bounds.getHeight());
            } else if(align == Align.Baseline) {
                c.setTranslateY(insets.getTop() + maxBaseline-c.getBaseline());
                c.setHeight(bounds.getHeight());
            } else if(align == Align.Bottom) {
                c.setTranslateY(getHeight()-bounds.getHeight()+insets.getTop());
                c.setHeight(bounds.getHeight());
            } else if (align == Align.Stretch) {
                c.setTranslateY(0+insets.getTop());
                c.setHeight(getHeight()-insets.getTop()-insets.getBottom());
            } else {
                c.setTranslateY(0+insets.getTop());
                c.setHeight(bounds.getHeight());
            }


            //layout child
            c.doLayout();
        }
        boxPainter = cssSkin.createBoxPainter(this, styleInfo, sizeInfo, "", CSSSkin.State.None);
    }

    @Override
    protected void drawSelf(GFX g) {
        super.drawSelf(g);
        //g.setPaint(FlatColor.RED);
        //g.drawLine(0,insets.getTop()+maxBaseline,getWidth(),insets.getTop()+maxBaseline);
    }

}
