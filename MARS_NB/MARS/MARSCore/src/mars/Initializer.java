/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars;

import java.nio.ShortBuffer;
import mars.states.SimState;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.debug.BulletDebugAppState;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.font.BitmapFont;
import com.jme3.input.InputManager;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.water.SimpleWaterProcessor;
import com.jme3.water.WaterFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import mars.auv.AUV_Manager;
import mars.server.MARS_Server;
import mars.terrain.MultMesh;
import com.jme3.font.BitmapText;
import com.jme3.light.AmbientLight;
import com.jme3.math.Vector4f;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.debug.Grid;
import com.jme3.shadow.CompareMode;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.shadow.PssmShadowRenderer;
import com.jme3.shadow.PssmShadowRenderer.FilterMode;
import com.jme3.system.AppSettings;
import com.jme3.system.Timer;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.HillHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.texture.Texture2D;
import com.shaderblow.skydome.SkyDomeControl;
import forester.Forester;
import forester.grass.GrassLayer;
import forester.grass.GrassLayer.MeshType;
import forester.grass.GrassLoader;
import forester.grass.algorithms.GPAUniform;
import forester.grass.datagrids.MapGrid;
import forester.image.DensityMap.Channel;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import mars.auv.CommunicationManager;
import mars.auv.CommunicationManagerRunnable;
import mars.filter.FishEyeFilter;
import mars.filter.LensFlareFilter;
import mars.server.ros.ROS_Node;
import mars.waves.MyProjectedGrid;
import mars.waves.ProjectedWaterProcessorWithRefraction;
import mars.waves.WaterHeightGenerator;
import mygame.MaterialSP;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

/**
 * With this class we initialize all the different things on the
 * begining like "Do we want to load the terrrain?" Uses the MARS_Settings to determine
 * what ist activated with what mars_settings.
 * @author Thomas Tosik
 */
public class Initializer {

    private MARS_Settings mars_settings;
    private MARS_Main mars;
    private PhysicalEnvironment physical_environment;
    private Node guiNode;
    private Node rootNode;
    private Node axisNode = new Node("AxisNode");
    private Node gridNode = new Node("GridNode");
    private AppSettings settings;
    private InputManager inputManager;
    private Node sceneReflectionNode;
    private Collider RayDetectable;
    private AssetManager assetManager;
    private RenderManager renderManager;
    private ViewPort viewPort;
    private float water_height;
    private AUV_Manager auv_manager;
    private CommunicationManager com_manager;
    private BulletAppState bulletAppState;
    private FilterPostProcessor fpp;

    //terrrain
    private RigidBodyControl terrain_physics_control;
    private Node terrain_node;
    private int[] pixelSamples;
    private int[][] pixelSample;
    private int terrain_image_width = 0;
    private int terrain_image_heigth = 0;
    private byte[] terrain_byte_arrray;
    private ChannelBuffer terrainChannelBuffer;
    private TerrainQuad terrain;
    private Material mat_terrain;
    AbstractHeightMap heightmap;
    
    //flow
    private int[] pixelSamplesFlowX;
    private int[] pixelSamplesFlowY;
    private Node flowNode;
    private Vector3f flowVector = Vector3f.ZERO;
    private int flow_image_width = 0;
    private int flow_image_heigth = 0;
    
    //grass
    private Forester forester;
    private GrassLoader grassLoader;
    
    //light
    DirectionalLight sun;
    AmbientLight ambLight = new AmbientLight();
    DirectionalLightShadowRenderer dlsr;
    DirectionalLightShadowFilter dlsf;
    FilterPostProcessor fppS;
    
    //gui
    BitmapText ch;
    
    //water
    private WaterFilter water;
    private float waves_time = 0f;
    Geometry water_plane;
    
    //projected waves water
    private MyProjectedGrid grid;
    private Geometry projectedGridGeometry;
    private ProjectedWaterProcessorWithRefraction waterProcessor;
    private WaterHeightGenerator whg = new WaterHeightGenerator();
    
    //debug
    WireProcessor wireProcessor;

    //Server
    private MARS_Server raw_server;
    private Thread raw_server_thread;
    private ROS_Node ros_server;
    private Thread ros_server_thread;
    private CommunicationManagerRunnable com_server;
    private Thread com_server_thread;

    /**
     *
     * @param mars
     * @param MARS_settings 
     * @param auv_manager 
     * @param com_manager 
     * @deprecated 
     */
    @Deprecated
    public Initializer(MARS_Main mars, MARS_Settings MARS_settings, AUV_Manager auv_manager, CommunicationManager com_manager){
        this.mars = mars;
        this.mars_settings = MARS_settings;
        this.guiNode = mars.getGuiNode();
        this.settings = mars.getSettings();
        this.rootNode = mars.getRootNode();
        this.inputManager = mars.getInputManager();
        this.assetManager = mars.getAssetManager();
        //this.sceneReflectionNode = mars.getSceneReflectionNode();
        //this.SonarDetectableNode = mars.getSonarDetectableNode();
        this.viewPort = mars.getViewPort();
        this.water_height = MARS_settings.getPhysical_environment().getWater_height();
        this.auv_manager = auv_manager;
        this.com_manager = com_manager;
        this.renderManager = mars.getRenderManager();
        this.bulletAppState = mars.getStateManager().getState(BulletAppState.class);
        this.mars_settings.setInit(this);
        fpp = new FilterPostProcessor(assetManager);
    }
    
    /**
     * 
     * @param mars
     * @param simstate
     * @param auv_manager
     * @param com_manager
     * @param physical_environment  
     */
    public Initializer(MARS_Main mars, SimState simstate, AUV_Manager auv_manager, CommunicationManager com_manager, PhysicalEnvironment physical_environment){
        this.mars = mars;
        this.mars_settings = simstate.getMARSSettings();
        this.guiNode = mars.getGuiNode();
        this.settings = mars.getSettings();
        this.rootNode = simstate.getRootNode();
        this.inputManager = mars.getInputManager();
        this.assetManager = simstate.getAssetManager();
        this.physical_environment = physical_environment;
        this.sceneReflectionNode = simstate.getSceneReflectionNode();
        this.RayDetectable = simstate.getCollider();
        this.viewPort = mars.getViewPort();
        this.water_height = mars_settings.getPhysical_environment().getWater_height();
        this.auv_manager = auv_manager;
        this.com_manager = com_manager;
        this.renderManager = mars.getRenderManager();
        this.bulletAppState = mars.getStateManager().getState(BulletAppState.class);
        this.mars_settings.setInit(this);
        sun = new DirectionalLight();
        fpp = new FilterPostProcessor(assetManager);
    }

    /**
     * Calls this method once after you have added the MARS_Settings.
     */
    public void init(){
        //if(mars_settings.isSetupAxis()){
            setupAxis();
        //}
            setupGrid();
        if(mars_settings.isSetupFog()){
            setupFog();
        }
        //if(mars_settings.isSetupLight()){
            setupLight();
        //}
        //if(mars_settings.isSetupPlaneWater()){
            setupPlaneWater();
        //}
        if(mars_settings.isSetupSimpleSkyBox()){
            setupSimpleSkyBox();
        }
        if(mars_settings.isSetupSkyBox()){
            setupSkyBox();
        }
        if(mars_settings.isSetupSkyDome()){
            setupSkyDome();
        }
        if(mars_settings.isSetupTerrain() && !mars_settings.isSetupAdvancedTerrain()){
            setupTerrain();
        }
        if(mars_settings.isSetupTerrain() && mars_settings.isSetupAdvancedTerrain()){
            setupAdvancedTerrain();
        }
        if(mars_settings.isSetupGrass()){
            setupGrass();
        }
        if(mars_settings.isSetupWater()){
            setupWater();
        }
        if(mars_settings.isSetupWavesWater()){
            setupWavesWater();
        }
        //if(mars_settings.isSetupProjectedWavesWater()){
            setupProjectedWavesWater();
        //}
        if(mars_settings.isSetupWireFrame()){
            setupWireFrame();
        }
        //if(mars_settings.isSetupCrossHairs()){
            setupCrossHairs();
        //}
        if(mars_settings.isSetupDepthOfField()){
            setupDepthOfField();
        }
        if(mars_settings.isSetupShadow()){
            setupShadow();
        }
        setupServer();
        //setupGlow();
        //setupFishEye();
        //setupLensFlare();
        setupFlow();
        //add all the filters to the viewport(main window)
        viewPort.addProcessor(fpp);
    }
    
