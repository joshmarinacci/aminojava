package org.joshy.gfx.test.text;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.control.Textarea;
import org.joshy.gfx.node.control.Textbox;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.u;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 28, 2010
 * Time: 2:47:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class Text implements Runnable {
    public static void main(String ... args) throws Exception {
        Core.setUseJOGL(true);
        Core.init();
        Core.getShared().defer(new Text());
    }

    public void run() {
        Stage stage = Stage.createStage();

        Panel p = new Panel();
        p.add(new Node() {

            @Override
            public void draw(GFX g) {
                Text.this.draw(g);
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

            Button b = new Button();
            b.setText("Action Yay!");
            p.add(b);

            Textbox tb = new Textbox();
            tb.setTranslateX(20);
            tb.setTranslateY(50);
            tb.setFont(Font.name("ChunkFive").size(50).resolve());
            tb.setText("typing");
            p.add(tb);

            Textarea ta = new Textarea();
            ta.setTranslateY(320);
            ta.setWidth(200);
            ta.setHeight(100);
            ta.setText("typing\nmultiline");
            p.add(ta);
        stage.setContent(p);

    }

    private void draw(GFX g) {

        g.setPaint(FlatColor.RED);
        g.drawRect(100,100,200,30);

        g.setPaint(FlatColor.BLACK);
        Font font = Font.name("Arial").size(30).resolve();

        Font.drawCentered(g, "FuyQx", font, 100,100,200,30, true);

        g.translate(100,200);
        Font.drawLines(g, font, "asdf","AS#SaaeYyasd","887&*(&asd");
        g.translate(-100,-200);
    }

    private void drawCentered(GFX g, Font font, double xoff, double yoff, double width, double height) {
        String string = "FuyQx";
        double tw = font.getWidth(string);
        u.p("tw = " + tw);
        double th = font.getAscender() + font.getDescender();
        u.p("th = " + th);
        g.drawText(string,font,xoff + (width -tw)/2, yoff + (height -th)/2 + font.getAscender());
        //g.drawRect(xoff+(width-tw)/2, yoff+(height-th)/2,tw,th);
    }
}
