package org.joshy.gfx.node.control;

import org.joshy.gfx.anim.PropertyAnimator;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.BackgroundTask;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.ProgressUpdate;
import org.joshy.gfx.util.u;

/**
 * The ProgressSpinner is a control which shows the progress of some process, usually a background
 * task. It shows the progress by constantly spinning, or by showing a percentage
 * complete as a piechart.
 *
 * cdIt was designed with background tasks in mind and can be easily attached to a background
 * task. For example, the following code will attach a progress bar to a background task.
 *
 * <pre><code>
 BackgroundTask task = new BackgroundTask<String, String>() {
     protected String onWork(String data) {
        ... do some work in the background
        ... call update gui to show progress from 0.0 to 1.0
         updateGUI(data,result,i/100.0);
        ... return result when done
         return result;
     }
 };

 //create progress bar and attach to the task
 ProgressBar pb = new ProgressBar();
 pb.setTask(task);

 //start the task
 task.start();
 *
 * </code></pre>
 */

public class ProgressSpinner extends Control {
    double percentage = 0.0;
    private PropertyAnimator indeterminateAnim;
    private double animAngle = 0;

    public ProgressSpinner() {
        doLayout();
        indeterminateAnim = PropertyAnimator.target(this).
                property("animAngle").
                startValue(0).
                endValue(360).
                milliseconds(1000).
                repeat(PropertyAnimator.INDEFINITE);
    }

    @Override
    public void draw(GFX g) {
        g.setPaint(FlatColor.BLACK);
        double cx = getWidth()/2;
        double cy = getHeight()/2;
        double radius = getWidth()/2;
        g.fillCircle(cx,cy,radius);
        g.setPaint(FlatColor.BLUE);
        if(percentage >=0) {
            double angle = 360.0*percentage;
            g.fillArc(cx,cy,radius,0,angle);
        } else {
            g.fillArc(cx,cy,radius,0-animAngle,60-animAngle);
            g.fillArc(cx,cy,radius,120-animAngle,180-animAngle);
            g.fillArc(cx,cy,radius,240-animAngle,300-animAngle);
        }
    }

    @Override
    public void doPrefLayout() {
        //noop
    }

    @Override
    public void doLayout() {
        setWidth(50);
        setHeight(50);
    }

    @Override
    public void doSkins() {
        u.p("prog spinner doesn't use skins yet");
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
        if(this.percentage < 0 && !indeterminateAnim.isRunning()) {
            indeterminateAnim.start();
        } else {
            if(indeterminateAnim.isRunning()) {
                indeterminateAnim.stop();
            }
        }
    }

    public void setTask(BackgroundTask task) {
        EventBus.getSystem().addListener(task, ProgressUpdate.TYPE, new Callback<ProgressUpdate>() {
            public void call(ProgressUpdate event) {
                setPercentage(event.getPercentage());
                setDrawingDirty();
            }
        });
        setDrawingDirty();
    }

    public void setAnimAngle(double animAngle) {
        this.animAngle = animAngle;
        setDrawingDirty();
    }
}
