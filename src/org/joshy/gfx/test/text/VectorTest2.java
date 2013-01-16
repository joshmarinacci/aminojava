package org.joshy.gfx.test.text;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.stage.Stage;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Apr 3, 2010
 * Time: 11:31:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class VectorTest2 implements Runnable {
    public static void main(String ... args) throws Exception {
        Core.setUseJOGL(true);
        Core.init();
        Core.getShared().defer(new VectorTest2());
    }

    public void run() {
        Stage stage = Stage.createStage();
        org.joshy.gfx.node.layout.Panel panel = new org.joshy.gfx.node.layout.Panel();
        panel.add(new TextNode());
        stage.setContent(panel);

    }

    private class TextNode extends Node {
        @Override
        public void draw(GFX g) {
            g.setPaint(FlatColor.RED);
            //g.drawRect(100.5,100.5,200,30);
            g.setPaint(FlatColor.BLACK);
            Font font = Font.name("Arial").size(100).resolve();
            g.drawText("abc",font,50,200);
        }

        @Override
        public Bounds getVisualBounds() {
            return new Bounds(0, 0, 300, 300);
        }

        @Override
        public Bounds getInputBounds() {
            return getVisualBounds();
        }
    }
}