    public void cleanup(){  
        if(fppS != null){
            fppS.removeAllFilters();
            viewPort.removeProcessor(fppS);
            viewPort.removeProcessor(dlsr);
        }
        
        fpp.removeAllFilters();
        viewPort.removeProcessor(fpp);
        viewPort.removeProcessor(waterProcessor);
        
        if(wireProcessor != null){
        viewPort.removeProcessor(wireProcessor);
        }
        
        //cleanupProjectedWavesWater();
    }

    /**
     * 
     * @param NewViewPort
     */
    public void addFiltersToViewport(ViewPort NewViewPort){
        FilterPostProcessor fppp = new FilterPostProcessor(assetManager);
        if(mars_settings.isSetupFog()){
            fppp.addFilter(createFog());
        }
        if(mars_settings.isSetupDepthOfField()){
            fppp.addFilter(createDepthOfField());
        }
        NewViewPort.addProcessor(fppp);
    }

    /**
     * A centred plus sign to help the player aim.
     */
    private void setupCrossHairs() {
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
        ch.setLocalTranslation( // center
        settings.getWidth()/2 - guiFont.getCharSet().getRenderedSize()/3*2,
        settings.getHeight()/2 + ch.getLineHeight()/2, 0);
        guiNode.attachChild(ch);
        hideCrossHairs(mars_settings.isSetupCrossHairs());
    }

    /*
     * setting up the raw_server for communication with the auvs
     */
    /**
     * 
     */
    public void setupServer(){
        if(mars_settings.isRAW_Server_enabled()){
            raw_server = new MARS_Server( mars, auv_manager, com_manager );
            raw_server.setServerPort(mars_settings.getRAW_Server_port());
            raw_server_thread = new Thread( raw_server );
            raw_server_thread.start();
        }
        if(mars_settings.isROS_Server_enabled()){
            ros_server = new ROS_Node( mars, auv_manager, mars_settings );
            ros_server.setMaster_port(mars_settings.getROS_Server_port());
            ros_server.setMaster_ip(mars_settings.getROS_Master_IP());
            ros_server.setLocal_ip(mars_settings.getROS_Local_IP());
            ros_server.init();
            ros_server_thread = new Thread( ros_server );
            ros_server_thread.start();
            
            com_server = new CommunicationManagerRunnable(com_manager);
            com_server_thread = new Thread( com_server );
            com_server_thread.start();
        }
    }
    
    /**
     * 
     */
    public void killServer(){
        Logger.getLogger(Initializer.class.getName()).log(Level.INFO, "Killing CommunicationManager Server...", "");
        if(this.getCom_server() != null){
            this.getCom_server().setRunning(false);
            Logger.getLogger(Initializer.class.getName()).log(Level.INFO, "CommunicationManager Server killed!", "");
        }else{
            Logger.getLogger(Initializer.class.getName()).log(Level.INFO, "CommunicationManager Server not running. Cant be killed", "");
        }
        
        Logger.getLogger(Initializer.class.getName()).log(Level.INFO, "Killing ROS Server...", "");
        if(this.getROS_Server() != null){
            this.getROS_Server().shutdown();
            Logger.getLogger(Initializer.class.getName()).log(Level.INFO, "ROS Server killed!", "");
        }else{
            Logger.getLogger(Initializer.class.getName()).log(Level.INFO, "ROS Server not running. Cant be killed", "");
        }
    }
    
    /**
     * 
     * @return
     */
    public boolean ServerRunning(){
        if(this.isROS_ServerReady()){
            /*if(this.getROS_Server().getMarsNode() != null){
                if(this.getROS_Server().getMarsNode().isRunning()){
                    return true;
                }
                return true;
            }*/
            return true;
        }
        return false;
    }
    
    /**
     * 
     * @return
     */
    public boolean checkROSServer(){
                Logger.getLogger(Initializer.class.getName()).log(Level.INFO, "Waiting for ROS Server to be ready...", "");
                while(!this.isROS_ServerReady()){
                    
                }
                Logger.getLogger(Initializer.class.getName()).log(Level.INFO, "ROS Server ready.", "");
                Logger.getLogger(Initializer.class.getName()).log(Level.INFO, "Waiting for ROS Server Nodes to be created...", "");
                /*while(!this.getROS_Server().isInitReady()){
                    
                }*/
                Logger.getLogger(Initializer.class.getName()).log(Level.INFO, "ROS Server Nodes running.", "");
                //server_init = true;//server running, is needed because view is sometimes null in the beginning(see update)
                return true;
    }
    
    /**
     * 
     */
    @Deprecated
    public void setupROS_Server(){
        if(mars_settings.isROS_Server_enabled()){
            ros_server = new ROS_Node( mars, auv_manager, mars_settings );
            ros_server.setMaster_port(mars_settings.getROS_Server_port());
            ros_server.setMaster_ip(mars_settings.getROS_Master_IP());
            ros_server.setLocal_ip(mars_settings.getROS_Local_IP());
            ros_server.init();
            ros_server_thread = new Thread( ros_server );
            ros_server_thread.start();
        }
    }
    
    /**
     * 
     */
    public synchronized void start_ROS_Server(){
        if(ros_server_thread != null){
            ros_server_thread.start();
        }
    }
    
    /**
     * 
     * @return
     */
    public synchronized boolean isROS_ServerReady(){
        if(ros_server_thread != null){
            return ros_server_thread.isAlive();
        }else{
            return false;
        }
    }
    
    /**
     * 
     * @return
     */
    public synchronized ROS_Node getROS_Server(){
        return ros_server;
    }

    public CommunicationManagerRunnable getCom_server() {
        return com_server;
    }

    public Thread getCom_server_thread() {
        return com_server_thread;
    }
        
    /**
     * 
     * @return
     */
    public synchronized MARS_Server getRAW_Server(){
        return raw_server;
    }
    
    /**
     * 
     */
    public synchronized void testraw(){
        if(raw_server_thread != null){
            raw_server.sendStringToAllConnections("test");
        }
    }

    private void setupWireFrame(){
        //we want to see wireframes on all objects
        new WireProcessor(assetManager,mars_settings.getWireframecolor());
        viewPort.addProcessor(wireProcessor);
    }

    private DepthOfFieldFilter createDepthOfField(){
        DepthOfFieldFilter dofFilter = new DepthOfFieldFilter();
        dofFilter.setFocusDistance(0);
        dofFilter.setFocusRange(mars_settings.getFocusRange());
        dofFilter.setBlurScale(mars_settings.getBlurScale());
        return dofFilter;
    }

    /*
     * This creates a Depth of Field effect. Objects far away are blurred.
     */
    private void setupDepthOfField(){
        fpp.addFilter(createDepthOfField());
    }

    /*
     * This creates water with waves.
     */
    private void setupWavesWater(){
        water = new WaterFilter(rootNode, mars_settings.getLight_direction().normalizeLocal());
        water.setWaterHeight(water_height);
        water.setWaveScale(0.003f);
        water.setMaxAmplitude(0.3f);
        water.setFoamExistence(new Vector3f(0.45f, 3, 3.0f));
        water.setFoamIntensity(0.2f);
        water.setFoamHardness(0.5f);
        water.setFoamTexture((Texture2D) assetManager.loadTexture("Common/MatDefs/Water/Textures/foam.jpg"));
        water.setUseFoam(true);
        //water.setNormalScale(0.5f);
        //water.setRefractionConstant(0.25f);
        water.setRefractionStrength(0.2f);
        //water.setFoamHardness(0.6f);
        water.setWindDirection(new Vector2f(0f, 1f));
        
        //water.setUseRipples(false);
        water.setWaterTransparency(0.2f);
        water.setWaveScale(0.008f);
        water.setSpeed(0.2f);
        water.setShoreHardness(1.0f);
        water.setRefractionConstant(0.2f);
        water.setShininess(0.6f);
        water.setSunScale(1.0f);
        
        fpp.addFilter(water);
        
        BloomFilter bloom=new BloomFilter();
        bloom.setExposurePower(55);
        bloom.setBloomIntensity(1.0f);
        fpp.addFilter(bloom);
        
        LightScatteringFilter lsf = new LightScatteringFilter(mars_settings.getLight_direction().mult(-300f));
        lsf.setLightDensity(1.0f);
        fpp.addFilter(lsf);
    }
    
