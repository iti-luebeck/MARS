/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import mars.misc.Collider;
import mars.MARS_Main;
import mars.MARS_Settings;
import mars.object.MARSObjectManager;
import mars.states.SimState;

/**
 * This manager manages the SimObjects. Intialises them...
 *
 * @author Thomas Tosik
 */
public class SimObjectManager extends MARSObjectManager{

    //auv HashMap to store and load auv's

    private HashMap<String, SimObject> simobs = new HashMap<String, SimObject>();
    private Collider RayDetectable;
    private Node sceneReflectionNode;
    private Node SimObNode;
    private MARS_Main mars;
    private AssetManager assetManager;
    private BulletAppState bulletAppState;
    private Node rootNode;
    private MARS_Settings mars_settings;
    
    /**
     *
     * @param simstate
     */
    public SimObjectManager(SimState simstate) {
        //set the logging
        try {
            Logger.getLogger(this.getClass().getName()).setLevel(Level.parse(simstate.getMARSSettings().getLoggingLevel()));

            if(simstate.getMARSSettings().getLoggingFileWrite()){
                // Create an appending file handler
                boolean append = true;
                FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
                handler.setLevel(Level.parse(simstate.getMARSSettings().getLoggingLevel()));
                // Add to the desired logger
                Logger logger = Logger.getLogger(this.getClass().getName());
                logger.addHandler(handler);
            }
            
            if(!simstate.getMARSSettings().getLoggingEnabled()){
                Logger.getLogger(this.getClass().getName()).setLevel(Level.OFF);
            }
        } catch (IOException e) {
        }

        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Creating SIM_OBJECT_MANAGER...", "");

        this.mars = simstate.getMARS();
        this.rootNode = simstate.getRootNode();
        this.assetManager = simstate.getAssetManager();
        this.RayDetectable = simstate.getCollider();
        this.sceneReflectionNode = simstate.getSceneReflectionNode();
        this.SimObNode = simstate.getSimObNodes();
        this.bulletAppState = simstate.getBulletAppState();
        this.mars_settings = simstate.getMARSSettings();
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
        Future<Void> fut = mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                addSimObjectToScene(fin_simob);
                simobs.put(fin_simob.getName(), fin_simob);
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
        Future<Void> fut = mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                addSimObjectToScene(fin_simob);
                simobs.put(fin_simob.getName(), fin_simob);
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
        Future<Void> fut = mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                SimObject ret = simobs.remove(fin_name);
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
        Future<Void> fut = mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                removeSimObjectFromScene(fin_simob);
                simobs.remove(fin_simob.getName());
                return null;
            }
        });
    }

    /**
     *
     */
    public void deregisterAllSimObjects() {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Deleting All Sim_Objects...", "");
        for (String elem : simobs.keySet()) {
            SimObject simob = simobs.get(elem);
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
     */
    public void deselectAllSimObs() {
        //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "DeSelecting all AUVs...", "");
        for (String elem : simobs.keySet()) {
            SimObject simob = simobs.get(elem);
            simob.setSelected(false);
        }
    }

    /**
     *
     * @param key Which unique registered auv do we want?
     * @return The auv that we asked for
     */
    public SimObject getSimObject(String key) {
        return simobs.get(key);
    }

    /**
     *
     * @return
     */
    public HashMap<String, SimObject> getSimObjects() {
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
     * We must add the auv to some Node that is in the RootNode to render them.
     *
     * @param node
     */
    @Deprecated
    private void addSimObjectsToNode(Node node) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Adding SimObjects to Node: " + node.getName(), "");
        for (String elem : simobs.keySet()) {
            SimObject simob = simobs.get(elem);
            if (simob.isEnabled()) {
                final Spatial final_spatial = simob.getSpatial();
                if (simob.getRayDetectable()) {
                    Future<Void> fut = mars.enqueue(new Callable<Void>() {
                        public Void call() throws Exception {
                            //SonarDetectableNode.attachChild(final_spatial);
                            return null;
                        }
                    });
                } else {
                    final Node final_node = node;
                    Future<Void> fut = mars.enqueue(new Callable<Void>() {
                        public Void call() throws Exception {
                            final_node.attachChild(final_spatial);
                            return null;
                        }
                    });
                }
            }
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
     * @param bulletAppState
     */
    public void setBulletAppState(BulletAppState bulletAppState) {
        this.bulletAppState = bulletAppState;
    }

    /**
     *
     * @return
     */
    public MARS_Settings getMARSSettings() {
        return mars_settings;
    }

    /**
     *
     * @param mars_settings
     */
    public void setMARSSettings(MARS_Settings mars_settings) {
        this.mars_settings = mars_settings;
    }

    /**
     *
     * @return
     */
    public SimObject getSelectedSimObject() {
        //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Getting selected AUV...", "");
        for (String elem : simobs.keySet()) {
            SimObject simob = simobs.get(elem);
            if (simob.isSelected()) {
                return simob;
            }
        }
        return null;
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
        SimObject simob = simobs.get(simob_name);
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
