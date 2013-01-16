package org.joshy.gfx.node.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.SkinManager;
import org.joshy.gfx.css.CSSMatcher;
import org.joshy.gfx.draw.*;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.event.*;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.util.GraphicsUtil;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

/** A table with columns and rows. Used for displaying tabular data. it has a model
 * for the columns and a model for the actual data. Styling can be done with
 * item renderers and text renderers just like the ListView. 
 */
public class TableView<D,H> extends Control implements Focusable, ScrollPane.ScrollingAware, SelectableControl {

    public static enum ResizeMode {
        Proportional,
        Manual
    }

    private boolean allowColumnResizing = true;
    private static final int HEADER_HEIGHT = 18;
    private static final double RESIZE_THRESHOLD = 10;
    private TableModel<D,H> model;
    private DataRenderer<D> renderer;
    private int selectedRow = -1;
    private HeaderRenderer<H> headerRenderer;
    private int selectedColumn = -1;
    private boolean focused;
    private double defaultColumnWidth = 50;
    private double scrollY = 0;
    private double scrollX = 0;
    final double rowHeight = 20;
    private ScrollPane scrollPane;
    private Font font;
    private Map<Integer, Double> columnSizes = new HashMap<Integer,Double>();
    private Set<Integer> columnVisibles = new HashSet<Integer>();
    private Sorter<D,H> sorter;
    private SortModel sortModel;
    private int sortColumn = -1;
    public enum SortOrder { Default, Ascending, Descending };
    private SortOrder currentSortOrder = SortOrder.Default;
    private Filter<D,H> filter;
    private FilterModel filterModel;

