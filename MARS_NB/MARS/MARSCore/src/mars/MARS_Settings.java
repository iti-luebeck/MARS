/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.tree.TreePath;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.gui.tree.UpdateState;
import mars.xml.HashMapAdapter;
import org.openide.util.NbPreferences;

/**
 * Holds the major of all settings that are useful for SIMAUV like
 * activating the terrain on start or changing the sky color.
 * @author Thomas Tosik
 */
@XmlRootElement(name="Settings")
@XmlAccessorType(XmlAccessType.NONE)
public class MARS_Settings implements UpdateState, PropertyChangeListenerSupport{

    @XmlJavaTypeAdapter(HashMapAdapter.class)
    private HashMap<String,Object> settings;
    private HashMap<String,Object> Graphics;
    private HashMap<String,Object> Gui;
    private HashMap<String,Object> Server;
    private HashMap<String,Object> RAW;
    private HashMap<String,Object> ROS;
    private HashMap<String,Object> Physics;
    private HashMap<String,Object> Resolution;
    private HashMap<String,Object> Axis;
    private HashMap<String,Object> Grid;
    private HashMap<String,Object> FPS;
    private HashMap<String,Object> Fog;
    private HashMap<String,Object> DepthOfField;
    private HashMap<String,Object> WavesWater;
    private HashMap<String,Object> ProjectedWavesWater;
    private HashMap<String,Object> Water;
    private HashMap<String,Object> PlaneWater;
    private HashMap<String,Object> SkyBox;
    private HashMap<String,Object> SkyDome;
    private HashMap<String,Object> SimpleSkyBox;
    private HashMap<String,Object> Terrain;
    private HashMap<String,Object> Flow;
    private HashMap<String,Object> Pollution;
    private HashMap<String,Object> Grass;
    private HashMap<String,Object> Light;
    private HashMap<String,Object> Shadow;
    private HashMap<String,Object> WireFrame;
    private HashMap<String,Object> CrossHairs;
    private HashMap<String,Object> Misc;
    private HashMap<String,Object> Camera;
    private HashMap<String,Object> Record;
    //private HashMap<String,Object> FPS;

    @XmlTransient
    private Initializer initer;
    @XmlTransient
    private List listeners = Collections.synchronizedList(new LinkedList());

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
    private ColorRGBA simpleskycolor = new ColorRGBA(116f/255f, 204f/255f, 254f/255f, 0.0f);
    private ColorRGBA fogcolor = new ColorRGBA(0.9f, 0.0f, 0.9f, 1.0f);
    private float FocusRange = 5f;
    private float FocusDistance = 5f;
    private float BlurScale = 1.5f;
    private ColorRGBA wireframecolor = ColorRGBA.Red;
    private String skyboxfilepath = "skytest.dds";
    private String planewaterfilepath = "water_2.png";
    private String terrainfilepath_hm = "image7.jpg";
    private String terrainfilepath_cm = "image8.jpg";
    
    //@XmlElement(name = "terrain_position")
    //@XmlAttribute
    private Vector3f terrain_position = new Vector3f(-60.0f, -15.0f, -30.0f);
    private ColorRGBA light_color = ColorRGBA.White;
    private Vector3f light_direction = new Vector3f(0f, -1f, 0f);
    //@XmlTransient
    //@XmlElement(name = "PhysicalEnvironment")
    private PhysicalEnvironment physical_environment;
    private int framerate = 60;
    private int FrameLimit = 60;
    //private boolean FPS = true;
    private boolean debug = false;
    private float tileLength = 0.4f;
    private float tileHeigth = 12f;
    
    /**
     * 
     */
    public MARS_Settings(){
        
    }
       
    /**
     * 
     */
    public void initAfterJAXB(){
        Physics = (HashMap<String,Object>)settings.get("Physics");
        Server = (HashMap<String,Object>)settings.get("Server");
        Graphics = (HashMap<String,Object>)settings.get("Graphics");
        Gui = (HashMap<String,Object>)settings.get("Gui");
        Misc = (HashMap<String,Object>)settings.get("Misc");
        RAW = (HashMap<String,Object>)Server.get("RAW");
        ROS = (HashMap<String,Object>)Server.get("ROS");
        Resolution = (HashMap<String,Object>)Graphics.get("Resolution");
        Axis = (HashMap<String,Object>)Graphics.get("Axis");
        Grid = (HashMap<String,Object>)Graphics.get("Grid");
        FPS = (HashMap<String,Object>)Graphics.get("FPS");
        Fog = (HashMap<String,Object>)Graphics.get("Fog");
        DepthOfField = (HashMap<String,Object>)Graphics.get("DepthOfField");
        WavesWater = (HashMap<String,Object>)Graphics.get("WavesWater");
        ProjectedWavesWater = (HashMap<String,Object>)Graphics.get("ProjectedWavesWater");
        Water = (HashMap<String,Object>)Graphics.get("Water");
        PlaneWater = (HashMap<String,Object>)Graphics.get("PlaneWater");
        SkyBox = (HashMap<String,Object>)Graphics.get("SkyBox");        
        SkyDome = (HashMap<String,Object>)Graphics.get("SkyDome");
        SimpleSkyBox = (HashMap<String,Object>)Graphics.get("SimpleSkyBox");
        Terrain = (HashMap<String,Object>)Graphics.get("Terrain");
        Flow = (HashMap<String,Object>)Graphics.get("Flow");
        Pollution = (HashMap<String,Object>)Graphics.get("Pollution");
        Grass = (HashMap<String,Object>)Graphics.get("Grass");
        Light = (HashMap<String,Object>)Graphics.get("Light");
        Shadow = (HashMap<String,Object>)Graphics.get("Shadow");
        WireFrame = (HashMap<String,Object>)Graphics.get("WireFrame");
        CrossHairs = (HashMap<String,Object>)Graphics.get("CrossHairs");
        Camera = (HashMap<String,Object>)Misc.get("Camera");
        Record = (HashMap<String,Object>)Misc.get("Record");
        //initPreferences(Graphics,"Physics", mars.core.GraphicsPanel.class);
        //initPreferences(Graphics,"Server");
        //initPreferences(Graphics,"Graphics",mars.core.GraphicsPanel.class);
        //initPreferences(Graphics,"Gui");
        //initPreferences(Graphics,"Misc");
    }
    
