package SwarmSimulation;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
//import org.openide.util.lookup.ServiceProvider;
import java.util.ArrayList;
//import mars.Initializer;
//import mars.MARS_Main;
//import mars.states.AppStateExtension;
//import mars.states.SimState;

/**
 *
 * @author Mandy Feldvoß
 */
//@ServiceProvider(service=AbstractAppState.class)
public class FishSim extends AbstractAppState implements AppStateExtension {
    //MARS variables
    private Node rootNode = new Node("FishSimState Root Node");
    private MARS_Main mars;
//    private Initializer initer;
    private BulletAppState bulletAppState;
    protected ArrayList<Swarm> removedSwarms = new ArrayList<Swarm>();
    protected ArrayList<Swarm> swarms = new ArrayList<Swarm>();
    FPSTest fpsTest = new FPSTest(10f, 60f, "FPSTest.txt");
 
    /**
     *
     *
     */
    public FishSim() {
        super();
    }
    
        /**
     *
     * @param main
     * @deprecated
     */
    @Deprecated
    public FishSim(Application main){
        super();
        this.mars = (MARS_Main) main;
    }
 
    /**
     *
     * @param stateManager
     * @param app
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app){
        if(!super.isInitialized()){
            if(app instanceof MARS_Main){
                mars = (MARS_Main)app;
                //assetManager = mars.getAssetManager();
                mars.getRootNode().attachChild(getRootNode());
//                if(stateManager.getState(SimState.class)!=null){
//                    initer = stateManager.getState(SimState.class).getIniter();
//                }else{
//                    throw new RuntimeException("SimState not found/initialized!");
//                }
                if(stateManager.getState(BulletAppState.class)!=null){
                    bulletAppState = stateManager.getState(BulletAppState.class);
                }else{
                    throw new RuntimeException("BulletAppState not found/initialized!");
                }
            }else{
                throw new RuntimeException("The passed application is not of type \"MARS_Main\"");
            } 
        }
        super.initialize(stateManager, app); 
        
        FoodSourceMap mapType0_1 = new FoodSourceMap();
        FoodSourceMap mapType0_2 = new FoodSourceMap();
        FoodSourceMap mapType0_3 = new FoodSourceMap();
        //FoodSourceMap mapType2 = new FoodSourceMap();
        
        FoodSource food = new FoodSource(1000, new Vector3f(-350f, 70f, 150));
        getRootNode().attachChild(food);
        mapType0_1.add(food);
        
        food = new FoodSource(1000, new Vector3f(-340f, 75f, 150));
        getRootNode().attachChild(food);
        mapType0_2.add(food);
        
        food = new FoodSource(1000, new Vector3f(-350f, 70f, 200));
        getRootNode().attachChild(food);
        mapType0_3.add(food);
        
        Swarm swarm = new Swarm(this, 100, new Vector3f(0.05f, 0.05f, 0.05f), 0.01f, new Vector3f(-350f, 80f, 150f), mapType0_1, 0, "Models/Fishtest/Fishtest.j3o", true);
        swarm.setMoveSpeed(0.01f);
        swarm.setRotationSpeed(0.01f);
        swarms.add(swarm);
        
        swarm = new Swarm(this, 100, new Vector3f(0.05f, 0.05f, 0.05f), 0.01f, new Vector3f(-345f, 75f, 150f), mapType0_2, 0, "Models/Fishtest/Fishtest.j3o", true);
        swarm.setMoveSpeed(0.01f);
        swarm.setRotationSpeed(0.01f);
        swarms.add(swarm);
        
        swarm = new Swarm(this, 100, new Vector3f(0.05f, 0.05f, 0.05f), 0.01f, new Vector3f(-350f, 80f, 145f), mapType0_3, 0, "Models/Fishtest/Fishtest.j3o", true);
        swarm.setMoveSpeed(0.01f);
        swarm.setRotationSpeed(0.01f);
        swarms.add(swarm);
        
        //swarm = new Swarm(this, 10, new Vector3f(0.2f, 0.2f, 0.2f), new Vector3f(-350f, 60f, 100), mapType2, 2);
        //swarm.setMoveSpeed(1f);
        //swarm.setRotationSpeed(1f);
        //swarms.add(swarm);
        createObstacle(new Vector3f(-350f, 75f, 150), 0.3f);
    }
    
    
    /**
     *
     * @return rootNode
     */
    @Override
    public Node getRootNode(){
        return rootNode;
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
    }
    
    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

    @Override
    public boolean isInitialized() {
        return super.isInitialized();
    }

    @Override
    public void postRender() {
        if (!super.isEnabled()) {
            return;
        }
        super.postRender();
    }

    /**
     *
     * @param rm
     */
    @Override
    public void render(RenderManager rm) {
        if (!super.isEnabled()) {
            return;
        }
        super.render(rm);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(!enabled){
            rootNode.setCullHint(Spatial.CullHint.Always);
        }else{
            rootNode.setCullHint(Spatial.CullHint.Never);
        }
    }

    /**
     *
     * @param stateManager
     */
    @Override
    public void stateAttached(AppStateManager stateManager) {
        super.stateAttached(stateManager);
    }

    /**
     *
     * @param stateManager
     */
    @Override
    public void stateDetached(AppStateManager stateManager) {
        super.stateDetached(stateManager);
    }

       
    @Override
    public void update(float tpf) {
        if (!super.isEnabled()) {
            return;
        }
        super.update(tpf);
        
        fpsTest.next(tpf);
        
        for(int i = 0; i < swarms.size(); i++){
            swarms.get(i).move(tpf);
        }
        
        swarms.removeAll(removedSwarms);
        removedSwarms.clear();
        
        rootNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();
    }
    
    /**
     *
     * @param pos   Position of the obstacle
     * @param size  Size of the obstacle
     */
    public void createObstacle(Vector3f pos, float size){
        SphereCollisionShape sphere = new SphereCollisionShape(size);
        RigidBodyControl obstacle = new RigidBodyControl(sphere, 1);
        obstacle.setKinematic(true);
        obstacle.setCollisionGroup(06);
        obstacle.setCollideWithGroups(04);
        obstacle.setCollisionGroup(6);
        obstacle.setCollideWithGroups(4);
        obstacle.setPhysicsLocation(pos);
        getBulletAppState().getPhysicsSpace().add(obstacle);
    }
    
    /**
     *
     * @return
     */
    public MARS_Main getMain(){
        return mars;
    }
    
        /**
     *
     * @return
     */
//    public Initializer getIniter() {
//        return initer;
//    }
    
        /**
     *
     * @return
     */
    public BulletAppState getBulletAppState() {
        return bulletAppState;
    }
    
    public float getCurrentWaterHeight(float x, float z){
        return mars.getWaterHeight();
    }

    /**
     *
     * @param cam
     */
//    @Override
//    public void setCamera(Camera cam) {        
//    }
}
