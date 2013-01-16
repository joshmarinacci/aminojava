/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.joshy.gfx.util.image;

import java.awt.image.BufferedImage;
import java.net.URL;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author josh
 */
public class BasicTest {
    public static void main(String ... args) throws Exception {
        MasterImageCache cache = new MasterImageCache(false, 4, "test");
        
        String feedURL = "http://api.flickr.com/services/feeds/photos_public.gne?tags=cat&lang=en-us&format=rss_200";
        XPath xpath = XPathFactory.newInstance().newXPath();
        NodeList nodes = (NodeList) xpath.evaluate("//item",
                new InputSource(new URL(feedURL).openStream()),
                XPathConstants.NODESET);
        for(int i=0; i<nodes.getLength(); i++) {
            Element e = (Element) nodes.item(i);
            NodeList contents = e.getElementsByTagName("media:content");
            Element content = ((Element)contents.item(0));
            final String url = content.getAttribute("url");
            p("w = " + content.getAttribute("width"));
            NodeList thumbs = e.getElementsByTagName("media:thumbnail");
            String thumbUrl = ((Element)thumbs.item(0)).getAttribute("url");
            cache.getImage(url, thumbUrl, 400, 400, SizingMethod.Stretch, new MasterImageCache.Callback() {
                public void fullImageLoaded(BufferedImage image) {
                    p("full loaded: " + image);
                }
                public void thumnailImageLoaded(BufferedImage image) {
                    p("thumb loaded: " + image);
                }
                public void error() {
                }
            });
        }
    }

    private static void p(String string) {
        System.out.println(string);
    }
}
