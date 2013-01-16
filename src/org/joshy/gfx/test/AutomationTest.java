package org.joshy.gfx.test;

import org.joshy.gfx.Core;
import org.joshy.gfx.event.ActionEvent;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.control.Textbox;
import org.joshy.gfx.node.layout.VFlexBox;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.u;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Oct 25, 2010
 * Time: 11:08:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class AutomationTest extends AutomationBase {

    public static void main(String ... args) throws Exception {
        //turn on testing
        Core.setTesting(true);
        Core.init();

        //create a gui
        final Textbox box = new Textbox("");
        box.setId("textbox");
        Button button = new Button("Submit");
        button.setId("button");
        Callback<ActionEvent> callback = new Callback<ActionEvent>(){
            @Override
            public void call(ActionEvent event) throws Exception {
                u.p("box value = " + box.getText());
            }
        };
        button.onClicked(callback);

        final Stage stage = Stage.createStage();
        stage.setContent(new VFlexBox().setBoxAlign(VFlexBox.Align.Stretch)
            .add(box)
            .add(button));


        //queue up some events
        click(60,60); //should do nothing, but lets events get processed
        click("#button");//click button, it prints the default value
        click("#textbox"); //should find the text field and click it
        type("newvalue"); //type into the current focused node
        click("#button"); //click button, it prints the new value

        //process the events
        processAndQuit(stage);
    }

}
