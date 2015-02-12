/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.auv;

import com.jme3.bullet.BulletAppState;
import com.jme3.scene.Node;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import mars.MARS_Main;
import mars.MARS_Settings;
import mars.PhysicalEnvironment;
import mars.misc.Collider;
import mars.object.MARSObjectManager;
import mars.ros.MARSNodeMain;
import mars.server.MARSClient;
import mars.server.MARSClientEvent;
import mars.states.MapState;
import mars.states.SimState;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 * Creates an AUV_Manger. You register your auv's here. The complete life cycle
 * of an AUV is managed here.
 *
 * @author Thomas Tosik
 */
@ServiceProvider(service = AUV_Manager.class)
public class AUV_Manager extends MARSObjectManager{

    //auv HashMap to store and load auv's
    private HashMap<String, AUV> auvs = new HashMap<String, AUV>();
    private Collider RayDetectable;
    private Node sceneReflectionNode;
    private Node AUVsNode;
    private MARS_Main mars;
    private PhysicalEnvironment physical_environment;
    private MARS_Settings mars_settings;
    private BulletAppState bulletAppState;
    private Node rootNode;
    private SimState simstate;
    private CommunicationManager com_manager;
    private HashMap<String, MARSNodeMain> mars_nodes = new HashMap<String, MARSNodeMain>();
    private EventListenerList listeners = new EventListenerList();

    /**
     *
     * @param simstate
     */
    public AUV_Manager(SimState simstate) {
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

        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Creating AUV_MANAGER...", "");
        this.simstate = simstate;
        this.mars = simstate.getMARS();
        this.rootNode = simstate.getRootNode();
        this.RayDetectable = simstate.getCollider();
        this.sceneReflectionNode = simstate.getSceneReflectionNode();
        this.AUVsNode = simstate.getAUVsNode();
        this.bulletAppState = simstate.getBulletAppState();
    }

    /**
     *
     */
    public AUV_Manager() {
    }

    /**
     *
     * @return True if no AUVs are registered.
     */
    public boolean isEmpty() {
        return auvs.isEmpty();
    }

    /**
     *
     * @param key Which unique registered auv do we want?
     * @return The auv that we asked for
     */
    public AUV getAUV(String key) {
        return auvs.get(key);
    }

    /**
     *
     * @return All AUVs registered.
     */
    public HashMap<String, AUV> getAUVs() {
        return auvs;
    }

    /**
     *
     * @param oldName
     * @param newName
     */
    public void updateAUVName(String oldName, String newName) {
        AUV auv = auvs.get(oldName);
        auv.setName(newName);
        auvs.remove(oldName);
        auvs.put(newName, auv);
    }

    /**
     *
     * @return All AUV classes.
     */
    public ArrayList<Class<? extends AUV>> getAUVClasses() {
        ArrayList<Class<? extends AUV>> ret = new ArrayList<Class<? extends AUV>>();
        for (String elem : auvs.keySet()) {
            AUV auv = auvs.get(elem);
            Class<? extends AUV> aClass = auv.getClass();
            if (!ret.contains(aClass)) {
                ret.add(aClass);
            }
        }
        return ret;
    }

