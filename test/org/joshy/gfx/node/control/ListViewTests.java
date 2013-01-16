package org.joshy.gfx.node.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.stage.Stage;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Jul 3, 2010
 * Time: 5:00:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class ListViewTests implements Runnable {
    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().defer(new ListViewTests());
    }

    public void run() {
        ListView<String> lv = new ListView<String>();
        lv.setRowHeight(50);
        lv.setColumnWidth(50);
        List<String> model = new ArrayList<String>();
        for(int i=0; i<100; i++) {
            model.add("item " + i);
        }
        lv.setModel(ListView.createModel(model));
        lv.setOrientation(ListView.Orientation.HorizontalWrap);


        ScrollPane scroll = new ScrollPane();
        scroll.setContent(lv);
        Stage stage = Stage.createStage();
        stage.setContent(scroll);
    }
}
