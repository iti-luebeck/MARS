package org.FishSim.SwarmSimulation;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author Mandy Feldvoß
 */

public class FoodSource extends Node{
    FishSim sim;
    float size;
    FoodSourceMap map;
    
    /**
     *
     * @param sim           Simulation
     * @param size          Size of the foodsource
     * @param localTrans    Position of the foodsource
     */
    public FoodSource(FishSim sim, float size, Vector3f localTrans){
        this.sim = sim;
        this.size = size;
        this.setLocalTranslation(localTrans);
    }
    
    /**
     *
     * @param map   Show foodsourcemap
     */
    public void show(FoodSourceMap map){
        this.map = map;
        sim.getMain().getRootNode().attachChild(this);
    }
    
    /**
     * Eat 
     */
    public void eat(){
        size--;
        if(size <= 0){
            map.remove(this);
        }
    }
}
