/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.simobjects;

import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
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
import com.jme3.scene.Node;
import mars.CollisionType;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import java.util.HashMap;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.MARS_Main;
import mars.MARS_Settings;
import mars.gui.TextFieldEditor;
import mars.xml.HashMapAdapter;
import mars.xml.XMLConfigReaderWriter;

/**
 * A basic simauv object that shall be loaded. For example an pipe or another custom made model.
 * @author Thomas Tosik
 */
@XmlRootElement(name="SimObject")
@XmlAccessorType(XmlAccessType.NONE)
public class SimObject implements CellEditorListener{

    @XmlJavaTypeAdapter(HashMapAdapter.class)
    @XmlElement(name="")
    private HashMap<String,Object> simob_variables;
    private HashMap<String,Object> collision;
    private XMLConfigReaderWriter xmll;
    
    //selection stuff aka highlightening
    private boolean selected = false;
    AmbientLight ambient_light = new AmbientLight();
    private Spatial ghost_simob_spatial;
    private Node selectionNode = new Node("selectionNode");

    private Vector3f position = new Vector3f(0f,0f,0f);
    private Vector3f rotation = new Vector3f(0f,0f,0f);
    private String filepath = "pipe/pipe_1.obj";
    private float scale = 0.1f;
    private String name = "pipe_test_simobject";
    private boolean enabled = true;
    private boolean collidable = false;
    private boolean sonar_detectable = false;
    private boolean pinger  = false;
    private ColorRGBA color = new ColorRGBA(0f, 1f, 0f, 1f);
    private boolean light = false;
    private boolean debug_collision = false;
    private Vector3f dimensions = new Vector3f(0.5f,0.5f,0.5f);
    private Vector3f collision_position = new Vector3f(0.0f,0.0f,0.0f);
    private int type = CollisionType.CYLINDERCOLLISIONSHAPE;
    private String icon = "box_closed.png";
    private String dnd_icon = "box_closed.png";

    private MARS_Main simauv;
    private AssetManager assetManager;
    private Spatial spatial;
    private RigidBodyControl physics_control;
    private MARS_Settings mars_settings;

    /**
     * 
     * @param xmll
     */
    public SimObject(XMLConfigReaderWriter xmll){
        simob_variables = new HashMap<String,Object> ();
        collision = new HashMap<String,Object> ();
        simob_variables.put("Collision", collision);
        setPosition(position);
        setRotation(rotation);
        setFilepath(filepath);
        setScale(scale);
        setName(name);
        setEnabled(enabled);
        setCollidable(collidable);
        setSonar_detectable(sonar_detectable);
        setColor(color);
        setLight(light);
        setDebugCollision(debug_collision);
        setDimensions(dimensions);
        setCollisionPosition(collision_position);
        setType(type);
        setIcon(icon);
        setDNDIcon(dnd_icon);
        this.xmll = xmll;
    }
    
    /**
     * 
     */
    public SimObject(){
        
    }

    public void editingCanceled(ChangeEvent e){
    }

    public void editingStopped(ChangeEvent e){
        Object obj = e.getSource();
        if (obj instanceof TextFieldEditor) {
            TextFieldEditor editor = (TextFieldEditor)obj;
            String sim_ob_tree = editor.getTreepath().getParentPath().getParentPath().getLastPathComponent().toString();
            if(sim_ob_tree.equals(getName())){//check if right simobject
                saveValue(editor);
            }
        }
    }
    
    private void saveValue(TextFieldEditor editor){
        HashMap<String,Object> hashmap = simob_variables;
        String target = editor.getTreepath().getParentPath().getLastPathComponent().toString();
        int pathcount = editor.getTreepath().getPathCount();
        Object[] treepath = editor.getTreepath().getPath();
        
        if( simob_variables.containsKey(target) && pathcount < 6){//no hasmap, direct save
            Object obj = simob_variables.get(target);
            detectType(obj,editor,target,simob_variables);
        }else{//it's in another hashmap, search deeper
            for (int i = 3; i < pathcount-2; i++) {
                hashmap = (HashMap<String,Object>)hashmap.get(treepath[i].toString());
            }
            //found the corresponding hashmap
            Object obj = hashmap.get(target);
            detectType(obj,editor,target,hashmap);
        }
    }

