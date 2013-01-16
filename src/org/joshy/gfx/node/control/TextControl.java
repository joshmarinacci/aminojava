package org.joshy.gfx.node.control;

import java.awt.event.InputMethodEvent;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.CharacterIterator;
import java.util.Date;
import org.joshy.gfx.Core;
import org.joshy.gfx.SkinManager;
import org.joshy.gfx.css.StyleInfo;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.event.*;
import org.joshy.gfx.node.Insets;
import org.joshy.gfx.util.OSUtil;

/*
 redesign text control to fix all of the bugs
 first, there should be a text model which holds the actual strings of text
 and converts between the various locations

 convert an x y pixel coordinate to a character point
 convertXYToCharPoint()

 convert a character point to the line or createColumn
 getLine(cp)
 getColumn(cp)

 delete or insert a char by typing:
 cursor == current char point
 model.insertChar(cursor,char);
 model.deleteChar(cursor);

 move cursor
 cursor = current char point
 cursor.advanceColumn()

 cursor.getX()
 cursor.getY()

 the text model simply stores lines of text and allows their editing
 the char point converts between screen coords to row & createColumn
 the textcontrol implements the actual drawing and input handleing 
*/

/**
 * The TextControl class is the base class for all text controls in Amino. It
 * implements the core cursor, selecting, and editing logic.
 */
public abstract class TextControl extends Control implements Focusable, FocusManager.IMETarget {
    protected boolean focused;
    protected String text = "";

    protected boolean allowMultiLine = false;
    private Font realFont;
    protected TextSelection selection;
    TextLayoutModel _layout_model;
    private CursorPosition cursor;
    protected StyleInfo styleInfo;
    private AttributedString composingText;


    protected TextControl() {
        cursor = new CursorPosition();
        selection = new TextSelection();

        EventBus.getSystem().addListener(this, MouseEvent.MousePressed, new Callback<MouseEvent>(){
            public void call(MouseEvent event) {
                if(!isEnabled()) return;
                Core.getShared().getFocusManager().setFocusedNode(TextControl.this);
                if(selection.isActive() && !event.isShiftPressed()) {
                    selection.clear();
                }
                double ex = filterMouseX(event.getX());
                double ey = filterMouseY(event.getY());
                cursor.setIndexFromMouse(ex,ey);
            }
        });

        EventBus.getSystem().addListener(FocusEvent.All, new Callback<FocusEvent>(){
            public void call(FocusEvent event) {
                if(event.getType() == FocusEvent.Lost && event.getSource() == TextControl.this) {
                    focused = false;
                    setLayoutDirty();
                }
                if(event.getType() == FocusEvent.Gained && event.getSource() == TextControl.this) {
                    focused = true;
                    setLayoutDirty();
                }
            }
        });
        
        EventBus.getSystem().addListener(this, KeyEvent.KeyPressed, new Callback<KeyEvent>() {
            public void call(KeyEvent event) {
                processKeyEvent(event);
            }
        });
        EventBus.getSystem().addListener(this, KeyEvent.KeyReleased, new Callback<KeyEvent>() {
            public void call(KeyEvent event) {
                processKeyEvent(event);
            }
        });
        EventBus.getSystem().addListener(this, KeyEvent.KeyTyped, new Callback<KeyEvent>() {
            public void call(KeyEvent event) {
                processKeyTyped(event);
            }
        });

        EventBus.getSystem().addListener(this, MouseEvent.MouseReleased, new Callback<MouseEvent>() {
            public long lastRelease = 0;
            public int clickCount = 0;

            public void call(MouseEvent event) {
                long now = new Date().getTime();
                if(now-lastRelease < 400) {
                } else {
                    clickCount = 0;
                }
                clickCount++;
                lastRelease = now;
                if(clickCount == 3) {
                    selectAll();
                }
            }
        });
    }

    protected double filterMouseY(double y) {
        return y;
    }

    protected double filterMouseX(double x) {
        return x;
    }

