/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.terrain;

import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;

/**
 * Is needed for the height map terrain.
 * @author Thomas Tosik,double1984
 */
public class MultMesh extends Mesh {
    private int xUnit, zUnit;
    private int xUp, zUp;
    private Vector3f[][] vertex;

    /**
     *
     * @param pts
     */
    public MultMesh(Vector3f[][] pts) {
        vertex = pts;
        zUnit = pts.length - 1;
        xUnit = pts[0].length - 1;
        zUp = pts.length;
        xUp = pts[0].length;
    }

    /**
     *
     */
    public void initMesh() {

        this.setMode(Mode.Triangles);
        float[] positions = new float[xUp * zUp * 3];
        int count = 0;
        for (int i = 0; i < zUp; i++) {
            for (int j = 0; j < xUp; j++) {
                positions[count] = vertex[i][j].x;
                count++;
                positions[count] = vertex[i][j].y;
                count++;
                positions[count] = vertex[i][j].z;
                count++;
            }
        }
        this.setBuffer(Type.Position, 3, positions);

        count = 0;
        int[] indexs = new int[xUnit * zUnit * 6];
        for (int i = 0; i < zUnit; i++) {
            for (int j = 0; j < xUnit; j++) {
                indexs[count] = i * xUp + j;
                count++;
                indexs[count] = i * xUp + xUp + j;
                count++;
                indexs[count] =  i * xUp + xUp + j + 1;
                count++;
                indexs[count] = i * xUp + j;
                count++;
                indexs[count] = i * xUp + xUp + j + 1;
                count++;
                indexs[count] = i * xUp + j + 1;
                count++;
            }
        }
        this.setBuffer(Type.Index, 3, indexs);

        count = 0;
        boolean xShift = true;
        boolean zShift = true;
        int texCoodSize = (xUnit + 1) * (zUnit + 1) * 2;
        float[] texCood = new float[texCoodSize];

        float xstep = 1f / (xUp - 1);
        float zstep = 1f / (zUp - 1);
        for (int i = 0; i < zUp; i++) {
            for (int j = 0; j < xUp; j++) {
                texCood[count] =j * xstep;
                count++;
                texCood[count] = 1-i * zstep;
                count++;
            }
        }

        this.setBuffer(Type.TexCoord, 2, texCood);

        count = 0;
        float[] vertexColors = new float[xUp * zUp * 4];
        for (int i = 0; i < xUp * zUp; i++) {
            vertexColors[count] = (float) Math.random();
            count++;
            vertexColors[count] = (float) Math.random();
            count++;
            vertexColors[count] = (float) Math.random();
            count++;
            vertexColors[count] = (float) Math.random();
            count++;
        }

        this.setBuffer(Type.Color, 4, vertexColors);
    }

    /**
     *
     * @return
     */
    public Vector3f[][] getVertex() {
        return this.vertex;
    }
}
