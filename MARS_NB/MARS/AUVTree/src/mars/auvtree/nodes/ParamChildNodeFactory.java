/*
* Copyright (c) 2015, Institute of Computer Engineering, University of Lübeck
* All rights reserved.
* 
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* 
* * Redistributions of source code must retain the above copyright notice, this
*   list of conditions and the following disclaimer.
* 
* * Redistributions in binary form must reproduce the above copyright notice,
*   this list of conditions and the following disclaimer in the documentation
*   and/or other materials provided with the distribution.
* 
* * Neither the name of the copyright holder nor the names of its
*   contributors may be used to endorse or promote products derived from
*   this software without specific prior written permission.
* 
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
* FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
* CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package mars.auvtree.nodes;

import java.util.HashMap;
import java.util.List;
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

    /**
     *
     */
    public static final int ACCUMULATORS = 0;

    /**
     *
     */
    public static final int ACTUATORS = 1;

    /**
     *
     */
    public static final int SENSORS = 2;

    /**
     *
     */
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
        auv = (BasicAUV) auv_manager.getMARSObject(name);
    }

    /**
     *
     * @param toPopulate
     * @return
     */
    @Override
    protected boolean createKeys(List toPopulate) {
        toPopulate.add("" + PARAMETER);
        toPopulate.add("" + ACCUMULATORS);
        toPopulate.add("" + ACTUATORS);
        toPopulate.add("" + SENSORS);
        return true;
    }

    /**
     *
     * @param key
     * @return
     */
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
                n = new PhysicalExchangerNode(auv.getAuv_param(), "Parameter");
                break;
        }
        return n;
    }
}
