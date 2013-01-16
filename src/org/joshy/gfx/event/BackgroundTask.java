package org.joshy.gfx.event;

import org.joshy.gfx.Core;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 23, 2010
 * Time: 6:55:26 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class BackgroundTask<D,R> {
    private D data;

    protected BackgroundTask() {
    }

    public void setData(D data) {
        this.data = data;

    }
    protected void onStart(D data) {
//        u.p("starting. thread = " + Thread.currentThread() + " data = " + data);
    }

    abstract protected R onWork(D data);
    
    protected void updateGUI(D data, R result, double percentage) {
        ProgressUpdate<D,R> e = new ProgressUpdate<D,R>(this,data,result,percentage);
        EventBus.getSystem().publish(e);
    }

    protected void onEnd(R result) {
//        u.p("ending. thread = " + Thread.currentThread() + " result = " + result);
    }

    public final void start() throws InterruptedException {
        onStart(data);
        ExecutorService pool = Executors.newFixedThreadPool(10);
        Future<R> result = pool.submit(new Callable<R>() {
            public R call() throws Exception {
                try {
                    final R results = onWork(data);
                    Core.getShared().defer(new Runnable() {
                        public void run() {
                            onEnd(results);
                        }
                    });
                    return results;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    throw ex;
                }
            }
        });

    }
}
