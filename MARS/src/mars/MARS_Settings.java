/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import java.util.HashMap;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.gui.TextFieldEditor;
import mars.xml.HashMapAdapter;
import mars.xml.MyHashMapEntryTypeHashMap;
import mars.xml.MyHashMapType;
import mars.xml.Vector3fAdapter;
import mars.xml.XMLConfigReaderWriter;

/**
 * Holds the major of all settings that are useful for SIMAUV like
 * activating the terrain on start or changing the sky color.
 * @author Thomas Tosik
 */
@XmlRootElement(name="Settings")
@XmlAccessorType(XmlAccessType.NONE)
public class MARS_Settings implements CellEditorListener{

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
    private HashMap<String,Object> Fog;
    private HashMap<String,Object> DepthOfField;
    private HashMap<String,Object> WavesWater;
    private HashMap<String,Object> Water;
    private HashMap<String,Object> PlaneWater;
    private HashMap<String,Object> SkyBox;
    private HashMap<String,Object> SimpleSkyBox;
    private HashMap<String,Object> Terrain;
    private HashMap<String,Object> Light;
    private HashMap<String,Object> WireFrame;
    private HashMap<String,Object> CrossHairs;
    private HashMap<String,Object> Misc;
    private HashMap<String,Object> Camera;
    //private HashMap<String,Object> FPS;

    @XmlTransient
    private XMLConfigReaderWriter xmll;
    @XmlTransient
    private Initializer init;

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
    private boolean FPS = true;
    private boolean debug = false;
    private float tileLength = 0.4f;
    private float tileHeigth = 12f;
    /**
     *
     * @param xmll 
     */
    public MARS_Settings(XMLConfigReaderWriter xmll){
        settings = new HashMap<String,Object> ();
        Graphics = new HashMap<String,Object> ();
        Gui = new HashMap<String,Object> ();
        Server = new HashMap<String,Object> ();
        RAW = new HashMap<String,Object> ();
        ROS = new HashMap<String,Object> ();
        Physics = new HashMap<String,Object> ();
        Resolution = new HashMap<String,Object> ();
        Axis = new HashMap<String,Object> ();
        Fog = new HashMap<String,Object> ();
        DepthOfField = new HashMap<String,Object> ();
        WavesWater = new HashMap<String,Object> ();
        Water = new HashMap<String,Object> ();
        PlaneWater = new HashMap<String,Object> ();
        SkyBox = new HashMap<String,Object> ();
        SimpleSkyBox = new HashMap<String,Object> ();
        Terrain = new HashMap<String,Object> ();
        Light = new HashMap<String,Object> ();
        WireFrame = new HashMap<String,Object> ();
        CrossHairs = new HashMap<String,Object> ();
        Misc = new HashMap<String,Object> ();
        Camera = new HashMap<String,Object> ();
        //FPS = new HashMap<String,Object> ();
        settings.put("Graphics", Graphics);
        Graphics.put("Resolution", Resolution);
        Graphics.put("FrameLimit", FrameLimit);
        Graphics.put("FPS", FPS);
        Graphics.put("Axis", Axis);
        Graphics.put("Fog", Fog);
        Graphics.put("DepthOfField", DepthOfField);
        Graphics.put("WavesWater", WavesWater);
        Graphics.put("Water", Water);
        Graphics.put("PlaneWater", PlaneWater);
        Graphics.put("SkyBox", SkyBox);
        Graphics.put("SimpleSkyBox", SimpleSkyBox);
        Graphics.put("Terrain", Terrain);
        Graphics.put("Light", Light);
        Graphics.put("WireFrame", WireFrame);
        Graphics.put("CrossHairs", CrossHairs);
        settings.put("Server", Server);
        settings.put("Physics", Physics);
        settings.put("Misc", Misc);
        Misc.put("Camera", Camera);
        Server.put("RAW", RAW);
        Server.put("ROS", ROS);
        settings.put("Gui", Gui);
        this.xmll = xmll;
    }
    
    /**
     * 
     */
    public MARS_Settings(){
        
    }
    
