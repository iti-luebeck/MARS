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
 * @author jaspe_000
 */
public class MsgandPossibleTargetHelper {
    
    private CommunicationMessage msg;
    private HashMap<String,AUV> targets;
    
    
    public MsgandPossibleTargetHelper(CommunicationMessage msg, HashMap<String,AUV> targets) {
        this.msg = msg;
        this.targets = targets;
    }
    
    public CommunicationMessage getMsg() {
        return msg;
    }
    
    public HashMap<String,AUV> getTargets() {
        return targets;
    }
}
