package org.FishSim.SwarmSimulation;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import org.openide.util.lookup.ServiceProvider;
import java.util.ArrayList;
import mars.Initializer;
import mars.MARS_Main;
import mars.states.AppStateExtension;
import mars.states.SimState;

/**
 *
 * @author Mandy Feldvoß
 */
@ServiceProvider(service=AbstractAppState.class)
public class FishSim extends AbstractAppState implements AppStateExtension {
    //MARS variables
    private static FishSim instance = null;
    protected SwarmPanel sPanel;
    protected FoodSourcePanel fSPanel;
    protected FoodSourceMapPanel fSMPanel;
    private Node rootNode = new Node("FishSimState Root Node");
    private MARS_Main mars;
    private Initializer initer;
    private BulletAppState bulletAppState;
    private boolean swarmsChanged = false;
    private boolean fSChanged = false;
    private boolean fSMChanged = false;
    
    private ArrayList<Swarm> removedSwarms = new ArrayList<Swarm>();
    private ArrayList<String> newSwarms = new ArrayList<String>();
    private ArrayList<Swarm> swarms = new ArrayList<Swarm>();
    
    private ArrayList<FoodSource> removedSources = new ArrayList<FoodSource>();
    private ArrayList<String> newSources = new ArrayList<String>();
    private ArrayList<FoodSource> sources = new ArrayList<FoodSource>();
    
    private ArrayList<FoodSourceMap> sourceMaps = new ArrayList<FoodSourceMap>();
 
    /**
     *
     *
     */
    public FishSim() {
        super();
    }
    
        /**
     *
     * @param main
     * @deprecated
     */
    @Deprecated
    public FishSim(Application main){
        super();
        this.mars = (MARS_Main) main;
    }
 
    /**
     *
     * @param stateManager
     * @param app
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app){
        if(!super.isInitialized()){
            if(app instanceof MARS_Main){
                mars = (MARS_Main)app;
                //assetManager = mars.getAssetManager();
                mars.getRootNode().attachChild(getRootNode());
                if(stateManager.getState(SimState.class)!=null){
                    initer = stateManager.getState(SimState.class).getIniter();
                }else{
                    throw new RuntimeException("SimState not found/initialized!");
                }
                if(stateManager.getState(BulletAppState.class)!=null){
                    bulletAppState = stateManager.getState(BulletAppState.class);
                }else{
                    throw new RuntimeException("BulletAppState not found/initialized!");
                }
            }else{
                throw new RuntimeException("The passed application is not of type \"MARS_Main\"");
            } 
        }
        
        super.initialize(stateManager, app);
        setInstance(this);
    }
    
    
    /**
     *
     * @return rootNode
     */
    @Override
    public Node getRootNode(){
        return rootNode;
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
    }
    
    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

    @Override
    public boolean isInitialized() {
        return super.isInitialized();
    }

    @Override
    public void postRender() {
        if (!super.isEnabled()) {
            return;
        }
        super.postRender();
    }

    /**
     *
     * @param rm
     */
    @Override
    public void render(RenderManager rm) {
        if (!super.isEnabled()) {
            return;
        }
        super.render(rm);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if(!enabled){
            rootNode.setCullHint(Spatial.CullHint.Always);
        }else{
            rootNode.setCullHint(Spatial.CullHint.Never);
        }
    }

    /**
     *
     * @param stateManager
     */
    @Override
    public void stateAttached(AppStateManager stateManager) {
        super.stateAttached(stateManager);
    }

    /**
     *
     * @param stateManager
     */
    @Override
    public void stateDetached(AppStateManager stateManager) {
        super.stateDetached(stateManager);
    }

       
    @Override
    public void update(float tpf) {
        if (!super.isEnabled()) {
            return;
        }
        super.update(tpf);
        newSwarms();
        newFoodSources();
        
        updatePanels();
        
        for(int i = 0; i < swarms.size(); i++){
            swarms.get(i).move(tpf);
        }
        
        removeSwarms();
        removeFoodSources();
        
        rootNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();
    }
    
    /**
     *
     * @param pos   Position of the obstacle
     * @param size  Size of the obstacle
     */
    public void createObstacle(Vector3f pos, float size){
        SphereCollisionShape sphere = new SphereCollisionShape(size);
        RigidBodyControl obstacle = new RigidBodyControl(sphere, 1);
        obstacle.setKinematic(true);
        obstacle.setCollisionGroup(6);
        obstacle.setCollideWithGroups(4);
        obstacle.setPhysicsLocation(pos);
        getBulletAppState().getPhysicsSpace().add(obstacle);
    }
    
    /**
     *
     * @return
     */
    public MARS_Main getMain(){
        return mars;
    }
    
        /**
     *
     * @return
     */
//    public Initializer getIniter() {
//        return initer;
//    }
    
