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
package mars.simobjects;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import mars.misc.Collider;
import mars.MARS_Main;
import mars.MARS_Settings;
import mars.object.MARSObject;
import mars.object.MARSObjectManager;
import mars.states.SimState;

/**
 * This manager manages the SimObjects. Intialises them...
 *
 * @author Thomas Tosik
 */
public class SimObjectManager extends MARSObjectManager{

    private Node SimObNode;
    private AssetManager assetManager;
    
    /**
     *
     * @param simstate
     */
    public SimObjectManager(SimState simstate) {
        super(simstate);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Creating SIM_OBJECT_MANAGER...", "");
        this.assetManager = simstate.getAssetManager();
        this.SimObNode = simstate.getSimObNodes();
    }

    public SimObjectManager() {
        super();
    }
 
    /**
     * With this method you register pre created auv like hanse.
     *
     * @param name
     * @param simob
     */
    public void registerSimObject(String name, SimObject simob) {
        simob.setName(name);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "SIM_OBJECT " + simob.getName() + " added...", "");
        final SimObject fin_simob = simob;
        mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                addSimObjectToScene(fin_simob);
                marsObjects.put(fin_simob.getName(), fin_simob);
                return null;
            }
        });
    }

    /**
     *
     * @param simob
     */
    public void registerSimObject(SimObject simob) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "SIM_OBJECT " + simob.getName() + " added...", "");
        final SimObject fin_simob = simob;
        mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                addSimObjectToScene(fin_simob);
                marsObjects.put(fin_simob.getName(), fin_simob);
                return null;
            }
        });
    }

    /**
     *
     * @param arrlist
     */
    public void registerSimObjects(ArrayList<SimObject> arrlist) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Adding Sim_Objects...", "");
        Iterator<SimObject> iter = arrlist.iterator();
        while (iter.hasNext()) {
            SimObject simob = iter.next();
            registerSimObject(simob);
        }
    }

    /**
     *
     * @param name
     */
    public void deregisterSimObject(String name) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "SIM_OBJECT " + name + " deleted...", "");
        final String fin_name = name;
        mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                SimObject ret = (SimObject)marsObjects.remove(fin_name);
                removeSimObjectFromScene(ret);
                return null;
            }
        });
    }

    /**
     *
     * @param simob
     */
    public void deregisterSimObject(SimObject simob) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "SIM_OBJECT " + simob.getName() + " deleted...", "");
        final SimObject fin_simob = simob;
        mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                removeSimObjectFromScene(fin_simob);
                marsObjects.remove(fin_simob.getName());
                return null;
            }
        });
    }

    /**
     *
     */
    public void deregisterAllSimObjects() {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Deleting All Sim_Objects...", "");
        for (String elem : marsObjects.keySet()) {
            SimObject simob = (SimObject)marsObjects.get(elem);
            deregisterSimObject(simob);
        }
    }

    /**
     *
     * @param simobs
     */
    public void deregisterSimObjects(ArrayList<SimObject> simobs) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Deleting Sim_Objects...", "");
        Iterator<SimObject> iter = simobs.iterator();
        while (iter.hasNext()) {
            SimObject simob = iter.next();
            deregisterSimObject(simob);
        }
    }

    /**
     *
     * @param key Which unique registered auv do we want?
     * @return The auv that we asked for
     */
    public SimObject getSimObject(String key) {
        return (SimObject)marsObjects.get(key);
    }

    /**
     *
     * @return
     */
    public HashMap<String, SimObject> getSimObjects() {
        HashMap<String, SimObject> simobs = new HashMap<String, SimObject>();
        for (Map.Entry<String, MARSObject> entrySet : marsObjects.entrySet()) {
            String key = entrySet.getKey();
            SimObject value = (SimObject)entrySet.getValue();
            simobs.put(key, value);
        }
        return simobs;
    }

    private void addSimObjectToScene(SimObject simob) {
        if (simob.isEnabled()) {
            initSimObject(simob);
            addSimObjectToNode(simob, SimObNode);
            //addSimObjectToPickingNode(simob,SimObPickingNode);
            addSimObjectToBulletAppState(simob);
        }
    }

    private void removeSimObjectFromScene(SimObject simob) {
        bulletAppState.getPhysicsSpace().remove(simob.getSpatial());
        RayDetectable.detachChild(simob.getSpatial());
        simob.getSimObNode().removeFromParent();
    }

    /**
     * We must add the auv to some Node that is in the RootNode to render them.
     *
     * @param node
     */
    private void addSimObjectToNode(SimObject simob, Node node) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Adding SimObjects to Node: " + node.getName(), "");
        if (simob.isEnabled()) {
            if (simob.getRayDetectable()) {
                RayDetectable.attachChild(simob.getSimObNode());
            }
            node.attachChild(simob.getSimObNode());
        }
    }

    /**
     * We must add the auv to a special Node so that we can pick it it without
     * interference of debug stuff.
     *
     * @param node
     */
    private void addSimObjectToPickingNode(SimObject simob, Node node) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Adding SimObjects to PickingNode: " + node.getName(), "");
        if (simob.isEnabled()) {
            node.attachChild(simob.getRenderNode());
        }
    }

    /**
     * We must add the auv to a BulletAppState so the physics can be applied.
     *
     * @param simob
     */
    public void addSimObjectToBulletAppState(SimObject simob) {
        if (simob.getCollisionCollidable() && simob.isEnabled()) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Adding SimObject " + simob.getName() + " to BulletAppState...", "");
            bulletAppState.getPhysicsSpace().add(simob.getSpatial());
        }
    }

    private void initSimObject(SimObject simob) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Initialising SimObject " + simob.getName() + "...", "");
        if (simob.isEnabled()) {
            simob.setSimauv(mars);
            simob.setAssetManager(assetManager);
            simob.setMARSSettings(mars_settings);
            simob.init();
        }
    }

    private void initSimObject(String simob_name) {
        SimObject simob = getSimObject(simob_name);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Initialising SimObject " + simob.getName() + "...", "");
        if (simob.isEnabled()) {
            simob.setSimauv(mars);
            simob.setAssetManager(assetManager);
            simob.setMARSSettings(mars_settings);
            simob.init();
        }
    }

    /**
     *
     * @return
     */
    public SimObject getSelectedSimObject() {
        return (SimObject)getSelected();
    }

    /**
     * Enables/Disables a preloaded AUV. Be sure to enable an AUV only after the
     * update cycle(future/get).
     *
     * @param simob
     * @param enable
     */
    public void enableSimObject(SimObject simob, boolean enable) {
        enableSimObject(simob.getName(), enable);
    }

    private void enableSimObject(String simob_name, boolean enable) {
        SimObject simob = (SimObject)marsObjects.get(simob_name);
        if (enable) {
            addSimObjectToScene(simob);
        } else {
            removeSimObjectFromScene(simob);
        }
    }

    @Override
    public String toString() {
        return "SimObjects";
    }
}
