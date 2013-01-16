package org.joshy.gfx.css;

import org.joshy.gfx.css.values.BaseValue;
import org.joshy.gfx.css.values.LinearGradientValue;
import org.joshy.gfx.css.values.URLValue;
import org.joshy.gfx.draw.*;
import org.joshy.gfx.node.Bounds;
import org.joshy.gfx.node.Insets;
import org.joshy.gfx.node.control.Control;
import org.joshy.gfx.node.control.Scrollbar;

import java.net.URI;

/**
 * Implements drawing Controls using the CSS box model.
 * This class is usually a singleton and should hold no state. All state
 * comes from the BoxStage instance passed in along with the control.
 */
public class CSSSkin {
    protected CSSRuleSet set;
    private Font defaultFont = Font.name("Arial").size(13).resolve();

    /* good functions */
    public CSSRuleSet getCSSSet() {
        return this.set;
    }

    public enum State {
        Pressed, Hover, Selected, Disabled, Focused, None
    }

    public StyleInfo getStyleInfo(Control control, Font realFont) {
        return getStyleInfo(control,realFont,null);
    }
    public StyleInfo getStyleInfo(Control control, Font realFont, String pseudoElement) {
        CSSMatcher matcher = createMatcher(control, CSSSkin.State.None, pseudoElement);
        StyleInfo info = new StyleInfo();
        info.matcher = matcher;
        info.margin = getMargin(matcher);
        info.padding = getPadding(matcher);
        info.borderWidth = getBorderWidth(matcher);
        info.font = getFont(matcher);
        if(realFont != null) {
            info.font = realFont;
        }
        info.contentBaseline = info.font.getAscender();
        return info;
    }
    public SizeInfo getSizeInfo(Control control, StyleInfo style, String content) {
        return getSizeInfo(control,style,content,null);
    }

    public SizeInfo getSizeInfo(Control control, StyleInfo style, String content, String pseudoElement) {
        CSSMatcher matcher = createMatcher(control, State.None, pseudoElement);
        SizeInfo size = new SizeInfo();
        size.contentWidth = control.getWidth()-style.margin.getLeft()-style.margin.getRight()-style.padding.getLeft()-style.padding.getRight();
        size.contentHeight = control.getHeight()-style.margin.getTop()-style.margin.getBottom()-style.padding.getTop()-style.padding.getBottom();

        Image icon = getIcon(matcher);
        //calc the sizes
        if("true".equals(set.findStringValue(matcher,"shrink-to-fit"))) {
            size.contentWidth = style.font.calculateWidth(content);
            size.contentHeight = style.font.calculateHeight(content);
            if(icon != null) {
                size.contentWidth += icon.getWidth();
                size.contentHeight = Math.max(size.contentHeight,icon.getHeight());
            }
            size.width = style.margin.getLeft()+style.margin.getRight()+style.borderWidth.getLeft()+style.borderWidth.getRight()+style.padding.getLeft()+style.padding.getRight()+size.contentWidth;
            size.height = style.margin.getTop()+style.margin.getBottom()+style.borderWidth.getTop()+style.borderWidth.getBottom()+style.padding.getTop()+style.padding.getBottom()+size.contentHeight;
            double fh = style.font.calculateHeight(content);
            size.contentBaseline = (size.contentHeight-fh)/2 + fh;
        } else {
            size.contentBaseline = size.contentHeight;
            size.width = set.findIntegerValue(matcher,"width");
            size.height = set.findIntegerValue(matcher,"height");
        }
        return size;
    }

    public BoxPainter createBoxPainter(Control control, StyleInfo style, SizeInfo size, String text, CSSSkin.State state) {
        return createBoxPainter(control,style,size,text,state,"");
    }

