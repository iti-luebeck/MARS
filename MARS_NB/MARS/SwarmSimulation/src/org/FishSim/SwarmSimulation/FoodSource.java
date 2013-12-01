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
    
    @Override
    public Vector3f getNearestLocation(Vector3f location){
        return getLocalTranslation().add(location.subtract(getLocalTranslation()).normalize().mult((float)size/1000));
    }
    
    /**
     * Feed
     */
    @Override
    public void feed(float tpf){
        size -= tpf;
        if(size <= 0){
            map.remove(this);
        }
    }
}
