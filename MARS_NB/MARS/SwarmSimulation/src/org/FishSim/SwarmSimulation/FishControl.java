package org.FishSim.SwarmSimulation;

import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author Mandy Feldvoß
 */

public class FishControl {
    Fish fish;
    
    public FishControl(Fish fish){
        this.fish = fish;
    }
    
    public void swim(float tpf){
   
       Vector3f diff = new Vector3f(0f ,0f ,0f);
        
       Vector3f tempVec = new Vector3f(0f ,0f ,0f);
       Vector3f avgMove = new Vector3f(0f, 0f, 0f);
       ArrayList<Fish> neigh = fish.swarm.getNearNeigh(fish);
        
       for(int i = 0; i < neigh.size(); i++){
           tempVec.set(fish.getLocalTranslation().subtract(neigh.get(i).getLocalTranslation()));
           tempVec.normalizeLocal();
           //Seperation
           //diff.addLocal(tempVec.multLocal(1/neigh.size()));
           diff.addLocal(tempVec.multLocal(1/fish.swarm.getNear())); // nach reynolds
           
           //Allignment
           avgMove.add(neigh.get(i).getLastMove());
       }
       
       //Allignment
       avgMove.divide(neigh.size());
       diff.addLocal(avgMove.subtract(fish.lastMove));
        
       //Cohesion
       diff.addLocal(fish.swarm.getCenter().subtract(fish.getLocalTranslation()));
        
       //EAT!
       FoodSource food = fish.map.getNearestFS(fish.getLocalTranslation());
       if(food != null){
           tempVec = food.getLocalTranslation().subtract(fish.getLocalTranslation());
           if(tempVec.length() > (fish.getLocalScale().length())){
               diff.addLocal(tempVec.divide(tempVec.length()/2));
           }
           if(fish.getLocalTranslation().distance(food.getLocalTranslation()) <= 0.5f){
               food.eat();
           }
       }
        
       //Escape
       tempVec = fish.swarm.getViewLocation();
       if(tempVec != null){
           diff.normalizeLocal();
           Vector3f tempVector = fish.getLocalTranslation().subtract(tempVec);
           tempVector.normalizeLocal();
           diff.addLocal(tempVector);   
       }
        
       //Collision
       tempVec = fish.swarm.getColLocation();
       if(tempVec != null){
           diff.normalizeLocal();
           Vector3f tempVector = fish.getLocalTranslation().subtract(tempVec);
           tempVector.normalizeLocal();
           diff.addLocal(tempVector);
       }
        
       //WaterHeight
       System.err.println("was geht aaaaaab!?");
       System.out.println(fish.sim.getIniter().getCurrentWaterHeight(fish.getLocalTranslation().x, fish.getLocalTranslation().z));
       if(fish.getLocalTranslation().y > (fish.sim.getIniter().getCurrentWaterHeight(fish.getLocalTranslation().x, fish.getLocalTranslation().z) - 5f)){
           diff.normalizeLocal();
           diff.subtractLocal(Vector3f.UNIT_Y.mult(2));
       }
        
       if(diff.equals(new Vector3f().zero())){
           diff.set((float) (Math.random() - Math.random())/5f, (float) (Math.random() - Math.random())/5f, (float) (Math.random() - Math.random())/5f);
       }
        
       if(diff.length() > 1){
           diff.normalizeLocal();
       }
       fish.rotation.lookAt(diff, fish.getLocalRotation().multLocal(Vector3f.UNIT_Y));
       Vector3f moveVec = fish.getLocalRotation().mult(Vector3f.UNIT_Z);
       moveVec.multLocal(fish.moveSpeed + fish.swarm.escapeInc);
       moveVec.multLocal(diff.length());
       //AnimationSpeed
       fish.channel_swim.setSpeed(moveVec.length());
       moveVec.multLocal(tpf);
       fish.lastMove = moveVec;
       fish.setLocalTranslation(fish.getLocalTranslation().add(moveVec)); 
        
       fish.getLocalRotation().slerp(fish.rotation, tpf*fish.rotateSpeed);       
   }
}
