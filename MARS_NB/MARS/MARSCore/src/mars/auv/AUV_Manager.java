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
import javax.swing.tree.TreePath;
import mars.Collider;
import mars.PhysicalEnvironment;
import mars.MARS_Settings;
import mars.MARS_Main;
import mars.gui.tree.UpdateState;
import mars.recorder.RecordManager;
import mars.states.SimState;
import mars.ros.MARSNodeMain;
import mars.server.MARSClient;
import mars.server.MARSClientEvent;
import mars.states.MapState;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.lookup.ServiceProvider;

/**
 * Creates an AUV_Manger. You register your auv's here.
 * @author Thomas Tosik
 */
@ServiceProvider(service=AUV_Manager.class)
public class AUV_Manager implements UpdateState{

    //auv HashMap to store and load auv's
    private HashMap<String,AUV> auvs = new HashMap<String,AUV> ();
    private Collider RayDetectable;
    private Node sceneReflectionNode;
    private Node AUVsNode;
    private MARS_Main mars;
    private PhysicalEnvironment physical_environment;
    private MARS_Settings simauv_settings;
    private BulletAppState bulletAppState;
    private Node rootNode;
    private SimState simstate;
    private CommunicationManager com_manager;
    private RecordManager recManager;
    private HashMap<String,MARSNodeMain> mars_nodes = new HashMap<String, MARSNodeMain>();
    private EventListenerList listeners = new EventListenerList();

    /**
     *
     * @param simstate 
     */
    public AUV_Manager(SimState simstate) {
       //set the logging
       try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
            // Add to the desired logger
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.addHandler(handler);
        } catch (IOException e) { }

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
     * @return
     */
    public boolean isEmpty(){
        return auvs.isEmpty();
    }

    /**
     *
     * @param key Which unique registered auv do we want?
     * @return The auv that we asked for
     */
    public AUV getAUV(String key){
        return auvs.get(key);
    }

    /**
     *
     * @return
     */
    public HashMap<String,AUV> getAUVs(){
        return auvs;
    }
    
    /**
     *
     * @param oldName
     * @param newName
     */
    public void updateAUVName(String oldName, String newName){
        AUV auv = auvs.get(oldName);
        auv.setName(newName);
        auvs.remove(oldName);
        auvs.put(newName, auv);
    }
    
