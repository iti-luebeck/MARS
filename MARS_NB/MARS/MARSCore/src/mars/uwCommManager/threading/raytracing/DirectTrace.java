/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.threading.raytracing;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.sensors.CommunicationDevice;
import mars.states.SimState;
import mars.uwCommManager.helpers.DistanceTrigger;
import mars.uwCommManager.threading.DistanceTriggerCalculator;
import mars.uwCommManager.threading.events.TriggerEventGenerator;

/**
 * This class traces the direction connections between two AUVs
 *
 * @version 0.1
 * @author Jasper Schwinghammer
 */
public class DirectTrace implements Runnable {

    private SimState simState;
    private AUV_Manager auvManager;
    private String auvName;
    private List<DistanceTrigger> distanceTriggers;
    private DistanceTriggerCalculator triggerCalc;

    private boolean debug;
    private Node debugNode;
    private final int MAX_REFLECTION;
    private final float SPEED_OF_SOUND;
    private final float MAX_DISTANCE;

    /**
     * Initialize the basic stuff
     *
     * @since 0.1
     * @param triggerCalc
     */
    public DirectTrace(DistanceTriggerCalculator triggerCalc, int maxReflectionCount, float speedOfSound, float MAX_DISTANCE) {
        simState = null;
        auvManager = null;
        distanceTriggers = null;
        auvName = null;
        this.triggerCalc = triggerCalc;
        this.MAX_REFLECTION = maxReflectionCount;
        this.SPEED_OF_SOUND = speedOfSound;
        this.MAX_DISTANCE = MAX_DISTANCE;
    }

    /**
     * Initialize everything that is not trivial and check if nothing has gone
     * wrong during initialization.
     *
     * @since 0.1
     * @param simState
     * @param auvManager
     * @param auvName
     * @param distanceTriggers
     * @return
     */
    public boolean init(SimState simState, AUV_Manager auvManager, String auvName, List<DistanceTrigger> distanceTriggers, boolean debug, Node debugNode) {
        if (simState == null || auvManager == null || auvName == null || distanceTriggers == null) {
            return false;
        }
        this.simState = simState;
        this.auvManager = auvManager;
        this.distanceTriggers = distanceTriggers;
        this.auvName = auvName;
        this.distanceTriggers = distanceTriggers;
        this.debug = debug;
        this.debugNode = debugNode;

        return true;
    }

    /**
     * Run the ray-trace
     *
     * @since 0.1
     */
    @Override
    public void run() {
        rayTraceConnections();
    }

