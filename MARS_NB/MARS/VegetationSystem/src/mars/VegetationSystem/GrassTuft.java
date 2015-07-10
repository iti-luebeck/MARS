/*
* Copyright (c) 2015, Institute of Computer Engineering, University of Lübeck
* All rights reserved.
* 
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* 
* * Redistributions of source code must retain the above copyright notice, this
*   list of conditions and the following disclaimer.
* 
* * Redistributions in binary form must reproduce the above copyright notice,
*   this list of conditions and the following disclaimer in the documentation
*   and/or other materials provided with the distribution.
* 
* * Neither the name of the copyright holder nor the names of its
*   contributors may be used to endorse or promote products derived from
*   this software without specific prior written permission.
* 
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
* FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
* CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package mars.VegetationSystem;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;
/**
 *
 * @author oven-_000
 */
public class GrassTuft extends Mesh{
    
    /**
     * Creates three 2D planes at the same position.
     * These planes are turned by 60° to one another.
     */
    public GrassTuft(){
        Vector3f[] vertices = new Vector3f[12];
        vertices[0] = new Vector3f(-1f,0,0);
        vertices[1] = new Vector3f(1f,0,0);
        vertices[2] = new Vector3f(-1f,2f,0);
        vertices[3] = new Vector3f(1f,2f,0);
        vertices[4] = new Vector3f(-0.5f,0,0.866f);
        vertices[5] = new Vector3f(0.5f,0,-0.866f);
        vertices[6] = new Vector3f(-0.5f,2f,0.866f);
        vertices[7] = new Vector3f(0.5f,2f,-0.866f);
        vertices[8] = new Vector3f(0.5f,0,0.866f);
        vertices[9] = new Vector3f(-0.5f,0,-0.866f);
        vertices[10] = new Vector3f(0.5f,2f,0.866f);
        vertices[11] = new Vector3f(-0.5f,2f,-0.866f);
    
        Vector2f[] texCoord = new Vector2f[12];
        texCoord[0] = new Vector2f(0,0);
        texCoord[1] = new Vector2f(1,0);
        texCoord[2] = new Vector2f(0,1);
        texCoord[3] = new Vector2f(1,1);
        texCoord[4] = new Vector2f(0,0);
        texCoord[5] = new Vector2f(1,0);
        texCoord[6] = new Vector2f(0,1);
        texCoord[7] = new Vector2f(1,1);
        texCoord[8] = new Vector2f(0,0);
        texCoord[9] = new Vector2f(1,0);
        texCoord[10] = new Vector2f(0,1);
        texCoord[11] = new Vector2f(1,1);
    
        int[] indexes = { 2,0,1, 1,3,2, 2,3,1, 1,0,2,   6,4,5, 5,7,6, 6,7,5, 5,4,6,   10,8,9, 9,11,10, 10,11,9, 9,8,10 };
        
        float[] normals = new float[]{0,1,0, 0,1,0, 0,1,0, 0,1,0, 0,1,0, 0,1,0, 0,1,0, 0,1,0, 0,1,0, 0,1,0, 0,1,0, 0,1,0};
        
        setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
        setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(indexes));
        setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(normals));
    }
}
