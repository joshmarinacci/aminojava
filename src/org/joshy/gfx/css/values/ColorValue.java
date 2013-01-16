package org.joshy.gfx.css.values;

import java.util.HashMap;
import java.util.Map;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: Jul 28, 2010
* Time: 9:45:54 PM
* To change this template use File | Settings | File Templates.
*/
public class ColorValue extends BaseValue {
    private static Map<String,Integer> colorMap;
    static {
        colorMap = new HashMap<String, Integer>();
        colorMap.put("red",    0xff0000);
        colorMap.put("green",  0x00ff00);
        colorMap.put("blue",   0x0000ff);
        colorMap.put("black",  0x000000);
        colorMap.put("white",  0xffffff);
        colorMap.put("yellow", 0x00ffff);
    }
    private int rgb;

    public ColorValue(int rgba, boolean hasAlpha) {
        this.rgb = rgba;
        //chop the alpha off if needed
        if(!hasAlpha) {
            this.rgb = rgb & 0x00FFFFFF;
        }
    }
    public ColorValue(String text) {
        if(colorMap.containsKey(text.toLowerCase())) {
            this.rgb = colorMap.get(text.toLowerCase());
            return;
        }
        this.rgb = 0XFF000000 | Integer.parseInt(text.substring(1),16);
    }

    @Override
    public String asString() {
        return Integer.toHexString(rgb);
    }

    public int getValue() {
        return rgb;
    }
}
