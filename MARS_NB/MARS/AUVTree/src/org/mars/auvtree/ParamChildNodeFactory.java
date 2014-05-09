/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mars.auvtree;

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
public class ParamChildNodeFactory extends ChildFactory<String> {

    private HashMap params;
    public static final int ACCUMULATORS = 0;
    public static final int ACTUATORS = 1;
    public static final int SENSORS = 2;
    public static final int PARAMETER = 3;
    private final int type;
    private final static int CHILD = 1;
    private final static int PARAMROOT = 2;
    private BasicAUV auv;

    /**
     * Constructor for every child node under accumulator, actuator and sensor
     *
     * @param params
     */
    public ParamChildNodeFactory(HashMap params) {
        type = CHILD;
        this.params = params;
    }

    /**
     * Constructor for the main categories accumulator, actuator and sensor
     *
     * @param name
     */
    public ParamChildNodeFactory(String name) {
        type = PARAMROOT;
        CentralLookup cl = CentralLookup.getDefault();
        AUV_Manager auv_manager = cl.lookup(AUV_Manager.class);
        auv = (BasicAUV) auv_manager.getAUV(name);
    }

    @Override
    protected boolean createKeys(List toPopulate) {
        if (type == CHILD) {

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
        } else if (type == PARAMROOT) {
            // create list for the main categories
            toPopulate.add("" + PARAMETER);
            toPopulate.add("" + ACCUMULATORS);
            toPopulate.add("" + ACTUATORS);
            toPopulate.add("" + SENSORS);
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(String key) {
        Node n = null;
        if (type == CHILD) {
            // create new node for every key
            n = new ParamNode(params.get(key), key);
        } else if (type == PARAMROOT) {
            // create node for every category
            int ikey = Integer.parseInt(key);
            switch (Integer.parseInt(key)) {
                case ACCUMULATORS:
                    n = new ParamNode(ikey, auv.getAccumulators());
                    break;
                case ACTUATORS:
                    n = new ParamNode(ikey, auv.getActuators());
                    break;
                case SENSORS:
                    n = new ParamNode(ikey, auv.getSensors());
                    break;
                case PARAMETER:
                    HashMap hasher = new HashMap();
                    hasher.put("Parmeter", auv.getAuv_param());
                    n = new ParamNode(ikey, hasher);
                    break;
            }
        }
        return n;
    }
}
