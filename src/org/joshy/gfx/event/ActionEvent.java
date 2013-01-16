package org.joshy.gfx.event;

import org.joshy.gfx.node.Node;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 26, 2010
 * Time: 4:20:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActionEvent extends Event {
    public static EventType Action = new EventType("Action");
    public ActionEvent(EventType type, Object source) {
        super(type);
        this.source = source;
    }
}
