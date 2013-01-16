package org.joshy.gfx.stage.jogl;

import com.sun.opengl.util.awt.TextRenderer;
import org.joshy.gfx.text.JFont;
import org.joshy.gfx.text.PackedFont;
import org.joshy.gfx.draw.Font;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 20, 2010
 * Time: 7:02:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class JOGLFont extends Font {
    static boolean PACKED_TEXT = true;
    public boolean initialized = false;
    public TextRenderer tr;
    JFont packedFont;

    public JOGLFont(String name, float size, boolean vector, File file) {
        super(name, size, vector, Font.Weight.Regular, Font.Style.Regular, file, null);
    }

    public void initialize() {



        tr = new TextRenderer(
                new java.awt.Font(name, java.awt.Font.PLAIN, (int)size), // jogltext.font
                true, // anti aliased
                false, //use fractional metrics
                null, // delegate
                false //mipmap
        );
        tr.setColor(0, 0, 0 , 1);

        if(PACKED_TEXT) {
            packedFont = new PackedFont(name,size,vector,fnt);
        }
        initialized = true;
    }
}
