package org.joshy.gfx.node.control;

import org.joshy.gfx.draw.*;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.KeyEvent;
import org.joshy.gfx.event.MouseEvent;
import org.joshy.gfx.util.GraphicsUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Dec 1, 2010
 * Time: 2:18:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class TreeView<T,S> extends TableView{
    public TreeView() {
        setModel(new DummyTreeTableModel());
        setRenderer(new TreeTableDataRenderer());
        EventBus.getSystem().addListener(this, MouseEvent.MousePressed, new TreeTableMouseEventHandler());
        EventBus.getSystem().addListener(this, KeyEvent.KeyPressed, new TreeTableKeyEventHandler());
    }

    private static class TreeTableDataRenderer<T,S> implements TableView.DataRenderer<T> {

    @Override
    public void draw(GFX g, TableView table, T cellData, int row, int column, double x, double y, double width, double height) {
        //draw the background
        g.setPaint(FlatColor.WHITE);
        if(row == table.getSelectedRow()) {
            //g.setPaint(new FlatColor(0xaaaaff));
            MultiGradientFill grad = new LinearGradientFill()
                    .setStartX(0).setEndX(0).setStartY(0).setEndY(height)
                    .addStop(0,new FlatColor(0x44bcff))
                    .addStop(1,new FlatColor(0x0186ba));
            /*
            GradientFill grad = new GradientFill(new FlatColor(0x44bcff),new FlatColor(0x0186ba),
                    0,true,0,0,0,height);*/
            g.setPaint(grad);
        }
        g.translate(x,y);
        g.fillRect(0,0,width,height);
        g.translate(-x,-y);
        g.setPaint(new FlatColor(0xc0c0c0));
        g.drawLine(x,y,x,y+height);

        if(cellData != null) {
            TreeTableModel model = (TreeTableModel) table.getModel();
            //draw the first column with nav arrows and indentation
            if(column == 0) {
                int depth = model.getDepth(row);
                if(model.hasChildren(cellData)) {
                    g.setPaint(new FlatColor(0x606060));
                    if(row == table.getSelectedRow()) {
                        g.setPaint(FlatColor.WHITE);
                    }
                    if(model.isCollapsed(cellData)) {
                        GraphicsUtil.fillRightArrow(g,x+10*depth,y+4,10);
                    } else {
                        GraphicsUtil.fillDownArrow(g,x+10*depth,y+4,10);
                    }
                }
                g.setPaint(FlatColor.BLACK);
                if(row == table.getSelectedRow()) {
                    g.setPaint(FlatColor.WHITE);
                }
                g.drawText(""+model.getColumnData(cellData,column), Font.DEFAULT,x+10*depth+12+5, y+13);
                return;
            }

            //draw the rest of the columns
            String s = "";
            if(column == 1) {
                s = ""+model.getColumnData(cellData, column);
            }
            if(column == 2) {
                s = ""+model.getColumnData(cellData, column);
            }
            g.setPaint(FlatColor.BLACK);
            if(row == table.getSelectedRow()) {
                g.setPaint(FlatColor.WHITE);
            }
            g.drawText(s, Font.DEFAULT, x+5, y+13);
        }
    }
}

    private static class TreeTableMouseEventHandler implements Callback<MouseEvent> {
        @Override
        public void call(MouseEvent event) throws Exception {
            TableView tb = (TableView) event.getSource();
            TreeTableModel model = (TreeTableModel) tb.getModel();
            double rh = tb.getRowHeight();
            int row = (int) (event.getPointInNodeCoords(tb).getY()/rh);
            row--; //take off one to account for the column headers
            //toggle only if near the arrow
            int depth = model.getDepth(row);
            double x = depth*10;
            if(event.getX() >= x && event.getX() <= x+12) {
                model.toggleRow(row);
            }
        }
    }

    private static class TreeTableKeyEventHandler implements Callback<KeyEvent> {
        @Override
        public void call(KeyEvent event) throws Exception {
            TableView tb = (TableView) event.getSource();
            TreeTableModel model = (TreeTableModel) tb.getModel();
            int index = tb.getSelectedRow();
            Object node = model.get(index, 0);
            //check for arrow up and down
            if(event.getKeyCode() == KeyEvent.KeyCode.KEY_RIGHT_ARROW) {
                if(model.hasChildren(node) && model.isCollapsed(node)) {
                    model.open(node);
                    tb.redraw();
                }
            }
            if(event.getKeyCode() == KeyEvent.KeyCode.KEY_LEFT_ARROW) {
                if(model.hasChildren(node) && !model.isCollapsed(node)) {
                    model.close(node);
                    tb.redraw();
                }
            }
        }
    }

    public static interface TreeTableModel<T, S> extends  TableView.TableModel {
        public int getColumnCount();
        public int getRowCount();
        public boolean hasChildren(T node);
        public Iterable<? extends T> getChildren(T node);
        public boolean isCollapsed(T node);
        public S getColumnHeader(int column);
        public void toggleRow(int row);
        public T get(int row, int column);
        public void open(T node);
        public void close(T node);
        public int getDepth(int row);
        public S getColumnData(T node, int column);
    }

    public static abstract class AbstractTreeTableModel<T,S> implements TreeTableModel<T, S> {
        private T root;
        private HashSet<T> collapsedSet;

        public AbstractTreeTableModel() {
            collapsedSet = new HashSet<T>();
        }

        public void setRoot(T root) {
            this.root = root;
        }

        @Override
        public int getRowCount() {
            return countBreadth();
        }

        private int countBreadth() {
            return countBreadth(root);
        }

        private int countBreadth(T root) {
            int count = 1;
            if(!collapsedSet.contains(root)) {
                if(hasChildren(root)) {
                    for(T n : getChildren(root)) {
                        count += countBreadth(n);
                    }
                }
            }
            return count;
        }

        public int getDepth(T root, Count row) {
            if(row.value == 0) return 1;
            if(!collapsedSet.contains(root)) {
                if(hasChildren(root)) {
                    for(T n : getChildren(root)) {
                        row.value--;
                        int d = getDepth(n,row);
                        if(d >= 0) return d+1;
                    }
                }
            }
            return -1;
        }

        private T findNodeAtRow(T root, Count row) {
            if(row.value == 0) return root;
            if(!collapsedSet.contains(root)) {
                if(hasChildren(root)) {
                    for(T n : getChildren(root)) {
                        row.value--;
                        T result = findNodeAtRow(n, row);
                        if(result != null) return result;
                    }
                }
            }
            return null;
        }

        @Override
        public T get(int row, int column) {
            T node = findNodeAtRow(root, new Count(row));
            if(node == null) return null;
            return node;
        }

        public void toggleRow(int row) {
            T node = findNodeAtRow(root, new Count(row));
            if(node == null) return;
            if(!hasChildren(node)) return;
            if(collapsedSet.contains(node)) {
                collapsedSet.remove(node);
            } else {
                collapsedSet.add(node);
            }
        }

        public int getDepth(int row) {
            return getDepth(root,new Count(row));
        }

        public boolean isCollapsed(T cellData) {
            return collapsedSet.contains(cellData);
        }

        public void open(T node) {
            collapsedSet.remove(node);
        }

        public void close(T node) {
            collapsedSet.add(node);
        }

        private class Count {
            private int value;

            public Count(int row) {
                this.value = row;
            }
        }
    }

    private static class DummyTreeTableModel extends AbstractTreeTableModel<TreeNode,String> {
        private DummyTreeTableModel() {
            TreeNode r = new TreeNode("Master","Control Program",10000);
            r.add(new TreeNode("Bart","Simpson",10));
            r.add(new TreeNode("Lisa","Simpson",8));
            TreeNode t3 = new TreeNode("Maggy","Simpson",1);
            t3.add(new TreeNode("Snowball","Cat",5));
            t3.add(new TreeNode("Santa's","Lil' helper",5));
            r.add(t3);
            r.add(new TreeNode("Mr.","Burns",100));
            r.add(new TreeNode("Waylon","Smithers",45));
            r.add(new TreeNode("Principle","Skinner",55));
            r.add(new TreeNode("Jebadiah","Springfield",250));
            this.setRoot(r);
        }
        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public boolean hasChildren(TreeNode node) {
            if(node.children.size()>0) {
                return true;
            }
            return false;
        }

        @Override
        public Iterable<TreeNode> getChildren(TreeNode node) {
            return node.children;
        }

        @Override
        public String getColumnHeader(int column) {
            switch(column) {
                case 0: return "first";
                case 1: return "last";
                case 2: return "age";
            }
            return "ack!";
        }

        @Override
        public String getColumnData(TreeNode cellData, int column) {
            switch(column) {
                case 0: return cellData.getFirst();
                case 1: return cellData.getLast();
                case 2: return ""+cellData.getAge();
            }
            return "";
        }

    }

    private static class TreeNode {
        private String first;
        List<TreeNode> children;
        private String last;
        private int age;

        public TreeNode(String first, String last, int age) {
            this.first = first;
            this.last = last;
            this.age = age;
            this.children = new ArrayList<TreeNode>();
        }

        public void add(TreeNode treeNode) {
            this.children.add(treeNode);
        }

        public String getFirst() {
            return first;
        }

        public String getLast() {
            return last;
        }

        public int getAge() {
            return age;
        }
    }

}