    private void processKeyTyped(KeyEvent event) {
        if(event.isSystemShortcut()) return;
        if(event.getKeyCode() == KeyEvent.KeyCode.KEY_UNKNOWN) {
            switch(event.getKeyChar()) {
                case '\u0008': return;
                case '\u007f': return;
                case 0xa: return;
            }
            //u.p("text = " + event.getGeneratedText());
            //u.p("char = " + event.getKeyChar());
            //u.p("hex = '\\u" + Integer.toHexString(event.getKeyChar())+"'");
        }
        //regular keys
        if(!"\t".equals(event.getGeneratedText())) {
            insertText(event.getGeneratedText());
        }
    }

    protected void processKeyEvent(KeyEvent event) {
        //skip all key released events
        if(event.getType() == KeyEvent.KeyReleased) return;
        
        //Paste
        if(event.getKeyCode().equals(KeyEvent.KeyCode.KEY_V) && event.isSystemPressed()) {
            insertText(OSUtil.getClipboardAsString());
            return;
        }

        //Copy
        if(event.getKeyCode().equals(KeyEvent.KeyCode.KEY_C) && event.isSystemPressed()) {
            if(selection.isActive() && selection.getLength() > 0) {
                OSUtil.setStringToClipboard(selection.getSelectedText());
            }
            return;
        }

        //Cut
        if(event.getKeyCode().equals(KeyEvent.KeyCode.KEY_X) && event.isSystemPressed()) {
            if(selection.isActive() && selection.getLength() > 0) {
                OSUtil.setStringToClipboard(selection.getSelectedText());
                insertText("");
            }
            return;
        }

        //Enter
        if(allowMultiLine && event.getKeyCode() == KeyEvent.KeyCode.KEY_ENTER) {
            insertText("\n");
            return;
        }

        //backspace and delete
        boolean bs = event.getKeyCode() == KeyEvent.KeyCode.KEY_BACKSPACE;
        boolean del = event.getKeyCode() == KeyEvent.KeyCode.KEY_DELETE;
        if( bs || del) {

            //if backspace and at start, do nothing
            if(bs && cursor.getRow() == 0 && cursor.getCol()==0) return;
            //if delete and at end, do nothing
            if(del && cursor.atEndOfText()) {
                return;
            }

            //if text is empty do nothing
            String t = getText();
            if(t.length() == 0) return;

            if(selection.isActive()) {
                String[] parts = cursor.splitSelectedText();
                setText(parts[0]+parts[2]);
                cursor.setIndex(parts[0].length());
                selection.clear();
            } else {
                //split, then remove text in the middle
                int splitPoint = cursor.getIndex();
                int altSplitPoint = cursor.rowColToIndex(cursor.getRow(),cursor.getCol());
                String[] parts = cursor.splitText(splitPoint);
                if(del) {
                    parts[1] = parts[1].substring(1);
                }
                if(bs) {
                    parts[0] = parts[0].substring(0,cursor.getIndex()-1);
                }
                setText(parts[0]+parts[1]);
                if(bs) {
                    cursor.moveLeft(1);
                }
            }
            return;
        }

        //left arrow
        if(event.getKeyCode() == KeyEvent.KeyCode.KEY_LEFT_ARROW) {
            if(event.isShiftPressed()) {
                if(!selection.isActive()) {
                    selection.clear();
                    selection.setStart(cursor.getIndex());
                    selection.setEnd(cursor.getIndex());
                }
                //extend selection only if this wouldn't make us wrap backward
                if(!cursor.atStartOfLine()) {
                    selection.setEnd(selection.getEnd()-1);
                }
            } else {
                selection.clear();
            }
            cursor.moveLeft(1);
            setDrawingDirty();
            return;
        }

        //right arrow
        if(event.getKeyCode() == KeyEvent.KeyCode.KEY_RIGHT_ARROW) {
            if(event.isShiftPressed()) {
                if(!selection.isActive()) {
                    selection.clear();
                    selection.setStart(cursor.getIndex());
                    selection.setEnd(cursor.getIndex());
                }
                //extend selection only it this wouldn't make us wrap forward
                if(!cursor.atEndOfLine()) {
                    selection.setEnd(selection.getEnd()+1);
                }
            } else {
                selection.clear();
            }
            cursor.moveRight(1);
            setDrawingDirty();
        }

        //up arrow
        if(event.getKeyCode() == KeyEvent.KeyCode.KEY_UP_ARROW) {
            if(allowMultiLine) {
                cursor.moveUp(1);
            } else {
                if(event.isShiftPressed()) {
                    if(!selection.isActive()) {
                        selection.clear();
                        selection.setStart(0);
                        selection.setEnd(cursor.getIndex());
                    } else {
                        selection.setEnd(0);
                    }
                }
                cursor.moveStart();
            }
            setDrawingDirty();
        }

        if(event.getKeyCode() == KeyEvent.KeyCode.KEY_DOWN_ARROW) {
            if(allowMultiLine) {
                cursor.moveDown(1);
            } else {
                if(event.isShiftPressed()) {
                    if(!selection.isActive()) {
                        selection.clear();
                        selection.setStart(cursor.getIndex());
                        selection.setEnd(getText().length());
                    } else {
                        selection.setEnd(getText().length());
                    }
                }
                cursor.moveEnd();
            }
            setDrawingDirty();
        }

        if(event.getKeyCode() == KeyEvent.KeyCode.KEY_TAB) {
            if(event.isShiftPressed()) {
                Core.getShared().getFocusManager().gotoPrevFocusableNode();
            } else {
                Core.getShared().getFocusManager().gotoNextFocusableNode();
            }
        }

}

