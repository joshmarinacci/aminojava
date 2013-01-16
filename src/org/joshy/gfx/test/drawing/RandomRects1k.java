package org.joshy.gfx.test.drawing;

import org.joshy.gfx.*;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.node.Group;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.shape.Rectangle;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.Core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Timer;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 20, 2010
 * Time: 7:25:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class RandomRects1k implements Runnable {
    public static void main(String ... args) throws Exception {
        Core.setUseJOGL(true);
        Core.init();
        Core.getShared().defer(new RandomRects1k());
    }

    private static double rand(double min, double max) {
        return Math.random()*(max-min) + min;
    }

    public void run() {
        try {

        Stage stage = Stage.createStage();

        Group g = new Group();
        final List<Node> nodes = new ArrayList<Node>();
        for(int i=0; i<3000; i++) {

            Rectangle r = new Rectangle();
            r.setWidth(rand(10,100));
            r.setHeight(rand(10,100));
            r.setTranslateX(rand(0,300));
            r.setTranslateY(rand(0,300));
            r.setFill(new FlatColor(Math.random(),Math.random(),Math.random(),1.0));
            /*
            Button b = new Button();
            b.setWidth(rand(10,100));
            b.setHeight(rand(10,100));
            b.setTranslateX(rand(0,300));
            b.setTranslateY(rand(0,300));
              */
            nodes.add(r);
            g.add(r);
        }

        stage.setContent(g);
        Timer timer = new Timer(1000/60, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for(Node node : nodes) {
                    node.setTranslateX((node.getTranslateX()+1)%300);
                    node.setTranslateY((node.getTranslateY()+1)%300);
                }
            }
        });
        timer.start();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
