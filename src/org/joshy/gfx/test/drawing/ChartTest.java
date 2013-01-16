package org.joshy.gfx.test.drawing;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.MouseEvent;
import org.joshy.gfx.node.Group;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.node.shape.Oval;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.test.control.GrandTour;
import org.joshy.gfx.util.u;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Oct 27, 2010
 * Time: 11:09:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChartTest extends GrandTour.Example implements Runnable {
    public ChartTest(String s) {
        super(s);
    }

    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().defer(new ChartTest("test"));
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
        EventBus.getSystem().addListener(MouseEvent.MousePressed, new Callback<MouseEvent>(){
            @Override
            public void call(MouseEvent event) throws Exception {
                //u.p("pressed: " + event.getSource());
            }
        });
        Group group = new Group();
        List<DataPoint> dataPoints = new ArrayList<DataPoint>();
        for(int i=0; i<1000; i++) {
            dataPoints.add(new DataPoint(Math.random()*700,Math.random()*300, 5+Math.random()*20));
        }
        for(DataPoint dp : dataPoints) {
            group.add(new Oval()
                    .setWidth(dp.v)
                    .setHeight(dp.v)
                    .setFill(FlatColor.RED.deriveWithAlpha(0.4))
                    .setStrokeWidth(0)
                    .setTranslateX(dp.x)
                    .setTranslateY(dp.y)
            );
        }
        return new Panel().add(group);
    }

    class DataPoint {

        public double x;
        public double y;
        private double v;

        public DataPoint(double x, double y, double v) {
            this.x = x;
            this.y = y;
            this.v = v;
        }
    }
}