    /**
     * 
     */
    public void setupProjectedWavesWater(){
        setupgridwaves(mars.getCamera(),mars.getViewPort(),mars.getTimer());
    }

    private void setupgridwaves(Camera cam,ViewPort viewPort,Timer timer){
        grid = new MyProjectedGrid(timer, cam, 100, 70, 0.02f, whg);
        updateProjectedWavesWater();
        projectedGridGeometry = new Geometry("Projected Grid", grid);  // create cube geometry from the shape
        //projectedGridGeometry.setCullHint(CullHint.Never);
        //projectedGridGeometry.setQueueBucket(Bucket.Translucent);
        projectedGridGeometry.setMaterial(setWaterProcessor(cam,viewPort));
        projectedGridGeometry.setLocalTranslation(0, 0, 0);
        rootNode.attachChild(projectedGridGeometry);
        hideProjectedWavesWater(mars_settings.isSetupProjectedWavesWater());
    }
    
    /**
     * 
     */
    public void updateProjectedWavesWater(){
        whg.setHeightbig(mars_settings.getProjectedWavesWaterHeightbig());
        whg.setHeightsmall(mars_settings.getProjectedWavesWaterHeightsmall());
        whg.setScalexbig(mars_settings.getProjectedWavesWaterScalexbig());
        whg.setScalexsmall(mars_settings.getProjectedWavesWaterScalexsmall());
        whg.setScaleybig(mars_settings.getProjectedWavesWaterScaleybig());
        whg.setScaleysmall(mars_settings.getProjectedWavesWaterScaleysmall());
        whg.setSpeedbig(mars_settings.getProjectedWavesWaterSpeedbig());
        whg.setSpeedsmall(mars_settings.getProjectedWavesWaterSpeedsmall());
        whg.setOctaves(mars_settings.getProjectedWavesWaterOctaves());
    }
    
    private Material setWaterProcessor(Camera cam, ViewPort viewPort){   
        waterProcessor = new ProjectedWaterProcessorWithRefraction(cam,assetManager);
        waterProcessor.setReflectionScene(sceneReflectionNode);
        waterProcessor.setDebug(false);
        viewPort.addProcessor(waterProcessor);              
        return waterProcessor.getMaterial();
    }
    
    private void cleanupProjectedWavesWater(){
        waterProcessor.cleanup();
    }

    /**
     * 
     * @return
     */
    public WaterHeightGenerator getWhg() {
        return whg;
    }
    
    /**
     * 
     * @param x
     * @param z
     * @return
     */
    public float getCurrentWaterHeight(float x, float z){
        if(mars_settings.isSetupProjectedWavesWater()){
            return whg.getHeight(x, z, mars.getTimer().getTimeInSeconds());
        }else{
            return physical_environment.getWater_height();
        }
    }

    
    /**
     * 
     * @param tpf
     */
    public void updateProjectedWavesWater(float tpf){
        float[] angles = new float[3];
        mars.getCamera().getRotation().toAngles(angles);
        grid.update( mars.getCamera().getViewMatrix().clone());
    }
    
    /**
     *
     * @param tpf
     */
    public void updateWavesWater(float tpf){
        waves_time += tpf;
        float waterHeight = (float) Math.cos(((waves_time * 0.6f) % FastMath.TWO_PI)) * 1.5f;
        water.setWaterHeight(water_height + waterHeight);
    }

