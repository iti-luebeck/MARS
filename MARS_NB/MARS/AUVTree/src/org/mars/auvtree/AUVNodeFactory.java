/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mars.auvtree;

import java.beans.PropertyChangeEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import mars.auv.AUV;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;

/**
 * Factory for creation of auv nodes.
 * 
 * @author Christian
 */
public class AUVNodeFactory extends ChildFactory<String> implements NodeListener{

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
        AUVNode auvNode = new AUVNode(key);
        auvNode.addNodeListener(this);
        return auvNode;
    }

    @Override
    public void childrenAdded(NodeMemberEvent nme) {
    }

    @Override
    public void childrenRemoved(NodeMemberEvent nme) {
    }

    @Override
    public void childrenReordered(NodeReorderEvent nre) {
    }

    @Override
    public void nodeDestroyed(NodeEvent ne) {
        AUVNode lookup = ne.getNode().getLookup().lookup(AUVNode.class);
        String name = lookup.getDisplayName();
        auvNames.remove(name);
        refresh(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        
    }

}
