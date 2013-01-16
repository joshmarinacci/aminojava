package org.joshy.gfx.event;

import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Dec 6, 2010
 * Time: 9:49:04 AM
 * To change this template use File | Settings | File Templates.
 */

public class FileOpenEvent extends Event {
    private List<File> files;
    public static final EventType FileOpen = new EventType("FileOpen");

    public FileOpenEvent(List<File> files) {
        super(FileOpen);
        this.files = files;
    }

    public List<File> getFiles() {
        return files;
    }
}
