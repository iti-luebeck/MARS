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
public class TraceBlockedEvent extends ATriggerEvent {
    
    
        
    private final String sourceAUVName;
    private final String targetAUVName;
    private final boolean surfaceFirst;
    private final List<Vector3f> traces;

    public TraceBlockedEvent(Object source,final int EVENT_ID, String sourceAUVName, String targetAUVName, List<Vector3f> traces,boolean surfaceFirst) {
        super(source, EVENT_ID);
        this.sourceAUVName = sourceAUVName;
        this.targetAUVName = targetAUVName;
        this.traces = traces;
        this.surfaceFirst = surfaceFirst;
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
    
    public boolean surfaceFirst() {
        return surfaceFirst;
    }
    
    
}
