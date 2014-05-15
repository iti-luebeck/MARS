/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.auvtree.nodes;

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import mars.MARS_Main;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.core.CentralLookup;
import mars.states.SimState;
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
        SortedSet<String> sortedset = new TreeSet<String>(auvNames);
        for (Iterator<String> it = sortedset.iterator(); it.hasNext();) {
            String auvName = it.next();
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
