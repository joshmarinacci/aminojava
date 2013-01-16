package org.joshy.gfx.test.drawing;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Group;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.shape.Oval;
import org.joshy.gfx.node.shape.Rectangle;
import org.joshy.gfx.stage.Stage;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 22, 2010
 * Time: 2:51:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class Geometry implements Runnable {
    public static void main(String... args) throws Exception {
        Core.setUseJOGL(false);
        Core.init();
        Core.getShared().defer(new Geometry());
    }

    public void run() {
        Stage stage = Stage.createStage();

        Group g = new Group();
        g.add(new Node() {
            @Override
            public void draw(GFX g) {
                g.setPaint(FlatColor.BLACK);
                g.drawRect(20.5, 20.5, 50, 50);
                g.setPaint(FlatColor.RED);
                g.drawLine(30, 40, 50, 60);

                g.translate(100, 0);
                g.setPaint(FlatColor.BLUE);
                double[] points = new double[]{
                        40, 40,
                        100, 40,
                        40, 100,
                        100, 100};
                g.drawPolygon(points);
                g.translate(-100, 0);

                g.setPaint(FlatColor.GREEN);
                g.fillRoundRect(100, 300, 60, 30, 10, 10);

                g.drawRoundRect(100, 400, 60, 30, 10, 10);

                g.setPaint(FlatColor.BLACK);
                g.fillCustomRoundRect(
                        230, 300, 100, 100, //x,y,w,h
                        0, 0, //top left
                        30, 30, //top right
                        0, 0, //bottom right
                        30, 30 //bottom left
                );
                g.fillRoundRect(
                        350, 300, 100, 100, //x,y,w,h
                        60, 60
                );
                g.drawCustomRoundRect(
                        230, 420, 100, 100, //x,y,w,h
                        30, 30, //top left
                        30, 30, //top right
                        0, 0, //bottom right
                        0, 0 //bottom left
                );
                g.drawRoundRect(
                        350, 420, 100, 100, //x,y,w,h
                        60, 60
                );
            }

            @Override
            public Bounds getVisualBounds() {
                return new Bounds(0, 0, 10, 10);
            }

            @Override
            public Bounds getInputBounds() {
                return new Bounds(0, 0, 10, 10);
            }
        }
        );

        Rectangle r = new Rectangle();
        r.setTranslateX(10);
        r.setTranslateY(10);
        r.setWidth(100);
        r.setHeight(100);
        r.setFill(FlatColor.RED);
        r.setStroke(FlatColor.BLACK);
        g.add(r);

        Oval o = new Oval();
        o.setTranslateX(100);
        o.setTranslateY(200);
        o.setWidth(200);
        o.setHeight(100);
        o.setFill(FlatColor.GREEN);
        o.setStroke(FlatColor.BLACK);
        g.add(o);
        
        stage.setContent(g);
    }
}
