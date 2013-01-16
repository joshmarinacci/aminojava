package org.joshy.gfx.test.painter;

import org.joshy.gfx.Core;
import org.joshy.gfx.node.control.SwatchColorPicker;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.stage.Stage;


/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Feb 16, 2010
 * Time: 5:43:38 PM
 * <p/>
 * basic design. a data structure which vends tiles on demand for painting. it creates tiles internally
 * on demand when drawing on to them with a brush. tiles which are requested for painting but which haven't
 * actually be drawn into yet return a fully transparent dummy tile. tiles are 256x256.
 * <p/>
 * app lets you draw by dragging the mouse cursor, and pan canvas by dragging + shift. you can pan forever
 * in any direction.
 * <p/>
 * end of V1.
 */

public class Painter implements Runnable {
    private TileManager manager;

    public static void main(String... args) throws Exception {
        Core.setUseJOGL(false);
        Core.init();
        Core.getShared().defer(new Painter());
    }

    public void run() {
        Stage stage = Stage.createStage();
        this.manager = new TileManager();

//        manager.fillBrushAt(100, 100);
//        manager.fillBrushAt(200, 200);
//        manager.fillBrushAt(300, 300);
        final CanvasNode node = new CanvasNode(this.manager);
        Panel panel = new Panel() {
            @Override
            public void doLayout() {
                super.doLayout();    //To change body of overridden methods use File | Settings | File Templates.
                node.setWidth(getWidth());
                node.setHeight(getHeight());
            }
        };
        panel.add(node);

        SwatchColorPicker color1 = null;
        color1 = new SwatchColorPicker();
        panel.add(color1);



        stage.setContent(panel);

    }

}

