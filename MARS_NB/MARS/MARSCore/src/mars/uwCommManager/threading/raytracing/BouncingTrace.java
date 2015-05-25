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
import java.util.LinkedList;
import java.util.List;
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
    private final DirectTrace father;

    Collider collider;

    private boolean oceanFloorFlat;
    private boolean debug;

    public BouncingTrace(DirectTrace father, final int MAX_BOUNCES, float speedOfSound, float maxRange) {
        this.MAX_BOUNCES = MAX_BOUNCES;
        this.SPEED_OF_SOUND = speedOfSound;
        this.MAX_RANGE = maxRange;
        floorBounceCounter = 0;
        surfaceBounceCounter = 0;
        oceanFloorFlat = true;
        this.father = father;
        debug = false;
    }

    public boolean init(AUV rootAUV, AUV targetAUV, Vector3f rootAUVPosition, Vector3f targetAUVPosition, Collider collider) {
        if (rootAUV == null || targetAUV == null || rootAUVPosition == null || targetAUVPosition == null) {
            return false;
        }
        this.rootAUV = rootAUV;
        this.targetAUV = targetAUV;
        this.rootAUVPosition = rootAUVPosition;
        this.targetAUVPosition = targetAUVPosition;
        this.collider = collider;
        return true;
    }

    public float calculateWaterDepth(Vector3f position) {
        Vector3f vector = new Vector3f(0f, -1f, 0f);

        Ray ray = new Ray(position, vector);
        CollisionResults colRes = new CollisionResults();
        collider.collideWith(ray, colRes);
        for (CollisionResult col : colRes) {
            if (col.getGeometry().getUserData("auv_name") == null) {
                //System.out.println("Position of my AUV " +rootAUVPosition+ " Name of my AUV " +rootAUV.getName() + " The water is "+ depthAtRootPos + " meters deep" );
                return col.getDistance();
            }
        }
        return 0f;
    }

    public synchronized DistanceTrigger nextBouncingRayTrace(final boolean surfaceFirst) {
        //If we are at the surface there is no direct surface reflection
        if (surfaceFirst && rootAUVPosition.y == 0f) {
            return null;
        }

        //Calculate the waterdepth at the positions of the AUVs
        float distance = 0;
        float depthAtRoot = calculateWaterDepth(rootAUVPosition);
        float depthAtTarget = calculateWaterDepth(targetAUVPosition);

        //calculate the overall waterDepth at the root
        float waterDepth = depthAtRoot - rootAUVPosition.y;
        boolean directionDown = !surfaceFirst;
        
        //we trace from the target to the rootAUV, check if last bounce is from surface or from ground
        if (MAX_BOUNCES % 2 == 1) {
            directionDown = surfaceFirst;
        } else {
            directionDown = !surfaceFirst;
        }

        //get the y-distance of the path
        float virtualHeight = calculateVirtualHeight(directionDown, depthAtRoot, depthAtTarget, waterDepth);
        if (virtualHeight == Float.MAX_VALUE) {
            return null;
        }
        //create a vector with our virtual position to get the direction of the vector.
        Vector3f virtualPosition = rootAUVPosition.clone();

        virtualPosition.setY(virtualHeight);
        //obtain the direction vector
        Vector3f direction = targetAUVPosition.subtract(virtualPosition);
        distance = direction.length();

        //reset direction value
        if (MAX_BOUNCES % 2 == 1) {
            directionDown = surfaceFirst;
        } else {
            directionDown = !surfaceFirst;
        }
        //Debug: main triangle
        if (debug) {
            if (distance > MAX_RANGE) {
                father.debugTrace(rootAUV.getName(), targetAUV.getName(), MAX_BOUNCES + "-" + surfaceFirst, direction.normalize(), virtualPosition);
            } else {
                father.debugTrace(rootAUV.getName(), targetAUV.getName(), MAX_BOUNCES + "-" + surfaceFirst, direction, virtualPosition);
            }
        }
        if (distance > MAX_RANGE) {
            return null;
        }
        //--------------------------------------------------------------------------//
        //--------------------------------------------------------------------------//
        //starting at the target start tracing the route
        int bounces = 0;
        //Setup the temporary targetposition for the trace Starting with targetAUVPosition, from there its always the last reflection point
        Vector3f virtualTarget = targetAUVPosition.clone();
        Vector3f normalizedDirection = direction.negate().normalize();

        if (directionDown) {
            float coeffizient = -virtualTarget.getY() / normalizedDirection.getY();
            Vector3f trace = normalizedDirection.mult(coeffizient);
            if (debug) {
                if (distance < MAX_RANGE) {
                    father.debugTrace(rootAUV.getName(), targetAUV.getName(), MAX_BOUNCES + "-" + surfaceFirst + "-" + directionDown + "-" + bounces, trace, virtualTarget.clone());
                }
            }
            rayTraceConnection(virtualTarget, normalizedDirection, trace.length());
            virtualTarget.addLocal(trace);
            virtualPosition.addLocal(0, targetAUVPosition.getY(), 0);
        } else {
            normalizedDirection.multLocal(1f, -1f, 1f);
            float coeffizient = Math.abs(depthAtTarget / normalizedDirection.getY());
            Vector3f trace = normalizedDirection.mult(coeffizient);
            if (debug) {
                if (distance < MAX_RANGE) {
                    father.debugTrace(rootAUV.getName(), targetAUV.getName(), MAX_BOUNCES + "-" + surfaceFirst + "-" + directionDown + "-" + bounces, trace, virtualTarget.clone());
                }
                rayTraceConnection(virtualTarget, normalizedDirection, trace.length());
            }
            virtualTarget.addLocal(trace);
            virtualPosition.addLocal(0f, -depthAtTarget, 0f);
        }

        normalizedDirection.multLocal(1f, -1f, 1f);
        directionDown = !directionDown;
        bounces++;
        while (bounces < MAX_BOUNCES) {
            float coeffizient = Math.abs(waterDepth / normalizedDirection.getY());
            Vector3f trace = normalizedDirection.mult(coeffizient);
            if (debug) {
                if (distance < MAX_RANGE) {
                    father.debugTrace(rootAUV.getName(), targetAUV.getName(), MAX_BOUNCES + "-" + surfaceFirst + "-" + directionDown + "-" + bounces, trace, virtualTarget.clone());
                }
            }
            rayTraceConnection(virtualTarget, normalizedDirection, trace.length());
            normalizedDirection.multLocal(1f, -1f, 1f);
            directionDown = !directionDown;
            virtualTarget.addLocal(trace);
            virtualPosition.addLocal(0f, -waterDepth, 0f);
            bounces++;
        }
        if (directionDown) {

            float coeffizient = depthAtRoot / normalizedDirection.getY();
            Vector3f trace = normalizedDirection.mult(coeffizient);
            if (debug) {
                if (distance < MAX_RANGE) {
                    father.debugTrace(rootAUV.getName(), targetAUV.getName(), MAX_BOUNCES + "-" + surfaceFirst + "-" + directionDown + "-" + bounces, trace, virtualTarget.clone());
                }
                rayTraceConnection(virtualTarget, normalizedDirection, trace.length());
            }
        } else {
            float coeffizient = rootAUVPosition.getY() / normalizedDirection.getY();
            Vector3f trace = normalizedDirection.mult(coeffizient);
            if (debug) {
                if (distance < MAX_RANGE) {
                    father.debugTrace(rootAUV.getName(), targetAUV.getName(), MAX_BOUNCES + "-" + surfaceFirst + "-" + directionDown + "-" + bounces, trace, virtualTarget.clone());
                }
                rayTraceConnection(virtualTarget, normalizedDirection, trace.length());
                virtualTarget.addLocal(trace);
                virtualPosition.addLocal(0f, rootAUVPosition.getY(), 0f);
            }
        }

        //create a distanceTrigger;
        DistanceTrigger returnTrigger = new DistanceTrigger(distance, targetAUV.getName(), surfaceBounceCounter, floorBounceCounter, SPEED_OF_SOUND, false);

        return returnTrigger;
    }

    public void toggleDebug() {
        debug = !debug;
    }

    private boolean rayTraceConnection(Vector3f position, Vector3f direction, float range) {
        Ray ray = new Ray(position, direction);
        CollisionResults results = new CollisionResults();
        collider.collideWith(ray, results);

        if (results.size() > 0) {
            for (CollisionResult res : results) {
                if (res.getDistance() < range) {
                    if (res.getGeometry().getUserData("auv_name") != null) {
                        System.out.println("Hit an AUV!");
                    }
                }
            }
        }
        return true;
    }

    private float calculateVirtualHeight(boolean directionDown, float depthAtRoot, float depthAtTarget, float waterDepth) {
        //reflection counter
        int bounces = 1;

        //the height, used with rootAUVPosition to calculate the angle and the distance
        float virtualHeight = 0f;
        
        if (directionDown) {
            if (targetAUVPosition.y == 0f) {
                return Float.MAX_VALUE;
            }
        } else {
            virtualHeight += depthAtTarget+targetAUVPosition.getY();
        }
                //For all further bounces until the last just add the waterdepth
        directionDown = !directionDown;
        while (bounces < MAX_BOUNCES) {
            virtualHeight += waterDepth;
            bounces++;
            directionDown = !directionDown;
        }
        
        //If we are starting with a downwards trace check if we are on the floor otherwise setup the height to the waterDepth
        if (directionDown) {
            virtualHeight += depthAtRoot;
        } else virtualHeight -= rootAUVPosition.getY();



        
        return virtualHeight;
    }

}
