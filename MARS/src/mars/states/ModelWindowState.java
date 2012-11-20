/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.states;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import mars.MARS_Main;
import mars.MARS_Settings;
import mars.auv.AUV_Manager;

/**
 * this is the state for the modeling window. i.e. adding a new auv or changing the position of sensors.
 * @author Tosik
 */
public class ModelWindowState extends AbstractAppState{

    private Node rootNode = new Node("Root Node");
    private Node auvsNode = new Node("AUVS Node");
    private AssetManager assetManager;
    private MARS_Main mars;
    private AUV_Manager auv_manager;
    private MARS_Settings mars_settings;
    

    /**
     * 
     * @param assetManager
     */
    public ModelWindowState(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    /**
     * 
     * @return
     */
    public Node getRootNode(){
        return rootNode;
    }
    
    @Override
    public void cleanup() {
        rootNode.detachAllChildren();
        super.cleanup();
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        if(!super.isInitialized()){
            if(app instanceof MARS_Main){
                mars = (MARS_Main)app;
                assetManager = mars.getAssetManager();
            }else{
                throw new RuntimeException("The passed application is not of type \"MARS_Main\"");
            }

            mars.getFlyByCamera().setEnabled(false);
            rootNode.attachChild(auvsNode);
        }
        super.initialize(stateManager, app);
    }
    
    /**
     * 
     * @param auv_manager
     */
    public void setAuv_manager(AUV_Manager auv_manager) {
        this.auv_manager = auv_manager;
    }
    
    /**
     * 
     * @param mars_settings
     */
    public void setMars_settings(MARS_Settings mars_settings) {
        this.mars_settings = mars_settings;
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

    @Override
    public void stateAttached(AppStateManager stateManager) {
        super.stateAttached(stateManager);
    }

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

        rootNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();
    }
    
    private void setupLight(){
        DirectionalLight sun = new DirectionalLight();
        sun.setColor(new ColorRGBA(1f, 1f, 1f, 0f));
        sun.setDirection(new Vector3f(0f,-1f,0f));
        rootNode.addLight(sun);
    }    
}
