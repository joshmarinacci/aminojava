package org.joshy.gfx.stage.jogl;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.awt.AWTTextureIO;
import org.joshy.gfx.draw.GridNine;
import org.joshy.gfx.util.u;

import javax.media.opengl.GL;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 20, 2010
 * Time: 5:41:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class JOGLGridNine extends GridNine {
    Texture texture;
    public boolean initialized = false;
    private BufferedImage image;

    public JOGLGridNine(BufferedImage image, double top, double right, double bottom, double left, boolean flipx) {
        super(top,right,bottom,left, flipx);
        this.image = image;
    }
    public void initialize() {
        texture = AWTTextureIO.newTexture(image,true);
        texture.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
        texture.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        texture.setTexParameteri(GL.GL_TEXTURE_WRAP_S,GL.GL_REPEAT);
        texture.setTexParameteri(GL.GL_TEXTURE_WRAP_T,GL.GL_REPEAT);
        initialized = true;
    }
}
