package org.joshy.gfx.test.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.MouseEvent;
import org.joshy.gfx.node.control.CompoundListView;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.control.ListModel;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.GraphicsUtil;

import java.util.ArrayList;
import java.util.List;

public class CollapsingListTest implements Runnable {
    public static void main(String... args) throws Exception {
        Core.setUseJOGL(false);
        Core.init();
        Core.getShared().defer(new CollapsingListTest());
    }

    public void run() {
        final CompoundListView list = new CompoundListView();
        list.setModel(new CollapsingListModel());
        list.setItemViewFactory(new CompoundListView.ItemViewFactory(){
            @Override
            public Control createItemView(CompoundListView list, int index, Control prev) {
                return new TreeItemView(list, index);
            }
        });
        Stage stage = Stage.createStage();
        stage.setContent(list);
        list.setSelectedIndex(3);
    }

    private class CollapsingListModel implements ListModel<Blah> {
        private Blah root;

        private CollapsingListModel() {
            root = new Blah(null,"root");
            root.add(new Blah(root,"c1"));
            root.add(new Blah(root,"c2"));
            Blah b = new Blah(root,"p1");
            b.add(new Blah(b,"ac1"));
            b.add(new Blah(b,"ac2"));
            b.collapsed = true;
            root.add(b);
            root.add(new Blah(root,"cx"));
        }

        public Blah get(int i) {
            int count = 0;
//            u.p("getting " + i);
            return findChild(root, count, i);
        }

        private Blah findChild(Blah root, int count, int i) {
//            u.p("at: " + root.name);
            if (count == i) {
//                u.p("returning: " + root.name);
                return root;
            }
            if(root.collapsed) {
                return null;
            }
            count+=1; //+1 for the node itself
//            u.p("   finding " + count + " " + i + " childount = " + root.children.size());
            for (Blah b : root.children) {
//                u.p("count = " + count + " b.count = " + b.count);
                if (count + b.count >= i) {
//                    u.p("recurse");
                    return findChild(b, count, i);
                }
                count += 1; // account for the node itself
                if(!b.collapsed) {
                    count += b.count; // account for the child nodes
                }
            }
            return null;
        }

        public int size() {
            return calculateSize(root);
        }

        private int calculateSize(Blah root) {
            if(root.collapsed) {
                return 1;
            }
            int total = 0;
            for (Blah b : root.children) {
                total += calculateSize(b);
            }
            return total + 1; //add one for the root itself
        }
    }

    private class Blah {
        public String name = "unnamed";
        private List<Blah> children = new ArrayList<Blah>();
        private int count = 0;
        private Blah parent;
        private boolean collapsed = false;

        private Blah(Blah parent) {
            this.parent = parent;
        }

        public Blah(Blah parent, String name) {
            this(parent);
            this.name = name;
        }

        public int branchCount() {
            return count;
        }

        public void add(Blah blah) {
            children.add(blah);
            count++;
            if (parent != null) parent.addedChild();
        }

        private void addedChild() {
            count++;
//            u.p("total count = " + count);
            if (parent != null) parent.addedChild();
        }

        private int depth() {
            if(parent == null) return 0;
            return 1 + parent.depth();
        }

        public void toggle() {
            collapsed = !collapsed;
        }
    }

    private class TreeItemView extends Control {
        private final CompoundListView list;
        private final int index;

        public TreeItemView(final CompoundListView list, final int index) {
            this.list = list;
            this.index = index;
            EventBus.getSystem().addListener(this, MouseEvent.MousePressed, new Callback<MouseEvent>() {
                public void call(MouseEvent event) {
                    Blah item = (Blah) list.getModel().get(index);
                    if (item != null && item.count > 0) {
                        int inset = item.depth() * 13 + 3;
                        if (event.getX() > inset && event.getX() < inset + 20) {
                            item.toggle();
                            setDrawingDirty();
                        }
                    }
                }
            });
        }

        @Override
        public void doLayout() {
        }

        @Override
        public void doPrefLayout() {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void doSkins() {
        }

        @Override
        public void draw(GFX gfx) {
            gfx.setPaint(FlatColor.WHITE);
            Blah item = (Blah) list.getModel().get(index);
            if (index == list.getSelectedIndex()) {
                gfx.setPaint(new FlatColor(0xddddff));
            }
            gfx.fillRect(0,0,getWidth(),getHeight());

            if (item != null) {
                gfx.setPaint(FlatColor.BLACK);
                int inset = item.depth()*13+3;
                if(item.count > 0) {
                    if(item.collapsed) {
                        GraphicsUtil.fillRightArrow(gfx,inset,getHeight()-3-14,14);
                    } else {
                        GraphicsUtil.fillDownArrow(gfx,inset,getHeight()-3-14,14);
                    }
                }
                gfx.drawText(item.name + " count = " + item.count, Font.DEFAULT, inset+20, 0 + 20);
            }
        }
    }
}
