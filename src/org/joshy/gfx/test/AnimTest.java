package org.joshy.gfx.test;

import org.joshy.gfx.Core;
import org.joshy.gfx.SkinManager;
import org.joshy.gfx.anim.PropertyAnimator;
import org.joshy.gfx.animation.*;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.stage.Stage;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 21, 2010
 * Time: 10:25:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class AnimTest implements Runnable {
    
    public  static void main(String ... args) throws Exception {
        Core.setUseJOGL(false);
        Core.init();
        Core.getShared().defer(new AnimTest());
    }

    public void run() {
        try {
            Stage stage = Stage.createStage();

            final Button b = new Button();
            stage.setContent(b);
            b.setText("A Button");
            b.setWidth(100);
            b.setHeight(100);

            final Panel p = new Panel();
            p.add(b);
            stage.setContent(p);

            /*
            //simple keyframe translate example
            AnimationDriver.start(
                KeyFrameAnimator.create(b,"translateX")
                    .keyFrame(0,0)
                    .keyFrame(3,300)
                );
            */

            /*
            //simple keyframe example with repeat
            AnimationDriver.start(
                KeyFrameAnimator.create(b,"translateX")
                    .keyFrame(0,0)
                    .keyFrame(1,300)
                    .repeat(3)
                );
            */
            

            /*
            //sequential example
            AnimationDriver.start(
                SequentialAnimation.create(
                    KeyFrameAnimator.create(b,"translateY")
                        .keyFrame(0,0)
                        .keyFrame(3,300)
                    ,KeyFrameAnimator.create(b,"translateY")
                        .keyFrame(0,300)
                        .keyFrame(3,0)
                )
            );
            */

            //parallel example
            AnimationDriver.start(
                ParallelAnimation.create(
                        KeyFrameAnimator.create(b,"translateX")
                            .keyFrame(0,0)
                            .keyFrame(3,300)
                        ,KeyFrameAnimator.create(b,"translateY")
                            .keyFrame(0,0)
                            .keyFrame(3,300)
                )
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