    private void insertText(String generatedText) {
        if(selection.isActive()) {
            String[] parts = cursor.splitSelectedText();
            setText(parts[0]+generatedText+parts[2]);
            if(!"\n".equals(generatedText)) {
                cursor.setIndex(parts[0].length() + generatedText.length());
            }
            selection.clear();
        } else {
            String[] parts = cursor.splitText(cursor.getIndex());
            setText(parts[0]+generatedText+parts[1]);
            relayoutText();
            cursor.moveRight(generatedText.length());
        }
    }

    //TODO: this might be a problem you can set text without it getting re-layedout
    public TextControl setText(String text) {
        this.text = text;
        EventBus.getSystem().publish(new ChangedEvent(ChangedEvent.StringChanged,text,TextControl.this));
        selection.clear();
        if(cursor.getIndex()-1 > getText().length()) {
            cursor.reset();
        }
        setLayoutDirty();
        setDrawingDirty();
        return this;
    }

    public void setComposingText(InputMethodEvent ime) {
        AttributedCharacterIterator text = ime.getText();
        //u.p("ime text length = " + text.getBeginIndex() + " -> " + text.getEndIndex() + " , " + ime.getCommittedCharacterCount());
        if(text.getEndIndex() - (text.getBeginIndex()+ime.getCommittedCharacterCount()) > 0) {
            //u.p("there is some composing text right now: ");
            composingText = new AttributedString(
                    text,
                    text.getBeginIndex()+ime.getCommittedCharacterCount(),
                    text.getEndIndex(),
                    IM_ATTRIBUTES
            );
        } else {
            composingText = new AttributedString("");
        }
    }
    public void appendCommittedText(InputMethodEvent inputMethodEvent) {
        int count = inputMethodEvent.getCommittedCharacterCount();
        //u.p("appending committed text : " + count + " chars");
        StringBuffer sb = new StringBuffer();
        AttributedCharacterIterator text = inputMethodEvent.getText();
        char c = text.first();
        while(count > 0) {
            sb.append(c);
            c = text.next();
            count--;
        }
        //u.p("committed text now = " + sb.toString());
        insertText(sb.toString());
        //u.p("total text now = " + getText());
    }

    public String getCommittedText() {
        return getText();
    }
    public String getComposingText() {
        StringBuffer sb = new StringBuffer();
        if(composingText != null) {
            AttributedCharacterIterator it = composingText.getIterator();
            for(char c = it.first(); c != CharacterIterator.DONE; c=it.next()) {
                sb.append(c);
                //graphics.drawString(""+c,60+i*60,120);
                //i++;
            }
        }
        return sb.toString();
    }


    protected void relayoutText() {
        if(getFont() == null) return;
        int index = cursor.getIndex();
        _layout_model = new TextLayoutModel(getFont(),getText(),allowMultiLine);
        _layout_model.layout(width,height);
        Insets insets = styleInfo.calcContentInsets();
        setHeight(_layout_model.calculatedHeight()+insets.getTop()+insets.getBottom());
        cursor.setIndex(index);
    }

