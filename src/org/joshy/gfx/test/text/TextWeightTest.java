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
import org.joshy.gfx.util.u;

import java.awt.*;
import java.io.IOException;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: 10/2/11
 * Time: 3:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class TextWeightTest implements Runnable {
    private Font opensans;

    public static void main(String ... args) throws Exception {
        Core.setUseJOGL(false);
        Core.init();
        Core.getShared().defer(new TextWeightTest());
    }

    @Override
    public void run() {
        /*
        try {
            URL url2 = TextWeightTest.class.getResource("/org/joshy/gfx/stage/swing/fonts/OpenSans-Bold.ttf");
            Font.registerFont(url2,"OpenSans",Font.Weight.Bold,Font.Style.Regular);
            URL url3 = TextWeightTest.class.getResource("/org/joshy/gfx/stage/swing/fonts/OpenSans-Italic.ttf");
            Font.registerFont(url3,"OpenSans",Font.Weight.Regular,Font.Style.Italic);
            URL url4 = TextWeightTest.class.getResource("/org/joshy/gfx/stage/swing/fonts/OpenSans-BoldItalic.ttf");
            Font.registerFont(url4,"OpenSans",Font.Weight.Bold,Font.Style.Italic);
            URL url = TextWeightTest.class.getResource("/org/joshy/gfx/stage/swing/fonts/OpenSans-Regular.ttf");
            Font.registerFont(url,"OpenSans",Font.Weight.Regular,Font.Style.Regular);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (FontFormatException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        */
        /*
        u.p(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        registerFont("OpenSans-Regular.ttf");
        registerFont("OpenSans-Bold.ttf");
        u.p(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        final java.awt.Font f1 = new java.awt.Font("OpenSans", java.awt.Font.PLAIN, 18);
        u.p("f1 = " + f1.getFamily() + " " + f1.getFontName() + " " + f1.getSize() + " " + f1.getStyle());
        final java.awt.Font f2 = new java.awt.Font("OpenSans-Bold", java.awt.Font.BOLD, 18);
        //u.p("f1 = " + f1.getFamily() + " " + f1.getFontName() + " " + f1.getSize() + " " + f1.getStyle());
        JFrame frame = new JFrame();
        frame.add(new JComponent() {
            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                graphics.setFont(f1);
                graphics.drawString("foo", 10, 30);
                graphics.setFont(f2);
                graphics.drawString("foo", 10, 60);
            }
        });
        frame.pack();
        frame.setSize(300,300);
        frame.setVisible(true);

        */

        //opensans = Font.fromURL(url).size(18).resolve();
        Stage stage = Stage.createStage();
        EventBus.getSystem().addListener(SystemMenuEvent.Quit, new Callback<Event>() {
            @Override
            public void call(Event event) throws Exception {
                System.exit(0);
            }
        });
        Panel p = new Panel();
        p.add(new Node() {

            @Override
            public void draw(GFX g) {
                TextWeightTest.this.draw(g);
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

    }

    private void registerFont(String s) {
        URL url = TextWeightTest.class.getResource("/org/joshy/gfx/stage/swing/fonts/"+s);
        try {
            boolean value = GraphicsEnvironment.getLocalGraphicsEnvironment()
                    .registerFont(java.awt.Font
                            .createFont(java.awt.Font.TRUETYPE_FONT, url.openStream()));
            u.p("registered = " + value);
        } catch (FontFormatException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void draw(GFX g) {
        g.setPaint(FlatColor.BLACK);
        Font font =null;

        font = Font.name("Arial").size(18).resolve();
        g.translate(0,30);
        g.drawText("Font: " + font.getName() + " " + font.getSize() + " " + font.getWeight(), font, 10, 50);
        font = Font.name("Arial").size(18).weight(Font.Weight.Bold).resolve();
        g.translate(0,30);
        g.drawText("Font: " + font.getName() + " " + font.getSize() + " " + font.getWeight(), font, 10, 50);
        font = Font.name("Arial").size(18).style(Font.Style.Italic).resolve();
        g.translate(0,30);
        g.drawText("Font: " + font.getName() + " " + font.getSize() + " " + font.getWeight(), font, 10, 50);
        g.translate(0,-30*3);

        g.translate(0,200);
        font = Font.name("Open Sans").size(18).weight(Font.Weight.Regular).style(Font.Style.Regular).resolve();
        g.translate(0,30);
        g.drawText("Font: " + font.getName() + " " + font.getSize() + " " + font.getWeight(), font, 10, 50);
        font = Font.name("Open Sans").size(18).weight(Font.Weight.Bold).resolve();
        g.translate(0, 30);
        g.drawText("Font: " + font.getName() + " " + font.getSize() + " " + font.getWeight(), font, 10, 50);
        font = Font.name("Open Sans").size(18).style(Font.Style.Italic).resolve();
        g.translate(0,30);
        g.drawText("Font: " + font.getName() + " " + font.getSize() + " " + font.getWeight(), font, 10, 50);
        font = Font.name("Open Sans").size(18).weight(Font.Weight.Bold).style(Font.Style.Italic).resolve();
        g.translate(0,30);
        g.drawText("Font: " + font.getName() + " " + font.getSize() + " " + font.getWeight(), font, 10, 50);
        font = Font.DEFAULT;//Font.name("Open Sans").size(18).weight(Font.Weight.Bold).style(Font.Style.Italic).resolve();
        g.translate(0,30);
        g.drawText("Font: " + font.getName() + " " + font.getSize() + " " + font.getWeight(), font, 10, 50);
        g.translate(0,-30*4);
        g.translate(0,-200);
    }
}
