package org.joshy.gfx.test.drawing;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.draw.Image;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Group;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.u;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 21, 2010
 * Time: 8:17:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class Particles implements Runnable {
    public static void main(String ... args) throws Exception {
        Core.setUseJOGL(true);
        Core.init();
        Core.getShared().defer(new Particles());
    }

    public void run() {
        try {

        //draw a red circle
        BufferedImage bi = new BufferedImage(32,32,BufferedImage.TYPE_INT_ARGB);
        Graphics2D gfx = bi.createGraphics();
        gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gfx.setColor(new java.awt.Color(0,0,0,0));
        gfx.fillRect(0,0,32,32);
        gfx.setColor(new java.awt.Color(255,0,0,128));
        gfx.fillOval(0,0,32,32);
        gfx.dispose();
        final Image img = Image.create(bi);


        Stage stage = Stage.createStage();
        final List<Particle> particles = new ArrayList<Particle>();

        Group g2 = new Group();
        final Group g = new Group();
        g2.add(g);

//        Button b = new Button();
//        b.setWidth(100); b.setHeight(40); b.setTranslateX(20); b.setTranslateY(20);
//        g2.add(b);
        stage.setContent(g2);


        final double cx = 500;
        final double cy = 500;
        Timer timer = new Timer(1000/30, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(particles.size() < 5000) {
                    if(particles.size() % 100 == 0) {
                        u.p("size = " + particles.size());
                    }
                    for(int i=0; i<10; i++) {
                        Particle p = new Particle();
                        p.image = img;
                        p.x = cx+Math.random()*100;
                        p.y = cy+Math.random()*100;
                        p.xv = (Math.random()-0.5)*3.0;
                        p.yv = (Math.random()-0.5)*3.0;
                        particles.add(p);
                        g.add(p);
                    }
                }
                for(Particle p : particles) {
                    p.setY(p.y+p.yv);
                    p.setX(p.x+p.xv);

                    if(p.x < 0) p.setX(cx);
                    if(p.y < 0) p.setY(cy);
                    if(p.x > 1200) p.setX(cx);
                    if(p.y > 1000) p.setY(cy);

                }
            }
        });

        timer.start();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        
    }


    private static class Particle extends Node {
        Image image;
        public void setX(double x) {
            this.x = x;
            setDrawingDirty();
        }

        public double getX() {
            return x;
        }

        double x = 0;

        public double getXv() {
            return xv;
        }

        public void setXv(double xv) {
            this.xv = xv;
        }

        double xv = 0;

        public double getYv() {
            return yv;
        }

        public void setYv(double yv) {
            this.yv = yv;
        }

        double yv = 0;

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
            setDrawingDirty();
        }

        double y = 0;
        @Override
        public void draw(GFX g) {
            //g.setPaint(FlatColor.BLACK);
            //g.fillRect(x,y,10,10);
            g.drawImage(image,x,y);
        }

        @Override
        public Bounds getVisualBounds() {
            return new Bounds(x,y,10,10);
        }

        @Override
        public Bounds getInputBounds() {
            return getVisualBounds();
        }
    }
}
