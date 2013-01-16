package org.joshy.gfx.test.threedee;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.stage.PerspectiveCamera;
import org.joshy.gfx.stage.Stage;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 27, 2010
 * Time: 5:18:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class DirectGL implements Runnable {

    public static void main(String... args) throws Exception {
        Core.setUseJOGL(true);
        Core.init();
        Core.getShared().defer(new DirectGL());
    }

    public void run() {
        try {
            //SkinManager.getShared().parseStylesheet(new File("assets/style.xml").toURI().toURL());
            //Rectangle r = new Rectangle();
            //r.setTranslateX(100);
            //r.setFill(FlatColor.GREEN);

            //Panel panel = new Panel();
            //panel.add(r);

            //panel.add(new GLNode());
            //Button b = new Button();
            //b.setText("asdf");
            //panel.add(b);


            //Textbox textbox = new Textbox();
            //textbox.setText("asdf");
            //textbox.setTranslateX(200);
            //textbox.setTranslateY(200);
            //panel.add(textbox);

            Stage stage = Stage.createStage();
            stage.setCamera(new PerspectiveCamera());
            stage.setContent(new GLNode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class GLNode extends Node {
        private int count;


        @Override
        public void draw(GFX g) {

            // regular drawing
            g.setPaint(FlatColor.RED);
            g.drawRect(200, 200, 20, 20);
            
            //custom GL drawing
            if (g.isGL()) {
                count++;
                GLAutoDrawable drawable = g.getDrawable();
                GL2 gl = drawable.getGL().getGL2();
                gl.glPushMatrix();
                gl.glColor3d(0.5, 1.0, 0.5);

                gl.glTranslated(200, 200, 0);
                gl.glRotated(count, 0, 1, 1);
                gl.glBegin(GL.GL_LINE_STRIP);
                gl.glVertex2f(0, 0);
                for (int i = 0; i < 40; i++) {
                    for (int j = 0; j < 40; j++) {
                        gl.glVertex3d(
                                Math.sin(Math.toRadians(i * 10)) * 40,
                                Math.cos(Math.toRadians(i * 10)) * 50,
                                Math.cos(Math.toRadians(j * 10)) * 60);
                    }
                    //gl.glVertex2d(Math.random()*100,Math.random()*100);
                }
                //gl.glVertex2d(200,100);
                //gl.glVertex2d(0,100);
                gl.glVertex2d(0, 0);
                gl.glEnd();

                gl.glPopMatrix();
            }
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