    protected void layoutText(double width, double height) {
        if(getFont() == null) return;
        int index = cursor.getIndex();
        _layout_model = new TextLayoutModel(getFont(),getText(),allowMultiLine);
        _layout_model.layout(width,height);
        Insets insets = styleInfo.calcContentInsets();
        setHeight(_layout_model.calculatedHeight()+insets.getTop()+insets.getBottom());
        cursor.setIndex(index);
    }

    public TextControl setFont(Font font) {
        this.realFont = font;
        setSkinDirty();
        return this;
    }

    public Font getFont() {
        if(realFont != null) {
            return realFont;
        }
        return styleInfo.font;
    }

    public boolean isFocused() {
        return focused;
    }

    @Override
    public void doSkins() {
        cssSkin = SkinManager.getShared().getCSSSkin();
        styleInfo = cssSkin.getStyleInfo(this,realFont);
        setLayoutDirty();
    }

    @Override
    public void doPrefLayout() {
        //noop
    }

    @Override
    public void doLayout() {
        //noop
    }


    public String getText() {
        return text;
    }

    public CursorPosition getCursor() {
        return cursor;
    }

    public class TextSelection {
        private boolean active;
        public int startCol;
        public int endCol;

        private TextSelection() {
        }

        public static final int LEFT = 1;
        public static final int RIGHT = 2;
        int direction = LEFT;

        public void clear() {
            active = false;
            setDrawingDirty();
        }

        protected boolean isActive() {
            return active;
        }

        public int getStart() {
            return startCol;
        }

        public void setStart(int index) {
            active = true;
            startCol = index;
        }

        public int getEnd() {
            return endCol;
        }

        public void setEnd(int index) {
            endCol = index;
        }

        public void selectAll() {
            active = true;
            startCol = 0;
            endCol = text.length();
        }

        public String toString() {
            return "selection: " + startCol + " -> " + endCol;
        }

        public int getLength() {
            return endCol - startCol;
        }

        public String getSelectedText() {
            return getText().substring(startCol,endCol);
        }
    }

    public void selectAll() {
        selection.selectAll();
        setDrawingDirty();
    }

    protected class CursorPosition {
        private int index = 0;
        private int col;
        private int row;

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        public int getIndex() {
            return index;
        }

        public int[] indexToRowCol(int i) {
            int[] rowCol = new int[2];
            int n = i;
            rowCol[0] = 0;
            rowCol[1] = 0;
            for(TextLayoutModel.LayoutLine line : _layout_model.lines()) {
                if(line.letterCount() >= n) {
                    break;
                }
                rowCol[0]++;
                n -= line.letterCount();
                if(line.newline) n--;
            }
            rowCol[1] = n;

            return rowCol;
        }
        
        private int rowColToIndex(int row, int col) {
            int index = 0;
            for(int i=0; i<row; i++) {
                TextLayoutModel.LayoutLine line = _layout_model.line(i);
                index += line.letterCount();
                if(line.newline) index++;
            }
            index+=col;

            return index;
        }

        public void setIndex(int i) {
            this.index = i;
            int[] rowCol = indexToRowCol(i);
            row = rowCol[0];
            col = rowCol[1];
        }

        public void moveLeft(int i) {
            col -= i;
            TextLayoutModel.LayoutLine rowLine = _layout_model.line(row);
            //wrap back to prev row?
            if(col < 0 && row > 0) {
                row--;
                col = _layout_model.line(row).letterCount();
            }
            index = rowColToIndex(row,col);
            if(index <0) {
                index = 0;
                col = 0;
                row = 0;
            }
            cursorMoved();
        }

        public void moveRight(int i) {
            col+=i;

            TextLayoutModel.LayoutLine rowLine = _layout_model.line(row);
            //wrap to next row?
            if(col > rowLine.letterCount()) {
                if(row < _layout_model.lineCount()-1) {
                    row++;
                    col=0;
                }
            }
            index = rowColToIndex(row,col);
            String t = getText();
            if(index > t.length()) {
                index = t.length();
                col = _layout_model.line(row).letterCount();
            }
            cursorMoved();
        }

