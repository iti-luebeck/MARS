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
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Dome;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import mars.Helper.Helper;
import mars.MARS_Main;
import mars.MARS_Settings;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.sensors.UnderwaterModem;
import mars.sensors.sonar.Sonar;
import org.openide.modules.InstalledFileLocator;

/**
 * This state is for updating the map in the gui.
 * @author Thomas Tosik
 */
public class MapState extends AbstractAppState implements AppStateExtension{

    private Node rootNode = new Node("MapState Root Node");
    private Node auvsNode = new Node("AUVS Node");
    private AssetManager assetManager;
    private MARS_Main mars;
    private AUV_Manager auv_manager;
    private HashMap<String,Node> auv_nodes = new HashMap<String,Node> ();
    private MARS_Settings mars_settings;
    
    //map stuff
    Quad quad = new Quad(2f, 2f);
    Geometry map_geom = new Geometry("My Textured Box", quad);
    Texture tex_ml;
    private Future simStateFuture;

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
        super.cleanup();
        //rootNode.detachAllChildren();
        mars.getRootNode().detachChild(getRootNode());
        /*simStateFuture = mars.enqueue(new Callable() {
            public Void call() throws Exception {
                mars.getRootNode().detachChild(getRootNode());
                return null;
            }
        });*/
    }
    
    public void clear(){
        auvsNode.detachAllChildren();
        auv_nodes.clear();
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
    
    /**
     * 
     * @param auv_manager
     */
    public void setAuv_manager(AUV_Manager auv_manager) {
        this.auv_manager = auv_manager;
        //init();
    }
    
    public void init(){
        HashMap<String, AUV> auvs = auv_manager.getAUVs();
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            addAUV(auv);
        }
    }
    
    public void addAUV(final AUV auv){
        Future fut = mars.enqueue(new Callable() {
             public Void call() throws Exception {
                if(auv.getAuv_param().isEnabled()){
                    Node auvNode = new Node(auv.getName());

                    Sphere auv_geom_sphere = new Sphere(16, 16, 0.025f);
                    Geometry auv_geom = new Geometry(auv.getName() + "-geom", auv_geom_sphere);
                    Material auv_geom_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    auv_geom_mat.setColor("Color", auv.getAuv_param().getMapColor());

                    //don't forget transparency for depth
                    auv_geom_mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
                    auv_geom.setQueueBucket(Bucket.Transparent);

                    auv_geom.setMaterial(auv_geom_mat);
                    Vector3f ter_pos = mars_settings.getTerrain_position();
                    float tile_length = mars_settings.getTerrain_scale().x;
                    int terx_px = tex_ml.getImage().getWidth();
                    int tery_px = tex_ml.getImage().getHeight();
                    //Vector3f auv_dist = (auv.getPhysicsControl().getPhysicsLocation()).subtract(ter_pos.add(new Vector3f((terx_px*tile_length)/2f, 0f, (tery_px*tile_length)/2f)));
                    Vector3f auv_dist = (auv.getPhysicsControl().getPhysicsLocation()).subtract(ter_pos);
                    auvNode.setLocalTranslation(auv_dist.x*(2f/(terx_px*tile_length)), (-1)*auv_dist.z*(2f/(tery_px*tile_length)), 0f);
                    auv_geom.updateGeometricState();
                    auvNode.attachChild(auv_geom);
                    auvsNode.attachChild(auvNode);
                    auv_nodes.put(auv.getName(), auvNode);

                    //adding propagation distance of underwater modems
                    ArrayList uws = auv.getSensorsOfClass(UnderwaterModem.class.getName());
                    Iterator it = uws.iterator();
                    while (it.hasNext()) {
                        UnderwaterModem uw = (UnderwaterModem)it.next();
                        Cylinder uw_geom_sphere = new Cylinder(16,16,uw.getPropagationDistance()*(2f/(terx_px*tile_length)),0.1f,true);
                        Geometry uw_geom = new Geometry(auv.getName()+ "-" + uw.getPhysicalExchangerName() + "-geom", uw_geom_sphere);
                        Material uw_geom_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                        uw_geom_mat.setColor("Color", uw.getDebugColor());

                        //don't forget transparency for depth
                        uw_geom_mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
                        uw_geom.setQueueBucket(Bucket.Transparent);

                        uw_geom.setMaterial(uw_geom_mat);
                        uw_geom.setLocalTranslation(0f,0f, -0.5f);
                        uw_geom.updateGeometricState();
                        auvNode.attachChild(uw_geom);
                        if(uw.isDebug()){
                            uw_geom.setCullHint(CullHint.Never);
                        }else{
                            uw_geom.setCullHint(CullHint.Always);
                        }
                    }

                    //adding sonar cones
                    ArrayList sons = auv.getSensorsOfClass(Sonar.class.getName());
                    it = sons.iterator();
                    while (it.hasNext()) {
                        Sonar son = (Sonar)it.next();
                        float sonRange = son.getMaxRange()*(2f/(terx_px*tile_length));
                        float alpha = son.getBeam_width()/2f;
                        float beta = FastMath.HALF_PI-alpha;
                        float width = (FastMath.sin(alpha)/FastMath.sin(beta))*sonRange;
                        Dome son_geom_cone = new Dome(new Vector3f(0f,-sonRange,0f), 2, 4, sonRange,true);
                        Geometry son_geom = new Geometry(auv.getName()+ "-" + son.getPhysicalExchangerName() + "-geom", son_geom_cone);
                        Material son_geom_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                        son_geom_mat.setColor("Color", son.getDebugColor());

                        //don't forget transparency for depth
                        son_geom_mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
                        son_geom.setQueueBucket(Bucket.Transparent);

                        son_geom.setMaterial(son_geom_mat);
                        son_geom.setLocalTranslation(0f,0f, -0.5f);
                        son_geom.setLocalScale(width, 1.0f, 0.1f);
                        Quaternion quat = new Quaternion();
                        quat.fromAngles(0f, 0f, 0f);
                        son_geom.setLocalRotation(quat);
                        son_geom.updateGeometricState();
                        auvNode.attachChild(son_geom);
                        if(son.isDebug()){
                            son_geom.setCullHint(CullHint.Never);
                        }else{
                            son_geom.setCullHint(CullHint.Always);
                        }
                    }

                }
                return null;
            }
        });
    }
    
    /**
     * 
     * @param mars_settings
     */
    public void setMars_settings(MARS_Settings mars_settings) {
        this.mars_settings = mars_settings;
    }
    
    private void initMap(){
       //File file = InstalledFileLocator.getDefault().locate("Assets/Images", "mars.core", false);
       //String absolutePath = file.getAbsolutePath();
       //assetManager.registerLocator(absolutePath, FileLocator.class);
       //assetManager.registerLocator("Images", FileLocator.class);
       //assetManager.registerLocator("Assets/Images", FileLocator.class);
       Material mat_stl = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
       tex_ml = assetManager.loadTexture("mars_logo_12f_white.png");
       mat_stl.setTexture("ColorMap", tex_ml);
       map_geom.setLocalTranslation(new Vector3f(-1f,-1f,-1f));
       map_geom.setMaterial(mat_stl);
       rootNode.attachChild(map_geom);
    }
    
    /**
     * 
     * @param terrain_image
     */
    public void loadMap(String terrain_image){
       //assetManager.registerLocator("Assets/Textures/Terrain", FileLocator.class);
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
            for ( String elem : auv_nodes.keySet() ){
                Node node = (Node)auv_nodes.get(elem);
                AUV auv = auv_manager.getAUV(elem);
                    if(auv != null && auv.getAuv_param().isEnabled()){   
                        
                        Vector3f ter_pos = mars_settings.getTerrain_position();
                        //float tile_length = mars_settings.getTileLength();
                        float tile_length = mars_settings.getTerrain_scale().getX();
                        int terx_px = tex_ml.getImage().getWidth();
                        int tery_px = tex_ml.getImage().getHeight(); 
                            
                        //update propagation distance
                        ArrayList uws = auv.getSensorsOfClass(UnderwaterModem.class.getName());
                        Iterator it = uws.iterator();
                        while (it.hasNext()) {
                            UnderwaterModem uw = (UnderwaterModem)it.next();
                            Geometry uwgeom = (Geometry)node.getChild(auv.getName()+ "-" + uw.getPhysicalExchangerName() + "-geom");
                            if(uw.isDebug()){
                                uwgeom.setCullHint(CullHint.Never);
                                Cylinder cyl = (Cylinder)uwgeom.getMesh();
                                cyl.updateGeometry(16,16,uw.getPropagationDistance()*(2f/(terx_px*tile_length)),uw.getPropagationDistance()*(2f/(terx_px*tile_length)),0.1f,true,false);
                            }else{
                                uwgeom.setCullHint(CullHint.Always);
                            }
                        }
                        
                        //update roation of sonar
                        ArrayList sons = auv.getSensorsOfClass(Sonar.class.getName());
                        it = sons.iterator();
                        while (it.hasNext()) {
                            Sonar son = (Sonar)it.next();
                            Geometry songeom = (Geometry)node.getChild(auv.getName()+ "-" + son.getPhysicalExchangerName() + "-geom");
                            if(son.isDebug()){
                                songeom.setCullHint(CullHint.Never);
                                Quaternion quat = new Quaternion();
                                quat.fromAngles(0f, 0f, -son.getCurrentHeadPosition());
                                songeom.setLocalRotation(quat);
                                
                                float sonRange = son.getMaxRange()*(2f/(terx_px*tile_length));
                                float alpha = son.getBeam_width()/2f;
                                float beta = FastMath.HALF_PI-alpha;
                                float width = (FastMath.sin(alpha)/FastMath.sin(beta))*sonRange;
                                songeom.setLocalScale(width, 1.0f, 0.1f);
                                Dome dom = (Dome)songeom.getMesh();
                                dom.updateGeometry(new Vector3f(0f,-sonRange,0f), 2, 4, sonRange,true);
                            }else{
                                songeom.setCullHint(CullHint.Always);
                            }
                        }
                        
                        //update selection color and position
                        Geometry geom = (Geometry)node.getChild(auv.getName()+"-geom");
                        geom.setCullHint(CullHint.Never);
                        if(auv.isSelected()){
                            //Vector3f auv_dist = (auv.getPhysicsControl().getPhysicsLocation()).subtract(ter_pos.add(new Vector3f((terx_px*tile_length)/2f, 0f, (tery_px*tile_length)/2f)));
                            Vector3f auv_dist = (auv.getPhysicsControl().getPhysicsLocation()).subtract(ter_pos);
                            node.setLocalTranslation(auv_dist.x*(2f/(terx_px*tile_length)), (-1)*auv_dist.z*(2f/(tery_px*tile_length)), 0f);
                            //System.out.println("auv_dist.x: " + auv_dist.x*(2f/(terx_px*tile_length)) + "auv_dist.y: " + (-1)*auv_dist.z*(2f/(tery_px*tile_length)));
                            //Geometry geom = (Geometry)node.getChild(auv.getName()+"-geom");
                            geom.getMaterial().setColor("Color", mars_settings.getSelectionColor());
                        }else{
                            //Vector3f auv_dist = (auv.getPhysicsControl().getPhysicsLocation()).subtract(ter_pos.add(new Vector3f((terx_px*tile_length)/2f, 0f, (tery_px*tile_length)/2f)));
                            Vector3f auv_dist = (auv.getPhysicsControl().getPhysicsLocation()).subtract(ter_pos);
                            
                            node.setLocalTranslation(auv_dist.x*(2f/(terx_px*tile_length)), (-1)*auv_dist.z*(2f/(tery_px*tile_length)), 0f);
                            float alpha = 0f;
                            if(auv.getAuv_param().getAlphaDepthScale() > 0f){
                                alpha = Math.max(0f,Math.min(Math.abs(auv.getPhysicsControl().getPhysicsLocation().y),auv.getAuv_param().getAlphaDepthScale()))*(1f/auv.getAuv_param().getAlphaDepthScale());
                            }
                            ColorRGBA auv_geom_color = auv.getAuv_param().getMapColor();
                            //Geometry geom = (Geometry)node.getChild(auv.getName()+"-geom");
                            geom.getMaterial().setColor("Color",  new ColorRGBA(auv_geom_color.getRed(), auv_geom_color.getGreen(), auv_geom_color.getBlue(), 1f-alpha));
                            
                            
                            
                            //some color stuff for depth. not finished. using alpha instead
                            /*float[] hsv = java.awt.Color.RGBtoHSB(255, 255, 255, null);
                            java.awt.Color col = new java.awt.Color(java.awt.Color.HSBtoRGB(hsv[0], hsv[1], hsv[2]));
                            col.get*/
                        }
                    }else if(auv == null){//auv doesnt exist anymore
                        
                    }else{//auv is disabled so dont show in on map
                        Geometry geom = (Geometry)node.getChild(auv.getName()+"-geom");
                        if(geom != null){
                            geom.setCullHint(CullHint.Always);
                        }
                    }
            }
        }
        rootNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();
    }
    
    /**
     * 
     * @param pos
     * @return
     */
    public Vector3f getSimStatePosition(Point pos){
            Vector3f click3d = mars.getMapCamera().getWorldCoordinates(new Vector2f(pos.x, mars.getMapCamera().getHeight()-pos.y), 0f).clone();
            Vector3f dir = mars.getMapCamera().getWorldCoordinates(new Vector2f(pos.x, mars.getMapCamera().getHeight()-pos.y), 1f).subtractLocal(click3d);

            Vector3f ter_pos = mars_settings.getTerrain_position();
            float tile_length = mars_settings.getTerrain_scale().x;
            int terx_px = tex_ml.getImage().getWidth();
            int tery_px = tex_ml.getImage().getHeight();
                
            float auv_dist_x = (click3d.x/(2f/(terx_px*tile_length)))+ter_pos.x;
            float auv_dist_z = (click3d.y/(2f/(tery_px*tile_length))*(-1f))+ter_pos.z;
            
            return new Vector3f(auv_dist_x, 0f, auv_dist_z);
    }
}

