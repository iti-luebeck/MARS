/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.graphics;

import com.jme3.scene.Node;
import mars.auv.AUV;

/**
 * @version 0.1
 * @author Jasper Schwinghammer
 */
public class AUVVisualizationNode {
    
    
    String name = null;
    AUV auv = null;
    Node visRootNode = null;
    Node auvNode = null;
    
    /**
     * @since 0.1
     * @param auv The AUV this Node will visualize
     * @param auvNode the node of the AUV we are interested in
     */
    public AUVVisualizationNode(AUV auv, Node auvNode) {
        this.auv = auv;
        this.auvNode = auvNode;
    }
    
    public boolean init() {
        if(auv==null || auvNode == null) return false;
        this.name = auv.getName() + "-visualisation-Node";
        this.visRootNode = new Node(name);
        auvNode.attachChild(visRootNode);
        return true;
    }
    
    public void update(float tpf) {
        
    }
    
}
