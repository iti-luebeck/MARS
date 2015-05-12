/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.threading.raytracing;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import mars.auv.AUV;
import mars.uwCommManager.helpers.DistanceTrigger;

/**
 *
 * @author jaspe_000
 */
public class BouncingTrace {
    
    private final int MAX_BOUNCES;
    private int floorBounceCounter;
    private int surfaceBounceCounter;
    private float temperature;
    
    private Vector3f rootAUVPosition = null;
    private Vector3f targetAUVPosition = null;
    
    private AUV rootAUV;
    private AUV targetAUV;
    
    Node sceneRootNode = null;
    
    public BouncingTrace(final int MAX_BOUNCES) {
        this.MAX_BOUNCES = MAX_BOUNCES;
        floorBounceCounter = 0;
        surfaceBounceCounter = 0;
    }
    
    
    public boolean init(AUV rootAUV, AUV targetAUV, Vector3f rootAUVPosition, Vector3f targetAUVPosition, Node sceneRootNode, float temperature) {
        if(rootAUV == null || targetAUV == null || rootAUVPosition == null || targetAUVPosition == null) return false;
        this.rootAUV = rootAUV; 
        this.targetAUV = targetAUV;
        this.rootAUVPosition = rootAUVPosition;
        this.targetAUVPosition = targetAUVPosition;
        this.sceneRootNode = sceneRootNode;
        this.temperature = temperature;
        return true;
    }
    
    
    public synchronized DistanceTrigger nextBouncingRayTrace() {
        
        float distance = 0;
        
        int bounces = 1;
        boolean down = false;
        float virtualHeight = 0f;
        if(surfaceBounceCounter<=floorBounceCounter) {
            //set virtualHeight to the distance of the surface
        } else {
            down = true;
            //set virtualHeight to the distance of the ocean floor
        }
        while(bounces < MAX_BOUNCES) {
            //add the distance between the surface and the floor MAX_BOUNCES -1 times
            // to virtualHeight
        }
        //create a vector with our virtual position to get the direction of the vector.
        Vector3f virtualPosition = rootAUVPosition.clone();
        virtualPosition.setX(virtualHeight);
        //obtain the direction vector
        Vector3f direction = targetAUVPosition.subtract(virtualPosition);
        distance = direction.length();
        // flip the direction vector on the xy-plane and normalize it
        direction = direction.normalize();
        direction.setZ(direction.getZ()*(-1f));
        //create a distanceTrigger;
        DistanceTrigger returnTrigger = new DistanceTrigger(distance, targetAUV.getName(), surfaceBounceCounter, floorBounceCounter,temperature);
        
        
        
        return returnTrigger;
    }
}