    public TableView() {
        setWidth(300);
        setHeight(200);

        final List<String> list = new ArrayList<String>();
        for(int i=0; i<20; i++) {
            int r = (int)(Math.random()*26);
            r += 'a';
            char cr = (char) r;
            list.add(cr+"Data ");
        }
        final List<String> list2 = new ArrayList<String>();
        for(int i=0; i<20; i++) {
            int r = (int)(Math.random()*26);
            r += 'a';
            char cr = (char) r;
            list2.add(cr+"Data ");
        }
        //set default model
        setModel(new TableModel() {
            public int getRowCount() {
                return list.size();
            }

            public int getColumnCount() {
                return 3;
            }

            public Object getColumnHeader(int column) {
                return "Column " + column;
            }

            public Object get(int row, int column) {
                if(column == 0) return list.get(row) + " " + row + "," + column;
                return list2.get(row)+" " + row + "," + column;
            }
        });

        //set default renderer
        setRenderer(new DataRenderer() {
            public void draw(GFX g, TableView table, Object cell, int row, int column, double x, double y, double width, double height) {
                //if(cssSkin != null) {
                CSSMatcher matcher = new CSSMatcher(table);
                Bounds bounds = new Bounds(x,y,width,height);
                matcher.pseudoElement = "item";
                if(getSelectedIndex() == row) {
                    matcher.pseudoElement = "selected-item";
                }
                cssSkin.drawBackground(g,matcher,bounds);
                cssSkin.drawBorder(g,matcher,bounds);
                int col = cssSkin.getCSSSet().findColorValue(matcher, "color");
                g.setPaint(new FlatColor(col));
                if(cell != null) {
                    String s = cell.toString();
                    Font.drawCenteredVertically(g, s, font,
                            x+2, y, width, height, true);
                }
            }
        });

        setHeaderRenderer(new HeaderRenderer() {
            public void draw(GFX g, TableView table, Object header, int column, double x, double y, double width, double height) {
                if(column == table.getSelectedColumn()) {
                    MultiGradientFill grad = new LinearGradientFill()
                            .setStartX(0).setEndX(0).setStartY(0).setEndY(height)
                            .addStop(0,new FlatColor(0x44bcff))
                            .addStop(1,new FlatColor(0x0186ba));
                    g.setPaint(grad);
                } else {
                    MultiGradientFill grad = new LinearGradientFill()
                            .setStartX(0).setEndX(0).setStartY(0).setEndY(height)
                            .addStop(0,new FlatColor(0xffffff))
                            .addStop(1,new FlatColor(0xa0a0a0));
                    g.setPaint(grad);
                }
                g.fillRect(x,y,width,height);
                g.setPaint(new FlatColor(0x808080));
                g.drawRect(x,y,width,height);

                g.setPaint(FlatColor.BLACK);
                if(column == table.getSelectedColumn()) {
                    g.setPaint(FlatColor.WHITE);
                }
                if(header != null) {
                    Font.drawCenteredVertically(g, header.toString(), font,
                            x+2, y, width, height, true);
                }
                if(sortColumn >= 0) {
                    if(column == sortColumn) {
                        switch(currentSortOrder) {
                            case Ascending:
                                GraphicsUtil.fillDownArrow(g,x+width-15,y+5,10);
                                break;
                            case Descending:
                                GraphicsUtil.fillUpArrow(g,x+width-15,y+5,10);
                                break;
                            case Default:
                                break;
                        }

                    }
                }
            }
        });

        // click listener
        EventBus.getSystem().addListener(this, MouseEvent.MouseAll, mouseListener);

        EventBus.getSystem().addListener(FocusEvent.All, new Callback<FocusEvent>(){
            public void call(FocusEvent event) {
                if(event.getType() == FocusEvent.Lost && event.getSource() == TableView.this) {
                    focused = false;
                    setDrawingDirty();
                }
                if(event.getType() == FocusEvent.Gained && event.getSource() == TableView.this) {
                    focused = true;
                    setDrawingDirty();
                }
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
                //check for arrow up and down
                if(event.getKeyCode() == KeyEvent.KeyCode.KEY_DOWN_ARROW) {
                    int index = getSelectedRow()+1;
                    if(index < getModel().getRowCount()) {
                        setSelectedRow(index);
                    }
                }
                if(event.getKeyCode() == KeyEvent.KeyCode.KEY_UP_ARROW) {
                    int index = getSelectedRow()-1;
                    if(index >= 0) {
                        setSelectedRow(index);
                    }
                }
            }
        });

        setDefaultColumnWidth(100);
        setResizeMode(ResizeMode.Manual);
    }

    public void redraw() {
        setDrawingDirty();
    }

    public void refilter() {
        if(filterModel != null) {
            filterModel.refilter();
            setLayoutDirty();
            setDrawingDirty();
        }
    }

    private ResizeMode resizeMode = ResizeMode.Proportional;

