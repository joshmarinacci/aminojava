package org.joshy.gfx.test.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.event.*;
import org.joshy.gfx.node.control.ListModel;
import org.joshy.gfx.node.control.ListView;
import org.joshy.gfx.node.control.PopupMenuButton;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.u;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: 12/29/10
 * Time: 2:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class PopupTest implements Runnable {
    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().defer(new PopupTest());
    }

    @Override
    public void run() {

        final PopupMenuButton<String> button = new PopupMenuButton<String>();
        button.onClicked(new Callback<ActionEvent>() {
            @Override
            public void call(ActionEvent event) throws Exception {
                u.p("called back");
                u.p("selected item = " + button.getSelectedItem());
                u.p("other sel item = " + button.getModel().get(button.getSelectedIndex()));
            }
        });
        ListModel<String> model = ListView.createModel("asdf1", "asdf2", "asdf3", "asdf4");
        button.setModel(model);


        Stage stage = Stage.createStage();
        stage.setWidth(400);
        stage.setHeight(400);
        stage.setContent(button);


        EventBus.getSystem().addListener(SystemMenuEvent.Quit, new Callback<Event>() {
            @Override
            public void call(Event event) throws Exception {
                System.exit(0);
            }
        });
    }
}
