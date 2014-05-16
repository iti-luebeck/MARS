/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.simobjects;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.ConeCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import mars.CollisionType;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.rits.cloning.Cloner;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.tree.TreePath;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.Helper.Helper;
import mars.MARS_Main;
import mars.MARS_Settings;
import mars.PickHint;
import mars.gui.tree.HashMapWrapper;
import mars.xml.HashMapAdapter;

/**
 * A basic simauv object that shall be loaded. For example an pipe or another custom made model.
 * @author Thomas Tosik
 */
@XmlRootElement(name="SimObject")
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso( {OilBurst.class} )
public class SimObject{

    /**
     *
     */
    @XmlJavaTypeAdapter(HashMapAdapter.class)
    @XmlElement(name="")
    protected HashMap<String,Object> simob_variables;
    private HashMap<String,Object> collision;
    
    //selection stuff aka highlightening
    private boolean selected = false;
    AmbientLight ambient_light = new AmbientLight();
    private Spatial ghost_simob_spatial;
    /**
     *
     */
    protected Node simObNode = new Node("simObNode");
    /**
     *
     */
    protected Node debugNode = new Node("debugNode");
    /**
     *
     */
    protected Node renderNode = new Node("renderNode");

    private MARS_Main simauv;
    /**
     *
     */
    protected AssetManager assetManager;
    private Spatial spatial;
    private Material spatialMaterial;
    private RigidBodyControl physics_control;
    private MARS_Settings mars_settings;
    private Spatial debugShape;
    
    /**
     * 
     */
    public SimObject(){
        
    }
    
    /**
     *
     * @param simob
     */
    public SimObject(SimObject simob){
        HashMap<String, Object> variablesOriginal = simob.getAllVariables();
        Cloner cloner = new Cloner();
        simob_variables = cloner.deepClone(variablesOriginal);
    }
    
    /**
     *
     * @return
     */
    public SimObject copy() {
        SimObject simob = new SimObject(this);
        simob.initAfterJAXB();
        return simob;
    }
    
    /**
     * 
     * @param path
     */
    public void updateState(TreePath path){
        if(path.getPathComponent(1).equals(this)){//make sure we want to change auv params 
            //System.out.println("update tts " + path);
            Object obj = path.getParentPath().getLastPathComponent();
            if(path.getParentPath().getLastPathComponent() instanceof HashMapWrapper){
                updateState(path.getLastPathComponent().toString(),path.getParentPath().getLastPathComponent().toString());
            }else{
                updateState(path.getLastPathComponent().toString(),"");
            }
        }
    }
    
    /**
     * 
     * @param target
     * @param hashmapname
     */
    public void updateState(String target, String hashmapname){
        if(target.equals("position") && hashmapname.equals("")){
            if(physics_control != null ){
                physics_control.setPhysicsLocation(getPosition());
            }
        }else if(target.equals("rotation") && hashmapname.equals("")){
            if(physics_control != null ){
                Matrix3f m_rot = new Matrix3f();
                Quaternion q_rot = new Quaternion();
                q_rot.fromAngles(getRotation().x, getRotation().y, getRotation().z);
                m_rot.set(q_rot);
                physics_control.setPhysicsRotation(m_rot);
            }
        }else if(target.equals("scale") && hashmapname.equals("")){
            getSpatial().setLocalScale(getScale());
        }else if(target.equals("debug_collision") && hashmapname.equals("Collision")){
            setCollisionVisible(isDebugCollision());
        }else if(target.equals("color") && hashmapname.equals("")){
            
            spatialMaterial.setColor("Color", getColor());
        }else if(target.equals("light") && hashmapname.equals("")){
            if(!isLight()){
                spatialMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                spatialMaterial.setColor("Color", getColor());
                spatial.setMaterial(spatialMaterial);
            }else{
                
            }
        }     
    }

    /**
     *
     * @return
     */
    public HashMap<String,Object> getAllVariables(){
        return simob_variables;
    }

    private void loadModel(){
        //assetManager.registerLocator("./Assets/Models/", FileLocator.class);
        spatialMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        spatial = assetManager.loadModel(getFilepath());

        spatial.setLocalScale(getScale());
        spatial.setUserData("simob_name", getName());
        spatial.setLocalTranslation(getPosition());
        spatial.rotate(getRotation().x, getRotation().y, getRotation().z);
        if(!isLight()){
            spatialMaterial.setColor("Color", getColor());
            spatial.setMaterial(spatialMaterial);
        }
        spatial.setShadowMode(ShadowMode.CastAndReceive);
        spatial.updateGeometricState();

        spatial.updateModelBound();

        spatial.setName(getName());
        renderNode.attachChild(spatial);
    }

