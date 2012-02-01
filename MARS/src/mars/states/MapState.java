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
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import java.util.HashMap;
import mars.MARS_Main;
import mars.MARS_Settings;
import mars.auv.AUV;
import mars.auv.AUV_Manager;

/**
 * This state is for updating the map in the gui.
 * @author Thomas Tosik
 */
public class MapState extends AbstractAppState{

    private Node rootNode = new Node("Root Node");
    private Node auvsNode = new Node("AUVS Node");
    private AssetManager assetManager;
    private MARS_Main mars;
    private AUV_Manager auv_manager;
    private HashMap<String,Geometry> auv_geoms = new HashMap<String,Geometry> ();
    private MARS_Settings mars_settings;
    
    //map stuff
    Quad quad = new Quad(2f, 2f);
    Geometry map_geom = new Geometry("My Textured Box", quad);
    Texture tex_ml;

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
            rootNode.attachChild(auvsNode);
        }
        super.initialize(stateManager, app);
    }
    
    public void setAuv_manager(AUV_Manager auv_manager) {
        this.auv_manager = auv_manager;
        HashMap<String, AUV> auvs = this.auv_manager.getAUVs();
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            if(auv.getAuv_param().isEnabled()){
                Sphere auv_geom_sphere = new Sphere(16, 16, 0.025f);
                Geometry auv_geom = new Geometry(auv.getName() + "-geom", auv_geom_sphere);
                Material auv_geom_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                auv_geom_mat.setColor("Color", auv.getAuv_param().getMapColor());
                
                //don't forget transparency for depth
                auv_geom_mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
                auv_geom.setQueueBucket(Bucket.Transparent);
                
                auv_geom.setMaterial(auv_geom_mat);
                Vector3f ter_pos = mars_settings.getTerrain_position();
                float tile_length = mars_settings.getTileLength();
                int terx_px = tex_ml.getImage().getWidth();
                int tery_px = tex_ml.getImage().getHeight();
                System.out.println("ter: " + terx_px + " " + terx_px*tile_length + " " + tery_px + " " + tery_px*tile_length);
                //auv_geom.setLocalTranslation(new Vector3f((-1f)*ter_pos.x*(1f/(terx_px*tile_length)), (-1f)*ter_pos.z*(1f/(tery_px*tile_length)), -0.5f));
                Vector3f auv_dist = (auv.getPhysicsControl().getPhysicsLocation()).subtract(ter_pos.add(new Vector3f((terx_px*tile_length)/2f, 0f, (tery_px*tile_length)/2f)));
                System.out.println("auv_dist" + auv_dist);
                //auv_geom.setLocalTranslation(Vector3f.ZERO);
                auv_geom.setLocalTranslation(auv_dist.x*(2f/(terx_px*tile_length)), (-1)*auv_dist.z*(2f/(tery_px*tile_length)), 0f);
                System.out.println("x: " + ter_pos.x*(1f/(terx_px*tile_length)));
                System.out.println("y: " + ter_pos.z*(1f/(tery_px*tile_length)));
                auv_geom.updateGeometricState();
                auvsNode.attachChild(auv_geom);
                auv_geoms.put(auv.getName(), auv_geom);
            }
        }
    }
    
    public void setMars_settings(MARS_Settings mars_settings) {
        this.mars_settings = mars_settings;
    }
    
    private void initMap(){
       assetManager.registerLocator("Assets/Images", FileLocator.class.getName());
       Material mat_stl = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
       tex_ml = assetManager.loadTexture("mars_logo_12f_white.png");
       mat_stl.setTexture("ColorMap", tex_ml);
       map_geom.setLocalTranslation(new Vector3f(-1f,-1f,-1f));
       map_geom.setMaterial(mat_stl);
       rootNode.attachChild(map_geom);
    }
    
    public void loadMap(String terrain_image){
       assetManager.registerLocator("Assets/Textures/Terrain", FileLocator.class.getName());
       Material mat_stl = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
       tex_ml = assetManager.loadTexture(terrain_image);
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
        
        if(auv_manager != null){
            for ( String elem : auv_geoms.keySet() ){
                Geometry auv_geom = (Geometry)auv_geoms.get(elem);
                AUV auv = auv_manager.getAUV(elem);
                    if(auv.getAuv_param().isEnabled()){   
                        if(auv.isSelected()){
                            Vector3f ter_pos = mars_settings.getTerrain_position();
                            float tile_length = mars_settings.getTileLength();
                            int terx_px = tex_ml.getImage().getWidth();
                            int tery_px = tex_ml.getImage().getHeight(); Vector3f auv_dist = (auv.getPhysicsControl().getPhysicsLocation()).subtract(ter_pos.add(new Vector3f((terx_px*tile_length)/2f, 0f, (tery_px*tile_length)/2f)));
                            auv_geom.setLocalTranslation(auv_dist.x*(2f/(terx_px*tile_length)), (-1)*auv_dist.z*(2f/(tery_px*tile_length)), 0f);
                            auv_geom.getMaterial().setColor("Color", mars_settings.getSelectionColor());
                        }else{
                            Vector3f ter_pos = mars_settings.getTerrain_position();
                            float tile_length = mars_settings.getTileLength();
                            int terx_px = tex_ml.getImage().getWidth();
                            int tery_px = tex_ml.getImage().getHeight(); Vector3f auv_dist = (auv.getPhysicsControl().getPhysicsLocation()).subtract(ter_pos.add(new Vector3f((terx_px*tile_length)/2f, 0f, (tery_px*tile_length)/2f)));
                            auv_geom.setLocalTranslation(auv_dist.x*(2f/(terx_px*tile_length)), (-1)*auv_dist.z*(2f/(tery_px*tile_length)), 0f);
                            float alpha = 0f;
                            if(auv.getAuv_param().getAlphaDepthScale() > 0f){
                                alpha = Math.max(0f,Math.min(Math.abs(auv.getPhysicsControl().getPhysicsLocation().y),auv.getAuv_param().getAlphaDepthScale()))*(1f/auv.getAuv_param().getAlphaDepthScale());
                            }
                            ColorRGBA auv_geom_color = auv.getAuv_param().getMapColor();
                            auv_geom.getMaterial().setColor("Color",  new ColorRGBA(auv_geom_color.getRed(), auv_geom_color.getGreen(), auv_geom_color.getBlue(), 1f-alpha));
                            
                            
                            
                            //some color stuff for depth. not finished. using alpha instead
                            /*float[] hsv = java.awt.Color.RGBtoHSB(255, 255, 255, null);
                            java.awt.Color col = new java.awt.Color(java.awt.Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]));
                            col.get*/
                        }
                    }
            }
        }
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

