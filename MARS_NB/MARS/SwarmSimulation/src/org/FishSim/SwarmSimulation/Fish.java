package org.FishSim.SwarmSimulation;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.animation.SkeletonControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.LodControl;
import java.util.ArrayList;
import java.util.List;
import jme3tools.optimize.LodGenerator;

/**
 *
 * @author Acer
 */

public class Fish extends Node{
    private final String path = "Models/Fishtest4/Fishtest4.j3o";
    private Swarm swarm;
    private Node model;
    private AnimControl modelControl;
    private AnimChannel channel_swim;
    private FishSim sim;
    FoodSourceMap map;
    private Vector3f lastMove = new Vector3f().zero();
    private Quaternion rotation = new Quaternion();   
    private float rotateSpeed = 2;
    protected float moveSpeed;
    private List<Geometry> listGeoms = new ArrayList<Geometry>();
 
    /**
     * Create a new Fish.
     *
     * @param scale             Size of the fish
     * @param rot               Starting rotation
     * @param localTrans        Starting position
     * @param swarm             Swarm where the fish belongs to
     * @param map               Foodsourcemap where the fish belongs to
     */
    public Fish(FishSim sim, Vector3f scale, Vector3f rot, Vector3f localTrans, Swarm swarm, FoodSourceMap map){
        this.sim = sim;
        
        model = (Node) sim.getMain().getAssetManager().loadModel(path);
        //modelControl = model.getChild("Cube").getControl(AnimControl.class);
        
        //channel_swim = modelControl.createChannel();
        //channel_swim.setAnim("ArmatureAction.014");
        //channel_swim.setLoopMode(LoopMode.Loop);
        //attachChild(model);
        scale(scale.x, scale.y, scale.z);
        rotate(rot.x, rot.y, rot.z);
        setLocalTranslation(localTrans.x, localTrans.y, localTrans.z);
        this.swarm = swarm;
        this.map = map;
        
        //Speed of this fish
        moveSpeed = (float) (Math.random()) + swarm.moveSpeed;
        rotateSpeed = (float) (Math.random()) + 1f;
    }
    
    public void setSwarm(Swarm swarm){
       this.swarm = swarm; 
    }
    
    public void show(){
        /*
        jme3tools.optimize.GeometryBatchFactory.optimize(model);
        
        Geometry geo = null;
        for(Spatial spatial : model.getChildren()){
           if(spatial instanceof Geometry){
                geo = (Geometry) spatial;
            }
        }
        
        LodGenerator lodGenerator = new LodGenerator(geo);          
        lodGenerator.bakeLods(LodGenerator.TriangleReductionMethod.PROPORTIONAL, 0.1f, 0.5f);
        geo.setLodLevel(1);
        LodControl control = new LodControl();
        control.setDistTolerance(1);
        control.setTrisPerPixel(1);
        geo.addControl(control);
        
        attachChild(geo);*/
        sim.getMain().getRootNode().attachChild(this);
    }
    
    public void swim(float tpf){
        Vector3f diff = new Vector3f(0f ,0f ,0f);
        
        Vector3f tempVec = new Vector3f(0f ,0f ,0f);
        float tempF;
        ArrayList<Fish> neigh = swarm.getNearNeigh(this);
        
        for(int i = 0; i < neigh.size(); i++){
            tempVec.set(getLocalTranslation().subtract(neigh.get(i).getLocalTranslation()));
            tempF = tempVec.length();
            if(tempF < 0.1){
                tempF = 0.1f;
            }
            tempVec.normalizeLocal();
            //Seperation
            diff.addLocal(tempVec.multLocal(swarm.getNear() / tempF));
            //Allignment
            diff.addLocal(neigh.get(i).getLastMove());
        }
        
        //Cohesion
        diff.addLocal(swarm.getCenter().subtract(getLocalTranslation()));
        
        //EAT!
        FoodSource food = map.getNearestFS(getLocalTranslation());
        if(food != null){
            tempVec = food.getLocalTranslation().subtract(getLocalTranslation());
            if(tempVec.length() > this.getLocalScale().length() + 1){
                diff.addLocal(tempVec.divide(tempVec.length()/2));
            }
            if(getLocalTranslation().distance(food.getLocalTranslation()) <= 0.5f){
                food.eat();
            }
        }
        
        //Escape
        tempVec = swarm.getViewLocation();
        if(tempVec != null){
            diff.normalizeLocal();
            Vector3f tempVector = getLocalTranslation().subtract(tempVec);
            tempF = tempVector.length();
            tempVector.normalizeLocal();
            diff.addLocal(tempVector.mult(2));   
        }
        
        //Collision
        tempVec = swarm.getColLocation();
        if(tempVec != null){
            diff.normalizeLocal();
            Vector3f tempVector = getLocalTranslation().subtract(tempVec);
            tempF = tempVector.length();
            tempVector.normalizeLocal();
            diff.addLocal(tempVector.divide(tempF/5));
        }
        
        //WaterHeight
        if(getLocalTranslation().y > (sim.getMain().getWaterHeight() - 5f)){
            diff.normalizeLocal();
            diff.subtractLocal(Vector3f.UNIT_Y.mult(2));
        }
        
        if(diff.equals(new Vector3f().zero())){
            diff.set((float) (Math.random() - Math.random())/5f, (float) (Math.random() - Math.random())/5f, (float) (Math.random() - Math.random())/5f);
        }
        
        if(diff.length() > 1){
            diff.normalizeLocal();
        }
        rotation.lookAt(diff, getLocalRotation().multLocal(Vector3f.UNIT_Y));
        Vector3f moveVec = getLocalRotation().mult(Vector3f.UNIT_Z);
        moveVec.multLocal(moveSpeed + swarm.escapeInc);
        moveVec.multLocal(tpf);
        moveVec.multLocal(diff.length());
        lastMove = moveVec;
        this.setLocalTranslation(getLocalTranslation().add(moveVec));
        
        //channel_swim.setSpeed((moveSpeed + swarm.escapeInc));
        
        this.getLocalRotation().slerp(rotation, tpf*rotateSpeed);
        
    }
    
    public Vector3f getLastMove(){
        return lastMove;
    }
}
