package org.joshy.gfx.animation;

import org.joshy.gfx.util.u;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class CompoundAnimation implements Animateable {
    
    protected List<Animateable> animators;
    private int repeatCount = 1;
    private int realCount = 0;

    protected long now() {
        return new Date().getTime();
    }

    CompoundAnimation() {
        this.animators = new ArrayList<Animateable>();
    }

    public CompoundAnimation add(Animateable ... animators) {
        for(Animateable a : animators) {
            this.animators.add(a);
        }
        return this;
    }

    public void loop() {
        for(Animateable a : animators) {
            a.loop();
        }
    }


    public void stop() {
    }

    protected void doStop() {
        realCount++;
        if(realCount < repeatCount || repeatCount == INFINITE) {
            loop();
        } else {
        }
    }


    public boolean isRunning() {
        return true;
    }

    public CompoundAnimation setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
        return this;
    }
}
