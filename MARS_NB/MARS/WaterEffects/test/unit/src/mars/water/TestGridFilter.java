package mars.water;

import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
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

/**
 * Test app for {@link WaterState}.
 * @author John Paul Jonte
 */
public class TestGridFilter extends SimpleApplication {
    Node sceneNode;
    private final Vector3f lightDir = new Vector3f(1, -1, -2);
    private Geometry boat;
    private HeightGenerator heightGenerator;
    
    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1280, 720);
        settings.setTitle("MARS Water Effects");
        
        TestGridFilter app = new TestGridFilter();
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
        //flyCam.setEnabled(false);
        
        CameraNode camNode = new CameraNode("Camera", cam);
        camNode.setControlDir(ControlDirection.SpatialToCamera);
        //camNode.setEnabled(false);
        
        MotionPath camPath = new MotionPath();
        camPath.addWayPoint(new Vector3f(-200, 20, -50));
        camPath.addWayPoint(new Vector3f(50, -5, 50));
        camPath.addWayPoint(new Vector3f(50, -5, -150));
        camPath.setCycle(true);
        camPath.setCurveTension(0.5f);
        camPath.enableDebugShape(assetManager, rootNode);
        
        MotionEvent camControl = new MotionEvent(camNode, camPath);
        camControl.setLoopMode(LoopMode.Loop);
        camControl.setLookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        camControl.setDirectionType(MotionEvent.Direction.LookAt);
        //camControl.setInitialDuration(60);
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
        
        // 'boat'
        Box boatShape = new Box(5, 1, 5);
        boat = new Geometry("Boat", boatShape);
        boat.setLocalTranslation(0, 0, 10);
        boat.setMaterial(mat_stl);
        sceneNode.attachChild(boat);
        
        assetManager.registerLocator("Assets", FileLocator.class);
        
        // Add WaterState
        WaterState waterState = new WaterState(sceneNode);
        stateManager.attach(waterState);
        waterState.track(boat);
        heightGenerator = waterState.getHeightGenerator();
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
        
        WaterState.getInstance().getWaterFilter().setUnderWaterFogDistance(1000);
        
        // update 'boat' position
        // 'boat' travels along circle with an added sinusoidal component
        float radius = 37.5f + 2.5f * ((float) Math.sin(timer.getTimeInSeconds()));
        float boatSpeed = 0.2f;
        float angle = timer.getTimeInSeconds() * boatSpeed - .5f * (float) Math.PI;
        float x = (float) (radius * Math.sin(angle));
        float z = (float) (radius * Math.cos(angle));

        // set rotation and position
        boat.setLocalRotation(new Quaternion().fromAngleAxis(angle, Vector3f.UNIT_Y));
        boat.setLocalTranslation(x, heightGenerator.getHeight(x, z, timer.getTimeInSeconds()),  z);
    }
}