package org.joshy.gfx.node.control;

import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.event.ChangedEvent;
import org.joshy.gfx.event.EventBus;
import org.joshy.gfx.event.MouseEvent;
import org.joshy.gfx.node.NodeUtils;
import org.joshy.gfx.stage.Stage;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/** A minimal color picker
 * 
 */
public class SwatchColorPicker extends Button {
    private FlatColor selectedColor = FlatColor.GREEN;
    private double inset = 2;
    private PopupColorPicker popup;
    private ColorCallback outsideColorCallback;
    private List<CustomSwatch> customSwatches = new ArrayList<CustomSwatch>();

    public SwatchColorPicker() {
        super();
        setPrefWidth(26);
        setPrefHeight(26);
    }

    public void setOutsideColorCallback(ColorCallback outsideColorCallback) {
        this.outsideColorCallback = outsideColorCallback;
    }

    public void addCustomSwatch(CustomSwatch swatch) {
        customSwatches.add(swatch);
    }

    @Override
    protected void setPressed(boolean pressed) {
        super.setPressed(pressed);
        if (pressed) {
            if (popup == null) {
                popup = new PopupColorPicker(this);
                popup.setVisible(false);
                Stage stage = getParent().getStage();
                stage.getPopupLayer().add(popup);
            }
            Point2D pt = NodeUtils.convertToScene(this,0,getHeight());
            popup.setTranslateX(Math.round(Math.max(pt.getX(),0)));
            popup.setTranslateY(Math.round(Math.max(pt.getY(),0)));
            popup.setVisible(true);
            EventBus.getSystem().setPressedNode(popup);
        } else {
            popup.setVisible(false);
        }
    }


    @Override
    public void draw(GFX g) {
        if(!isVisible())return;
        g.setPaint(FlatColor.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setPaint(FlatColor.WHITE);
        g.fillRect(0+1, 0+1, getWidth()-2, getHeight()-2);
        g.setPaint(selectedColor);
        g.fillRect(inset, inset, getWidth() - inset*2, getHeight() - inset*2);
    }

    public FlatColor getSelectedColor() {
        return selectedColor;
    }

    private class PopupColorPicker extends Control {
        private int selectedX;
        private int selectedY;
        private final int size = 9;
        private final int rowCount = 18;
        private final int colCount = 36;

        public PopupColorPicker(SwatchColorPicker swatchColorPicker) {
            EventBus.getSystem().addListener(this, MouseEvent.MouseAll, new Callback<MouseEvent>() {
                public void call(MouseEvent event) {
                    processMouse(event);
                }
            });
        }

        private void processMouse(MouseEvent event) {
            if (event.getType() == MouseEvent.MouseDragged) {
                selectedX = (int)(event.getX()/(getWidth()/colCount));
                selectedY = (int)(event.getY()/(getHeight()/rowCount));
                //outside
                if(selectedX < 0 || selectedX > colCount-1) {
                    setSelectedColor(getOutsideColor(event));
                    setDrawingDirty();
                    return;
                }
                if(selectedY < 0 || selectedY > rowCount) {
                    setSelectedColor(getOutsideColor(event));
                    setDrawingDirty();
                    return;
                }
                //gradient edge
                if(selectedY == rowCount) {
                    try {
                        setSelectedColor(customSwatches.get(selectedX).getColor());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return;
                }
                setSelectedColor(xyToColor(selectedX,selectedY));
                setDrawingDirty();
            }
            if (event.getType() == MouseEvent.MouseReleased) {
                setDrawingDirty();
                setVisible(false);
                SwatchColorPicker.this.setFinalColor(getSelectedColor());
            }

        }

        private FlatColor getOutsideColor(MouseEvent event) {
            if(outsideColorCallback != null) {
                return outsideColorCallback.call(event);
            }
            return FlatColor.BLACK;
        }

        private FlatColor xyToColor(double x, double y) {
            //the empty color
            if(y == 0 && x == 0) {
                return null;
            }
            //the gray row
            if(y == 0) {
                return xToGray((int)x);
            }
            if (y <= rowCount / 2.0) {
                //the desaturation half / pastels
                return FlatColor.hsb(x * 360.0 / colCount, y / rowCount * 2.0, 1.0);
            } else {
                //the saturated half / darks
                return FlatColor.hsb(x * 360.0 / colCount, 1.0, (rowCount - y) / rowCount * 2.0);
            }
        }

        private FlatColor xToGray(int x) {
            double value = ((double)x-1)/((double)colCount-2);
            return new FlatColor(value,value,value,1);
        }
        
        @Override
        public void doPrefLayout() {
            //noop
        }

        @Override
        public void doLayout() {
            setWidth(colCount*size);
            setHeight((rowCount+1)*size);
        }

        @Override
        public void doSkins() {
        }

        @Override
        public void draw(GFX g) {
            if (!isVisible()) return;
            g.setPaint(FlatColor.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());

            //draw the null / empty square
            g.setPaint(FlatColor.WHITE);
            g.fillRect(0,0,size,size);
            g.setPaint(FlatColor.BLACK);
            g.drawRect(0,0,size-1,size-1);
            g.drawLine(0,size-1,size-1,0);
            //draw the grayscale line of squares
            for(int x=1; x<colCount;x++) {
                g.setPaint(xToGray(x));
                g.fillRect(x*size+1, 0, size-1, size-1);
            }
            
            //draw the color squares
            for (int x = 0; x < colCount; x++) {
                for (int y = 1; y < rowCount; y++) {
                    g.setPaint(xyToColor(x, y));
                    g.fillRect(x * size + 1, y * size, size-1, size-1);
                }
            }

            //draw the custom squares
            int y = rowCount;
            int x = 0;
            for(CustomSwatch swatch : customSwatches) {
                swatch.draw(g,x*size+1,y*size,size-1,size-1);
                x++;
            }


            //draw the selected color border
            g.setPaint(FlatColor.BLACK);
            g.drawRect(selectedX*size+1,selectedY*size,size-2,size-1);
            g.setPaint(FlatColor.WHITE);
            g.drawRect(selectedX*size+1+1,selectedY*size+1,size-4,size-3);

            g.setPaint(FlatColor.BLACK);
            g.drawRect(0, 0, getWidth(), getHeight());
        }

    }

    private void setFinalColor(FlatColor selectedColor) {
        EventBus.getSystem().publish(new ChangedEvent(ChangedEvent.FinalChange,selectedColor,this));
    }

    public void setSelectedColor(FlatColor flatColor) {
        if(selectedColor != null && selectedColor.equals(flatColor)) return;
        selectedColor = flatColor;
        EventBus.getSystem().publish(new ChangedEvent(ChangedEvent.ColorChanged,selectedColor,this));
        setDrawingDirty();
    }

    public void onColorSelected(Callback<ChangedEvent> callback) {
        EventBus.getSystem().addListener(this, ChangedEvent.ColorChanged, callback);
    }

    public static interface ColorCallback {
        public FlatColor call(MouseEvent event);
    }

    public static interface CustomSwatch {
        public void draw(GFX gfx, double x, double y, double w, double h);

        public FlatColor getColor();
    }
}
