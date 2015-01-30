
package mars.uwCommManager.graphics;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import mars.MARS_Main;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.sensors.Sensor;
import mars.sensors.UnderwaterModem;
import mars.uwCommManager.CommunicationState;

/**
 * @version 0.1
 * @author Jasper Schwinghammer
 */
public class CommunicationVisualizer {
    
    CommunicationState comState = null;
    MARS_Main app = null;
    AUV_Manager auvMngr = null;
    
    Map<String, AUVVisualizationNode> nodeMap = null;
    
    
    public CommunicationVisualizer(MARS_Main app, CommunicationState comState,AUV_Manager auvMngr) {
        this.app = app;
        this.comState= comState;
        this.auvMngr = auvMngr;
        
        nodeMap = new HashMap();
    }
    
    public boolean init() {
        
        for(Map.Entry<String, AUV> entry : auvMngr.getAUVs().entrySet()) {
            AUV auv = entry.getValue();
            ArrayList uws = auv.getSensorsOfClass(UnderwaterModem.class.getName());
            Iterator it = uws.iterator();
            while (it.hasNext()) {
                UnderwaterModem uw = (UnderwaterModem) it.next();
                //1. Erstelle AUVVisualisationNode
                //2. Füttere sie mit allen Informationen
                //3. adde sie zur Map
            }
        }
        return true;
    }
    
    public void update(float tpf) {
        
    }
    
}
