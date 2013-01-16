package org.joshy.gfx.node.layout;

import org.joshy.gfx.SkinManager;
import org.joshy.gfx.css.CSSMatcher;
import org.joshy.gfx.css.CSSSkin;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.MouseEvent;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.control.Control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Jul 5, 2010
 * Time: 6:27:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class TabPanel extends Panel {
    private TabTop tabtop = new TabTop();
    private List<Control> tabs = new ArrayList<Control>();
    private Map<Control,CharSequence> titleMap = new HashMap<Control,CharSequence>();

    public TabPanel() {
        add(tabtop);
    }

    public TabPanel add(CharSequence title, Control control) {
        add(control);
        if(!tabs.isEmpty()) {
            control.setVisible(false);
        }
        tabs.add(control);
        titleMap.put(control,title);
        return this;
    }

    @Override
    public void doPrefLayout() {
        super.doPrefLayout();    //To change body of overridden methods use File | Settings | File Templates.
        for(Control c : controlChildren()) {
            c.doPrefLayout();
        }
    }

    @Override
    public void doLayout() {
        for(Control c : controlChildren()) {
            if(c == tabtop) {
                tabtop.setWidth(getWidth());
                tabtop.setHeight(30);
                tabtop.setTranslateX(0);
                tabtop.setTranslateY(0);
            } else {
                c.setWidth(getWidth());
                c.setHeight(getHeight()-30);
                c.setTranslateX(0);
                c.setTranslateY(30);
            }
            c.doLayout();
        }
        boxPainter = cssSkin.createBoxPainter(this, styleInfo, sizeInfo, "", CSSSkin.State.None);
        setDrawingDirty();
    }

    public TabPanel setSelected(Node node) {
        for(Control c : tabs){
            c.setVisible(false);
        }
        node.setVisible(true);
        setDrawingDirty();
        return this;
    }

    public Control getSelected() {
        for(Control c : tabs) {
            if(c.isVisible()) return c;
        }
        return null;
    }

    private class TabTop extends Control {
        private TabTop() {
            EventBus.getSystem().addListener(this, MouseEvent.MousePressed, new Callback<MouseEvent>(){
                public void call(MouseEvent event) {
                    int x = (int) (event.getX()/getWidth()*tabs.size());
                    setSelected(tabs.get(x));
                }
            });
        }

        @Override
        public void doLayout() {
        }

        @Override
        public void doPrefLayout() {
            
        }

        @Override
        public void doSkins() {
            cssSkin = SkinManager.getShared().getCSSSkin();
        }

        @Override
        public void draw(GFX g) {
            g.setPaint(FlatColor.GRAY);
            g.fillRect(0,0,getWidth(),getHeight());


            double size = getWidth()/tabs.size();
            double x = 0;
            for(Control c : tabs) {
                CSSMatcher matcher = new CSSMatcher(this);
                if(c.isVisible()) {
                    matcher.pseudo = "selected";
                }
                Bounds bounds = new Bounds(x,0,size,30);
                CharSequence title = titleMap.get(c);
                cssSkin.drawBackground(g,matcher,bounds);
                cssSkin.drawBorder(g,matcher,bounds);
                cssSkin.drawText(g,matcher,bounds,title.toString());
                x+=size;
            }
        }
    }
}
