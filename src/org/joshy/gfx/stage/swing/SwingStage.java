package org.joshy.gfx.stage.swing;

import com.apple.eawt.AppEvent;
import com.apple.eawt.FullScreenListener;
import com.apple.eawt.FullScreenUtilities;
import com.sun.awt.AWTUtilities;
import org.joshy.gfx.Core;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.FocusManager;
import org.joshy.gfx.event.SystemMenuEvent;
import org.joshy.gfx.node.Node;
import org.joshy.gfx.node.NodeUtils;
import org.joshy.gfx.node.Parent;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.layout.Container;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.stage.AWTEventPublisher;
import org.joshy.gfx.stage.Camera;
import org.joshy.gfx.stage.Stage;
import org.joshy.gfx.util.PerformanceTracker;
import org.joshy.gfx.util.u;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.font.TextHitInfo;
import java.awt.im.InputMethodRequests;
import java.io.File;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.List;

public class SwingStage extends Stage {
    private JFrame frame;
    private SceneComponent scene;
    private boolean skinsDirty = true;
    private boolean layoutDirty = true;
    private boolean drawingDirty = true;
    private int minimumHeight = 10;
    private int minimumWidth = 10;
    protected Panel popupLayer;
    protected Container root;
    protected Container contentLayer;
    private Node contentNode;
    private String id;
    private DisplayMode oldDisplayMode;

    @Override
    public void setUndecorated(boolean undecorated) {
        JFrame newFrame = new JFrame("stage");
        newFrame.setUndecorated(undecorated);
        newFrame.setMinimumSize(new Dimension(200,200));
        newFrame.setSize(frame.getSize());
        newFrame.setLocation(frame.getLocation());
        newFrame.add(scene);
        frame.remove(scene);
        frame.setVisible(false);
        newFrame.setJMenuBar(frame.getJMenuBar());
        newFrame.setVisible(true);
        frame = newFrame;
        scene.requestFocus();
    }

    @Override
    public void hide() {
        frame.setVisible(false);
        u.p("displosing");
        frame.dispose();
    }

    public void close() {
    }

    @Override
    public void raiseToTop() {
        frame.setVisible(true);
        frame.toFront();
    }

    @Override
    public Stage setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public void centerOnScreen() {
//        Toolkit tk = Toolkit.getDefaultToolkit();
        Point pt = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
//        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
//        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
        frame.setLocation(pt.x-(int)getWidth()/2,pt.y-(int)getHeight()/2);
    }

    @Override
    public void setFullScreen(boolean fullScreen) {
        if(fullScreen) {

            JFrame newFrame = new JFrame("stage");
            newFrame.setUndecorated(true);
            newFrame.setResizable(false);

            newFrame.setSize(frame.getSize());
            newFrame.setLocation(frame.getLocation());
            newFrame.add(scene);
            frame.remove(scene);
            frame.setVisible(false);
            newFrame.setJMenuBar(frame.getJMenuBar());

            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            oldDisplayMode = gd.getDisplayMode();
            gd.setFullScreenWindow(newFrame);
            newFrame.setVisible(true);

            frame = newFrame;
            scene.requestFocus();
        } else {
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            gd.setDisplayMode(oldDisplayMode);
            JFrame newFrame = new JFrame("stage");
            newFrame.setUndecorated(false);
            newFrame.setResizable(true);
            newFrame.setSize(frame.getSize());
            newFrame.setLocation(frame.getLocation());
            newFrame.add(scene);
            frame.remove(scene);
            frame.setVisible(false);
            newFrame.setJMenuBar(frame.getJMenuBar());
            frame = newFrame;
            scene.requestFocus();
        }
    }

    @Override
    public void setOpacity(double v) {
        JFrame newFrame = new JFrame("stage");
        newFrame.setUndecorated(true);
        newFrame.setTitle(frame.getTitle());
        newFrame.getRootPane().putClientProperty("apple.awt.draggableWindowBackground",Boolean.FALSE);
        AWTUtilities.setWindowOpaque(newFrame,false);
        newFrame.setSize(frame.getSize());
        newFrame.setLocation(frame.getLocation());
        newFrame.add(scene);
        frame.remove(scene);
        frame.setVisible(false);
        newFrame.setJMenuBar(frame.getJMenuBar());
        newFrame.setVisible(true);
        frame = newFrame;
    }

    @Override
    public void setAlwaysOnTop(boolean b) {
        frame.setAlwaysOnTop(b);
    }

