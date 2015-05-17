
package mars.uwCommManager.graphics;

import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import mars.MARS_Main;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.sensors.UnderwaterModem;
import mars.uwCommManager.CommunicationState;
import mars.uwCommManager.options.CommOptionsConstants;
import mars.uwCommManager.threading.DistanceTriggerCalculator;

/**
 * This class pure purpose is to manage the visualization system. Each AUV gets its own sub nodes for visualization purpose
 * @version 0.1
 * @author Jasper Schwinghammer
 */
public class CommunicationVisualizer {
    
    CommunicationState comState = null;
    MARS_Main app = null;
    AUV_Manager auvMngr = null;
    
    Map<String, AUVVisualizationNode> nodeMap = null;
    
    private DistanceTriggerCalculator triggerCalc;
    
    /**
     * @since 0.1
     * just init basic stuff
     * @param app
     * @param comState
     * @param auvMngr
     * @param triggerCalc 
     */
    public CommunicationVisualizer(MARS_Main app, CommunicationState comState,AUV_Manager auvMngr,DistanceTriggerCalculator triggerCalc) {
        this.app = app;
        this.comState= comState;
        this.auvMngr = auvMngr;
        this.triggerCalc = triggerCalc;
        nodeMap = new HashMap();
    }
    
    /**
     * Check if everything is set up properly and set up nontrivial stuff
     * @return if everyhting is set up properly
     */
    public boolean init() {
        if(app == null || comState == null ||auvMngr == null ||triggerCalc == null) return false; 
        for(Map.Entry<String, AUV> entry : auvMngr.getAUVs().entrySet()) {
            AUV auv = entry.getValue();
            ArrayList uws = auv.getSensorsOfClass(UnderwaterModem.class.getName());
            Iterator it = uws.iterator();
            while (it.hasNext()) {
                UnderwaterModem uw = (UnderwaterModem) it.next();
                Node modemNode = (Node)uw.getAuv().getAUVNode().getChild("modem");
                AUVVisualizationNode modemVisNode = new AUVVisualizationNode(auv, modemNode,app);
                modemVisNode.init();
                nodeMap.put(uw.getAuv().getName(), modemVisNode);
                triggerCalc.getEventGenerator().addListener(modemVisNode);
                //1. Erstelle AUVVisualisationNode
                //2. Füttere sie mit allen Informationen
                //3. adde sie zur Map
            }
        }
        loadAndInitPreferenceListeners();
        return true;
    }
    
    private boolean loadAndInitPreferenceListeners() {
        Preferences pref = Preferences.userNodeForPackage(mars.uwCommManager.options.CommunicationConfigurationOptionsPanelController.class);
        if(pref == null) return false;
        
        if(pref.getBoolean(CommOptionsConstants.OPTIONS_MAIN_SHOW_ACTIVE_LINKS_CHECKBOX, false)) {
            for(Map.Entry<String,AUVVisualizationNode> entry : nodeMap.entrySet()) {
                entry.getValue().showCommunicationLinks();
            }
        } else {
            for(Map.Entry<String,AUVVisualizationNode> entry : nodeMap.entrySet()) {
                entry.getValue().deactivateCommunicationLinks();
            }
        }
        if(pref.getBoolean(CommOptionsConstants.OPTIONS_MAIN_SHOW_MAXIMUM_PROPAGATIONDISTANCE, false)) {
            for(Map.Entry<String,AUVVisualizationNode> entry : nodeMap.entrySet()) {
                entry.getValue().activatePopagationSphere();
            }
        } else {
            for(Map.Entry<String,AUVVisualizationNode> entry : nodeMap.entrySet()) {
                entry.getValue().deactivatePropagationSphere();
            }
        }
        
        pref.addPreferenceChangeListener(new PreferenceChangeListener() {
            @Override
            public void preferenceChange(PreferenceChangeEvent e) {
                if(e.getKey().equals(CommOptionsConstants.OPTIONS_MAIN_SHOW_ACTIVE_LINKS_CHECKBOX)) {
                    if(Boolean.parseBoolean(e.getNewValue())) {
                        for(Map.Entry<String,AUVVisualizationNode> entry : nodeMap.entrySet()) {
                            entry.getValue().showCommunicationLinks();
                        }
                    } else {
                        for(Map.Entry<String,AUVVisualizationNode> entry : nodeMap.entrySet()) {
                            entry.getValue().deactivateCommunicationLinks();
                        }
                    }
                } else if(e.getKey().equals(CommOptionsConstants.OPTIONS_MAIN_SHOW_MAXIMUM_PROPAGATIONDISTANCE)) {
                    if(Boolean.parseBoolean(e.getNewValue())) {
                        for(Map.Entry<String,AUVVisualizationNode> entry : nodeMap.entrySet()) {
                            entry.getValue().activatePopagationSphere();
                        } 
                    }
                    else {
                        for(Map.Entry<String,AUVVisualizationNode> entry : nodeMap.entrySet()) {
                            entry.getValue().deactivatePropagationSphere();
                        }
                    }
                }
            }
        });
        
        
        
        return true;
    }
    
    /**
     * call update in all AUVNodes
     * @param tpf 
     */
    public void update(float tpf) {
        
        
        for(Map.Entry<String, AUVVisualizationNode> e : nodeMap.entrySet()) {
            e.getValue().update(tpf);
        }
    }
    
}
