package org.joshy.gfx.stage.jogl;

import com.sun.opengl.util.FPSAnimator;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.layout.Container;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.stage.AWTEventPublisher;
import org.joshy.gfx.stage.Camera;
import org.joshy.gfx.stage.OrthoCamera;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.PerformanceTracker;
import org.joshy.gfx.util.u;

import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * A stage implementation for the JOGL graphics stack
 */
public class JOGLStage extends Stage {
    GLCanvas canvas;
    private JOGLFrame frame;
    private FPSAnimator animator;
    private AWTEventPublisher publisher;
    Container parent;
    private GLCapabilities caps;
    protected Node content;
    private int minimumWidth;
    private int minimumHeight;
    boolean use3DCamera = false;
    Camera camera = new OrthoCamera();

    public JOGLStage(boolean use3DCamera) {
        this.use3DCamera = use3DCamera;
        parent = new Panel() {
            public Stage getStage() {
                return JOGLStage.this;
            }
        };
        frame = new JOGLFrame(this);
        frame.setSize(500,500);
        caps = new GLCapabilities(null);
        //turn on anti-aliasing full screen
        //caps.setSampleBuffers(true);
        //caps.setNumSamples(4);
        
        canvas = new GLCanvas(caps);
        canvas.addGLEventListener(frame);

        publisher = new AWTEventPublisher(parent);
        canvas.addMouseListener(publisher);
        canvas.addMouseMotionListener(publisher);
        canvas.addKeyListener(publisher);
        canvas.requestFocus();

        frame.add(canvas);
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });
        animator = new FPSAnimator(canvas,60, true);
        animator.setRunAsFastAsPossible(false);
        animator.start();
    }

    public JOGLStage() {
        this(false);
    }

    @Override
    public void setContent(Node node) {
        this.content = node;
        parent.add(node);
    }

    @Override
    public Node getContent() {
        return this.content;
    }

    @Override
    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void setWidth(double width) {
        this.frame.setSize((int)width,this.frame.getHeight());
    }

    @Override
    public void setHeight(double height) {
        this.frame.setSize(this.frame.getWidth(), (int) height);
    }

    @Override
    public double getX() {
        return frame.getX();
    }

    @Override
    public void setX(double v) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double getY() {
        return frame.getY();
    }

    @Override
    public void setY(double v) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double getWidth() {
        return 0;
    }

    @Override
    public void setMinimumWidth(double width) {
        this.minimumWidth = (int) width;
        this.frame.setMinimumSize(new Dimension(minimumWidth,minimumHeight));
    }

    @Override
    public void setMinimumHeight(double height) {
        this.minimumHeight = (int) height;
        this.frame.setMinimumSize(new Dimension(minimumWidth,minimumHeight));
    }

    @Override
    public Container getPopupLayer() {
        return null;
    }

    @Override
    public Object getNativeWindow() {
        return frame;
    }

    @Override
    public void setTitle(CharSequence title) {
        frame.setTitle(title.toString());
    }

    @Override
    public void setUndecorated(boolean undecorated) {

    }

    @Override
    public void hide() {
        frame.setVisible(false);
    }

    @Override
    public void raiseToTop() {
        frame.setVisible(true);
        frame.toFront();
    }

    @Override
    public Stage setId(String id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void centerOnScreen() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setFullScreen(boolean fullScreen) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setOpacity(double v) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setAlwaysOnTop(boolean b) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public double getHeight() {
        return 0;
    }
}

class JOGLFrame extends Frame implements GLEventListener {
    private JOGLStage stage;
    private boolean layoutDirty;
    private boolean skinsDirty = true;

    JOGLFrame(JOGLStage stage) throws HeadlessException {
        this.stage = stage;
    }


    private void doSkins() {
        PerformanceTracker.getInstance().skinStart();
        if(stage.content != null) {
            if(stage.content instanceof Control) {
                Control c = (Control) stage.content;
                c.doSkins();
            }
        }
        PerformanceTracker.getInstance().skinEnd();
    }

    private void doGFXLayout(int width, int height) {
        PerformanceTracker.getInstance().layoutStart();
        if(stage.content != null) {
            if(stage.content instanceof Control) {
                Control c = (Control) stage.content;
                c.setWidth(width);
                c.setHeight(height);
                c.doLayout();
            }
        }
        PerformanceTracker.getInstance().layoutEnd();
    }

    public void init(GLAutoDrawable glAutoDrawable) {
        glAutoDrawable.getGL().setSwapInterval(1);
        glAutoDrawable.setAutoSwapBufferMode(true);
        GL2 gl = glAutoDrawable.getGL().getGL2();

        double w = getWidth();
        double h = getHeight()-getInsets().top;
        stage.camera.configureDisplay(gl,stage,w,h);

        //turn on anti-aliasing for smooth lines
        gl.glEnable(GL.GL_LINE_SMOOTH);
        gl.glEnable(GL2.GL_POLYGON_SMOOTH);
        gl.glEnable(GL.GL_BLEND);
        gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
        gl.glBlendFunc(GL.GL_LINE_SMOOTH_HINT, GL.GL_DONT_CARE);
    }

    public void display(GLAutoDrawable drawable) {
        PerformanceTracker.getInstance().drawStart();
        if(skinsDirty) {
            doSkins();
            skinsDirty = false;
        }
        if(layoutDirty) {
            doGFXLayout(stage.canvas.getWidth(),stage.canvas.getHeight());
            layoutDirty = false;
        }
        //clear the screen
        GL2 gl = drawable.getGL().getGL2();
        //gl.glEnable(GL.GL_DEPTH_TEST);
        //gl.glClear(GL.GL_COLOR_BUFFER_BIT);

        //set up normal projection mode
        //gl.glMatrixMode(GL2ES1.GL_PROJECTION);
        //gl.glLoadIdentity();

        double w = getWidth();
        double h = getHeight()-getInsets().top;
        stage.camera.configureDisplay(gl,stage,w,h);
        if(stage.content != null) {
            JOGLGFX gfx = new JOGLGFX(drawable);
            stage.content.draw(gfx);
            gfx.dispose();
        }
        PerformanceTracker.getInstance().drawEnd();
    }

    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int width, int height) {
        u.p("reshape called");
        GL2 gl = glAutoDrawable.getGL().getGL2();
        stage.camera.configureDisplay(gl,stage,width,height);
        /*
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL2ES1.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrtho(0.0, getWidth(), getHeight(), 0.0, -100.0, 100.0);
        gl.glMatrixMode(GL2ES1.GL_MODELVIEW);
        gl.glLoadIdentity();*/
        layoutDirty = true;
    }

    public void dispose(GLAutoDrawable glAutoDrawable) {
    }
}

