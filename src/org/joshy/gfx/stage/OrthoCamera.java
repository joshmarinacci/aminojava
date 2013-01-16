package org.joshy.gfx.stage;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Apr 25, 2010
 * Time: 10:29:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class OrthoCamera extends Camera {
    @Override
    public void configureDisplay(GL2 gl, Stage stage, double w, double h) {
        //u.p("ortho configure");
        gl.glClearColor(1f, 1f, 1f, 0);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT|GL.GL_DEPTH_BUFFER_BIT);
        gl.glOrtho(0.0, w, h, 0.0, -100.0, 100.0);
        gl.glLineWidth(1.0f);
    }
}
