package org.FishSim.SwarmSimulation;

import com.jme3.math.Vector3f;
import java.util.ArrayList;

/**
 *
 * @author Mandy Feldvoß
 */

public class FoodSourceMap extends ArrayList<FoodSource>{
    
    /**
     *
     * @param location Location of the foodsource
     * @return Nearest foodsource
     */
    public Vector3f getNearestFS(Vector3f location, float tpf){
        FoodSource nearest;
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
        
        Vector3f distVec = nearest.getNearestLocation(location);
        if(dist <= 0.3){
               nearest.feed(tpf);
        }
        return distVec;
    }
    
    @Override
    public boolean add(FoodSource food){
        food.setMap(this);
        return super.add(food);
    }
}
