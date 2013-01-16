package org.joshy.gfx.stage;

import org.joshy.gfx.stage.jogl.JOGLStage;

import javax.media.opengl.GL2;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Apr 25, 2010
 * Time: 10:27:45 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Camera {
    public abstract void configureDisplay(GL2 gl, Stage stage, double w, double h);
}
