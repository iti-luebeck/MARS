package org.FishSim.SwarmSimulation;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;

public class FishSim extends AbstractAppState implements AppStateExtension {
    private Node rootNode = new Node("FishSimState Root Node");
    private main main;
    protected ArrayList<Swarm> removedSwarms = new ArrayList<Swarm>();
    protected ArrayList<Swarm> addedSwarms = new ArrayList<Swarm>();
    protected ArrayList<Swarm> swarms = new ArrayList<Swarm>();
    private int latestSwarmId;
    private FoodSourceMap map;
 
    public FishSim(Application main){
        this.main = (main) main;
    }
 
    @Override
    public void initialize(AppStateManager stateManager, Application app){
           
        
        map = new FoodSourceMap();
        
        //Collision and escaping
        //map.add(new FoodSource(this, 100, new Vector3f(-327.21957f, 30.6459f, 86.884346f)));
        //swarms.add(new Swarm(this, 400, new Vector3f(-327.21957f, 81.6459f, 116.884346f), map, 0));
        //swarms.add(new Swarm(this, 200, new Vector3f(0.25f, 0.25f, 0.25f), new Vector3f(-197.21957f, 81.6459f, 136.884346f), map, 2));
        
        //Splitting
        map.add(new FoodSource(this, 10000, new Vector3f(-327.21957f, 81.6459f, 0.884346f)));
        addSwarm(500, new Vector3f(-327.21957f, 81.6459f, 120.884346f), map, 0);
        createObstacle(new Vector3f(-327.21957f, 81.6459f, 80.884346f), 5f);
    }
    
    public void addSwarm(int size, Vector3f trans, FoodSourceMap map, int type){
        addedSwarms.add(new Swarm(this, size, trans, map, type, latestSwarmId));
        latestSwarmId ++;
        
    }
    
    public Node getRootNode(){
        return rootNode;
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //rootNode.detachAllChildren();
        //mars.getRootNode().detachChild(getRootNode());
        /*simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                mars.getRootNode().detachChild(getRootNode());
                return null;
            }
        });*/
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

    @Override
    public void stateAttached(AppStateManager stateManager) {
        super.stateAttached(stateManager);
    }

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
        
        swarms.addAll(addedSwarms);
        addedSwarms.clear();
        
        swarms.removeAll(removedSwarms);
        removedSwarms.clear();
        
        rootNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();
    }
    
    public void createObstacle(Vector3f pos, float size){
        SphereCollisionShape sphere = new SphereCollisionShape(size);
        RigidBodyControl obstacle = new RigidBodyControl(sphere, 1);
        obstacle.setKinematic(true);
        obstacle.setCollisionGroup(3);
        obstacle.setCollideWithGroups(1);
        obstacle.setPhysicsLocation(pos);
        main.getBulletAppState().getPhysicsSpace().add(obstacle);
    }
    
    public main getMain(){
        return main;
    }
}
