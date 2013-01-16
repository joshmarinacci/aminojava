package org.joshy.gfx.draw;

import org.joshy.gfx.Core;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.SystemMenuEvent;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.stage.Stage;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: 12/20/10
 * Time: 12:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class GFXStateTest implements Runnable {
    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().defer(new GFXStateTest());
    }

    @Override
    public void run() {
        EventBus.getSystem().addListener(SystemMenuEvent.Quit, new Callback<SystemMenuEvent>(){
            @Override
            public void call(SystemMenuEvent event) throws Exception {
                System.exit(0);
            }
        });
        Stage stage = Stage.createStage();
        stage.setContent(new Node() {

            @Override
            public void draw(GFX g) {
                g.setPaint(FlatColor.BLUE);
                g.drawLine(0,0,50,50);
                g.push();
                g.setPaint(FlatColor.RED);
                g.translate(100, 0);
                g.drawLine(0, 0, 50, 50);
                g.translate(50, 0);
                g.setPaint(FlatColor.GREEN);
                g.drawLine(0, 0, 50, 50);
                g.pop();
                g.drawLine(0,50,50,100);
            }

            @Override
            public Bounds getVisualBounds() {
                return new Bounds(0,0,0,0);
            }

            @Override
            public Bounds getInputBounds() {
                return getVisualBounds();
            }
        });
    }
}
