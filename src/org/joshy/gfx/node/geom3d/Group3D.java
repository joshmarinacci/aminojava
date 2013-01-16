package org.joshy.gfx.node.geom3d;

import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.draw.Transform;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import java.util.ArrayList;
import java.util.List;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: Apr 16, 2010
* Time: 7:05:35 PM
* To change this template use File | Settings | File Templates.
*/
public class Group3D extends Node {
    private List<Node> children = new ArrayList<Node>();
    private Transform axis = Transform.Z_AXIS;
    private double rotation = 0;

    @Override
    public void draw(GFX g) {
        GLAutoDrawable drawable = g.getDrawable();
        GL2 gl = drawable.getGL().getGL2();
        gl.glTranslated(getTranslateX(),getTranslateY(),getTranslateZ());
        gl.glRotated(getRotation(),getRotationAxis().getX(),getRotationAxis().getY(),getRotationAxis().getZ());
        for(Node n : children) {
            n.draw(g);
        }
        gl.glRotated(-getRotation(),getRotationAxis().getX(),getRotationAxis().getY(),getRotationAxis().getZ());
        gl.glTranslated(-getTranslateX(),-getTranslateY(),-getTranslateZ());
    }

    @Override
    public Bounds getVisualBounds() {
        return new Bounds(0,0,100,100);
    }

    @Override
    public Bounds getInputBounds() {
        return new Bounds(0,0,100,100);
    }

    public Group3D add(Node... nodes) {
        for(Node n : nodes) {
            this.children.add(n);
        }
        return this;
    }

    public void removeAll() {
        this.children.clear();
    }

    public void setRotationAxis(Transform axis) {
        this.axis = axis;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public double getRotation() {
        return rotation;
    }

    public Transform getRotationAxis() {
        return axis;
    }
}
