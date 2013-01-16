package org.joshy.gfx.css;

import org.joshy.gfx.node.control.Control;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Jul 31, 2010
 * Time: 1:05:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class CSSMatcher {
    public String element;
    public String pseudo;
    public String pseudoElement;
    public String id;
    public Set<String> classes = new HashSet<String>();
    public CSSMatcher parent = null;
    public CSSRule rule;
    Control control;

    public CSSMatcher() {
    }

    public CSSMatcher(String element) {
        this.element = element;
    }

    public CSSMatcher(Control c) {
        this.control = c;
        this.element = c.getClass().getSimpleName();
        this.id = c.getId();
        classes.addAll(c.getCSSClasses());
    }

    @Override
    public String toString() {
        return "CSSMatcher{" +
                "element='" + element + '\'' +
                ", pseudo='" + pseudo + '\'' +
                ", pseudoElement='" + pseudoElement + '\'' +
                ", id='" + id + '\'' +
                ", classes=" + classes +
                ", parent=" + parent +
                '}';
    }
}
