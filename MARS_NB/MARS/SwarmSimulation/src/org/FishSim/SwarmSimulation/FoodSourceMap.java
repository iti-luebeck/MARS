package org.FishSim.SwarmSimulation;

import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author Acer
 */
public class FoodSourceMap extends ArrayList<FoodSource>{
    
    /**
     *
     * @param location
     * @return
     */
    public FoodSource getNearestFS(Vector3f location){
        FoodSource nearest = null;
        float dist;
        try{
            nearest = this.get(0);
            dist = location.distance(nearest.getLocalTranslation());
        }catch( Exception e){
            
            return nearest; 
        }
        
        float temp;
        
        for(int i = 1; i < this.size(); i++){
            temp = location.distance(this.get(i).getLocalTranslation());
            if(temp < dist){
                dist = temp;
                nearest = this.get(i);
            }
        }
        return nearest;
    }
    
    @Override
    public boolean add(FoodSource food){
        food.show(this);
        return super.add(food);
    }
}
