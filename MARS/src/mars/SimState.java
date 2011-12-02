/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapFont;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import mars.actuators.BrushlessThruster;
import mars.actuators.SeaBotixThruster;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.auv.AUV_Parameters;
import mars.auv.BasicAUV;
import mars.auv.Communication_Manager;
import mars.auv.example.Hanse;
import mars.auv.example.Monsun2;
import mars.gui.MARSView;
import mars.sensors.InfraRedSensor;
import mars.simobjects.SimObject;
import mars.simobjects.SimObjectManager;
import mars.xml.XMLConfigReaderWriter;
import mars.xml.XML_JAXB_ConfigReaderWriter;

/**
 *
 * @author Thomas Tosik
 */
public class SimState extends AbstractAppState implements PhysicsTickListener{

    private Node rootNode = new Node("Root Node");
    private AssetManager assetManager;
    private InputManager inputManager;
    private AUV_Manager auv_manager;
    private SimObjectManager simob_manager;
    private Communication_Manager com_manager;
    private BulletAppState bulletAppState;
    private MARS_Main mars;
    
    private boolean initial_ready = false;
        
    //physics
    private RigidBodyControl AUVPhysicsControl;

    private float time = 0f;

    //needed for graphs
    private MARSView view;
    private boolean view_init = false;
    private boolean man_init = false;
    
    //main settings file
    MARS_Settings mars_settings;
    PhysicalEnvironment physical_environment;
    Initializer initer;
    ArrayList auvs = new ArrayList();
    ArrayList simobs = new ArrayList();
    XMLConfigReaderWriter xmll;
    
    ChaseCamera chaseCam;
    
    //water
    private Node sceneReflectionNode = new Node("sceneReflectionNode");
    private Node SonarDetectableNode = new Node("SonarDetectableNode");
    
    private Hanse auv_hanse;
    private Monsun2 auv_monsun2;
    //we need the motors here because we want to steer them(actionlistener)
    private SeaBotixThruster mot1left;
    private SeaBotixThruster mot2left;
    private SeaBotixThruster mot1right;
    private SeaBotixThruster mot2right;
    
    private BrushlessThruster motb1_push;
    private BrushlessThruster motb2_push;
    private BrushlessThruster mot_rightfront;
    private BrushlessThruster mot_leftfront;
    private BrushlessThruster mot_rightback;
    private BrushlessThruster mot_leftback;

    
    /**
     * 
     * @param assetManager
     */
    public SimState(AssetManager assetManager) {
        this.assetManager = assetManager;
    }
    
    /**
     * 
     * @param view
     */
    public SimState(MARSView view) {
        this.view = view;
    }
        
    /**
     * 
     * @return
     */
    public Node getRootNode(){
        return rootNode;
    }
    
