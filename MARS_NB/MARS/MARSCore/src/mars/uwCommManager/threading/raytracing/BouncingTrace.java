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
    
    
    public boolean init(AUV rootAUV, AUV targetAUV, Vector3f rootAUVPosition, Vector3f targetAUVPosition, Node sceneRootNode) {
        if(rootAUV == null || targetAUV == null || rootAUVPosition == null || targetAUVPosition == null) return false;
        this.rootAUV = rootAUV; 
        this.targetAUV = targetAUV;
        this.rootAUVPosition = rootAUVPosition;
        this.targetAUVPosition = targetAUVPosition;
        this.sceneRootNode = sceneRootNode;
        return true;
    }
    
    
    public synchronized DistanceTrigger nextBouncingRayTrace() {
        
        
        
        return null;
    }
}
