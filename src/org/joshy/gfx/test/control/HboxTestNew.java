package org.joshy.gfx.test.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.node.control.*;
import org.joshy.gfx.node.layout.*;
import org.joshy.gfx.stage.Stage;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Aug 24, 2010
 * Time: 10:58:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class HboxTestNew implements Runnable {

    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().defer(new HboxTestNew());

    }

    public void run() {
        Stage stage = Stage.createStage();
        stage.setWidth(668);
        stage.setHeight(500);

        //a test of a simple toolbar with two spacers in it
        stage.setContent(
             new HFlexBox()
                  .setBoxAlign(HFlexBox.Align.Baseline) //this is the default
                  .add(new Button("foo"))
                  .add(new Spacer(),1)
                  .add(new Button("bar"))
                  .add(new Button("baz"))
                  .add(new Spacer(),1)
                  .add(new Button("qux"))
             );

        //a test of a complex layout. toolbar, split pane of lists, then bottom statusbar
        stage.setContent(
            new VFlexBox()
                .setBoxAlign(FlexBox.Align.Stretch)//make them stretch to fill the width of the window
                .add(
                    //the toolbar
                    new HFlexBox()
                        .add(new Button()).add(new Button()).add(new Button())
                        .setHeight(50)
                    )

                .add(
                      //two lists in a split pane
                      new SplitPane(false)
                           //.setResizeValue(0) //0% means all excess space goes to the right part
                           .setPosition(200) // 200px as the default value
                           .setFirst(new ListView())
                           .setSecond(new ListView())
                        ,1
                     )
                .add(
                     //status bar
                     new HFlexBox()
                          .add(new Label("left edge"))
                          .add(new Spacer(),1)
                          .add(new Label("right edge"))
                          .setHeight(50)
                     )
            );

        stage.setContent(
            new GridBox()
                .setPadding(5)
                .createColumn(120, GridBox.Align.Right)
                .createColumn(200, GridBox.Align.Left)
                    .addControl(new Label("Appearance:"))
                    .addControl(createPopup("Blue"))
                .nextRow()
                    .skip()
                    .addControl(new Label("For the overall look of buttons, menus, and windows"))
                .nextRow()
                    .addControl(new Label("Highlight Color"))
                    .addControl(createPopup("Blue"))
                .nextRow()
                    .skip().addControl(new Label("For selected text"))
                .nextRow()
                    .addControl(new Label("Place scroll arrows:"))
                    .addControl(new Radiobutton("Together"))
                .nextRow()
                    .skip()
                    .addControl(new Radiobutton("At top and bottom"))
                .nextRow()
                    .addControl(new Label("Click in the scroll bar to:"))
                    .addControl(new Radiobutton("Jump to the next page"))
                .nextRow()
                    .skip()
                    .addControl(new Radiobutton("Jump to the spot that's clicked"))
                .nextRow()
                    .skip()
                    .addControl(new Checkbox("Use smooth scrolling"))
                .nextRow()
                    .skip()
                    .addControl(new Checkbox("Double-click a window's title bar to minimize"))
                .nextRow()
                    .addControl(new Label("Number of recent items:"))
                    .addControl(new HFlexBox().add(createPopup("10"),new Label("Applications")).setHeight(20))
                .nextRow()
                    .skip()
                    .addControl(new HFlexBox().add(createPopup("10"),new Label("Documents")).setHeight(20))
                .nextRow()
                    .skip()
                    .addControl(new HFlexBox().add(createPopup("10"),new Label("Servers")).setHeight(20))
                .nextRow()
                    .skip()
                    .addControl(new Checkbox("Use LCD font smoothing when available"))
                .nextRow()
                    .addControl(new HFlexBox().add(
                        new Label("Turn off font smoothing for font sizes")
                        ,createPopup("4")
                        ,new Label("and smaller")).setHeight(20))


        );

    }

    private static PopupMenuButton createPopup(final String s) {
        PopupMenuButton<String> pm = new PopupMenuButton<String>();
        pm.setModel(new ListModel<String>() {

            public String get(int i) {
                return s;
            }

            public int size() {
                return 3;
            }
        });
        return pm;
    }


}
