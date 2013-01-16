package org.joshy.gfx.node.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.SkinManager;
import org.joshy.gfx.css.CSSMatcher;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.*;
import org.joshy.gfx.node.Bounds;

import java.awt.geom.Point2D;
import java.util.List;
import org.joshy.gfx.util.u;

/**
 * ListView is the classic list control that uses lightweight renderers.  If is scrolling
 * aware so you can drop it into a Scrollpane and it will do the right thing.  Put data into
 * the list by setting it's list model. You can customize the look of the list view in one of two ways:
 *
 * If you just want to do text formating on the list items
 * then set the textRenderer with your own implementation. ex:
 *
 * myListView.setTextRenderer(new TextRenderer<Song>(){
 *           public String toString(SelectableControl view, Song item, int index) {
 *               if(item == null) return "";
 *               return item.getArtist() + " - " + item.getTitle();
 *           }
 *       });
 *
 *
 * If you want to do custom drawing, then install your own ItemRenderer. ex:
 *
 * myListView.setRenderer(ItemRenderer<Song>() {
 *    public void draw(GFX gfx, ListView listView, Song item, int index, double x, double y, double width, double height) {
 *      gfx.drawRect(x,y,width,height);
 *      gfx.drawImage(song.getArtwork(),0,0,75,75);
 *      gfx.drawText(item.getTitle(), 100, 20;
 *   }
 * }
 *
 * Your renderer function will be called each time an item needs to be drawn on the screen.
 * If you need a list containing real UI controls that work (like buttons) rather than just custom
 * styling, then you should use the CompoundLlistView instead.
 *
 *
 * @param <E>
 */
public class ListView<E> extends Control implements Focusable, ScrollPane.ScrollingAware, SelectableControl {

    public static int NO_SELECTION = -1;

    private ListModel<E> model;
    private ItemRenderer<E> renderer;
    private int selectedIndex = NO_SELECTION;
    private boolean focused;

    private double rowHeight = 20.0;
    private double colWidth = 100.0;
    private double scrollX = 0;
    private double scrollY = 0;
    private ScrollPane scrollPane;
    private Orientation orientation = Orientation.Vertical;
    private boolean dropIndicatorVisible;
    private int dropIndicatorIndex;
    private TextRenderer<E> textRenderer;
    private Font font;


    public ListView() {
        setWidth(200);
        setHeight(300);
        setRenderer(defaultItemRenderer);
        setTextRenderer(new TextRenderer<E>(){
            public String toString(SelectableControl view, E item, int index) {
                if(item == null) return "null";
                return item.toString();
            }
        });

        setModel(new ListModel<E>() {
            public E get(int i) {
                return (E)("dummy item " + i);
            }
            public int size() {
                return 3;
            }
        });
        EventBus.getSystem().addListener(FocusEvent.All, new Callback<FocusEvent>(){
            public void call(FocusEvent event) {
                if(event.getType() == FocusEvent.Lost && event.getSource() == ListView.this) {
                    focused = false;
                    setDrawingDirty();
                }
                if(event.getType() == FocusEvent.Gained && event.getSource() == ListView.this) {
                    focused = true;
                    setDrawingDirty();
                }
            }
        });

        // click listener
        EventBus.getSystem().addListener(this, MouseEvent.MousePressed, new Callback<MouseEvent>(){
            public void call(MouseEvent event) {
                if(event.getType() == MouseEvent.MousePressed) {
                    int index = calculateIndexAt(event.getX(),event.getY());
                    setSelectedIndex(index);
                    setDrawingDirty();
                }
                Core.getShared().getFocusManager().setFocusedNode(ListView.this);
            }
        });
        
        //keyboard listener
        EventBus.getSystem().addListener(this, KeyEvent.KeyPressed, new Callback<KeyEvent>() {
            public void call(KeyEvent event) {
                //check for focus changes
                if(event.getKeyCode() == KeyEvent.KeyCode.KEY_TAB) {
                    if(event.isShiftPressed()) {
                        Core.getShared().getFocusManager().gotoPrevFocusableNode();
                    } else {
                        Core.getShared().getFocusManager().gotoNextFocusableNode();
                    }
                }
                
                //check for arrow keys
                switch(event.getKeyCode()) {
                    case KEY_LEFT_ARROW:
                    case KEY_RIGHT_ARROW:
                    case KEY_DOWN_ARROW:
                    case KEY_UP_ARROW:
                       handleArrowKeys(event);
                }
            }
        });


    }

