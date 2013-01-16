package org.joshy.gfx.css.values;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Aug 3, 2010
 * Time: 6:57:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class StringListValue extends BaseValue {
    private List<String> list;

    public StringListValue(String[] strings) {
        list = new ArrayList<String>();
        for(String s : strings) {
            list.add(s.trim());
        }
        //To change body of created methods use File | Settings | File Templates.
    }

    @Override
    public String asString() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<String> getList() {
        return list;
    }
}
