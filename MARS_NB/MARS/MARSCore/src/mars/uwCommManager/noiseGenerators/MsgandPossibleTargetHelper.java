/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.noiseGenerators;

import java.util.HashMap;
import mars.auv.AUV;
import mars.sensors.CommunicationMessage;

/**
 * 
 * A helper class used to return the message as well as all suitable targets.
 * @version 0.1
 * @author Jasper Schwinghammer
 */
public class MsgandPossibleTargetHelper {
    /**
     * The message
     */
    private CommunicationMessage msg;
    /**
     * all possible targets
     */
    private HashMap<String,AUV> targets;
    
    /**
     * Initialization of the two private Objects
     * @param msg
     * @param targets 
     */
    public MsgandPossibleTargetHelper(CommunicationMessage msg, HashMap<String,AUV> targets) {
        this.msg = msg;
        this.targets = targets;
    }
    /**
     * 
     * Get the message object
     * @since 0.1
     * @return the message
     */
    public CommunicationMessage getMsg() {
        return msg;
    }
    
    /**
     * Get the HashMap containing all targetable AUV's
     * @since 0.1
     * @return 
     */
    public HashMap<String,AUV> getTargets() {
        return targets;
    }
}
