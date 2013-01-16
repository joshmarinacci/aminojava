package org.joshy.gfx.animation;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: Apr 16, 2010
* Time: 7:02:38 PM
* To change this template use File | Settings | File Templates.
*/
public class KeyFrame {
    double time;
    double keyValue;

    KeyFrame(double time, double keyValue) {
        this.time = time;
        this.keyValue = keyValue;
    }
}
