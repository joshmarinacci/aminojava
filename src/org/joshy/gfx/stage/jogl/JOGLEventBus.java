package org.joshy.gfx.stage.jogl;

import org.joshy.gfx.event.Event;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.util.u;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 20, 2010
 * Time: 5:28:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class JOGLEventBus extends EventBus {
    public JOGLEventBus() {
        super();
    }

    @Override
    protected void invokeLater(final Event event) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                processEvents(event);
            }
        });
    }

}
