/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.auvtree.nodes;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import mars.PhysicalExchange.PhysicalExchanger;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;

/**
 *
 * @author Christian Friedrich
 * @author Thomas Tosik
 */
public class PhysicalExchangerChildNodeFactory extends ChildFactory<String> implements NodeListener {

    private HashMap params;

    /**
     * Constructor for every child node under accumulator, actuator and sensor
     *
     * @param params
     */
    public PhysicalExchangerChildNodeFactory(HashMap params) {
        this.params = params;
    }

    /**
     *
     * @param toPopulate
     * @return
     */
    @Override
    protected boolean createKeys(List toPopulate) {
        //sorted output
        SortedSet<String> sortedset = new TreeSet<String>(params.keySet());
        for (String string : sortedset) {
            toPopulate.add(string);
        }
        return true;
    }

    /**
     *
     * @param key
     * @return
     */
    @Override
    protected Node createNodeForKey(String key) {
        // create new node for every key
        Node n = new PhysicalExchangerNode(params.get(key), key);
        n.addNodeListener(this);
        return n;
    }

    /**
     *
     * @param nme
     */
    @Override
    public void childrenAdded(NodeMemberEvent nme) {
        refresh(true);
    }
    
    /**
     *
     */
    public void refresh(){
        refresh(true);
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
        PhysicalExchanger lookup = ne.getNode().getLookup().lookup(PhysicalExchanger.class);
        String name = lookup.getName();
        params.remove(name);
        refresh(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
