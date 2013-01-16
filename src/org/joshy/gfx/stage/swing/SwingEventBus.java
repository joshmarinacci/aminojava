package org.joshy.gfx.stage.swing;

import org.joshy.gfx.event.Event;
import org.joshy.gfx.event.EventBus;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 20, 2010
 * Time: 11:08:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class SwingEventBus extends EventBus {
    public SwingEventBus() {
        super();
    }

    @Override
    protected void invokeLater(final Event event) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                processQueuedEvents();
                processEvents(event);
            }
        });
    }
}
