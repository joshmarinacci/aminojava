package org.joshy.gfx.node.control;

import org.joshy.gfx.css.BoxPainter;
import org.joshy.gfx.css.CSSMatcher;
import org.joshy.gfx.css.CSSSkin;
import org.joshy.gfx.css.SizeInfo;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Insets;

/**
 * The TextArea is a text control for editing multiline text. The text is
 * drawn with only one font style.
 */
public class Textarea extends TextControl implements ScrollPane.ScrollingAware{
    private SizeInfo sizeInfo;
    private BoxPainter boxPainter;
    private FlatColor selectionColor;
    private FlatColor cursorColor;
    private ScrollPane scrollParent;
    private double scrollX;
    private double scrollY;
    private boolean sizeToText;

    public Textarea() {
        setWidth(100);
        setHeight(100);
        this.allowMultiLine = true;
    }

    public Textarea(String text) {
        this();
        setText(text);
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
        layoutText(sizeInfo.contentWidth,sizeInfo.contentHeight);
        if(prefHeight != CALCULATED) {
            setHeight(prefHeight);
            sizeInfo.height = prefHeight;
        } else {
            setHeight(sizeInfo.height);
        }
    }

    @Override
    public void doLayout() {
        double h = getHeight();
        Insets insets = styleInfo.calcContentInsets();
        double w = getWidth()-insets.getLeft()-insets.getRight();
        layoutText(w,sizeInfo.contentHeight);
        setHeight(h);
        sizeInfo.width = getWidth();
        if(sizeInfo != null) {
            sizeInfo.width = getWidth();
            sizeInfo.height = getHeight();
        }

        if(isSizeToText()) {
            w = -1;
            h = -1;
            String[] strings = getText().split("\n");
            double maxWidth = -1;
            double maxHeight = 0;
            for(String s : strings) {
                maxWidth = Math.max(maxWidth,getFont().calculateWidth(s));
                maxHeight += getFont().getAscender();
                maxHeight += getFont().getLeading();
                maxHeight += getFont().getDescender();
            }
            sizeInfo.width = maxWidth + insets.getLeft()+insets.getRight();
            sizeInfo.height = maxHeight + insets.getTop() + insets.getBottom();
            setHeight(sizeInfo.height + insets.getTop() + insets.getBottom());
        }

        CSSSkin.State state = CSSSkin.State.None;
        if(isFocused()) {
            state = CSSSkin.State.Focused;
        }        
        boxPainter = cssSkin.createBoxPainter(this, styleInfo, sizeInfo, text, state);
        CSSMatcher matcher = CSSSkin.createMatcher(this, state);
        selectionColor = new FlatColor(cssSkin.getCSSSet().findColorValue(matcher,"selection-color"));
        cursorColor = new FlatColor(cssSkin.getCSSSet().findColorValue(matcher,"cursor-color"));

    }

    @Override
    protected void cursorMoved() {
        
    }


    @Override
    protected double filterMouseX(double x) {
        return x - scrollX - styleInfo.calcContentInsets().getLeft();
    }

    @Override
    protected double filterMouseY(double y) {
        return y - scrollY - styleInfo.calcContentInsets().getTop();
    }

    @Override
    public double getBaseline() {
        return styleInfo.calcContentInsets().getTop()+ styleInfo.contentBaseline;
    }

    @Override
    public void draw(GFX g) {
        if(!isVisible()) return;
        //draw background and border, but skip the text
        boxPainter.draw(g, styleInfo, sizeInfo, "");

        Insets insets = styleInfo.calcContentInsets();
        //set a new clip
        Bounds oldClip = g.getClipRect();
        g.setClipRect(new Bounds(
                insets.getLeft(),
                insets.getTop(),
                getWidth() - insets.getLeft() - insets.getRight() + 1, //+1 for the cursor width
                getHeight()-insets.getTop()-insets.getBottom()));

        //draw the text
        Font font = getFont();
        double y = 0 + scrollY;
        double x = insets.getLeft();
        g.setPaint(FlatColor.BLACK);
        for(int row = 0; row < _layout_model.lineCount(); row++) {
            TextLayoutModel.LayoutLine line = _layout_model.line(row);
            //draw the selection part of the line
            if(selection.isActive()) {
                int[] startRC = getCursor().indexToRowCol(selection.getStart());
                int[] endRC = getCursor().indexToRowCol(selection.getEnd());
                double start = getCursor().calculateX(startRC[0],startRC[1]);
                double end = getCursor().calculateX(endRC[0],endRC[1]);
                if(end < start && endRC[0] == startRC[0]) {
                    double t = start;
                    start = end;
                    end = t;
                }
                if(endRC[0] < startRC[0]) {
                    double t = start;
                    start = end;
                    end = t;
                    int[] tt = startRC;
                    startRC = endRC;
                    endRC = tt;
                }
                
                //if all on same line
                if(startRC[0] == endRC[0] && startRC[0] == row) {
                    //draw normal strip
                    drawSelectionStrip(g, insets,font,start,end,y);
                } else {
                    //start strip
                    if(row == startRC[0]) {
                        drawSelectionStrip(g,insets,font,start,getWidth()-insets.getRight(),y);
                    }
                    //middle strip
                    if(row > startRC[0] && row < endRC[0]) {
                        drawSelectionStrip(g,insets,font,0,getWidth()-insets.getRight(),y);
                    }
                    //end strip
                    if(row == endRC[0]) {
                        drawSelectionStrip(g,insets,font,0,end,y);
                    }
                }
            }
            g.setPaint(FlatColor.BLACK);
            g.drawText(line.getString(), font, x, insets.getTop()+y+font.getAscender());
            y += line.getHeight();
        }

        //draw the cursor
        if(isFocused()) {
            g.setPaint(cursorColor);
            CursorPosition cursor = getCursor();
            double cx = cursor.calculateX();
            double cy = cursor.calculateY();
            // draw cursor
            g.fillRect(
                    insets.getLeft()+cx,
                    insets.getTop()+cy + scrollY,
                    1,
                    font.getAscender()+font.getDescender());
        }


        //restore the old clip
        g.setClipRect(oldClip);
    }

    private void drawSelectionStrip(GFX g, Insets insets, Font font, double start, double end, double y) {
        g.setPaint(selectionColor);
        g.fillRect(
                insets.getLeft() + start,
                insets.getTop()+y,
                end-start,
                font.getAscender()+font.getDescender());
    }

    @Override
    public double getFullWidth(double width, double height) {
        return width;
    }

    @Override
    public double getFullHeight(double width, double height) {
        _layout_model.layout(width, height);
        Insets insets = styleInfo.calcContentInsets();
        double h =  _layout_model.calculatedHeight()+insets.getTop()+insets.getBottom();
        return h;
    }

    @Override
    public void setScrollX(double value) {
        this.scrollX = value;
    }

    @Override
    public void setScrollY(double value) {
        this.scrollY = value;
    }

    @Override
    public void setScrollParent(ScrollPane scrollPane) {
        this.scrollParent = scrollPane;
    }

    public void setSizeToText(boolean sizeToText) {
        this.sizeToText = sizeToText;
    }

    public boolean isSizeToText() {
        return sizeToText;
    }
}
