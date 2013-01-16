package org.joshy.gfx.test.misc;

import org.joshy.gfx.Core;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.Event;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.SystemMenuEvent;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.u;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: 10/4/11
 * Time: 1:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImportDragTest implements Runnable {
    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().defer(new ImportDragTest());
    }

    @Override
    public void run() {
        Stage stage = Stage.createStage();
        stage.setContent(new Button("this is a button"));

        Stage stage2 = Stage.createStage();
        stage2.setContent(new Button("this is a button"));

        EventBus.getSystem().addListener(SystemMenuEvent.Quit, new Callback<Event>() {
            @Override
            public void call(Event event) throws Exception {
                u.p("quit event");
                System.exit(0);
            }
        });

        EventBus.getSystem().addListener(SystemMenuEvent.FileDrop, new Callback<SystemMenuEvent>() {
            @Override
            public void call(SystemMenuEvent event) throws Exception {
                u.p("the user dropped some files in:");
                u.p("the source is: " + event.getSource());
                u.p(event.getFiles());
            }
        });
    }
}
