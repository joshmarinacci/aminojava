package org.joshy.gfx.test.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.MouseEvent;
import org.joshy.gfx.node.Group;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.node.shape.Line;
import org.joshy.gfx.node.shape.Rectangle;
import org.joshy.gfx.stage.Stage;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: Oct 29, 2010
* Time: 6:18:39 AM
* To change this template use File | Settings | File Templates.
*/
class BoxExample extends GrandTour.Example implements Runnable {
    private static Dragger dragger;

    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().defer(new BoxExample());
    }

    public BoxExample() {
        super("Draggable Scenegraph Boxes");
    }

    @Override
    public Control build() throws Exception {
        final Line l1 = new Line(0, 0, 100, 100);
        l1.setStrokeWidth(3);
        final Line l2 = new Line(0, 0, 100, 100);
        l2.setStrokeWidth(3);

        Node r1 = new GraphRect(0,0,100,100)
                .addStartLine(l1)
                .addStartLine(l2)
                .setTranslateX(200)
                .setTranslateY(50);

        Node r2 = new GraphRect(0,0,100,100)
                .addEndLine(l1)
                .setTranslateX(50)
                .setTranslateY(200);

        Node r3 = new GraphRect(0,0,100,100)
                .addEndLine(l2)
                .setTranslateX(350)
                .setTranslateY(200);

        //only register the dragger once
        if(dragger == null) {
            dragger = new Dragger();
            EventBus.getSystem().addListener(MouseEvent.MouseAll, dragger);
        }

        return new Panel().add(new Group().add(l1,l2).add(r1,r2,r3));
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

    private static class GraphRect extends Rectangle {
        private List<Line> startLines = new ArrayList<Line>();
        private List<Line> endLines = new ArrayList<Line>();

        public GraphRect(double x1, double y1, double x2, double y2) {
            super(x1,y1,x2,y2);
            setFill(FlatColor.RED);
            setStrokeWidth(3);
            setArcWidth(20);
            setArcHeight(20);
        }

        @Override
        public Node setTranslateX(double translateX) {
            for(Line l : startLines) {
                l.setX1(translateX+getWidth()/2);
            }
            for(Line l : endLines) {
                l.setX2(translateX+getWidth()/2);
            }
            return super.setTranslateX(translateX);
        }

        @Override
        public Node setTranslateY(double translateY) {
            for(Line l : startLines) {
                l.setY1(translateY+getWidth()/2);
            }
            for(Line l : endLines) {
                l.setY2(translateY+getWidth()/2);
            }
            return super.setTranslateY(translateY);
        }

        public GraphRect addStartLine(Line line) {
            startLines.add(line);
            return this;
        }
        public GraphRect addEndLine(Line line) {
            endLines.add(line);
            return this;
        }

    }

    private static class Dragger implements Callback<MouseEvent> {
        public Point2D pt;

        @Override
        public void call(MouseEvent event) throws Exception {
            if(event.getSource() instanceof GraphRect) {
                GraphRect r = (GraphRect) event.getSource();
                if(event.getType() == MouseEvent.MousePressed) {
                    pt = event.getPointInSceneCoords();
                }
                if(event.getType() == MouseEvent.MouseDragged) {
                    Point2D.Double dx = new Point2D.Double(
                            event.getPointInSceneCoords().getX()-pt.getX(),
                            event.getPointInSceneCoords().getY()-pt.getY()
                    );
                    pt = event.getPointInSceneCoords();
                    r.setTranslateX(r.getTranslateX()+dx.getX());
                    r.setTranslateY(r.getTranslateY()+dx.getY());
                }
            }
        }
    }
}
