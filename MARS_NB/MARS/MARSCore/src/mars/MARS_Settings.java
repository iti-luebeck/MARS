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
package mars;

import mars.misc.PropertyChangeListenerSupport;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.xml.HashMapAdapter;

/**
 * Holds the major of all settings that are useful for SIMAUV like activating
 * the terrain on start or changing the sky color.
 *
 * @author Thomas Tosik
 */
@XmlRootElement(name = "Settings")
@XmlAccessorType(XmlAccessType.NONE)
public class MARS_Settings implements PropertyChangeListenerSupport {
    
    @XmlJavaTypeAdapter(HashMapAdapter.class)
    private HashMap<String, Object> settings;
    private HashMap<String, Object> Graphics;
    private HashMap<String, Object> Gui;
    private HashMap<String, Object> Server;
    private HashMap<String, Object> RAW;
    private HashMap<String, Object> ROS;
    private HashMap<String, Object> Physics;
    private HashMap<String, Object> Resolution;
    private HashMap<String, Object> Axis;
    private HashMap<String, Object> Grid;
    private HashMap<String, Object> FPS;
    private HashMap<String, Object> Fog;
    private HashMap<String, Object> DepthOfField;
    private HashMap<String, Object> WavesWater;
    private HashMap<String, Object> ProjectedWavesWater;
    private HashMap<String, Object> PlaneWater;
    private HashMap<String, Object> SkyBox;
    private HashMap<String, Object> SkyDome;
    private HashMap<String, Object> SimpleSkyBox;
    private HashMap<String, Object> Terrain;
    private HashMap<String, Object> Flow;
    private HashMap<String, Object> Pollution;
    private HashMap<String, Object> Grass;
    private HashMap<String, Object> Light;
    private HashMap<String, Object> Shadow;
    private HashMap<String, Object> WireFrame;
    private HashMap<String, Object> CrossHairs;
    private HashMap<String, Object> Misc;
    private HashMap<String, Object> Camera;
    private HashMap<String, Object> Record;
    private HashMap<String, Object> Logging;

    @XmlTransient
    private final List<PropertyChangeListener> listeners = Collections.synchronizedList(new LinkedList<PropertyChangeListener>());

    private boolean setupAxis = true;
    private boolean setupFog = false;
    private boolean setupDepthOfField = false;
    private boolean setupWavesWater = false;
    private boolean setupWater = true;
    private boolean setupPlaneWater = false;
    private boolean setupSkyBox = true;
    private boolean setupSimpleSkyBox = false;
    private boolean setupCrossHairs = true;
    private int FlyCamMoveSpeed = 10;
    private boolean setupTerrain = true;
    private boolean setupWireFrame = false;
    private boolean setupLight = true;
    private int server_port = 80;
    private int backlog = 10;
    private int OutputStreamSize = 1228100;
    private ColorRGBA simpleskycolor = new ColorRGBA(116f / 255f, 204f / 255f, 254f / 255f, 0.0f);
    private ColorRGBA fogcolor = new ColorRGBA(0.9f, 0.0f, 0.9f, 1.0f);
    private float FocusRange = 5f;
    private float FocusDistance = 5f;
    private float BlurScale = 1.5f;
    private ColorRGBA wireframecolor = ColorRGBA.Red;
    private String skyboxfilepath = "skytest.dds";
    private String planewaterfilepath = "water_2.png";
    private String terrainfilepath_hm = "image7.jpg";
    private String terrainfilepath_cm = "image8.jpg";

    private Vector3f terrain_position = new Vector3f(-60.0f, -15.0f, -30.0f);
    private ColorRGBA light_color = ColorRGBA.White;
    private Vector3f light_direction = new Vector3f(0f, -1f, 0f);
    private PhysicalEnvironment physical_environment;
    private int framerate = 60;
    private int FrameLimit = 60;
    private boolean debug = false;
    private float tileLength = 0.4f;
    private float tileHeigth = 12f;

    /**
     *
     */
    public MARS_Settings() {

    }

    /**
     * Called by JAXB after JAXB loaded the basic stuff.
     */
    @SuppressWarnings("unchecked")
    public void initAfterJAXB() {
        Physics = (HashMap<String, Object>) settings.get("Physics");
        Server = (HashMap<String, Object>) settings.get("Server");
        Graphics = (HashMap<String, Object>) settings.get("Graphics");
        Gui = (HashMap<String, Object>) settings.get("Gui");
        Misc = (HashMap<String, Object>) settings.get("Misc");
        RAW = (HashMap<String, Object>) Server.get("RAW");
        ROS = (HashMap<String, Object>) Server.get("ROS");
        Resolution = (HashMap<String, Object>) Graphics.get("Resolution");
        Axis = (HashMap<String, Object>) Graphics.get("Axis");
        Grid = (HashMap<String, Object>) Graphics.get("Grid");
        FPS = (HashMap<String, Object>) Graphics.get("FPS");
        Fog = (HashMap<String, Object>) Graphics.get("Fog");
        DepthOfField = (HashMap<String, Object>) Graphics.get("DepthOfField");
        WavesWater = (HashMap<String, Object>) Graphics.get("WavesWater");
        ProjectedWavesWater = (HashMap<String, Object>) Graphics.get("ProjectedWavesWater");
        PlaneWater = (HashMap<String, Object>) Graphics.get("PlaneWater");
        SkyBox = (HashMap<String, Object>) Graphics.get("SkyBox");
        SkyDome = (HashMap<String, Object>) Graphics.get("SkyDome");
        SimpleSkyBox = (HashMap<String, Object>) Graphics.get("SimpleSkyBox");
        Terrain = (HashMap<String, Object>) Graphics.get("Terrain");
        Flow = (HashMap<String, Object>) Graphics.get("Flow");
        Pollution = (HashMap<String, Object>) Graphics.get("Pollution");
        Grass = (HashMap<String, Object>) Graphics.get("Grass");
        Light = (HashMap<String, Object>) Graphics.get("Light");
        Shadow = (HashMap<String, Object>) Graphics.get("Shadow");
        WireFrame = (HashMap<String, Object>) Graphics.get("WireFrame");
        CrossHairs = (HashMap<String, Object>) Graphics.get("CrossHairs");
        Camera = (HashMap<String, Object>) Misc.get("Camera");
        Record = (HashMap<String, Object>) Misc.get("Record");
        Logging = (HashMap<String, Object>) Misc.get("Logging");
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
    }
    
    /**
     *
     * @return
     */
    public HashMap<String, Object> getSettings() {
        return settings;
    }

    /**
     *
     * @return
     */
    public Integer getResolutionHeight() {
        return (Integer) Resolution.get("height");
    }

    /**
     *
     * @param height
     */
    public void setResolutionHeight(Integer height) {
        Integer old = getResolutionHeight();
        Resolution.put("height", height);
        fire("ResolutionHeight", old, height);
    }

    /**
     *
     * @return
     */
    public Integer getResolutionWidth() {
        return (Integer) Resolution.get("width");
    }

    /**
     *
     * @param width
     */
    public void setResolutionWidth(Integer width) {
        Integer old = getResolutionWidth();
        Resolution.put("width", width);
        fire("ResolutionWidth", old, width);
    }

