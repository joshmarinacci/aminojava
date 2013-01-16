package org.joshy.gfx.node.geom3d;

import org.joshy.gfx.draw.Transform;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Apr 17, 2010
 * Time: 10:14:12 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Shape3D extends Node {
    private Transform axis = Transform.Z_AXIS;
    private double rotation = 0;
    private double opacity = 1.0;
    
    public double getOpacity() {
        return opacity;
    }

    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    @Override
    public Bounds getVisualBounds() {
        return new Bounds(0,0,100,100);
    }

    @Override
    public Bounds getInputBounds() {
        return new Bounds(0,0,100,100);
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
