/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mars.auvtree.nodes;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import mars.auv.AUV_Manager;
import mars.auv.BasicAUV;
import mars.core.CentralLookup;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Christian Friedrich
 * @author Thomas Tosik
 */
public class PhysicalExchangerChildNodeFactory extends ChildFactory<String> {

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
        // iterate params and store keys in list
        /*Iterator<Map.Entry<String, Object>> aI = params.entrySet().iterator();

        for (Map.Entry<String, Object> mE; aI.hasNext();) {
            mE = aI.next();
            toPopulate.add(mE.getKey());
        }*/
        return true;
    }

    @Override
    protected Node createNodeForKey(String key) {
        // create new node for every key
        Node n = new PhysicalExchangerNode(params.get(key), key);
        return n;
    }
}
