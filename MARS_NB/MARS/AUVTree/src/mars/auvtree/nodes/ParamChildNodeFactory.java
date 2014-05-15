/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.auvtree.nodes;

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
    private BasicAUV auv;

    /**
     * Constructor for the main categories accumulator, actuator and sensor
     *
     * @param name
     */
    public ParamChildNodeFactory(String name) {
        CentralLookup cl = CentralLookup.getDefault();
        AUV_Manager auv_manager = cl.lookup(AUV_Manager.class);
        auv = (BasicAUV) auv_manager.getAUV(name);
    }

    @Override
    protected boolean createKeys(List toPopulate) {
        toPopulate.add("" + PARAMETER);
        toPopulate.add("" + ACCUMULATORS);
        toPopulate.add("" + ACTUATORS);
        toPopulate.add("" + SENSORS);
        return true;
    }

    @Override
    protected Node createNodeForKey(String key) {
        Node n = null;
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
                hasher.put("Parameter", auv.getAuv_param());
                n = new ParamNode(ikey, hasher);
                break;
        }
        return n;
    }
}
