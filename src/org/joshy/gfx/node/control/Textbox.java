package org.joshy.gfx.node.control;

import java.text.AttributedString;
import org.joshy.gfx.Core;
import org.joshy.gfx.css.BoxPainter;
import org.joshy.gfx.css.CSSMatcher;
import org.joshy.gfx.css.CSSSkin;
import org.joshy.gfx.css.SizeInfo;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.ActionEvent;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.KeyEvent;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Insets;
import org.joshy.gfx.stage.Stage;

/**
 * The Textbox is a text control for editing a single line of text.
 * It will scroll the contents horizontally as needed.
 */
public class Textbox extends TextControl {
    private SizeInfo sizeInfo;
    private BoxPainter boxPainter;
    private CharSequence hintText = "";
    private FlatColor selectionColor;
    private FlatColor cursorColor;
    private AttributedString comp;
    private FlatColor textColor;

    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().defer(new Runnable(){
            @Override
            public void run() {
                Stage stage = Stage.createStage();
                Textbox tb = new Textbox();
                stage.setContent(tb);
                Core.getShared().getFocusManager().setFocusedNode(tb);
            }
        });
    }
    double xoff = 0;

    public Textbox() {
        setWidth(100);
        setHeight(40);
    }

    public Textbox(String text) {
        this();
        setText(text);
    }

    public Textbox setHintText(CharSequence text) {
        this.hintText = text;
        return this;
    }

    @Override
    protected double filterMouseX(double x) {
        return x - xoff - 0 - styleInfo.calcContentInsets().getLeft();
    }

    @Override
    protected double filterMouseY(double y) {
        return y - styleInfo.calcContentInsets().getTop();
    }

    /* =========== Layout stuff ================= */
    @Override
    public void doPrefLayout() {
        sizeInfo = cssSkin.getSizeInfo(this,styleInfo,text);

        if(prefWidth != CALCULATED) {
            setWidth(prefWidth);
            sizeInfo.width = prefWidth;
        } else {
            setWidth(sizeInfo.width);
        }
        if(prefHeight != CALCULATED) {
            setHeight(prefHeight);
            sizeInfo.height = prefHeight;
        } else {
            setHeight(sizeInfo.height);
        }
        layoutText(sizeInfo.contentWidth,sizeInfo.contentHeight);
    }

    @Override
    public void doLayout() {
        layoutText(sizeInfo.contentWidth,sizeInfo.contentHeight);
        sizeInfo.width = getWidth();
        if(sizeInfo != null) {
            sizeInfo.width = getWidth();
            sizeInfo.height = getHeight();
        }

        CSSSkin.State state = CSSSkin.State.None;
        if(isFocused()) {
            state = CSSSkin.State.Focused;
        }
        boxPainter = cssSkin.createBoxPainter(this, styleInfo, sizeInfo, text, state);
        CSSMatcher matcher = CSSSkin.createMatcher(this, state);
        selectionColor = new FlatColor(cssSkin.getCSSSet().findColorValue(matcher,"selection-color"));
        cursorColor = new FlatColor(cssSkin.getCSSSet().findColorValue(matcher,"cursor-color"));
        textColor = new FlatColor(cssSkin.getCSSSet().findColorValue(matcher,"color"));
    }

    @Override
    protected void cursorMoved() {
        double cx = getCursor().calculateX();
        Insets insets = styleInfo.calcContentInsets();
        double fudge = getFont().getAscender();
        double w = getWidth() - insets.getLeft() - insets.getRight();
        //don't let jump get beyond 1/3rd of the width of the text box
        double jump = Math.min(getFont().getAscender()*2,w/3);
        if(cx >= w - fudge -xoff) {
            xoff -= jump;
        }
        if(cx + xoff < fudge) {
            xoff += jump;
            if(xoff > 0) xoff = 0;
        }
    }

    @Override
    public double getBaseline() {
        return styleInfo.calcContentInsets().getTop()+ styleInfo.contentBaseline;
    }

    @Override
    public Bounds getLayoutBounds() {
        return new Bounds(getTranslateX(), getTranslateY(), getWidth(), getHeight());
    }

    /* =============== Drawing stuff ================ */
    @Override
    public void draw(GFX g) {
        if(!isVisible()) return;
        //draw background and border, but skip the text
        drawBackground(g);

        Insets insets = styleInfo.calcContentInsets();
        //set a new clip
        Bounds oldClip = g.getClipRect();
        g.setClipRect(new Bounds(
                styleInfo.margin.getLeft()+styleInfo.borderWidth.getLeft(),
                0,
                getWidth() - insets.getLeft() - insets.getRight(),
                getHeight()));

        //filter the text
        String text = getText();
        text = text + getComposingText();
        text = filterText(text);

        //draw the selection
        if(selection.isActive() && text.length() > 0) {
            double start = getCursor().calculateX(0,selection.getStart());
            double end = getCursor().calculateX(0,selection.getEnd());
            if(end < start) {
                double t = start;
                start = end;
                end = t;
            }
            g.setPaint(selectionColor);
            g.fillRect(
                    insets.getLeft() + start + xoff,
                    insets.getTop(),
                    end-start,
                    getHeight()-insets.getTop()-insets.getBottom());
        }

        //draw the text
        Font font = getFont();
        double y = font.getAscender();
        y+=insets.getTop();
        double x = insets.getLeft();
        if(!focused && text.length() == 0) {
            g.setPaint(new FlatColor(0x6080a0));
            g.drawText(hintText.toString(), getFont(), x + xoff, y);
        }
        g.setPaint(textColor);
        g.drawText(text, getFont(), x + xoff, y);

        //draw the composing underline
        if(getComposingText().length() > 0) {
            double underlineLen = font.calculateWidth(getComposingText());
            g.setPaint(FlatColor.RED);
            double fullLen = font.calculateWidth(text);
            g.drawLine(x+xoff+fullLen-underlineLen,y+2,x+xoff+fullLen,y+2);
        }

        //draw the cursor
        if(isFocused()) {
            g.setPaint(cursorColor);
            CursorPosition cursor = getCursor();
            double cx = cursor.calculateX() + xoff;
            // draw cursor
            g.fillRect(
                    insets.getLeft()+cx, insets.getTop(),
                    1,
                    getHeight()-insets.getTop()-insets.getBottom());
        }

        //restore the old clip
        g.setClipRect(oldClip);
    }

    protected void drawBackground(GFX g) {
        boxPainter.draw(g, styleInfo, sizeInfo, "");
    }

    protected String filterText(String text) {
        return text;
    }

    @Override
    protected void processKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KeyCode.KEY_ENTER) {
            ActionEvent act = new ActionEvent(ActionEvent.Action,this);
            EventBus.getSystem().publish(act);
        } else {
            super.processKeyEvent(event);
        }
    }

    public Textbox onAction(Callback<ActionEvent> callback) {
        EventBus.getSystem().addListener(this,ActionEvent.Action,callback);
        return this;
    }

}
