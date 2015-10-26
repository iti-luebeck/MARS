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
import mars.communication.AUVConnectionType;
import mars.misc.PropertyChangeListenerSupport;
import mars.xml.HashMapAdapter;

/**
 * Thsi class is a bucket for parameters of an AUV like mass or collision boxes. Basically just HashMaps.
 *
 * @author Thomas Tosik
 */
@XmlRootElement(name = "Parameters")
@XmlAccessorType(XmlAccessType.NONE)
public class AUV_Parameters implements PropertyChangeListenerSupport {

    @XmlJavaTypeAdapter(HashMapAdapter.class)
    private final HashMap<String, Object> params = new HashMap<String, Object>();
    private HashMap<String, Object> waypoints;
    private HashMap<String, Object> model;
    private HashMap<String, Object> debug;
    private HashMap<String, Object> collision;
    private HashMap<String, Object> buoyancy;
    private HashMap<String, Object> optimize;
    private HashMap<String, Object> connection;
    private AUV auv;

    private final List<PropertyChangeListener> listeners = Collections.synchronizedList(new LinkedList<PropertyChangeListener>());

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

    /**
     *
     */
    @Override
    public void removeAllPropertyChangeListeners() {
        listeners.clear();
    }
    
    private void fire(String propertyName, Object old, Object nue) {
        //Passing 0 below on purpose, so you only synchronize for one atomic call:
        PropertyChangeListener[] pcls = listeners.toArray(new PropertyChangeListener[0]);
        for (PropertyChangeListener pcl : pcls) {
            pcl.propertyChange(new PropertyChangeEvent(this, propertyName, old, nue));
        }
    }

    /**
     * You have to initialize first when you read the data in trough jaxb.
     */
    @SuppressWarnings("unchecked")
    public void initAfterJAXB() {
        waypoints = (HashMap<String, Object>) params.get("DistanceCoveredPath");
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
        setDistanceCoveredPathMaxPoints(25);
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
        setDistanceCoveredPathLineWidth(5.0f);
        setDistanceCoveredPathColor(ColorRGBA.White);
        setDistanceCoveredPathEnabled(true);
        setDistanceCoveredPathGradient(true);
        setDistanceCoveredPathUpdaterate(1.0f);
        setDistanceCoveredPathVisiblity(true);
        setConnectionEnabled(false);
        setConnectionGlobalQueueSize(10);
        setConnectionLocalip("127.0.0.1");
        setConnectionTargetip("127.0.0.1");
        setConnectionPort(11311);
        setConnectionType(AUVConnectionType.ROS.toString());
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
        String old = getName();
        params.put("name", name);
        fire("name", old, name);
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
        String old = getIcon();
        params.put("icon", icon);
        fire("icon", old, icon);
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
        String old = getDndIcon();
        params.put("dndIcon", dndIcon);
        fire("dndIcon", old, dndIcon);
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
        Vector3f old = getLinear_factor();
        params.put("linear_factor", linear_factor);
        fire("linear_factor", old, linear_factor);
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
        Float old = getAngular_factor();
        params.put("angular_factor", angular_factor);
        fire("angular_factor", old, angular_factor);
    }

    /**
     *
     * @return
     */
    public Boolean getManualControl() {
        return (Boolean) params.get("manualControl");
    }

    /**
     *
     * @param manualControl
     */
    public void setManualControl(Boolean manualControl) {
        Boolean old = getManualControl();
        params.put("manualControl", manualControl);
        fire("manualControl", old, manualControl);
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
        Float old = getThreatLevel();
        params.put("ThreatLevel", ThreatLevel);
        fire("ThreatLevel", old, ThreatLevel);
    }

    /**
     *
     * @return
     */
    public Float getDistanceCoveredPathLineWidth() {
        return (Float) waypoints.get("lineWidth");
    }

    /**
     *
     * @param lineWidth
     */
    public void setDistanceCoveredPathLineWidth(Float lineWidth) {
        Float old = getDistanceCoveredPathLineWidth();
        waypoints.put("lineWidth", lineWidth);
        fire("lineWidth", old, lineWidth);
    }

    /**
     *
     * @return
     */
    public Integer getDistanceCoveredPathMaxPoints() {
        return (Integer) waypoints.get("maxPoints");
    }

    /**
     * 
     * @param maxPoints 
     */
    public void setDistanceCoveredPathMaxPoints(Integer maxPoints) {
        Integer old = getDistanceCoveredPathMaxPoints();
        waypoints.put("maxPoints", maxPoints);
        fire("maxPoints", old, maxPoints);
    }

    /**
     *
     * @return
     */
    public ColorRGBA getDistanceCoveredPathColor() {
        return (ColorRGBA) waypoints.get("color");
    }

    /**
     *
     * @param color
     */
    public void setDistanceCoveredPathColor(ColorRGBA color) {
        ColorRGBA old = getDistanceCoveredPathColor();
        waypoints.put("color", color);
        fire("color", old, color);
    }

    /**
     *
     * @return
     */
    public Boolean isDistanceCoveredPathEnabled() {
        return (Boolean) waypoints.get("enabled");
    }

