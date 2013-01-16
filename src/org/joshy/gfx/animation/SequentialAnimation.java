package org.joshy.gfx.animation;

import org.joshy.gfx.util.u;

import java.util.Date;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: Apr 16, 2010
* Time: 7:05:14 PM
* To change this template use File | Settings | File Templates.
*/
public class SequentialAnimation extends CompoundAnimation {
    private int currentAnimatorIndex = 0;
    private boolean done = false;

    public void onStart(long startTime) {
        currentAnimatorIndex = 0;
        //TODO why do we have this startime calc? why don't we just pass starttime in directly?
        animators.get(currentAnimatorIndex).onStart(new Date().getTime()-startTime);
    }

    public void update(long currentTime) {
        Animateable currentAnimator = animators.get(currentAnimatorIndex);
        currentAnimator.update(currentTime);
        if(currentAnimator.isDone()) {
            currentAnimatorIndex++;
            if(currentAnimatorIndex >= animators.size()) {
                done = true;
                return;
            } else {
                animators.get(currentAnimatorIndex).onStart(currentTime);
            }
        }
    }

    protected void onEnd() {
        
    }

    public void onStop(long currentTime) {
        
    }

    public boolean isDone() {
        return done;
    }

    public static SequentialAnimation create(Animateable ... animateable) {
        SequentialAnimation a = new SequentialAnimation();
        a.add(animateable);
        return a;
    }
}
