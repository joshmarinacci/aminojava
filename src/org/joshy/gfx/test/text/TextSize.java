package org.joshy.gfx.test.text;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.Event;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.SystemMenuEvent;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.stage.Stage;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: 4/26/11
 * Time: 2:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class TextSize implements Runnable {
    public static void main(String ... args) throws Exception {
         Core.setUseJOGL(false);
         Core.init();
         Core.getShared().defer(new TextSize());
     }

    public void run() {
        EventBus.getSystem().addListener(SystemMenuEvent.Quit, new Callback<Event>() {
            @Override
            public void call(Event event) throws Exception {
                System.exit(0);
            }
        });
         Stage stage = Stage.createStage();
        final Font font = Font.name("ChunkFive").size(50).resolve();

         Panel p = new Panel();
         p.add(new Node() {

             @Override
             public void draw(GFX g) {
                 //String str = "This is big text! TEXT BIG! SMASH!";
                 String str = "Hi There";
                 double w = font.calculateWidth(str);
                 g.setPaint(FlatColor.BLACK);
                 g.drawText(str, font, 0,50);
                 g.setPaint(FlatColor.RED);
                 g.drawRect(0,0,w,50);
             }

             @Override
             public Bounds getVisualBounds() {
                 return new Bounds(0,0,200,200);
             }

             @Override
             public Bounds getInputBounds() {
                 return getVisualBounds();
             }
         });

        stage.setContent(p);
        stage.setWidth(800);
    }
}
