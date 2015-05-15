/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.threading.events;

/**
 *
 * @author Jasper Schwinghammer
 */
public class TriggerOutOfDistanceEvent extends ATriggerEvent {
    
    private final String sourceAUV;
    private final String targetAUV;

    public TriggerOutOfDistanceEvent(Object source, int EVENT_ID, final String sourceAUV,final String targetAUV) {
        super(source, EVENT_ID);
        this.sourceAUV = sourceAUV;
        this.targetAUV = targetAUV;
    }
    
    public String getSourceAUV() {
        return sourceAUV;
    }
    
    public String getTargetAUV() {
        return targetAUV;
    }
    
}
