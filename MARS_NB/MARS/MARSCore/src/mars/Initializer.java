/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars;

import java.nio.ShortBuffer;
import mars.states.SimState;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
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
import mars.auv.AUV_Manager;
import mars.server.MARS_Server;
import com.jme3.font.BitmapText;
import com.jme3.light.AmbientLight;
import com.jme3.post.Filter;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.post.filters.TranslucentBucketFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.debug.Grid;
import com.jme3.shadow.CompareMode;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.system.AppSettings;
import com.jme3.system.Timer;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture2D;
import forester.Forester;
import forester.grass.GrassLayer;
import forester.grass.GrassLayer.MeshType;
import forester.grass.GrassLoader;
import forester.grass.datagrids.MapGrid;
import forester.image.DensityMap.Channel;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import jme3utilities.TimeOfDay;
import jme3utilities.sky.SkyControl;
import mars.auv.CommunicationManager;
import mars.auv.CommunicationManagerRunnable;
import mars.filter.FishEyeFilter;
import mars.filter.LensFlareFilter;
import mars.server.MARSClient;
import mars.server.PhysicalExchangerPublisher;
import mars.server.ros.ROS_Node;
import mars.waves.MyProjectedGrid;
import mars.waves.ProjectedWaterProcessorWithRefraction;
import mars.waves.WaterHeightGenerator;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 * With this class we initialize all the different things in the begining like
 * "Do we want to load the terrrain?". Uses the MARS_Settings to determine what
 * ist activated with what mars_settings.
 *
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

    //pollution
    ImageBasedHeightMap pollutionmap;
    private Node pollutionNode;
    private int pollution_image_width = 0;
    private int pollution_image_heigth = 0;
    Geometry pollution_plane;

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

    //SkyDome
    SkyControl skyControl;
    TimeOfDay timeOfDay;

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
     * @param simstate
     * @param auv_manager
     * @param com_manager
     * @param physical_environment
     */
    public Initializer(MARS_Main mars, SimState simstate, AUV_Manager auv_manager, CommunicationManager com_manager, PhysicalEnvironment physical_environment) {
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
    public void init() {
        //load only on demand
        if (mars_settings.isFogEnabled()) {
            setupFog();
        }
        if (mars_settings.isTerrainEnabled() && !mars_settings.isTerrainAdvanced()) {
            setupTerrain();
        }
        if (mars_settings.isTerrainEnabled() && mars_settings.isTerrainAdvanced()) {
            setupTerrain();
        }
        if (mars_settings.isGrassEnabled()) {
            setupGrass();
        }
        if (mars_settings.isWaterEnabled()) {
            setupWater();
        }
        if (mars_settings.isWavesWaterEnabled()) {
            setupWavesWater();
        }
        if (mars_settings.isWireFrameEnabled()) {
            setupWireFrame();
        }
        if (mars_settings.isDepthOfFieldEnabled()) {
            setupDepthOfField();
        }
        if (mars_settings.isShadowEnabled()) {
            setupShadow();
        }
        if (mars_settings.isSimpleSkyBoxEnabled()) {
            setupSimpleSkyBox();
        }
        if (mars_settings.isSkyBoxEnabled()) {
            setupSkyBox();
        }
        if (mars_settings.isSkyDomeEnabled()) {
            setupSkyDome();
        }
        
        //always loaded
        setupAxis();
        setupGrid();
        setupLight();
        setupPlaneWater();
        setupProjectedWavesWater();
        setupCrossHairs();
        setupServer();
        setupAdvServer();
        setupPublisher();
        //setupGlow();
        //setupFishEye();
        //setupLensFlare();
        setupFlow();
        setupPollution();
        setupTranslucentBucketFilter();

        //add all the filters to the viewport(main window)
        viewPort.addProcessor(fpp);
    }

    /**
     *
     */
    public void cleanup() {
        if (fppS != null) {
            fppS.removeAllFilters();
            viewPort.removeProcessor(fppS);
            viewPort.removeProcessor(dlsr);
        }

        fpp.removeAllFilters();
        viewPort.removeProcessor(fpp);
        viewPort.removeProcessor(waterProcessor);

        if (wireProcessor != null) {
            viewPort.removeProcessor(wireProcessor);
        }

        //cleanupProjectedWavesWater();
    }

    /**
     *
     * @param NewViewPort
     */
    public void addFiltersToViewport(ViewPort NewViewPort) {
        FilterPostProcessor fppp = new FilterPostProcessor(assetManager);
        if (mars_settings.isFogEnabled()) {
            fppp.addFilter(createFog());
        }
        if (mars_settings.isDepthOfFieldEnabled()) {
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
                settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2,
                settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
        guiNode.attachChild(ch);
        hideCrossHairs(mars_settings.isCrossHairsEnabled());
    }

    /*
     * setting up the raw_server for communication with the auvs
     */
    /**
     *
     */
    public void setupServer() {
        if (mars_settings.getRAWEnabled()) {
            raw_server = new MARS_Server(mars, auv_manager, com_manager);
            raw_server.setServerPort(mars_settings.getRAWPort());
            raw_server_thread = new Thread(raw_server);
            raw_server_thread.start();
        }
        if (mars_settings.getROSEnabled()) {
            ros_server = new ROS_Node(mars, auv_manager, mars_settings);
            ros_server.setMaster_port(mars_settings.getROSMasterport());
            ros_server.setMaster_ip(mars_settings.getROSMasterip());
            ros_server.setLocal_ip(mars_settings.getROSLocalip());
            ros_server.init();
            ros_server_thread = new Thread(ros_server);
            ros_server_thread.start();

            com_server = new CommunicationManagerRunnable(com_manager);
            com_server_thread = new Thread(com_server);
            com_server_thread.start();
        }
    }

    /**
     *
     */
    public void setupAdvServer() {
        //we have to find new classes from modules/plugins(NBP) and add to them to the jaxbcontext so they can be marshalled
        Lookup bag = Lookup.getDefault();
        // the bag of objects
        // A query that looks up instances extending "MyClass"...
        Lookup.Template<MARSClient> pattern = new Lookup.Template(MARSClient.class);
        // The result of the query
        Lookup.Result<MARSClient> result = bag.lookup(pattern);
        Set<Class<? extends MARSClient>> allClasses = result.allClasses();
        //go trough all results and instance
        for (Class<? extends MARSClient> next : allClasses) {
            try {
                MARSClient marsClient = next.newInstance();
                marsClient.setAUVManager(auv_manager);
                auv_manager.addAdListener(marsClient);
                PhysicalExchangerPublisher puber = new PhysicalExchangerPublisher(mars, auv_manager, mars_settings);
                Thread puber_thread = new Thread(puber);
                puber_thread.start();
            } catch (InstantiationException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    /**
     *
     */
    public void setupPublisher() {

    }

    /**
     *
     */
    public void killServer() {
        Logger.getLogger(Initializer.class.getName()).log(Level.INFO, "Killing CommunicationManager Server...", "");
        if (this.getCom_server() != null) {
            this.getCom_server().setRunning(false);
            Logger.getLogger(Initializer.class.getName()).log(Level.INFO, "CommunicationManager Server killed!", "");
        } else {
            Logger.getLogger(Initializer.class.getName()).log(Level.INFO, "CommunicationManager Server not running. Cant be killed", "");
        }

        Logger.getLogger(Initializer.class.getName()).log(Level.INFO, "Killing ROS Server...", "");
        if (this.getROS_Server() != null) {
            this.getROS_Server().shutdown();
            Logger.getLogger(Initializer.class.getName()).log(Level.INFO, "ROS Server killed!", "");
        } else {
            Logger.getLogger(Initializer.class.getName()).log(Level.INFO, "ROS Server not running. Cant be killed", "");
        }
    }

    /**
     *
     * @return
     */
    public boolean ServerRunning() {
        if (this.isROS_ServerReady()) {
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
    public boolean checkROSServer() {
        Logger.getLogger(Initializer.class.getName()).log(Level.INFO, "Waiting for ROS Server to be ready...", "");
        while (!this.isROS_ServerReady()) {

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
     * @deprecated
     */
    @Deprecated
    public void setupROS_Server() {
        if (mars_settings.getROSEnabled()) {
            ros_server = new ROS_Node(mars, auv_manager, mars_settings);
            ros_server.setMaster_port(mars_settings.getROSMasterport());
            ros_server.setMaster_ip(mars_settings.getROSMasterip());
            ros_server.setLocal_ip(mars_settings.getROSLocalip());
            ros_server.init();
            ros_server_thread = new Thread(ros_server);
            ros_server_thread.start();
        }
    }

    /**
     *
     */
    public synchronized void start_ROS_Server() {
        if (ros_server_thread != null) {
            ros_server_thread.start();
        }
    }

    /**
     *
     * @return
     */
    public synchronized boolean isROS_ServerReady() {
        if (ros_server_thread != null) {
            return ros_server_thread.isAlive();
        } else {
            return false;
        }
    }

    /**
     *
     * @return
     */
    public synchronized ROS_Node getROS_Server() {
        return ros_server;
    }

    /**
     *
     * @return
     */
    public CommunicationManagerRunnable getCom_server() {
        return com_server;
    }

    /**
     *
     * @return
     */
    public Thread getCom_server_thread() {
        return com_server_thread;
    }

    /**
     *
     * @return
     */
    public synchronized MARS_Server getRAW_Server() {
        return raw_server;
    }

    /**
     *
     */
    public synchronized void testraw() {
        if (raw_server_thread != null) {
            raw_server.sendStringToAllConnections("test");
        }
    }

    private void setupWireFrame() {
        //we want to see wireframes on all objects
        new WireProcessor(assetManager, mars_settings.getWireFrameColor());
        viewPort.addProcessor(wireProcessor);
    }

    private DepthOfFieldFilter createDepthOfField() {
        DepthOfFieldFilter dofFilter = new DepthOfFieldFilter();
        dofFilter.setFocusDistance(0);
        dofFilter.setFocusRange(mars_settings.getDepthOfFieldFocusRange());
        dofFilter.setBlurScale(mars_settings.getDepthOfFieldBlurScale());
        return dofFilter;
    }

    /*
     * This creates a Depth of Field effect. Objects far away are blurred.
     */
    private void setupDepthOfField() {
        fpp.addFilter(createDepthOfField());
    }

    /*
     * This creates water with waves.
     */
    private void setupWavesWater() {
        water = new WaterFilter(rootNode, mars_settings.getLightDirection().normalizeLocal());
        water.setWaterHeight(water_height);
        water.setWaveScale(0.003f);
        water.setMaxAmplitude(0.3f);
        water.setFoamExistence(new Vector3f(0.45f, 3, 3.0f));
        water.setFoamIntensity(0.2f);
        water.setFoamHardness(0.5f);
        water.setFoamTexture((Texture2D) assetManager.loadTexture("Common/MatDefs/Water/Textures/foam.jpg"));
        water.setCausticsIntensity(0.25f);
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

        BloomFilter bloom = new BloomFilter();
        bloom.setExposurePower(55);
        bloom.setBloomIntensity(1.0f);
        fpp.addFilter(bloom);

        LightScatteringFilter lsf = new LightScatteringFilter(mars_settings.getLightDirection().mult(-300f));
        lsf.setLightDensity(1.0f);
        fpp.addFilter(lsf);
    }

    /**
     *
     */
    public void setupProjectedWavesWater() {
        setupgridwaves(mars.getCamera(), mars.getViewPort(), mars.getTimer());
    }

    private void setupgridwaves(Camera cam, ViewPort viewPort, Timer timer) {
        grid = new MyProjectedGrid(timer, cam, 100, 70, 0.02f, whg);
        updateProjectedWavesWater();
        projectedGridGeometry = new Geometry("Projected Grid", grid);  // create cube geometry from the shape
        //projectedGridGeometry.setCullHint(CullHint.Never);
        //projectedGridGeometry.setQueueBucket(Bucket.Translucent);
        projectedGridGeometry.setMaterial(setWaterProcessor(cam, viewPort));
        projectedGridGeometry.setLocalTranslation(0, 0, 0);
        rootNode.attachChild(projectedGridGeometry);
        hideProjectedWavesWater(mars_settings.isProjectedWavesWaterEnabled());
    }

    /**
     *
     */
    public void updateProjectedWavesWater() {
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

    private Material setWaterProcessor(Camera cam, ViewPort viewPort) {
        waterProcessor = new ProjectedWaterProcessorWithRefraction(cam, assetManager);
        waterProcessor.setReflectionScene(sceneReflectionNode);
        waterProcessor.setDebug(false);
        viewPort.addProcessor(waterProcessor);
        return waterProcessor.getMaterial();
    }

    private void cleanupProjectedWavesWater() {
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
    public float getCurrentWaterHeight(float x, float z) {
        if (mars_settings.isProjectedWavesWaterEnabled()) {
            return whg.getHeight(x, z, mars.getTimer().getTimeInSeconds());
        } else {
            return physical_environment.getWater_height();
        }
    }

    /**
     *
     * @param tpf
     */
    public void updateProjectedWavesWater(float tpf) {
        float[] angles = new float[3];
        mars.getCamera().getRotation().toAngles(angles);
        grid.update(mars.getCamera().getViewMatrix().clone());
    }

    /**
     *
     * @param tpf
     */
    public void updateWavesWater(float tpf) {
        waves_time += tpf;
        float waterHeight = (float) Math.cos(((waves_time * 0.6f) % FastMath.TWO_PI)) * 1.5f;
        water.setWaterHeight(water_height + waterHeight);
    }

    /*
     * This creates shader water. I never got the params right and it always look so unrealistic like in morrorwind. Fog also was never implemented.
     * Onyl works form one side etc....
     */
    @Deprecated
    private void setupWater() {
        SimpleWaterProcessor waterProcessor = new SimpleWaterProcessor(assetManager);
        waterProcessor.setReflectionScene(sceneReflectionNode);
        waterProcessor.setDebug(false);
        waterProcessor.setLightPosition(mars_settings.getLightDirection().mult(-400f));

        //setting the water plane
        Vector3f waterLocation = new Vector3f(0, water_height, 0);
        waterProcessor.setPlane(new Plane(Vector3f.UNIT_Y, waterLocation.dot(Vector3f.UNIT_Y)));
        waterProcessor.setWaterColor(ColorRGBA.Blue);
        //lower render size for higher performance
//        waterProcessor.setRenderSize(128,128);
        //raise depth to see through water
        waterProcessor.setWaterDepth(0.5f);
        //lower the distortion scale if the waves appear too strong
        //waterProcessor.setDistortionScale(0.1f);
        //lower the speed of the waves if they are too fast
        waterProcessor.setWaveSpeed(0.01f);
        waterProcessor.setWaterTransparency(0.05f);
        waterProcessor.setRefractionClippingOffset(2.0f);
        waterProcessor.setReflectionClippingOffset(2.0f);

        Quad quad = new Quad(1000, 1000);

        //the texture coordinates define the general size of the waves
        quad.scaleTextureCoordinates(new Vector2f(6f, 6f));

        Geometry water_geom = new Geometry("water", quad);
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
    public void setupPlaneWater() {
        // A translucent/transparent texture, similar to a window frame.
        /*Box boxshape = new Box(new Vector3f(0f,0f,0f), 1000f,0.01f,1000f);
         water_plane = new Geometry("water_plane", boxshape);
         water_plane.setLocalTranslation(0.0f, water_height, 5.0f);
         Material mat_tt = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         assetManager.registerLocator("Assets/Textures/Water", FileLocator.class);
         mat_tt.setTexture("ColorMap", assetManager.loadTexture(mars_settings.getPlanewaterFilepath()));
         mat_tt.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
         water_plane.setMaterial(mat_tt);
         water_plane.setQueueBucket(Bucket.Transparent);
         rootNode.attachChild(water_plane);*/
        //hidePlaneWater(mars_settings.isPlaneWaterEnabled());

        Future fut = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if (water_plane != null) {
                    water_plane.removeFromParent();
                }
                Box boxshape = new Box(new Vector3f(0f, 0f, 0f), 2000f, 0.01f, 2000f);
                water_plane = new Geometry("water_plane", boxshape);
                water_plane.setLocalTranslation(0.0f, water_height, 5.0f);
                Material mat_tt = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                //assetManager.registerLocator("Assets/Textures/Water", FileLocator.class);
                mat_tt.setTexture("ColorMap", assetManager.loadTexture(mars_settings.getPlanewaterFilepath()));
                mat_tt.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
                water_plane.setMaterial(mat_tt);
                water_plane.setQueueBucket(Bucket.Transparent);
                rootNode.attachChild(water_plane);
                hidePlaneWater(mars_settings.isPlaneWaterEnabled());
                return null;
            }
        });
    }

    private FogFilter createFog() {
        FogFilter fog = new FogFilter();
        fog.setFogColor(mars_settings.getFogColor());
        fog.setFogDistance(mars_settings.getDepthOfFieldDistance());
        fog.setFogDensity(mars_settings.getDepthOfFieldDensity());
        return fog;
    }
    /*
     * This is only rudimental fog. This will be deprecated in the futrue in jme3. For Water there will be an own fog system.
     */

    private void setupFog() {
        fpp.addFilter(createFog());
    }

    private void setupGlow() {
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        fpp.addFilter(bloom);
        //mars.getViewPort().addProcessor(fpp);
    }

    private void setupFishEye() {
        FishEyeFilter fisheye = new FishEyeFilter();
        fpp.addFilter(fisheye);
    }

    private void setupTranslucentBucketFilter() {
        TranslucentBucketFilter tbf = new TranslucentBucketFilter(true);
        fpp.addFilter(tbf);
    }

    private void setupLensFlare() {
        //LensFlareFilter lf = new LensFlareFilter("Textures/lensdirt.png"); // or null if you don't own a +1 Lens Cloth of Smiting
        LensFlareFilter lf = new LensFlareFilter(null); // or null if you don't own a +1 Lens Cloth of Smiting
        lf.setGhostSpacing(0.125f);
        lf.setHaloDistance(0.48f);
        fpp.addFilter(lf);
    }

    /**
     *
     * @param filter
     */
    public void addFilter(Filter filter) {
        fpp.addFilter(filter);
    }

    /**
     *
     */
    public void setupLight() {
        final Node rootNodeMars = mars.getRootNode();
        Future fut = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                rootNodeMars.removeLight(sun);//remove all old stuff before
                rootNodeMars.removeLight(ambLight);
                sun.setColor(mars_settings.getLightColor());
                //sun.setDirection(mars_settings.getLightDirection().normalize());
                ambLight.setColor(mars_settings.getLightAmbientColor());
                if (mars_settings.isLightEnabled()) {
                    rootNodeMars.addLight(sun);
                } else {
                    rootNodeMars.removeLight(sun);
                }
                if (mars_settings.getLightAmbient()) {
                    rootNodeMars.addLight(ambLight);
                } else {
                    rootNodeMars.removeLight(ambLight);
                }
                return null;
            }
        });
        /* AmbientLight amb = new AmbientLight();
         amb.setColor(mars_settings.getLightColor().multLocal(0.1f));
         rootNode.addLight(amb);*/
    }

    /*
     * A simple sky. Makes the background color of the viewport not black ;).
     */
    private void setupSimpleSkyBox() {
        renderManager.getMainView("Default").setBackgroundColor(mars_settings.getSimpleskyColor());
    }

    /*
     * This creates a sky.
     */
    private void setupSkyBox() {
        //assetManager.registerLocator("Assets/Textures/Sky", FileLocator.class);
        Spatial sky = (SkyFactory.createSky(assetManager, mars_settings.getSkyboxFilepath(), false));
        sky.setLocalScale(100);
        sceneReflectionNode.attachChild(sky);
    }

    /*
     * This creates a dynamic sky.
     */
    private void setupSkyDome() {
        Future fut = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                /*
                 * Create a SkyControl to animate the sky.
                 */
                float cloudFlattening;
                boolean starMotion;
                boolean bottomDome;
                /*if (singleDome) {
                 cloudFlattening = 0f; // single dome implies clouds on hemisphere
                 starMotion = false; // single dome implies non-moving stars
                 bottomDome = false; // single dome implies exposed background
                 } else {
                 cloudFlattening = 0.9f; // clouds overhead are 10x closer
                 starMotion = true; // allow stars to move
                 bottomDome = true; // helpful in case scene has a low horizon
                 }*/

                cloudFlattening = 0.9f; // clouds overhead are 10x closer
                starMotion = true; // allow stars to move
                bottomDome = true; // helpful in case scene has a low horizon

                skyControl = new SkyControl(assetManager, mars.getCamera(), cloudFlattening, starMotion,
                        bottomDome);

                skyControl.getSunAndStars().setHour(mars_settings.getSkyDomeHour());
                skyControl.getSunAndStars().setObserverLatitude(mars_settings.getSkyDomeObserverLatitude());
                skyControl.getSunAndStars().setSolarLongitude(mars_settings.getSkyDomeSolarLongitude());

                skyControl.setCloudRate(mars_settings.getSkyDomeCloudRate());
                skyControl.setCloudiness(mars_settings.getSkyDomeCloudiness());
                skyControl.setCloudModulation(mars_settings.isSkyDomeCloudModulation());
                skyControl.setLunarDiameter(mars_settings.getSkyDomeLunarDiameter());
                skyControl.getUpdater().addViewPort(viewPort);
                skyControl.getUpdater().addShadowRenderer(dlsr);
                skyControl.getUpdater().addShadowFilter(dlsf);
                skyControl.getUpdater().setAmbientLight(ambLight);
                //skyControl.getUpdater().setAmbientMultiplier(2f);
                skyControl.getUpdater().setMainLight(sun);
                skyControl.getUpdater().setMainMultiplier(5f);
                skyControl.getUpdater().setShadowFiltersEnabled(true);

                //add bloom filter for a better sun
                BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
                bloom.setBlurScale(2.5f);
                bloom.setExposurePower(1f);
                fpp.addFilter(bloom);
                skyControl.getUpdater().addBloomFilter(bloom);

                /*
                 * Add SkyControl to the scene and enable it.
                 */
                final Node rootNodeMars = mars.getRootNode();
                rootNodeMars.addControl(skyControl);
                skyControl.setEnabled(true);

                timeOfDay = new TimeOfDay(mars_settings.getSkyDomeHour());
                mars.getStateManager().attach(timeOfDay);
                timeOfDay.setRate(mars_settings.getSkyDomeSpeed() * mars_settings.getSkyDomeDirection());

                return null;
            }
        });
    }

    /*
     * give us some orientation
     */
    private void setupAxis() {
        Geometry y_axis = new Geometry("y_axis", new Arrow(Vector3f.UNIT_Y.mult(1)));
        Material y_axis_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        y_axis_mat.setColor("Color", ColorRGBA.Green);
        y_axis.setMaterial(y_axis_mat);
        y_axis.setLocalTranslation(new Vector3f(0f, 0f, 0f));
        y_axis.updateGeometricState();
        axisNode.attachChild(y_axis);

        Geometry x_axis = new Geometry("x_axis!", new Arrow(Vector3f.UNIT_X.mult(1)));
        Material x_axis_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        x_axis_mat.setColor("Color", ColorRGBA.Blue);
        x_axis.setMaterial(x_axis_mat);
        x_axis.setLocalTranslation(new Vector3f(0f, 0f, 0f));
        x_axis.updateGeometricState();
        axisNode.attachChild(x_axis);

        Geometry z_axis = new Geometry("z_axis", new Arrow(Vector3f.UNIT_Z.mult(1)));
        Material z_axis_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        z_axis_mat.setColor("Color", ColorRGBA.Red);
        z_axis.setMaterial(z_axis_mat);
        z_axis.setLocalTranslation(new Vector3f(0f, 0f, 0f));
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
        hideAxis(mars_settings.isAxisEnabled());
    }

    /**
     *
     */
    public void setupGrid() {
        Future fut = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                gridNode.detachAllChildren();
                Geometry grid = new Geometry("wireframe grid", new Grid(mars_settings.getGridSizeX(), mars_settings.getGridSizeY(), mars_settings.getGridLineDistance()));
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
                hideGrid(mars_settings.isGridEnabled());
                return null;
            }
        });
    }

    /**
     *
     * @param hide
     */
    public void hideAxis(boolean hide) {
        if (!hide) {
            axisNode.setCullHint(CullHint.Always);
        } else {
            axisNode.setCullHint(CullHint.Never);
        }
    }

    /**
     *
     * @param hide
     */
    public void hideGrid(boolean hide) {
        if (!hide) {
            gridNode.setCullHint(CullHint.Always);
        } else {
            gridNode.setCullHint(CullHint.Never);
        }
    }

    /**
     *
     * @param hide
     */
    public void showPhysicsDebug(boolean hide) {
        if (mars.getStateManager().getState(BulletAppState.class) != null) {
            mars.getStateManager().getState(BulletAppState.class).setDebugEnabled(hide);
        }
    }

    /**
     *
     * @param hide
     */
    public void hidePlaneWater(boolean hide) {
        if (!hide) {
            water_plane.setCullHint(CullHint.Always);
        } else {
            water_plane.setCullHint(CullHint.Never);
        }
    }

    /**
     *
     * @param hide
     */
    public void hidePollution(boolean hide) {
        if (!hide) {
            pollution_plane.setCullHint(CullHint.Always);
        } else {
            pollution_plane.setCullHint(CullHint.Never);
        }
    }

    /**
     *
     * @param hide
     */
    public void hideProjectedWavesWater(boolean hide) {
        if (!hide) {
            projectedGridGeometry.setCullHint(CullHint.Always);
        } else {
            projectedGridGeometry.setCullHint(CullHint.Never);
        }
    }

    /**
     *
     * @param hide
     */
    public void hideCrossHairs(boolean hide) {
        if (!hide) {
            ch.setCullHint(CullHint.Always);
        } else {
            ch.setCullHint(CullHint.Never);
        }
    }

    /**
     *
     * @param hide
     */
    public void hideFPS(boolean hide) {
        if (!hide) {
            mars.setDisplayFps(false);
            mars.setDisplayStatView(false);
        } else {
            mars.setDisplayFps(true);
            mars.setDisplayStatView(true);
        }
    }

    /**
     *
     * @param framelimit
     */
    public void changeFrameLimit(int framelimit) {
        mars.getSettings().setFrameRate(framelimit);
        mars.restart();
    }

    /**
     *
     * @param speed
     */
    public void changeSpeed(float speed) {
        mars.setSpeed(speed);
    }

    /**
     *
     */
    public void changePlaneWater() {

    }

    /**
     *
     * @param tpf
     */
    public void updateGrass(float tpf) {
        forester.update(tpf);
    }

    private void setupGrass() {
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
        //terrainMat.setTexture("AlphaMap", assetManager.loadTexture(mars_settings.getTerrainAlphaMap()));
        //Vector4f texScales = new Vector4f();
        // GRASS texture
        //Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
        //Texture grass = assetManager.loadTexture(mars_settings.getTerrainColorMap());
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
        forester.initialize(rootNode, mars.getCamera(), terrain, null, mars);

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
        GrassLayer layer = grassLoader.addLayer(grassMat, MeshType.CROSSQUADS);
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
        GrassLayer layer2 = grassLoader.addLayer(grassMat2, MeshType.BILLBOARDS);

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
        GrassLayer layer3 = grassLoader.addLayer(grassMat3, MeshType.CROSSQUADS);

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
        grassLoader.setWind(new Vector2f(mars_settings.getPhysical_environment().getWater_current().getX(), mars_settings.getPhysical_environment().getWater_current().getZ()));
    }

    private void setupTerrain() {
        /**
         * 1. Create terrain material and load four textures into it.
         */
        /*mat_terrain = new Material(assetManager, 
         "Common/MatDefs/Terrain/Terrain.j3md");*/
        mat_terrain = new Material(assetManager,
                "Common/MatDefs/Terrain/TerrainLighting.j3md");
        mat_terrain.setBoolean("useTriPlanarMapping", false);
        //mat_terrain.setFloat("Shininess", 0.5f);

        /**
         * 1.1) Add ALPHA map (for red-blue-green coded splat textures)
         */
        /*mat_terrain.setTexture("Alpha", assetManager.loadTexture(
         "Textures/Terrain/splat/alphamap.png"));*/
        //assetManager.registerLocator("Assets/Textures/Terrain", FileLocator.class);
        //assetManager.registerLocator("Assets/Forester", FileLocator.class);
        Texture alphaMapImage = assetManager.loadTexture(
                mars_settings.getTerrainAlphaMap());
        //alphaMapImage.getImage().setFormat(Format.RGBA8);
        //mat_terrain.setTexture("Alpha", alphaMapImage);
        mat_terrain.setTexture("AlphaMap", alphaMapImage);

        /**
         * 1.2) Add GRASS texture into the red layer (Tex1).
         */
        /*Texture grass = assetManager.loadTexture(
         "Textures/Terrain/splat/grass.jpg");*/
        Texture grass = assetManager.loadTexture(
                mars_settings.getTerrainColorMap());
        /* assetManager.registerLocator("Assets/Forester", FileLocator.class);
         Texture grass = assetManager.loadTexture("Textures/Sea/seamless_beach_sand.jpg");
         grass.setWrap(WrapMode.Repeat);
         //mat_terrain.setTexture("Tex1", grass);
         //mat_terrain.setFloat("Tex1Scale", 1f);*/
        mat_terrain.setTexture("DiffuseMap", grass);
        mat_terrain.setFloat("DiffuseMap_0_scale", 1f);

        /**
         * 1.3) Add DIRT texture into the green layer (Tex2)
         */
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
        /**
         * 1.4) Add ROAD texture into the blue layer (Tex3)
         */
        /*assetManager.registerLocator("Assets/Forester", FileLocator.class);
         Texture rock = assetManager.loadTexture(
         "Textures/Terrain/splat/grass.jpg");
         rock.setWrap(WrapMode.Repeat);
         mat_terrain.setTexture("Tex3", rock);
         mat_terrain.setFloat("Tex3Scale", 64f);*/
        /**
         * 2. Create the height map
         */
        /*Texture heightMapImage = assetManager.loadTexture(
         "Textures/Terrain/splat/mountains512.png");*/
        Texture heightMapImage = assetManager.loadTexture(
                mars_settings.getTerrainHeightMap());
        //heightMapImage.getImage().setFormat(Format.RGB8);//fix for format problems
        heightmap = new ImageBasedHeightMap(heightMapImage.getImage());
        terrain_image_heigth = heightMapImage.getImage().getHeight();
        terrain_image_width = heightMapImage.getImage().getWidth();
        heightmap.load();

        //convert terrain for ros
        terrain_byte_arrray = new byte[terrain_image_heigth * terrain_image_width];
        float[] heightMap = heightmap.getHeightMap();
        for (int i = 0; i < (terrain_image_heigth * terrain_image_width); i++) {
            terrain_byte_arrray[i] = (byte) Math.round(((heightMap[i] * 100f) / 255f));
        }
        terrainChannelBuffer = ChannelBuffers.copiedBuffer(ByteOrder.LITTLE_ENDIAN, terrain_byte_arrray);

        //random terrain generation
        /*HillHeightMap heightmap2 = null;
         try {
         heightmap2 = new HillHeightMap(256, 1000, 50, 100, (byte) 4);
         } catch (Exception ex) {
         ex.printStackTrace();
         }*/
        /**
         * 3. We have prepared material and heightmap. Now we create the actual
         * terrain: 3.1) Create a TerrainQuad and name it "my terrain". 3.2) A
         * good value for terrain tiles is 64x64 -- so we supply 64+1=65. 3.3)
         * We prepared a heightmap of size 512x512 -- so we supply 512+1=513.
         * 3.4) As LOD step scale we supply Vector3f(1,1,1). 3.5) We supply the
         * prepared heightmap itself.
         */
        int patchSize = mars_settings.getTerrainPatchSize() + 1;
        terrain = new TerrainQuad("advancedTerrain", patchSize, (heightmap.getSize()) + 1, heightmap.getHeightMap());

        /**
         * 4. We give the terrain its material, position & scale it, and attach
         * it.
         */
        terrain.setMaterial(mat_terrain);
        terrain.setLocalTranslation(mars_settings.getTerrainPosition());
        terrain.setLocalScale(mars_settings.getTerrainScale());
        float[] rots = new float[3];
        rots[0] = mars_settings.getTerrainRotation().getX();
        rots[1] = mars_settings.getTerrainRotation().getY();
        rots[2] = mars_settings.getTerrainRotation().getZ();
        Quaternion rot = new Quaternion(rots);
        terrain.setLocalRotation(rot);
        //rootNode.attachChild(terrain);

        /**
         * 5. The LOD (level of detail) depends on were the camera is:
         */
        TerrainLodControl control = new TerrainLodControl(terrain, mars.getCamera());
        control.setLodCalculator(new DistanceLodCalculator(65, 2.7f));
        terrain.addControl(control);
        control.setEnabled(mars_settings.getTerrainLod());

        terrain_node = new Node("terrain");
        /*terrain_node.setLocalTranslation(mars_settings.getTerrainPosition());
         float[] rots = new float[3];
         rots[0] = mars_settings.getTerrainRotation().getX();
         rots[1] = mars_settings.getTerrainRotation().getY();
         rots[2] = mars_settings.getTerrainRotation().getZ();
         Quaternion rot = new Quaternion(rots);
         terrain_node.setLocalRotation(rot);
         terrain_node.setLocalScale(mars_settings.getTerrainScale());
         terrain_node.updateGeometricState();*/

        /**
         * 6. Add physics:
         */
        // We set up collision detection for the scene by creating a
        // compound collision shape and a static RigidBodyControl with mass zero.*/
        //Making a terrain Physics
        //terrain_node = new Node("terrain");
        CollisionShape terrainShape = CollisionShapeFactory.createMeshShape(terrain);

        //terrain_node = new Node("terrain");
        terrain_physics_control = new RigidBodyControl(terrainShape, 0);
        //terrain_physics_control = new RigidBodyControl(0);

        /*Material debug_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         debug_mat.setColor("Color", ColorRGBA.Red);
         Spatial createDebugShape = terrain_physics_control.createDebugShape(assetManager);
         createDebugShape.setMaterial(debug_mat);
         terrain_node.attachChild(createDebugShape);*/
        terrain_physics_control.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_01);
        terrain_physics_control.setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_02);
        terrain_physics_control.addCollideWithGroup(PhysicsCollisionObject.COLLISION_GROUP_03);
        terrain_physics_control.addCollideWithGroup(PhysicsCollisionObject.COLLISION_GROUP_04);
        //terrain_physics_control.setFriction(0f);
        //terrain_physics_control.setRestitution(1f);
        //terrain_node.attachChild(terrain);
        terrain_physics_control.setEnabled(true);
        terrain.addControl(terrain_physics_control);

        //set shadwos for terrain
        terrain.setShadowMode(ShadowMode.Receive);

        terrain_node.attachChild(terrain);
        //SonarDetectableNode.attachChild(terrain_node);
        sceneReflectionNode.attachChild(terrain_node);
        RayDetectable.attachChild(terrain_node);
        bulletAppState.getPhysicsSpace().add(terrain);
        //bulletAppState.getPhysicsSpace().add(terrain_physics_control);
    }

    /**
     *
     */
    public void updateTerrain() {
        Future fut = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if (terrain_node != null) {
                    terrain_node.setLocalTranslation(mars_settings.getTerrainPosition());
                    float[] rots = new float[3];
                    rots[0] = mars_settings.getTerrainRotation().getX();
                    rots[1] = mars_settings.getTerrainRotation().getY();
                    rots[2] = mars_settings.getTerrainRotation().getZ();
                    Quaternion rot = new Quaternion(rots);
                    terrain_node.setLocalRotation(rot);
                    terrain_node.setLocalScale(mars_settings.getTerrainScale());
                            //terrain_physics_control.setPhysicsLocation(mars_settings.getTerrainPosition());
                    //terrain_physics_control.setPhysicsRotation(rot);
                }
                return null;
            }
        });
    }

    private void setupFlow() {
        //assetManager.registerLocator("Assets/Textures/Flow", FileLocator.class);

        Texture heightMapImage = assetManager.loadTexture(
                mars_settings.getFlowMapX());
        heightMapImage.getImage().setFormat(Format.Luminance16);//fix for format problems

        int w = heightMapImage.getImage().getWidth();
        int h = heightMapImage.getImage().getHeight();
        flow_image_heigth = h;
        flow_image_width = w;
        pixelSamplesFlowX = new int[h * w];

        pixelSamplesFlowX = load(false, false, heightMapImage.getImage());

        Texture heightMapImage2 = assetManager.loadTexture(
                mars_settings.getFlowMapY());
        heightMapImage2.getImage().setFormat(Format.Luminance16);//fix for format problems

        int w2 = heightMapImage2.getImage().getWidth();
        int h2 = heightMapImage2.getImage().getHeight();
        pixelSamplesFlowY = new int[h2 * w2];

        pixelSamplesFlowY = load(false, false, heightMapImage2.getImage());

        flowNode = new Node("flow");

        //flowNode.setLocalTranslation(mars_settings.getTerrainPosition());
        flowNode.updateGeometricState();

        Geometry grid = new Geometry("flow grid", new Grid(h, w, mars_settings.getTerrainScale().x));
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
         Vector3f ray_start = new Vector3f(j*mars_settings.getTerrainScale().x+(mars_settings.getTerrainScale().x/2f), 0f, i*mars_settings.getTerrainScale().x+(mars_settings.getTerrainScale().x/2f));
         float flowX = pixelSamplesFlowX[i*(h)+j];
         float flowY = pixelSamplesFlowY[i*(h)+j];
         Vector3f ray_direction = new Vector3f(flowX, 0f, flowY);
         ray_direction.normalizeLocal();
         ray_direction.multLocal(mars_settings.getTerrainScale().x/2f);
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

    private void setupPollution() {

        /**
         * 2. Create the height map
         */
        Texture pollutionMapImage = assetManager.loadTexture(
                mars_settings.getPollutionPollutionMap());
        //heightMapImage.getImage().setFormat(Format.RGB8);//fix for format problems
        pollutionmap = new ImageBasedHeightMap(pollutionMapImage.getImage());
        int h = pollutionMapImage.getImage().getHeight();
        int w = pollutionMapImage.getImage().getWidth();
        pollution_image_heigth = h;
        pollution_image_width = w;
        pollutionmap.load();

        float[] heightMap = pollutionmap.getHeightMap();
        pollutionNode = new Node("pollution");

        //flowNode.setLocalTranslation(mars_settings.getTerrainPosition());
        pollutionNode.updateGeometricState();

        Geometry grid = new Geometry("pollution grid", new Grid(h, w, mars_settings.getTerrainScale().x));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", mars_settings.getGridColor());
        grid.setMaterial(mat);
        //grid.center().move(mars_settings.getGridPosition());
        //Quaternion quat = new Quaternion();
        //quat.fromAngles(mars_settings.getGridRotation().x, mars_settings.getGridRotation().y, mars_settings.getGridRotation().z);
        //grid.setLocalRotation(quat);
        grid.move(0f, -1f, 0f);

        //flowNode.attachChild(grid);
        rootNode.attachChild(pollutionNode);

        //let us see the pollution
        Future fut = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if (pollution_plane != null) {
                    pollution_plane.removeFromParent();
                }
                //Box boxshape = new Box(new Vector3f(0f,0f,0f), 10f,0.01f,10f);
                Quad quad = new Quad(mars_settings.getPollutionScale().x * pollution_image_heigth, mars_settings.getPollutionScale().z * pollution_image_width, false);
                pollution_plane = new Geometry("pollution_plane", quad);
                Quaternion quat = new Quaternion();
                quat.fromAngles(-FastMath.HALF_PI, 0f, 0f);
                pollution_plane.setLocalRotation(quat);
                pollution_plane.setLocalTranslation(-mars_settings.getPollutionScale().x * pollution_image_heigth / 2f + mars_settings.getPollutionPosition().x, water_height + 0.02f, mars_settings.getPollutionScale().z * pollution_image_width / 2f + mars_settings.getPollutionPosition().z);
                Material mat_tt = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat_tt.setColor("Color", new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
                mat_tt.setTexture("ColorMap", assetManager.loadTexture(mars_settings.getPollutionPollutionMap()));
                mat_tt.getAdditionalRenderState().setBlendMode(BlendMode.Modulate);
                mat_tt.getAdditionalRenderState().setDepthWrite(false);
                mat_tt.getAdditionalRenderState().setAlphaTest(true);
                pollution_plane.setMaterial(mat_tt);
                pollution_plane.setQueueBucket(Bucket.Transparent);
                rootNode.attachChild(pollution_plane);
                hidePollution(mars_settings.isPollutionVisible());
                return null;
            }
        });

    }

    /**
     *
     */
    public void updatePollution() {
        Future fut = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if (pollution_plane != null) {
                    pollution_plane.removeFromParent();
                }
                Material mat_tt = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mat_tt.setColor("Color", new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
                mat_tt.setTexture("ColorMap", assetManager.loadTexture(mars_settings.getPollutionPollutionMap()));
                mat_tt.getAdditionalRenderState().setBlendMode(BlendMode.Modulate);
                mat_tt.getAdditionalRenderState().setDepthWrite(false);
                mat_tt.getAdditionalRenderState().setAlphaTest(true);
                pollution_plane.setMaterial(mat_tt);
                pollution_plane.setQueueBucket(Bucket.Transparent);
                rootNode.attachChild(pollution_plane);
                hidePollution(mars_settings.isPollutionVisible());
                return null;
            }
        });
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

        if (imageWidth != imageHeight) {
            throw new RuntimeException("imageWidth: " + imageWidth
                    + " != imageHeight: " + imageHeight);
        }

        ByteBuffer buf = colorImage.getData(0);

        int[] heightData = new int[(imageWidth * imageHeight)];

        int index = 0;
        if (flipY) {
            for (int h = 0; h < imageHeight; ++h) {
                if (flipX) {
                    for (int w = imageWidth - 1; w >= 0; --w) {
                        int baseIndex = (h * imageWidth) + w;
                        heightData[index++] = getHeightAtPostion(buf, colorImage, baseIndex);
                    }
                } else {
                    for (int w = 0; w < imageWidth; ++w) {
                        int baseIndex = (h * imageWidth) + w;
                        heightData[index++] = getHeightAtPostion(buf, colorImage, baseIndex);
                    }
                }
            }
        } else {
            for (int h = imageHeight - 1; h >= 0; --h) {
                if (flipX) {
                    for (int w = imageWidth - 1; w >= 0; --w) {
                        int baseIndex = (h * imageWidth) + w;
                        heightData[index++] = getHeightAtPostion(buf, colorImage, baseIndex);
                    }
                } else {
                    for (int w = 0; w < imageWidth; ++w) {
                        int baseIndex = (h * imageWidth) + w;
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
        switch (image.getFormat()) {
            case Luminance16:
                ShortBuffer sbuf = buf.asShortBuffer();
                sbuf.position(position);
                return (sbuf.get() & 0xFFFF) - 32768;
            case Luminance8:
                ShortBuffer sbuf2 = buf.asShortBuffer();
                sbuf2.position(position);
                return (sbuf2.get() & 0xFF) - 128;
            default:
                throw new UnsupportedOperationException("Image format: " + image.getFormat());
        }
    }

    /**
     *
     */
    public void updateGrass() {
        Future fut = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                if (grassLoader != null) {
                    grassLoader.setFarViewingDistance(mars_settings.getGrassFarViewingDistance());
                }
                return null;
            }
        });
    }

    private void setupShadow() {
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
    public Node getTerrainNode() {
        return terrain_node;
    }

    /**
     *
     * @return
     */
    public int[] getFlowX() {
        return pixelSamplesFlowX;
    }

    /**
     *
     * @return
     */
    public int[] getFlowY() {
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
    public Vector3f getFlowVector() {
        return flowVector;
    }

    /**
     *
     * @return
     */
    public int getPollution_image_width() {
        return pollution_image_width;
    }

    /**
     *
     * @return
     */
    public int getPollution_image_heigth() {
        return pollution_image_heigth;
    }

    /**
     *
     * @return
     */
    public ImageBasedHeightMap getPollutionmap() {
        return pollutionmap;
    }

    /**
     *
     * @param position
     * @return
     */
    public float getPollution(Vector3f position) {
        if (!mars_settings.isPollutionDetectable()) {
            return 0f;
        } else {
            Vector3f flow_scale = mars_settings.getPollutionScale();

            Vector3f addedPollutionPos = mars_settings.getPollutionPosition().add(-((float) getPollution_image_width() * flow_scale.x) / 2f, 0f, -((float) getPollution_image_width() * flow_scale.z) / 2f);
            Vector3f relSensorPos = position.subtract(addedPollutionPos);

            if ((relSensorPos.x <= ((float) getPollution_image_width() * flow_scale.x)) && (relSensorPos.x >= 0) && (relSensorPos.z <= ((float) getPollution_image_width() * flow_scale.z)) && (relSensorPos.z >= 0)) {//in pollutionmap bounds

                int auv_pos_x = (int) (((float) getPollution_image_width() / ((float) getPollution_image_width() * flow_scale.x)) * relSensorPos.x);
                int auv_pos_y = (int) (((float) getPollution_image_width() / ((float) getPollution_image_width() * flow_scale.z)) * relSensorPos.z);

                int pollution = (int) getPollutionmap().getTrueHeightAtPoint(auv_pos_x, auv_pos_y);
                //System.out.println(auv_pos_x + "/" + auv_pos_y + " pollution: " + pollution);

                return pollution;
            } else {//out of pollutionmap bound. no pollution
                return 0f;
            }
        }
    }

    /**
     *
     * @param flowVector
     */
    public void setFlowVector(Vector3f flowVector) {
        this.flowVector = flowVector;
    }

    /**
     *
     * @return
     */
    public SkyControl getSkyControl() {
        return skyControl;
    }

    /**
     *
     * @return
     */
    public TimeOfDay getTimeOfDay() {
        return timeOfDay;
    }

    /**
     *
     * @param hour
     */
    public void resetTimeOfDay(final float hour) {
        Future fut = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                timeOfDay = new TimeOfDay(hour);
                return null;
            }
        });
    }

}
