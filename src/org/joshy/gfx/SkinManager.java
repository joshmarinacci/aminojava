package org.joshy.gfx;

import org.joshy.gfx.css.CSSSkin;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 18, 2010
 * Time: 1:58:25 PM
 * To change this template use File | Settings | File Templates.
 */

public class SkinManager {
    private static SkinManager _shared;
    private CSSSkin cssSkin;

    private SkinManager() {
    }

    public static SkinManager getShared() {
        if(_shared == null) {
            _shared = new SkinManager();
        }
        return _shared;
    }

    public void setCSSSkin(CSSSkin cssSkin) {
        this.cssSkin = cssSkin;
    }

    public CSSSkin getCSSSkin() {
        return cssSkin;
    }
}
