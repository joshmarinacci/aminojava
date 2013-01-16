package org.joshy.gfx.test.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Font;
import org.joshy.gfx.draw.PatternPaint;
import org.joshy.gfx.event.*;
import org.joshy.gfx.node.Group;
import org.joshy.gfx.node.control.*;
import org.joshy.gfx.node.layout.*;
import org.joshy.gfx.node.shape.*;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.test.drawing.ChartTest;
import org.joshy.gfx.test.drawing.OverlayTest;
import org.joshy.gfx.test.drawing.TransformTest;

import java.awt.geom.Path2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GrandTour implements Runnable { 
    public static void main(String... args) throws Exception, InterruptedException {
        Core.setUseJOGL(false);
        Core.init();
        //Core.getShared().loadCSS(new File("test.css"));
        //Core.setDebugCSS(new File("test.css"));
        //Core.requestDebugCSS();
        Core.getShared().defer(new GrandTour());
    }

    public void run() {
        EventBus.getSystem().addListener(SystemMenuEvent.Quit, new Callback<SystemMenuEvent>() {
            @Override
            public void call(SystemMenuEvent event) throws Exception {
                System.exit(0);
            }
        });
        List<Example> examples = new ArrayList<Example>();
        examples.add(new Example("Buttons") {
            public Control build() {
                FlexBox box = new VFlexBox()
                        .setBoxAlign(FlexBox.Align.Left)
                        .add(new Button("Regular Button"))
                        .add(new Button("Disabled Button").setEnabled(false))
                        .add(new Togglebutton("Toggle Button"))
                        .add(new Togglebutton("Disabled Toggle").setEnabled(false))
                        .add(new Checkbox("Check box"))
                        .add(new Radiobutton("Radio button"))
                        .add(new Linkbutton("a hyperlink"));
                Button hoverButton = new Button("Tooltip");
                Tooltip tooltip = new Tooltip(hoverButton,"I'm a tooltip");
                box.add(hoverButton);
                return box;
            }
        });
        
        examples.add(new Example("Labels") {
            @Override
            public Control build() throws Exception {
                FlexBox box = new VFlexBox().setBoxAlign(FlexBox.Align.Left)
                        .add(new Label("Alabel"))
                        .add(new Label("A label"))
                        .add(new Label("A label so long that it will have to wrap").setPrefWidth(150))
                        .add(new Label("A multiline\nlabel with \nhard coded returns"))
                        .add(new Label("A Big Font!").setFont(Font.name("Arial").size(30).resolve()));
                ;
                return box;
            }
        });

        examples.add(new Example("Text controls") {
            public Control build() {
                Textbox tb = new Textbox();
                tb.setText("012345");
                tb.selectAll();
                tb.setPrefWidth(150);
                Passwordbox passbox = new Passwordbox();
                passbox.setText("password");
                SpinBox<Integer> spinBox = new SpinBox<Integer>()
                        .setValue(5)
                        .setMinValue(0)
                        .setMaxValue(10);
                        ;

                Textbox searchbox = new Textbox("searchbox");
                searchbox.setId("searchbox");
                searchbox.addCSSClass("searchbox");
                searchbox.setPrefWidth(150);

                Control largeBox = new Textbox("large font")
                        .setFont(Font.name("Arial").size(30).resolve())
                        .setPrefWidth(150)
                        ;

                Textarea ta = new Textarea();
                ta.setText("A\nText\nArea");
                ta.setPrefWidth(200);
                ta.setPrefHeight(100);
                FlexBox box = new VFlexBox().setBoxAlign(FlexBox.Align.Left)
                        .add(tb)
                        .add(spinBox)
                        .add(passbox)
                        .add(searchbox)
                        .add(largeBox)
                        .add(ta);
                return box;
            }
        });
        examples.add(new Example("Sliders and Scrollbars") {
            public Control build() {
                Scrollbar sb = new Scrollbar();
                sb.setWidth(200);
                sb.setMin(0); sb.setMax(200); sb.setValue(100);
                Scrollbar psb = new Scrollbar();
                psb.setProportional(true);
                psb.setWidth(200);
                psb.setMin(0); psb.setMax(200); psb.setValue(100); psb.setSpan(0.5);
                Slider slider = new Slider(false);
                //slider.setWidth(200);
                slider.setMin(0);
                slider.setMax(100);
                slider.setValue(50);
                FlexBox box = new VFlexBox().setBoxAlign(FlexBox.Align.Left)
                        .add(sb)
                        .add(psb)
                        .add(slider);
                return box;
            }
        });
        examples.add(new Example("Progress Bars and Spinners") {
            public Control build() throws InterruptedException {
                ProgressBar pb = new ProgressBar();
                ProgressSpinner ps = new ProgressSpinner();
                FlexBox box = new VFlexBox().setBoxAlign(FlexBox.Align.Left)
                        .add(pb)
                        .add(ps);
                BackgroundTask task = new BackgroundTask<String, String>() {
                    @Override
                    protected String onWork(String data) {
                        String result = "bar";
                        for(int i=0; i<100; i++) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            this.updateGUI(data,result,i/100.0);
                        }
                        updateGUI(data,result,1);
                        return result;
                    }
                };
                task.setData("foo");
                pb.setTask(task);

                BackgroundTask task2 = new BackgroundTask<String,String>() {
                    @Override
                    protected String onWork(String data) {
                        String result = "blah";
                        this.updateGUI(data,result,-1);
                        try {
                            Thread.sleep(100*100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        this.updateGUI(data,result,1);
                        return result;
                    }
                };

                ps.setTask(task2);
                task.start();
                task2.start();
                return box;
            }
        });
        examples.add(new Example("Complex (lists & dropdowns)") {
            public Control build() throws IOException {
                PopupMenuButton popup = new PopupMenuButton();
                SwatchColorPicker color1 = new SwatchColorPicker();
                SwatchColorPicker color2 = new SwatchColorPicker();
                popup.setModel(ListView.createModel(new String[]{"Ethernet","WiFi","Bluetooth","FireWire","USB hack"}));

                ListView listView = new ListView();
                listView.setModel(new ListModel(){
                    public Object get(int i) {
                        return "item " + i;
                    }
                    public int size() {
                        return 100;
                    }
                });
                ScrollPane sp = new ScrollPane();
                sp.setContent(listView);
                sp.setWidth(300);
                sp.setHeight(200);

                CompoundListView clistView = new CompoundListView();
                clistView.setItemViewFactory(new CompoundListView.ItemViewFactory(){
                    @Override
                    public Control createItemView(CompoundListView listView, int index, Control prev) {
                        return new Button("item");
                    }
                });
                clistView.setRowHeight(35);
                ScrollPane sp3 = new ScrollPane();
                sp3.setContent(clistView);
                sp3.setWidth(300);
                sp3.setHeight(200);


                FlexBox box = new VFlexBox()
                        .add(popup)
                        .add(new HFlexBox().add(color1,color2))
                        .add(new Label("List View"))
                        .add(sp)
                        .add(new Label("Compounds List View"))
                        .add(sp3)
                        ;
                return box;

            }
        });

        examples.add(new Example("Title Panel") {
            @Override
            public Control build() throws Exception {
                TitlePanel panel = new TitlePanel();
                panel.add(new Button("blah"));
                return panel;
            }
        });

        examples.add(new Example("Disclosure Panel"){
            @Override
            public Control build() throws Exception {
                return new VFlexBox()
                        .setBoxAlign(FlexBox.Align.Stretch)
                        .add(new DisclosurePanel()
                            .setTitle(new Label("click to disclose"))
                            .setContent(new Panel().setFill(FlatColor.RED).setPrefWidth(200).setPrefHeight(200))
                        )
                        .add(new DisclosurePanel()
                            .setTitle(new Label("click to disclose"))
                            .setContent(
                                    new TabPanel()
                                    .add("foo",new Button("foo"))
                                    .add("bar",new Button("bar"))
                                    .setPrefHeight(100)
                                    .setPrefWidth(100)
                                    )
                        )
                        .add(new DisclosurePanel()
                            .setTitle(new Label("Right"))
                            .setPosition(DisclosurePanel.Position.Right)
                            .setContent(
                                    new HFlexBox()
                                            .add(new Button("b1"))
                                            .add(new Button("b2"))
                                            .add(new Button("b3"))
                                            .add(new Button("b4"))
                                    )

                        )
                        .add(new DisclosurePanel()
                            .setTitle(new Label("Right + tabs"))
                            .setPosition(DisclosurePanel.Position.Right)
                            .setContent(
                                    new TabPanel()
                                    .add("foo",new Button("foo"))
                                    .add("bar",new Button("bar"))
                                    .setPrefHeight(100)
                                    .setPrefWidth(100)
                                    )

                        )
                        ;
            }
        });

        examples.add(new Example("Tabbed Panel") {
            @Override
            public Control build() throws Exception {
                TabPanel panel = new TabPanel();
                panel.add("Tab 1", new Button("Button 1"));
                panel.add("Tab 2", new Button("Button 2"));
                return panel;
            }
        });

        examples.add(new BoxExample());
        examples.add(new Example("Scenegraph Shapes"){
            @Override
            public Control build() throws Exception {
                Group group = new Group();
                PatternPaint pat = PatternPaint.create(GrandTour.class.getResource("pattern.png"),"pattern.png");
                group.add(new Rectangle(100,20,200,100)
                        .setFill(pat));
                group.add(new Oval(50,20)
                        .setFill(FlatColor.GREEN)
                        .setTranslateX(30)
                        .setTranslateY(50)
                );
                group.add(new Circle(40,100)
                        .setRadius(20)
                        .setFill(FlatColor.BLUE.deriveWithAlpha(0.5))
                );
                group.add(new Arc(40,200)
                        .setStartAngle(30)
                        .setEndAngle(180)
                        .setRadius(30)
                        .setClosed(true)
                        .setFill(FlatColor.PURPLE)
                );
                group.add(new Polygon()
                        .addPoint(0,0).addPoint(10,10).addPoint(20,0).addPoint(25,30).addPoint(0,30)
                        .setClosed(true)
                        .setFill(FlatColor.YELLOW)
                        .setTranslateX(20)
                        .setTranslateY(130)
                );
                group.add(new BezierCurve(
                        0,0,0,20,
                        30,10,30,30
                        )
                        .setStrokeWidth(3)
                        .setTranslateX(150)
                        .setTranslateY(150)
                );
                Path2D.Double path = new Path2D.Double();
                path.moveTo(0,0);
                path.lineTo(30,40);
                path.curveTo(40,50, 10,10, 0,30);
                path.closePath();
                group.add(new PathShape(path)
                        .setClosed(true)
                        .setFill(FlatColor.GRAY)
                        .setTranslateX(50)
                        .setTranslateY(200)
                );
                return new Panel().add(group);
            }
        });

        examples.add(new TransformTest("Transformed Controls"));
        examples.add(new ChartTest("Scenegraph Chart w/ 1000 data points"));
        examples.add(new OverlayTest("Interleave Controls and Shapes"));
        examples.add(new StageTest("Stage Test"));
        examples.add(new TableTest("Tables and Tree and TreeTables"));



        ListView exampleList = new ListView();
        final ListModel<Example> exampleModel = (ListModel<Example>)ListView.createModel(examples);
        exampleList.setModel(exampleModel);
        exampleList.setTextRenderer(new ListView.TextRenderer<Example>(){
            public String toString(SelectableControl view, Example item, int index) {
                if(item != null) {
                    return item.name;
                } else {
                    return null;
                }
            }
        });
        
        ScrollPane exampleListScroll = new ScrollPane();
        exampleListScroll.setContent(exampleList);

        final SplitPane split = new SplitPane(false);
        split.setFirst(exampleListScroll);
        split.setSecond(new Panel());
        split.setPosition(300);

        Callback<SelectionEvent> callback = new Callback<SelectionEvent>() {
            public void call(SelectionEvent event) {
                Example ex = exampleModel.get(event.getView().getSelectedIndex());
                if(ex == null) return;
                try {
                    split.setSecond(ex.build());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        EventBus.getSystem().addListener(exampleList, SelectionEvent.Changed, callback);

        Stage stage = Stage.createStage();
        stage.setContent(split);
        stage.setWidth(800);
        stage.centerOnScreen();
    }

    public abstract static class Example {
        public String name;

        public Example(String name) {
            this.name = name;
        }

        public abstract Control build() throws Exception;
    }

}