    @Override
    public void cleanup() {
        rootNode.detachAllChildren();
        mars = null;
        assetManager = null;
        mars_settings = null;
        super.cleanup();
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        if(!super.isInitialized()){
            if(app instanceof MARS_Main){
                mars = (MARS_Main)app;
                assetManager = mars.getAssetManager();
                inputManager = mars.getInputManager();
            }else{
                throw new RuntimeException("The passed application is not of type \"MARS_Main\"");
            }
                    
            sceneReflectionNode.attachChild(SonarDetectableNode);
            rootNode.attachChild(sceneReflectionNode);
            
            loadXML();
            initKeys();// load custom key mappings
            setupPhysics();
            setupGUI();
            setupCams();
        
            auv_manager = new AUV_Manager(this);
            simob_manager = new SimObjectManager(this);
            com_manager = new Communication_Manager(auv_manager, this, rootNode, physical_environment);
        
            initer = new Initializer(mars,this,auv_manager,com_manager);
            initer.init();
            
            com_manager.setServer(initer.getRAW_Server());

            if(mars_settings.isROS_Server_enabled()){
                Logger.getLogger(SimState.class.getName()).log(Level.INFO, "Waiting for ROS Server to be ready...", "");
                while(!initer.isROS_ServerReady()){
                    
                }
                while(initer.getROS_Server().getMarsNode() == null){
                    
                }
                while(!initer.getROS_Server().getMarsNode().isExisting()){
                //while(initer.getROS_Server().getNode() == null){
                    
                }
                while(!initer.getROS_Server().getMarsNode().isRunning()){
                    
                }
            }
            
            populateAUV_Manager(auvs,physical_environment,mars_settings,com_manager,initer);
            populateSim_Object_Manager(simobs);
            
            /*JAXBContext context;
            try {*/
                /*context = JAXBContext.newInstance( MARS_Settings.class );
                Marshaller m = context.createMarshaller();
                m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                m.marshal( mars_settings, System.out );*/
                
                //XML_JAXB_ConfigReaderWriter xabl = new XML_JAXB_ConfigReaderWriter();
                //XML_JAXB_ConfigReaderWriter.loadSimObjects(); 
                
                /*File file = new File( "./xml/simobjects/room.xml" );
                context = JAXBContext.newInstance( SimObject.class );
                Marshaller m = context.createMarshaller();
                Unmarshaller u = context.createUnmarshaller();
                m = context.createMarshaller();
                m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                SimObject blo = (SimObject)simobs.get(0);
                //m.marshal( blo, System.out );
                m.marshal( blo, file );
                
                SimObject room2 = (SimObject)u.unmarshal( file );
                System.out.println( room2.getName() );*/
                
                /*SimObject blo = (SimObject)simobs.get(0);
                XML_JAXB_ConfigReaderWriter.saveSimObject(blo);
                
                XML_JAXB_ConfigReaderWriter.saveSimObjects(simobs);
                
                XML_JAXB_ConfigReaderWriter.loadSimObject("testpipe");
                
                XML_JAXB_ConfigReaderWriter.loadSimObjects();*/
            
                /*XML_JAXB_ConfigReaderWriter.saveMARS_Settings(mars_settings);
                
                MARS_Settings stt = XML_JAXB_ConfigReaderWriter.loadMARS_Settings();
                stt.init();
                
                XML_JAXB_ConfigReaderWriter.savePhysicalEnvironment(physical_environment);
                
                PhysicalEnvironment pee = XML_JAXB_ConfigReaderWriter.loadPhysicalEnvironment();
                pee.init();
                pee.getFluid_temp();*/
            
                //XML_JAXB_ConfigReaderWriter.saveAUV(auv_monsun2);
                
                //Monsun2 mon = (Monsun2)XML_JAXB_ConfigReaderWriter.loadAUV("monsun");  
            
                /*XML_JAXB_ConfigReaderWriter.saveAUV(auv_hanse);
                Hanse han = (Hanse)XML_JAXB_ConfigReaderWriter.loadAUV("hanse2"); */
                
                /*context = JAXBContext.newInstance( BasicAUV.class );
                m = context.createMarshaller();
                AUV aa = (AUV)auvs.get(0);
                m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                m.marshal( aa, System.out );*/
            /*} catch (JAXBException ex) {
                Logger.getLogger(StartState.class.getName()).log(Level.SEVERE, null, ex);
            }*/
            
            //waiting for auvs and sensors/actuators to be ready
            //initer.start_ROS_Server();
            //initer.setupROS_Server();

            rootNode.updateGeometricState();
        }
        super.initialize(stateManager, app);
    }
    
    /*
     * 
     */
    private void setupGUI(){
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        mars.setGuiFont(guiFont);
        if(!mars_settings.isFPS()){
            mars.setDisplayFps(false);
            mars.setDisplayStatView(false);
        }
    }
    
