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
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import mars.PhysicalExchanger;
import mars.auv.AUV_Manager;
import mars.auv.BasicAUV;
import mars.core.CentralLookup;
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
public class PhysicalExchangerChildNodeFactory extends ChildFactory<String> implements NodeListener{

    private HashMap params;

    /**
     * Constructor for every child node under accumulator, actuator and sensor
     *
     * @param params
     */
    public PhysicalExchangerChildNodeFactory(HashMap params) {
        this.params = params;
    }

    @Override
    protected boolean createKeys(List toPopulate) {
        //sorted output
        SortedSet<String> sortedset= new TreeSet<String>(params.keySet());
        for (Iterator<String> it2 = sortedset.iterator(); it2.hasNext();) {
            String string = it2.next();
            toPopulate.add(string);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(String key) {
        // create new node for every key
        Node n = new PhysicalExchangerNode(params.get(key), key);
        n.addNodeListener(this);
        return n;
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
        PhysicalExchanger lookup = ne.getNode().getLookup().lookup(PhysicalExchanger.class);
        String name = lookup.getName();
        params.remove(name);
        refresh(true);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        
    }
}
