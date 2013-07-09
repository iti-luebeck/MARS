/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.auv;

import com.jme3.renderer.queue.RenderQueue;
import javax.swing.tree.TreePath;
import mars.CollisionType;
import mars.actuators.Thruster;
import mars.actuators.Actuator;
import mars.sensors.Sensor;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.ConeCollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.DebugShapeFactory;
import com.jme3.collision.CollisionResults;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Triangle;
import com.jme3.math.Vector3f;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;
import com.jme3.texture.Image.Format;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.FrameBuffer;
import com.jme3.util.BufferUtils;
import com.rits.cloning.Cloner;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.FileHandler;
import javax.swing.event.EventListenerList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.DebugHint;
import mars.Helper.Helper;
import mars.Initializer;
import mars.Keys;
import mars.PhysicalEnvironment;
import mars.PhysicalExchanger;
import mars.MARS_Settings;
import mars.gui.MARSView;
import mars.MARS_Main;
import mars.Manipulating;
import mars.Moveable;
import mars.MyCustomGhostControl;
import mars.PickHint;
import mars.accumulators.Accumulator;
import mars.actuators.BallastTank;
import mars.actuators.visualizer.PointVisualizer;
import mars.actuators.visualizer.VectorVisualizer;
import mars.states.SimState;
import mars.auv.example.Hanse;
import mars.auv.example.ASV;
import mars.auv.example.Monsun2;
import mars.auv.example.SMARTE;
import mars.core.AUVListener;
import mars.core.ChartEvent;
import mars.core.LimitedRigidBodyControl;
import mars.gui.HashMapWrapper;
import mars.ros.MARSNodeMain;
import mars.ros.RosNodeEvent;
import mars.sensors.AmpereMeter;
import mars.sensors.FlowMeter;
import mars.sensors.InfraRedSensor;
import mars.sensors.PingDetector;
import mars.sensors.RayBasedSensor;
import mars.sensors.sonar.Sonar;
import mars.sensors.TerrainSender;
import mars.sensors.UnderwaterModem;
import mars.sensors.VideoCamera;
import mars.xml.HashMapAdapter;

