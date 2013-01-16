package org.joshy.gfx.node.layout;

import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.util.u;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Jul 30, 2010
 * Time: 9:28:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class GridBox extends Panel {
    private Row currentRow;
    private double padding = 5;
    private boolean debug;

    public void debug(boolean debug) {
        this.debug = debug;
    }

    public GridBox setPadding(double padding) {
        this.padding = padding;
        return this;
    }


    public enum Align {
        Left, Center, Fill, Right
    }
    public enum VAlign {
        Top, Center, Baseline, Bottom
    }


    private List<Column> columns;
    private List<Row> rows;


    public GridBox() {
        reset();
    }

    public void reset() {
        columns = new ArrayList<Column>();
        rows = new ArrayList<Row>();
        currentRow = new Row();
        rows.add(currentRow);
    }

    public GridBox createColumn(int width, Align align) {
        columns.add(new Column(width,align,VAlign.Baseline));
        return this;
    }

    public GridBox createColumn(int width, Align align, VAlign vAlign) {
        columns.add(new Column(width,align,vAlign));
        return this;
    }

    /*
    @Override
    public Panel add(Node... nodes) {
        for(Node n : nodes) {
            if(n instanceof Control) {
                //currentRow.controls.add((Control)n);
            }
        }
        return super.add(nodes);
    }

    */
    /*
    @Override
    public Container add(Node node) {
        if(node instanceof Control) {
            currentRow.controls.add((Control)node);
        }
        return super.add(node);
    }

    */
    public GridBox addControl(Control ... controls) {
        currentRow.controls.addAll(Arrays.asList(controls));
        super.add(controls);
        return this;
    }

    public GridBox skip() {
        return addControl(new Spacer());
    }

    public GridBox nextRow() {
        currentRow = new Row();
        rows.add(currentRow);
        return this;
    }

    @Override
    public void doPrefLayout() {
        super.doPrefLayout();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void doLayout() {
        super.doLayout();
        //lay out the columns first
        //calc min width first
        double minWidth = 0;
        for(Column col : columns) {
            minWidth+=col.minWidth;
        }
        minWidth+= padding*columns.size();
        
        //distribute the extra width
        double extraWidth = getWidth()-minWidth;
        if(extraWidth > 0) {
            for(Column col : columns) {
                col.realWidth = col.minWidth +extraWidth/columns.size() + padding;
            }
        } else {
            u.p("warning. the columns exceed the width of the grid box");
        }

        //now lay out the columns for real
        double x = 0;
        for(Column col : columns) {
            col.x = x;
            x+=col.realWidth;
        }

        //lay out the controls by row
        double y = padding;
        for(Row row : rows) {

            //find the max height for the row
            double maxHeight = 0;
            double maxBaseline = 0;
            for(Control c : row.controls) {
                //pref layout on controls has already been called
                //c.doPrefLayout();
                Bounds bounds = c.getLayoutBounds();
                maxHeight = Math.max(maxHeight,bounds.getHeight()+padding);
                double baseline = (bounds.getY()+bounds.getHeight()) - bounds.getY();
                maxBaseline = Math.max(maxBaseline,baseline);
                //u.p("control " + c + " height = " + c.getHeight() + " vs " + c.getLayoutBounds() + " baseline = " + baseline);
            }
            //now really lay out the controls
            for(int i=0; i<row.controls.size(); i++) {
                Control c = row.controls.get(i);
                if(i < columns.size()) {
                    Column col = columns.get(i);
                    switch(col.align) {
                        case Left: c.setTranslateX(col.x+padding); break;
                        case Center: c.setTranslateX(col.x+(col.realWidth-c.getWidth())/2); break;
                        case Right: c.setTranslateX(col.x+col.realWidth-c.getWidth()); break;
                        case Fill:
                            c.setTranslateX(col.x+padding);
                            c.setWidth(col.realWidth-padding*2);
                            break;
                    }
                    switch(col.vAlign) {
                        case Top:
                            c.setTranslateY(y);
                            break;
                        case Baseline:
                            double baseline = (c.getLayoutBounds().getY()+c.getLayoutBounds().getHeight()) - c.getVisualBounds().getY();
                            c.setTranslateY(y+maxBaseline-baseline);
                            break;
                    }
                } else {
                    u.p("Warning: more controls in this row than there are columns");
                }
                c.doLayout();
            }
            y+=maxHeight;
        }
    }

    @Override
    protected void drawSelf(GFX g) {
        super.drawSelf(g);
        if(debug) {
            g.setPaint(FlatColor.RED);
            for(Column c : columns) {
                g.drawLine(c.x,0,c.x,getHeight()-1);
            }
            g.drawRect(0,0,getWidth()-1,getHeight()-1);
        }
    }

    private class Row {
        List<Control> controls = new ArrayList<Control>();
    }
    
    private class Column {
        private double minWidth;
        private double realWidth;
        private double x = 0;
        private Align align;
        private VAlign vAlign;

        public Column(int minWidth, Align align, VAlign vAlign) {
            this.minWidth = minWidth;
            this.align = align;
            this.vAlign = vAlign;
        }
    }

}
