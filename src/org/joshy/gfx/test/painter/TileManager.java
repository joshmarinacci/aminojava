package org.joshy.gfx.test.painter;

import org.joshy.gfx.draw.Image;
import org.joshy.gfx.stage.swing.SwingImage;
import org.joshy.gfx.util.u;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Feb 19, 2010
 * Time: 8:55:45 PM
 * To change this template use File | Settings | File Templates.
 */
class TileManager {
    private Image dummy;
    private Image dummy2;
    private Map<String, Image> map = new HashMap<String, Image>();

    TileManager() {
        dummy = Image.create(new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB));
        BufferedImage d2 = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = d2.createGraphics();
        g2.setPaint(Color.BLACK);
        g2.fillRect(10, 10, 50, 50);
        g2.dispose();
        dummy2 = Image.create(d2);
    }

    public Image getImage(int x, int y) {
        String key = x + "x" + y;
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return dummy;
    }

    public void fillBrushAt(double x, double y) {
        int tilex = (int) (Math.floor(x / 256.0));
        int tiley = (int) (Math.floor(y / 256.0));
        double offx = x - tilex * 256;
//        u.p("tilex = " + tilex + " offx = " + offx);
        double offy = y - tiley * 256;
        double radius = 10.0;
        String key = tilex + "x" + tiley;
//        u.p("key = " + key);
        if (!map.containsKey(key)) {
//            u.p("creating");
            BufferedImage buf = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
            map.put(key, Image.create(buf));
        }
        SwingImage img = (SwingImage) map.get(key);
        Graphics2D g2 = img.buffer.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(Color.BLACK);
        g2.fillOval((int) (offx - radius), (int) (offy - radius), (int) radius * 2, (int) radius * 2);
        g2.dispose();
//        u.p("tile count = " + map.size());
    }
}
