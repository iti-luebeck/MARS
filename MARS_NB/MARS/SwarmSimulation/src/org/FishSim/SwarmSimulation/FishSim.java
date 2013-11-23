package org.FishSim.SwarmSimulation;

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
import org.openide.util.lookup.ServiceProvider;
import java.util.ArrayList;
import mars.Initializer;
import mars.MARS_Main;
import mars.states.AppStateExtension;
import mars.states.SimState;

/**
 *
 * @author Mandy Feldvoß
 */
@ServiceProvider(service=AbstractAppState.class)
public class FishSim extends AbstractAppState implements AppStateExtension {
    //MARS variables
    private Node rootNode = new Node("FishSimState Root Node");
    private MARS_Main mars;
    private Initializer initer;
    private BulletAppState bulletAppState;
    protected ArrayList<Swarm> removedSwarms = new ArrayList<Swarm>();
    protected ArrayList<Swarm> swarms = new ArrayList<Swarm>();
    private int latestSwarmId;
    private FoodSourceMap map;
 
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
                if(stateManager.getState(SimState.class)!=null){
                    initer = stateManager.getState(SimState.class).getIniter();
                }else{
                    throw new RuntimeException("SimState not found/initialized!");
                }
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
        
        map = new FoodSourceMap();
        
        //Collision and escaping
        //map.add(new FoodSource(this, 100, new Vector3f(-327.21957f, 30.6459f, 86.884346f)));
        //swarms.add(new Swarm(this, 400, new Vector3f(-327.21957f, 81.6459f, 116.884346f), map, 0));
        //swarms.add(new Swarm(this, 200, new Vector3f(0.25f, 0.25f, 0.25f), new Vector3f(-197.21957f, 81.6459f, 136.884346f), map, 2));
        
        //Splitting-Test
        //map.add(new FoodSource(this, 10000, new Vector3f(-327.21957f, 81.6459f, 0.884346f)));
        addSwarm(100, new Vector3f(0.01f, 0.01f, 0.01f), new Vector3f(0f, 0f, 0f), map, 0);
        swarms.get(latestSwarmId-1).setMoveSpeed(0.5f);
        swarms.get(latestSwarmId-1).setRotationSpeed(1f);
        //addSwarm(1, new Vector3f(0.25f, 0.25f, 0.25f), new Vector3f(-327.21957f, 81.6459f, -120.884346f), map, 2);
        //createObstacle(new Vector3f(-327.21957f, 81.6459f, 80.884346f), 5f);
    }
    
    /**
     *
     * @param size      Size of the swarm
     * @param trans
     * @param map
     * @param type
     */
    public void addSwarm(int size, Vector3f scale, Vector3f trans, FoodSourceMap map, int type){
        if(scale==null){
            swarms.add(new Swarm(this, size, trans, map, type, latestSwarmId));
        }else{
            swarms.add(new Swarm(this, size, scale, trans, map, type, latestSwarmId));
        }
        latestSwarmId ++;
        
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
        obstacle.setCollisionGroup(3);
        obstacle.setCollideWithGroups(1);
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
    public Initializer getIniter() {
        return initer;
    }
    
        /**
     *
     * @return
     */
    public BulletAppState getBulletAppState() {
        return bulletAppState;
    }

    /**
     *
     * @param cam
     */
    @Override
    public void setCamera(Camera cam) {
        
    }
}
