package org.joshy.gfx.test.text;

import org.joshy.gfx.Core;
import org.joshy.gfx.SkinManager;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.stage.Stage;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Feb 6, 2010
 * Time: 7:37:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class MultiFont implements Runnable {
    public static void main(String ... args) throws Exception, InterruptedException {
        Core.setUseJOGL(true);
        Core.init();
        Core.getShared().defer(new MultiFont());
    }

    public void run() {
        try {
            final Font[] fonts = new Font[4];
            for(int i=0; i<fonts.length;i++) {
                fonts[i] = Font.name("Helvetica").size(11f + ((float) (i*8))).resolve();
            }

            Stage stage = Stage.createStage();
            stage.setContent(new Node() {
                @Override
                public void draw(GFX g) {
                    g.setPaint(FlatColor.BLACK);
                    String text = "The lazy dog jumped over the quick fox";
                    double y = 0;
                    for(int i=0; i<fonts.length;i++) {
                        double h = fonts[i].getAscender()+fonts[i].getDescender();
                        y+=h;
                        g.drawText(text,fonts[i],10, y);
                    }
                }

                @Override
                public Bounds getVisualBounds() {
                    return new Bounds(0,0,100,100);
                }

                @Override
                public Bounds getInputBounds() {
                    return new Bounds(0,0,100,100);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
