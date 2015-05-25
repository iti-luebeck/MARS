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
        debug = true;
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

        List<Vector3f> traceList = new LinkedList();
        traceList.add(targetAUVPosition);

        if (surfaceFirst && rootAUVPosition.y == 0f) {
            return null;
        }
        float distance = 0;
        float depthAtRoot = calculateWaterDepth(rootAUVPosition);
        float depthAtTarget = calculateWaterDepth(targetAUVPosition);

        float waterDepth = depthAtRoot - rootAUVPosition.y;

        int bounces = 1;
        boolean directionDown = !surfaceFirst;
        float virtualHeight = 0f;
        if (directionDown) {
            virtualHeight = waterDepth;
            if (virtualHeight == 0f) {
                return null;
            }
        } 

        directionDown = !directionDown;
        while (bounces < MAX_BOUNCES) {
            virtualHeight += waterDepth;
            bounces++;
            directionDown = !directionDown;
        }

        if (directionDown) {
            if (targetAUVPosition.y == 0f) {
                return null;
            }
            virtualHeight -= rootAUVPosition.getY();
        } else {
            virtualHeight = -(virtualHeight + depthAtRoot);
        }

        //create a vector with our virtual position to get the direction of the vector.
        Vector3f virtualPosition = rootAUVPosition.clone();
        virtualPosition.setY(virtualHeight);
        //obtain the direction vector
        Vector3f direction = targetAUVPosition.subtract(virtualPosition);
        distance = direction.length();

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
        Vector3f virtualtarget = targetAUVPosition.clone();
        Vector3f normalizedDirection = direction.negate().normalize();

        if (directionDown) {
            float coeffizient = -virtualtarget.getY()/ normalizedDirection.getY();
            Vector3f trace = normalizedDirection.mult(coeffizient);
            if (debug) {
                if (distance > MAX_RANGE) {
                    father.debugTrace(rootAUV.getName(), targetAUV.getName(), MAX_BOUNCES + "-"+surfaceFirst +"-" + directionDown + "-0", trace, virtualtarget.clone());
                } else {
                    father.debugTrace(rootAUV.getName(), targetAUV.getName(), MAX_BOUNCES + "-"+surfaceFirst +"-" + directionDown + "-0", trace, virtualtarget.clone());
                }
            }
            rayTraceConnection(virtualtarget, normalizedDirection, trace.length());
            virtualtarget.addLocal(trace);
            virtualPosition.addLocal(0, virtualtarget.getY() - waterDepth, 0);
        } else {
            float coeffizient = -depthAtTarget / normalizedDirection.getY();
            Vector3f trace = normalizedDirection.mult(coeffizient);
            if (debug) {
                if (distance > MAX_RANGE) {
                    father.debugTrace(rootAUV.getName(), targetAUV.getName(), MAX_BOUNCES + "-"+surfaceFirst +"-" + directionDown + "-0", trace, virtualtarget.clone());
                } else {
                    father.debugTrace(rootAUV.getName(), targetAUV.getName(), MAX_BOUNCES + "-" +surfaceFirst +"-"+ directionDown + "-0", trace, virtualtarget.clone());
                }
                rayTraceConnection(virtualtarget, normalizedDirection, trace.length());
            }
            virtualtarget.addLocal(trace);
            virtualPosition.addLocal(0f, depthAtTarget + waterDepth, 0f);
        }
        traceList.add(virtualtarget.clone());

        normalizedDirection.multLocal(1f, -1f, 1f);
        directionDown = !directionDown;

        while (Math.abs(virtualPosition.getY()) > waterDepth) {
            System.out.println("wups" + virtualPosition);
            //TODO RAYTRACE IT
            break;
        }

        if (directionDown) {
            float coeffizient = depthAtRoot / normalizedDirection.getY();
            Vector3f trace = normalizedDirection.mult(coeffizient);
            if (debug) {
                if (distance > MAX_RANGE) {
                    father.debugTrace(rootAUV.getName(), targetAUV.getName(), MAX_BOUNCES + "-" +surfaceFirst +"-"+ directionDown + "-0", trace, virtualtarget.clone());
                } else {
                    father.debugTrace(rootAUV.getName(), targetAUV.getName(), MAX_BOUNCES + "-" +surfaceFirst +"-"+ directionDown + "-0", trace, virtualtarget.clone());
                }
                rayTraceConnection(virtualtarget, normalizedDirection, trace.length());
            }
        } else {
            float coeffizient = rootAUVPosition.getY() / normalizedDirection.getY();
            Vector3f trace = normalizedDirection.mult(coeffizient);
            if (debug) {
                if (distance > MAX_RANGE) {
                    father.debugTrace(rootAUV.getName(), targetAUV.getName(), MAX_BOUNCES + "-" +surfaceFirst +"-"+ directionDown + "-0", trace, virtualtarget.clone());
                } else {
                    father.debugTrace(rootAUV.getName(), targetAUV.getName(), MAX_BOUNCES + "-" +surfaceFirst +"-"+ directionDown + "-0", trace, virtualtarget.clone());
                }
                rayTraceConnection(virtualtarget, normalizedDirection, trace.length());
            virtualtarget.addLocal(trace);
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

}
