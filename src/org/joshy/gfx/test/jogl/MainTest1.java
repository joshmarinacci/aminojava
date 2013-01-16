package org.joshy.gfx.test.jogl;

import com.sun.javafx.newt.opengl.GLWindow;
import com.sun.opengl.util.FPSAnimator;
import com.sun.opengl.util.texture.TextureIO;
import com.sun.opengl.util.texture.Texture;
import org.joshy.gfx.stage.AWTEventPublisher;
import org.joshy.gfx.util.u;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

public class MainTest1 implements GLEventListener
{
    private static int WINDOW_WIDTH = 640;
    private static int WINDOW_HEIGHT = 480;

    private static final int RENDERBUFFER_WIDTH = 512;
    private static final int RENDERBUFFER_HEIGHT = 512;

    private JFrame window;
    private GL2 gl;
    private GLU glu;

    private int[] frameBuffer = new int[1];
    private int[] textureId = new int[1];
    private Texture regularTexture;
    private GLCapabilities caps;
    private GLCanvas canvas;
    private FPSAnimator animator;


    public MainTest1()
    {
        window = new JFrame("JOGL Ext framebuffer object extnesion test");
        window.setSize(WINDOW_WIDTH,WINDOW_HEIGHT);
        //window = new JOGLWindow("JOGL EXT_framebuffer_object extension test", WINDOW_WIDTH, WINDOW_HEIGHT, false);
        //window.addJOGLContextThread(this);
        glu = new GLU();

        caps = new GLCapabilities(null);
        //turn on anti-aliasing full screen
        caps.setSampleBuffers(true);
        caps.setNumSamples(4);

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

        /*window.activate();

        while(window.isActivated())
        {
            window.display();

            try
            {
                Thread.sleep(1);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }*/
    }


    public void init(GLAutoDrawable glAutoDrawable)
    {
        this.gl = (GL2) glAutoDrawable.getGL();

        gl.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glEnable(GL.GL_DEPTH_TEST);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        gl.glGenFramebuffers(1, frameBuffer, 0);

        // Check for errors
        checkFramebufferStatus();

        // Create dynamic texture
        gl.glGenTextures(1, textureId, 0);
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureId[0]);
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, GL.GL_DEPTH_COMPONENT16, RENDERBUFFER_WIDTH, RENDERBUFFER_HEIGHT, 0, GL2.GL_DEPTH_COMPONENT, GL.GL_UNSIGNED_BYTE, null);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR );
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR );
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL2.GL_TEXTURE_COMPARE_MODE, GL.GL_NONE);

        // Load a regulat texture
        //File file = new File("C:\\dev\\projects\\JOGLShadowMapping\\src\\NeHe.png");
        File file = new File("smudge.png");
        u.p("file = " + file.getAbsolutePath());

        try
        {
            regularTexture = TextureIO.newTexture(file, true);
            regularTexture.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
            regularTexture.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void dispose(GLAutoDrawable glAutoDrawable) {
        
    }


    public void display(GLAutoDrawable glAutoDrawable)
    {
        this.gl = (GL2) glAutoDrawable.getGL();

        /* Offscreen rendering */
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, frameBuffer[0]);
        gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_DEPTH_ATTACHMENT, GL.GL_TEXTURE_2D, textureId[0], 0);

        gl.glDrawBuffer(GL.GL_NONE);
        gl.glReadBuffer(GL.GL_NONE);

        checkFramebufferStatus();

        gl.glViewport(0, 0, RENDERBUFFER_WIDTH, RENDERBUFFER_HEIGHT);
	    gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

	    gl.glClear(GL.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL2.GL_MODELVIEW);
	    gl.glLoadIdentity();

        //gl.glBindTexture(GL.GL_TEXTURE_2D, regularTexture.getTextureObject());
        gl.glColor4d(1.0,0,0,1.0);
        //gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

        // Render
        gl.glBegin(GL.GL_TRIANGLES);
        //gl.glTexCoord2f(0, 0);
        gl.glVertex3f(-2, 0, -6);
        //gl.glTexCoord2f(0.5f, 1);
        gl.glVertex3f(0, 2, -6);
        //gl.glTexCoord2f(1, 0);
        gl.glVertex3f(2, 0, -6);
        gl.glEnd();

        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);




        /* On-screen rendering */
        gl.glViewport(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
	    gl.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
	    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT );

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        //josh added
        gl.glColor4d(1,1,1,1.0);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL2.GL_ALPHA_TEST);
        gl.glAlphaFunc(GL.GL_GREATER, 0);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glTexEnvf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
        //end josh added

	    gl.glBindTexture(GL.GL_TEXTURE_2D, textureId[0]);

        for(int i=0; i<2; i++) {
            gl.glTranslated(0.5,0.5,0);
            // Render qaud
            gl.glBegin(GL.GL_TRIANGLES);

            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-2, 0, -6);
            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(-2, 2, -6);
            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(2, 2, -6);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(2, 2, -6);
            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(2, 0, -6);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(-2, 0, -6);
            gl.glEnd();
        }
    }

    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height)
    {
        this.gl = (GL2) glAutoDrawable.getGL();

        WINDOW_WIDTH = width;
        WINDOW_HEIGHT = height;

        // Calculate aspect ratio
        if (WINDOW_HEIGHT <= 0)
        {
            WINDOW_HEIGHT = 1;
        }

        final float aspect = (float) WINDOW_WIDTH / (float) WINDOW_HEIGHT;

        // Create projection matrix
        gl.glMatrixMode(GL2.GL_PROJECTION);
        glu.gluPerspective(45.0, aspect, 0.1, 10.0);

   //     gl.glMatrixMode(GL.GL_MODELVIEW);
   //     gl.glLoadIdentity();
    }

    public void displayChanged(GLAutoDrawable glAutoDrawable, boolean b, boolean b1)
    {
        this.gl = (GL2) glAutoDrawable.getGL();
    }


    private void checkFramebufferStatus()
    {
        int status = gl.glCheckFramebufferStatus(GL.GL_FRAMEBUFFER);
        switch(status)
        {
            case GL.GL_FRAMEBUFFER_COMPLETE:
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


    public static void main(String[] args)
    {
        new MainTest1();
    }
}




