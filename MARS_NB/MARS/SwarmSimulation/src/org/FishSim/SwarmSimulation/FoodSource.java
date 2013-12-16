package org.FishSim.SwarmSimulation;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;

/**
 *
 * @author Mandy Feldvoß
 */

public class FoodSource extends Node implements IFoodSource{
    float size;
    ArrayList<FoodSourceMap> foreignMaps;
    
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
     * @param foreignMap  foodsourcemap which this foodsource belongs to
     */
    @Override
    public void addToMap(FoodSourceMap foreignMap){
        if(foreignMaps == null){
            foreignMaps = new ArrayList<FoodSourceMap>();
        }
        foreignMaps.add(foreignMap);
    }
    
    @Override
    public Vector3f getNearestLocation(Vector3f location){
        Vector3f radiusVec = location.subtract(getLocalTranslation()).normalize().mult(size/1000f);
        if(getLocalTranslation().distance(location) > getLocalTranslation().distance(getLocalTranslation().add(radiusVec))){
            return getLocalTranslation().add(radiusVec);
        }else{
            return new Vector3f(location);
        }
    }
    
    /**
     * Feed
     */
    @Override
    public float feed(Vector3f location, float amount){
        size -= amount;
        if(size <= 0){
            for(int i = 0; i < foreignMaps.size(); i++){
                foreignMaps.get(i).remove(this);
            }
        }
        return 1+amount;
    }
}
