package mars.water;

import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import java.util.concurrent.Callable;

/**
 * Test app for {@link WaterState}.
 * @author John Paul Jonte
 */
public class TestWaterEffects extends SimpleApplication {
    Node sceneNode;
    private final Vector3f lightDir = new Vector3f(1, -1, -2);
    
    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1280, 720);
        settings.setTitle("MARS Water Effects");
        
        TestWaterEffects app = new TestWaterEffects();
        app.setSettings(settings);
        app.setShowSettings(false);
        app.setDisplayStatView(false);
        app.setDisplayFps(false);
        app.start();
    }
    
    @Override
    public void simpleInitApp() {
        // set up camera
        cam.setFrustumPerspective(45.0f, (float) settings.getWidth() / (float) settings.getHeight(), 1f, 2000f);
        cam.setLocation(new Vector3f(-10, 2, 0));
        cam.update();
        flyCam.setMoveSpeed(10);
        
        CameraNode camNode = new CameraNode("Camera", cam);
        camNode.setControlDir(ControlDirection.SpatialToCamera);
        //camNode.setEnabled(false);
        
        MotionPath camPath = new MotionPath();
        camPath.addWayPoint(new Vector3f(-300, 20, -50));
        camPath.addWayPoint(new Vector3f(50, -5, 50));
        camPath.addWayPoint(new Vector3f(50, -5, -150));
        camPath.setCycle(true);
        camPath.setCurveTension(0.5f);
        
        MotionEvent camControl = new MotionEvent(camNode, camPath);
        camControl.setLoopMode(LoopMode.Loop);
        camControl.setLookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        camControl.setDirectionType(MotionEvent.Direction.LookAt);
        camControl.setCurrentWayPoint(0);
        camControl.setSpeed(.2f);
        camControl.play();
        
        rootNode.attachChild(camNode);
        
        // set up scene
        sceneNode = new Node("Scene");
        rootNode.attachChild(sceneNode);
        createTerrain(sceneNode);
        
        // add light
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(lightDir.normalizeLocal());
        sun.setColor(ColorRGBA.White);
        rootNode.addLight(sun);
        
        // construct sky
        Spatial sky = SkyFactory.createSky(assetManager, "Scenes/Beach/FullskiesSunset0068.dds", false);
        sky.setLocalScale(350);
        sceneNode.attachChild(sky);
        
        // add objects
        // box above water
        Box boxshape1 = new Box(5f, 5f, 5f);
        
        Texture tex_ml = assetManager.loadTexture("Interface/Logo/Monkey.jpg");
        tex_ml.setAnisotropicFilter(3);
        
        Material mat_stl = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        mat_stl.setTexture("DiffuseMap", tex_ml);
        
        Geometry cube = new Geometry("A Textured Box", boxshape1);
        cube.setLocalTranslation(0, 6, -20);
        cube.setMaterial(mat_stl);
        
        sceneNode.attachChild(cube);
        
        // box under water
        Box boxshape2 = new Box(10f, 3f, 10f);
        
        Geometry cube2 = new Geometry("A Textured Box", boxshape2);
        cube2.setLocalTranslation(0, -6, -10);
        cube2.setMaterial(mat_stl);
        
        sceneNode.attachChild(cube2);
        
        // 'boats'
        Box boatShape = new Box(5, 1, 5);
        Geometry boat1 = new Geometry("Boat", boatShape);
        boat1.setLocalTranslation(0, 0, 10);
        boat1.setMaterial(mat_stl);
        sceneNode.attachChild(boat1);
        
        MotionPath boatPath1 = new MotionPath();
        boatPath1.addWayPoint(new Vector3f(10, 0, 0));
        boatPath1.addWayPoint(new Vector3f(0, 0, 10));
        boatPath1.addWayPoint(new Vector3f(-10, 0, 0));
        boatPath1.addWayPoint(new Vector3f(0, 0, -10));
        boatPath1.setCycle(true);
        boatPath1.setCurveTension(1);
        
        MotionEvent boatControl1 = new MotionEvent(boat1, boatPath1);
        boatControl1.setLoopMode(LoopMode.Loop);
        boatControl1.setDirectionType(MotionEvent.Direction.PathAndRotation);
        boatControl1.setSpeed(.5f);
        boatControl1.play();
        
        Geometry boat2 = new Geometry("Boat", boatShape);
        boat2.setLocalTranslation(0, 0, 10);
        boat2.setMaterial(mat_stl);
        sceneNode.attachChild(boat2);
        
        MotionPath boatPath2 = new MotionPath();
        boatPath2.addWayPoint(new Vector3f(30, 0, 0));
        boatPath2.addWayPoint(new Vector3f(0, 0, 30));
        boatPath2.addWayPoint(new Vector3f(-30, 0, 0));
        boatPath2.addWayPoint(new Vector3f(0, 0, -30));
        boatPath2.setCycle(true);
        boatPath2.setCurveTension(1);
        
        MotionEvent boatControl2 = new MotionEvent(boat2, boatPath2);
        boatControl2.setLoopMode(LoopMode.Loop);
        boatControl2.setDirectionType(MotionEvent.Direction.PathAndRotation);
        boatControl2.setSpeed(.4f);
        boatControl2.play();
        
        Geometry boat3 = new Geometry("Boat", boatShape);
        boat3.setLocalTranslation(0, 0, 10);
        boat3.setMaterial(mat_stl);
        sceneNode.attachChild(boat3);
        
        MotionPath boatPath3 = new MotionPath();
        boatPath3.addWayPoint(new Vector3f(60, 0, 0));
        boatPath3.addWayPoint(new Vector3f(0, 0, 60));
        boatPath3.addWayPoint(new Vector3f(-60, 0, 0));
        boatPath3.addWayPoint(new Vector3f(0, 0, -60));
        boatPath3.setCycle(true);
        boatPath3.setCurveTension(1);
        
        MotionEvent boatControl3 = new MotionEvent(boat3, boatPath3);
        boatControl3.setLoopMode(LoopMode.Loop);
        boatControl3.setDirectionType(MotionEvent.Direction.PathAndRotation);
        boatControl3.setSpeed(.3f);
        boatControl3.play();
        
        Geometry boat4 = new Geometry("Boat", boatShape);
        boat4.setLocalTranslation(0, 0, 10);
        boat4.setMaterial(mat_stl);
        sceneNode.attachChild(boat4);
        
        MotionPath boatPath4 = new MotionPath();
        boatPath4.addWayPoint(new Vector3f(-100, 0, 100));
        boatPath4.addWayPoint(new Vector3f(-100, 0, -100));
        boatPath4.addWayPoint(new Vector3f(-200, 0, 0));
        boatPath4.setCycle(true);
        boatPath4.setCurveTension(1);
        
        MotionEvent boatControl4 = new MotionEvent(boat4, boatPath4);
        boatControl4.setLoopMode(LoopMode.Loop);
        boatControl4.setDirectionType(MotionEvent.Direction.PathAndRotation);
        boatControl4.setSpeed(.3f);
        boatControl4.play();
        
        Geometry boat5 = new Geometry("Boat", boatShape);
        boat5.setLocalTranslation(0, 0, 10);
        boat5.setMaterial(mat_stl);
        sceneNode.attachChild(boat5);
        
        MotionPath boatPath5 = new MotionPath();
        boatPath5.addWayPoint(new Vector3f(150, 0, 0));
        boatPath5.addWayPoint(new Vector3f(100, 0, -150));
        boatPath5.addWayPoint(new Vector3f(0, 0, -200));
        boatPath5.setCycle(true);
        boatPath5.setCurveTension(1);
        
        MotionEvent boatControl5 = new MotionEvent(boat5, boatPath5);
        boatControl5.setLoopMode(LoopMode.Loop);
        boatControl5.setDirectionType(MotionEvent.Direction.PathAndRotation);
        boatControl5.setSpeed(.3f);
        boatControl5.play();
        
        // assure test can load assets
        assetManager.registerLocator("Assets", FileLocator.class);
        
        // Add WaterState
        final WaterState waterState = new WaterState(sceneNode);
        stateManager.attach(waterState);
        waterState.track(boat1);
        waterState.track(boat2);
        waterState.track(boat3);
        waterState.track(boat4);
        waterState.track(boat5);
        
        // setup FPS tests
        FPSState FPSJME = new FPSState(5, 60, "JMEWater.txt", "jMonkeyEngine WaterFilter only");
        FPSJME.addSetup(new Callable() {
            @Override
            public Object call() {
                waterState.getWaterFilter().setEnabled(false);
                waterState.getJMEWaterFilter().setEnabled(true);
                waterState.getParticleFilter().setEnabled(false);
                waterState.getSedimentEmitter().setEnabled(false);
                
                return null;
            }
        });
        stateManager.attach(FPSJME);
        
        FPSState FPSNew = new FPSState(70, 60, "NewWater.txt", "WaterGridFilter only");
        FPSNew.addSetup(new Callable() {
            @Override
            public Object call() {
                waterState.getWaterFilter().setEnabled(true);
                waterState.getWaterFilter().setUseFoamTrails(false);
                waterState.getJMEWaterFilter().setEnabled(false);
                waterState.getParticleFilter().setEnabled(false);
                waterState.getSedimentEmitter().setEnabled(false);
                
                return null;
            }
        });
        stateManager.attach(FPSNew);
        
        FPSState FPSNewFoam = new FPSState(135, 60, "NewWaterWithFoam.txt", "WaterGridFilter with Foam Trails");
        FPSNewFoam.addSetup(new Callable() {
            @Override
            public Object call() {
                waterState.getWaterFilter().setEnabled(true);
                waterState.getWaterFilter().setUseFoamTrails(true);
                waterState.getJMEWaterFilter().setEnabled(false);
                waterState.getParticleFilter().setEnabled(false);
                waterState.getSedimentEmitter().setEnabled(false);
                
                return null;
            }
        });
        stateManager.attach(FPSNewFoam);
        
        FPSState FPSParticles = new FPSState(200, 60, "Particles.txt", "WaterParticleFilter only");
        FPSParticles.addSetup(new Callable() {
            @Override
            public Object call() {
                waterState.getWaterFilter().setEnabled(false);
                waterState.getJMEWaterFilter().setEnabled(false);
                waterState.getParticleFilter().setEnabled(true);
                waterState.getParticleFilter().setUnderwater(true);
                waterState.getSedimentEmitter().setEnabled(false);
                
                return null;
            }
        });
        stateManager.attach(FPSParticles);
        
        FPSState FPSSediment = new FPSState(265, 60, "Sediment.txt", "Sediment Emitter");
        FPSSediment.addSetup(new Callable() {
            @Override
            public Object call() {
                waterState.getWaterFilter().setEnabled(false);
                waterState.getJMEWaterFilter().setEnabled(false);
                waterState.getParticleFilter().setEnabled(false);
                
                waterState.getSedimentEmitter().setEnabled(true);
                waterState.getSedimentEmitter().addEmitter(new Vector3f(0, -10, -60));
                waterState.getSedimentEmitter().addEmitter(new Vector3f(0, -10, -30));
                waterState.getSedimentEmitter().addEmitter(new Vector3f(0, -10, 0));
                waterState.getSedimentEmitter().addEmitter(new Vector3f(0, -10, 30));
                waterState.getSedimentEmitter().addEmitter(new Vector3f(0, -10, 60));
                
                return null;
            }
        });
        stateManager.attach(FPSSediment);
        
        FPSState FPSAll = new FPSState(330, 60, "All.txt", "All Water Effects");
        FPSAll.addSetup(new Callable() {
            @Override
            public Object call() {
                waterState.getWaterFilter().setEnabled(true);
                waterState.getJMEWaterFilter().setEnabled(false);
                waterState.getParticleFilter().setEnabled(true);
                waterState.getSedimentEmitter().setEnabled(true);
                
                return null;
            }
        });
        stateManager.attach(FPSAll);
    }
    
    /**
     * Creates a mountainous terrain from a height map and adds it to the root node.
     * @param rootNode Node to add the terrain to
     */
    private void createTerrain(Node rootNode) {
        Texture heightMapImage = assetManager.loadTexture("Textures/Terrain/splat/mountains512.png");
        Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
        Texture dirt = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
        Texture rock = assetManager.loadTexture("Textures/Terrain/splat/road.jpg");
        Texture normalMap0 = assetManager.loadTexture("Textures/Terrain/splat/grass_normal.jpg");
        Texture normalMap1 = assetManager.loadTexture("Textures/Terrain/splat/dirt_normal.png");
        Texture normalMap2 = assetManager.loadTexture("Textures/Terrain/splat/road_normal.png");
        
        grass.setWrap(Texture.WrapMode.Repeat);
        dirt.setWrap(Texture.WrapMode.Repeat);
        rock.setWrap(Texture.WrapMode.Repeat);
        normalMap0.setWrap(Texture.WrapMode.Repeat);
        normalMap1.setWrap(Texture.WrapMode.Repeat);
        normalMap2.setWrap(Texture.WrapMode.Repeat);
        
        Material matRock = new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md");
        matRock.setBoolean("useTriPlanarMapping", false);
        matRock.setBoolean("WardIso", true);
        matRock.setTexture("AlphaMap", assetManager.loadTexture("Textures/Terrain/splat/alphamap.png"));
        matRock.setTexture("DiffuseMap", grass);
        matRock.setFloat("DiffuseMap_0_scale", 64);
        matRock.setTexture("DiffuseMap_1", dirt);
        matRock.setFloat("DiffuseMap_1_scale", 16);
        matRock.setTexture("DiffuseMap_2", rock);
        matRock.setFloat("DiffuseMap_2_scale", 128);
        matRock.setTexture("NormalMap", normalMap0);
        matRock.setTexture("NormalMap_1", normalMap2);
        matRock.setTexture("NormalMap_2", normalMap2);

        // create terrain from heightmap
        try {
            AbstractHeightMap heightmap = new ImageBasedHeightMap(heightMapImage.getImage(), 0.1f);
            
            if (heightmap.load()) {
                TerrainQuad terrain = new TerrainQuad("terrain", 65, 513, heightmap.getHeightMap());
                terrain.setMaterial(matRock);
                terrain.setLocalScale(new Vector3f(5, 5, 5));
                terrain.setLocalTranslation(new Vector3f(200, -50, 50));
                terrain.setLocked(false);
                terrain.setShadowMode(RenderQueue.ShadowMode.Receive);

                rootNode.attachChild(terrain);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
    }
}