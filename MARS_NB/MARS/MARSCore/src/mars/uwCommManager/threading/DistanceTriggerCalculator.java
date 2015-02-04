/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.threading;

import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.sensors.CommunicationDevice;
import mars.sensors.UnderwaterModem;
import mars.uwCommManager.helpers.DistanceTrigger;
import org.openide.util.Exceptions;

/**
 * This class is used to calculate distances between AUVS, right now it only calculates the direct way between two AUVs. Reflections are ignored and not implemented
 * @version 0.1
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
     * Does nothing but initialize variables
     * @since 0.1 
     */
    public DistanceTriggerCalculator() {
        distanceMap = new HashMap();
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
        return distanceMap;
    }

    private void calculatePathDistances() {
        System.out.println("updating distances");
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
                                            newDistanceTriggers.add(new DistanceTrigger(Math.abs(distance.length()), targetName));
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
    }
}