    /**
     * You have to initialize first when you read the data in trough jaxb.
     */
    public void init(){
        Physics = (HashMap<String,Object>)settings.get("Physics");
        Server = (HashMap<String,Object>)settings.get("Server");
        Graphics = (HashMap<String,Object>)settings.get("Graphics");
        Gui = (HashMap<String,Object>)settings.get("Gui");
        Misc = (HashMap<String,Object>)settings.get("Misc");
        RAW = (HashMap<String,Object>)Server.get("RAW");
        ROS = (HashMap<String,Object>)Server.get("ROS");
        Resolution = (HashMap<String,Object>)Graphics.get("Resolution");
        Axis = (HashMap<String,Object>)Graphics.get("Axis");
        Fog = (HashMap<String,Object>)Graphics.get("Fog");
        DepthOfField = (HashMap<String,Object>)Graphics.get("DepthOfField");
        WavesWater = (HashMap<String,Object>)Graphics.get("WavesWater");
        Water = (HashMap<String,Object>)Graphics.get("Water");
        PlaneWater = (HashMap<String,Object>)Graphics.get("PlaneWater");
        SkyBox = (HashMap<String,Object>)Graphics.get("SkyBox");
        SimpleSkyBox = (HashMap<String,Object>)Graphics.get("SimpleSkyBox");
        Terrain = (HashMap<String,Object>)Graphics.get("Terrain");
        Light = (HashMap<String,Object>)Graphics.get("Light");
        WireFrame = (HashMap<String,Object>)Graphics.get("WireFrame");
        CrossHairs = (HashMap<String,Object>)Graphics.get("CrossHairs");
        Camera = (HashMap<String,Object>)Misc.get("Camera");
    }

    public void editingCanceled(ChangeEvent e){
        System.out.println("canceld");
    }

    public void editingStopped(ChangeEvent e){
        Object obj = e.getSource();
        if (obj instanceof TextFieldEditor) {
            TextFieldEditor editor = (TextFieldEditor)obj;
            String settings_tree = editor.getTreepath().getPathComponent(1).toString();//get the settings
            if(settings_tree.equals("Settings")){//check if right auv
                saveValue(editor);
            }
        }
    }

    private void saveValue(TextFieldEditor editor){
        HashMap<String,Object> hashmap = settings;
        String target = editor.getTreepath().getParentPath().getLastPathComponent().toString();
        int pathcount = editor.getTreepath().getPathCount();
        Object[] treepath = editor.getTreepath().getPath();
        if( settings.containsKey(target) && pathcount < 4){//no hasmap, direct save
            Object obj = settings.get(target);
            detectType(obj,editor,target,settings);
        }else{//it's in another hashmap, search deeper
            for (int i = 2; i < pathcount-2; i++) {
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
            xmll.setPathElement(treepath, pathcount, node_obj);
        }else if(obj instanceof Integer){
            hashmap.put(target, (Integer)node_obj);
            updateState(target);
            xmll.setPathElement(treepath, pathcount, node_obj);
        }else if(obj instanceof Boolean){
            hashmap.put(target, (Boolean)node_obj);
            updateState(target);
            xmll.setPathElement(treepath, pathcount, node_obj);
        }else if(obj instanceof String){
            hashmap.put(target, (String)node_obj);
            updateState(target);
            xmll.setPathElement(treepath, pathcount, node_obj);
        }else if(obj instanceof Vector3f){
            hashmap.put(target, (Vector3f)node_obj);
            updateState(target);
            xmll.setPathElement(treepath, pathcount, node_obj);
        }else if(obj instanceof ColorRGBA){
            hashmap.put(target, (ColorRGBA)node_obj);
            updateState(target);
            xmll.setPathElement(treepath, pathcount, node_obj);
        }
    }

    private void updateState(String target){
        if(target.equals("position")){
            RigidBodyControl physics_control = init.getTerrain_physics_control();
            if(physics_control != null ){
                physics_control.setPhysicsLocation(getTerrain_position());
            }
        }
    }

    /**
     *
     * @param init
     */
    public void setInit(Initializer init) {
        this.init = init;
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
        return (Boolean)Graphics.get("FPS");
    }

    /**
     *
     * @param enabled
     */
    public void setFPS(boolean enabled) {
        Graphics.put("FPS", enabled);
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
    public Float getTileLength() {
        return (Float)Terrain.get("tileLength");
    }

    /**
     *
     * @param tileLength
     */
    public void setTileLength(float tileLength) {
        Terrain.put("tileLength", tileLength);
    }

    /**
     *
     * @return
     */
    public Float getTileHeigth() {
        return (Float)Terrain.get("tileHeigth");
    }

    /**
     *
     * @param tileHeigth
     */
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
}
