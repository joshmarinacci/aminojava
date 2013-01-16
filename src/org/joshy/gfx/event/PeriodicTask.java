package org.joshy.gfx.event;

import org.joshy.gfx.Core;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: Jul 31, 2010
* Time: 2:06:40 PM
* To change this template use File | Settings | File Templates.
*/
public class PeriodicTask {
    Thread thread;
    private Callback callback;

    public PeriodicTask(final long time) {
        thread = new Thread(new Runnable(){
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    doCallback();
                }
            }
        });
    }

    private void doCallback() {
        Core.getShared().defer(new Runnable(){
            public void run() {
                try {
                    callback.call(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public PeriodicTask call(Callback callback) {
        this.callback = callback;
        return this;
    }

    public PeriodicTask start() {
        thread.start();
        return this;
    }
}
