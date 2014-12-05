/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.Helper;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * <code>Pyramid</code> is a four sided pyramid.
 * 
 * <p>
 * A pyramid is defined by a width at the base and a height. The pyramid is a
 * 4-sided pyramid with the center at (0,0), it will be axis aligned with the
 * peak being on the positive y axis and the base being in the x-z plane.
 * <p>
 * The texture that defines the look of the pyramid has the top point of the
 * pyramid as the top center of the texture, with the remaining texture wrapping
 * around it.
 * 
 * Based on Code from Mark Powell (JME2)
 * http://hub.jmonkeyengine.org/forum/topic/jme3-pyramid/
 *
 * @author Denis Hock
 */
public class Pyramid extends Mesh {

    private float width, height;

    /**
     * Empty constructor for serialization only, do not use.
     */
    public Pyramid() {
    }

    /**
     * Constructor instantiates a new <code>Pyramid</code> object. The base
     * width and the height are provided.
     *     
     * @param width the base width of the pyramid.
     * @param height the height of the pyramid from the base to the peak.
     */
    public Pyramid(float width, float height) {
        updateGeometry(width, height);
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    /**
     * builds the vertices based on the radius, radial and zSamples.
     */
    private void setGeometryData() {

        // Update the vertex buffer
        float pkx = 0, pky = height / 2, pkz = 0;
        float vx0 = -width / 2, vy0 = -height / 2, vz0 = -width / 2;
        float vx1 = width / 2, vy1 = -height / 2, vz1 = -width / 2;
        float vx2 = width / 2, vy2 = -height / 2, vz2 = width / 2;
        float vx3 = -width / 2, vy3 = -height / 2, vz3 = width / 2;
        FloatBuffer verts = BufferUtils.createVector3Buffer(16);
        verts.put(new float[]{
            vx3, vy3, vz3, vx2, vy2, vz2, vx1, vy1, vz1, vx0, vy0, vz0, // base
            vx0, vy0, vz0, vx1, vy1, vz1, pkx, pky, pkz, // side 1
            vx1, vy1, vz1, vx2, vy2, vz2, pkx, pky, pkz, // side 2
            vx2, vy2, vz2, vx3, vy3, vz3, pkx, pky, pkz, // side 3
            vx3, vy3, vz3, vx0, vy0, vz0, pkx, pky, pkz // side 4
        });
        verts.rewind();
        setBuffer(Type.Position, 3, verts);

        // Update the normals buffer
        FloatBuffer norms = BufferUtils.createVector3Buffer(16);
        float pn = 0.70710677f, nn = -0.70710677f;
        norms.put(new float[]{
            0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, // top
            0, pn, nn, 0, pn, nn, 0, pn, nn, // back
            pn, pn, 0, pn, pn, 0, pn, pn, 0, // right
            0, pn, pn, 0, pn, pn, 0, pn, pn, // front
            nn, pn, 0, nn, pn, 0, nn, pn, 0 // left
        });
        norms.rewind();
        setBuffer(Type.Normal, 3, norms);

        // Update the texture buffer
        FloatBuffer texCoords = BufferUtils.createVector2Buffer(16);
        texCoords.put(new float[]{
            1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0.75f, 0, 0.5f, 1, 0.75f, 0, 0.5f,
            0, 0.5f, 1, 0.5f, 0, 0.25f, 0, 0.5f, 1, 0.25f, 0, 0, 0, 0.5f, 1
        });
        texCoords.rewind();
        setBuffer(Type.TexCoord, 2, texCoords);

        updateBound();
        setStatic();

    }

    /**
     * sets the indices for rendering the sphere.
     */
    private void setIndexData() {
        // Update the indices buffer
        IntBuffer indices = BufferUtils.createIntBuffer(18);
        indices.put(new int[]{
            3, 2, 1, 3, 1, 0, 6, 5, 4, 9, 8, 7, 12, 11, 10, 15, 14, 13
        });
        indices.rewind();
        setBuffer(Type.Index, 3, indices);
    }

    public void updateGeometry(float width, float height) {
        this.width = width;
        this.height = height;
        setGeometryData();
        setIndexData();
    }

    @Override
    public void read(JmeImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        width = capsule.readInt("width", 0);
        height = capsule.readInt("height", 0);
    }

    @Override
    public void write(JmeExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(width, "width", 0);
        capsule.write(height, "height", 0);
    }

}
