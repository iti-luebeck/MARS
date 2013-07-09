/*
 * Copyright (c) 2012, Andreas Olofsson
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions 
 * are met:
 * 
 * Redistributions of source code must retain the above copyright notice, 
 * this list of conditions and the following disclaimer.
 * 
 * Redistributions in binary form must reproduce the above copyright notice, 
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED 
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR 
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF 
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package mars;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.audio.AudioNode;
import com.jme3.audio.LowPassFilter;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.scene.Geometry;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.texture.Texture2D;
import com.jme3.util.SkyFactory;
import com.jme3.water.WaterFilter;
import forester.Forester;
import forester.grass.GrassLayer;
import forester.grass.GrassLayer.MeshType;
import forester.grass.GrassLoader;
import forester.grass.datagrids.MapGrid;
import forester.image.DensityMap.Channel;

/*
 * This example is based on the jME-tests post processing water demo.
 * 
 * @author Andreas
 */
/**
 * 
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class UnderWaterTest extends SimpleApplication {

    private TerrainQuad terrain;
    Material matRock;
    PointLight pl;
    Geometry lightMdl;
    private float sandScale = 64;
    private float grassScale = 2;
    
    private WaterFilter water;
    
    AudioNode waves;
    LowPassFilter underWaterAudioFilter = new LowPassFilter(0.5f, 0.1f);
    LowPassFilter underWaterReverbFilter = new LowPassFilter(0.5f, 0.1f);
    LowPassFilter aboveWaterAudioFilter = new LowPassFilter(1, 1);
    
    private float time = 0.0f;
    private float waterHeight = 0.0f;
    private float initialWaterHeight = 0.8f;
    private boolean uw = false;
    
    private Forester forester;
    
    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        UnderWaterTest app = new UnderWaterTest();
        app.setShowSettings(false);
        app.start();
    }
    
    /**
     * 
     */
    @Override
    public void simpleInitApp() {

        this.setDisplayFps(false);
        this.setDisplayStatView(false);
        
        assetManager.registerLocator("Assets/Forester", FileLocator.class);
       rootNode.attachChild(SkyFactory.createSky(
            assetManager, "Textures/Sky/Bright/BrightSky.dds", false));
       
        // First, we load up our textures and the heightmap texture for the terrain

        // TERRAIN TEXTURE material
        matRock = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");
        matRock.setBoolean("useTriPlanarMapping", false);

        // ALPHA map (for splat textures)
        matRock.setTexture("Alpha", assetManager.loadTexture("Textures/Sea/sea_alphamap.png"));

        Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
        Texture sand = assetManager.loadTexture("Textures/Sea/seamless_beach_sand.jpg");
        sand.setWrap(WrapMode.Repeat);
        grass.setWrap(WrapMode.Repeat);
        matRock.setTexture("Tex1", grass);
        matRock.setFloat("Tex1Scale", grassScale);
        matRock.setTexture("Tex2", grass);
        matRock.setFloat("Tex2Scale", grassScale);
        matRock.setTexture("Tex3", sand);
        matRock.setFloat("Tex3Scale", sandScale);

        // HEIGHTMAP image (for the terrain heightmap)
        Texture heightMapImage = assetManager.loadTexture("Textures/Sea/sea_heightmap.png");
        
        // CREATE HEIGHTMAP
        AbstractHeightMap heightmap = null;
        try {
            //heightmap = new HillHeightMap(1025, 1000, 50, 100, (byte) 3);

            heightmap = new ImageBasedHeightMap(heightMapImage.getImage(), 1f);
            heightmap.load();
            heightmap.normalizeTerrain(40);

        } catch (Exception e) {
        }

        terrain = new TerrainQuad("terrain", 65, 513, heightmap.getHeightMap());
        TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
        control.setLodCalculator( new DistanceLodCalculator(65, 2.7f) ); // patch size, and a multiplier
        terrain.addControl(control);
        terrain.setMaterial(matRock);
        terrain.setLocalTranslation(0, -70, 0);
        terrain.setLocalScale(2f, 2f, 2f);
        rootNode.attachChild(terrain);

        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White);
        sun.setDirection((new Vector3f(-0.5f, -1f, -0.5f)).normalize());
        rootNode.addLight(sun);
        
        AmbientLight amb = new AmbientLight();
        amb.setColor(ColorRGBA.White.multLocal(0.4f));
        rootNode.addLight(amb);

        cam.setLocation(new Vector3f(0, -50, -10));
        cam.lookAtDirection(new Vector3f(0, -0.5f, -1).normalizeLocal(), Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(50);
        
        water = new WaterFilter(rootNode, sun.getDirection());

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        
        fpp.addFilter(water);
        DepthOfFieldFilter dof=new DepthOfFieldFilter();
        dof.setFocusDistance(0);
        dof.setFocusRange(100);     
        fpp.addFilter(dof);
        water.setWaveScale(0.003f);
        water.setMaxAmplitude(2f);
        water.setFoamExistence(new Vector3f(1f, 4, 0.5f));
        water.setFoamTexture((Texture2D) assetManager.loadTexture("Common/MatDefs/Water/Textures/foam2.jpg"));
        //water.setNormalScale(0.5f);

        //water.setRefractionConstant(0.25f);
        water.setRefractionStrength(0.2f);
        //water.setFoamHardness(0.6f);

        water.setWaterHeight(50);
        uw=cam.getLocation().y<waterHeight; 
      
        /*waves = new AudioNode(audioRenderer, assetManager, "Sounds/Ocean Waves.ogg", false);
        waves.setLooping(true);
        waves.setReverbEnabled(true);
        if(uw){
            waves.setDryFilter(new LowPassFilter(0.5f, 0.1f));
        }else{
            waves.setDryFilter(aboveWaterAudioFilter);            
        }
        audioRenderer.playSource(waves);*/
          
        viewPort.addProcessor(fpp);
        
        setupForester();
        
    }
    
    /**
     * 
     */
    public void setupForester(){
        
        // Step 1 - set up the forester. The forester is a singleton class that
        // can be accessed statically from anywhere, but we use a reference
        // variable here.
        forester = Forester.getInstance();
        forester.initialize(rootNode, cam, terrain, null,this);
        forester.getForesterNode().setLocalTranslation(terrain.getLocalTranslation());
        // Step 2 - set up the grassloader. We're using a pagesize of 1026 in
        // this demo, which is the same size as the scaled terrain. We use a
        // resolution of 4, meaning we get a grid of 4x4 blocks of grass
        // in total; each 256x256 units in size. Far viewing range is 300 world
        // units, and fading range is 20.
        GrassLoader grassLoader = forester.createGrassLoader(1026, 2, 300f, 20f);

        // Step 3 - set up the mapgrid. This is where you link densitymaps with
        // terrain tiles if you use a custom grid (not terrain grid).
        MapGrid grid = grassLoader.createMapGrid();
        
        // Now add a texture to the grid. We will only use one map here, and only one gridcell (0,0).
        // The three zeros are x-coord, z-coord and density map index (starting from 0).
        // If we wanted to index this texture as densitymap 2 in cell (43,-12) we would have
        // written grid.addDensityMap(density,43,-12,2);
        Texture density = terrain.getMaterial().getTextureParam("Alpha").getTextureValue();
        grid.addDensityMap(density, 0, 0, 0);

        // Step 4 - set up a grass layer. We're gonna use the Grass.j3m material file
        // for the grass in this layer. The texture and all other variables are already
        // set in the material. Another alternative would have been to set them 
        // programatically (through the GrassLayer's methods). 
        Material grassMat = assetManager.loadMaterial("Materials/Grass/GreenSeaweed.j3m");
        Material grassMat2 = assetManager.loadMaterial("Materials/Grass/RedSeaweed.j3m");
        Material grassMat3 = assetManager.loadMaterial("Materials/Grass/Stalk.j3m");
        // Two parameters - the material, and the type of grass mesh to create.
        // Crossquads are two static quads that cross eachother at a right angle.
        // Switching between CROSSQUADS and QUADS is straightforward, but for
        // BILLBOARDS we need to use a different material base.
        GrassLayer layer = grassLoader.addLayer(grassMat,MeshType.CROSSQUADS);

        // Important! Link this particular grass layer with densitymap nr. 0 
        // This is the number we used for the alphamap when we set up the mapgrid.
        // We choose the red channel here (the grass texture channel) for density values.
        layer.setDensityTextureData(0, Channel.Red);
        
        layer.setDensityMultiplier(0.6f);
        
        // This sets the size boundaries of the grass-quads. When generated,
        // the quads will vary in size randomly, but sizes will never exceed these
        // bounds. Also, aspect ratio is always preserved.
        
        layer.setMaxHeight(4.4f);
        layer.setMinHeight(2.f);
        
        layer.setMaxWidth(4.4f);
        layer.setMinWidth(2.f);
        
        // Another layer
        GrassLayer layer2 = grassLoader.addLayer(grassMat2,MeshType.CROSSQUADS);

        layer2.setDensityTextureData(0, Channel.Green);
        
        layer2.setDensityMultiplier(0.6f);
        
        layer2.setMaxHeight(3.4f);
        layer2.setMinHeight(2.5f);
        
        layer2.setMaxWidth(3.4f);
        layer2.setMinWidth(2.5f);
        
        
        // Another layer
        GrassLayer layer3 = grassLoader.addLayer(grassMat3,MeshType.BILLBOARDS);

        layer3.setDensityTextureData(0, Channel.Red);
        
        layer3.setDensityMultiplier(0.006f);
        
        layer3.setMaxHeight(8.4f);
        layer3.setMinHeight(6.5f);
        
        layer3.setMaxWidth(8.4f);
        layer3.setMinWidth(6.5f);
        
        grassLoader.setWind(new Vector2f(1,0));
    }
        
    /**
     * 
     * @param tpf
     */
    @Override
    public void simpleUpdate(float tpf) {
        
        super.simpleUpdate(tpf);
        forester.update(tpf);
        time += tpf;
        waterHeight = (float) Math.cos(((time * 0.6f) % FastMath.TWO_PI)) * 1.5f;
        water.setWaterHeight(initialWaterHeight + waterHeight);
        if(water.isUnderWater() && !uw){
           
            //waves.setDryFilter(new LowPassFilter(0.5f, 0.1f));
            uw=true;
        }
        if(!water.isUnderWater() && uw){
            uw=false;
             //waves.setReverbEnabled(false);
             //waves.setDryFilter(new LowPassFilter(1, 1f));
             //waves.setDryFilter(new LowPassFilter(1,1f));
             
        }
    }
}