/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.FishSim.test;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.util.SkyFactory;
import com.jme3.water.WaterFilter;
import mars.FishSim.FishSim;

/**
 *
 * @author Acer
 * @deprecated 
 */
//@Deprecated
public class MARS_Main extends SimpleApplication {
    
 private Vector3f lightDir = new Vector3f(-4.9236743f, -1.27054665f, 5.896916f);
 private WaterFilter water;
 TerrainQuad terrain;
 Material matRock;
    /**
     *
     */
    protected RigidBodyControl land;
 BulletAppState bullet;
 private float time = 0.0f;
 private float waterHeight = 0.0f;
 private float initialWaterHeight = 120f;
  
 private FishSim fSim;
 
 
    /**
     *
     */
    @Override
    public void simpleInitApp() {
        fSim = new FishSim(this);
        stateManager.attach(fSim);
        
        bullet = new BulletAppState();
        stateManager.attach(bullet);
        bullet.setDebugEnabled(true);
              
        flyCam.setMoveSpeed(50);
        
        Node mainScene = new Node("Main Scene");
        rootNode.attachChild(mainScene);
        
        cam.setLocation(new Vector3f(-327.21957f, 61.6459f, 156.884346f));

        createTerrain(mainScene);
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape((Node) terrain);
        land = new RigidBodyControl(sceneShape, 0);
        land.setCollisionGroup(0);
        terrain.addControl(land);
        bullet.getPhysicsSpace().add(land);
       
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(lightDir);
        sun.setColor(ColorRGBA.White.clone().multLocal(1.7f));
        rootNode.addLight(sun);

        DirectionalLight l = new DirectionalLight();
        l.setDirection(Vector3f.UNIT_Y.mult(-1));
        l.setColor(ColorRGBA.White.clone().multLocal(0.3f));
        
        Spatial sky = SkyFactory.createSky(assetManager, "Scenes/FullskiesSunset0068.dds", false);
        sky.setLocalScale(350);

        mainScene.attachChild(sky);
        cam.setFrustumFar(4000);

        water = new WaterFilter(rootNode, lightDir);

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);

        fpp.addFilter(water);
        BloomFilter bloom = new BloomFilter();
        bloom.setExposurePower(55);
        bloom.setBloomIntensity(1.0f);
        fpp.addFilter(bloom);
        LightScatteringFilter lsf = new LightScatteringFilter(lightDir.mult(-300));
        lsf.setLightDensity(1.0f);
        fpp.addFilter(lsf);
        DepthOfFieldFilter dof = new DepthOfFieldFilter();
        dof.setFocusDistance(0);
        dof.setFocusRange(100);
        fpp.addFilter(dof);

        water.setWaveScale(0.003f);
        water.setMaxAmplitude(2f);
        water.setFoamExistence(new Vector3f(1f, 4, 0.5f));
        water.setFoamTexture((Texture2D) assetManager.loadTexture("MatDefs/foam2.jpg"));

        water.setRefractionStrength(0.2f);

        water.setWaterHeight(initialWaterHeight);
 
        viewPort.addProcessor(fpp);     
    }
    
    /**
     *
     * @param tpf
     */
    @Override
    public void simpleUpdate(float tpf){
        super.simpleUpdate(tpf);
        time += tpf;
        waterHeight = (float) Math.cos(((time * 0.6f) % FastMath.TWO_PI)) * 1.5f;
        water.setWaterHeight(initialWaterHeight + waterHeight);
        stateManager.update(tpf);
    }
    
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        //Create an instance of this program
        MARS_Main app = new MARS_Main();
        //The start() method runs simpleInitGame(),
        app.start();
    }
    
    private void createTerrain(Node rootNode) {
        matRock = new Material(assetManager, "MatDefs/TerrainLighting.j3md");
        matRock.setBoolean("useTriPlanarMapping", false);
        matRock.setBoolean("WardIso", true);
        matRock.setTexture("AlphaMap", assetManager.loadTexture("Textures/alphamap.png"));
        Texture heightMapImage = assetManager.loadTexture("Textures/mountains512.png");
        Texture grass = assetManager.loadTexture("Textures/grass.jpg");
        grass.setWrap(Texture.WrapMode.Repeat);
        matRock.setTexture("DiffuseMap", grass);
        matRock.setFloat("DiffuseMap_0_scale", 64);
        Texture dirt = assetManager.loadTexture("Textures/dirt.jpg");
        dirt.setWrap(Texture.WrapMode.Repeat);
        matRock.setTexture("DiffuseMap_1", dirt);
        matRock.setFloat("DiffuseMap_1_scale", 16);
        Texture rock = assetManager.loadTexture("Textures/road.jpg");
        rock.setWrap(Texture.WrapMode.Repeat);
        matRock.setTexture("DiffuseMap_2", rock);
        matRock.setFloat("DiffuseMap_2_scale", 128);
        Texture normalMap0 = assetManager.loadTexture("Textures/grass_normal.jpg");
        normalMap0.setWrap(Texture.WrapMode.Repeat);
        Texture normalMap1 = assetManager.loadTexture("Textures/dirt_normal.png");
        normalMap1.setWrap(Texture.WrapMode.Repeat);
        Texture normalMap2 = assetManager.loadTexture("Textures/road_normal.png");
        normalMap2.setWrap(Texture.WrapMode.Repeat);
        matRock.setTexture("NormalMap", normalMap0);
        matRock.setTexture("NormalMap_1", normalMap2);
        matRock.setTexture("NormalMap_2", normalMap2);

        AbstractHeightMap heightmap = null;
        try {
            heightmap = new ImageBasedHeightMap(heightMapImage.getImage(), 0.25f);
            heightmap.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        terrain = new TerrainQuad("terrain", 65, 513, heightmap.getHeightMap());
        terrain.setMaterial(matRock);
        terrain.setLocalScale(new Vector3f(5, 5, 5));
        terrain.setLocalTranslation(new Vector3f(0, -30, 0));
        terrain.setLocked(false); // unlock it so we can edit the height

        terrain.setShadowMode(RenderQueue.ShadowMode.Receive);
        rootNode.attachChild(terrain);
    }
    
    /**
     *
     * @return
     */
    public float getWaterHeight(){
        return initialWaterHeight;
    }
    
    /**
     *
     * @return
     */
    public BulletAppState getBulletAppState(){
        return bullet;
    }
}