    public BoxPainter createBoxPainter(Control control, StyleInfo style, SizeInfo size, String text, State state, String pseudoElement) {
        CSSMatcher matcher = createMatcher(control, state, pseudoElement);
        BoxPainter boxPainter = new BoxPainter();
        boxPainter.borderRadius = getBorderRadius(matcher);
        boxPainter.transparent = "transparent".equals(set.findStringValue(matcher,"background-color"));
        if(!boxPainter.transparent) {
            boxPainter.background_color = new FlatColor(set.findColorValue(matcher,"background-color"),true);
        } else {
            boxPainter.background_color = FlatColor.BLACK;
        }
        BaseValue background = set.findValue(matcher,"background");


        double backWidth = size.width-style.margin.getLeft()-style.margin.getRight();
        double backHeight = size.height-style.margin.getTop()-style.margin.getBottom();
        Bounds bounds = new Bounds(style.margin.getLeft(),style.margin.getTop(),backWidth,backHeight);
        if(background instanceof LinearGradientValue) {
            boxPainter.gradient = true;
            boxPainter.gradientFill = toGradientFill((LinearGradientValue)background,bounds.getWidth(),bounds.getHeight());
        }

        //border stuff
        boxPainter.margin = getMargin(matcher);
        boxPainter.borderWidth = getBorderWidth(matcher);
        if(!boxPainter.borderWidth.allEquals(0)) {
            boxPainter.border_color = (new FlatColor(set.findColorValue(matcher,"border-color")));
        }

        //content stuff
        boxPainter.icon = getIcon(matcher);
        boxPainter.font = style.font;
        boxPainter.textAlign = set.findStringValue(matcher.element,"text-align");
        boxPainter.color = new FlatColor(set.findColorValue(matcher,"color"));
        boxPainter.text_shadow = set.findValue(matcher, "text-shadow");
        boxPainter.box_shadow = set.findValue(matcher, "box-shadow");

        return boxPainter;
    }

    protected Font getFont(CSSMatcher matcher) {
        int fontSize = set.findIntegerValue(matcher, "font-size");
        Font font = Font.name("Arial").size(fontSize).resolve();
        return font;
    }


    public void drawBackground(GFX g, CSSMatcher matcher, Bounds bounds) {
        g.translate(bounds.getX(),bounds.getY());
        Insets margin = getMargin(matcher);
        BaseValue background = set.findValue(matcher,"background");
        Insets radius = getBorderRadius(matcher);

        if(!"transparent".equals(set.findStringValue(matcher,"background-color"))) {
            g.setPaint(new FlatColor(set.findColorValue(matcher,"background-color")));
            if(background instanceof LinearGradientValue) {
                g.setPaint(toGradientFill((LinearGradientValue)background,bounds.getWidth(),bounds.getHeight()));
            }
            if(radius.allEquals(0)) {
                g.fillRect(
                        0+margin.getLeft(),
                        0+margin.getTop(),
                        bounds.getWidth()-margin.getLeft()-margin.getRight(),
                        bounds.getHeight()-margin.getTop()-margin.getBottom()
                        );
            } else if(radius.allEqual()) {
                g.fillRoundRect(
                        0+margin.getLeft(),
                        0+margin.getTop(),
                        bounds.getWidth()-margin.getLeft()-margin.getRight(),
                        bounds.getHeight()-margin.getTop()-margin.getBottom(),
                        radius.getLeft(),
                        radius.getRight());
            } else {
                g.fillCustomRoundRect(
                        0+margin.getLeft(),
                        0+margin.getTop(),
                        bounds.getWidth()-margin.getLeft()-margin.getRight(),
                        bounds.getHeight()-margin.getTop()-margin.getBottom(),
                        radius.getTop(),
                        radius.getTop(),
                        radius.getRight(),
                        radius.getRight(),
                        radius.getBottom(),
                        radius.getBottom(),
                        radius.getLeft(),
                        radius.getLeft()
                        );
            }
        }
        g.translate(-bounds.getX(),-bounds.getY());
    }

