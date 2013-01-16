package org.joshy.gfx.text;

import com.sun.opengl.util.texture.awt.AWTTextureIO;

import javax.media.opengl.GL2;
import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Feb 6, 2010
 * Time: 4:27:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class SquareFont extends JFont {
    public SquareFont(String name, double size, Font font) {
        super(name, size, false, font);
    }

    @Override
    protected void init() {
        if (texture == null) {
            System.out.println("generating texture");
            BufferedImage buffer = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = buffer.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setPaint(new Color(255, 255, 255, 0));
            g.fillRect(0, 0, buffer.getWidth(), buffer.getHeight());
            g.setPaint(new Color(255, 255, 255));
            g.setFont(font);
            int count = 0;
            for (char ch = ' '; ch <= '~'; ch++) {
                int x = (count % 16) * 16;
                int y = (int) (count / 16) * 16;
                //int y = 0;
                g.drawString("" + ch, x, y + 16 - 3);
                count++;
            }
            g.dispose();
            texture = AWTTextureIO.newTexture(buffer, false);
            //        texture.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
            //        texture.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
            //        texture.setTexParameteri(GL.GL_TEXTURE_WRAP_S,GL.GL_REPEAT);
            //        texture.setTexParameteri(GL.GL_TEXTURE_WRAP_T,GL.GL_REPEAT);
        }

    }

    @Override
    protected void drawVector(GL2 gl, String text) {

    }

    @Override
    Dimension2D getCharLayoutDimension(int ch) {
        return new Dimension2D() {
            @Override
            public double getWidth() {
                return 16;
            }

            @Override
            public double getHeight() {
                return 16;
            }

            @Override
            public void setSize(double width, double height) {
                //no op
            }
        };

    }

    @Override
    Rectangle2D getCharTextureBounds(int ch) {
        int chx = ch % 16;
        int chy = ch / 16;
        return new Rectangle2D.Double(
                ((double)chx)*1.0/16.0,
                ((double)chy)*1.0/16.0,
                1.0/16.0,
                1.0/16.0
                );
    }

    @Override
    int getMaxAscent() {
        return 16;
    }

}
