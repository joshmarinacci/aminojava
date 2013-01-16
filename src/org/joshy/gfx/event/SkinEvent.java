package org.joshy.gfx.event;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Nov 29, 2010
 * Time: 9:53:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class SkinEvent extends Event {
    public static final EventType SystemWideReload = new EventType("SystemWideReload");
    public SkinEvent(EventType type) {
        super(type);
    }
}
