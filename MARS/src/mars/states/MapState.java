/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.states;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import mars.MARS_Main;

/**
 * This state is for updating the map in the gui.
 * @author Thomas Tosik
 */
public class MapState extends AbstractAppState{

    private Node rootNode = new Node("Root Node");
    private AssetManager assetManager;
    private MARS_Main mars;
    
    //map stuff
    Quad quad = new Quad(2f, 2f);
    Geometry map_geom = new Geometry("My Textured Box", quad);

    /**
     * 
     * @param assetManager
     */
    public MapState(AssetManager assetManager) {
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
            initMap();
        }
        super.initialize(stateManager, app);
    }
    
    private void initMap(){
       assetManager.registerLocator("Assets/Images", FileLocator.class.getName());
       Material mat_stl = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
       Texture tex_ml = assetManager.loadTexture("mars_logo_12f_white.png");
       mat_stl.setTexture("ColorMap", tex_ml);
       map_geom.setLocalTranslation(new Vector3f(-1f,-1f,0f));
       map_geom.setMaterial(mat_stl);
       rootNode.attachChild(map_geom);
    }
    
    public void loadMap(String terrain_image){
       assetManager.registerLocator("Assets/Textures/Terrain", FileLocator.class.getName());
       Material mat_stl = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
       Texture tex_ml = assetManager.loadTexture(terrain_image);
       mat_stl.setTexture("ColorMap", tex_ml);
       map_geom.setMaterial(mat_stl);
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

