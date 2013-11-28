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
    public Vector3f getNearestFS(Vector3f location){
        FoodSource nearest;
        float dist;
        try{
            nearest = this.get(0);
            dist = location.distance(nearest.getLocalTranslation());
        }catch( Exception e){
            
            return null; 
        }
        
        float temp;
        
        for(int i = 1; i < this.size(); i++){
            temp = location.distance(this.get(i).getLocalTranslation());
            if(temp < dist){
                dist = temp;
                nearest = this.get(i);
            }
        }
        Vector3f tempVec = location.subtract(nearest.getLocalTranslation()).normalize().mult((float)nearest.size/1000);
        if(location.distance(nearest.getLocalTranslation()) <= tempVec.length() + 0.03){
               nearest.feed();
        }
        return nearest.getLocalTranslation().add(tempVec);
    }
    
    @Override
    public boolean add(FoodSource food){
        food.setMap(this);
        return super.add(food);
    }
}
