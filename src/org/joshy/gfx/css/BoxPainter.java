package org.joshy.gfx.css;

import org.joshy.gfx.css.values.BaseValue;
import org.joshy.gfx.css.values.ShadowValue;
import org.joshy.gfx.draw.*;
import org.joshy.gfx.draw.effects.BlurEffect;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Insets;

/**
 * Paints a control laid out by the css boxmodel.
 */
public class BoxPainter {
    public Insets borderRadius;
    Insets margin;
    public boolean transparent;
    public FlatColor background_color;
    public boolean gradient;
    public LinearGradientFill gradientFill;
    public Insets borderWidth;
    public FlatColor border_color;
    public Image icon;
    public Font font;
    public String textAlign;
    public FlatColor color;
    public BaseValue text_shadow;
    public BaseValue box_shadow;
    private String oldText;
    private ImageBuffer oldBuf;


    public void draw(GFX g, StyleInfo styleInfo, SizeInfo size, String text) {
        drawBackground(g, styleInfo,size);
        drawContent(g, styleInfo, size,text);
        drawBorder(g, styleInfo,size);
    }
    
    public void drawBackground(GFX g, StyleInfo box, SizeInfo size) {
        double backWidth = size.width-box.margin.getLeft()-box.margin.getRight();
        double backHeight = size.height-box.margin.getTop()-box.margin.getBottom();
        Bounds bounds = new Bounds(box.margin.getLeft(),box.margin.getTop(),backWidth,backHeight);
        g.translate(bounds.getX(),bounds.getY());

        //shadow first
        if(box_shadow instanceof ShadowValue) {
            ShadowValue shadow = (ShadowValue) box_shadow;
            ImageBuffer buf = g.createBuffer(100,100);
            int br = shadow.getBlurRadius();
            if(buf != null) {
                GFX g2 = buf.getGFX();
                g2.setPaint(new FlatColor(shadow.getColor(),1.0));
                g2.translate(br,br);
                drawBG(g2, bounds);
                buf.apply(new BlurEffect(br));
                g2.translate(-br,-br);
            }
            g.draw(buf,shadow.getXoffset()-br,shadow.getYoffset()-br);
        }
        g.setPaint(background_color);
        if(gradient) {
            g.setPaint(gradientFill);
        }
        drawBG(g,bounds);
        g.translate(-bounds.getX(),-bounds.getY());
    }

    private void drawBG(GFX g, Bounds bounds) {
        if(!transparent) {
            if(borderRadius.allEquals(0)) {
                g.fillRect(
                        0,
                        0,
                        bounds.getWidth(),
                        bounds.getHeight()
                        );
            } else if(borderRadius.allEqual()) {
                g.fillRoundRect(
                        0,
                        0,
                        bounds.getWidth(),
                        bounds.getHeight(),
                        borderRadius.getLeft(),
                        borderRadius.getLeft());
            } else {
                g.fillCustomRoundRect(
                        0,
                        0,
                        bounds.getWidth(),
                        bounds.getHeight(),
                        borderRadius.getLeft(),
                        borderRadius.getLeft(),
                        borderRadius.getLeft(),
                        borderRadius.getLeft(),
                        borderRadius.getLeft(),
                        borderRadius.getLeft(),
                        borderRadius.getLeft(),
                        borderRadius.getLeft()
                );
            }
        }

    }