        /**
     *
     * @return
     */
    public BulletAppState getBulletAppState() {
        return bulletAppState;
    }
    
    public float getCurrentWaterHeight(float x, float z){
        return initer.getCurrentWaterHeight(x, z);
    }

    /**
     *
     * @param cam
     */
    @Override
    public void setCamera(Camera cam) {        
    }
    
    private void updatePanels(){
        if(sPanel != null && fSPanel != null && fSMPanel != null){
            if(swarmsChanged || fSChanged){
                if(swarmsChanged){
                    sPanel.updateSwarmList(swarms);
                    swarmsChanged = false;
                }
                if(fSChanged){
                    fSPanel.updateFoodSources(sources);
                    fSChanged = false;
                }
                fSMPanel.updateFoodSources(sources, swarms);
            }
            
            if(fSMChanged){
                sPanel.updateFoodSourceList(sourceMaps);
                fSMPanel.updateFoodSourceMapList(sourceMaps);
                fSMChanged = false;
            }
        }
    }
    
    public void addSwarm(String values){
        newSwarms.add(values);
    }
    
    public void addFoodSource(String values){
        newSources.add(values);
    }
    
    public void addFoodSourceMap(){
        sourceMaps.add(new FoodSourceMap());
        fSMPanel.updateFoodSourceMapList(sourceMaps);
        fSMChanged = true;
    }
    
    private void newSwarms(){
        for(int i = 0; i < newSwarms.size(); i++){
            String[] values = newSwarms.get(i).split(" ");
            boolean anim = false;
            if (values[10].equals("true")){
                anim = true;
            }  
            Swarm swarm = new Swarm(this, Integer.parseInt(values[0]), new Vector3f(Float.parseFloat(values[1]), Float.parseFloat(values[2]), Float.parseFloat(values[3])), Float.parseFloat(values[4]), new Vector3f(Float.parseFloat(values[5]), Float.parseFloat(values[6]), Float.parseFloat(values[7])), Integer.parseInt(values[8]), values[9], anim);
            if(Integer.parseInt(values[11]) >= 0){
                swarm.setFoodSourceMap(sourceMaps.get(Integer.parseInt(values[11])));
            }
            swarm.setMoveSpeed(Float.parseFloat(values[12]));
            swarm.setRotationSpeed(Float.parseFloat(values[13]));
            swarms.add(swarm);
            swarmsChanged = true;
        }
        newSwarms.clear();
    }
    
    public void newSwarm(Swarm swarm){
        swarms.add(swarm);
        swarmsChanged = true;
    }
    
    private void newFoodSources(){
        for(int i = 0; i < newSources.size(); i++){
            String[] values = newSources.get(i).split(" ");
            sources.add( new FoodSource(this, Float.parseFloat(values[0]), new Vector3f(Float.parseFloat(values[1]), Float.parseFloat(values[2]), Float.parseFloat(values[3]))));
            fSChanged = true;
        }
            newSources.clear();
    }
    
    protected void removeSwarm(int i){
        removedSwarms.add(swarms.get(i));
    }
    
    protected void removeSwarm(Swarm swarm){
        removedSwarms.add(swarm);
    }
    
    protected void removeFoodSource(int i){
        removedSources.add(sources.get(i));
    }
    
    protected void removeFoodSource(FoodSource source){
        removedSources.add(source);
    }
    
    public void removeFoodSourceMap(int idx){
        sourceMaps.remove(idx);
        fSMChanged = true;
    }
    
    private void removeSwarms(){
        for(int i = 0; i < removedSwarms.size(); i++){
            removedSwarms.get(i).delete();
            swarms.remove(removedSwarms.get(i));
            swarmsChanged = true;
        }
        removedSwarms.clear();
    }
    
        private void removeFoodSources(){
        for(int i = 0; i < removedSources.size(); i++){
            removedSources.get(i).delete();
            sources.remove(removedSources.get(i));
            fSChanged = true;
        }
        removedSources.clear();
    }
    
    public void addToMap(int mapIdx, int list, int sourceIdx){
        if(list == 0){
            sourceMaps.get(mapIdx).add(swarms.get(sourceIdx));
        }else{
            sourceMaps.get(mapIdx).add(sources.get(sourceIdx));
        }
    }
    
    public int getFoodSourcesSize(){
        return sources.size();
    }
    
    private void setInstance(FishSim sim){
        instance = sim;
    }
    
    public static FishSim getInstance(){
        return instance;
    }
    
    public void setSwarmPanel(SwarmPanel sPanel){
      this.sPanel = sPanel;  
    }
    
    public void setFoodSourcePanel(FoodSourcePanel fSPanel){
      this.fSPanel = fSPanel;  
    }
    
    public void setFoodSourceMapPanel(FoodSourceMapPanel fSMPanel){
      this.fSMPanel = fSMPanel;  
    }
}