    public void drawBorder(GFX gfx, CSSMatcher matcher, Bounds bounds) {
        Insets margin = getMargin(matcher);
        Insets borderWidth = getBorderWidth(matcher);
        Insets radius = getBorderRadius(matcher);
        if(!borderWidth.allEquals(0)) {
            gfx.setPaint(new FlatColor(set.findColorValue(matcher,"border-color")));
            if(radius.allEquals(0)) {
                if(borderWidth.allEqual()) {
                    if(borderWidth.getLeft() >0) {
                        gfx.setStrokeWidth(borderWidth.getLeft());
                        gfx.drawRect(
                                bounds.getX()+margin.getLeft(),
                                bounds.getY()+margin.getTop(),
                                bounds.getWidth()-margin.getLeft()-margin.getRight(),
                                bounds.getHeight()-margin.getTop()-margin.getBottom()
                        );
                    }
                    gfx.setStrokeWidth(1);
                } else {
                    double x = bounds.getX()+margin.getLeft();
                    double y = bounds.getY()+margin.getTop();
                    double w = bounds.getWidth()-margin.getLeft()-margin.getRight()-1;
                    double h = bounds.getHeight()-margin.getTop()-margin.getBottom()-1;
                    if(borderWidth.getLeft()>0) {
                        gfx.setStrokeWidth(borderWidth.getLeft());
                        gfx.drawLine(x,y,x,y+h);
                    }
                    if(borderWidth.getTop()>0) {
                        gfx.setStrokeWidth(borderWidth.getTop());
                        gfx.drawLine(x,y,x+w,y);
                    }
                    if(borderWidth.getRight()>0) {
                        gfx.setStrokeWidth(borderWidth.getRight());
                        gfx.drawLine(x+w,y,x+w,y+h);
                    }
                    if(borderWidth.getBottom()>0) {
                        gfx.setStrokeWidth(borderWidth.getBottom());
                        gfx.drawLine(x,y+h,  x+w,y+h);
                    }
                }
            } else {
                if(radius.allEqual()) {
                    gfx.drawRoundRect(
                            bounds.getX()+margin.getLeft(),
                            bounds.getY()+margin.getTop(),
                            bounds.getWidth()-margin.getLeft()-margin.getRight(),
                            bounds.getHeight()-margin.getTop()-margin.getBottom(),
                        radius.getLeft(),
                        radius.getRight());
                } else {
                    gfx.drawCustomRoundRect(
                            bounds.getX()+margin.getLeft(),
                            bounds.getY()+margin.getTop(),
                            bounds.getWidth()-margin.getLeft()-margin.getRight(),
                            bounds.getHeight()-margin.getTop()-margin.getBottom(),
                        radius.getTop(),
                        radius.getTop(),
                        radius.getRight(),
                        radius.getRight(),
                        radius.getBottom(),
                        radius.getBottom(),
                        radius.getLeft(),
                        radius.getLeft()
                        );
                }
            }
            gfx.setStrokeWidth(1);
        }
    }




    private Insets getPadding(CSSMatcher matcher) {
        int padding_left = set.findIntegerValue(matcher,"padding-left");
        int padding_right = set.findIntegerValue(matcher,"padding-right");
        int padding_top = set.findIntegerValue(matcher,"padding-top");
        int padding_bottom = set.findIntegerValue(matcher,"padding-bottom");
        return new Insets(padding_top,padding_right,padding_bottom,padding_left);
    }

    private Insets getMargin(CSSMatcher matcher) {
        int margin_left = set.findIntegerValue(matcher,"margin-left");
        int margin_right = set.findIntegerValue(matcher,"margin-right");
        int margin_top = set.findIntegerValue(matcher,"margin-top");
        int margin_bottom = set.findIntegerValue(matcher,"margin-bottom");
        return new Insets(margin_top,margin_right,margin_bottom,margin_left);
    }

    protected Insets getBorderWidth(CSSMatcher matcher) {
        int border_left = set.findIntegerValue(matcher,"border-left-width");
        int border_right = set.findIntegerValue(matcher,"border-right-width");
        int border_top = set.findIntegerValue(matcher,"border-top-width");
        int border_bottom = set.findIntegerValue(matcher,"border-bottom-width");
        return new Insets(border_top,border_right,border_bottom,border_left);
    }

