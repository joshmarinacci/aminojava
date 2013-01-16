package org.joshy.gfx.animation;

public class ParallelAnimation extends CompoundAnimation {
    private boolean done = false;

    public void onStart(long startTime) {
        for(Animateable a : animators) {
            a.onStart(now()-startTime);
        }
    }

    public void update(long currentTime) {
        boolean allDone = true;
        for(Animateable a : animators) {
            if(!a.isDone()) {
                a.update(currentTime);
                allDone = false;
            }
        }
        this.done = allDone;
    }

    protected void onEnd() {

    }
    
    public void onStop(long currentTime) {
        
    }

    public boolean isDone() {
        return done;
    }

    public static ParallelAnimation create(Animateable ... animateable) {
        ParallelAnimation a = new ParallelAnimation();
        a.add(animateable);
        return a;
    }
}
