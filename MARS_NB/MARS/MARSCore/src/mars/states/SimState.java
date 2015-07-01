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
package mars.states;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.debug.BulletDebugAppState;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import mars.Helper.Helper;
import mars.Initializer;
import mars.KeyConfig;
import mars.MARS_Main;
import mars.MARS_Settings;
import mars.PhysicalEnvironment;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.auv.BasicAUV;
import mars.auv.CommunicationManager;
import mars.simobjects.SimObject;
import mars.simobjects.SimObjectManager;
import mars.xml.XML_JAXB_ConfigReaderWriter;
import javax.swing.TransferHandler;
import mars.misc.Collider;
import mars.misc.MyDebugAppStateFilter;
import mars.core.CentralLookup;
import mars.core.MARSMapTopComponent;
import mars.core.MARSTopComponent;
import mars.xml.ConfigManager;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Lookup;

/**
 * The main MARS AppState. Here is where the MARS magic is happening.
 *
 * @author Thomas Tosik
 */
public class SimState extends MARSAppState implements PhysicsTickListener, AppStateExtension{

    private Node rootNode = new Node("SimState Root Node");
    private AssetManager assetManager;
    private InputManager inputManager;
    private AUV_Manager auvManager;
    private SimObjectManager simobManager;
    private CommunicationManager comManager;
    private BulletAppState bulletAppState;
    private MARS_Main mars;

    private boolean initial_ready = false;

    //needed for graphs
    private MARSTopComponent MARSTopComp;
    private MARSMapTopComponent MARSMapComp;
    private boolean view_init = false;
    private boolean server_init = false;
    private boolean debugFilter = false;
    private boolean init = false;

    //main settings file
    private MARS_Settings mars_settings;
    private KeyConfig keyconfig;
    private PhysicalEnvironment physical_environment;
    private Initializer initer;
    private ArrayList<AUV> auvs = new ArrayList<AUV>();
    private ArrayList<SimObject> simobs = new ArrayList<SimObject>();
    private XML_JAXB_ConfigReaderWriter xml;
    private ConfigManager configManager;

    private ChaseCamera chaseCam;

    //water
    private Node sceneReflectionNode = new Node("sceneReflectionNode");
    private Collider RayDetectable = new Collider();
    private Node AUVNodes = new Node("AUVNodes");
    private Node SimObNodes = new Node("SimObNodes");
    //warter currents
    private Node currents = new Node("currents");

    @SuppressWarnings("unchecked")
    private Future<Void> simStateFuture = null;

    //map stuff
    private MapState mapState;

    //progress bar (nb)
    private final ProgressHandle progr = ProgressHandleFactory.createHandle("SimState");

    /**
     *
     * @param assetManager
     */
    public SimState(AssetManager assetManager) {
        this.assetManager = assetManager;
        setupLogger();
    }

    /**
     *
     * @param MARSTopComp
     * @param MARSMapComp
     * @param configManager
     */
    public SimState(MARSTopComponent MARSTopComp, MARSMapTopComponent MARSMapComp, ConfigManager configManager) {
        this.MARSTopComp = MARSTopComp;
        this.MARSMapComp = MARSMapComp;
        this.configManager = configManager;
    }
    
    private void setupLogger(){
        //setup logging
        Handler[] handlers = Logger.getLogger(this.getClass().getName()).getHandlers();
        for (Handler handler : handlers) {
            handler.setLevel(Level.parse(getMARSSettings().getLoggingLevel()));
            Logger.getLogger(this.getClass().getName()).setLevel(Level.parse(getMARSSettings().getLoggingLevel()));

            if(!getMARSSettings().getLoggingFileWrite()){
                handler.setLevel(Level.OFF);
            }
        }
        if(!getMARSSettings().getLoggingEnabled()){
            Logger.getLogger(this.getClass().getName()).setLevel(Level.OFF);
        }
    }

    /**
     *
     * @return
     */
    @Override
    public Node getRootNode() {
        return rootNode;
    }

    /**
     *
     * @param cam
     */
    @Override
    public void setCamera(Camera cam) {

    }

