package org.joshy.gfx.node.control;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.SkinEvent;
import org.joshy.gfx.util.u;

public class Menubar {
    private List<Menu> menus = new ArrayList<Menu>();
    private JFrame jframe;
    private JMenuBar jmenubar;

    public Menubar(JFrame frame) {
        jframe = frame;
        jmenubar = new JMenuBar();
        jframe.setJMenuBar(jmenubar);
        EventBus.getSystem().addListener(SkinEvent.SystemWideReload, new Callback<SkinEvent>() {
            public void call(SkinEvent skinEvent) throws Exception {
                u.p("skins were reloaded system wide. time to refresh the jmenus");
                jmenubar.removeAll();
                for(Menu m : menus) {
                    jmenubar.add(m.createJMenu());
                }
                jmenubar.validate();
            }
        });
    }

    public void remove(Menu menu) {
        this.menus.remove(menu);
        this.jmenubar.remove(menu.getNativeMenu());
    }

    public void add(Menu menu) {
        this.menus.add(menu);
        this.jmenubar.add(menu.createJMenu());
    }
}

