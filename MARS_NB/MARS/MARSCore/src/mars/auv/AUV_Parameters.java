/*
* Copyright (c) 2015, Institute of Computer Engineering, University of Lübeck
* All rights reserved.
* 
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* 
* * Redistributions of source code must retain the above copyright notice, this
*   list of conditions and the following disclaimer.
* 
* * Redistributions in binary form must reproduce the above copyright notice,
*   this list of conditions and the following disclaimer in the documentation
*   and/or other materials provided with the distribution.
* 
* * Neither the name of the copyright holder nor the names of its
*   contributors may be used to endorse or promote products derived from
*   this software without specific prior written permission.
* 
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
* FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
* CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package mars.auv;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.rits.cloning.Cloner;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.misc.PropertyChangeListenerSupport;
import mars.server.ConnectionType;
import mars.xml.HashMapAdapter;

/**
 * Thsi class is a bucket for parameters of an AUV like mass or collision boxes.
 * Basically just HashMaps.
 *
 * @author Thomas Tosik
 */
@XmlRootElement(name = "Parameters")
@XmlAccessorType(XmlAccessType.NONE)
public class AUV_Parameters implements PropertyChangeListenerSupport {

    @XmlJavaTypeAdapter(HashMapAdapter.class)
    private HashMap<String, Object> params = new HashMap<String, Object>();
    private HashMap<String, Object> waypoints;
    private HashMap<String, Object> model;
    private HashMap<String, Object> debug;
    private HashMap<String, Object> collision;
    private HashMap<String, Object> buoyancy;
    private HashMap<String, Object> optimize;
    private HashMap<String, Object> connection;
    private AUV auv;

    private List<PropertyChangeListener> listeners = Collections.synchronizedList(new LinkedList<PropertyChangeListener>());

    /**
     *
     */
    public AUV_Parameters() {

    }

    /**
     *
     * @return
     */
    public AUV_Parameters copy() {
        Cloner cloner = new Cloner();
        cloner.dontCloneInstanceOf(AUV.class, List.class);
        AUV_Parameters auvParamCopy = cloner.deepClone(this);
        return auvParamCopy;
    }

    /**
     *
     * @param pcl
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        listeners.add(pcl);
    }

    /**
     *
     * @param pcl
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        listeners.remove(pcl);
    }

    private void fire(String propertyName, Object old, Object nue) {
        //Passing 0 below on purpose, so you only synchronize for one atomic call:
        PropertyChangeListener[] pcls = listeners.toArray(new PropertyChangeListener[0]);
        for (PropertyChangeListener pcl : pcls) {
            pcl.propertyChange(new PropertyChangeEvent(this, propertyName, old, nue));
        }
        updateVariable(propertyName);
    }

    /**
     * You have to initialize first when you read the data in trough jaxb.
     */
    @SuppressWarnings("unchecked")
    public void initAfterJAXB() {
        waypoints = (HashMap<String, Object>) params.get("Waypoints");
        model = (HashMap<String, Object>) params.get("Model");
        debug = (HashMap<String, Object>) params.get("Debug");
        collision = (HashMap<String, Object>) params.get("Collision");
        buoyancy = (HashMap<String, Object>) params.get("Buoyancy");
        optimize = (HashMap<String, Object>) params.get("Optimize");
        connection = (HashMap<String, Object>) params.get("Connection");
    }

