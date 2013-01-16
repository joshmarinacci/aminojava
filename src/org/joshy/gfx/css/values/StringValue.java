package org.joshy.gfx.css.values;

/**
* Created by IntelliJ IDEA.
* User: joshmarinacci
* Date: Jul 28, 2010
* Time: 9:45:41 PM
* To change this template use File | Settings | File Templates.
*/
public class StringValue extends BaseValue {
    private String string;

    public StringValue(String string) {
        this.string = string;
    }

    @Override
    public String asString() {
        return string;
    }
}
