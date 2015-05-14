
package mars.uwCommManager.graphics;

import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import mars.MARS_Main;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.sensors.UnderwaterModem;
import mars.uwCommManager.CommunicationState;
import mars.uwCommManager.threading.DistanceTriggerCalculator;

/**
 * @version 0.1
 * @author Jasper Schwinghammer
 */
public class CommunicationVisualizer {
    
    CommunicationState comState = null;
    MARS_Main app = null;
    AUV_Manager auvMngr = null;
    
    Map<String, AUVVisualizationNode> nodeMap = null;
    
    private DistanceTriggerCalculator triggerCalc;
    
    
    public CommunicationVisualizer(MARS_Main app, CommunicationState comState,AUV_Manager auvMngr,DistanceTriggerCalculator triggerCalc) {
        this.app = app;
        this.comState= comState;
        this.auvMngr = auvMngr;
        this.triggerCalc = triggerCalc;
        nodeMap = new HashMap();
    }
    
    public boolean init() {
        
        for(Map.Entry<String, AUV> entry : auvMngr.getAUVs().entrySet()) {
            AUV auv = entry.getValue();
            ArrayList uws = auv.getSensorsOfClass(UnderwaterModem.class.getName());
            Iterator it = uws.iterator();
            while (it.hasNext()) {
                UnderwaterModem uw = (UnderwaterModem) it.next();
                Node modemNode = (Node)uw.getAuv().getAUVNode().getChild("modem");
                AUVVisualizationNode modemVisNode = new AUVVisualizationNode(auv, modemNode);
                modemVisNode.init();
                nodeMap.put(uw.getAuv().getName(), modemVisNode);
                triggerCalc.getEventGenerator().addListener(modemVisNode);
                //1. Erstelle AUVVisualisationNode
                //2. Füttere sie mit allen Informationen
                //3. adde sie zur Map
            }
        }
        return true;
    }
    
    public void update(float tpf) {
        
        
        for(Map.Entry<String, AUVVisualizationNode> e : nodeMap.entrySet()) {
            e.getValue().update(tpf);
        }
    }
    
}
