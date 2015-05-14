/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.threading.events;

import com.jme3.math.Vector3f;
import java.util.List;

/**
 *
 * @author jaspe_000
 */
public class TraceHitAUVEvent extends ATriggerEvent{
    
    
    private String sourceAUVName;
    private String targetAUVName;
    
    private List<Vector3f> traces;

    public TraceHitAUVEvent(Object source,final int EVENT_ID, String sourceAUVName, String targetAUVName, List<Vector3f> traces) {
        super(source, EVENT_ID);
        this.sourceAUVName = sourceAUVName;
        this.targetAUVName = targetAUVName;
        this.traces = traces;
    }
    
    
    public String getSourceAUVName() {
        return sourceAUVName;
    }
    
    public String getTargetAUVName() {
        return targetAUVName;
    }
    
    public List<Vector3f> getTraces() {
        return traces;
    }
    
    
    
}
