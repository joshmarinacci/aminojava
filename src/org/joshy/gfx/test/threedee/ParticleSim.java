package org.joshy.gfx.test.threedee;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.ChangedEvent;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.control.Label;
import org.joshy.gfx.node.control.PopupMenuButton;
import org.joshy.gfx.node.control.Slider;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.node.layout.VFlexBox;
import org.joshy.gfx.stage.Stage;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Feb 2, 2010
 * Time: 11:51:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class ParticleSim implements Runnable {
    public static void main(String... args) throws Exception {
        Core.setUseJOGL(true);
        Core.init();
        Core.getShared().defer(new ParticleSim());
    }

    public void run() {
        try {
            Stage stage = Stage.createStage();
            final ParticleSimView sim = new ParticleSimView();

            Panel panel = new Panel() {
                @Override
                public void doLayout() {
                    sim.setWidth(getWidth());
                    sim.setHeight(getHeight());
                    super.doLayout();    //To change body of overridden methods use File | Settings | File Templates.

                }
            };
            panel.add(sim);


            VFlexBox vbox = new VFlexBox() {
                @Override
                protected void drawSelf(GFX g) {
                    g.setPaint(new FlatColor("#242424",0.5));
                    g.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                    g.setPaint(new FlatColor("#808080",0.8));
                    g.drawRoundRect(0,0,getWidth(),getHeight(),10,10);
                }
            };
            //vbox.setPadding(new Insets(5,5,5,5));
            vbox.setWidth(150);
            vbox.setHeight(300);
            final Slider emissionRate = new Slider(false);
            emissionRate.setThumbFill(new FlatColor("#f0f0f0"));
            emissionRate.setTrackFill(new FlatColor("#606060"));
            emissionRate.setMin(1);
            emissionRate.setValue(5);
            emissionRate.setMax(20);

            final Slider angle = new Slider(false);
            angle.setThumbFill(new FlatColor("#f0f0f0"));
            angle.setTrackFill(new FlatColor("#606060"));
            angle.setMin(0);
            angle.setMax(360);
            angle.setValue(180);

            final Slider jitter = new Slider(false);
            jitter.setThumbFill(new FlatColor("#f0f0f0"));
            jitter.setTrackFill(new FlatColor("#606060"));
            jitter.setMin(0);
            jitter.setMax(180);
            jitter.setValue(20);

            final Slider maxAge = new Slider(false);
            maxAge.setThumbFill(new FlatColor("#f0f0f0"));
            maxAge.setTrackFill(new FlatColor("#606060"));
            maxAge.setMin(10);
            maxAge.setMax(400);
            maxAge.setValue(100);

            EventBus.getSystem().addListener(ChangedEvent.DoubleChanged, new Callback<ChangedEvent>() {
                public void call(ChangedEvent event) {
                    if(event.getType() == ChangedEvent.DoubleChanged) {
                        if(event.getSource() == emissionRate) {
                            sim.setEmissionRate((Double)event.getValue());
                        }
                        if(event.getSource() == angle) {
                            sim.setAngle((Double)event.getValue());
                        }
                        if(event.getSource() == jitter) {
                            sim.setJitter((Double)event.getValue());
                        }
                        if(event.getSource() == maxAge) {
                            sim.setMaxAge((Double)event.getValue());
                        }

                    }
                }
            });

            PopupMenuButton blendPopup = new PopupMenuButton();
            
            vbox.add(
                    new Label("Emission Rate")/*.setFill(FlatColor.WHITE)*/,
                    emissionRate,
                    new Label("Angle")/*.setFill(FlatColor.WHITE)*/,
                    angle,
                    new Label("Jitter")/*.setFill(FlatColor.WHITE)*/,
                    jitter,
                    new Label("Max Age")/*.setFill(FlatColor.WHITE)*/,
                    maxAge,
                    blendPopup
            );
            vbox.setFill(new FlatColor(1.0,1.0,1.0,0.5));
            vbox.setTranslateX(10);
            vbox.setTranslateY(10);

            panel.add(vbox);

            stage.setContent(panel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class ParticleSimView extends Control {
        private List<Particle> particles;
        private int limit = 2000;
        private int emissionRate = 10;
        private double angle = 0;
        private double jitter = 0;
        private double maxAge = 100;

        private ParticleSimView() {
            particles = new ArrayList<Particle>();
            setWidth(300);
            setHeight(300);
        }

        @Override
        public void doLayout() {
        }

        @Override
        public void doPrefLayout() {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void doSkins() {
        }

        @Override
        public void draw(GFX g) {
            g.setPaint(FlatColor.BLACK);
            g.fillRect(0, 0, getWidth() - 20, getHeight() - 30);
            if (particles.size() < limit) {
                for (int i = 0; i < emissionRate; i++) {
                    Particle p = new Particle();
                    p.x = getWidth() / 2;
                    p.y = getHeight() / 2;
                    p.z = 0;
                    double angleJitter = (Math.random()-0.5)*jitter*2;
                    p.vx = Math.sin(Math.toRadians(angle+angleJitter))*3.0;
                    p.vy = Math.cos(Math.toRadians(angle+angleJitter))*3.0;
                    particles.add(p);
                }
            }


            if (g.isGL()) {
                GLAutoDrawable drawable = g.getDrawable();
                GL2 gl = drawable.getGL().getGL2();
                //don't care about depth
                gl.glDepthMask(false);


                //blending
                gl.glEnable(GL2.GL_BLEND);
                gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA); //regular translucency
                //gl.glBlendFunc(GL2.GL_SRC_ALPHA,GL2.GL_SRC_ALPHA); //regular translucency

                //gl.glBlendFunc(GL2.GL_SRC_ALPHA,GL2.GL_ONE); //
                gl.glBlendFunc(GL2.GL_DST_ALPHA, GL2.GL_ONE_MINUS_SRC_COLOR); //

                //gl.glBlendFunc(GL2.GL_SRC_ALPHA_SATURATE, GL2.GL_ONE);

                //draw the particles
                for (Particle p : particles) {
                    p.x += p.vx;
                    p.y += p.vy;

                    drawParticle(gl, p);
                    /*
                    if (p.x < 0 || p.x > getWidth()) {
                        p.x = getWidth() / 2;
                    }
                    if (p.y < 0 || p.y > getHeight()) {
                        p.y = getHeight() / 2;
                    }
                      */
                    p.age++;
                }
                gl.glDisable(GL2.GL_BLEND);
                Iterator<Particle> it = particles.iterator();
                while (it.hasNext()) {
                    Particle p = it.next();
                    if (p.age > maxAge) {
                        it.remove();
                    }
                }
            }
        }

        private void drawParticle(GL2 gl, Particle p) {

            gl.glPushMatrix();
            /*gl.glColor4d(
                    Math.abs(p.vx)/3.0,
                    Math.abs(p.vy)/3.0,
                    1.0,0.5);*/
            gl.glColor4d(0.8, 0.1, 0.1, 1.0 - p.age / maxAge);
            gl.glTranslated(p.x, p.y, p.z);
            double size = 10.0;

            gl.glBegin(GL2.GL_QUADS);
            gl.glVertex3d(-size, size, 0);
            gl.glVertex3d(-size, -size, 0);
            gl.glVertex3d(size, -size, 0);
            gl.glVertex3d(size, size, 0);
            gl.glEnd();

            gl.glPopMatrix();
        }

        public void setEmissionRate(double rate) {
            this.emissionRate = (int)rate;
        }

        public void setAngle(double angle) {
            this.angle = angle;
        }

        public void setJitter(double jitter) {
            this.jitter = jitter;
        }

        public void setMaxAge(double maxAge) {
            this.maxAge = maxAge;
        }
    }

    private static class Particle {
        public double x;
        public double y;
        public double vx;
        public double vy;
        public double z;
        public int age = 0;
    }
}
