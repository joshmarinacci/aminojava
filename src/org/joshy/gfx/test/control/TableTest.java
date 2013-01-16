package org.joshy.gfx.test.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.KeyEvent;
import org.joshy.gfx.event.SystemMenuEvent;
import org.joshy.gfx.node.control.*;
import org.joshy.gfx.node.layout.TabPanel;
import org.joshy.gfx.node.layout.VFlexBox;
import org.joshy.gfx.stage.Stage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TableTest extends GrandTour.Example implements Runnable{
    public static void main(String ... args) throws Exception, InterruptedException {
        Core.setUseJOGL(false);
        Core.init();
        Core.getShared().defer(new TableTest("asdf"));
    }

    public TableTest(String s) {
        super(s);
    }

    public void run() {
        Stage s = Stage.createStage();
        try {
            s.setContent(build());
        } catch (Exception e) {
            e.printStackTrace();
        }
        EventBus.getSystem().addListener(SystemMenuEvent.Quit, new Callback<SystemMenuEvent>(){
            @Override
            public void call(SystemMenuEvent event) throws Exception {
                System.exit(0);
            }
        });
    }
    @Override
    public Control build() throws Exception {
        return new TabPanel()
                .add("Sort Table",standardTable())
                .add("Filter Table",filterTable())
                .add("Tree",tree())
                .add("Tree Table", treeTable())
                ;
    }

    private class MyTreeNode {
        public String title;
        public List<MyTreeNode> children = new ArrayList<MyTreeNode>();
    }
    private Control tree() {
        TreeView tv = new TreeView();
        MyTreeNode n1 = new MyTreeNode();
        n1.title = "a";
        MyTreeNode n2 = new MyTreeNode();
        n2.title = "b";
        MyTreeNode n3 = new MyTreeNode();
        n3.title = "c";
        MyTreeNode n4 = new MyTreeNode();
        n4.title = "d";
        n1.children.add(n2);
        n1.children.add(n3);
        n2.children.add(n4);

        TreeView.AbstractTreeTableModel<MyTreeNode, String> attm = new TreeView.AbstractTreeTableModel<MyTreeNode, String>() {
            @Override
            public int getColumnCount() {
                return 1;
            }

            @Override
            public boolean hasChildren(MyTreeNode node) {
                return !node.children.isEmpty();
            }

            @Override
            public Iterable<MyTreeNode> getChildren(MyTreeNode node) {
                return node.children;
            }

            @Override
            public String getColumnHeader(int column) {
                return "name";
            }

            @Override
            public String getColumnData(MyTreeNode node, int column) {
                return node.title;
            }
        };
        attm.setRoot(n1);
        tv.setModel(attm);
        tv.setWidth(500);
        tv.setResizeMode(TreeView.ResizeMode.Proportional);
        return new ScrollPane(tv);
    }

    private Control treeTable() {
        TreeView treeView = new TreeView();
        treeView.setWidth(500);
        return new ScrollPane(treeView);
    }

    private Control standardTable() {
        TableView table = new TableView();
        table.setAllowColumnResizing(false);
        table.setSorter(new TableView.Sorter() {
            @Override
            public Comparator createComparator(TableView.TableModel table, int column, TableView.SortOrder order) {
                if(order == TableView.SortOrder.Default) {
                    return new Comparator<String>() {
                        @Override
                        public int compare(String s, String s1) {
                            return 0;
                        }
                    };
                }
                if(order == TableView.SortOrder.Ascending) {
                    return new Comparator<String>() {
                        @Override
                        public int compare(String s, String s1) {
                            return s.compareTo(s1);
                        }
                    };
                }
                if(order == TableView.SortOrder.Descending) {
                    return new Comparator<String>() {
                        @Override
                        public int compare(String s, String s1) {
                            return s1.compareTo(s);
                        }
                    };
                }
                return null;
            }
        });
        table.setWidth(500);
        return new ScrollPane(table);
    }

    private Control filterTable() {
        final TableView table = new TableView();
        final Textbox filterBox = new Textbox()
                .setHintText("filter text");
        EventBus.getSystem().addListener(filterBox,KeyEvent.KeyReleased,new Callback<KeyEvent>(){
            @Override
            public void call(KeyEvent event) throws Exception {
                table.refilter();
            }
        });
        //filterBox.setPrefWidth(200);

        //This will filter by the first column.
        //It only applies if you type in at least 3 letters.
        //It is not case sensitive.
        table.setFilter(new TableView.Filter() {
            @Override
            public boolean matches(TableView.TableModel table, int row) {
                String text = filterBox.getText();
                if(text.length() < 3) return true;
                Object data = table.get(row,0);
                if(data.toString().toLowerCase().contains(text.toLowerCase())) {
                    return true;
                }
                return false;
            }
        });
        return new VFlexBox()
                .setBoxAlign(VFlexBox.Align.Stretch)
                .add(filterBox)
                .add(new ScrollPane(table),1)
                .setPrefWidth(100)
                .setPrefHeight(100)
                ;
    }

}
