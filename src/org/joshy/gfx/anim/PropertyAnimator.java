package org.joshy.gfx.anim;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.util.Date;


public class PropertyAnimator {
    private Object target;
    private String property;
    private double start;
    private double end;
    private long duration;
    private long current;
    private Timer timer;
    private long startTime;
    private long prev;
    private boolean running;
    public static final int INDEFINITE = -1;
    private int loopCount = 1;
    private boolean autoReverse = false;
    private boolean forward = true;


    private PropertyAnimator(Object b) {
        this.target = b;
    }

    public void start() {
        int fps = 1000/60;
        startTime = new Date().getTime();
        timer = new Timer(fps, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                update();
            }
        });
        timer.start();
        running = true;
    }

    public void stop() {
        running = false;
        if(timer != null) {
            timer.stop();
        }
    }


    int looped = 0;
    public boolean isRunning() {
        return running;
    }
    private void update() {
        current = new Date().getTime() - startTime;
        double fraction = (double)current/(double)duration;
        if(!forward) {
            fraction = 1.0-fraction;
        }
        double value = (end-start) * fraction + start;
//        u.p("fract = " + fraction + " value = " + value);

        long diff = new Date().getTime()-prev;
        prev = new Date().getTime();
//        u.p("diff = " + diff);

        updateProperty(value);
        if(forward && fraction >= 1.0 || (!forward && fraction <= 0)) {
            looped++;
            startTime = new Date().getTime();
            if(autoReverse) forward = !forward;
            if(looped == loopCount) {
                timer.stop();
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
            e.printStackTrace();
        }
    }

    public static PropertyAnimator target(Object b) {
        return new PropertyAnimator(b);
    }

    public PropertyAnimator property(String s) {
        this.property = s;
        return this;
    }

    public PropertyAnimator startValue(double value) {
        this.start = value;
        return this;
    }

    public PropertyAnimator endValue(double value) {
        this.end = value;
        return this;
    }

    public PropertyAnimator seconds(int seconds) {
        this.duration = seconds*1000;
        return this;
    }

    public PropertyAnimator milliseconds(int milliseconds) {
        this.duration = milliseconds;
        return this;
    }

    public PropertyAnimator repeat(int loopCount) {
        this.loopCount = loopCount;
        return this;
    }

    public PropertyAnimator autoReverse(boolean autoReverse) {
        this.autoReverse = autoReverse;
        return this;
    }
}