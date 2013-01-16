package org.joshy.gfx.draw;

import org.joshy.gfx.Core;
import org.joshy.gfx.stage.jogl.JOGLFont;
import org.joshy.gfx.util.u;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
* Created by IntelliJ IDEA.
* User: josh
* Date: Jan 21, 2010
* Time: 7:37:41 PM
* To change this template use File | Settings | File Templates.
*/
public class FontBuilder {
    private String name = "Arial";
    private float size = 10;

    private static Map<String, Font> cache;
    static Map<String, java.awt.Font> customRootFontCache;
    private boolean vector = false;
    private File file = null;
    private Font.Weight weight = Font.Weight.Regular;
    private Font.Style style = Font.Style.Regular;
    private URL url;
    static Map<String, java.awt.Font> styledFontCache = new HashMap<String, java.awt.Font>();

    static {
        cache = new HashMap<String,Font>();
        customRootFontCache = new HashMap<String,java.awt.Font>();
        try {
            URL url2 = FontBuilder.class.getResource("/org/joshy/gfx/stage/swing/fonts/OpenSans-Bold.ttf");
            Font.registerFont(url2,"Open Sans",Font.Weight.Bold,Font.Style.Regular);
            URL url3 = FontBuilder.class.getResource("/org/joshy/gfx/stage/swing/fonts/OpenSans-Italic.ttf");
            Font.registerFont(url3,"Open Sans",Font.Weight.Regular,Font.Style.Italic);
            URL url4 = FontBuilder.class.getResource("/org/joshy/gfx/stage/swing/fonts/OpenSans-BoldItalic.ttf");
            u.p("bold italic url = " + url4);
            Font.registerFont(url4,"Open Sans",Font.Weight.Bold,Font.Style.Italic);
            URL url = FontBuilder.class.getResource("/org/joshy/gfx/stage/swing/fonts/OpenSans-Regular.ttf");
            Font.registerFont(url,"Open Sans",Font.Weight.Regular,Font.Style.Regular);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FontFormatException e) {
            e.printStackTrace();
        }

    }

    public FontBuilder(String name) {
        this.name = name;
    }

    public FontBuilder(File file) {
        this.file = file;
    }

    public FontBuilder(URL url) {
        this.url = url;
    }

    public FontBuilder size(float size) {
        this.size = size;
        return this;
    }

    private static void initCache() {
        if(cache == null) {
        }
    }

    public static void registerFont(InputStream inputStream, String name, Font.Weight weight, Font.Style style) throws IOException, FontFormatException {
        initCache();
        java.awt.Font fnt = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT,inputStream);
        FontBuilder.customRootFontCache.put(name,fnt);
        String key = "Font:"+ name +":"+weight+":"+style+":".intern();
        FontBuilder.styledFontCache.put(key, fnt);
        //u.p("added to styled font cache: " + key);
    }

    public Font resolve() {
        initCache();
        //load from file to get the name

        boolean custom = false;
        if(file != null) {
            try {
                java.awt.Font fnt = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT,file);
                name = fnt.getFontName();
                customRootFontCache.put(name,fnt);
                custom = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(url != null) {
            try {
                java.awt.Font fnt = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT,url.openStream());
                name = fnt.getFontName();
                customRootFontCache.put(name,fnt);
                custom = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String key = "Font:"+this.name+":"+size+":"+weight+":"+style+":".intern();
        if(cache.containsKey(key)) {
            return cache.get(key);
        }

        //use for custom fonts that are grouped by styles and were formally registered
        String styledKey = "Font:"+this.name+":"+weight+":"+style+":".intern();
        if(styledFontCache.containsKey(styledKey)) {
            //u.p("pulling from styled font cache: " + styledKey);
            java.awt.Font rootFont = styledFontCache.get(styledKey);
            Font font = new Font(rootFont,this.name,size);
            font.custom = custom;
            cache.put(key,font);
            return font;
        }

        Font font = null;
        if(Core.getShared().isUseJOGL()) {
            font = new JOGLFont(name,size,vector,file);
            font.custom = true;
        } else {
            //u.p("creating new with: " + name + " " + size + " " + vector + " " + weight + " " + style + " " + file + " " + url);
            font = new Font(name,size,vector,weight,style,file,url);
            font.custom = true;
        }
        cache.put(key,font);
        return font;
    }

    public FontBuilder setVector(boolean vector) {
        this.vector = vector;
        return this;
    }

    public FontBuilder weight(Font.Weight weight) {
        this.weight = weight;
        return this;
    }

    public FontBuilder style(Font.Style style) {
        this.style = style;
        return this;
    }

}
