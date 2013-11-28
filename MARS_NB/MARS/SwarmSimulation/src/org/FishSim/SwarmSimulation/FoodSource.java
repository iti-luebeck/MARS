package org.FishSim.SwarmSimulation;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author Mandy Feldvoß
 */

public class FoodSource extends Node implements IFoodSource{
    float size;
    FoodSourceMap map;
    
    /**
     *
     * @param size          Size of the foodsource
     * @param localTrans    Position of the foodsource
     */
    public FoodSource(float size, Vector3f localTrans){
        this.size = size;
        setLocalTranslation(localTrans);
    }
    
    /**
     *
     * @param map   Show foodsourcemap
     */
    public void setMap(FoodSourceMap map){
        this.map = map;
    }
    
    /**
     * Feed
     */
    @Override
    public void feed(){
        size--;
        if(size <= 0){
            map.remove(this);
        }
    }
}
