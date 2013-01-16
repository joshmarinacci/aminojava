package org.joshy.gfx.test.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.MouseEvent;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.control.ScrollPane;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.node.layout.VFlexBox;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.u;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Oct 4, 2010
 * Time: 8:24:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScrollPaneInputCropping implements Runnable {

    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().defer(new ScrollPaneInputCropping());
    }

    @Override
    public void run() {
        Panel panel = new Panel();
        panel.setWidth(1000);
        panel.setHeight(1000);
        panel.setFill(FlatColor.PURPLE);
        EventBus.getSystem().addListener(panel, MouseEvent.MousePressed, new Callback<MouseEvent>() {
            @Override
            public void call(MouseEvent event) {
                u.p("panel was pressed");
            }
        });
        Stage stage = Stage.createStage();
        stage.setContent(new VFlexBox()
                .setBoxAlign(VFlexBox.Align.Stretch)
                .add(new Button("spacer button"),1)
                .add(new ScrollPane(panel),1));

    }
}
