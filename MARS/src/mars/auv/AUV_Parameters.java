/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.auv;

import mars.CollisionType;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import java.util.HashMap;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.gui.TextFieldEditor;
import mars.xml.HashMapAdapter;
import mars.xml.XMLConfigReaderWriter;

/**
 *
 * @author Thomas Tosik
 */
@XmlRootElement(name="Parameters")
@XmlAccessorType(XmlAccessType.NONE)
public class AUV_Parameters implements CellEditorListener{

    @XmlJavaTypeAdapter(HashMapAdapter.class)
    private HashMap<String,Object> params;
    private HashMap<String,Object> waypoints;
    private HashMap<String,Object> model;
    private HashMap<String,Object> debug;
    private HashMap<String,Object> collision;
    private XMLConfigReaderWriter xmll;
    private AUV auv;

    private String auv_class = "Hanse";
    private Vector3f position = new Vector3f(0f,0f,0f);
    private Vector3f rotation = new Vector3f(0f,0f,0f);
    private Vector3f centroid_center_distance = new Vector3f(-0.04f,0.175f,-0.035f);
    private float mass_auv = 24.0f;
    private float drag_coefficient_linear = 1.45f;
    private float drag_coefficient_angular = 1.0f;
    private float damping_linear = 0.2f;
    private float damping_angular = 0.3f;
    private int offCamera_width = 320;
    private int offCamera_height = 240;
    private float model_scale = 0.1f;
    private String model_name = "HANSE_MODEL_2";
    private String model_filepath = "hanse_very_low.obj";
    private int buoyancy_updaterate = 0;
    private int drag_updaterate = 5;
    private float waypoints_updaterate = 1.0f;
    private float physicalvalues_updaterate = 1.0f;
    private int maxWaypoints = 0;
    private ColorRGBA waypoints_color = new ColorRGBA(0f, 0f, 0f, 0.0f);
    private boolean waypoints_enabled = true;
    private boolean waypoints_gradient = true;
    private boolean waypoints_visible = false;
    private float angular_factor = 1.0f;
    private Vector3f linear_factor = new Vector3f(1f,1f,1f);
    private String auv_name = "hanse_irgendwas";
    private boolean debug_drag = false;
    private boolean debug_buoycancy = false;
    private boolean debug_physical_exchanger = false;
    private boolean debug_collision = false;
    private boolean debug_centers = false;
    private Vector3f dimensions = new Vector3f(0.5f,0.5f,0.5f);
    private Vector3f collision_position = new Vector3f(0.0f,0.0f,0.0f);
    private int type = CollisionType.CYLINDERCOLLISIONSHAPE;
    private float buoyancy_distance = 0f;
    private float buoyancy_scale = 0.9f;
    private boolean enabled = false;
    private ColorRGBA selection_color = ColorRGBA.Red;
    private ColorRGBA map_color = ColorRGBA.Red;
    private float alpha_depth_scale = 3.0f;

    /**
     *
     * @param xmll
     */
    public AUV_Parameters(XMLConfigReaderWriter xmll){
        params = new HashMap<String,Object> ();
        waypoints = new HashMap<String,Object> ();
        model = new HashMap<String,Object> ();
        debug = new HashMap<String,Object> ();
        collision = new HashMap<String,Object> ();
        params.put("Waypoints", waypoints);
        params.put("Model", model);
        params.put("Debug", debug);
        params.put("Collision", collision);
        setAngular_factor(angular_factor);
        setAuv_name(auv_name);
        setAuv_class(auv_class);
        setBuoyancy_updaterate(buoyancy_updaterate);
        setBuoyancy_distance(buoyancy_distance);
        setCentroid_center_distance(centroid_center_distance);
        setDamping_angular(damping_angular);
        setDamping_linear(damping_linear);
        setDrag_coefficient_linear(drag_coefficient_linear);
        setDrag_coefficient_angular(drag_coefficient_angular);
        setDrag_updaterate(drag_updaterate);
        setPosition(position);
        setRotation(rotation);
        setLinear_factor(linear_factor);
        setMass_auv(mass_auv);
        setMaxWaypoints(maxWaypoints);
        setModelFilePath(model_filepath);
        setModel_name(model_name);
        setModel_scale(model_scale);
        setOffCamera_height(offCamera_height);
        setOffCamera_width(offCamera_width);
        setPhysicalvalues_updaterate(physicalvalues_updaterate);
        setWaypoints_color(waypoints_color);
        setWaypoints_enabled(waypoints_enabled);
        setWaypoints_updaterate(waypoints_updaterate);
        setWaypoints_visible(waypoints_visible);
        setWaypoints_gradient(waypoints_gradient);
        setDebugDrag(debug_drag);
        setDebugBuoycancy(debug_buoycancy);
        setDebugPhysicalExchanger(debug_physical_exchanger);
        setDebugCollision(debug_collision);
        setDimensions(dimensions);
        setCollisionPosition(collision_position);
        setType(type);
        setDebugCenters(debug_centers);
        setEnabled(enabled);
        setSelection_color(selection_color);
        setMapColor(map_color);
        setAlphaDepthScale(alpha_depth_scale);
        this.xmll = xmll;
    }
    