    /**
     * Creates an default filled AUVParameters.
     */
    public void createDefault() {
        initAfterJAXB();
        setModelAlphaDepthScale(3.0f);
        setAngular_factor(1.0f);
        setName("basicAUV");
        setOptimizeBatched(true);
        setBuoyancyDistance(0.0f);
        setBuoyancyResolution(0.03125f);
        setBuoyancyFactor(0.9f);
        setBuoyancyUpdaterate(1);
        setBuoyancyDimensions(Vector3f.UNIT_XYZ);
        setBuoyancyPosition(Vector3f.ZERO);
        setBuoyancyScale(Vector3f.UNIT_XYZ);
        setBuoyancyType(0);
        setCentroid_center_distance(Vector3f.ZERO);
        setCollisionPosition(Vector3f.ZERO);
        setDndIcon("");
        setDamping_angular(0.1f);
        setDamping_linear(0.2f);
        setDebugBounding(false);
        setDebugBuoycancy(false);
        setDebugCenters(false);
        setDebugCollision(false);
        setDebugDrag(false);
        setDebugPhysicalExchanger(false);
        setDebugVisualizer(false);
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
        setModelMapColor(ColorRGBA.Red);
        setMass(1.0f);
        setWaypointsMaxWaypoints(25);
        setModelFilepath("");
        setModelName("");
        setModelScale(0.1f);
        setOffCamera_height(240);
        setOffCamera_width(320);
        setPhysicalvalues_updaterate(0.0f);
        setPosition(Vector3f.ZERO);
        setRayDetectable(false);
        setRotation(Vector3f.ZERO);
        setRotationQuaternion(Quaternion.IDENTITY);
        setModelSelectionColor(ColorRGBA.Red);
        setCollisionType(1);
        setWaypointsLineWidth(5.0f);
        setWaypointsColor(ColorRGBA.White);
        setWaypointsEnabled(true);
        setWaypointsGradient(true);
        setWaypointsUpdaterate(1.0f);
        setWaypointsVisiblity(true);
        setConnectionEnabled(false);
        setConnectionGlobalQueueSize(10);
        setConnectionLocalip("127.0.0.1");
        setConnectionTargetip("127.0.0.1");
        setConnectionPort(11311);
        setConnectionType(ConnectionType.ROS);
    }

