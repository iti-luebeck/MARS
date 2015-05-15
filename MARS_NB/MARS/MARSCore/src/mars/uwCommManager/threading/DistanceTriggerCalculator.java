/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.threading;

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
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.core.CentralLookup;
import mars.sensors.CommunicationDevice;
import mars.sensors.UnderwaterModem;
import mars.states.SimState;
import mars.uwCommManager.helpers.DistanceTrigger;
import mars.uwCommManager.threading.events.TriggerEventGenerator;
import mars.uwCommManager.threading.raytracing.DirectTrace;
import org.openide.util.Exceptions;

/**
 * This class is used to calculate distances between AUVS, right now it only calculates the direct way between two AUVs. Reflections are ignored and not implemented
 * @version 1.1
 * @author Jasper Schwinghammer
 */
public class DistanceTriggerCalculator implements Runnable {
    
    /**
     * the AUV Manager
     */
    private AUV_Manager auvManager = null;
    
    /**
     * the map that stores as Key a AUV name and as value the distances to all other AUVs
     */
    private Map<String,List<DistanceTrigger>> distanceMap;
    
    /**
     * the same as distanceMap, but has all triggers that are unavailable due to blocked connections removed
     */
    private Map<String,List<DistanceTrigger>> distanceMapWithTraces;
    
    private ScheduledThreadPoolExecutor executor;
    
    private final SimState simState;
    
    private TriggerEventGenerator eventGen;
    
    /**
     * Does nothing but initialize variables
     * @since 0.1 
     */
    public DistanceTriggerCalculator(SimState simState,ScheduledThreadPoolExecutor executor) {
        distanceMap = new HashMap();
        distanceMapWithTraces = new HashMap();
        this.simState = simState;
        this.executor = executor;
        eventGen = new TriggerEventGenerator();
    }
    
    /**
     * @since 0.1
     * Init all nontrivial stuff
     * @param auvManager the AUV_Manager
     * @return if all initialization worked 
     */
    public boolean init(final AUV_Manager auvManager) {
        if (auvManager == null) return false;
        this.auvManager = auvManager;
        return true;
    }

    /**
     * calculate all ways to other AUVs
     * @since 0.1
     */
    @Override
    public void run() {
        
        try {
            calculatePathDistances();
            
        } catch(Exception e) {
            Exceptions.printStackTrace(e);
        }
       
    }
    
    /**
     * get all current Triggers
     * @return the distances from all AUVs with modems to other AUVs with modems
     */
    public synchronized Map<String,List<DistanceTrigger>> getDistanceTriggerMap() {
        return new HashMap<String,List<DistanceTrigger>>(distanceMapWithTraces);
    }

    private void calculatePathDistances() {
        float temperature = ((SimState)CentralLookup.getDefault().lookup(SimState.class)).getMARSSettings().getPhysical_environment().getFluid_temp();
        //System.out.println("updating distances");
         //We calculate every AUV to every AUV O(n^2)
        HashMap<String,AUV> auvs = auvManager.getAUVs();
        HashMap<String,AUV> targets = auvManager.getAUVs();
        
        //For every AUV #1
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            
            //Check if it has any communication capabilitys
            if(auv.getAuv_param().isEnabled() && auv.hasSensorsOfClass(CommunicationDevice.class.getName())){
                List<DistanceTrigger> newDistanceTriggers = new LinkedList();
                
                //For every AUV #2
                for( String targetName : targets.keySet()) {
                    AUV targetAUV = (AUV)targets.get(targetName);
                    //Check if the AUV has any communication capabilitys and if it is not our current AUV #1
                    if(targetAUV.getAuv_param().isEnabled() && targetAUV.hasSensorsOfClass(CommunicationDevice.class.getName()) && !targetName.equals(elem)){
                        ArrayList uwmo = auv.getSensorsOfClass(CommunicationDevice.class.getName());
                        ArrayList uwmoTarget = targetAUV.getSensorsOfClass(CommunicationDevice.class.getName());
                        Iterator it = uwmo.iterator();
                        
                        //For every commucation device in #1
                        while(it.hasNext()){
                        CommunicationDevice mod = (CommunicationDevice)it.next();
                        
                            //Check if it is a modem
                            if(mod instanceof UnderwaterModem) {
                                Vector3f modPos = mod.getWorldPosition();
                                
                                Iterator itTargetMo = uwmoTarget.iterator();
                                
                                //For every commucation device in #2
                                while(itTargetMo.hasNext()) {
                                    CommunicationDevice modTarget = (CommunicationDevice)itTargetMo.next();
                                    
                                    //check if its a modem
                                    if(modTarget instanceof UnderwaterModem) {
                                        Vector3f modTargetPos = modTarget.getWorldPosition();
                                        Vector3f distance = modTargetPos.subtract(modPos);
                                        
                                        //Check distance, if close enough add to the triggermap
                                        if(Math.abs(distance.length())<=mod.getPropagationDistance()) {
                                            newDistanceTriggers.add(new DistanceTrigger(Math.abs(distance.length()), targetName,temperature));
                                        } else {
                                            //FIRE NEW EVENT THAT THEY ARE OUT OF RANGE
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
                //only for synchronization purpose we need this to update the Triggermap
                synchronized(this) {
                    if(!distanceMap.containsKey(elem)) distanceMap.put(elem, new LinkedList<DistanceTrigger>());
                    distanceMap.get(elem).clear();
                    distanceMap.get(elem).addAll(newDistanceTriggers);
                }
            }
        }
        for(Map.Entry<String,List<DistanceTrigger>> e : distanceMap.entrySet()) {
            DirectTrace trace = new DirectTrace(this);
            trace.init(simState, auvManager, e.getKey(), e.getValue());
            executor.schedule(trace, 0, TimeUnit.MICROSECONDS);
        }
    }
    
    public void updateTraceMap(String auvName, List<DistanceTrigger> triggers) {
        
        synchronized(this) {
            if(distanceMapWithTraces.containsKey(auvName)) {
                distanceMapWithTraces.remove(auvName);
                distanceMapWithTraces.put(auvName, triggers);
            } else {
                distanceMapWithTraces.put(auvName, triggers);
            }
        }

    }
    
    
    public TriggerEventGenerator getEventGenerator() {
        return eventGen;
    }
   
    
    
}
