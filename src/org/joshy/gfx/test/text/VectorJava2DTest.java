package org.joshy.gfx.test.text;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.io.File;
import java.io.IOException;

public class VectorJava2DTest implements Runnable {
    public static void main(String ... args) {
        SwingUtilities.invokeLater(new VectorJava2DTest());
    }

    Font fnt = null;
    public void run() {

        try {
            Font fnt2 = Font.createFont(Font.TRUETYPE_FONT, new File("goudy.ttf"));
            fnt = fnt2.deriveFont(60.0f);
            //fnt = new Font("Arial",Font.PLAIN, 30);
        } catch (FontFormatException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        JFrame frame = new JFrame();

        frame.add(new JComponent(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


                g2.setColor(Color.GREEN);
                g2.setFont(fnt);
                g2.drawString("asdf",100,100);
                
                g2.setPaint(Color.RED);
                g2.translate(50,150);
                fillVector(g2);


            }
        });

        frame.pack();
        frame.setSize(200,200);
        frame.setVisible(true);
    }

    private void drawVector(Graphics2D g) {
        String text = "&";
        g.setFont(new Font("Arial",Font.PLAIN,50));
        FontRenderContext frc = g.getFontRenderContext();
        GlyphVector vector = g.getFont().createGlyphVector(frc,text);
        p("--------------");



        for(int i=0; i<vector.getNumGlyphs(); i++) {
            p("--");
            Shape glyphShape = vector.getGlyphOutline(i);
            PathIterator it = glyphShape.getPathIterator(new AffineTransform());
            it = new FlatteningPathIterator(it,0.5);
            double[] coords = new double[6];
            double px = 0;
            double py = 0;
            double mx = 0;
            double my = 0;
            int pointCount = 0;
            while(true) {
                if(it.isDone()) break;
                int type = it.currentSegment(coords);
                p(type,coords);
                if(type == PathIterator.SEG_LINETO) {
                    g.drawLine((int)px,(int)py, (int)coords[0], (int)coords[1]);
                    px = coords[0];
                    py = coords[1];
                }
                if(type == PathIterator.SEG_MOVETO) {
                    px = coords[0];
                    py = coords[1];
                    mx = px;
                    my = py;
                }
                if(type == PathIterator.SEG_CLOSE) {
                    g.drawLine((int)px,(int)py, (int)mx, (int)my);
                }
                pointCount++;
                it.next();
            }
            p("-- count = " + pointCount);
        }

        g.dispose();

        p("--------------");

    }
    private void fillVector(Graphics2D g) {
        String text = "X";
        g.setFont(new Font("Arial",Font.PLAIN,100));
        FontRenderContext frc = g.getFontRenderContext();
        GlyphVector vector = g.getFont().createGlyphVector(frc,text);
        p("--------------");



        for(int i=0; i<vector.getNumGlyphs(); i++) {
            p("--");
            Shape glyphShape = vector.getGlyphOutline(i);
            PathIterator it = glyphShape.getPathIterator(new AffineTransform());
            it = new FlatteningPathIterator(it,0.5);
            double[] coords = new double[6];
            double px = 0;
            double py = 0;
            double mx = 0;
            double my = 0;
            int pointCount = 0;
            while(true) {
                if(it.isDone()) break;
                int type = it.currentSegment(coords);
                p(type,coords);
                if(type == PathIterator.SEG_LINETO) {
                    //g.drawLine((int)px,(int)py, (int)coords[0], (int)coords[1]);
                    int[] xp = new int[4];
                    xp[0] = (int) mx;
                    xp[1] = (int) px;
                    xp[2] = (int) coords[0];
                    xp[3] = (int) mx;
                    int[] yp = new int[4];
                    yp[0] = (int) my;
                    yp[1] = (int) py;
                    yp[2] = (int) coords[1];
                    yp[3] = (int) my;
                    g.drawPolygon(xp,yp,4);
                    px = coords[0];
                    py = coords[1];
                }
                if(type == PathIterator.SEG_MOVETO) {
                    px = coords[0];
                    py = coords[1];
                    mx = px;
                    my = py;
                }
                if(type == PathIterator.SEG_CLOSE) {
                    g.drawLine((int)px,(int)py, (int)mx, (int)my);
                }
                pointCount++;
                it.next();
            }
            p("-- count = " + pointCount);
        }

        g.dispose();

        p("--------------");

    }

    private void p(int type, double[] coords) {
        switch(type) {
            case PathIterator.SEG_MOVETO: p("moveto"); break;
            case PathIterator.SEG_LINETO: p("lineto"); break;
            case PathIterator.SEG_QUADTO: p("quadto"); break;
            case PathIterator.SEG_CUBICTO: p("cubicto"); break;
            case PathIterator.SEG_CLOSE: p("close"); break;
        }
    }

    private static void p(String s) {
        System.out.println(s);
    }
}
