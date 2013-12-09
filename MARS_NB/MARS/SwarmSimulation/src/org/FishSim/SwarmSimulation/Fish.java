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
    private final String path = "Models/Fishtest/Fishtest.j3o";
    protected Swarm swarm;
    private FishControl control;
    private Node model;
    private AnimControl modelControl;
    protected AnimChannel channel_swim;
    protected FishSim sim;
    protected Vector3f lastMove = new Vector3f().zero();
    protected Quaternion rotation = new Quaternion();   
    private List<Geometry> listGeoms = new ArrayList<Geometry>();
    private float initHunger = 100;
    private float hungerAmount = initHunger;
    private boolean hunger = true;
 
    /**
     * Create a new Fish.
     *
     * @param sim               Simulation
     * @param scale             Size of the fish
     * @param rot               Starting rotation
     * @param localTrans        Starting position
     * @param swarm             Swarm where the fish belongs to
     * @param map               Foodsourcemap where the fish belongs to
     */
    public Fish(FishSim sim, Vector3f scale, Vector3f localTrans, Swarm swarm){
        this.sim = sim;
        control = new FishControl(this);
        model = (Node) sim.getMain().getAssetManager().loadModel(path);
        modelControl = model.getChild("Cube").getControl(AnimControl.class);
        optimize(model);
        channel_swim = modelControl.createChannel();
        channel_swim.setAnim("ArmatureAction.001");
        channel_swim.setLoopMode(LoopMode.Loop);
        attachChild(model);
        scale(scale.x, scale.y, scale.z);
        setLocalTranslation(localTrans.x, localTrans.y, localTrans.z);
        this.swarm = swarm;
        
        //Speed of this fish
        //moveSpeed = (float) (Math.random()) + swarm.moveSpeed;
        //moveSpeed = (float) swarm.moveSpeed + this.getLocalScale().length();
        //rotateSpeed = (float) (Math.random()) + 1f;
        
        sim.getRootNode().attachChild(this);
    }
    
    /**
     *
     * @param swarm
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
                lodGenerator.bakeLods(LodGenerator.TriangleReductionMethod.PROPORTIONAL, 0.1f, 0.8f);
                /*LodGenerator lodGenerator = new LodGenerator(geo);          
                lodGenerator.bakeLods(LodGenerator.TriangleReductionMethod.PROPORTIONAL, 0.8f, 0.8f);*/
                geo.setLodLevel(0);
                MyLodControl control = new MyLodControl();
                control.setDistTolerance(25f);
                control.setTrisPerPixel(0.5f);
                control.setCam(sim.getMain().getCamera());
                geo.addControl(control);
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
        control.swim(tpf);
    }
    
    /**
     *
     * @return lastMove
     */
    public Vector3f getLastMove(){
        return lastMove;
    }
    
    public void eat(IFoodSource source, float tpf){
        hungerAmount -= source.feed(getLocalTranslation(), (1+getLocalScale().length())*tpf);
        if(hungerAmount <= 0){
            hungerAmount = 0;
            hunger = false;
        }
    }
    
    public void getHungry(float tpf){
        hungerAmount += tpf;
        if(hungerAmount >= initHunger){
            hungerAmount = initHunger;
            hunger = true;
        }
    }
    
    public boolean isHungry(){
        return hunger;
    }
    
}
