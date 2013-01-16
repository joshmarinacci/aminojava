package org.joshy.gfx.draw;

import org.joshy.gfx.Core;
import org.joshy.gfx.stage.jogl.JOGLImage;
import org.joshy.gfx.stage.swing.SwingImage;
import org.joshy.gfx.util.u;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 18, 2010
 * Time: 3:01:36 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Image {
    private static Map<URL,Image> cache;

    protected Image() {
    }

    public static Image create(BufferedImage buffer) {
        if(Core.getShared().isUseJOGL()) {
            return new JOGLImage(buffer);
        } else {
            return new SwingImage(buffer);
        }
    }
    public static Image create(int w, int h) {
        BufferedImage img = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
        return create(img);
    }

    public abstract int getWidth();
    public abstract int getHeight();

    public static Image getImageFromCache(URL url) throws IOException {
        if(cache == null) {
            cache = new HashMap<URL,Image>();
        }
        if(cache.containsKey(url)) {
            return cache.get(url);
        } else {
            try {
                Image image = create(ImageIO.read(url));
                cache.put(url,image);
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
                Image img = create(20,20);
                cache.put(url,img);
            }
            return cache.get(url);
        }
    }
}
