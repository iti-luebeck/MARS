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
package mars.auv;

import com.jme3.bullet.BulletAppState;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import mars.PhysicalEnvironment;
import mars.object.MARSObjectManager;
import mars.states.MapState;
import mars.states.SimState;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 * Creates an AUV_Manger. You register your auv's here. The complete life cycle of an AUV is managed here.
 *
 * @author Thomas Tosik
 */
@ServiceProvider(service = AUV_Manager.class)
public class AUV_Manager extends MARSObjectManager<AUV> {

    private Node AUVsNode;
    private PhysicalEnvironment physical_environment;
    private CommunicationManager com_manager;
    private EventListenerList listeners = new EventListenerList();

    /**
     *
     * @param simstate
     */
    public AUV_Manager(SimState simstate) {
        super(simstate);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Creating AUV_MANAGER...", "");
        this.AUVsNode = simstate.getAUVNodes();
    }

    /**
     *
     */
    public AUV_Manager() {
        super();
    }

    /**
     *
     * @param oldName
     * @param newName
     */
    public void updateAUVName(String oldName, String newName) {
        AUV auv = marsObjects.get(oldName);
        auv.setName(newName);
        marsObjects.remove(oldName);
        marsObjects.put(newName, auv);
    }

    /**
     *
     * @return All AUV classes.
     */
    public ArrayList<Class<? extends AUV>> getAUVClasses() {
        ArrayList<Class<? extends AUV>> ret = new ArrayList<Class<? extends AUV>>();
        for (String elem : marsObjects.keySet()) {
            AUV auv = marsObjects.get(elem);
            Class<? extends AUV> aClass = auv.getClass();
            if (!ret.contains(aClass)) {
                ret.add(aClass);
            }
        }
        return ret;
    }

    /**
     *
     * @param name
     * @return The requested AUV or null if not existing
     */
    public AUV getAUV(String name) {
        return marsObjects.get(name);
    }

