/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.VegetationSystem;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture;

/**
 *
 * @author oven-_000
 */
public class VegetationSystem extends Node{
    DensityMap densityMap;
    Camera cam;
    TerrainQuad terrain;
    Spatial genuineRed = null;
    Spatial genuineGreen = null;
    Spatial genuineBlue = null;
    Spatial imposterRed = null;
    Spatial imposterGreen = null;
    Spatial imposterBlue = null;
    float pixelWidth = 0;
    float pixelHeight= 0;
    float patchLength = 0;
    float minDistRed = 0.2f;
    float minDistGreen = 0.2f;
    float minDistBlue = 0.2f;
    float terrainWidth;
    float terrainHeight;
    ObjectPatches posRed;
    ObjectPatches posGreen;
    ObjectPatches posBlue;
    int patchSize = 1;
    Vector3f lastLocation = Vector3f.ZERO;
    int lastWidth = 20;
    int lastHeight = 20;
    float imposterMView = 1000;
    float genuineMView = 500;
    AssetManager am;
    ShadowMode sModeGR;
    ShadowMode sModeIR;
    ShadowMode sModeGG;
    ShadowMode sModeIG;
    ShadowMode sModeGB;
    ShadowMode sModeIB;
    boolean debug = false;
    String info = "";
    
    /**
     * Creates a new VegetaionSystem
     * @param terrain the terrain on which the objects will be placed
     * @param am the assestmanager
     * @param cam the camera which presents the users perspective
     * @param densityMap a densityMap which specifies who the objects will be placed
     * @param patchTimes the size of the patches relative to the pixel size of the densityMap.
     * The values describes which power of two the patch size will be.
     */
    public VegetationSystem(TerrainQuad terrain, AssetManager am, Camera cam, DensityMap densityMap, int patchTimes){
        this.terrain = terrain;
        this.am = am;
        this.cam = cam;
        this.densityMap = densityMap;
        this.patchSize = (int) Math.pow(2, patchTimes);
        
        sModeGR = ShadowMode.Off;
        sModeIR = ShadowMode.Off;
        sModeGG = ShadowMode.Off;
        sModeIG = ShadowMode.Off;
        sModeGB = ShadowMode.Off;
        sModeIB = ShadowMode.Off;
        
        BoundingBox vol = (BoundingBox) terrain.getWorldBound();
        terrainWidth = vol.getXExtent();
        terrainHeight = vol.getZExtent();
        pixelWidth = terrainWidth*2/(float)densityMap.getWidth();
        pixelHeight = terrainHeight*2/(float)densityMap.getHeight();
        patchLength = (float) Math.sqrt(Math.pow(Math.abs(pixelWidth*patchSize), 2)+Math.pow(Math.abs(pixelHeight*patchSize), 2));
        //System.out.println(pixelWidth*patchSize +  " " + pixelHeight*patchSize + " " + patchLength);
    }
    
    /**
     * Attaches the vegetationSystem to a node
     * @param rootNode the vegetationSystem will be attached to this
     */
    public void attach(Node rootNode){
        rootNode.attachChild(this);
    }
    
    /**
     * Detaches the vegetationSytem from its parent
     */
    public void detach(){
        this.getParent().detachChild(this);
    }
    
    /**
     * Shows a geometrie for each patch
     * @param debug on/off (true/false)
     */
    public void debug(boolean debug){
        this.debug = debug;
    }
    
    /**
     * Info about the vegetationSystem
     * @return
     */
    public String getInfo(){
        return info;
    }

    /**
     * Sets an exemplary genuine object for the red part of the densityMap 
     * @param meshRed an exemplary object
     */
    public void setGenuineRed(Spatial meshRed){
        this.genuineRed = meshRed;
    }
    
    /**
     * Sets an exemplary imposter object for the red part of the densityMap 
     * @param imposter an exemplary object
     */
    public void setImposterRed(Spatial imposter){
        this.imposterRed = imposter;
    }

    /**
     * Sets an exemplary genuine object for the green part of the densityMap 
     * @param meshGreen an exemplary object
     */
    public void setGenuineGreen(Spatial meshGreen){
        this.genuineGreen = meshGreen;
    }
    
