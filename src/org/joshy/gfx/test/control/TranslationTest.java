package org.joshy.gfx.test.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.event.ActionEvent;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.SystemMenuEvent;
import org.joshy.gfx.node.control.*;
import org.joshy.gfx.node.layout.HFlexBox;
import org.joshy.gfx.node.layout.VFlexBox;
import org.joshy.gfx.sidehatch.SideHatch;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.ArrayListModel;
import org.joshy.gfx.util.localization.Localization;

import static org.joshy.gfx.util.localization.Localization.getString;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Nov 29, 2010
 * Time: 4:49:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class TranslationTest implements Runnable {
    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().defer(new TranslationTest());
    }

    @Override
    public void run() {
        EventBus.getSystem().addListener(SystemMenuEvent.Quit, new Callback<SystemMenuEvent>() {
            @Override
            public void call(SystemMenuEvent event) throws Exception {
                System.exit(0);
            }
        });
        final Stage stage = Stage.createStage();
        try {
            Localization.init(TranslationTest.class.getResource("translationtest.xml"),"en-US");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Callback<ActionEvent> updateAction = new Callback<ActionEvent>(){
            @Override
            public void call(ActionEvent event) throws Exception {
                SideHatch.launch();
            }
        };
        ArrayListModel<CharSequence> popupModel = new ArrayListModel<CharSequence>();
        popupModel.add(getString("test.popup1"));
        popupModel.add(getString("test.popup2"));
        popupModel.add(getString("test.popup3"));
        stage.setContent(new VFlexBox()
                .add(new Button(getString("test.button.text")))
                .add(new Textbox().setHintText(getString("test.textbox.hint")).setPrefWidth(200))
                .add(new Checkbox(getString("test.checkbox.text")))
                .add(new Label(getString("test.label.text")))
                .add(new Label(getString("test.label2.text")))
                .add(new PopupMenuButton<CharSequence>().setModel(popupModel))
                .add(new HFlexBox().add(new Button("b1"),new Button("b2")))
                .add(new Button("edit").onClicked(updateAction))
        );
    }
}
