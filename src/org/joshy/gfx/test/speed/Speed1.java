package org.joshy.gfx.test.speed;

import org.joshy.gfx.Core;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.layout.FlexBox;
import org.joshy.gfx.node.layout.VFlexBox;
import org.joshy.gfx.stage.Stage;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Nov 3, 2010
 * Time: 4:04:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class Speed1 implements Runnable {
    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().defer(new Speed1());
    }

    @Override
    public void run() {
        final FlexBox box = new VFlexBox().setBoxAlign(VFlexBox.Align.Stretch);
        for(int i=0; i<100; i++) {
            box.add(new Button("button " + i));
        }
        Stage stage = Stage.createStage();
        stage.setContent(box);

    }
}
