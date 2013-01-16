package org.joshy.gfx.util.image;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/*
 * @author joshua@marinacci.org
 */
class CacheUtil {

    static File getCachePath(File cacheDir, String url, int requestedWidth, int requestedHeight, SizingMethod sizing) throws URISyntaxException {
        //System.out.println("dealing with url: " + url);
        URI uri = new URI(url);
        String host = uri.getHost().replace('.', '_');
        //p("host = " + host);
        String path = uri.getPath().replace('/', '_');
        //p("path = " + path);
        //p("query = " + uri.getQuery());
        String extension = path.substring(path.lastIndexOf('.')+1);
        //p("extension = " + extension);

        File imagesDir = new File(cacheDir, "images");
        File hostDir = new File(imagesDir,host);
        File pathDir = new File(hostDir, path);
        if(requestedWidth > 0 || requestedHeight > 0) {
            extension = "png";
        }
        File image = new File(pathDir,"image_"+sizing.toString()+"_"+requestedWidth+"_"+requestedHeight+"."+extension);
        //p("image path = " + image.getPath());
        return image;

    }

    
    static void recursiveDelete(File cacheDir) {
        if(cacheDir.isDirectory() && cacheDir.exists()) {
            for(File f : cacheDir.listFiles()) {
                recursiveDelete(f);
            }
        }
        if(cacheDir.exists()) {
            cacheDir.delete();
        }
    }


}