    /**
     * Sets an exemplary imposter object for the green part of the densityMap 
     * @param imposter an exemplary object
     */
    public void setImposterGreen(Spatial imposter){
        this.imposterGreen = imposter;
    }

    /**
     * Sets an exemplary genuine object for the blue part of the densityMap 
     * @param meshBlue an exemplary object
     */
    public void setGenuineBlue(Spatial meshBlue){
        this.genuineBlue = meshBlue;
    }
    
    /**
     * Sets an exemplary imposter object for the blue part of the densityMap 
     * @param imposter an exemplary object
     */
    public void setImposterBlue(Spatial imposter){
        this.imposterBlue = imposter;
    }
    
    /**
     * Sets the minimum distance of each geometry of the same color to ona another
     * @param minDistR minimum distance for geomtries of type red
     * @param minDistG minimum distance for geomtries of type green
     * @param minDistB minimum distance for geomtries of type blue
     */
    public void setMinDist(float minDistR, float minDistG, float minDistB){
        this.minDistRed = minDistR;
        this.minDistGreen = minDistG;
        this.minDistBlue = minDistB;
    }
    
    /**
     * Sets the range in which object are displayed for genuine and imposter objects
     * @param genuineMaxView range in which genuine objects are displayed
     * @param imposterMaxView range in which imposter objects are displayed
     */
    public void setMaxView(float genuineMaxView, float imposterMaxView){
        genuineMView = genuineMaxView;
        imposterMView = imposterMaxView;
    }
    
    /**
     * Sets the shadowMode for all types of patches
     * @param sModeGR shadowMode of genuine patches of type red
     * @param sModeIR shadowMode of imposter patches of type red
     * @param sModeGG shadowMode of genuine patches of type green
     * @param sModeIG shadowMode of imposter patches of type green
     * @param sModeGB shadowMode of genuine patches of type blue
     * @param sModeIB shadowMode of imposter patches of type blue
     */
    public void setShadowModes(ShadowMode sModeGR, ShadowMode sModeIR, ShadowMode sModeGG, ShadowMode sModeIG, ShadowMode sModeGB, ShadowMode sModeIB){
        this.sModeGR = sModeGR;
        this.sModeIR = sModeIR;
        this.sModeGG = sModeGG;
        this.sModeIG = sModeIG;
        this.sModeGB = sModeGB;
        this.sModeIB = sModeIB;
    }
    
    /**
     * Creates a genuine grass object
     * @param texturePath path of the texture
     * @param alphaDT the alpha discard threshold
     * @param sway sway animation performed by the grass by force of wind
     * @param response deforming the grass when near
     * @param fade in and out fading of the object when displayed at maximium range
     * @param wind vector which defines the wind
     * @param swayFreq frequancy at which the grass will sway
     * @param radius radius in which the camera is considered near
     * @param rHeight height in which the camera affects the grass
     * @param scale scaling of the grass object
     * @return the genuine grass object
     */
    public Geometry createGenuineGrass(String texturePath, float alphaDT, boolean sway, boolean response, boolean fade, Vector2f wind, float swayFreq, float radius, float rHeight, Vector3f scale){
        Geometry grassModel = new Geometry("Grass", new GrassTuft());
        grassModel.scale(scale.x, scale.y, scale.z);
        Material grassMaterial = new Material(am, "MatDefs/Grass/Grass.j3md");
        Texture grass = am.loadTexture(texturePath);
        
        grassMaterial.setTexture("DiffuseMap", grass);
        grassMaterial.setFloat("AlphaDiscardThreshold", alphaDT);
        grassMaterial.setBoolean("Swaying", sway);
        grassMaterial.setBoolean("Response", response);
        grassMaterial.setFloat("RespRadius", radius);
        grassMaterial.setFloat("RespHeight", rHeight);
        grassMaterial.setFloat("ModelHeight", 2*scale.y);
        grassMaterial.setBoolean("Fade", fade);
        grassMaterial.setVector2("Wind", wind);
        grassMaterial.setFloat("SwayFrequency", swayFreq);
        grassMaterial.setFloat("FadeDistMax", genuineMView-0.75f*patchLength);
        grassMaterial.setFloat("FadeDistMin", genuineMView-1.5f*patchLength);
        
        grassMaterial.getAdditionalRenderState().setAlphaTest(true);
        grassModel.setMaterial(grassMaterial);
        return grassModel;
    }
    
