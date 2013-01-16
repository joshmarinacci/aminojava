package org.joshy.gfx.test.jogl;

import com.sun.opengl.util.FPSAnimator;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;
import org.joshy.gfx.draw.Image;
import org.joshy.gfx.stage.jogl.JOGLImage;
import org.joshy.gfx.util.u;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Apr 28, 2010
 * Time: 7:02:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainTest2 implements GLEventListener {
    private JFrame window;
    private GLCapabilities caps;
    private GLCanvas canvas;
    private FPSAnimator animator;
    private GL2 gl;
    private int[] frameBuffer = new int[1];
    private int[] depthBuffer = new int[1];
    private int[] img = new int[1];
    private int WINDOW_WIDTH;
    private int WINDOW_HEIGHT;
    private GLU glu;
    private Texture regularTexture;


    public static void main(String ... args) {
        new MainTest2();
    }

    public MainTest2() {
        window = new JFrame("my crazy cool demo");
        window.setSize(300,300);
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

    }

    public void init(GLAutoDrawable glAutoDrawable) {
        this.gl = (GL2) glAutoDrawable.getGL();


        GL2 gl = glAutoDrawable.getGL ().getGL2();
         int buf[] = new int[1];
         int sbuf[] = new int[1];
         gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
         gl.glGetIntegerv(GL.GL_SAMPLE_BUFFERS, buf, 0);
         System.out.println("number of sample buffers is " + buf[0]);
         gl.glGetIntegerv(GL.GL_SAMPLES, sbuf, 0);
         System.out.println("number of samples is " + sbuf[0]);
        // Enable polygon antialiasing.
         gl.glEnable (GL.GL_BLEND);
         gl.glBlendFunc (GL.GL_SRC_ALPHA_SATURATE, GL.GL_ONE);
         gl.glEnable (GL2.GL_POLYGON_SMOOTH);
         gl.glHint (GL2.GL_POLYGON_SMOOTH_HINT, GL.GL_NICEST);
         gl.glShadeModel(GL2.GL_SMOOTH);

        /*
        //regular setup
        glAutoDrawable.getGL().setSwapInterval(1);
        glAutoDrawable.setAutoSwapBufferMode(true);
        //turn on anti-aliasing for smooth lines
        gl.glEnable(GL.GL_LINE_SMOOTH);
        gl.glEnable(GL2.GL_POLYGON_SMOOTH);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glBlendFunc(GL.GL_LINE_SMOOTH_HINT, GL.GL_DONT_CARE);


        //regular setup
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();

        u.p("initting");*/
        setupFramebuffer();

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

    private void setupFramebuffer() {
        u.p("setting up the framebuffer");
        //total framebuffer setup include texture and depth buffer
        //create the framebuffer
        gl.glGenFramebuffers(1, frameBuffer, 0);

        //for reference
        // http://www.gamedev.net/reference/programming/features/fbo1/

        //bind to the framebuffer
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, frameBuffer[0]);
        //generate a depth buffer
        gl.glGenRenderbuffers(1,depthBuffer,0);
        //bind to the depth buffer
        gl.glBindRenderbuffer(GL2.GL_RENDERBUFFER, depthBuffer[0]);
        //allocate storage (256x256)
        gl.glRenderbufferStorage(GL2.GL_RENDERBUFFER, GL2.GL_DEPTH_COMPONENT, 256,256);
        //attach the depth buffer to the frame buffer
        gl.glFramebufferRenderbuffer(GL2.GL_FRAMEBUFFER, GL2.GL_DEPTH_ATTACHMENT, GL2.GL_RENDERBUFFER, depthBuffer[0]);

        //create a texture (storage buffer for color)
        gl.glGenTextures(1,img,0);
        //bind to the texture
        gl.glBindTexture(GL2.GL_TEXTURE_2D,img[0]);
        //create the actual texture storage. *must* be same size as the depth buffer above.
        gl.glTexImage2D(GL2.GL_TEXTURE_2D,0,GL2.GL_RGBA8, 256, 256, 0, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, null);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D,GL.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL2.GL_TEXTURE_2D,GL.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D,GL.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D,GL.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);
        gl.glGenerateMipmap(GL2.GL_TEXTURE_2D);

        // attach the texture to the framebuffer
        gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0, GL.GL_TEXTURE_2D, img[0],0);

        //check that everything went okay
        //gl.glCheckFramebufferStatus(GL2.GL_FRAMEBUFFER);
        checkFramebufferStatus();


        //glGenTextures(1, &img);
