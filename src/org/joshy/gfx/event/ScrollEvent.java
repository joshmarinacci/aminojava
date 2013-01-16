package org.joshy.gfx.event;

import org.joshy.gfx.node.Node;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Jun 26, 2010
 * Time: 9:39:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScrollEvent extends Event {
    public static final EventType ScrollVertical = new EventType("ScrollVertical");
    public static final EventType ScrollHorizontal = new EventType("ScrollHorizontal");
    public static final EventType ScrollAll = new EventType("ScrollAll") {
        @Override
        public boolean matches(EventType type) {
            if(type == ScrollVertical) return true;
            if(type == ScrollHorizontal) return true;
            return super.matches(type);
        }
    };
    
    private int amount;

    public ScrollEvent(EventType type, Node node, int amount) {
        super(type);
        this.source = node;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }
}
