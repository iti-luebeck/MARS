package org.FishSim.SwarmSimulation;


import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.List;
import jme3tools.optimize.LodGenerator;
import mars.control.MyLodControl;

/**
 *
 * @author Mandy Feldvoß
 */

public class Fish extends Node{
    protected Swarm swarm;
    private final FishControl control;
    private final Node model;
    private AnimControl modelControl;
    protected AnimChannel channel_swim;
    protected FishSim sim;
    protected Vector3f lastMove = new Vector3f().zero();
    protected Quaternion rotation = new Quaternion();   
    private final List<Geometry> listGeoms = new ArrayList<Geometry>();
    protected float moveSpeed = 0;
    protected float rotationSpeed = 0;
    private final float initHunger = 100;
    private float hungerAmount = initHunger;
    private boolean hunger = true;
 
    /**
     * Create a new Fish.
     *
     * @param sim               Simulation
     * @param scale             Size of the fish
     * @param localTrans        Starting position
     * @param swarm             Swarm where the fish belongs to
     * @param path              Path of the model
     * @param animation         Animation on/off
     */
    public Fish(FishSim sim, Vector3f scale, Vector3f localTrans, Swarm swarm, String path, boolean animation){
        this.sim = sim;
        control = new FishControl(this);
        model = (Node) sim.getMain().getAssetManager().loadModel(path);
        if(animation){
            modelControl = model.getChild("Cube").getControl(AnimControl.class);
            channel_swim = modelControl.createChannel();
            channel_swim.setAnim("ArmatureAction.001");
            channel_swim.setLoopMode(LoopMode.Loop);
        }
        optimize(model);
        attachChild(model);
        scale(scale.x, scale.y, scale.z);
        setLocalTranslation(localTrans);
        this.swarm = swarm;     
        sim.getRootNode().attachChild(this);
    }
    
    /**
     *
     * @param swarm Swarm where the fish belongs to
     */
    public void setSwarm(Swarm swarm){
       this.swarm = swarm; 
    }
    
    private void optimize(Node node){
        
        //jme3tools.optimize.GeometryBatchFactory.optimize(model);
        
        for(Spatial spatial : node.getChildren()){
           if(spatial instanceof Geometry){
                Geometry geo = (Geometry) spatial;
                LodGenerator lodGenerator = new LodGenerator(geo);          
                lodGenerator.bakeLods(LodGenerator.TriangleReductionMethod.PROPORTIONAL, 0.98f);
                geo.setLodLevel(0);
                MyLodControl lodControl = new MyLodControl();
                lodControl.setDistTolerance(0.1f);
                lodControl.setTrisPerPixel(0.5f);
                lodControl.setCam(sim.getMain().getCamera());
                geo.addControl(lodControl);
            }else if(spatial instanceof Node){
                optimize((Node)spatial);
            }
        }
    }
   
    /**
     *
     * @param enabled 
     */
    public void setVisible(boolean enabled) {
        if(!enabled){
            this.setCullHint(Spatial.CullHint.Always);
        }else{
            this.setCullHint(Spatial.CullHint.Never);
        }
    }
    
    /**
     *
     * @param tpf Time per frame
     */
    public void swim(float tpf){
        //control.swim(tpf);
    }
    
    /**
     *
     * @param speed Movement speed
     */
    public void setMoveSpeed(float speed){
        this.moveSpeed = speed;
    }
    
    /**
     *
     * @param speed Rotation speed
     */
    public void setRotationSpeed(float speed){
        this.rotationSpeed = speed;
    }
    
    /**
     *
     * @return lastMove Last move of the fish
     */
    public Vector3f getLastMove(){
        return lastMove;
    }
    
    /**
     *
     * @param source Foodsource
     * @param tpf Time per frame
     */
    public void eat(IFoodSource source, float tpf){
        hungerAmount -= source.feed(getLocalTranslation(), (1+getLocalScale().length())*tpf);
        if(hungerAmount <= 0){
            hungerAmount = 0;
            hunger = false;
        }
    }
    
    /**
     *
     * @param tpf Time per frame
     */
    public void getHungry(float tpf){
        hungerAmount += tpf;
        if(hungerAmount >= initHunger){
            hungerAmount = initHunger;
            hunger = true;
        }
    }
    
    /**
     *
     * @return Amount of hunger of the fish
     */
    public boolean isHungry(){
        return hunger;
    }   
}