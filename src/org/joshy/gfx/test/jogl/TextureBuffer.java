package org.joshy.gfx.test.jogl;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Apr 29, 2010
 * Time: 12:29:51 PM
 * To change this template use File | Settings | File Templates.
 */
class TextureBuffer {
    public int[] frameBuffer = new int[1];
    public int[] depthBuffer = new int[1];
    public int[] img = new int[1];
    int width;
    int height;

    public TextureBuffer(int width, int height) {
        this.width = width;
        this.height = height;
    }
}
