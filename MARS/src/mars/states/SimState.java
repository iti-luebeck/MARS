/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.states;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Rectangle;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Console;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
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
import mars.GuiControlState;
import mars.Helper.Helper;
import mars.Initializer;
import mars.KeyConfig;
import mars.MARS_Main;
import mars.MARS_Settings;
import mars.PhysicalEnvironment;
import mars.actuators.BrushlessThruster;
import mars.actuators.SeaBotixThruster;
import mars.actuators.Thruster;
import mars.actuators.servos.Dynamixel_AX12PLUS;
import mars.actuators.servos.Servo;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.auv.AUV_Parameters;
import mars.auv.BasicAUV;
import mars.auv.Communication_Manager;
import mars.auv.example.Hanse;
import mars.auv.example.Monsun2;
import mars.gui.MARSView;
import mars.sensors.IMU;
import mars.sensors.InfraRedSensor;
import mars.sensors.Posemeter;
import mars.sensors.Positionmeter;
import mars.sensors.TerrainSender;
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

    private float time = 0f;

    //needed for graphs
    private MARSView view;
    private boolean view_init = false;
    
    //main settings file
    MARS_Settings mars_settings;
    KeyConfig keyconfig;
    PhysicalEnvironment physical_environment;
    Initializer initer;
    ArrayList auvs = new ArrayList();
    ArrayList simobs = new ArrayList();
    XMLConfigReaderWriter xmll;
    
    ChaseCamera chaseCam;
    
    //water
    private Node sceneReflectionNode = new Node("sceneReflectionNode");
    private Node SonarDetectableNode = new Node("SonarDetectableNode");
    private Node AUVsNode = new Node("AUVNode");
    
    private Hanse auv_hanse;
    private Monsun2 auv_monsun2;
    
    //loading screen
    private Nifty nifty_load;
    //private TextRenderer textRenderer;
    
    //nifty(gui) stuff
    private NiftyJmeDisplay niftyDisplay;
    private Nifty nifty;
    private Element progressBarElement;
    private TextRenderer textRenderer;
    private boolean load = false;
    private Future simStateFuture = null;
    
    //general gui stuff
    GuiControlState guiControlState = new GuiControlState();
    
    //map stuff
    MapState mapState;
    
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
        nifty_load = null;
        super.cleanup();
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        if(!super.isInitialized()){
            if(app instanceof MARS_Main){
                mars = (MARS_Main)app;
                assetManager = mars.getAssetManager();
                inputManager = mars.getInputManager();
                nifty_load = mars.getNifty();
            }else{
                throw new RuntimeException("The passed application is not of type \"MARS_Main\"");
            }
                    
            sceneReflectionNode.attachChild(SonarDetectableNode);
            sceneReflectionNode.attachChild(AUVsNode);
            rootNode.attachChild(sceneReflectionNode);
            
            initNiftyLoading();
            loadXML();
            initPrivateKeys();// load custom key mappings
            setupPhysics();
            setupGUI();
            setupCams();
            
            auv_manager = new AUV_Manager(this);
            simob_manager = new SimObjectManager(this);
            com_manager = new Communication_Manager(auv_manager, this, rootNode, physical_environment);
        
            initer = new Initializer(mars,this,auv_manager,com_manager);
            initer.init();
            
            //set camera to look to (0,0,0)
            mars.getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
            
            com_manager.setServer(initer.getRAW_Server());

            if(mars_settings.isROS_Server_enabled()){
                Logger.getLogger(SimState.class.getName()).log(Level.INFO, "Waiting for ROS Server to be ready...", "");
                while(!initer.isROS_ServerReady()){
                    
                }
                Logger.getLogger(SimState.class.getName()).log(Level.INFO, "ROS Server ready.", "");
                Logger.getLogger(SimState.class.getName()).log(Level.INFO, "Waiting for ROS Server Node to be created...", "");
                while(initer.getROS_Server().getMarsNode() == null){
                    
                }
                Logger.getLogger(SimState.class.getName()).log(Level.INFO, "ROS Server Node created.", "");
                Logger.getLogger(SimState.class.getName()).log(Level.INFO, "Waiting for ROS Server Node to exist...", "");
                while(!initer.getROS_Server().getMarsNode().isExisting()){
                    
                }
                Logger.getLogger(SimState.class.getName()).log(Level.INFO, "ROS Server Node exists.", "");
                Logger.getLogger(SimState.class.getName()).log(Level.INFO, "Waiting for ROS Server Node to be running...", "");
                while(!initer.getROS_Server().getMarsNode().isRunning()){
                    
                }
                Logger.getLogger(SimState.class.getName()).log(Level.INFO, "ROS Server Node running.", "");
            }
            
            populateAUV_Manager(auvs,physical_environment,mars_settings,com_manager,initer);
            populateSim_Object_Manager(simobs);
            
            initMap();
            
            initPublicKeys();
            
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
                Marshaller m = context.createMarshaller();
                m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );

                m.marshal( auv_hanse, System.out );*/
           /* } catch (JAXBException ex) {
                Logger.getLogger(StartState.class.getName()).log(Level.SEVERE, null, ex);
            }*/
            
            //waiting for auvs and sensors/actuators to be ready
            //initer.start_ROS_Server();
            //initer.setupROS_Server();
            /*Thruster tt = (Thruster)auv_hanse.getActuator("thrusterDownFront");
            tt.test();*/
            //XML_JAXB_ConfigReaderWriter.saveAUV(auv_hanse);

            rootNode.updateGeometricState();
        }
        super.initialize(stateManager, app);
    }
    
    private void initMap(){
        mapState.loadMap(mars_settings.getTerrainfilepath_cm());
        Future fut = mars.enqueue(new Callable() {
             public Void call() throws Exception {
                mapState.setMars_settings(mars_settings);
                mapState.setAuv_manager(auv_manager);
                return null;
            }
        });
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
        bulletAppState.getPhysicsSpace().setMaxSubSteps(mars_settings.getPhysicsMaxSubSteps());
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
             keyconfig = XML_JAXB_ConfigReaderWriter.loadKeyConfig();    
             mars_settings = XML_JAXB_ConfigReaderWriter.loadMARS_Settings();
             mars_settings.init();
             physical_environment = XML_JAXB_ConfigReaderWriter.loadPhysicalEnvironment();
             physical_environment.init();
             mars_settings.setPhysical_environment(physical_environment);
             
             /*Dynamixel_AX12PLUS serv = new Dynamixel_AX12PLUS();
                serv.setEnabled(true);
                serv.setNodeVisibility(true);
                serv.setPhysicalExchangerName("servo");
                serv.setServoStartVector(new Vector3f(0.015f, -0.02f,-0.24f));
                serv.setServoDirection(new Vector3f(0f, 0f, -1f));
                JAXBContext context = JAXBContext.newInstance( Dynamixel_AX12PLUS.class );
                Marshaller m = context.createMarshaller();
                m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                m.marshal( serv, System.out );*/
               /* TerrainSender serv = new TerrainSender();
                serv.setEnabled(true);
                serv.setNodeVisibility(true);
                serv.setPhysicalExchangerName("terrainsender");
                JAXBContext context = JAXBContext.newInstance( TerrainSender.class );
                Marshaller m = context.createMarshaller();
                m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                m.marshal( serv, System.out );*/
             
             
             auvs = XML_JAXB_ConfigReaderWriter.loadAUVs();//xmll.getAuvs();
                /*IMU im = new IMU();
                im.setEnabled(true);
                im.setNodeVisibility(false);
                im.setPhysicalExchangerName("imu");
                Hanse hans = (Hanse)auvs.get(1);
                hans.registerPhysicalExchanger(im);*/
             
                /*Dynamixel_AX12PLUS serv = new Dynamixel_AX12PLUS();
                serv.setEnabled(true);
                serv.setNodeVisibility(true);
                serv.setPhysicalExchangerName("servo");
                serv.setServoStartVector(new Vector3f(0.015f, -0.02f,-0.24f));
                serv.setServoDirection(new Vector3f(0f, 0f, -1f));
                JAXBContext context = JAXBContext.newInstance( Dynamixel_AX12PLUS.class );
                Marshaller m = context.createMarshaller();
                m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                m.marshal( serv, System.out );*/
                
                /*Hanse hans = null;
                Iterator iter2 = auvs.iterator();
                while(iter2.hasNext() ) {
                    AUV aaa = (AUV)iter2.next();
                    if(            aaa.getAuv_param().getAuv_name().equals("hanse2") ){
                    hans = (Hanse)aaa;
                    }
                 }

                hans.registerPhysicalExchanger(serv);*/
             
             Iterator iter = auvs.iterator();
             while(iter.hasNext() ) {
                BasicAUV bas_auv = (BasicAUV)iter.next();
                bas_auv.getAuv_param().init();
                bas_auv.setName(bas_auv.getAuv_param().getAuv_name());
                bas_auv.setState(this);
             }
             simobs = XML_JAXB_ConfigReaderWriter.loadSimObjects();;
        } catch (Exception ex) {
            Logger.getLogger(SimState.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void initPublicKeys() {
        
    }
    
    /** Declaring the "Shoot" action and mapping to its triggers. */
    private void initPrivateKeys() {
        inputManager.addRawInputListener(mouseMotionListener);
        
        inputManager.addMapping("Shoott",new KeyTrigger(KeyInput.KEY_SPACE));         // trigger 2: left-button click
        inputManager.addListener(actionListener, "Shoott");

        inputManager.addMapping("start", new KeyTrigger(KeyInput.KEY_X));
        inputManager.addListener(actionListener, "start");
        inputManager.addMapping("stop", new KeyTrigger(KeyInput.KEY_Y));
        inputManager.addListener(actionListener, "stop");

        inputManager.addMapping("reset", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addListener(actionListener, "reset");
        
        inputManager.addMapping("moveauv", new KeyTrigger(KeyInput.KEY_LCONTROL));
        inputManager.addListener(actionListener, "moveauv");
        
        inputManager.addMapping("rotateauv", new KeyTrigger(KeyInput.KEY_LSHIFT));
        inputManager.addListener(actionListener, "rotateauv");
        
        inputManager.addMapping("context_menue",new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));         // trigger 2: left-button click
        inputManager.addListener(actionListener, "context_menue");
        
        inputManager.addMapping("context_menue_off",new MouseButtonTrigger(MouseInput.BUTTON_LEFT));         // trigger 2: left-button click
        inputManager.addListener(actionListener, "context_menue_off");
        
        inputManager.addMapping("depth_auv_down",new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));         // trigger 2: left-button click
        inputManager.addListener(actionListener, "depth_auv_down");
        
        inputManager.addMapping("depth_auv_up",new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));         // trigger 2: left-button click
        inputManager.addListener(actionListener, "depth_auv_up");
    }
    
    private RawInputListener mouseMotionListener = new RawInputListener() {

        public void beginInput() {
        }

        public void endInput() {
        }

        public void onJoyAxisEvent(JoyAxisEvent evt) {
        }

        public void onJoyButtonEvent(JoyButtonEvent evt) {
        }

        public void onMouseMotionEvent(MouseMotionEvent evt) {
            /*System.out.println("Mouse moved from: " + evt.getX() + " " + evt.getY());
            System.out.println("Mouse moved from: " + inputManager.getCursorPosition());*/
            
                if(guiControlState.isMove_auv()){
                    System.out.println("Mouving auv to: " + inputManager.getCursorPosition());
                    AUV selected_auv = auv_manager.getSelectedAUV();
                    if(selected_auv != null){
                        moveSelectedGhostAUV(selected_auv);
                    }
                }else if(guiControlState.isRotate_auv()){
                    System.out.println("roating auv to: " + inputManager.getCursorPosition());
                    AUV selected_auv = auv_manager.getSelectedAUV();
                    if(selected_auv != null){
                        rotateSelectedGhostAUV(selected_auv);
                    }
                }else{
                    pickHover();
                }
            
        }

        public void onMouseButtonEvent(MouseButtonEvent evt) {
        }

        public void onKeyEvent(KeyInputEvent evt) {
        }

        public void onTouchEvent(TouchEvent evt) {
        }
    };
            
    /*
     * what actions should be done when pressing a registered button?
     */
    private ActionListener actionListener = new ActionListener() {

        public void onAction(String name, boolean keyPressed, float tpf) {
            if(name.equals("start") && !keyPressed) {
                startSimulation();
            }else if(name.equals("stop") && !keyPressed) {
                pauseSimulation();
            }else  if (name.equals("Shoott") && !keyPressed) {

            }else if(name.equals("reset") && !keyPressed) {
                restartSimulation();
            }else if(name.equals("context_menue") && !keyPressed) {
                System.out.println("context");
                pickRightClick();
            }else if(name.equals("context_menue_off") && !keyPressed) {
                System.out.println("context_menue_off");
                auv_manager.deselectAllAUVs();
                view.hideAllPopupWindows();
            }else if(name.equals("depth_auv_down") && keyPressed) {
                System.out.println("depth_auv_down");
                if(guiControlState.isMove_auv()){
                    AUV selected_auv = auv_manager.getSelectedAUV();
                    if(selected_auv != null){
                        guiControlState.decrementDepthIteration();
                        guiControlState.getGhost_auv().setLocalTranslation(selected_auv.getGhostAUV().getLocalTranslation().add(new Vector3f(0f,guiControlState.getDepth_factor()*guiControlState.getDepth_iteration(),0f)));
                    }
                }
            }else if(name.equals("depth_auv_up") && keyPressed) {
                if(guiControlState.isMove_auv()){
                    AUV selected_auv = auv_manager.getSelectedAUV();
                    if(selected_auv != null){
                        guiControlState.incrementDepthIteration();
                        guiControlState.getGhost_auv().setLocalTranslation(selected_auv.getGhostAUV().getLocalTranslation().add(new Vector3f(0f,guiControlState.getDepth_factor()*guiControlState.getDepth_iteration(),0f)));
                    }
                }
            }else if(name.equals("moveauv") && keyPressed) {
                //System.out.println("moveauv");
                guiControlState.setMove_auv(true);
                mars.getFlyByCamera().setEnabled(false);
                //System.out.println(guiControlState.isMove_auv());
                //System.out.println(guiControlState.isFree());
                AUV selected_auv = auv_manager.getSelectedAUV();
                if(selected_auv != null){
                    guiControlState.setGhost_auv(selected_auv.getGhostAUV());
                    //guiControlState.getGhost_auv().setLocalTranslation(selected_auv.getAUVNode().worldToLocal(selected_auv.getAUVNode().getWorldTranslation(),null));//initial location set
                    selected_auv.hideGhostAUV(false);
                }
            }else if(name.equals("moveauv") && !keyPressed) {
                System.out.println("stop moveauv");
                AUV selected_auv = auv_manager.getSelectedAUV();
                mars.getFlyByCamera().setEnabled(true);
                if(selected_auv != null){
                    selected_auv.getPhysicsControl().setPhysicsLocation(guiControlState.getIntersection().add(new Vector3f(0f,guiControlState.getDepth_factor()*guiControlState.getDepth_iteration(),0f)));//set end postion
                    guiControlState.getGhost_auv().setLocalTranslation(selected_auv.getAUVNode().worldToLocal(selected_auv.getAUVNode().getWorldTranslation(),null));//reset ghost auv for rotation
                    selected_auv.hideGhostAUV(true);
                }
                guiControlState.setDepth_iteration(0);
                guiControlState.setMove_auv(false);
            }else if(name.equals("rotateauv") && keyPressed) {
                System.out.println("rotateauv");
                AUV selected_auv = auv_manager.getSelectedAUV();
                mars.getFlyByCamera().setEnabled(false);
                if(selected_auv != null){
                    guiControlState.setGhost_auv(selected_auv.getGhostAUV());
                    //guiControlState.getGhost_auv().setLocalRotation(selected_auv.getAUVNode().getLocalRotation());//initial rotations et
                    selected_auv.hideGhostAUV(false);
                }
                guiControlState.setRotate_auv(true);
            }else if(name.equals("rotateauv") && !keyPressed) {
                System.out.println("stop rotateauv");
                AUV selected_auv = auv_manager.getSelectedAUV();
                mars.getFlyByCamera().setEnabled(true);
                if(selected_auv != null){
                    selected_auv.getPhysicsControl().setPhysicsRotation(guiControlState.getRotation());//set end roation
                    selected_auv.hideGhostAUV(true);
                }
                //guiControlState.getGhost_auv().setLocalRotation(selected_auv.getAUVNode().getLocalRotation());//initial rotations et
                guiControlState.setRotate_auv(false);
            }
        }
    };
    
    private void moveSelectedGhostAUV(AUV auv){
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);
        Vector3f intersection = Helper.getIntersectionWithPlane(auv.getAUVNode().getWorldTranslation(),Vector3f.UNIT_Y,click3d, dir);
        guiControlState.setIntersection(intersection);
        //System.out.println("Intersection: " + intersection);
        if(guiControlState.getGhost_auv() != null){
            guiControlState.getGhost_auv().setLocalTranslation(auv.getAUVNode().worldToLocal(intersection,null));
            guiControlState.getGhost_auv().setLocalRotation(guiControlState.getRotation());
        }
    }
    
    private void rotateSelectedGhostAUV(AUV auv){
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);
        Vector3f intersection = Helper.getIntersectionWithPlane(auv.getAUVNode().getWorldTranslation(),Vector3f.UNIT_Y,click3d, dir);
        //System.out.println("Intersection: " + intersection);
        Vector3f diff = intersection.subtract(auv.getAUVNode().getWorldTranslation());
        diff.y = 0f;
        diff.normalizeLocal();
        float angle = 0f;
        if(diff.z < 0f){
            angle = diff.angleBetween(Vector3f.UNIT_X);
        }else{
            angle = diff.angleBetween(Vector3f.UNIT_X)*(-1);
        }
        
        //System.out.println("angle: " + angle);
        if(guiControlState.getGhost_auv() != null){
            Quaternion quat = new Quaternion();
            quat.fromAngleNormalAxis(angle, Vector3f.UNIT_Y);
            guiControlState.setRotation(quat);
            guiControlState.getGhost_auv().setLocalRotation(quat);
        }
    }
    
    private void pickHover(){
        CollisionResults results = new CollisionResults();
        // Convert screen click to 3d position
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);

        // Aim the ray from the clicked spot forwards.
        Ray ray = new Ray(click3d, dir);
        // Collect intersections between ray and all nodes in results list.
        AUVsNode.collideWith(ray, results);
        // Use the results -- we rotate the selected geometry.
        if (results.size() > 0) {
          // The closest result is the target that the player picked:
          Geometry target = results.getClosestCollision().getGeometry();
          // Here comes the action:
          System.out.println("i choose you hover !, " + target.getParent().getUserData("auv_name") );
          BasicAUV auv = (BasicAUV)auv_manager.getAUV((String)target.getParent().getUserData("auv_name"));
          if(auv != null){
                auv.setSelected(true);
            //guiControlState.setFree(false);
          }
        }else{//nothing to pickRightClick
                auv_manager.deselectAllAUVs();
            //guiControlState.setFree(true);
        }
    }
    
    private void pickRightClick(){
        CollisionResults results = new CollisionResults();
        // Convert screen click to 3d position
        Vector2f click2d = inputManager.getCursorPosition();
        System.out.println("click2d: " + click2d);
        Vector3f click3d = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, mars.getViewPort().getCamera().getHeight()-click2d.y), 0f).clone();
        Vector3f dir = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, mars.getViewPort().getCamera().getHeight()-click2d.y), 1f).subtractLocal(click3d);


        /*Geometry mark4 = new Geometry("Sonar_Arrow", new Arrow(dir));
        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat4.setColor("Color", ColorRGBA.White);
        mark4.setMaterial(mark_mat4);
        mark4.setLocalTranslation(click3d);
        mark4.updateGeometricState();
        rootNode.attachChild(mark4);*/
        
        // Aim the ray from the clicked spot forwards.
        Ray ray = new Ray(click3d, dir);
        // Collect intersections between ray and all nodes in results list.
        AUVsNode.collideWith(ray, results);
        // Use the results -- we rotate the selected geometry.
        if (results.size() > 0) {
          // The closest result is the target that the player picked:
          Geometry target = results.getClosestCollision().getGeometry();
          guiControlState.setAuvContactPoint(results.getClosestCollision().getContactPoint());
          guiControlState.setAuvContactDirection(dir.normalize());
          // Here comes the action:
          System.out.println("i choose you!, " + target.getParent().getUserData("auv_name") );
          BasicAUV auv = (BasicAUV)auv_manager.getAUV((String)target.getParent().getUserData("auv_name"));
          if(auv != null){
            auv.setSelected(true);
          }
          //view.showpopupWindowSwitcher((int)inputManager.getCursorPosition().x,mars_settings.getResolution_Height()-(int)inputManager.getCursorPosition().y);    
          view.showpopupAUV((int)inputManager.getCursorPosition().x,(int)inputManager.getCursorPosition().y);  
        }else{//nothing to pickRightClick
            //System.out.println("nothing to choose");
            auv_manager.deselectAllAUVs();
            view.hideAllPopupWindows();
        }
    }
    
     /*
     *
     */
    private void populateAUV_Manager(ArrayList auvs,PhysicalEnvironment pe, MARS_Settings simauv_settings, Communication_Manager com_manager, Initializer initer){
        auv_manager.setBulletAppState(bulletAppState);
        auv_manager.setPhysical_environment(pe);
        auv_manager.setSimauv_settings(simauv_settings);
        auv_manager.setCommunicationManager(com_manager);
        if(mars_settings.isROS_Server_enabled()){
            auv_manager.setMARSNode(initer.getROS_Server().getMarsNode());
        }
        auv_manager.registerAUVs(auvs);
        
        /*Iterator iter = auvs.iterator();
        while(iter.hasNext() ) {
            AUV aaa = (AUV)iter.next();
            if(            aaa.getAuv_param().getAuv_name().equals("hanse2") ){
                auv_hanse = (Hanse)aaa;
            }
        }*/
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

    public Node getAUVsNode() {
        return AUVsNode;
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
    public MARS_Main getMARS() {
        return mars;
    }

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
     * @param view
     */
    public void setView(MARSView view){
        this.view = view;
    }

    public void prePhysicsTick(PhysicsSpace ps, float tpf) {
        if(view == null){
            System.out.println("View is NULL");
        }
        if(view != null && !view_init && mars_settings!=null){
            view.setMarsSettings(mars_settings);
            view.initTree(mars_settings,auvs,simobs);
            view.setXMLL(xmll);
            view.setAuv_manager(auv_manager);
            view.setSimob_manager(simob_manager);
            //auv_hanse.setView(view);
            //auv_monsun2.setView(view);
            view_init = true;
        }
        if(/*AUVPhysicsControl != null*/true){
            //only update physics if auv_hanse exists and when simulation is started
            if(auv_manager != null /*&& auv_hanse != null*/ && initial_ready){
                auv_manager.updateAllAUVs(tpf);
                com_manager.update(tpf);
                //time = time + tpf;
                //System.out.println("time: " + time);
            }/*else if(auv_manager != null && auv_hanse != null && !initial_ready){
                //auv_manager.clearForcesOfAUVs();
            }*/
            
            if(auv_manager != null){
                com_manager.update(tpf);
            }
        }
    }

    public void physicsTick(PhysicsSpace ps, float tpf) {

    }

    private void initNiftyLoading(){
        //Element element = nifty_load.getScreen("loadlevel").findElementByName("loadingtext");
        //textRenderer = element.getRenderer(TextRenderer.class);
        //mars.setProgressWithoutEnq(0.5f, "dfsdfsdf");
        //System.out.println("setting loading!!!");
        //mars.setProgress(0.5f, "dfsdfsdf");
        /*try {
            Thread.sleep(4000);
        } catch (InterruptedException ex) {
            Logger.getLogger(SimState.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }
    
    public void startSimulation(){
        bulletAppState.getPhysicsSpace().setGravity(physical_environment.getGravitational_acceleration_vector());
        initial_ready = true;
        System.out.println("Simulation started...");
    }
            
    public void pauseSimulation(){
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0.0f, 0.0f, 0.0f));
        auv_manager.clearForcesOfAUVs();
        initial_ready = false;
        System.out.println("Simulation stopped...");            
    }
    
    public void restartSimulation(){
        System.out.println("RESET!!!");
        time = 0f;
        auv_manager.resetAllAUVs();
    }
    
    public void setMapState(MapState mapState) {
        this.mapState = mapState;
    }
    
    public void pokeSelectedAUV(){
        AUV selected_auv = auv_manager.getSelectedAUV();
        if(selected_auv != null){
            Vector3f rel_pos = selected_auv.getMassCenterGeom().getWorldTranslation().subtract(guiControlState.getAuvContactPoint());
            Vector3f direction = guiControlState.getAuvContactDirection().mult(50f);
            System.out.println("POKE!");     
            selected_auv.getPhysicsControl().applyImpulse(direction.mult(1f), rel_pos);
            
            /*Geometry mark4 = new Geometry("Sonar_Arrow", new Arrow(direction));
            Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mark_mat4.setColor("Color", ColorRGBA.White);
            mark4.setMaterial(mark_mat4);
            mark4.setLocalTranslation(guiControlState.getAuvContactPoint());
            mark4.updateGeometricState();
            rootNode.attachChild(mark4);*/
        }
    }
    
    public void chaseSelectedAUV(){
        AUV selected_auv = auv_manager.getSelectedAUV();
        if(selected_auv != null){
            mars.getFlyByCamera().setEnabled(false);
            mars.getChaseCam().setSpatial(selected_auv.getAUVNode());
            mars.getChaseCam().setEnabled(true);
        }
    }
    
    public void debugSelectedAUV(int debug_mode){
        AUV selected_auv = auv_manager.getSelectedAUV();
        if(selected_auv != null){
            switch(debug_mode){
                case 0:System.out.println("debug 0");
                case 1:;
                default:;
            }                
        }
    }
}
