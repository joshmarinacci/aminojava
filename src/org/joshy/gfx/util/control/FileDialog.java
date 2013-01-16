package org.joshy.gfx.util.control;

import org.joshy.gfx.event.Callback;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Oct 16, 2010
 * Time: 6:46:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileDialog {
    private Callback<FileDialog> cancelCallback;
    private Callback<FileDialog> succeedCallback;
    private String title = "Untitled";

    public FileDialog() {
    }

    public void onCanceled(Callback<FileDialog> callback) {
        this.cancelCallback = callback;
    }

    public void onSucceeded(Callback<FileDialog> callback) {
        this.succeedCallback = callback;
    }

    public void showOpenDialog() {
        java.awt.FileDialog fd = new java.awt.FileDialog((Frame) null);
        fd.setMode(java.awt.FileDialog.LOAD);
        show(fd);
    }

    public void showSaveDialog() {
        java.awt.FileDialog fd = new java.awt.FileDialog((Frame) null);
        fd.setMode(java.awt.FileDialog.SAVE);
        show(fd);
    }

    private void show(java.awt.FileDialog fd) {
        fd.setTitle(getTitle());
        fd.setVisible(true);
        if(fd.getFile() != null) {
            if(succeedCallback != null) {
                try {
                    succeedCallback.call(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            if(cancelCallback != null) {
                try {
                    cancelCallback.call(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getTitle() {
        return title;
    }
    
}
