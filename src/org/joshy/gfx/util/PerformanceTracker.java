package org.joshy.gfx.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Feb 4, 2010
 * Time: 3:24:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class PerformanceTracker {
    private static PerformanceTracker _tracker;
    private boolean enabled = true;
    private long layoutStart;
    private long layoutEnd;

    private List<Long> layoutTimes;
    private long drawStart;
    private long drawEnd;
    private List<Long> drawTimes;
    private long skinStart;
    private long skinEnd;
    private List<Long> skinTimes;

    public PerformanceTracker() {
        layoutTimes = new ArrayList<Long>();
        drawTimes = new ArrayList<Long>();
        skinTimes = new ArrayList<Long>();
    }

    public static PerformanceTracker getInstance() {
        if(_tracker == null) {
            _tracker = new PerformanceTracker();
        }
        return _tracker;
    }

    public void enableTracking() {
        enabled = true;
    }


    public void layoutStart() {
        layoutStart = new Date().getTime();
    }
    public void layoutEnd() {
        layoutEnd = new Date().getTime();
        layoutTimes.add(layoutEnd-layoutStart);
        printStats();
    }


    public void skinStart() {
        skinStart = time();
    }

    public void skinEnd() {
        skinEnd = time();
        skinTimes.add(skinEnd-skinStart);
    }

    public void drawStart() {
        drawStart = time();
    }

    public void drawEnd() {
        drawEnd = time();
        drawTimes.add(drawEnd-drawStart);
        printStats();
    }

    private long time() {
        return new Date().getTime();
    }

    private void printStats() {
        if(!enabled)return;
        printTimes(skinTimes, "skin");
        printTimes(layoutTimes, "layout");
        printTimes(drawTimes, "draw");
    }

    private void printTimes(List<Long> times, String name) {
        if(times.size() > 10) {
            long total = 0;
            for(Long l : times) {
                total += l;
            }
            //u.p("avg "+name+" time = " + (total/times.size()));
            times.clear();
        }
    }
}
