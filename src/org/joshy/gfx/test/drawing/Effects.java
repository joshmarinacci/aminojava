package org.joshy.gfx.test.drawing;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.draw.ImageBuffer;
import org.joshy.gfx.draw.effects.BlurEffect;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.stage.Stage;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Aug 3, 2010
 * Time: 1:14:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class Effects implements Runnable {
    public static void main(String ... args) throws Exception, InterruptedException {
        Core.init();
        Core.getShared().defer(new Effects());
    }

    public void run() {

        Node node = new Node() {

            @Override
            public void draw(GFX g) {
                g.setPaint(FlatColor.RED);
                g.drawLine(0,0,100,100);


                ImageBuffer buf = g.createBuffer(100,25);
                GFX g2 = buf.getGFX();
                g2.setPaint(new FlatColor(0.1,0.1,0.1,0.3));
                g2.drawText("ABCabc", Font.DEFAULT,20,20);
                buf.apply(new BlurEffect(3));
                g.draw(buf,0,0);

                g.setPaint(new FlatColor(0,0,0,1.0));
                g.drawText("ABCabc", Font.DEFAULT,20,20-1);


            }

            @Override
            public Bounds getVisualBounds() {
                return new Bounds(0,0,100,100);
            }

            @Override
            public Bounds getInputBounds() {
                return getVisualBounds();
            }
        };

        Stage stage = Stage.createStage();
        stage.setContent(node);
    }

}
