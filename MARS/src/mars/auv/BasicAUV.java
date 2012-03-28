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
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.ConeCollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResults;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.texture.Image.Format;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.FrameBuffer;
import com.jme3.util.BufferUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.FileHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.Initializer;
import mars.Keys;
import mars.PhysicalEnvironment;
import mars.PhysicalExchanger;
import mars.MARS_Settings;
import mars.gui.MARSView;
import mars.MARS_Main;
import mars.Manipulating;
import mars.Moveable;
import mars.states.SimState;
import mars.auv.example.Hanse;
import mars.auv.example.Hanse2;
import mars.auv.example.Monsun2;
import mars.auv.example.SMARTE;
import mars.ros.MARSNodeMain;
import mars.sensors.InfraRedSensor;
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
@XmlSeeAlso( {Hanse.class, Monsun2.class, Hanse2.class, SMARTE.class} )
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
    private CollisionShape collisionShape;

    private Camera onCamera;

    private int buoyancy_updaterate = 5;
    private int drag_updaterate = 5;

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
    private float frustumSize = 0.6f;
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

    private PhysicalValues physicalvalues;
    
    private Communication_Manager com_manager;
    @Deprecated
    private org.ros.node.Node ros_node;  
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
    public BasicAUV(){
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
        this.physicalvalues.setAuv_name(auv_param.getAuv_name());
        this.auv_param.setAuv(this);
        buoyancy_updaterate = auv_param.getBuoyancy_updaterate();
        drag_updaterate = auv_param.getDrag_updaterate();
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
     * @param ros_node
     */
    public void setROS_Node(org.ros.node.Node ros_node){
        this.ros_node = ros_node;
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
        setupDragOffscreenView();
        if(auv_param.isDebugDrag()){
            setupCam2();
        }

        //calculate the volume one time exact as possible, ignore water height
        long old_time = System.currentTimeMillis();
        float[] vol = (float[])calculateVolume(auv_spatial,0.015625f,60,60,true);//0.03125f,30,30      0.0625f,80,60     0.03125f,160,120   0.0078125f,640,480
        volume = vol[0];
        long new_time = System.currentTimeMillis();
        System.out.println("time: " + (new_time-old_time));
        System.out.println("VOLUME: " + volume + "VOLUME AIR: " + vol[1]);
        actual_vol = volume;
        buoyancy_force = physical_environment.getFluid_density() * (physical_environment.getGravitational_acceleration()) * actual_vol;

        initPhysicalExchangers();
        
        if(mars_settings.isROS_Server_enabled()){
            initROS();
        }
        auv_node.rotate(auv_param.getRotation().x, auv_param.getRotation().y, auv_param.getRotation().z);
        rotateAUV();
        auv_node.updateGeometricState();
    };

    private void initPhysicalExchangers(){
        //init sensors
        for ( String elem : sensors.keySet() ){
            Sensor element = (Sensor)sensors.get(elem);
            if(element.isEnabled()){
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
                    ((InfraRedSensor)element).setDetectable((com.jme3.scene.Node)mars.getRootNode().getChild("terrain"));//is needed for filters
                }  
                if(element instanceof Sonar){
                    ((Sonar)element).setDetectable((com.jme3.scene.Node)mars.getRootNode().getChild("terrain"));//is needed for filters
                } 
                if(element instanceof TerrainSender){
                    ((TerrainSender)element).setIniter(initer);
                    ((TerrainSender)element).setMarsSettings(mars_settings);
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
                element.setSimState(simstate);
                element.setPhysical_environment(physical_environment);
                element.setPhysicsControl(physics_control);
                element.setMassCenterGeom(this.getMassCenterGeom());
                element.setSimauv_settings(mars_settings);
                element.setNodeVisibility(auv_param.isDebugPhysicalExchanger());
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
                //element.initROS(ros_node,auv_param.getAuv_name());
                element.initROS(mars_node,auv_param.getAuv_name());
            }
        }
        for ( String elem : actuators.keySet() ){
            Actuator element = (Actuator)actuators.get(elem);
            if(element.isEnabled()){
                //element.initROS(ros_node,auv_param.getAuv_name());
                element.initROS(mars_node,auv_param.getAuv_name());
            }
        }
    }

    private void updateActuatorForces(){
        for ( String elem : actuators.keySet() ){
            Actuator element = (Actuator)actuators.get(elem);
            if(element instanceof Thruster){
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
        physics_control.applyImpulse(drag_force_vec,Vector3f.ZERO);
    }

    private void updateAngularDragForces(){
        Vector3f cur_ang = physics_control.getAngularVelocity();
        float angular_velocity = physics_control.getAngularVelocity().length();
        float drag_torque = (float)(auv_param.getDrag_coefficient_angular() * drag_area * 0.25f * physical_environment.getFluid_density()* Math.pow(angular_velocity, 2));
        Vector3f drag_direction = physics_control.getAngularVelocity().normalize().negate();
        Vector3f angular_drag_torque_vec = drag_direction.mult(drag_torque/mars_settings.getPhysicsFramerate());
        /*System.out.println("cur_ang: " + cur_ang);
        System.out.println("angular_velocity: " + angular_velocity);
        System.out.println("drag_torque: " + drag_torque);
        System.out.println("drag_direction: " + drag_direction);
        System.out.println("angular_drag_torque_vec: " + angular_drag_torque_vec);
        System.out.println("angular_drag_torque_scalar: " + angular_drag_torque_vec.length());
        System.out.println("==========================");*/
        physics_control.applyTorqueImpulse(angular_drag_torque_vec);
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

            float[] vol = (float[])calculateVolume(auv_spatial,0.03125f,30,30,false);
            actual_vol = vol[0];
            actual_vol_air = vol[1];

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
        physics_control.applyCentralForce(physical_environment.getWater_current());
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
        if(auv_node.getParent().getParent().getParent().getParent().getName() != null && auv_node.getParent().getParent().getParent().getParent().getName().equals("Root Node")){//check if PhysicsNode added to rootNode
            //since bullet deactivate nodes that dont move enough we must activate it
            if(!physics_control.isActive()){
                physics_control.activate();
            }
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

    /**
     *
     * @param view
     */
    public void setView(MARSView view){
        this.view = view;
    }

    /**
     *
     * @return
     */
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
        assetManager.registerLocator("Assets/Models", FileLocator.class.getName());

        auv_spatial = assetManager.loadModel(auv_param.getModelFilePath());
        auv_spatial.setLocalScale(auv_param.getModel_scale());
        //auv_spatial.rotate(-(float)Math.PI/4 , (float)Math.PI/4 , 0f);
        //Material mat_white = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //mat_white.setColor("Color", ColorRGBA.White);
        //auv_spatial.setMaterial(mat_white);
        /*Material mat_white = new Material(assetManager, "Common/MatDefs/Misc/WireColor.j3md");
        mat_white.setColor("Color", ColorRGBA.Blue);
        auv_spatial.setMaterial(mat_white);*/
        auv_spatial.setLocalTranslation(auv_param.getCentroid_center_distance().x, auv_param.getCentroid_center_distance().y,auv_param.getCentroid_center_distance().z);
        auv_spatial.updateGeometricState();
        auv_spatial.updateModelBound();
        auv_spatial.setName(auv_param.getModel_name());
        auv_spatial.setUserData("auv_name", getName());
        auv_spatial.setCullHint(CullHint.Never);//never cull it because offscreen uses it
        setWireframeVisible(auv_param.isDebugWireframe());
        auv_node.attachChild(auv_spatial);
    }
    
    private void createGhostAUV(){
        assetManager.registerLocator("Assets/Models", FileLocator.class.getName());
        ghost_auv_spatial = assetManager.loadModel(auv_param.getModelFilePath());
        ghost_auv_spatial.setLocalScale(auv_param.getModel_scale());
        ghost_auv_spatial.setLocalTranslation(auv_param.getCentroid_center_distance().x, auv_param.getCentroid_center_distance().y,auv_param.getCentroid_center_distance().z);
        ghost_auv_spatial.updateGeometricState();
        ghost_auv_spatial.updateModelBound();
        ghost_auv_spatial.setName(auv_param.getModel_name() + "_ghost");
        ghost_auv_spatial.setUserData("auv_name", getName());
        ghost_auv_spatial.setCullHint(CullHint.Always);
        auv_node.attachChild(ghost_auv_spatial);
    }
    
    public Spatial getGhostAUV(){
        return ghost_auv_spatial;
    }
    
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

        physics_control = new RigidBodyControl(compoundCollisionShape1, auv_param.getMass());
        physics_control.setCollisionGroup(1);
        physics_control.setCollideWithGroups(1);
        physics_control.setDamping(auv_param.getDamping_linear(), auv_param.getDamping_angular());
        physics_control.setAngularFactor(auv_param.getAngular_factor());
        //physics_control.setFriction(0f);
        //physics_control.setRestitution(0.3f);

        //debug
        Material debug_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        debug_mat.setColor("Color", ColorRGBA.Red);
        debugShape = physics_control.createDebugShape(assetManager);
        auv_node.attachChild(debugShape);
        if(getAuv_param().isDebugCollision()){
            debugShape.setCullHint(CullHint.Inherit);
        }else{
            debugShape.setCullHint(CullHint.Always);
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
        drag_offCamera.setLocation(OldCenterGeom.getWorldTranslation());
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
        drag_offView.attachScene(auv_spatial);
    }

    /**
     *
     */
    public void cleanupOffscreenView(){
        drag_offView.detachScene(auv_spatial);
        debug_drag_view.detachScene(auv_spatial);
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
        //in wich direction are we moving? mirror the vector
        drag_offCamera.setLocation(OldCenterGeom.getWorldTranslation());
        drag_offCamera.lookAt( OldCenterGeom.getWorldTranslation().add(physics_control.getLinearVelocity().normalize().negate())
                , OldCenterGeom.getWorldTranslation());

        if(auv_param.isDebugDrag()){
            onCamera.setLocation(OldCenterGeom.getWorldTranslation());
            onCamera.lookAt( OldCenterGeom.getWorldTranslation().add(physics_control.getLinearVelocity().normalize().negate())
                , OldCenterGeom.getWorldTranslation());
        }

        if(physics_control.getLinearVelocity().length() != 0){//when we have no velocity then we have no water resistance than we dont need an update
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

     /*
     * Calculates the volume of the auv.
     */
    private float[] calculateVolume(Spatial auv, float resolution, int x_length, int y_width, boolean ignore_water_height){
        float[] arr_ret = new float[2];
        float ray_distance_x = 0.45f;//0.45f;
        float ray_distance_z = 0.5f;
        float calc_volume = 0.0f;
        float calc_volume_air = 0.0f;
        float shift = 0.01f;
        float old_volume_mass = 0.0f;
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
     * @param tpf
     */
    public void updateValues(float tpf){
        physicalvalues.incTime(tpf);
        if(physicalvalues.getTime() >= auv_param.getPhysicalvalues_updaterate() && auv_param.getPhysicalvalues_updaterate() > 0.0f){
            physicalvalues.clearTime();
            physicalvalues.setVelocity(String.valueOf(physics_control.getLinearVelocity().length()));
            view.updateValues(getName(),"velocity",physicalvalues.getVelocity());
            physicalvalues.setPosition(String.valueOf(MassCenterGeom.getWorldTranslation()));
            view.updateValues(getName(),"position",physicalvalues.getPosition());
            physicalvalues.setAngularVelocity(String.valueOf(physics_control.getAngularVelocity().length()));
            view.updateValues(getName(),"angular_velocity",physicalvalues.getAngularVelocity());
            physicalvalues.setVolume(String.valueOf(this.actual_vol));
            view.updateValues(getName(),"volume",physicalvalues.getVolume());
            Matrix3f matrix = Matrix3f.ZERO;
            physics_control.getPhysicsRotationMatrix(matrix);
            //physics_control.getPhysicsRotation(matrix);
            Quaternion q_rot = new Quaternion();
            q_rot.fromRotationMatrix(matrix);
            physicalvalues.setRotation(String.valueOf(new Vector3f(q_rot.getX(),q_rot.getY(),q_rot.getZ())));
            view.updateValues(getName(),"rotation",physicalvalues.getRotation());
        }
    }

    /*
     * 
     */
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
        //updateImageContents();
    }

    public void cleanup() {
        
    }
    
    @Override
    public void setSelected(boolean selected){
        if(selected && this.selected==false){
            ambient_light.setColor(mars_settings.getSelectionColor());
            selectionNode.addLight(ambient_light); 
        }else if(selected == false){
            selectionNode.removeLight(ambient_light);
        }
        this.selected = selected;
    }
    
    @Override
    public boolean isSelected(){
        return selected;
    }
    
    public void setCentersVisible(boolean visible){
        if(!visible){
            VolumeCenterGeom.setCullHint(CullHint.Always);
            OldCenterGeom.setCullHint(CullHint.Always);
            MassCenterGeom.setCullHint(CullHint.Always);
            VolumeCenterPreciseGeom.setCullHint(CullHint.Always);
        }else{
            VolumeCenterGeom.setCullHint(CullHint.Never);
            OldCenterGeom.setCullHint(CullHint.Never);
            MassCenterGeom.setCullHint(CullHint.Never);
            VolumeCenterPreciseGeom.setCullHint(CullHint.Never);
        }
    }
    
    public void setPhysicalExchangerVisible(boolean visible){
        for ( String elem : sensors.keySet() ){
            Sensor element = (Sensor)sensors.get(elem);
            if(element.isEnabled()){
                element.setNodeVisibility(auv_param.isDebugPhysicalExchanger());
            }
        }
        for ( String elem : actuators.keySet() ){
            Actuator element = (Actuator)actuators.get(elem);
            if(element.isEnabled()){
                element.setNodeVisibility(auv_param.isDebugPhysicalExchanger());
            }
        }
    }
    
    public void setCollisionVisible(boolean visible){
        if(visible){
            debugShape.setCullHint(CullHint.Inherit);
        }else{
            debugShape.setCullHint(CullHint.Always);
        }
    }
    
    public void setBuoycancyVisible(boolean visible){
        
    }
    
    public void setDragVisible(boolean visible){
        
    }
    
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
    
    public void setWayPointsVisible(boolean visible){
        WayPoints.setWaypointVisibility(visible);
    }
    
    public void setWaypointsEnabled(boolean enabled){
    }
    
    public WayPoints getWaypoints(){
        return WayPoints;
    }

    public void updateState(TreePath path) {
        getAuv_param().updateState(path);
    }
}
