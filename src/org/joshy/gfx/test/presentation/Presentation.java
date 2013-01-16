package org.joshy.gfx.test.presentation;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.draw.Image;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Feb 16, 2010
 * Time: 4:57:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class Presentation implements Runnable {
    private Font font;
    private Image image;
    private Font font2;

    public static void main(String ... args) throws Exception {
        Core.setUseJOGL(false);
        Core.init();
        Core.getShared().defer(new Presentation());
    }

    public void run() {
        Panel panel = new Panel();
        panel.add(new Node() {

            @Override
            public void draw(GFX g) {
                doDraw(g,this);
            }

            @Override
            public Bounds getVisualBounds() {
                return new Bounds(0,0,300,300);

            }

            @Override
            public Bounds getInputBounds() {
                return getVisualBounds();
            }
        });

        font = Font.name("League Gothic").size(120).resolve();
        font2 = Font.name("League Gothic").size(60).resolve();
        try {
            image = Image.create(ImageIO.read(Presentation.class.getResource("Test01.png")));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        Stage stage = Stage.createStage();
        stage.setContent(panel);
        stage.setWidth(1024);
        stage.setHeight(768);
    }

    private void doDraw(GFX g, Node node) {
        String text = "Welcome to Bedrock";
        String text2 = "by josh marinacci";
        g.drawImage(image,0,0);

        Stage s = node.getParent().getStage();

        double x = 0;
        double y = 0;

        x = (s.getWidth()-font.getWidth(text))/2;
        y = (s.getHeight()/2) - font.getDescender();
        g.setPaint(FlatColor.BLACK);
        g.drawText(text,font,x+4,y+4);
        g.setPaint(FlatColor.WHITE);
        g.drawText(text,font,x,y);

        x = (s.getWidth()-font2.getWidth(text2))/2;
        y = y + font2.getAscender() + font2.getDescender();
        g.setPaint(FlatColor.BLACK);
        g.drawText(text2,font2,x+4,y+4);
        g.setPaint(FlatColor.WHITE);
        g.drawText(text2,font2,x,y);
    }
}