//glBindTexture(GL_TEXTURE_2D, img);
//glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8,  width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);
//glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
//glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
//glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
//glGenerateMipmapEXT(GL_TEXTURE_2D);
    }


    public void display(GLAutoDrawable glAutoDrawable) {
        this.gl = (GL2) glAutoDrawable.getGL();

        // Clear the drawing surface to the background color, which defaults to
        // black.

        gl.glClear (GL.GL_COLOR_BUFFER_BIT);

        // Reset the modelview matrix.

        gl.glLoadIdentity ();

        // The camera is currently positioned at the (0, 0, 0) origin, its lens
        // is pointing along the negative Z axis (0, 0, -1) into the screen, and
        // its orientation up-vector is (0, 1, 0). The following call positions
        // the camera at (0, 0, 2), points the lens towards the origin, and
        // keeps the same up-vector.

        glu.gluLookAt (0.0, 0.0, 2.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);


        /*
        //normal setup from bedrock
        //gl.glMatrixMode(GL2ES1.GL_PROJECTION);
        //gl.glLoadIdentity();
        final float aspect = (float) WINDOW_WIDTH / (float) WINDOW_HEIGHT;

        // Create projection matrix
        gl.glMatrixMode(GL2.GL_PROJECTION);
        glu.gluPerspective(45.0, (float)(640.0/480.0), 0.1, 10.0);

        gl.glClearColor(1f, 1f, 1f, 0);
        gl.glClear(GL.GL_COLOR_BUFFER_BIT|GL.GL_DEPTH_BUFFER_BIT);
        // this camera should give us a 3d perspective view that is
        // still pixel matching when objects are at z=0
        double w = window.getWidth();
        double h = window.getHeight()-window.getInsets().top;
        double front = 100;
        double back = 500;
        //gl.glFrustum( -w/2, w/2, h/2, -h/2, front, back );
        gl.glMatrixMode(GL2ES1.GL_MODELVIEW);
        gl.glLoadIdentity();
        //gl.glTranslated(-w,-h,0);
        //gl.glScaled(2,2,1);
        gl.glLineWidth(2.0f);
          */


        //bind to the framebuffer
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER,frameBuffer[0]);
        gl.glPushAttrib(GL2.GL_VIEWPORT_BIT);
        gl.glViewport(0,0,256,256);
        //render stuff
        //drawInTexture(gl);
        drawTriangle(gl);
        //switch back to main screen buffer
        gl.glPopAttrib();
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER,0);


        //bind to the texture
        //gl.glBindTexture(GL2.GL_TEXTURE_2D, img[0]);
        //generate mipmaps
        //gl.glGenerateMipmap(GL2.GL_TEXTURE_2D);

        drawWithTexture(gl);
        gl.glTranslated(1,1,0);
        drawWithTexture(gl);
        gl.glTranslated(-1,-1,0);

        //drawInTexture(gl);
        //drawTriangle(gl);

        
    }
    void drawTriangle (GL2 gl)   {
       // Make sure that smooth shading is enabled. This type of shading is
       // the default the first time display() is invoked. Because flat shading
       // is enabled later on, it becomes the new default. Without the
       // following method call, the triangle would also be flat shaded on
       // subsequent display() method invocations.

       gl.glShadeModel (GL2.GL_SMOOTH);

       // Render a triangle one unit in front of the origin along the positive
       // Z axis.

        gl.glScaled(5,5,0);
       gl.glBegin (GL.GL_TRIANGLES);
       gl.glColor3f (1.0f, 0.0f, 0.0f); // red at the top
       gl.glVertex3d (0.0, 1.0, 1.0);
       gl.glColor3f (0.0f, 1.0f, 0.0f); // green at the lower left
       gl.glVertex3d (-1.0, -1.0, 1.0);
       gl.glColor3f (0.0f, 0.0f, 1.0f); // blue at the lower right
       gl.glVertex3d (1.0, -1.0, 1.0);
       gl.glEnd ();
        gl.glScaled(1.0/5.0,1.0/5.0,0);
    }

    private void drawWithTexture(GL2 gl) {
        gl.glPushMatrix();
        //translate
        //set bg color to white
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        //enable the texture
        //regularTexture.enable();
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glEnable(GL2.GL_ALPHA_TEST);
        gl.glAlphaFunc(GL.GL_GREATER, 0);
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glTexEnvf(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
        //regularTexture.bind();
        gl.glBindTexture(GL.GL_TEXTURE_2D, img[0]);

        double x = 0;
        double y = 0;

        //draw a rectangle
        gl.glTranslated(x, y, 0);
        gl.glBegin(GL2.GL_QUADS);
        gl.glTexCoord2d(0,0);//tc.left(), tc.top());
        gl.glVertex3d(0, 0, 0);
        gl.glTexCoord2d(1,0);//tc.right(), tc.top());
        gl.glVertex3d(1,0,0);//image.texture.getWidth(), 0, 0);
        gl.glTexCoord2d(1,1);//tc.right(), tc.bottom());
        gl.glVertex3d(1,1,0);//image.texture.getWidth(), image.texture.getHeight(), 0);
        gl.glTexCoord2d(0,1);//tc.left(), tc.bottom());
        gl.glVertex3d(0, 1,0);// image.texture.getHeight(), 0);
        gl.glEnd();
        
        //image.texture.disable();
        gl.glPopMatrix();



        /*gl.glBindTexture(GL.GL_TEXTURE_2D, img[0]);

        for(int i=0; i<2; i++) {
            // Render qaud
            gl.glBegin(GL.GL_TRIANGLES);

            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(0, 0, 0);
            gl.glTexCoord2f(0, 1);
            gl.glVertex3f(0, 2, 0);
            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(2, 2, 0);

            gl.glTexCoord2f(1, 1);
            gl.glVertex3f(2, 2, 0);
            gl.glTexCoord2f(1, 0);
            gl.glVertex3f(2, 0, 0);
            gl.glTexCoord2f(0, 0);
            gl.glVertex3f(0, 0, 0);
            gl.glEnd();
        }
          */
    }


    //draw a line
    private void drawInTexture(GL2 gl) {
        double x = 0; double y =0;
        double x2 = 10.0; double y2 = 10.0;
        gl.glColor4d(1.0,0.0,1.0,1.0);
        gl.glBegin(GL.GL_LINE_STRIP);
        gl.glVertex2d(x, y);
        gl.glVertex2d(x2, y2);
        gl.glEnd();
    }

    public void dispose(GLAutoDrawable glAutoDrawable) {
        u.p("disposing");
        gl.glDeleteFramebuffers(1, frameBuffer,0);
        gl.glDeleteRenderbuffers(1,depthBuffer,0);
    }

    /*public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
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
    }
      */
    private final static double NEAR = 0.5; // Z values < NEAR are clipped
    private final static double FAR = 4.0;  // Z values > FAR are clipped
    public void reshape (GLAutoDrawable drawable, int x, int y, int width,
                         int height)
    {
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
}