    /**
     *
     */
    @Override
    public void cleanup() {
        super.cleanup();

        //cleanup the initer (viewport, filters)
        initer.cleanup();

        //clean up all auvs (offscreen view from drag for example)
        auvManager.cleanup();
        simobManager.cleanup();

        //cleanup gui state
        //clean the cameras
        chaseCam.setEnabled(false);
        chaseCam = null;

        //deattach the state root node from the main 
        getRootNode().removeFromParent();
        getRootNode().detachAllChildren();
        
        //clear cntralLookup
        CentralLookup.getDefault().remove(auvManager);
        CentralLookup.getDefault().remove(physical_environment);
        CentralLookup.getDefault().remove(simobManager);
        
        //cleanup other related states
        //bulletAppState.setEnabled(false);
        //mars.getStateManager().detach(bulletAppState);
        //bulletAppState = null;

        /*if (mars.getStateManager().getState(GuiState.class) != null) {
            GuiState guistate = mars.getStateManager().getState(GuiState.class);
            guistate.setEnabled(false);
            mars.getStateManager().detach(guistate);
            guistate = null;
        }*/
    }

    /**
     *
     * @param stateManager
     * @param app
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {

        //starting progressbar nb
        progr.start();
        progr.progress("Starting SimState");

        if (!super.isInitialized()) {
            if (app instanceof MARS_Main) {
                mars = (MARS_Main) app;
                assetManager = mars.getAssetManager();
                inputManager = mars.getInputManager();
                mars.getRootNode().attachChild(getRootNode());
            } else {
                throw new RuntimeException("The passed application is not of type \"MARS_Main\"");
            }

            progr.progress("Adding Nodes");
            sceneReflectionNode.attachChild(AUVNodes);
            sceneReflectionNode.attachChild(SimObNodes);
            rootNode.attachChild(sceneReflectionNode);
            rootNode.attachChild(currents);

            progr.progress("Loading Nifty");
            initNiftyLoading();
            progr.progress("Starting Nifty");
            startNiftyState();
            progr.progress("Loading Config");
            loadXML(configManager.getConfigName());
            //setupLogger();
            progr.progress("Starting Physics");
            setupPhysics();
            progr.progress("Starting Cameras");
            setupCams();

            progr.progress("Creating Managers");
            auvManager = new AUV_Manager(this);
            simobManager = new SimObjectManager(this);
            comManager = new CommunicationManager(auvManager, this, rootNode, physical_environment);

            progr.progress("Creating Initializer");
            initer = new Initializer(mars, this, auvManager, comManager, physical_environment);
            initer.init();

            //set camera to look to (0,0,0)
            setupCamPos();
            mars.getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);

            comManager.setServer(initer.getRAW_Server());

            if (mars_settings.getROSEnabled()) {
                if (initer.checkROSServer()) {//Waiting for ROS Server to be ready

                }
            }

            progr.progress("Init Map");
            initMap();//for mars_settings

            progr.progress("Populate AUVManager");
            populateAUV_Manager(auvs, physical_environment, mars_settings, comManager, initer);
            
            mars.enqueue(new Callable<Void>() {
                public Void call() throws Exception {
                    CentralLookup.getDefault().add(auvManager);
                    CentralLookup.getDefault().add(physical_environment);
                    return null;
                }
            });

            progr.progress("Populate SimObjectManager");
            populateSim_Object_Manager(simobs);
            mars.enqueue(new Callable<Void>() {
                public Void call() throws Exception {
                    CentralLookup.getDefault().add(simobManager);
                    return null;
                }
            });

            progr.progress("Init View");
            initView();

            init = true;
            
            progr.progress("Init GuiState");
            final GuiState guiState = new GuiState();
            guiState.setAuvManager(auvManager);
            guiState.setSimobManager(simobManager);
            guiState.setIniter(initer);
            guiState.setAUVsNode(AUVNodes);
            guiState.setSimObNode(SimObNodes);
            guiState.setMars_settings(mars_settings);
            guiState.setSimState(this);
            final AppStateManager stateManagerFin = stateManager;
            
            @SuppressWarnings("unchecked")
            Future<Void> fut2 = mars.enqueue(new Callable<Void>() {
                public Void call() throws Exception {
                    getMARS().getViewPort().attachScene(guiState.getRootNode());
                    stateManagerFin.attach(guiState);
                    return null;
                }
            });

            progr.progress("Init other States");
            progr.progress("Init FishSwarm State");
            Lookup lkp = Lookup.getDefault();
            AbstractAppState state = lkp.lookup(AbstractAppState.class);
            if (state != null) {
                stateManager.attach(state);
            }
        }
        progr.progress("Init Super");
        super.initialize(stateManager, app);

        progr.finish();
    }

    private void initView() {
        MARSTopComp.setMarsSettings(mars_settings);
        MARSTopComp.setPenv(physical_environment);
        MARSTopComp.setKeyConfig(keyconfig);
        MARSTopComp.setConfigManager(configManager);
        MARSTopComp.setAuv_manager(auvManager);
        MARSTopComp.setSimob_manager(simobManager);
        MARSTopComp.initDND();
        MARSTopComp.allowSimInteraction();
        MARSMapComp.initDND();

        if (mars_settings.getROSEnabled()) {
            if (initer.checkROSServer()) {
                MARSTopComp.allowServerInteraction(true);
            } else {
                MARSTopComp.allowServerInteraction(false);
            }
        } else {
            MARSTopComp.allowServerInteraction(false);
        }
    }

    /**
     *
     */
    public void connectToServer() {
        mars_settings.setROSEnabled(true);
        initer.setupServer();
        if (initer.checkROSServer()) {
            MARSTopComp.allowServerInteraction(true);
        } else {
            MARSTopComp.allowServerInteraction(false);
        }
    }

