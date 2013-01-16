package org.joshy.gfx.test.jogl;

import java.awt.event.*;
import javax.media.opengl.awt.GLCanvas;
import javax.swing.*;

import javax.media.opengl.*;
import javax.media.opengl.glu.*;
import com.sun.opengl.util.*;
import com.sun.opengl.util.gl2.GLUT;

/**
 * This program draws shows how to use multisampling to draw anti-aliased
 * geometric primitives. The same display list, a pinwheel of triangles and
 * lines of varying widths, is rendered twice. Multisampling is enabled when the
 * left side is drawn. Multisampling is disabled when the right side is drawn.
 * <br>
 * Pressing the 'b' key toggles drawing of the checkerboard background.
 * Antialiasing is sometimes easier to see when objects are rendered over a
 * contrasting background.
 */
public class multisamp extends JFrame
    implements GLEventListener
    , KeyListener
{
  private GLCapabilities caps;
  private GLCanvas canvas;
  private GLU glu;
  private GLUT glut;
  private boolean bgtoggle = true;


  public multisamp()
  {
    super("mulisamp");

    caps = new GLCapabilities(null);
    caps.setSampleBuffers(true);
    caps.setNumSamples(2);
    canvas = new GLCanvas(caps);
    canvas.addGLEventListener(this);
    canvas.addKeyListener(this);

    add(canvas);
  }

  public void run()
  {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(600, 300);
    setLocationRelativeTo(null);
    setVisible(true);
    canvas.requestFocusInWindow();
  }

  public static void main(String[] args)
  {
    new multisamp().run();
  }

  /*
   * Print out state values related to multisampling. Create display list with
   * "pinwheel" of lines and triangles.
   */
  public void init(GLAutoDrawable drawable)
  {
    GL2 gl = drawable.getGL().getGL2();
    glu = new GLU();
    glut = new GLUT();
    //
    int buf[] = new int[1];
    int sbuf[] = new int[1];

    gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    gl.glGetIntegerv(GL.GL_SAMPLE_BUFFERS, buf, 0);
    System.out.println("number of sample buffers is " + buf[0]);
    gl.glGetIntegerv(GL.GL_SAMPLES, sbuf, 0);
    System.out.println("number of samples is " + sbuf[0]);

    gl.glNewList(1, GL2.GL_COMPILE);
    for (int i = 0; i < 19; i++)
    {
      gl.glPushMatrix();
      gl.glRotatef(360.0f * (float) i / 19.0f, 0.0f, 0.0f, 1.0f);
      gl.glColor3f(1.0f, 1.0f, 1.0f);
      gl.glLineWidth((i % 3) + 1.0f);
      gl.glBegin(GL.GL_LINES);
      gl.glVertex2f(0.25f, 0.05f);
      gl.glVertex2f(0.9f, 0.2f);
      gl.glEnd();
      gl.glColor3f(0.0f, 1.0f, 1.0f);
      gl.glBegin(GL.GL_TRIANGLES);
      gl.glVertex2f(0.25f, 0.0f);
      gl.glVertex2f(0.9f, 0.0f);
      gl.glVertex2f(0.875f, 0.10f);
      gl.glEnd();
      gl.glPopMatrix();
    }
    gl.glEndList();

    gl.glNewList(2, GL2.GL_COMPILE);
    gl.glColor3f(1.0f, 0.5f, 0.0f);
    gl.glBegin(GL2.GL_QUADS);
    for (int i = 0; i < 16; i++)
      for (int j = 0; j < 16; j++)
        if (((i + j) % 2) == 0)
        {
          gl.glVertex2f(-2.0f + (i * 0.25f), -2.0f + (j * 0.25f));
          gl.glVertex2f(-2.0f + (i * 0.25f), -1.75f + (j * 0.25f));
          gl.glVertex2f(-1.75f + (i * 0.25f), -1.75f + (j * 0.25f));
          gl.glVertex2f(-1.75f + (i * 0.25f), -2.0f + (j * 0.25f));
        }

    gl.glEnd();
    gl.glEndList();
  }

    public void dispose(GLAutoDrawable glAutoDrawable) {

    }

    /*
    * Draw two sets of primitives, so that you can compare the user of
    * multisampling against its absence. This code enables antialiasing and draws
    * one display list and disables and draws the other display list
    */
  public void display(GLAutoDrawable drawable)
  {
    GL2 gl = drawable.getGL().getGL2();
    gl.glClear(GL.GL_COLOR_BUFFER_BIT);
    //
    if (bgtoggle) gl.glCallList(2);

    gl.glEnable(GL.GL_MULTISAMPLE);
    gl.glPushMatrix();
    gl.glTranslatef(-1.0f, 0.0f, 0.0f);
    gl.glCallList(1);
    gl.glPopMatrix();

    gl.glDisable(GL.GL_MULTISAMPLE);
    gl.glPushMatrix();
    gl.glTranslatef(1.0f, 0.0f, 0.0f);
    gl.glCallList(1);
    gl.glPopMatrix();

    gl.glFlush();
  }

  public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h)
  {
    GL2 gl = drawable.getGL().getGL2();
    //
    gl.glViewport(0, 0, w, h);
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();
    if (w <= (2 * h)) glu.gluOrtho2D(-2.0, 2.0, //
        -2.0 * (float) h / (float) w, 2.0 * (float) h / (float) w);
    else glu.gluOrtho2D(-2.0 * (float) w / (float) h, //
        2.0 * (float) w / (float) h, -2.0, 2.0);
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glLoadIdentity();
  }

  public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
      boolean deviceChanged)
  {
  }

  public void keyPressed(KeyEvent key)
  {
    switch (key.getKeyChar()) {

      case KeyEvent.VK_ESCAPE:
        System.exit(0);
        break;
      case 'b':
      case 'B':
        bgtoggle = !bgtoggle;
        break;

      default:
        break;
    }
    canvas.display();
  }

  public void keyTyped(KeyEvent key)
  {
  }

  public void keyReleased(KeyEvent key)
  {
  }

}