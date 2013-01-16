package org.joshy.gfx.event;

import org.joshy.gfx.stage.Stage;

import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Dec 6, 2010
 * Time: 9:54:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class SystemMenuEvent extends Event {
    public static final EventType About = new EventType("SystemMenuEvent.About");
    public static final EventType Quit = new EventType("SystemMenuEvent.Quit");
    public static final EventType Preferences = new EventType("SystemMenuEvent.Preferences");
    public static final EventType FileDrop = new EventType("SystemMenuEvent.DropFile");
    public static final EventType All = new EventType("All") {
        @Override
        public boolean matches(EventType type) {
            if(type == About) return true;
            if(type == Quit) return true;
            if(type == Preferences) return true;
            if(type == FileDrop) return true;
            return super.matches(type);
        }

    };
    private List<File> files;

    public SystemMenuEvent(EventType type) {
        super(type);
    }

    public SystemMenuEvent(EventType fileDrop, Stage stage, List<File> files) {
        super(fileDrop, stage);
        this.files = files;
    }

    public List<File> getFiles() {
        return this.files;
    }
}
