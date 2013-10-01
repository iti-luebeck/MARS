/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.auv;

import mars.CollisionType;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.rits.cloning.Cloner;
import java.util.HashMap;
import javax.swing.tree.TreePath;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.xml.HashMapAdapter;

/**
 *
 * @author Thomas Tosik
 */
@XmlRootElement(name="Parameters")
@XmlAccessorType(XmlAccessType.NONE)
public class AUV_Parameters{

    @XmlJavaTypeAdapter(HashMapAdapter.class)
    private HashMap<String,Object> params = new HashMap<String,Object> ();
    private HashMap<String,Object> waypoints;
    private HashMap<String,Object> model;
    private HashMap<String,Object> debug;
    private HashMap<String,Object> collision;
    private HashMap<String,Object> buoyancy;
    private HashMap<String,Object> optimize;
    private AUV auv;

    /**
     * 
     */
    public AUV_Parameters(){
        
    }
    
    public AUV_Parameters copy(){
        Cloner cloner = new Cloner();
        cloner.dontCloneInstanceOf(AUV.class); 
        AUV_Parameters auvParamCopy = cloner.deepClone(this);
        return auvParamCopy;
    }
    
    /**
     * You have to initialize first when you read the data in trough jaxb.
     */
    public void initAfterJAXB(){
        waypoints = (HashMap<String,Object>)params.get("Waypoints");
        model = (HashMap<String,Object>)params.get("Model");
        debug = (HashMap<String,Object>)params.get("Debug");
        collision = (HashMap<String,Object>)params.get("Collision");
        buoyancy = (HashMap<String,Object>)params.get("Buoyancy");
        optimize = (HashMap<String,Object>)params.get("Optimize");
    }

    /**
     * 
     * @param path
     */
    public void updateState(TreePath path){
        System.out.println("TREEPATH: " + path);
        if(path.getPathComponent(2).equals(this)){//make sure we want to change auv params
            if( path.getParentPath().getLastPathComponent().toString().equals("AUVParameters")){
                updateState(path.getLastPathComponent().toString(),"");
            }else{
                updateState(path.getLastPathComponent().toString(),path.getParentPath().getLastPathComponent().toString());
            }
        }
    }
    