    @Deprecated
    private void initPreferences(HashMap<String,Object> hashmap, String path, Class cla){
        for (Map.Entry<String, Object> entry : hashmap.entrySet()) {
            String string = entry.getKey();
            Object object = entry.getValue();
            if(object instanceof HashMap){
                HashMap hasher = (HashMap)object;
                initPreferences(hasher,path.concat(string),cla);
            }
            else if(object instanceof Boolean){
                NbPreferences.forModule(cla).putBoolean(path.concat(string), (Boolean)object);
            }
        }
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
        PropertyChangeListener[] pcls = (PropertyChangeListener[]) listeners.toArray(new PropertyChangeListener[0]);
        for (int i = 0; i < pcls.length; i++) {
            pcls[i].propertyChange(new PropertyChangeEvent(this, propertyName, old, nue));
        }
    }
    
    /**
     * 
     * @param target
     * @param hashmapname
     */
    public void updateState(String target, String hashmapname){
        if(target.equals("enabled") && hashmapname.equals("Axis")){
            initer.hideAxis(isAxisEnabled());
        }else if(target.equals("enabled") && hashmapname.equals("FPS")){
            initer.hideFPS(isFPSEnabled());
        }else if(target.equals("FrameLimit") && hashmapname.equals("Graphics")){
            initer.changeFrameLimit(getFrameLimit());
        }else if(hashmapname.equals("Light")){
            initer.setupLight();
        }else if(target.equals("enabled") && hashmapname.equals("CrossHairs")){
            initer.hideCrossHairs(isCrossHairsEnabled());
        }else if(target.equals("enabled") && hashmapname.equals("PlaneWater")){
            initer.hidePlaneWater(isPlaneWaterEnabled());
        }else if(hashmapname.equals("PlaneWater")){
            initer.setupPlaneWater();
        }else if(target.equals("enabled") && hashmapname.equals("ProjectedWavesWater")){
            initer.hideProjectedWavesWater(isProjectedWavesWaterEnabled());
        }else if(hashmapname.equals("ProjectedWavesWater")){
            initer.updateProjectedWavesWater();
        }/*else if(target.equals("position") && hashmapname.equals("Terrain")){
            initer.getTerrainNode().setLocalTranslation(getTerrainPosition());
        }*/
        else if(hashmapname.equals("Terrain")){
            //initer.updateTerrain();
        }else if(hashmapname.equals("Grass")){
            initer.updateGrass();
        }else if(target.equals("enabled") && hashmapname.equals("Grid")){
            initer.hideGrid(isGridEnabled());
        }else if(hashmapname.equals("Grid")){
            initer.setupGrid();
        }else if(target.equals("speed") && hashmapname.equals("Physics")){
            initer.changeSpeed(getPhysicsSpeed());
        }else if(target.equals("debug") && hashmapname.equals("Physics")){
            initer.showPhysicsDebug(getPhysicsDebug());
        }else if(target.equals("visible") && hashmapname.equals("Pollution")){
            initer.hidePollution(isPollutionVisible());
        }else if(target.equals("hour") && hashmapname.equals("SkyDome")){
            initer.getSkyControl().getSunAndStars().setHour(getSkyDomeHour());
            initer.resetTimeOfDay(getSkyDomeHour());
        }
        
        
    }
    
    /**
     * 
     * @param path
     */
    public void updateState(TreePath path){
        if(path.getPathComponent(0).equals(this)){//make sure we want to change auv params
            if( path.getParentPath().getLastPathComponent().toString().equals("Settings")){
                updateState(path.getLastPathComponent().toString(),"");
            }else{
                updateState(path.getLastPathComponent().toString(),path.getParentPath().getLastPathComponent().toString());
            }
        }
    }

    /**
     *
     * @param init 
     */
    public void setInit(Initializer init) {
        this.initer = init;
    }

    /**
     *
     * @return
     */
    public HashMap<String,Object> getSettings(){
        return settings;
    }

    /**
     *
     * @return
     */
    public Integer getResolutionHeight() {
        return (Integer)Resolution.get("height");
    }

    /**
     *
     * @param height
     */
    public void setResolutionHeight(Integer height) {
        Resolution.put("height", height);
    }

     /**
     *
     * @return
     */
    public Integer getResolutionWidth() {
        return (Integer)Resolution.get("width");
    }

    /**
     *
     * @param width
     */
    public void setResolutionWidth(Integer width) {
        Resolution.put("width", width);
    }

     /**
     *
     * @return
     */
    public Integer getFrameLimit() {
        return (Integer)Graphics.get("FrameLimit");
    }

    /**
     *
     * @param FrameLimit
     */
    public void setFrameLimit(Integer FrameLimit) {
        Graphics.put("FrameLimit", FrameLimit);
    }

    /**
     *
     * @return
     */
    public Boolean isFPSEnabled() {
        return (Boolean)FPS.get("enabled");
    }
    