        public void moveUp(int i) {
            if(row > 0) {
                row--;
            }
            TextLayoutModel.LayoutLine line = _layout_model.line(row);
            if(col > line.letterCount()) {
                col = line.letterCount();
            }
            index = rowColToIndex(row,col);
            cursorMoved();
        }

        public void moveDown(int i) {
            if(row < _layout_model.lineCount()-1) {
                row++;
            }
            TextLayoutModel.LayoutLine line = _layout_model.line(row);
            if(col > line.letterCount()) {
                col = line.letterCount();
            }
            index = rowColToIndex(row,col);
            cursorMoved();
        }

        public boolean atStartOfLine() {
            TextLayoutModel.LayoutLine rowLine = _layout_model.line(row);
            if(col == 0) {
                return true;
            }
            return false;
        }

        public boolean atEndOfLine() {
            TextLayoutModel.LayoutLine rowLine = _layout_model.line(row);
            if(col == rowLine.letterCount()) {
                return true;
            }
            return false;
        }

        public boolean atEndOfText() {
            if(row == _layout_model.lineCount()-1) {
                if(col == _layout_model.line(row).letterCount()) {
                    return true;
                }
            }
            return false;
        }


        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("cursor: " + col + "," + row + "  index = " + index);
            if(getText().length() > index) {
                sb.append(" char = " + getText().substring(index,index+1));
            }
            return sb.toString();
        }

        public String[] splitText(int index) {
            String t = getText();
            String[] parts =  new String[2];
            parts[0] = t.substring(0,index);
            parts[1] = t.substring(index);
            return parts;
        }

        public String[] splitSelectedText() {
            String t = getText();
            String[] parts =  new String[3];
            parts[0] = t.substring(0,selection.getStart());
            parts[1] = t.substring(selection.getStart(),selection.getEnd());
            parts[2] = t.substring(selection.getEnd());
            return parts;
        }

        public void moveStart() {
            index = 0;
            col = 0;
            row = 0;
        }

        public void moveEnd() {
            String t = getText();
            index = t.length();
            col = index;
        }

        public double calculateX() {
            TextLayoutModel.LayoutLine line = _layout_model.line(row);
            if(col > line.letterCount()) {
                return getFont().calculateWidth(line.getString());
            }
            String t = line.getString().substring(0,col);
            return getFont().calculateWidth(t);
        }

        public double calculateX(int row, int col) {
            TextLayoutModel.LayoutLine line = _layout_model.line(row);
            String t = line.getString().substring(0,col);
            return getFont().calculateWidth(t);
        }

        public double calculateY() {
            return (getFont().getAscender()+getFont().getDescender())*row;
        }


        public void reset() {
            this.row = 0;
            this.col = 0;
            this.index = 0;
        }

        public void setIndexFromMouse(double ex, double ey) {
//            u.p("calculating from mouse: " + ex + " " + ey);

            //calc row first
            double y = 0;
            int rowcount =0;
            for(TextLayoutModel.LayoutLine line : _layout_model.lines()) {
                if(ey >= y && ey < y + line.getHeight()) {
                    row = rowcount;
                    break;
                }
                y+= line.getHeight();
                rowcount++;
            }

            TextLayoutModel.LayoutLine line = _layout_model.line(row);
            if(ex < 0) {
                col = 0;
            } else if(ex > line.getWidth()) {
                col = line.letterCount();
            } else {
                //now calc the column
                String text = line.getString();
//                u.p("text = " + text);
                double lastX = 0;
                for(int i=0; i<text.length(); i++) {
                    double x = getFont().calculateWidth(text.substring(0,i));
                    double w = (x-lastX)/2;
//                    u.p("i = " + i + " x = " + x + " w = " + w);
                    if(ex >= x-w && ex < x+w) {
//                        u.p("found at " + i);
                        col = i;
                    }
                    if(i==1 && ex < w) {
                        col = 0;
                    }
                    lastX = x;
                }
            }
//            u.p("pos = " + col+","+row);

            index = rowColToIndex(row,col);
        }

    }

    protected abstract void cursorMoved();
}