    /**
     * Creates an imposter grass object
     * @param texturePath path of the texture
     * @param alphaDT the alpha discard threshold
     * @param sway sway animation performed by the grass by force of wind
     * @param response deforming the grass when near
     * @param fade in and out fading of the object when displayed at maximium range
     * @param wind vector which defines the wind
     * @param swayFreq frequancy at which the grass will sway
     * @param radius radius in which the camera is considered near
     * @param rHeight height in which the camera affects the grass
     * @param scale scaling of the grass object
     * @return the genuine grass object
     */
    public Geometry createImposterGrass(String texturePath, float alphaDT, boolean sway, boolean response, boolean fade, Vector2f wind, float swayFreq, float radius, float rHeight, Vector3f scale){
        Geometry grassModel = new Geometry("Grass", new GrassQuad());
        grassModel.scale(scale.x, scale.y, scale.z);
        Material grassMaterial = new Material(am, "MatDefs/Grass/Grass.j3md");
        Texture grass = am.loadTexture(texturePath);
        
        grassMaterial.setTexture("DiffuseMap", grass);
        grassMaterial.setFloat("AlphaDiscardThreshold", alphaDT);
        grassMaterial.setBoolean("Swaying", sway);
        grassMaterial.setBoolean("Response", response);
        grassMaterial.setFloat("RespRadius", radius);
        grassMaterial.setFloat("ModelHeight", 2*scale.y);
        grassMaterial.setBoolean("Fade", fade);
        grassMaterial.setVector2("Wind", wind);
        grassMaterial.setFloat("SwayFrequency", swayFreq);
        grassMaterial.setFloat("FadeDistMax", imposterMView-0.5f*patchLength);
        grassMaterial.setFloat("FadeDistMin", ((imposterMView-0.5f*patchLength)+genuineMView)/2);
        
        grassMaterial.getAdditionalRenderState().setAlphaTest(true);
        grassModel.setMaterial(grassMaterial);
        return grassModel;
    }
    
