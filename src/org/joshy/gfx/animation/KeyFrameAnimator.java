package org.joshy.gfx.animation;

import org.joshy.gfx.event.Callback;
import org.joshy.gfx.util.u;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: Apr 16, 2010
* Time: 7:02:27 PM
* To change this template use File | Settings | File Templates.
*/
public class KeyFrameAnimator implements Animateable {
    private Object target;
    private String property;
    private List<KeyFrame> keyFrames;
    private long startTime;
    private int currentFrame;
    public static final boolean DEBUG = false;
    private Callback doAfterCallback;
    private Callback doBeforeCallback;
    private int repeat = 1;
    private int count = 0;
    private boolean done = false;
    private AnimationDriver driver;

    public KeyFrameAnimator(Object target) {
        this.keyFrames = new ArrayList<KeyFrame>();
        this.target = target;
    }

    public static KeyFrameAnimator target(Object target) {
        return new KeyFrameAnimator(target);
    }

    public static KeyFrameAnimator create(Object r, String s) {
        KeyFrameAnimator kfa = new KeyFrameAnimator(r);
        kfa.property = s;
        return kfa;
    }

    public KeyFrameAnimator property(String property) {
        this.property = property;
        return this;
    }

    public KeyFrameAnimator keyFrame(double duration, double keyValue) {
        this.keyFrames.add(new KeyFrame(duration,keyValue));
        return this;
    }
    
    private long prev;

    public void onStart(long current) {
        startTime = new Date().getTime();
        if(doBeforeCallback != null) {
            try {
                doBeforeCallback.call(target);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void update(long currentTime) {
        if(currentFrame > keyFrames.size()-2) {
            u.p("too far. returning early");
            return;
        }
        if(DEBUG)u.p("updating. current frame = " + currentFrame);
        KeyFrame thisFrame = keyFrames.get(currentFrame);
        KeyFrame nextFrame = keyFrames.get(currentFrame+1);

        long current = new Date().getTime() - startTime;
        double fraction = (double)current/(double)((nextFrame.time-thisFrame.time) *1000.0);
        if(fraction > 1) fraction = 1.0;
        if(DEBUG) u.p("current = " + current + " dur = " + nextFrame.time);
        //the interpolation function
        double value = (nextFrame.keyValue-thisFrame.keyValue) * fraction + thisFrame.keyValue;
        if(DEBUG) u.p("fract = " + fraction + " value = " + value);

        long diff = new Date().getTime()-prev;
        prev = new Date().getTime();

        updateProperty(value);
        if(fraction >= 1.0) {
            currentFrame++;
            startTime = new Date().getTime();
        }
        if(currentFrame > keyFrames.size()-2) {
            count++;
            if(repeat == INFINITE || count < repeat) {
                currentFrame = 0;
                startTime = new Date().getTime();
                done = false;
            } else {
                done = true;
            }
        }

    }

    private void updateProperty(double value) {
        Class[] args = new Class[1];
        args[0] = double.class;
        String methodName = "set" + property.substring(0,1).toUpperCase() + property.substring(1);
        try {
            Method method = target.getClass().getMethod(methodName, args);
            method.invoke(target,value);
        } catch (Exception e) {
            u.p("error accessing property: " + property);
            e.printStackTrace();
        }
    }

    public void onStop(long currentTime) {
        if(doAfterCallback != null) {
            try {
                doAfterCallback.call(target);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void loop() {
        currentFrame = 0;
        startTime = new Date().getTime();
    }

    public boolean isDone() {
        return done;
    }

    public KeyFrameAnimator doBefore(Callback callback) {
        doBeforeCallback = callback;
        return this;
    }
    public KeyFrameAnimator doAfter(Callback callback) {
        doAfterCallback = callback;
        return this;
    }

    public void start() {
        driver = new AnimationDriver(this);
        driver.start();
    }
    public void stop() {
        this.done = true;
    }

    public KeyFrameAnimator repeat(int repeat) {
        this.repeat = repeat;
        return this;
    }
}
