package org.joshy.gfx.util;

import org.w3c.dom.Element;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: May 20, 2010
 * Time: 9:41:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class URLUtils {
    public static URI calculateURL(Element val, String uri) throws URISyntaxException {
        URI parentURI = new URI(val.getOwnerDocument().getDocumentURI());
//        u.p("parent uri = " + parentURI);
        URI src = new URI(uri);
//        u.p("src = " + src);
        URI resolved = parentURI.resolve(src);
//        u.p("resolved = " + resolved);
        if(parentURI.toString().startsWith("jar:")) {
            String s = parentURI.toString();
            s = s.substring(0,s.lastIndexOf("/"));
//            u.p("chopped to: " + s);
            s = s + "/"+src.toString();
//            u.p("extended to : " + s);
            resolved = new URI(s);
//            u.p("resolved = " + resolved);
        }
        return resolved;
    }

    public static URI safeURIResolve(URI baseURI, URI imageURI) throws URISyntaxException {
        if(baseURI.toASCIIString().startsWith("jar:")) {
            baseURI = new URI(baseURI.toASCIIString().substring(4));
            URI uri = baseURI.resolve(imageURI);
            return new URI("jar:"+uri.toASCIIString());
        }
        return baseURI.resolve(imageURI);
    }
}
