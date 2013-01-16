package org.joshy.gfx.test.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.event.ActionEvent;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.MouseEvent;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.control.Label;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.node.layout.VFlexBox;
import org.joshy.gfx.node.shape.Rectangle;
import org.joshy.gfx.node.shape.Shape;
import org.joshy.gfx.stage.Stage;

import java.awt.geom.Point2D;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Nov 30, 2010
 * Time: 7:19:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class StageTest extends GrandTour.Example implements Runnable {
    private Callback<ActionEvent> openNormalStage;
    private Callback<ActionEvent> openNoChromeStage;
    private Callback<ActionEvent> openTranslucentStage;
    private Callback<ActionEvent> openAlwaysOnTopStage;

    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().defer(new StageTest("test"));
    }
    public StageTest(String name) {
        super(name);
    }

    @Override
    public void run() {
        Stage stage = Stage.createStage();
        try {
            stage.setContent(build());
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    @Override
    public Control build() throws Exception {
        openNormalStage = new Callback<ActionEvent>(){
            @Override
            public void call(ActionEvent event) throws Exception {
                Stage s1 = Stage.createStage();
                s1.setContent(new Label("A normal stage"));
                s1.setWidth(300);
                s1.setHeight(300);
                s1.setTitle("normal stage");
            }
        };

        openNoChromeStage = new Callback<ActionEvent>() {
            @Override
            public void call(ActionEvent event) throws Exception {
                Stage s1 = Stage.createStage();
                s1.setTitle("no chrome");
                s1.setContent(new Label("A no-chrome stage"));
                s1.setUndecorated(true);
                s1.centerOnScreen();
            }
        };

        openTranslucentStage = new Callback<ActionEvent>() {
            @Override
            public void call(ActionEvent event) throws Exception {
                final Stage s1 = Stage.createStage();
                Callback<ActionEvent> closeAction = new Callback<ActionEvent>(){
                    @Override
                    public void call(ActionEvent event) throws Exception {
                        s1.hide();
                    }
                };
                s1.setTitle("translucent");
                Shape rect = new Rectangle(0, 0, 200, 300)
                        .setArcWidth(30)
                        .setArcHeight(30)
                        .setFill(FlatColor.BLACK.deriveWithAlpha(0.5));
                EventBus.getSystem().addListener(rect,MouseEvent.MouseAll, new Callback<MouseEvent>(){
                    public Point2D start;

                    @Override
                    public void call(MouseEvent event) throws Exception {
                        if(event.getType() == MouseEvent.MousePressed) {
                            start = event.getPointInScreenCoords();
                        }
                        if(event.getType() == MouseEvent.MouseDragged) {
                            Point2D current = event.getPointInScreenCoords();
                            s1.setX(s1.getX()+current.getX()-start.getX());
                            s1.setY(s1.getY()+current.getY()-start.getY());
                            start = current;
                        }
                    }
                });
                s1.setContent(new Panel()
                        .add(rect)
                        .add(new Label("Translucent Stage")
                            .setColor(FlatColor.WHITE)
                            .setTranslateX(20)
                            .setTranslateY(20))
                        .add(new Button("close")
                            .onClicked(closeAction)
                            .setTranslateX(20)
                            .setTranslateY(60))
                        );
                s1.setOpacity(0.5);
                s1.centerOnScreen();
            }
        };

        openAlwaysOnTopStage = new Callback<ActionEvent>() {
            @Override
            public void call(ActionEvent event) throws Exception {
                Stage s1 = Stage.createStage();
                s1.setTitle("always on top");
                s1.setContent(new Label("An always on top stage"));
                s1.setAlwaysOnTop(true);
                s1.centerOnScreen();
            }
        };

        return new VFlexBox()
                .add(new Button("normal stage").onClicked(openNormalStage))
                .add(new Button("no chrome").onClicked(openNoChromeStage))
                .add(new Button("translucent stage").onClicked(openTranslucentStage))
                .add(new Button("always on top stage").onClicked(openAlwaysOnTopStage))
                ;
    }

}
