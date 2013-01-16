package org.joshy.gfx.css;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Jul 28, 2010
 * Time: 4:44:14 PM
 * To change this template use File | Settings | File Templates.
 *
 * represents a single matcher with a set of property assignments within it
 * 
 */
public class CSSRule {
    public List<CSSMatcher> matchers = new ArrayList<CSSMatcher>();
    private List<CSSProperty> properties = new ArrayList<CSSProperty>();
    public URI baseURI;

    public Iterable<? extends CSSProperty> getProperties() {
        return properties;
    }

    public void addProperty(CSSProperty cssProperty) {
        this.properties.add(cssProperty);
    }
    
    public URI getBaseURI() {
        return baseURI;
    }

    public void setBaseURI(URI uri) {
        baseURI = uri;
    }

}
