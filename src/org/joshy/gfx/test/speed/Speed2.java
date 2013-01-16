package org.joshy.gfx.test.speed;

import org.joshy.gfx.Core;
import org.joshy.gfx.SkinManager;
import org.joshy.gfx.css.CSSMatcher;
import org.joshy.gfx.css.CSSRuleSet;
import org.joshy.gfx.css.CSSSkin;
import org.joshy.gfx.event.Callback;
import org.joshy.gfx.node.control.Button;
import org.joshy.gfx.util.u;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Nov 3, 2010
 * Time: 4:41:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class Speed2 implements Runnable {
    public static void main(String ... args) throws Exception {
        Core.init();
        Core.getShared().defer(new Speed2());
    }

    @Override
    public void run() {
        CSSSkin cssSkin = SkinManager.getShared().getCSSSkin();
        final CSSRuleSet set = cssSkin.getCSSSet();
        final CSSMatcher matcher = CSSSkin.createMatcher(new Button(), CSSSkin.State.None);
        //currently takes about 0.3ms to 0.7ms per findStringValue. Needs to be much much faster
        final String prefix = "";
        long t = time(new Callback() {
            @Override
            public void call(Object event) throws Exception {
                for (int i = 0; i < 100000; i++) {
                    set.findStringValue(matcher, prefix + "background-color");
                }
            }
        });
        u.p("time = " + t);
    }
    private static long time(Callback callback) {
        long start = System.currentTimeMillis();
        try {
            callback.call(null);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        long end = System.currentTimeMillis();
        return end-start;
    }
}