    /**
     * 
     * @param target
     * @param hashmapname
     */
    public void updateState(String target, String hashmapname){
        RigidBodyControl physics_control = auv.getPhysicsControl();
        if(target.equals("collision") && hashmapname.equals("Debug")){
            auv.setCollisionVisible(isDebugCollision());
        }else if(target.equals("position") && hashmapname.equals("")){
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
        }else if(target.equals("scale") && hashmapname.equals("Model")){
            auv.getAUVSpatial().setLocalScale(getModel_scale());
        }else if(target.equals("collisionbox")){
            /*if(physics_control != null ){
                CompoundCollisionShape compoundCollisionShape1 = new CompoundCollisionShape();
                BoxCollisionShape boxCollisionShape = new BoxCollisionShape(getDimensions());
                compoundCollisionShape1.addChildShape(boxCollisionShape, getCentroid_center_distance());
                RigidBodyControl new_physics_control = new RigidBodyControl(compoundCollisionShape1, getMass());
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
        }else if(target.equals("physical_exchanger") && hashmapname.equals("Debug")){
            auv.setPhysicalExchangerVisible(isDebugPhysicalExchanger());
        }else if(target.equals("centers") && hashmapname.equals("Debug")){
            auv.setCentersVisible(isDebugCenters());
        }else if(target.equals("visualizer") && hashmapname.equals("Debug")){
            auv.setVisualizerVisible(isDebugVisualizers());
        }else if(target.equals("bounding") && hashmapname.equals("Debug")){
            auv.setBoundingBoxVisible(isDebugBounding());
        }else if(target.equals("enable") && hashmapname.equals("Waypoints")){
            auv.setWaypointsEnabled(isWaypoints_enabled());
        }else if(target.equals("visiblity") && hashmapname.equals("Waypoints")){
            auv.setWayPointsVisible(isWaypoints_visible());
        }else if(target.equals("centroid_center_distance") && hashmapname.equals("")){
            
        }else if(target.equals("mass_auv") && hashmapname.equals("")){
            if(physics_control != null ){
                physics_control.setMass(getMass());
            }
        }else if(target.equals("enabled") && hashmapname.equals("")){
            if(!isEnabled()){
                //check if it exist before removing

            }
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
    public String getIcon() {
        return (String)params.get("icon");
    }

    /**
     *
     * @param icon 
     */
    public void setIcon(String icon) {
        params.put("icon", icon);
    }
    
        /**
     *
     * @return
     */
    public String getDNDIcon() {
        return (String)params.get("dnd_icon");
    }

    /**
     *
     * @param dnd_icon 
     */
    public void setDNDIcon(String dnd_icon) {
        params.put("dnd_icon", dnd_icon);
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
    public Float getWayPointLineWidth() {
         return (Float)waypoints.get("lineWidth");
    }

    /**
     *
     * @param lineWidth 
     */
    public void setWayPointLineWidth(float lineWidth) {
        waypoints.put("lineWidth", lineWidth);
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
     * @param gradient 
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
     * @return
     */
    public boolean isRayDetectable() {
        return (Boolean)params.get("ray_detectable");
    }

    /**
     *
     * @param sonar_detectable
     */
    public void setRayDetectable(boolean ray_detectable) {
        params.put("ray_detectable", ray_detectable);
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
    public boolean isBatched() {
        return (Boolean)optimize.get("batched");
    }

    /**
     *
     * @param sonar_detectable
     */
    public void setBatched(boolean batched) {
        optimize.put("batched", batched);
    }
    
    /**
     *
     * @return
     */
    public boolean isLod() {
        return (Boolean)optimize.get("lod");
    }

    /**
     *
     * @param sonar_detectable
     */
    public void setLod(boolean lod) {
        optimize.put("lod", lod);
    }
    
    /**
     *
     * @return
     */
    public Float getLodTrisPerPixel() {
        return (Float)optimize.get("LodTrisPerPixel");
    }

    /**
     *
     * @param LodTrisPerPixel
     */
    public void setLodTrisPerPixel(float LodTrisPerPixel) {
        optimize.put("LodTrisPerPixel", LodTrisPerPixel);
    }
    
    /**
     *
     * @return
     */
    public Float getLodDistTolerance() {
        return (Float)optimize.get("LodDistTolerance");
    }

    /**
     *
     * @param LodDistTolerance
     */
    public void setLodDistTolerance(float LodDistTolerance) {
        optimize.put("LodDistTolerance", LodDistTolerance);
    }
    
    /**
     *
     * @return
     */
    public Float getLodReduction1() {
        return (Float)optimize.get("LodReduction1");
    }

    /**
     *
     * @param LodReduction1
     */
    public void setLodReduction1(float LodReduction1) {
        optimize.put("LodReduction1", LodReduction1);
    }
    
    /**
     *
     * @return
     */
    public Float getLodReduction2() {
        return (Float)optimize.get("LodReduction2");
    }

    /**
     *
     * @param LodReduction2
     */
    public void setLodReduction2(float LodReduction2) {
        optimize.put("LodReduction2", LodReduction2);
    }

    /**
     *
     * @return
     */
    public int getBuoyancy_updaterate() {
        return (Integer)buoyancy.get("buoyancy_updaterate");
    }

    /**
     *
     * @param buoyancy_updaterate
     */
    public void setBuoyancy_updaterate(int buoyancy_updaterate) {
        buoyancy.put("buoyancy_updaterate", buoyancy_updaterate);
    }

    /**
     *
     * @return
     */
    public Float getBuoyancy_distance() {
        return (Float)buoyancy.get("buoyancy_distance");
    }

    /**
     *
     * @param buoyancy_distance
     */
    public void setBuoyancy_distance(float buoyancy_distance) {
        buoyancy.put("buoyancy_distance", buoyancy_distance);
    }

    /**
     *
     * @return
     */
    public Float getBuoyancy_scale() {
        return (Float)buoyancy.get("buoyancy_scale");
    }

    /**
     *
     * @param buoyancy_scale
     */
    public void setBuoyancy_scale(float buoyancy_scale) {
        buoyancy.put("buoyancy_scale", buoyancy_scale);
    }
    
        /**
     *
     * @return
     */
    public Float getBuoyancy_resolution() {
        return (Float)buoyancy.get("buoyancy_resolution");
    }

    /**
     *
     * @param buoyancy_resolution 
     */
    public void setBuoyancy_resolution(float buoyancy_resolution) {
        buoyancy.put("buoyancy_resolution", buoyancy_resolution);
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
        params.put("drag_updaterate", drag_updaterate);
    }
    
        /**
     *
     * @return
     */
    public int getFlow_updaterate() {
        return (Integer)params.get("flow_updaterate");
    }

    /**
     *
     * @param flow_updaterate 
     */
    public void setFlow_updaterate(int flow_updaterate) {
        params.put("flow_updaterate", flow_updaterate);
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
    public Quaternion getRotationQuaternion() {
        Quaternion quat = new Quaternion();
        Vector3f rotation = getRotation();
        quat.fromAngles(rotation.x, rotation.y, rotation.z);
        return quat;
    }

    /**
     *
     * @param rotation
     */
    public void setRotationQuaternion(Quaternion quaternion) {
        Vector3f axis = new Vector3f();
        quaternion.toAngleAxis(axis);
        setRotation(axis);
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
    public float getMass() {
        return (Float)params.get("mass_auv");
    }

    /**
     *
     * @param mass_auv
     */
    public void setMass(float mass_auv) {
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
    public boolean isDebugBounding() {
         return (Boolean)debug.get("bounding");
    }

    /**
     *
     * @param bounding 
     */
    public void setDebugBounding(boolean bounding) {
        debug.put("bounding", bounding);
    }
    
    /**
     *
     * @return
     */
    public boolean isDebugWireframe() {
         return (Boolean)debug.get("wireframe");
    }

    /**
     *
     * @param wireframe 
     */
    public void setDebugWireframe(boolean wireframe) {
        debug.put("wireframe", wireframe);
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
    public boolean isDebugVisualizers() {
         return (Boolean)debug.get("visualizer");
    }

    /**
     *
     * @param visualizer 
     */
    public void setDebugVisualizers(boolean visualizer) {
        debug.put("visualizer", visualizer);
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

    @Override
    public String toString(){
        return "AUVParameters";
    }
}
