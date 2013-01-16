package org.joshy.gfx.test.jogl;

import com.sun.opengl.util.BufferUtil;
import org.joshy.gfx.Core;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.draw.Transform;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.stage.OrthoCamera;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.u;

import javax.media.opengl.*;
import java.nio.ByteBuffer;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Apr 27, 2010
 * Time: 9:25:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class RenderBufferTest implements Runnable {
    public static void main(String ... args) throws Exception, InterruptedException {
        Core.setUseJOGL(true);
        Core.init();
        Core.getShared().defer(new RenderBufferTest());
    }

    public void run() {
        Stage stage = Stage.createStage();
        OrthoCamera cam = new OrthoCamera();
        stage.setCamera(cam);
        
        stage.setContent(new BufferTest());
        //stage.setContent(new Rectangle3D().setWidth(100).setHeight(100).setFill(FlatColor.BLACK));
        stage.setWidth(640);
        stage.setHeight(480);
    }

    private static class B2 extends Node {

        @Override
        public void draw(GFX g) {

        }

        @Override
        public Bounds getVisualBounds() {
            return new Bounds(0,0,100,100);
        }

        @Override
        public Bounds getInputBounds() {
            return getVisualBounds();
        }
    }

    private static class BufferTest extends Node {
        private int[] blurTexture = new int[1]; // An Unsigned Int To Store The Texture Number
        private boolean init = true;
        private GLPbuffer buffer;

        @Override
        public void draw(GFX g) {
            GLAutoDrawable gl = g.getDrawable();

            if(init) {
                blurTexture[0] = emptyTexture(gl.getGL());
                GLDrawableFactory fact = GLDrawableFactory.getFactory(gl.getGLProfile());
                buffer = fact.createGLPbuffer(gl.getChosenGLCapabilities(),null,128,128,gl.getContext());
                init = false;
                u.p("created pbuffer");
            }

            //renderToTexture(g,gl.getGL());  // Render To A Texture
            //renderToTexture2(g,(GL2)gl.getGL());
            //processHelix(g);
            //drawBlur((GL2)gl.getGL(),3,0.02f);
            //drawBlur2((GL2)gl.getGL());
            drawRect((GL2)gl.getGL(),5,5,100,100);
            buffer.getContext().makeCurrent();
            drawRect((GL2)buffer.getGL(),5,5,100,100);
            gl.getContext().makeCurrent();
            drawBuffer((GL2)gl.getGL());

            //GL2 g2 = (GL2) gl.getGL();

        }

        private void drawBuffer(GL2 gl) {
            gl.glPushMatrix();
            //translate
            gl.glTranslated(100,0,0);
            //set bg color to white
            gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

            //enable the texture
            //image.texture.enable();
            gl.glEnable(GL.GL_BLEND);
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
            gl.glEnable(GL2.GL_ALPHA_TEST);
            gl.glAlphaFunc(GL.GL_GREATER, 0);
            gl.glEnable(GL.GL_TEXTURE_2D);
            gl.glTexEnvf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
            //image.texture.bind();
            buffer.bindTexture();

            double x = 0;
            double y = 0;
            double w = 100;
            double h = 100;
            double tcl = 0;
            //draw a rectangle
            gl.glTranslated(x, y, 0);
            gl.glBegin(GL2.GL_QUADS);
            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(0, 0, 0);
            gl.glTexCoord2d(1,0);
            gl.glVertex3d(w,0,0);///*image.texture.getWidth()*/, 0, 0);
            gl.glTexCoord2d(1,1);//tc.right(), tc.bottom());
            gl.glVertex3d(w,h,0);//image.texture.getWidth(), image.texture.getHeight(), 0);
            gl.glTexCoord2d(0,1);//tc.left(), tc.bottom());
            gl.glVertex3d(0, h,0);//image.texture.getHeight(), 0);
            gl.glEnd();

            //image.texture.disable();
            //buffer.releaseTexture();
            gl.glPopMatrix();
        }

        private void renderToTexture2(GFX g, GL2 gl2) {
            
        }

        public void drawRect(GL2 gl, double x, double y, double width, double height) {
            gl.glColor4d(0.0,0.8,0.0,1.0);
            gl.glTranslatef((float) x, (float) y, 0);
            gl.glBegin(GL.GL_LINE_STRIP);
            gl.glVertex2f(0, 0);
            gl.glVertex2d(width, 0);
            gl.glVertex2d(width, height);
            gl.glVertex2d(0, height);
            gl.glVertex2d(0, 0);
            gl.glEnd();
            gl.glTranslatef(-(float) x, -(float) y, 0);
        }

        private void drawBlur2(GL2 gl) {
            gl.glPushMatrix();
            gl.glTranslated(100,100,0);

            // set up for drawing the texture
            // Disable AutoTexture Coordinates
            gl.glDisable(GL2.GL_TEXTURE_GEN_S);
            gl.glDisable(GL2.GL_TEXTURE_GEN_T);

            gl.glEnable(GL.GL_TEXTURE_2D);  // Enable 2D Texture Mapping
            gl.glDisable(GL.GL_DEPTH_TEST);  // Disable Depth Testing
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);  // Set Blending Mode
            gl.glEnable(GL.GL_BLEND);  // Enable Blending
            gl.glBindTexture(GL.GL_TEXTURE_2D, blurTexture[0]); // Bind To The Blur Texture

            for(int i=0; i<10; i++) {
                gl.glTranslated(50,0,0);
                gl.glBegin(GL2.GL_QUADS);  // Begin Drawing Quads

                float spost = 0.0f;  // Starting Texture Coordinate Offset
                gl.glColor4f(1.0f, 1.0f, 1.0f, 0.2f); // Set The Alpha Value (Starts At 0.2)
                gl.glTexCoord2f(0 + spost, 1 - spost); // Texture Coordinate  ( 0, 1 )
                gl.glVertex2f(0, 0);  // First Vertex    (   0,   0 )

                // Texture Coordinate  ( 0, 0 )
                gl.glTexCoord2f(0 + spost, 0 + spost);
                gl.glVertex2f(0, 100+i*2);  // Second Vertex  (   0, 480 )

                // Texture Coordinate  ( 1, 0 )
                gl.glTexCoord2f(1 - spost, 0 + spost);
                gl.glVertex2f(100, 100);  // Third Vertex    ( 640, 480 )

                // Texture Coordinate  ( 1, 1 )
                gl.glTexCoord2f(1 - spost, 1 - spost);
                gl.glVertex2f(100, 0);  // Fourth Vertex  ( 640,   0 )

                // Gradually Increase spost (Zooming Closer To Texture Center)
                //spost += inc;
                // Gradually Decrease alpha (Gradually Fading Image Out)
                //alpha = alpha - alphainc;


                gl.glEnd();        // Done Drawing Quads
            }
            gl.glEnable(GL.GL_DEPTH_TEST);  // Enable Depth Testing
            gl.glDisable(GL.GL_TEXTURE_2D);  // Disable 2D Texture Mapping
            gl.glDisable(GL.GL_TEXTURE_2D);  // Disable 2D Texture Mapping
            gl.glDisable(GL.GL_TEXTURE_2D);  // Disable 2D Texture Mapping
            gl.glDisable(GL.GL_BLEND);  // Disable Blending
            gl.glBindTexture(GL.GL_TEXTURE_2D, 0);  // Unbind The Blur Texture
            gl.glPopMatrix();
        }

        private void drawBlur(GL2 gl, int times, float inc)  // Draw The Blurred Image
        {
        gl.glPushMatrix();
        float spost = 0.0f;  // Starting Texture Coordinate Offset
        float alpha = 0.2f;  // Starting Alpha Value

        // Disable AutoTexture Coordinates
        gl.glDisable(GL2.GL_TEXTURE_GEN_S);
        gl.glDisable(GL2.GL_TEXTURE_GEN_T);

        gl.glEnable(GL.GL_TEXTURE_2D);  // Enable 2D Texture Mapping
        gl.glDisable(GL.GL_DEPTH_TEST);  // Disable Depth Testing
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);  // Set Blending Mode
        gl.glEnable(GL.GL_BLEND);  // Enable Blending
        gl.glBindTexture(GL.GL_TEXTURE_2D, blurTexture[0]); // Bind To The Blur Texture
        //viewOrtho(gl);  // Switch To An Ortho View

        float alphainc = alpha / times;  // alphainc=0.2f / Times To Render Blur

        gl.glBegin(GL2.GL_QUADS);  // Begin Drawing Quads
        for (int num = 0; num < times; num++)  // Number Of Times To Render Blur
        {
            gl.glColor4f(1.0f, 1.0f, 1.0f, alpha); // Set The Alpha Value (Starts At 0.2)
            gl.glTexCoord2f(0 + spost, 1 - spost); // Texture Coordinate  ( 0, 1 )
            gl.glVertex2f(0, 0);  // First Vertex    (   0,   0 )

            // Texture Coordinate  ( 0, 0 )
            gl.glTexCoord2f(0 + spost, 0 + spost);
            gl.glVertex2f(0, 480);  // Second Vertex  (   0, 480 )

            // Texture Coordinate  ( 1, 0 )
            gl.glTexCoord2f(1 - spost, 0 + spost);
            gl.glVertex2f(640, 480);  // Third Vertex    ( 640, 480 )

            // Texture Coordinate  ( 1, 1 )
            gl.glTexCoord2f(1 - spost, 1 - spost);
            gl.glVertex2f(640, 0);  // Fourth Vertex  ( 640,   0 )

            // Gradually Increase spost (Zooming Closer To Texture Center)
            spost += inc;
            // Gradually Decrease alpha (Gradually Fading Image Out)
            alpha = alpha - alphainc;
        }
        gl.glEnd();        // Done Drawing Quads

        //viewPerspective(gl);  // Switch To A Perspective View

        gl.glEnable(GL.GL_DEPTH_TEST);  // Enable Depth Testing
        gl.glDisable(GL.GL_TEXTURE_2D);  // Disable 2D Texture Mapping
        gl.glDisable(GL.GL_TEXTURE_2D);  // Disable 2D Texture Mapping
        gl.glDisable(GL.GL_TEXTURE_2D);  // Disable 2D Texture Mapping
        gl.glDisable(GL.GL_BLEND);  // Disable Blending
        gl.glBindTexture(GL.GL_TEXTURE_2D, 0);  // Unbind The Blur Texture
        gl.glPopMatrix();
    }


        @Override
        public Bounds getVisualBounds() {
            return new Bounds(0,0,100,100);
        }

        @Override
        public Bounds getInputBounds() {
            return new Bounds(0,0,100,100);
        }

        private int emptyTexture(GL gl) {  // Create An Empty Texture
            // Create Storage Space For Texture Data (128x128x4)
            ByteBuffer data = BufferUtil.newByteBuffer(128 * 128 * 4);
            data.limit(data.capacity());

            int[] txtnumber = new int[1];
            gl.glGenTextures(1, txtnumber, 0);  // Create 1 Texture
            gl.glBindTexture(GL.GL_TEXTURE_2D, txtnumber[0]);  // Bind The Texture

            // Build Texture Using Information In data
            gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, 4, 128, 128, 0,
                    GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, data);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
            gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

            return txtnumber[0];  // Return The Texture ID
        }

    private void renderToTexture(GFX g, GL gl) // Renders To A Texture
    {
        gl.glViewport(0, 0, 128, 128);    // Set Our Viewport (Match Texture Size)

        processHelix(g);      // Render The Helix

        // Bind To The Blur Texture
        gl.glBindTexture(GL.GL_TEXTURE_2D, blurTexture[0]);

        // Copy Our ViewPort To The Blur Texture (From 0,0 To 128,128... No Border)
        gl.glCopyTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_LUMINANCE, 0, 0, 128, 128, 0);

        gl.glClearColor(0.0f, 0.0f, 0.5f, 0.5f); // Set The Clear Color To Medium Blue
        // Clear The Screen And Depth Buffer
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

        gl.glViewport(0, 0, 640, 480);  // Set Viewport (0,0 to 640x480)

        gl.glClearColor(1f, 1f, 1f, 0);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT|GL.GL_DEPTH_BUFFER_BIT);
        //gl.glOrtho(0.0, w, h, 0.0, -100.0, 100.0);
        //gl.glLineWidth(1.0f);

    }

    private void processHelix(GFX g) {
        g.setPaint(FlatColor.BLUE);
        g.drawRect(0,0,127,127);
        
        g.push();
        for(int i=0; i<10; i++) {
            g.rotate(15, Transform.Z_AXIS);
            g.setPaint(FlatColor.RED);
            g.drawRect(30,30,70,90);
        }
        g.pop();
    }
}
}
