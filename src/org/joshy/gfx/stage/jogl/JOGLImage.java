package org.joshy.gfx.stage.jogl;

import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.awt.AWTTextureIO;
import org.joshy.gfx.draw.Image;

import javax.media.opengl.GL;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 21, 2010
 * Time: 8:46:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class JOGLImage extends Image {
    private BufferedImage buffer;
    private boolean initialized;
    public Texture texture;

    public JOGLImage(BufferedImage buffer) {
        super();
        this.buffer = buffer;
        initialized = false;
    }

    public void initialize() {
        if(!initialized) {
            texture = AWTTextureIO.newTexture(buffer,true);
            texture.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
            texture.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
            texture.setTexParameteri(GL.GL_TEXTURE_WRAP_S,GL.GL_REPEAT);
            texture.setTexParameteri(GL.GL_TEXTURE_WRAP_T,GL.GL_REPEAT);
            initialized = true;
        }
    }

    @Override
    public int getWidth() {
        return buffer.getWidth();
    }

    @Override
    public int getHeight() {
        return buffer.getHeight();
    }
}
