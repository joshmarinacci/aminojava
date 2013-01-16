package org.joshy.gfx.event;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 23, 2010
 * Time: 7:41:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class ProgressUpdate<D,R> extends Event {
    private double percentage;
    private R result;
    private D data;
    public static final EventType TYPE = new EventType("ProgressUpdate");
    private BackgroundTask task;

    public ProgressUpdate(BackgroundTask task, D data, R result, double percentage) {
        super(TYPE);
        this.source = task;
        this.task = task;
        this.data = data;
        this.result = result;
        this.percentage = percentage;
    }


    public double getPercentage() {
        return percentage;
    }

    public R getResult() {
        return result;
    }

    public D getData() {
        return data;
    }

    public BackgroundTask getTask() {
        return task;
    }
}