/**
 * The basic BasicAUV class. When you want to make own auv's or enchance them than extend from this class and make your own implementation.
 * Or implement the AUV interface when you want to do something completly different that i have done with the BasicAUV class.
 * @author Thomas Tosik
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso( {Hanse.class, Monsun2.class, ASV.class, SMARTE.class} )
public class BasicAUV implements AUV,SceneProcessor{

    private Geometry MassCenterGeom;
    private Geometry VolumeCenterGeom;
    private Geometry VolumeCenterPreciseGeom;
    private Geometry OldCenterGeom;

    private MARS_Main mars;
    private SimState simstate;
    private AssetManager assetManager;
    private RenderManager renderManager;
    private Renderer renderer;
    @Deprecated
    private MARSView view;
    private WayPoints WayPoints;
    private MARS_Settings mars_settings;
    private Initializer initer;

    @XmlElement(name="Parameters")
    private AUV_Parameters auv_param;

    private Vector3f volume_center_precise = new Vector3f(0,0,0);
    private Spatial auv_spatial;
    private Spatial debugShape;
    private Node auv_node = new Node("");
    private Node selectionNode = new Node("selectionNode");
    private RigidBodyControl physics_control;
    private MyCustomGhostControl ghostControl;
    private ColorRGBA ghostColor = new ColorRGBA();
    private CollisionShape collisionShape;
    private Geometry boundingBox;
    private Geometry boundingBoxGeom;
    float bbVolume = 0f;

    private Camera onCamera;

    private int buoyancy_updaterate = 5;
    private int drag_updaterate = 5;
    private int flow_updaterate = 1;

    //offview
    private FrameBuffer drag_offBuffer;
    private ViewPort drag_offView;
    //private Texture2D offTex;
    private Camera drag_offCamera;
    //private ImageDisplay display;
    private int offCamera_width = 640;
    private int offCamera_height = 480;
    private final ByteBuffer cpuBuf = BufferUtils.createByteBuffer(offCamera_width * offCamera_height * 4);
    private final byte[] cpuArray = new byte[offCamera_width * offCamera_height * 4];

    ViewPort debug_drag_view;

    //area
    private float frustumSize = 0.6f;//0.6f
    private float pixel_heigth = 0.0f;
    private float pixel_width = 0.0f;
    private float pixel_area = 0.0f;

    //physics
    private PhysicalEnvironment physical_environment;

    private float volume = 0.0f;//m³
    private float actual_vol = 0.0f;//m³
    private float actual_vol_air = 0.0f;//m³
    private float drag_area = 0.0f;//m² pojected
    private float drag_area_temp = 0.0f;//m² pojected

    //forces
    private float buoyancy_force = 0.0f;
    private Vector3f drag_force_vec = new Vector3f(0f,0f,0f);

    //private float density_X_gravity = fluid_density * (gravitational_acceleration);

    private Node rootNode;
    
    //PhysicalExchanger HashMaps to store and load sensors and actuators
    @XmlJavaTypeAdapter(HashMapAdapter.class)
    @XmlElement(name="Sensors")
    private HashMap<String,Sensor> sensors = new HashMap<String,Sensor> ();
    @XmlJavaTypeAdapter(HashMapAdapter.class)
    @XmlElement(name="Actuators")
    private HashMap<String,Actuator> actuators = new HashMap<String,Actuator> ();
    @XmlJavaTypeAdapter(HashMapAdapter.class)
    @XmlElement(name="Accumulators")
    private HashMap<String,Accumulator> accumulators = new HashMap<String,Accumulator> ();

    private PhysicalValues physicalvalues;
    private EventListenerList listeners = new EventListenerList();
    
    private Communication_Manager com_manager;
    private MARSNodeMain mars_node;
    
    //selection stuff aka highlightening
    private boolean selected = false;
    AmbientLight ambient_light = new AmbientLight();
    private Spatial ghost_auv_spatial;

    /**
     * This is the main auv class. This is where the auv will be made vivisble. All sensors and actuators will be added to it.
     * Also all the physics stuff happens here.
     * @param simstate 
     */
    public BasicAUV(SimState simstate){
        //set the logging
       try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
            // Add to the desired logger
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.addHandler(handler);
        } catch (IOException e) { }

        this.simstate = simstate;
        this.mars = simstate.getMARS();
        this.assetManager = simstate.getAssetManager();
        this.renderer = mars.getRenderer();
        this.renderManager = mars.getRenderManager();
        this.view = mars.getView();
        this.rootNode = simstate.getRootNode();
        this.physicalvalues = new PhysicalValues();
        this.initer = simstate.getIniter();
        selectionNode.attachChild(auv_node);
    }

    /**
     * 
     */
    /*public BasicAUV(){
        //set the logging
       try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
            // Add to the desired logger
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.addHandler(handler);
        } catch (IOException e) { }
        this.physicalvalues = new PhysicalValues();
        selectionNode.attachChild(auv_node);
    }*/
    
    public BasicAUV(){
        
    }
    
    public BasicAUV(AUV auv){
        initAfterJAXB();
        AUV_Parameters auvCopy = auv.getAuv_param().copy();
        setAuv_param(auvCopy);
        
        //clone accumulators, since they are simple no big problem here
        HashMap<String, Accumulator> accumulatorsOriginal = auv.getAccumulators();
        Cloner cloner = new Cloner();
        accumulators = cloner.deepClone(accumulatorsOriginal);
        
        HashMap<String, Actuator> actuatorOriginal = auv.getActuators();
        for ( String elem : actuatorOriginal.keySet() ){
            Actuator element = (Actuator)actuatorOriginal.get(elem);
            PhysicalExchanger copy = element.copy();
            copy.initAfterJAXB();
            registerPhysicalExchanger(copy);
        }
        
        HashMap<String, Sensor> sensorsOriginal = auv.getSensors();
        for ( String elem : sensorsOriginal.keySet() ){
            Sensor element = (Sensor)sensorsOriginal.get(elem);
            PhysicalExchanger copy = element.copy();
            copy.initAfterJAXB();
            registerPhysicalExchanger(copy);
        }
    }
    
    /**
     * 
     */
    public void initAfterJAXB(){
        try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
            // Add to the desired logger
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.addHandler(handler);
        } catch (IOException e) { }
        this.physicalvalues = new PhysicalValues();
        selectionNode.attachChild(auv_node);
    }
    
    @Override
    public void cleanupAUV() {
        cleanupOffscreenView();
        for ( String elem : sensors.keySet() ){
            Sensor element = (Sensor)sensors.get(elem);
            element.cleanup();
        }
        for ( String elem : actuators.keySet() ){
            Actuator element = (Actuator)actuators.get(elem);
            element.cleanup();
        }
    }

    /**
     *
     * @return
     */
    public PhysicalValues getPhysicalvalues() {
        return physicalvalues;
    }

    /**
     *
     * @param physicalvalues
     */
    public void setPhysicalvalues(PhysicalValues physicalvalues) {
        this.physicalvalues = physicalvalues;
    }

    /**
     *
     * @return
     */
    public AUV_Parameters getAuv_param() {
        return auv_param;
    }

    /**
     *
     * @param auv_param
     */
    public void setAuv_param(AUV_Parameters auv_param) {
        this.auv_param = auv_param;
        this.physicalvalues.setAuv(this);
        this.auv_param.setAuv(this);
        buoyancy_updaterate = auv_param.getBuoyancy_updaterate();
        drag_updaterate = auv_param.getDrag_updaterate();
        flow_updaterate = auv_param.getFlow_updaterate();
        auv_node.setName(auv_param.getAuv_name() + "_physicnode");
    }

    /**
     *
     * @return
     */
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
    public MARS_Settings getSimauv_settings() {
        return mars_settings;
    }

    /**
     *
     */
    public void setSimauv_settings(MARS_Settings simauv_settings) {
        this.mars_settings = simauv_settings;
    }
    
    /**
     * 
     * @return
     */
    public Communication_Manager getCommunicationManager() {
        return com_manager;
    }

    /**
     * 
     * @param com_manager
     */
    public void setCommunicationManager(Communication_Manager com_manager) {
        this.com_manager = com_manager;
    }
    
    /**
     * 
     * @param mars_node
     */
    public void setROS_Node(MARSNodeMain mars_node){
        this.mars_node = mars_node;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return auv_param.getAuv_name();
    }

    /**
     *
     * @param auv_name
     */
    public void setName(String auv_name) {
        auv_param.setAuv_name(auv_name);
        auv_node.setName(auv_name + "_physicnode");
    }

    /**
     *
     * @return
     */
    public String getPhysicalNodeName() {
        return auv_node.getName();
    }

    /**
     *
     * @param name
     * @param pex
     */
    public void registerPhysicalExchanger( String name, PhysicalExchanger pex ){
        pex.setPhysicalExchangerName(name);
        if(pex instanceof Sensor){
            sensors.put(name, (Sensor)pex);
            Logger.getLogger(BasicAUV.class.getName()).log(Level.INFO, "Sensor " + name + " added...", "");
        }else if(pex instanceof Actuator){
            actuators.put(name, (Actuator)pex);
            Logger.getLogger(BasicAUV.class.getName()).log(Level.INFO, "Actuator " + name + " added...", "");
        }
    }

    /**
     *
     * @param pex
     */
    public void registerPhysicalExchanger( PhysicalExchanger pex ){
        if(pex instanceof Sensor){
            sensors.put(pex.getPhysicalExchangerName(), (Sensor)pex);
            Logger.getLogger(BasicAUV.class.getName()).log(Level.INFO, "Sensor " + pex.getPhysicalExchangerName() + " added...", "");
        }else if(pex instanceof Actuator){
            actuators.put(pex.getPhysicalExchangerName(), (Actuator)pex);
            Logger.getLogger(BasicAUV.class.getName()).log(Level.INFO, "Actuator " + pex.getPhysicalExchangerName() + " added...", "");
        }
    }

    /**
     *
     * @param arrlist
     */
    public void registerPhysicalExchangers( ArrayList arrlist ){
        Iterator iter = arrlist.iterator();
        while(iter.hasNext() ) {
            PhysicalExchanger pex = (PhysicalExchanger)iter.next();
            registerPhysicalExchanger(pex);
        }
    }
    
    

    /**
     * disable the visible debug spheres that indicates the sensors/actuators positions/directions
     * @param visible
     */
    public void debugView( boolean visible ){
        for ( String elem : sensors.keySet() ){
            Sensor element = (Sensor)sensors.get(elem);
            element.setNodeVisibility(visible);
        }

        for ( String elem : actuators.keySet() ){
            Actuator element = (Actuator)actuators.get(elem);
            element.setNodeVisibility(visible);
        }
        Logger.getLogger(BasicAUV.class.getName()).log(Level.INFO, "All Sensors/Actuators have visibility: " + visible, "");
    }

    /**
     *
     * @param key Which unique registered actuator do we want?
     * @return The actuator that we asked for
     */
    public Actuator getActuator(String key){
        return actuators.get(key);
    }

    /**
     * 
     * @return
     */
    public HashMap<String,Actuator> getActuators(){
        return actuators;
    }

    /**
     *
     * @param key Which unique registered sensor do we want?
     * @return The sensor that we asked for
     */
    public Sensor getSensor(String key){
        return sensors.get(key);
    }

    /**
     *
     * @return
     */
    public HashMap<String,Sensor> getSensors(){
        return sensors;
    }
    
        /**
     *
     * @param key Which unique registered actuator do we want?
     * @return The actuator that we asked for
     */
    public Accumulator getAccumulator(String key){
        return accumulators.get(key);
    }

    /**
     * 
     * @return
     */
    public HashMap<String,Accumulator> getAccumulators(){
        return accumulators;
    }

    /**
     *
     * @param classNameString 
     * @return
     */
    public ArrayList getSensorsOfClass(String classNameString){
        ArrayList ret = new ArrayList();        
        for ( String elem : sensors.keySet() ){
            Sensor sens = (Sensor)sensors.get(elem);
            try {
                if (Class.forName(classNameString).isInstance(sens)) {
                    ret.add(sens);
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(BasicAUV.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }
    
    public boolean hasSensorsOfClass(String classNameString){
        for ( String elem : sensors.keySet() ){
            Sensor sens = (Sensor)sensors.get(elem);
            try {
                return (Class.forName(classNameString).isInstance(sens));
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(BasicAUV.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    /**
     * Call this method ONLY ONCE AFTER you have added ALL sensors and actuators to your auv.
     */
    public void init(){
        Logger.getLogger(BasicAUV.class.getName()).log(Level.INFO, "Initialising AUV: " + this.getName(), "");
        loadModel();
        createGhostAUV();
        createPhysicsNode();

        initCenters();
        initWaypoints();
        //the offscreen for area calculating(drag) must be set
        setupDragOffscreenView();//<-- buggy when deleting/deregister etc
        if(auv_param.isDebugDrag()){
            setupCam2();
        }
        
        //calculate the volume one time exact as possible, ignore water height
        long old_time = System.currentTimeMillis();
        //float[] vol = (float[])calculateVolumeAuto(auv_spatial,0.015625f,60,60,true);//0.03125f,30,30      0.0625f,80,60     0.03125f,160,120   0.0078125f,640,480
        float[] vol = (float[])calculateVolumeAutoRound(auv_spatial,0.015625f,true);//0.03125f,30,30      0.0625f,80,60     0.03125f,160,120   0.0078125f,640,480
        volume = vol[0];
        long new_time = System.currentTimeMillis();
        System.out.println("time: " + (new_time-old_time));
        System.out.println("VOLUME: " + volume + "VOLUME AIR: " + vol[1]);
        actual_vol = volume;
        buoyancy_force = physical_environment.getFluid_density() * (physical_environment.getGravitational_acceleration()) * actual_vol;

        initPhysicalExchangers();
        
        auv_node.rotate(auv_param.getRotation().x, auv_param.getRotation().y, auv_param.getRotation().z);
        rotateAUV();
        auv_node.updateGeometricState();
    };

    private void initPhysicalExchangers(){
        //init sensors
        for ( String elem : sensors.keySet() ){
            Sensor element = (Sensor)sensors.get(elem);
            if(element.isEnabled()){
                element.setAuv(this);
                element.setSimState(simstate);
                element.setPhysical_environment(physical_environment);
                element.setPhysicsControl(physics_control);
                element.setNodeVisibility(auv_param.isDebugPhysicalExchanger());
                if(element instanceof VideoCamera){
                    ((VideoCamera)element).setIniter(initer);//is needed for filters
                }
                if(element instanceof UnderwaterModem){
                    ((UnderwaterModem)element).setCommunicationManager(com_manager);//is needed for filters
                }
                if(element instanceof InfraRedSensor){
                    //((InfraRedSensor)element).setDetectable(simstate.getSonarDetectableNode());//is needed for filters
                    ((InfraRedSensor)element).setCollider(simstate.getCollider());//is needed for filters
                }  
                if(element instanceof RayBasedSensor){
                    //((RayBasedSensor)element).setDetectable(simstate.getSonarDetectableNode());//is needed for filters
                    ((RayBasedSensor)element).setCollider(simstate.getCollider());//is needed for filters
                } 
                if(element instanceof TerrainSender){
                    ((TerrainSender)element).setIniter(initer);
                    ((TerrainSender)element).setMarsSettings(mars_settings);
                }
                if(element instanceof PingDetector){
                    ((PingDetector)element).setSimObjectManager(simstate.getSimob_manager());
                }
                if(element instanceof AmpereMeter){
                    ((AmpereMeter)element).setAuv(this);
                }
                if(element instanceof FlowMeter){
                    ((FlowMeter)element).setIniter(initer);//is needed for filters
                }
                element.init(auv_node);
                if(element instanceof Keys){
                    Keys elementKeys = (Keys)element;
                    elementKeys.addKeys(mars.getInputManager(), simstate.getKeyconfig());
                }
            }
        }
        //init actuators
        for ( String elem : actuators.keySet() ){
            Actuator element = (Actuator)actuators.get(elem);
            if(element.isEnabled()){
                element.setAuv(this);
                element.setSimState(simstate);
                element.setPhysical_environment(physical_environment);
                element.setPhysicsControl(physics_control);
                element.setMassCenterGeom(this.getMassCenterGeom());
                element.setSimauv_settings(mars_settings);
                if(element instanceof PointVisualizer || element instanceof VectorVisualizer){
                    element.setNodeVisibility(auv_param.isDebugVisualizers());
                }else{
                    element.setNodeVisibility(auv_param.isDebugPhysicalExchanger());
                }
                element.setIniter(initer);
                element.init(auv_node);
                if(element instanceof Keys){
                    Keys elementKeys = (Keys)element;
                    elementKeys.addKeys(mars.getInputManager(), simstate.getKeyconfig());
                }
            }
        }
        //init special actuators like manipulating ones(servos)
        for ( String elem : actuators.keySet() ){
            Actuator element = (Actuator)actuators.get(elem);
            if(element instanceof Manipulating && element.isEnabled()){
                Manipulating mani = (Manipulating)element;
                ArrayList<String> slaves_names = mani.getSlavesNames();
                Iterator iter = slaves_names.iterator();
                while(iter.hasNext() ) {//search for the moveables(slaves) and add them to the master
                    String slave_name = (String)iter.next();
                    Moveable moves = getMoveable(slave_name);
                    moves.setLocalRotationAxisPoints(mani.getWorldRotationAxisPoints());
                    mani.addSlave(moves);
                }
            }
        }
    }
    
    private Moveable getMoveable(String name){
        for ( String elem : sensors.keySet() ){
            Sensor element = (Sensor)sensors.get(elem);
            if(element.getPhysicalExchangerName().equals(name) && element instanceof Moveable){
                return (Moveable)element;
            }
        }
        for ( String elem : actuators.keySet() ){
            Actuator element = (Actuator)actuators.get(elem);
            if(element.getPhysicalExchangerName().equals(name) && element instanceof Moveable){
                return (Moveable)element;
            }
        }
        return null;
    }
    
    /*
     * 
     */
    /**
     * 
     */
    @Override
    public void initROS(){
        for ( String elem : sensors.keySet() ){
            Sensor element = (Sensor)sensors.get(elem);
            if(element.isEnabled()){
                element.initROS(mars_node,auv_param.getAuv_name());
            }
        }
        for ( String elem : actuators.keySet() ){
            Actuator element = (Actuator)actuators.get(elem);
            if(element.isEnabled()){
                element.initROS(mars_node,auv_param.getAuv_name());
            }
        }
    }

    private void updateActuatorForces(){
        for ( String elem : actuators.keySet() ){
            Actuator element = (Actuator)actuators.get(elem);
            if(element instanceof Thruster){
                element.update();
            }else if(element instanceof BallastTank){
                element.update();
            }
        }
    }

    @Deprecated
    private void updateStabiliyTorque(){
        Vector3f centroid_center = MassCenterGeom.getWorldTranslation();
        Vector3f R = centroid_center.subtract(VolumeCenterGeom.getWorldTranslation());

        Vector3f torque = R.cross(new Vector3f(centroid_center.x,(-1)*physical_environment.getGravitational_acceleration()*auv_param.getMass(),centroid_center.z));

        //check if auv is "stable"(see swim stability to this topic:http://de.wikipedia.org/wiki/Stabilit%C3%A4t_%28Schiff%29),
        //if not then it's going to be unstable and we must reverse the torque
        if(centroid_center.y <= VolumeCenterGeom.getWorldTranslation().y){
            physics_control.applyTorque(torque);
        }else{
            physics_control.applyTorque(torque.negate());
        }
        //System.out.println("R " + R);
        //System.out.println("|R| " + R.length());
        //System.out.println("D  " + torque);
    }

    private void updateDragForces(){
        if(drag_updaterate == 0){//take all drag_updaterate times new values
            drag_updaterate = auv_param.getDrag_updaterate();
            drag_area = 0f;
        }else if(drag_updaterate == 1){
            drag_updaterate = auv_param.getDrag_updaterate();
            float new_drag_area = calculateArea();
            if(!((physics_control.getLinearVelocity().length() != 0) && (new_drag_area == 0.0f))){//we move so there must be drag area != 0, if no we have an update bug/problem, use the old one stored
                drag_area = new_drag_area;
            }
        }else{
            drag_updaterate--;
        }
        
        float velocity = physics_control.getLinearVelocity().length();
        float drag_force = (float)(auv_param.getDrag_coefficient_linear() * drag_area * 0.5f * physical_environment.getFluid_density()* Math.pow(velocity, 2));
        //System.out.println("dragarr: " + drag_area);
        //System.out.println("drag_for: " + drag_force);
        Vector3f drag_direction = physics_control.getLinearVelocity().negate();
        //System.out.println("V1_" + drag_direction.normalize());
        //norm the drag direction
        drag_direction = drag_direction.normalize();
        //System.out.println("V2_" + drag_direction);
        //drag_force_vec = drag_direction.mult(drag_force);
        drag_force_vec = drag_direction.mult(drag_force/mars_settings.getPhysicsFramerate());
        //System.out.println("comp: " + complete_force.length());
        /*System.out.println("===========");
        System.out.println(getAuv_param().getAuv_name());
        System.out.println("drag: " + drag_force);
        System.out.println("dragarr: " + drag_area);
        System.out.println("velo: " + velocity);*/
        //addValueToSeries(drag_force,0);
        //addValueToSeries(drag_area,1);
        //addValueToSeries(physics_control.getLinearVelocity().y,2);
        //System.out.println("comp: " + complete_force.length());
        /*
        if(complete_force.length() > drag_force_vec.length()){//only add drag if it's not greater than the rest of the forces, so we dont get "sucked in" through the drag
            complete_force = complete_force.add(drag_force_vec);
        }else{
            drag_force_vec = drag_direction.mult(complete_force.length());
            complete_force = complete_force.add(drag_force_vec);
        }*/
        //physics_control.applyCentralForce(drag_force_vec);
        
        //since the impulse vector should be in world space (http://www.bulletphysics.org/Bullet/phpBB3/viewtopic.php?p=&f=9&t=2693) we have to convert it
        //Vector3f world_drag_force_vec = physics_control.getPhysicsLocation().add(drag_force_vec);
        Vector3f world_drag_force_vec = new Vector3f();
        auv_node.localToWorld(drag_force_vec, world_drag_force_vec);
        physics_control.applyImpulse(drag_force_vec,new Vector3f(0f, 0f, 0f));
        //physics_control.applyImpulse(drag_force_vec,physics_control.getPhysicsLocation());
        physicalvalues.updateDragForce(drag_force/mars_settings.getPhysicsFramerate());
        physicalvalues.updateDragArea(drag_area);
        //physicalvalues.updateVector(physics_control.getLinearVelocity());
        notifySafeAdvertisement(new ChartEvent(this, drag_area, 0));
    }

    private void updateAngularDragForces(){
        Vector3f cur_ang = physics_control.getAngularVelocity();
        float angular_velocity = physics_control.getAngularVelocity().length();
        float drag_torque = (float)(auv_param.getDrag_coefficient_angular() * drag_area * 0.25f * physical_environment.getFluid_density()* Math.pow(angular_velocity, 2));
        Vector3f drag_direction = physics_control.getAngularVelocity().negate().normalize();
        Vector3f angular_drag_torque_vec = drag_direction.mult(drag_torque/mars_settings.getPhysicsFramerate());
        /*System.out.println("cur_ang: " + cur_ang);
        System.out.println("angular_velocity: " + angular_velocity);
        System.out.println("drag_torque: " + drag_torque);
        System.out.println("drag_direction: " + drag_direction);
        System.out.println("angular_drag_torque_vec: " + angular_drag_torque_vec);
        System.out.println("angular_drag_torque_scalar: " + angular_drag_torque_vec.length());
        System.out.println("==========================");*/
        physics_control.applyTorqueImpulse(angular_drag_torque_vec);
        /*System.out.println("after:angular_velocity: " + physics_control.getAngularVelocity());
        System.out.println("after:angular_velocity_length: " + physics_control.getAngularVelocity().length());*/
        physicalvalues.updateDragTorque(drag_torque);
        physicalvalues.updateVector(cur_ang);
    }

    private void updateStaticBuyocancyForces(){
        Vector3f brick_vec = OldCenterGeom.getWorldTranslation();
        float distance_to_surface = 1.0f;
        float epsilon = 0.5f;
        if(buoyancy_updaterate == 1){//take all buoyancy_updaterate times new values
            buoyancy_updaterate = auv_param.getBuoyancy_updaterate();

            //System.out.println("wah " + (this.water_height-distance_to_surface));
            //System.out.println("bv " + brick_vec.y);

/*
            //we wanted to save some ressources here but it lead to some problems so its deactivated
            if(brick_vec.y >= physical_environment.getWater_height()-distance_to_surface){//calculate repressed volume only if we are near the water surface
                //actual_vol = (float)calculateVolume(pn,false,0.0625f,80,60,false);
                actual_vol = (float)calculateVolume(auv_spatial,false,0.03125f,30,30,false);
            }else{//we are deep enough underwater that we use our super precision calculated volume and volumecenter
                actual_vol = volume;
                final Vector3f in = VolumeCenterPreciseGeom.getLocalTranslation();
                Future fut = mars.enqueue(new Callable() {
                    public Void call() throws Exception {
                        VolumeCenterGeom.setLocalTranslation(in);
                        return null;
                    }
                });
            }*/
            
/*
            if( (brick_vec.y <= (physical_environment.getWater_height()-distance_to_surface)+epsilon) && (brick_vec.y >= (physical_environment.getWater_height()-distance_to_surface)-epsilon)){// if we are in the "zone" than "fuzzy" a little
                System.out.println("ZONE");
                float difference = Math.abs(brick_vec.y-(physical_environment.getWater_height()-distance_to_surface+epsilon));
                float length = 2*epsilon;
                float x = (difference/length)*100f;
                actual_vol = (float)calculateVolume(auv_spatial,0.03125f,30,30,false);
                actual_vol = (actual_vol*(100f-x)+volume*x)/100f;

                
//                Vector3f in = VolumeCenterPreciseGeom.getLocalTranslation();
//                Vector3f distance_vec = VolumeCenterGeom.getLocalTranslation().subtract(in).normalize();
//                float distance = in.distance(VolumeCenterGeom.getLocalTranslation());
//                float y = (difference/length)*distance;
//                distance_vec = distance_vec.mult(y);
//                final Vector3f gabe = in.add(distance_vec);
//
//                Future fut = mars.enqueue(new Callable() {
//                    public Void call() throws Exception {
//                        VolumeCenterGeom.setLocalTranslation(gabe);
//                        VolumeCenterGeom.updateGeometricState();
//                        return null;
//                    }
//                });
            }else if(brick_vec.y > (physical_environment.getWater_height()-distance_to_surface)+epsilon){// over the "zone"
                actual_vol = (float)calculateVolume(auv_spatial,0.03125f,30,30,false);
            }else{//under the "zone"
                actual_vol = volume;
                final Vector3f in = VolumeCenterPreciseGeom.getLocalTranslation();
                Future fut = mars.enqueue(new Callable() {
                    public Void call() throws Exception {
                        VolumeCenterGeom.setLocalTranslation(in);
                        VolumeCenterGeom.updateGeometricState();
                        return null;
                    }
                });
            }*/

            //float[] vol = (float[])calculateVolume(auv_spatial,0.03125f,30,30,false);
            //float[] vol = (float[])calculateVolumeAutoRound(auv_spatial,0.03125f,false);
            float[] vol = (float[])calculateVolumeExcact(auv_spatial,false);
            actual_vol = vol[0]*auv_param.getBuoyancy_scale();
            actual_vol_air = vol[1]*auv_param.getBuoyancy_scale();

            //System.out.println("act vol: " + actual_vol);
            //System.out.println("vol: " + volume);
            
            //buoyancy_force = physical_environment.getFluid_density() * physical_environment.getGravitational_acceleration() * volume;
            buoyancy_force = (physical_environment.getFluid_density() * actual_vol + physical_environment.getAir_density() * actual_vol_air) * physical_environment.getGravitational_acceleration();
            
            //buoyancy_force_air = physical_environment.getAir_density() * physical_environment.getGravitational_acceleration() * Math.abs(volume - actual_vol);
            /*if(volume >= actual_vol){
                System.out.println("!!!!!!!!!!!!!");
                System.out.println(buoyancy_force_water);
                System.out.println(buoyancy_force_air);
            }*/
            //addValueToSeries(actual_vol,1);
            //addValueToSeries(OldCenterGeom.getWorldTranslation().y + Math.abs(physical_environment.getWater_height()),0);
            //addValueToSeries((float)Math.sqrt(Math.pow(this.AUVPhysicsNode.get.getContinuousForce().x, 2)+Math.pow(this.AUVPhysicsNode.getContinuousForce().y, 2)+Math.pow(this.AUVPhysicsNode.getContinuousForce().z,2)),2);
            //addValueToSeries(buoyancy_force_water+buoyancy_force_air,2);
            //final PressureSensor press = (PressureSensor)this.getSensor("press");
            //addValueToSeries(press.getDepth(),0);
        }else if(auv_param.getBuoyancy_updaterate() == 0){//dont compute everytime the buoyancy, use the computed once
            buoyancy_updaterate = auv_param.getBuoyancy_updaterate();
            if(brick_vec.y <= (physical_environment.getWater_height()-auv_param.getBuoyancy_distance())){//under water
                buoyancy_force = physical_environment.getFluid_density() * physical_environment.getGravitational_acceleration() * volume;
            }else{//at water surface
                buoyancy_force = physical_environment.getFluid_density() * physical_environment.getGravitational_acceleration() * volume*auv_param.getBuoyancy_scale();
            }
        } else {
            //buoyancy_force = physical_environment.getFluid_density() * physical_environment.getGravitational_acceleration() * actual_vol;
            buoyancy_force = (physical_environment.getFluid_density() * actual_vol + physical_environment.getAir_density() * actual_vol_air) * physical_environment.getGravitational_acceleration();
            buoyancy_updaterate--;
        }

        //buoyancy_force = buoyancy_force_water + buoyancy_force_air;

        //System.out.println("buyo: " + buoyancy_force);
        //Vector3f buoyancy_force_vec = new Vector3f(0.0f,buoyancy_force,0.0f);
        Vector3f buoyancy_force_vec = new Vector3f(0.0f,buoyancy_force/mars_settings.getPhysicsFramerate(),0.0f);
        physicalvalues.updateVolume(volume);
        physicalvalues.updateBuoyancyForce(buoyancy_force);
        //notifySafeAdvertisement(new ChartEvent(this, OldCenterGeom.getWorldTranslation().y + Math.abs(physical_environment.getWater_height()), 0));
        //notifySafeAdvertisement(new ChartEvent(this, actual_vol, 0));
        //addValueToSeries(buoyancy_force,1);
        //addValueToSeries(actual_vol,1);
        //addValueToSeries(actual_vol_air,2);
        //System.out.println("vol: " + actual_vol + " " + actual_vol_air + " " + buoyancy_force_vec);
        //physics_control.applyCentralForce(buoyancy_force_vec);
        //physics_control.applyForce(buoyancy_force_vec, VolumeCenterGeom.getWorldTranslation().subtract(MassCenterGeom.getWorldTranslation()));
        if(!infinityCheck(buoyancy_force_vec)){
            physics_control.applyImpulse(buoyancy_force_vec, VolumeCenterGeom.getWorldTranslation().subtract(MassCenterGeom.getWorldTranslation()));
            //physics_control.applyForce(buoyancy_force_vec, VolumeCenterGeom.getWorldTranslation().subtract(MassCenterGeom.getWorldTranslation()));
        }else{
            System.out.println("Too much force, caused be infinity...");
        }
    }
    
    private boolean infinityCheck(Vector3f vec){
        if ( (vec.x == Vector3f.NAN.x) || (vec.y == Vector3f.NAN.y) || (vec.z == Vector3f.NAN.z) || (vec.x == Vector3f.POSITIVE_INFINITY.x) || (vec.y == Vector3f.POSITIVE_INFINITY.y) || (vec.z == Vector3f.POSITIVE_INFINITY.z) || (vec.x == Vector3f.NEGATIVE_INFINITY.x) || (vec.y == Vector3f.NEGATIVE_INFINITY.y) || (vec.z == Vector3f.NEGATIVE_INFINITY.z)){
            return true;
        }
        return false;
    }

    /**
     * Override this method to implement your own forces.
     * @return
     */
    protected Vector3f updateMyForces(){
        return new Vector3f(0f,0f,0f);
    }

    /**
     * Override this method to implement your own torques.
     * @return
     */
    protected Vector3f updateMyTorque(){
        return new Vector3f(0f,0f,0f);
    }

    private void updateMyForcesAndTorques(Vector3f force, Vector3f torque){
        physics_control.applyCentralForce(force);
        physics_control.applyTorque(torque);
    }

    private void updateDynamicBuyocancyForces(){
        
    }

    private void updateWaterCurrentForce(){
        if(flow_updaterate == 1){//take all flow_updaterate times new values
            flow_updaterate = auv_param.getFlow_updaterate();
            Vector3f physicsLocation = physics_control.getPhysicsLocation();
            Vector3f flow_scale = mars_settings.getFlowScale();
            int flow_image_width = initer.getFlow_image_width();
            
            Vector3f addedFlowPos = mars_settings.getFlowPosition().add(-((float)flow_image_width*flow_scale.x)/2f, 0f, -((float)flow_image_width*flow_scale.z)/2f);
            Vector3f relAuvPos = physicsLocation.subtract(addedFlowPos);
            
            if( (relAuvPos.x <= ((float)flow_image_width*flow_scale.x)) && (relAuvPos.x >= 0) && (relAuvPos.z <= ((float)flow_image_width*flow_scale.z)) && (relAuvPos.z >= 0) ){//in flowmap bounds
                
                int auv_pos_x = (int)(((float)flow_image_width/((float)flow_image_width*flow_scale.x))*relAuvPos.x);
                int auv_pos_y = (int)(((float)flow_image_width/((float)flow_image_width*flow_scale.z))*relAuvPos.z);
                
                int flowX = initer.getFlowX()[(auv_pos_x)+(initer.getTerrain_image_width()*auv_pos_y)];
                int flowY = initer.getFlowY()[(auv_pos_x)+(initer.getTerrain_image_width()*auv_pos_y)];
                //System.out.println("physicsLocation: " + physicsLocation + " " + "auv_pos_x: " + auv_pos_x + " " + "auv_pos_y: " + auv_pos_y + " " + "flowX: " + flowX + "flowY: " + flowY);

                float scaledFlowX = (flowX/32768f)/mars_settings.getPhysicsFramerate();
                float scaledFlowY = (flowY/32768f)/mars_settings.getPhysicsFramerate();
                Vector3f flowForce = new Vector3f(scaledFlowX, 0f, scaledFlowY);
                flowForce.multLocal(mars_settings.getFlowForceScale());
                initer.setFlowVector(new Vector3f((flowX/32768f), 0f, (flowY/32768f)));
                physics_control.applyImpulse(flowForce, Vector3f.ZERO);
            }else{//out of flowmap bound. no force
                
            }            
        }else if(flow_updaterate == 0){
            flow_updaterate = auv_param.getFlow_updaterate();
        }else{
            flow_updaterate--;
        }
    }

    /**
     *
     */
    public void clearForces(){
        physics_control.clearForces();
        physics_control.setAngularVelocity(Vector3f.ZERO);
        physics_control.setLinearVelocity(Vector3f.ZERO);
    }

    /**
     *
     */
    public void reset(){
        resetAllActuators();
        resetAllSensors();
        resetAllAccumulators();
        clearForces();
        physics_control.setPhysicsLocation(auv_param.getPosition());
        rotateAUV();
    }

    /*
     *
     */
    private void rotateAUV(){
        Matrix3f m_rot = new Matrix3f();
        Quaternion q_rot = new Quaternion();
        q_rot.fromAngles(auv_param.getRotation().x, auv_param.getRotation().y, auv_param.getRotation().z);
        m_rot.set(q_rot);
        physics_control.setPhysicsRotation(m_rot);
    }

    /**
     *
     * @param tpf
     */
    public void updateForces(float tpf){
        if(auv_node.getParent().getParent().getParent().getParent().getName() != null && auv_node.getParent().getParent().getParent().getParent().getName().equals("SimState Root Node")){//check if PhysicsNode added to rootNode
            //since bullet deactivate nodes that dont move enough we must activate it
            /*if(!physics_control.isActive()){
                physics_control.activate();
            }*/
            //calculate actuator(motors) forces
            updateActuatorForces();

            //calculate stability torque
            //updateStabiliyTorque();

            //calculate buyocancy
            updateStaticBuyocancyForces();

            updateDynamicBuyocancyForces();

            //externalforces
            updateMyForcesAndTorques(updateMyForces(),updateMyTorque());

            //calculate the drag
            updateDragForces();
            updateAngularDragForces();

            //add the water_current
            updateWaterCurrentForce();
            
            updatePhysicalValues();
            
        }else{//if not inform
            Logger.getLogger(BasicAUV.class.getName()).log(Level.WARNING, "AUV PhysicsNode is not added to the rootNode!", "");
        }
    }

    /**
     *
     * @param tpf time per frame
     */
    public void updateSensors(float tpf){
        for ( String elem : sensors.keySet() ){
            Sensor element = (Sensor)sensors.get(elem);
            if(element.isEnabled()){
                element.update(tpf);
            }
        }
    }
    
    /**
     *
     * @param tpf time per frame
     */
    public void updateActuators(float tpf){
        for ( String elem : actuators.keySet() ){
            Actuator element = (Actuator)actuators.get(elem);
            if(element.isEnabled()){
                element.update(tpf);
            }
        }
    }
    
    public void updateAccumulators(float tpf){
        //update current consumption for the activated sensors
        for ( String elem : sensors.keySet() ){
            Sensor element = (Sensor)sensors.get(elem);
            if(element.isEnabled()){
                Accumulator acc = (Accumulator)accumulators.get(element.getAccumulator());
                if(acc != null){ //accu exists from where we can suck energy
                    Float currentConsumption = element.getCurrentConsumption();
                    if(currentConsumption != null){//suck energy
                        float aH = (currentConsumption/3600f)*tpf;
                        acc.subsractActualCurrent(aH);
                    }
                }
            }
        }
        //update current consumption for the activated actuators and thrusters
        for ( String elem : actuators.keySet() ){
            Actuator element = (Actuator)actuators.get(elem);
            if(element.isEnabled()){
                Accumulator acc = (Accumulator)accumulators.get(element.getAccumulator());
                if(acc != null){ //accu exists from where we can suck energy
                    if(element instanceof Thruster){//check if thruster(curent function) or normal actuator
                        Thruster th = (Thruster)element;
                        float motorCurrent = th.getMotorCurrent();
                        float aH = (motorCurrent/3600f)*tpf;
                        acc.subsractActualCurrent(aH);
                    }else{
                        Float currentConsumption = element.getCurrentConsumption();
                        if(currentConsumption != null){//suck energy
                            float aH = (currentConsumption/3600f)*tpf;
                            acc.subsractActualCurrent(aH);
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @param view
     */
    @Deprecated
    public void setView(MARSView view){
        this.view = view;
    }

    /**
     *
     * @return
     */
    @Deprecated
    public MARSView getView(){
        return view;
    }

    /*
     * This method is used to add values to the graphs so they can be plotted. For example depth of the auv over time.
     */
    private void addValueToSeries(float value, int series){
        if(series == 0){
            view.addValueToSeries(value,0);
        }else if(series == 1){
            view.addValueToSeries(value,1);
        }else if(series == 2){
            view.addValueToSeries(value,2);
        }else if(series == 3){
            view.addValueToSeries(value,3);
        }
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
     * @param physics_control
     */
    public void setPhysicsControl(RigidBodyControl physics_control) {
        this.physics_control = physics_control;
        auv_node.addControl(physics_control);
    }

    public MyCustomGhostControl getGhostControl() {
        return ghostControl;
    }
    
    private ColorRGBA getGhostColor(){
        return ghostColor;
    }

    /**
     *
     * @return
     */
    public Node getAUVNode() {
        return auv_node;
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
    public Spatial getAUVSpatial() {
        return auv_spatial;
    }

    private void initCenters(){
        Sphere sphere4 = new Sphere(16, 16, 0.015f);
        VolumeCenterGeom = new Geometry("VolumeCenterGeom", sphere4);
        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat4.setColor("Color", ColorRGBA.Cyan);
        VolumeCenterGeom.setMaterial(mark_mat4);
        VolumeCenterGeom.setLocalTranslation(auv_param.getCentroid_center_distance().x, auv_param.getCentroid_center_distance().y,auv_param.getCentroid_center_distance().z);
        VolumeCenterGeom.updateGeometricState();
        if(!auv_param.isDebugCenters()){
            VolumeCenterGeom.setCullHint(CullHint.Always);
        }
        auv_node.attachChild(VolumeCenterGeom);

        Sphere sphere6 = new Sphere(16, 16, 0.0125f);//0.03f
        OldCenterGeom = new Geometry("OldCenterGeom", sphere6);
        Material mark_mat6 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat6.setColor("Color", ColorRGBA.Brown);
        OldCenterGeom.setMaterial(mark_mat6);
        OldCenterGeom.setLocalTranslation(auv_param.getCentroid_center_distance().x, auv_param.getCentroid_center_distance().y,auv_param.getCentroid_center_distance().z);
        OldCenterGeom.updateGeometricState();
        if(!auv_param.isDebugCenters()){
            OldCenterGeom.setCullHint(CullHint.Always);
        }
        auv_node.attachChild(OldCenterGeom);

        Sphere sphere5 = new Sphere(16, 16, 0.025f);//0.03f
        MassCenterGeom = new Geometry("MassCenterGeom", sphere5);
        Material mark_mat5 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat5.setColor("Color", ColorRGBA.Yellow);
        MassCenterGeom.setMaterial(mark_mat5);
        Vector3f temp = new Vector3f(0f,0f,0f);
        auv_node.worldToLocal(auv_node.getWorldTranslation(), temp);
        MassCenterGeom.setLocalTranslation(temp);
        MassCenterGeom.updateGeometricState();
        if(!auv_param.isDebugCenters()){
            MassCenterGeom.setCullHint(CullHint.Always);
        }
        auv_node.attachChild(MassCenterGeom);

        Sphere sphere12 = new Sphere(16, 16, 0.015f);
        VolumeCenterPreciseGeom = new Geometry("VolumeCenterPreciseGeom", sphere12);
        Material mark_mat12 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat12.setColor("Color", ColorRGBA.Red);
        VolumeCenterPreciseGeom.setMaterial(mark_mat12);
        auv_node.worldToLocal(volume_center_precise, temp);
        VolumeCenterPreciseGeom.setLocalTranslation(temp);
        VolumeCenterPreciseGeom.updateGeometricState();
        if(!auv_param.isDebugCenters()){
            VolumeCenterPreciseGeom.setCullHint(CullHint.Always);
        }
        auv_node.attachChild(VolumeCenterPreciseGeom);
    }

    /*
     *
     */
    private void initWaypoints(){
        //if(auv_param.isWaypoints_enabled()){
            WayPoints = new WayPoints("WayPoints_" + getName(),mars,auv_param);
            Future fut = mars.enqueue(new Callable() {
                public Void call() throws Exception {
                    rootNode.attachChild(WayPoints);
                    return null;
                }
            });
        //}
    }

    /**
     *
     * @return
     */
    public Geometry getMassCenterGeom(){
        return MassCenterGeom;
    }

    /*
     *
     */
    private void loadModel(){
        //assetManager.registerLocator("Assets/Models", FileLocator.class);

        auv_spatial = assetManager.loadModel(auv_param.getModelFilePath());
        //assetManager.unregisterLoader(OBJLoader.class);
        //assetManager.registerLoader(MyOBJLoader.class,"obj");
        //auv_spatial = (Spatial)assetManager.loadAsset(new ModelKey(auv_param.getModelFilePath()));

                
        /*assetManager.registerLoader(MyMTLLoader.class);
        int index = auv_param.getModelFilePath().lastIndexOf(".");
        String matPath = auv_param.getModelFilePath().substring(0, index).concat(".mtl");
        Material auv_mat = (Material)assetManager.loadAsset(matPath);*/
        
        auv_spatial.setLocalScale(auv_param.getModel_scale());
        //auv_spatial.rotate(-(float)Math.PI/4 , (float)Math.PI/4 , 0f);
        //Material mat_white = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        //mat_white.setColor("Color", ColorRGBA.White);
        //mat_white.setColor("GlowColor", ColorRGBA.Blue); 
        //auv_spatial.setMaterial(mat_white);
        /*Material mat_white = new Material(assetManager, "Common/MatDefs/Misc/WireColor.j3md");
        mat_white.setColor("Color", ColorRGBA.Blue);
        auv_spatial.setMaterial(mat_white);*/
        auv_spatial.setLocalTranslation(auv_param.getCentroid_center_distance().x, auv_param.getCentroid_center_distance().y,auv_param.getCentroid_center_distance().z);
        auv_spatial.updateModelBound();
        auv_spatial.updateGeometricState();
        auv_spatial.setName(auv_param.getModel_name());
        auv_spatial.setUserData("auv_name", getName());
        auv_spatial.setCullHint(CullHint.Never);//never cull it because offscreen uses it
        auv_spatial.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        
        //own load controler, still lacks a lot of features to be used productive
        /*SpatialLodControl slc = new SpatialLodControl(mars.getCamera(),auv_spatial);
        auv_spatial.addControl(slc);*/
        
        WireBox wbx = new WireBox();
        BoundingBox bb = (BoundingBox) auv_spatial.getWorldBound();
        bbVolume = bb.getVolume();
        //BoundingBox bb = new BoundingBox();
        //bb.computeFromPoints(auv_spatial.);
        wbx.fromBoundingBox(bb);
        boundingBox = new Geometry("TheMesh", wbx);
        Material mat_box = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_box.setColor("Color", ColorRGBA.Blue);
        boundingBox.setMaterial(mat_box);
        boundingBox.setLocalTranslation(bb.getCenter());
        //boundingBox.setLocalTranslation(auv_param.getCentroid_center_distance().x, auv_param.getCentroid_center_distance().y,auv_param.getCentroid_center_distance().z);
        boundingBox.updateModelBound();
        boundingBox.updateGeometricState();
        setBoundingBoxVisible(auv_param.isDebugBounding());
        Helper.setNodePickUserData(boundingBox,PickHint.NoPick);
        auv_node.attachChild(boundingBox);
                
        //add a full geom box bounding box since the WireBox produces NPE
        Box box = new Box(bb.getXExtent(),bb.getYExtent(),bb.getZExtent());
        boundingBoxGeom = new Geometry("BBMesh", box);
        boundingBoxGeom.setLocalTranslation(bb.getCenter());
        boundingBoxGeom.updateGeometricState();
        Helper.setNodePickUserData(boundingBoxGeom,PickHint.NoPick);
        setSpatialVisible(boundingBoxGeom,false);
        auv_node.attachChild(boundingBoxGeom);
        
        setWireframeVisible(auv_param.isDebugWireframe());
        auv_node.attachChild(auv_spatial);
    }
    
    private void createGhostAUV(){
        //assetManager.registerLocator("Assets/Models", FileLocator.class);
        ghost_auv_spatial = assetManager.loadModel(auv_param.getModelFilePath());
        ghost_auv_spatial.setLocalScale(auv_param.getModel_scale());
        ghost_auv_spatial.setLocalTranslation(auv_param.getCentroid_center_distance().x, auv_param.getCentroid_center_distance().y,auv_param.getCentroid_center_distance().z);
        ghost_auv_spatial.updateGeometricState();
        ghost_auv_spatial.updateModelBound();
        ghost_auv_spatial.setName(auv_param.getModel_name() + "_ghost");
        ghost_auv_spatial.setUserData("auv_name", getName());
        ghost_auv_spatial.setCullHint(CullHint.Always);
        Helper.setNodePickUserData(ghost_auv_spatial,PickHint.NoPick);
        auv_node.attachChild(ghost_auv_spatial);
        
        //add ghost collision to the "ghost" object so we can get collision results
        BoundingBox ghostBound = (BoundingBox)ghost_auv_spatial.getWorldBound();
        ghostControl = new MyCustomGhostControl(new BoxCollisionShape(ghostBound.getExtent(null)));
        ghostControl.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_01);
        ghostControl.setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_01);

        ghost_auv_spatial.addControl(ghostControl);
        
        /*Spatial debugShape2 = ghostControl.createDebugShape(assetManager);
        auv_node.attachChild(debugShape2);*/
    }
    
    /**
     * 
     * @return
     */
    public Spatial getGhostAUV(){
        return ghost_auv_spatial;
    }
    
    /**
     * 
     * @param hide
     */
    public void hideGhostAUV(boolean hide){
        if(hide){
             ghost_auv_spatial.setCullHint(CullHint.Always);
        }else{
             ghost_auv_spatial.setCullHint(CullHint.Never);
        }
    }

    /*
     * When we have the spatial for the auv we create the physics node out of it. Needed for all the physics and collisions.
     */
    private void createPhysicsNode(){
        CompoundCollisionShape compoundCollisionShape1 = new CompoundCollisionShape();

        if(auv_param.getType() == CollisionType.BOXCOLLISIONSHAPE){
            collisionShape = new BoxCollisionShape(auv_param.getDimensions());
        }else if(auv_param.getType() == CollisionType.SPHERECOLLISIONSHAPE){
            collisionShape = new SphereCollisionShape(auv_param.getDimensions().x);
        }else if(auv_param.getType() == CollisionType.CONECOLLISIONSHAPE){
            collisionShape = new ConeCollisionShape(auv_param.getDimensions().x,auv_param.getDimensions().y);
        }else if(auv_param.getType() == CollisionType.CYLINDERCOLLISIONSHAPE){
            collisionShape = new CylinderCollisionShape(auv_param.getDimensions(),0);
        }else if(auv_param.getType() == CollisionType.MESHACCURATE){
            //collisionShape = CollisionShapeFactory.createDynamicMeshShape(auv_spatial);
        }else{
            collisionShape = new BoxCollisionShape(auv_param.getDimensions());
        }

        compoundCollisionShape1.addChildShape(collisionShape, auv_param.getCentroid_center_distance().add(auv_param.getCollisionPosition()));

        physics_control = new LimitedRigidBodyControl(compoundCollisionShape1, auv_param.getMass());
        physics_control.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_01);
        physics_control.setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_01);
        physics_control.setDamping(auv_param.getDamping_linear(), auv_param.getDamping_angular());
        physics_control.setAngularFactor(auv_param.getAngular_factor());
        physics_control.setSleepingThresholds(0f, 0f);// so the physics node doesn't get deactivated
        //physics_control.setApplyPhysicsLocal(true);
        //physics_control.setFriction(0f);
        //physics_control.setRestitution(0.3f);

        //debug
        Material debug_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        debug_mat.setColor("Color", ColorRGBA.Red);

        
        if(getAuv_param().isDebugCollision()){
            Helper.setNodeUserData(auv_node,DebugHint.DebugName,DebugHint.Debug);
        }else{
            Helper.setNodeUserData(auv_node,DebugHint.DebugName,DebugHint.NoDebug);
        }
            
        auv_node.setLocalTranslation(auv_param.getPosition());
        auv_node.addControl(physics_control);
        auv_node.updateGeometricState();
    }

    /**
     *
     */
    public void setInitialPosition(){
        Vector3f temp_location = new Vector3f(0f,0f,0f);
        auv_node.worldToLocal(auv_param.getPosition(),temp_location);
        auv_node.setLocalTranslation(temp_location);
        auv_node.updateGeometricState();
    }

    /*
     * This view is needed for calculating the projected area of the auv. It's used in water resistance calculations.
     */
    private void setupDragOffscreenView(){
        drag_offCamera = new Camera(offCamera_width,offCamera_height);

        //calculate frusturm size so we render the maximum possible of the auv
        BoundingBox boundBox = (BoundingBox)auv_spatial.getWorldBound();
        Vector3f centerBB = boundBox.getCenter();
        Vector3f extBB = boundBox.getExtent(null);
        
        frustumSize = (float)Math.atan(extBB.length());
        
        // create a pre-view. a view that is rendered before the main view
        drag_offView = renderManager.createPreView("Offscreen View Area", drag_offCamera);
        drag_offView.setBackgroundColor(ColorRGBA.Green);
        drag_offView.setClearFlags(true, true, true);
        drag_offView.addProcessor(this);

        // create offscreen framebuffer
        drag_offBuffer = new FrameBuffer(offCamera_width,offCamera_height, 1);

        //setup framebuffer's cam
        drag_offCamera.setParallelProjection(true);
        float aspect = (float) offCamera_width / offCamera_height;
        drag_offCamera.setFrustum(-1000, 1000, -aspect * frustumSize, aspect * frustumSize, frustumSize, -frustumSize);
        drag_offCamera.setLocation(centerBB);
        pixel_heigth = ((2*frustumSize)/offCamera_height);//multiplied by 2 because the frustrumsize counts from the middle
        pixel_width = ((2*(aspect * frustumSize))/offCamera_width);
        pixel_area = pixel_heigth*pixel_width;
        //System.out.println("pa: " + pixel_area + " ph: " + pixel_heigth + " pw: " + pixel_width);

        //setup framebuffer to use renderbuffer
        // this is faster for gpu -> cpu copies
        drag_offBuffer.setDepthBuffer(Format.Depth);
        drag_offBuffer.setColorBuffer(Format.RGBA8);

        //set viewport to render to offscreen framebuffer
        drag_offView.setOutputFrameBuffer(drag_offBuffer);

        // attach the scene to the viewport to be rendered
        if(auv_param.isEnabled()){
            drag_offView.attachScene(auv_spatial);//<-- this is the bad boy when registering (modifying the thread blabla)
        }
    }
    
    /**
     * 
     */
    public void addDragOffscreenView(){
        drag_offView.attachScene(auv_spatial);
    }

    /**
     *
     */
    public void cleanupOffscreenView(){
        drag_offView.setEnabled(false);
        drag_offView.clearProcessors();
        drag_offView.clearScenes();
        renderManager.removePreView(drag_offView);
        if(debug_drag_view != null){
            debug_drag_view.setEnabled(false);
            debug_drag_view.clearProcessors();
            debug_drag_view.clearScenes();
            renderManager.removePreView(debug_drag_view);
        }
    }

    /*
     * This method is used for getting the offbuffer and count the pixels that aren't green on it
     */
    private float updateImageContents(){
        if(renderer != null){
            cpuBuf.clear();

            /*Future fut = mars.enqueue(new Callable() {
                    public Void call() throws Exception {
                        renderer.readFrameBuffer(drag_offBuffer, cpuBuf);
                        return null;
                    }
            });*/
            renderer.readFrameBuffer(drag_offBuffer, cpuBuf);
            /*try {
                fut.get();
            } catch (InterruptedException ex) {
                Logger.getLogger(BasicAUV.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(BasicAUV.class.getName()).log(Level.SEVERE, null, ex);
            }*/

            // copy native memory to java memory
            cpuBuf.clear();
            cpuBuf.get(cpuArray);
            cpuBuf.clear();
            int whites = 0;

            for (int i = 0; i < offCamera_width * offCamera_height * 4; i+=4){
                byte b = cpuArray[i+0];
                byte g = cpuArray[i+1];
                byte r = cpuArray[i+2];
                byte a = cpuArray[i+3];
                //System.out.println(b + " " + g + " " + r);
                if(g == -1 && b == 0 && r ==0){
                    whites++;
                }
            }
            whites = (offCamera_width*offCamera_height)-whites;
            //System.out.println(getAuv_param().getAuv_name() + " WHITES: " + whites);
            return whites*pixel_area;
        }
        return 0.0f;
    }

    /*
     * Calculates the projected area of the auv.
     */
    private float calculateArea(){
        //center of bb ist needed for correct frustrum to apply
        BoundingBox boundBox = (BoundingBox)auv_spatial.getWorldBound();
        Vector3f centerBB = boundBox.getCenter();
        float waterheight = initer.getCurrentWaterHeight(centerBB.x, centerBB.z);
        
        //in wich direction are we moving? mirror the vector
        drag_offCamera.setLocation(centerBB.add(physics_control.getLinearVelocity().normalize()));
        drag_offCamera.lookAt( centerBB
                , Vector3f.UNIT_Y);
        
        if(auv_param.isDebugDrag()){
            onCamera.setLocation( centerBB.add(physics_control.getLinearVelocity().normalize()) );
            //onCamera.setLocation( centerBB.add(Vector3f.UNIT_X) );
            onCamera.lookAt( centerBB 
                , Vector3f.UNIT_Y);
        }
        
        //System.out.println("physics_control.getLinearVelocity(): " + physics_control.getLinearVelocity());

        if(physics_control.getLinearVelocity().length() != 0f){//when we have no velocity then we have no water resistance than we dont need an update
            return drag_area_temp;//updateImageContents();
        }else{
            return 0.0f;
        }
    }

    /*
     * A debug view. Lets us see what the calculateArea method "sees"
     */
    private void setupCam2(){
        //extra view for looking what he sees
        onCamera = drag_offCamera.clone();
        onCamera.setViewPort(0f, 0.5f, 0f, 0.5f);
        debug_drag_view = renderManager.createMainView("Onscreen View Area", onCamera);
        debug_drag_view.setBackgroundColor(ColorRGBA.Green);
        debug_drag_view.setClearFlags(true, true, true);
        debug_drag_view.attachScene(auv_spatial);
    }

    /*
     * gets us the volume and volume center of one bracket
     */
    private float[] giveLengthVolumeCenterCollisionAuto(Spatial auv, Vector3f start, boolean ignore_water_height){
        CollisionResults results = new CollisionResults();
        float ret = 0.0f;
        Vector3f ret2 = new Vector3f(0f,0f,0f);
        float ret3 = 0.0f;
        Vector3f ret4 = new Vector3f(0f,0f,0f);
        //get the depth of object
        Vector3f first = new Vector3f(0f,0f,0f);
        Vector3f second =new Vector3f(0f,0f,0f);
        Vector3f ray_start_up = new Vector3f(start.x,start.y,start.z);
        Vector3f ray_direction_up = new Vector3f(0.0f,1.0f,0.0f);
        // 2. Aim the ray from cam loc to cam direction.
        Ray ray_up = new Ray(ray_start_up, ray_direction_up);
        ray_up.setLimit(1000f);
        // 3. Collect intersections between Ray and Shootables in results list.
        //only collide with the spatial
        auv.collideWith(ray_up, results);
      //  System.out.println("=========================");
        //System.out.println("# " + results.size());

              /*  Sphere sphere4 = new Sphere(16, 16, 0.025f);
                Geometry mark4 = new Geometry("BOOM2!", new Arrow(Vector3f.UNIT_Y.mult(10)));
                Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mark_mat4.setColor("Color", ColorRGBA.White);
                mark4.setMaterial(mark_mat4);
                mark4.setLocalTranslation(ray_start_up);
                rootNode.attachChild(mark4);
*/
        //System.out.println("=========================");

        
        //water height for checking what is air and water volume
        /*float waterheight = 0;
        if(mars_settings.isSetupProjectedWavesWater()){
            waterheight = initer.getWhg().getHeight(start.x, start.z, mars.getTimer().getTimeInSeconds());
        }else{
            waterheight = physical_environment.getWater_height();
        }*/
        float waterheight = initer.getCurrentWaterHeight(start.x, start.z);
        
        boolean skip_inf = false;
        for (int i = 0; i < results.size(); i++) {
/*            Vector3f con = results.getCollision(i).getContactPoint();
            if(con.x == Vector3f.NAN.x || con.x == Vector3f.NEGATIVE_INFINITY.x || con.x == Vector3f.POSITIVE_INFINITY.x ){
                System.out.println("xinf: " + con);
            }
            if(con.y == Vector3f.NAN.y || con.y == Vector3f.NEGATIVE_INFINITY.y || con.y == Vector3f.POSITIVE_INFINITY.y ){
                System.out.println("yinf: " + con);
                results.clear();
                auv.collideWith(ray_up, results);
            }
            if(con.z == Vector3f.NAN.z || con.z == Vector3f.NEGATIVE_INFINITY.z || con.z == Vector3f.POSITIVE_INFINITY.z ){
                System.out.println("zinf: " + con);
            }*/
            
          if(i%2==0){//if "i" even then first else second(uneven)
            first = results.getCollision(i).getContactPoint();
            //System.out.println("f " + first);
            if(results.size()%2!=0){
                //System.out.println("NANX " + results.size());
                //System.out.println("f " + first);
                /*Sphere sphere4 = new Sphere(16, 16, 0.025f);
                Geometry mark4 = new Geometry("BOOM2!", new Arrow(Vector3f.UNIT_Y.mult(10)));
                Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mark_mat4.setColor("Color", ColorRGBA.White);
                mark4.setMaterial(mark_mat4);
                mark4.setLocalTranslation(ray_start_up);
                rootNode.attachChild(mark4);*/
            }
            if(first.y == Vector3f.POSITIVE_INFINITY.y){
                //System.out.println("NANY");
                /*Sphere sphere4 = new Sphere(16, 16, 0.025f);
                Geometry mark4 = new Geometry("BOOM2!", new Arrow(Vector3f.UNIT_Y.mult(10)));
                Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mark_mat4.setColor("Color", ColorRGBA.White);
                mark4.setMaterial(mark_mat4);
                mark4.setLocalTranslation(ray_start_up);
                rootNode.attachChild(mark4);*/
            }
            //if(first.y > waterheight/*physical_environment.getWater_height()*/ && !ignore_water_height){
            //    first.y = waterheight;//physical_environment.getWater_height();
            //    break;//because the water height is the end
            //}
            if(infinityCheck(first)){
                System.out.println("INFINITY CHECK!");
                skip_inf = true;
                break;
            }
          }else{
              if(!skip_inf){
                second = results.getCollision(i).getContactPoint();
                if(!infinityCheck(second)){
                    

                    if(second.y > waterheight/*physical_environment.getWater_height()*/ && !ignore_water_height){
                        //we need to calculate the volume + center above water height
                        float temp_overwater =  Math.abs((second.y)-(waterheight));
                        if(temp_overwater != 0.0f){
                            Vector3f temp3 = new Vector3f(first.x,waterheight + (temp_overwater/2f),first.z);
                            ret2 = calculateVolumeCentroid(ret2, temp3, ret3*physical_environment.getAir_density(), temp_overwater*physical_environment.getAir_density());
                            ret3 = ret3 + temp_overwater;
                        }

                        //caluclate under water
                        if(first.y <= waterheight){
                            second.y = waterheight;//physical_environment.getWater_height();
                            float temp =  Math.abs((first.y)-(second.y));
                            if(temp != 0.0f){
                                Vector3f temp2 = new Vector3f(first.x,first.y + (temp/2f),first.z);
                                ret2 = calculateVolumeCentroid(ret2, temp2, ret*physical_environment.getFluid_density(), temp*physical_environment.getFluid_density());
                                ret = ret + temp;
                            }
                        }    
                        break;//because the water height is the end, or finished
                    }

                    float temp =  Math.abs((first.y)-(second.y));
                    if(temp != 0.0f){
                        Vector3f temp2 = new Vector3f(first.x,first.y + (temp/2),first.z);
                        ret2 = calculateVolumeCentroid(ret2, temp2, ret*physical_environment.getFluid_density(), temp*physical_environment.getFluid_density());
                        ret = ret + temp;
                    }
                }else{
                    System.out.println("INFINITY CHECK!");
                }
            }
            skip_inf = false; 
            
          }
        }

        //System.out.println("HOEHE: " + ret);
        float[] arr_ret = new float[3];
        //System.out.println("ret " + ret);
        arr_ret[0] = ret;
        arr_ret[1] = ret2.y;
        arr_ret[2] = ret3;
        return arr_ret;
    }
    
    /*
     * gets us the volume and volume center of one bracket
     */
    @Deprecated
    private float[] giveLengthVolumeCenterCollision(Spatial auv, Vector3f start, boolean ignore_water_height){
        CollisionResults results = new CollisionResults();
        float ret = 0.0f;
        Vector3f ret2 = new Vector3f(0f,0f,0f);
        float ret3 = 0.0f;
        Vector3f ret4 = new Vector3f(0f,0f,0f);
        //get the depth of object
        float ray_distance = 5.0f;
        Vector3f first = new Vector3f(0f,0f,0f);
        Vector3f second =new Vector3f(0f,0f,0f);
        Vector3f ray_start_up = new Vector3f(start.x,start.y-ray_distance,start.z);
        Vector3f ray_direction_up = new Vector3f(0.0f,1.0f,0.0f);
        // 2. Aim the ray from cam loc to cam direction.
        Ray ray_up = new Ray(ray_start_up, ray_direction_up);
        ray_up.setLimit(1000f);
        // 3. Collect intersections between Ray and Shootables in results list.
        //only collide with the spatial
        auv.collideWith(ray_up, results);
      //  System.out.println("=========================");
        //System.out.println("# " + results.size());

              /*  Sphere sphere4 = new Sphere(16, 16, 0.025f);
                Geometry mark4 = new Geometry("BOOM2!", new Arrow(Vector3f.UNIT_Y.mult(10)));
                Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mark_mat4.setColor("Color", ColorRGBA.White);
                mark4.setMaterial(mark_mat4);
                mark4.setLocalTranslation(ray_start_up);
                rootNode.attachChild(mark4);
*/
        //System.out.println("=========================");

        
        //water height for checking what is air and water volume
        /*float waterheight = 0;
        if(mars_settings.isSetupProjectedWavesWater()){
            waterheight = initer.getWhg().getHeight(start.x, start.z, mars.getTimer().getTimeInSeconds());
        }else{
            waterheight = physical_environment.getWater_height();
        }*/
        float waterheight = initer.getCurrentWaterHeight(start.x, start.z);
        
        boolean skip_inf = false;
        for (int i = 0; i < results.size(); i++) {
/*            Vector3f con = results.getCollision(i).getContactPoint();
            if(con.x == Vector3f.NAN.x || con.x == Vector3f.NEGATIVE_INFINITY.x || con.x == Vector3f.POSITIVE_INFINITY.x ){
                System.out.println("xinf: " + con);
            }
            if(con.y == Vector3f.NAN.y || con.y == Vector3f.NEGATIVE_INFINITY.y || con.y == Vector3f.POSITIVE_INFINITY.y ){
                System.out.println("yinf: " + con);
                results.clear();
                auv.collideWith(ray_up, results);
            }
            if(con.z == Vector3f.NAN.z || con.z == Vector3f.NEGATIVE_INFINITY.z || con.z == Vector3f.POSITIVE_INFINITY.z ){
                System.out.println("zinf: " + con);
            }*/
            
          if(i%2==0){//if "i" even then first else second(uneven)
            first = results.getCollision(i).getContactPoint();
            //System.out.println("f " + first);
            if(results.size()%2!=0){
                //System.out.println("NANX " + results.size());
                //System.out.println("f " + first);
                /*Sphere sphere4 = new Sphere(16, 16, 0.025f);
                Geometry mark4 = new Geometry("BOOM2!", new Arrow(Vector3f.UNIT_Y.mult(10)));
                Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mark_mat4.setColor("Color", ColorRGBA.White);
                mark4.setMaterial(mark_mat4);
                mark4.setLocalTranslation(ray_start_up);
                rootNode.attachChild(mark4);*/
            }
            if(first.y == Vector3f.POSITIVE_INFINITY.y){
                //System.out.println("NANY");
                /*Sphere sphere4 = new Sphere(16, 16, 0.025f);
                Geometry mark4 = new Geometry("BOOM2!", new Arrow(Vector3f.UNIT_Y.mult(10)));
                Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                mark_mat4.setColor("Color", ColorRGBA.White);
                mark4.setMaterial(mark_mat4);
                mark4.setLocalTranslation(ray_start_up);
                rootNode.attachChild(mark4);*/
            }
            //if(first.y > waterheight/*physical_environment.getWater_height()*/ && !ignore_water_height){
            //    first.y = waterheight;//physical_environment.getWater_height();
            //    break;//because the water height is the end
            //}
            if(infinityCheck(first)){
                System.out.println("INFINITY CHECK!");
                skip_inf = true;
                break;
            }
          }else{
              if(!skip_inf){
                second = results.getCollision(i).getContactPoint();
                if(!infinityCheck(second)){
                    

                    if(second.y > waterheight/*physical_environment.getWater_height()*/ && !ignore_water_height){
                        //we need to calculate the volume + center above water height
                        float temp_overwater =  Math.abs((second.y)-(waterheight));
                        if(temp_overwater != 0.0f){
                            Vector3f temp3 = new Vector3f(first.x,waterheight + (temp_overwater/2f),first.z);
                            ret2 = calculateVolumeCentroid(ret2, temp3, ret3*physical_environment.getAir_density(), temp_overwater*physical_environment.getAir_density());
                            ret3 = ret3 + temp_overwater;
                        }

                        //caluclate under water
                        if(first.y <= waterheight){
                            second.y = waterheight;//physical_environment.getWater_height();
                            float temp =  Math.abs((first.y)-(second.y));
                            if(temp != 0.0f){
                                Vector3f temp2 = new Vector3f(first.x,first.y + (temp/2f),first.z);
                                ret2 = calculateVolumeCentroid(ret2, temp2, ret*physical_environment.getFluid_density(), temp*physical_environment.getFluid_density());
                                ret = ret + temp;
                            }
                        }    
                        break;//because the water height is the end, or finished
                    }

                    float temp =  Math.abs((first.y)-(second.y));
                    if(temp != 0.0f){
                        Vector3f temp2 = new Vector3f(first.x,first.y + (temp/2),first.z);
                        ret2 = calculateVolumeCentroid(ret2, temp2, ret*physical_environment.getFluid_density(), temp*physical_environment.getFluid_density());
                        ret = ret + temp;
                    }
                }else{
                    System.out.println("INFINITY CHECK!");
                }
            }
            skip_inf = false; 
            
          }
        }

        //System.out.println("HOEHE: " + ret);
        float[] arr_ret = new float[3];
        //System.out.println("ret " + ret);
        arr_ret[0] = ret;
        arr_ret[1] = ret2.y;
        arr_ret[2] = ret3;
        return arr_ret;
    }

    /*
     * Calculates the center of the volume. It's basicly the same like with normal
     * centroid calculation only that we assume that the volume "weights" everywhere the same.
     */
    private Vector3f calculateVolumeCentroid(Vector3f old_centroid, Vector3f new_centroid, float old_mass, float new_mass){
        float all_mass = old_mass + new_mass;
        Vector3f ret = new Vector3f((float)(((old_centroid.x*old_mass)+(new_centroid.x*new_mass))/(all_mass)),(float)(((old_centroid.y*old_mass)+(new_centroid.y*new_mass))/(all_mass)),(float)(((old_centroid.z*old_mass)+(new_centroid.z*new_mass))/(all_mass)));
        return ret;
    }
    
    private float[] calculateVolumeExcact(Spatial auv, boolean ignore_water_height){
        float[] arr_ret = new float[2];
        float volume = 0f;
        
        /*Vector3f v1 = new Vector3f(0f,0f,0f);
        Vector3f v2 = new Vector3f(2f,0f,0f);
        Vector3f v3 = new Vector3f(0f,2f,0f);
        Vector3f v4 = new Vector3f(0f,2f,0f);
        ArrayList<Vector3f> vecs = new ArrayList<Vector3f>();
        vecs.add(v1);
        vecs.add(v2);
        vecs.add(v3);
        //vecs.add(v4);
        vecs.add(v1);
        Vector3f nor = FastMath.computeNormal(v1, v2, v3);
        float area3D_Polygon = Helper.area3D_Polygon(vecs, nor);*/
        

        /*BoundingBox bb = (BoundingBox) auv.getWorldBound();
        Vector3f extent = bb.getExtent(null);
        float volume1 = bb.getVolume();
        Box box = new Box(bb.getXExtent(),bb.getYExtent(),bb.getZExtent());
        final Geometry boundingBoxGeom = new Geometry("TheMesh", box);
        boundingBoxGeom.setLocalTranslation(bb.getCenter());
        Mesh mesh = boundingBoxGeom.getMesh();*/
        Mesh mesh = boundingBoxGeom.getMesh();
        
        float waterheight = initer.getCurrentWaterHeight(auv.getWorldTranslation().x, auv.getWorldTranslation().z);
        Vector3f worldToLocal = boundingBox.worldToLocal(new Vector3f(0f, waterheight, 0f), null);
        Vector3f worldToLocalUnit = boundingBox.worldToLocal(Vector3f.UNIT_Y, null);
        waterheight = worldToLocal.y;
        ArrayList<Vector3f> polyline = new ArrayList<Vector3f>();
        
        for (int i = 0; i < mesh.getTriangleCount(); i++) {
            Triangle t = new Triangle();
            mesh.getTriangle(i, t);
            //System.out.println("triang" + i + ": " + t.get1() + " " + t.get2() + " " + t.get3());
            //float sign = Math.signum(t.get1().dot(t.getNormal()));
            Vector3f a = t.get1();
            Vector3f b = t.get2();
            Vector3f c = t.get3();
            if(a.y < waterheight && b.y < waterheight && c.y < waterheight){//if all vertex of the triangle are underwater we are safe, count them towards normal volume
                float volume_t = Helper.calculatePolyederVolume(a, b, c);
                volume = volume + volume_t;
            }else if(a.y >= waterheight && b.y >= waterheight && c.y >= waterheight){//if they are abouth water we can forget them because we need only the waterplane
                
            }else{//we have to check furhter, the triangel is above and under water
                if( (a.y < waterheight && b.y >= waterheight && c.y >= waterheight) ){//when one vertex is under water => case 1 (pretty triangle)
                    Vector3f intersectionWithPlaneB = Helper.getIntersectionWithPlaneCorrect(new Vector3f(0f, waterheight, 0f), worldToLocalUnit, a, b.subtract(a));
                    Vector3f intersectionWithPlaneC = Helper.getIntersectionWithPlaneCorrect(new Vector3f(0f, waterheight, 0f), worldToLocalUnit, a, c.subtract(a));
                    /*Triangle tN = new Triangle();
                    tN.set1(a);
                    tN.set2(intersectionWithPlaneB);
                    tN.set3(intersectionWithPlaneC);*/
                    float volume_t = Helper.calculatePolyederVolume(a, intersectionWithPlaneB, intersectionWithPlaneC);
                    volume = volume + volume_t;
                    //dont forget to add the new vertex to the polylist for later triangulation
                    //polyline.add(intersectionWithPlaneB);
                    //polyline.add(intersectionWithPlaneC);
                }else if( (a.y >= waterheight && b.y < waterheight && c.y >= waterheight) ){
                    Vector3f intersectionWithPlaneA = Helper.getIntersectionWithPlaneCorrect(new Vector3f(0f, waterheight, 0f), worldToLocalUnit.normalize(), b, a.subtract(b));
                    Vector3f intersectionWithPlaneC = Helper.getIntersectionWithPlaneCorrect(new Vector3f(0f, waterheight, 0f), worldToLocalUnit.normalize(), b, c.subtract(b));
                    /*Triangle tN = new Triangle();
                    tN.set1(a);
                    tN.set2(intersectionWithPlaneB);
                    tN.set3(intersectionWithPlaneC);*/
                    float volume_t = Helper.calculatePolyederVolume(intersectionWithPlaneA, b, intersectionWithPlaneC);
                    volume = volume + volume_t;
                    //polyline.add(intersectionWithPlaneA);
                    //polyline.add(intersectionWithPlaneC);
                }else if( (a.y >= waterheight && b.y >= waterheight && c.y < waterheight) ){
                    Vector3f intersectionWithPlaneB = Helper.getIntersectionWithPlaneCorrect(new Vector3f(0f, waterheight, 0f), worldToLocalUnit.normalize(), c, b.subtract(c));
                    Vector3f intersectionWithPlaneA = Helper.getIntersectionWithPlaneCorrect(new Vector3f(0f, waterheight, 0f), worldToLocalUnit.normalize(), c, a.subtract(c));
                    /*Triangle tN = new Triangle();
                    tN.set1(a);
                    tN.set2(intersectionWithPlaneB);
                    tN.set3(intersectionWithPlaneC);*/
                    float volume_t = Helper.calculatePolyederVolume(intersectionWithPlaneA, intersectionWithPlaneB, c);
                    volume = volume + volume_t;
                    polyline.add(intersectionWithPlaneA);
                    polyline.add(intersectionWithPlaneB);
                }else{//when two vertex are under water => case2 (make two triangles)
                    if(a.y >= waterheight){//check which vertex is above water, the other ones must be under water due to the check above
                        //we have now to produce 2 triangles
                        //but first check the intersections
                        Vector3f intersectionWithPlaneB = Helper.getIntersectionWithPlaneCorrect(new Vector3f(0f, waterheight, 0f), worldToLocalUnit.normalize(), a, b.subtract(a));
                        Vector3f intersectionWithPlaneC = Helper.getIntersectionWithPlaneCorrect(new Vector3f(0f, waterheight, 0f), worldToLocalUnit.normalize(), a, c.subtract(a));
                        
                        //now produce the two triangles
                        float volume_t = Helper.calculatePolyederVolume(intersectionWithPlaneB, b, c);
                        volume = volume + volume_t;
                        volume_t = Helper.calculatePolyederVolume(intersectionWithPlaneB, c, intersectionWithPlaneC);
                        volume = volume + volume_t;
                        
                        //dont forget to add the new vertex to the polylist for later triangulation
                        polyline.add(intersectionWithPlaneB);
                        polyline.add(intersectionWithPlaneC);
                    }else if(b.y >= waterheight){
                        //we have now to produce 2 triangles
                        //but first check the intersections
                        Vector3f intersectionWithPlaneA = Helper.getIntersectionWithPlaneCorrect(new Vector3f(0f, waterheight, 0f), worldToLocalUnit.normalize(), b, a.subtract(b));
                        Vector3f intersectionWithPlaneC = Helper.getIntersectionWithPlaneCorrect(new Vector3f(0f, waterheight, 0f), worldToLocalUnit.normalize(), b, c.subtract(b));
                        
                        //now produce the two triangles
                        float volume_t = Helper.calculatePolyederVolume(a, intersectionWithPlaneA, intersectionWithPlaneC);
                        volume = volume + volume_t;
                        volume_t = Helper.calculatePolyederVolume(a, intersectionWithPlaneC, c);
                        volume = volume + volume_t;
                        
                        //dont forget to add the new vertex to the polylist for later triangulation
                        polyline.add(intersectionWithPlaneA);
                        polyline.add(intersectionWithPlaneC);
                    }else if(c.y >= waterheight){
                        //we have now to produce 2 triangles
                        //but first check the intersections
                        Vector3f intersectionWithPlaneB = Helper.getIntersectionWithPlaneCorrect(new Vector3f(0f, waterheight, 0f), worldToLocalUnit.normalize(), c, b.subtract(c));
                        Vector3f intersectionWithPlaneA = Helper.getIntersectionWithPlaneCorrect(new Vector3f(0f, waterheight, 0f), worldToLocalUnit.normalize(), c, a.subtract(c));
                        
                        //now produce the two triangles
                        float volume_t = Helper.calculatePolyederVolume(a, intersectionWithPlaneA, intersectionWithPlaneB);
                        volume = volume + volume_t;
                        volume_t = Helper.calculatePolyederVolume(a, intersectionWithPlaneB, b);
                        volume = volume + volume_t;
                        
                        //dont forget to add the new vertex to the polylist for later triangulation
                        polyline.add(intersectionWithPlaneA);
                        polyline.add(intersectionWithPlaneB);
                    }
                }
            }
            
        }
        
        /*//first we need the area of the polygon
        polyline.add(polyline.get(0));//make sure everything is set for the method Helper.area3D_Polygon
        Vector3f nor = FastMath.computeNormal(polyline.get(0),polyline.get(1), polyline.get(3));//minimum number of vertixes from a slice of a 3d box is 6, due to the code above
        
        float area3D_Polygon = Helper.area3D_Polygon(polyline, nor);*/
        
        //we calculated the volume of the cut but we forgot the cutting plane
        //since we stored all cutting points (always a pair) we can "triangulize" the cutting plane
        //by taking a point in the middle of the konvex hull as a origin from which we can triangulize
        if(!polyline.isEmpty()){//it could be that the auv is completely above water
            Vector3f v1 = polyline.get(0);
            Vector3f v2 = polyline.get(5);
            Vector3f origin = v1.add((v2.subtract(v1)).mult(0.5f));
            float sign = Math.signum(origin.dot(worldToLocalUnit));//its always up direction because its a clean cut in the x,z plane
            for (int i = 0; i < polyline.size(); i=i+2) {
                Vector3f vec1 = polyline.get(i);
                Vector3f vec2 = polyline.get(i+1);
                float volume_t = Helper.calculatePolyederVolume(origin, vec1, vec2, sign);
                volume = volume + volume_t;
            }
        }
        
        //debug polyline
        /*for (int i = 0; i < polyline.size(); i=i+2) {
            
            
            Vector3f vec1 = polyline.get(i);
            Vector3f vec2 = polyline.get(i+1);
            
            final Geometry line = new Geometry("tedt", new Line(boundingBoxGeom.localToWorld(vec1, null), boundingBoxGeom.localToWorld(vec2, null)));
            Material mark_mat9 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mark_mat9.setColor("Color", ColorRGBA.Red);
            line.setMaterial(mark_mat9);
            Future simStateFutureView = mars.enqueue(new Callable() {
                    public Void call() throws Exception {
                        rootNode.attachChild(line);
                        return null;
                    }
            });
        }*/
        
        /*Future simStateFutureView = mars.enqueue(new Callable() {
                    public Void call() throws Exception {
                        WireBox wbx = new WireBox();
                        wbx.fromBoundingBox((BoundingBox)boundingBoxGeom.getWorldBound());
                        Geometry boundingBox2 = new Geometry("TheMesh", wbx);
                        Material mat_box = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                        mat_box.setColor("Color", ColorRGBA.Blue);
                        boundingBox2.setMaterial(mat_box);
                        rootNode.attachChild(boundingBox2);
                        return null;
                    }
        });*/

        arr_ret[0] = volume;
        System.out.println("volume: " + volume);
        arr_ret[1] = bbVolume - volume;
        return arr_ret;
    }
    
    /*
     * Calculates the volume of the auv. Uses a ray-based approach. The rays are fired in a equidistant ciruclar pattern.
     */
    private float[] calculateVolumeAutoRound(Spatial auv, float resolution, boolean ignore_water_height){
        float[] arr_ret = new float[2];
        float calc_volume = 0.0f;
        float calc_volume_air = 0.0f;
        float old_volume_mass = 0.0f;
        BoundingBox boundBox = (BoundingBox)auv.getWorldBound();
        Vector3f centerBB = boundBox.getCenter();
        Vector3f extBB = boundBox.getExtent(null);
        //System.out.println("centerBB: " + centerBB + " " + "extBB: " + extBB + " " + "maxBB: " + extBB.length() + "/" + extBB.lengthSquared());
        int resolutionCounter = (int)Math.ceil(extBB.length()/resolution);
        //System.out.println("resolutionCounter: " + resolutionCounter);
        Vector3f volume_center = new Vector3f(0f,0f,0f);
        //System.out.println("boundingBox.getWorldTranslation(): " + boundingBox.getWorldTranslation());
        //System.out.println("auv_node.getWorldTranslation(): " + auv_node.getWorldTranslation());
        Vector3f ray_start = new Vector3f(boundingBox.getWorldTranslation().x,boundingBox.getWorldTranslation().y,boundingBox.getWorldTranslation().z);

        
        float radius = extBB.length();
        
        for (int i = -resolutionCounter; i < resolutionCounter; i++) {
            float heightOfSegment = radius-Math.abs(resolution*i);
            float alpha = 2f * (float)Math.acos(1f-(heightOfSegment/radius));
            float chord = 2f*radius*(float)Math.sin(alpha/2f);
            float chordStart = chord/2f;
            //System.out.println("chordStart: " + chordStart);
            float resolutionLengthCounter = (int)Math.rint(chord/resolution);
            for (int j = 0; j < resolutionLengthCounter; j++) {
                Vector3f ray_start_new = new Vector3f((float)(ray_start.x+(i*resolution)),(float)(ray_start.y)-extBB.length()-0.1f,(float)(ray_start.z+(j*resolution)-chordStart));
                float length = 0.0f;
                float length_air = 0.0f;
                float volume_center_y = 0.0f;
                float[] ret_arr = giveLengthVolumeCenterCollisionAuto(auv,ray_start_new,ignore_water_height);
                length = ret_arr[0];
                length_air = ret_arr[2];
                /*if(length == Float.POSITIVE_INFINITY){
                    System.out.println("inf: " + length);
                }
                if(length_air == Float.POSITIVE_INFINITY){
                    System.out.println("inf air: " + length_air);
                }*/
               // System.out.println("length: " + length);
                if( length != 0){
                    calc_volume = calc_volume + (length*resolution*resolution);
                    volume_center_y = ret_arr[1];
                    //System.out.println(i + ":" + j + ":p: " + volume_center + " " + length + " " + old_volume_mass);
                   // System.out.println(i + ":" + j + ":p: " + old_volume_mass);
                    volume_center = calculateVolumeCentroid(volume_center,new Vector3f(ray_start_new.x,volume_center_y,ray_start_new.z),old_volume_mass,(length*resolution*resolution));
                    //System.out.println(i + ":" + j + ":p: " + volume_center + " " + length + " " + old_volume_mass);
                    old_volume_mass = old_volume_mass + (length*resolution*resolution);

                    if(auv_param.isDebugBuoycancy()){
                        Sphere sphere4 = new Sphere(16, 16, 0.00125f);
                        Geometry mark4 = new Geometry("BOOM2!", sphere4);
                        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                        mark_mat4.setColor("Color", ColorRGBA.Green);
                        mark4.setMaterial(mark_mat4);
                        mark4.setLocalTranslation(volume_center);
                        rootNode.attachChild(mark4);
                    }
                }
                
                if( length_air != 0){
                    calc_volume_air = calc_volume_air + (length_air*resolution*resolution);
                    //volume_center_y = ret_arr[1];
                    //System.out.println(i + ":" + j + ":p: " + volume_center + " " + length + " " + old_volume_mass);
                   // System.out.println(i + ":" + j + ":p: " + old_volume_mass);
                    //volume_center = calculateVolumeCentroid(volume_center,new Vector3f(ray_start_new.x,volume_center_y,ray_start_new.z),old_volume_mass,(length*resolution*resolution));
                    //System.out.println(i + ":" + j + ":p: " + volume_center + " " + length + " " + old_volume_mass);
                    //old_volume_mass = old_volume_mass + (length*resolution*resolution);

                    /*if(auv_param.isDebugBuoycancy()){
                        Sphere sphere4 = new Sphere(16, 16, 0.00125f);
                        Geometry mark4 = new Geometry("BOOM2!", sphere4);
                        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                        mark_mat4.setColor("Color", ColorRGBA.Green);
                        mark4.setMaterial(mark_mat4);
                        mark4.setLocalTranslation(volume_center);
                        rootNode.attachChild(mark4);
                    }*/
                }

                if(auv_param.isDebugBuoycancy()){
                        Sphere sphere3 = new Sphere(16, 16, 0.0125f);
                        Geometry mark3 = new Geometry("BOOM!", sphere3);
                        Material mark_mat3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                        if(length!=0.0f){
                            mark_mat3.setColor("Color", ColorRGBA.Red);
                        }else{
                            mark_mat3.setColor("Color", ColorRGBA.Blue);
                        }
                        mark3.setMaterial(mark_mat3);
                        mark3.setLocalTranslation(ray_start_new);
                        auv_node.attachChild(mark3);
                }
            }
        }
        
  /*      final Vector3f volume_center_fin = volume_center.clone();
        //final Vector3f volume_center_fin = Vector3f.ZERO;
        final Vector3f volume_center_precise_fin = volume_center_precise.clone();
        Future fut = mars.enqueue(new Callable() {
                public Void call() throws Exception {
                    Vector3f volume_center_local = new Vector3f(0f,0f,0f);
                    auv_node.worldToLocal(volume_center_fin, volume_center_local);//NPE!!!!!!!!????????, when update rate = 2

                    final Vector3f in = volume_center_local.clone();
                    VolumeCenterGeom.setLocalTranslation(in);
                    VolumeCenterGeom.updateGeometricState();


                    if( VolumeCenterPreciseGeom.getWorldTranslation().equals(volume_center_precise_fin) ){//save the precise only once

                    VolumeCenterPreciseGeom.setLocalTranslation(in);
                    VolumeCenterPreciseGeom.updateGeometricState();

                    }

                    return null;
                }
        });*/
        
        
            Vector3f volume_center_local = new Vector3f(0f,0f,0f);
            try {
                auv_node.worldToLocal(volume_center, volume_center_local);//NPE!!!!!!!!????????, when update rate = 2
            } catch (Exception e) {
                System.out.println("NPE");
            }
            //auv_node.worldToLocal(volume_center, volume_center_local);//NPE!!!!!!!!????????, when update rate = 2

            //addValueToSeries( VolumeCenterPreciseGeom.getWorldTranslation().subtract(volume_center).y, 1);

            final Vector3f in = volume_center_local.clone();
            Future fut = mars.enqueue(new Callable() {
                        public Void call() throws Exception {
                            VolumeCenterGeom.setLocalTranslation(in);
                            VolumeCenterGeom.updateGeometricState();
                            return null;
                        }
                        });

            if( VolumeCenterPreciseGeom.getWorldTranslation().equals(this.volume_center_precise) ){//save the precise only once
                        Future fut2 = mars.enqueue(new Callable() {
                        public Void call() throws Exception {
                            VolumeCenterPreciseGeom.setLocalTranslation(in);
                            VolumeCenterPreciseGeom.updateGeometricState();
                            return null;
                        }
                        });
            }
         

        //return calc_volume;
        arr_ret[0] = calc_volume;
        arr_ret[1] = calc_volume_air;
        return arr_ret;
    }
    
     /*
     * Calculates the volume of the auv.
     * 
     *  |
     *  *
     *  |
     *  *
     *  |
     *  *
     * 
     */
    @Deprecated
    private float[] calculateVolumeAuto(Spatial auv, float resolution, boolean ignore_water_height){
        float[] arr_ret = new float[2];
        float calc_volume = 0.0f;
        float calc_volume_air = 0.0f;
        float old_volume_mass = 0.0f;
        BoundingBox boundBox = (BoundingBox)auv.getWorldBound();
        Vector3f centerBB = boundBox.getCenter();
        Vector3f extBB = boundBox.getExtent(null);
        System.out.println("centerBB: " + centerBB + " " + "extBB: " + extBB + " " + "maxBB: " + extBB.length() + "/" + extBB.lengthSquared());
        int resolutionCounter = (int)Math.rint(extBB.length()/resolution);
        System.out.println("resolutionCounter: " + resolutionCounter);
        Vector3f volume_center = new Vector3f(0f,0f,0f);
        System.out.println("boundingBox.getWorldTranslation(): " + boundingBox.getWorldTranslation());
        System.out.println("auv_node.getWorldTranslation(): " + auv_node.getWorldTranslation());
        
        Vector3f ray_start = new Vector3f(boundingBox.getWorldTranslation().x,boundingBox.getWorldTranslation().y,boundingBox.getWorldTranslation().z);

        for (int i = -resolutionCounter; i < resolutionCounter; i++) {
            for (int j = -resolutionCounter; j < resolutionCounter; j++) {
                Vector3f ray_start_new = new Vector3f((float)(ray_start.x+(i*resolution)),(float)(ray_start.y)-extBB.length()-0.1f,(float)(ray_start.z+(j*resolution)));
                float length = 0.0f;
                float length_air = 0.0f;
                float volume_center_y = 0.0f;
                float[] ret_arr = giveLengthVolumeCenterCollisionAuto(auv,ray_start_new,ignore_water_height);
                length = ret_arr[0];
                length_air = ret_arr[2];
                /*if(length == Float.POSITIVE_INFINITY){
                    System.out.println("inf: " + length);
                }
                if(length_air == Float.POSITIVE_INFINITY){
                    System.out.println("inf air: " + length_air);
                }*/
               // System.out.println("length: " + length);
                if( length != 0){
                    calc_volume = calc_volume + (length*resolution*resolution);
                    volume_center_y = ret_arr[1];
                    //System.out.println(i + ":" + j + ":p: " + volume_center + " " + length + " " + old_volume_mass);
                   // System.out.println(i + ":" + j + ":p: " + old_volume_mass);
                    volume_center = calculateVolumeCentroid(volume_center,new Vector3f(ray_start_new.x,volume_center_y,ray_start_new.z),old_volume_mass,(length*resolution*resolution));
                    //System.out.println(i + ":" + j + ":p: " + volume_center + " " + length + " " + old_volume_mass);
                    old_volume_mass = old_volume_mass + (length*resolution*resolution);

                    if(auv_param.isDebugBuoycancy()){
                        Sphere sphere4 = new Sphere(16, 16, 0.00125f);
                        Geometry mark4 = new Geometry("BOOM2!", sphere4);
                        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                        mark_mat4.setColor("Color", ColorRGBA.Green);
                        mark4.setMaterial(mark_mat4);
                        mark4.setLocalTranslation(volume_center);
                        rootNode.attachChild(mark4);
                    }
                }
                
                if( length_air != 0){
                    calc_volume_air = calc_volume_air + (length_air*resolution*resolution);
                    //volume_center_y = ret_arr[1];
                    //System.out.println(i + ":" + j + ":p: " + volume_center + " " + length + " " + old_volume_mass);
                   // System.out.println(i + ":" + j + ":p: " + old_volume_mass);
                    //volume_center = calculateVolumeCentroid(volume_center,new Vector3f(ray_start_new.x,volume_center_y,ray_start_new.z),old_volume_mass,(length*resolution*resolution));
                    //System.out.println(i + ":" + j + ":p: " + volume_center + " " + length + " " + old_volume_mass);
                    //old_volume_mass = old_volume_mass + (length*resolution*resolution);

                    /*if(auv_param.isDebugBuoycancy()){
                        Sphere sphere4 = new Sphere(16, 16, 0.00125f);
                        Geometry mark4 = new Geometry("BOOM2!", sphere4);
                        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                        mark_mat4.setColor("Color", ColorRGBA.Green);
                        mark4.setMaterial(mark_mat4);
                        mark4.setLocalTranslation(volume_center);
                        rootNode.attachChild(mark4);
                    }*/
                }

                if(auv_param.isDebugBuoycancy()){
                        Sphere sphere3 = new Sphere(16, 16, 0.0125f);
                        Geometry mark3 = new Geometry("BOOM!", sphere3);
                        Material mark_mat3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                        if(length!=0.0f){
                            mark_mat3.setColor("Color", ColorRGBA.Red);
                        }else{
                            mark_mat3.setColor("Color", ColorRGBA.Blue);
                        }
                        mark3.setMaterial(mark_mat3);
                        mark3.setLocalTranslation(ray_start_new);
                        auv_node.attachChild(mark3);
                }
            }
        }
        
  /*      final Vector3f volume_center_fin = volume_center.clone();
        //final Vector3f volume_center_fin = Vector3f.ZERO;
        final Vector3f volume_center_precise_fin = volume_center_precise.clone();
        Future fut = mars.enqueue(new Callable() {
                public Void call() throws Exception {
                    Vector3f volume_center_local = new Vector3f(0f,0f,0f);
                    auv_node.worldToLocal(volume_center_fin, volume_center_local);//NPE!!!!!!!!????????, when update rate = 2

                    final Vector3f in = volume_center_local.clone();
                    VolumeCenterGeom.setLocalTranslation(in);
                    VolumeCenterGeom.updateGeometricState();


                    if( VolumeCenterPreciseGeom.getWorldTranslation().equals(volume_center_precise_fin) ){//save the precise only once

                    VolumeCenterPreciseGeom.setLocalTranslation(in);
                    VolumeCenterPreciseGeom.updateGeometricState();

                    }

                    return null;
                }
        });*/
        
        
            Vector3f volume_center_local = new Vector3f(0f,0f,0f);
            try {
                auv_node.worldToLocal(volume_center, volume_center_local);//NPE!!!!!!!!????????, when update rate = 2
            } catch (Exception e) {
                System.out.println("NPE");
            }
            //auv_node.worldToLocal(volume_center, volume_center_local);//NPE!!!!!!!!????????, when update rate = 2

            //addValueToSeries( VolumeCenterPreciseGeom.getWorldTranslation().subtract(volume_center).y, 1);

            final Vector3f in = volume_center_local.clone();
            Future fut = mars.enqueue(new Callable() {
                        public Void call() throws Exception {
                            VolumeCenterGeom.setLocalTranslation(in);
                            VolumeCenterGeom.updateGeometricState();
                            return null;
                        }
                        });

            if( VolumeCenterPreciseGeom.getWorldTranslation().equals(this.volume_center_precise) ){//save the precise only once
                        Future fut2 = mars.enqueue(new Callable() {
                        public Void call() throws Exception {
                            VolumeCenterPreciseGeom.setLocalTranslation(in);
                            VolumeCenterPreciseGeom.updateGeometricState();
                            return null;
                        }
                        });
            }
         

        //return calc_volume;
        arr_ret[0] = calc_volume;
        arr_ret[1] = calc_volume_air;
        return arr_ret;
    }

     /*
     * Calculates the volume of the auv.
     */
    @Deprecated
    private float[] calculateVolume(Spatial auv, float resolution, int x_length, int y_width, boolean ignore_water_height){
        float[] arr_ret = new float[2];
        float ray_distance_x = 0.45f;//0.45f;
        float ray_distance_z = 0.5f;
        float calc_volume = 0.0f;
        float calc_volume_air = 0.0f;
        float shift = 0.01f;
        float old_volume_mass = 0.0f;
        BoundingBox boundBox = (BoundingBox)auv.getWorldBound();
        Vector3f centerBB = boundBox.getCenter();
        Vector3f extBB = boundBox.getExtent(null);
        System.out.println("centerBB: " + centerBB + " " + "extBB: " + extBB);
        Vector3f volume_center = new Vector3f(0f,0f,0f);
        Vector3f ray_start = new Vector3f(OldCenterGeom.getWorldTranslation().x-ray_distance_x+(shift),OldCenterGeom.getWorldTranslation().y,OldCenterGeom.getWorldTranslation().z-ray_distance_z+(shift));

        for (int i = 1; i < x_length; i++) {
            for (int j = 1; j < y_width; j++) {
                Vector3f ray_start_new = new Vector3f((float)(ray_start.x+(i*resolution)),(float)(ray_start.y),(float)(ray_start.z+(j*resolution)));
                float length = 0.0f;
                float length_air = 0.0f;
                float volume_center_y = 0.0f;
               // System.out.println("=========================");
                float[] ret_arr = giveLengthVolumeCenterCollision(auv,ray_start_new,ignore_water_height);
                length = ret_arr[0];
                length_air = ret_arr[2];
                /*if(length == Float.POSITIVE_INFINITY){
                    System.out.println("inf: " + length);
                }
                if(length_air == Float.POSITIVE_INFINITY){
                    System.out.println("inf air: " + length_air);
                }*/
               // System.out.println("length: " + length);
                if( length != 0){
                    calc_volume = calc_volume + (length*resolution*resolution);
                    volume_center_y = ret_arr[1];
                    //System.out.println(i + ":" + j + ":p: " + volume_center + " " + length + " " + old_volume_mass);
                   // System.out.println(i + ":" + j + ":p: " + old_volume_mass);
                    volume_center = calculateVolumeCentroid(volume_center,new Vector3f(ray_start_new.x,volume_center_y,ray_start_new.z),old_volume_mass,(length*resolution*resolution));
                    //System.out.println(i + ":" + j + ":p: " + volume_center + " " + length + " " + old_volume_mass);
                    old_volume_mass = old_volume_mass + (length*resolution*resolution);

                    if(auv_param.isDebugBuoycancy()){
                        Sphere sphere4 = new Sphere(16, 16, 0.00125f);
                        Geometry mark4 = new Geometry("BOOM2!", sphere4);
                        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                        mark_mat4.setColor("Color", ColorRGBA.Green);
                        mark4.setMaterial(mark_mat4);
                        mark4.setLocalTranslation(volume_center);
                        rootNode.attachChild(mark4);
                    }
                }
                
                if( length_air != 0){
                    calc_volume_air = calc_volume_air + (length_air*resolution*resolution);
                    //volume_center_y = ret_arr[1];
                    //System.out.println(i + ":" + j + ":p: " + volume_center + " " + length + " " + old_volume_mass);
                   // System.out.println(i + ":" + j + ":p: " + old_volume_mass);
                    //volume_center = calculateVolumeCentroid(volume_center,new Vector3f(ray_start_new.x,volume_center_y,ray_start_new.z),old_volume_mass,(length*resolution*resolution));
                    //System.out.println(i + ":" + j + ":p: " + volume_center + " " + length + " " + old_volume_mass);
                    //old_volume_mass = old_volume_mass + (length*resolution*resolution);

                    /*if(auv_param.isDebugBuoycancy()){
                        Sphere sphere4 = new Sphere(16, 16, 0.00125f);
                        Geometry mark4 = new Geometry("BOOM2!", sphere4);
                        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                        mark_mat4.setColor("Color", ColorRGBA.Green);
                        mark4.setMaterial(mark_mat4);
                        mark4.setLocalTranslation(volume_center);
                        rootNode.attachChild(mark4);
                    }*/
                }

                if(auv_param.isDebugBuoycancy()){
                        Sphere sphere3 = new Sphere(16, 16, 0.0125f);
                        Geometry mark3 = new Geometry("BOOM!", sphere3);
                        Material mark_mat3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                        if(length!=0.0f){
                            mark_mat3.setColor("Color", ColorRGBA.Red);
                        }else{
                            mark_mat3.setColor("Color", ColorRGBA.Blue);
                        }
                        mark3.setMaterial(mark_mat3);
                        mark3.setLocalTranslation(ray_start_new);
                        auv_node.attachChild(mark3);
                }
            }
        }
        
  /*      final Vector3f volume_center_fin = volume_center.clone();
        //final Vector3f volume_center_fin = Vector3f.ZERO;
        final Vector3f volume_center_precise_fin = volume_center_precise.clone();
        Future fut = mars.enqueue(new Callable() {
                public Void call() throws Exception {
                    Vector3f volume_center_local = new Vector3f(0f,0f,0f);
                    auv_node.worldToLocal(volume_center_fin, volume_center_local);//NPE!!!!!!!!????????, when update rate = 2

                    final Vector3f in = volume_center_local.clone();
                    VolumeCenterGeom.setLocalTranslation(in);
                    VolumeCenterGeom.updateGeometricState();


                    if( VolumeCenterPreciseGeom.getWorldTranslation().equals(volume_center_precise_fin) ){//save the precise only once

                    VolumeCenterPreciseGeom.setLocalTranslation(in);
                    VolumeCenterPreciseGeom.updateGeometricState();

                    }

                    return null;
                }
        });*/
        
        
            Vector3f volume_center_local = new Vector3f(0f,0f,0f);
            try {
                auv_node.worldToLocal(volume_center, volume_center_local);//NPE!!!!!!!!????????, when update rate = 2
            } catch (Exception e) {
                System.out.println("NPE");
            }
            //auv_node.worldToLocal(volume_center, volume_center_local);//NPE!!!!!!!!????????, when update rate = 2

            //addValueToSeries( VolumeCenterPreciseGeom.getWorldTranslation().subtract(volume_center).y, 1);

            final Vector3f in = volume_center_local.clone();
            Future fut = mars.enqueue(new Callable() {
                        public Void call() throws Exception {
                            VolumeCenterGeom.setLocalTranslation(in);
                            VolumeCenterGeom.updateGeometricState();
                            return null;
                        }
                        });

            if( VolumeCenterPreciseGeom.getWorldTranslation().equals(this.volume_center_precise) ){//save the precise only once
                        Future fut2 = mars.enqueue(new Callable() {
                        public Void call() throws Exception {
                            VolumeCenterPreciseGeom.setLocalTranslation(in);
                            VolumeCenterPreciseGeom.updateGeometricState();
                            return null;
                        }
                        });
            }
         

        //return calc_volume;
        arr_ret[0] = calc_volume;
        arr_ret[1] = calc_volume_air;
        return arr_ret;
    }

    public void updateWaypoints(float tpf){
        if(auv_param.isWaypoints_enabled() && WayPoints != null){
            WayPoints.incTime(tpf);
            if(WayPoints.getTime() >= auv_param.getWaypoints_updaterate()){
                WayPoints.clearTime();
                WayPoints.addWaypoint(getMassCenterGeom().getWorldTranslation().clone());
                if(auv_param.isWaypoints_gradient()){
                    WayPoints.updateGradient();
                }
            }
        }
    }
    
    /**
     * 
     */
    public void updatePhysicalValues(){
        physicalvalues.updateAngularVelocity(physics_control.getAngularVelocity().length());
        physicalvalues.updateVelocity(physics_control.getLinearVelocity().length());
        physicalvalues.updateDepth(physics_control.getPhysicsLocation().getY());
    }

    /**
     *
     * @param tpf
     * @deprecated 
     */
    @Deprecated
    public void updateValues(float tpf){
        /*physicalvalues.incTime(tpf);
        if(physicalvalues.getTime() >= auv_param.getPhysicalvalues_updaterate() && auv_param.getPhysicalvalues_updaterate() > 0.0f){
            physicalvalues.clearTime();
            physicalvalues.setVelocity(String.valueOf(physics_control.getLinearVelocity().length()));
            view.updatePhysicalValues(getName(),"velocity",physicalvalues.getVelocity());
            physicalvalues.setPosition(String.valueOf(MassCenterGeom.getWorldTranslation()));
            view.updatePhysicalValues(getName(),"position",physicalvalues.getPosition());
            physicalvalues.setAngularVelocity(String.valueOf(physics_control.getAngularVelocity().length()));
            view.updatePhysicalValues(getName(),"angular_velocity",physicalvalues.getAngularVelocity());
            physicalvalues.setVolume(String.valueOf(this.actual_vol));
            view.updatePhysicalValues(getName(),"volume",physicalvalues.getVolume());
            Matrix3f matrix = Matrix3f.ZERO;
            physics_control.getPhysicsRotationMatrix(matrix);
            //physics_control.getPhysicsRotation(matrix);
            Quaternion q_rot = new Quaternion();
            q_rot.fromRotationMatrix(matrix);
            physicalvalues.setRotation(String.valueOf(new Vector3f(q_rot.getX(),q_rot.getY(),q_rot.getZ())));
            view.updatePhysicalValues(getName(),"rotation",physicalvalues.getRotation());
        }*/
    }

    /**
     * 
     */
    @Override
    public void publishSensorsOfAUV(){
        for ( String elem : sensors.keySet() ){
            Sensor element = (Sensor)sensors.get(elem);
            element.publishUpdate();
        }
    }
    
    /**
     * 
     */
    @Override
    public void publishActuatorsOfAUV(){
        for ( String elem : actuators.keySet() ){
            Actuator element = (Actuator)actuators.get(elem);
            element.publishUpdate();
        }
    }
    
    /**
     *
     * @return
     */
    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public String toString(){
        return getName();
    }

    private void resetAllActuators(){
        for ( String elem : actuators.keySet() ){
            Actuator element = (Actuator)actuators.get(elem);
            element.reset();
        }
    }

    private void resetAllSensors(){
        for ( String elem : sensors.keySet() ){
            Sensor element = (Sensor)sensors.get(elem);
            element.reset();
        }
    }
    
    private void resetAllAccumulators(){
        for ( String elem : accumulators.keySet() ){
            Accumulator element = (Accumulator)accumulators.get(elem);
            element.reset();
        }
    }

    /**
     *
     * @param simstate 
     */
    @Override
    public void setState(SimState simstate) {
        this.simstate = simstate;
        this.mars = simstate.getMARS();
        this.assetManager = simstate.getAssetManager();
        this.renderer = mars.getRenderer();
        this.renderManager = mars.getRenderManager();
        this.view = mars.getView();
        this.rootNode = simstate.getRootNode();
        this.physicalvalues = new PhysicalValues();
        this.initer = simstate.getIniter();
    }

    /**
     *
     * @return
     */
    @Override
    public CollisionShape getCollisionShape() {
        return collisionShape;
    }

    /**
     *
     * @param collisionShape
     */
    public void setCollisionShape(CollisionShape collisionShape) {
        this.collisionShape = collisionShape;
    }

    public void initialize(RenderManager rm, ViewPort vp) {
        
    }

    public void reshape(ViewPort vp, int w, int h) {
        
    }

    public boolean isInitialized() {
        return true;
    }

    public void preFrame(float tpf) {
        if(drag_updaterate == 1){
            drag_area_temp = updateImageContents();
        }
    }

    public void postQueue(RenderQueue rq) {
    }

    public void postFrame(FrameBuffer out) {
    }

    public void cleanup() {
        
    }
    
    /**
     * 
     * @param selected
     */
    @Override
    public void setSelected(boolean selected){
        if(selected && this.selected==false){
            if(mars_settings.isAmbientSelection()){
                ambient_light.setColor(mars_settings.getSelectionColor());
                selectionNode.addLight(ambient_light);
            }
            if(mars_settings.isGlowSelection()){
                /*Material mat_white = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
                mat_white.setColor("GlowColor", ColorRGBA.Blue); 
                auv_spatial.setMaterial(mat_white);*/
                setGlowColor(ghost_auv_spatial,mars_settings.getSelectionColor());
            }
        }else if(selected == false){
            selectionNode.removeLight(ambient_light);
            setGlowColor(ghost_auv_spatial,ColorRGBA.Black);
        }
        this.selected = selected;
    }
    
    private void setGlowColor(Spatial spatial, ColorRGBA glow){
        if(spatial instanceof Node){
            Node node = (Node)spatial;
            List<Spatial> children = node.getChildren();
            for (int i = 0; i < children.size(); i++) {
                Spatial spatial1 = children.get(i);
                if(spatial1 instanceof Geometry){
                    Geometry geom = (Geometry)spatial1;
                    Material material = geom.getMaterial();
                    material.setColor("GlowColor", glow);
                }else{//go deeper
                    setGlowColor(spatial1,glow);
                }
            }
        }
    }
    
    /**
     * 
     * @return
     */
    @Override
    public boolean isSelected(){
        return selected;
    }
    
    /**
     * 
     * @param visible
     */
    public void setCentersVisible(boolean visible){
        setSpatialVisible(VolumeCenterGeom,visible);
        setSpatialVisible(OldCenterGeom,visible);
        setSpatialVisible(MassCenterGeom,visible);
        setSpatialVisible(VolumeCenterPreciseGeom,visible);
    }

    /**
     * 
     * @param visible
     */
    public void setVisualizerVisible(boolean visible){
        for ( String elem : actuators.keySet() ){
            Actuator element = (Actuator)actuators.get(elem);
            if(element.isEnabled()){
                if(element instanceof PointVisualizer){
                    element.setNodeVisibility(auv_param.isDebugVisualizers());
                }else if(element instanceof VectorVisualizer){
                    element.setNodeVisibility(auv_param.isDebugVisualizers());
                }
            }
        }
    }
    /**
     * 
     * @param visible
     */
    public void setPhysicalExchangerVisible(boolean visible){
        for ( String elem : sensors.keySet() ){
            Sensor element = (Sensor)sensors.get(elem);
            if(element.isEnabled()){
                element.setNodeVisibility(auv_param.isDebugPhysicalExchanger());
            }
        }
        for ( String elem : actuators.keySet() ){
            Actuator element = (Actuator)actuators.get(elem);
            if(element.isEnabled() && !(element instanceof PointVisualizer) && !(element instanceof VectorVisualizer)){
                element.setNodeVisibility(auv_param.isDebugPhysicalExchanger());
            }
        }
    }
    
    /**
     * 
     * @param visible
     */
    public void setCollisionVisible(boolean visible){
        if(visible){
            Helper.setNodeUserData(auv_node,DebugHint.DebugName,DebugHint.Debug);
        }else{
            Helper.setNodeUserData(auv_node,DebugHint.DebugName,DebugHint.NoDebug);
        }
    }
    
    /**
     * 
     * @param visible
     */
    public void setBuoycancyVisible(boolean visible){
        
    }
    
    /**
     * 
     * @param visible
     */
    public void setDragVisible(boolean visible){
        
    }
    
    /**
     * 
     * @param visible
     */
    public void setBoundingBoxVisible(boolean visible){
        setSpatialVisible(boundingBox,visible);
    }
    
    /**
     * 
     * @param spatial
     * @param visible
     */
    protected void setSpatialVisible(Spatial spatial,boolean visible){
        if(visible){
            spatial.setCullHint(CullHint.Inherit);
        }else{
            spatial.setCullHint(CullHint.Always);
        }
    }
    
    /**
     * 
     * @param visible
     */
    public void setWireframeVisible(boolean visible){
        if(visible){
            Node nodes = (Node)auv_spatial;
            List<Spatial> children = nodes.getChildren();
            for (Iterator<Spatial> it = children.iterator(); it.hasNext();) {
                Spatial spatial = it.next();
                System.out.println(spatial.getName());
                if(spatial instanceof Geometry){
                    Geometry geom = (Geometry)spatial;
                    geom.getMaterial().getAdditionalRenderState().setWireframe(true);
                }
            }
        }else{
            Node nodes = (Node)auv_spatial;
            List<Spatial> children = nodes.getChildren();
            for (Iterator<Spatial> it = children.iterator(); it.hasNext();) {
                Spatial spatial = it.next();
                System.out.println(spatial.getName());
                if(spatial instanceof Geometry){
                    Geometry geom = (Geometry)spatial;
                    geom.getMaterial().getAdditionalRenderState().setWireframe(false);
                }
            }
        }
    }
    
    /**
     * 
     * @param visible
     */
    public void setWayPointsVisible(boolean visible){
        WayPoints.setWaypointVisibility(visible);
    }
    
    /**
     * 
     * @param enabled
     */
    @Override
    public void setWaypointsEnabled(boolean enabled){
    }
    
    /**
     * 
     * @return
     */
    @Override
    public WayPoints getWaypoints(){
        return WayPoints;
    }

    /**
     * 
     * @param path
     */
    @Override
    public void updateState(TreePath path) {
        getAuv_param().updateState(path);
        Object obj = path.getPathComponent(3);
        if(obj != null){
            if( obj instanceof HashMapWrapper){
                HashMapWrapper hasher = (HashMapWrapper)obj;
                if(hasher.getUserData() instanceof PhysicalExchanger){
                    PhysicalExchanger pe = (PhysicalExchanger) hasher.getUserData();
                    pe.updateState(path);        
                }else if(hasher.getUserData() instanceof Accumulator){
                    Accumulator acc = (Accumulator) hasher.getUserData();
                    acc.updateState(path);        
                }   
            }
        }
    }
    
    private void setNodePickUserData(Spatial spatial){
        if(spatial instanceof Node){
            Node node = (Node)spatial;
            node.setUserData(PickHint.PickName, PickHint.NoPick);
            List<Spatial> children = node.getChildren();
            for (Spatial spatial1 : children) {
                setNodePickUserData(spatial1);
            }
        }else{//its a spatial or geom, we dont care because it cant go deeper
            spatial.setUserData(PickHint.PickName, PickHint.NoPick);
        }
    }
    
    /**
     * 
     * @param e
     */
    public void fireEvent( RosNodeEvent e ){
        if(getAuv_param().isEnabled()){
                setROS_Node((MARSNodeMain)e.getSource());
                initROS();
        }
    }

    @Override
    public Object getChartValue() {
        return 1f;
    }

    @Override
    public long getSleepTime() {
        return 1000;
    }
    
    @Override
    public void addAdListener( AUVListener listener )
    {
      listeners.add( AUVListener.class, listener );
    }

    @Override
    public void removeAdListener( AUVListener listener )
    {
      listeners.remove( AUVListener.class, listener );
    }
    
    @Override
    public void removeAllListener(){
        //listeners.
    }

    @Override
    public void notifyAdvertisement( ChartEvent event )
    {
      for ( AUVListener l : listeners.getListeners( AUVListener.class ) )
        l.onNewData( event );
    }
    
    protected synchronized void notifySafeAdvertisement( ChartEvent event )
    {
      notifyAdvertisement(event);
    }
}