    /**
     *
     * @param classNameString
     * @return All AUVs of a specific class.
     */
    public ArrayList<AUV> getAUVsOfClass(String classNameString) {
        ArrayList<AUV> ret = new ArrayList<AUV>();
        for (String elem : marsObjects.keySet()) {
            AUV auv = marsObjects.get(elem);
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
        for (String elem : marsObjects.keySet()) {
            AUV auv = marsObjects.get(elem);
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
        for (String elem : marsObjects.keySet()) {
            AUV auv = marsObjects.get(elem);
            if (auv.getAuv_param().isEnabled()) {
                auv.publishSensorsOfAUV();
            }
        }
    }

    /**
     *
     */
    public void publishActuatorsOfAUVs() {
        for (String elem : marsObjects.keySet()) {
            AUV auv = marsObjects.get(elem);
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
        for (String elem : marsObjects.keySet()) {
            AUV auv = marsObjects.get(elem);
            if (auv.getAuv_param().isEnabled()) {
                auv.updateWaypoints(tpf);
            }
        }
    }

    /**
     * The camera view direction/location will be updated here. Normaly in a SimpleUpdate method in the Main_Loop.
     *
     * @param tpf
     */
    private void updateSensorsOfAUVs(float tpf) {
        for (String elem : marsObjects.keySet()) {
            AUV auv = marsObjects.get(elem);
            if (auv.getAuv_param().isEnabled()) {
                auv.updateSensors(tpf);
            }
        }
    }

    /*
     * 
     */
    private void updateActuatorsOfAUVs(float tpf) {
        for (String elem : marsObjects.keySet()) {
            AUV auv = marsObjects.get(elem);
            if (auv.getAuv_param().isEnabled()) {
                auv.updateActuators(tpf);
            }
        }
    }

    /**
     * In this method the communication between the auv's through underwater modems should be done
     *
     * @param tpf
     */
    @Deprecated
    private void updateCommunicationOfAUVs(float tpf) {
        for (String elem : marsObjects.keySet()) {
            AUV auv = marsObjects.get(elem);
            if (auv.getAuv_param().isEnabled()) {
                //auv.updateSensors(tpf);
            }
        }
    }

    /**
     * The forces of the auv's will be updated here. Normaly in a SimpleUpdate method in the Main_Loop.
     *
     * @param tpf
     */
    private void updateForcesOfAUVs(final float tpf) {
        for (String elem : marsObjects.keySet()) {
            final AUV auv = marsObjects.get(elem);
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
        for (String elem : marsObjects.keySet()) {
            AUV auv = marsObjects.get(elem);
            if (auv.getAuv_param().isEnabled()) {
                auv.updateAccumulators(tpf);
            }
        }
    }

    /**
     *
     */
    public void clearForcesOfAUVs() {
        for (String elem : marsObjects.keySet()) {
            AUV auv = marsObjects.get(elem);
            if (auv.getAuv_param().isEnabled()) {
                auv.clearForces();
            }
        }
    }

    /**
     *
     */
    public void resetAllAUVs() {
        for (String elem : marsObjects.keySet()) {
            AUV auv = marsObjects.get(elem);
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
    @Override
    public void register(String name, AUV auv) {
        auv.setName(name);
        final AUV fin_auv = auv;
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "AUV " + auv.getName() + " added...", "");
        mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                marsObjects.put(fin_auv.getName(), fin_auv);
                preloadAUV(fin_auv);
                return null;
            }
        });
    }

    /**
     *
     * @param auv
     */
    @Override
    public void register(AUV auv) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "AUV " + auv.getName() + " added...", "");
        final AUV fin_auv = auv;
        mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                final ProgressHandle progr = ProgressHandleFactory.createHandle("AUVManager: " + fin_auv.getName());
                progr.start();
                progr.progress("Loading AUV: " + fin_auv.getName());
                marsObjects.put(fin_auv.getName(), fin_auv);
                preloadAUV(fin_auv);
                progr.finish();
                content.add(new NodeRefreshEvent());
                return null;
            }
        });
    }

    /**
     *
     * @param auv
     */
    @Override
    public void deregister(AUV auv) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "AUV " + auv.getName() + " deleted...", "");
        final AUV fin_auv = auv;
        mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                removeFromScene(fin_auv);
                marsObjects.remove(fin_auv.getName());
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
        removeFromScene(fin_auv);
        marsObjects.remove(fin_auv.getName());
    }

    /*
     * Make some loading so we don't have to wait longer in MARS. Used for deactivation.
     */
    private void preloadAUV(AUV auv) {
        //if(auv.getAuv_param().isEnabled()){
        auv.setState(simstate);
        auv.setMARS_Settings(getMARS_settings());
        auv.setPhysical_environment(physical_environment);
        auv.setCommunicationManager(com_manager);
        init(auv);
        if (auv.getAuv_param().isEnabled()) {
            //initAUV(auv);
            //addAUVToNode(auv,sceneReflectionNode);
            addAUVToNode(auv, AUVsNode);
            addAUVToBulletAppState(auv, bulletAppState);
            addAUVtoMap(auv);
        }else{
            auv.setPhysicalExchangerVisible(false);
        }
    }

    /*
     * Adds the AUV node to the scengraph(rootNode).
     */
    @Override
    protected void addToScene(AUV auv) {
        auv.addDragOffscreenView();
        addAUVToNode(auv, AUVsNode);
        addAUVToBulletAppState(auv, bulletAppState);
    }

    /*
     * Removes the AUV node from the scengraph(rootNode).
     */
    @Override
    protected void removeFromScene(AUV auv) {
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
        for (String elem : marsObjects.keySet()) {
            AUV auv = marsObjects.get(elem);
            bulletAppState.getPhysicsSpace().add(auv.getAUVNode());
        }
    }

    /**
     * Add the AUV to the mini-map state.
     *
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
    @Override
    protected void init(AUV auv) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Initialising AUV " + auv.getName() + "...", "");
        auv.init();
    }

    /**
     * GUI stuff.
     *
     * @param auv
     */
    public void deselectAUV(AUV auv) {
        auv.setSelected(false);
    }

    @Override
    public String toString() {
        return "AUVs";
    }

    /**
     *
     */
    public void removeAllListener() {
        //listeners.
    }
}
