package org.joshy.gfx.test.threedee;

import org.joshy.gfx.Core;
import org.joshy.gfx.anim.PropertyAnimator;
import org.joshy.gfx.draw.FlatColor;
import org.joshy.gfx.draw.Transform;
import org.joshy.gfx.draw.TransformNode;
import org.joshy.gfx.node.shape.Rectangle;
import org.joshy.gfx.stage.PerspectiveCamera;
import org.joshy.gfx.stage.Stage;

/**
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jan 26, 2010
 * Time: 9:35:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class SpinningRectangle implements Runnable {
    public static void main(String ... args) throws Exception, InterruptedException {
        Core.setUseJOGL(true);
        Core.init();
        Core.getShared().defer(new SpinningRectangle());

    }

    public void run() {
        Rectangle r = new Rectangle();
        r.setWidth(100);
        r.setHeight(100);
        r.setTranslateX(-50);
        r.setTranslateY(-50);
        r.setTranslateZ(0);
        r.setFill(FlatColor.RED);

        TransformNode t = new TransformNode();
        t.setContent(r);
        t.setAxis(Transform.Y_AXIS);
        t.setTranslateX(0);
        t.setTranslateY(0);
        Stage s = Stage.create3DStage();
        s.setCamera(new PerspectiveCamera());
        s.setContent(t);

        PropertyAnimator anim = PropertyAnimator.target(t)
                .property("rotate")
                .startValue(0)
                .endValue(360)
                .seconds(4)
                .repeat(PropertyAnimator.INDEFINITE);
        anim.start();
    }
}
