package org.joshy.gfx.event;

import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.control.Focusable;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 26, 2010
 * Time: 10:10:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class FocusEvent extends Event {

    public static final EventType Gained = new EventType("FocusGained");
    public static final EventType Lost = new EventType("FocusLost");
    public static final EventType All = new EventType("FocusAll") {
        @Override
        public boolean matches(EventType type) {
            if(type == Gained) return true;
            if(type == Lost) return true;
            return  super.matches(type);
        }
    };

    public FocusEvent(EventType type, Focusable source) {
        super(type);
        this.source = source;
    }
}
