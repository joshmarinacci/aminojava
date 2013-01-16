package org.joshy.gfx.test.threedee;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.*;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Group;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.stage.PerspectiveCamera;
import org.joshy.gfx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: 12/15/10
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class Xmas implements Runnable {
    private Group parts;
    private static Image[] img;

    public static void main(String ... args) throws Exception, InterruptedException {
        Core.setUseJOGL(true);
        Core.init();
        Core.getShared().defer(new Xmas());
    }

    @Override
    public void run() {
        try {
            img = new Image[9];
            for(int i=0; i<img.length; i++) {

                BufferedImage bi = ImageIO.read(Xmas.class.getResourceAsStream("resources/snowflake" + i + ".png"));
                fillColor(FlatColor.WHITE,bi);
                img[i] = Image.create(bi);
            }
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        parts = new Group() {
            @Override
            public void draw(GFX g) {
                updateParticles();
                super.draw(g);
            }
        };
        Stage s = Stage.create3DStage();
        s.setCamera(new PerspectiveCamera()
                .setBackground(FlatColor.RED)
        );
        s.setContent(parts);
        s.setWidth(1024);
        s.setHeight(768);
    }

    private void fillColor(FlatColor white, BufferedImage bi) {
        for(int x = 0; x < bi.getWidth(); x++) {
            for(int y = 0; y< bi.getHeight(); y++) {
                int argb = bi.getRGB(x, y);
                argb = argb | 0x00FFFFFF;  //set all color to white, but leave the alpha alone
                bi.setRGB(x,y,argb);
            }
        }
    }

    int maxFlakes = 30;
    int particlesPerTick = 1;

    double minX = -300;
    double maxX = 300;

    double startY = 500;
    double endY = -500;

    double minZ = -200;
    double maxZ = -100;

    double minYv = -0.5;
    double maxYv = -1.5;
    double minRotV = -1.5;
    double maxRotV = 1.5;

    double minTilt = -30;
    double maxTilt = 30;

    private void updateParticles() {
        for(int n = 0; n<particlesPerTick; n++) {
            if(parts.getChildCount() < maxFlakes) {
                Flake p = new Flake();
                p.setTranslateX(rand(minX,maxX));
                p.setTranslateY(startY);
                p.setTranslateZ(rand(minZ, maxZ));
                p.setAxis(Transform.Y_AXIS);
                p.yV = rand(minYv,maxYv);
                p.rotV = rand(minRotV,maxRotV);
                p.img = img[(int)rand(0,8)];
                p.tilt = rand(minTilt,maxTilt);
                parts.add(p);
            }
        }

        List<Flake> dead = new ArrayList<Flake>();
        for(Node node : parts.children()) {
            Flake f = (Flake) node;
            //update values
            f.setTranslateY(f.getTranslateY()+f.yV);
            f.setRotate(f.getRotate()+f.rotV);

            //clear the dead ones
            if(f.getTranslateY() < endY) {
                dead.add(f);
            }
        }
        for(Flake f : dead) {
            parts.remove(f);
        }
    }

    private double rand(double minX, double maxX) {
        return minX + Math.random()*(maxX-minX);
    }

    private static class Flake extends TransformNode {
        double yV = 1.0;
        public double rotV;
        public Image img;
        double tilt = 0;

        private Flake() {
            super();
            setContent(new Node() {

                @Override
                public void draw(GFX g) {
                    g.scale(0.5,0.5);
                    g.rotate(tilt, Transform.Z_AXIS);
                    g.drawImage(img, -img.getWidth() / 2, -img.getHeight() - 2);
                    g.rotate(-tilt, Transform.Z_AXIS);
                    g.scale(2,2);
                }

                @Override
                public Bounds getVisualBounds() {
                    return new Bounds(0,0,0,0);
                }

                @Override
                public Bounds getInputBounds() {
                    return getVisualBounds();
                }
            });
        }
    }
}
