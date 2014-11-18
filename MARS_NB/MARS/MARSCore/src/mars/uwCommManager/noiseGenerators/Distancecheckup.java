
package mars.uwCommManager.noiseGenerators;

import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import mars.CommunicationType;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.sensors.CommunicationDevice;
import mars.sensors.CommunicationMessage;
import mars.sensors.UnderwaterModem;
import mars.sensors.WiFi;

/**
 * Just a basic filter for the distance. We only need to test for AUV's that are in range of the modem
 * 
 * @version 0.1
 * @author Jasper Schwinghammer
 */
public class Distancecheckup{
    
    AUV_Manager auvManager = null;

    public Distancecheckup() {
 
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
     * 
     * Checks for a CommuncationMessage if any AUV's are in range to recieve the message.
     * 
     * @since 0.1
     * @param msg the CommunicationsMessage containing the sender reference
     * @return the message and alle AUV's that are possibly in range of the message
     */
    public MsgandPossibleTargetHelper checkDistanceForMessage(final CommunicationMessage msg) {
        AUV sender = (AUV)auvManager.getAUV(msg.getAuvName());
        
        
        CommunicationDevice senderUW;
        
        
        Vector3f senderUWPos;
        
        if(msg.getCommunicationType() == CommunicationType.UNDERWATERSOUND){
            ArrayList sender_uwmo = sender.getSensorsOfClass(UnderwaterModem.class.getName());
            senderUW = (UnderwaterModem)sender_uwmo.get(0);
            senderUWPos = senderUW.getWorldPosition();
        }else if(msg.getCommunicationType() == CommunicationType.WIFI){
            ArrayList sender_uwmo = sender.getSensorsOfClass(WiFi.class.getName());
            senderUW = (WiFi)sender_uwmo.get(0);
            senderUWPos = senderUW.getWorldPosition();
        }else{//no type -> error
            return null;
        }
        HashMap<String,AUV> auvs = auvManager.getAUVs();
        HashMap<String,AUV> targets = new HashMap<String,AUV>();
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            //Code copy-pasted from the old method by Thomas Tosik
            if(auv.getAuv_param().isEnabled() && auv.hasSensorsOfClass(CommunicationDevice.class.getName()) && !auv.getName().equals(msg.getAuvName())){
                ArrayList uwmo = auv.getSensorsOfClass(CommunicationDevice.class.getName());
                Iterator it = uwmo.iterator();
                while(it.hasNext()){
                    CommunicationDevice mod = (CommunicationDevice)it.next();
                    Vector3f modPos = mod.getWorldPosition();
                    Vector3f distance = modPos.subtract(senderUWPos);
                    
                    System.out.println("Sender position: " + senderUWPos.toString() + "\n reciever position: " + modPos.toString() + " \n distance: " + distance.toString());
                    if(msg.getCommunicationType() == CommunicationType.UNDERWATERSOUND && mod instanceof UnderwaterModem){//check the communications ways (underwater, overwater)
                        if( Math.abs(distance.length()) <= senderUW.getPropagationDistance() ){//check if other underwatermodem isn't too far away
                            //if()//check if the receiver is also underwater
                            targets.put(mod.getAuv().getName(),mod.getAuv());
                        }
                    }else if(msg.getCommunicationType() == CommunicationType.WIFI && mod instanceof WiFi){
                        if( Math.abs(distance.length()) <= senderUW.getPropagationDistance() ){//check if other underwatermodem isn't too far away
                            //if()//check if the receiver is also overwater
                            targets.put(mod.getAuv().getName(),mod.getAuv());
                        }
                    }
                }        
            } 
        }
        return new MsgandPossibleTargetHelper(msg, targets);
    }
    
    
}