    /**
     * Creates new patches and places objects inside of them submissioned by the densityMap
     * @param randPR (0,1) randomness in the placing process for type red 
     * @param randPG (0,1) randomness in the placing process for type green
     * @param randPB (0,1) randomness in the placing process for type blue
     * @param randSR (0,1) randomness in the size of objects for type red
     * @param randSG (0,1) randomness in the size of objects for type green
     * @param randSB (0,1) randomness in the size of objects for type blue
     */
    public void plant(float randPR, float randPG, float randPB, float randSR, float randSG, float randSB){
        
        int[][] densRed = densityMap.getDensitysRed();
        int[][] densGreen = densityMap.getDensitysGreen();
        int[][] densBlue = densityMap.getDensitysBlue();
        
        posRed = new ObjectPatches(densityMap.getWidth()/patchSize, densityMap.getHeight()/patchSize, am, sModeGR, sModeIR);
        posGreen = new ObjectPatches(densityMap.getWidth()/patchSize, densityMap.getHeight()/patchSize, am, sModeGG, sModeIG);
        posBlue = new ObjectPatches(densityMap.getWidth()/patchSize, densityMap.getHeight()/patchSize, am, sModeGB, sModeIB);
        
        posRed.debug(debug);
        posGreen.debug(debug);
        posBlue.debug(debug);
            
        float posX = -terrainWidth + terrain.getLocalTranslation().x;
        float posZ;

        Spatial grassGenuine;
        Spatial grassImposter;
        int numWidth;
        int numHeight;
        float transX;
        float transZ;
        float tempX;
        float tempZ;
        float scaleMult;
        float temp;
        int countRed = 0;
        int countGreen = 0;
        int countBlue = 0;
        
        for (int i = 0; i < densityMap.getWidth(); i++){
            posZ = -terrainHeight + terrain.getLocalTranslation().z;     
            for (int j = 0; j < densityMap.getHeight(); j++){
                
                if(genuineBlue != null && imposterBlue != null){
                    numWidth = (int)(pixelWidth/minDistBlue)*densBlue[i][j]/255;
                    numHeight = (int)(pixelHeight/minDistBlue)*densBlue[i][j]/255;
                
                    if(i%patchSize == 0 && j%patchSize == 0){
                        posBlue.createGenuineNode(i/patchSize, j/patchSize);
                        posBlue.createImposterNode(i/patchSize, j/patchSize);
                        tempX = posX+pixelWidth*patchSize/2;
                        tempZ = posZ+pixelHeight*patchSize/2;
                        posBlue.setLocalTranslation(i/patchSize, j/patchSize, tempX, terrain.getHeight(new Vector2f(tempX, tempZ))+terrain.getLocalTranslation().y, tempZ);
                    }
                
                    for(int k = 0; k < numWidth; k++){
                        for(int l = 0; l < numHeight; l++){
                            countBlue ++;
                            grassGenuine = genuineBlue.clone(false);
                            grassImposter = imposterBlue.clone(false);
                            transX = (float)(posX + k*(pixelWidth/numWidth)+(float)Math.random()*randPB*(1+pixelWidth/numWidth));
                            transZ = (float)(posZ + l*(pixelHeight/numHeight)+(float)Math.random()*randPB*(1+pixelWidth/numHeight));
                            grassGenuine.setLocalTranslation(transX, terrain.getHeight(new Vector2f(transX, transZ)), transZ);
                            grassGenuine.getLocalTranslation().subtractLocal(posBlue.getGenuine(i/patchSize, j/patchSize).getLocalTranslation().subtract(0, terrain.getLocalTranslation().y, 0));
                            grassImposter.setLocalTranslation(grassGenuine.getLocalTranslation());
                            temp = (float)Math.random();
                            grassGenuine.rotate(0, temp, 0);
                            grassImposter.rotate(0, temp, 0);
                            scaleMult = (float) (1 + (Math.random()-Math.random())*0.5f*randSB);
                            grassGenuine.setLocalScale(grassGenuine.getLocalScale().mult(scaleMult));
                            grassImposter.setLocalScale(grassImposter.getLocalScale().mult(scaleMult));
                        
                            posBlue.addGenuine(i/patchSize, j/patchSize, grassGenuine);
                            posBlue.addImposter(i/patchSize, j/patchSize, grassImposter);
                        }
                    }
                    
                    if((i+1)%patchSize == 0 && (j+1)%patchSize == 0){
                            posBlue.optimizePatch(i/patchSize, j/patchSize);
                    }
                }
                
                
                if(genuineRed != null && imposterRed != null){
                    numWidth = (int)(pixelWidth/minDistRed)*densRed[i][j]/255;
                    numHeight = (int)(pixelHeight/minDistRed)*densRed[i][j]/255;
                
                    if(i%patchSize == 0 && j%patchSize == 0){
                        posRed.createGenuineNode(i/patchSize, j/patchSize);
                        posRed.createImposterNode(i/patchSize, j/patchSize);
                        tempX = posX+pixelWidth*patchSize/2;
                        tempZ = posZ+pixelHeight*patchSize/2;
                        posRed.setLocalTranslation(i/patchSize, j/patchSize, tempX, terrain.getHeight(new Vector2f(tempX, tempZ))+terrain.getLocalTranslation().y, tempZ);
                    }
                
                    for(int k = 0; k < numWidth; k++){
                        for(int l = 0; l < numHeight; l++){
                            countRed ++;
                            grassGenuine = genuineRed.clone(false);
                            grassImposter = imposterRed.clone(false);
                            transX = (float)(posX + k*(pixelWidth/numWidth)+(float)Math.random()*randPR*(1+pixelWidth/numWidth));
                            transZ = (float)(posZ + l*(pixelHeight/numHeight)+(float)Math.random()*randPR*(1+pixelWidth/numHeight));
                            grassGenuine.setLocalTranslation(transX, terrain.getHeight(new Vector2f(transX, transZ)), transZ);
                            grassGenuine.getLocalTranslation().subtractLocal(posRed.getGenuine(i/patchSize, j/patchSize).getLocalTranslation().subtract(0, terrain.getLocalTranslation().y, 0));
                            grassImposter.setLocalTranslation(grassGenuine.getLocalTranslation());
                            temp = (float)Math.random();
                            grassGenuine.rotate(0, temp, 0);
                            grassImposter.rotate(0, temp, 0);
                            scaleMult = (float) (1 + (Math.random()-Math.random())*0.5f*randSR);
                            grassGenuine.setLocalScale(grassGenuine.getLocalScale().mult(scaleMult));
                            grassImposter.setLocalScale(grassImposter.getLocalScale().mult(scaleMult));
                        
                            posRed.addGenuine(i/patchSize, j/patchSize, grassGenuine);
                            posRed.addImposter(i/patchSize, j/patchSize, grassImposter);
                        }
                    }

                    if((i+1)%patchSize == 0 && (j+1)%patchSize == 0){
                            posRed.optimizePatch(i/patchSize, j/patchSize);
                    }
                }
                
                if(genuineGreen != null && imposterGreen != null){
                    numWidth = (int)(pixelWidth/minDistGreen)*densGreen[i][j]/255;
                    numHeight = (int)(pixelHeight/minDistGreen)*densGreen[i][j]/255;
                
                    if(i%patchSize == 0 && j%patchSize == 0){
                        posGreen.createGenuineNode(i/patchSize, j/patchSize);
                        posGreen.createImposterNode(i/patchSize, j/patchSize);
                        tempX = posX+pixelWidth*patchSize/2;
                        tempZ = posZ+pixelHeight*patchSize/2;
                        posGreen.setLocalTranslation(i/patchSize, j/patchSize, tempX, terrain.getHeight(new Vector2f(tempX, tempZ))+terrain.getLocalTranslation().y, tempZ);
                    }
                
                    for(int k = 0; k < numWidth; k++){
                        for(int l = 0; l < numHeight; l++){
                            countGreen ++;
                            grassGenuine = genuineGreen.clone(false);
                            grassImposter = imposterGreen.clone(false);
                            transX = (float)(posX + k*(pixelWidth/numWidth)+(float)Math.random()*randPG*(1+pixelWidth/numWidth));
                            transZ = (float)(posZ + l*(pixelHeight/numHeight)+(float)Math.random()*randPG*(1+pixelWidth/numHeight));
                            grassGenuine.setLocalTranslation(transX, terrain.getHeight(new Vector2f(transX, transZ)), transZ);
                            grassGenuine.getLocalTranslation().subtractLocal(posGreen.getGenuine(i/patchSize, j/patchSize).getLocalTranslation().subtract(0, terrain.getLocalTranslation().y, 0));
                            grassImposter.setLocalTranslation(grassGenuine.getLocalTranslation());
                            temp = (float)Math.random();
                            grassGenuine.rotate(0, temp, 0);
                            grassImposter.rotate(0, temp, 0);
                            scaleMult = (float) (1 + (Math.random()-Math.random())*0.5f*randSG);
                            grassGenuine.setLocalScale(grassGenuine.getLocalScale().mult(scaleMult));
                            grassImposter.setLocalScale(grassImposter.getLocalScale().mult(scaleMult));
                        
                            posGreen.addGenuine(i/patchSize, j/patchSize, grassGenuine);
                            posGreen.addImposter(i/patchSize, j/patchSize, grassImposter);
                        }
                    }
                    
                    if((i+1)%patchSize == 0 && (j+1)%patchSize == 0){
                            posGreen.optimizePatch(i/patchSize, j/patchSize);
                    }                  
                }

                posZ += pixelHeight;
            }
            posX += pixelWidth;
        }
        //this.setShadowMode(ShadowMode.CastAndReceive);
        this.setShadowMode(ShadowMode.Off);
        
        info += "Total Objects: Red: " + countRed + " " + "Green: " + countGreen + " " + "Blue: " + countBlue;
    }
    
