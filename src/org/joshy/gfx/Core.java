package org.joshy.gfx;

import org.joshy.gfx.event.FocusManager;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.stage.jogl.JOGLCore;
import org.joshy.gfx.stage.swing.SwingCore;
import org.joshy.gfx.util.u;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import org.joshy.gfx.event.FocusManager;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 18, 2010
 * Time: 8:52:24 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Core {
    private static Core _runtime;
    private static boolean jogl = false;
    protected Thread _gui_thread;
    private FocusManager _focusManager;
    private static boolean testing;
    private static boolean isInitting;

    public static synchronized Core getShared() {
        if(_runtime == null) {
            throw new RuntimeException("Core not inited yet. Call Core.init() first");
        }
        return _runtime;
    }
    
    protected Core() {
        
    }

    public abstract Iterable<Stage> getStages();

    public static void setUseJOGL(boolean useJOGL) {
        jogl = useJOGL;
    }

    public static boolean isUseJOGL() {
        return jogl;
    }

    public static void init() throws Exception {
        isInitting = true;
        if(isUseJOGL()) {
            _runtime = new JOGLCore();
        } else {
            _runtime = new SwingCore();
        }
        _runtime.initSkinning();
        _runtime.createDefaultEventBus();
        isInitting = false;
    }

    protected abstract void initSkinning() throws Exception;

    protected abstract void createDefaultEventBus();

    public void assertGUIThread() {
//        u.p("checking for the gui thread");
//        u.p("current thread = " + Thread.currentThread());
//        u.p("gui thread = " + _gui_thread);
        if(testing) return;
        
        //if(Thread.currentThread() != _gui_thread) {
        if(!Thread.currentThread().getName().equals(_gui_thread.getName())) {
            u.p("id = " + _gui_thread.getId());
            u.p("name = " + _gui_thread.getName());
            throw new RuntimeException("Not on the GUI thread! gthread = " + _gui_thread + " curren thread = " + Thread.currentThread());
        }
    }

    public void defer(Runnable runnable) {
        SwingUtilities.invokeLater(runnable);
    }

    public FocusManager getFocusManager() {
        if(_focusManager == null) {
            _focusManager = new FocusManager();
        }
        return _focusManager;
    }

    /**
     * Setting testing to true turns off thread checks. This is mainly useful
     * for running automated unit tests on the current thread.
     * @param testingt
     */
    public static void setTesting(boolean testingt) {
        testing = testingt;
    }

    public abstract void reloadSkins();

    public static void setDebugCSS(File file) {
        Core.getShared().addDebugCSS(file);
    }
    
    public static void requestDebugCSS() {
        FileDialog fd = new FileDialog((Frame)null);
        fd.setTitle("Select Debugging CSS File");
        fd.setMode(FileDialog.LOAD);
        fd.setVisible(true);
        if(fd.getFile() != null) {
            Core.getShared().addDebugCSS(new File(fd.getDirectory(),fd.getFile()));
        }
    }

    protected abstract void addDebugCSS(File file);

    public abstract void loadCSS(File file) throws IOException;
    public abstract void loadCSS(InputStream in, URL uri) throws IOException, URISyntaxException;
}
