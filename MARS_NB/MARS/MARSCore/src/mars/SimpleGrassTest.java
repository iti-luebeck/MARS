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
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Geometry;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.SkyFactory;
import forester.Forester;
//import forester.MaterialSP;
import forester.grass.GrassLayer;
import forester.grass.GrassLayer.MeshType;
import forester.grass.GrassLoader;
import forester.grass.algorithms.GPAUniform;
import forester.grass.datagrids.MapGrid;
import forester.image.DensityMap.Channel;
import mygame.MaterialSP;

/*
 * This example is based on TerrainTest.java. It uses a single terrain for 
 * height and density. The density of grass is based on the alpha values of 
 * the grassy terrain texture.
 * 
 * @author Andreas
 */
/**
 * 
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class SimpleGrassTest extends SimpleApplication {

    private TerrainQuad terrain;
    Material matRock;
    PointLight pl;
    Geometry lightMdl;
    private float grassScale = 64;
    private float dirtScale = 16;
    private float roadScale = 128;
    
    private Forester forester;
    
    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        SimpleGrassTest app = new SimpleGrassTest();
        app.setShowSettings(false);
        app.start();
    }
    
    /**
     * 
     */
    @Override
    public void simpleInitApp() {

       rootNode.attachChild(SkyFactory.createSky(
            assetManager, "Textures/Sky/Bright/BrightSky.dds", false));
       
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White);
        sun.setDirection((new Vector3f(-0.5f, -1f, -0.5f)).normalize());
        rootNode.addLight(sun);
        
        AmbientLight amb = new AmbientLight();
        amb.setColor(ColorRGBA.White.multLocal(0.4f));
        rootNode.addLight(amb);

        cam.setLocation(new Vector3f(0, 10, -10));
        cam.lookAtDirection(new Vector3f(0, -1.5f, -1).normalizeLocal(), Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(50);
        
        setupTerrain();
        
        setupForester();
        
    }
    
    /**
     * 
     */
    public void setupTerrain(){
        
        assetManager.registerLocator("Assets/Forester", FileLocator.class);
        MaterialSP terrainMat = new MaterialSP(assetManager,"MatDefs/TerrainBase.j3md");
        // First, we load up our textures and the heightmap texture for the terrain

        // ALPHA map (for splat textures)
        terrainMat.setTexture("AlphaMap", assetManager.loadTexture("Textures/Terrain/splat/alphamap2.png"));

        Vector4f texScales = new Vector4f();
        
        // GRASS texture
        Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
        grass.setWrap(WrapMode.Repeat);
        terrainMat.setTexture("TextureRed", grass);
        texScales.x = grassScale;
        
        // DIRT texture
        Texture dirt = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
        dirt.setWrap(WrapMode.Repeat);
        terrainMat.setTexture("TextureGreen", dirt);
        texScales.y = dirtScale;

        // ROCK texture
        Texture road = assetManager.loadTexture("Textures/Terrain/splat/dirt_ground.jpg");
        road.setWrap(WrapMode.Repeat);
        terrainMat.setTexture("TextureBlue", road);
        texScales.z = roadScale;
        
        terrainMat.setVector4("TexScales", texScales);
        
        // HEIGHTMAP image (for the terrain heightmap)
        Texture heightMapImage = assetManager.loadTexture("Textures/Terrain/splat/mountains512.png");
        
        // CREATE HEIGHTMAP
        AbstractHeightMap heightmap = null;
        try {
            heightmap = new ImageBasedHeightMap(heightMapImage.getImage(), 1f);
            heightmap.load();

        } catch (Exception e) {
        }

        terrain = new TerrainQuad("terrain", 65, 513, heightmap.getHeightMap());
        TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
        control.setLodCalculator(new DistanceLodCalculator(65, 2.7f)); // patch size, and a multiplier
        terrain.addControl(control);
        terrain.setMaterial(terrainMat);
        terrain.setLocalTranslation(0, -100, 0);
        terrain.setLocalScale(2f, .5f, 2f);
        rootNode.attachChild(terrain);
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
        
        // Displace the vegetation.
        forester.getForesterNode().setLocalTranslation(terrain.getLocalTranslation());

        // Step 2 - set up the grassloader. We're using a pagesize of 1026 in
        // this demo, which is the same size as the scaled terrain. We use a
        // resolution of 4, meaning we get a grid of 4x4 blocks of grass
        // in total; each 256x256 units in size. Far viewing range is 300 world
        // units, and fading range is 20.
        GrassLoader grassLoader = forester.createGrassLoader(1026, 4, 300f, 20f);

        // Step 3 - set up the mapgrid. This is where you link densitymaps with
        // terrain tiles if you use a custom grid (not terrain grid).
        MapGrid grid = grassLoader.createMapGrid();
        
        // Now add a texture to the grid. We will only use one map here, and only one gridcell (0,0).
        // The three zeros are x-coord, z-coord and density map index (starting from 0).
        // If we wanted to index this texture as densitymap 2 in cell (43,-12) we would have
        // written grid.addDensityMap(density,43,-12,2);
        Texture density = terrain.getMaterial().getTextureParam("AlphaMap").getTextureValue();
        grid.addDensityMap(density, 0, 0, 0);

        // Step 4 - set up a grass layer. We're gonna use the Grass.j3m material file
        // for the grass in this layer. The texture and all other variables are already
        // set in the material. Another alternative would have been to set them 
        // programatically (through the GrassLayer's methods). 
        Material grassMat = assetManager.loadMaterial("Materials/Grass/Grass.j3m");

        // Two parameters - the material, and the type of grass mesh to create.
        // Crossquads are two static quads that cross eachother at a right angle.
        // Switching between CROSSQUADS and QUADS is straightforward, but for
        // BILLBOARDS we need to use a different material base.
        GrassLayer layer = grassLoader.addLayer(grassMat,MeshType.CROSSQUADS);

        // Important! Link this particular grass layer with densitymap nr. 0 
        // This is the number we used for the alphamap when we set up the mapgrid.
        // We choose the red channel here (the grass texture channel) for density values.
        layer.setDensityTextureData(0, Channel.Red);
        
        layer.setDensityMultiplier(0.8f);
        
        // This sets the size boundaries of the grass-quads. When generated,
        // the quads will vary in size randomly, but sizes will never exceed these
        // bounds. Also, aspect ratio is always preserved.
        
        layer.setMaxHeight(2.4f);
        layer.setMinHeight(2.f);
        
        layer.setMaxWidth(2.4f);
        layer.setMinWidth(2.f);
        
        // Setting a maximum slope for the grassquads, to reduce stretching. 
        // No grass is placed in areas with a slope higher then this angle.
        //
        // Use a degree between 0 and 90 (it's automatically normalized to this 
        // range). The default value is 30 degrees, so this is not really
        // necessary here, but I set it to show how it works.
        layer.setMaxTerrainSlope(30);
        
        // This is a way of discarding all densityvalues that are lower then 0.6.
        // A threshold value is optional, but in this case it's useful to restrict
        // grass from being planted in areas where the grass texture is only a few
        // percent visible (dominated by other textures).
        ((GPAUniform)layer.getPlantingAlgorithm()).setThreshold(0.6f);
        
        
        // Adding another grasslayer.
        
        Material grassMat2 = assetManager.loadMaterial("Materials/Grass/DaisyBillboarded.j3m");

        // Using billboards. Different material base but pretty much the same
        // parameters.
        GrassLayer layer2 = grassLoader.addLayer(grassMat2,MeshType.BILLBOARDS);

        // Using the same densitymap and channel as the grass.
        layer2.setDensityTextureData(0, Channel.Red);
        layer2.setDensityMultiplier(0.05f);
        
        layer2.setMaxHeight(2.4f);
        layer2.setMinHeight(2.f);
        
        layer2.setMaxWidth(2.4f);
        layer2.setMinWidth(2.f);
        
        ((GPAUniform)layer2.getPlantingAlgorithm()).setThreshold(0.6f);
        
        // Finally...

        // Swaying is checked in the material file, but we have to provide a wind
        // direction and speed to the grassloader. The reason this is done through
        // the grassloader and not the grasslayers is because the grassloader
        // automatically sets the wind variable in all it's layers, ensuring that 
        // the wind is the same for all grass-layers.
        //
        // The effect of the wind, such as swaying amplitude (strength), and
        // frequency can be set for each grass type in its material file, or
        // through the grasslayer methods.
        grassLoader.setWind(new Vector2f(1,0));
    }
        
    /**
     * 
     * @param tpf
     */
    @Override
    public void simpleUpdate(float tpf) {
        forester.update(tpf);
    }
}
