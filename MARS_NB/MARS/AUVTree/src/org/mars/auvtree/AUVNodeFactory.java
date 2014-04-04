/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mars.auvtree;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 * Factory for creation of auv nodes.
 * 
 * @author Christian
 */
public class AUVNodeFactory extends ChildFactory<String>{

    /**
     * Set of auv names.
     */
    private Set auvNames;
    
    public AUVNodeFactory(Set auvNames) {
        this.auvNames = auvNames;
    }
    
    /**
     * Creates list of keys.
     * 
     * @param toPopulate List of created keys.
     * @return always true
     */
    @Override
    protected boolean createKeys(List toPopulate) {
        Iterator<String> anI = auvNames.iterator();
        for(int i=0;anI.hasNext();i++) {
            String auvName = anI.next();
            toPopulate.add(auvName);
        }
        return true;
    }
    
    /**
     * This method creates a node for a given key.
     * 
     * @param key
     * @return Instance of AUVNode.
     */
    @Override
    protected Node createNodeForKey(String key) {
        return new AUVNode(key);
    }
    
}