    /*
     * This creates shader water.
     */
    private void setupWater(){
        SimpleWaterProcessor waterProcessor = new SimpleWaterProcessor(assetManager);
        waterProcessor.setReflectionScene(sceneReflectionNode);
        waterProcessor.setDebug(false);
        waterProcessor.setLightPosition(mars_settings.getLight_direction().normalizeLocal());

        //setting the water plane
        Vector3f waterLocation=new Vector3f(0,-10,0);
        waterProcessor.setPlane(new Plane(Vector3f.UNIT_Y, waterLocation.dot(Vector3f.UNIT_Y)));
        waterProcessor.setWaterColor(ColorRGBA.Blue);
        //lower render size for higher performance
//        waterProcessor.setRenderSize(128,128);
        //raise depth to see through water
//        waterProcessor.setWaterDepth(20);
        //lower the distortion scale if the waves appear too strong
//        waterProcessor.setDistortionScale(0.1f);
        //lower the speed of the waves if they are too fast
//        waterProcessor.setWaveSpeed(0.01f);
        waterProcessor.setRefractionClippingOffset(100.0f);
        waterProcessor.setReflectionClippingOffset(0.0f);

        Quad quad = new Quad(1000,1000);

        //the texture coordinates define the general size of the waves
        quad.scaleTextureCoordinates(new Vector2f(6f,6f));

        Geometry water_geom=new Geometry("water", quad);
       // water.setShadowMode(ShadowMode.Recieve);
        water_geom.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));
        water_geom.setMaterial(waterProcessor.getMaterial());
        water_geom.setLocalTranslation(-500, water_height, 550);
        water_geom.setShadowMode(com.jme3.renderer.queue.RenderQueue.ShadowMode.Receive);

        rootNode.attachChild(water_geom);

        viewPort.addProcessor(waterProcessor);
    }

    /**
     * 
     */
    public void setupPlaneWater(){
        // A translucent/transparent texture, similar to a window frame.
        /*Box boxshape = new Box(new Vector3f(0f,0f,0f), 1000f,0.01f,1000f);
        water_plane = new Geometry("water_plane", boxshape);
        water_plane.setLocalTranslation(0.0f, water_height, 5.0f);
        Material mat_tt = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        assetManager.registerLocator("Assets/Textures/Water", FileLocator.class);
        mat_tt.setTexture("ColorMap", assetManager.loadTexture(mars_settings.getPlanewaterfilepath()));
        mat_tt.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        water_plane.setMaterial(mat_tt);
        water_plane.setQueueBucket(Bucket.Transparent);
        rootNode.attachChild(water_plane);*/
        //hidePlaneWater(mars_settings.isSetupPlaneWater());
        
        Future fut = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if(water_plane != null){
                    water_plane.removeFromParent();
                }
                Box boxshape = new Box(new Vector3f(0f,0f,0f), 1000f,0.01f,1000f);
                water_plane = new Geometry("water_plane", boxshape);
                water_plane.setLocalTranslation(0.0f, water_height, 5.0f);
                Material mat_tt = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                //assetManager.registerLocator("Assets/Textures/Water", FileLocator.class);
                mat_tt.setTexture("ColorMap", assetManager.loadTexture(mars_settings.getPlanewaterfilepath()));
                mat_tt.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
                water_plane.setMaterial(mat_tt);
                water_plane.setQueueBucket(Bucket.Transparent);
                rootNode.attachChild(water_plane);
                hidePlaneWater(mars_settings.isSetupPlaneWater());
                return null;
             }
        });
    }

    private FogFilter createFog(){
        FogFilter fog = new FogFilter();
        fog.setFogColor(mars_settings.getFogcolor());
        fog.setFogDistance(mars_settings.getFogDistance());
        fog.setFogDensity(mars_settings.getFogDensity());
        return fog;
    }
    /*
     * This is only rudimental fog. This will be deprecated in the futrue in jme3. For Water there will be an own fog system.
     */
    private void setupFog(){
        fpp.addFilter(createFog());
    }
    
    private void setupGlow(){
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bloom);
        //mars.getViewPort().addProcessor(fpp);
    }
    
    private void setupFishEye(){
        FishEyeFilter fisheye = new FishEyeFilter();
        fpp.addFilter(fisheye);
    }
    
    private void setupLensFlare(){
        //LensFlareFilter lf = new LensFlareFilter("Textures/lensdirt.png"); // or null if you don't own a +1 Lens Cloth of Smiting
        LensFlareFilter lf = new LensFlareFilter(null); // or null if you don't own a +1 Lens Cloth of Smiting
        lf.setGhostSpacing(0.125f);
        lf.setHaloDistance(0.48f);
        fpp.addFilter(lf);
    }

    /**
     * 
     */
    public void setupLight(){
        Future fut = mars.enqueue(new Callable() {
                    public Void call() throws Exception {
                        rootNode.removeLight(sun);//remove all old stuff before
                        rootNode.removeLight(ambLight);
                        sun.setColor(mars_settings.getLight_color());
                        sun.setDirection(mars_settings.getLight_direction().normalize());
                        ambLight.setColor(new ColorRGBA(0.8f, 0.8f, 0.8f, 0.2f));
                        if(mars_settings.isSetupLight()){
                            rootNode.addLight(sun);
                        }else{
                            rootNode.removeLight(sun);
                        }
                        if(mars_settings.isSetupAmbient()){
                            rootNode.addLight(ambLight);
                        }else{
                            rootNode.removeLight(ambLight);
                        }
                        return null;
                    }
                });
       /* AmbientLight amb = new AmbientLight();
        amb.setColor(mars_settings.getLight_color().multLocal(0.1f));
        rootNode.addLight(amb);*/
    }

    /*
     * A simple sky. Makes the background color of the viewport not black ;).
     */
    private void setupSimpleSkyBox(){
        renderManager.getMainView("Default").setBackgroundColor( mars_settings.getSimpleskycolor() );
    }

    /*
     * This creates a sky.
     */
    private void setupSkyBox(){
        //assetManager.registerLocator("Assets/Textures/Sky", FileLocator.class);
        Spatial sky = (SkyFactory.createSky(assetManager, mars_settings.getSkyboxfilepath(), false));
        sky.setLocalScale(100);
        sceneReflectionNode.attachChild(sky);
    }
    
    /*
     * This creates a dynamic sky.
     */
    private void setupSkyDome(){
        //this.assetManager.registerLocator("Assets/shaderblowlibs", FileLocator.class);
        SkyDomeControl skyDome = new SkyDomeControl(assetManager, mars.getCamera(),
                "TestModels/SkyDome/SkyDome.j3o",
                "TestTextures/SkyDome/SkyNight_L.png",
                "TestTextures/SkyDome/Moon_L.png",
                "TestTextures/SkyDome/Clouds_L.png",
                "TestTextures/SkyDome/Fog_Alpha.png");
        Node sky = new Node();
        sky.setQueueBucket(Bucket.Sky);
        sky.addControl(skyDome);
        sky.setCullHint(Spatial.CullHint.Never);
        
        // Either add a reference to the control for the existing JME fog filter or use the one I posted…
// But… REMEMBER!  If you use JME’s… the sky dome will have fog rendered over it.
// Sorta pointless at that point
//        FogFilter fog = new FogFilter(ColorRGBA.Blue, 0.5f, 10f);
//        skyDome.setFogFilter(fog, viewPort);

// Set some fog colors… or not (defaults are cool)
        /*skyDome.setFogColor(ColorRGBA.Blue);
        skyDome.setFogNightColor(new ColorRGBA(0.5f, 0.5f, 1f, 1f));
        skyDome.setDaySkyColor(new ColorRGBA(0.5f, 0.5f, 0.9f, 1f));*/

// Enable the control to modify the fog filter
        skyDome.setControlFog(false);

// Add the directional light you use for sun… or not
        /*DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.8f, -0.6f, -0.08f).normalizeLocal());
        sun.setColor(new ColorRGBA(1, 1, 1, 1));
        rootNode.addLight(sun);
        skyDome.setSun(sun);*/
        skyDome.setSun(sun);
        
        /*AmbientLight al = new AmbientLight();
        al.setColor(new ColorRGBA(0.7f,0.7f,1f,1.0f));
        rootNode.addLight(al);  */        

// Set some sunlight day/night colors… or not
        skyDome.setSunDayLight(new ColorRGBA(1, 1, 1, 1));
        skyDome.setSunNightLight(new ColorRGBA(0.5f, 0.5f, 0.9f, 1f));

// Enable the control to modify your sunlight
        skyDome.setControlSun(true);

// Enable the control
        skyDome.setEnabled(true);
        skyDome.cycleNightToDay();
        skyDome.cycleNightToDay();
// Add the skydome to the root… or where ever
        rootNode.attachChild(sky);
    }

    /*
     * give us some orientation
     */
    private void setupAxis(){
        Geometry y_axis = new Geometry("y_axis", new Arrow(Vector3f.UNIT_Y.mult(1)));
        Material y_axis_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        y_axis_mat.setColor("Color", ColorRGBA.Green);
        y_axis.setMaterial(y_axis_mat);
        y_axis.setLocalTranslation(new Vector3f(0f,0f,0f));
        y_axis.updateGeometricState();
        axisNode.attachChild(y_axis);

        Geometry x_axis = new Geometry("x_axis!", new Arrow(Vector3f.UNIT_X.mult(1)));
        Material x_axis_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        x_axis_mat.setColor("Color", ColorRGBA.Blue);
        x_axis.setMaterial(x_axis_mat);
        x_axis.setLocalTranslation(new Vector3f(0f,0f,0f));
        x_axis.updateGeometricState();
        axisNode.attachChild(x_axis);

        Geometry z_axis = new Geometry("z_axis", new Arrow(Vector3f.UNIT_Z.mult(1)));
        Material z_axis_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        z_axis_mat.setColor("Color", ColorRGBA.Red);
        z_axis.setMaterial(z_axis_mat);
        z_axis.setLocalTranslation(new Vector3f(0f,0f,0f));
        z_axis.updateGeometricState();
        axisNode.attachChild(z_axis);
        
         /*//Geometry length_axis = new Geometry("length_axis", new Arrow(Vector3f.UNIT_Z.mult(0.6f)));
         Geometry length_axis = new Geometry("length_axis", new Arrow(Vector3f.UNIT_Z.mult(0.615f)));
         Material length_axis_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         length_axis_mat.setColor("Color", ColorRGBA.Red);
         length_axis.setMaterial(length_axis_mat);
         //length_axis.setLocalTranslation(new Vector3f(0f,-2f,0f));
         length_axis.setLocalTranslation(new Vector3f(0f,0f,0f));
         length_axis.updateGeometricState();
         axisNode.attachChild(length_axis);*/
        rootNode.attachChild(axisNode);
         /*Geometry length_axis = new Geometry("length_axis", new Arrow(Vector3f.UNIT_Z.mult(2.87f)));
         Material length_axis_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         length_axis_mat.setColor("Color", ColorRGBA.Red);
         length_axis.setMaterial(length_axis_mat);
         length_axis.setLocalTranslation(new Vector3f(0f,0f,-0.32f));
         //length_axis.setLocalTranslation(new Vector3f(0f,0f,-0.615f));
         length_axis.updateGeometricState();
         rootNode.attachChild(length_axis);*/
        hideAxis(mars_settings.isSetupAxis());
    }
    
    /**
     * 
     */
    public void setupGrid(){
        Future fut = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                gridNode.detachAllChildren();
                Geometry grid = new Geometry("wireframe grid", new Grid(mars_settings.getSizeX(), mars_settings.getSizeY(), mars_settings.getGridLineDistance()));
                Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat.getAdditionalRenderState().setWireframe(true);
                mat.setColor("Color", mars_settings.getGridColor());
                grid.setMaterial(mat);
                grid.center().move(mars_settings.getGridPosition());
                Quaternion quat = new Quaternion();
                quat.fromAngles(mars_settings.getGridRotation().x, mars_settings.getGridRotation().y, mars_settings.getGridRotation().z);
                grid.setLocalRotation(quat);
                gridNode.attachChild(grid);
                rootNode.attachChild(gridNode);
                hideGrid(mars_settings.isSetupGrid());
                return null;
             }
        });
    }
    
    /**
     * 
     * @param hide
     */
    public void hideAxis(boolean hide){
        if(!hide){
            axisNode.setCullHint(CullHint.Always);
        }else{
            axisNode.setCullHint(CullHint.Never);
        }
    }
    
    /**
     * 
     * @param hide
     */
    public void hideGrid(boolean hide){
        if(!hide){
            gridNode.setCullHint(CullHint.Always);
        }else{
            gridNode.setCullHint(CullHint.Never);
        }
    }
    
    /**
     * 
     * @param hide
     */
    public void showPhysicsDebug(boolean hide){
        if (mars.getStateManager().getState(BulletAppState.class) != null) {
            mars.getStateManager().getState(BulletAppState.class).setDebugEnabled(hide);
        }
    }
    
    /**
     * 
     * @param hide
     */
    public void hidePlaneWater(boolean hide){
        if(!hide){
            water_plane.setCullHint(CullHint.Always);
        }else{
            water_plane.setCullHint(CullHint.Never);
        }
    }
    
    /**
     * 
     * @param hide
     */
    public void hideProjectedWavesWater(boolean hide){
        if(!hide){
            projectedGridGeometry.setCullHint(CullHint.Always);
        }else{
            projectedGridGeometry.setCullHint(CullHint.Never);
        }
    }
    
    /**
     * 
     * @param hide
     */
    public void hideCrossHairs(boolean hide){
        if(!hide){
            ch.setCullHint(CullHint.Always);
        }else{
            ch.setCullHint(CullHint.Never);
        }
    }
    
    /**
     * 
     * @param hide
     */
    public void hideFPS(boolean hide){
        if(!hide){
            mars.setDisplayFps(false);
            mars.setDisplayStatView(false);
        }else{
            mars.setDisplayFps(true);
            mars.setDisplayStatView(true);
        }
    }
    
    /**
     * 
     * @param framelimit
     */
    public void changeFrameLimit(int framelimit){
        mars.getSettings().setFrameRate(framelimit);
        mars.restart();
    }
    
    public void changeSpeed(float speed){
        mars.setSpeed(speed);
    }
    
    /**
     * 
     */
    public void changePlaneWater(){
        
    }
    
    /**
     * 
     * @param tpf
     */
    public void updateGrass(float tpf){
        forester.update(tpf);
    }
    
    private void setupGrass(){
        float grassScale = 32f;
        float dirtScale = 32f;
        float roadScale = 32f;
        //assetManager.registerLocator("Assets", FileLocator.class);
        //assetManager.registerLocator("Assets/Textures/Terrain", FileLocator.class);
        //assetManager.registerLocator("Assets/Forester", FileLocator.class);
        //MaterialSP terrainMat = new MaterialSP(assetManager,"MatDefs/TerrainBase.j3md");
        // First, we load up our textures and the heightmap texture for the terrain

        // ALPHA map (for splat textures)
        //terrainMat.setTexture("AlphaMap", assetManager.loadTexture("Textures/Sea/sea_alphamap2.png"));
        //terrainMat.setTexture("AlphaMap", assetManager.loadTexture(mars_settings.getTerrainfilepath_am()));

        //Vector4f texScales = new Vector4f();
        
        // GRASS texture
        //Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
        //Texture grass = assetManager.loadTexture(mars_settings.getTerrainfilepath_cm());
        /*Texture grass = assetManager.loadTexture("Textures/Sea/seamless_beach_sand.jpg");
        grass.setWrap(WrapMode.Repeat);
        terrainMat.setTexture("TextureRed", grass);
        texScales.x = grassScale;*/
        
        // DIRT texture
        /*Texture dirt = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
        dirt.setWrap(WrapMode.Repeat);
        terrainMat.setTexture("TextureGreen", dirt);
        texScales.y = dirtScale;*/

        // ROCK texture
        /*Texture road = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
        road.setWrap(WrapMode.Repeat);
        terrainMat.setTexture("TextureBlue", road);
        texScales.z = roadScale;
        
        terrainMat.setVector4("TexScales", texScales);
        
        terrain.setMaterial(terrainMat);*/

        // Step 1 - set up the forester. The forester is a singleton class that
        // can be accessed statically from anywhere, but we use a reference
        // variable here.
        forester = Forester.getInstance();
        forester.initialize(rootNode, mars.getCamera(), terrain, null,mars);
        
        // Displace the vegetation.
        forester.getForesterNode().setLocalTranslation(new Vector3f(0f, -2f, 0f));//0,-4,0
        forester.getForesterNode().setCullHint(CullHint.Never);
        //forester.getForesterNode().setLocalTranslation(new Vector3f(0f, -2f, 0f));
        
        // Step 2 - set up the grassloader. We're using a pagesize of 1026 in
        // this demo, which is the same size as the scaled terrain. We use a
        // resolution of 4, meaning we get a grid of 4x4 blocks of grass
        // in total; each 256x256 units in size. Far viewing range is 300 world
        // units, and fading range is 20. (heightmap.getSize()*2)+2
        // BUGGGY LIKE HELL!!!!!!!!!!!!!!
        grassLoader = forester.createGrassLoader(80, 8, mars_settings.getGrassFarViewingDistance(), mars_settings.getGrassFadingRange());//res:80

        // Step 3 - set up the mapgrid. This is where you link densitymaps with
        // terrain tiles if you use a custom grid (not terrain grid).
        MapGrid grasGrid = grassLoader.createMapGrid();
        // Now add a texture to the grid. We will only use one map here, and only one gridcell (0,0).
        // The three zeros are x-coord, z-coord and density map index (starting from 0).
        // If we wanted to index this texture as densitymap 2 in cell (43,-12) we would have
        // written grid.addDensityMap(density,43,-12,2);
        Material material = terrain.getMaterial();
        Texture density = terrain.getMaterial().getTextureParam("AlphaMap").getTextureValue();
        //Texture density = terrain.getMaterial().getTextureParam("Alpha").getTextureValue();//because of lighing terrain
        grasGrid.addDensityMap(density, 0, 0, 0);

        // Step 4 - set up a grass layer. We're gonna use the Grass.j3m material file
        // for the grass in this layer. The texture and all other variables are already
        // set in the material. Another alternative would have been to set them 
        // programatically (through the GrassLayer's methods). 
        //Material grassMat = assetManager.loadMaterial("Materials/Grass/Grass.j3m");
        Material grassMat = assetManager.loadMaterial("Materials/Grass/GreenSeaweed.j3m");
        grassMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        
        // Two parameters - the material, and the type of grass mesh to create.
        // Crossquads are two static quads that cross eachother at a right angle.
        // Switching between CROSSQUADS and QUADS is straightforward, but for
        // BILLBOARDS we need to use a different material base.
        GrassLayer layer = grassLoader.addLayer(grassMat,MeshType.CROSSQUADS);
        layer.setSwaying(true);
        layer.setSwayingFrequency(8f);
        // Important! Link this particular grass layer with densitymap nr. 0 
        // This is the number we used for the alphamap when we set up the mapgrid.
        // We choose the red channel here (the grass texture channel) for density values.
        layer.setDensityTextureData(0, Channel.Red);
        
        layer.setDensityMultiplier(0.6f);
        
        // This sets the size boundaries of the grass-quads. When generated,
        // the quads will vary in size randomly, but sizes will never exceed these
        // bounds. Also, aspect ratio is always preserved.
        
        layer.setMinHeight(0.2f);
        layer.setMaxHeight(0.6f);
        
        layer.setMinWidth(0.2f);
        layer.setMaxWidth(0.6f);
        
        // Setting a maximum slope for the grassquads, to reduce stretching. 
        // No grass is placed in areas with a slope higher then this angle.
        //
        // Use a degree between 0 and 90 (it's automatically normalized to this 
        // range). The default value is 30 degrees, so this is not really
        // necessary here, but I set it to show how it works.
        layer.setMaxTerrainSlope(30);
        
        //layer.setShadowMode(ShadowMode.Receive);<-- bugged
        
        // This is a way of discarding all densityvalues that are lower then 0.6.
        // A threshold value is optional, but in this case it's useful to restrict
        // grass from being planted in areas where the grass texture is only a few
        // percent visible (dominated by other textures).
        //((GPAUniform)layer.getPlantingAlgorithm()).setThreshold(0.9f);
        
        
        // Adding another grasslayer.
        
        Material grassMat2 = assetManager.loadMaterial("Materials/Grass/Stalk.j3m");

        // Using billboards. Different material base but pretty much the same
        // parameters.
        GrassLayer layer2 = grassLoader.addLayer(grassMat2,MeshType.BILLBOARDS);

        layer2.setSwaying(true);
        layer2.setSwayingFrequency(4f);
        
        // Using the same densitymap and channel as the grass.
        layer2.setDensityTextureData(0, Channel.Red);
        layer2.setDensityMultiplier(0.4f);
        
        layer2.setMinHeight(0.2f);
        layer2.setMaxHeight(0.8f);
        
        layer2.setMinWidth(0.2f);
        layer2.setMaxWidth(0.8f);
        
        //((GPAUniform)layer2.getPlantingAlgorithm()).setThreshold(0.6f);
        
         // Adding another grasslayer.
        
        Material grassMat3 = assetManager.loadMaterial("Materials/Grass/RedSeaweed.j3m");

        // Using billboards. Different material base but pretty much the same
        // parameters.
        GrassLayer layer3 = grassLoader.addLayer(grassMat3,MeshType.CROSSQUADS);

        layer3.setSwaying(true);
        layer3.setSwayingFrequency(3f);
        
        // Using the same densitymap and channel as the grass.
        layer3.setDensityTextureData(0, Channel.Red);
        layer3.setDensityMultiplier(0.2f);
        
        layer3.setMinHeight(0.2f);
        layer3.setMaxHeight(0.4f);
        
        layer3.setMinWidth(0.2f);
        layer3.setMaxWidth(0.4f);
        
        //((GPAUniform)layer3.getPlantingAlgorithm()).setThreshold(0.3f);
        
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
        grassLoader.setWind(new Vector2f(mars_settings.getPhysical_environment().getWater_current().getX(),mars_settings.getPhysical_environment().getWater_current().getZ()));
    }

    private void setupAdvancedTerrain(){
        /** 1. Create terrain material and load four textures into it. */
        /*mat_terrain = new Material(assetManager, 
                "Common/MatDefs/Terrain/Terrain.j3md");*/
        mat_terrain = new Material(assetManager, 
                "Common/MatDefs/Terrain/TerrainLighting.j3md");
        mat_terrain.setBoolean("useTriPlanarMapping", false);
        //mat_terrain.setFloat("Shininess", 0.5f);
                
        /** 1.1) Add ALPHA map (for red-blue-green coded splat textures) */
        /*mat_terrain.setTexture("Alpha", assetManager.loadTexture(
                "Textures/Terrain/splat/alphamap.png"));*/
        //assetManager.registerLocator("Assets/Textures/Terrain", FileLocator.class);
        //assetManager.registerLocator("Assets/Forester", FileLocator.class);
        Texture alphaMapImage = assetManager.loadTexture(
                mars_settings.getTerrainfilepath_am());
        //alphaMapImage.getImage().setFormat(Format.RGBA8);
        //mat_terrain.setTexture("Alpha", alphaMapImage);
        mat_terrain.setTexture("AlphaMap", alphaMapImage);

        /** 1.2) Add GRASS texture into the red layer (Tex1). */
        /*Texture grass = assetManager.loadTexture(
                "Textures/Terrain/splat/grass.jpg");*/
        Texture grass = assetManager.loadTexture(
                mars_settings.getTerrainfilepath_cm());
       /* assetManager.registerLocator("Assets/Forester", FileLocator.class);
        Texture grass = assetManager.loadTexture("Textures/Sea/seamless_beach_sand.jpg");
        grass.setWrap(WrapMode.Repeat);
        //mat_terrain.setTexture("Tex1", grass);
        //mat_terrain.setFloat("Tex1Scale", 1f);*/
        mat_terrain.setTexture("DiffuseMap", grass);
        mat_terrain.setFloat("DiffuseMap_0_scale", 1f);

        /** 1.3) Add DIRT texture into the green layer (Tex2) */
        /*Texture dirt = assetManager.loadTexture(
                "Textures/Terrain/splat/dirt.jpg");
        dirt.setWrap(WrapMode.Repeat);
        mat_terrain.setTexture("Tex2", dirt);
        mat_terrain.setFloat("Tex2Scale", 32f);*/
        /*Texture dirt = assetManager.loadTexture(
                "Textures/Terrain/splat/grass.jpg");
        dirt.setWrap(WrapMode.Repeat);
        mat_terrain.setTexture("DiffuseMap_1", dirt);
        mat_terrain.setFloat("DiffuseMap_1_scale", 64f);*/

        /** 1.4) Add ROAD texture into the blue layer (Tex3) */
        /*assetManager.registerLocator("Assets/Forester", FileLocator.class);
        Texture rock = assetManager.loadTexture(
                "Textures/Terrain/splat/grass.jpg");
        rock.setWrap(WrapMode.Repeat);
        mat_terrain.setTexture("Tex3", rock);
        mat_terrain.setFloat("Tex3Scale", 64f);*/
    
        /** 2. Create the height map */
        /*Texture heightMapImage = assetManager.loadTexture(
                "Textures/Terrain/splat/mountains512.png");*/
        Texture heightMapImage = assetManager.loadTexture(
                mars_settings.getTerrainfilepath_hm());
        //heightMapImage.getImage().setFormat(Format.RGB8);//fix for format problems
        heightmap = new ImageBasedHeightMap(heightMapImage.getImage());
        terrain_image_heigth = heightMapImage.getImage().getHeight();
        terrain_image_width = heightMapImage.getImage().getWidth();
        heightmap.load();
        
        //convert terrain for ros
        terrain_byte_arrray = new byte[terrain_image_heigth*terrain_image_width];
        float[] heightMap = heightmap.getHeightMap();
        for (int i = 0; i < (terrain_image_heigth*terrain_image_width); i++) {
            terrain_byte_arrray[i] = (byte)Math.round(((heightMap[i]*100f)/255f));
        }
        terrainChannelBuffer = ChannelBuffers.copiedBuffer(ByteOrder.LITTLE_ENDIAN,terrain_byte_arrray);
        
        //random terrain generation
        /*HillHeightMap heightmap2 = null;
        try {
            heightmap2 = new HillHeightMap(256, 1000, 50, 100, (byte) 4);
        } catch (Exception ex) {
            ex.printStackTrace();
        }*/
        
        /** 3. We have prepared material and heightmap. 
         * Now we create the actual terrain:
         * 3.1) Create a TerrainQuad and name it "my terrain".
         * 3.2) A good value for terrain tiles is 64x64 -- so we supply 64+1=65.
         * 3.3) We prepared a heightmap of size 512x512 -- so we supply 512+1=513.
         * 3.4) As LOD step scale we supply Vector3f(1,1,1).
         * 3.5) We supply the prepared heightmap itself.
         */
        int patchSize = mars_settings.getTerrainPatchSize()+1;
        terrain = new TerrainQuad("advancedTerrain", patchSize, (heightmap.getSize())+1, heightmap.getHeightMap());

        /** 4. We give the terrain its material, position & scale it, and attach it. */
        terrain.setMaterial(mat_terrain);
        terrain.setLocalTranslation(mars_settings.getTerrain_position());
        terrain.setLocalScale(mars_settings.getTerrain_scale());
        float[] rots = new float[3];
        rots[0] = mars_settings.getTerrain_rotation().getX();
        rots[1] = mars_settings.getTerrain_rotation().getY();
        rots[2] = mars_settings.getTerrain_rotation().getZ();
        Quaternion rot = new Quaternion(rots);
        terrain.setLocalRotation(rot);
        //rootNode.attachChild(terrain);
        
        /** 5. The LOD (level of detail) depends on were the camera is: */
        TerrainLodControl control = new TerrainLodControl(terrain, mars.getCamera());
        control.setLodCalculator(new DistanceLodCalculator(65, 2.7f));
        terrain.addControl(control);
        control.setEnabled(mars_settings.isTerrainLod());
        
        terrain_node = new Node("terrain");
        /*terrain_node.setLocalTranslation(mars_settings.getTerrain_position());
        float[] rots = new float[3];
        rots[0] = mars_settings.getTerrain_rotation().getX();
        rots[1] = mars_settings.getTerrain_rotation().getY();
        rots[2] = mars_settings.getTerrain_rotation().getZ();
        Quaternion rot = new Quaternion(rots);
        terrain_node.setLocalRotation(rot);
        terrain_node.setLocalScale(mars_settings.getTerrain_scale());
        terrain_node.updateGeometricState();*/
        
        /** 6. Add physics: */ 
        // We set up collision detection for the scene by creating a
        // compound collision shape and a static RigidBodyControl with mass zero.*/
        //Making a terrain Physics
        //terrain_node = new Node("terrain");
        CollisionShape terrainShape = CollisionShapeFactory.createMeshShape(terrain);

        //terrain_node = new Node("terrain");
        terrain_physics_control = new RigidBodyControl(terrainShape, 0);

        /*Material debug_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        debug_mat.setColor("Color", ColorRGBA.Red);
        Spatial createDebugShape = terrain_physics_control.createDebugShape(assetManager);
        createDebugShape.setMaterial(debug_mat);
        terrain_node.attachChild(createDebugShape);*/
        
        
        terrain_physics_control.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_01);
        terrain_physics_control.setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_01);
        //terrain_physics_control.setFriction(0f);
        //terrain_physics_control.setRestitution(1f);
        //terrain_node.attachChild(terrain);
        terrain.addControl(terrain_physics_control);
        
        //set shadwos for terrain
        terrain.setShadowMode(ShadowMode.Receive);
        
        terrain_node.attachChild(terrain);
        //SonarDetectableNode.attachChild(terrain_node);
        sceneReflectionNode.attachChild(terrain_node);
        RayDetectable.attachChild(terrain_node);
        //bulletAppState.getPhysicsSpace().add(terrain);
        bulletAppState.getPhysicsSpace().add(terrain_physics_control);
    }
    
    private void setupTerrain(){
        //read the gray scale map
        File file = new File("./Assets/Textures/Terrain/" + mars_settings.getTerrainfilepath_hm());
        BufferedImage bimage = null;
        try {
            bimage = ImageIO.read(file);
        } catch (IOException ex) {
        }

        int w = bimage.getWidth();
        int h = bimage.getHeight();
        terrain_image_width = w;
        terrain_image_heigth = h;
        pixelSamples = new int[h * w];
        pixelSample = new int[h][w];
        terrain_byte_arrray = new byte[h*w];
        bimage.getRaster().getSamples(0, 0, w, h, 0, pixelSamples);
        int count = 0;
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                pixelSample[i][j] = pixelSamples[count];
                /*if(Math.round(((pixelSamples[count]*100f)/256f)) < 20f){
                    terrain_byte_arrray[count] = (byte)(0);
                }else{*/
                    terrain_byte_arrray[count] = (byte)Math.round(((pixelSamples[count]*100f)/255f));
                //}
                //System.out.println(count + ": " + pixelSamples[count]);
                count++;
            }
        }
        //generate vectors based on the gray information
        Vector3f[][] vertex = new Vector3f[h][w];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                vertex[i][j] = new Vector3f(j * mars_settings.getTerrain_scale().x, pixelSample[i][j] / mars_settings.getTileHeigth(), i * mars_settings.getTerrain_scale().z);
            }
        }
        //pass the vectors to the MultMesh object
        MultMesh mm = new MultMesh(vertex);
        mm.initMesh();
        //setup material
        Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //assetManager.registerLocator("Assets/Textures/Terrain", FileLocator.class);
        TextureKey key2 = new TextureKey(mars_settings.getTerrainfilepath_cm());
        key2.setGenerateMips(true);
        Texture tex2 = assetManager.loadTexture(key2);
        mat2.setTexture("ColorMap", tex2);
        //generate grometry
        Geometry mmG = new Geometry("TerrainMesh", mm);
        mmG.setMaterial(mat2);
        mmG.updateModelBound();
        mmG.updateGeometricState();

        //Making a terrain Physics
        CollisionShape terrainShape = CollisionShapeFactory.createMeshShape(mmG);

        //CompoundCollisionShape compoundCollisionShape1 = new CompoundCollisionShape();

        //BoxCollisionShape boxCollisionShape = new BoxCollisionShape(new Vector3f(5f,5f,2f));
        //compoundCollisionShape1.addChildShape(terrainShape, new Vector3f(55f,3f,17f));

        terrain_node = new Node("terrain");
        terrain_physics_control = new RigidBodyControl(terrainShape, 0);

        /*Material debug_mat = new Material(assetManager, "Common/MatDefs/Misc/WireColor.j3md");
            debug_mat.setColor("Color", ColorRGBA.Red);
            terrain_physics_control.attachDebugShape(debug_mat);*/

        terrain_physics_control.setCollisionGroup(1);
        terrain_physics_control.setCollideWithGroups(1);
        //terrain_physics_control.setFriction(0f);
        //terrain_physics_control.setRestitution(1f);

        terrain_node.setLocalTranslation(mars_settings.getTerrain_position());
        terrain_node.addControl(terrain_physics_control);
        terrain_node.updateGeometricState();


        /*physicsTerrain = new PhysicsNode(mmG,terrainShape,0);
        physicsTerrain.setName("terrain");
        physicsTerrain.setLocalTranslation(mars_settings.getTerrain_position());
        physicsTerrain.updateGeometricState();
        physicsTerrain.updateModelBound();*/

        terrain_node.attachChild(mmG);
        sceneReflectionNode.attachChild(terrain_node);
        RayDetectable.attachChild(terrain_node);
        bulletAppState.getPhysicsSpace().add(terrain_node);
    }
    
    /**
     * 
     */
    public void updateTerrain(){
        Future fut = mars.enqueue(new Callable() {
                    public Void call() throws Exception {
                        if(terrain_node != null){
                            terrain_node.setLocalTranslation(mars_settings.getTerrain_position());
                            float[] rots = new float[3];
                            rots[0] = mars_settings.getTerrain_rotation().getX();
                            rots[1] = mars_settings.getTerrain_rotation().getY();
                            rots[2] = mars_settings.getTerrain_rotation().getZ();
                            Quaternion rot = new Quaternion(rots);
                            terrain_node.setLocalRotation(rot);
                            terrain_node.setLocalScale(mars_settings.getTerrain_scale());
                            //terrain_physics_control.setPhysicsLocation(mars_settings.getTerrain_position());
                            //terrain_physics_control.setPhysicsRotation(rot);
                        }
                        return null;
                    }
        });
    }
    
    private void setupFlow(){
        //assetManager.registerLocator("Assets/Textures/Flow", FileLocator.class);

        Texture heightMapImage = assetManager.loadTexture(
                mars_settings.getFlowfilepath_x());
        heightMapImage.getImage().setFormat(Format.Luminance16);//fix for format problems
        
        int w = heightMapImage.getImage().getWidth();
        int h = heightMapImage.getImage().getHeight();
        flow_image_heigth = h;
        flow_image_width = w;
        pixelSamplesFlowX = new int[h * w];

        pixelSamplesFlowX = load(false, false, heightMapImage.getImage());
        
        
        Texture heightMapImage2 = assetManager.loadTexture(
                mars_settings.getFlowfilepath_y());
        heightMapImage2.getImage().setFormat(Format.Luminance16);//fix for format problems

        int w2 = heightMapImage2.getImage().getWidth();
        int h2 = heightMapImage2.getImage().getHeight();
        pixelSamplesFlowY = new int[h2 * w2];
        
        pixelSamplesFlowY = load(false, false, heightMapImage2.getImage());

        flowNode = new Node("flow");

        //flowNode.setLocalTranslation(mars_settings.getTerrain_position());
        flowNode.updateGeometricState();

        Geometry grid = new Geometry("flow grid", new Grid(h, w, mars_settings.getTerrain_scale().x));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", mars_settings.getGridColor());
        grid.setMaterial(mat);
        //grid.center().move(mars_settings.getGridPosition());
        //Quaternion quat = new Quaternion();
        //quat.fromAngles(mars_settings.getGridRotation().x, mars_settings.getGridRotation().y, mars_settings.getGridRotation().z);
        //grid.setLocalRotation(quat);
        grid.move(0f, -1f, 0f);
        
        //add vectors to grid
        /*for (int i = 0; i < 250; i++) {
            for (int j = 0; j < w; j++) {
                Vector3f ray_start = new Vector3f(j*mars_settings.getTerrain_scale().x+(mars_settings.getTerrain_scale().x/2f), 0f, i*mars_settings.getTerrain_scale().x+(mars_settings.getTerrain_scale().x/2f));
                float flowX = pixelSamplesFlowX[i*(h)+j];
                float flowY = pixelSamplesFlowY[i*(h)+j];
                Vector3f ray_direction = new Vector3f(flowX, 0f, flowY);
                ray_direction.normalizeLocal();
                ray_direction.multLocal(mars_settings.getTerrain_scale().x/2f);
                Arrow arrow = new Arrow(ray_direction);
                arrow.setLineWidth(4f);
                Geometry ArrowGeom = new Geometry("VectorVisualizer_Arrow", arrow);
                Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                Vector3f color = new Vector3f(flowX, 0f, flowY);
                Vector3f colorMax = new Vector3f(32768, 0f, 32768);
                float lengthSquaredMax = colorMax.lengthSquared();
                float lengthSquared = color.lengthSquared();
                ColorRGBA col = new ColorRGBA(lengthSquared/lengthSquaredMax, 1f-(lengthSquared/lengthSquaredMax), 0f, 0f);
                mark_mat4.setColor("Color", col);
                ArrowGeom.setMaterial(mark_mat4);
                ArrowGeom.setLocalTranslation(ray_start);
                ArrowGeom.updateGeometricState();
                flowNode.attachChild(ArrowGeom);
            }
        }*/

        
        //flowNode.attachChild(grid);
        rootNode.attachChild(flowNode);

    }
    
    /**
     * 
     * @param flipX
     * @param flipY
     * @param colorImage
     * @return
     */
    public int[] load(boolean flipX, boolean flipY, Image colorImage) {

        int imageWidth = colorImage.getWidth();
        int imageHeight = colorImage.getHeight();

        if (imageWidth != imageHeight)
                throw new RuntimeException("imageWidth: " + imageWidth
                        + " != imageHeight: " + imageHeight);

        ByteBuffer buf = colorImage.getData(0);

        int[] heightData = new int[(imageWidth * imageHeight)];
        
        int index = 0;
        if (flipY) {
            for (int h = 0; h < imageHeight; ++h) {
                if (flipX) {
                    for (int w = imageWidth - 1; w >= 0; --w) {
                        int baseIndex = (h * imageWidth)+ w;
                        heightData[index++] = getHeightAtPostion(buf, colorImage, baseIndex);
                    }
                } else {
                    for (int w = 0; w < imageWidth; ++w) {
                        int baseIndex = (h * imageWidth)+ w;
                        heightData[index++] = getHeightAtPostion(buf, colorImage, baseIndex);
                    }
                }
            }
        } else {
            for (int h = imageHeight - 1; h >= 0; --h) {
                if (flipX) {
                    for (int w = imageWidth - 1; w >= 0; --w) {
                        int baseIndex = (h * imageWidth)+ w;
                        heightData[index++] = getHeightAtPostion(buf, colorImage, baseIndex);
                    }
                } else {
                    for (int w = 0; w < imageWidth; ++w) {
                        int baseIndex = (h * imageWidth)+ w;
                        heightData[index++] = getHeightAtPostion(buf, colorImage, baseIndex);
                    }
                }
            }
        }

        return heightData;
    }
    
    /**
     * 
     * @param buf
     * @param image
     * @param position
     * @return
     */
    protected int getHeightAtPostion(ByteBuffer buf, Image image, int position) {
        switch (image.getFormat()){
            case Luminance16:
                ShortBuffer sbuf = buf.asShortBuffer();
                sbuf.position( position );
                return (sbuf.get() & 0xFFFF)-32768;
            default:
                throw new UnsupportedOperationException("Image format: "+image.getFormat());
        }
    }
    
    /**
     * 
     */
    public void updateGrass(){
        Future fut = mars.enqueue(new Callable() {
                    public Void call() throws Exception {
                        if(grassLoader != null){
                            grassLoader.setFarViewingDistance(mars_settings.getGrassFarViewingDistance());
                        }
                        return null;
                    }
        });
    }

    private void setupShadow(){
        dlsr = new DirectionalLightShadowRenderer(assetManager, 1024, 3);
        dlsr.setLight(sun);
        dlsr.setLambda(0.55f);
        dlsr.setShadowIntensity(0.6f);    
        dlsr.setShadowCompareMode(CompareMode.Software);
        dlsr.setEdgeFilteringMode(EdgeFilteringMode.PCF4);
        //dlsr.displayFrustum();
        viewPort.addProcessor(dlsr);

        dlsf = new DirectionalLightShadowFilter(assetManager, 1024, 3);
        dlsf.setLight(sun);
        dlsf.setLambda(0.55f);
        dlsf.setShadowIntensity(0.6f);    
        dlsf.setShadowCompareMode(CompareMode.Software);
        dlsf.setEdgeFilteringMode(EdgeFilteringMode.PCF4);
        dlsf.setEnabled(false);

        fppS = new FilterPostProcessor(assetManager);
        fppS.addFilter(dlsf);

        viewPort.addProcessor(fppS);
    }
    /**
     * 
     * @return
     */
    public RigidBodyControl getTerrain_physics_control() {
        return terrain_physics_control;
    }
    
    /**
     * 
     * @return
     */
    public int getTerrain_image_heigth() {
        return terrain_image_heigth;
    }

    /**
     * 
     * @return
     */
    public int getTerrain_image_width() {
        return terrain_image_width;
    }
    
    /**
     * 
     * @return
     */
    public byte[] getTerrainByteArray() {
        return terrain_byte_arrray;
    }
    
    /**
     * 
     * @return
     */
    public ChannelBuffer getTerrainChannelBuffer() {
        return terrainChannelBuffer;
    }
    
    /**
     * 
     * @return
     */
    public Node getTerrainNode(){
        return terrain_node;
    }
    
    /**
     * 
     * @return
     */
    public int[] getFlowX(){
        return pixelSamplesFlowX;
    }
    
    /**
     * 
     * @return
     */
    public int[] getFlowY(){
        return pixelSamplesFlowY;
    }
    
    /**
     * 
     * @return
     */
    public int getFlow_image_heigth() {
        return flow_image_heigth;
    }

    /**
     * 
     * @return
     */
    public int getFlow_image_width() {
        return flow_image_width;
    }
    
    /**
     * 
     * @return
     */
    public Vector3f getFlowVector(){
        return flowVector;
    }

    /**
     * 
     * @param flowVector
     */
    public void setFlowVector(Vector3f flowVector) {
        this.flowVector = flowVector;
    }
}
