/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.util.HashMap;
import javax.swing.tree.TreePath;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.gui.tree.UpdateState;
import mars.xml.HashMapAdapter;

/**
 * Holds the major of all settings that are useful for SIMAUV like
 * activating the terrain on start or changing the sky color.
 * @author Thomas Tosik
 */
@XmlRootElement(name="Settings")
@XmlAccessorType(XmlAccessType.NONE)
public class MARS_Settings implements UpdateState{

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
    private HashMap<String,Object> Auto;
    //private HashMap<String,Object> FPS;

    @XmlTransient
    private Initializer initer;

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
        Auto = (HashMap<String,Object>)Misc.get("Auto");
    }
    
    /**
     * 
     * @param target
     * @param hashmapname
     */
    public void updateState(String target, String hashmapname){
        if(target.equals("enabled") && hashmapname.equals("Axis")){
            initer.hideAxis(isSetupAxis());
        }else if(target.equals("enabled") && hashmapname.equals("FPS")){
            initer.hideFPS(isFPS());
        }else if(target.equals("FrameLimit") && hashmapname.equals("Graphics")){
            initer.changeFrameLimit(getFrameLimit());
        }else if(hashmapname.equals("Light")){
            initer.setupLight();
        }else if(target.equals("enabled") && hashmapname.equals("CrossHairs")){
            initer.hideCrossHairs(isSetupCrossHairs());
        }else if(target.equals("enabled") && hashmapname.equals("PlaneWater")){
            initer.hidePlaneWater(isSetupPlaneWater());
        }else if(hashmapname.equals("PlaneWater")){
            initer.setupPlaneWater();
        }else if(target.equals("enabled") && hashmapname.equals("ProjectedWavesWater")){
            initer.hideProjectedWavesWater(isSetupProjectedWavesWater());
        }else if(hashmapname.equals("ProjectedWavesWater")){
            initer.updateProjectedWavesWater();
        }/*else if(target.equals("position") && hashmapname.equals("Terrain")){
            initer.getTerrainNode().setLocalTranslation(getTerrain_position());
        }*/
        else if(hashmapname.equals("Terrain")){
            //initer.updateTerrain();
        }else if(hashmapname.equals("Grass")){
            initer.updateGrass();
        }else if(target.equals("enabled") && hashmapname.equals("Grid")){
            initer.hideGrid(isSetupGrid());
        }else if(hashmapname.equals("Grid")){
            initer.setupGrid();
        }else if(target.equals("speed") && hashmapname.equals("Physics")){
            initer.changeSpeed(getPhysicsSpeed());
        }else if(target.equals("debug") && hashmapname.equals("Physics")){
            initer.showPhysicsDebug(isPhysicsDebug());
        }else if(target.equals("visible") && hashmapname.equals("Pollution")){
            initer.hidePollution(isPollutionVisible());
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
    public int getResolution_Height() {
        return (Integer)Resolution.get("height");
    }

    /**
     *
     * @param height
     */
    public void setResolution_Height(int height) {
        Resolution.put("height", height);
    }

     /**
     *
     * @return
     */
    public int getResolution_Width() {
        return (Integer)Resolution.get("width");
    }

    /**
     *
     * @param width
     */
    public void setResolution_Width(int width) {
        Resolution.put("width", width);
    }

     /**
     *
     * @return
     */
    public int getFrameLimit() {
        return (Integer)Graphics.get("FrameLimit");
    }

    /**
     *
     * @param FrameLimit
     */
    public void setFrameLimit(int FrameLimit) {
        Graphics.put("FrameLimit", FrameLimit);
    }

    /**
     *
     * @return
     */
    public boolean isFPS() {
        return (Boolean)FPS.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setFPS(boolean enabled) {
        FPS.put("enabled", enabled);
    }

    /**
     *
     * @return
     */
    public boolean isPhysicsDebug() {
        return (Boolean)Physics.get("debug");
    }

    /**
     *
     * @param enabled
     */
    public void setPhysicsDebug(boolean enabled) {
        Physics.put("debug", enabled);
    }

    /**
     *
     * @return
     */
    public int getPhysicsFramerate() {
        return (Integer)Physics.get("framerate");
    }

    /**
     *
     * @param framerate 
     */
    public void setPhysicsFramerate(int framerate) {
        Physics.put("framerate", framerate);
    }
    
     /**
     *
     * @return
     */
    public int getPhysicsMaxSubSteps() {
        return (Integer)Physics.get("maxsubsteps");
    }

    /**
     *
     * @param maxsubsteps 
     */
    public void setPhysicsMaxSubSteps(int maxsubsteps) {
        Physics.put("maxsubsteps", maxsubsteps);
    }
    
    /**
     *
     * @return
     */
    public float getPhysicsSpeed() {
        return (Float)Physics.get("speed");
    }

    /**
     *
     * @param speed 
     */
    public void setPhysicsSpeed(float speed) {
        Physics.put("speed", speed);
    }

    /**
     *
     * @return
     */
    public int getFlyCamMoveSpeed() {
        return (Integer)Camera.get("FlyCamMoveSpeed");
    }

    /**
     *
     * @param FlyCamMoveSpeed
     */
    public void setFlyCamMoveSpeed(int FlyCamMoveSpeed) {
        Camera.put("FlyCamMoveSpeed", FlyCamMoveSpeed);
    }
    
    /**
     *
     * @return
     */
    public float getChaseCamZoomSensitivity() {
        return (Float)Camera.get("ChaseCamZoomSensitivity");
    }

    /**
     *
     * @param speed 
     */
    public void setChaseCamZoomSensitivity(float ChaseCamZoomSensitivity) {
        Camera.put("ChaseCamZoomSensitivity", ChaseCamZoomSensitivity);
    }
    
    /**
     *
     * @return
     */
    public String getAutoConfigName() {
        return (String)Auto.get("config");
    }

    /**
     *
     * @param FlyCamMoveSpeed
     */
    public void setAutoConfigName(String config) {
        Auto.put("config", config);
    }
    
    /**
     *
     * @return
     */
    public boolean isAutoEnabled() {
         return (Boolean)Auto.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setAutoEnabled(boolean enabled) {
        Auto.put("enabled", enabled);
    }
    
    /**
     *
     * @return
     */
    public boolean isSetupCrossHairs() {
         return (Boolean)CrossHairs.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setSetupCrossHairs(boolean enabled) {
        CrossHairs.put("enabled", enabled);
    }

    /**
     *
     * @return
     */
    public ColorRGBA getLight_color() {
        return (ColorRGBA)Light.get("color");
    }

    /**
     *
     * @param ambient_color 
     */
    public void setAmbientColor(ColorRGBA ambient_color) {
        Light.put("ambient_color", ambient_color);
    }
    
    /**
     *
     * @return
     */
    public ColorRGBA getAmbientColor() {
        return (ColorRGBA)Light.get("ambient_color");
    }

    /**
     *
     * @param color
     */
    public void setLight_color(ColorRGBA color) {
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
    public Vector3f getLight_direction() {
        return (Vector3f)Light.get("direction");
    }

    /**
     *
     * @param direction
     */
    public void setLight_direction(Vector3f direction) {
        Light.put("direction", direction);
    }
    
    /**
     *
     * @return
     */
    public Boolean isSetupShadow() {
        return (Boolean)Shadow.get("enabled");
    }

    /**
     *
     * @param enabled 
     */
    public void setSetupShadow(boolean enabled) {
        Shadow.put("enabled", enabled);
    }

    /**
     *
     * @return
     */
    public boolean isSetupWavesWater() {
        return (Boolean)WavesWater.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setSetupWavesWater(boolean enabled) {
        WavesWater.put("enabled", enabled);
    }
    
    /**
     *
     * @return
     */
    public boolean isSetupProjectedWavesWater() {
        return (Boolean)ProjectedWavesWater.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setSetupProjectedWavesWater(boolean enabled) {
        ProjectedWavesWater.put("enabled", enabled);
    }
    
    /**
     *
     * @return
     */
    public int getProjectedWavesWaterOctaves() {
        return (Integer)ProjectedWavesWater.get("Octaves");
    }

    /**
     *
     * @param Octaves 
     */
    public void setProjectedWavesWaterOctaves(int Octaves) {
        ProjectedWavesWater.put("Octaves", Octaves);
    }
    
    /**
     *
     * @return
     */
    public float getProjectedWavesWaterScaleybig() {
        return (Float)ProjectedWavesWater.get("Scaleybig");
    }

    /**
     *
     * @param Scaleybig 
     */
    public void setProjectedWavesWaterScaleybig(float Scaleybig) {
        ProjectedWavesWater.put("Scaleybig", Scaleybig);
    }
    
    /**
     *
     * @return
     */
    public float getProjectedWavesWaterScaleysmall() {
        return (Float)ProjectedWavesWater.get("Scaleysmall");
    }

    /**
     *
     * @param Scaleysmall 
     */
    public void setProjectedWavesWaterScaleysmall(float Scaleysmall) {
        ProjectedWavesWater.put("Scaleysmall", Scaleysmall);
    }
    
    /**
     *
     * @return
     */
    public float getProjectedWavesWaterScalexbig() {
        return (Float)ProjectedWavesWater.get("Scalexbig");
    }

    /**
     *
     * @param Scalexbig 
     */
    public void setProjectedWavesWaterScalexbig(float Scalexbig) {
        ProjectedWavesWater.put("Scalexbig", Scalexbig);
    }
    
    /**
     *
     * @return
     */
    public float getProjectedWavesWaterScalexsmall() {
        return (Float)ProjectedWavesWater.get("Scalexsmall");
    }

    /**
     *
     * @param Scalexsmall 
     */
    public void setProjectedWavesWaterScalexsmall(float Scalexsmall) {
        ProjectedWavesWater.put("Scalexsmall", Scalexsmall);
    }
    
        /**
     *
     * @return
     */
    public float getProjectedWavesWaterHeightsmall() {
        return (Float)ProjectedWavesWater.get("Heightsmall");
    }

    /**
     *
     * @param Heightsmall 
     */
    public void setProjectedWavesWaterHeightsmall(float Heightsmall) {
        ProjectedWavesWater.put("Heightsmall", Heightsmall);
    }
    
            /**
     *
     * @return
     */
    public float getProjectedWavesWaterHeightbig() {
        return (Float)ProjectedWavesWater.get("Heightbig");
    }

    /**
     *
     * @param Heightbig 
     */
    public void setProjectedWavesWaterHeightbig(float Heightbig) {
        ProjectedWavesWater.put("Heightbig", Heightbig);
    }
    
                /**
     *
     * @return
     */
    public float getProjectedWavesWaterSpeedbig() {
        return (Float)ProjectedWavesWater.get("Speedbig");
    }

    /**
     *
     * @param Speedbig 
     */
    public void setProjectedWavesWaterSpeedbig(float Speedbig) {
        ProjectedWavesWater.put("Speedbig", Speedbig);
    }
    
                    /**
     *
     * @return
     */
    public float getProjectedWavesWaterSpeedsmall() {
        return (Float)ProjectedWavesWater.get("Speedsmall");
    }

    /**
     *
     * @param Speedsmall 
     */
    public void setProjectedWavesWaterSpeedsmall(float Speedsmall) {
        ProjectedWavesWater.put("Speedsmall", Speedsmall);
    }

    /**
     *
     * @return
     */
    public boolean isSetupLight() {
        return (Boolean)Light.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setSetupLight(boolean enabled) {
        Light.put("enabled", enabled);
    }
    
    /**
     *
     * @return
     */
    public boolean isSetupAmbient() {
        return (Boolean)Light.get("ambient");
    }

    /**
     *
     * @param ambient 
     */
    public void setSetupAmbient(boolean ambient) {
        Light.put("ambient", ambient);
    }

     /**
     *
     * @return
     */
    public int getRAW_Server_port() {
        return (Integer)RAW.get("port");
    }

    /**
     * 
     * @param port
     */
    public void setRAW_Server_port(int port) {
        RAW.put("port", port);
    }
    
    /**
     *
     * @return
     */
    public boolean isRAW_Server_enabled() {
        return (Boolean)RAW.get("enabled");
    }

    /**
     * 
     * @param raw_enabled 
     */
    public void setRAW_Server_enabled(boolean raw_enabled) {
        RAW.put("enabled", raw_enabled);
    }

    /**
     *
     * @return
     */
    public int getRAW_Server_backlog() {
        return (Integer)RAW.get("backlog");
    }

    /**
     *
     * @param backlog
     */
    public void setRAW_Server_backlog(int backlog) {
        RAW.put("backlog", backlog);
    }

    /**
     *
     * @return
     */
    public int getRAW_Server_OutputStreamSize() {
        return (Integer)RAW.get("OutputStreamSize");
    }

    /**
     *
     * @param OutputStreamSize
     */
    public void setRAW_Server_OutputStreamSize(int OutputStreamSize) {
        RAW.put("OutputStreamSize", OutputStreamSize);
    }
    
         /**
     *
     * @return
     */
    public int getROS_Server_port() {
        return (Integer)ROS.get("masterport");
    }

    /**
     * 
     * @param master_port 
     */
    public void setROS_Server_port(int master_port) {
        ROS.put("masterport", master_port);
    }
    
    /**
     *
     * @return
     */
    public String getROS_Master_IP() {
        return (String)ROS.get("masterip");
    }

    /**
     *
     * @param master_ip 
     */
    public void setROS_Master_IP(String master_ip) {
        ROS.put("masterip", master_ip);
    }
    
    /**
     *
     * @return
     */
    public String getROS_Local_IP() {
        return (String)ROS.get("localip");
    }

    /**
     *
     * @param localip 
     */
    public void setROS_Local_IP(String localip) {
        ROS.put("localip", localip);
    }
    
    /**
     *
     * @return
     */
    public int getROS_Gloabl_Queue_Size() {
        return (Integer)ROS.get("GlobalQueueSize");
    }

    /**
     *
     * @param FrameLimit
     */
    public void setROS_Gloabl_Queue_Size(int GlobalQueueSize) {
        ROS.put("GlobalQueueSize", GlobalQueueSize);
    }
    
    /**
     *
     * @return
     */
    public boolean isROS_Server_enabled() {
        return (Boolean)ROS.get("enabled");
    }

    /**
     * 
     * @param enabled 
     */
    public void setROS_Server_enabled(boolean enabled) {
        ROS.put("enabled", enabled);
    }
    
        /**
     *
     * @return
     */
    public boolean isROS_Server_publish() {
        return (Boolean)ROS.get("publish");
    }

    /**
     * 
     * @param enabled 
     */
    public void setROS_Server_publish(boolean publish) {
        ROS.put("publish", publish);
    }

    /**
     *
     * @return
     */
    public boolean isSetupAxis() {
        return (Boolean)Axis.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setSetupAxis(boolean enabled) {
        Axis.put("enabled", enabled);
    }
    
    /**
     *
     * @return
     */
    public boolean isSetupGrid() {
        return (Boolean)Grid.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setSetupGrid(boolean enabled) {
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
    public void setGridLineDistance(float LineDistance) {
        Grid.put("LineDistance", LineDistance);
    }
    
    /**
     *
     * @return
     */
    public Integer getSizeX() {
        return (Integer)Grid.get("SizeX");
    }

    /**
     *
     * @param SizeX 
     */
    public void setSizeX(int SizeX) {
        Grid.put("SizeX", SizeX);
    }
    
    /**
     *
     * @return
     */
    public Integer getSizeY() {
        return (Integer)Grid.get("SizeY");
    }

    /**
     *
     * @param SizeY 
     */
    public void setSizeY(int SizeY) {
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
    public Float getBlurScale() {
        return (Float)DepthOfField.get("BlurScale");
    }

    /**
     *
     * @param BlurScale
     */
    public void setBlurScale(float BlurScale) {
        DepthOfField.put("BlurScale", BlurScale);
    }

    /**
     *
     * @return
     */
    public Float getFocusDistance() {
        return (Float)DepthOfField.get("FocusDistance");
    }

    /**
     *
     * @param FocusDistance
     */
    public void setFocusDistance(float FocusDistance) {
        DepthOfField.put("FocusDistance", FocusDistance);
    }

    /**
     *
     * @return
     */
    public Float getFocusRange() {
        return (Float)DepthOfField.get("FocusRange");
    }

    /**
     *
     * @param FocusRange
     */
    public void setFocusRange(float FocusRange) {
        DepthOfField.put("FocusRange", FocusRange);
    }

    /**
     *
     * @return
     */
    public boolean isSetupDepthOfField() {
        return (Boolean)DepthOfField.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setSetupDepthOfField(boolean enabled) {
        DepthOfField.put("enabled", enabled);
    }

    /**
     *
     * @return
     */
    public boolean isSetupFog() {
        return (Boolean)Fog.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setSetupFog(boolean enabled) {
        Fog.put("enabled", enabled);
    }

    /**
     *
     * @return
     */
    public ColorRGBA getFogcolor() {
        return (ColorRGBA)Fog.get("color");
    }

    /**
     *
     * @param color
     */
    public void setFogcolor(ColorRGBA color) {
        color.a = 1.0f;
        Fog.put("color", color);
    }

    /**
     *
     * @return
     */
    public Float getFogDistance() {
        return (Float)DepthOfField.get("Distance");
    }

    /**
     *
     * @param Distance
     */
    public void setFogDistance(float Distance) {
        DepthOfField.put("Distance", Distance);
    }

    /**
     *
     * @return
     */
    public Float getFogDensity() {
        return (Float)DepthOfField.get("Density");
    }

    /**
     *
     * @param Density
     */
    public void setFogDensity(float Density) {
        DepthOfField.put("Density", Density);
    }

    /**
     *
     * @return
     */
    public boolean isSetupPlaneWater() {
        return (Boolean)PlaneWater.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setSetupPlainWater(boolean enabled) {
        PlaneWater.put("enabled", enabled);
    }

    /**
     *
     * @return
     */
    public String getPlanewaterfilepath() {
        return (String)PlaneWater.get("filepath");
    }

    /**
     *
     * @param filepath
     */
    public void setPlanewaterfilepath(String filepath) {
        PlaneWater.put("filepath", filepath);
    }

    /**
     *
     * @return
     */
    public boolean isSetupSimpleSkyBox() {
        return (Boolean)SimpleSkyBox.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setSetupSimpleSkyBox(boolean enabled) {
        SimpleSkyBox.put("enabled", enabled);
    }

    /**
     *
     * @return
     */
    public ColorRGBA getSimpleskycolor() {
        return (ColorRGBA)SimpleSkyBox.get("color");
    }

    /**
     *
     * @param color
     */
    public void setSimpleskycolor(ColorRGBA color) {
        SimpleSkyBox.put("color", color);
    }

    /**
     *
     * @return
     */
    public boolean isSetupSkyBox() {
        return (Boolean)SkyBox.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setSetupSkyBox(boolean enabled) {
        SkyBox.put("enabled", enabled);
    }
    
    /**
     *
     * @return
     */
    public boolean isSetupSkyDome() {
        return (Boolean)SkyDome.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setSetupSkyDome(boolean enabled) {
        SkyDome.put("enabled", enabled);
    }

    /**
     *
     * @return
     */
    public String getSkyboxfilepath() {
        return (String)SkyBox.get("filepath");
    }

    /**
     *
     * @param filepath
     */
    public void setSkyboxfilepath(String filepath) {
        SkyBox.put("filepath", filepath);
    }
    
    /**
     *
     * @return
     */
    public boolean isSetupGrass() {
        return (Boolean)Grass.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setSetupGrass(boolean enabled) {
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
    public void setGrassFarViewingDistance(float farViewingDistance) {
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
    public void setGrassFadingRange(float fadingRange) {
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
    public void setGrassPagesizeResolution(int pagesizeResolution) {
        Grass.put("pagesizeResolution", pagesizeResolution);
    }

    /**
     *
     * @return
     */
    public boolean isSetupTerrain() {
        return (Boolean)Terrain.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setSetupTerrain(boolean enabled) {
        Terrain.put("enabled", enabled);
    }
    
        /**
     *
     * @return
     */
    public boolean isSetupAdvancedTerrain() {
        return (Boolean)Terrain.get("advanced");
    }

    /**
     *
     * @param advanced 
     */
    public void setSetupAdvancedTerrain(boolean advanced) {
        Terrain.put("advanced", advanced);
    }
    
    /**
     *
     * @return
     */
    public boolean isTerrainLod() {
        return (Boolean)Terrain.get("lod");
    }

    /**
     *
     * @param lod 
     */
    public void setTerrainLod(boolean lod) {
        Terrain.put("lod", lod);
    }

    /**
     *
     * @return
     * @deprecated 
     */
    @Deprecated
    public Float getTileHeigth() {
        return (Float)Terrain.get("tileHeigth");
    }

    /**
     *
     * @param tileHeigth
     * @deprecated 
     */
    @Deprecated
    public void setTileHeigth(float tileHeigth) {
        Terrain.put("tileHeigth", tileHeigth);
    }

    /**
     *
     * @return
     */
    public String getTerrainfilepath_cm() {
        return (String)Terrain.get("filepath_color");
    }

    /**
     *
     * @param filepath_color
     */
    public void setTerrainfilepath_cm(String filepath_color) {
        Terrain.put("filepath_color", filepath_color);
    }

    /**
     *
     * @return
     */
    public String getTerrainfilepath_hm() {
        return (String)Terrain.get("filepath_heightmap");
    }

    /**
     *
     * @param filepath_heightmap
     */
    public void setTerrainfilepath_hm(String filepath_heightmap) {
        Terrain.put("filepath_heightmap", filepath_heightmap);
    }
    
        /**
     *
     * @return
     */
    public String getTerrainfilepath_am() {
        return (String)Terrain.get("filepath_alphamap");
    }

    /**
     *
     * @param filepath_alphamap
     */
    public void setTerrainfilepath_am(String filepath_alphamap) {
        Terrain.put("filepath_alphamap", filepath_alphamap);
    }
    
    /**
     *
     * @return
     */
    public int getTerrainPatchSize() {
        return (Integer)Terrain.get("patchSize");
    }

    /**
     *
     * @param patchSize 
     */
    public void setTerrainPatchSize(int patchSize) {
        Terrain.put("patchSize", patchSize);
    }

    /**
     *
     * @return
     */
    public Vector3f getTerrain_position() {
        return (Vector3f)Terrain.get("position");
    }

    /**
     *
     * @param position
     */
    public void setTerrain_position(Vector3f position) {
        Terrain.put("position", position);
    }
    
    /**
     *
     * @return
     */
    public Vector3f getTerrain_scale() {
        return (Vector3f)Terrain.get("scale");
    }

    /**
     *
     * @param scale 
     */
    public void setTerrain_scale(Vector3f scale) {
        Terrain.put("scale", scale);
    }
    
    /**
     *
     * @return
     */
    public Vector3f getTerrain_rotation() {
        return (Vector3f)Terrain.get("rotation");
    }

    /**
     *
     * @param rotation 
     */
    public void setTerrain_rotation(Vector3f rotation) {
        Terrain.put("rotation", rotation);
    }
    
    /**
     *
     * @return
     */
    public String getFlowfilepath_x() {
        return (String)Flow.get("filepath_flowmap_x");
    }

    /**
     *
     * @param filepath_flowmap_x 
     */
    public void setFlowfilepath_x(String filepath_flowmap_x) {
        Flow.put("filepath_flowmap_x", filepath_flowmap_x);
    }
    
    /**
     *
     * @return
     */
    public String getFlowfilepath_y() {
        return (String)Flow.get("filepath_flowmap_y");
    }

    /**
     *
     * @param filepath_flowmap_y 
     */
    public void setFlowfilepath_y(String filepath_flowmap_y) {
        Flow.put("filepath_flowmap_y", filepath_flowmap_y);
    }
    
    /**
     *
     * @return
     */
    public Float getFlowForceScale() {
        return (Float)Flow.get("flowScale");
    }

    /**
     *
     * @param flowScale 
     */
    public void setFlowForceScale(float flowScale) {
        Flow.put("flowScale", flowScale);
    }
    
    /**
     *
     * @return
     */
    public Boolean isSetupFlow() {
        return (Boolean)Flow.get("enabled");
    }

    /**
     *
     * @param enabled 
     */
    public void setSetupFlow(boolean enabled) {
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
    public String getPollutionFilepath() {
        return (String)Pollution.get("filepath_pollutionmap");
    }

    /**
     *
     * @param filepath_flowmap_x 
     */
    public void setPollutionFilepath(String filepath_pollutionmap) {
        Pollution.put("filepath_pollutionmap", filepath_pollutionmap);
    }
    
    /**
     *
     * @return
     */
    public Float getPollutionScaleFactor() {
        return (Float)Pollution.get("pollutionScale");
    }

    /**
     *
     * @param flowScale 
     */
    public void setPollutionScaleFactor(float pollutionScale) {
        Pollution.put("pollutionScale", pollutionScale);
    }
    
    /**
     *
     * @return
     */
    public Boolean isSetupPollution() {
        return (Boolean)Pollution.get("enabled");
    }

    /**
     *
     * @param enabled 
     */
    public void setSetupPollution(boolean enabled) {
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
     * @param enabled 
     */
    public void setPollutionVisible(boolean visible) {
        Pollution.put("visible", visible);
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
    public boolean isSetupWater() {
        return (Boolean)Water.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setSetupWater(boolean enabled) {
        Water.put("enabled", enabled);
    }

    /**
     *
     * @return
     */
    public boolean isSetupWireFrame() {
        return (Boolean)WireFrame.get("enabled");
    }

    /**
     *
     * @param enabled
     */
    public void setSetupWireFrame(boolean enabled) {
        WireFrame.put("enabled", enabled);
    }

    /**
     *
     * @return
     */
    public ColorRGBA getWireframecolor() {
        return (ColorRGBA)WireFrame.get("color");
    }

    /**
     *
     * @param color
     */
    public void setWireframecolor(ColorRGBA color) {
        WireFrame.put("color", color);
    }
    
    /**
     *
     * @return
     */
    public ColorRGBA getSelectionColor() {
        return (ColorRGBA)Gui.get("selection_color");
    }

    /**
     *
     * @param color
     */
    public void setSelectionColor(ColorRGBA color) {
        Gui.put("selection_color", color);
    }
    
        /**
     *
     * @return
     */
    public ColorRGBA getCollisionColor() {
        return (ColorRGBA)Gui.get("collision_color");
    }

    /**
     *
     * @param color
     */
    public void setCollisionColor(ColorRGBA color) {
        Gui.put("collision_color", color);
    }
    
    /**
     *
     * @return
     */
    public boolean isAmbientSelection() {
        return (Boolean)Gui.get("AmbientSelection");
    }

    /**
     *
     * @param AmbientSelection 
     */
    public void setAmbientSelection(boolean AmbientSelection) {
        Gui.put("AmbientSelection", AmbientSelection);
    }
    
    /**
     *
     * @return
     */
    public boolean isGlowSelection() {
        return (Boolean)Gui.get("GlowSelection");
    }

    /**
     *
     * @param GlowSelection 
     */
    public void setGlowSelection(boolean GlowSelection) {
        Gui.put("GlowSelection", GlowSelection);
    }
    
    /**
     *
     * @return
     */
    public boolean isPopUpAUVName() {
        return (Boolean)Gui.get("PopUpAUVName");
    }

    /**
     *
     * @param GlowSelection 
     */
    public void setPopUpAUVName(boolean PopUpAUVName) {
        Gui.put("PopUpAUVName", PopUpAUVName);
    }
    
    /**
     *
     * @return
     */
    public float getPopUpAUVNameDistance() {
        return (Float)Gui.get("PopUpAUVNameDistance");
    }

    /**
     *
     * @param speed 
     */
    public void setPopUpAUVNameDistance(float PopUpAUVNameDistance) {
        Gui.put("PopUpAUVNameDistance", PopUpAUVNameDistance);
    }
    
    /**
     *
     * @param GlowSelection 
     */
    public void setMouseUpdateFollow(boolean MouseUpdateFollow) {
        Gui.put("MouseUpdateFollow", MouseUpdateFollow);
    }
    
    /**
     *
     * @return
     */
    public boolean isMouseUpdateFollow() {
        return (Boolean)Gui.get("MouseUpdateFollow");
    }

    /**
     *
     * @return
     */
    public boolean isHeadless() {
        return (Boolean)Misc.get("headless");
    }

    /**
     *
     * @param headless 
     */
    public void setHeadless(boolean headless) {
        Misc.put("headless", headless);
    }
    
    @Override
    public String toString(){
        return "MarsSettings";
    }
}
