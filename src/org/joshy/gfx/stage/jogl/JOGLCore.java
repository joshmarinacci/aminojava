package org.joshy.gfx.stage.jogl;

import org.joshy.gfx.Core;
import org.joshy.gfx.SkinManager;
import org.joshy.gfx.css.CSSProcessor;
import org.joshy.gfx.css.CSSRuleSet;
import org.joshy.gfx.css.CSSSkin;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.stage.swing.SwingCore;
import org.parboiled.support.ParsingResult;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

public class JOGLCore extends Core {
    public JOGLCore() {
        super();
    }

    @Override
    public Iterable<Stage> getStages() {
        return null;
    }


    @Override
    protected void initSkinning() throws Exception {
        URL url = SwingCore.class.getResource("default.css");
        ParsingResult<?> result = CSSProcessor.parseCSS(url.openStream());
        CSSRuleSet set = new CSSRuleSet();
        CSSSkin cssskin = new CSSSkin();
        cssskin.setRuleSet(set);
        CSSProcessor.condense(result.parseTreeRoot,set, url.toURI());
        SkinManager.getShared().setCSSSkin(cssskin);
    }
    
    @Override
    protected void createDefaultEventBus() {
        EventBus.setSystem(new JOGLEventBus());
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    _gui_thread = Thread.currentThread();
                }
            } );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reloadSkins() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void addDebugCSS(File file) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void loadCSS(File file) throws IOException {
        
    }

    @Override
    public void loadCSS(InputStream in, URL uri) throws IOException, URISyntaxException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