    private boolean addPatch(ObjectPatches patch, int width, int height, Camera cam){
        boolean ans = false;
        Vector2f tVec1 = new Vector2f();
        Vector2f tvec2 = new Vector2f();
        float distance;
        
        if(width < densityMap.getWidth()/patchSize && width >= 0 && height < densityMap.getHeight()/patchSize && height >= 0){
            if(patch.getGenuine(width, height) != null){
                distance = cam.getLocation().distance(patch.getLocalTranslation(width, height));
                if(distance < genuineMView){
                    attachChild(patch.getGenuine(width, height));
                }
                if(distance < imposterMView && distance > genuineMView - 1.75f*patchLength){
                    attachChild(patch.getImposter(width, height));
                }
                
                tVec1.set(cam.getLocation().x, cam.getLocation().z);
                tvec2.set(patch.getLocalTranslation(width, height).x, patch.getLocalTranslation(width, height).z);
                if(tVec1.distance(tvec2) < imposterMView){
                    ans = true;
                }
            }
        }else{
            tVec1.set(cam.getLocation().x+terrainWidth, cam.getLocation().z+terrainHeight);
            int cWidth = (int)(tVec1.x*densityMap.getWidth()/(2*terrainWidth*patchSize));
            int cHeight = (int)(tVec1.y*densityMap.getHeight()/(2*terrainHeight*patchSize));
            if(Math.sqrt(Math.pow(Math.abs(cWidth - width), 2)+Math.pow(Math.abs(cHeight - height), 2)) < imposterMView/patchLength){ // ToDo: Calculation of distance
                ans = true;  
            }
        }
        return ans;
    }
    
