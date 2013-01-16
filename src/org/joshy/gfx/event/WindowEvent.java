package org.joshy.gfx.event;

import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.u;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Jul 16, 2010
 * Time: 7:51:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class WindowEvent extends Event {
    public static EventType Closing = new EventType("WindowClosing");
    private Stage stage;
    private boolean veto;

    public WindowEvent(EventType type, Stage source) {
        super(type, source);
        this.stage = source;
    }

    @Override
    protected void finished() {
        u.p("closing the window as my last thing");
        if(!veto) {
            stage.hide();
        }
    }

    public void veto() {
        veto = true;
    }
}
