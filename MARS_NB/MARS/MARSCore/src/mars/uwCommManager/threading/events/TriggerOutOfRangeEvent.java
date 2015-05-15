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
public class TriggerOutOfRangeEvent extends ATriggerEvent {
    
    private final String sourceAUVName;
    private final String targetAUVName;

    public TriggerOutOfRangeEvent(Object source, int EVENT_ID, final String sourceAUVName,final String targetAUVName) {
        super(source, EVENT_ID);
        this.sourceAUVName = sourceAUVName;
        this.targetAUVName = targetAUVName;
    }
    
    public String getSourceAUVName() {
        return sourceAUVName;
    }
    
    public String getTargetAUVName() {
        return targetAUVName;
    }
    
}
