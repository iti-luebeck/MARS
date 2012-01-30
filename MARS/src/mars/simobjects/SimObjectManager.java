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
import mars.MARS_Main;
import mars.states.SimState;

/**
 * This manager manages the SimObjects. He intialises them...
 * @author Thomas Tosik
 */
public class SimObjectManager {
    //auv HashMap to store and load auv's
    private HashMap<String,SimObject> simobs = new HashMap<String,SimObject> ();
    private Node SonarDetectableNode;
    private Node sceneReflectionNode;
    private MARS_Main simauv;
    private AssetManager assetManager;
    private BulletAppState bulletAppState;
    private Node rootNode;
    /**
     *
     * @param simstate 
     */
    public SimObjectManager(SimState simstate) {
       //set the logging
       try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
            // Add to the desired logger
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.addHandler(handler);
        } catch (IOException e) { }

        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Creating SIM_OBJECT_MANAGER...", "");

        this.simauv = simstate.getMARS();
        this.rootNode = simstate.getRootNode();
        this.assetManager = simstate.getAssetManager();
        this.SonarDetectableNode = simstate.getSonarDetectableNode();
        this.sceneReflectionNode = simstate.getSceneReflectionNode();
        this.bulletAppState = simstate.getBulletAppState();
    }

    /**
     * With this method you register pre created auv like hanse.
     * @param name
     * @param simob
     */
    public void registerSimObject( String name, SimObject simob ){
        simob.setName(name);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "SIM_OBJECT " + simob.getName() + " added...", "");
        final SimObject fin_simob = simob;
        Future fut = simauv.enqueue(new Callable() {
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
    public void registerSimObject( SimObject simob ){
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "SIM_OBJECT " + simob.getName() + " added...", "");
        final SimObject fin_simob = simob;
        Future fut = simauv.enqueue(new Callable() {
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
    public void registerSimObjects( ArrayList arrlist ){
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Adding Sim_Objects...", "");
        Iterator iter = arrlist.iterator();
        while(iter.hasNext() ) {
            SimObject simob = (SimObject)iter.next();
            registerSimObject(simob);
        }
    }

    /**
     *
     * @param name
     */
    public void deregisterSimObject( String name ){
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "SIM_OBJECT " + name + " deleted...", "");
        final String fin_name = name;
        Future fut = simauv.enqueue(new Callable() {
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
    public void deregisterSimObject( SimObject simob ){
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "SIM_OBJECT " + simob.getName() + " deleted...", "");
        final SimObject fin_simob = simob;
        Future fut = simauv.enqueue(new Callable() {
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
    public void deregisterAllSimObjects(){
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Deleting All Sim_Objects...", "");
        for ( String elem : simobs.keySet() ){
            SimObject simob = (SimObject)simobs.get(elem);
            deregisterSimObject(simob);
        }
    }

    /**
     *
     * @param simobs
     */
    public void deregisterSimObjects( ArrayList simobs ){
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Deleting Sim_Objects...", "");
        Iterator iter = simobs.iterator();
        while(iter.hasNext() ) {
            SimObject simob = (SimObject)iter.next();
            deregisterSimObject(simob);
        }
    }

    /**
     *
     * @param key Which unique registered auv do we want?
     * @return The auv that we asked for
     */
    public SimObject getSimObject(String key){
        return simobs.get(key);
    }

    /**
     *
     * @return
     */
    public HashMap<String,SimObject> getSimObjects(){
        return simobs;
    }

    private void addSimObjectToScene(SimObject simob){
        if(simob.isEnabled()){
            initSimObject(simob);
            addSimObjectToNode(simob,sceneReflectionNode);
            addSimObjectToBulletAppState(simob);
        }
    }

    private void removeSimObjectFromScene(SimObject simob){
        bulletAppState.getPhysicsSpace().remove(simob.getSpatial());
        simob.getSpatial().removeFromParent();
    }

    /**
     * We must add the auv to some Node that is in the RootNode to render them.
     * @param node
     */
    private void addSimObjectToNode(SimObject simob, Node node){
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Adding SimObjects to Node: " + node.getName(), "");
            if(simob.isEnabled()){
                if(simob.isSonar_detectable()){
                    SonarDetectableNode.attachChild(simob.getSpatial());
                }else{
                    node.attachChild(simob.getSpatial());
                }
            }
    }

    /**
     * We must add the auv to some Node that is in the RootNode to render them.
     * @param node
     */
    @Deprecated
    private void addSimObjectsToNode(Node node){
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Adding SimObjects to Node: " + node.getName(), "");
        for ( String elem : simobs.keySet() ){
            SimObject simob = (SimObject)simobs.get(elem);
            if(simob.isEnabled()){
                final Spatial final_spatial = simob.getSpatial();
                if(simob.isSonar_detectable()){
                    Future fut = simauv.enqueue(new Callable() {
                        public Void call() throws Exception {
                            SonarDetectableNode.attachChild(final_spatial);
                            return null;
                        }
                    });
                }else{
                    final Node final_node = node;
                    Future fut = simauv.enqueue(new Callable() {
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
     * @param simob 
     */
    public void addSimObjectToBulletAppState(SimObject simob){
        if(simob.isCollidable() && simob.isEnabled()){
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Adding SimObject " + simob.getName() + " to BulletAppState...", "");
            bulletAppState.getPhysicsSpace().add(simob.getSpatial());
        }
    }

    private void initSimObject(SimObject simob){
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Initialising SimObject " + simob.getName() + "...", "");
        if(simob.isEnabled()){
            simob.setSimauv(simauv);
            simob.setAssetManager(assetManager);
            simob.init();
        }
    }

    private void initSimObject(String simob_name){
        SimObject simob = (SimObject)getSimObject(simob_name);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Initialising SimObject " + simob.getName() + "...", "");
        if(simob.isEnabled()){
            simob.setSimauv(simauv);
            simob.setAssetManager(assetManager);
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
}
