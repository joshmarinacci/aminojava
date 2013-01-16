package org.joshy.gfx.stage.testing;

import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.Parent;
import org.joshy.gfx.node.layout.Container;
import org.joshy.gfx.stage.Camera;
import org.joshy.gfx.stage.Stage;

/**
 * A stage implementation just for running unit tests. It is headless
 */
public class TestingStage extends Stage {
    private Node content;

    private Container root = new Container() {
        @Override
        public void draw(GFX g) {
            
        }
    };
    private String id;

    @Override
    public void setContent(Node node) {
        content = node;
        root.add(node);
    }

    @Override
    public Node getContent() {
        return content;
    }

    @Override
    public void setCamera(Camera camera) {
    }

    @Override
    public void setWidth(double width) {
    }

    @Override
    public double getWidth() {
        return 0;
    }

    @Override
    public void setHeight(double height) {
    }

    @Override
    public double getHeight() {
        return 0;
    }

    @Override
    public double getX() {
        return 0;
    }

    @Override
    public void setX(double v) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double getY() {
        return 0;
    }

    @Override
    public void setY(double v) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setMinimumWidth(double width) {
    }

    @Override
    public void setMinimumHeight(double height) {
    }

    @Override
    public Container getPopupLayer() {
        return null;
    }

    @Override
    public Object getNativeWindow() {
        return null;
    }

    @Override
    public void setTitle(CharSequence title) {
    }

    @Override
    public void setUndecorated(boolean undecorated) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void hide() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void raiseToTop() {

    }

    @Override
    public Stage setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public void centerOnScreen() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setFullScreen(boolean fullScreen) {

    }

    @Override
    public void setOpacity(double v) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setAlwaysOnTop(boolean b) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Parent getRoot() {
        return root;
    }
}
