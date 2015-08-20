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

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.ConeCollisionShape;
import com.jme3.bullet.collision.shapes.CylinderCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
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
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image.Format;
import com.jme3.util.BufferUtils;
import com.rits.cloning.Cloner;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import jme3tools.optimize.LodGenerator;
import mars.Helper.Helper;
import mars.Initializer;
import mars.Keys;
import mars.MARS_Main;
import mars.MARS_Settings;
import mars.PhysicalEnvironment;
import mars.PhysicalExchange.Manipulating;
import mars.PhysicalExchange.Moveable;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.accumulators.Accumulator;
import mars.actuators.Actuator;
import mars.actuators.BallastTank;
import mars.actuators.thruster.Thruster;
import mars.actuators.visualizer.PointVisualizer;
import mars.actuators.visualizer.VectorVisualizer;
import mars.auv.example.ASV;
import mars.auv.example.Buoy;
import mars.auv.example.Hanse;
import mars.auv.example.Manta;
import mars.auv.example.Monsun2;
import mars.auv.example.ROMP;
import mars.auv.example.SMARTE;
import mars.control.GuiControl;import mars.communication.AUVConnection;
import mars.communication.AUVConnectionFactory;import mars.control.LimitedRigidBodyControl;
import mars.control.MyCustomGhostControl;
import mars.control.MyLodControl;
import mars.control.PopupControl;
import mars.control.SedimentEmitterControl;
import mars.events.MARSObjectEvent;
import mars.events.MARSObjectListener;
import mars.misc.DebugHint;
import mars.misc.PickHint;
import mars.object.BuoyancyType;
import mars.object.CollisionType;
import mars.sensors.CommunicationDevice;
import mars.sensors.FlowMeter;
import mars.sensors.InfraRedSensor;
import mars.sensors.PingDetector;
import mars.sensors.PollutionMeter;
import mars.sensors.RayBasedSensor;
import mars.sensors.Sensor;
import mars.sensors.TerrainSender;
import mars.sensors.VideoCamera;
import mars.sensors.energy.EnergyHarvester;
import mars.sensors.energy.SolarPanel;
import mars.states.SimState;
import mars.xml.HashMapAdapter;

