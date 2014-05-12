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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.swing.tree.TreePath;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.PropertyChangeListenerSupport;
import mars.xml.HashMapAdapter;

/**
 *
 * @author Thomas Tosik
 */
@XmlRootElement(name="Parameters")
@XmlAccessorType(XmlAccessType.NONE)
public class AUV_Parameters implements PropertyChangeListenerSupport{

    @XmlJavaTypeAdapter(HashMapAdapter.class)
    private HashMap<String,Object> params = new HashMap<String,Object> ();
    private HashMap<String,Object> waypoints;
    private HashMap<String,Object> model;
    private HashMap<String,Object> debug;
    private HashMap<String,Object> collision;
    private HashMap<String,Object> buoyancy;
    private HashMap<String,Object> optimize;
    private AUV auv;

    private List listeners = Collections.synchronizedList(new LinkedList());
    
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
    
        

    @Override
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        listeners.add(pcl);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        listeners.remove(pcl);
    }

    private void fire(String propertyName, Object old, Object nue) {
        //Passing 0 below on purpose, so you only synchronize for one atomic call:
        PropertyChangeListener[] pcls = (PropertyChangeListener[]) listeners.toArray(new PropertyChangeListener[0]);
        for (int i = 0; i < pcls.length; i++) {
            pcls[i].propertyChange(new PropertyChangeEvent(this, propertyName, old, nue));
        }
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
    
    public void createDefault(){
        initAfterJAXB();
        setAlpha_Depth_Scale(3.0f);
        setAngular_factor(1.0f);
        setName("basicAUV");
        setOptimizeBatched(true);
        setBuoyancy_distance(0.0f);
        setBuoyancy_resolution(0.03125f);
        setBuoyancy_scale(0.9f);
        setBuoyancy_updaterate(1);
        setBuoyancy_Dimensions(Vector3f.UNIT_XYZ);
        setBuoyancy_Position(Vector3f.ZERO);
        setBuoyancyScale(Vector3f.UNIT_XYZ);
        setBuoyancy_Type(0);
        setCentroid_center_distance(Vector3f.ZERO);
        setCollisionPosition(Vector3f.ZERO);
        setDND_Icon("");
        setDamping_angular(0.1f);
        setDamping_linear(0.2f);
        setDebugBounding(false);
        setDebugBuoycancy(false);
        setDebugCenters(false);
        setDebugCollision(false);
        setDebugDrag(false);
        setDebugPhysicalExchanger(false);
        setDebugVisualizers(false);
        setDebugWireframe(false);
        setDebugBuoycancyVolume(false);
        setCollisionDimensions(Vector3f.UNIT_XYZ);
        setDrag_coefficient_angular(0.3f);
        setDrag_coefficient_linear(1.45f);
        setDrag_updaterate(1);
        setEnabled(true);
        setFlow_updaterate(0);
        setIcon("");
        setLinear_factor(Vector3f.UNIT_XYZ);
        setOptimizeLod(true);
        setOptimizeLodDistTolerance(1.0f);
        setOptimizeLodReduction1(0.3f);
        setOptimizeLodReduction2(0.6f);
        setOptimizeLodTrisPerPixel(0.5f);
        setMap_Color(ColorRGBA.Red);
        setMass(1.0f);
        setWaypointsMaxWaypoints(25);
        setModelFilepath("");
        setModelName("");
        setModelScale(0.1f);
        setOffCamera_height(240);
        setOffCamera_width(320);
        setPhysicalvalues_updaterate(0.0f);
        setPosition(Vector3f.ZERO);
        setRay_Detectable(false);
        setRotation(Vector3f.ZERO);
        setRotationQuaternion(Quaternion.IDENTITY);
        setSelection_color(ColorRGBA.Red);
        setCollisionType(1);
        setWaypointsLineWidth(5.0f);
        setWaypointsColor(ColorRGBA.White);
        setWaypointsEnabled(true);
        setWaypointsGradient(true);
        setWaypointsUpdaterate(1.0f);
        setWaypointsVisiblity(true);
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
            auv.getAUVSpatial().setLocalScale(getModelScale());
        }else if(target.equals("collisionbox")){
            /*if(physics_control != null ){
                CompoundCollisionShape compoundCollisionShape1 = new CompoundCollisionShape();
                BoxCollisionShape boxCollisionShape = new BoxCollisionShape(getCollisionDimensions());
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
            auv.setWaypointsEnabled(isWaypointsEnabled());
        }else if(target.equals("visiblity") && hashmapname.equals("Waypoints")){
            auv.setWayPointsVisible(isWaypointsVisiblity());
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
    public String getName() {
        return (String)params.get("name");
    }

    /**
     *
     * @param auv_name
     */
    public void setName(String name) {
        params.put("name", name);
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
    public String getDND_Icon() {
        return (String)params.get("dnd_icon");
    }

    /**
     *
     * @param dnd_icon 
     */
    public void setDND_Icon(String dnd_icon) {
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
    public Float getAngular_factor() {
         return (Float)params.get("angular_factor");
    }

    /**
     *
     * @param angular_factor
     */
    public void setAngular_factor(Float angular_factor) {
        params.put("angular_factor", angular_factor);
    }
    
    /**
     *
     * @return
     */
    public Float getThreatLevel() {
        Float ret = (Float)params.get("ThreatLevel");
        if(ret != null){
            return ret;
        }else{
            return 0f;
        }
    }

    /**
     *
     * @param ThreatLevel
     */
    public void setThreatLevel(Float ThreatLevel) {
        params.put("ThreatLevel", ThreatLevel);
    }
    
    /**
     *
     * @return
     */
    public Float getWaypointsLineWidth() {
         return (Float)waypoints.get("lineWidth");
    }

    /**
     *
     * @param lineWidth 
     */
    public void setWaypointsLineWidth(Float lineWidth) {
        waypoints.put("lineWidth", lineWidth);
    }
    
    /**
     *
     * @return
     */
    public Integer getWaypointsMaxWaypoints() {
         return (Integer)waypoints.get("maxWaypoints");
    }

    /**
     *
     * @param maxWaypoints
     */
    public void setWaypointsMaxWaypoints(Integer maxWaypoints) {
        waypoints.put("maxWaypoints", maxWaypoints);
    }

    /**
     *
     * @return
     */
    public ColorRGBA getWaypointsColor() {
         return (ColorRGBA)waypoints.get("color");
    }

    /**
     *
     * @param color
     */
    public void setWaypointsColor(ColorRGBA color) {
        waypoints.put("color", color);
    }

    /**
     *
     * @return
     */
    public Boolean isWaypointsEnabled() {
         return (Boolean)waypoints.get("enabled");
    }
    
    /**
     *
     * @return
     */
    public Boolean getWaypointsEnabled() {
         return (Boolean)waypoints.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setWaypointsEnabled(Boolean enabled) {
        waypoints.put("enabled", enabled);
    }
    
    /**
     *
     * @return
     */
    public Boolean isWaypointsGradient() {
         return (Boolean)waypoints.get("gradient");
    }
    
    /**
     *
     * @return
     */
    public Boolean getWaypointsGradient() {
         return (Boolean)waypoints.get("gradient");
    }

    /**
     *
     * @param gradient 
     */
    public void setWaypointsGradient(Boolean gradient) {
        waypoints.put("gradient", gradient);
    }

    /**
     *
     * @return
     */
    public Float getWaypointsUpdaterate() {
         return (Float)waypoints.get("updaterate");
    }

    /**
     *
     * @param updaterate
     */
    public void setWaypointsUpdaterate(Float updaterate) {
        waypoints.put("updaterate", updaterate);
    }

    /**
     *
     * @return
     */
    public Boolean isWaypointsVisiblity() {
         return (Boolean)waypoints.get("visiblity");
    }
    
    /**
     *
     * @return
     */
    public Boolean getWaypointsVisiblity() {
         return (Boolean)waypoints.get("visiblity");
    }

    /**
     *
     * @param visiblity
     */
    public void setWaypointsVisiblity(Boolean visiblity) {
        waypoints.put("visiblity", visiblity);
    }
    
    /**
     *
     * @return
     */
    public Boolean isRay_Detectable() {
        return (Boolean)params.get("ray_detectable");
    }

    /**
     *
     * @param sonar_detectable
     */
    public void setRay_Detectable(Boolean ray_detectable) {
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
    public Boolean isOptimizeBatched() {
        return (Boolean)optimize.get("batched");
    }
    
    /**
     *
     * @return
     */
    public Boolean getOptimizeBatched() {
        return (Boolean)optimize.get("batched");
    }

    /**
     *
     * @param sonar_detectable
     */
    public void setOptimizeBatched(Boolean batched) {
        optimize.put("batched", batched);
    }
    
    /**
     *
     * @return
     */
    public Boolean isOptimizeLod() {
        return (Boolean)optimize.get("lod");
    }
    
    /**
     *
     * @return
     */
    public Boolean getOptimizeLod() {
        return (Boolean)optimize.get("lod");
    }

    /**
     *
     * @param sonar_detectable
     */
    public void setOptimizeLod(Boolean lod) {
        optimize.put("lod", lod);
    }
    
    /**
     *
     * @return
     */
    public Float getOptimizeLodTrisPerPixel() {
        return (Float)optimize.get("LodTrisPerPixel");
    }

    /**
     *
     * @param LodTrisPerPixel
     */
    public void setOptimizeLodTrisPerPixel(Float LodTrisPerPixel) {
        optimize.put("LodTrisPerPixel", LodTrisPerPixel);
    }
    
    /**
     *
     * @return
     */
    public Float getOptimizeLodDistTolerance() {
        return (Float)optimize.get("LodDistTolerance");
    }

    /**
     *
     * @param LodDistTolerance
     */
    public void setOptimizeLodDistTolerance(Float LodDistTolerance) {
        optimize.put("LodDistTolerance", LodDistTolerance);
    }
    
    /**
     *
     * @return
     */
    public Float getOptimizeLodReduction1() {
        return (Float)optimize.get("LodReduction1");
    }

    /**
     *
     * @param LodReduction1
     */
    public void setOptimizeLodReduction1(Float LodReduction1) {
        optimize.put("LodReduction1", LodReduction1);
    }
    
    /**
     *
     * @return
     */
    public Float getOptimizeLodReduction2() {
        return (Float)optimize.get("LodReduction2");
    }

    /**
     *
     * @param LodReduction2
     */
    public void setOptimizeLodReduction2(Float LodReduction2) {
        optimize.put("LodReduction2", LodReduction2);
    }

    /**
     *
     * @return
     */
    public Integer getBuoyancy_updaterate() {
        return (Integer)buoyancy.get("buoyancy_updaterate");
    }

    /**
     *
     * @param buoyancy_updaterate
     */
    public void setBuoyancy_updaterate(Integer buoyancy_updaterate) {
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
    public void setBuoyancy_distance(Float buoyancy_distance) {
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
    public void setBuoyancy_scale(Float buoyancy_scale) {
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
    public void setBuoyancy_resolution(Float buoyancy_resolution) {
        buoyancy.put("buoyancy_resolution", buoyancy_resolution);
    }
    
        /**
     *
     * @return
     */
    public Vector3f getBuoyancy_Dimensions() {
        return (Vector3f)buoyancy.get("buoyancy_dimensions");
    }

    /**
     *
     * @param dimensions
     */
    public void setBuoyancy_Dimensions(Vector3f buoyancy_dimensions) {
        buoyancy.put("buoyancy_dimensions", buoyancy_dimensions);
    }
    
    /**
     *
     * @return
     */
    public Vector3f getBuoyancy_Position() {
        return (Vector3f)buoyancy.get("buoyancy_position");
    }

    /**
     *
     * @param collision_position 
     */
    public void setBuoyancy_Position(Vector3f buoyancy_position) {
        buoyancy.put("buoyancy_position", buoyancy_position);
    }
    
    /**
     *
     * @return
     */
    public Vector3f getBuoyancyScale() {
        return (Vector3f)buoyancy.get("buoyancyScale");
    }

    /**
     *
     * @param collision_position 
     */
    public void setBuoyancyScale(Vector3f buoyancyScale) {
        buoyancy.put("buoyancyScale", buoyancyScale);
    }

    /**
     *
     * @return
     */
    public Integer getBuoyancy_Type() {
        return (Integer)buoyancy.get("buoyancy_type");
    }

    /**
     *
     * @param type
     */
    public void setBuoyancy_Type(Integer buoyancy_type) {
        buoyancy.put("buoyancy_type", buoyancy_type);
    }

    /**
     *
     * @return
     */
    public Integer getDrag_updaterate() {
        return (Integer)params.get("drag_updaterate");
    }

    /**
     *
     * @param drag_updaterate
     */
    public void setDrag_updaterate(Integer drag_updaterate) {
        params.put("drag_updaterate", drag_updaterate);
    }
    
        /**
     *
     * @return
     */
    public Integer getFlow_updaterate() {
        return (Integer)params.get("flow_updaterate");
    }

    /**
     *
     * @param flow_updaterate 
     */
    public void setFlow_updaterate(Integer flow_updaterate) {
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
    public ColorRGBA getMap_Color() {
         return (ColorRGBA)model.get("map_color");
    }

    /**
     *
     * @param color
     */
    public void setMap_Color(ColorRGBA color) {
        model.put("map_color", color);
    }
    
    /**
     *
     * @return
     */
    public Float getAlpha_Depth_Scale() {
        return (Float)model.get("alpha_depth_scale");
    }

    /**
     *
     * @param scale
     */
    public void setAlpha_Depth_Scale(Float alpha_depth_scale) {
        model.put("alpha_depth_scale", alpha_depth_scale);
    }

    /**
     *
     * @return
     */
    public String getModelName() {
        return (String)model.get("name");
    }

    /**
     *
     * @param name
     */
    public void setModelName(String name) {
        model.put("name", name);
    }

    /**
     *
     * @return
     */
    public Float getModelScale() {
        return (Float)model.get("scale");
    }

    /**
     *
     * @param scale
     */
    public void setModelScale(Float scale) {
        model.put("scale", scale);
    }

    /**
     *
     * @return
     */
    public String getModelFilepath() {
        return (String)model.get("filepath");
    }

    /**
     *
     * @param filepath 
     */
    public void setModelFilepath(String filepath) {
        model.put("filepath", filepath);
    }

    /**
     *
     * @return
     */
    public Float getDrag_coefficient_angular() {
        return (Float)params.get("drag_coefficient_angular");
    }

    /**
     *
     * @param drag_coefficient_angular
     */
    public void setDrag_coefficient_angular(Float drag_coefficient_angular) {
        params.put("drag_coefficient_angular", drag_coefficient_angular);
    }

    /**
     *
     * @return
     */
    public Float getDrag_coefficient_linear() {
        return (Float)params.get("drag_coefficient_linear");
    }

    /**
     *
     * @param drag_coefficient_linear
     */
    public void setDrag_coefficient_linear(Float drag_coefficient_linear) {
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
    public Float getDamping_angular() {
        return (Float)params.get("damping_angular");
    }

    /**
     *
     * @param damping_angular
     */
    public void setDamping_angular(Float damping_angular) {
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
    public Vector3f getCollisionDimensions() {
        return (Vector3f)collision.get("dimensions");
    }

    /**
     *
     * @param dimensions
     */
    public void setCollisionDimensions(Vector3f dimensions) {
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
    public Integer getCollisionType() {
        return (Integer)collision.get("type");
    }

    /**
     *
     * @param type
     */
    public void setCollisionType(Integer type) {
        collision.put("type", type);
    }

    /**
     *
     * @return
     */
    public Float getMass() {
        return (Float)params.get("mass_auv");
    }

    /**
     *
     * @param mass_auv
     */
    public void setMass(Float mass_auv) {
        params.put("mass_auv", mass_auv);
    }

    /**
     *
     * @return
     */
    public Integer getOffCamera_height() {
        return (Integer)params.get("offCamera_height");
    }

    /**
     *
     * @param offCamera_height
     */
    public void setOffCamera_height(Integer offCamera_height) {
        params.put("offCamera_height", offCamera_height);
    }

    /**
     *
     * @return
     */
    public Integer getOffCamera_width() {
        return (Integer)params.get("offCamera_width");
    }

    /**
     *
     * @param offCamera_width
     */
    public void setOffCamera_width(Integer offCamera_width) {
        params.put("offCamera_width", offCamera_width);
    }

    /**
     *
     * @return
     */
    public Boolean isDebugDrag() {
         return (Boolean)debug.get("drag");
    }
    
    /**
     *
     * @return
     */
    public Boolean getDebugDrag() {
         return (Boolean)debug.get("drag");
    }

    /**
     *
     * @param drag
     */
    public void setDebugDrag(Boolean drag) {
        debug.put("drag", drag);
    }
    
    /**
     *
     * @return
     */
    public Boolean isDebugBounding() {
         return (Boolean)debug.get("bounding");
    }
    
    /**
     *
     * @return
     */
    public Boolean getDebugBounding() {
         return (Boolean)debug.get("bounding");
    }

    /**
     *
     * @param bounding 
     */
    public void setDebugBounding(Boolean bounding) {
        debug.put("bounding", bounding);
    }
    
    /**
     *
     * @return
     */
    public Boolean isDebugWireframe() {
         return (Boolean)debug.get("wireframe");
    }
    
    /**
     *
     * @return
     */
    public Boolean getDebugWireframe() {
         return (Boolean)debug.get("wireframe");
    }

    /**
     *
     * @param wireframe 
     */
    public void setDebugWireframe(Boolean wireframe) {
        debug.put("wireframe", wireframe);
    }

    /**
     *
     * @return
     */
    public Boolean isDebugBuoycancy() {
         return (Boolean)debug.get("buoycancy");
    }
    
    /**
     *
     * @return
     */
    public Boolean getDebugBuoycancy() {
         return (Boolean)debug.get("buoycancy");
    }

    /**
     *
     * @param buoycancy
     */
    public void setDebugBuoycancy(Boolean buoycancy) {
        debug.put("buoycancy", buoycancy);
    }
    
    /**
     *
     * @return
     */
    public Boolean isDebugBuoycancyVolume() {
         return (Boolean)debug.get("buoycancyVolume");
    }
    
    /**
     *
     * @return
     */
    public Boolean getDebugBuoycancyVolume() {
         return (Boolean)debug.get("buoycancyVolume");
    }

    /**
     *
     * @param buoycancy
     */
    public void setDebugBuoycancyVolume(Boolean buoycancyVolume) {
        debug.put("buoycancyVolume", buoycancyVolume);
    }

    /**
     *
     * @return
     */
    public Boolean isDebugPhysicalExchanger() {
         return (Boolean)debug.get("physical_exchanger");
    }
    
    /**
     *
     * @return
     */
    public Boolean getDebugPhysicalExchanger() {
         return (Boolean)debug.get("physical_exchanger");
    }

    /**
     *
     * @param physical_exchanger
     */
    public void setDebugPhysicalExchanger(Boolean physical_exchanger) {
        debug.put("physical_exchanger", physical_exchanger);
    }

    /**
     *
     * @return
     */
    public Boolean isDebugCenters() {
         return (Boolean)debug.get("centers");
    }
    
    /**
     *
     * @return
     */
    public Boolean getDebugCenters() {
         return (Boolean)debug.get("centers");
    }

    /**
     *
     * @param centers 
     */
    public void setDebugCenters(Boolean centers) {
        debug.put("centers", centers);
    }
    
    /**
     *
     * @return
     */
    public Boolean isDebugVisualizers() {
         return (Boolean)debug.get("visualizer");
    }
    
     /**
     *
     * @return
     */
    public Boolean getDebugVisualizers() {
         return (Boolean)debug.get("visualizer");
    }

    /**
     *
     * @param visualizer 
     */
    public void setDebugVisualizers(Boolean visualizer) {
        debug.put("visualizer", visualizer);
    }

    /**
     *
     * @return
     */
    public Boolean isDebugCollision() {
         return (Boolean)debug.get("collision");
    }
    
    /**
     *
     * @return
     */
    public Boolean getDebugCollision() {
         return (Boolean)debug.get("collision");
    }

    /**
     *
     * @param collision
     */
    public void setDebugCollision(Boolean collision) {
        debug.put("collision", collision);
    }
    
   /**
     *
     * @return
     */
    public Boolean isEnabled() {
         return (Boolean)params.get("enabled");
    }
    
    /**
     *
     * @return
     */
    public Boolean getEnabled() {
         return (Boolean)params.get("enabled");
    }

    /**
     *
     * @param enabled 
     */
    public void setEnabled(Boolean enabled) {
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
