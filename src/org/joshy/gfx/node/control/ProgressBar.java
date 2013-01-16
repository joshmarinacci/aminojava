package org.joshy.gfx.node.control;

import org.joshy.gfx.SkinManager;
import org.joshy.gfx.css.BoxPainter;
import org.joshy.gfx.css.CSSSkin;
import org.joshy.gfx.css.SizeInfo;
import org.joshy.gfx.css.StyleInfo;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.BackgroundTask;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.ProgressUpdate;

/**
 *  The ProgressBar is a control which shows the progress of some process, usually a background
 * task. It shows the progress as a bar of increasing width.
 * It was designed with background tasks in mind and can be easily attached to a background
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
public class ProgressBar extends Control {
    double percentage = 0.0;
    private StyleInfo styleInfo;
    private StyleInfo barStyleInfo;
    private SizeInfo sizeInfo;
    private SizeInfo barSizeInfo;
    private BoxPainter boxPainter;
    private BoxPainter barPainter;

    public ProgressBar() {
    }

    @Override
    public void doSkins() {
        cssSkin = SkinManager.getShared().getCSSSkin();
        styleInfo = cssSkin.getStyleInfo(this, null);
        barStyleInfo = cssSkin.getStyleInfo(this,null,"bar");
        setLayoutDirty();
    }

    @Override
    public void doPrefLayout() {
        sizeInfo = cssSkin.getSizeInfo(this,styleInfo,"");
        if(prefWidth != CALCULATED) {
            setWidth(prefWidth);
            sizeInfo.width = prefWidth;
        } else {
            setWidth(sizeInfo.width);
        }
        setHeight(sizeInfo.height);
        barSizeInfo = cssSkin.getSizeInfo(this,barStyleInfo,"");
    }

    @Override
    public void doLayout() {
        boxPainter = cssSkin.createBoxPainter(this, styleInfo, sizeInfo, "", CSSSkin.State.None);
        barPainter = cssSkin.createBoxPainter(this, barStyleInfo, barSizeInfo, "", CSSSkin.State.None, "bar");
    }
    

    @Override
    public void draw(GFX g) {
        if(!isVisible()) return;
        boxPainter.draw(g,styleInfo,sizeInfo,"");
        barSizeInfo.width = getWidth()*percentage;
        barPainter.draw(g,barStyleInfo,barSizeInfo,"");
    }



    public void setTask(BackgroundTask task) {
        EventBus.getSystem().addListener(task, ProgressUpdate.TYPE, new Callback<ProgressUpdate>() {
            public void call(ProgressUpdate event) {
                percentage = event.getPercentage();
                setDrawingDirty();
            }
        });
        setDrawingDirty();
    }

}
