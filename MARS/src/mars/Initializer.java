/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars;

import mars.states.SimState;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import mars.auv.AUV_Manager;
import mars.server.MARS_Server;
import mars.terrain.MultMesh;
import com.jme3.font.BitmapText;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.system.AppSettings;
import com.jme3.system.Timer;
import mars.auv.Communication_Manager;
import mars.server.ros.ROS_Node;
import mars.waves.MyProjectedGrid;
import mars.waves.ProjectedWaterProcessorWithRefraction;
import mars.waves.WaterHeightGenerator;

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
    private AppSettings settings;
    private InputManager inputManager;
    private Node sceneReflectionNode;
    private Node SonarDetectableNode;
    private AssetManager assetManager;
    private RenderManager renderManager;
    private ViewPort viewPort;
    private float water_height;
    private AUV_Manager auv_manager;
    private Communication_Manager com_manager;
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
    
    //water
    private WaterFilter water;
    private float waves_time = 0f;
    
    //projected waves water
    private MyProjectedGrid grid;
    private Geometry projectedGridGeometry;
    private ProjectedWaterProcessorWithRefraction waterProcessor;
    private WaterHeightGenerator whg = new WaterHeightGenerator();
    
    //Server
    private MARS_Server raw_server;
    private Thread raw_server_thread;
    private ROS_Node ros_server;
    private Thread ros_server_thread;

    /**
     *
     * @param mars
     * @param mars_settings
     * @param auv_manager 
     * @param com_manager 
     * @deprecated 
     */
    @Deprecated
    public Initializer(MARS_Main mars, MARS_Settings MARS_settings, AUV_Manager auv_manager, Communication_Manager com_manager){
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
     */
    public Initializer(MARS_Main mars, SimState simstate, AUV_Manager auv_manager, Communication_Manager com_manager, PhysicalEnvironment physical_environment){
        this.mars = mars;
        this.mars_settings = simstate.getMARSSettings();
        this.guiNode = mars.getGuiNode();
        this.settings = mars.getSettings();
        this.rootNode = simstate.getRootNode();
        this.inputManager = mars.getInputManager();
        this.assetManager = simstate.getAssetManager();
        this.physical_environment = physical_environment;
        this.sceneReflectionNode = simstate.getSceneReflectionNode();
        this.SonarDetectableNode = simstate.getSonarDetectableNode();
        this.viewPort = mars.getViewPort();
        this.water_height = mars_settings.getPhysical_environment().getWater_height();
        this.auv_manager = auv_manager;
        this.com_manager = com_manager;
        this.renderManager = mars.getRenderManager();
        this.bulletAppState = mars.getStateManager().getState(BulletAppState.class);
        this.mars_settings.setInit(this);
        fpp = new FilterPostProcessor(assetManager);
    }

    /**
     * Calls this method once after you have added the MARS_Settings.
     */
    public void init(){
        if(mars_settings.isSetupAxis()){
            setupAxis();
        }
        if(mars_settings.isSetupFog()){
            setupFog();
        }
        if(mars_settings.isSetupLight()){
            setupLight();
        }
        if(mars_settings.isSetupPlaneWater()){
            setupPlaneWater();
        }
        if(mars_settings.isSetupSimpleSkyBox()){
            setupSimpleSkyBox();
        }
        if(mars_settings.isSetupSkyBox()){
            setupSkyBox();
        }
        if(mars_settings.isSetupTerrain()){
            setupTerrain();
        }
        if(mars_settings.isSetupWater()){
            setupWater();
        }
        if(mars_settings.isSetupWavesWater()){
            setupWavesWater();
        }
        if(mars_settings.isSetupProjectedWavesWater()){
            setupProjectedWavesWater();
        }
        if(mars_settings.isSetupWireFrame()){
            setupWireFrame();
        }
        if(mars_settings.isSetupCrossHairs()){
            setupCrossHairs();
        }
        if(mars_settings.isSetupDepthOfField()){
            setupDepthOfField();
        }
        setupServer();
        setupGlow();
        //add all the filters to the viewport(main window)
        viewPort.addProcessor(fpp);
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
        BitmapText ch = new BitmapText(guiFont, false);
        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        ch.setText("+"); // crosshairs
        ch.setLocalTranslation( // center
        settings.getWidth()/2 - guiFont.getCharSet().getRenderedSize()/3*2,
        settings.getHeight()/2 + ch.getLineHeight()/2, 0);
        guiNode.attachChild(ch);
    }

    /*
     * setting up the raw_server for communication with the auvs
     */
    private void setupServer(){
        if(mars_settings.isRAW_Server_enabled()){
            raw_server = new MARS_Server( mars, auv_manager, com_manager );
            raw_server.setServerPort(mars_settings.getRAW_Server_port());
            raw_server_thread = new Thread( raw_server );
            raw_server_thread.start();
        }
        if(mars_settings.isROS_Server_enabled()){
            ros_server = new ROS_Node( mars, auv_manager );
            ros_server.setMaster_port(mars_settings.getROS_Server_port());
            ros_server.setMaster_ip(mars_settings.getROS_Master_IP());
            ros_server.setLocal_ip(mars_settings.getROS_Local_IP());
            ros_server_thread = new Thread( ros_server );
            ros_server_thread.start();
        }
    }
    
    /**
     * 
     */
    public void setupROS_Server(){
        if(mars_settings.isROS_Server_enabled()){
            ros_server = new ROS_Node( mars, auv_manager );
            ros_server.setMaster_port(mars_settings.getROS_Server_port());
            ros_server.setMaster_ip(mars_settings.getROS_Master_IP());
            ros_server.setLocal_ip(mars_settings.getROS_Local_IP());
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
        viewPort.addProcessor(new WireProcessor(assetManager,mars_settings.getWireframecolor()));
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
        fpp.addFilter(water);
    }
    
    private void setupProjectedWavesWater(){
        setupgridwaves(mars.getCamera(),mars.getViewPort(),mars.getTimer());
    }

    private void setupgridwaves(Camera cam,ViewPort viewPort,Timer timer){
        grid = new MyProjectedGrid(timer, cam, 100, 70, 0.02f, whg);
        projectedGridGeometry = new Geometry("Projected Grid", grid);  // create cube geometry from the shape
        projectedGridGeometry.setCullHint(CullHint.Never);
        projectedGridGeometry.setMaterial(setWaterProcessor(cam,viewPort));
        projectedGridGeometry.setLocalTranslation(0, 0, 0);
        rootNode.attachChild(projectedGridGeometry);
    }
    
    private Material setWaterProcessor(Camera cam, ViewPort viewPort){   
        waterProcessor = new ProjectedWaterProcessorWithRefraction(cam,assetManager);
        waterProcessor.setReflectionScene(sceneReflectionNode);
        waterProcessor.setDebug(false);
        viewPort.addProcessor(waterProcessor);              
        return waterProcessor.getMaterial();
    }

    public WaterHeightGenerator getWhg() {
        return whg;
    }
    
    public float getCurrentWaterHeight(float x, float z){
        if(mars_settings.isSetupProjectedWavesWater()){
            return whg.getHeight(x, z, mars.getTimer().getTimeInSeconds());
        }else{
            return physical_environment.getWater_height();
        }
    }

    
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

    private void setupPlaneWater(){
        // A translucent/transparent texture, similar to a window frame.
        Box boxshape = new Box(new Vector3f(0f,0f,0f), 1000f,0.01f,1000f);
        Geometry water_plane = new Geometry("water_plane", boxshape);
        water_plane.setLocalTranslation(0.0f, water_height, 5.0f);
        Material mat_tt = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        assetManager.registerLocator("Assets/Textures/Water", FileLocator.class);
        mat_tt.setTexture("ColorMap", assetManager.loadTexture(mars_settings.getPlanewaterfilepath()));
        mat_tt.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        water_plane.setMaterial(mat_tt);
        water_plane.setQueueBucket(Bucket.Transparent);
        rootNode.attachChild(water_plane);
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

    private void setupLight(){
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(mars_settings.getLight_color());
        sun.setDirection(mars_settings.getLight_direction().normalize());
        rootNode.addLight(sun);
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
        assetManager.registerLocator("Assets/Textures/Sky", FileLocator.class);
        Spatial sky = (SkyFactory.createSky(assetManager, mars_settings.getSkyboxfilepath(), false));
        sky.setLocalScale(100);
        sceneReflectionNode.attachChild(sky);
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
         rootNode.attachChild(y_axis);

         Geometry x_axis = new Geometry("x_axis!", new Arrow(Vector3f.UNIT_X.mult(1)));
         Material x_axis_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         x_axis_mat.setColor("Color", ColorRGBA.Blue);
         x_axis.setMaterial(x_axis_mat);
         x_axis.setLocalTranslation(new Vector3f(0f,0f,0f));
         x_axis.updateGeometricState();
         rootNode.attachChild(x_axis);

         Geometry z_axis = new Geometry("z_axis", new Arrow(Vector3f.UNIT_Z.mult(1)));
         Material z_axis_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         z_axis_mat.setColor("Color", ColorRGBA.Red);
         z_axis.setMaterial(z_axis_mat);
         z_axis.setLocalTranslation(new Vector3f(0f,0f,0f));
         z_axis.updateGeometricState();
         rootNode.attachChild(z_axis);
        
         //Geometry length_axis = new Geometry("length_axis", new Arrow(Vector3f.UNIT_Z.mult(0.6f)));
         Geometry length_axis = new Geometry("length_axis", new Arrow(Vector3f.UNIT_Z.mult(0.615f)));
         Material length_axis_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         length_axis_mat.setColor("Color", ColorRGBA.Red);
         length_axis.setMaterial(length_axis_mat);
         //length_axis.setLocalTranslation(new Vector3f(0f,-2f,0f));
         length_axis.setLocalTranslation(new Vector3f(0f,0f,0f));
         length_axis.updateGeometricState();
         rootNode.attachChild(length_axis);
         /*Geometry length_axis = new Geometry("length_axis", new Arrow(Vector3f.UNIT_Z.mult(2.87f)));
         Material length_axis_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         length_axis_mat.setColor("Color", ColorRGBA.Red);
         length_axis.setMaterial(length_axis_mat);
         length_axis.setLocalTranslation(new Vector3f(0f,0f,-0.32f));
         //length_axis.setLocalTranslation(new Vector3f(0f,0f,-0.615f));
         length_axis.updateGeometricState();
         rootNode.attachChild(length_axis);*/
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
                vertex[i][j] = new Vector3f(j * mars_settings.getTileLength(), pixelSample[i][j] / mars_settings.getTileHeigth(), i * mars_settings.getTileLength());
            }
        }
        //pass the vectors to the MultMesh object
        MultMesh mm = new MultMesh(vertex);
        mm.initMesh();
        //setup material
        Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        assetManager.registerLocator("Assets/Textures/Terrain", FileLocator.class);
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
        SonarDetectableNode.attachChild(terrain_node);
        bulletAppState.getPhysicsSpace().add(terrain_node);
    }

    /**
     * 
     * @return
     */
    public RigidBodyControl getTerrain_physics_control() {
        return terrain_physics_control;
    }
    
    public int getTerrain_image_heigth() {
        return terrain_image_heigth;
    }

    public int getTerrain_image_width() {
        return terrain_image_width;
    }
    
    public byte[] getTerrainByteArray() {
        return terrain_byte_arrray;
    }
}
