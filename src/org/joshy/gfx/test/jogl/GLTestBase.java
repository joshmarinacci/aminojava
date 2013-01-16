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
 * Date: Apr 29, 2010
 * Time: 12:29:37 PM
 * To change this template use File | Settings | File Templates.
 */
abstract class GLTestBase implements GLEventListener {
    private FPSAnimator animator;
    protected GL2 gl;
    public static final TextureBuffer SCREEN = new TextureBuffer(-1,-1);
    public static final TextureBuffer NONE = new TextureBuffer(-1,-1);

    public static void launch(RenderToTexture1 renderToTexture1) {
        renderToTexture1.launch();
    }

    private JFrame window;
    protected GLU glu;
    private GLCanvas canvas;
    private GLCapabilities caps;

    void launch() {
        window = new JFrame("my crazy cool demo");
        window.setSize(300,300);
        glu = new GLU();
        caps = new GLCapabilities(null);
        canvas = new GLCanvas(caps);
        canvas.addGLEventListener(this);
        window.add(canvas);
        window.setVisible(true);
        window.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
        animator = new FPSAnimator(canvas,60, true);
        animator.setRunAsFastAsPossible(false);
        animator.start();

    }


    public void init(GLAutoDrawable glAutoDrawable) {
        gl = glAutoDrawable.getGL().getGL2();
        init();
    }

    protected abstract void init();

    public void dispose(GLAutoDrawable glAutoDrawable) {
    }

    public void display(GLAutoDrawable glAutoDrawable) {
        display();
    }

    protected abstract void display();

    private final static double NEAR = 0.5; // Z values < NEAR are clipped
    private final static double FAR = 4.0;  // Z values > FAR are clipped
    
    public void reshape(GLAutoDrawable drawable, int i, int i1, int width, int height) {
        GL2 gl = drawable.getGL ().getGL2();

        // We don't need to invoke gl.glViewport(x, y, width, height) because
        // this is done automatically by JOGL prior to invoking reshape().

        // Because the modelview matrix is currently in effect, we need to
        // switch to the projection matrix before we can establish a perspective
        // view.

        gl.glMatrixMode (GL2.GL_PROJECTION);

        // Reset the projection matrix.

        gl.glLoadIdentity ();

        // Establish a perspective view in which any drawing in front of the
        // NEAR clipping plane or behind the FAR clipping plane is discarded.
        // The frustum is assigned the same aspect ratio as the viewport, to
        // prevent distortion.

        float aspect = (float) width/(float) height;
        gl.glFrustum (-aspect, aspect, -1.0f, 1.0f, NEAR, FAR);

        // From now on, we'll work with the modelview matrix.

        gl.glMatrixMode (GL2.GL_MODELVIEW);
    }



    protected TextureBuffer generateFrameBuffer(int width, int height) {
        TextureBuffer tb = new TextureBuffer(width,height);
        u.p("setting up the framebuffer");
        //total framebuffer setup include texture and depth buffer
        //create the framebuffer
        gl.glGenFramebuffers(1, tb.frameBuffer, 0);

        //for reference
        // http://www.gamedev.net/reference/programming/features/fbo1/

        //bind to the framebuffer
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, tb.frameBuffer[0]);
        //generate a depth buffer
        gl.glGenRenderbuffers(1,tb.depthBuffer,0);
        //bind to the depth buffer
        gl.glBindRenderbuffer(GL2.GL_RENDERBUFFER, tb.depthBuffer[0]);
        //allocate storage (256x256)
        gl.glRenderbufferStorage(GL2.GL_RENDERBUFFER, GL2.GL_DEPTH_COMPONENT, width, height);
        //attach the depth buffer to the frame buffer
        gl.glFramebufferRenderbuffer(GL2.GL_FRAMEBUFFER, GL2.GL_DEPTH_ATTACHMENT, GL2.GL_RENDERBUFFER, tb.depthBuffer[0]);

