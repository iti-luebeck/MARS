/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.libs.VegetationSystem;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

/**
 *
 * @author oven-_000
 */
public class GrassQuad extends Mesh{

    /**
     * Creates a 2D plane
     */
    public GrassQuad(){
        Vector3f[] vertices = new Vector3f[4];
        vertices[0] = new Vector3f(-1f,0,0);
        vertices[1] = new Vector3f(1f,0,0);
        vertices[2] = new Vector3f(-1f,2f,0);
        vertices[3] = new Vector3f(1f,2f,0);
    
        Vector2f[] texCoord = new Vector2f[4];
        texCoord[0] = new Vector2f(0,0);
        texCoord[1] = new Vector2f(1,0);
        texCoord[2] = new Vector2f(0,1);
        texCoord[3] = new Vector2f(1,1);
    
        int[] indexes = { 2,0,1, 1,3,2, 2,3,1, 1,0,2 };
        
        float[] normals = new float[]{0,1,0, 0,1,0, 0,1,0, 0,1,0};
        
        setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(indexes));
        setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(normals));
    }
}