    /*
     *
     */
    private void setupPhysics(){
        bulletAppState = new BulletAppState();
        bulletAppState.setThreadingType(BulletAppState.ThreadingType.PARALLEL);
        mars.getStateManager().attach(bulletAppState);
        //set the physis world parameters
        bulletAppState.getPhysicsSpace().setMaxSubSteps(4);
        if(mars_settings.isPhysicsDebug()){
            bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        }
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0.0f, 0.0f, 0.0f));
        bulletAppState.getPhysicsSpace().setAccuracy(1f/mars_settings.getPhysicsFramerate());
        bulletAppState.getPhysicsSpace().addTickListener(this);
    }

    private void setupCams(){
        mars.getFlyByCamera().setMoveSpeed(mars_settings.getFlyCamMoveSpeed());
        mars.getFlyByCamera().setEnabled(true);
        chaseCam = new ChaseCamera(mars.getCamera(),rootNode,inputManager);
        chaseCam.setInvertVerticalAxis(true);
        chaseCam.setZoomSensitivity(0.1f);
        chaseCam.setEnabled(false);
    }
    
    /*
     *
     */
    private void loadXML(){
        try {
             xmll = new XMLConfigReaderWriter(this);
             mars_settings = XML_JAXB_ConfigReaderWriter.loadMARS_Settings();//xmll.getSimAUVSettings();
             mars_settings.init();
             physical_environment = XML_JAXB_ConfigReaderWriter.loadPhysicalEnvironment();//mars_settings.getPhysical_environment();
             physical_environment.init();
             mars_settings.setPhysical_environment(physical_environment);
             //auvs = xmll.getAuvs();
             auvs = XML_JAXB_ConfigReaderWriter.loadAUVs();//xmll.getAuvs();
             Iterator iter = auvs.iterator();
             while(iter.hasNext() ) {
                BasicAUV bas_auv = (BasicAUV)iter.next();
                bas_auv.getAuv_param().init();
                bas_auv.setName(bas_auv.getAuv_param().getAuv_name());
                bas_auv.setState(this);
             }
             simobs = XML_JAXB_ConfigReaderWriter.loadSimObjects();//xmll.getObjects();
        } catch (Exception ex) {
            Logger.getLogger(SimState.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /** Declaring the "Shoot" action and mapping to its triggers. */
    private void initKeys() {
        inputManager.addMapping("Shoott",new KeyTrigger(KeyInput.KEY_SPACE));         // trigger 2: left-button click
        inputManager.addListener(actionListener, "Shoott");

        inputManager.addMapping("start", new KeyTrigger(KeyInput.KEY_X));
        inputManager.addListener(actionListener, "start");
        inputManager.addMapping("stop", new KeyTrigger(KeyInput.KEY_Y));
        inputManager.addListener(actionListener, "stop");

        inputManager.addMapping("thruster_left_forward", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addListener(actionListener, "thruster_left_forward");
        inputManager.addMapping("thruster_both_turn", new KeyTrigger(KeyInput.KEY_NUMPAD5));
        inputManager.addListener(actionListener, "thruster_both_turn");
        inputManager.addMapping("thruster_left_back", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addListener(actionListener, "thruster_left_back");
        inputManager.addMapping("thruster_right_forward", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addListener(actionListener, "thruster_right_forward");
        inputManager.addMapping("thruster_right_back", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addListener(actionListener, "thruster_right_back");

        inputManager.addMapping("thruster_both_forward", new KeyTrigger(KeyInput.KEY_NUMPAD8));
        inputManager.addListener(actionListener, "thruster_both_forward");
        inputManager.addMapping("thruster_both_back", new KeyTrigger(KeyInput.KEY_NUMPAD2));
        inputManager.addListener(actionListener, "thruster_both_back");

        inputManager.addMapping("thruster_both_up", new KeyTrigger(KeyInput.KEY_NUMPAD9));
        inputManager.addListener(actionListener, "thruster_both_up");
        inputManager.addMapping("thruster_both_down", new KeyTrigger(KeyInput.KEY_NUMPAD7));
        inputManager.addListener(actionListener, "thruster_both_down");

        inputManager.addMapping("reset", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addListener(actionListener, "reset");
    }
    
    /*
     * what actions should be done when pressing a registered button?
     */
    private ActionListener actionListener = new ActionListener() {

        public void onAction(String name, boolean keyPressed, float tpf) {
            if(name.equals("start") && !keyPressed) {
                bulletAppState.getPhysicsSpace().setGravity(physical_environment.getGravitational_acceleration_vector());
                initial_ready = true;
                System.out.println("Simulation started...");
            }else if(name.equals("stop") && !keyPressed) {
                bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0.0f, 0.0f, 0.0f));
                //auv_hanse.clearForces();
                auv_manager.clearForcesOfAUVs();
                initial_ready = false;
                System.out.println("Simulation stopped...");
            }else if(name.equals("thruster_left_forward") && !keyPressed) {
                //mot1left.thruster_forward();
                motb1_push.thruster_forward();
            }else if(name.equals("thruster_left_back") && !keyPressed) {
                //mot1left.thruster_back();
                motb1_push.thruster_back();
            }else if(name.equals("thruster_right_forward") && !keyPressed) {
                //mot1right.thruster_forward();
                motb2_push.thruster_forward();
            }else if(name.equals("thruster_right_back") && !keyPressed) {
                //mot1right.thruster_back();
                motb2_push.thruster_back();
            }else if(name.equals("thruster_both_forward") && !keyPressed) {
                /*mot1left.thruster_forward();
                mot1right.thruster_forward();*/
                //mot1left.set_thruster_speed(100);
                //mot1right.set_thruster_speed(100);
                motb1_push.thruster_forward();
                motb2_push.thruster_forward();
            }else if(name.equals("thruster_both_turn") && !keyPressed) {
                mot1left.set_thruster_speed(40);
                mot1right.set_thruster_speed(-40);
                //mot1left.set_thruster_speed(100);
                //mot1right.set_thruster_speed(100);
            }else if(name.equals("thruster_both_back") && !keyPressed) {
                /*mot1left.thruster_back();
                mot1right.thruster_back();*/
                //mot1left.set_thruster_speed(-100);
                //mot1right.set_thruster_speed(-100);
                motb1_push.thruster_back();
                motb2_push.thruster_back();
            }else if(name.equals("thruster_both_up") && !keyPressed) {
                /*mot2left.thruster_forward();
                mot2right.thruster_forward();*/
                mot_leftback.thruster_forward();
                mot_leftfront.thruster_forward();
                mot_rightback.thruster_forward();
                mot_rightfront.thruster_forward();
            }else if(name.equals("thruster_both_down") && !keyPressed) {
                /*mot2left.thruster_back();
                mot2right.thruster_back();*/
                mot_leftback.thruster_back();
                mot_leftfront.thruster_back();
                mot_rightback.thruster_back();
                mot_rightfront.thruster_back();
            }else  if (name.equals("Shoott") && !keyPressed) {
                /*final InfraRedSensor infra = (InfraRedSensor)auv_monsun2.getSensor("infraLeft");
                Future fut = mars.enqueue(new Callable() {
                    public Float call() throws Exception {
                        return infra.getDistance();
                    }
                });*/
                /*try {
                    System.out.println("Dis: " + fut.get());
                } catch (InterruptedException ex) {
                    Logger.getLogger(SimState.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(SimState.class.getName()).log(Level.SEVERE, null, ex);
                }*/
//                Compass comp = (Compass)auv_hanse.getSensor("compass");
//                System.out.println(comp.getYawDegree());
//                //System.out.println(comp.getYawRadiant());
//                System.out.println(comp.getPitchDegree());
//                //System.out.println(comp.getPitchRadiant());
//                System.out.println(comp.getRollDegree());
//                //System.out.println(comp.getRollRadiant());
                System.out.println("====================00");
//                ImagenexSonar_852_Scanning son = (ImagenexSonar_852_Scanning)auv_hanse.getSensor("sonar_360");
//                byte[] sondat = new byte[son.getSonarReturnDataTotalLength()];
//                sondat = son.getSonarData();
//                System.out.println("Sondat:");
//                for (int i = 12; i < sondat.length; i++) {
//                    byte b = sondat[i];
//                    System.out.print(b);
//                    System.out.print("|");
//                }

                /*ImagenexSonar_852_Echo son = (ImagenexSonar_852_Echo)auv_hanse.getSensor("sonar_side");
                byte[] sondat = new byte[son.getSonarReturnDataTotalLength()];
                sondat = son.getSonarData();
                System.out.println("Sondat:");
                for (int i = 12; i < sondat.length; i++) {
                    byte b = sondat[i];
                    System.out.print(b);
                    System.out.print("|");
                }*/
                
                //PingDetector ping = (PingDetector)auv_hanse.getSensor("ping");
                //System.out.println("ping angel: " + ping.getPingerAngleRadiant("pingpong"));
            }else if(name.equals("reset") && !keyPressed) {
                System.out.println("RESET!!!");
                time = 0f;
                auv_manager.resetAllAUVs();
            }
        }
    };
    
     /*
     *
     */
    private void populateAUV_Manager(ArrayList auvs,PhysicalEnvironment pe, MARS_Settings simauv_settings, Communication_Manager com_manager, Initializer initer){
        auv_manager.setBulletAppState(bulletAppState);
        auv_manager.setPhysical_environment(pe);
        auv_manager.setSimauv_settings(simauv_settings);
        auv_manager.setCommunicationManager(com_manager);
        if(mars_settings.isROS_Server_enabled()){
            //auv_manager.setRos_node(initer.getROS_Server().getNode());
            auv_manager.setMARSNode(initer.getROS_Server().getMarsNode());
        }
        auv_manager.registerAUVs(auvs);
        //auv_hanse = (Hanse)auvs.get(1);
        //auv_monsun2 = (Monsun2)auv_manager.getAUV("monsun");
        auv_monsun2 = (Monsun2)auvs.get(2);
    }
    
         /*
     *
     */
    @Deprecated
    private void populateAUV_Manager(HashMap auvs,PhysicalEnvironment pe, MARS_Settings simauv_settings){
        /*auv_manager.setBulletAppState(bulletAppState);
        auv_manager.setPhysical_environment(pe);
        auv_manager.setSimauv_settings(mars_settings);
        auv_manager.registerAUVs(auvs);
        //auv_hanse = (Hanse)auvs.get(0);
        auv_monsun2 = (Monsun2)auvs.get(2);*/
    }

    /*
     *
     */
    private void populateSim_Object_Manager(ArrayList simobs){
        simob_manager.setBulletAppState(bulletAppState);
        simob_manager.registerSimObjects(simobs);
    }

    @Override
    public boolean isEnabled() {
        return super.isEnabled();
    }

    @Override
    public boolean isInitialized() {
        return super.isInitialized();
    }

    @Override
    public void postRender() {
        if (!super.isEnabled()) {
            return;
        }
        super.postRender();
    }

    @Override
    public void render(RenderManager rm) {
        if (!super.isEnabled()) {
            return;
        }
        super.render(rm);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }

    @Override
    public void stateAttached(AppStateManager stateManager) {
        super.stateAttached(stateManager);
    }

    @Override
    public void stateDetached(AppStateManager stateManager) {
        super.stateDetached(stateManager);
    }

    @Override
    public void update(float tpf) {
        if (!super.isEnabled()) {
            return;
        }
        super.update(tpf);
        
        //System.out.println("time: " + tpf);
        /*if(mars_settings.isSetupWavesWater()){
            //initer.updateWavesWater(tpf);
        }*/
        
        rootNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();
        //initer.testraw();
    }
    
    /**
     * 
     * @return
     */
    public SimObjectManager getSimob_manager() {
        return simob_manager;
    }

    /**
     *
     * @return
     */
    public BulletAppState getBulletAppState(){
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
    public Node getSonarDetectableNode() {
        return SonarDetectableNode;
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
     * @return
     */
    public ChaseCamera getChaseCam(){
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
    public MARS_Main getSimauv() {
        return mars;
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
     * @param view
     */
    public void setView(MARSView view){
        this.view = view;
    }

    public void prePhysicsTick(PhysicsSpace ps, float tpf) {
        if(auv_manager.isEmpty() == false && AUVPhysicsControl == null && !man_init){
            /*AUVPhysicsControl = auv_hanse.getPhysicsControl();
            mot1left = (SeaBotixThruster)auv_hanse.getActuator("thrusterLeft");
            mot1right = (SeaBotixThruster)auv_hanse.getActuator("thrusterRight");
            mot2left = (SeaBotixThruster)auv_hanse.getActuator("thrusterDownFront");
            mot2right = (SeaBotixThruster)auv_hanse.getActuator("thrusterDown");*/
            AUVPhysicsControl = auv_monsun2.getPhysicsControl();
            motb1_push = (BrushlessThruster)auv_monsun2.getActuator("thrusterLeft");
            motb2_push = (BrushlessThruster)auv_monsun2.getActuator("thrusterRight");
            mot_leftfront = (BrushlessThruster)auv_monsun2.getActuator("thrusterFrontLeft");
            mot_rightfront = (BrushlessThruster)auv_monsun2.getActuator("thrusterFrontRight");
            mot_rightback = (BrushlessThruster)auv_monsun2.getActuator("thrusterBackRight");
            mot_leftback = (BrushlessThruster)auv_monsun2.getActuator("thrusterBackLeft");
            man_init = true;
        }
        if(view == null){
            System.out.println("View is NULL");
        }
        if(view != null && !view_init && mars_settings!=null){
            view.initTree(mars_settings,auvs,simobs);
            view.setXMLL(xmll);
            view.setAuv_manager(auv_manager);
            view.setSimob_manager(simob_manager);
            //auv_hanse.setView(view);
            auv_monsun2.setView(view);
            view_init = true;
        }
        if(/*AUVPhysicsControl != null*/true){
            //only update physics if auv_hanse exists and when simulation is started
            if(auv_manager != null /*&& auv_hanse != null*/ && initial_ready){
                auv_manager.updateAllAUVs(tpf);
                com_manager.update(tpf);
                //time = time + tpf;
                //System.out.println("time: " + time);
            }else if(auv_manager != null && auv_hanse != null && !initial_ready){
                //auv_manager.clearForcesOfAUVs();
            }
            
            if(auv_manager != null){
                com_manager.update(tpf);
            }
        }
    }

    public void physicsTick(PhysicsSpace ps, float tpf) {

    }

}
