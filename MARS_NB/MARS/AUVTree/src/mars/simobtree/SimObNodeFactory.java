/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.simobtree;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import mars.auv.NodeRefreshEvent;
import mars.auvtree.nodes.*;
import mars.simobjects.SimObject;
import mars.simobjects.SimObjectManager;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 * Factory for creation of auv nodes.
 *
 * @author Christian
 * @author Thomas Tosik
 */
public class SimObNodeFactory extends ChildFactory<String> implements NodeListener, LookupListener {

    /**
     * Set of auv names.
     */
    private HashMap<String, SimObject> simobs;
    private final Lookup.Result<NodeRefreshEvent> lookupResult;

    /**
     *
     * @param simobs
     * @param simob_manager
     */
    public SimObNodeFactory(HashMap<String, SimObject> simobs, SimObjectManager simob_manager) {
        this.simobs = simobs;
        lookupResult = simob_manager.getLookup().lookupResult(NodeRefreshEvent.class);
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
        SortedSet<String> sortedset = new TreeSet<String>(simobs.keySet());
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
     * @return Instance of SimObNode.
     */
    @Override
    protected Node createNodeForKey(String key) {
        SimObNode auvNode = new SimObNode(simobs.get(key), key);
        auvNode.addNodeListener(this);
        System.out.println("childrenAdded: " + key);
        return auvNode;
    }

    /**
     *
     * @param lookupEvent
     */
    @Override
    public void resultChanged(LookupEvent lookupEvent) {
        refresh(true);
        System.out.println("refresh");
    }

    /**
     *
     * @param nme
     */
    @Override
    public void childrenAdded(NodeMemberEvent nme) {
        System.out.println("childrenAdded");
    }

    /**
     *
     * @param nme
     */
    @Override
    public void childrenRemoved(NodeMemberEvent nme) {
    }

    /**
     *
     * @param nre
     */
    @Override
    public void childrenReordered(NodeReorderEvent nre) {
    }

    /**
     *
     * @param ne
     */
    @Override
    public void nodeDestroyed(NodeEvent ne) {
        SimObject lookup = ne.getNode().getLookup().lookup(SimObject.class);
        String name = lookup.getName();
        simobs.remove(name);
        refresh(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        System.out.println("prop change: " + evt);
    }

}