        //create a texture (storage buffer for color)
        gl.glGenTextures(1,tb.img,0);
        //bind to the texture
        gl.glBindTexture(GL2.GL_TEXTURE_2D,tb.img[0]);
        //create the actual texture storage. *must* be same size as the depth buffer above.
        gl.glTexImage2D(GL2.GL_TEXTURE_2D,0,GL2.GL_RGBA8, width, height, 0, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, null);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D,GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D,GL.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D,GL.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);
        gl.glGenerateMipmap(GL2.GL_TEXTURE_2D);

        // attach the texture to the framebuffer
        gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0, GL.GL_TEXTURE_2D, tb.img[0],0);

        //check that everything went okay
        //gl.glCheckFramebufferStatus(GL2.GL_FRAMEBUFFER);
        checkFramebufferStatus();
        return tb;
    }

    private void checkFramebufferStatus()    {
        int status = gl.glCheckFramebufferStatus(GL.GL_FRAMEBUFFER);
        switch(status)
        {
            case GL.GL_FRAMEBUFFER_COMPLETE:
                System.out.println("FRAMEBUFFER_COMPLETE");
                // All good
                break;

            case GL.GL_FRAMEBUFFER_UNSUPPORTED:
                throw new RuntimeException("Frame buffer extension not supported.");
            case GL.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
                throw new RuntimeException("Incompleat attachments");
            case GL.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS:
                throw new RuntimeException("Incompleat dimensions");
            //case GL.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
            //    throw new RuntimeException("Incompleat drw buffer");
            //case GL.GL_FRAMEBUFFER_INCOMPLETE_DUPLICATE_ATTACHMENT:
            //    throw new RuntimeException("Incompleat duplciate attachments");
            case GL.GL_FRAMEBUFFER_INCOMPLETE_FORMATS:
                throw new RuntimeException("incompleate foramts");
            case GL.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
                throw new RuntimeException("incompleate attachments");
            //case GL.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
            //   throw new RuntimeException("Incompleate read buffer");

            default:
                throw new RuntimeException("Logic error");
        }
    }

    protected void translate(double x, double y, double z) {
        gl.glTranslated(x,y,z);
    }

    protected void setDrawTarget(TextureBuffer buffer) {
        if(buffer == SCREEN) {
            gl.glPopAttrib();
            gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER,0);
        } else {
            gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER,buffer.frameBuffer[0]);
            gl.glPushAttrib(GL2.GL_VIEWPORT_BIT);
            gl.glViewport(0,0,buffer.width,buffer.height);
        }
    }

    protected void positionCamera(float x, float y, float z) {
        glu.gluLookAt (0.0, 0.0, 2.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);
    }

    protected void transformIdentity() {
        gl.glLoadIdentity();
    }

    protected void clearColor() {
        gl.glClear (GL.GL_COLOR_BUFFER_BIT);
    }

    protected void drawVertex(double x, double y, double z) {
        gl.glVertex3d (x,y,z);
    }

    protected void setColor(double red, double green, double blue) {
        gl.glColor3d(red,green,blue);//1.0f, 0.0f, 0.0f); // red at the top
    }

    protected void beginTriangles() {
        gl.glShadeModel (GL2.GL_SMOOTH);
        //gl.glScaled(5,5,0);
        gl.glBegin (GL.GL_TRIANGLES);
    }

    protected void endTriangles() {
        gl.glEnd ();
        //gl.glScaled(1.0/5.0,1.0/5.0,0);
    }

    protected void setTextureCoords(double tx, double ty) {
        gl.glTexCoord2d(tx,ty);
    }

    protected void setTexture(TextureBuffer buffer) {
        if(buffer == NONE) {
            gl.glDisable(GL.GL_TEXTURE_2D);
        } else {
            //set bg color to white
            gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            gl.glEnable(GL.GL_BLEND);
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
            gl.glEnable(GL2.GL_ALPHA_TEST);
            gl.glAlphaFunc(GL.GL_GREATER, 0);
            gl.glEnable(GL.GL_TEXTURE_2D);
            gl.glTexEnvf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
            gl.glBindTexture(GL.GL_TEXTURE_2D, buffer.img[0]);
        }
    }

    protected void beginQuads() {
        gl.glPushMatrix();
        gl.glBegin(GL2.GL_QUADS);
    }

    protected void endQuads() {
        gl.glEnd();
        gl.glPopMatrix();
    }
}