        /*
     * When we have the spatial for the auv we create the physics node out of it. Needed for all the physics and collisions.
     */
    private void createPhysicsNode(){
        CollisionShape collisionShape;
        if(getType() == CollisionType.BOXCOLLISIONSHAPE){
            collisionShape = new BoxCollisionShape(getDimensions());
        }else if(getType() == CollisionType.SPHERECOLLISIONSHAPE){
            collisionShape = new SphereCollisionShape(getDimensions().x);
        }else if(getType() == CollisionType.CONECOLLISIONSHAPE){
            collisionShape = new ConeCollisionShape(getDimensions().x,getDimensions().y);
        }else if(getType() == CollisionType.CYLINDERCOLLISIONSHAPE){
            //collisionShape = new CylinderCollisionShape(auv_param.getDimensions().x,auv_param.getDimensions().y);
            collisionShape = new BoxCollisionShape(getDimensions());
        }else if(getType() == CollisionType.MESHACCURATE){
            collisionShape = CollisionShapeFactory.createMeshShape(spatial);
        } else {
            collisionShape = new BoxCollisionShape(getDimensions());
        }
        physics_control = new RigidBodyControl(collisionShape, 0f);
        physics_control.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_03);
        physics_control.setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_01);
        physics_control.setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_02);
        physics_control.setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_03);
        physics_control.setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_04);
        
         //debug
        Material debug_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        debug_mat.setColor("Color", ColorRGBA.Red);
        /*debugShape = physics_control.createDebugShape(assetManager);
        debugNode.attachChild(debugShape);
        if(isDebugCollision()){
            debugShape.setCullHint(CullHint.Inherit);
        }else{
            debugShape.setCullHint(CullHint.Always);
        }*/
        
        spatial.addControl(physics_control);
        spatial.updateGeometricState();
    }
    
    /**
     * 
     */
    public void initAfterJAXB(){
        collision = (HashMap<String,Object>)simob_variables.get("Collision");
    }

    /**
     *
     */
    public void init(){
        loadModel();
        createPhysicsNode();
        createGhostSpatial();
        Helper.setNodePickUserData(debugNode,PickHint.NoPick);
        simObNode.attachChild(renderNode);
        simObNode.attachChild(debugNode);
        spatial.updateGeometricState();
        simObNode.updateGeometricState();
    }
    
    /**
     *
     * @return
     */
    public MARS_Settings getMARSSettings() {
        return mars_settings;
    }

    /**
     *
     * @param mars_settings 
     */
    public void setMARSSettings(MARS_Settings mars_settings) {
        this.mars_settings = mars_settings;
    }
    
    /**
     * 
     * @param selected
     */
    public void setSelected(boolean selected){
        if(selected && this.selected==false){
            ambient_light.setColor(mars_settings.getGuiSelectionColor());
            simObNode.addLight(ambient_light); 
        }else if(selected == false){
            simObNode.removeLight(ambient_light);
        }
        this.selected = selected;
    }
    
    /**
     * 
     * @return
     */
    public boolean isSelected(){
        return selected;
    }
    
    /**
     *
     * @return
     */
    public Node getSimObNode() {
        return simObNode;
    }
    
    /**
     *
     * @return
     */
    public Node getRenderNode() {
        return renderNode;
    }

    /**
     *
     * @return
     */
    public RigidBodyControl getPhysicsControl() {
        return physics_control;
    }

        /**
     *
     * @return
     */
    public String getIcon() {
        return (String)simob_variables.get("icon");
    }

    /**
     *
     * @param icon 
     */
    public void setIcon(String icon) {
        simob_variables.put("icon", icon);
    }
    
        /**
     *
     * @return
     */
    public String getDND_Icon() {
        return (String)simob_variables.get("dnd_icon");
    }

    /**
     *
     * @param dnd_icon 
     */
    public void setDND_Icon(String dnd_icon) {
        simob_variables.put("dnd_icon", dnd_icon);
    }
    
    /**
     *
     * @return
     */
    public Vector3f getDimensions() {
        return (Vector3f)((HashMap<String,Object>)simob_variables.get("Collision")).get("dimensions");
    }

    /**
     *
     * @param dimensions 
     */
    public void setDimensions(Vector3f dimensions) {
        ((HashMap<String,Object>)simob_variables.get("Collision")).put("dimensions", dimensions);
    }
        
    /**
     *
     * @return
     */
    public Vector3f getCollisionPosition() {
        return (Vector3f)((HashMap<String,Object>)simob_variables.get("Collision")).get("collision_position");
    }

    /**
     *
     * @param collision_position 
     */
    public void setCollisionPosition(Vector3f collision_position) {
        ((HashMap<String,Object>)simob_variables.get("Collision")).put("collision_position", collision_position);
    }

    /**
     *
     * @return
     */
    public int getType() {
        return (Integer)((HashMap<String,Object>)simob_variables.get("Collision")).get("type");
    }

    /**
     *
     * @param type
     */
    public void setType(int type) {
        ((HashMap<String,Object>)simob_variables.get("Collision")).put("type", type);
    }

    /**
     *
     * @return
     */
    public boolean isCollidable() {
        return (Boolean)((HashMap<String,Object>)simob_variables.get("Collision")).get("collidable");
    }

    /**
     *
     * @param collidable
     */
    public void setCollidable(boolean collidable) {
        ((HashMap<String,Object>)simob_variables.get("Collision")).put("collidable", collidable);
    }

    /**
     *
     * @return
     * @deprecated 
     */
    @Deprecated
    public boolean isSonar_detectable() {
        return (Boolean)simob_variables.get("sonar_detectable");
    }

    /**
     *
     * @param sonar_detectable
     * @deprecated 
     */
    @Deprecated
    public void setSonar_detectable(boolean sonar_detectable) {
        simob_variables.put("sonar_detectable", sonar_detectable);
    }
    
    /**
     *
     * @return
     */
    public boolean isRayDetectable() {
        return (Boolean)simob_variables.get("ray_detectable");
    }

    /**
     *
     * @param ray_detectable 
     */
    public void setRayDetectable(boolean ray_detectable) {
        simob_variables.put("ray_detectable", ray_detectable);
    }

    /**
     *
     * @return
     */
    public boolean isPinger() {
        return (Boolean)simob_variables.get("pinger");
    }

    /**
     *
     * @param pinger
     */
    public void setPinger(boolean pinger) {
        simob_variables.put("pinger", pinger);
    }

    /**
     *
     * @return
     */
    public boolean isLight() {
        return (Boolean)simob_variables.get("light");
    }

    /**
     *
     * @param light
     */
    public void setLight(boolean light) {
        simob_variables.put("light", light);
    }

    /**
     *
     * @param color
     */
    public void setColor(ColorRGBA color) {
        simob_variables.put("color", color);
    }

    /**
     *
     * @return
     */
    public ColorRGBA getColor() {
        return (ColorRGBA)simob_variables.get("color");
    }

    /**
     *
     * @param simobject_spatial
     */
    private void setSpatial(Spatial spatial) {
        this.spatial = spatial;
    }

    /**
     *
     * @return
     */
    public Spatial getSpatial() {
        return spatial;
    }

    /**
     *
     * @return
     */
    public boolean isEnabled() {
        return (Boolean)simob_variables.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        simob_variables.put("enabled", enabled);
    }

    /**
     *
     * @return
     */
    public String getFilepath() {
        return (String)simob_variables.get("filepath");
    }

    /**
     *
     * @param filepath
     */
    public void setFilepath(String filepath) {
        simob_variables.put("filepath", filepath);
    }

    /**
     *
     * @return
     */
    public String getName() {
        return (String)simob_variables.get("name");
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        simob_variables.put("name", name);
    }

    /**
     *
     * @return
     */
    public Vector3f getPosition() {
        return (Vector3f)simob_variables.get("position");
    }

    /**
     *
     * @param position
     */
    public void setPosition(Vector3f position) {
        simob_variables.put("position", position);
    }

    /**
     *
     * @return
     */
    public Vector3f getRotation() {
        return (Vector3f)simob_variables.get("rotation");
    }

    /**
     *
     * @param rotation
     */
    public void setRotation(Vector3f rotation) {
        simob_variables.put("rotation", rotation);
    }
    
     /**
     *
     * @return
     */
    public Vector3f getScale() {
        return (Vector3f)simob_variables.get("scale");
    }

    /**
     *
     * @param scale 
     */
    public void setScale(Vector3f scale) {
        simob_variables.put("scale", scale);
    }

    /**
     *
     * @return
     */
    public boolean isDebugCollision() {
         return (Boolean)((HashMap<String,Object>)simob_variables.get("Collision")).get("debug_collision");
    }

    /**
     *
     * @param debug_collision
     */
    public void setDebugCollision(boolean debug_collision) {
        ((HashMap<String,Object>)simob_variables.get("Collision")).put("debug_collision", debug_collision);
    }

    /**
     *
     * @param value
     * @param hashmapname
     * @return
     */
    public Object getValue(String value,String hashmapname) {
        if(hashmapname.equals("") || hashmapname == null){
            return (Object)simob_variables.get(value);
        }else{
            HashMap<String,Object> hashmap = (HashMap<String,Object>)simob_variables.get(hashmapname);
            return (Object)hashmap.get(value);
        }
    }

    /**
     *
     * @param value
     * @param object
     * @param hashmapname
     */
    public void setValue(String value, Object object, String hashmapname) {
        if(hashmapname.equals("") || hashmapname == null){
            simob_variables.put(value, object);
        }else{
            HashMap<String,Object> hashmap = (HashMap<String,Object>)simob_variables.get(hashmapname);
            hashmap.put(value, object);
        }
    }

    /**
     *
     * @return
     */
    public AssetManager getAssetManager() {
        return assetManager;
    }

    /**
     *
     * @param assetManager
     */
    public void setAssetManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    /**
     *
     * @return
     */
    public MARS_Main getSimauv() {
        return simauv;
    }

    /**
     *
     * @param simauv
     */
    public void setSimauv(MARS_Main simauv) {
        this.simauv = simauv;
    }
    
    private void createGhostSpatial(){
        //assetManager.registerLocator("Assets/Models", FileLocator.class);
        ghost_simob_spatial = assetManager.loadModel(getFilepath());
        ghost_simob_spatial.setLocalScale(getScale());
        ghost_simob_spatial.setLocalTranslation(getPosition());
        ghost_simob_spatial.rotate(getRotation().x, getRotation().y, getRotation().z);
        ghost_simob_spatial.updateGeometricState();
        ghost_simob_spatial.updateModelBound();
        ghost_simob_spatial.setName(getName() + "_ghost");
        ghost_simob_spatial.setUserData("simob_name", getName());
        ghost_simob_spatial.setCullHint(CullHint.Always);
        debugNode.attachChild(ghost_simob_spatial);
    }
    
    /**
     * 
     * @return
     */
    public Spatial getGhostSpatial(){
        return ghost_simob_spatial;
    }
    
    /**
     * 
     * @param hide
     */
    public void hideGhostSpatial(boolean hide){
        if(hide){
             ghost_simob_spatial.setCullHint(CullHint.Always);
        }else{
             ghost_simob_spatial.setCullHint(CullHint.Never);
        }
    }
    
    /**
     * 
     * @param visible
     */
    public void setCollisionVisible(boolean visible){
        if(visible){
            debugShape.setCullHint(CullHint.Inherit);
        }else{
            debugShape.setCullHint(CullHint.Always);
        }
    }
    
    /**
     * 
     * @param visible
     */
    public void setWireframeVisible(boolean visible){
        if(visible){
            Node nodes = (Node)spatial;
            List<Spatial> children = nodes.getChildren();
            for (Iterator<Spatial> it = children.iterator(); it.hasNext();) {
                Spatial spatial2 = it.next();
                System.out.println(spatial2.getName());
                if(spatial2 instanceof Geometry){
                    Geometry geom = (Geometry)spatial2;
                    geom.getMaterial().getAdditionalRenderState().setWireframe(true);
                }
            }
        }else{
            Node nodes = (Node)spatial;
            List<Spatial> children = nodes.getChildren();
            for (Iterator<Spatial> it = children.iterator(); it.hasNext();) {
                Spatial spatial2 = it.next();
                System.out.println(spatial2.getName());
                if(spatial2 instanceof Geometry){
                    Geometry geom = (Geometry)spatial2;
                    geom.getMaterial().getAdditionalRenderState().setWireframe(false);
                }
            }
        }
    }

    @Override
    public String toString(){
        return getName();
    }
}
