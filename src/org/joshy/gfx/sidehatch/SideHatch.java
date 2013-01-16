package org.joshy.gfx.sidehatch;

import org.joshy.gfx.Core;
import org.joshy.gfx.node.layout.TabPanel;
import org.joshy.gfx.stage.Stage;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Nov 30, 2010
 * Time: 3:00:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class SideHatch {
    public static void launch() {
        Stage stage = Stage.createStage();
        stage.setTitle("SideHatch");
        InspectorEditor ie = new InspectorEditor();
        stage.setContent(new TabPanel()
                .add("Translator",new TranslationEditor())
                .add("Inspector", ie)
        );
        stage.setWidth(700);
        stage.setHeight(400);

        //install the overlays
        for(Stage s : Core.getShared().getStages()) {
            if(s == stage) continue;
            s.getPopupLayer().removeAll();
            s.getPopupLayer().add(new ControlBoundsOverlay(ie,s));
        }
    }
}
