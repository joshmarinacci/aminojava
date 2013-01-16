package org.joshy.gfx.event;

import org.joshy.gfx.node.control.SelectableControl;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: Jul 1, 2010
* Time: 4:19:42 PM
* To change this template use File | Settings | File Templates.
*/
public class SelectionEvent extends Event {
    public static final EventType Changed = new EventType("SelectionEventChanged");
    private SelectableControl view;
    public SelectionEvent(EventType type, SelectableControl view) {
        super(type);
        this.source = view;
        this.view = view;
    }

    public SelectableControl getView() {
        return view;
    }
}