    public E getItemAt(double x, double y) {
        int index = calculateIndexAt(x,y);
        if(index < 0) return null;
        return getModel().get(index);
    }
    public E getItemAt(Point2D pt) {
        int index = calculateIndexAt(pt.getX(),pt.getY());
        if(index < 0) return null;
        return getModel().get(index);
    }

    private int calculateIndexAt(double x, double y) {
        int startRow = (int)(-scrollY/rowHeight);
        int startCol = (int)(-scrollX/colWidth);
        int index = 0;
        int voff = (int) (scrollY % rowHeight);
        int ay = (int) (y-voff);
        int erow = (int) (ay / rowHeight);
        erow += startRow;
        int row = (int) ((y+voff)/rowHeight+startRow);
        int col = (int) (x/colWidth+startCol);
        switch(orientation) {
            case Vertical: index = erow; break;
            case Horizontal: index = (int) (x/colWidth+startCol); break;
            case HorizontalWrap:
                int rowLength = (int) (getWidth()/colWidth);
                index = row * rowLength + col;
                break;
            case VerticalWrap:
                int colLength = (int) (getHeight()/rowHeight);
                index = col * colLength + row;
                break;
        }
        return index;
    }

    private void handleArrowKeys(KeyEvent event) {
        int index = 0;
        if(event.getKeyCode() == KeyEvent.KeyCode.KEY_DOWN_ARROW) {
            switch (orientation) {
                case Vertical: 
                case VerticalWrap: index = getSelectedIndex()+1; break;
                case Horizontal: index = getSelectedIndex(); break;
                case HorizontalWrap: index = getSelectedIndex()+(int)(getWidth()/colWidth); break;
            }
        }
        if(event.getKeyCode() == KeyEvent.KeyCode.KEY_UP_ARROW) {
            switch (orientation) {
                case Vertical:
                case VerticalWrap: index = getSelectedIndex()-1; break;
                case Horizontal: index = getSelectedIndex(); break;
                case HorizontalWrap: index = getSelectedIndex()-(int)(getWidth()/colWidth); break;
            }
        }
        if(event.getKeyCode() == KeyEvent.KeyCode.KEY_LEFT_ARROW) {
            switch (orientation) {
                case Vertical: index = getSelectedIndex(); break;
                case VerticalWrap: index = getSelectedIndex()-(int)(getHeight()/rowHeight); break;
                case Horizontal:
                case HorizontalWrap: index = getSelectedIndex()-1; break;
            }
        }
        if(event.getKeyCode() == KeyEvent.KeyCode.KEY_RIGHT_ARROW) {
            switch (orientation) {
                case Vertical: index = getSelectedIndex(); break;
                case VerticalWrap: index = getSelectedIndex()+(int)(getHeight()/rowHeight); break;
                case Horizontal:
                case HorizontalWrap: index = getSelectedIndex()+1;break;
            }
        }
        if(index >= 0 && index < getModel().size()) {
            setSelectedIndex(index);
        }
    }

    public ListView<E> setRenderer(ItemRenderer<E> renderer) {
        this.renderer = renderer;
        return this;
    }

    @Override
    public void doSkins() {
        cssSkin = SkinManager.getShared().getCSSSkin();
        font = cssSkin.getStyleInfo(this,null).font;
        setLayoutDirty();
    }

    @Override
    public void doPrefLayout() {
        setWidth(50);
        setHeight(50);
    }

    @Override
    public void doLayout() {
    }