    public void setResizeMode(ResizeMode newMode) {
        if(resizeMode == ResizeMode.Proportional && newMode == ResizeMode.Manual) {
            columnSizes.clear();
        }
        resizeMode = newMode;
        setDrawingDirty();
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public void setSelectedRow(int row) {
        if(row >= 0 && row < getModel().getRowCount()) {
            selectedRow = row;
        } else {
            selectedRow = -1;
        }
        if(scrollPane != null) {
            Bounds bounds = new Bounds(0,
                    getSelectedRow()*rowHeight,
                    getWidth(),
                    rowHeight+ HEADER_HEIGHT);
            scrollPane.scrollToShow(bounds);
        }
        EventBus.getSystem().publish(new SelectionEvent(SelectionEvent.Changed,this));
        setDrawingDirty();
    }


    public int getSelectedColumn() {
        return selectedColumn;
    }

    public void setSelectedColumn(int selectedColumn) {
        this.selectedColumn = selectedColumn;
        if(sortModel != null) {
            if(sortColumn == selectedColumn) {
                switch(currentSortOrder) {
                    case Default: currentSortOrder = SortOrder.Ascending; break;
                    case Ascending: currentSortOrder = SortOrder.Descending; break;
                    case Descending: currentSortOrder = SortOrder.Default; break;
                }
            }
            sortColumn = selectedColumn;
            sortModel.reSort(selectedColumn,currentSortOrder);
        }
        setDrawingDirty();
    }

    public static interface DataRenderer<D> {

        /**
         *
         * @param g graphics context
         * @param table the table view
         * @param cellData the cell data, may be null
         * @param row
         * @param column
         * @param x
         * @param y
         * @param width
         * @param height
         */
        public void draw(GFX g, TableView table, D cellData, int row, int column, double x, double y, double width, double height);


    }

    public void setRenderer(DataRenderer<D> renderer) {
        this.renderer = renderer;
    }

    public static interface HeaderRenderer<H> {

        public void draw(GFX g, TableView table, H header, int column, double x, double y, double width, double height);

    }

    public void setHeaderRenderer(HeaderRenderer headerRenderer) {
        this.headerRenderer = headerRenderer;
    }

    public static interface TableModel<D,H> {

        public int getRowCount();

        public int getColumnCount();

        public H getColumnHeader(int column);

        /**
         * Return the data item at the specified row and createColumn. May return null.
         * @param row
         * @param column
         * @return the data at the specified row and createColumn. May be null.
         */
        public D get(int row, int column);

    }

    public TableModel<D,H> getModel() {
        return model;
    }

    public void setModel(TableModel<D,H> model) {
        this.model = model;
        for(int i=0; i<model.getColumnCount();i++) {
            columnVisibles.add(i);
        }
    }

    public void setDefaultColumnWidth(double defaultColumnWidth) {
        this.defaultColumnWidth = defaultColumnWidth;
        setWidth(getViewModel().getColumnCount()*defaultColumnWidth);
    }

    public TableView<D, H> setAllowColumnResizing(boolean allowColumnResizing) {
        this.allowColumnResizing = allowColumnResizing;
        return this;
    }

    public static interface Filter<D,H> {

        public boolean matches(TableModel<D,H> table, int row);

    }

    public static interface Sorter<D,H> {


        public Comparator createComparator(TableModel table, int column, SortOrder order);

    }

    public void setSorter(Sorter sorter) {
        this.sorter = sorter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public int getSelectedIndex() {
        return getSelectedRow();
    }

    public double getRowHeight() {
        return rowHeight;
    }

    /* private impl shared only with treetable */

    private Cursor resizeCursor = Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);

    private void setCursor(Cursor cursor) {
        Frame frame = (Frame) getParent().getStage().getNativeWindow();
        frame.setCursor(cursor);
    }

    private void setDefaultCursor() {
        Frame frame = (Frame) getParent().getStage().getNativeWindow();
        frame.setCursor(Cursor.getDefaultCursor());
    }

    private Callback<MouseEvent> mouseListener = new Callback<MouseEvent>(){
        public int resizeColumn = -1;
        public Point2D start;

        public void call(MouseEvent event) {
            if(event.getType() == MouseEvent.MouseMoved) {
                if(event.getY() < HEADER_HEIGHT &&  allowColumnResizing) {
                    if(isOverLeftColumnEdge(event) || isOverRightColumnEdge(event)) {
                        setCursor(resizeCursor);
                    } else {
                        setDefaultCursor();
                    }
                }
                return;
            }

            if(event.getType() == MouseEvent.OpenContextMenu) {
                if(event.getY() < HEADER_HEIGHT) {
                    showColumnFilterPopup(event);
                    Core.getShared().getFocusManager().setFocusedNode(TableView.this);
                    return;
                }
            }
            if(event.getType() == MouseEvent.MousePressed) {
                if(event.getY() < HEADER_HEIGHT) {
                    setSelectedColumn(mouseToViewColumn(event));
                    if(isOverLeftColumnEdge(event) && allowColumnResizing) {
                        resizeColumn = mouseToViewColumn(event)-1;
                        start = event.getPointInNodeCoords(TableView.this);
                        start = new Point2D.Double(start.getX()-scrollX,start.getY());
                    }
                    if(isOverRightColumnEdge(event) && allowColumnResizing) {
                        resizeColumn = mouseToViewColumn(event);
                        start = event.getPointInNodeCoords(TableView.this);
                        start = new Point2D.Double(start.getX()-scrollX,start.getY());
                    }
                } else {
                    int startRow = (int)(-scrollY/rowHeight);
                    setSelectedRow((int)((event.getY()- HEADER_HEIGHT)/getRowHeight())+startRow);
                }
                Core.getShared().getFocusManager().setFocusedNode(TableView.this);
            }

            if(event.getType() == MouseEvent.MouseDragged && resizeColumn != -1 && allowColumnResizing) {
                Point2D current = event.getPointInNodeCoords(TableView.this);
                current = new Point2D.Double(current.getX()-scrollX,current.getY());
                double dx = current.getX()-start.getX();
                start = current;
                setColumnWidth(resizeColumn,getColumnWidth(resizeColumn)+dx);
            }

            if(event.getType() == MouseEvent.MouseReleased) {
                resizeColumn = -1;
            }
        }

        private int mouseToViewColumn(MouseEvent event) {
            double cx = event.getX()-scrollX;
            if(resizeMode == ResizeMode.Proportional) {
                double columnWidth = getWidth() / model.getColumnCount();
                int column = (int)(cx/columnWidth);
                return column;
            }
            if(resizeMode == ResizeMode.Manual) {
                double x = 0;
                ListIterator it = getVisibleColumns();
                while(it.hasNext()) {
                    Object header = it.next();
                    int col = it.previousIndex();
                    double w = getColumnWidth(col);
                    if(cx>=x && cx<x+w) {
                        return col;
                    }
                    x+=w;
                }
            }
            return -1;
        }
    };

    private void showColumnFilterPopup(MouseEvent event) {

        PopupMenu popup = new PopupMenu(new ListModel() {
            @Override
            public Object get(int col) {
                String prefix = "    ";
                if(columnVisibles.contains(col)) {
                    prefix = "\u2713 ";
                }
                return prefix+getModel().getColumnHeader(col);
            }
            @Override
            public int size() {
                return getModel().getColumnCount();
            }
        },new Callback<ChangedEvent>() {
            @Override
            public void call(ChangedEvent event) throws Exception {
                //toggle column, assuming it's not the last one visible
                int col = (Integer)event.getValue();
                if(columnVisibles.contains(col)) {
                    if(columnVisibles.size() > 1) {
                        columnVisibles.remove(col);
                    }
                } else {
                    columnVisibles.add(col);
                }
            }
        });
        popup.setTranslateX(event.getPointInSceneCoords().getX());
        popup.setTranslateY(event.getPointInSceneCoords().getY());
        getParent().getStage().getPopupLayer().add(popup);
        popup.setVisible(true);
    }

    private boolean isOverRightColumnEdge(MouseEvent event) {
        double cx = event.getX() - scrollX;
        if(resizeMode == ResizeMode.Proportional){
            double columnWidth = getWidth() / model.getColumnCount();
            double my = cx % columnWidth;
            if(my > columnWidth-5 && my <= columnWidth) {
                return true;
            }
            return false;
        }
        if(resizeMode == ResizeMode.Manual) {
            double x = 0;
            ListIterator it = getVisibleColumns();
            while(it.hasNext()) {
                Object header = it.next();
                int col = it.previousIndex();
                double w = getColumnWidth(col);
                if(cx>=x+w-RESIZE_THRESHOLD && cx<x+w) {
                    return true;
                }
                x+=w;
            }
        }
        return false;
    }

    private boolean isOverLeftColumnEdge(MouseEvent event) {
        double cx = event.getX() - scrollX;
        if(resizeMode == ResizeMode.Proportional){
            double columnWidth = getWidth() / model.getColumnCount();
            double my = cx % columnWidth;
            if(my >= 0 && my < 10) {
                return true;
            }
            return false;
        }
        if(resizeMode == ResizeMode.Manual) {
            double x = 0;
            ListIterator it = getVisibleColumns();
            while(it.hasNext()) {
                Object header = it.next();
                int col = it.previousIndex();
                double w = getColumnWidth(col);
                if(cx>=x && cx<x+ RESIZE_THRESHOLD) {
                    return true;
                }
                x+=w;
            }
        }
        return false;
    }

    @Override
    public void doSkins() {
        cssSkin = SkinManager.getShared().getCSSSkin();
        font = cssSkin.getStyleInfo(this,null).font;
        setLayoutDirty();
    }

    @Override
    public void doPrefLayout() {
    }

    @Override
    public void doLayout() {
    }

    @Override
    public void draw(GFX g) {
        if(!isVisible()) return;
        CSSMatcher matcher = new CSSMatcher(this);

        Bounds clip = g.getClipRect();
        g.setClipRect(new Bounds(0,0,width,height));

        //draw bg
        if(cssSkin != null) {
            cssSkin.drawBackground(g,matcher,new Bounds(0,0,width,height));
        } else {
            g.setPaint(FlatColor.WHITE);
            g.fillRect(0,0,width,height);
        }

        //draw headers
        double x = 0;
        ListIterator<H> it = getVisibleColumns();
        while(it.hasNext()) {
            H header = it.next();
            int col = it.previousIndex();
            double columnWidth = getColumnWidth(col);
            headerRenderer.draw(g, this, header, col, x+scrollX, 0, columnWidth, HEADER_HEIGHT);
            x += columnWidth;
        }

        //draw data
        int startRow = (int)(-scrollY/rowHeight);
        for(int row=0; row*rowHeight+ HEADER_HEIGHT < getHeight(); row++) {
            double cx = 0;
            ListIterator lit = getVisibleColumns();
            while(lit.hasNext()) {
                Object header = lit.next();
                int col = lit.previousIndex();
                D item = null;
                double columnWidth = getColumnWidth(col);
                if(row+startRow < model.getRowCount()) {
                    item = getViewData(row+startRow,col);
                }
                renderer.draw(g, this, item,
                        row+startRow, col,
                        (int)(cx+scrollX),
                        (int)(row*rowHeight+1+HEADER_HEIGHT),
                        (int)columnWidth, rowHeight);
                cx+=columnWidth;
            }
        }


        //draw border
        g.setClipRect(clip);
        if(cssSkin != null) {
            cssSkin.drawBorder(g,matcher,new Bounds(0,0,width,height));
        }
    }

    //returns cell data in view columns

    private D getViewData(int row, int col) {
        col = columnViewToModel(col);
        TableModel<D,H> model = getViewModel();
        D item = model.get(row,col);
        return item;
    }

    private TableModel<D,H> getViewModel() {
        TableModel<D,H> m = getModel();
        if(filter != null) {
            if(filterModel == null) {
                filterModel = new FilterModel(m,filter);
            }
            m = filterModel;
        }
        if(sorter != null) {
            if(sortModel == null) {
                sortModel = new SortModel(m,sorter);
            }
            m = sortModel;
        }
        return m;
    }

    //returns only the view columns

    private ListIterator<H> getVisibleColumns() {
        ArrayList l = new ArrayList();
        for(int i=0; i<getModel().getColumnCount(); i++) {
            if(!columnVisibles.contains(i)) {
                continue;
            }
            l.add(getModel().getColumnHeader(i));
        }
        return l.listIterator();
    }

    // in view columns

    private double getColumnWidth(int col) {
        if(resizeMode == ResizeMode.Proportional) {
            return getWidth()/getModel().getColumnCount();
        }
        col = columnViewToModel(col);
        if(!columnSizes.containsKey(col)) {
            columnSizes.put(col,defaultColumnWidth);
        }
        return columnSizes.get(col);
    }

    //in view columns

    private void setColumnWidth(int col, double v) {
        //no-op for proportional sizing
        if(resizeMode == ResizeMode.Proportional) {
            return;
        }
        col = columnViewToModel(col);
        columnSizes.put(col,v);
        setDrawingDirty();
        setLayoutDirty();
    }

    private double getTotalColumnWidth() {
        double width = 0;
        for(int col : columnSizes.keySet()) {
            width += columnSizes.get(col);
        }
        return width;
    }

    //convert view columns to model columns

    private int columnViewToModel(int col) {
        int n = 0;
        for(int c = 0; c<getModel().getColumnCount(); c++) {
            if(columnVisibles.contains(c)) {
                col--;
            }
            if(col < 0) {
                break;
            }
            n++;
        }
        return n;
    }

    public boolean isFocused() {
        return focused;
    }

    /* =========== scrolling impl =========== */

    public double getFullWidth(double width, double height) {
        if(resizeMode == ResizeMode.Manual) {
            return getTotalColumnWidth();
        }
        return getWidth();
    }

    public double getFullHeight(double width, double height) {
        return Math.max(getViewModel().getRowCount()*rowHeight,height);
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

    private static class SortModel implements TableModel {
        private TableModel model;
        private Sorter sorter;
        private List<Row> sortedList;

        public SortModel(final TableModel model, Sorter sorter) {
            this.model = model;
            this.sorter = sorter;
            List<Row> list = new ArrayList<Row>();
            for(int row =0; row<model.getRowCount(); row++) {
                list.add(new Row(row,model));
            }
            sortedList = list;
        }

        public void reSort(final int column, SortOrder order) {
            final Comparator comp = sorter.createComparator(model, column, order);
            if(order == SortOrder.Default) {
                List<Row> list = new ArrayList<Row>();
                for(int row =0; row<model.getRowCount(); row++) {
                    list.add(new Row(row,model));
                }
                sortedList = list;
                return;
            }

            Collections.sort(sortedList,new Comparator<Row>(){
                @Override
                public int compare(Row a, Row b) {
                    Object ad = model.get(a.index,column);
                    Object bd = model.get(b.index,column);
                    return comp.compare(ad,bd);
                }
            });
        }

        @Override
        public int getRowCount() {
            return this.model.getRowCount();
        }

        @Override
        public int getColumnCount() {
            return this.model.getColumnCount();
        }

        @Override
        public Object getColumnHeader(int column) {
            return this.model.getColumnHeader(column);
        }

        @Override
        public Object get(int row, int column) {
            int realRow = this.sortedList.get(row).index;
            return this.model.get(realRow,column);
        }
    }

    private static class FilterModel implements TableModel {
        private TableModel model;
        private List<Row> filteredList;
        private Filter filter;

        public FilterModel(TableModel model, Filter filter) {
            this.model = model;
            this.filter = filter;
            refilter();
        }
        public void refilter() {
            List<Row> list = new ArrayList<Row>();
            for(int row =0; row<model.getRowCount(); row++) {
                if(filter.matches(model,row)) {
                    list.add(new Row(row,model));
                }
            }
            filteredList = list;
        }
        @Override
        public int getRowCount() {
            return filteredList.size();
        }

        @Override
        public int getColumnCount() {
            return model.getColumnCount();
        }

        @Override
        public Object getColumnHeader(int column) {
            return model.getColumnHeader(column);
        }

        @Override
        public Object get(int row, int column) {
            if(row >= filteredList.size()) return null;
            int realRow = filteredList.get(row).index;
            return this.model.get(realRow,column);
        }
    }

    private static class Row {
        int index;
        private TableModel model;

        public Row(int row, TableModel model) {
            this.index = row;
            this.model = model;
        }
    }

}
