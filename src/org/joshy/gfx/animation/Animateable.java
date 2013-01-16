package org.joshy.gfx.animation;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Apr 17, 2010
 * Time: 9:11:31 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Animateable {
    public static final int INFINITE = -1;
    public void onStart(long current);
    public void update(long currentTime);
    public void onStop(long currentTime);
    public void loop();
    public boolean isDone();
}