    @Override
    public void draw(GFX g) {
        if(getWidth() < 1) return;
        CSSMatcher matcher = new CSSMatcher(this);
        
        if(cssSkin != null) {
            cssSkin.drawBackground(g,matcher,new Bounds(0,0,width,height));
        } else {
            g.setPaint(FlatColor.WHITE);
            g.fillRect(0,0,width,height);
            g.setPaint(FlatColor.BLACK);
            g.drawRect(0,0,width,height);
        }

        Bounds oldClip = g.getClipRect();
        g.setClipRect(new Bounds(0,0,width,height));

        if(orientation == Orientation.Vertical) {
            double dy = scrollY - ((int)(scrollY/rowHeight))*rowHeight;
            int startRow = (int)(-scrollY/rowHeight);
            for(int i=0; i<model.size();i++) {
                if(i*rowHeight > getHeight() + rowHeight) break;
                E item = null;
                if(i+startRow < model.size()) {
                    try {
                        item = model.get(i+startRow);
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        item = null;
                    }
                }
                renderer.draw(g, this, item, i+startRow, 0+1, i*rowHeight+1+dy, getWidth()-1, rowHeight);
            }
        }

        if(orientation == Orientation.Horizontal) {
            double dx = scrollX - ((int)(scrollX/colWidth))*colWidth;
            int startCol = (int)(-scrollX/colWidth);
            for(int i=0; i<model.size();i++) {
                if(i*colWidth > getWidth()+colWidth) break;
                E item = null;
                if(i+startCol < model.size()) {
                    item = model.get(i+startCol);
                }
                renderer.draw(g, this, item, i+startCol, i*colWidth+1+dx, 0+1, colWidth, getHeight()-1);
            }

            if(dropIndicatorVisible) {
                g.setPaint(new FlatColor(1.0,0,0,0.7));
                g.fillRect(dx+dropIndicatorIndex*colWidth-2,0,5,getHeight()-1);
            }

        }

        if(orientation == Orientation.HorizontalWrap || orientation == Orientation.VerticalWrap) {
            double dy = scrollY - ((int)(scrollY/rowHeight))*rowHeight;
            double dx = scrollX - ((int)(scrollX/colWidth))*colWidth;
            int startRow = (int)(-scrollY/rowHeight);
            int startCol = (int)(-scrollX/colWidth);
            for(int i=0; i<model.size();i++) {
                int x = 0;
                int y = 0;
                int ioff = 0;
                if(orientation == Orientation.HorizontalWrap) {
                    int rowLength = (int) (getWidth()/colWidth);
                    x = i%rowLength;
                    y = i/rowLength;
                    ioff = startRow*rowLength;
                }
                if(orientation == Orientation.VerticalWrap) {
                    int colLength = (int) (getHeight()/rowHeight);
                    x = i/colLength;
                    y = i%colLength;
                    ioff = startCol*colLength;
                }
                E item = null;
                if(i+ioff < model.size()) {
                    item = model.get(i+ioff);
                }
                renderer.draw(g, this, item, i+ioff,
                        x*colWidth+1+dx, y*rowHeight+1+dy,
                        colWidth, rowHeight);
            }

        }

        
        g.setClipRect(oldClip);
        if(cssSkin != null) {
            cssSkin.drawBorder(g,matcher,new Bounds(0,0,width,height));
        }

    }


    public ListModel<E> getModel() {
        return model;
    }
    
    public ListView<E> setModel(ListModel<E> listModel) {
        this.model = listModel;
        EventBus.getSystem().addListener(model, ListEvent.Updated, new Callback<ListEvent>() {
            public void call(ListEvent event) {
                setLayoutDirty();
                setDrawingDirty();
            }
        });
        return this;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        if(selectedIndex < -1) return;
        if(selectedIndex >= model.size()) return;
        this.selectedIndex = selectedIndex;

        if(scrollPane != null && selectedIndex >= 0) {
            Bounds bounds = null;
            int colLen = (int) (getHeight()/rowHeight);
            int rowLen = (int) (getWidth()/colWidth);
            switch(orientation) {
                case Vertical:       bounds = new Bounds(0,selectedIndex*getRowHeight(),getWidth(),getRowHeight()); break;
                case Horizontal:     bounds = new Bounds(selectedIndex*getColumnWidth(),0,getColumnWidth(),getHeight()); break;
                case HorizontalWrap: bounds = new Bounds((selectedIndex%rowLen * getColumnWidth()), selectedIndex/rowLen * getRowHeight(), getColumnWidth(),getRowHeight()); break;
                case VerticalWrap:   bounds = new Bounds((selectedIndex/colLen * getColumnWidth()), selectedIndex%rowLen * getRowHeight(), getColumnWidth(),getRowHeight()); break;
            }
            scrollPane.scrollToShow(bounds);
        }

        EventBus.getSystem().publish(new SelectionEvent(SelectionEvent.Changed,this));
        setDrawingDirty();
    }
    
