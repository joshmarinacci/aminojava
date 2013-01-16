package org.joshy.gfx.test.control;

import org.joshy.gfx.Core;
import org.joshy.gfx.node.control.Scrollbar;
import org.joshy.gfx.node.control.Slider;
import org.joshy.gfx.node.layout.Panel;
import org.joshy.gfx.stage.Stage;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 29, 2010
 * Time: 1:15:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class DragControls implements Runnable {

    public static void main(String ... args) throws Exception {
        Core.setUseJOGL(false);
        Core.init();
        Core.getShared().defer(new DragControls());
    }

    public void run() {
        try {
            
            Panel p = new Panel();
            Stage s = Stage.createStage();
            s.setContent(p);

            Scrollbar scroll = new Scrollbar(false);
            scroll.setHeight(20);
            scroll.setWidth(200);
            scroll.setMin(0);
            scroll.setMax(100);
            scroll.setValue(50);
            scroll.setTranslateX(50);
            scroll.setTranslateY(50);
            p.add(scroll);

            Scrollbar vs = new Scrollbar(true);
            vs.setWidth(20);
            vs.setHeight(200);
            vs.setMin(0);
            vs.setMax(100);
            vs.setValue(50);
            vs.setTranslateX(300);
            vs.setTranslateY(100);
            p.add(vs);


            Slider slider = new Slider(false);
            slider.setWidth(200);
            slider.setHeight(20);
            slider.setTranslateX(50);
            slider.setTranslateY(200);
            p.add(slider);

            Slider vslider = new Slider(true);
            vslider.setWidth(20);
            vslider.setHeight(200);
            vslider.setTranslateX(200);
            vslider.setTranslateY(250);
            vslider.setMin(0);
            vslider.setMax(100);
            vslider.setValue(50);
            p.add(vslider);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
