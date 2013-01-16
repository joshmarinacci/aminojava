package org.joshy.gfx.stage;

import org.joshy.gfx.Core;
import org.joshy.gfx.node.Group;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.shape.Rectangle;
import org.joshy.gfx.stage.testing.TestingStage;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Jun 5, 2010
 * Time: 11:34:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class AWTEventPublisherTest {
    private TestingStage stage;
    private EventPublisher eventPublisher;

    @Before public void doSetup() throws Exception, InterruptedException {
        Core.setTesting(true);
        Core.init();
        Core.getShared();
        stage = new TestingStage();
        stage.setWidth(100);
        stage.setHeight(100);
        eventPublisher = new EventPublisher(stage.getRoot());
    }

    @Test public void basicFindNode() {
        Rectangle rect = new Rectangle().setX(0).setY(0).setWidth(100).setHeight(100);
        stage.setContent(rect);
        Node node = eventPublisher.findTopNode(50, 50);
        assertTrue(rect == node);
    }

    @Test public void scaledFind() {
        Rectangle rect = new Rectangle().setX(0).setY(0).setWidth(100).setHeight(100);
        Group g = new Group();
        g.add(rect);
        stage.setContent(g);

        //check inside rect
        assertTrue(rect == eventPublisher.findTopNode(50,50));

        //check outside rect
        assertFalse(rect == eventPublisher.findTopNode(105,50));

        //move the group so that the rect is inside again
        g.setTranslateX(50);
        assertTrue(rect == eventPublisher.findTopNode(105,50));

        
    }




    private static void p(String s) {
        System.out.println(s);
    }
}
