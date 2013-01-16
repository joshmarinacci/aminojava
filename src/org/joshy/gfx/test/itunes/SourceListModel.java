package org.joshy.gfx.test.itunes;

import org.joshy.gfx.node.control.ListModel;

/**
* Created by IntelliJ IDEA.
* User: josh
* Date: Jan 28, 2010
* Time: 9:20:26 PM
* To change this template use File | Settings | File Templates.
*/
class SourceListModel implements ListModel {
    private String[] sources;

    SourceListModel() {
        sources = new String[] {"Music","Movies","TV Shows","Podcasts","Audiobooks" };
    }

    public Object get(int i) {
        return sources[i];
    }

    public int size() {
        return sources.length;
    }
}
