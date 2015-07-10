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

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import jme3tools.optimize.GeometryBatchFactory;

/**
 *
 * @author oven-_000
 */
public class ObjectPatches{
    boolean debug = false;
    Node[][] genuine;
    Node[][] imposter;
    int width;
    int height;
    Node rootNode;
    AssetManager am;
    ShadowMode sModeG;
    ShadowMode sModeI;
    
    /**
     * Create object patches 
     * @param width total number of patches in its width
     * @param height total number of patches in its height
     * @param am the assetmanager
     * @param sModeG shadowmode of genuine patches
     * @param sModeI shadowmode of imposter patches
     */
    public ObjectPatches(int width, int height, AssetManager am, ShadowMode sModeG, ShadowMode sModeI){
        genuine = new Node[width][height];
        imposter = new Node[width][height];
        this.width = width;
        this.height = height;
        this.am = am;
        this.sModeG = sModeG;
        this.sModeI = sModeI;
    }
    
    /**
     *
     * @param debug
     */
    public void debug(boolean debug){
        this.debug = debug;
    }
    
    /**
     * Creates a new node for genuine objects
     * @param width width value where the patch is placed
     * @param height height value where the patch is placed
     */
    public void createGenuineNode(int width, int height){
        genuine[width][height] = new Node();
        genuine[width][height].attachChild(new Node("child"));
        genuine[width][height].setShadowMode(sModeG);
        if(debug){
            Node child = (Node) genuine[width][height].getChild("child");
            Box cube1Mesh = new Box( 1f,1f,1f);
            Geometry cube1Geo = new Geometry("My Textured Box", cube1Mesh);
            cube1Geo.setLocalTranslation(new Vector3f(-3f,1.1f,0f));
            Material cube1Mat = new Material(am, 
            "Common/MatDefs/Misc/Unshaded.j3md");
            Texture cube1Tex = am.loadTexture(
            "Textures/Terrain/italy_am2.png");
            cube1Mat.setTexture("ColorMap", cube1Tex);
            cube1Geo.setMaterial(cube1Mat);
            child.attachChild(cube1Geo);
        }
    }
    
    /**
     * Creates a new node for imposter objects
     * @param width width value where the patch is placed
     * @param height height value where the patch is placed
     */
    public void createImposterNode(int width, int height){
        imposter[width][height] = new Node();
        imposter[width][height].attachChild(new Node("child"));
        imposter[width][height].setShadowMode(sModeI);
        
        if(debug){
            Node child = (Node) imposter[width][height].getChild("child");
            Box cube1Mesh = new Box( 1f,1f,1f);
            Geometry cube1Geo = new Geometry("My Textured Box", cube1Mesh);
            cube1Geo.setLocalTranslation(new Vector3f(-3f,1.1f,0f));
            Material cube1Mat = new Material(am, 
            "Common/MatDefs/Misc/Unshaded.j3md");
            Texture cube1Tex = am.loadTexture(
            "Textures/Terrain/italy_am2.png");
            cube1Mat.setTexture("ColorMap", cube1Tex);
            cube1Geo.setMaterial(cube1Mat);
            child.attachChild(cube1Geo);
        }
    }
    
    /**
     * Adds a genuine objekt to a specified patch
     * @param width specifies the patch by its witdh value
     * @param height specifies the patch by its height value
     * @param geo objekt to be added
     */
    public void addGenuine(int width, int height, Spatial geo){
        Node child = (Node) genuine[width][height].getChild("child");
        child.attachChild(geo);
    }
    
    /**
     * Adds an imposter objekt to a specified patch
     * @param width specifies the patch by its witdh value
     * @param height specifies the patch by its height value
     * @param geo objekt to be added
     */
    public void addImposter(int width, int height, Spatial geo){
        Node child = (Node) imposter[width][height].getChild("child");
        child.attachChild(geo);
    }
    
    /**
     * Returns the genuine patch specified by width and height
     * @param width specifies the width where the patch is stored
     * @param height specifies the heigth where the patch is stored
     * @return the patch
     */
    public Node getGenuine(int width, int height){
        return genuine[width][height];
    }
    
    /**
     * Returns the imposter patch specified by width and height
     * @param width specifies the patch by its witdh value
     * @param height specifies the patch by its height value
     * @return the patch
     */
    public Node getImposter(int width, int height){
        return imposter[width][height];
    }
    
    /**
     * Set the location of the patch
     * @param width specifies the patch by its witdh value
     * @param height specifies the patch by its height value
     * @param x translation along the x axis
     * @param y translation along the y axis
     * @param z translation along the z axis
     */
    public void setLocalTranslation(int width, int height, float x, float y, float z){
        genuine[width][height].setLocalTranslation(x, y, z);
        imposter[width][height].setLocalTranslation(x, y, z);
    }
    
    /**
     * Returns the transation of a patch
     * @param width specifies the patch by its witdh value
     * @param height specifies the patch by its height value
     * @return a vector containing the translation
     */
    public Vector3f getLocalTranslation(int width, int height){
        return genuine[width][height].getLocalTranslation();
    }
    
    /**
     * Optimizes all patches by batching
     */
    public void optimize(){
        Node tempNode;
        for(int i = 0; i < width; i++){
            for(int j = 0; j < height; j++){
                if(genuine[i][j] != null){
                    tempNode = (Node) genuine[i][j].getChild("child");
                    genuine[i][j].detachAllChildren();
                    genuine[i][j].attachChild(GeometryBatchFactory.optimize(tempNode));
                }
                
                if(imposter[i][j] != null){
                    tempNode = (Node) imposter[i][j].getChild("child");
                    imposter[i][j].detachAllChildren();
                    imposter[i][j].attachChild(GeometryBatchFactory.optimize(tempNode));
                }
            }
        }
    }
    
    /**
     * Optimizes a patch specified by width and height
     * @param width specifies the patch by its witdh value
     * @param height specifies the patch by its height value
     */
    public void optimizePatch(int width, int height){
        Node tempNode;
        if(genuine[width][height] != null){
                    tempNode = (Node) genuine[width][height].getChild("child");
                    genuine[width][height].detachAllChildren();
                    genuine[width][height].attachChild(GeometryBatchFactory.optimize(tempNode));
        }
        
        if(imposter[width][height] != null){
                    tempNode = (Node) imposter[width][height].getChild("child");
                    imposter[width][height].detachAllChildren();
                    imposter[width][height].attachChild(GeometryBatchFactory.optimize(tempNode));
        }
    }
}
