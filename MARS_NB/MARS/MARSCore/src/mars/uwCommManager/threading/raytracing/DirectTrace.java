/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.threading.raytracing;

import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.sensors.CommunicationDevice;
import mars.states.SimState;
import mars.uwCommManager.helpers.DistanceTrigger;

/**
 *
 * @author jaspe_000
 */
public class DirectTrace implements Runnable{
    
    private SimState simState;
    private AUV_Manager auvManager;
    private String auvName;
    private List<DistanceTrigger> distanceTriggers;
    
    public DirectTrace() {
        simState = null;
        auvManager = null;
        distanceTriggers = null;
        auvName = null;
    }
    
    public boolean init(SimState simState, AUV_Manager auvManager,String auvName, List<DistanceTrigger> distanceTriggers ) {
        if(simState == null || auvManager == null || auvName == null || distanceTriggers == null) return false;
        this.simState = simState;
        this.auvManager = auvManager;
        this.distanceTriggers = distanceTriggers;
        this.auvName = auvName;
        this.distanceTriggers = distanceTriggers;
        return true;
    }

    @Override
    public void run() {
        rayTraceConnections();
    }
    
    
     private void rayTraceConnections() {
        //The node holding all simulation objects
        if(simState == null) return;
        Node rootNode = simState.getSceneReflectionNode();
        
        //get all auvs to retrieve their position later on
        HashMap<String,AUV> auvs = auvManager.getAUVs();
        //For every AUV that has any other auvs in range
        //First step:
        //Start a ray directly to each of the AUV's find out what it hits.
        String rootAUVName = auvName;
        AUV rootAUV = auvs.get(rootAUVName);
        List<DistanceTrigger> removedTriggers = new LinkedList<DistanceTrigger>();
        //For every auv in range
        for(DistanceTrigger trigger : distanceTriggers) {
            //retrieve the AUV object
            String targetAUVName = trigger.getAUVName();
            AUV targetAUV = auvs.get(targetAUVName);

            //Get the CommunicationSensors
            ArrayList uwmo = rootAUV.getSensorsOfClass(CommunicationDevice.class.getName());
            ArrayList uwmoTarget = targetAUV.getSensorsOfClass(CommunicationDevice.class.getName());
            Iterator it = uwmo.iterator();
            //For each sensor of the sending AUV
            while(it.hasNext()){
                //get its position
                CommunicationDevice mod = (CommunicationDevice)it.next();
                Vector3f modPos = mod.getWorldPosition();
                //for each sensor of the recieving auv
                Iterator itTargetMo = uwmoTarget.iterator();

                while (itTargetMo.hasNext()) {
                    //get its position
                    CommunicationDevice targetMod = (CommunicationDevice)itTargetMo.next();
                    Vector3f targetModPos = targetMod.getWorldPosition();

                    //calculate the vector between the two modems
                    Vector3f direction = targetModPos.subtract(modPos);

                    //raytrace the connection
                    Ray ray = new Ray(modPos.add(direction.normalize()),direction);

                    CollisionResults results = new CollisionResults();

                    rootNode.collideWith(ray, results);
                    if (results.size() != 0) {
                        if(results.getClosestCollision().getDistance() < direction.mult(0.9f).length()-1) {
                            removedTriggers.add(trigger);
                        }

                       //System.out.println(rootAUVName + ": " +results.getClosestCollision().getDistance() + " ;; " + (direction.length()-1) + " " + results.getClosestCollision().getGeometry().getName());
                    }
                }
            }
            //System.out.println(rootAUVName+ ": To be removed Triggers; " + removedTriggers.toString());
            distanceTriggers.removeAll(removedTriggers);

        }
    }
}
