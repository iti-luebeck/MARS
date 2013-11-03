package SwarmSimulation;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author Acer
 */
public class FoodSource extends Node{
    FishSim sim;
    float size;
    FoodSourceMap map;
    
    public FoodSource(FishSim sim, float size, Vector3f localTrans){
        this.sim = sim;
        this.size = size;
        this.setLocalTranslation(localTrans);
    }
    
    public void show(FoodSourceMap map){
        this.map = map;
        sim.getMain().getRootNode().attachChild(this);
    }
    
    public void eat(){
        size--;
        if(size <= 0){
            map.remove(this);
        }
    }
}
