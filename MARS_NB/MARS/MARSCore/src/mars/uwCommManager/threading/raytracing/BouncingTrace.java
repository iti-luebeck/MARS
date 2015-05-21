/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.threading.raytracing;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import mars.auv.AUV;
import mars.misc.Collider;
import mars.uwCommManager.helpers.DistanceTrigger;

/**
 *
 * @author jaspe_000
 */
public class BouncingTrace {
    
    private final int MAX_BOUNCES;
    private final float MAX_RANGE;
    private final float SPEED_OF_SOUND;
    private int floorBounceCounter;
    private int surfaceBounceCounter;

    
    private Vector3f rootAUVPosition = null;
    private Vector3f targetAUVPosition = null;
    
    private AUV rootAUV;
    private AUV targetAUV;
    
    Collider collider;
    
    private boolean oceanFloorFlat;
    

    public BouncingTrace(final int MAX_BOUNCES, float speedOfSound, float maxRange) {
        this.MAX_BOUNCES = MAX_BOUNCES;
        this.SPEED_OF_SOUND = speedOfSound;
        this.MAX_RANGE = maxRange;
        floorBounceCounter = 0;
        surfaceBounceCounter = 0;
        oceanFloorFlat = true;
    }
    
    
    public boolean init(AUV rootAUV, AUV targetAUV, Vector3f rootAUVPosition, Vector3f targetAUVPosition, Collider collider) {
        if(rootAUV == null || targetAUV == null || rootAUVPosition == null || targetAUVPosition == null) return false;
        this.rootAUV = rootAUV; 
        this.targetAUV = targetAUV;
        this.rootAUVPosition = rootAUVPosition;
        this.targetAUVPosition = targetAUVPosition;
        this.collider = collider;
        return true;
    }
    
    
    public float calculateWaterDepth(Vector3f position) {
        Vector3f vector = new Vector3f(0f,-1f,0f);
        
        Ray ray = new Ray(position, vector);
        CollisionResults colRes = new CollisionResults();
        collider.collideWith(ray, colRes);
        for(CollisionResult col : colRes) {
            if(col.getGeometry().getUserData("auv_name")==null) {
                //System.out.println("Position of my AUV " +rootAUVPosition+ " Name of my AUV " +rootAUV.getName() + " The water is "+ depthAtRootPos + " meters deep" );
                return col.getDistance();
            }
        }
        return 0f;
    }
    
    
    public synchronized DistanceTrigger nextBouncingRayTrace() {
        
        float distance = 0;
        float depthAtRoot = calculateWaterDepth(rootAUVPosition);
        float depthAtTarget = 0f;
        if(oceanFloorFlat) depthAtTarget = depthAtRoot;
        else depthAtTarget = calculateWaterDepth(targetAUVPosition);
        
        
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
        DistanceTrigger returnTrigger = new DistanceTrigger(distance, targetAUV.getName(), surfaceBounceCounter, floorBounceCounter,SPEED_OF_SOUND,false);
        
        
        
        return returnTrigger;
    }
}