    /**
     *
     * @return
     */
    public Boolean getFPSEnabled() {
        return (Boolean)FPS.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setFPSEnabled(Boolean enabled) {
        FPS.put("enabled", enabled);
    }

    /**
     *
     * @return
     */
    public Boolean getPhysicsDebug() {
        return (Boolean)Physics.get("debug");
    }

    /**
     *
     * @param enabled
     */
    public void setPhysicsDebug(Boolean enabled) {
        Physics.put("debug", enabled);
    }

    /**
     *
     * @return
     */
    public Integer getPhysicsFramerate() {
        return (Integer)Physics.get("framerate");
    }

    /**
     *
     * @param framerate 
     */
    public void setPhysicsFramerate(Integer framerate) {
        Physics.put("framerate", framerate);
    }
    
     /**
     *
     * @return
     */
    public Integer getPhysicsMaxsubsteps() {
        return (Integer)Physics.get("maxsubsteps");
    }

    /**
     *
     * @param maxsubsteps 
     */
    public void setPhysicsMaxsubsteps(Integer maxsubsteps) {
        Physics.put("maxsubsteps", maxsubsteps);
    }
    
    /**
     *
     * @return
     */
    public Float getPhysicsSpeed() {
        return (Float)Physics.get("speed");
    }

    /**
     *
     * @param speed 
     */
    public void setPhysicsSpeed(Float speed) {
        Physics.put("speed", speed);
    }

    /**
     *
     * @return
     */
    public Integer getCameraFlyCamMoveSpeed() {
        return (Integer)Camera.get("FlyCamMoveSpeed");
    }

    /**
     *
     * @param FlyCamMoveSpeed
     */
    public void setCameraFlyCamMoveSpeed(Integer FlyCamMoveSpeed) {
        Camera.put("FlyCamMoveSpeed", FlyCamMoveSpeed);
    }
    
    /**
     *
     * @return
     */
    public Vector3f getCameraDefaultPosition() {
        return (Vector3f)Camera.get("DefaultPosition");
    }

    /**
     *
     * @param DefaultPosition 
     */
    public void setCameraDefaultPosition(Vector3f DefaultPosition) {
        Camera.put("DefaultPosition", DefaultPosition);
    }
    
    /**
     *
     * @return
     */
    public Vector3f getCameraDefaultRotation() {
        return (Vector3f)Camera.get("DefaultRotation");
    }

    /**
     *
     * @param DefaultRotation 
     */
    public void setCameraDefaultRotation(Vector3f DefaultRotation) {
        Camera.put("DefaultRotation", DefaultRotation);
    }
    
    /**
     *
     * @return
     */
    public Float getCameraChaseCamZoomSensitivity() {
        return (Float)Camera.get("ChaseCamZoomSensitivity");
    }

    /**
     *
     * @param ChaseCamZoomSensitivity 
     */
    public void setCameraChaseCamZoomSensitivity(Float ChaseCamZoomSensitivity) {
        Camera.put("ChaseCamZoomSensitivity", ChaseCamZoomSensitivity);
    }
    
    /**
     *
     * @return
     */
    public Boolean getRecordEnabled() {
        return (Boolean)Record.get("enabled");
    }

    /**
     *
     * @param enabled 
     */
    public void setRecordEnabled(Boolean enabled) {
        Record.put("enabled", enabled);
    }
    
    /**
     *
     * @return
     */
    public Boolean isCrossHairsEnabled() {
         return (Boolean)CrossHairs.get("enabled");
    }
    
    /**
     *
     * @return
     */
    public Boolean getCrossHairsEnabled() {
         return (Boolean)CrossHairs.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setCrossHairsEnabled(Boolean enabled) {
        CrossHairs.put("enabled", enabled);
    }
    
    
    /**
     *
     * @return
     */
    public Boolean isLightEnabled() {
        return (Boolean)Light.get("enabled");
    }
    
    /**
     *
     * @return
     */
    public Boolean getLightEnabled() {
        return (Boolean)Light.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setLightEnabled(Boolean enabled) {
        Light.put("enabled", enabled);
    }
    
    /**
     *
     * @return
     */
    public Boolean getLightAmbient() {
        return (Boolean)Light.get("ambient");
    }

    /**
     *
     * @param ambient 
     */
    public void setLightAmbient(Boolean ambient) {
        Light.put("ambient", ambient);
    }

    /**
     *
     * @return
     */
    public ColorRGBA getLightColor() {
        return (ColorRGBA)Light.get("color");
    }

    /**
     *
     * @param ambientColor 
     */
    public void setLightAmbientColor(ColorRGBA ambientColor) {
        Light.put("ambientColor", ambientColor);
    }
    
    /**
     *
     * @return
     */
    public ColorRGBA getLightAmbientColor() {
        return (ColorRGBA)Light.get("ambientColor");
    }

    /**
     *
     * @param color
     */
    public void setLightColor(ColorRGBA color) {
        Light.put("color", color);
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
        this.physical_environment = physical_environment;
    }

    /**
     *
     * @return
     */
    public Vector3f getLightDirection() {
        return (Vector3f)Light.get("direction");
    }

    /**
     *
     * @param direction
     */
    public void setLightDirection(Vector3f direction) {
        Light.put("direction", direction);
    }
    
    /**
     *
     * @return
     */
    public Boolean isShadowEnabled() {
        return (Boolean)Shadow.get("enabled");
    }
    
    /**
     *
     * @return
     */
    public Boolean getShadowEnabled() {
        return (Boolean)Shadow.get("enabled");
    }

    /**
     *
     * @param enabled 
     */
    public void setShadowEnabled(Boolean enabled) {
        Shadow.put("enabled", enabled);
    }

    /**
     *
     * @return
     */
    public Boolean isWavesWaterEnabled() {
        return (Boolean)WavesWater.get("enabled");
    }
    
    /**
     *
     * @return
     */
    public Boolean getWavesWaterEnabled() {
        return (Boolean)WavesWater.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setWavesWaterEnabled(Boolean enabled) {
        WavesWater.put("enabled", enabled);
    }
    
    /**
     *
     * @return
     */
    public Boolean isProjectedWavesWaterEnabled() {
        return (Boolean)ProjectedWavesWater.get("enabled");
    }
    
    /**
     *
     * @return
     */
    public Boolean getProjectedWavesWaterEnabled() {
        return (Boolean)ProjectedWavesWater.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setProjectedWavesWaterEnabled(Boolean enabled) {
        ProjectedWavesWater.put("enabled", enabled);
    }
    
    /**
     *
     * @return
     */
    public Integer getProjectedWavesWaterOctaves() {
        return (Integer)ProjectedWavesWater.get("Octaves");
    }

    /**
     *
     * @param Octaves 
     */
    public void setProjectedWavesWaterOctaves(Integer Octaves) {
        ProjectedWavesWater.put("Octaves", Octaves);
    }
    
    /**
     *
     * @return
     */
    public Float getProjectedWavesWaterScaleybig() {
        return (Float)ProjectedWavesWater.get("Scaleybig");
    }

    /**
     *
     * @param Scaleybig 
     */
    public void setProjectedWavesWaterScaleybig(Float Scaleybig) {
        ProjectedWavesWater.put("Scaleybig", Scaleybig);
    }
    
    /**
     *
     * @return
     */
    public Float getProjectedWavesWaterScaleysmall() {
        return (Float)ProjectedWavesWater.get("Scaleysmall");
    }

    /**
     *
     * @param Scaleysmall 
     */
    public void setProjectedWavesWaterScaleysmall(Float Scaleysmall) {
        ProjectedWavesWater.put("Scaleysmall", Scaleysmall);
    }
    
    /**
     *
     * @return
     */
    public Float getProjectedWavesWaterScalexbig() {
        return (Float)ProjectedWavesWater.get("Scalexbig");
    }

    /**
     *
     * @param Scalexbig 
     */
    public void setProjectedWavesWaterScalexbig(Float Scalexbig) {
        ProjectedWavesWater.put("Scalexbig", Scalexbig);
    }
    
    /**
     *
     * @return
     */
    public Float getProjectedWavesWaterScalexsmall() {
        return (Float)ProjectedWavesWater.get("Scalexsmall");
    }

    /**
     *
     * @param Scalexsmall 
     */
    public void setProjectedWavesWaterScalexsmall(Float Scalexsmall) {
        ProjectedWavesWater.put("Scalexsmall", Scalexsmall);
    }
    
        /**
     *
     * @return
     */
    public Float getProjectedWavesWaterHeightsmall() {
        return (Float)ProjectedWavesWater.get("Heightsmall");
    }

    /**
     *
     * @param Heightsmall 
     */
    public void setProjectedWavesWaterHeightsmall(Float Heightsmall) {
        ProjectedWavesWater.put("Heightsmall", Heightsmall);
    }
    
            /**
     *
     * @return
     */
    public Float getProjectedWavesWaterHeightbig() {
        return (Float)ProjectedWavesWater.get("Heightbig");
    }

    /**
     *
     * @param Heightbig 
     */
    public void setProjectedWavesWaterHeightbig(Float Heightbig) {
        ProjectedWavesWater.put("Heightbig", Heightbig);
    }
    
                /**
     *
     * @return
     */
    public Float getProjectedWavesWaterSpeedbig() {
        return (Float)ProjectedWavesWater.get("Speedbig");
    }

    /**
     *
     * @param Speedbig 
     */
    public void setProjectedWavesWaterSpeedbig(Float Speedbig) {
        ProjectedWavesWater.put("Speedbig", Speedbig);
    }
    
                    /**
     *
     * @return
     */
    public Float getProjectedWavesWaterSpeedsmall() {
        return (Float)ProjectedWavesWater.get("Speedsmall");
    }

    /**
     *
     * @param Speedsmall 
     */
    public void setProjectedWavesWaterSpeedsmall(Float Speedsmall) {
        ProjectedWavesWater.put("Speedsmall", Speedsmall);
    }

     /**
     *
     * @return
     */
    public Integer getRAWPort() {
        return (Integer)RAW.get("port");
    }

    /**
     * 
     * @param port
     */
    public void setRAWPort(Integer port) {
        RAW.put("port", port);
    }
    
    /**
     *
     * @return
     */
    public Boolean getRAWEnabled() {
        return (Boolean)RAW.get("enabled");
    }

    /**
     * 
     * @param enabled 
     */
    public void setRAWEnabled(Boolean enabled) {
        RAW.put("enabled", enabled);
    }

    /**
     *
     * @return
     */
    public Integer getRAWBacklog() {
        return (Integer)RAW.get("backlog");
    }

    /**
     *
     * @param backlog
     */
    public void setRAWBacklog(Integer backlog) {
        RAW.put("backlog", backlog);
    }

    /**
     *
     * @return
     */
    public Integer getRAWOutputStreamSize() {
        return (Integer)RAW.get("OutputStreamSize");
    }

    /**
     *
     * @param OutputStreamSize
     */
    public void setRAWOutputStreamSize(Integer OutputStreamSize) {
        RAW.put("OutputStreamSize", OutputStreamSize);
    }
    
         /**
     *
     * @return
     */
    public Integer getROSMasterport() {
        return (Integer)ROS.get("masterport");
    }

    /**
     * 
     * @param master_port 
     */
    public void setROSMasterport(Integer master_port) {
        ROS.put("masterport", master_port);
    }
    
    /**
     *
     * @return
     */
    public String getROSMasterip() {
        return (String)ROS.get("masterip");
    }

    /**
     *
     * @param master_ip 
     */
    public void setROSMasterip(String master_ip) {
        ROS.put("masterip", master_ip);
    }
    
    /**
     *
     * @return
     */
    public String getROSLocalip() {
        return (String)ROS.get("localip");
    }

    /**
     *
     * @param localip 
     */
    public void setROSLocalip(String localip) {
        ROS.put("localip", localip);
    }
    
    /**
     *
     * @return
     */
    public Integer getROSGlobalQueueSize() {
        return (Integer)ROS.get("GlobalQueueSize");
    }

    /**
     *
     * @param GlobalQueueSize 
     */
    public void setROSGlobalQueueSize(Integer GlobalQueueSize) {
        ROS.put("GlobalQueueSize", GlobalQueueSize);
    }
    
    /**
     *
     * @return
     */
    public Boolean getROSEnabled() {
        return (Boolean)ROS.get("enabled");
    }

    /**
     * 
     * @param enabled 
     */
    public void setROSEnabled(Boolean enabled) {
        ROS.put("enabled", enabled);
    }
    
        /**
     *
     * @return
     */
    public Boolean getROSPublish() {
        return (Boolean)ROS.get("publish");
    }

    /**
     * 
     * @param publish 
     */
    public void setROSPublish(Boolean publish) {
        ROS.put("publish", publish);
    }

    /**
     *
     * @return
     */
    public Boolean isAxisEnabled() {
        return (Boolean)Axis.get("enabled");
    }
    
    /**
     *
     * @return
     */
    public Boolean getAxisEnabled() {
        return (Boolean)Axis.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setAxisEnabled(Boolean enabled) {
        Axis.put("enabled", enabled);
    }
    
    /**
     *
     * @return
     */
    public Boolean isGridEnabled() {
        return (Boolean)Grid.get("enabled");
    }
    
    /**
     *
     * @return
     */
    public Boolean getGridEnabled() {
        return (Boolean)Grid.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setGridEnabled(Boolean enabled) {
        Grid.put("enabled", enabled);
    }

    /**
     *
     * @return
     */
    public Float getGridLineDistance() {
        return (Float)Grid.get("LineDistance");
    }

    /**
     *
     * @param LineDistance 
     */
    public void setGridLineDistance(Float LineDistance) {
        Grid.put("LineDistance", LineDistance);
    }
    
    /**
     *
     * @return
     */
    public Integer getGridSizeX() {
        return (Integer)Grid.get("SizeX");
    }

    /**
     *
     * @param SizeX 
     */
    public void setGridSizeX(Integer SizeX) {
        Grid.put("SizeX", SizeX);
    }
    
    /**
     *
     * @return
     */
    public Integer getGridSizeY() {
        return (Integer)Grid.get("SizeY");
    }

    /**
     *
     * @param SizeY 
     */
    public void setGridSizeY(Integer SizeY) {
        Grid.put("SizeY", SizeY);
    }
    
    /**
     *
     * @return
     */
    public ColorRGBA getGridColor() {
        return (ColorRGBA)Grid.get("color");
    }

    /**
     *
     * @param color
     */
    public void setGridColor(ColorRGBA color) {
        Grid.put("color", color);
    }
    
    /**
     *
     * @return
     */
    public Vector3f getGridPosition() {
        return (Vector3f)Grid.get("position");
    }

    /**
     *
     * @param position
     */
    public void setGridPosition(Vector3f position) {
        Grid.put("position", position);
    }
    
        /**
     *
     * @return
     */
    public Vector3f getGridRotation() {
        return (Vector3f)Grid.get("rotation");
    }

    /**
     *
     * @param rotation 
     */
    public void setGridRotation(Vector3f rotation) {
        Grid.put("rotation", rotation);
    }
    
    /**
     *
     * @return
     */
    public Float getDepthOfFieldBlurScale() {
        return (Float)DepthOfField.get("BlurScale");
    }

    /**
     *
     * @param BlurScale
     */
    public void setDepthOfFieldBlurScale(Float BlurScale) {
        DepthOfField.put("BlurScale", BlurScale);
    }

    /**
     *
     * @return
     */
    public Float getDepthOfFieldFocusDistance() {
        return (Float)DepthOfField.get("FocusDistance");
    }

    /**
     *
     * @param FocusDistance
     */
    public void setDepthOfFieldFocusDistance(Float FocusDistance) {
        DepthOfField.put("FocusDistance", FocusDistance);
    }

    /**
     *
     * @return
     */
    public Float getDepthOfFieldFocusRange() {
        return (Float)DepthOfField.get("FocusRange");
    }

    /**
     *
     * @param FocusRange
     */
    public void setDepthOfFieldFocusRange(Float FocusRange) {
        DepthOfField.put("FocusRange", FocusRange);
    }

    /**
     *
     * @return
     */
    public Boolean isDepthOfFieldEnabled() {
        return (Boolean)DepthOfField.get("enabled");
    }
    
    /**
     *
     * @return
     */
    public Boolean getDepthOfFieldEnabled() {
        return (Boolean)DepthOfField.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setDepthOfFieldEnabled(Boolean enabled) {
        DepthOfField.put("enabled", enabled);
    }

    /**
     *
     * @return
     */
    public Boolean isFogEnabled() {
        return (Boolean)Fog.get("enabled");
    }
    
    /**
     *
     * @return
     */
    public Boolean getFogEnabled() {
        return (Boolean)Fog.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setFogEnabled(Boolean enabled) {
        Fog.put("enabled", enabled);
    }

    /**
     *
     * @return
     */
    public ColorRGBA getFogColor() {
        return (ColorRGBA)Fog.get("color");
    }

    /**
     *
     * @param color
     */
    public void setFogColor(ColorRGBA color) {
        color.a = 1.0f;
        Fog.put("color", color);
    }

    /**
     *
     * @return
     */
    public Float getDepthOfFieldDistance() {
        return (Float)DepthOfField.get("Distance");
    }

    /**
     *
     * @param Distance
     */
    public void setDepthOfFieldDistance(Float Distance) {
        DepthOfField.put("Distance", Distance);
    }

    /**
     *
     * @return
     */
    public Float getDepthOfFieldDensity() {
        return (Float)DepthOfField.get("Density");
    }

    /**
     *
     * @param Density
     */
    public void setDepthOfFieldDensity(Float Density) {
        DepthOfField.put("Density", Density);
    }

    /**
     *
     * @return
     */
    public Boolean isPlaneWaterEnabled() {
        return (Boolean)PlaneWater.get("enabled");
    }
    
    /**
     *
     * @return
     */
    public Boolean getPlaneWaterEnabled() {
        return (Boolean)PlaneWater.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setPlaneWaterEnabled(Boolean enabled) {
        PlaneWater.put("enabled", enabled);
    }

    /**
     *
     * @return
     */
    public String getPlanewaterFilepath() {
        return (String)PlaneWater.get("filepath");
    }

    /**
     *
     * @param filepath
     */
    public void setPlanewaterFilepath(String filepath) {
        PlaneWater.put("filepath", filepath);
    }

    /**
     *
     * @return
     */
    public Boolean isSimpleSkyBoxEnabled() {
        return (Boolean)SimpleSkyBox.get("enabled");
    }
    
    /**
     *
     * @return
     */
    public Boolean getSimpleSkyBoxEnabled() {
        return (Boolean)SimpleSkyBox.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setSimpleSkyBoxEnabled(Boolean enabled) {
        SimpleSkyBox.put("enabled", enabled);
    }

    /**
     *
     * @return
     */
    public ColorRGBA getSimpleskyColor() {
        return (ColorRGBA)SimpleSkyBox.get("color");
    }

    /**
     *
     * @param color
     */
    public void setSimpleskyColor(ColorRGBA color) {
        SimpleSkyBox.put("color", color);
    }

    /**
     *
     * @return
     */
    public Boolean isSkyBoxEnabled() {
        return (Boolean)SkyBox.get("enabled");
    }
    
    /**
     *
     * @return
     */
    public Boolean getSkyBoxEnabled() {
        return (Boolean)SkyBox.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setSkyBoxEnabled(Boolean enabled) {
        SkyBox.put("enabled", enabled);
    }
    
    /**
     *
     * @return
     */
    public String getSkyboxFilepath() {
        return (String)SkyBox.get("filepath");
    }

    /**
     *
     * @param filepath
     */
    public void setSkyboxFilepath(String filepath) {
        SkyBox.put("filepath", filepath);
    }
    
    /**
     *
     * @return
     */
    public Boolean isSkyDomeEnabled() {
        return (Boolean)SkyDome.get("enabled");
    }
    
    /**
     *
     * @return
     */
    public Boolean getSkyDomeEnabled() {
        return (Boolean)SkyDome.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setSkyDomeEnabled(Boolean enabled) {
        SkyDome.put("enabled", enabled);
    }
    
    /**
     *
     * @return
     */
    public Boolean isSkyDomeCloudModulation() {
        return (Boolean)SkyDome.get("cloudModulation");
    }
    
    /**
     *
     * @return
     */
    public Boolean getSkyDomeCloudModulation() {
        return (Boolean)SkyDome.get("cloudModulation");
    }

    /**
     *
     * @param cloudModulation 
     */
    public void setSkyDomeCloudModulation(Boolean cloudModulation) {
        SkyDome.put("cloudModulation", cloudModulation);
    }
    
    /**
     *
     * @return
     */
    public Float getSkyDomeSpeed() {
        return (Float)SkyDome.get("speed");
    }

    /**
     *
     * @param speed 
     */
    public void setSkyDomeSpeed(Float speed) {
        SkyDome.put("speed", speed);
    }
    
    /**
     *
     * @return
     */
    public Float getSkyDomeDirection() {
        return (Float)SkyDome.get("direction");
    }

    /**
     *
     * @param direction 
     */
    public void setSkyDomeDirection(Float direction) {
        SkyDome.put("direction", direction);
    }
    
        /**
     *
     * @return
     */
    public Float getSkyDomeCloudiness() {
        return (Float)SkyDome.get("cloudiness");
    }

    /**
     *
     * @param cloudiness 
     */
    public void setSkyDomeCloudiness(Float cloudiness) {
        SkyDome.put("cloudiness", cloudiness);
    }
    
        /**
     *
     * @return
     */
    public Float getSkyDomeCloudRate() {
        return (Float)SkyDome.get("cloudRate");
    }

    /**
     *
     * @param cloudRate 
     */
    public void setSkyDomeCloudRate(Float cloudRate) {
        SkyDome.put("cloudRate", cloudRate);
    }
    
        /**
     *
     * @return
     */
    public Float getSkyDomeHour() {
        return (Float)SkyDome.get("hour");
    }

    /**
     *
     * @param hour 
     */
    public void setSkyDomeHour(Float hour) {
        SkyDome.put("hour", hour);
    }
    
        /**
     *
     * @return
     */
    public Float getSkyDomeObserverLatitude() {
        return (Float)SkyDome.get("observerLatitude");
    }

    /**
     *
     * @param observerLatitude 
     */
    public void setSkyDomeObserverLatitude(Float observerLatitude) {
        SkyDome.put("observerLatitude", observerLatitude);
    }
    
        /**
     *
     * @return
     */
    public Float getSkyDomeLunarDiameter() {
        return (Float)SkyDome.get("lunarDiameter");
    }

    /**
     *
     * @param lunarDiameter 
     */
    public void setSkyDomeLunarDiameter(Float lunarDiameter) {
        SkyDome.put("lunarDiameter", lunarDiameter);
    }
    
        /**
     *
     * @return
     */
    public Float getSkyDomeSolarLongitude() {
        return (Float)SkyDome.get("solarLongitude");
    }

    /**
     *
     * @param solarLongitude 
     */
    public void setSkyDomeSolarLongitude(Float solarLongitude) {
        SkyDome.put("solarLongitude", solarLongitude);
    }
    
        /**
     *
     * @return
     */
    public Integer getSkyDomeLunarPhase() {
        return (Integer)SkyDome.get("lunarPhase");
    }

    /**
     *
     * @param lunarPhase 
     */
    public void setSkyDomeLunarPhase(Integer lunarPhase) {
        SkyDome.put("lunarPhase", lunarPhase);
    }


    
    /**
     *
     * @return
     */
    public Boolean isGrassEnabled() {
        return (Boolean)Grass.get("enabled");
    }
    
    /**
     *
     * @return
     */
    public Boolean getGrassEnabled() {
        return (Boolean)Grass.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setGrassEnabled(Boolean enabled) {
        Grass.put("enabled", enabled);
    }
    
    /**
     *
     * @return
     */
    public Float getGrassFarViewingDistance() {
        return (Float)Grass.get("farViewingDistance");
    }

    /**
     *
     * @param farViewingDistance 
     */
    public void setGrassFarViewingDistance(Float farViewingDistance) {
        Grass.put("farViewingDistance", farViewingDistance);
    }
    
        /**
     *
     * @return
     */
    public Float getGrassFadingRange() {
        return (Float)Grass.get("fadingRange");
    }

    /**
     *
     * @param fadingRange 
     */
    public void setGrassFadingRange(Float fadingRange) {
        Grass.put("fadingRange", fadingRange);
    }
    
    /**
     *
     * @return
     */
    public Integer getGrassPagesizeResolution() {
        return (Integer)Grass.get("pagesizeResolution");
    }

    /**
     *
     * @param pagesizeResolution 
     */
    public void setGrassPagesizeResolution(Integer pagesizeResolution) {
        Grass.put("pagesizeResolution", pagesizeResolution);
    }

    /**
     *
     * @return
     */
    public Boolean isTerrainEnabled() {
        return (Boolean)Terrain.get("enabled");
    }
    
    /**
     *
     * @return
     */
    public Boolean getTerrainEnabled() {
        return (Boolean)Terrain.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setTerrainEnabled(Boolean enabled) {
        Terrain.put("enabled", enabled);
    }
    
    /**
     *
     * @return
     */
    public Boolean isTerrainAdvanced() {
        return (Boolean)Terrain.get("advanced");
    }
    
    /**
     *
     * @return
     */
    public Boolean getTerrainAdvanced() {
        return (Boolean)Terrain.get("advanced");
    }

    /**
     *
     * @param advanced 
     */
    public void setTerrainAdvanced(Boolean advanced) {
        Terrain.put("advanced", advanced);
    }
    
    /**
     *
     * @return
     */
    public Boolean getTerrainLod() {
        return (Boolean)Terrain.get("lod");
    }

    /**
     *
     * @param lod 
     */
    public void setTerrainLod(Boolean lod) {
        Terrain.put("lod", lod);
    }

    /**
     *
     * @return
     * @deprecated 
     */
    @Deprecated
    public Float getTerrainTileHeigth() {
        return (Float)Terrain.get("tileHeigth");
    }

    /**
     *
     * @param tileHeigth
     * @deprecated 
     */
    @Deprecated
    public void setTerrainTileHeigth(Float tileHeigth) {
        Terrain.put("tileHeigth", tileHeigth);
    }

    /**
     *
     * @return
     */
    public String getTerrainColorMap() {
        return (String)Terrain.get("ColorMap");
    }

    /**
     *
     * @param ColorMap 
     */
    public void setTerrainColorMap(String ColorMap) {
        Terrain.put("ColorMap", ColorMap);
    }

    /**
     *
     * @return
     */
    public String getTerrainHeightMap() {
        return (String)Terrain.get("HeightMap");
    }

    /**
     *
     * @param HeightMap 
     */
    public void setTerrainHeightMap(String HeightMap) {
        Terrain.put("HeightMap", HeightMap);
    }
    
        /**
     *
     * @return
     */
    public String getTerrainAlphaMap() {
        return (String)Terrain.get("AlphaMap");
    }

    /**
     *
     * @param AlphaMap 
     */
    public void setTerrainAlphaMap(String AlphaMap) {
        Terrain.put("AlphaMap", AlphaMap);
    }
    
    /**
     *
     * @return
     */
    public Integer getTerrainPatchSize() {
        return (Integer)Terrain.get("patchSize");
    }

    /**
     *
     * @param patchSize 
     */
    public void setTerrainPatchSize(Integer patchSize) {
        Terrain.put("patchSize", patchSize);
    }

    /**
     *
     * @return
     */
    public Vector3f getTerrainPosition() {
        return (Vector3f)Terrain.get("position");
    }

    /**
     *
     * @param position
     */
    public void setTerrainPosition(Vector3f position) {
        Terrain.put("position", position);
    }
    
    /**
     *
     * @return
     */
    public Vector3f getTerrainScale() {
        return (Vector3f)Terrain.get("scale");
    }

    /**
     *
     * @param scale 
     */
    public void setTerrainScale(Vector3f scale) {
        Terrain.put("scale", scale);
    }
    
    /**
     *
     * @return
     */
    public Vector3f getTerrainRotation() {
        return (Vector3f)Terrain.get("rotation");
    }

    /**
     *
     * @param rotation 
     */
    public void setTerrainRotation(Vector3f rotation) {
        Terrain.put("rotation", rotation);
    }
    
    /**
     *
     * @return
     */
    public String getFlowMapX() {
        return (String)Flow.get("MapX");
    }

    /**
     *
     * @param MapX 
     */
    public void setFlowMapX(String MapX) {
        Flow.put("MapX", MapX);
    }
    
    /**
     *
     * @return
     */
    public String getFlowMapY() {
        return (String)Flow.get("MapY");
    }

    /**
     *
     * @param MapY 
     */
    public void setFlowMapY(String MapY) {
        Flow.put("MapY", MapY);
    }
    
    /**
     *
     * @return
     */
    public Float getFlowForceScale() {
        return (Float)Flow.get("forceScale");
    }

    /**
     *
     * @param forceScale 
     */
    public void setFlowForceScale(Float forceScale) {
        Flow.put("forceScale", forceScale);
    }
    
    /**
     *
     * @return
     */
    public Boolean isFlowEnabled() {
        return (Boolean)Flow.get("enabled");
    }

    /**
     *
     * @return
     */
    public Boolean getFlowEnabled() {
        return (Boolean)Flow.get("enabled");
    }

    /**
     *
     * @param enabled 
     */
    public void setFlowEnabled(Boolean enabled) {
        Flow.put("enabled", enabled);
    }
    
           /**
     *
     * @return
     */
    public Vector3f getFlowPosition() {
        return (Vector3f)Flow.get("position");
    }

    /**
     *
     * @param position
     */
    public void setFlowPosition(Vector3f position) {
        Flow.put("position", position);
    }
    
    /**
     *
     * @return
     */
    public Vector3f getFlowScale() {
        return (Vector3f)Flow.get("scale");
    }

    /**
     *
     * @param scale 
     */
    public void setFlowScale(Vector3f scale) {
        Flow.put("scale", scale);
    }
    
    /**
     *
     * @return
     */
    public Vector3f getFlowRotation() {
        return (Vector3f)Flow.get("rotation");
    }

    /**
     *
     * @param rotation 
     */
    public void setFlowRotation(Vector3f rotation) {
        Flow.put("rotation", rotation);
    }
   
    /**
     *
     * @return
     */
    public String getPollutionPollutionMap() {
        return (String)Pollution.get("pollutionMap");
    }

    /**
     *
     * @param pollutionMap 
     */
    public void setPollutionPollutionMap(String pollutionMap) {
        Pollution.put("pollutionMap", pollutionMap);
    }
    
    /**
     *
     * @return
     */
    public Float getPollutionScaleFactor() {
        return (Float)Pollution.get("scaleFactor");
    }

    /**
     *
     * @param scaleFactor 
     */
    public void setPollutionScaleFactor(Float scaleFactor) {
        Pollution.put("scaleFactor", scaleFactor);
    }
    
    /**
     *
     * @return
     */
    public Float getPollutionAlpha() {
        return (Float)Pollution.get("Alpha");
    }

    /**
     *
     * @param Alpha 
     */
    public void setpollutionAlpha(Float Alpha) {
        Pollution.put("Alpha", Alpha);
    }
    
    /**
     *
     * @return
     */
    public Boolean isPollutionEnabled() {
        return (Boolean)Pollution.get("enabled");
    }
    
    /**
     *
     * @return
     */
    public Boolean getPollutionEnabled() {
        return (Boolean)Pollution.get("enabled");
    }

    /**
     *
     * @param enabled 
     */
    public void setPollutionEnabled(Boolean enabled) {
        Pollution.put("enabled", enabled);
    }
    
    /**
     *
     * @return
     */
    public Boolean isPollutionVisible() {
        return (Boolean)Pollution.get("visible");
    }
    
    /**
     *
     * @return
     */
    public Boolean getPollutionVisible() {
        return (Boolean)Pollution.get("visible");
    }

    /**
     *
     * @param visible 
     */
    public void setPollutionVisible(Boolean visible) {
        Pollution.put("visible", visible);
    }
    
     /**
     *
     * @return
     */
    public Boolean isPollutionDetectable() {
        return (Boolean)Pollution.get("detectable");
    }
    
    /**
     *
     * @return
     */
    public Boolean getPollutionDetectable() {
        return (Boolean)Pollution.get("detectable");
    }

    /**
     *
     * @param detectable 
     */
    public void setPollutionDetectable(Boolean detectable) {
        Pollution.put("detectable", detectable);
    }

           /**
     *
     * @return
     */
    public Vector3f getPollutionPosition() {
        return (Vector3f)Pollution.get("position");
    }

    /**
     *
     * @param position
     */
    public void setPollutionPosition(Vector3f position) {
        Pollution.put("position", position);
    }
    
    /**
     *
     * @return
     */
    public Vector3f getPollutionScale() {
        return (Vector3f)Pollution.get("scale");
    }

    /**
     *
     * @param scale 
     */
    public void setPollutionScale(Vector3f scale) {
        Pollution.put("scale", scale);
    }
    
    /**
     *
     * @return
     */
    public Vector3f getPollutionRotation() {
        return (Vector3f)Pollution.get("rotation");
    }

    /**
     *
     * @param rotation 
     */
    public void setPollutionRotation(Vector3f rotation) {
        Pollution.put("rotation", rotation);
    }

    /**
     *
     * @return
     */
    public Boolean isWaterEnabled() {
        return (Boolean)Water.get("enabled");
    }
    
    /**
     *
     * @return
     */
    public Boolean getWaterEnabled() {
        return (Boolean)Water.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setWaterEnabled(Boolean enabled) {
        Water.put("enabled", enabled);
    }

    /**
     *
     * @return
     */
    public Boolean isWireFrameEnabled() {
        return (Boolean)WireFrame.get("enabled");
    }
    
    /**
     *
     * @return
     */
    public Boolean getWireFrameEnabled() {
        return (Boolean)WireFrame.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setWireFrameEnabled(Boolean enabled) {
        WireFrame.put("enabled", enabled);
    }

    /**
     *
     * @return
     */
    public ColorRGBA getWireFrameColor() {
        return (ColorRGBA)WireFrame.get("color");
    }

    /**
     *
     * @param color
     */
    public void setWireFrameColor(ColorRGBA color) {
        WireFrame.put("color", color);
    }
    
    /**
     *
     * @return
     */
    public ColorRGBA getGuiSelectionColor() {
        return (ColorRGBA)Gui.get("selectionColor");
    }

    /**
     *
     * @param selectionColor 
     */
    public void setGuiSelectionColor(ColorRGBA selectionColor) {
        Gui.put("selectionColor", selectionColor);
    }
    
        /**
     *
     * @return
     */
    public ColorRGBA getGuiCollisionColor() {
        return (ColorRGBA)Gui.get("collisionColor");
    }

    /**
     *
     * @param collisionColor 
     */
    public void setGuiCollisionColor(ColorRGBA collisionColor) {
        Gui.put("collisionColor", collisionColor);
    }
    
    /**
     *
     * @return
     */
    public Boolean getGuiAmbientSelection() {
        return (Boolean)Gui.get("AmbientSelection");
    }

    /**
     *
     * @param AmbientSelection 
     */
    public void setGuiAmbientSelection(Boolean AmbientSelection) {
        Gui.put("AmbientSelection", AmbientSelection);
    }
    
    /**
     *
     * @return
     */
    public Boolean getGuiGlowSelection() {
        return (Boolean)Gui.get("GlowSelection");
    }

    /**
     *
     * @param GlowSelection 
     */
    public void setGuiGlowSelection(Boolean GlowSelection) {
        Gui.put("GlowSelection", GlowSelection);
    }
    
    /**
     *
     * @return
     */
    public Boolean getGuiPopUpAUVName() {
        return (Boolean)Gui.get("PopUpAUVName");
    }

    /**
     *
     * @param PopUpAUVName 
     */
    public void setGuiPopUpAUVName(Boolean PopUpAUVName) {
        Gui.put("PopUpAUVName", PopUpAUVName);
    }
    
    /**
     *
     * @return
     */
    public Float getGuiPopUpAUVNameDistance() {
        return (Float)Gui.get("PopUpAUVNameDistance");
    }

    /**
     *
     * @param PopUpAUVNameDistance 
     */
    public void setGuiPopUpAUVNameDistance(Float PopUpAUVNameDistance) {
        Gui.put("PopUpAUVNameDistance", PopUpAUVNameDistance);
    }
    
    /**
     *
     * @param MouseUpdateFollow 
     */
    public void setGuiMouseUpdateFollow(Boolean MouseUpdateFollow) {
        Gui.put("MouseUpdateFollow", MouseUpdateFollow);
    }
    
    /**
     *
     * @return
     */
    public Boolean getGuiMouseUpdateFollow() {
        return (Boolean)Gui.get("MouseUpdateFollow");
    }

    /**
     *
     * @return
     */
    public Boolean getMiscHeadless() {
        return (Boolean)Misc.get("headless");
    }

    /**
     *
     * @param headless 
     */
    public void setMiscHeadless(Boolean headless) {
        Misc.put("headless", headless);
    }
    
    @Override
    public String toString(){
        return "MarsSettings";
    }
}