    /**
     *
     * @param classNameString
     * @return All AUVs of a specific class.
     */
    public ArrayList<AUV> getAUVsOfClass(String classNameString) {
        ArrayList<AUV> ret = new ArrayList<AUV>();
        for (String elem : auvs.keySet()) {
            AUV auv = auvs.get(elem);
            try {
                if (Class.forName(classNameString).isInstance(auv)) {
                    ret.add(auv);
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }

    /**
     *
     * @param classNameString
     * @return True if an AUV exists with a specific class.
     */
    public boolean hasAUVsOfClass(String classNameString) {
        for (String elem : auvs.keySet()) {
            AUV auv = auvs.get(elem);
            try {
                return (Class.forName(classNameString).isInstance(auv));
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    /**
     *
     * @return
     */
    public PhysicalEnvironment getPhysical_environment() {
        return physical_environment;
    }

    /**
     *
     * @param physical_environment
     */
    public void setPhysical_environment(PhysicalEnvironment physical_environment) {
        this.physical_environment = physical_environment;
    }

    /**
     *
     * @return
     */
    public CommunicationManager getCommunicationManager() {
        return com_manager;
    }

    /**
     *
     * @param com_manager
     */
    public void setCommunicationManager(CommunicationManager com_manager) {
        this.com_manager = com_manager;
    }

    /**
     *
     * @return
     */
    public MARS_Settings getMARS_settings() {
        return mars_settings;
    }

    /**
     *
     * @param mars_settings
     */
    public void setMARS_settings(MARS_Settings mars_settings) {
        this.mars_settings = mars_settings;
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
    public HashMap<String, MARSNodeMain> getMARSNodes() {
        return mars_nodes;
    }

    /**
     *
     * @param auv_name
     * @return
     */
    public MARSNodeMain getMARSNodeForAUV(String auv_name) {
        return mars_nodes.get(auv_name);
    }

    /**
     *
     * @param mars_nodes
     */
    public void setMARSNodes(HashMap<String, MARSNodeMain> mars_nodes) {
        this.mars_nodes = mars_nodes;
    }

    /**
     *
     */
    public void updateMARSNode() {
        for (String elem : auvs.keySet()) {
            AUV auv = auvs.get(elem);
            if (auv.getAuv_param().isEnabled()) {
                auv.setROS_Node(getMARSNodeForAUV(elem));
                auv.initROS();
            }
        }
    }

    /**
     * Updates all AUVs.
     *
     * @param tpf
     */
    public void updateAllAUVs(float tpf) {
        updateForcesOfAUVs(tpf);
        updateActuatorsOfAUVs(tpf);
        updateSensorsOfAUVs(tpf);
        updateCommunicationOfAUVs(tpf);
        updateWaypointsOfAUVs(tpf);
        updateAccumulatorsOfAUVs(tpf);
    }

    /**
     *
     */
    public void publishSensorsOfAUVs() {
        for (String elem : auvs.keySet()) {
            AUV auv = auvs.get(elem);
            if (auv.getAuv_param().isEnabled()) {
                auv.publishSensorsOfAUV();
            }
        }
    }

    /**
     *
     */
    public void publishActuatorsOfAUVs() {
        for (String elem : auvs.keySet()) {
            AUV auv = auvs.get(elem);
            if (auv.getAuv_param().isEnabled()) {
                auv.publishActuatorsOfAUV();
            }
        }
    }

    /**
     *
     * @param tpf
     */
    private void updateWaypointsOfAUVs(float tpf) {
        for (String elem : auvs.keySet()) {
            AUV auv = auvs.get(elem);
            if (auv.getAuv_param().isEnabled()) {
                auv.updateWaypoints(tpf);
            }
        }
    }

    /**
     * The camera view direction/location will be updated here. Normaly in a
     * SimpleUpdate method in the Main_Loop.
     *
     * @param tpf
     */
    private void updateSensorsOfAUVs(float tpf) {
        for (String elem : auvs.keySet()) {
            AUV auv = auvs.get(elem);
            if (auv.getAuv_param().isEnabled()) {
                auv.updateSensors(tpf);
            }
        }
    }

    /*
     * 
     */
    private void updateActuatorsOfAUVs(float tpf) {
        for (String elem : auvs.keySet()) {
            AUV auv = auvs.get(elem);
            if (auv.getAuv_param().isEnabled()) {
                auv.updateActuators(tpf);
            }
        }
    }

    /**
     * In this method the communication between the auv's through underwater
     * modems should be done
     *
     * @param tpf
     */
    @Deprecated
    private void updateCommunicationOfAUVs(float tpf) {
        for (String elem : auvs.keySet()) {
            AUV auv = auvs.get(elem);
            if (auv.getAuv_param().isEnabled()) {
                //auv.updateSensors(tpf);
            }
        }
    }

    /**
     * The forces of the auv's will be updated here. Normaly in a SimpleUpdate
     * method in the Main_Loop.
     *
     * @param tpf
     */
    private void updateForcesOfAUVs(final float tpf) {
        for (String elem : auvs.keySet()) {
            final AUV auv = auvs.get(elem);
            if (auv.getAuv_param().isEnabled()) {
                /*  Future fut = mars.enqueue(new Callable() {
                 public Void call() throws Exception {
                 auv.updateForces(tpf);
                 return null;
                 }
                 });*/
                auv.updateForces(tpf);
            }
        }
    }

    private void updateAccumulatorsOfAUVs(float tpf) {
        for (String elem : auvs.keySet()) {
            AUV auv = auvs.get(elem);
            if (auv.getAuv_param().isEnabled()) {
                auv.updateAccumulators(tpf);
            }
        }
    }

    /**
     *
     */
    public void clearForcesOfAUVs() {
        for (String elem : auvs.keySet()) {
            AUV auv = auvs.get(elem);
            if (auv.getAuv_param().isEnabled()) {
                auv.clearForces();
            }
        }
    }

    /**
     *
     */
    public void resetAllAUVs() {
        for (String elem : auvs.keySet()) {
            AUV auv = auvs.get(elem);
            if (auv.getAuv_param().isEnabled()) {
                auv.reset();
            }
        }
    }

    /**
     * With this method you register pre-created auv like hanse.
     *
     * @param name
     * @param auv
     */
    public void registerAUV(String name, AUV auv) {
        auv.setName(name);
        final AUV fin_auv = auv;
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "AUV " + auv.getName() + " added...", "");
        Future<Void> fut = mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                auvs.put(fin_auv.getName(), fin_auv);
                preloadAUV(fin_auv);
                return null;
            }
        });
    }

    /**
     *
     * @param auv
     */
    public void registerAUV(AUV auv) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "AUV " + auv.getName() + " added...", "");
        final AUV fin_auv = auv;
        Future<Void> fut = mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                final ProgressHandle progr = ProgressHandleFactory.createHandle("AUVManager: " + fin_auv.getName());
                progr.start();
                progr.progress("Loading AUV: " + fin_auv.getName());
                auvs.put(fin_auv.getName(), fin_auv);
                preloadAUV(fin_auv);
                progr.finish();
                content.add(new NodeRefreshEvent());
                return null;
            }
        });
    }

    /**
     *
     * @param arrlist
     */
    public void registerAUVs(ArrayList<AUV> arrlist) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Adding AUVs...", "");
        Iterator<AUV> iter = arrlist.iterator();
        while (iter.hasNext()) {
            AUV auv = iter.next();
            registerAUV(auv);
        }
    }

    /**
     *
     * @param auv_name
     */
    public void deregisterAUV(String auv_name) {
        final String fin_auv_name = auv_name;
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "AUV " + auv_name + " deleted...", "");
        Future<Void> fut = mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                AUV ret = auvs.remove(fin_auv_name);
                removeAUVFromScene(ret);
                return null;
            }
        });
    }

    /**
     *
     * @param auv
     */
    public void deregisterAUV(AUV auv) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "AUV " + auv.getName() + " deleted...", "");
        final AUV fin_auv = auv;
        Future<Void> fut = mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                removeAUVFromScene(fin_auv);
                auvs.remove(fin_auv.getName());
                return null;
            }
        });
    }

    /**
     *
     * @param auv
     */
    public void deregisterAUVNoFuture(AUV auv) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "AUV " + auv.getName() + " deleted...", "");
        final AUV fin_auv = auv;
        removeAUVFromScene(fin_auv);
        auvs.remove(fin_auv.getName());
    }

    /**
     *
     * @param auvs
     */
    public void deregisterAUVs(ArrayList<AUV> auvs) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Deleting AUVs...", "");
        Iterator<AUV> iter = auvs.iterator();
        while (iter.hasNext()) {
            AUV auv = iter.next();
            deregisterAUV(auv);
        }
    }

    /**
     *
     */
    public void deregisterAllAUVs() {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Deleting All AUVs...", "");
        for (String elem : auvs.keySet()) {
            AUV auv = auvs.get(elem);
            deregisterAUV(auv);
        }
    }

    /*
     * Make some loading so we don't have to wait longer in MARS. Used for deactivation.
     */
    private void preloadAUV(AUV auv) {
        //if(auv.getAuv_param().isEnabled()){
        auv.setState(simstate);
        auv.setMARS_Settings(mars_settings);
        auv.setPhysical_environment(physical_environment);
        auv.setCommunicationManager(com_manager);
        auv.setROS_Node(getMARSNodeForAUV(auv.getName()));
        initAUV(auv);
        if (auv.getAuv_param().isEnabled()) {
            //initAUV(auv);
            //addAUVToNode(auv,sceneReflectionNode);
            addAUVToNode(auv, AUVsNode);
            addAUVToBulletAppState(auv, bulletAppState);
            addAUVtoMap(auv);
        }
    }

    /**
     * Enables/Disables a preloaded AUV. Be sure to enable an AUV only after the
     * update cycle(future/get).
     *
     * @param auv
     * @param enable
     */
    public void enableAUV(AUV auv, boolean enable) {
        enableAUV(auv.getName(), enable);
    }

    private void enableAUV(String auv_name, boolean enable) {
        AUV auv = auvs.get(auv_name);
        if (enable) {
            addAUVToScene(auv);
        } else {
            removeAUVFromScene(auv);
        }
    }

    /*
    * Adds the AUV node to the scengraph(rootNode).
    */
    private void addAUVToScene(AUV auv) {
        auv.addDragOffscreenView();
        addAUVToNode(auv, AUVsNode);
        addAUVToBulletAppState(auv, bulletAppState);
    }

    /*
    * Removes the AUV node from the scengraph(rootNode).
    */
    private void removeAUVFromScene(AUV auv) {
        bulletAppState.getPhysicsSpace().remove(auv.getAUVNode());
        if (auv.getGhostControl() != null) {//only try too remove when ghost control exists
            bulletAppState.getPhysicsSpace().remove(auv.getGhostAUV());
        }
        RayDetectable.detachChild(auv.getSelectionNode());
        auv.cleanupOffscreenView();
        auv.getSelectionNode().removeFromParent();
    }

    /**
     * We must add the auv to some Node that is in the RootNode to render them.
     *
     * @param node
     */
    private void addAUVToNode(AUV auv, Node node) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Adding AUV's to Node: " + node.getName(), "");
        if (auv.getAuv_param().isRayDetectable()) {
            RayDetectable.attachChild(auv.getSelectionNode());
        }
        node.attachChild(auv.getSelectionNode());
    }

    /**
     * We must add the auv to some Node that is in the RootNode to render them.
     *
     * @param node
     */
    @Deprecated
    private void addAUVsToNode(Node node) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Adding AUV's to Node: " + node.getName(), "");
        for (String elem : auvs.keySet()) {
            AUV auv = auvs.get(elem);
            addAUVToNode(auv, node);
        }
    }

    /**
     * We must add the auv to a BulletAppState so the physics can be applied.
     *
     * @param auv
     * @param bulletAppState
     */
    public void addAUVToBulletAppState(AUV auv, BulletAppState bulletAppState) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Adding AUV " + auv.getName() + " to BulletAppState...", "");
        bulletAppState.getPhysicsSpace().add(auv.getAUVNode());
        if (auv.getGhostControl() != null) {
            //bulletAppState.getPhysicsSpace().add(auv.getGhostControl());
            bulletAppState.getPhysicsSpace().add(auv.getGhostAUV());
            bulletAppState.getPhysicsSpace().addCollisionListener(auv.getGhostControl());
            bulletAppState.getPhysicsSpace().addTickListener(auv.getGhostControl());
        }
    }

    /**
     * We must add the auv's to a BulletAppState so the physics can be applied.
     *
     * @param bulletAppState
     */
    public void addAUVsToBulletAppState(BulletAppState bulletAppState) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Adding AUV's to BulletAppState...", "");
        for (String elem : auvs.keySet()) {
            AUV auv = auvs.get(elem);
            bulletAppState.getPhysicsSpace().add(auv.getAUVNode());
        }
    }

    /**
     * Add the AUV to the mini-map state.
     * @param auv
     */
    public void addAUVtoMap(AUV auv) {
        if (mars.getStateManager().getState(MapState.class) != null) {
            MapState mapState = mars.getStateManager().getState(MapState.class);
            mapState.addAUV(auv);
        }
    }

    /*
    * Call the init method of all AUVs.
    */
    private void initAUV(AUV auv) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Initialising AUV " + auv.getName() + "...", "");
        auv.init();
    }

    /**
     * GUI stuff.
     */
    public void deselectAllAUVs() {
        //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "DeSelecting all AUVs...", "");
        for (String elem : auvs.keySet()) {
            AUV auv = auvs.get(elem);
            auv.setSelected(false);
        }
    }

    /**
     * GUI stuff.
     * 
     * @param auv
     */
    public void deselectAUV(AUV auv) {
        //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "DeSelecting all AUVs...", "");
        auv.setSelected(false);
    }

    /**
     *
     */
    public void cleanup() {
        for (String elem : auvs.keySet()) {
            AUV auv = auvs.get(elem);
            auv.cleanupAUV();
        }
        auvs.clear();
    }

    /**
     * GUI stuff.
     * 
     * @return
     */
    public AUV getSelectedAUV() {
        //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Getting selected AUV...", "");
        for (String elem : auvs.keySet()) {
            AUV auv = auvs.get(elem);
            if (auv.isSelected()) {
                return auv;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "AUVs";
    }

    /**
     *
     * @param listener
     */
    public void addAdListener(MARSClient listener) {
        listeners.add(MARSClient.class, listener);
    }

    /**
     *
     * @param listener
     */
    public void removeAdListener(MARSClient listener) {
        listeners.remove(MARSClient.class, listener);
    }

    /**
     *
     */
    public void removeAllListener() {
        //listeners.
    }

    /**
     *
     * @param event
     */
    public void notifyAdvertisement(MARSClientEvent event) {
        for (MARSClient l : listeners.getListeners(MARSClient.class)) {
            l.onNewData(event);
        }
    }

    /**
     *
     * @param event
     */
    protected synchronized void notifySafeAdvertisement(MARSClientEvent event) {
        notifyAdvertisement(event);
    }
}