    /**
     *
     * @param target
     */
    public void updateVariable(String target) {
        RigidBodyControl physics_control = auv.getPhysicsControl();
        if (target.equals("position")) {
            if (physics_control != null) {
                physics_control.setPhysicsLocation(getPosition());
            }
        }/*else if(target.equals("collision") && hashmapname.equals("Debug")){
         auv.setCollisionVisible(isDebugCollision());
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
         }else if(target.equals("collisionbox")){*/
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
        /*}else if(target.equals("physical_exchanger") && hashmapname.equals("Debug")){
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
         }     */

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
     * @return The main HashMap with all variables.
     */
    public HashMap<String, Object> getAllVariables() {
        return params;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return (String) params.get("name");
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        params.put("name", name);
    }

    /**
     *
     * @return
     */
    public String getIcon() {
        return (String) params.get("icon");
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
    public String getDndIcon() {
        return (String) params.get("dndIcon");
    }

    /**
     *
     * @param dndIcon
     */
    public void setDndIcon(String dndIcon) {
        params.put("dndIcon", dndIcon);
    }

    /**
     *
     * @return
     */
    public Vector3f getLinear_factor() {
        return (Vector3f) params.get("linear_factor");
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
        return (Float) params.get("angular_factor");
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
        Float ret = (Float) params.get("ThreatLevel");
        if (ret != null) {
            return ret;
        } else {
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
        return (Float) waypoints.get("lineWidth");
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
        return (Integer) waypoints.get("maxWaypoints");
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
        return (ColorRGBA) waypoints.get("color");
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
        return (Boolean) waypoints.get("enabled");
    }

    /**
     *
     * @return
     */
    public Boolean getWaypointsEnabled() {
        return (Boolean) waypoints.get("enabled");
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
        return (Boolean) waypoints.get("gradient");
    }

    /**
     *
     * @return
     */
    public Boolean getWaypointsGradient() {
        return (Boolean) waypoints.get("gradient");
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
        return (Float) waypoints.get("updaterate");
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
        return (Boolean) waypoints.get("visiblity");
    }

    /**
     *
     * @return
     */
    public Boolean getWaypointsVisiblity() {
        return (Boolean) waypoints.get("visiblity");
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
    public Boolean isRayDetectable() {
        return (Boolean) params.get("rayDetectable");
    }

    /**
     *
     * @return
     */
    public Boolean getRayDetectable() {
        return (Boolean) params.get("rayDetectable");
    }

    /**
     *
     * @param rayDetectable
     */
    public void setRayDetectable(Boolean rayDetectable) {
        params.put("rayDetectable", rayDetectable);
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
        return (Float) params.get("physicalvalues_updaterate");
    }

    /**
     *
     * @return
     */
    public Boolean isOptimizeBatched() {
        return (Boolean) optimize.get("batched");
    }

    /**
     *
     * @return
     */
    public Boolean getOptimizeBatched() {
        return (Boolean) optimize.get("batched");
    }

    /**
     *
     * @param batched
     */
    public void setOptimizeBatched(Boolean batched) {
        optimize.put("batched", batched);
    }

    /**
     *
     * @return
     */
    public Boolean isOptimizeLod() {
        return (Boolean) optimize.get("lod");
    }

    /**
     *
     * @return
     */
    public Boolean getOptimizeLod() {
        return (Boolean) optimize.get("lod");
    }

    /**
     *
     * @param lod
     */
    public void setOptimizeLod(Boolean lod) {
        optimize.put("lod", lod);
    }

    /**
     *
     * @return
     */
    public Float getOptimizeLodTrisPerPixel() {
        return (Float) optimize.get("LodTrisPerPixel");
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
        return (Float) optimize.get("LodDistTolerance");
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
        return (Float) optimize.get("LodReduction1");
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
        return (Float) optimize.get("LodReduction2");
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
    public Integer getBuoyancyUpdaterate() {
        return (Integer) buoyancy.get("updaterate");
    }

    /**
     *
     * @param updaterate
     */
    public void setBuoyancyUpdaterate(Integer updaterate) {
        buoyancy.put("updaterate", updaterate);
    }

    /**
     *
     * @return
     */
    public Float getBuoyancyDistance() {
        return (Float) buoyancy.get("distance");
    }

    /**
     *
     * @param distance
     */
    public void setBuoyancyDistance(Float distance) {
        buoyancy.put("distance", distance);
    }

    /**
     *
     * @return
     */
    public Float getBuoyancyFactor() {
        return (Float) buoyancy.get("factor");
    }

    /**
     *
     * @param factor
     */
    public void setBuoyancyFactor(Float factor) {
        buoyancy.put("factor", factor);
    }

    /**
     *
     * @return
     */
    public Float getBuoyancyResolution() {
        return (Float) buoyancy.get("resolution");
    }

    /**
     *
     * @param resolution
     * @param buoyancy_resolution
     */
    public void setBuoyancyResolution(Float resolution) {
        buoyancy.put("resolution", resolution);
    }

    /**
     *
     * @return
     */
    public Vector3f getBuoyancyDimensions() {
        return (Vector3f) buoyancy.get("dimensions");
    }

    /**
     *
     * @param dimensions
     * @param buoyancy_dimensions
     */
    public void setBuoyancyDimensions(Vector3f dimensions) {
        buoyancy.put("dimensions", dimensions);
    }

    /**
     *
     * @return
     */
    public Vector3f getBuoyancyPosition() {
        return (Vector3f) buoyancy.get("position");
    }

    /**
     *
     * @param position
     */
    public void setBuoyancyPosition(Vector3f position) {
        buoyancy.put("position", position);
    }

    /**
     *
     * @return
     */
    public Vector3f getBuoyancyScale() {
        return (Vector3f) buoyancy.get("scale");
    }

    /**
     *
     * @param scale
     */
    public void setBuoyancyScale(Vector3f scale) {
        buoyancy.put("scale", scale);
    }

    /**
     *
     * @return
     */
    public Integer getBuoyancyType() {
        return (Integer) buoyancy.get("type");
    }

    /**
     *
     * @param type
     */
    public void setBuoyancyType(Integer type) {
        buoyancy.put("type", type);
    }

    /**
     *
     * @return
     */
    public Integer getDrag_updaterate() {
        return (Integer) params.get("drag_updaterate");
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
        return (Integer) params.get("flow_updaterate");
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
    public ColorRGBA getModelSelectionColor() {
        return (ColorRGBA) model.get("selectionColor");
    }

    /**
     *
     * @param selectionColor
     * @param color
     */
    public void setModelSelectionColor(ColorRGBA selectionColor) {
        model.put("selectionColor", selectionColor);
    }

    /**
     *
     * @return
     */
    public ColorRGBA getModelMapColor() {
        return (ColorRGBA) model.get("mapColor");
    }

    /**
     *
     * @param mapColor
     */
    public void setModelMapColor(ColorRGBA mapColor) {
        model.put("mapColor", mapColor);
    }

    /**
     *
     * @return
     */
    public Float getModelAlphaDepthScale() {
        return (Float) model.get("alphaDepthScale");
    }

    /**
     *
     * @param alphaDepthScale
     */
    public void setModelAlphaDepthScale(Float alphaDepthScale) {
        model.put("alphaDepthScale", alphaDepthScale);
    }

    /**
     *
     * @return
     */
    public String getModelName() {
        return (String) model.get("name");
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
        return (Float) model.get("scale");
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
        return (String) model.get("filepath");
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
        return (Float) params.get("drag_coefficient_angular");
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
        return (Float) params.get("drag_coefficient_linear");
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
        return (Vector3f) params.get("centroid_center_distance");
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
        return (Float) params.get("damping_angular");
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
        return (Float) params.get("damping_linear");
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
        return (Vector3f) params.get("position");
    }

    /**
     *
     * @param position
     */
    public void setPosition(Vector3f position) {
        Vector3f old = getPosition();
        //PhysicalExchangerName = name;
        params.put("position", position);
        fire("position", old, position);
    }

    /**
     *
     * @return
     */
    public Vector3f getRotation() {
        return (Vector3f) params.get("rotation");
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
     * @param quaternion
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
        return (Vector3f) collision.get("dimensions");
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
        return (Vector3f) collision.get("position");
    }

    /**
     *
     * @param position
     */
    public void setCollisionPosition(Vector3f position) {
        collision.put("position", position);
    }

    /**
     *
     * @return
     */
    public Integer getCollisionType() {
        return (Integer) collision.get("type");
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
        return (Float) params.get("mass");
    }

    /**
     *
     * @param mass
     * @param mass_auv
     */
    public void setMass(Float mass) {
        params.put("mass", mass);
    }

    /**
     *
     * @return
     */
    public Integer getOffCamera_height() {
        return (Integer) params.get("offCamera_height");
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
        return (Integer) params.get("offCamera_width");
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
        return (Boolean) debug.get("drag");
    }

    /**
     *
     * @return
     */
    public Boolean getDebugDrag() {
        return (Boolean) debug.get("drag");
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
        return (Boolean) debug.get("bounding");
    }

    /**
     *
     * @return
     */
    public Boolean getDebugBounding() {
        return (Boolean) debug.get("bounding");
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
        return (Boolean) debug.get("wireframe");
    }

    /**
     *
     * @return
     */
    public Boolean getDebugWireframe() {
        return (Boolean) debug.get("wireframe");
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
        return (Boolean) debug.get("buoycancy");
    }

    /**
     *
     * @return
     */
    public Boolean getDebugBuoycancy() {
        return (Boolean) debug.get("buoycancy");
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
        return (Boolean) debug.get("buoycancyVolume");
    }

    /**
     *
     * @return
     */
    public Boolean getDebugBuoycancyVolume() {
        return (Boolean) debug.get("buoycancyVolume");
    }

    /**
     *
     * @param buoycancyVolume
     */
    public void setDebugBuoycancyVolume(Boolean buoycancyVolume) {
        debug.put("buoycancyVolume", buoycancyVolume);
    }

    /**
     *
     * @return
     */
    public Boolean isDebugPhysicalExchanger() {
        return (Boolean) debug.get("physicalExchanger");
    }

    /**
     *
     * @return
     */
    public Boolean getDebugPhysicalExchanger() {
        return (Boolean) debug.get("physicalExchanger");
    }

    /**
     *
     * @param physical_exchanger
     */
    public void setDebugPhysicalExchanger(Boolean physicalExchanger) {
        debug.put("physicalExchanger", physicalExchanger);
    }

    /**
     *
     * @return
     */
    public Boolean isDebugCenters() {
        return (Boolean) debug.get("centers");
    }

    /**
     *
     * @return
     */
    public Boolean getDebugCenters() {
        return (Boolean) debug.get("centers");
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
        return (Boolean) debug.get("visualizer");
    }

    /**
     *
     * @return
     */
    public Boolean getDebugVisualizer() {
        return (Boolean) debug.get("visualizer");
    }

    /**
     *
     * @param visualizer
     */
    public void setDebugVisualizer(Boolean visualizer) {
        debug.put("visualizer", visualizer);
    }

    /**
     *
     * @return
     */
    public Boolean isDebugCollision() {
        return (Boolean) debug.get("collision");
    }

    /**
     *
     * @return
     */
    public Boolean getDebugCollision() {
        return (Boolean) debug.get("collision");
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
        return (Boolean) params.get("enabled");
    }

    /**
     *
     * @return
     */
    public Boolean getEnabled() {
        return (Boolean) params.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setEnabled(Boolean enabled) {
        Boolean old = getEnabled();
        params.put("enabled", enabled);
        fire("enabled", old, enabled);
    }
    
    /**
     *
     * @return
     */
    public Boolean getConnectionEnabled() {
        return (Boolean) connection.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setConnectionEnabled(Boolean enabled) {
        connection.put("enabled", enabled);
    }
    
    /**
     *
     * @return
     */
    public String getConnectionLocalip() {
        return (String) connection.get("localip");
    }

    /**
     *
     * @param localip
     */
    public void setConnectionLocalip(String localip) {
        connection.put("localip", localip);
    }
    
    /**
     *
     * @return
     */
    public String getConnectionTargetip() {
        return (String) connection.get("targetip");
    }

    /**
     *
     * @param targetip
     */
    public void setConnectionTargetip(String targetip) {
        connection.put("targetip", targetip);
    }
    
    /**
     *
     * @return
     */
    public Integer getConnectionPort() {
        return (Integer) connection.get("port");
    }

    /**
     *
     * @param port
     */
    public void setConnectionPort(Integer port) {
        connection.put("port", port);
    }
    
    /**
     *
     * @return
     */
    public Integer getConnectionGlobalQueueSize() {
        return (Integer) connection.get("GlobalQueueSize");
    }

    /**
     *
     * @param GlobalQueueSize
     */
    public void setConnectionGlobalQueueSize(Integer GlobalQueueSize) {
        connection.put("GlobalQueueSize", GlobalQueueSize);
    }
    
    /**
     *
     * @return
     */
    public String getConnectionType() {
        return (String) connection.get("type");
    }

    /**
     *
     * @param type
     */
    public void setConnectionType(String type) {
        connection.put("type", type);
    }

    /**
     * Get a value of a specific HashMap.
     * 
     * @param value
     * @param hashmapname
     * @return
     */
    @SuppressWarnings("unchecked")
    public Object getValue(String value, String hashmapname) {
        if (hashmapname.equals("") || hashmapname == null) {
            return params.get(value);
        } else {
            HashMap<String, Object> hashmap = (HashMap<String, Object>) params.get(hashmapname);
            return hashmap.get(value);
        }
    }

    /**
     * Set a value of specific HashMap.
     * 
     * @param value
     * @param object
     * @param hashmapname
     */
    @SuppressWarnings("unchecked")
    public void setValue(String value, Object object, String hashmapname) {
        if (hashmapname.equals("") || hashmapname == null) {
            params.put(value, object);
        } else {
            HashMap<String, Object> hashmap = (HashMap<String, Object>) params.get(hashmapname);
            hashmap.put(value, object);
        }
    }

    @Override
    public String toString() {
        return "AUVParameters";
    }
}
