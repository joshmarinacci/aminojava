package org.joshy.gfx.node.control;

import java.io.IOException;
import java.net.URL;
import org.joshy.gfx.draw.GFX;
import org.joshy.gfx.draw.Image;

/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2/6/13
 * Time: 11:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class ImageView extends Control {

    private URL source;
    private Image image;

    public ImageView() {
    }

    @Override
    public void doLayout() {
    }

    @Override
    public void doPrefLayout() {
        setWidth(prefWidth);
        setHeight(prefHeight);
    }

    @Override
    public void doSkins() {
    }

    @Override
    public void draw(GFX g) {
        if(image == null) {
            try {
                image = Image.getImageFromCache(source);
                g.drawImage(image, 0, 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            g.drawImage(image, 0, 0);
        }
    }

    public ImageView setSource(URL source) {
        this.source = source;
        return this;
    }

    public URL getSource() {
        return source;
    }
}
