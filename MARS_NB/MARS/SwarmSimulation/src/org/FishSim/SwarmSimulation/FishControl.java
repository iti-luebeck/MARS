package org.FishSim.SwarmSimulation;

import com.jme3.math.Vector3f;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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
   
       Vector3f steerVec = new Vector3f(0,0,0);
       
       steerVec = basicRules(steerVec);
       steerVec = feed(steerVec);
       steerVec = flee(steerVec);
       steerVec = collision(steerVec);
       steerVec = waterHeight(steerVec);

       fish.rotation.lookAt(steerVec, fish.getLocalRotation().multLocal(Vector3f.UNIT_Y));
       Vector3f moveVec = fish.getLocalRotation().mult(Vector3f.UNIT_Z);
       moveVec.multLocal(fish.swarm.moveSpeed + fish.swarm.escapeInc);
       moveVec.multLocal(steerVec.length());
       moveVec.multLocal(1f-(float)Math.toDegrees(moveVec.normalize().angleBetween(steerVec.normalize()))/180f);
       //AnimationSpeed
       fish.channel_swim.setSpeed(10*moveVec.length());
       moveVec.multLocal(tpf);
       fish.lastMove = moveVec;
       fish.setLocalTranslation(fish.getLocalTranslation().add(moveVec)); 
        
       fish.getLocalRotation().slerp(fish.rotation, tpf*fish.swarm.rotationSpeed);       
   }
    
    private Vector3f basicRules(Vector3f steerVec){
        Vector3f tempVec;
        float tempF;
        Vector3f avgMove = new Vector3f(0f, 0f, 0f);
        ArrayList<Fish> neigh = fish.swarm.getNearNeigh(fish);
        
        //Cohesion
        steerVec.addLocal(fish.swarm.getCenter().subtract(fish.getLocalTranslation()));
       
        for(int i = 0; i < neigh.size(); i++){
            //Seperation
            tempVec = fish.getLocalTranslation().subtract(neigh.get(i).getLocalTranslation());
            tempF = tempVec.length();
            tempVec.normalizeLocal();
            tempVec.multLocal((float)Math.pow(1f-tempF/fish.swarm.getNear(), 2f));
            //tempVec.multLocal(1+fish.getLocalScale().length()+neigh.get(i).getLocalScale().length());
           
            steerVec.addLocal(tempVec.multLocal(1/fish.swarm.getNear())); // nach reynolds
           
            //Allignment
            avgMove.add(neigh.get(i).getLastMove());
        }
       
        //Allignment
        avgMove.divide(neigh.size());
        steerVec.addLocal(avgMove.subtract(fish.lastMove));
        return steerVec;
    }
    
    private Vector3f feed(Vector3f steerVec){
       //EAT!
        Vector3f tempVec;
        float tempF;
        tempVec = fish.map.getNearestFS(fish.getLocalTranslation());
        if(tempVec != null){
            tempVec.subtractLocal(fish.getLocalTranslation());
            if(tempVec.length() > 1f){
                steerVec.addLocal(tempVec.normalize());
            }else{
                tempF = tempVec.length();
                steerVec.multLocal(tempVec.length());
                tempVec.normalizeLocal();
                steerVec.addLocal(tempVec.multLocal(1+tempF));
            }
        }
         return steerVec;
     }
    
    private Vector3f flee(Vector3f steerVec){
        //Escape
        Vector3f tempVec;
        tempVec = fish.swarm.getViewLocation();
        if(tempVec != null){
            steerVec.normalizeLocal();
            Vector3f tempVector = fish.getLocalTranslation().subtract(tempVec);
            tempVector.normalizeLocal();
            steerVec.addLocal(tempVector);   
        }
         return steerVec;
     }
    
    private Vector3f collision(Vector3f steerVec){
        //Collision
        Vector3f tempVec;
        tempVec = fish.swarm.getColLocation();
        if(tempVec != null){
            steerVec.normalizeLocal();
            Vector3f tempVector = fish.getLocalTranslation().subtract(tempVec);
            tempVector.normalizeLocal();
            steerVec.addLocal(tempVector);
        }
        return steerVec;
    }
    
    private Vector3f waterHeight(Vector3f steerVec){
        //WaterHeight
        float waterHeight = fish.sim.getIniter().getCurrentWaterHeight(fish.getLocalTranslation().x, fish.getLocalTranslation().z);
        if(fish.getLocalTranslation().y > waterHeight-1){
            steerVec = new Vector3f(0, -1, 0);
        }
        return steerVec;
    }
}