/**
 * The basic BasicAUV class. When you want to make own auv's or enchance them than extend from this class and make your own implementation. Or implement the AUV interface when you want to do something completly different that i have done with the BasicAUV class.
 *
 * @author Thomas Tosik
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({Hanse.class, Monsun2.class, ASV.class, SMARTE.class, Buoy.class, ROMP.class, Manta.class})
public class BasicAUV implements AUV, SceneProcessor {

    private Geometry MassCenterGeom;
    private Geometry VolumeCenterGeom;
    private Geometry VolumeCenterPreciseGeom;
    private Geometry OldCenterGeom;
    private MARS_Main mars;
    private SimState simstate;
    private AssetManager assetManager;
    private RenderManager renderManager;
    private Renderer renderer;
    private DistanceCoveredPath distanceCoveredPath;
    private MARS_Settings mars_settings;
    private Initializer initer;
    @XmlElement(name = "Parameters")
    private AUV_Parameters auv_param;
    private Vector3f volume_center_precise = new Vector3f(0, 0, 0);
    private Spatial auv_spatial;
    private Spatial debugShape;
    private Node auv_node = new Node("");
    private Node selectionNode = new Node("selectionNode");
    private RigidBodyControl physics_control;
    private MyCustomGhostControl ghostControl;
    private ColorRGBA ghostColor = new ColorRGBA();
    private CollisionShape collisionShape;
    private Geometry boundingBox;
    private Geometry BuoyancyGeom;
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
    private float completeVolume = 0.0f;//m³
    private float actual_vol = 0.0f;//m³
    private float actual_vol_air = 0.0f;//m³
    private float drag_area = 0.0f;//m² pojected
    private float drag_area_temp = 0.0f;//m² pojected

    //forces
    private float buoyancy_force = 0.0f;
    private Vector3f drag_force_vec = new Vector3f(0f, 0f, 0f);
    private Node rootNode;

    // ROS/TCP Connector --------
    private AUVConnection auvConnection;

    @Override
    public void setAuvConnection(AUVConnection connection) {
        auvConnection = connection;
    }

    @Override
    public AUVConnection getAuvConnection() {
        return auvConnection;
    }
    // --------------------------

    //PhysicalExchanger HashMaps to store and load sensors and actuators
    @XmlJavaTypeAdapter(HashMapAdapter.class)
    @XmlElement(name = "Sensors")
    private HashMap<String, Sensor> sensors = new HashMap<String, Sensor>();
    @XmlJavaTypeAdapter(HashMapAdapter.class)
    @XmlElement(name = "Actuators")
    private HashMap<String, Actuator> actuators = new HashMap<String, Actuator>();
    @XmlJavaTypeAdapter(HashMapAdapter.class)
    @XmlElement(name = "Accumulators")
    private HashMap<String, Accumulator> accumulators = new HashMap<String, Accumulator>();

    private EventListenerList listeners = new EventListenerList();
    private CommunicationManager com_manager;

    //selection stuff aka highlightening
    private boolean selected = false;
    AmbientLight ambient_light = new AmbientLight();
    private Spatial ghost_auv_spatial;

    //LOD
    private List<Geometry> listGeoms = new ArrayList<Geometry>();

    /**
     * This is the main auv class. This is where the auv will be made vivisble. All sensors and actuators will be added to it. Also all the physics stuff happens here.
     *
     * @param simstate
     */
    public BasicAUV(SimState simstate) {
        //set the logging
        try {
            Logger.getLogger(this.getClass().getName()).setLevel(Level.parse(simstate.getMARSSettings().getLoggingLevel()));

            if(simstate.getMARSSettings().getLoggingFileWrite()){
                // Create an appending file handler
                boolean append = true;
                FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
                handler.setLevel(Level.parse(simstate.getMARSSettings().getLoggingLevel()));
                // Add to the desired logger
                Logger logger = Logger.getLogger(this.getClass().getName());
                logger.addHandler(handler);
            }
            
            if(!simstate.getMARSSettings().getLoggingEnabled()){
                Logger.getLogger(this.getClass().getName()).setLevel(Level.OFF);
            }
        } catch (IOException e) {
        }

        this.simstate = simstate;
        this.mars = simstate.getMARS();
        this.assetManager = simstate.getAssetManager();
        this.renderer = mars.getRenderer();
        this.renderManager = mars.getRenderManager();
        this.rootNode = simstate.getRootNode();
        this.initer = simstate.getIniter();
        selectionNode.attachChild(auv_node);
    }

    /**
     *
     */
    public BasicAUV() {

    }

    /**
     *
     * @param auv
     */
    public BasicAUV(AUV auv) {
        initAfterJAXB();
        AUV_Parameters auvCopy = auv.getAuv_param().copy();
        setAuv_param(auvCopy);

        //clone accumulators, since they are simple no big problem here
        HashMap<String, Accumulator> accumulatorsOriginal = auv.getAccumulators();
        Cloner cloner = new Cloner();
        accumulators = cloner.deepClone(accumulatorsOriginal);

        HashMap<String, Actuator> actuatorOriginal = auv.getActuators();
        for (String elem : actuatorOriginal.keySet()) {
            Actuator element = actuatorOriginal.get(elem);
            PhysicalExchanger copy = element.copy();
            copy.initAfterJAXB();
            registerPhysicalExchanger(copy);
        }

        HashMap<String, Sensor> sensorsOriginal = auv.getSensors();
        for (String elem : sensorsOriginal.keySet()) {
            Sensor element = sensorsOriginal.get(elem);
            PhysicalExchanger copy = element.copy();
            copy.initAfterJAXB();
            registerPhysicalExchanger(copy);
        }

    }

    /**
     * Called by JAXB after object creation.
     */
    public void initAfterJAXB() {
        selectionNode.attachChild(auv_node);
    }

    /**
     *
     */
    @Override
    public void createDefault() {
        initAfterJAXB();
    }

    /**
     *
     */
    @Override
    public void cleanupAUV() {
        cleanupOffscreenView();
        for (String elem : sensors.keySet()) {
            Sensor element = sensors.get(elem);
            element.cleanup();
        }
        for (String elem : actuators.keySet()) {
            Actuator element = actuators.get(elem);
            element.cleanup();
        }
    }

    /**
     *
     * @return
     */
    @Override
    public AUV_Parameters getAuv_param() {
        return auv_param;
    }

    /**
     *
     * @param auv_param
     */
    @Override
    public void setAuv_param(AUV_Parameters auv_param) {
        this.auv_param = auv_param;
        this.auv_param.addPropertyChangeListener(this);
        //this.auv_param.setAuv(this);
        buoyancy_updaterate = auv_param.getBuoyancyUpdaterate();
        drag_updaterate = auv_param.getDrag_updaterate();
        flow_updaterate = auv_param.getFlow_updaterate();
        auv_node.setName(auv_param.getName() + "_physicnode");
        if (distanceCoveredPath != null) {
            distanceCoveredPath.setAuv_param(auv_param);
        }
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalEnvironment getPhysical_environment() {
        return physical_environment;
    }

    /**
     *
     * @param physical_environment
     */
    @Override
    public void setPhysical_environment(PhysicalEnvironment physical_environment) {
        this.physical_environment = physical_environment;
    }

    /**
     *
     * @return
     */
    @Override
    public MARS_Settings getMARS_Settings() {
        return mars_settings;
    }

    /**
     *
     */
    @Override
    public void setMARS_Settings(MARS_Settings simauv_settings) {
        this.mars_settings = simauv_settings;
    }

    /**
     *
     * @return
     */
    @Override
    public CommunicationManager getCommunicationManager() {
        return com_manager;
    }

    /**
     *
     * @param com_manager
     */
    @Override
    public void setCommunicationManager(CommunicationManager com_manager) {
        this.com_manager = com_manager;
    }

    /**
     *
     * @return
     */
    @Override
    public String getName() {
        return auv_param.getName();
    }

    /**
     *
     * @param auv_name
     */
    @Override
    public void setName(String auv_name) {
        auv_param.setName(auv_name);
        auv_node.setName(auv_name + "_physicnode");
    }

    /**
     *
     * @return
     */
    @Override
    public String getPhysicalNodeName() {
        return auv_node.getName();
    }

    /**
     *
     * @param name
     * @param pex
     */
    @Override
    public void registerPhysicalExchanger(String name, PhysicalExchanger pex) {
        pex.setName(name);
        registerPhysicalExchanger(pex);
    }

    /**
     *
     * @param pex
     */
    @Override
    public void registerPhysicalExchanger(final PhysicalExchanger pex) {
        pex.setName(pex.getName());
        if (pex instanceof Sensor) {
            sensors.put(pex.getName(), (Sensor) pex);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Sensor " + pex.getName() + " added...", "");
        } else if (pex instanceof Actuator) {
            actuators.put(pex.getName(), (Actuator) pex);
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Actuator " + pex.getName() + " added...", "");
        }
    }
    
    /**
     * Init the PE in a safe way. Means that first a mars instance is available and second enqueue the register.
     */
    @Override
    public void initPhysicalExchangerFuture() {
        mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                //init
                initPhysicalExchangers();
                return null;
            }
        });
    }

    /**
     *
     * @param arrlist
     */
    @Override
    public void registerPhysicalExchangers(ArrayList<PhysicalExchanger> arrlist) {
        Iterator<PhysicalExchanger>  iter = arrlist.iterator();
        while (iter.hasNext()) {
            PhysicalExchanger pex = iter.next();
            registerPhysicalExchanger(pex);
        }
    }

    @Override
    public void deregisterPhysicalExchanger(final PhysicalExchanger pex) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "AUV " + getName() + " is deleting PhysicalExchanger: " + pex.getName(), "");
        mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                sensors.remove(pex.getName());
                actuators.remove(pex.getName());
                accumulators.remove(pex.getName());
                pex.cleanup();
                return null;
            }
        });
    }

    @Override
    public void deregisterPhysicalExchanger(String name) {
        Sensor sens = sensors.get(name);
        if (sens != null) {
            deregisterPhysicalExchanger(sens);
        }
        Actuator act = actuators.get(name);
        if (act != null) {
            deregisterPhysicalExchanger(act);
        }
    }

    /**
     *
     * @param oldName
     * @param newName
     */
    @Override
    public void updatePhysicalExchangerName(String oldName, String newName) {
        Sensor sens = sensors.get(oldName);
        if (sens != null) {
            sens.setName(newName);
            sensors.remove(oldName);
            sensors.put(newName, sens);
        }
        Actuator act = actuators.get(oldName);
        if (act != null) {
            act.setName(newName);
            actuators.remove(oldName);
            actuators.put(newName, act);
        }
        Accumulator acc = accumulators.get(oldName);
        if (acc != null) {
            acc.setName(newName);
            accumulators.remove(oldName);
            accumulators.put(newName, acc);
        }
    }

    /**
     * disable the visible debug spheres that indicates the sensors/actuators positions/directions
     *
     * @param visible
     */
    @Override
    public void debugView(boolean visible) {
        for (String elem : sensors.keySet()) {
            Sensor element = sensors.get(elem);
            element.setNodeVisibility(visible);
        }

        for (String elem : actuators.keySet()) {
            Actuator element = actuators.get(elem);
            element.setNodeVisibility(visible);
        }
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "All Sensors/Actuators have visibility: " + visible, "");
    }

    /**
     *
     * @param key Which unique registered actuator do we want?
     * @return The actuator that we asked for
     */
    public Actuator getActuator(String key) {
        return actuators.get(key);
    }

    /**
     *
     * @return
     */
    @Override
    public HashMap<String, Actuator> getActuators() {
        return actuators;
    }

    /**
     *
     * @param key Which unique registered sensor do we want?
     * @return The sensor that we asked for
     */
    @Override
    public Sensor getSensor(String key) {
        return sensors.get(key);
    }

    /**
     *
     * @return
     */
    @Override
    public HashMap<String, Sensor> getSensors() {
        return sensors;
    }

    /**
     *
     * @param key Which unique registered actuator do we want?
     * @return The actuator that we asked for
     */
    @Override
    public Accumulator getAccumulator(String key) {
        return accumulators.get(key);
    }

    /**
     *
     * @return
     */
    @Override
    public HashMap<String, Accumulator> getAccumulators() {
        return accumulators;
    }

    /**
     *
     * @param classNameString
     * @return
     */
    @Override
    public ArrayList<Sensor> getSensorsOfClass(String classNameString) {
        ArrayList<Sensor> ret = new ArrayList<Sensor>();
        for (String elem : sensors.keySet()) {
            Sensor sens = sensors.get(elem);
            try {
                if (Class.forName(classNameString).isInstance(sens)) {
                    ret.add(sens);
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }

    @Override
    public boolean hasSensorsOfClass(String classNameString) {
        for (String elem : sensors.keySet()) {
            Sensor sens = sensors.get(elem);
            try {
                boolean ret = (Class.forName(classNameString).isInstance(sens));
                if (ret) {
                    return true;
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }
    
    @Override
    public void setupLogger(){
        //set the logging
        try {
            Logger.getLogger(this.getClass().getName()).setLevel(Level.parse(simstate.getMARSSettings().getLoggingLevel()));

            if(simstate.getMARSSettings().getLoggingFileWrite()){
                // Create an appending file handler
                boolean append = true;
                FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
                handler.setLevel(Level.parse(simstate.getMARSSettings().getLoggingLevel()));
                // Add to the desired logger
                Logger logger = Logger.getLogger(this.getClass().getName());
                logger.addHandler(handler);
            }
            
            if(!simstate.getMARSSettings().getLoggingEnabled()){
                Logger.getLogger(this.getClass().getName()).setLevel(Level.OFF);
            }
        } catch (IOException e) {
        }  
    }

    /**
     * Call this method ONLY ONCE AFTER you have added ALL sensors and actuators to your auv.
     */
    @Override
    public void init() {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Initialising AUV: " + this.getName(), "");
        loadModel();
        createGhostAUV();
        createPhysicsNode();

        initCenters();
        initWaypoints();
        //initControls();
        //the offscreen for area calculating(drag) must be set
        setupDragOffscreenView();//<-- buggy when deleting/deregister etc
        if (auv_param.isDebugDrag()) {
            setupCam2();
        }

        //calculate the completeVolume one time exact as possible, ignore water height
        //float[] vol = (float[])calculateVolumeAuto(auv_spatial,0.015625f,60,60,true);//0.03125f,30,30      0.0625f,80,60     0.03125f,160,120   0.0078125f,640,480
        //used primarly for auftriebspunkt
        if (getAuv_param().getBuoyancyType() == BuoyancyType.NOSHAPE) {
            float[] vol = calculateVolumeAutoRound(auv_spatial, 0.015625f, true);//0.03125f,30,30      0.0625f,80,60     0.03125f,160,120   0.0078125f,640,480
            completeVolume = vol[0];
        } else {
            float[] calculateVolumeExcact = calculateVolumeExcact(auv_spatial, true);
            completeVolume = calculateVolumeExcact[0];
        }

        //calculate first buoyancy force
        //System.out.println("VOLUME: " + completeVolume + "VOLUME AIR: " + calculateVolumeExcact[1]);
        actual_vol = completeVolume;
        buoyancy_force = physical_environment.getFluid_density() * (physical_environment.getGravitational_acceleration()) * actual_vol;

        initPhysicalExchangers();

        auv_node.rotate(auv_param.getRotation().x, auv_param.getRotation().y, auv_param.getRotation().z);
        rotateAUV();
        auv_node.updateGeometricState();
    }

    private void initPhysicalExchangers() {
        //init sensors
        for (String elem : sensors.keySet()) {
            Sensor element = sensors.get(elem);
            element.setName(element.getName());
            element.setAuv(this);
            if (element.isEnabled() && !element.isInitialized()) {
                element.setSimState(simstate);
                element.setMARS_settings(mars_settings);
                element.setPhysicalEnvironment(physical_environment);
                element.setPhysicsControl(physics_control);
                element.setNodeVisibility(auv_param.isDebugPhysicalExchanger());
                element.setupLogger();
                if (element instanceof VideoCamera) {
                    ((VideoCamera) element).setIniter(initer);//is needed for filters
                }
                if (element instanceof CommunicationDevice) {
                    ((CommunicationDevice) element).setCommunicationManager(com_manager);//is needed for filters
                }
                if (element instanceof InfraRedSensor) {
                    ((InfraRedSensor) element).setCollider(simstate.getCollider());//is needed for filters
                }
                if (element instanceof RayBasedSensor) {
                    ((RayBasedSensor) element).setCollider(simstate.getCollider());//is needed for filters
                }
                if (element instanceof TerrainSender) {
                    ((TerrainSender) element).setIniter(initer);
                    ((TerrainSender) element).setMarsSettings(mars_settings);
                }
                if (element instanceof PingDetector) {
                    ((PingDetector) element).setSimObjectManager(simstate.getSimob_manager());
                }
                if (element instanceof FlowMeter) {
                    ((FlowMeter) element).setIniter(initer);//is needed for filters
                }
                if (element instanceof PollutionMeter) {
                    ((PollutionMeter) element).setIniter(initer);//is needed for filters
                }
                if (element instanceof SolarPanel) {
                    ((SolarPanel) element).setIniter(initer);//is needed for filters
                }
                element.init(auv_node);
                if (element instanceof Keys) {
                    Keys elementKeys = (Keys) element;
                    elementKeys.addKeys(mars.getInputManager(), simstate.getKeyconfig());
                }
                element.setInitialized(true);
            }
        }
        //init actuators
        for (String elem : actuators.keySet()) {
            Actuator element = actuators.get(elem);
            element.setName(element.getName());
            element.setAuv(this);
            if (element.isEnabled() && !element.isInitialized()) {
                element.setSimState(simstate);
                element.setPhysicalEnvironment(physical_environment);
                element.setPhysicsControl(physics_control);
                element.setMassCenterGeom(this.getMassCenterGeom());
                element.setMARS_settings(mars_settings);
                element.setupLogger();
                if (element instanceof PointVisualizer || element instanceof VectorVisualizer) {
                    element.setNodeVisibility(auv_param.isDebugVisualizers());
                } else {
                    element.setNodeVisibility(auv_param.isDebugPhysicalExchanger());
                }
                element.setIniter(initer);
                element.init(auv_node);
                if (element instanceof Keys) {
                    Keys elementKeys = (Keys) element;
                    elementKeys.addKeys(mars.getInputManager(), simstate.getKeyconfig());
                }
                element.setInitialized(true);
            }
        }
        //init special actuators like manipulating ones(servos)
        for (String elem : actuators.keySet()) {
            Actuator element = actuators.get(elem);
            if (element instanceof Manipulating && element.isEnabled()) {
                Manipulating mani = (Manipulating) element;
                ArrayList<String> slaves_names = mani.getSlavesNames();
                Iterator<String> iter = slaves_names.iterator();
                while (iter.hasNext()) {//search for the moveables(slaves) and add them to the master
                    String slave_name = iter.next();
                    Moveable moves = getMoveable(slave_name);
                    moves.setLocalRotationAxisPoints(mani.getWorldRotationAxisPoints());
                    mani.addSlave(moves);
                }
            }
        }
    }

    private Moveable getMoveable(String name) {
        for (String elem : sensors.keySet()) {
            Sensor element = sensors.get(elem);
            if (element.getName().equals(name) && element instanceof Moveable) {
                return (Moveable) element;
            }
        }
        for (String elem : actuators.keySet()) {
            Actuator element = actuators.get(elem);
            if (element.getName().equals(name) && element instanceof Moveable) {
                return (Moveable) element;
            }
        }
        return null;
    }

    private void updateActuatorForces() {
        for (String elem : actuators.keySet()) {
            Actuator element = actuators.get(elem);
            if (element instanceof Thruster) {
                element.updateForces();
            } else if (element instanceof BallastTank) {
                element.updateForces();
            }
        }
    }

    private void updateDragForces() {
        if (drag_updaterate == 0) {//take all drag_updaterate times new values
            drag_updaterate = auv_param.getDrag_updaterate();
            drag_area = 0f;
        } else if (drag_updaterate == 1) {
            drag_updaterate = auv_param.getDrag_updaterate();
            float new_drag_area = calculateArea();
            if (!((physics_control.getLinearVelocity().length() != 0) && (new_drag_area == 0.0f))) {//we move so there must be drag area != 0, if no we have an updateForces bug/problem, use the old one stored
                drag_area = new_drag_area;
            }
        } else {
            drag_updaterate--;
        }

        float velocity = physics_control.getLinearVelocity().length();
        float drag_force = (float) (auv_param.getDrag_coefficient_linear() * drag_area * 0.5f * physical_environment.getFluid_density() * Math.pow(velocity, 2));

        Vector3f drag_direction = physics_control.getLinearVelocity().negate();

        //norm the drag direction
        drag_direction = drag_direction.normalize();
        drag_force_vec = drag_direction.mult(drag_force / mars_settings.getPhysicsFramerate());

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
        physics_control.applyImpulse(drag_force_vec, new Vector3f(0f, 0f, 0f));
        //physics_control.applyImpulse(drag_force_vec,physics_control.getPhysicsLocation());
        //physicalvalues.updateDragForce(drag_force / ((float)mars_settings.getPhysicsFramerate()));
        //physicalvalues.updateDragArea(drag_area);
        //physicalvalues.updateVector(physics_control.getLinearVelocity());
        //notifySafeAdvertisement(new ChartEvent(this, drag_area, 0));
        //notifySafeAdvertisement(new ChartEvent(this, drag_force_vec.length(), 0));
    }

    private void updateAngularDragForces() {
        Vector3f cur_ang = physics_control.getAngularVelocity();
        float angular_velocity = physics_control.getAngularVelocity().length();
        //System.out.println("angular_drag_torque_vec: " + angular_velocity + " " + physics_control.getAngularVelocity());
        //notifySafeAdvertisement(new ChartEvent(this, angular_velocity, 0));

        /*if(Helper.infinityCheck(physics_control.getAngularVelocity())){
         System.out.println("UNF!!!!!!!!!!");
         physics_control.setAngularVelocity(Vector3f.ZERO);
         }*/
        if (angular_velocity <= 5f) {
            float drag_torque = (float) (auv_param.getDrag_coefficient_angular() * drag_area * 0.25f * physical_environment.getFluid_density() * Math.pow(angular_velocity, 2));
            //float drag_torque = (float) (auv_param.getDrag_coefficient_angular() * drag_area * 0.25f * physical_environment.getFluid_density() * Math.pow(angular_velocity,1.5f));
            Vector3f drag_direction = physics_control.getAngularVelocity().negate().normalize();
            Vector3f angular_drag_torque_vec = drag_direction.mult(drag_torque / ((float) mars_settings.getPhysicsFramerate()));
            //System.out.println("angular_drag_torque_vec: " + angular_drag_torque_vec.length() + " " + angular_velocity);
            /*if(angular_drag_torque_vec.negate().length() >= angular_velocity){
             angular_drag_torque_vec.normalizeLocal().multLocal(angular_velocity);
             }*/
            physics_control.applyTorqueImpulse(angular_drag_torque_vec);
            //physicalvalues.updateDragTorque(drag_torque);
            //physicalvalues.updateVector(cur_ang);
        } else {
            /*float drag_torque = (float) (auv_param.getDrag_coefficient_angular() * drag_area * 0.25f * physical_environment.getFluid_density() * angular_velocity);
             Vector3f drag_direction = physics_control.getAngularVelocity().negate().normalize();
             Vector3f angular_drag_torque_vec = drag_direction.mult(drag_torque / ((float)mars_settings.getPhysicsFramerate()));

             physics_control.applyTorqueImpulse(angular_drag_torque_vec);
             physicalvalues.updateDragTorque(drag_torque);
             physicalvalues.updateVector(cur_ang);*/
            physics_control.setAngularVelocity(Vector3f.ZERO);
        }
    }

    private void updateStaticBuyocancyForces() {
        Vector3f brick_vec = OldCenterGeom.getWorldTranslation();
        float distance_to_surface = 1.0f;
        float epsilon = 0.5f;
        buoyancy_updaterate = auv_param.getBuoyancyUpdaterate();
        if (buoyancy_updaterate == 1) {//take all buoyancy_updaterate times new values

            //float[] vol = (float[])calculateVolume(auv_spatial,0.03125f,30,30,false);
            if (getAuv_param().getBuoyancyType() == BuoyancyType.NOSHAPE) {
                float[] vol = calculateVolumeAutoRound(auv_spatial, 0.03125f, false);
                actual_vol = vol[0] * auv_param.getBuoyancyFactor();
                actual_vol_air = vol[1] * auv_param.getBuoyancyFactor();
            } else {
                float[] vol = calculateVolumeExcact(auv_spatial, false);
                actual_vol = vol[0] * auv_param.getBuoyancyFactor();
                actual_vol_air = vol[1] * auv_param.getBuoyancyFactor();
            }

            //buoyancy_force = physical_environment.getFluid_density() * physical_environment.getGravitational_acceleration() * completeVolume;
            buoyancy_force = (physical_environment.getFluid_density() * actual_vol + physical_environment.getAir_density() * actual_vol_air) * physical_environment.getGravitational_acceleration();

            //buoyancy_force_air = physical_environment.getAir_density() * physical_environment.getGravitational_acceleration() * Math.abs(completeVolume - actual_vol);
            /*if(completeVolume >= actual_vol){
             System.out.println("!!!!!!!!!!!!!");
             System.out.println(buoyancy_force_water);
             System.out.println(buoyancy_force_air);
             }*/
            //addValueToSeries(actual_vol,1);
            //addValueToSeries(OldCenterGeom.getWorldTranslation().y + Math.abs(physical_environment.getWater_height()),0);
            //addValueToSeries((float)Math.sqrt(Math.pow(this.AUVPhysicsNode.get.getContinuousForce().x, 2)+Math.pow(this.AUVPhysicsNode.getContinuousForce().y, 2)+Math.pow(this.AUVPhysicsNode.getContinuousForce().z,2)),2);
            //addValueToSeries(buoyancy_force_water+buoyancy_force_air,2);
        } else if (auv_param.getBuoyancyUpdaterate() == 0) {//dont compute everytime the buoyancy, use the computed once
            if (brick_vec.y <= (physical_environment.getWater_height() - auv_param.getBuoyancyDistance())) {//under water
                buoyancy_force = physical_environment.getFluid_density() * physical_environment.getGravitational_acceleration() * completeVolume;
            } else {//at water surface
                buoyancy_force = physical_environment.getFluid_density() * physical_environment.getGravitational_acceleration() * completeVolume * auv_param.getBuoyancyFactor();
            }
        } else {
            buoyancy_force = (physical_environment.getFluid_density() * actual_vol + physical_environment.getAir_density() * actual_vol_air) * physical_environment.getGravitational_acceleration();
            if (buoyancy_updaterate > 0) {
                buoyancy_updaterate--;
            }
        }

        //buoyancy_force = buoyancy_force_water + buoyancy_force_air;
        //Vector3f buoyancy_force_vec = new Vector3f(0.0f,buoyancy_force,0.0f);
        Vector3f buoyancy_force_vec = new Vector3f(0.0f, buoyancy_force / ((float) mars_settings.getPhysicsFramerate()), 0.0f);
        //notifySafeAdvertisement(new ChartEvent(this, OldCenterGeom.getWorldTranslation().y + Math.abs(physical_environment.getWater_height()), 0));
        notifySafeAdvertisementMARSObject(new MARSObjectEvent(this, actual_vol, 0));
        //notifySafeAdvertisement(new ChartEvent(this,VolumeCenterGeom.getWorldTranslation().subtract(MassCenterGeom.getWorldTranslation()).length(), 0));

        //physics_control.applyCentralForce(buoyancy_force_vec);
        //physics_control.applyForce(buoyancy_force_vec, VolumeCenterGeom.getWorldTranslation().subtract(MassCenterGeom.getWorldTranslation()));
        if (!Helper.infinityCheck(buoyancy_force_vec)) {
            //System.out.println("VolumeCenterGeom: " + VolumeCenterGeom.getWorldTranslation().subtract(MassCenterGeom.getWorldTranslation()));
            physics_control.applyImpulse(buoyancy_force_vec, VolumeCenterGeom.getWorldTranslation().subtract(MassCenterGeom.getWorldTranslation()));
            //physics_control.applyForce(buoyancy_force_vec, VolumeCenterGeom.getWorldTranslation().subtract(MassCenterGeom.getWorldTranslation()));
        } else {
            System.out.println("Too much force, caused be infinity...");
        }
    }

    /**
     * Override this method to implement your own forces.
     *
     * @return
     */
    protected Vector3f updateMyForces() {
        return new Vector3f(0f, 0f, 0f);
    }

    /**
     * Override this method to implement your own torques.
     *
     * @return
     */
    protected Vector3f updateMyTorque() {
        return new Vector3f(0f, 0f, 0f);
    }

    private void updateMyForcesAndTorques(Vector3f force, Vector3f torque) {
        physics_control.applyCentralForce(force);
        physics_control.applyTorque(torque);
    }

    private void updateDynamicBuyocancyForces() {
    }

    private void updateWaterCurrentForce() {
        if (flow_updaterate == 1) {//take all flow_updaterate times new values
            flow_updaterate = auv_param.getFlow_updaterate();
            Vector3f physicsLocation = physics_control.getPhysicsLocation();
            Vector3f flow_scale = mars_settings.getFlowScale();
            int flow_image_width = initer.getFlow_image_width();

            Vector3f addedFlowPos = mars_settings.getFlowPosition().add(-((float) flow_image_width * flow_scale.x) / 2f, 0f, -((float) flow_image_width * flow_scale.z) / 2f);
            Vector3f relAuvPos = physicsLocation.subtract(addedFlowPos);

            if ((relAuvPos.x <= ((float) flow_image_width * flow_scale.x)) && (relAuvPos.x >= 0) && (relAuvPos.z <= ((float) flow_image_width * flow_scale.z)) && (relAuvPos.z >= 0)) {//in flowmap bounds

                int auv_pos_x = (int) (((float) flow_image_width / ((float) flow_image_width * flow_scale.x)) * relAuvPos.x);
                int auv_pos_y = (int) (((float) flow_image_width / ((float) flow_image_width * flow_scale.z)) * relAuvPos.z);

                //check on bounds....has to be done here if flowmap!=heightmap
                int flowX = initer.getFlowX()[(auv_pos_x) + (initer.getTerrain_image_width() * auv_pos_y)];
                int flowY = initer.getFlowY()[(auv_pos_x) + (initer.getTerrain_image_width() * auv_pos_y)];

                float scaledFlowX = (flowX / 32768f) / ((float) mars_settings.getPhysicsFramerate());
                float scaledFlowY = (flowY / 32768f) / ((float) mars_settings.getPhysicsFramerate());
                Vector3f flowForce = new Vector3f(scaledFlowX, 0f, scaledFlowY);
                flowForce.multLocal(mars_settings.getFlowForceScale());
                initer.setFlowVector(new Vector3f((flowX / 32768f), 0f, (flowY / 32768f)));
                physics_control.applyImpulse(flowForce, Vector3f.ZERO);
            } else {//out of flowmap bound. no force
            }
        } else if (flow_updaterate == 0) {
            flow_updaterate = auv_param.getFlow_updaterate();
        } else {
            flow_updaterate--;
        }
    }

    /**
     *
     */
    public void clearForces() {
        physics_control.clearForces();
        physics_control.setAngularVelocity(Vector3f.ZERO);
        physics_control.setLinearVelocity(Vector3f.ZERO);
        physics_control.activate();
        System.out.println("FORCES: " + physics_control.getLinearVelocity() + " " + physics_control.getAngularVelocity() + " " + physics_control.getMotionState().getObjectId());
    }

    /**
     *
     */
    @Override
    public void reset() {
        resetAllActuators();
        resetAllSensors();
        resetAllAccumulators();
        clearForces();
        distanceCoveredPath.reset();
        physics_control.setPhysicsLocation(auv_param.getPosition());
        rotateAUV();
        for (final Geometry geometry : listGeoms) {
            geometry.setLodLevel(0);
        }
    }

    /*
     *
     */
    private void rotateAUV() {
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
    @Override
    public void updateForces(float tpf) {
        if (auv_node.getParent().getParent().getParent().getParent().getName() != null && auv_node.getParent().getParent().getParent().getParent().getName().equals("SimState Root Node")) {//check if PhysicsNode added to rootNode
            //since bullet deactivate nodes that dont move enough we must activate it
            /*if(!physics_control.isActive()){
             physics_control.activate();
             }*/
            //calculate actuator(motors) forces
            updateActuatorForces();

            //calculate buyocancy
            updateStaticBuyocancyForces();

            updateDynamicBuyocancyForces();

            //externalforces
            updateMyForcesAndTorques(updateMyForces(), updateMyTorque());

            //calculate the drag
            updateDragForces();
            updateAngularDragForces();

            //add the water_current
            if (getMARS_Settings().isFlowEnabled()) {
                updateWaterCurrentForce();
            }

            //set all velocity to zero for debug purposes
            //physics_control.setLinearVelocity(Vector3f.ZERO);
            //physics_control.setAngularVelocity(Vector3f.ZERO);
            //physics_control.setPhysicsLocation(Vector3f.ZERO);
            //System.out.println("FORCES: " + physics_control.getLinearVelocity() + " " + physics_control.getAngularVelocity());
        } else {//if not inform
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "AUV PhysicsNode is not added to the rootNode!", "");
        }
    }

    /**
     *
     * @param tpf time per frame
     */
    @Override
    public void updateSensors(float tpf) {
        for (String elem : sensors.keySet()) {
            Sensor element = sensors.get(elem);
            if (element.isEnabled()) {
                element.update(tpf);
            }
        }
    }

    /**
     *
     * @param tpf time per frame
     */
    @Override
    public void updateActuators(float tpf) {
        for (String elem : actuators.keySet()) {
            Actuator element = actuators.get(elem);
            if (element.isEnabled()) {
                element.update(tpf);
            }
        }
    }

    @Override
    public void updateAccumulators(float tpf) {
        //update current consumption for the activated sensors
        for (String elem : sensors.keySet()) {
            Sensor element = sensors.get(elem);
            if (element.isEnabled()) {
                Accumulator acc = accumulators.get(element.getAccumulator());
                if (acc != null) { //accu exists from where we can suck energy
                    if (element instanceof EnergyHarvester) {//we have someone who gives us energy
                        EnergyHarvester energyHarvester = (EnergyHarvester) element;
                        acc.addActualCurrent(energyHarvester.getEnergy());
                        energyHarvester.setEnergy(0f);// We have transfered the energy into the accumulator, clean the energyHarvester.
                    } else {// we have someone who wants energy
                        Float currentConsumption = element.getCurrentConsumption();
                        if (currentConsumption != null) {//suck energy
                            float aH = (currentConsumption / 3600f) * tpf;
                            acc.subsractActualCurrent(aH);
                        }
                    }
                }
            }
        }
        //update current consumption for the activated actuators and thrusters
        for (String elem : actuators.keySet()) {
            Actuator element = actuators.get(elem);
            if (element.isEnabled()) {
                Accumulator acc = accumulators.get(element.getAccumulator());
                if (acc != null) { //accu exists from where we can suck energy
                    if (element instanceof Thruster) {//check if thruster(curent function) or normal actuator
                        Thruster th = (Thruster) element;
                        float motorCurrent = th.getMotorCurrent();
                        float aH = (motorCurrent / 3600f) * tpf;
                        acc.subsractActualCurrent(aH);
                    } else {
                        Float currentConsumption = element.getCurrentConsumption();
                        if (currentConsumption != null) {//suck energy
                            float aH = (currentConsumption / 3600f) * tpf;
                            acc.subsractActualCurrent(aH);
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @return
     */
    @Override
    public RigidBodyControl getPhysicsControl() {
        return physics_control;
    }

    /**
     *
     * @param physics_control
     */
    @Override
    public void setPhysicsControl(RigidBodyControl physics_control) {
        this.physics_control = physics_control;
        auv_node.addControl(physics_control);
    }

    /**
     *
     * @return
     */
    @Override
    public MyCustomGhostControl getGhostControl() {
        return ghostControl;
    }

    private ColorRGBA getGhostColor() {
        return ghostColor;
    }

    /**
     *
     * @return
     */
    @Override
    public Node getAUVNode() {
        return auv_node;
    }

    /**
     *
     * @return
     */
    @Override
    public Node getSelectionNode() {
        return selectionNode;
    }

    /**
     *
     * @return
     */
    @Override
    public Spatial getAUVSpatial() {
        return auv_spatial;
    }

    private void initCenters() {
        Sphere sphere4 = new Sphere(16, 16, 0.015f);
        VolumeCenterGeom = new Geometry("VolumeCenterGeom", sphere4);
        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat4.setColor("Color", ColorRGBA.Cyan);
        VolumeCenterGeom.setMaterial(mark_mat4);
        VolumeCenterGeom.setLocalTranslation(auv_param.getCentroid_center_distance().x, auv_param.getCentroid_center_distance().y, auv_param.getCentroid_center_distance().z);
        VolumeCenterGeom.updateGeometricState();
        if (!auv_param.isDebugCenters()) {
            VolumeCenterGeom.setCullHint(CullHint.Always);
        }
        auv_node.attachChild(VolumeCenterGeom);

        Sphere sphere6 = new Sphere(16, 16, 0.0125f);//0.03f
        OldCenterGeom = new Geometry("OldCenterGeom", sphere6);
        Material mark_mat6 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat6.setColor("Color", ColorRGBA.Brown);
        OldCenterGeom.setMaterial(mark_mat6);
        OldCenterGeom.setLocalTranslation(auv_param.getCentroid_center_distance().x, auv_param.getCentroid_center_distance().y, auv_param.getCentroid_center_distance().z);
        OldCenterGeom.updateGeometricState();
        if (!auv_param.isDebugCenters()) {
            OldCenterGeom.setCullHint(CullHint.Always);
        }
        auv_node.attachChild(OldCenterGeom);

        Sphere sphere5 = new Sphere(16, 16, 0.025f);//0.03f
        MassCenterGeom = new Geometry("MassCenterGeom", sphere5);
        Material mark_mat5 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat5.setColor("Color", ColorRGBA.Yellow);
        MassCenterGeom.setMaterial(mark_mat5);
        Vector3f temp = new Vector3f(0f, 0f, 0f);
        auv_node.worldToLocal(auv_node.getWorldTranslation(), temp);
        MassCenterGeom.setLocalTranslation(temp);
        MassCenterGeom.updateGeometricState();
        if (!auv_param.isDebugCenters()) {
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
        if (!auv_param.isDebugCenters()) {
            VolumeCenterPreciseGeom.setCullHint(CullHint.Always);
        }
        auv_node.attachChild(VolumeCenterPreciseGeom);
    }

    /*
     *
     */
    private void initWaypoints() {
        //if(auv_param.isDistanceCoveredPathEnabled()){
        distanceCoveredPath = new DistanceCoveredPath("WayPoints_" + getName(), mars, auv_param, getMARS_Settings());
        Future<Void> fut = mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                rootNode.attachChild(distanceCoveredPath);
                return null;
            }
        });
        //}
    }
    
    /*
    *
    */
    private void initControls(){
        SedimentEmitterControl sediment = new SedimentEmitterControl(initer.getTerrainNode(), assetManager);
        auv_node.addControl(sediment);
        if(!getAuv_param().getEnabled()){
            sediment.setEnabled(false);
        }
    }

    /**
     *
     * @return
     */
    @Override
    public Geometry getMassCenterGeom() {
        return MassCenterGeom;
    }

    /*
     *
     */
    private void loadModel() {
        auv_spatial = assetManager.loadModel(auv_param.getModelFilepath());

        optimizeSpatial(auv_spatial);

        //assetManager.unregisterLoader(OBJLoader.class);
        //assetManager.registerLoader(MyOBJLoader.class,"obj");
        //auv_spatial = (Spatial)assetManager.loadAsset(new ModelKey(auv_param.getModelFilepath()));
        /*assetManager.registerLoader(MyMTLLoader.class);
         int index = auv_param.getModelFilepath().lastIndexOf(".");
         String matPath = auv_param.getModelFilepath().substring(0, index).concat(".mtl");
         Material auv_mat = (Material)assetManager.loadAsset(matPath);*/
        auv_spatial.setLocalScale(auv_param.getModelScale());
        auv_spatial.setLocalTranslation(auv_param.getCentroid_center_distance().x, auv_param.getCentroid_center_distance().y, auv_param.getCentroid_center_distance().z);

        auv_spatial.updateModelBound();
        auv_spatial.updateGeometricState();
        auv_spatial.setName(auv_param.getModelName());
        Helper.setNodeUserData(auv_spatial, "auv_name", getName());
        //auv_spatial.setUserData("auv_name", getName());
        auv_spatial.setCullHint(CullHint.Never);//never cull it because offscreen uses it
        auv_spatial.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        //a control for showing a popup when too far away from the auv
        PopupControl ppcontrol = new PopupControl();
        ppcontrol.setCam(mars.getCamera());
        ppcontrol.setSpatial(auv_spatial);
        ppcontrol.setStateManager(mars.getStateManager());
        ppcontrol.setAuv(this);
        auv_spatial.addControl(ppcontrol);

        //a control for controling the auv from the gui
        GuiControl guicontrol;
        guicontrol = new GuiControl(this,mars.getStateManager());
        selectionNode.addControl(guicontrol);

        WireBox wbx = new WireBox();
        BoundingBox bb = (BoundingBox) auv_spatial.getWorldBound();
        wbx.fromBoundingBox(bb);
        boundingBox = new Geometry("TheMesh", wbx);
        Material mat_box = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_box.setColor("Color", ColorRGBA.Blue);
        boundingBox.setMaterial(mat_box);
        boundingBox.setLocalTranslation(bb.getCenter());
        boundingBox.updateModelBound();
        boundingBox.updateGeometricState();
        setBoundingBoxVisible(auv_param.isDebugBounding());
        Helper.setNodePickUserData(boundingBox, PickHint.NoPick);
        auv_node.attachChild(boundingBox);

        //add a full geom box bounding box since the WireBox produces NPE
        //Box box = new Box(bb.getXExtent(), bb.getYExtent(), bb.getZExtent());
        //BuoyancyGeom = new Geometry("BuoyancyGeom", box);
        Material BuoyancyGeomMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        BuoyancyGeomMat.setColor("Color", ColorRGBA.Cyan);
        /*BuoyancyGeom.setMaterial(BuoyancyGeomMat);
         BuoyancyGeom.setLocalTranslation(bb.getCenter());
         BuoyancyGeom.updateModelBound();
         BuoyancyGeom.updateGeometricState();
         Helper.setNodePickUserData(BuoyancyGeom, PickHint.NoPick);
         setSpatialVisible(BuoyancyGeom, false);
         auv_node.attachChild(BuoyancyGeom);*/

        //add a buoyancy geom, needed for exact completeVolume calculation later
        if (auv_param.getBuoyancyType() == BuoyancyType.BOXCOLLISIONSHAPE) {
            Box buoyancyBox = new Box(auv_param.getBuoyancyDimensions().x, auv_param.getBuoyancyDimensions().y, auv_param.getBuoyancyDimensions().z);
            BuoyancyGeom = new Geometry("BuoyancyGeom", buoyancyBox);
            BuoyancyGeom.setMaterial(BuoyancyGeomMat);
            BuoyancyGeom.setLocalTranslation(auv_param.getBuoyancyPosition());
            BuoyancyGeom.updateModelBound();
            BuoyancyGeom.updateGeometricState();
            Helper.setNodePickUserData(BuoyancyGeom, PickHint.NoPick);
            auv_node.attachChild(BuoyancyGeom);
        } else if (auv_param.getBuoyancyType() == BuoyancyType.SPHERECOLLISIONSHAPE) {
            //collisionShape = new SphereCollisionShape(auv_param.getCollisionDimensions().x);
        } else if (auv_param.getBuoyancyType() == BuoyancyType.CONECOLLISIONSHAPE) {
            //collisionShape = new ConeCollisionShape(auv_param.getCollisionDimensions().x, auv_param.getCollisionDimensions().y);
        } else if (auv_param.getBuoyancyType() == BuoyancyType.CYLINDERCOLLISIONSHAPE) {
            //collisionShape = new CylinderCollisionShape(auv_param.getCollisionDimensions(), 0);
        } else if (auv_param.getBuoyancyType() == BuoyancyType.MESHACCURATE) {
            //collisionShape = CollisionShapeFactory.createDynamicMeshShape(auv_spatial);
        } else if (auv_param.getBuoyancyType() == BuoyancyType.BOUNDINGBOX) {
            Box buoyancyBox = new Box(bb.getXExtent(), bb.getYExtent(), bb.getZExtent());
            BuoyancyGeom = new Geometry("BuoyancyGeom", buoyancyBox);
            BuoyancyGeom.setMaterial(BuoyancyGeomMat);
            BuoyancyGeom.setLocalTranslation(bb.getCenter());
            BuoyancyGeom.updateModelBound();
            BuoyancyGeom.updateGeometricState();
            Helper.setNodePickUserData(BuoyancyGeom, PickHint.NoPick);
            auv_node.attachChild(BuoyancyGeom);
        } else if (auv_param.getBuoyancyType() == BuoyancyType.NOSHAPE) {
            //collisionShape = CollisionShapeFactory.createDynamicMeshShape(auv_spatial);
        } else {
            //collisionShape = new BoxCollisionShape(auv_param.getCollisionDimensions());
        }

        if (BuoyancyGeom != null) {//for init
            setBuoyancyVolumeVisible(getAuv_param().isDebugBuoycancyVolume());
        }

        setWireframeVisible(auv_param.isDebugWireframe());
        auv_node.attachChild(auv_spatial);
    }

    /*
     *
     */
    /**
     *
     * @return
     */
    public Spatial loadModelCopy() {
        Spatial auv_spatial_copy = assetManager.loadModel(auv_param.getModelFilepath());

        optimizeSpatial(auv_spatial_copy);

        auv_spatial_copy.setLocalScale(auv_param.getModelScale());

        auv_spatial_copy.updateModelBound();
        auv_spatial_copy.updateGeometricState();
        auv_spatial_copy.setName(auv_param.getModelName() + "_copy");
        auv_spatial_copy.setUserData("auv_name", getName());
        auv_spatial_copy.setCullHint(CullHint.Never);//never cull it because offscreen uses it
        auv_spatial_copy.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        return auv_spatial_copy;
    }

    private void optimizeSpatial(Spatial auv_spatial) {
        if (auv_spatial instanceof Node) {
            Node auv = (Node) auv_spatial;
            if (auv_param.isOptimizeBatched()) {
                jme3tools.optimize.GeometryBatchFactory.optimize(auv);
            }
            if (auv_param.isOptimizeLod()) {
                for (Spatial spatial : auv.getChildren()) {
                    if (spatial instanceof Geometry) {
                        listGeoms.add((Geometry) spatial);
                    }
                }

                for (final Geometry geometry : listGeoms) {
                    LodGenerator lodGenerator = new LodGenerator(geometry);
                    lodGenerator.bakeLods(LodGenerator.TriangleReductionMethod.PROPORTIONAL, auv_param.getOptimizeLodReduction1(), auv_param.getOptimizeLodReduction2());
                    geometry.setLodLevel(0);
                    MyLodControl control = new MyLodControl();
                    control.setDistTolerance(auv_param.getOptimizeLodDistTolerance());
                    control.setTrisPerPixel(auv_param.getOptimizeLodTrisPerPixel());
                    control.setCam(mars.getCamera());
                    geometry.addControl(control);
                }
            }
        }
    }

    private void createGhostAUV() {
        ghost_auv_spatial = assetManager.loadModel(auv_param.getModelFilepath());
        ghost_auv_spatial.setLocalScale(auv_param.getModelScale());
        ghost_auv_spatial.setLocalTranslation(auv_param.getCentroid_center_distance().x, auv_param.getCentroid_center_distance().y, auv_param.getCentroid_center_distance().z);
        ghost_auv_spatial.updateGeometricState();
        ghost_auv_spatial.updateModelBound();
        ghost_auv_spatial.setName(auv_param.getModelName() + "_ghost");
        Helper.setNodeUserData(ghost_auv_spatial, "auv_name", getName());
        ghost_auv_spatial.setCullHint(CullHint.Always);
        Helper.setNodePickUserData(ghost_auv_spatial, PickHint.NoPick);
        auv_node.attachChild(ghost_auv_spatial);

        //add ghost collision to the "ghost" object so we can get collision results
        /*BoundingBox ghostBound = (BoundingBox) ghost_auv_spatial.getWorldBound();
         ghostControl = new MyCustomGhostControl(new BoxCollisionShape(ghostBound.getExtent(null)));
         ghostControl.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_01);
         ghostControl.setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_01);

         ghost_auv_spatial.addControl(ghostControl);*/

        /*Spatial debugShape2 = ghostControl.createDebugShape(assetManager);
         auv_node.attachChild(debugShape2);*/
    }

    /**
     *
     * @return
     */
    @Override
    public Spatial getGhostAUV() {
        return ghost_auv_spatial;
    }

    /**
     *
     * @param hide
     */
    @Override
    public void hideGhostAUV(boolean hide) {
        if (hide) {
            ghost_auv_spatial.setCullHint(CullHint.Always);
        } else {
            ghost_auv_spatial.setCullHint(CullHint.Never);
        }
    }

    /*
     * When we have the spatial for the auv we create the physics node out of it. Needed for all the physics and collisions.
     */
    private void createPhysicsNode() {
        CompoundCollisionShape compoundCollisionShape1 = new CompoundCollisionShape();

        if (auv_param.getCollisionType() == CollisionType.BOXCOLLISIONSHAPE) {
            collisionShape = new BoxCollisionShape(auv_param.getCollisionDimensions());
        } else if (auv_param.getCollisionType() == CollisionType.SPHERECOLLISIONSHAPE) {
            collisionShape = new SphereCollisionShape(auv_param.getCollisionDimensions().x);
        } else if (auv_param.getCollisionType() == CollisionType.CONECOLLISIONSHAPE) {
            collisionShape = new ConeCollisionShape(auv_param.getCollisionDimensions().x, auv_param.getCollisionDimensions().y);
        } else if (auv_param.getCollisionType() == CollisionType.CYLINDERCOLLISIONSHAPE) {
            collisionShape = new CylinderCollisionShape(auv_param.getCollisionDimensions(), 0);
        } else if (auv_param.getCollisionType() == CollisionType.MESHACCURATE) {
            //collisionShape = CollisionShapeFactory.createDynamicMeshShape(auv_spatial);
        } else {
            collisionShape = new BoxCollisionShape(auv_param.getCollisionDimensions());
        }

        compoundCollisionShape1.addChildShape(collisionShape, auv_param.getCentroid_center_distance().add(auv_param.getCollisionPosition()));

        physics_control = new LimitedRigidBodyControl(compoundCollisionShape1, auv_param.getMass());
        physics_control.setCollisionGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
        physics_control.setCollideWithGroups(PhysicsCollisionObject.COLLISION_GROUP_01);
        physics_control.addCollideWithGroup(PhysicsCollisionObject.COLLISION_GROUP_02);
        physics_control.addCollideWithGroup(PhysicsCollisionObject.COLLISION_GROUP_03);
        physics_control.addCollideWithGroup(PhysicsCollisionObject.COLLISION_GROUP_04);
        physics_control.setDamping(auv_param.getDamping_linear(), auv_param.getDamping_angular());
        physics_control.setAngularFactor(auv_param.getAngular_factor());
        physics_control.setSleepingThresholds(0f, 0f);// so the physics node doesn't get deactivated
        physics_control.setEnabled(true);

        //debug
        Material debug_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        debug_mat.setColor("Color", ColorRGBA.Red);

        if (getAuv_param().isDebugCollision()) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Setting DebugHint for: " + getName(), "");
            Helper.setNodeUserData(auv_node, DebugHint.DebugName, DebugHint.Debug);
        } else {
            Helper.setNodeUserData(auv_node, DebugHint.DebugName, DebugHint.NoDebug);
        }

        auv_node.setLocalTranslation(auv_param.getPosition());
        auv_node.addControl(physics_control);
        auv_node.updateGeometricState();
    }

    /**
     *
     */
    public void setInitialPosition() {
        Vector3f temp_location = new Vector3f(0f, 0f, 0f);
        auv_node.worldToLocal(auv_param.getPosition(), temp_location);
        auv_node.setLocalTranslation(temp_location);
        auv_node.updateGeometricState();
    }

    /*
     * This view is needed for calculating the projected area of the auv. It's used in water resistance calculations.
     */
    private void setupDragOffscreenView() {
        drag_offCamera = new Camera(offCamera_width, offCamera_height);

        //calculate frusturm size so we render the maximum possible of the auv
        BoundingBox boundBox = (BoundingBox) auv_spatial.getWorldBound();
        Vector3f centerBB = boundBox.getCenter();
        Vector3f extBB = boundBox.getExtent(null);

        frustumSize = (float) Math.atan(extBB.length());

        // create a pre-view. a view that is rendered before the main view
        drag_offView = renderManager.createPreView("Offscreen View Area", drag_offCamera);
        drag_offView.setBackgroundColor(ColorRGBA.Green);
        drag_offView.setClearFlags(true, true, true);
        drag_offView.addProcessor(this);

        // create offscreen framebuffer
        drag_offBuffer = new FrameBuffer(offCamera_width, offCamera_height, 1);

        //setup framebuffer's cam
        drag_offCamera.setParallelProjection(true);
        float aspect = (float) offCamera_width / offCamera_height;
        drag_offCamera.setFrustum(-1000, 1000, -aspect * frustumSize, aspect * frustumSize, frustumSize, -frustumSize);
        drag_offCamera.setLocation(centerBB);
        pixel_heigth = ((2 * frustumSize) / offCamera_height);//multiplied by 2 because the frustrumsize counts from the middle
        pixel_width = ((2 * (aspect * frustumSize)) / offCamera_width);
        pixel_area = pixel_heigth * pixel_width;

        //setup framebuffer to use renderbuffer
        // this is faster for gpu -> cpu copies
        drag_offBuffer.setDepthBuffer(Format.Depth);
        drag_offBuffer.setColorBuffer(Format.RGBA8);

        //set viewport to render to offscreen framebuffer
        drag_offView.setOutputFrameBuffer(drag_offBuffer);

        // attach the scene to the viewport to be rendered
        if (auv_param.isEnabled()) {
            drag_offView.attachScene(auv_spatial);//<-- this is the bad boy when registering (modifying the thread blabla)
        }
    }

    /**
     *
     */
    @Override
    public void addDragOffscreenView() {
        drag_offView.attachScene(auv_spatial);
    }

    /**
     *
     */
    @Override
    public void cleanupOffscreenView() {
        drag_offView.setEnabled(false);
        drag_offView.clearProcessors();
        drag_offView.clearScenes();
        renderManager.removePreView(drag_offView);
        if (debug_drag_view != null) {
            debug_drag_view.setEnabled(false);
            debug_drag_view.clearProcessors();
            debug_drag_view.clearScenes();
            renderManager.removePreView(debug_drag_view);
        }
    }

    /*
     * This method is used for getting the offbuffer and count the pixels that aren't green on it
     */
    private float updateImageContents() {
        if (renderer != null) {
            cpuBuf.clear();
            renderer.readFrameBuffer(drag_offBuffer, cpuBuf);

            // copy native memory to java memory
            cpuBuf.clear();
            cpuBuf.get(cpuArray);
            cpuBuf.clear();
            int whites = 0;

            for (int i = 0; i < offCamera_width * offCamera_height * 4; i += 4) {
                byte b = cpuArray[i + 0];
                byte g = cpuArray[i + 1];
                byte r = cpuArray[i + 2];
                byte a = cpuArray[i + 3];
                if (g == -1 && b == 0 && r == 0) {
                    whites++;
                }
            }
            whites = (offCamera_width * offCamera_height) - whites;
            return whites * pixel_area;
        }
        return 0.0f;
    }

    /*
     * Calculates the projected area of the auv.
     */
    private float calculateArea() {
        //center of bb ist needed for correct frustrum to apply
        BoundingBox boundBox = (BoundingBox) auv_spatial.getWorldBound();
        Vector3f centerBB = boundBox.getCenter();
        float waterheight = initer.getCurrentWaterHeight(centerBB.x, centerBB.z);

        //in wich direction are we moving? mirror the vector
        drag_offCamera.setLocation(centerBB.add(physics_control.getLinearVelocity().normalize()));
        drag_offCamera.lookAt(centerBB, Vector3f.UNIT_Y);

        if (auv_param.isDebugDrag()) {
            onCamera.setLocation(centerBB.add(physics_control.getLinearVelocity().normalize()));
            //onCamera.setLocation( centerBB.add(Vector3f.UNIT_X) );
            onCamera.lookAt(centerBB, Vector3f.UNIT_Y);
        }

        if (physics_control.getLinearVelocity().length() != 0f) {//when we have no velocity then we have no water resistance than we dont need an updateForces
            return drag_area_temp;//updateImageContents();
        } else {
            return 0.0f;
        }
    }

    /*
     * A debug view. Lets us see what the calculateArea method "sees"
     */
    private void setupCam2() {
        //extra view for looking what he sees
        onCamera = drag_offCamera.clone();
        onCamera.setViewPort(0f, 0.5f, 0f, 0.5f);
        debug_drag_view = renderManager.createMainView("Onscreen View Area", onCamera);
        debug_drag_view.setBackgroundColor(ColorRGBA.Green);
        debug_drag_view.setClearFlags(true, true, true);
        debug_drag_view.attachScene(auv_spatial);
    }

    /*
     * gets us the completeVolume and completeVolume center of one bracket
     */
    private float[] giveLengthVolumeCenterCollisionAuto(Spatial auv, Vector3f start, boolean ignore_water_height) {
        CollisionResults results = new CollisionResults();
        float ret = 0.0f;
        Vector3f ret2 = new Vector3f(0f, 0f, 0f);
        float ret3 = 0.0f;
        Vector3f ret4 = new Vector3f(0f, 0f, 0f);
        //get the depth of object
        Vector3f first = new Vector3f(0f, 0f, 0f);
        Vector3f second = new Vector3f(0f, 0f, 0f);
        Vector3f ray_start_up = new Vector3f(start.x, start.y, start.z);
        Vector3f ray_direction_up = new Vector3f(0.0f, 1.0f, 0.0f);
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
        //water height for checking what is air and water completeVolume
        /*float waterheight = 0;
         if(mars_settings.isProjectedWavesWaterEnabled()){
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

            if (i % 2 == 0) {//if "i" even then first else second(uneven)
                first = results.getCollision(i).getContactPoint();
                //System.out.println("f " + first);
                if (results.size() % 2 != 0) {
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
                if (first.y == Vector3f.POSITIVE_INFINITY.y) {
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
                if (Helper.infinityCheck(first)) {
                    System.out.println("INFINITY CHECK!");
                    skip_inf = true;
                    break;
                }
            } else {
                if (!skip_inf) {
                    second = results.getCollision(i).getContactPoint();
                    if (!Helper.infinityCheck(second)) {

                        if (second.y > waterheight/*physical_environment.getWater_height()*/ && !ignore_water_height) {
                            //we need to calculate the completeVolume + center above water height
                            float temp_overwater = Math.abs((second.y) - (waterheight));
                            if (temp_overwater != 0.0f) {
                                Vector3f temp3 = new Vector3f(first.x, waterheight + (temp_overwater / 2f), first.z);
                                ret2 = calculateVolumeCentroid(ret2, temp3, ret3 * physical_environment.getAir_density(), temp_overwater * physical_environment.getAir_density());
                                ret3 = ret3 + temp_overwater;
                            }

                            //caluclate under water
                            if (first.y <= waterheight) {
                                second.y = waterheight;//physical_environment.getWater_height();
                                float temp = Math.abs((first.y) - (second.y));
                                if (temp != 0.0f) {
                                    Vector3f temp2 = new Vector3f(first.x, first.y + (temp / 2f), first.z);
                                    ret2 = calculateVolumeCentroid(ret2, temp2, ret * physical_environment.getFluid_density(), temp * physical_environment.getFluid_density());
                                    ret = ret + temp;
                                }
                            }
                            break;//because the water height is the end, or finished
                        }

                        float temp = Math.abs((first.y) - (second.y));
                        if (temp != 0.0f) {
                            Vector3f temp2 = new Vector3f(first.x, first.y + (temp / 2), first.z);
                            ret2 = calculateVolumeCentroid(ret2, temp2, ret * physical_environment.getFluid_density(), temp * physical_environment.getFluid_density());
                            ret = ret + temp;
                        }
                    } else {
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
     * Calculates the center of the completeVolume. It's basicly the same like with normal
     * centroid calculation only that we assume that the completeVolume "weights" everywhere the same.
     */
    private Vector3f calculateVolumeCentroid(Vector3f old_centroid, Vector3f new_centroid, float old_mass, float new_mass) {
        float all_mass = old_mass + new_mass;
        Vector3f ret = new Vector3f((((old_centroid.x * old_mass) + (new_centroid.x * new_mass)) / (all_mass)), (((old_centroid.y * old_mass) + (new_centroid.y * new_mass)) / (all_mass)), (((old_centroid.z * old_mass) + (new_centroid.z * new_mass)) / (all_mass)));
        return ret;
    }

    private float[] calculateVolumeExcact(Spatial auv, boolean ignore_water_height) {
        float[] arr_ret = new float[2];
        float volume = 0f;

        Mesh mesh = BuoyancyGeom.getMesh();

        float waterheightWorld = initer.getCurrentWaterHeight(auv.getWorldTranslation().x, auv.getWorldTranslation().z);
        Vector3f worldToLocal = BuoyancyGeom.worldToLocal(new Vector3f(0f, waterheightWorld, 0f), null);
        //System.out.println("unity: " + Vector3f.UNIT_Y);
        Vector3f worldToLocalUnit = Vector3f.UNIT_Y;//(BuoyancyGeom.worldToLocal(Vector3f.UNIT_Y, null));
        //System.out.println("worldToLocalUnit: " + worldToLocalUnit + " length: " + worldToLocalUnit.length());
        float waterheight = waterheightWorld;//worldToLocal.y;

        /*Arrow arrow = new Arrow(BuoyancyGeom.localToWorld(worldToLocalUnit, null));
         //arrow.set
         final Geometry line3 = new Geometry("tedt", arrow);
         Material mark_mat11 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         mark_mat11.setColor("Color", ColorRGBA.Cyan);
         line3.setMaterial(mark_mat11);
         line3.setLocalTranslation(BuoyancyGeom.localToWorld(new Vector3f(0f, waterheight, 0f), null));
         line3.updateGeometricState();
         Future simStateFutureView3 = mars.enqueue(new Callable() {
         public Void call() throws Exception {
         rootNode.attachChild(line3);
         return null;
         }
         });*/
        ArrayList<Vector3f> polyline = new ArrayList<Vector3f>();

        for (int i = 0; i < mesh.getTriangleCount(); i++) {
            Triangle t = new Triangle();
            mesh.getTriangle(i, t);
            Vector3f a = BuoyancyGeom.localToWorld(t.get1(), null);
            Vector3f b = BuoyancyGeom.localToWorld(t.get2(), null);
            Vector3f c = BuoyancyGeom.localToWorld(t.get3(), null);
            if (ignore_water_height || a.y < waterheight && b.y < waterheight && c.y < waterheight) {//if all vertex of the triangle are underwater we are safe, count them towards normal completeVolume
                float volume_t = Helper.calculatePolyederVolume(a, b, c);
                volume = volume + volume_t;
            } else if (a.y >= waterheight && b.y >= waterheight && c.y >= waterheight) {//if they are abouth water we can forget them because we need only the waterplane
            } else {//we have to check furhter, the triangel is above and under water
                if ((a.y < waterheight && b.y >= waterheight && c.y >= waterheight)) {//when one vertex is under water => case 1 (pretty triangle)
                    Vector3f intersectionWithPlaneB = Helper.getIntersectionWithPlaneCorrect(new Vector3f(0f, waterheight, 0f), worldToLocalUnit, a, b.subtract(a));
                    Vector3f intersectionWithPlaneC = Helper.getIntersectionWithPlaneCorrect(new Vector3f(0f, waterheight, 0f), worldToLocalUnit, a, c.subtract(a));
                    /*Triangle tN = new Triangle();
                     tN.set1(a);
                     tN.set2(intersectionWithPlaneB);
                     tN.set3(intersectionWithPlaneC);*/
                    float volume_t = Helper.calculatePolyederVolume(a, intersectionWithPlaneB, intersectionWithPlaneC);
                    volume = volume + volume_t;
                    //dont forget to add the new vertex to the polylist for later triangulation
                    polyline.add(intersectionWithPlaneB);
                    polyline.add(intersectionWithPlaneC);
                } else if ((a.y >= waterheight && b.y < waterheight && c.y >= waterheight)) {
                    Vector3f intersectionWithPlaneA = Helper.getIntersectionWithPlaneCorrect(new Vector3f(0f, waterheight, 0f), worldToLocalUnit, b, a.subtract(b));
                    Vector3f intersectionWithPlaneC = Helper.getIntersectionWithPlaneCorrect(new Vector3f(0f, waterheight, 0f), worldToLocalUnit, b, c.subtract(b));
                    /*Triangle tN = new Triangle();
                     tN.set1(a);
                     tN.set2(intersectionWithPlaneB);
                     tN.set3(intersectionWithPlaneC);*/
                    float volume_t = Helper.calculatePolyederVolume(intersectionWithPlaneA, b, intersectionWithPlaneC);
                    volume = volume + volume_t;
                    polyline.add(intersectionWithPlaneA);
                    polyline.add(intersectionWithPlaneC);
                } else if ((a.y >= waterheight && b.y >= waterheight && c.y < waterheight)) {
                    /*final Geometry line = new Geometry("tedt", new Line(BuoyancyGeom.localToWorld(c, null), BuoyancyGeom.localToWorld(a, null)));
                     Material mark_mat9 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                     mark_mat9.setColor("Color", ColorRGBA.Cyan);
                     line.setMaterial(mark_mat9);
                     Future simStateFutureView = mars.enqueue(new Callable() {
                     public Void call() throws Exception {
                     rootNode.attachChild(line);
                     return null;
                     }
                     }); 
                     final Geometry line2 = new Geometry("tedt", new Line(BuoyancyGeom.localToWorld(c, null), BuoyancyGeom.localToWorld(b, null)));
                     Material mark_mat10 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                     mark_mat10.setColor("Color", ColorRGBA.Cyan);
                     line2.setMaterial(mark_mat10);
                     Future simStateFutureView2 = mars.enqueue(new Callable() {
                     public Void call() throws Exception {
                     rootNode.attachChild(line2);
                     return null;
                     }
                     });
                     final Geometry line3 = new Geometry("tedt", new Line(BuoyancyGeom.localToWorld(worldToLocal, null), BuoyancyGeom.localToWorld(worldToLocalUnit, null)));
                     Material mark_mat11 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                     mark_mat11.setColor("Color", ColorRGBA.Cyan);
                     line3.setMaterial(mark_mat11);
                     Future simStateFutureView3 = mars.enqueue(new Callable() {
                     public Void call() throws Exception {
                     rootNode.attachChild(line3);
                     return null;
                     }
                     });*/

                    Vector3f intersectionWithPlaneB = Helper.getIntersectionWithPlaneCorrect(new Vector3f(0f, waterheight, 0f), worldToLocalUnit, c, b.subtract(c));
                    Vector3f intersectionWithPlaneA = Helper.getIntersectionWithPlaneCorrect(new Vector3f(0f, waterheight, 0f), worldToLocalUnit, c, a.subtract(c));
                    /*Triangle tN = new Triangle();
                     tN.set1(a);
                     tN.set2(intersectionWithPlaneB);
                     tN.set3(intersectionWithPlaneC);*/
                    float volume_t = Helper.calculatePolyederVolume(intersectionWithPlaneA, intersectionWithPlaneB, c);
                    volume = volume + volume_t;
                    polyline.add(intersectionWithPlaneA);
                    polyline.add(intersectionWithPlaneB);
                } else {//when two vertex are under water => case2 (make two triangles)
                    if (a.y >= waterheight) {//check which vertex is above water, the other ones must be under water due to the check above
                        //we have now to produce 2 triangles
                        //but first check the intersections
                        Vector3f intersectionWithPlaneB = Helper.getIntersectionWithPlaneCorrect(new Vector3f(0f, waterheight, 0f), worldToLocalUnit, a, b.subtract(a));
                        Vector3f intersectionWithPlaneC = Helper.getIntersectionWithPlaneCorrect(new Vector3f(0f, waterheight, 0f), worldToLocalUnit, a, c.subtract(a));

                        //now produce the two triangles
                        float volume_t = Helper.calculatePolyederVolume(intersectionWithPlaneB, b, c);
                        volume = volume + volume_t;
                        volume_t = Helper.calculatePolyederVolume(intersectionWithPlaneB, c, intersectionWithPlaneC);
                        volume = volume + volume_t;

                        //dont forget to add the new vertex to the polylist for later triangulation
                        polyline.add(intersectionWithPlaneB);
                        polyline.add(intersectionWithPlaneC);
                    } else if (b.y >= waterheight) {
                        //we have now to produce 2 triangles
                        //but first check the intersections
                        Vector3f intersectionWithPlaneA = Helper.getIntersectionWithPlaneCorrect(new Vector3f(0f, waterheight, 0f), worldToLocalUnit, b, a.subtract(b));
                        Vector3f intersectionWithPlaneC = Helper.getIntersectionWithPlaneCorrect(new Vector3f(0f, waterheight, 0f), worldToLocalUnit, b, c.subtract(b));

                        //now produce the two triangles
                        float volume_t = Helper.calculatePolyederVolume(a, intersectionWithPlaneA, intersectionWithPlaneC);
                        volume = volume + volume_t;
                        volume_t = Helper.calculatePolyederVolume(a, intersectionWithPlaneC, c);
                        volume = volume + volume_t;

                        //dont forget to add the new vertex to the polylist for later triangulation
                        polyline.add(intersectionWithPlaneA);
                        polyline.add(intersectionWithPlaneC);
                    } else if (c.y >= waterheight) {
                        //we have now to produce 2 triangles
                        //but first check the intersections
                        Vector3f intersectionWithPlaneB = Helper.getIntersectionWithPlaneCorrect(new Vector3f(0f, waterheight, 0f), worldToLocalUnit, c, b.subtract(c));
                        Vector3f intersectionWithPlaneA = Helper.getIntersectionWithPlaneCorrect(new Vector3f(0f, waterheight, 0f), worldToLocalUnit, c, a.subtract(c));

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
        //we calculated the completeVolume of the cut but we forgot the cutting plane
        //since we stored all cutting points (always a pair) we can "triangulize" the cutting plane
        //by taking a point in the middle of the konvex hull as a origin from which we can triangulize
        if (!polyline.isEmpty()) {//it could be that the auv is completely above water
            Vector3f v1 = polyline.get(0);
            Vector3f v2 = polyline.get(5);//<-- buggy!!!
            Vector3f origin = v1.add((v2.subtract(v1)).mult(0.5f));
            float sign = 1.0f;//Math.signum(origin.dot(worldToLocalUnit));//its always up direction because its a clean cut in the x,z plane

            for (int i = 0; i < polyline.size(); i = i + 2) {
                Vector3f vec1 = polyline.get(i);
                Vector3f vec2 = polyline.get(i + 1);
                float volume_t = Helper.calculatePolyederVolume(origin, vec1, vec2, sign);
                volume = volume + volume_t;
            }
            //System.out.println("sign: " + sign);
        }

        //debug polyline
        /*for (int i = 0; i < polyline.size(); i=i+2) {
            
            
         Vector3f vec1 = polyline.get(i);
         Vector3f vec2 = polyline.get(i+1);

         final Geometry line = new Geometry("tedt", new Line(BuoyancyGeom.localToWorld(vec1, null), BuoyancyGeom.localToWorld(vec2, null)));
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
         wbx.fromBoundingBox((BoundingBox)BuoyancyGeom.getWorldBound());
         Geometry boundingBox2 = new Geometry("TheMesh", wbx);
         Material mat_box = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
         mat_box.setColor("Color", ColorRGBA.Blue);
         boundingBox2.setMaterial(mat_box);
         rootNode.attachChild(boundingBox2);
         return null;
         }
         });*/
        arr_ret[0] = volume;
        System.out.println("volume: " + volume + " completeVolume: " + completeVolume + " polySize: " + polyline.size());
        if (volume > completeVolume + 0.1f) {
            System.out.println("too much volume!!!!!!!");
        }
        arr_ret[1] = completeVolume - volume;
        return arr_ret;
    }

    /*
     * Calculates the completeVolume of the auv. Uses a ray-based approach. The rays are fired in a equidistant ciruclar pattern.
     */
    private float[] calculateVolumeAutoRound(Spatial auv, float resolution, boolean ignore_water_height) {
        float[] arr_ret = new float[2];
        float calc_volume = 0.0f;
        float calc_volume_air = 0.0f;
        float old_volume_mass = 0.0f;
        BoundingBox boundBox = (BoundingBox) auv.getWorldBound();
        Vector3f centerBB = boundBox.getCenter();
        Vector3f extBB = boundBox.getExtent(null);
        //System.out.println("centerBB: " + centerBB + " " + "extBB: " + extBB + " " + "maxBB: " + extBB.length() + "/" + extBB.lengthSquared());
        int resolutionCounter = (int) Math.ceil(extBB.length() / resolution);
        //System.out.println("resolutionCounter: " + resolutionCounter);
        Vector3f volume_center = new Vector3f(0f, 0f, 0f);
        //System.out.println("boundingBox.getWorldTranslation(): " + boundingBox.getWorldTranslation());
        //System.out.println("auv_node.getWorldTranslation(): " + auv_node.getWorldTranslation());
        Vector3f ray_start = new Vector3f(boundingBox.getWorldTranslation().x, boundingBox.getWorldTranslation().y, boundingBox.getWorldTranslation().z);

        float radius = extBB.length();

        for (int i = -resolutionCounter; i < resolutionCounter; i++) {
            float heightOfSegment = radius - Math.abs(resolution * i);
            float alpha = 2f * (float) Math.acos(1f - (heightOfSegment / radius));
            float chord = 2f * radius * (float) Math.sin(alpha / 2f);
            float chordStart = chord / 2f;
            //System.out.println("chordStart: " + chordStart);
            float resolutionLengthCounter = (int) Math.rint(chord / resolution);
            for (int j = 0; j < resolutionLengthCounter; j++) {
                Vector3f ray_start_new = new Vector3f((float) (ray_start.x + (i * resolution)), (float) (ray_start.y) - extBB.length() - 0.1f, (float) (ray_start.z + (j * resolution) - chordStart));
                float length = 0.0f;
                float length_air = 0.0f;
                float volume_center_y = 0.0f;
                float[] ret_arr = giveLengthVolumeCenterCollisionAuto(auv, ray_start_new, ignore_water_height);
                length = ret_arr[0];
                length_air = ret_arr[2];
                /*if(length == Float.POSITIVE_INFINITY){
                 System.out.println("inf: " + length);
                 }
                 if(length_air == Float.POSITIVE_INFINITY){
                 System.out.println("inf air: " + length_air);
                 }*/
                // System.out.println("length: " + length);
                if (length != 0) {
                    calc_volume = calc_volume + (length * resolution * resolution);
                    volume_center_y = ret_arr[1];
                    //System.out.println(i + ":" + j + ":p: " + volume_center + " " + length + " " + old_volume_mass);
                    // System.out.println(i + ":" + j + ":p: " + old_volume_mass);
                    volume_center = calculateVolumeCentroid(volume_center, new Vector3f(ray_start_new.x, volume_center_y, ray_start_new.z), old_volume_mass, (length * resolution * resolution));
                    //System.out.println(i + ":" + j + ":p: " + volume_center + " " + length + " " + old_volume_mass);
                    old_volume_mass = old_volume_mass + (length * resolution * resolution);

                    if (auv_param.isDebugBuoycancy()) {
                        Sphere sphere4 = new Sphere(16, 16, 0.00125f);
                        Geometry mark4 = new Geometry("BOOM2!", sphere4);
                        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                        mark_mat4.setColor("Color", ColorRGBA.Green);
                        mark4.setMaterial(mark_mat4);
                        mark4.setLocalTranslation(volume_center);
                        rootNode.attachChild(mark4);
                    }
                }

                if (length_air != 0) {
                    calc_volume_air = calc_volume_air + (length_air * resolution * resolution);
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

                if (auv_param.isDebugBuoycancy()) {
                    Sphere sphere3 = new Sphere(16, 16, 0.0125f);
                    Geometry mark3 = new Geometry("BOOM!", sphere3);
                    Material mark_mat3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    if (length != 0.0f) {
                        mark_mat3.setColor("Color", ColorRGBA.Red);
                    } else {
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
         auv_node.worldToLocal(volume_center_fin, volume_center_local);//NPE!!!!!!!!????????, when updateForces rate = 2

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
        Vector3f volume_center_local = new Vector3f(0f, 0f, 0f);
        try {
            auv_node.worldToLocal(volume_center, volume_center_local);//NPE!!!!!!!!????????, when updateForces rate = 2
        } catch (Exception e) {
            System.out.println("NPE");
        }
        //auv_node.worldToLocal(volume_center, volume_center_local);//NPE!!!!!!!!????????, when updateForces rate = 2

        final Vector3f in = volume_center_local.clone();
        Future<Void> fut = mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                VolumeCenterGeom.setLocalTranslation(in);
                VolumeCenterGeom.updateGeometricState();
                return null;
            }
        });

        if (VolumeCenterPreciseGeom.getWorldTranslation().equals(this.volume_center_precise)) {//save the precise only once
            Future<Void> fut2 = mars.enqueue(new Callable<Void>() {
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

    @Override
    public void updateWaypoints(float tpf) {
        if (auv_param.isDistanceCoveredPathEnabled() && distanceCoveredPath != null) {
            distanceCoveredPath.incTime(tpf);
            if (distanceCoveredPath.getTime() >= auv_param.getDistanceCoveredPathUpdaterate()) {
                distanceCoveredPath.clearTime();
                distanceCoveredPath.addPathPoint(getMassCenterGeom().getWorldTranslation().clone());
                if (auv_param.isDistanceCoveredPathGradient()) {
                    distanceCoveredPath.updateGradient();
                }
            }
        }
    }

    /**
     *
     */
    @Override
    public void publishSensorsOfAUV() {
        for (String elem : sensors.keySet()) {
            Sensor element = sensors.get(elem);
            if (element.isEnabled() && element.isInitialized()) {
                element.publishDataUpdate();
            }
        }
    }

    /**
     *
     */
    @Override
    public void publishActuatorsOfAUV() {
        for (String elem : actuators.keySet()) {
            Actuator element = actuators.get(elem);
            if (element.isEnabled() && element.isInitialized()) {
                element.publishDataUpdate();
            }
        }
    }

    /**
     *
     * @return
     */
    @Override
    public AssetManager getAssetManager() {
        return assetManager;
    }

    @Override
    public String toString() {
        return getName();
    }

    private void resetAllActuators() {
        for (String elem : actuators.keySet()) {
            Actuator element = actuators.get(elem);
            element.reset();
        }
    }

    private void resetAllSensors() {
        for (String elem : sensors.keySet()) {
            Sensor element = sensors.get(elem);
            element.reset();
        }
    }

    private void resetAllAccumulators() {
        for (String elem : accumulators.keySet()) {
            Accumulator element = accumulators.get(elem);
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
        this.rootNode = simstate.getRootNode();
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

    /**
     *
     * @param rm
     * @param vp
     */
    @Override
    public void initialize(RenderManager rm, ViewPort vp) {
    }

    /**
     *
     * @param vp
     * @param w
     * @param h
     */
    @Override
    public void reshape(ViewPort vp, int w, int h) {
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isInitialized() {
        return true;
    }

    /**
     *
     * @param tpf
     */
    @Override
    public void preFrame(float tpf) {
        if (drag_updaterate == 1) {
            drag_area_temp = updateImageContents();
        }
    }

    /**
     *
     * @param rq
     */
    @Override
    public void postQueue(RenderQueue rq) {
    }

    /**
     *
     * @param out
     */
    @Override
    public void postFrame(FrameBuffer out) {
    }

    /**
     *
     */
    @Override
    public void cleanup() {
    }

    /**
     * GUI stuff.
     *
     * @param selected
     */
    @Override
    public void setSelected(boolean selected) {
        if (selected && this.selected == false) {
            if (mars_settings.getGuiAmbientSelection()) {
                ambient_light.setColor(mars_settings.getGuiSelectionColor());
                selectionNode.addLight(ambient_light);
            }
            if (mars_settings.getGuiGlowSelection()) {
                setGlowColor(ghost_auv_spatial, mars_settings.getGuiSelectionColor());
            }
        } else if (selected == false) {
            selectionNode.removeLight(ambient_light);
            setGlowColor(ghost_auv_spatial, ColorRGBA.Black);
        }
        this.selected = selected;
    }

    private void setGlowColor(Spatial spatial, ColorRGBA glow) {
        if (spatial instanceof Node) {
            Node node = (Node) spatial;
            List<Spatial> children = node.getChildren();
            for (Spatial spatial1 : children) {
                if (spatial1 instanceof Geometry) {
                    Geometry geom = (Geometry) spatial1;
                    Material material = geom.getMaterial();
                    material.setColor("GlowColor", glow);
                } else {//go deeper
                    setGlowColor(spatial1, glow);
                }
            }
        }
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isSelected() {
        return selected;
    }

    /**
     *
     * @param visible
     */
    @Override
    public void setCentersVisible(boolean visible) {
        setSpatialVisible(VolumeCenterGeom, visible);
        setSpatialVisible(OldCenterGeom, visible);
        setSpatialVisible(MassCenterGeom, visible);
        setSpatialVisible(VolumeCenterPreciseGeom, visible);
    }

    /**
     *
     * @param visible
     */
    @Override
    public void setVisualizerVisible(boolean visible) {
        for (String elem : actuators.keySet()) {
            Actuator element = actuators.get(elem);
            if (element.isEnabled()) {
                if (element instanceof PointVisualizer) {
                    element.setNodeVisibility(auv_param.isDebugVisualizers());
                } else if (element instanceof VectorVisualizer) {
                    element.setNodeVisibility(auv_param.isDebugVisualizers());
                }
            }
        }
    }

    /**
     *
     * @param visible
     */
    @Override
    public void setPhysicalExchangerVisible(boolean visible) {
        for (String elem : sensors.keySet()) {
            Sensor element = sensors.get(elem);
            if (element.isEnabled()) {
                element.setNodeVisibility(auv_param.isDebugPhysicalExchanger());
            }
        }
        for (String elem : actuators.keySet()) {
            Actuator element = actuators.get(elem);
            if (element.isEnabled() && !(element instanceof PointVisualizer) && !(element instanceof VectorVisualizer)) {
                element.setNodeVisibility(auv_param.isDebugPhysicalExchanger());
            }
        }
    }

    /**
     *
     * @param visible
     */
    @Override
    public void setCollisionVisible(boolean visible) {
        if (visible) {
            Helper.setNodeUserData(auv_node, DebugHint.DebugName, DebugHint.Debug);
        } else {
            Helper.setNodeUserData(auv_node, DebugHint.DebugName, DebugHint.NoDebug);
        }
    }

    /**
     *
     * @param visible
     */
    @Override
    public void setBuoycancyVisible(boolean visible) {
    }

    /**
     *
     * @param visible
     */
    @Override
    public void setDragVisible(boolean visible) {
    }

    /**
     *
     * @param visible
     */
    @Override
    public void setBoundingBoxVisible(boolean visible) {
        setSpatialVisible(boundingBox, visible);
    }

    /**
     *
     * @param visible
     */
    @Override
    public void setBuoyancyVolumeVisible(boolean visible) {
        setSpatialVisible(BuoyancyGeom, visible);
    }

    /**
     *
     * @param spatial
     * @param visible
     */
    protected void setSpatialVisible(Spatial spatial, boolean visible) {
        if (visible) {
            spatial.setCullHint(CullHint.Inherit);
        } else {
            spatial.setCullHint(CullHint.Always);
        }
    }

    /**
     *
     * @param visible
     */
    @Override
    public void setWireframeVisible(boolean visible) {
        List<Spatial> children = new ArrayList<Spatial>();
        if (auv_spatial instanceof Node) {
            Node nodes = (Node) auv_spatial;
            children.addAll(nodes.getChildren());
        } else {//its a spatial or geometry
            children.add(auv_spatial);
        }
        if (visible) {
            for (Spatial spatial : children) {
                if (spatial instanceof Geometry) {
                    Geometry geom = (Geometry) spatial;
                    geom.getMaterial().getAdditionalRenderState().setWireframe(true);
                }
            }
        } else {
            for (Spatial spatial : children) {
                if (spatial instanceof Geometry) {
                    Geometry geom = (Geometry) spatial;
                    geom.getMaterial().getAdditionalRenderState().setWireframe(false);
                }
            }
        }
    }

    /**
     *
     * @param visible
     */
    @Override
    public void setWayPointsVisible(boolean visible) {
        distanceCoveredPath.setPathVisibility(visible);
    }

    /**
     *
     * @param enabled
     */
    @Override
    public void setWaypointsEnabled(boolean enabled) {
    }

    /**
     *
     * @return
     */
    @Override
    public DistanceCoveredPath getDistanceCoveredPath() {
        return distanceCoveredPath;
    }

    private void setNodePickUserData(Spatial spatial) {
        if (spatial instanceof Node) {
            Node node = (Node) spatial;
            node.setUserData(PickHint.PickName, PickHint.NoPick);
            List<Spatial> children = node.getChildren();
            for (Spatial spatial1 : children) {
                setNodePickUserData(spatial1);
            }
        } else {//its a spatial or geom, we dont care because it cant go deeper
            spatial.setUserData(PickHint.PickName, PickHint.NoPick);
        }
    }

    /**
     *
     * @param listener
     */
    @Override
    public void addMARSObjectListener(MARSObjectListener listener) {
        listeners.add(MARSObjectListener.class, listener);
    }

    /**
     *
     * @param listener
     */
    @Override
    public void removeMARSObjectListener(MARSObjectListener listener) {
        listeners.remove(MARSObjectListener.class, listener);
    }

    /**
     *
     */
    @Override
    public void removeAllMARSObjectListener() {
        //listeners.remove(MARSObjectListener.class, null);
    }

    /**
     *
     * @param event
     */
    @Override
    public void notifyAdvertisementMARSObject(MARSObjectEvent event) {
        for (MARSObjectListener l : listeners.getListeners(MARSObjectListener.class)) {
            l.onNewData(event);
        }
    }

    /**
     *
     * @param event
     */
    protected synchronized void notifySafeAdvertisementMARSObject(MARSObjectEvent event) {
        notifyAdvertisementMARSObject(event);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if(evt.getPropertyName().equals("position")) {
            RigidBodyControl pC = getPhysicsControl();
            if(pC != null) {
                pC.setPhysicsLocation((Vector3f) evt.getNewValue());
            }
        }

        /*RigidBodyControl physics_control = auv.getPhysicsControl();
         if (target.equals("position")) {
         if (physics_control != null) {
         physics_control.setPhysicsLocation(getPosition());
         }
         }*//*else if(target.equals("collision") && hashmapname.equals("Debug")){
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
         auv.setDistanceCoveredPathEnabled(isDistanceCoveredPathEnabled());
         }else if(target.equals("visiblity") && hashmapname.equals("Waypoints")){
         auv.setWayPointsVisible(isDistanceCoveredPathVisiblity());
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
}
