package org.joshy.gfx.css;

import org.joshy.gfx.draw.Font;
import org.joshy.gfx.node.Insets;

public class StyleInfo {
    public Insets margin;
    public Insets borderWidth;
    public Insets padding;
    public double contentBaseline;
    public Font font;
    public CSSMatcher matcher;

    public Insets calcContentInsets() {
        return new Insets(margin,borderWidth,padding);
    }
}
