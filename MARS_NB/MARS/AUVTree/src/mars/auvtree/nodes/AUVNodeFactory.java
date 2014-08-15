/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.auvtree.nodes;

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.HashMap;
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
import mars.auv.NodeRefreshEvent;
import mars.core.CentralLookup;
import mars.states.SimState;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Result;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 * Factory for creation of auv nodes.
 * 
 * @author Christian
 * @author Thomas Tosik
 */
public class AUVNodeFactory extends ChildFactory<String> implements NodeListener,LookupListener{

    /**
     * Set of auv names.
     */
    private HashMap<String,AUV> auvs;
    private final Lookup.Result<NodeRefreshEvent> lookupResult;
    
    public AUVNodeFactory(HashMap<String,AUV> auvs, AUV_Manager auv_manager) {
        this.auvs = auvs;
        lookupResult = auv_manager.getLookup().lookupResult(NodeRefreshEvent.class);
        lookupResult.addLookupListener(this);
    }
    
    /**
     * Creates list of keys.
     * 
     * @param toPopulate List of created keys.
     * @return always true
     */
    @Override
    protected boolean createKeys(List toPopulate) {
        SortedSet<String> sortedset = new TreeSet<String>(auvs.keySet());
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
        AUVNode auvNode = new AUVNode(auvs.get(key),key);
        auvNode.addNodeListener(this);
        System.out.println("childrenAdded: " + key);
        return auvNode;
    }
    
    @Override
    public void resultChanged(LookupEvent lookupEvent) {
        refresh(true);
        System.out.println("refresh");
    }

    @Override
    public void childrenAdded(NodeMemberEvent nme) {
        System.out.println("childrenAdded");
    }

    @Override
    public void childrenRemoved(NodeMemberEvent nme) {
    }

    @Override
    public void childrenReordered(NodeReorderEvent nre) {
    }

    @Override
    public void nodeDestroyed(NodeEvent ne) {
        AUV lookup = ne.getNode().getLookup().lookup(AUV.class);
        String name = lookup.getName();
        auvs.remove(name);
        refresh(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println("prop change: " + evt);
    }

}