    /**
     * This method will trace all direct connections between the current AUV and
     * all other AUVs that posses a modem and are in range. If a connection is
     * found, a event is triggered and the graphics system notified, if the path
     * is blocked another event is triggered that notifies the graphics system.
     *
     * @since 0.1
     */
    private void rayTraceConnections() {
        //The node holding all simulation objects
        if (simState == null) {
            return;
        }
        //get all auvs to retrieve their position later on
        HashMap<String, AUV> auvs = auvManager.getAUVs();
        List<DistanceTrigger> tracedTriggers = new LinkedList();
        //For every AUV that has any other auvs in range
        //First step:
        //Start a ray directly to each of the AUV's find out what it hits.
        String rootAUVName = auvName;
        AUV rootAUV = auvs.get(rootAUVName);
        List<DistanceTrigger> removedTriggers = new LinkedList<DistanceTrigger>();
        //For every auv in range
        for (DistanceTrigger trigger : distanceTriggers) {
            //retrieve the AUV object
            String targetAUVName = trigger.getAUVName();
            AUV targetAUV = auvs.get(targetAUVName);

            //Get the CommunicationSensors
            ArrayList uwmo = rootAUV.getSensorsOfClass(CommunicationDevice.class.getName());
            ArrayList uwmoTarget = targetAUV.getSensorsOfClass(CommunicationDevice.class.getName());
            Iterator it = uwmo.iterator();
            //For each sensor of the sending AUV
            while (it.hasNext()) {
                //get its position
                CommunicationDevice mod = (CommunicationDevice) it.next();
                Vector3f modPos = mod.getWorldPosition();
                //for each sensor of the recieving auv
                Iterator itTargetMo = uwmoTarget.iterator();

                while (itTargetMo.hasNext()) {
                    List<Vector3f> traceList = new LinkedList<Vector3f>();
                    traceList.add(new Vector3f(0, 0, 0));
                    //get its position
                    CommunicationDevice targetMod = (CommunicationDevice) itTargetMo.next();
                    Vector3f targetModPos = targetMod.getWorldPosition();

                    //calculate the vector between the two modems
                    Vector3f direction = targetModPos.subtract(modPos);;
                    /////////////////////////////START DEBUG CODE
                    if (debug) {
                        debugTrace(rootAUVName, targetAUVName, "direct", direction, modPos);
                    }
                    ///////////////////////////////END DEBUG CODE
                    //raytrace the connection
                    Ray ray = new Ray(modPos, direction.normalize());
                    CollisionResults results = new CollisionResults();

                    //rootNode.collideWith(ray, results);
                    simState.getCollider().collideWith(ray, results);
                    if (results.size() == 0) {
                        traceList.add(direction);
                        //System.out.println("Trigger hit!" + rootAUVName + " to " + targetAUVName + " distance: " +results.getClosestCollision().getDistance()+ " direction:" + direction);
                        triggerCalc.getEventGenerator().fireNewTraceHitAUVEvent(this, rootAUVName, targetAUVName, traceList, true, false);
                    } else {
                        for (CollisionResult res : results) {
                            if (res.getDistance() > direction.length()) {
                                traceList.add(direction);
                                //System.out.println("Trigger hit!" + rootAUVName + " to " + targetAUVName + " distance: " +results.getClosestCollision().getDistance()+ " direction:" + direction);
                                triggerCalc.getEventGenerator().fireNewTraceHitAUVEvent(this, rootAUVName, targetAUVName, traceList, true, false);
                                break;
                            } else if (res.getGeometry().getUserData("auv_name") == null) {
                                removedTriggers.add(trigger);
                                traceList.add(direction.normalize().mult(results.getClosestCollision().getDistance()));
                                //System.out.println("Trigger failed!" + rootAUVName + " to " + targetAUVName + " distance: " +results.getClosestCollision().getDistance()+ " direction:" + direction);
                                triggerCalc.getEventGenerator().fireNewTraBlockedEvent(this, rootAUVName, targetAUVName, traceList, true);
                                break;
                            } else {
                                trigger.hitAUV();
                                removedTriggers.add(trigger);
                                traceList.add(direction);
                                //System.out.println("Trigger failed!" + rootAUVName + " to " + targetAUVName + " distance: " +results.getClosestCollision().getDistance()+ " direction:" + direction);
                                triggerCalc.getEventGenerator().fireNewTraceHitAUVEvent(this, rootAUVName, targetAUVName, traceList, true, true);
                                break;
                            }
                        }
                    }
                    //if(auvName.equals("laura")) {
                    for (int i = 1; i < 10; i++) {
                        BouncingTrace bTrace = new BouncingTrace(this, i, SPEED_OF_SOUND, MAX_DISTANCE,debug);
                        bTrace.init(rootAUV, targetAUV, modPos, targetModPos, simState.getCollider());
                        boolean surfaceFirst = true;
                        DistanceTrigger tempTrigger = bTrace.nextBouncingRayTrace(surfaceFirst);
                        DistanceTrigger tempTrigger2 = bTrace.nextBouncingRayTrace(!surfaceFirst);
                        if(tempTrigger != null) tracedTriggers.add(tempTrigger);
                        if(tempTrigger2 != null) tracedTriggers.add(tempTrigger2);
                    }

                    //}
                    //System.out.println(rootAUVName + ": " +results.getClosestCollision().getDistance() + " ;; " + (direction.length()-1) + " " + results.getClosestCollision().getGeometry().getName());
                }
            }
            //System.out.println(rootAUVName+ ": To be removed Triggers; " + removedTriggers.toString());
        }
        distanceTriggers.removeAll(removedTriggers);
        distanceTriggers.addAll(tracedTriggers);
        triggerCalc.updateTraceMap(auvName, distanceTriggers);
    }
    
    
    public TriggerEventGenerator getTriggerEventGenerator() {
        return triggerCalc.getEventGenerator();
    }

    /**
     * This method is used for debugging purposes with the raytracing.
     *
     * @param rootAUVName
     * @param targetAUVName
     * @param direction
     * @param modPos
     */
    void debugTrace(String rootAUVName, String targetAUVName, String suffix, final Vector3f direction, final Vector3f modPos) {
        String debugName = rootAUVName + "-" + targetAUVName + "-" + suffix;
        if (debugNode.getChild(debugName) == null && !triggerCalc.debugAUVRouteInInit(debugName)) {
            triggerCalc.setDebugAUVRoute(debugName);
            Arrow arrow = new Arrow(direction);
            final Geometry geom = new Geometry(debugName, arrow);
            Material mat = new Material(simState.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
            geom.setMaterial(mat);
            geom.setLocalTranslation(modPos);
            geom.setCullHint(Spatial.CullHint.Inherit);
            simState.getMARS().enqueue(new Callable<Object>() {
                @Override
                public Object call() {
                    debugNode.attachChild(geom);
                    return null;
                }
            });
        } else if (!(debugNode.getChild(debugName) == null)) {
            final Geometry geom = (Geometry) debugNode.getChild(debugName);
            final Vector3f debugDirections = direction.clone();
            final Vector3f debugModPos = modPos.clone();
            simState.getMARS().enqueue(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    geom.setLocalTranslation(debugModPos);
                    ((Arrow) geom.getMesh()).setArrowExtent(debugDirections);
                    return null;
                }
            });
        }
    }

}