    public boolean isFocused() {
        return focused;
    }

    /* ===== ScrollingAware Implementation ===== */
    public double getFullWidth(double width, double height) {
        switch (orientation) {
            case Vertical: return width;
            case VerticalWrap:
                int colLen = (int) (getHeight()/rowHeight);
                return Math.max(getModel().size()/colLen*colWidth,width);
            case Horizontal: return Math.max(getModel().size()*colWidth,width);
            case HorizontalWrap: return width;
        }
        return width;
    }

    public double getFullHeight(double width, double height) {
        if(getModel().size() <= 0) {
            return height;
        }
        switch (orientation) {
            case Vertical:   return Math.max(getModel().size()*rowHeight,height);
            case VerticalWrap: return height;
            case Horizontal: return height;
            case HorizontalWrap:
                if(width < 1) return height;
                int rowLen = (int) (width/colWidth);
                if(rowLen < 1) rowLen = 1;
                double rowCount = getModel().size() / rowLen;
                return Math.max(rowCount*rowHeight,height);
        }
        return height;
    }

    public void setScrollX(double value) {
        this.scrollX = value;
    }

    public void setScrollY(double value) {
        this.scrollY = value;
    }

    public void setScrollParent(ScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }






    /* ============= =================== */

    public static ListModel createModel(final List list) {
        return new ListModel() {

            public Object get(int i) {
                if(i >= list.size() || i < 0) {
                    return null;
                }
                return list.get(i);
            }

            public int size() {
                return list.size();
            }
        };
    }

    private double getRowHeight() {
        return rowHeight;
    }

    private double getColumnWidth() {
        return colWidth;
    }


    public ListView<E> setRowHeight(double rowHeight) {
        this.rowHeight = rowHeight;
        return this;
    }

    
    public ListView<E> setColumnWidth(double colWidth) {
        this.colWidth = colWidth;
        return this;
    }


    public ListView<E> setOrientation(Orientation orientation) {
        this.orientation = orientation;
        return this;
    }

    public void setDropIndicatorVisible(boolean dropIndicatorVisible) {
        this.dropIndicatorVisible = dropIndicatorVisible;
    }

    public void setDropIndicatorIndex(int dropIndicatorIndex) {
        this.dropIndicatorIndex = dropIndicatorIndex;
    }

    public ListView<E> setTextRenderer(TextRenderer<E> textRenderer) {
        this.textRenderer = textRenderer;
        return this;
    }


    public static class ListEvent extends Event {
        public static final EventType Updated = new EventType("ListEventUpdated");
        public ListEvent(EventType type, ListModel model) {
            super(type);
            this.source = model;            
        }
    }

    public static <T> ListModel<T> createModel(final T ... strings) {
        return new ListModel<T>() {

            public T get(int i) {
                if(i >= strings.length) {
                    return null;
                }
                return strings[i];
            }

            public int size() {
                return strings.length;
            }
        };
    }


    public static interface ItemRenderer<E> {
        public void draw(GFX gfx, ListView listView, E item, int index, double x, double y, double width, double height);
    }

    public enum Orientation {
        Horizontal, HorizontalWrap, VerticalWrap, Vertical
    }

    public static interface TextRenderer<E> {
        public String toString(SelectableControl view, E item, int index);
    }

    ItemRenderer defaultItemRenderer =  new ItemRenderer<E>() {
        public void draw(GFX gfx, ListView listView, E item, int index, double x, double y, double width, double height) {
            CSSMatcher matcher = new CSSMatcher(listView);
            Bounds bounds = new Bounds(x,y,width,height);
            matcher.pseudoElement = "item";
            if(listView.getSelectedIndex() == index) {
                matcher.pseudoElement = "selected-item";
            }
            cssSkin.drawBackground(gfx,matcher,bounds);
            cssSkin.drawBorder(gfx,matcher,bounds);
            int col = cssSkin.getCSSSet().findColorValue(matcher, "color");
            gfx.setPaint(new FlatColor(col));
            if(item != null) {
                String s = textRenderer.toString(listView, item, index);
                gfx.drawText(s, font, x+2, y+15);
            }
        }
    };

}
