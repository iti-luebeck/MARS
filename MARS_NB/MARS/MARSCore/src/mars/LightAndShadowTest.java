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
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Spatial;
import com.jme3.shadow.PssmShadowRenderer;
import com.jme3.shadow.PssmShadowRenderer.CompareMode;
import com.jme3.shadow.PssmShadowRenderer.FilterMode;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.SkyFactory;
import forester.Forester;
import forester.grass.GrassLayer;
import forester.grass.GrassLayer.MeshType;
import forester.grass.GrassLoader;
import forester.grass.algorithms.GPAUniform;
import forester.grass.datagrids.MapGrid;
import forester.image.DensityMap.Channel;
import forester.trees.TreeLayer;
import forester.trees.TreeLoader;
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
public class LightAndShadowTest extends SimpleApplication {

    private TerrainQuad terrain;
    Material matRock;
    private float grassScale = 64;
    private float dirtScale = 16;
    private float roadScale = 128;
    private Forester forester;
    private FilterPostProcessor fpp;
    private FogFilter fog;

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        LightAndShadowTest app = new LightAndShadowTest();
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
        
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White);
        sun.setDirection((new Vector3f(-0.5f, -1f, -0.5f)).normalize());
        rootNode.addLight(sun);

        AmbientLight amb = new AmbientLight();
        amb.setColor(ColorRGBA.White.multLocal(0.4f));
        rootNode.addLight(amb);
        
        PssmShadowRenderer pssmRenderer = new PssmShadowRenderer(assetManager, 2048, 3);
        pssmRenderer.setDirection(sun.getDirection());
        pssmRenderer.setLambda(0.55f);
        pssmRenderer.setShadowIntensity(0.6f);
        pssmRenderer.setCompareMode(CompareMode.Software);
        pssmRenderer.setFilterMode(FilterMode.PCF4);
        //pssmRenderer.displayDebug();
        viewPort.addProcessor(pssmRenderer);
        
        rootNode.setShadowMode(ShadowMode.Off);
        
        rootNode.attachChild(SkyFactory.createSky(
                assetManager, "Textures/Sky/Bright/BrightSky.dds", false));
//
//        fpp = new FilterPostProcessor(assetManager);
//        fpp.setNumSamples(4);
//        fog = new FogFilter();
//        fog.setFogColor(new ColorRGBA(0.8f, 0.8f, 1.0f, 1.0f));
//        fog.setFogDistance(400);
//        fog.setFogDensity(1.4f);
//        fpp.addFilter(fog);
        
        //viewPort.addProcessor(fpp);
        
        // First, we load up our textures and the heightmap texture for the terrain

        
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
        terrain.setShadowMode(ShadowMode.Receive);
        rootNode.attachChild(terrain);
    }

    /**
     * 
     */
    public void setupForester() {

        // Step 1 - set up the forester. The forester is a singleton class that
        // can be accessed statically from anywhere, but we use a reference
        // variable here.
        forester = Forester.getInstance();
        forester.initialize(rootNode, cam, terrain, null, this);

        forester.getForesterNode().setLocalTranslation(terrain.getLocalTranslation());
        
        // Step 2 - set up the treeloader. Page size is the same size as the
        // scaled terrain. Resolution is 4, meaning there are 4x4 = 16 blocks
        // of tree geometry per page (16 batches).
        // 
        // Far viewing range is set to 800, so that you can see the tree-blocks
        // being added and removed. Increase it by 100 or so and the trees will 
        // be added/removed seamlessly (no popping).
        TreeLoader treeLoader = forester.createTreeLoader(1026, 4, 800f);

        Texture density = terrain.getMaterial().getTextureParam("AlphaMap").getTextureValue();

        // Step 3 - set up the datagrid.
        forester.trees.datagrids.MapGrid mapGrid = treeLoader.createMapGrid();
        mapGrid.addDensityMap(density, 0, 0, 0);
        mapGrid.setThreshold(0.4f);

        // Step 4 - set up a tree layer
        Spatial model = assetManager.loadModel("Models/Spruce/SpruceMediumPoly.j3o");

        // Create a tree-layer and configure it. The density texture data and
        // density multiplier works as described in SimpleGrassTest.
        TreeLayer treeLayer = treeLoader.addTreeLayer(model, false);
        treeLayer.setDensityTextureData(0, Channel.Red);
        treeLayer.setDensityMultiplier(0.2f);

        treeLayer.setMaximumScale(3.5f);
        treeLayer.setMinimumScale(2f);

        treeLayer.setShadowMode(ShadowMode.Cast);

        //Adding some grass as well.
        GrassLoader grassLoader = forester.createGrassLoader(1026, 4, 300f, 20f);

        MapGrid grid = grassLoader.createMapGrid();

        grid.addDensityMap(density, 0, 0, 0);

        Material grassMat = assetManager.loadMaterial("Materials/Grass/Grass.j3m");

        GrassLayer layer = grassLoader.addLayer(grassMat, MeshType.CROSSQUADS);

        layer.setDensityTextureData(0, Channel.Red);

        layer.setDensityMultiplier(0.5f);

        layer.setMaxHeight(2.4f);
        layer.setMinHeight(2.f);

        layer.setMaxWidth(2.4f);
        layer.setMinWidth(2.f);

        layer.setMaxTerrainSlope(30);

        layer.setShadowMode(ShadowMode.CastAndReceive);
        
        ((GPAUniform) layer.getPlantingAlgorithm()).setThreshold(0.6f);

        grassLoader.setWind(new Vector2f(1, 0));
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