    /**
     * 
     */
    public AUV_Parameters(){
        
    }
    
    /**
     * You have to initialize first when you read the data in trough jaxb.
     */
    public void init(){
        waypoints = (HashMap<String,Object>)params.get("Waypoints");
        model = (HashMap<String,Object>)params.get("Model");
        debug = (HashMap<String,Object>)params.get("Debug");
        collision = (HashMap<String,Object>)params.get("Collision");
    }
    
    public void editingCanceled(ChangeEvent e){
    }

    public void editingStopped(ChangeEvent e){
        Object obj = e.getSource();
        if (obj instanceof TextFieldEditor) {
            TextFieldEditor editor = (TextFieldEditor)obj;
            String auv_name_tree = editor.getTreepath().getPathComponent(2).toString();//get the auv
            if(auv_name_tree.equals(getAuv_name())){//check if right auv
                saveValue(editor);
            }
        }
    }
    
    private void saveValue(TextFieldEditor editor){
        HashMap<String,Object> hashmap = params;
        String target = editor.getTreepath().getParentPath().getLastPathComponent().toString();
        int pathcount = editor.getTreepath().getPathCount();
        Object[] treepath = editor.getTreepath().getPath();
        
        if( params.containsKey(target) && pathcount < 7){//no hasmap, direct save
            Object obj = params.get(target);
            detectType(obj,editor,target,params);
        }else{//it's in another hashmap, search deeper
            for (int i = 4; i < pathcount-2; i++) {
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
            xmll.setPathElementAUV(getAuv_name(),treepath, pathcount, node_obj);
        }else if(obj instanceof Integer){
            hashmap.put(target, (Integer)node_obj);
            updateState(target);
            xmll.setPathElementAUV(getAuv_name(),treepath, pathcount, node_obj);
        }else if(obj instanceof Boolean){
            hashmap.put(target, (Boolean)node_obj);
            updateState(target);
            xmll.setPathElementAUV(getAuv_name(),treepath, pathcount, node_obj);
        }else if(obj instanceof String){
            hashmap.put(target, (String)node_obj);
            updateState(target);
            xmll.setPathElementAUV(getAuv_name(),treepath, pathcount, node_obj);
        }else if(obj instanceof Vector3f){
            hashmap.put(target, (Vector3f)node_obj);
            updateState(target);
            xmll.setPathElementAUV(getAuv_name(),treepath, pathcount, node_obj);
        }else if(obj instanceof ColorRGBA){
            hashmap.put(target, (ColorRGBA)node_obj);
            updateState(target);
            xmll.setPathElementAUV(getAuv_name(),treepath, pathcount, node_obj);
        }
    }

    public void updateState(String target){
        RigidBodyControl physics_control = auv.getPhysicsControl();
        if(target.equals("collision")){
           /* if( isDebugCollision() == false && physics_control != null ){
                physics_control.detachDebugShape();
            }else if( isDebugCollision() && physics_control != null ){
                Material debug_mat = new Material(auv.getAssetManager(), "Common/MatDefs/Misc/WireColor.j3md");
                debug_mat.setColor("Color", ColorRGBA.Red);
                //physics_control.attachDebugShape(debug_mat);
            }*/
        }else if(target.equals("position")){
            if(physics_control != null ){
                physics_control.setPhysicsLocation(getPosition());
            }
        }else if(target.equals("rotation")){
            if(physics_control != null ){
                Matrix3f m_rot = new Matrix3f();
                Quaternion q_rot = new Quaternion();
                q_rot.fromAngles(getRotation().x, getRotation().y, getRotation().z);
                m_rot.set(q_rot);
                physics_control.setPhysicsRotation(m_rot);
            }
        }else if(target.equals("scale")){
            auv.getAUVSpatial().setLocalScale(getModel_scale());
        }else if(target.equals("collisionbox")){
            /*if(physics_control != null ){
                CompoundCollisionShape compoundCollisionShape1 = new CompoundCollisionShape();
                BoxCollisionShape boxCollisionShape = new BoxCollisionShape(getDimensions());
                compoundCollisionShape1.addChildShape(boxCollisionShape, getCentroid_center_distance());
                RigidBodyControl new_physics_control = new RigidBodyControl(compoundCollisionShape1, getMass_auv());
                if(isDebugCollision()){
                    Material debug_mat = new Material(auv.getAssetManager(), "Common/MatDefs/Misc/WireColor.j3md");
                    debug_mat.setColor("Color", ColorRGBA.Red);
                    physics_control.attachDebugShape(debug_mat);
                }
                new_physics_control.setCollisionGroup(1);
                new_physics_control.setCollideWithGroups(1);
                new_physics_control.setDamping(getDamping_linear(), getDamping_angular());
                auv.setPhysicsControl(new_physics_control);
            }*/
        }
    }
    
    /**
     *
     * @param auv
     */
    public void setAuv(AUV auv) {
        this.auv = auv;
    }

    /**
     *
     * @return
     */
    public HashMap<String,Object> getAllVariables(){
        return params;
    }
    
    /**
     *
     * @return
     */
    public String getAuv_class() {
        return (String)params.get("auv_class");
    }

    /**
     *
     * @param auv_class
     */
    public void setAuv_class(String auv_class) {
        params.put("auv_class", auv_class);
    }
    
    /**
     *
     * @return
     */
    public String getAuv_name() {
        return (String)params.get("auv_name");
    }

    /**
     *
     * @param auv_name
     */
    public void setAuv_name(String auv_name) {
        params.put("auv_name", auv_name);
    }

    /**
     *
     * @return
     */
    public Vector3f getLinear_factor() {
        return (Vector3f)params.get("linear_factor");
    }

    /**
     *
     * @param linear_factor
     */
    public void setLinear_factor(Vector3f linear_factor) {
        params.put("linear_factor", linear_factor);
    }

    /**
     *
     * @return
     */
    public float getAngular_factor() {
         return (Float)params.get("angular_factor");
    }

    /**
     *
     * @param angular_factor
     */
    public void setAngular_factor(float angular_factor) {
        params.put("angular_factor", angular_factor);
    }

    /**
     *
     * @return
     */
    public int getMaxWaypoints() {
         return (Integer)waypoints.get("maxWaypoints");
    }

    /**
     *
     * @param maxWaypoints
     */
    public void setMaxWaypoints(int maxWaypoints) {
        waypoints.put("maxWaypoints", maxWaypoints);
    }

    /**
     *
     * @return
     */
    public ColorRGBA getWaypoints_color() {
         return (ColorRGBA)waypoints.get("color");
    }

    /**
     *
     * @param color
     */
    public void setWaypoints_color(ColorRGBA color) {
        waypoints.put("color", color);
    }

    /**
     *
     * @return
     */
    public boolean isWaypoints_enabled() {
         return (Boolean)waypoints.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setWaypoints_enabled(boolean enabled) {
        waypoints.put("enabled", enabled);
    }
    
    /**
     *
     * @return
     */
    public boolean isWaypoints_gradient() {
         return (Boolean)waypoints.get("gradient");
    }

    /**
     *
     * @param enabled
     */
    public void setWaypoints_gradient(boolean gradient) {
        waypoints.put("gradient", gradient);
    }

    /**
     *
     * @return
     */
    public float getWaypoints_updaterate() {
         return (Float)waypoints.get("updaterate");
    }

    /**
     *
     * @param updaterate
     */
    public void setWaypoints_updaterate(float updaterate) {
        waypoints.put("updaterate", updaterate);
    }

    /**
     *
     * @return
     */
    public boolean isWaypoints_visible() {
         return (Boolean)waypoints.get("visiblity");
    }

    /**
     *
     * @param visiblity
     */
    public void setWaypoints_visible(boolean visiblity) {
        waypoints.put("visiblity", visiblity);
    }

        /**
     *
     * @param physicalvalues_updaterate
     */
    public void setPhysicalvalues_updaterate(Float physicalvalues_updaterate) {
        params.put("physicalvalues_updaterate", physicalvalues_updaterate);
    }

    /**
     *
     * @return
     */
    public Float getPhysicalvalues_updaterate() {
        return (Float)params.get("physicalvalues_updaterate");
    }

    /**
     *
     * @return
     */
    public int getBuoyancy_updaterate() {
        return (Integer)params.get("buoyancy_updaterate");
    }

    /**
     *
     * @param buoyancy_updaterate
     */
    public void setBuoyancy_updaterate(int buoyancy_updaterate) {
        params.put("buoyancy_updaterate", buoyancy_updaterate);
    }

    /**
     *
     * @return
     */
    public Float getBuoyancy_distance() {
        return (Float)params.get("buoyancy_distance");
    }

    /**
     *
     * @param buoyancy_distance
     */
    public void setBuoyancy_distance(float buoyancy_distance) {
        params.put("buoyancy_distance", buoyancy_distance);
    }

    /**
     *
     * @return
     */
    public Float getBuoyancy_scale() {
        return (Float)params.get("buoyancy_scale");
    }

    /**
     *
     * @param buoyancy_scale
     */
    public void setBuoyancy_scale(float buoyancy_scale) {
        params.put("buoyancy_scale", buoyancy_scale);
    }

    /**
     *
     * @return
     */
    public int getDrag_updaterate() {
        return (Integer)params.get("drag_updaterate");
    }

    /**
     *
     * @param drag_updaterate
     */
    public void setDrag_updaterate(int drag_updaterate) {
        params.put("drag_updaterate", drag_updaterate);;
    }
    
    /**
     *
     * @return
     */
    public ColorRGBA getSelection_color() {
         return (ColorRGBA)model.get("selection_color");
    }

    /**
     *
     * @param color
     */
    public void setSelection_color(ColorRGBA color) {
        model.put("selection_color", color);
    }
    
        /**
     *
     * @return
     */
    public ColorRGBA getMapColor() {
         return (ColorRGBA)model.get("map_color");
    }

    /**
     *
     * @param color
     */
    public void setMapColor(ColorRGBA color) {
        model.put("map_color", color);
    }
    
    /**
     *
     * @return
     */
    public float getAlphaDepthScale() {
        return (Float)model.get("alpha_depth_scale");
    }

    /**
     *
     * @param scale
     */
    public void setAlphaDepthScale(float scale) {
        model.put("alpha_depth_scale", scale);
    }

    /**
     *
     * @return
     */
    public String getModel_name() {
        return (String)model.get("name");
    }

    /**
     *
     * @param name
     */
    public void setModel_name(String name) {
        model.put("name", name);
    }

    /**
     *
     * @return
     */
    public float getModel_scale() {
        return (Float)model.get("scale");
    }

    /**
     *
     * @param scale
     */
    public void setModel_scale(float scale) {
        model.put("scale", scale);
    }

    /**
     *
     * @return
     */
    public String getModelFilePath() {
        return (String)model.get("filepath");
    }

    /**
     *
     * @param filepath 
     */
    public void setModelFilePath(String filepath) {
        model.put("filepath", filepath);
    }

    /**
     *
     * @return
     */
    public float getDrag_coefficient_angular() {
        return (Float)params.get("drag_coefficient_angular");
    }

    /**
     *
     * @param drag_coefficient_angular
     */
    public void setDrag_coefficient_angular(float drag_coefficient_angular) {
        params.put("drag_coefficient_angular", drag_coefficient_angular);
    }

    /**
     *
     * @return
     */
    public float getDrag_coefficient_linear() {
        return (Float)params.get("drag_coefficient_linear");
    }

    /**
     *
     * @param drag_coefficient_linear
     */
    public void setDrag_coefficient_linear(float drag_coefficient_linear) {
        params.put("drag_coefficient_linear", drag_coefficient_linear);
    }

    /**
     *
     * @return
     */
    public Vector3f getCentroid_center_distance() {
        return (Vector3f)params.get("centroid_center_distance");
    }

    /**
     *
     * @param centroid_center_distance
     */
    public void setCentroid_center_distance(Vector3f centroid_center_distance) {
        params.put("centroid_center_distance", centroid_center_distance);
    }

    /**
     *
     * @return
     */
    public float getDamping_angular() {
        return (Float)params.get("damping_angular");
    }

    /**
     *
     * @param damping_angular
     */
    public void setDamping_angular(float damping_angular) {
        params.put("damping_angular", damping_angular);
    }

    /**
     *
     * @return
     */
    public Float getDamping_linear() {
        return (Float)params.get("damping_linear");
    }

    /**
     *
     * @param damping_linear
     */
    public void setDamping_linear(Float damping_linear) {
        params.put("damping_linear", damping_linear);
    }

    /**
     *
     * @return
     */
    public Vector3f getPosition() {
        return (Vector3f)params.get("position");
    }

    /**
     *
     * @param position
     */
    public void setPosition(Vector3f position) {
        params.put("position", position);
    }

    /**
     *
     * @return
     */
    public Vector3f getRotation() {
        return (Vector3f)params.get("rotation");
    }

    /**
     *
     * @param rotation
     */
    public void setRotation(Vector3f rotation) {
        params.put("rotation", rotation);
    }

    /**
     *
     * @return
     */
    public Vector3f getDimensions() {
        return (Vector3f)collision.get("dimensions");
    }

    /**
     *
     * @param dimensions
     */
    public void setDimensions(Vector3f dimensions) {
        collision.put("dimensions", dimensions);
    }
    
    /**
     *
     * @return
     */
    public Vector3f getCollisionPosition() {
        return (Vector3f)collision.get("collision_position");
    }

    /**
     *
     * @param collision_position 
     */
    public void setCollisionPosition(Vector3f collision_position) {
        collision.put("collision_position", collision_position);
    }

    /**
     *
     * @return
     */
    public int getType() {
        return (Integer)collision.get("type");
    }

    /**
     *
     * @param type
     */
    public void setType(int type) {
        collision.put("type", type);
    }

    /**
     *
     * @return
     */
    public float getMass_auv() {
        return (Float)params.get("mass_auv");
    }

    /**
     *
     * @param mass_auv
     */
    public void setMass_auv(float mass_auv) {
        params.put("mass_auv", mass_auv);
    }

    /**
     *
     * @return
     */
    public int getOffCamera_height() {
        return (Integer)params.get("offCamera_height");
    }

    /**
     *
     * @param offCamera_height
     */
    public void setOffCamera_height(int offCamera_height) {
        params.put("offCamera_height", offCamera_height);
    }

    /**
     *
     * @return
     */
    public int getOffCamera_width() {
        return (Integer)params.get("offCamera_width");
    }

    /**
     *
     * @param offCamera_width
     */
    public void setOffCamera_width(int offCamera_width) {
        params.put("offCamera_width", offCamera_width);
    }

    /**
     *
     * @return
     */
    public boolean isDebugDrag() {
         return (Boolean)debug.get("drag");
    }

    /**
     *
     * @param drag
     */
    public void setDebugDrag(boolean drag) {
        debug.put("drag", drag);
    }

    /**
     *
     * @return
     */
    public boolean isDebugBuoycancy() {
         return (Boolean)debug.get("buoycancy");
    }

    /**
     *
     * @param buoycancy
     */
    public void setDebugBuoycancy(boolean buoycancy) {
        debug.put("buoycancy", buoycancy);
    }

    /**
     *
     * @return
     */
    public boolean isDebugPhysicalExchanger() {
         return (Boolean)debug.get("physical_exchanger");
    }

    /**
     *
     * @param physical_exchanger
     */
    public void setDebugPhysicalExchanger(boolean physical_exchanger) {
        debug.put("physical_exchanger", physical_exchanger);
    }

    /**
     *
     * @return
     */
    public boolean isDebugCenters() {
         return (Boolean)debug.get("centers");
    }

    /**
     *
     * @param centers 
     */
    public void setDebugCenters(boolean centers) {
        debug.put("centers", centers);
    }

    /**
     *
     * @return
     */
    public boolean isDebugCollision() {
         return (Boolean)debug.get("collision");
    }

    /**
     *
     * @param collision
     */
    public void setDebugCollision(boolean collision) {
        debug.put("collision", collision);
    }
    
   /**
     *
     * @return
     */
    public boolean isEnabled() {
         return (Boolean)params.get("enabled");
    }

    /**
     *
     * @param enabled 
     */
    public void setEnabled(boolean enabled) {
        params.put("enabled", enabled);
    }

    /**
     *
     * @param value
     * @param hashmapname
     * @return
     */
    public Object getValue(String value,String hashmapname) {
        if(hashmapname.equals("") || hashmapname == null){
            return (Object)params.get(value);
        }else{
            HashMap<String,Object> hashmap = (HashMap<String,Object>)params.get(hashmapname);
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
            params.put(value, object);
        }else{
            HashMap<String,Object> hashmap = (HashMap<String,Object>)params.get(hashmapname);
            hashmap.put(value, object);
        }
    }

}
