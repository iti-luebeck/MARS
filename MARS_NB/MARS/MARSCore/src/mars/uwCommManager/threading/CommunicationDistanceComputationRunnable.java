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

/**
 *
 * @author Jasper Schwinghammer
 */
public class CommunicationDistanceComputationRunnable implements Runnable {
    
    private AUV_Manager auvManager = null;
    
    
    private Map<String,List<DistanceTrigger>> distanceMap;
    
    
    public CommunicationDistanceComputationRunnable() {
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

    @Override
    public void run() {
        HashMap<String,AUV> auvs = auvManager.getAUVs();
        HashMap<String,AUV> targets = auvManager.getAUVs();
        
        
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            
            
            if(auv.getAuv_param().isEnabled() && auv.hasSensorsOfClass(CommunicationDevice.class.getName())){
                List<DistanceTrigger> newDistanceTriggers = new LinkedList();
                
                
                for( String targetName : targets.keySet()) {
                    AUV targetAUV = (AUV)targets.get(targetName);
                    
                    if(targetAUV.getAuv_param().isEnabled() && targetAUV.hasSensorsOfClass(CommunicationDevice.class.getName()) && !targetName.equals(elem)){
                        ArrayList uwmo = auv.getSensorsOfClass(CommunicationDevice.class.getName());
                        ArrayList uwmoTarget = targetAUV.getSensorsOfClass(CommunicationDevice.class.getName());
                        Iterator it = uwmo.iterator();
                        
                        
                        while(it.hasNext()){
                        CommunicationDevice mod = (CommunicationDevice)it.next();
                        
                        
                            if(mod instanceof UnderwaterModem) {
                                Vector3f modPos = mod.getWorldPosition();
                                
                                Iterator itTargetMo = uwmoTarget.iterator();
                                
                                
                                while(itTargetMo.hasNext()) {
                                    CommunicationDevice modTarget = (CommunicationDevice)itTargetMo.next();
                                    
                                    
                                    if(modTarget instanceof UnderwaterModem) {
                                        Vector3f modTargetPos = modTarget.getWorldPosition();
                                        Vector3f distance = modTargetPos.subtract(modPos);
                                        
                                        
                                        if(Math.abs(distance.length())<=mod.getPropagationDistance()) {
                                            newDistanceTriggers.add(new DistanceTrigger(Math.abs(distance.length()), targetName));
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
                synchronized(this) {
                    if(!distanceMap.containsKey(elem)) distanceMap.put(elem, new LinkedList<DistanceTrigger>());
                    distanceMap.get(elem).clear();
                    distanceMap.get(elem).addAll(newDistanceTriggers);
                }
            }
        }
    }
    
    public synchronized Map<String,List<DistanceTrigger>> getDistanceTriggerMap() {
        return distanceMap;
    }
}