    @Override
    public void updateGeometricState(){
        
        Vector3f location = cam.getLocation().add(terrainWidth,0,terrainHeight).subtract(terrain.getLocalTranslation().x, 0, terrain.getLocalTranslation().z);
        
        int width = (int)(location.x*densityMap.getWidth()/(2*terrainWidth*patchSize));
        int height = (int)(location.z*densityMap.getHeight()/(2*terrainHeight*patchSize));
        
        if(width != lastWidth || height != lastHeight || Math.abs(lastLocation.y-location.y) > 1){
            detachAllChildren();
            
            boolean nextW = true;
            boolean nextH = true;
            int widthAdd = 0;
            int heightAdd;
            
            while(nextW){
                nextW = false;
                heightAdd = 0;
                while(nextH){
                    nextH = false;
                    
                    nextH |= addPatch(posRed, width+widthAdd, height+heightAdd, cam);
                    nextH |= addPatch(posGreen, width+widthAdd, height+heightAdd, cam);
                    nextH |= addPatch(posBlue, width+widthAdd, height+heightAdd, cam);
    
                    nextH |= addPatch(posRed, width+widthAdd, height-heightAdd, cam);
                    nextH |= addPatch(posGreen, width+widthAdd, height-heightAdd, cam);
                    nextH |= addPatch(posBlue, width+widthAdd, height-heightAdd, cam);
                    
                    nextH |= addPatch(posRed, width-widthAdd, height+heightAdd, cam);
                    nextH |= addPatch(posGreen, width-widthAdd, height+heightAdd, cam);
                    nextH |= addPatch(posBlue, width-widthAdd, height+heightAdd, cam);;
                       
                    nextH |= addPatch(posRed, width-widthAdd, height-heightAdd, cam);
                    nextH |= addPatch(posGreen, width-widthAdd, height-heightAdd, cam);
                    nextH |= addPatch(posBlue, width-widthAdd, height-heightAdd, cam);
                    
                    nextW |= nextH;

                    heightAdd+=1;
                }
                nextH = true;
                widthAdd+=1;
            }
            lastWidth = width;
            lastHeight = height;
            lastLocation = location;
        }
        
        super.updateGeometricState();
    }
}
