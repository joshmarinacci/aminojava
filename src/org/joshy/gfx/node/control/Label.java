package org.joshy.gfx.node.control;

import org.joshy.gfx.SkinManager;
import org.joshy.gfx.css.BoxPainter;
import org.joshy.gfx.css.CSSSkin;
import org.joshy.gfx.css.SizeInfo;
import org.joshy.gfx.css.StyleInfo;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Insets;

/**
 * A text label. It can have only one font style, but can be single or multiple lines.
 * A label will naturally stretch out to one long line unless the width is constrained
 * by a parent or by setting the preferred width.  You can also force line breaks
 * by putting the newline character, \n, in your string.
 */
public class Label extends Control {
    private CharSequence text = "Label";
    private StyleInfo styleInfo;
    private SizeInfo sizeInfo;
    private BoxPainter boxPainter;
    private Font realFont;
    public TextLayoutModel _layout_model;
    private FlatColor color;

    public Label(CharSequence text) {
        this.text = text;
    }
    public Label() {
        this.text = "";
    }
    @Override
    public void doSkins() {
        cssSkin = SkinManager.getShared().getCSSSkin();
        styleInfo = cssSkin.getStyleInfo(this,realFont);
        setLayoutDirty();
    }

    @Override
    public void doPrefLayout() {
        sizeInfo = cssSkin.getSizeInfo(this,styleInfo,text.toString());
        if(prefWidth != CALCULATED) {
            setWidth(prefWidth);
            sizeInfo.width = prefWidth;
        } else {
            setWidth(sizeInfo.width);
        }
        setHeight(sizeInfo.height);
        layoutText();
    }

    @Override
    public void doLayout() {
        layoutText();
        if(sizeInfo != null) {
            sizeInfo.width = getWidth();
            sizeInfo.height = getHeight();
        }
        boxPainter = cssSkin.createBoxPainter(this,styleInfo,sizeInfo,text.toString(),CSSSkin.State.None);
    }

    private void layoutText() {
        _layout_model = new TextLayoutModel(styleInfo.font,getText().toString(), true);
        _layout_model.layout(getWidth(),getHeight());
        Insets insets = styleInfo.calcContentInsets();
        setHeight(_layout_model.calculatedHeight()+insets.getTop()+insets.getBottom());
    }

    @Override
    public void draw(GFX g) {
        if(!isVisible()) return;
        if(sizeInfo == null) {
            doPrefLayout();
        }

        double oldOpacity = g.getOpacity();
        if(getOpacity() != 1.0) {
            g.setOpacity(getOpacity());
        }
        boxPainter.draw(g, styleInfo, sizeInfo, "");
        g.setPaint(boxPainter.color);
        if(color != null) {
            g.setPaint(color);
        }
        double y = styleInfo.font.getAscender();
        Insets insets = styleInfo.calcContentInsets();
        y+=insets.getTop();
        double x = insets.getLeft();
        for(TextLayoutModel.LayoutLine line : _layout_model.lines()) {
            g.drawText(line.getString(),styleInfo.font,x,y);
            y+= line.getHeight();
        }
        if(getOpacity() != 1.0) {
            g.setOpacity(oldOpacity);
        }
        //g.setPaint(FlatColor.RED);
        //g.drawRect(0,0,getWidth(),getHeight());
    }

    public Label setText(String text) {
        this.text = text;
        setLayoutDirty();
        setDrawingDirty();
        return this;
    }

    public CharSequence getText() {
        return this.text;
    }

    @Override
    public Bounds getLayoutBounds() {
        return new Bounds(getTranslateX(), getTranslateY(), getWidth(), getHeight());
    }

    @Override
    public double getBaseline() {
        if(sizeInfo == null) {
            doPrefLayout();
        }
        return styleInfo.margin.getTop() + styleInfo.borderWidth.getTop() + styleInfo.padding.getTop() + styleInfo.contentBaseline;
    }

    public Label setFont(Font font) {
        this.realFont = font;
        setSkinDirty();
        return this;
    }


    public Label setColor(FlatColor color) {
        this.color = color;
        setDrawingDirty();
        return this;
    }

    public Font getFont() {
        return realFont;
    }
}
