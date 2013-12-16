package org.FishSim.SwarmSimulation;

import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author Mandy Feldvoß
 */

public class FoodSourceMap extends ArrayList<IFoodSource>{
    
    /**
     *
     * @param location Location of the foodsource
     * @return Nearest foodsource
     */
    public IFoodSource getNearestFS(Vector3f location){
        IFoodSource nearest;
        float dist;
        try{
            nearest = this.get(0);
            dist = location.distance(nearest.getNearestLocation(location));
        }catch( Exception e){
            
            return null; 
        }
        
        float temp;
        
        for(int i = 1; i < this.size(); i++){
            temp = location.distance(this.get(i).getNearestLocation(location));
            if(temp < dist){
                dist = temp;
                nearest = this.get(i);
            }
        }
        
        return nearest;
    }
    
    @Override
    public boolean add(IFoodSource food){
        food.addToMap(this);
        return super.add(food);
    }
}
