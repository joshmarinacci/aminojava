package org.joshy.gfx.test.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.Event;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.SystemMenuEvent;
import org.joshy.gfx.node.control.Textarea;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.stage.Stage;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Nov 5, 2010
 * Time: 1:28:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class TextareaTest implements Runnable {
    public static void main(String ... args) throws Exception, InterruptedException {
        Core.setUseJOGL(false);
        Core.init();
        Core.getShared().defer(new TextareaTest());
    }

    @Override
    public void run() {
        EventBus.getSystem().addListener(SystemMenuEvent.Quit, new Callback<Event>() {
            @Override
            public void call(Event event) throws Exception {
                System.exit(0);
            }
        });
        Stage stage = Stage.createStage();
        //stage.setContent(g);
//final Textarea ta = new Textarea("hello there! hello there! how are you doing today mister man?");
        final Textarea ta = new Textarea("Hello There");
        ta.setFont(Font.name("ChunkFive").size(100).resolve());
        ta.setSizeToText(true);
        //ta.setSize
        stage.setContent(new Panel().add(ta));
        stage.setWidth(800);
    }
}