    private void detectType(Object obj,TextFieldEditor editor,String target,HashMap hashmap){
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)editor.getTreepath().getLastPathComponent();
        Object node_obj = node.getUserObject();
        Object[] treepath = editor.getTreepath().getPath();
        int pathcount = editor.getTreepath().getPathCount();
        if(obj instanceof Float){
            hashmap.put(target, (Float)node_obj);
            updateState(target);
            xmll.setPathElementSimObject(getName(), treepath, pathcount, node_obj);
        }else if(obj instanceof Integer){
            hashmap.put(target, (Integer)node_obj);
            updateState(target);
            xmll.setPathElementSimObject(getName(), treepath, pathcount, node_obj);
        }else if(obj instanceof Boolean){
            hashmap.put(target, (Boolean)node_obj);
            xmll.setPathElementSimObject(getName(), treepath, pathcount, node_obj);
            updateState(target);
        }else if(obj instanceof String){
            hashmap.put(target, (String)node_obj);
            updateState(target);
            xmll.setPathElementSimObject(getName(), treepath, pathcount, node_obj);
        }else if(obj instanceof Vector3f){
            hashmap.put(target, (Vector3f)node_obj);
            updateState(target);
            xmll.setPathElementSimObject(getName(), treepath, pathcount, node_obj);
        }else if(obj instanceof ColorRGBA){
            hashmap.put(target, (ColorRGBA)node_obj);
            updateState(target);
            xmll.setPathElementSimObject(getName(), treepath, pathcount, node_obj);
        }
    }

    private void updateState(String target){
        if(target.equals("debug_collision")){
            if( isDebugCollision() == false && physics_control != null ){
                physics_control.detachDebugShape();
            }else if( isDebugCollision() && physics_control != null ){
                Material debug_mat = new Material(assetManager, "Common/MatDefs/Misc/WireColor.j3md");
                debug_mat.setColor("Color", ColorRGBA.Red);
                //physics_control.attachDebugShape(debug_mat);
            }
        }else if(target.equals("position")){
            if(physics_control != null ){
                physics_control.setPhysicsLocation(getPosition());
            }else{
                spatial.setLocalTranslation(getPosition());
            }
        }else if(target.equals("rotation")){
            if(physics_control != null ){
                Matrix3f m_rot = new Matrix3f();
                Quaternion q_rot = new Quaternion();
                q_rot.fromAngles(getRotation().x, getRotation().y, getRotation().z);
                m_rot.set(q_rot);
                physics_control.setPhysicsRotation(m_rot);
            }else{
                spatial.rotate(getRotation().x,getRotation().y,getRotation().z);
            }
        }else if(target.equals("scale")){
            spatial.setLocalScale(getScale());
        }else if(target.equals("color")){
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", getColor());
            spatial.setMaterial(mat);
        }else if(target.equals("dimensions")){
            /*if(physics_control != null ){
                BoxCollisionShape collisionShape = new BoxCollisionShape(getDimensions());
                physics_control.setCollisionShape(collisionShape);
            }*/
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
        assetManager.registerLocator("./Assets/Models/", FileLocator.class.getName());

        spatial = assetManager.loadModel(getFilepath());

        spatial.setLocalScale(getScale());
        spatial.setUserData("simob_name", getName());
        spatial.setLocalTranslation(getPosition());
        spatial.rotate(getRotation().x, getRotation().y, getRotation().z);
        if(!isLight()){
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", getColor());
            spatial.setMaterial(mat);
        }
        spatial.updateGeometricState();

        spatial.updateModelBound();

        spatial.setName(getName());
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
        physics_control.setCollisionGroup(1);
        physics_control.setCollideWithGroups(1);
        if(isDebugCollision()){
            Material debug_mat = new Material(assetManager, "Common/MatDefs/Misc/WireColor.j3md");
            debug_mat.setColor("Color", ColorRGBA.Red);
            //physics_control.attachDebugShape(debug_mat);
        }
        spatial.addControl(physics_control);
        spatial.updateGeometricState();
    }
    
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
        selectionNode.attachChild(spatial);
        spatial.updateGeometricState();
        selectionNode.updateGeometricState();
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
     */
    public void setMARSSettings(MARS_Settings mars_settings) {
        this.mars_settings = mars_settings;
    }
    
    public void setSelected(boolean selected){
        if(selected && this.selected==false){
            ambient_light.setColor(mars_settings.getSelectionColor());
            selectionNode.addLight(ambient_light); 
        }else if(selected == false){
            selectionNode.removeLight(ambient_light);
        }
        this.selected = selected;
    }
    
    public boolean isSelected(){
        return selected;
    }
    
    /**
     *
     * @return
     */
    public Node getSelectionNode() {
        return selectionNode;
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
     * @param auv_name
     */
    public void setIcon(String icon) {
        simob_variables.put("icon", icon);
    }
    
        /**
     *
     * @return
     */
    public String getDNDIcon() {
        return (String)simob_variables.get("dnd_icon");
    }

    /**
     *
     * @param auv_name
     */
    public void setDNDIcon(String dnd_icon) {
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
     */
    public boolean isSonar_detectable() {
        return (Boolean)simob_variables.get("sonar_detectable");
    }

    /**
     *
     * @param sonar_detectable
     */
    public void setSonar_detectable(boolean sonar_detectable) {
        simob_variables.put("sonar_detectable", sonar_detectable);
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
    public float getScale() {
        return (Float)simob_variables.get("scale");
    }

    /**
     *
     * @param scale
     */
    public void setScale(float scale) {
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
        assetManager.registerLocator("Assets/Models", FileLocator.class.getName());
        ghost_simob_spatial = assetManager.loadModel(getFilepath());
        ghost_simob_spatial.setLocalScale(getScale());
        ghost_simob_spatial.setLocalTranslation(getPosition());
        ghost_simob_spatial.updateGeometricState();
        ghost_simob_spatial.updateModelBound();
        ghost_simob_spatial.setName(getName() + "_ghost");
        ghost_simob_spatial.setUserData("simob_name", getName());
        ghost_simob_spatial.setCullHint(CullHint.Always);
        selectionNode.attachChild(ghost_simob_spatial);
    }
    
    public Spatial getGhostSpatial(){
        return ghost_simob_spatial;
    }
    
    public void hideGhostSpatial(boolean hide){
        if(hide){
             ghost_simob_spatial.setCullHint(CullHint.Always);
        }else{
             ghost_simob_spatial.setCullHint(CullHint.Never);
        }
    }

    @Override
    public String toString(){
        return getName();
    }
}
