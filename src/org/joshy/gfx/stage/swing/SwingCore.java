package org.joshy.gfx.stage.swing;

import com.apple.eawt.*;
import com.apple.eawt.Application;
import org.joshy.gfx.Core;
import org.joshy.gfx.SkinManager;
import org.joshy.gfx.css.CSSProcessor;
import org.joshy.gfx.css.CSSRuleSet;
import org.joshy.gfx.css.CSSSkin;
import org.joshy.gfx.event.*;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.OSUtil;
import org.joshy.gfx.util.u;
import org.parboiled.support.ParsingResult;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 23, 2010
 * Time: 3:23:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class SwingCore extends Core {
    private List<Stage> stages = new ArrayList<Stage>();
    private ParsingResult<?> baseResult;
    private long lastScan;
    private URI baseResultURI;
    private Application app;

    public SwingCore() {
        super();
    }

    private void initOSHooks() {
        if(OSUtil.isMac()) {
            app = Application.getApplication();
            app.setOpenFileHandler(new OpenFilesHandler(){
                @Override
                public void openFiles(AppEvent.OpenFilesEvent openFilesEvent) {
//                    u.p("files were opened: " + openFilesEvent);
//                    u.p("search term = " + openFilesEvent.getSearchTerm());
//                    u.p("files = ");
//                    for(File f : openFilesEvent.getFiles()) {
//                        u.p("file = " + f.getAbsolutePath());
//                    }
                    final FileOpenEvent evt = new FileOpenEvent(openFilesEvent.getFiles());
                    EventBus.getSystem().publishDeferred(evt);
                }
            });
            app.setAboutHandler(new AboutHandler(){
                @Override
                public void handleAbout(AppEvent.AboutEvent aboutEvent) {
//                    u.p("got about");
                    EventBus.getSystem().publish(new SystemMenuEvent(SystemMenuEvent.About));
                }
            });
            app.setQuitHandler(new QuitHandler(){
                @Override
                public void handleQuitRequestWith(AppEvent.QuitEvent quitEvent, QuitResponse quitResponse) {
//                    u.p("got quit");
                    EventBus.getSystem().publish(new SystemMenuEvent(SystemMenuEvent.Quit));
                }
            });
            app.setPreferencesHandler(new PreferencesHandler(){
                @Override
                public void handlePreferences(AppEvent.PreferencesEvent preferencesEvent) {
//                    u.p("got prefs");
                    EventBus.getSystem().publish(new SystemMenuEvent(SystemMenuEvent.Preferences));
                }
            });
        }
    }

    @Override
    public Iterable<Stage> getStages() {
        return stages;
    }

    @Override
    protected void initSkinning() throws Exception {
        URL url = SwingCore.class.getResource("default.css");
        baseResult = CSSProcessor.parseCSS(url.openStream());
        baseResultURI = url.toURI();
        CSSRuleSet baseSet = new CSSRuleSet();
        CSSProcessor.condense(baseResult.parseTreeRoot, baseSet, url.toURI());
        CSSSkin cssskin = new CSSSkin();
        cssskin.setRuleSet(baseSet);
        SkinManager.getShared().setCSSSkin(cssskin);
    }

    @Override
    protected void createDefaultEventBus() {
        EventBus.setSystem(new SwingEventBus());
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    //u.p("setting the thread " + Thread.currentThread());
                    _gui_thread = Thread.currentThread();
                }
            } );
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            initOSHooks();
        } catch (Throwable thr) {
            thr.printStackTrace();
        }
    }

    @Override
    public void reloadSkins() {
        for(Stage s : getStages()) {
            Node root = s.getContent();
            if(root instanceof Control) {
                ((Control)root).doSkins();
            }
        }
        EventBus.getSystem().publish(new SkinEvent(SkinEvent.SystemWideReload));
    }

    @Override
    protected void addDebugCSS(final File file) {
        u.p("scanning debug css from: " + file.getAbsolutePath());
        new PeriodicTask(100).call(new Callback() {
            public void call(Object event) {
                checkDebugCSSFile(file);
            }
        }).start();
    }

    @Override
    public void loadCSS(File file) throws IOException {
        //create a new set
        CSSRuleSet set = new CSSRuleSet();
        //add in the old rules
        CSSProcessor.condense(baseResult.parseTreeRoot,set, baseResultURI);
        //u.p("rule count = " + set.rulesCount());

        //parse the new file
        ParsingResult<?> result = CSSProcessor.parseCSS(new FileInputStream(file));

        //add in the new rules
        CSSProcessor.condense(result.parseTreeRoot,set, file.toURI());
        //u.p("rule count = " + set.rulesCount());

        CSSSkin skin = SkinManager.getShared().getCSSSkin();
        skin.setRuleSet(set);
        //u.p("parsed. reloading skins");
        reloadSkins();
    }

    @Override
    public void loadCSS(InputStream in, URL uri) throws IOException, URISyntaxException {
        //create a new set
        CSSRuleSet set = new CSSRuleSet();
        //add in the old rules
        CSSProcessor.condense(baseResult.parseTreeRoot,set, baseResultURI);
        //u.p("rule count = " + set.rulesCount());

        //parse the new file
        ParsingResult<?> result = CSSProcessor.parseCSS(in);

        //add in the new rules
        CSSProcessor.condense(result.parseTreeRoot,set, uri.toURI());
        //u.p("rule count = " + set.rulesCount());

        CSSSkin skin = SkinManager.getShared().getCSSSkin();
        skin.setRuleSet(set);
        //u.p("parsed. reloading skins");
        reloadSkins();
    }

    private void checkDebugCSSFile(File file) {
        if(file.exists()) {
            if(file.lastModified() - lastScan > 1000 ) {
                //u.p("it's been more than a second");
                lastScan = new Date().getTime();
            try {
                //create a new set
                CSSRuleSet set = new CSSRuleSet();
                //add in the old rules
                CSSProcessor.condense(baseResult.parseTreeRoot,set, baseResultURI);
                u.p("rule count = " + set.rulesCount());

                //parse the new file
                ParsingResult<?> result = CSSProcessor.parseCSS(new FileInputStream(file));

                //add in the new rules
                CSSProcessor.condense(result.parseTreeRoot,set, file.toURI());
                u.p("rule count = " + set.rulesCount());

                CSSSkin skin = SkinManager.getShared().getCSSSkin();
                skin.setRuleSet(set);
                u.p("parsed. reloading skins");
                reloadSkins();
            } catch (Exception e) {
                e.printStackTrace();
            }
            }
        }
    }

    public void addStage(SwingStage swingStage) {
        this.stages.add(swingStage);
    }

}
