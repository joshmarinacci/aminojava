package org.joshy.gfx.stage;

import org.joshy.gfx.draw.FlatColor;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

public class PerspectiveCamera extends Camera {

    private FlatColor backgroundColor = FlatColor.BLACK;

    @Override
    public void configureDisplay(GL2 gl, Stage stage, double w, double h) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClearColor(
                (float)backgroundColor.getRed(),
                (float)backgroundColor.getGreen(),
                (float)backgroundColor.getBlue(),
                (float)backgroundColor.getAlpha());
        gl.glClear(GL.GL_COLOR_BUFFER_BIT|GL.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode (GL2.GL_PROJECTION);
        gl.glLoadIdentity ();
        float aspect = (float) w/(float) h;
        GLU glu = new GLU ();
        final double NEAR = 1.0; // Z values < NEAR are clipped
        final double FAR = 8.0;  // Z values > FAR are clipped
        float s = 100;
        gl.glFrustum (-aspect*s, aspect*s, -1.0f*s, 1.0f*s, NEAR*s, FAR*s);
        //u.p("near = " + (NEAR * s) + " far = " + (FAR * s));

        // From now on, we'll work with the modelview matrix.
        gl.glMatrixMode(GL2.GL_MODELVIEW);

        gl.glLoadIdentity ();
        glu.gluLookAt (0.0, 0.0, 2.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
        float rotAngle = 0;
        gl.glRotatef (rotAngle, 0.0f, 1.0f, 0.0f);
        gl.glTranslated(0,0,-235);
    }

    public PerspectiveCamera setBackground(FlatColor color) {
        this.backgroundColor = color;
        return this;
    }
}
