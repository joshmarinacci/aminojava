package org.joshy.gfx.animation;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: May 20, 2010
 * Time: 1:45:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class AnimationDriver {
    private Animateable animateable;
    private boolean running;
    protected long startTime;
    private Timer timer;
    private int fps = 30;

    protected long now() {
        return new Date().getTime();
    }

    public AnimationDriver(Animateable animateable) {
        this.animateable = animateable;
    }

    public void start() {
        running = true;
        int fps = 1000/this.fps;
        timer = new Timer(fps, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                long currentTime = now() - startTime;
                animateable.update(currentTime);
                if(animateable.isDone()) {
                    animateable.onStop(currentTime);
                    running = false;
                    timer.stop();
                }
            }
        });
        startTime = now();
        animateable.onStart(startTime);
        timer.start();
        running = true;
    }

    public static void start(Animateable animateable) {
        new AnimationDriver(animateable).start();
    }

    public void setFPS(int fps) {
        this.fps = fps;
    }
}
