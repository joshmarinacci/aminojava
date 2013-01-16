package org.joshy.gfx.test.jogl;

import com.sun.opengl.util.FPSAnimator;
import org.joshy.gfx.util.u;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Apr 28, 2010
 * Time: 10:49:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class RenderToTexture1 extends GLTestBase {
    private TextureBuffer buffer;

    public static void main(String ... args) {
        GLTestBase.launch(new RenderToTexture1());
    }

    public void init() {
        //setTitle("My Demo App");
        //setFPS(60);
        //setMultiSamples(4);
        //start();
        //setPolygonSmooth(true);

        
        // Enable polygon antialiasing.
         gl.glEnable (GL.GL_BLEND);
         gl.glBlendFunc (GL.GL_SRC_ALPHA_SATURATE, GL.GL_ONE);
         gl.glEnable (GL2.GL_POLYGON_SMOOTH);
         gl.glHint (GL2.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);
         gl.glShadeModel(GL2.GL_SMOOTH);
        //set up a frame buffer
        buffer = generateFrameBuffer(256,256);
    }

    public void display() {
        clearColor();
        transformIdentity();
        positionCamera(0,0,2);

        //draw on the buffer
        setDrawTarget(buffer);
        
        //draw a colored triangle into the buffer
        beginTriangles();
        setColor(1,0,0);  drawVertex(0,1,1);
        setColor(0,1,0);  drawVertex(-1,-1,1);
        setColor(0,0,1);  drawVertex(1,-1,1);
        endTriangles();


        //switch back to the screen
        setDrawTarget(SCREEN);

        //draw full polygon
        beginTriangles();
        setColor(1,0,0);  drawVertex(0,1,1);
        setColor(0,1,0);  drawVertex(-1,-1,1);
        setColor(0,0,1);  drawVertex(1,-1,1);
        endTriangles();

        //draw quads using the buffer texture
        setTexture(buffer);
        for(int i=0; i<4; i++) {
            translate(1.0*i,1.0*i,0);
            beginQuads();
            setTextureCoords(0,0); drawVertex(0,0,0);
            setTextureCoords(1,0); drawVertex(1,0,0);
            setTextureCoords(1,1); drawVertex(1,1,0);
            setTextureCoords(0,1); drawVertex(0,1,0);
            endQuads();
            translate(-1.0*i,-1.0*i,0);
        }
        setTexture(NONE);

    }

}