    /**
     *
     * @return
     */
    public Boolean getDistanceCoveredPathEnabled() {
        return (Boolean) waypoints.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setDistanceCoveredPathEnabled(Boolean enabled) {
        Boolean old = getDistanceCoveredPathEnabled();
        waypoints.put("enabled", enabled);
        fire("distanceCoveredPathEnabled", old, enabled);
    }

    /**
     *
     * @return
     */
    public Boolean isDistanceCoveredPathGradient() {
        return (Boolean) waypoints.get("gradient");
    }

    /**
     *
     * @return
     */
    public Boolean getDistanceCoveredPathGradient() {
        return (Boolean) waypoints.get("gradient");
    }

    /**
     *
     * @param gradient
     */
    public void setDistanceCoveredPathGradient(Boolean gradient) {
        Boolean old = getDistanceCoveredPathGradient();
        waypoints.put("gradient", gradient);
        fire("gradient", old, gradient);
    }

    /**
     *
     * @return
     */
    public Float getDistanceCoveredPathUpdaterate() {
        return (Float) waypoints.get("updaterate");
    }

    /**
     *
     * @param updaterate
     */
    public void setDistanceCoveredPathUpdaterate(Float updaterate) {
        Float old = getDistanceCoveredPathUpdaterate();
        waypoints.put("updaterate", updaterate);
        fire("updaterate", old, updaterate);
    }

    /**
     *
     * @return
     */
    public Boolean isDistanceCoveredPathVisiblity() {
        return (Boolean) waypoints.get("visiblity");
    }

    /**
     *
     * @return
     */
    public Boolean getDistanceCoveredPathVisiblity() {
        return (Boolean) waypoints.get("visiblity");
    }

    /**
     *
     * @param visiblity
     */
    public void setDistanceCoveredPathVisiblity(Boolean visiblity) {
        Boolean old = getDistanceCoveredPathVisiblity();
        waypoints.put("visiblity", visiblity);
        fire("distanceCoveredPathVisiblity", old, visiblity);
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
        Boolean old = getRayDetectable();
        params.put("rayDetectable", rayDetectable);
        fire("rayDetectable", old, rayDetectable);
    }

    /**
     *
     * @param physicalvalues_updaterate
     */
    public void setPhysicalvalues_updaterate(Float physicalvalues_updaterate) {
        Float old = getPhysicalvalues_updaterate();
        params.put("physicalvalues_updaterate", physicalvalues_updaterate);
        fire("physicalvalues_updaterate", old, physicalvalues_updaterate);
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
        boolean old = getOptimizeBatched();
        optimize.put("batched", batched);
        fire("batched", old, batched);
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
        boolean old = getOptimizeLod();
        optimize.put("lod", lod);
        fire("lod", old, lod);
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
        Float old = getOptimizeLodTrisPerPixel();
        optimize.put("LodTrisPerPixel", LodTrisPerPixel);
        fire("LodTrisPerPixel", old, LodTrisPerPixel);
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
        Float old = getOptimizeLodDistTolerance();
        optimize.put("LodDistTolerance", LodDistTolerance);
        fire("LodDistTolerance", old, LodDistTolerance);
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
        Float old = getOptimizeLodReduction1();
        optimize.put("LodReduction1", LodReduction1);
        fire("LodReduction1", old, LodReduction1);
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
        Float old = getOptimizeLodReduction2();
        optimize.put("LodReduction2", LodReduction2);
        fire("LodReduction2", old, LodReduction2);
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
        Integer old = getBuoyancyUpdaterate();
        buoyancy.put("updaterate", updaterate);
        fire("updaterate", old, updaterate);
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
        Float old = getBuoyancyDistance();
        buoyancy.put("distance", distance);
        fire("distance", old, distance);
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
        Float old = getBuoyancyFactor();
        buoyancy.put("factor", factor);
        fire("factor", old, factor);
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
     */
    public void setBuoyancyResolution(Float resolution) {
        Float old = getBuoyancyResolution();
        buoyancy.put("resolution", resolution);
        fire("resolution", old, resolution);
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
     */
    public void setBuoyancyDimensions(Vector3f dimensions) {
        Vector3f old = getBuoyancyDimensions();
        buoyancy.put("dimensions", dimensions);
        fire("buoyancyDimensions", old, dimensions);
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
        Vector3f old = getBuoyancyPosition();
        buoyancy.put("position", position);
        fire("buoyancyPosition", old, position);
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
        Vector3f old = getBuoyancyScale();
        buoyancy.put("scale", scale);
        fire("buoyancyScale", old, scale);
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
        Integer old = getBuoyancyType();
        buoyancy.put("type", type);
        fire("buoyancyType", old, type);
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
        Integer old = getDrag_updaterate();
        params.put("drag_updaterate", drag_updaterate);
        fire("drag_updaterate", old, drag_updaterate);
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
        Integer old = getFlow_updaterate();
        params.put("flow_updaterate", flow_updaterate);
        fire("flow_updaterate", old, flow_updaterate);
    }

    /**
     *
     * @return
     */
    public ColorRGBA getModelSelectionColor() {
        return (ColorRGBA) model.get("selectionColor");
    }

    /**
     * @param selectionColor
     */
    public void setModelSelectionColor(ColorRGBA selectionColor) {
        ColorRGBA old = getModelSelectionColor();
        model.put("selectionColor", selectionColor);
        fire("selectionColor", old, selectionColor);
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
        ColorRGBA old = getModelMapColor();
        model.put("mapColor", mapColor);
        fire("mapColor", old, mapColor);
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
        Float old = getModelAlphaDepthScale();
        model.put("alphaDepthScale", alphaDepthScale);
        fire("alphaDepthScale", old, alphaDepthScale);
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
        String old = getModelName();
        model.put("name", name);
        fire("modelName", old, name);
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
        Float old = getModelScale();
        model.put("scale", scale);
        fire("modelScale", old, scale);
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
        String old = getModelFilepath();
        model.put("filepath", filepath);
        fire("filepath", old, filepath);
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
        Float old = getDrag_coefficient_angular();
        params.put("drag_coefficient_angular", drag_coefficient_angular);
        fire("drag_coefficient_angular", old, drag_coefficient_angular);
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
        Float old = getDrag_coefficient_linear();
        params.put("drag_coefficient_linear", drag_coefficient_linear);
        fire("drag_coefficient_linear", old, drag_coefficient_linear);
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
        Vector3f old = getCentroid_center_distance();
        params.put("centroid_center_distance", centroid_center_distance);
        fire("centroid_center_distance", old, centroid_center_distance);
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
        Float old = getDamping_angular();
        params.put("damping_angular", damping_angular);
        fire("damping_angular", old, damping_angular);
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
        Float old = getDamping_linear();
        params.put("damping_linear", damping_linear);
        fire("damping_linear", old, damping_linear);
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
        Vector3f old = getRotation();
        params.put("rotation", rotation);
        fire("rotation", old, rotation);
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
     * Set the rotation by quaternion. Transform passed quaternion to angle axes
     * and calls setRotation.
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
        Vector3f old = getCollisionDimensions();
        collision.put("dimensions", dimensions);
        fire("collisionDimensions", old, dimensions);
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
        Vector3f old = getCollisionPosition();
        collision.put("position", position);
        fire("collisionPosition", old, position);
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
        Integer old = getCollisionType();
        collision.put("type", type);
        fire("collisionType", old, type);
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
     */
    public void setMass(Float mass) {
        Float old = getMass();
        params.put("mass", mass);
        fire("mass", old, mass);
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
        Integer old = getOffCamera_height();
        params.put("offCamera_height", offCamera_height);
        fire("offCamera_height", old, offCamera_height);
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
        Integer old = getOffCamera_width();
        params.put("offCamera_width", offCamera_width);
        fire("offCamera_width", old, offCamera_width);
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
        Boolean old = getDebugDrag();
        debug.put("drag", drag);
        fire("drag", old, drag);
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
        Boolean old = getDebugBounding();
        debug.put("bounding", bounding);
        fire("bounding", old, bounding);
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
        Boolean old = getDebugWireframe();
        debug.put("wireframe", wireframe);
        fire("wireframe", old, wireframe);
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
        Boolean old = getDebugBuoycancy();
        debug.put("buoycancy", buoycancy);
        fire("buoycancy", old, buoycancy);
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
        Boolean old = getDebugBuoycancyVolume();
        debug.put("buoycancyVolume", buoycancyVolume);
        fire("buoycancyVolume", old, buoycancyVolume);
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
     * @param physicalExchanger
     */
    public void setDebugPhysicalExchanger(Boolean physicalExchanger) {
        Boolean old = getDebugPhysicalExchanger();
        debug.put("physicalExchanger", physicalExchanger);
        fire("physicalExchanger", old, physicalExchanger);
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
        Boolean old = getDebugCenters();
        debug.put("centers", centers);
        fire("centers", old, centers);
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
        Boolean old = getDebugVisualizer();
        debug.put("visualizer", visualizer);
        fire("visualizer", old, visualizer);
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
        Boolean old = getDebugCollision();
        debug.put("collision", collision);
        fire("collision", old, collision);
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
        Boolean old = getConnectionEnabled();
        connection.put("enabled", enabled);
        fire("connectionEnabled", old, enabled);
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
        String old = getConnectionLocalip();
        connection.put("localip", localip);
        fire("localip", old, localip);
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
        String old = getConnectionTargetip();
        connection.put("targetip", targetip);
        fire("targetip", old, targetip);
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
        Integer old = getConnectionPort();
        connection.put("port", port);
        fire("port", old, port);
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
        Integer old = getConnectionGlobalQueueSize();
        connection.put("GlobalQueueSize", GlobalQueueSize);
        fire("GlobalQueueSize", old, GlobalQueueSize);
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
        String old = getConnectionType();
        connection.put("type", type);
        fire("connectionType", old, type);
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
        if (hashmapname == null || hashmapname.equals("")) {
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
        if (hashmapname == null || hashmapname.equals("")) {
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
