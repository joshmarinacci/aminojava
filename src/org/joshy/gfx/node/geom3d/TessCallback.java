package org.joshy.gfx.node.geom3d;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUtessellatorCallback;

/**
 * Created by IntelliJ IDEA.
 * User: joshmarinacci
 * Date: Apr 17, 2010
 * Time: 10:09:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class TessCallback implements GLUtessellatorCallback {
    private GL2 gl;
    private GLU glu;

    public TessCallback(GL2 gl, GLU glu) {
        this.gl = gl;
        this.glu = glu;
    }

    public void begin(int type) {
        gl.glBegin(type);
    }

    public void beginData(int i, Object o) {
    }

    public void edgeFlag(boolean b) {
    }

    public void edgeFlagData(boolean b, Object o) {
    }

    public void vertex(Object vertexData) {
        double[] pointer;
        if (vertexData instanceof double[])
        {
            pointer = (double[]) vertexData;
            if (pointer.length == 6) gl.glColor3dv(pointer, 3);
            gl.glVertex3dv(pointer, 0);
        }
    }

    public void vertexData(Object o, Object o1) {
    }

    public void end() {
        gl.glEnd();
    }

    public void endData(Object o) {
    }

    public void combine(double[] coords, Object[] data, float[] weight, Object[] outData) {
        double[] vertex = new double[6];
        int i;

        vertex[0] = coords[0];
        vertex[1] = coords[1];
        vertex[2] = coords[2];
        for (i = 3; i < 6/* 7OutOfBounds from C! */; i++)
            vertex[i] = weight[0] //
                    * ((double[]) data[0])[i] + weight[1]
                    * ((double[]) data[1])[i] + weight[2]
                    * ((double[]) data[2])[i] + weight[3]
                    * ((double[]) data[3])[i];
        outData[0] = vertex;
    }

    public void combineData(double[] doubles, Object[] objects, float[] floats, Object[] objects1, Object o) {
    }

    public void error(int errnum) {
        String estring;

        estring = glu.gluErrorString(errnum);
        System.err.println("Tessellation Error: " + estring);
        System.exit(0);
    }

    public void errorData(int i, Object o) {
    }
}