    /**
     *
     */
    public void disconnectFromServer() {
        mars_settings.setROSEnabled(false);
        MARSTopComp.enableServerInteraction(false);
        initer.killServer();
        MARSTopComp.allowServerInteraction(false);
    }

    /**
     *
     * @param enable
     */
    public void enablePublishing(boolean enable) {
        mars_settings.setROSPublish(enable);
    }

    @SuppressWarnings("unchecked")
    private void initMap() {
        Future<Void> fut = mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                mapState.loadMap(mars_settings.getTerrainColorMap());
                mapState.setMars_settings(mars_settings);
                mapState.setAuv_manager(auvManager);
                return null;
            }
        });
    }

    /*
     * Initialize the Bullet physics. Enable Bullet AppState.
     */
    private void setupPhysics() {
        bulletAppState = new BulletAppState();
        bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        mars.getStateManager().attach(bulletAppState);
        //set the physis world parameters
        bulletAppState.getPhysicsSpace().setMaxSubSteps(mars_settings.getPhysicsMaxsubsteps());

        //setting Filter in the DebugState so we can show specific collision boxes
        /*if (mars.getStateManager().getState(BulletDebugAppState.class) != null) {
         mars.getStateManager().getState(BulletDebugAppState.class).setFilter(new MyDebugAppStateFilter()); 
         }*/ //doesnt work here because DebugAppState suuuuuucks
        if (mars_settings.getPhysicsDebug()) {
            bulletAppState.setDebugEnabled(true);
        }

        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0.0f, 0.0f, 0.0f));
        bulletAppState.getPhysicsSpace().setAccuracy(1f / mars_settings.getPhysicsFramerate());
        bulletAppState.getPhysicsSpace().addTickListener(this);
        physical_environment.setBulletAppState(bulletAppState);

        bulletAppState.setEnabled(false);
    }

    /*
     * Initialize the FlyByCamera and ChaseCamera.
     */
    private void setupCams() {
        mars.getFlyByCamera().setMoveSpeed(mars_settings.getCameraFlyCamMoveSpeed());
        mars.getFlyByCamera().setEnabled(true);
        chaseCam = new ChaseCamera(mars.getCamera(), rootNode, inputManager);
        chaseCam.setInvertVerticalAxis(true);
        chaseCam.setZoomSensitivity(mars_settings.getCameraChaseCamZoomSensitivity());
        chaseCam.setEnabled(false);
    }

    /*
     *
     */
    private void setupCamPos() {
        moveCamera(mars_settings.getCameraDefaultPosition(), false);
        rotateCamera(mars_settings.getCameraDefaultRotation(), false);
    }

    /*
     * Load the xml configuration.
     */
    private void loadXML(String config) {
        try {
            xml = new XML_JAXB_ConfigReaderWriter(config);
            keyconfig = xml.loadKeyConfig();
            mars_settings = xml.loadMARS_Settings();
            physical_environment = xml.loadPhysicalEnvironment();
            mars_settings.setPhysical_environment(physical_environment);
            auvs = xml.loadAUVs();

            //do stuff after jaxb, see also UnmarshallListener
            Iterator<AUV> iter = auvs.iterator();
            while (iter.hasNext()) {
                AUV bas_auv = iter.next();
                bas_auv.getAuv_param().setAuv(bas_auv);
                bas_auv.setName(bas_auv.getAuv_param().getName());
                bas_auv.setState(this);
                bas_auv.setMARS_Settings(mars_settings);
                bas_auv.setupLogger();
            }
            simobs = xml.loadSimObjects();
        } catch (Exception ex) {
            Logger.getLogger(SimState.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*
     * Setup the AUVManager and put AUVs into it.
     */
    private void populateAUV_Manager(ArrayList<AUV> auvs, PhysicalEnvironment pe, MARS_Settings mars_settings, CommunicationManager com_manager, Initializer initer) {
        auvManager.setBulletAppState(bulletAppState);
        auvManager.setPhysical_environment(pe);
        auvManager.setMARS_settings(mars_settings);
        auvManager.setCommunicationManager(com_manager);
        if (mars_settings.getROSEnabled()) {
            auvManager.setMARSNodes(initer.getROS_Server().getMarsNodes());
        }
        auvManager.registerAUVs(auvs);
    }

    /*
     *
     */
    private void populateSim_Object_Manager(ArrayList<SimObject> simobs) {
        simobManager.setBulletAppState(bulletAppState);
        simobManager.registerSimObjects(simobs);
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isInitialized() {
        return super.isInitialized();
    }

    /**
     *
     */
    @Override
    public void postRender() {
        if (!super.isEnabled()) {
            return;
        }
        super.postRender();
    }

    /**
     *
     * @param rm
     */
    @Override
    public void render(RenderManager rm) {
        if (!super.isEnabled()) {
            return;
        }
        super.render(rm);
    }

    /**
     *
     * @param enabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            rootNode.setCullHint(Spatial.CullHint.Always);
        } else {
            rootNode.setCullHint(Spatial.CullHint.Never);
        }
    }

    /**
     *
     * @param stateManager
     */
    @Override
    public void stateAttached(AppStateManager stateManager) {
        super.stateAttached(stateManager);
    }

    /**
     *
     * @param stateManager
     */
    @Override
    public void stateDetached(AppStateManager stateManager) {
        super.stateDetached(stateManager);
    }

    /**
     * The main update loop of MARS.
     *
     * @param tpf
     */
    @Override
    public void update(float tpf) {
        if (!super.isEnabled()) {
            return;
        }
        super.update(tpf);

        if (MARSTopComp == null) {
            System.out.println("MARSTopComp is NULL");
        }

        if (mars_settings.isWavesWaterEnabled()) {
            initer.updateWavesWater(tpf);
        }

        if (mars_settings.isProjectedWavesWaterEnabled()) {
            initer.updateProjectedWavesWater(tpf);
        }

        if (mars_settings.isGrassEnabled()) {
            initer.updateGrass(tpf);
        }

        if (initer != null && initer.getSkyControl() != null) {
            if (getMARSSettings().getSkyDomeSpeed() != 0f) {
                if (initer.getTimeOfDay().isInitialized()) {
                    initer.getTimeOfDay().update(tpf);
                    initer.getTimeOfDay().setRate(getMARSSettings().getSkyDomeSpeed() * getMARSSettings().getSkyDomeDirection());
                    initer.getSkyControl().getSunAndStars().setHour(initer.getTimeOfDay().getHour());
                }
            }
        }
    }

    /**
     *
     * @return
     */
    public SimObjectManager getSimob_manager() {
        return simobManager;
    }

    /**
     *
     * @return
     */
    public AUV_Manager getAuvManager() {
        return auvManager;
    }

    /**
     *
     * @return
     */
    public BulletAppState getBulletAppState() {
        return bulletAppState;
    }

    /**
     *
     * @return
     */
    public Node getSceneReflectionNode() {
        return sceneReflectionNode;
    }

    /**
     *
     * @return
     */
    public Node getAUVNodes() {
        return AUVNodes;
    }

    /**
     *
     * @return
     */
    public Collider getCollider() {
        return RayDetectable;
    }

    /**
     *
     * @return
     */
    public Node getSimObNodes() {
        return SimObNodes;
    }

    /**
     *
     * @return
     */
    public MARS_Settings getMARSSettings() {
        return mars_settings;
    }

    /**
     *
     * @return
     */
    public ChaseCamera getChaseCam() {
        return chaseCam;
    }

    /**
     *
     * @return
     */
    public Initializer getIniter() {
        return initer;
    }

    /**
     *
     * @return
     */
    public MARS_Main getMARS() {
        return mars;
    }

    /**
     *
     * @return
     */
    public KeyConfig getKeyconfig() {
        return keyconfig;
    }

    /**
     *
     * @return
     */
    public AssetManager getAssetManager() {
        return assetManager;
    }

    /**
     *
     * @param ps
     * @param tpf
     */
    @Override
    public void prePhysicsTick(PhysicsSpace ps, final float tpf) {
        if (!super.isEnabled()) {
            return;
        }
        //limit velocitys
        /*Collection<PhysicsRigidBody> rigidBodyList = ps.getRigidBodyList();
         //System.out.println("rbody size: " + rigidBodyList.size());
         for (PhysicsRigidBody physicsRigidBody : rigidBodyList) {
         Vector3f angularVelocity = physicsRigidBody.getAngularVelocity();
         System.out.println("angular_drag_torque_vec: " + angularVelocity.length() + " " + angularVelocity);
         float speed = angularVelocity.length();
         if(Helper.infinityCheck(angularVelocity)){
         System.out.println("INF!!!!!");
         physicsRigidBody.setAngularVelocity(Vector3f.ZERO);
         }
         if(speed > 1f) {
         angularVelocity.mult(1f/speed);
         physicsRigidBody.setAngularVelocity(angularVelocity);
         }
         }*/

        /*AUV auv = auvManager.getAUV("hanse");
         if(auv != null){
         GhostControl ghostControl = auv.getGhostControl();
         if(ghostControl != null){
         List<PhysicsCollisionObject> overlappingObjects = ghostControl.getOverlappingObjects();
         for (PhysicsCollisionObject physicsCollisionObject : overlappingObjects) {
         System.out.println(physicsCollisionObject.toString());
         }
         System.out.println("ghostControl.getOverlappingCount(): " + ghostControl.getOverlappingCount());
         }
         }*/

        //setting Filter in the DebugState so we can show specific collision boxes
        if (mars.getStateManager().getState(BulletDebugAppState.class) != null) {
            if (!debugFilter && init) {
                Logger.getLogger(SimState.class.getName()).log(Level.INFO, "Initialiazing DebugAppStateFilter...", "");
                mars.getStateManager().getState(BulletDebugAppState.class).setFilter(new MyDebugAppStateFilter(mars_settings, auvManager));
                debugFilter = true;
            }
        }

        //only update physics when simulation is started and auv_manager/comManager are both ready and instantied.
        if (auvManager != null && initial_ready && comManager != null) {
            auvManager.updateAllAUVs(tpf);
            comManager.update(tpf);
        }
    }

    /**
     *
     * @param ps
     * @param tpf
     */
    @Override
    public void physicsTick(PhysicsSpace ps, float tpf) {
        if (!super.isEnabled()) {
            return;
        }
        /*//limit velocitys
         Collection<PhysicsRigidBody> rigidBodyList = ps.getRigidBodyList();
         //System.out.println("rbody size: " + rigidBodyList.size());
         for (PhysicsRigidBody physicsRigidBody : rigidBodyList) {
         Vector3f angularVelocity = physicsRigidBody.getAngularVelocity();
         float speed = angularVelocity.length();
         if(speed > 1f) {
         angularVelocity.mult(1f/speed);
         physicsRigidBody.setAngularVelocity(angularVelocity);
         }
         }*/
        //System.out.println("PHYSICS: " + tpf);
    }

    private void initNiftyLoading() {
    }

    /**
     *
     */
    public void startSimulation() {
        simStateFuture = mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                bulletAppState.getPhysicsSpace().setGravity(physical_environment.getGravitational_acceleration_vector());
                initial_ready = true;
                MARSTopComp.allowPhysicsInteraction(true);
                bulletAppState.setEnabled(true);
                System.out.println("Simulation started...");
                return null;
            }
        });
    }

    /**
     *
     */
    public void pauseSimulation() {
        simStateFuture = mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                bulletAppState.setEnabled(false);
                bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0.0f, 0.0f, 0.0f));
                auvManager.clearForcesOfAUVs();
                initial_ready = false;
                MARSTopComp.allowPhysicsInteraction(false);
                System.out.println("Simulation stopped...");
                return null;
            }
        });
    }

    /**
     *
     */
    public void restartSimulation() {
        simStateFuture = mars.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                System.out.println("Simulation reseted...");
                auvManager.resetAllAUVs();
                return null;
            }
        });
    }

    /**
     *
     * @param mapState
     */
    public void setMapState(MapState mapState) {
        this.mapState = mapState;
    }

    /**
     *
     * @param new_position
     * @param relative
     */
    public void moveCamera(Vector3f new_position, boolean relative) {
        if (!relative) {
            mars.getCamera().setLocation(new_position);
        } else {
            mars.getCamera().setLocation(mars.getCamera().getLocation().add(new_position));
        }
    }

    /**
     *
     * @param new_rotation
     * @param relative
     */
    public void rotateCamera(Vector3f new_rotation, boolean relative) {
        //System.out.println("rotateCamera" + new_rotation);
        if (!relative) {
            Quaternion quat = new Quaternion();
            quat.fromAngles(new_rotation.getX(), new_rotation.getY(), new_rotation.getZ());
            mars.getCamera().setRotation(quat);
        }
    }

    /**
     * Chase the corresponding AUV with the ChaseCam.
     *
     * @param auv
     */
    public void chaseAUV(AUV auv) {
        if (auv != null) {
            mars.getFlyByCamera().setEnabled(false);
            mars.getChaseCam().setSpatial(auv.getAUVNode());
            mars.getChaseCam().setEnabled(true);
        }
    }

    /**
     * Enables an AUV and sets it to the position. If already enabled then
     * position change. The position is computed from he screen position.
     *
     * @param auvName
     * @param pos
     * @param dropAction
     * @param name
     */
    public void enableAUV(String auvName, Point pos, int dropAction, String name) {
        AUV auv = auvManager.getAUV(auvName);
        if (auv != null) {
            Vector3f click3d = mars.getCamera().getWorldCoordinates(new Vector2f(pos.x, mars.getCamera().getHeight() - pos.y), 0f).clone();
            Vector3f dir = mars.getCamera().getWorldCoordinates(new Vector2f(pos.x, mars.getCamera().getHeight() - pos.y), 1f).subtractLocal(click3d);
            Vector3f intersection = Helper.getIntersectionWithPlaneCorrect(new Vector3f(0f, initer.getCurrentWaterHeight(pos.x, mars.getCamera().getHeight() - pos.y), 0f), Vector3f.UNIT_Y, click3d, dir);
            if (dropAction == TransferHandler.COPY) {
                AUV auvCopy = new BasicAUV(auv);
                auvCopy.getAuv_param().setAuv(auvCopy);
                auvCopy.setName(name);
                auvCopy.getAuv_param().setPosition(intersection);
                auvCopy.setState(this);
                auvManager.registerAUV(auvCopy);
            } else {
                if (auv.getAuv_param().isEnabled()) {//check if auf auv already enabled, then only new position
                    auv.getAuv_param().setPosition(intersection);
                    auv.getPhysicsControl().setPhysicsLocation(intersection);
                } else {
                    auv.getAuv_param().setPosition(intersection);
                    auv.getAuv_param().setEnabled(true);
                    auvManager.enableMARSObject(auv, true);
                    auv.getPhysicsControl().setPhysicsLocation(intersection);
                }
            }
        }
    }

    /**
     * Enables an AUV and sets it to the position. If already enabled then
     * position change.
     *
     * @param auvName
     * @param pos
     * @param dropAction
     * @param name
     */
    public void enableAUV(String auvName, Vector3f pos, int dropAction, String name) {
        AUV auv = auvManager.getAUV(auvName);
        pos.y = initer.getCurrentWaterHeight(pos.x, mars.getCamera().getHeight() - pos.y);
        if (auv != null) {
            if (dropAction == TransferHandler.COPY) {
                AUV auvCopy = new BasicAUV(auv);
                auvCopy.getAuv_param().setAuv(auvCopy);
                auvCopy.setName(name);
                auvCopy.getAuv_param().setPosition(pos);
                auvCopy.setState(this);
                auvManager.registerAUV(auvCopy);
                /*view.updateTrees();
                 Future simStateFutureView = mars.enqueue(new Callable() {
                 public Void call() throws Exception {
                 if(view != null){
                 view.updateTrees(); 
                 }
                 return null;
                 }
                 }); */
            } else {
                if (auv.getAuv_param().isEnabled()) {//check if auf auv already enabled, then only new position
                    auv.getAuv_param().setPosition(pos);
                    auv.getPhysicsControl().setPhysicsLocation(pos);
                } else {
                    auv.getAuv_param().setPosition(pos);
                    auv.getAuv_param().setEnabled(true);
                    auvManager.enableMARSObject(auv, true);
                    auv.getPhysicsControl().setPhysicsLocation(pos);
                }
            }
        }
    }

    /**
     * Enables an SimObject and sets it to the position. If already enabled then
     * position change.
     *
     * @param simobName
     * @param pos
     * @param dropAction
     * @param name
     */
    public void enableSIMOB(String simobName, Vector3f pos, int dropAction, String name) {
        SimObject simob = simobManager.getSimObject(simobName);
        pos.y = initer.getCurrentWaterHeight(pos.x, mars.getCamera().getHeight() - pos.y);
        if (simob != null) {
            if (dropAction == TransferHandler.COPY) {
                SimObject simobCopy = simob.copy();
                simobCopy.setName(name);
                simobCopy.setPosition(pos);
                simobManager.registerSimObject(simobCopy);
            } else {
                if (simob.isEnabled()) {//check if auf simob already enabled, then only new position
                    simob.setPosition(pos);
                    simob.getPhysicsControl().setPhysicsLocation(pos);
                } else {
                    simob.setPosition(pos);
                    simob.setEnabled(true);
                    simobManager.enableMARSObject(simob, true);
                    simob.getPhysicsControl().setPhysicsLocation(pos);
                }
            }
        }
    }

    /**
     * Enables an AUV and sets it to the position. If already enabled then
     * position change. The position is computed from he screen position.
     *
     * @param simobName
     * @param pos
     * @param dropAction
     * @param name
     */
    public void enableSIMOB(String simobName, Point pos, int dropAction, String name) {
        SimObject simob = simobManager.getSimObject(simobName);
        if (simob != null) {
            Vector3f click3d = mars.getCamera().getWorldCoordinates(new Vector2f(pos.x, mars.getCamera().getHeight() - pos.y), 0f).clone();
            Vector3f dir = mars.getCamera().getWorldCoordinates(new Vector2f(pos.x, mars.getCamera().getHeight() - pos.y), 1f).subtractLocal(click3d);
            Vector3f intersection = Helper.getIntersectionWithPlaneCorrect(new Vector3f(0f, initer.getCurrentWaterHeight(pos.x, mars.getCamera().getHeight() - pos.y), 0f), Vector3f.UNIT_Y, click3d, dir);
            if (dropAction == TransferHandler.COPY) {
                SimObject simobCopy = simob.copy();
                simobCopy.setName(name);
                simobCopy.setPosition(intersection);
                simobManager.registerSimObject(simobCopy);
                /*view.updateTrees();
                 Future simStateFutureView = mars.enqueue(new Callable() {
                 public Void call() throws Exception {
                 if(view != null){
                 view.updateTrees(); 
                 }
                 return null;
                 }
                 }); */
            } else {
                if (simob.isEnabled()) {//check if auf simob already enabled, then only new position
                    simob.setPosition(intersection);
                    simob.getPhysicsControl().setPhysicsLocation(intersection);
                } else {
                    simob.setPosition(intersection);
                    simob.setEnabled(true);
                    simobManager.enableMARSObject(simob, true);
                    simob.getPhysicsControl().setPhysicsLocation(intersection);
                }
            }
        }
    }

    /**
     *
     */
    public void startNiftyState() {
        if (mars.getStateManager().getState(NiftyState.class) != null) {
            mars.getStateManager().getState(NiftyState.class).show();
        }
    }

    /**
     *
     * @return
     */
    public MARSTopComponent getMARSTopComp() {
        return MARSTopComp;
    }

    /**
     *
     * @param MARSTopComp
     */
    public void setMARSTopComp(MARSTopComponent MARSTopComp) {
        this.MARSTopComp = MARSTopComp;
    }
}
