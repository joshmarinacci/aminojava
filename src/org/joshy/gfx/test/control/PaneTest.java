package org.joshy.gfx.test.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.node.layout.SplitPane;
import org.joshy.gfx.stage.Stage;

public class PaneTest implements Runnable {
    public static void main(String ... args) throws Exception {
        Core.setUseJOGL(false);
        Core.init();
        Core.getShared().defer(new PaneTest());
    }

    public void run() {
        Stage stage = Stage.createStage();
        SplitPane pane = new SplitPane(true);
        pane.setPosition(200);


        SplitPane pane2 = new SplitPane(false);
        pane2.setFirst(new TestPanel());
        pane2.setSecond(new TestPanel());

        pane.setFirst(new TestPanel());
        pane.setSecond(pane2);
        stage.setContent(pane);
    }

    private class TestPanel extends Panel {
        @Override
        public void draw(GFX g) {
            g.setPaint(FlatColor.WHITE);
            g.fillRect(0,0,getWidth(),getHeight());
            g.setPaint(FlatColor.BLACK);
            g.drawLine(0,0,getWidth(),getHeight());
            g.drawLine(0,getHeight(),getWidth(),0);
        }
    }
}
