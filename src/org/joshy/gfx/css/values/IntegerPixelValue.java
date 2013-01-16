package org.joshy.gfx.css.values;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: Jul 28, 2010
* Time: 9:46:04 PM
* To change this template use File | Settings | File Templates.
*/
public class IntegerPixelValue extends BaseValue {
    private int value;

    public IntegerPixelValue(String prevText) {
        this.value = Integer.parseInt(prevText.substring(0,prevText.length()-2));
    }

    @Override
    public String asString() {
        return value+"px";
    }

    public int getValue() {
        return value;
    }
}