    public SwingStage(boolean fullscreen) {
        super();
        frame = new JFrame("stage");
        
        

        if(fullscreen) {
            Window window = frame;
            FullScreenUtilities.addFullScreenListenerTo(window, new FullScreenListener() {
                public void windowEnteringFullScreen(AppEvent.FullScreenEvent fullScreenEvent) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

                public void windowEnteredFullScreen(AppEvent.FullScreenEvent fullScreenEvent) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

                public void windowExitingFullScreen(AppEvent.FullScreenEvent fullScreenEvent) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }

                public void windowExitedFullScreen(AppEvent.FullScreenEvent fullScreenEvent) {
                    //To change body of implemented methods use File | Settings | File Templates.
                }
            });
            FullScreenUtilities.setWindowCanFullScreen(window, true);
        }
        
        
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setMinimumSize(new Dimension(200,200));
        root = new Container() {
            @Override
            protected void setSkinDirty() {
                super.setSkinDirty();
                SwingStage.this.skinsDirty = true;
            }

            @Override
            protected void setLayoutDirty() {
                super.setLayoutDirty();
                SwingStage.this.layoutDirty = true;
            }

            @Override
            public void doPrefLayout() {
                super.doPrefLayout();
            }

            @Override
            public void doLayout() {
                contentLayer.setWidth(getWidth());
                contentLayer.setHeight(getHeight());
                super.doLayout();
            }
            @Override
            public void setDrawingDirty(Node node) {
                super.setDrawingDirty(node);
                scene.repaint();
                SwingStage.this.drawingDirty = true;
            }

            @Override
            public void setLayoutDirty(Node node) {
                super.setLayoutDirty(node);
                scene.repaint();
                layoutDirty = true;
            }

            public Stage getStage() {
                return SwingStage.this;
            }

            @Override
            public void setSkinDirty(Node node) {
                super.setSkinDirty(node);
                scene.repaint();
                skinsDirty = true;
            }

            @Override
            public void draw(GFX g) {
                for(Node child : children) {
                    g.translate(child.getTranslateX(),child.getTranslateY());
                    child.draw(g);
                    g.translate(-child.getTranslateX(),-child.getTranslateY());
                }
                this.drawingDirty = false;
            }

            @Override
            public String getId() {
                return SwingStage.this.id;
            }
        };
        root.addCSSClass("-root");
        scene = new SceneComponent();
        contentLayer = new Container() {
            
            @Override
            public void doPrefLayout() {
                for(Node n : children()) {
                    if(n instanceof Control) {
                        Control c = (Control) n;
                        c.doPrefLayout();
                    }
                }
            }

            @Override
            public void doLayout() {
                for(Node n : children()) {
                    if(n instanceof Control) {
                        Control c = (Control) n;
                        c.setWidth(getWidth());
                        c.setHeight(getHeight());
                        c.doLayout();
                    }
                }
            }

            @Override
            public void draw(GFX g) {
                //g.setPaint(FlatColor.RED);
                //g.fillRect(0,0,getWidth(),getHeight());
                for(Node child : children) {
                    g.translate(child.getTranslateX(),child.getTranslateY());
                    child.draw(g);
                    g.translate(-child.getTranslateX(),-child.getTranslateY());
                }
                this.drawingDirty = false;
            }
        };
        root.add(contentLayer);
        popupLayer = new Panel();
        root.add(popupLayer);
        frame.add(scene);
        frame.setSize(500,500);
        frame.setVisible(true);
        scene.requestFocus();
        frame.addWindowListener(scene.publisher);
        frame.addWindowStateListener(scene.publisher);
        ((SwingCore)Core.getShared()).addStage(this);
    }

    @Override
    public void setContent(Node node) {
        this.contentNode = node;
        contentLayer.removeAll();
        contentLayer.add(node);
        this.skinsDirty = true;
        this.layoutDirty = true;
        this.drawingDirty = true;
    }

    @Override
    public Node getContent() {
        return contentNode;
    }

    @Override
    public void setCamera(Camera camera) {
        //swing stage doesn't use cameras at all
    }

    @Override
    public void setWidth(double width) {
        this.frame.setSize((int)width,this.frame.getHeight());
    }

    @Override
    public double getWidth() {
        return this.frame.getWidth();
    }

    public void setHeight(double height) {
        this.frame.setSize(this.frame.getWidth(), (int) height);
    }

    @Override
    public double getX() {
        return frame.getX();
    }

    @Override
    public void setX(double v) {
        this.frame.setLocation((int)v,this.frame.getY());
    }

    @Override
    public double getY() {
        return frame.getY();
    }

    @Override
    public void setY(double v) {
        this.frame.setLocation(this.frame.getX(), (int) v);
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
        return popupLayer;
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
    public double getHeight() {
        return this.frame.getHeight();

    }

    public Parent getRoot() {
        return root;
    }

    private class SceneComponent extends JComponent {
        private AWTEventPublisher publisher;
        private TextHitInfo ime_caret;
        private InputMethodRequests ime_imr;

        private SceneComponent() {
            publisher = new AWTEventPublisher(root);
            this.addMouseListener(publisher);
            this.addMouseMotionListener(publisher);
            this.addMouseWheelListener(publisher);
            this.addKeyListener(publisher);
            this.setFocusTraversalKeysEnabled(false);
            this.addInputMethodListener(new InputMethodListener() {
                @Override
                public void inputMethodTextChanged(InputMethodEvent inputMethodEvent) {
                    //u.p("input method text changed: " + inputMethodEvent);
                    //u.p("count = " + inputMethodEvent.getCommittedCharacterCount());
                    //u.p("text = " + inputMethodEvent.getText());
                    FocusManager.IMETarget target = Core.getShared().getFocusManager().getIMETarget();
                    if(target != null) {
                        target.setComposingText(inputMethodEvent);
                        target.appendCommittedText(inputMethodEvent);
                        inputMethodEvent.consume();
                        ime_caret = inputMethodEvent.getCaret();
                    }
                }

                @Override
                public void caretPositionChanged(InputMethodEvent inputMethodEvent) {
                    //u.p("caret position changed: " + inputMethodEvent);
                    ime_caret = inputMethodEvent.getCaret();
                    inputMethodEvent.consume();
                    //repaint();
                }
            });
            ime_imr = new InputMethodRequests(){
                @Override
                public Rectangle getTextLocation(TextHitInfo textHitInfo) {
                    //u.p("get text location called: " + textHitInfo);
                    return new Rectangle(60,60,100,100);
                }

                @Override
                public TextHitInfo getLocationOffset(int i, int i1) {
                    //u.p("get location offset called");
                    return null;
                }

                @Override
                public int getInsertPositionOffset() {
                    //u.p("get insert position offset called");
                    return 0;
                }

                @Override
                public AttributedCharacterIterator getCommittedText(int i, int i1, AttributedCharacterIterator.Attribute[] attributes) {
                    //u.p("get committed text called: " + i + " " + i1 + " " + attributes);
                    FocusManager.IMETarget target = Core.getShared().getFocusManager().getIMETarget();
                    if(target != null) {
                        return new AttributedString(target.getCommittedText()).getIterator();
                    }
                    return null;
                }

                @Override
                public int getCommittedTextLength() {
                    //u.p("get committed text length called");
                    FocusManager.IMETarget target = Core.getShared().getFocusManager().getIMETarget();
                    if(target != null) {
                        return target.getCommittedText().length();
                    }
                    return 0;
                }

                @Override
                public AttributedCharacterIterator cancelLatestCommittedText(AttributedCharacterIterator.Attribute[] attributes) {
                    //u.p("cancel latest committed text called " + attributes);
                    return null;
                }

                @Override
                public AttributedCharacterIterator getSelectedText(AttributedCharacterIterator.Attribute[] attributes) {
                    //u.p("get selected text caleld: " + attributes);
                    return (new AttributedString("")).getIterator();
                }
            };
            enableInputMethods(true);
            this.setTransferHandler(new FileDropTransferHandler());
        }

        @Override
        public InputMethodRequests getInputMethodRequests() {
            return ime_imr;
        }

        @Override
        public void setBounds(int x, int y, int width, int height) {
            super.setBounds(x, y, width, height);
            layoutDirty = true;
            drawingDirty = true;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            if(skinsDirty) {
                doSkins();
                skinsDirty = false;
            }
            if(layoutDirty) {
                PerformanceTracker.getInstance().layoutStart();
                doGFXPrefLayout(getWidth(), getHeight());
                doGFXLayout(getWidth(),getHeight());
                layoutDirty = false;
                PerformanceTracker.getInstance().layoutEnd();
            }
            //if paintcomponent was called, then we must always draw
            //if(drawingDirty) {
                doGFXDrawing(graphics, getWidth(), getHeight());
                drawingDirty = false;
            //}
        }

        private void doSkins() {
            PerformanceTracker.getInstance().skinStart();
            NodeUtils.doSkins(root);
            PerformanceTracker.getInstance().skinEnd();
        }


        private void doGFXPrefLayout(int width, int height) {
            root.setWidth(width);
            root.setHeight(height);
            root.doPrefLayout();
        }
        private void doGFXLayout(int width, int height) {
            root.setWidth(width);
            root.setHeight(height);
            root.doLayout();
        }

        private void doGFXDrawing(Graphics graphics, int width, int height) {
            PerformanceTracker.getInstance().drawStart();
            //graphics.setColor(Color.BLUE);
            //graphics.fillRect(0,0,getWidth(),getHeight());
            GFX gfx = new SwingGFX((Graphics2D) graphics);
            root.draw(gfx);
            gfx.dispose();
            PerformanceTracker.getInstance().drawEnd();
        }

        private class FileDropTransferHandler extends TransferHandler {
            @Override
            public boolean canImport(TransferSupport transferSupport) {
                //u.p("can import called ");
                //u.p("can do file drop: " + transferSupport.isDataFlavorSupported(DataFlavor.javaFileListFlavor));
                //u.p("flavors = "); u.p(transferSupport.getDataFlavors());
                return transferSupport.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }

            @Override
            public boolean importData(TransferSupport transferSupport) {
                //u.p("doing drop");
                Transferable tf = transferSupport.getTransferable();
                try {
                    Object data = tf.getTransferData(DataFlavor.javaFileListFlavor);
                    //u.p("data = " + data);
                    List<File> files = (List<File>) data;
                    //u.p("files = " + files);
                    //u.p(files);
                    SystemMenuEvent event = new SystemMenuEvent(SystemMenuEvent.FileDrop,SwingStage.this,files);
                    EventBus.getSystem().publish(event);
                } catch (UnsupportedFlavorException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                return true;
            }
        }
    }



}