    protected Insets getBorderRadius(CSSMatcher matcher) {
        int border_top_left = set.findIntegerValue(matcher,"border-top-left-radius");
        int border_top_right = set.findIntegerValue(matcher,"border-top-right-radius");
        int border_bottom_right = set.findIntegerValue(matcher,"border-bottom-right-radius");
        int border_bottom_left = set.findIntegerValue(matcher,"border-bottom-left-radius");
        //TODO: we really should use a class other than Insets for this
        return new Insets(border_top_left,border_top_right,border_bottom_right,border_bottom_left);
    }

    public void drawText(GFX g, CSSMatcher matcher, Bounds b, String text) {
        g.translate(b.getX(),b.getY());
        Insets margin = getMargin(matcher);
        Insets borderWidth = getBorderWidth(matcher);
        Insets padding = getPadding(matcher);
        g.setPaint(new FlatColor(set.findColorValue(matcher,"color")));
        double x = margin.getLeft() + borderWidth.getLeft() + padding.getLeft();
        String textAlign = set.findStringValue(matcher,"text-align");
        double contentX = margin.getLeft()+borderWidth.getLeft()+padding.getLeft();
        double contentY = margin.getTop()+borderWidth.getTop()+padding.getTop();
        double textX = contentX;
        Font font = getFont(matcher);
        if("center".equals(textAlign)) {
            Font.drawCentered(g,text,font,textX,contentY,b.getWidth(),b.getHeight(),true);
        } else {
            Font.drawCenteredVertically(g,text,font,textX,contentY,b.getWidth(),b.getHeight(),true);
        }
        g.translate(-b.getX(),-b.getY());
    }

    public Insets getInsets(Control control) {
        CSSMatcher matcher = createMatcher(control,null);
        Insets margin = getMargin(matcher);
        Insets border = getBorderWidth(matcher);
        Insets padding = getPadding(matcher);
        return new Insets(margin,border,padding);
    }

    public void setRuleSet(CSSRuleSet set) {
        this.set = set;
    }

    public CSSRuleSet getRuleSet() {
        return this.set;
    }

    protected Image getIcon(CSSMatcher matcher) {
        Image icon = null;
        URLValue uv = set.findURIValue(matcher, "icon");
        try {
            if(uv != null) {
                URI uri = uv.getFullURI();
                icon = Image.getImageFromCache(uri.toURL());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return icon;
    }

    public static CSSMatcher createMatcher(Control control, State state) {
        return createMatcher(control,state,null);
    }
    public static CSSMatcher createMatcher(Control control, State state, String pseudoElement) {
        CSSMatcher matcher = new CSSMatcher(control);
        if(state == State.Disabled) {
            matcher.pseudo = "disabled";
        }
        if(state == State.Hover) {
            matcher.pseudo = "hover";
        }
        if(state == State.Pressed) {
            matcher.pseudo = "pressed";
        }
        if(state == State.Selected) {
            matcher.pseudo = "selected";
        }
        if(state == State.Focused) {
            matcher.pseudo = "focused";
        }
        if(control instanceof Scrollbar) {
            if(((Scrollbar)control).isVertical()) {
                matcher.pseudo = "vertical";
            }
        }
        matcher.pseudoElement = pseudoElement;
        return matcher;
    }

    protected LinearGradientFill toGradientFill(LinearGradientValue grad, double backWidth, double backHeight) {
        /*GradientFill gf = new GradientFill(
                new FlatColor(grad.getStop(0).getColor()),
                new FlatColor(grad.getStop(1).getColor()),
                90, false
        );*/
        LinearGradientFill gf = new LinearGradientFill();
        gf.addStop(0,new FlatColor((grad.getStop(0).getColor())))
            .addStop(1,new FlatColor((grad.getStop(1).getColor())));
        gf.setStartX(0);
        gf.setEndX(0);
        gf.setStartY(0);
        gf.setEndY(backHeight);
        if("left".equals(grad.getPosition1())) {
            gf.setEndX(backWidth);
            gf.setEndY(0);
        }
        return gf;
    }

    public Font getDefaultFont() {
        return defaultFont;
    }
}
