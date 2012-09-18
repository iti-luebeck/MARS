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
import mars.PhysicalEnvironment;
import mars.MARS_Settings;
import mars.MARS_Main;
import mars.states.SimState;
import mars.ros.MARSNodeMain;
import mars.sensors.InfraRedSensor;
import mars.sensors.sonar.Sonar;

/**
 * Creates an AUV_Manger. You register your auv's here.
 * @author Thomas Tosik
 */
public class AUV_Manager {

    //auv HashMap to store and load auv's
    private HashMap<String,AUV> auvs = new HashMap<String,AUV> ();
    private Node SonarDetectableNode;
    private Node sceneReflectionNode;
    private Node AUVsNode;
    private MARS_Main mars;
    private PhysicalEnvironment physical_environment;
    private MARS_Settings simauv_settings;
    private BulletAppState bulletAppState;
    private Node rootNode;
    private SimState simstate;
    private Communication_Manager com_manager;
    private HashMap<String,MARSNodeMain> mars_nodes = new HashMap<String, MARSNodeMain>();

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
        this.SonarDetectableNode = simstate.getSonarDetectableNode();
        this.sceneReflectionNode = simstate.getSceneReflectionNode();
        this.AUVsNode = simstate.getAUVsNode();
        this.bulletAppState = simstate.getBulletAppState();
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
    public Communication_Manager getCommunicationManager() {
        return com_manager;
    }

    /**
     * 
     * @param com_manager
     */
    public void setCommunicationManager(Communication_Manager com_manager) {
        this.com_manager = com_manager;
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
     * @return
     */
    public MARSNodeMain getMARSNodeForAUV(String auv_name) {
        return mars_nodes.get(auv_name);
    }

    /**
     * 
     * @param mars_node
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
        updateValuesOfAUVs(tpf);
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
     *
     * @param tpf
     */
    @Deprecated
    private void updateValuesOfAUVs(float tpf){
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            if(auv.getAuv_param().isEnabled()){
                auv.updateValues(tpf);
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
                auvs.put(fin_auv.getName(), fin_auv);
                preloadAUV(fin_auv);
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
            auv.setSimauv_settings(simauv_settings);
            auv.setPhysical_environment(physical_environment);
            auv.setCommunicationManager(com_manager);
            auv.setROS_Node(getMARSNodeForAUV(auv.getName()));
            initAUV(auv);
        if(auv.getAuv_param().isEnabled()){    
            //initAUV(auv);
            //addAUVToNode(auv,sceneReflectionNode);
            addAUVToNode(auv,AUVsNode);
            addAUVToBulletAppState(auv,bulletAppState);
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
        auv.cleanupOffscreenView();
        auv.getSelectionNode().removeFromParent();
    }

    /**
     * We must add the auv to some Node that is in the RootNode to render them.
     * @param node
     */
    private void addAUVToNode(AUV auv, Node node){
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Adding AUV's to Node: " + node.getName(), "");

        node.attachChild(auv.getSelectionNode());

        ArrayList sons = auv.getSensorsOfClass(Sonar.class.getName());
        Iterator iter = sons.iterator();
        while(iter.hasNext() ) {
            Sonar son = (Sonar)iter.next();
            son.setDetectable(SonarDetectableNode);
        }
        
        ArrayList infras = auv.getSensorsOfClass(InfraRedSensor.class.getName());
        Iterator iter2 = infras.iterator();
        while(iter2.hasNext() ) {
            InfraRedSensor infra = (InfraRedSensor)iter2.next();
            infra.setDetectable(SonarDetectableNode);
        }
    }

    /**
     * We must add the auv to some Node that is in the RootNode to render them.
     * @param node
     */
    private void addAUVsToNode(Node node){
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Adding AUV's to Node: " + node.getName(), "");
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            node.attachChild(auv.getSelectionNode());

            ArrayList sons = auv.getSensorsOfClass(Sonar.class.getName());
            Iterator iter = sons.iterator();
            while(iter.hasNext() ) {
                Sonar son = (Sonar)iter.next();
                son.setDetectable(SonarDetectableNode);
            }
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
}