    protected void drawBorder(GFX gfx, StyleInfo box, SizeInfo size) {
        double backWidth = size.width-box.margin.getLeft()-box.margin.getRight();
        double backHeight = size.height-box.margin.getTop()-box.margin.getBottom();
        Bounds bounds = new Bounds(box.margin.getLeft(),box.margin.getTop(),backWidth,backHeight);

        if(!borderWidth.allEquals(0)) {
            gfx.setPaint(border_color);
            if(borderRadius.allEquals(0)) {
                if(borderWidth.allEqual()) {
                    gfx.setStrokeWidth(borderWidth.getLeft());
                    gfx.drawRect(
                            bounds.getX(),
                            bounds.getY(),
                            bounds.getWidth(),
                            bounds.getHeight()
                    );
                    gfx.setStrokeWidth(1);
                } else {
                    if(borderWidth.getTop()>0) {
                        gfx.setStrokeWidth(borderWidth.getTop());
                        gfx.drawLine(bounds.getX(),bounds.getY(),bounds.getX()+bounds.getWidth(),bounds.getY());
                    }
                    if(borderWidth.getRight()>0) {
                        gfx.setStrokeWidth(borderWidth.getRight());
                        gfx.drawLine(bounds.getX()+bounds.getWidth(),bounds.getY(),bounds.getX()+bounds.getWidth(),bounds.getY()+bounds.getHeight());
                    }
                    if(borderWidth.getBottom()>0) {
                        gfx.setStrokeWidth(borderWidth.getBottom());
                        gfx.drawLine(bounds.getX(),bounds.getY()+bounds.getHeight(),bounds.getX()+bounds.getWidth(),bounds.getY()+bounds.getHeight());
                    }
                    if(borderWidth.getLeft()>0) {
                        gfx.setStrokeWidth(borderWidth.getLeft());
                        gfx.drawLine(bounds.getX(),bounds.getY(),bounds.getX(),bounds.getY()+bounds.getHeight());
                    }

                }
            } else if(borderRadius.allEqual()) {
                gfx.setStrokeWidth(borderWidth.getLeft());
                gfx.drawRoundRect(
                        bounds.getX(),
                        bounds.getY(),
                        bounds.getWidth(),
                        bounds.getHeight(),
                        borderRadius.getLeft(),
                        borderRadius.getLeft());
            } else {
                gfx.fillCustomRoundRect(
                        bounds.getX(),
                        bounds.getY(),
                        bounds.getWidth(),
                        bounds.getHeight(),
                        borderRadius.getLeft(),
                        borderRadius.getLeft(),
                        borderRadius.getLeft(),
                        borderRadius.getLeft(),
                        borderRadius.getLeft(),
                        borderRadius.getLeft(),
                        borderRadius.getLeft(),
                        borderRadius.getLeft()
                );
            }
        }
    }

    private void drawContent(GFX gfx, StyleInfo box, SizeInfo size, String content) {
        //draw the internal content
        double contentX = box.margin.getLeft()+borderWidth.getLeft()+box.padding.getLeft();
        double contentY = box.margin.getTop()+borderWidth.getTop()+box.padding.getTop();
        gfx.setPaint(color);

        double textX = contentX;
        double textWidth = size.contentWidth;
        if(icon != null) {
            textX += icon.getWidth();
            textWidth -= icon.getWidth();
        }
        //do drop shadow on text content
        if(content != null && content.length() > 0) {
            //BaseValue value = null;//set.findValue(matcher, "text-shadow");
            if(text_shadow instanceof ShadowValue) {
                ShadowValue shadow = (ShadowValue) text_shadow;
                if(!content.equals(oldText)) {
                    ImageBuffer buf = gfx.createBuffer((int)textWidth,(int)size.contentHeight);
                    if(buf != null) {
                        GFX g2 = buf.getGFX();
                        g2.setPaint(new FlatColor(shadow.getColor(),0.3));
                        g2.translate(-textX,-contentY);
                        Font.drawCentered(g2,content,font,textX,contentY,textWidth,size.contentHeight,true);
                        buf.apply(new BlurEffect(3));
                        oldBuf = buf;
                    }
                    oldText = content;
                }
                if(oldBuf != null) {
                    gfx.draw(oldBuf,textX+shadow.getXoffset(),contentY+shadow.getYoffset());
                }

            }
        }

        if("center".equals(textAlign)) {
            Font.drawCentered(gfx,content,font,textX,contentY,textWidth,size.contentHeight,true);
        } else {
            Font.drawCenteredVertically(gfx,content,font,textX,contentY,textWidth,size.contentHeight,true);
        }

        if(icon != null) {
            gfx.drawImage(icon,contentX,contentY);
        }

    }

}
