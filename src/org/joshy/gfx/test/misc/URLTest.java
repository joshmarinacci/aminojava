package org.joshy.gfx.test.misc;

import org.joshy.gfx.util.URLUtils;
import org.joshy.gfx.util.u;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Aug 3, 2010
 * Time: 6:25:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class URLTest {
    public static void main(String ... args) throws URISyntaxException {
        URI baseURI = new URI("jar:file:/Users/joshmarinacci/projects/personal/t/leonardosketch/build/jars/Core.jar!/org/joshy/gfx/stage/swing/default.css");
        //URI baseURI = new URI("file:/Users/joshmarinacci/projects/personal/t/leonardosketch/build/jars/Core.jar!/org/joshy/gfx/stage/swing/default.css");
        //URI baseURI = new URI("http://www.yahoo.com/default.css");
        u.p("base uri = " + baseURI);
        URI imageURI = new URI("images/checkbox_normal.png");
        u.p("image uri = " + imageURI);
        //URI resolvedImageURI = baseURI.resolve(imageURI);
        URI resolvedImageURI = URLUtils.safeURIResolve(baseURI,imageURI);
        u.p("resolved = " + resolvedImageURI);

    }

}
