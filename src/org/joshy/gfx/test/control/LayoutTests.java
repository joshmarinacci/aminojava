package org.joshy.gfx.test.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.event.ActionEvent;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.SelectionEvent;
import org.joshy.gfx.node.control.*;
import org.joshy.gfx.node.layout.*;
import org.joshy.gfx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Aug 28, 2010
 * Time: 6:55:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class LayoutTests implements Runnable {
    private SplitPane split;
    private static final String SIMPLE_HBOX_BUTTONS = "Simple HBox with buttons";
    private static final String SIMPLE_HBOX = "Simple HBox";
    private static final String SIMPLE_HBOX_BASELINE = "Simple HBox with baseline";
    private static final String HBOX_WITH_SPACERS = "Hbox with spacers";
    private static final String VBOX_WITH_TOOLBAR_AND_STATUSBAR = "VBox with toolbar and statusbar";
    private static final String VBOX_SIMPLE = "VBox simple";
    private static final String VBOX_RIGHT_SIMPLE = "VBox simple, right aligned";
    private static final String VBOX_STRETCH_SIMPLE = "VBox simple, stretch";
    private static final String COMPLEX_CENTER_ALIGNED_DIALOG = "Complex center aligned dialog";
    private static final String SIMPLE_HBOX_BOTTOM = "Simple HBox Bottom";
    private static final String BUTTON_SIZING = "Button Sizing";
    private static final String NEW_DOC_DIALOG_GRID_TEST = "Grid Test: New doc dialog";
    private static final String CONTROLS_BASELINE_TEST = "Controls baseline test";
    private static final String TEXTBOX_GRID_WIDTH = "Textbox Grid Width";
    private static final String HBOX_SHRINK_TO_FIT = "HBox shrink to fit 1";
    private static final String HBOX_SHRINK_TO_FIT_EXPLICIT_WIDTH = "Hbox explicit preferred width";
    private static final String HBOX_SHRINK_TO_FIT_EXPLICIT_HEIGHT = "HBox explicit preferred height";
    private static final String HBOX_SHRINK_TO_FIT_EXPLICIT_WIDTH_HEIGHT = "HBox explicit pref w & h";
    private static final String VBOX_SHRINK_TO_FIT = "VBox shrink to fit 1";
    private static final String VBOX_SHRINK_TO_FIT_EXPLICIT_WIDTH = "VBox explicit preferred width";
    private static final String VBOX_SHRINK_TO_FIT_EXPLICIT_HEIGHT = "VBox explicit preferred height";
    private static final String VBOX_SHRINK_TO_FIT_EXPLICIT_WIDTH_HEIGHT = "VBox explicit pref w & h";

    public static void main(String ... args) throws Exception {
        Core.init();
        //Core.setDebugCSS(new File("/Users/joshmarinacci/projects/personal/amino/test.css"));
        Core.getShared().defer(new LayoutTests());
    }

    public void run() {
        Stage stage = Stage.createStage();
        stage.setWidth(900);
        stage.setHeight(600);

        split = new SplitPane(false);
        stage.setContent(split);
        split.setPosition(200);

        List<String> tests = new ArrayList<String>();
        tests.add(SIMPLE_HBOX_BUTTONS);
        tests.add(BUTTON_SIZING);
        tests.add(SIMPLE_HBOX);
        tests.add(SIMPLE_HBOX_BASELINE);
        tests.add(SIMPLE_HBOX_BOTTOM);
        tests.add(HBOX_WITH_SPACERS);
        tests.add(VBOX_SIMPLE);
        tests.add(VBOX_RIGHT_SIMPLE);
        tests.add(VBOX_STRETCH_SIMPLE);
        tests.add(VBOX_WITH_TOOLBAR_AND_STATUSBAR);
        tests.add(COMPLEX_CENTER_ALIGNED_DIALOG);
        tests.add(NEW_DOC_DIALOG_GRID_TEST);
        tests.add(CONTROLS_BASELINE_TEST);
        tests.add(TEXTBOX_GRID_WIDTH);
        tests.add(HBOX_SHRINK_TO_FIT);
        tests.add(HBOX_SHRINK_TO_FIT_EXPLICIT_WIDTH);
        tests.add(HBOX_SHRINK_TO_FIT_EXPLICIT_HEIGHT);
        tests.add(HBOX_SHRINK_TO_FIT_EXPLICIT_WIDTH_HEIGHT);
        tests.add(VBOX_SHRINK_TO_FIT);
        tests.add(VBOX_SHRINK_TO_FIT_EXPLICIT_WIDTH);
        tests.add(VBOX_SHRINK_TO_FIT_EXPLICIT_HEIGHT);
        tests.add(VBOX_SHRINK_TO_FIT_EXPLICIT_WIDTH_HEIGHT);

        final ListView<String> testView =new ListView<String>().setModel(ListView.createModel(tests)); 
        split.setFirst(new ScrollPane(testView));
        split.setSecond(new Panel());
        EventBus.getSystem().addListener(SelectionEvent.Changed, new Callback<SelectionEvent>() {
            public void call(SelectionEvent event) {
                if(event.getView() == testView) {
                    split.setSecond(getTest(testView.getModel().get(testView.getSelectedIndex())));
                }
            }
        });
    }

    private Control getTest(String testName) {
        //u.p("doing test " + testName);

        if(SIMPLE_HBOX_BUTTONS.equals(testName)) {
            return new HFlexBox().add(new Button("Label"),new Button("Button"), new Button("Popup List"));
        }
        if(BUTTON_SIZING.equals(testName)) {
            return new HFlexBox().add(new Button("pref width == 200px").setPrefWidth(200));
        }
        
        if(SIMPLE_HBOX.equals(testName)) {
            return new HFlexBox()
                    .add(new Label("Label"), new Button("Button"), createPopup("Popup List"));
        }

        if(SIMPLE_HBOX_BASELINE.equals(testName)) {
            return new HFlexBox()
                    .setBoxAlign(FlexBox.Align.Baseline)
                    .add(
                            new Label("Label")
                            , new Button("Button")
                            , createPopup("Popup List")
                            , new Textbox("this is a text box")
                    );
        }
        if(SIMPLE_HBOX_BOTTOM.equals(testName)) {
            return new HFlexBox()
                    .setBoxAlign(FlexBox.Align.Bottom)
                    .add(new Label("Label"), new Button("Button"), createPopup("Popup List"));
        }

        if(HBOX_WITH_SPACERS.equals(testName)) {
            return new HFlexBox()
                    .add(new Button("before"))
                    .add(new Spacer(),1)
                    .add(new Button("between"))
                    .add(new Spacer(),1)
                    .add(new Button("after"))
                    .add(new Button("stretchy button"));
        }

        if(VBOX_SIMPLE.equals(testName)) {
            return new VFlexBox()
                    .add(new Button("B1"))
                    .add(new Button("B2"))
                    .add(new Button("super long button"));
        }
        if(VBOX_RIGHT_SIMPLE.equals(testName)) {
            return new VFlexBox()
                    .setBoxAlign(FlexBox.Align.Right)
                    .add(new Button("B1"))
                    .add(new Button("B2"))
                    .add(new Button("super long button"));
        }
        if(VBOX_STRETCH_SIMPLE.equals(testName)) {
            return new VFlexBox()
                    .setBoxAlign(FlexBox.Align.Stretch)
                    .add(new Button("B1"))
                    .add(new Button("B2"))
                    .add(new Button("super long button"));
        }

        if(VBOX_WITH_TOOLBAR_AND_STATUSBAR.equals(testName)) {
            FlexBox toolbar = new HFlexBox();
            toolbar.add(new Button("B1"),new Button("B2"), new Button("B3"));
            toolbar.add(new Spacer(),1);
            toolbar.add(new Button("B4"),new Button("B5"),new Button("B6"));
            //toolbar.setHeight(50);

            Control statusbar = new HFlexBox()
                    .add(new Label("left edge"))
                    .add(new Spacer(),1)
                    .add(new Label("right edge"))
                    ;//.setHeight(50);

            return new VFlexBox()
                    .setBoxAlign(FlexBox.Align.Stretch)
                    .add(toolbar)
                    .add(new Panel().setFill(FlatColor.RED),1)
                    .add(statusbar);
        }
        if(COMPLEX_CENTER_ALIGNED_DIALOG.equals(testName)) {
            return new GridBox()
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
                        ,new Label("and smaller")).setHeight(20));
        }
        if(NEW_DOC_DIALOG_GRID_TEST.equals(testName)) {
            final Textbox width = new Textbox("800");
            width.setPrefWidth(100);
            final Textbox height = new Textbox("500");
            height.setPrefWidth(100);
            
            final PopupMenuButton popup = new PopupMenuButton();
            popup.setModel(ListView.createModel(new String[]{"16x16","1024x1024"}));
            Callback<ActionEvent> clicked = new Callback<ActionEvent>() {
                public void call(ActionEvent event) {
                    switch(popup.getSelectedIndex()) {
                        case 0: width.setText("16"); height.setText("16"); break;
                        case 1: width.setText("1024"); height.setText("768"); break;
                    }
                }
            };

            GridBox gb = new GridBox()
                    .setPadding(5)
                    .createColumn(100, GridBox.Align.Right)
                    .createColumn(100, GridBox.Align.Left)
                    .addControl(new Label("Preset:"))
                    .addControl(popup)
                    .nextRow()
                    .addControl(new Label("Width (px):"))
                    .addControl(width)
                    .nextRow()
                    .addControl(new Label("Height (px):"))
                    .addControl(height)
                    .nextRow()
                    .addControl(new Button("Cancel"))
                    .addControl(new Button("Okay"));
            gb.debug(false);
            return gb;
        }

        if(CONTROLS_BASELINE_TEST.equals(testName)) {
            return new VFlexBox()
                    .setBoxAlign(FlexBox.Align.Stretch)
                    .add(new HFlexBox()
                        .setBoxAlign(FlexBox.Align.Baseline)
                        .add(new Button("A Button"))
                        .add(new Label("A Label"))
                        .add(new Checkbox("A Checkbox"))
                        .add(new Radiobutton("A Radiobutton"))
                        .add(new Textbox("A Textbox"))
                        .add(createPopup("A Popup menu"))
                        .add(new Linkbutton("A HyperLink"))
                        .setHeight(100)
                    );
        }

        if(TEXTBOX_GRID_WIDTH.equals(testName)) {
            return new GridBox()
                    .setPadding(5)
                    .createColumn(100, GridBox.Align.Right)
                    .createColumn(100, GridBox.Align.Fill)
                    .addControl(new Label("Textbox: "))
                    .addControl(new Textbox("tb"));
        }

        if(HBOX_SHRINK_TO_FIT.equals(testName)) {
            return new Panel().add(
                    new HFlexBox().add(
                            new Button("Label"),
                            new Button("Button"),
                            new Button("Popup List")
                    ).setFill(FlatColor.RED));
        }
        if(HBOX_SHRINK_TO_FIT_EXPLICIT_WIDTH.equals(testName)) {
            return new Panel().add(
                    new HFlexBox().add(
                            new Button("Label"),
                            new Button("Button"),
                            new Button("Popup List")
                    ).setFill(FlatColor.RED).setPrefWidth(100));
        }
        if(HBOX_SHRINK_TO_FIT_EXPLICIT_HEIGHT.equals(testName)) {
            return new Panel().add(
                    new HFlexBox().add(
                            new Button("Label"),
                            new Button("Button"),
                            new Button("Popup List")
                    ).setFill(FlatColor.RED).setPrefHeight(100));
        }
        if(HBOX_SHRINK_TO_FIT_EXPLICIT_WIDTH_HEIGHT.equals(testName)) {
            return new Panel().add(
                    new HFlexBox().add(
                            new Button("Label"),
                            new Button("Button"),
                            new Button("Popup List")
                    ).setFill(FlatColor.RED).setPrefWidth(100).setPrefHeight(100));
        }

        if(VBOX_SHRINK_TO_FIT.equals(testName)) {
            return new Panel().add(
                    new VFlexBox().add(
                            new Button("Label"),
                            new Button("Button"),
                            new Button("Popup List")
                    ).setFill(FlatColor.RED));
        }
        if(VBOX_SHRINK_TO_FIT_EXPLICIT_WIDTH.equals(testName)) {
            return new Panel().add(
                    new VFlexBox().add(
                            new Button("Label"),
                            new Button("Button"),
                            new Button("Popup List")
                    ).setFill(FlatColor.RED).setPrefWidth(100));
        }
        if(VBOX_SHRINK_TO_FIT_EXPLICIT_HEIGHT.equals(testName)) {
            return new Panel().add(
                    new VFlexBox().add(
                            new Button("Label"),
                            new Button("Button"),
                            new Button("Popup List")
                    ).setFill(FlatColor.RED).setPrefHeight(100));
        }
        if(VBOX_SHRINK_TO_FIT_EXPLICIT_WIDTH_HEIGHT.equals(testName)) {
            return new Panel().add(
                    new VFlexBox().add(
                            new Button("Label"),
                            new Button("Button"),
                            new Button("Button"),
                            new Button("Button"),
                            new Button("Button"),
                            new Button("Button"),
                            new Button("Button"),
                            new Button("Button"),
                            new Button("Button"),
                            new Button("Button"),
                            new Button("Button"),
                            new Button("Button"),
                            new Button("Button"),
                            new Button("Button"),
                            new Button("Button"),
                            new Button("Button"),
                            new Button("Button"),
                            new Button("Popup List")
                    ).setFill(FlatColor.RED).setPrefWidth(100).setPrefHeight(100));
        }

        return new Panel();
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
