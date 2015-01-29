
package mars.uwCommManager.graphics;

import java.util.Map;
import java.util.HashMap;
import mars.MARS_Main;
import mars.auv.AUV_Manager;
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
    
}
