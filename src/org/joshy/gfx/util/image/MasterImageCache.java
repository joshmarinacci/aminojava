package org.joshy.gfx.util.image;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Caches images. The file path for each image constructed like this:
 * <rootdir>/images/<hostname>/<path>/image_w_h.<extension>
 *
 * root dir is specified when instantiating this object
 * the hostname is the hostname of the url with periods replaced with underscores
 * the path is the path of the url with slashes replaced with underscores, including the filename
 * the actual on disk filename is image followed by the width and height if this is scaled, or 0_0
 * if this is the full size image (regardless of the actual size), followed by the original file extension, if available.
 *
 * callbacks will always be called on the GUI thread
 *
 * @author joshua@marinacci.org
 */

public class MasterImageCache {
    private final boolean useBackgroundThreads = true;
    private ExecutorService pool;

    public MasterImageCache(boolean deleteOnStart, int threadCount, String cacheName) {
        initCache(cacheName, deleteOnStart);
        pool = Executors.newFixedThreadPool(threadCount);
    }

    public void shutdown() {
        pool.shutdownNow();
    }

    public File getCacheDir() {
        return cacheDir;
    }

    private File cacheDir;

    public void cleanCache() {
        CacheUtil.recursiveDelete(cacheDir);
        if(!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }

    private void initCache(String cacheName, boolean deleteOnStart) {
        String BASE_DIR = "/tmp/FXImageCache/";
        BASE_DIR = org.joshy.gfx.util.OSUtil.getBaseStorageDir(cacheName);
        p("MasterImageCache: using dir: " + BASE_DIR);
        cacheDir = new File(BASE_DIR);
        if(deleteOnStart) {
            CacheUtil.recursiveDelete(cacheDir);
        }
        if(!cacheDir.exists()) {
            cacheDir.mkdirs();
            //TODO: handle the exception when the dir doesn't exist
        }
    }

    public void getImage(String url, String thumbnail, int requestedWidth, int requestedHeight, SizingMethod sizing, Callback callback) {
        p("getting");
        p("url = " + url);
        p("thumbnail = " + thumbnail);
        if(thumbnail != null && !thumbnail.trim().equals("")) {
            processImage(thumbnail, requestedWidth, requestedHeight, sizing, callback, true, useBackgroundThreads);
        }
        if(url != null && !url.trim().equals("")) {
            processImage(url, requestedWidth, requestedHeight, sizing, callback, false, useBackgroundThreads);
        }
    }

    // an internal threadsafe map of callbacks
    final Map<String,List<Callback>> imageMap = new Hashtable<String,List<Callback>>();

    private void processImage(final String url, final int requestedWidth, final int requestedHeight, final SizingMethod sizing, final Callback callback, final boolean isThumbnail, boolean useAnotherThread) {
        if (useAnotherThread) {
            pool.submit(new Runnable() {
                public void run() {
                    Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
                    processImage(url, requestedWidth, requestedHeight, sizing, callback, isThumbnail);
                }

            });
        } else {
            processImage(url, requestedWidth, requestedHeight, sizing, callback, isThumbnail);
        }
    }

    private void processImage(final String url, final int requestedWidth, final int requestedHeight, final SizingMethod sizing, final Callback callback, final boolean isThumbnail) {
        try {
            // check if resized image exists in the cache
            synchronized(imageMap) {
                if(imageMap.containsKey(url)) {
                    p("======== we are already loading this image!");
                    imageMap.get(url).add(callback);
                    return;
                }
                imageMap.put(url, new ArrayList<Callback>());
                imageMap.get(url).add(callback);
            }

            File resizedFile = CacheUtil.getCachePath(cacheDir, url, requestedWidth, requestedHeight, sizing);
            if(resizedFile.exists()) {
                // return image
                p("resized image is in the cache");
                loadImage(resizedFile, isThumbnail, url);
                return;
            }

            downloadAndScaleImage(url, resizedFile, requestedWidth, requestedHeight, sizing, isThumbnail);

        } catch (Exception ex) {
            if (!"bandOffsets.length is wrong!".equals(ex.getMessage())) {
                System.err.println("Problems with the following url: " + url);
                ex.printStackTrace();
            }
        }
    }

    
    private void downloadAndScaleImage(String url, File resizedFile, int requestedWidth, int requestedHeight,
            SizingMethod sizing, boolean isThumbnail) throws URISyntaxException, IOException {
        // check if unresized image exists in the cache
        File fullFile = CacheUtil.getCachePath(cacheDir, url, 0, 0, SizingMethod.Preserve);
        //p("full file = " + fullFile.getAbsolutePath());
        if(!fullFile.exists()) {
            //p("image not in the cache: " + url);
            downloadToCache(fullFile, url);
        } else {
            //p("image is in the cache:  " + url);
        }

        // generate scaled image into cache
        resizedFile = generateScaledToCache(fullFile, resizedFile, requestedWidth, requestedHeight, sizing, isThumbnail);
        // return image
        //p("final resized image = " + resizedFile.getAbsolutePath());
        //p("exists = " + resizedFile.exists());
        loadImage(resizedFile, isThumbnail, url);
    }

    private void downloadToCache(File fullFile, String url) throws MalformedURLException, IOException {
        // create full path if necessary
        fullFile.getParentFile().mkdirs();

        byte[] buff = new byte[1024*16];
        InputStream in = new BufferedInputStream(new URL(url).openStream());
        OutputStream out = new FileOutputStream(fullFile);
        while(true) {
            int n = in.read(buff);
            if(n < 0) {
                break;
            }
            out.write(buff, 0, n);
            //p("read " + n);
        }
        out.close();
        in.close();
    }

    static public interface Callback {
        public void fullImageLoaded(BufferedImage image);
        public void thumnailImageLoaded(BufferedImage image);
        public void error();
    }

    private void loadImage(File resizedFile, final boolean isThumbnail, final String originalURL) throws IOException {
        final BufferedImage image = ImageIO.read(resizedFile);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                synchronized(imageMap) {


                    List<Callback> callbacks = imageMap.get(originalURL);
                    if(callbacks == null || callbacks.size() < 1) {
                        p("=========================== Major Error. No callbacks for this url!!!");
                        return;
                    }
                    for(Callback cb : callbacks) {
                        if(isThumbnail) {
                            cb.thumnailImageLoaded(image);
                        } else {
                            cb.fullImageLoaded(image);
                        }
                    }
                            p("removing from map: " + originalURL);
                    imageMap.remove(originalURL);
                }
            }
        });
    }
    
    private File generateScaledToCache(File fullFile, File resizedFile, int requestedWidth, int requestedHeight, SizingMethod sizing, boolean isThumbnail) throws IOException {
        p("generating scaled to cache: full file =" + fullFile.getAbsolutePath());
        p("resizing to " + requestedWidth + " x " + requestedHeight + " = " + resizedFile.getAbsolutePath());
        BufferedImage fullImage = ImageIO.read(fullFile);
        BufferedImage scaledImage = ScaleUtil.scaleImage(fullImage,requestedWidth,requestedHeight,sizing, isThumbnail);
        File finalFile = new File(resizedFile.getParent(),"image_"+sizing.toString()+"_"+requestedWidth+"_"+requestedHeight+".png");
        ImageIO.write(scaledImage, "png", finalFile);
        return finalFile;
    }

    private void p(String string) {
        //System.out.println(string);
    }

}
