package org.joshy.gfx.css;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Sep 6, 2010
 * Time: 10:30:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class CSSPropertySet {
    private List<CSSProperty> props = new ArrayList<CSSProperty>();

    public void add(CSSProperty ... props) {
        this.props.addAll(Arrays.asList(props));
    }

    public Iterable<CSSProperty> getProps() {
        return props;
    }
}