    /**
     *
     * @return
     */
    public ArrayList<Class<? extends AUV>> getAUVClasses(){
        ArrayList<Class<? extends AUV>> ret = new ArrayList<Class<? extends AUV>>();
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            Class<? extends AUV> aClass = auv.getClass();
            if(!ret.contains(aClass)){
                ret.add(aClass);
            }
        }
        return ret;
    }
    
    /**
     *
     * @param classNameString 
     * @return
     */
    public ArrayList getAUVsOfClass(String classNameString){
        ArrayList ret = new ArrayList();        
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            try {
                if (Class.forName(classNameString).isInstance(auv)) {
                    ret.add(auv);
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(BasicAUV.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }
    
    /**
     *
     * @param classNameString
     * @return
     */
    public boolean hasAUVsOfClass(String classNameString){
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            try {
                return (Class.forName(classNameString).isInstance(auv));
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(BasicAUV.class.getName()).log(Level.SEVERE, null, ex);
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
     * @param recManager
     */
    public void setRecManager(RecordManager recManager) {
        this.recManager = recManager;
    }

    /**
     *
     * @return
     */
    public MARS_Settings getSimauv_settings() {
        return simauv_settings;
    }

    /**
     * 
     * @param simauv_settings
     */
    public void setSimauv_settings(MARS_Settings simauv_settings) {
        this.simauv_settings = simauv_settings;
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
    public HashMap<String,MARSNodeMain> getMARSNodes() {
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
    public void setMARSNodes(HashMap<String,MARSNodeMain> mars_nodes) {
        this.mars_nodes = mars_nodes;
    }
    
    /**
     * 
     */
    public void updateMARSNode(){
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            if(auv.getAuv_param().isEnabled()){
                auv.setROS_Node(getMARSNodeForAUV(elem));
                auv.initROS();
            }
        }
    }
            
    /**
     *
     * @param tpf
     */
    public void updateAllAUVs(float tpf){
        updateForcesOfAUVs(tpf);
        updateActuatorsOfAUVs(tpf);
        updateSensorsOfAUVs(tpf);
        updateCommunicationOfAUVs(tpf);
        updateWaypointsOfAUVs(tpf);
        updateAccumulatorsOfAUVs(tpf);
        updateRecord(tpf);
    }
    
    /**
     *
     */
    public void publishSensorsOfAUVs(){
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            if(auv.getAuv_param().isEnabled()){
                auv.publishSensorsOfAUV();
            }
        }
    }
    
    /**
     *
     */
    public void publishActuatorsOfAUVs(){
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            if(auv.getAuv_param().isEnabled()){
                auv.publishActuatorsOfAUV();
            }
        }
    }

    /**
     *
     * @param tpf
     */
    private void updateWaypointsOfAUVs(float tpf){
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            if(auv.getAuv_param().isEnabled()){
                auv.updateWaypoints(tpf);
            }
        }
    }

    /**
     * The camera view direction/location will be updated here. Normaly in a SimpleUpdate method in the Main_Loop.
     * @param tpf
     */
    private void updateSensorsOfAUVs(float tpf){
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            if(auv.getAuv_param().isEnabled()){
                auv.updateSensors(tpf);
            }
        }
    }
    
    /*
     * 
     */
    private void updateActuatorsOfAUVs(float tpf){
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            if(auv.getAuv_param().isEnabled()){
                auv.updateActuators(tpf);
            }
        }
    }

    /**
     * In this method the communication between the auv's through underwater modems should be done
     * @param tpf
     */
    @Deprecated
    private void updateCommunicationOfAUVs(float tpf){
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            if(auv.getAuv_param().isEnabled()){
                //auv.updateSensors(tpf);
            }
        }
    }

    /**
     * The forces of the auv's will be updated here. Normaly in a SimpleUpdate method in the Main_Loop.
     * @param tpf
     */
    private void updateForcesOfAUVs(final float tpf){
        for ( String elem : auvs.keySet() ){
            final AUV auv = (AUV)auvs.get(elem);
            if(auv.getAuv_param().isEnabled()){
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
    
    private void updateAccumulatorsOfAUVs(float tpf){
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            if(auv.getAuv_param().isEnabled()){
                auv.updateAccumulators(tpf);
            }
        }
    }
    
    private void updateRecord(float tpf){
        if(recManager != null){
            if(recManager.isEnabled()){
                for ( String elem : auvs.keySet() ){
                    AUV auv = (AUV)auvs.get(elem);
                    if(auv.getAuv_param().isEnabled()){
                        recManager.update(auv);
                    }
                }
            }
        }
    }

    /**
     *
     */
    public void clearForcesOfAUVs(){
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            if(auv.getAuv_param().isEnabled()){
                auv.clearForces();
            }
        }
    }

    /**
     *
     */
    public void resetAllAUVs(){
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            if(auv.getAuv_param().isEnabled()){
                auv.reset();
            }
        }
    }

    /**
     * With this method you register pre-created auv like hanse.
     * @param name
     * @param auv
     */
    public void registerAUV( String name, AUV auv ){
        auv.setName(name);
        final AUV fin_auv = auv;
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "AUV " + auv.getName() + " added...", "");
        Future fut = mars.enqueue(new Callable() {
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
    public void registerAUV( AUV auv ){
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "AUV " + auv.getName() + " added...", "");
        final AUV fin_auv = auv;
        Future fut = mars.enqueue(new Callable() {
             public Void call() throws Exception {
                final ProgressHandle progr = ProgressHandleFactory.createHandle("AUVManager: " + fin_auv.getName());
                progr.start();
                progr.progress( "Loading AUV: " + fin_auv.getName());
                auvs.put(fin_auv.getName(), fin_auv);
                preloadAUV(fin_auv);
                progr.finish();
                return null;
            }
        });
    }

    /**
     *
     * @param arrlist
     */
    public void registerAUVs( ArrayList arrlist ){
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Adding AUVs...", "");
        Iterator iter = arrlist.iterator();
        while(iter.hasNext() ) {
            AUV auv = (AUV)iter.next();
            registerAUV(auv);
        }
    }
    
    /**
     *
     * @param auv_name
     */
    public void deregisterAUV( String auv_name ){
        final String fin_auv_name = auv_name;
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "AUV " + auv_name + " deleted...", "");
        Future fut = mars.enqueue(new Callable() {
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
    public void deregisterAUV( AUV auv ){
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "AUV " + auv.getName() + " deleted...", "");
        final AUV fin_auv = auv;
        Future fut = mars.enqueue(new Callable() {
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
    public void deregisterAUVNoFuture( AUV auv ){
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "AUV " + auv.getName() + " deleted...", "");
        final AUV fin_auv = auv;
        removeAUVFromScene(fin_auv);
         auvs.remove(fin_auv.getName());
    }

    /**
     *
     * @param auvs
     */
    public void deregisterAUVs( ArrayList auvs ){
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Deleting AUVs...", "");
        Iterator iter = auvs.iterator();
        while(iter.hasNext() ) {
            AUV auv = (AUV)iter.next();
            deregisterAUV(auv);
        }
    }

    /**
     *
     */
    public void deregisterAllAUVs(){
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Deleting All AUVs...", "");
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            deregisterAUV(auv);
        }
    }

    @Deprecated
    private void addAUVsToScene(){
        initAUVs();
        //addAUVsToNode(sceneReflectionNode);
        addAUVsToNode(AUVsNode);
        addAUVsToBulletAppState(bulletAppState);
    }

    private void preloadAUV(AUV auv){
        //if(auv.getAuv_param().isEnabled()){
            auv.setState(simstate);
            auv.setMARS_Settings(simauv_settings);
            auv.setPhysical_environment(physical_environment);
            auv.setCommunicationManager(com_manager);
            auv.setROS_Node(getMARSNodeForAUV(auv.getName()));
            initAUV(auv);
        if(auv.getAuv_param().isEnabled()){    
            //initAUV(auv);
            //addAUVToNode(auv,sceneReflectionNode);
            addAUVToNode(auv,AUVsNode);
            addAUVToBulletAppState(auv,bulletAppState);
            addAUVtoMap(auv);
        }
    }
    
    /**
     * Enables/Disables a preloaded AUV. Be sure to enable an AUV only after the update cycle(future/get).
     * @param auv
     * @param enable
     */
    public void enableAUV(AUV auv, boolean enable){
        enableAUV(auv.getName(),enable);
    }
    
    private void enableAUV(String auv_name, boolean enable){
        AUV auv = (AUV)auvs.get(auv_name);
        if(enable){
            addAUVToScene(auv);
        }else{
            removeAUVFromScene(auv);
        }
    }

    private void addAUVToScene(AUV auv){
        auv.addDragOffscreenView();
        addAUVToNode(auv,AUVsNode);
        addAUVToBulletAppState(auv,bulletAppState);
    }

    private void removeAUVFromScene(AUV auv){
        bulletAppState.getPhysicsSpace().remove(auv.getAUVNode());
        if(auv.getGhostControl() != null){//only try too remove when ghost control exists
            bulletAppState.getPhysicsSpace().remove(auv.getGhostAUV());
        }
        RayDetectable.detachChild(auv.getSelectionNode());
        auv.cleanupOffscreenView();
        auv.getSelectionNode().removeFromParent();
    }

    /**
     * We must add the auv to some Node that is in the RootNode to render them.
     * @param node
     */
    private void addAUVToNode(AUV auv, Node node){
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Adding AUV's to Node: " + node.getName(), "");
        if(auv.getAuv_param().isRayDetectable()){
            RayDetectable.attachChild(auv.getSelectionNode());
        }
        node.attachChild(auv.getSelectionNode());
    }

    /**
     * We must add the auv to some Node that is in the RootNode to render them.
     * @param node
     */
    private void addAUVsToNode(Node node){
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Adding AUV's to Node: " + node.getName(), "");
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            addAUVToNode(auv,node);
        }
    }

    /**
     * We must add the auv to a BulletAppState so the physics can be applied.
     * @param auv
     * @param bulletAppState
     */
    public void addAUVToBulletAppState(AUV auv,BulletAppState bulletAppState){
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Adding AUV " + auv.getName() + " to BulletAppState...", "");
        bulletAppState.getPhysicsSpace().add(auv.getAUVNode());
        if(auv.getGhostControl() != null){
            //bulletAppState.getPhysicsSpace().add(auv.getGhostControl());
            bulletAppState.getPhysicsSpace().add(auv.getGhostAUV());
            bulletAppState.getPhysicsSpace().addCollisionListener(auv.getGhostControl());
            bulletAppState.getPhysicsSpace().addTickListener(auv.getGhostControl());
        }
    }

    /**
     * We must add the auv's to a BulletAppState so the physics can be applied.
     * @param bulletAppState
     */
    public void addAUVsToBulletAppState(BulletAppState bulletAppState){
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Adding AUV's to BulletAppState...", "");
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            bulletAppState.getPhysicsSpace().add(auv.getAUVNode());
        }
    }
    
    /**
     *
     * @param auv
     */
    public void addAUVtoMap(AUV auv){
        if(mars.getStateManager().getState(MapState.class) != null){
            MapState mapState = (MapState)mars.getStateManager().getState(MapState.class);
            mapState.addAUV(auv);
        }
    }

    /**
     *
     */
    private void initAUVs(){
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Initialising AUV's...", "");
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            auv.init();
        }
    }

    private void initAUV(AUV auv){
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Initialising AUV " + auv.getName() + "...", "");
        auv.init();
    }

    private void initAUV(String auv_name){
        AUV auv = (AUV)getAUV(auv_name);
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Initialising AUV " + auv.getName() + "...", "");
        auv.init();
    }
    
     /**
     *
     */
    public void deselectAllAUVs(){
        //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "DeSelecting all AUVs...", "");
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            auv.setSelected(false);
        }
    }
    
    /**
     *
     * @param auv 
     */
    public void deselectAUV(AUV auv){
        //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "DeSelecting all AUVs...", "");
        auv.setSelected(false);
    }
    
    /**
     *
     */
    public void cleanup(){
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            auv.cleanupAUV();
        }
        auvs.clear();
    }
    
    /**
     * 
     * @return
     */
    public AUV getSelectedAUV(){
        //Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Getting selected AUV...", "");
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            if(auv.isSelected()){
                return auv;
            }
        }
        return null;
    }
    
    @Override
    public String toString(){
        return "AUVs";
    }

    /**
     *
     * @param path
     */
    @Override
    public void updateState(TreePath path) {
        AUV auv = (AUV)path.getPathComponent(1);
        auv.updateState(path);
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