    /**
     *
     * @return
     */
    public Integer getFrameLimit() {
        return (Integer) Graphics.get("FrameLimit");
    }

    /**
     *
     * @param FrameLimit
     */
    public void setFrameLimit(Integer FrameLimit) {
        Integer old = getFrameLimit();
        Graphics.put("FrameLimit", FrameLimit);
        fire("FrameLimit", old, FrameLimit);
    }

    /**
     *
     * @return
     */
    public Boolean isFPSEnabled() {
        return (Boolean) FPS.get("enabled");
    }

    /**
     *
     * @return
     */
    public Boolean getFPSEnabled() {
        return (Boolean) FPS.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setFPSEnabled(Boolean enabled) {
        Boolean old = getFPSEnabled();
        FPS.put("enabled", enabled);
        fire("FPSEnabled", old, enabled);
    }

    /**
     *
     * @return
     */
    public Boolean getPhysicsDebug() {
        return (Boolean) Physics.get("debug");
    }

    /**
     *
     * @param enabled
     */
    public void setPhysicsDebug(Boolean enabled) {
        Boolean old = getPhysicsDebug();
        Physics.put("debug", enabled);
        fire("PhysicsDebug", old, enabled);
    }

    /**
     *
     * @return
     */
    public Integer getPhysicsFramerate() {
        return (Integer) Physics.get("framerate");
    }

    /**
     *
     * @param framerate
     */
    public void setPhysicsFramerate(Integer framerate) {
        Integer old = getPhysicsFramerate();
        Physics.put("framerate", framerate);
        fire("PhysicsFramerate", old, framerate);
    }

    /**
     *
     * @return
     */
    public Integer getPhysicsMaxsubsteps() {
        return (Integer) Physics.get("maxsubsteps");
    }

    /**
     *
     * @param maxsubsteps
     */
    public void setPhysicsMaxsubsteps(Integer maxsubsteps) {
        Integer old = getPhysicsMaxsubsteps();
        Physics.put("maxsubsteps", maxsubsteps);
        fire("PhysicsMaxsubsteps", old, maxsubsteps);
    }

    /**
     *
     * @return
     */
    public Float getPhysicsSpeed() {
        return (Float) Physics.get("speed");
    }

    /**
     *
     * @param speed
     */
    public void setPhysicsSpeed(Float speed) {
        Float old = getPhysicsSpeed();
        Physics.put("speed", speed);
        fire("PhysicsSpeed", old, speed);
    }
    
    /**
     *
     * @return
     */
    public Float getPhysicsPoke() {
        return ((Float) Physics.get("poke") != null) ? (Float) Physics.get("poke") : 5.0f;
    }

    /**
     *
     * @param poke
     */
    public void setPhysicsPoke(Float poke) {
        Float old = getPhysicsPoke();
        Physics.put("poke", poke);
        fire("PhysicsPoke", old, poke);
    }

    /**
     *
     * @return
     */
    public Integer getCameraFlyCamMoveSpeed() {
        return (Integer) Camera.get("FlyCamMoveSpeed");
    }

    /**
     *
     * @param FlyCamMoveSpeed
     */
    public void setCameraFlyCamMoveSpeed(Integer FlyCamMoveSpeed) {
        Integer old = getCameraFlyCamMoveSpeed();
        Camera.put("FlyCamMoveSpeed", FlyCamMoveSpeed);
        fire("CameraFlyCamMoveSpeed", old, FlyCamMoveSpeed);
    }

    /**
     *
     * @return
     */
    public Vector3f getCameraDefaultPosition() {
        return (Vector3f) Camera.get("DefaultPosition");
    }

    /**
     *
     * @param DefaultPosition
     */
    public void setCameraDefaultPosition(Vector3f DefaultPosition) {
        Vector3f old = getCameraDefaultPosition();
        Camera.put("DefaultPosition", DefaultPosition);
        fire("CameraDefaultPosition", old, DefaultPosition);
    }

    /**
     *
     * @return
     */
    public Vector3f getCameraDefaultRotation() {
        return (Vector3f) Camera.get("DefaultRotation");
    }

    /**
     *
     * @param DefaultRotation
     */
    public void setCameraDefaultRotation(Vector3f DefaultRotation) {
        Vector3f old = getCameraDefaultRotation();
        Camera.put("DefaultRotation", DefaultRotation);
        fire("CameraDefaultRotation", old, DefaultRotation);
    }

    /**
     *
     * @return
     */
    public Float getCameraChaseCamZoomSensitivity() {
        return (Float) Camera.get("ChaseCamZoomSensitivity");
    }

    /**
     *
     * @param ChaseCamZoomSensitivity
     */
    public void setCameraChaseCamZoomSensitivity(Float ChaseCamZoomSensitivity) {
        Float old = getCameraChaseCamZoomSensitivity();
        Camera.put("ChaseCamZoomSensitivity", ChaseCamZoomSensitivity);
        fire("CameraChaseCamZoomSensitivity", old, ChaseCamZoomSensitivity);
    }

    /**
     *
     * @return
     */
    public Boolean getRecordEnabled() {
        return (Boolean) Record.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setRecordEnabled(Boolean enabled) {
        Boolean old = getRecordEnabled();
        Record.put("enabled", enabled);
        fire("RecordEnabled", old, enabled);
    }
    
    /**
     *
     * @return
     */
    public Boolean getLoggingEnabled() {
        return (Boolean) Logging.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setLoggingEnabled(Boolean enabled) {
        Boolean old = getLoggingEnabled();
        Logging.put("enabled", enabled);
        fire("LoggingEnabled", old, enabled);
    }
    
    /**
     *
     * @return
     */
    public Boolean getLoggingFileWrite() {
        return (Boolean) Logging.get("fileWrite");
    }

    /**
     *
     * @param fileWrite
     */
    public void setLoggingFileWrite(Boolean fileWrite) {
        Boolean old = getLoggingFileWrite();
        Logging.put("fileWrite", fileWrite);
        fire("LoggingFileWrite", old, fileWrite);
    }

    /**
     *
     * @return
     */
    public String getLoggingLevel() {
        return (String) Logging.get("level");
    }

    /**
     *
     * @param level
     */
    public void setLoggingLevel(String level) {
        String old = getLoggingLevel();
        Logging.put("level", level);
        fire("LoggingLevel", old, level);
    }
    
    /**
     *
     * @return
     */
    public Boolean isCrossHairsEnabled() {
        return (Boolean) CrossHairs.get("enabled");
    }

    /**
     *
     * @return
     */
    public Boolean getCrossHairsEnabled() {
        return (Boolean) CrossHairs.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setCrossHairsEnabled(Boolean enabled) {
        Boolean old = getCrossHairsEnabled();
        CrossHairs.put("enabled", enabled);
        fire("CrossHairsEnabled", old, enabled);
    }

    /**
     *
     * @return
     */
    public Boolean isLightEnabled() {
        return (Boolean) Light.get("enabled");
    }

    /**
     *
     * @return
     */
    public Boolean getLightEnabled() {
        return (Boolean) Light.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setLightEnabled(Boolean enabled) {
        Boolean old = getLightEnabled();
        Light.put("enabled", enabled);
        fire("LightEnabled", old, enabled);
    }

    /**
     *
     * @return
     */
    public Boolean getLightAmbient() {
        return (Boolean) Light.get("ambient");
    }

    /**
     *
     * @param ambient
     */
    public void setLightAmbient(Boolean ambient) {
        Boolean old = getLightAmbient();
        Light.put("ambient", ambient);
        fire("LightAmbient", old, ambient);
    }

    /**
     *
     * @return
     */
    public ColorRGBA getLightColor() {
        return (ColorRGBA) Light.get("color");
    }

    /**
     *
     * @param ambientColor
     */
    public void setLightAmbientColor(ColorRGBA ambientColor) {
        ColorRGBA old = getLightAmbientColor();
        Light.put("ambientColor", ambientColor);
        fire("LightAmbientColor", old, ambientColor);
    }

    /**
     *
     * @return
     */
    public ColorRGBA getLightAmbientColor() {
        return (ColorRGBA) Light.get("ambientColor");
    }

    /**
     *
     * @param color
     */
    public void setLightColor(ColorRGBA color) {
        ColorRGBA old = getLightColor();
        Light.put("color", color);
        fire("LightColor", old, color);
    }

    /**
     *
     * @return
     */
    @XmlTransient
    public PhysicalEnvironment getPhysical_environment() {
        return physical_environment;
    }

    /**
     *
     * @param physical_environment
     */
    public void setPhysical_environment(PhysicalEnvironment physical_environment) {
        PhysicalEnvironment old = getPhysical_environment();
        this.physical_environment = physical_environment;
        fire("Physical_environment", old, physical_environment);
    }

    /**
     *
     * @return
     */
    public Vector3f getLightDirection() {
        return (Vector3f) Light.get("direction");
    }

    /**
     *
     * @param direction
     */
    public void setLightDirection(Vector3f direction) {
        Vector3f old = getLightDirection();
        Light.put("direction", direction);
        fire("LightDirection", old, direction);
    }

    /**
     *
     * @return
     */
    public Boolean isShadowEnabled() {
        return (Boolean) Shadow.get("enabled");
    }

    /**
     *
     * @return
     */
    public Boolean getShadowEnabled() {
        return (Boolean) Shadow.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setShadowEnabled(Boolean enabled) {
        Boolean old = getShadowEnabled();
        Shadow.put("enabled", enabled);
        fire("ShadowEnabled", old, enabled);
    }

    /**
     *
     * @return
     */
    public Boolean isWavesWaterEnabled() {
        return (Boolean) WavesWater.get("enabled");
    }

    /**
     *
     * @return
     */
    public Boolean getWavesWaterEnabled() {
        return (Boolean) WavesWater.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setWavesWaterEnabled(Boolean enabled) {
        Boolean old = getWavesWaterEnabled();
        WavesWater.put("enabled", enabled);
        fire("WavesWaterEnabled", old, enabled);
    }

    /**
     *
     * @return
     */
    public Boolean isProjectedWavesWaterEnabled() {
        return (Boolean) ProjectedWavesWater.get("enabled");
    }

    /**
     *
     * @return
     */
    public Boolean getProjectedWavesWaterEnabled() {
        return (Boolean) ProjectedWavesWater.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setProjectedWavesWaterEnabled(Boolean enabled) {
        Boolean old = getProjectedWavesWaterEnabled();
        ProjectedWavesWater.put("enabled", enabled);
        fire("ProjectedWavesWaterEnabled", old, enabled);
    }

    /**
     *
     * @return
     */
    public Integer getProjectedWavesWaterOctaves() {
        return (Integer) ProjectedWavesWater.get("Octaves");
    }

    /**
     *
     * @param Octaves
     */
    public void setProjectedWavesWaterOctaves(Integer Octaves) {
        Integer old = getProjectedWavesWaterOctaves();
        ProjectedWavesWater.put("Octaves", Octaves);
        fire("ProjectedWavesWaterOctaves", old, Octaves);
    }

    /**
     *
     * @return
     */
    public Float getProjectedWavesWaterScaleybig() {
        return (Float) ProjectedWavesWater.get("Scaleybig");
    }

    /**
     *
     * @param Scaleybig
     */
    public void setProjectedWavesWaterScaleybig(Float Scaleybig) {
        Float old = getProjectedWavesWaterScaleybig();
        ProjectedWavesWater.put("Scaleybig", Scaleybig);
        fire("ProjectedWavesWaterScaleybig", old, Scaleybig);
    }

    /**
     *
     * @return
     */
    public Float getProjectedWavesWaterScaleysmall() {
        return (Float) ProjectedWavesWater.get("Scaleysmall");
    }

    /**
     *
     * @param Scaleysmall
     */
    public void setProjectedWavesWaterScaleysmall(Float Scaleysmall) {
        Float old = getProjectedWavesWaterScaleysmall();
        ProjectedWavesWater.put("Scaleysmall", Scaleysmall);
        fire("ProjectedWavesWaterScaleysmall", old, Scaleysmall);
    }

    /**
     *
     * @return
     */
    public Float getProjectedWavesWaterScalexbig() {
        return (Float) ProjectedWavesWater.get("Scalexbig");
    }

    /**
     *
     * @param Scalexbig
     */
    public void setProjectedWavesWaterScalexbig(Float Scalexbig) {
        Float old = getProjectedWavesWaterScalexbig();
        ProjectedWavesWater.put("Scalexbig", Scalexbig);
        fire("ProjectedWavesWaterScalexbig", old, Scalexbig);
    }

    /**
     *
     * @return
     */
    public Float getProjectedWavesWaterScalexsmall() {
        return (Float) ProjectedWavesWater.get("Scalexsmall");
    }

    /**
     *
     * @param Scalexsmall
     */
    public void setProjectedWavesWaterScalexsmall(Float Scalexsmall) {
        Float old = getProjectedWavesWaterScalexsmall();
        ProjectedWavesWater.put("Scalexsmall", Scalexsmall);
        fire("ProjectedWavesWaterScalexsmall", old, Scalexsmall);
    }

    /**
     *
     * @return
     */
    public Float getProjectedWavesWaterHeightsmall() {
        return (Float) ProjectedWavesWater.get("Heightsmall");
    }

    /**
     *
     * @param Heightsmall
     */
    public void setProjectedWavesWaterHeightsmall(Float Heightsmall) {
        Float old = getProjectedWavesWaterHeightsmall();
        ProjectedWavesWater.put("Heightsmall", Heightsmall);
        fire("ProjectedWavesWaterHeightsmall", old, Heightsmall);
    }

    /**
     *
     * @return
     */
    public Float getProjectedWavesWaterHeightbig() {
        return (Float) ProjectedWavesWater.get("Heightbig");
    }

    /**
     *
     * @param Heightbig
     */
    public void setProjectedWavesWaterHeightbig(Float Heightbig) {
        Float old = getProjectedWavesWaterHeightbig();
        ProjectedWavesWater.put("Heightbig", Heightbig);
        fire("ProjectedWavesWaterHeightbig", old, Heightbig);
    }

    /**
     *
     * @return
     */
    public Float getProjectedWavesWaterSpeedbig() {
        return (Float) ProjectedWavesWater.get("Speedbig");
    }

    /**
     *
     * @param Speedbig
     */
    public void setProjectedWavesWaterSpeedbig(Float Speedbig) {
        Float old = getProjectedWavesWaterSpeedbig();
        ProjectedWavesWater.put("Speedbig", Speedbig);
        fire("ProjectedWavesWaterSpeedbig", old, Speedbig);
    }

    /**
     *
     * @return
     */
    public Float getProjectedWavesWaterSpeedsmall() {
        return (Float) ProjectedWavesWater.get("Speedsmall");
    }

    /**
     *
     * @param Speedsmall
     */
    public void setProjectedWavesWaterSpeedsmall(Float Speedsmall) {
        Float old = getProjectedWavesWaterSpeedsmall();
        ProjectedWavesWater.put("Speedsmall", Speedsmall);
        fire("ProjectedWavesWaterSpeedsmall", old, Speedsmall);
    }

    /**
     *
     * @return
     */
    public Integer getRAWPort() {
        return (Integer) RAW.get("port");
    }

    /**
     *
     * @param port
     */
    public void setRAWPort(Integer port) {
        Integer old = getRAWPort();
        RAW.put("port", port);
        fire("RAWPort", old, port);
    }

    /**
     *
     * @return
     */
    public Boolean getRAWEnabled() {
        return (Boolean) RAW.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setRAWEnabled(Boolean enabled) {
        Boolean old = getRAWEnabled();
        RAW.put("enabled", enabled);
        fire("RAWEnabled", old, enabled);
    }

    /**
     *
     * @return
     */
    public Integer getRAWBacklog() {
        return (Integer) RAW.get("backlog");
    }

    /**
     *
     * @param backlog
     */
    public void setRAWBacklog(Integer backlog) {
        Integer old = getRAWBacklog();
        RAW.put("backlog", backlog);
        fire("RAWBacklog", old, backlog);
    }

    /**
     *
     * @return
     */
    public Integer getRAWOutputStreamSize() {
        return (Integer) RAW.get("OutputStreamSize");
    }

    /**
     *
     * @param OutputStreamSize
     */
    public void setRAWOutputStreamSize(Integer OutputStreamSize) {
        Integer old = getRAWOutputStreamSize();
        RAW.put("OutputStreamSize", OutputStreamSize);
        fire("RAWOutputStreamSize", old, OutputStreamSize);
    }

    /**
     *
     * @return
     */
    public Integer getROSMasterport() {
        return (Integer) ROS.get("masterport");
    }

    /**
     *
     * @param master_port
     */
    public void setROSMasterport(Integer master_port) {
        Integer old = getROSMasterport();
        ROS.put("masterport", master_port);
        fire("ROSMasterport", old, master_port);
    }

    /**
     *
     * @return
     */
    public String getROSMasterip() {
        return (String) ROS.get("masterip");
    }

    /**
     *
     * @param master_ip
     */
    public void setROSMasterip(String master_ip) {
        String old = getROSMasterip();
        ROS.put("masterip", master_ip);
        fire("ROSMasterip", old, master_ip);
    }

    /**
     *
     * @return
     */
    public String getROSLocalip() {
        return (String) ROS.get("localip");
    }

    /**
     *
     * @param localip
     */
    public void setROSLocalip(String localip) {
        String old = getROSLocalip();
        ROS.put("localip", localip);
        fire("ROSLocalip", old, localip);
    }

    /**
     *
     * @return
     */
    public Integer getROSGlobalQueueSize() {
        return (Integer) ROS.get("GlobalQueueSize");
    }

    /**
     *
     * @param GlobalQueueSize
     */
    public void setROSGlobalQueueSize(Integer GlobalQueueSize) {
        Integer old = getROSGlobalQueueSize();
        ROS.put("GlobalQueueSize", GlobalQueueSize);
        fire("ROSGlobalQueueSize", old, GlobalQueueSize);
    }

    /**
     *
     * @return
     */
    public Boolean getROSEnabled() {
        return (Boolean) ROS.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setROSEnabled(Boolean enabled) {
        Boolean old = getROSEnabled();
        ROS.put("enabled", enabled);
        fire("ROSEnabled", old, enabled);
    }

    /**
     *
     * @return
     */
    public Boolean getROSPublish() {
        return (Boolean) ROS.get("publish");
    }

    /**
     *
     * @param publish
     */
    public void setROSPublish(Boolean publish) {
        Boolean old = getROSPublish();
        ROS.put("publish", publish);
        fire("ROSPublish", old, publish);
    }

    /**
     *
     * @return
     */
    public Boolean isAxisEnabled() {
        return (Boolean) Axis.get("enabled");
    }

    /**
     *
     * @return
     */
    public Boolean getAxisEnabled() {
        return (Boolean) Axis.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setAxisEnabled(Boolean enabled) {
        Boolean old = getAxisEnabled();
        Axis.put("enabled", enabled);
        fire("AxisEnabled", old, enabled);
    }

    /**
     *
     * @return
     */
    public Boolean isGridEnabled() {
        return (Boolean) Grid.get("enabled");
    }

    /**
     *
     * @return
     */
    public Boolean getGridEnabled() {
        return (Boolean) Grid.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setGridEnabled(Boolean enabled) {
        Boolean old = getGridEnabled();
        Grid.put("enabled", enabled);
        fire("GridEnabled", old, enabled);
    }

    /**
     *
     * @return
     */
    public Float getGridLineDistance() {
        return (Float) Grid.get("LineDistance");
    }

    /**
     *
     * @param LineDistance
     */
    public void setGridLineDistance(Float LineDistance) {
        Float old = getGridLineDistance();
        Grid.put("LineDistance", LineDistance);
        fire("GridLineDistance", old, LineDistance);
    }

    /**
     *
     * @return
     */
    public Integer getGridSizeX() {
        return (Integer) Grid.get("SizeX");
    }

    /**
     *
     * @param SizeX
     */
    public void setGridSizeX(Integer SizeX) {
        Integer old = getGridSizeX();
        Grid.put("SizeX", SizeX);
        fire("GridSizeX", old, SizeX);
    }

    /**
     *
     * @return
     */
    public Integer getGridSizeY() {
        return (Integer) Grid.get("SizeY");
    }

    /**
     *
     * @param SizeY
     */
    public void setGridSizeY(Integer SizeY) {
        Integer old = getGridSizeY();
        Grid.put("SizeY", SizeY);
        fire("GridSizeY", old, SizeY);
    }

    /**
     *
     * @return
     */
    public ColorRGBA getGridColor() {
        return (ColorRGBA) Grid.get("color");
    }

    /**
     *
     * @param color
     */
    public void setGridColor(ColorRGBA color) {
        ColorRGBA old = getGridColor();
        Grid.put("color", color);
        fire("GridColor", old, color);
    }

    /**
     *
     * @return
     */
    public Vector3f getGridPosition() {
        return (Vector3f) Grid.get("position");
    }

    /**
     *
     * @param position
     */
    public void setGridPosition(Vector3f position) {
        Vector3f old = getGridPosition();
        Grid.put("position", position);
        fire("GridPosition", old, position);
    }

    /**
     *
     * @return
     */
    public Vector3f getGridRotation() {
        return (Vector3f) Grid.get("rotation");
    }

    /**
     *
     * @param rotation
     */
    public void setGridRotation(Vector3f rotation) {
        Vector3f old = getGridRotation();
        Grid.put("rotation", rotation);
        fire("GridRotation", old, rotation);
    }

    /**
     *
     * @return
     */
    public Float getDepthOfFieldBlurScale() {
        return (Float) DepthOfField.get("BlurScale");
    }

    /**
     *
     * @param BlurScale
     */
    public void setDepthOfFieldBlurScale(Float BlurScale) {
        Float old = getDepthOfFieldBlurScale();
        DepthOfField.put("BlurScale", BlurScale);
        fire("DepthOfFieldBlurScale", old, BlurScale);
    }

    /**
     *
     * @return
     */
    public Float getDepthOfFieldFocusDistance() {
        return (Float) DepthOfField.get("FocusDistance");
    }

    /**
     *
     * @param FocusDistance
     */
    public void setDepthOfFieldFocusDistance(Float FocusDistance) {
        Float old = getDepthOfFieldFocusDistance();
        DepthOfField.put("FocusDistance", FocusDistance);
        fire("DepthOfFieldFocusDistance", old, FocusDistance);
    }

    /**
     *
     * @return
     */
    public Float getDepthOfFieldFocusRange() {
        return (Float) DepthOfField.get("FocusRange");
    }

    /**
     *
     * @param FocusRange
     */
    public void setDepthOfFieldFocusRange(Float FocusRange) {
        Float old = getDepthOfFieldFocusRange();
        DepthOfField.put("FocusRange", FocusRange);
        fire("DepthOfFieldFocusRange", old, FocusRange);
    }

    /**
     *
     * @return
     */
    public Boolean isDepthOfFieldEnabled() {
        return (Boolean) DepthOfField.get("enabled");
    }

    /**
     *
     * @return
     */
    public Boolean getDepthOfFieldEnabled() {
        return (Boolean) DepthOfField.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setDepthOfFieldEnabled(Boolean enabled) {
        Boolean old = getDepthOfFieldEnabled();
        DepthOfField.put("enabled", enabled);
        fire("DepthOfFieldEnabled", old, enabled);
    }

    /**
     *
     * @return
     */
    public Boolean isFogEnabled() {
        return (Boolean) Fog.get("enabled");
    }

    /**
     *
     * @return
     */
    public Boolean getFogEnabled() {
        return (Boolean) Fog.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setFogEnabled(Boolean enabled) {
        Boolean old = getFogEnabled();
        Fog.put("enabled", enabled);
        fire("FogEnabled", old, enabled);
    }

    /**
     *
     * @return
     */
    public ColorRGBA getFogColor() {
        return (ColorRGBA) Fog.get("color");
    }

    /**
     *
     * @param color
     */
    public void setFogColor(ColorRGBA color) {
        ColorRGBA old = getFogColor();
        color.a = 1.0f;
        Fog.put("color", color);
        fire("FogColor", old, color);
    }

    /**
     *
     * @return
     */
    public Float getDepthOfFieldDistance() {
        return (Float) DepthOfField.get("Distance");
    }

    /**
     *
     * @param Distance
     */
    public void setDepthOfFieldDistance(Float Distance) {
        Float old = getDepthOfFieldDistance();
        DepthOfField.put("Distance", Distance);
        fire("DepthOfFieldDistance", old, Distance);
    }

    /**
     *
     * @return
     */
    public Float getDepthOfFieldDensity() {
        return (Float) DepthOfField.get("Density");
    }

    /**
     *
     * @param Density
     */
    public void setDepthOfFieldDensity(Float Density) {
        Float old = getDepthOfFieldDensity();
        DepthOfField.put("Density", Density);
        fire("DepthOfFieldDensity", old, Density);
    }

    /**
     *
     * @return
     */
    public Boolean isPlaneWaterEnabled() {
        return (Boolean) PlaneWater.get("enabled");
    }

    /**
     *
     * @return
     */
    public Boolean getPlaneWaterEnabled() {
        return (Boolean) PlaneWater.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setPlaneWaterEnabled(Boolean enabled) {
        Boolean old = getPlaneWaterEnabled();
        PlaneWater.put("enabled", enabled);
        fire("PlaneWaterEnabled", old, enabled);
    }

    /**
     *
     * @return
     */
    public String getPlanewaterFilepath() {
        return (String) PlaneWater.get("filepath");
    }

    /**
     *
     * @param filepath
     */
    public void setPlanewaterFilepath(String filepath) {
        String old = getPlanewaterFilepath();
        PlaneWater.put("filepath", filepath);
        fire("PlanewaterFilepath", old, filepath);
    }

    /**
     *
     * @return
     */
    public Boolean isSimpleSkyBoxEnabled() {
        return (Boolean) SimpleSkyBox.get("enabled");
    }

    /**
     *
     * @return
     */
    public Boolean getSimpleSkyBoxEnabled() {
        return (Boolean) SimpleSkyBox.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setSimpleSkyBoxEnabled(Boolean enabled) {
        Boolean old = getSimpleSkyBoxEnabled();
        SimpleSkyBox.put("enabled", enabled);
        fire("SimpleSkyBoxEnabled", old, enabled);
    }

    /**
     *
     * @return
     */
    public ColorRGBA getSimpleskyColor() {
        return (ColorRGBA) SimpleSkyBox.get("color");
    }

    /**
     *
     * @param color
     */
    public void setSimpleskyColor(ColorRGBA color) {
        ColorRGBA old = getSimpleskyColor();
        SimpleSkyBox.put("color", color);
        fire("SimpleskyColor", old, color);
    }

    /**
     *
     * @return
     */
    public Boolean isSkyBoxEnabled() {
        return (Boolean) SkyBox.get("enabled");
    }

    /**
     *
     * @return
     */
    public Boolean getSkyBoxEnabled() {
        return (Boolean) SkyBox.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setSkyBoxEnabled(Boolean enabled) {
        Boolean old = getSkyBoxEnabled();
        SkyBox.put("enabled", enabled);
        fire("SkyBoxEnabled", old, enabled);
    }

    /**
     *
     * @return
     */
    public String getSkyboxFilepath() {
        return (String) SkyBox.get("filepath");
    }

    /**
     *
     * @param filepath
     */
    public void setSkyboxFilepath(String filepath) {
        String old = getSkyboxFilepath();
        SkyBox.put("filepath", filepath);
        fire("SkyboxFilepath", old, filepath);
    }

    /**
     *
     * @return
     */
    public Boolean isSkyDomeEnabled() {
        return (Boolean) SkyDome.get("enabled");
    }

    /**
     *
     * @return
     */
    public Boolean getSkyDomeEnabled() {
        return (Boolean) SkyDome.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setSkyDomeEnabled(Boolean enabled) {
        Boolean old = getSkyBoxEnabled();
        SkyDome.put("enabled", enabled);
        fire("SkyDomeEnabled", old, enabled);
    }

    /**
     *
     * @return
     */
    public Boolean isSkyDomeCloudModulation() {
        return (Boolean) SkyDome.get("cloudModulation");
    }

    /**
     *
     * @return
     */
    public Boolean getSkyDomeCloudModulation() {
        return (Boolean) SkyDome.get("cloudModulation");
    }

    /**
     *
     * @param cloudModulation
     */
    public void setSkyDomeCloudModulation(Boolean cloudModulation) {
        Boolean old = getSkyDomeCloudModulation();
        SkyDome.put("cloudModulation", cloudModulation);
        fire("SkyDomeCloudModulation", old, cloudModulation);
    }

    /**
     *
     * @return
     */
    public Float getSkyDomeSpeed() {
        return (Float) SkyDome.get("speed");
    }

    /**
     *
     * @param speed
     */
    public void setSkyDomeSpeed(Float speed) {
        Float old = getSkyDomeSpeed();
        SkyDome.put("speed", speed);
        fire("SkyDomeSpeed", old, speed);
    }

    /**
     *
     * @return
     */
    public Float getSkyDomeDirection() {
        return (Float) SkyDome.get("direction");
    }

    /**
     *
     * @param direction
     */
    public void setSkyDomeDirection(Float direction) {
        Float old = getSkyDomeDirection();
        SkyDome.put("direction", direction);
        fire("SkyDomeDirection", old, direction);
    }

    /**
     *
     * @return
     */
    public Float getSkyDomeCloudiness() {
        return (Float) SkyDome.get("cloudiness");
    }

    /**
     *
     * @param cloudiness
     */
    public void setSkyDomeCloudiness(Float cloudiness) {
        Float old = getSkyDomeCloudiness();
        SkyDome.put("cloudiness", cloudiness);
        fire("SkyDomeCloudiness", old, cloudiness);
    }

    /**
     *
     * @return
     */
    public Float getSkyDomeCloudRate() {
        return (Float) SkyDome.get("cloudRate");
    }

    /**
     *
     * @param cloudRate
     */
    public void setSkyDomeCloudRate(Float cloudRate) {
        Float old = getSkyDomeCloudRate();
        SkyDome.put("cloudRate", cloudRate);
        fire("SkyDomeCloudRate", old, cloudRate);
    }

    /**
     *
     * @return
     */
    public Float getSkyDomeHour() {
        return (Float) SkyDome.get("hour");
    }

    /**
     *
     * @param hour
     */
    public void setSkyDomeHour(Float hour) {
        Float old = getSkyDomeHour();
        SkyDome.put("hour", hour);
        fire("SkyDomeHour", old, hour);
    }

    /**
     *
     * @return
     */
    public Float getSkyDomeObserverLatitude() {
        return (Float) SkyDome.get("observerLatitude");
    }

    /**
     *
     * @param observerLatitude
     */
    public void setSkyDomeObserverLatitude(Float observerLatitude) {
        Float old = getSkyDomeObserverLatitude();
        SkyDome.put("observerLatitude", observerLatitude);
        fire("SkyDomeObserverLatitude", old, observerLatitude);
    }

    /**
     *
     * @return
     */
    public Float getSkyDomeLunarDiameter() {
        return (Float) SkyDome.get("lunarDiameter");
    }

    /**
     *
     * @param lunarDiameter
     */
    public void setSkyDomeLunarDiameter(Float lunarDiameter) {
        Float old = getSkyDomeLunarDiameter();
        SkyDome.put("lunarDiameter", lunarDiameter);
        fire("SkyDomeLunarDiameter", old, lunarDiameter);
    }

    /**
     *
     * @return
     */
    public Float getSkyDomeSolarLongitude() {
        return (Float) SkyDome.get("solarLongitude");
    }

    /**
     *
     * @param solarLongitude
     */
    public void setSkyDomeSolarLongitude(Float solarLongitude) {
        Float old = getSkyDomeSolarLongitude();
        SkyDome.put("solarLongitude", solarLongitude);
        fire("SkyDomeSolarLongitude", old, solarLongitude);
    }

    /**
     *
     * @return
     */
    public Integer getSkyDomeLunarPhase() {
        return (Integer) SkyDome.get("lunarPhase");
    }

    /**
     *
     * @param lunarPhase
     */
    public void setSkyDomeLunarPhase(Integer lunarPhase) {
        Integer old = getSkyDomeLunarPhase();
        SkyDome.put("lunarPhase", lunarPhase);
        fire("SkyDomeLunarPhase", old, lunarPhase);
    }

    /**
     *
     * @return
     */
    public Boolean isGrassEnabled() {
        return (Boolean) Grass.get("enabled");
    }

    /**
     *
     * @return
     */
    public Boolean getGrassEnabled() {
        return (Boolean) Grass.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setGrassEnabled(Boolean enabled) {
        Boolean old = getGrassEnabled();
        Grass.put("enabled", enabled);
        fire("GrassEnabled", old, enabled);
    }

    /**
     *
     * @return
     */
    public Float getGrassFarViewingDistance() {
        return (Float) Grass.get("farViewingDistance");
    }

    /**
     *
     * @param farViewingDistance
     */
    public void setGrassFarViewingDistance(Float farViewingDistance) {
        Float old = getGrassFarViewingDistance();
        Grass.put("farViewingDistance", farViewingDistance);
        fire("GrassFarViewingDistance", old, farViewingDistance);
    }
    
    /**
     *
     * @return
     */
    public Float getGrassFarViewingDistanceImposter() {
        return (Float) Grass.get("farViewingDistanceImposter");
    }

    /**
     *
     * @param farViewingDistanceImposter
     */
    public void setGrassFarViewingDistanceImposter(Float farViewingDistanceImposter) {
        Float old = getGrassFarViewingDistanceImposter();
        Grass.put("farViewingDistanceImposter", farViewingDistanceImposter);
        fire("GrassFarViewingDistanceImposter", old, farViewingDistanceImposter);
    }
    
    /**
     *
     * @return
     */
    public String getGrassDensityMap() {
        return (String) Grass.get("DensityMap");
    }

    /**
     *
     * @param DensityMap
     */
    public void setGrassDensityMap(String DensityMap) {
        String old = getGrassDensityMap();
        Grass.put("DensityMap", DensityMap);
        fire("GrassDensityMap", old, DensityMap);
    }
    
    /**
     *
     * @return
     */
    public Float getGrassPlantingRandomness() {
        return (Float) Grass.get("plantingRandomness");
    }

    /**
     *
     * @param plantingRandomness
     */
    public void setGrassPlantingRandomness(Float plantingRandomness) {
        Float old = getGrassPlantingRandomness();
        Grass.put("plantingRandomness", plantingRandomness);
        fire("GrassPlantingRandomness", old, plantingRandomness);
    }

    /**
     *
     * @return
     */
    public Integer getGrassPatchSize() {
        return (Integer) Grass.get("patchSize");
    }

    /**
     *
     * @param patchSize
     */
    public void setGrassPatchSize(Integer patchSize) {
        Integer old = getGrassPatchSize();
        Grass.put("patchSize", patchSize);
        fire("GrassPatchSize", old, patchSize);
    }

    /**
     *
     * @return
     */
    public Boolean isTerrainEnabled() {
        return (Boolean) Terrain.get("enabled");
    }

    /**
     *
     * @return
     */
    public Boolean getTerrainEnabled() {
        return (Boolean) Terrain.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setTerrainEnabled(Boolean enabled) {
        Boolean old = getTerrainEnabled();
        Terrain.put("enabled", enabled);
        fire("TerrainEnabled", old, enabled);
    }

    /**
     *
     * @return
     */
    public Boolean getTerrainLod() {
        return (Boolean) Terrain.get("lod");
    }

    /**
     *
     * @param lod
     */
    public void setTerrainLod(Boolean lod) {
        Boolean old = getTerrainLod();
        Terrain.put("lod", lod);
        fire("TerrainLod", old, lod);
    }

    /**
     *
     * @return
     */
    public String getTerrainColorMap() {
        return (String) Terrain.get("ColorMap");
    }

    /**
     *
     * @param ColorMap
     */
    public void setTerrainColorMap(String ColorMap) {
        String old = getTerrainColorMap();
        Terrain.put("ColorMap", ColorMap);
        fire("TerrainColorMap", old, ColorMap);
    }

    /**
     *
     * @return
     */
    public String getTerrainHeightMap() {
        return (String) Terrain.get("HeightMap");
    }

    /**
     *
     * @param HeightMap
     */
    public void setTerrainHeightMap(String HeightMap) {
        String old = getTerrainHeightMap();
        Terrain.put("HeightMap", HeightMap);
        fire("TerrainHeightMap", old, HeightMap);
    }

    /**
     *
     * @return
     */
    public String getTerrainAlphaMap() {
        return (String) Terrain.get("AlphaMap");
    }

    /**
     *
     * @param AlphaMap
     */
    public void setTerrainAlphaMap(String AlphaMap) {
        String old = getTerrainAlphaMap();
        Terrain.put("AlphaMap", AlphaMap);
        fire("TerrainAlphaMap", old, AlphaMap);
    }

    /**
     *
     * @return
     */
    public Integer getTerrainPatchSize() {
        return (Integer) Terrain.get("patchSize");
    }

    /**
     *
     * @param patchSize
     */
    public void setTerrainPatchSize(Integer patchSize) {
        Integer old = getTerrainPatchSize();
        Terrain.put("patchSize", patchSize);
        fire("TerrainPatchSize", old, patchSize);
    }
    
    /**
     *
     * @return
     */
    public Float getTerrainLodMultiplier() {
        return (Float) Terrain.get("LodMultiplier");
    }

    /**
     *
     * @param lod
     */
    public void setTerrainLodMultiplier(Float lod) {
        Float old = getTerrainLodMultiplier();
        Terrain.put("LodMultiplier", lod);
        fire("TerrainLodMultiplier", old, lod);
    }

    /**
     *
     * @return
     */
    public Vector3f getTerrainPosition() {
        return (Vector3f) Terrain.get("position");
    }

    /**
     *
     * @param position
     */
    public void setTerrainPosition(Vector3f position) {
        Vector3f old = getTerrainPosition();
        Terrain.put("position", position);
        fire("TerrainPosition", old, position);
    }

    /**
     *
     * @return
     */
    public Vector3f getTerrainScale() {
        return (Vector3f) Terrain.get("scale");
    }

    /**
     *
     * @param scale
     */
    public void setTerrainScale(Vector3f scale) {
        Vector3f old = getTerrainScale();
        Terrain.put("scale", scale);
        fire("TerrainScale", old, scale);
    }

    /**
     *
     * @return
     */
    public Vector3f getTerrainRotation() {
        return (Vector3f) Terrain.get("rotation");
    }

    /**
     *
     * @param rotation
     */
    public void setTerrainRotation(Vector3f rotation) {
        Vector3f old = getTerrainRotation();
        Terrain.put("rotation", rotation);
        fire("TerrainRotation", old, rotation);
    }

    /**
     *
     * @return
     */
    public String getFlowMapX() {
        return (String) Flow.get("MapX");
    }

    /**
     *
     * @param MapX
     */
    public void setFlowMapX(String MapX) {
        String old = getFlowMapX();
        Flow.put("MapX", MapX);
        fire("FlowMapX", old, MapX);
    }

    /**
     *
     * @return
     */
    public String getFlowMapY() {
        return (String) Flow.get("MapY");
    }

    /**
     *
     * @param MapY
     */
    public void setFlowMapY(String MapY) {
        String old = getFlowMapY();
        Flow.put("MapY", MapY);
        fire("FlowMapY", old, MapY);
    }

    /**
     *
     * @return
     */
    public Float getFlowForceScale() {
        return (Float) Flow.get("forceScale");
    }

    /**
     *
     * @param forceScale
     */
    public void setFlowForceScale(Float forceScale) {
        Float old = getFlowForceScale();
        Flow.put("forceScale", forceScale);
        fire("FlowForceScale", old, forceScale);
    }

    /**
     *
     * @return
     */
    public Boolean isFlowEnabled() {
        return (Boolean) Flow.get("enabled");
    }

    /**
     *
     * @return
     */
    public Boolean getFlowEnabled() {
        return (Boolean) Flow.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setFlowEnabled(Boolean enabled) {
        Boolean old = getFPSEnabled();
        Flow.put("enabled", enabled);
        fire("FlowEnabled", old, enabled);
    }

    /**
     *
     * @return
     */
    public Vector3f getFlowPosition() {
        return (Vector3f) Flow.get("position");
    }

    /**
     *
     * @param position
     */
    public void setFlowPosition(Vector3f position) {
        Vector3f old = getFlowPosition();
        Flow.put("position", position);
        fire("FlowPosition", old, position);
    }

    /**
     *
     * @return
     */
    public Vector3f getFlowScale() {
        return (Vector3f) Flow.get("scale");
    }

    /**
     *
     * @param scale
     */
    public void setFlowScale(Vector3f scale) {
        Vector3f old = getFlowScale();
        Flow.put("scale", scale);
        fire("FlowScale", old, scale);
    }

    /**
     *
     * @return
     */
    public Vector3f getFlowRotation() {
        return (Vector3f) Flow.get("rotation");
    }

    /**
     *
     * @param rotation
     */
    public void setFlowRotation(Vector3f rotation) {
        Vector3f old = getFlowRotation();
        Flow.put("rotation", rotation);
        fire("FlowRotation", old, rotation);
    }

    /**
     *
     * @return
     */
    public String getPollutionPollutionMap() {
        return (String) Pollution.get("pollutionMap");
    }

    /**
     *
     * @param pollutionMap
     */
    public void setPollutionPollutionMap(String pollutionMap) {
        String old = getPollutionPollutionMap();
        Pollution.put("pollutionMap", pollutionMap);
        fire("PollutionPollutionMap", old, pollutionMap);
    }

    /**
     *
     * @return
     */
    public Float getPollutionScaleFactor() {
        return (Float) Pollution.get("scaleFactor");
    }

    /**
     *
     * @param scaleFactor
     */
    public void setPollutionScaleFactor(Float scaleFactor) {
        Float old = getPollutionScaleFactor();
        Pollution.put("scaleFactor", scaleFactor);
        fire("PollutionScaleFactor", old, scaleFactor);
    }

    /**
     *
     * @return
     */
    public Float getPollutionAlpha() {
        return (Float) Pollution.get("Alpha");
    }

    /**
     *
     * @param Alpha
     */
    public void setpollutionAlpha(Float Alpha) {
        Float old = getPollutionAlpha();
        Pollution.put("Alpha", Alpha);
        fire("PollutionAlpha", old, Alpha);
    }

    /**
     *
     * @return
     */
    public Boolean isPollutionEnabled() {
        return (Boolean) Pollution.get("enabled");
    }

    /**
     *
     * @return
     */
    public Boolean getPollutionEnabled() {
        return (Boolean) Pollution.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setPollutionEnabled(Boolean enabled) {
        Boolean old = getPollutionEnabled();
        Pollution.put("enabled", enabled);
        fire("PollutionEnabled", old, enabled);
    }

    /**
     *
     * @return
     */
    public Boolean isPollutionVisible() {
        return (Boolean) Pollution.get("visible");
    }

    /**
     *
     * @return
     */
    public Boolean getPollutionVisible() {
        return (Boolean) Pollution.get("visible");
    }

    /**
     *
     * @param visible
     */
    public void setPollutionVisible(Boolean visible) {
        Boolean old = getPollutionVisible();
        Pollution.put("visible", visible);
        fire("PollutionVisible", old, visible);
    }

    /**
     *
     * @return
     */
    public Boolean isPollutionDetectable() {
        return (Boolean) Pollution.get("detectable");
    }

    /**
     *
     * @return
     */
    public Boolean getPollutionDetectable() {
        return (Boolean) Pollution.get("detectable");
    }

    /**
     *
     * @param detectable
     */
    public void setPollutionDetectable(Boolean detectable) {
        Boolean old = getPollutionDetectable();
        Pollution.put("detectable", detectable);
        fire("PollutionDetectable", old, detectable);
    }

    /**
     *
     * @return
     */
    public Vector3f getPollutionPosition() {
        return (Vector3f) Pollution.get("position");
    }

    /**
     *
     * @param position
     */
    public void setPollutionPosition(Vector3f position) {
        Vector3f old = getPollutionPosition();
        Pollution.put("position", position);
        fire("PollutionPosition", old, position);
    }

    /**
     *
     * @return
     */
    public Vector3f getPollutionScale() {
        return (Vector3f) Pollution.get("scale");
    }

    /**
     *
     * @param scale
     */
    public void setPollutionScale(Vector3f scale) {
        Vector3f old = getPollutionScale();
        Pollution.put("scale", scale);
        fire("PollutionScale", old, scale);
    }

    /**
     *
     * @return
     */
    public Vector3f getPollutionRotation() {
        return (Vector3f) Pollution.get("rotation");
    }

    /**
     *
     * @param rotation
     */
    public void setPollutionRotation(Vector3f rotation) {
        Vector3f old = getPollutionRotation();
        Pollution.put("rotation", rotation);
        fire("PollutionRotation", old, rotation);
    }

    /**
     *
     * @return
     */
    public Boolean isWireFrameEnabled() {
        return (Boolean) WireFrame.get("enabled");
    }

    /**
     *
     * @return
     */
    public Boolean getWireFrameEnabled() {
        return (Boolean) WireFrame.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setWireFrameEnabled(Boolean enabled) {
        Boolean old = getWireFrameEnabled();
        WireFrame.put("enabled", enabled);
        fire("WireFrameEnabled", old, enabled);
    }

    /**
     *
     * @return
     */
    public ColorRGBA getWireFrameColor() {
        return (ColorRGBA) WireFrame.get("color");
    }

    /**
     *
     * @param color
     */
    public void setWireFrameColor(ColorRGBA color) {
        ColorRGBA old = getWireFrameColor();
        WireFrame.put("color", color);
        fire("WireFrameColor", old, color);
    }

    /**
     *
     * @return
     */
    public ColorRGBA getGuiSelectionColor() {
        return (ColorRGBA) Gui.get("selectionColor");
    }

    /**
     *
     * @param selectionColor
     */
    public void setGuiSelectionColor(ColorRGBA selectionColor) {
        ColorRGBA old = getGuiSelectionColor();
        Gui.put("selectionColor", selectionColor);
        fire("GuiSelectionColor", old, selectionColor);
    }

    /**
     *
     * @return
     */
    public ColorRGBA getGuiCollisionColor() {
        return (ColorRGBA) Gui.get("collisionColor");
    }

    /**
     *
     * @param collisionColor
     */
    public void setGuiCollisionColor(ColorRGBA collisionColor) {
        ColorRGBA old = getGuiCollisionColor();
        Gui.put("collisionColor", collisionColor);
        fire("GuiCollisionColor", old, collisionColor);
    }

    /**
     *
     * @return
     */
    public Boolean getGuiAmbientSelection() {
        return (Boolean) Gui.get("AmbientSelection");
    }

    /**
     *
     * @param AmbientSelection
     */
    public void setGuiAmbientSelection(Boolean AmbientSelection) {
        Boolean old = getGuiAmbientSelection();
        Gui.put("AmbientSelection", AmbientSelection);
        fire("GuiAmbientSelection", old, AmbientSelection);
    }

    /**
     *
     * @return
     */
    public Boolean getGuiGlowSelection() {
        return (Boolean) Gui.get("GlowSelection");
    }

    /**
     *
     * @param GlowSelection
     */
    public void setGuiGlowSelection(Boolean GlowSelection) {
        Boolean old = getGuiGlowSelection();
        Gui.put("GlowSelection", GlowSelection);
        fire("GuiGlowSelection", old, GlowSelection);
    }

    /**
     *
     * @return
     */
    public Boolean getGuiPopUpAUVName() {
        return (Boolean) Gui.get("PopUpAUVName");
    }

    /**
     *
     * @param PopUpAUVName
     */
    public void setGuiPopUpAUVName(Boolean PopUpAUVName) {
        Boolean old = getGuiPopUpAUVName();
        Gui.put("PopUpAUVName", PopUpAUVName);
        fire("GuiPopUpAUVName", old, PopUpAUVName);
    }

    /**
     *
     * @return
     */
    public Float getGuiPopUpAUVNameDistance() {
        return (Float) Gui.get("PopUpAUVNameDistance");
    }

    /**
     *
     * @param PopUpAUVNameDistance
     */
    public void setGuiPopUpAUVNameDistance(Float PopUpAUVNameDistance) {
        Float old = getGuiPopUpAUVNameDistance();
        Gui.put("PopUpAUVNameDistance", PopUpAUVNameDistance);
        fire("GuiPopUpAUVNameDistance", old, PopUpAUVNameDistance);
    }

    /**
     *
     * @param MouseUpdateFollow
     */
    public void setGuiMouseUpdateFollow(Boolean MouseUpdateFollow) {
        Boolean old = getGuiMouseUpdateFollow();
        Gui.put("MouseUpdateFollow", MouseUpdateFollow);
        fire("GuiMouseUpdateFollow", old, MouseUpdateFollow);
    }

    /**
     *
     * @return
     */
    public Boolean getGuiMouseUpdateFollow() {
        return (Boolean) Gui.get("MouseUpdateFollow");
    }

    /**
     *
     * @return
     */
    public Boolean getMiscHeadless() {
        return (Boolean) Misc.get("headless");
    }

    /**
     *
     * @param headless
     */
    public void setMiscHeadless(Boolean headless) {
        Boolean old = getMiscHeadless();
        Misc.put("headless", headless);
        fire("MiscHeadless", old, headless);
    }

    @Override
    public String toString() {
        return "MarsSettings";
    }
}
