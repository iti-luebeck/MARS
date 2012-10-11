/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.states;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.input.ChaseCamera;
import com.jme3.input.FlyByCamera;
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
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Box;
import com.jme3.system.NanoTimer;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import mars.GuiControlState;
import mars.Helper.Helper;
import mars.Initializer;
import mars.KeyConfig;
import mars.MARS_Main;
import mars.MARS_Settings;
import mars.PhysicalEnvironment;
import mars.PickHint;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.auv.BasicAUV;
import mars.auv.Communication_Manager;
import mars.auv.example.Hanse;
import mars.auv.example.Monsun2;
import mars.gui.MARSView;
import mars.gui.ViewManager;
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
    private boolean server_init = false;
    
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
    private Node SimObNode = new Node("SimObNode");
    @Deprecated
    private Node SimObPickingNode = new Node("SimObPickingNode");
    //warter currents
    private Node currents = new Node("currents");
    
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
    private GuiControlState guiControlState;
    private ViewManager viewManager = new ViewManager();
        
    //map stuff
    private MapState mapState;
    
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
        super.cleanup();
        rootNode.detachAllChildren();
        /*mars = null;
        assetManager = null;
        mars_settings = null;*/
        //nifty_load = null;
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
            
         /*   Matrix4f matrix = new Matrix4f(0.099684946f, 0.003476259f, 0.007129367f, -0.05035142f, -0.0035146326f, 0.099937364f, 4.1346974E-4f, -0.021245062f, -0.0071105273f, -6.6273817E-4f, 0.09974468f, -0.023290642f, 0.0f, 0.0f, 0.0f, 1.0f);
            Vector3f start = new Vector3f(1.9252679f, -49.951576f, -2.914092f); 
            Vector3f dir = new Vector3f(-0.035146322f, 0.9993737f, 0.0041346983f);
            Ray test = new Ray(start, dir);
            Vector3f v1 = new Vector3f(2.154625f, -0.832799f, -2.879551f);
            Vector3f v2 = new Vector3f(2.154625f, -0.832799f, -2.534256f);
            Vector3f v3 =  new Vector3f(-1.67098f, -0.832798f, -2.879551f);
            Vector3f v4 = Vector3f.ZERO;
            Vector3f v5 = Vector3f.ZERO;
            Vector3f v6 = Vector3f.ZERO;
            float t_world = 0f;
            float t = test.intersects(v1, v2, v3);
                if (!Float.isInfinite(t)) {
                        matrix.mult(v1, v4);
                        matrix.mult(v2, v5);
                        matrix.mult(v3, v6);
                        t_world = test.intersects(v4, v5, v6);
                }*/
            
          /*  Vector3f start = new Vector3f(0.2182425f, -5.027495f, 0.12827098f); 
            Vector3f dir = new Vector3f(0f, 1f, 0f);    
            Vector3f v1 = new Vector3f(0.19529787f, -0.10055651f, 0.12947007f);
            Vector3f v2 = new Vector3f(0.22324294f, -0.13183054f, 0.12800966f);
            Vector3f v3 =  new Vector3f(0.21205825f, -0.13176638f, 0.12909873f);
            mars.Ray test = new mars.Ray(start, dir);
            float testt = test.intersects(v1, v2, v3);
            
        Geometry mark5 = new Geometry("VideoCamera_Arrow_2", new Arrow(test.getDirection().mult(10f)));
        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat4.setColor("Color", ColorRGBA.Green);
        mark5.setMaterial(mark_mat4);
        mark5.setLocalTranslation(test.getOrigin());
        mark5.updateGeometricState();
        rootNode.attachChild(mark5);
        
        Material mark_mat5 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat5.setColor("Color", ColorRGBA.Red);
        Geometry mark6 = new Geometry("VideoCamera_Arrow_3", new Arrow(new Vector3f(0.22324294f, -0.13183054f, 0.12800966f).subtract(new Vector3f(0.19529787f, -0.10055651f, 0.12947007f)).mult(1f)));
        mark6.setMaterial(mark_mat5);
        mark6.setLocalTranslation(new Vector3f(0.19529787f, -0.10055651f, 0.12947007f));
        mark6.updateGeometricState();
        rootNode.attachChild(mark6);
        
        Geometry mark7 = new Geometry("VideoCamera_Arrow_4", new Arrow(new Vector3f(0.21205825f, -0.13176638f, 0.12909873f).subtract(new Vector3f(0.22324294f, -0.13183054f, 0.12800966f)).mult(1f)));
        mark7.setMaterial(mark_mat5);
        mark7.setLocalTranslation(new Vector3f(0.22324294f, -0.13183054f, 0.12800966f));
        mark7.updateGeometricState();
        rootNode.attachChild(mark7);
        
        Geometry mark8 = new Geometry("VideoCamera_Arrow_5", new Arrow(new Vector3f(0.19529787f, -0.10055651f, 0.12947007f).subtract(new Vector3f(0.21205825f, -0.13176638f, 0.12909873f)).mult(1f)));
        mark8.setMaterial(mark_mat5);
        mark8.setLocalTranslation(new Vector3f(0.21205825f, -0.13176638f, 0.12909873f));
        mark8.updateGeometricState();
        rootNode.attachChild(mark8);*/
            
            sceneReflectionNode.attachChild(SonarDetectableNode);
            sceneReflectionNode.attachChild(AUVsNode);
            sceneReflectionNode.attachChild(SimObNode);
            rootNode.attachChild(sceneReflectionNode);
            rootNode.attachChild(currents);
            
            initNiftyLoading();
            loadXML();
            initPrivateKeys();// load custom key mappings
            setupPhysics();
            setupGUI();
            setupCams();
            
            auv_manager = new AUV_Manager(this);
            simob_manager = new SimObjectManager(this);
            com_manager = new Communication_Manager(auv_manager, this, rootNode, physical_environment);
        
            initer = new Initializer(mars,this,auv_manager,com_manager,physical_environment);
            initer.init();
            
            //set camera to look to (0,0,0)
            mars.getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
            
            com_manager.setServer(initer.getRAW_Server());

            //if(mars_settings.isROS_Server_enabled()){
              /*  Logger.getLogger(SimState.class.getName()).log(Level.INFO, "Waiting for ROS Server to be ready...", "");
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
                server_init = true;//server running, is needed because view is sometimes null in the beginning(see update)*/
                //server_init = initer.checkROSServer();
            //}
            if(mars_settings.isROS_Server_enabled()){
                if(initer.checkROSServer()){//Waiting for ROS Server to be ready
                
                }
            }
            
            populateAUV_Manager(auvs,physical_environment,mars_settings,com_manager,initer);
            populateSim_Object_Manager(simobs);
            
            initMap();
            
            initPublicKeys();
            
            initView();
            
           /*             Box box = new Box(1f, 1f, 1f);
                        Geometry cur1 = new Geometry("BOOM2!", box);
                        Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                        mark_mat.setColor("Color", ColorRGBA.Green);
                        cur1.setMaterial(mark_mat);
            cur1.setLocalTranslation(Vector3f.ZERO);
            currents.attachChild(cur1);*/
            
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
                stt.initAfterJAXB();
                
                XML_JAXB_ConfigReaderWriter.savePhysicalEnvironment(physical_environment);
                
                PhysicalEnvironment pee = XML_JAXB_ConfigReaderWriter.loadPhysicalEnvironment();
                pee.initAfterJAXB();
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
    
    private void initView(){
        //if(view != null && !view_init && mars_settings!=null){
            view.setMarsSettings(mars_settings);
            view.setPenv(physical_environment);
            view.setKeyConfig(keyconfig);
            view.setXMLL(xmll);
            view.setAuv_manager(auv_manager);
            view.setSimob_manager(simob_manager);
            //view.initCharts();
            view.initAUVTree(auv_manager);
            view.initSimObjectTree(simob_manager);
            view.initEnvironmentTree(physical_environment);
            view.initSettingsTree(mars_settings);
            view.initKeysTree(keyconfig);
            view.initPopUpMenues();
            view.allowSimInteraction();
            view.updateTrees();

        if(mars_settings.isROS_Server_enabled()){
                /*Logger.getLogger(SimState.class.getName()).log(Level.INFO, "Waiting for ROS Server to be ready...", "");
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
                Logger.getLogger(SimState.class.getName()).log(Level.INFO, "ROS Server Node running.", "");*/
                //server_init = true;//server running, is needed because view is sometimes null in the beginning(see update)
                //server_init = initer.checkROSServer();
            if(initer.checkROSServer()){
                view.allowServerInteraction(true);
            }else{
                view.allowServerInteraction(false);
            }
        }else{
            view.allowServerInteraction(false);
        }
    }
    
    public void connectToServer(){
        mars_settings.setROS_Server_enabled(true);
        initer.setupServer();
        if(initer.checkROSServer()){
                view.allowServerInteraction(true);
                //auv_manager.setMARSNodes(initer.getROS_Server().getMarsNodes());
                //auv_manager.updateMARSNode();
        }else{
                view.allowServerInteraction(false);
        }
    }
    
    public void disconnectFromServer(){
        mars_settings.setROS_Server_enabled(false);
        view.enableServerInteraction(false);
        initer.killServer();
        view.allowServerInteraction(false);
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
        guiControlState = new GuiControlState(assetManager);
        guiControlState.init();
        rootNode.attachChild(guiControlState.getGUINode());
        BitmapFont guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
        mars.setGuiFont(guiFont);
        if(mars_settings.isFPS()){
            mars.setDisplayFps(true);
            mars.setDisplayStatView(true);
        }else{
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
        physical_environment.setBulletAppState(bulletAppState);
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
             //mars_settings.initAfterJAXB();
             physical_environment = XML_JAXB_ConfigReaderWriter.loadPhysicalEnvironment();
             //physical_environment.initAfterJAXB();
             mars_settings.setPhysical_environment(physical_environment);
             
              /*  ImagenexSonar_852_Scanning serv = new ImagenexSonar_852_Scanning();
                serv.setEnabled(true);
                serv.setNodeVisibility(true);
                serv.setPhysicalExchangerName("vec");
                serv.initAfterJAXB();
                JAXBContext context = JAXBContext.newInstance( ImagenexSonar_852_Scanning.class );
                Marshaller m = context.createMarshaller();
                m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                m.marshal( serv, System.out );*/
                
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
             
             //do stuff after jaxb, see also UnmarshallListener
             Iterator iter = auvs.iterator();
             while(iter.hasNext() ) {
                BasicAUV bas_auv = (BasicAUV)iter.next();
                bas_auv.getAuv_param().setAuv(bas_auv);
                //bas_auv.getAuv_param().initAfterJAXB();
                bas_auv.setName(bas_auv.getAuv_param().getAuv_name());
                bas_auv.setState(this);
             }
             simobs = XML_JAXB_ConfigReaderWriter.loadSimObjects();
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
        
        inputManager.addMapping("ampp",new KeyTrigger(KeyInput.KEY_H));         // trigger 2: left-button click
        inputManager.addListener(actionListener, "ampp");
        
        inputManager.addMapping("ampm",new KeyTrigger(KeyInput.KEY_J));         // trigger 2: left-button click
        inputManager.addListener(actionListener, "ampm");
        
        inputManager.addMapping("octp",new KeyTrigger(KeyInput.KEY_K));         // trigger 2: left-button click
        inputManager.addListener(actionListener, "octp");
        
        inputManager.addMapping("octm",new KeyTrigger(KeyInput.KEY_L));         // trigger 2: left-button click
        inputManager.addListener(actionListener, "octm");
        
        inputManager.addMapping("scalebigp",new KeyTrigger(KeyInput.KEY_U));         // trigger 2: left-button click
        inputManager.addListener(actionListener, "scalebigp");
        
        inputManager.addMapping("scalebigm",new KeyTrigger(KeyInput.KEY_I));         // trigger 2: left-button click
        inputManager.addListener(actionListener, "scalebigm");
        
        inputManager.addMapping("speedbigp",new KeyTrigger(KeyInput.KEY_O));         // trigger 2: left-button click
        inputManager.addListener(actionListener, "speedbigp");
        
        inputManager.addMapping("speedbigm",new KeyTrigger(KeyInput.KEY_P));         // trigger 2: left-button click
        inputManager.addListener(actionListener, "speedbigm");
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
            }else if(guiControlState.isMove_simob()){
                    System.out.println("Mouving simob to: " + inputManager.getCursorPosition());
                    SimObject selected_simob = simob_manager.getSelectedSimObject();
                    if(selected_simob != null){
                        moveSelectedGhostSimOb(selected_simob);
                    }
            }else if(guiControlState.isRotate_simob()){
                    System.out.println("Mouving simob to: " + inputManager.getCursorPosition());
                    SimObject selected_simob = simob_manager.getSelectedSimObject();
                    if(selected_simob != null){
                        rotateSelectedGhostSimOb(selected_simob);
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
            }else if(name.equals("ampp") && !keyPressed) {
                initer.getWhg().setHeightbig(initer.getWhg().getHeightbig()+0.1f);
            }else if(name.equals("ampm") && !keyPressed) {
                initer.getWhg().setHeightbig(initer.getWhg().getHeightbig()-0.1f);
            }else if(name.equals("octp") && !keyPressed) {
                initer.getWhg().setOctaves(initer.getWhg().getOctaves()+1);
            }else if(name.equals("octm") && !keyPressed) {
                initer.getWhg().setOctaves(initer.getWhg().getOctaves()-1);
            }else if(name.equals("scalebigp") && !keyPressed) {
                initer.getWhg().setScaleybig(initer.getWhg().getScaleybig()+0.1f);
            }else if(name.equals("scalebigm") && !keyPressed) {
                initer.getWhg().setScaleybig(initer.getWhg().getScaleybig()-0.1f);
            }else if(name.equals("speedbigp") && !keyPressed) {
                initer.getWhg().setSpeedbig(initer.getWhg().getSpeedbig()+0.1f);
            }else if(name.equals("speedbigm") && !keyPressed) {
                initer.getWhg().setSpeedbig(initer.getWhg().getSpeedbig()-0.1f);
            }else  if (name.equals("Shoott") && !keyPressed) {
                /*CollisionResults results = new CollisionResults();

                Ray ray_up = new Ray(new Vector3f(0f, -2f, 0f), Vector3f.UNIT_Y);
                // 3. Collect intersections between Ray and Shootables in results list.
                //only collide with the spatial
                currents.collideWith(ray_up, results);

                for (int i = 0; i < results.size(); i++) {
                    Geometry geom = results.getCollision(i).getGeometry();
                    System.out.println("contact " + i + " : " + geom.getName() + " at : " + results.getCollision(i).getContactPoint() + " normal: " + results.getCollision(i).getContactNormal());
                }
                */
            }else if(name.equals("reset") && !keyPressed) {
                restartSimulation();
            }else if(name.equals("context_menue") && !keyPressed) {
                System.out.println("context");
                pickRightClick();
            }else if(name.equals("context_menue_off") && !keyPressed) {
                System.out.println("context_menue_off");
                //auv_manager.deselectAllSimObs();
               // view.hideAllPopupWindows();
            }else if(name.equals("depth_auv_down") && keyPressed) {
                System.out.println("depth_auv_down");
                if(guiControlState.isMove_auv()){
                    AUV selected_auv = auv_manager.getSelectedAUV();
                    if(selected_auv != null){
                        guiControlState.decrementDepthIteration();
                        guiControlState.getGhostObject().setLocalTranslation(selected_auv.getGhostAUV().getLocalTranslation().add(new Vector3f(0f,guiControlState.getDepth_factor()*guiControlState.getDepth_iteration(),0f)));
                    }
                }
            }else if(name.equals("depth_auv_up") && keyPressed) {
                if(guiControlState.isMove_auv()){
                    AUV selected_auv = auv_manager.getSelectedAUV();
                    if(selected_auv != null){
                        guiControlState.incrementDepthIteration();
                        guiControlState.getGhostObject().setLocalTranslation(selected_auv.getGhostAUV().getLocalTranslation().add(new Vector3f(0f,guiControlState.getDepth_factor()*guiControlState.getDepth_iteration(),0f)));
                    }
                }
            }else if(name.equals("moveauv") && keyPressed) {
                //System.out.println("moveauv");
                //guiControlState.setMove_auv(true);
                //guiControlState.setMove_simob(true);
                mars.getFlyByCamera().setEnabled(false);
                //System.out.println(guiControlState.isMove_auv());
                //System.out.println(guiControlState.isFree());
                AUV selected_auv = auv_manager.getSelectedAUV();
                if(selected_auv != null){
                    guiControlState.setMove_auv(true);
                    guiControlState.setGhostObject(selected_auv.getGhostAUV());
                    //guiControlState.getGhostObject().setLocalTranslation(selected_auv.getAUVNode().worldToLocal(selected_auv.getAUVNode().getWorldTranslation(),null));//initial location set
                    selected_auv.hideGhostAUV(false);
                }
                SimObject selected_simob = simob_manager.getSelectedSimObject();
                if(selected_simob != null){
                    guiControlState.setMove_simob(true);
                    guiControlState.setGhostObject(selected_simob.getGhostSpatial());
                    //guiControlState.getGhostObject().setLocalTranslation(selected_simob.getSpatial().worldToLocal(selected_simob.getSpatial().getWorldTranslation(),null));//initial location set
                    //guiControlState.getGhostObject().setLocalTranslation(selected_simob.getSimObNode().worldToLocal(guiControlState.getIntersection(),null));
                    selected_simob.hideGhostSpatial(false);
                }
            }else if(name.equals("moveauv") && !keyPressed) {
                System.out.println("stop moveauv");
                AUV selected_auv = auv_manager.getSelectedAUV();
                mars.getFlyByCamera().setEnabled(true);
                if(selected_auv != null){
                    selected_auv.getPhysicsControl().setPhysicsLocation(guiControlState.getIntersection().add(new Vector3f(0f,guiControlState.getDepth_factor()*guiControlState.getDepth_iteration(),0f)));//set end postion
                    guiControlState.getGhostObject().setLocalTranslation(selected_auv.getAUVNode().worldToLocal(selected_auv.getAUVNode().getWorldTranslation(),null));//reset ghost auv for rotation
                    selected_auv.hideGhostAUV(true);
                }
                
                SimObject selected_simob = simob_manager.getSelectedSimObject();
                if(selected_simob != null){
                    selected_simob.getPhysicsControl().setPhysicsLocation(guiControlState.getIntersection().add(new Vector3f(0f,guiControlState.getDepth_factor()*guiControlState.getDepth_iteration(),0f)));//set end postion
                    //guiControlState.getGhostObject().setLocalTranslation(selected_simob.getSimObNode().worldToLocal(selected_simob.getSimObNode().getWorldTranslation(),null));//reset ghost auv for rotation
                    selected_simob.hideGhostSpatial(true);
                }
                guiControlState.setDepth_iteration(0);
                guiControlState.setMove_auv(false);
                guiControlState.setMove_simob(false);
            }else if(name.equals("rotateauv") && keyPressed) {
                System.out.println("rotateauv");
                AUV selected_auv = auv_manager.getSelectedAUV();
                mars.getFlyByCamera().setEnabled(false);
                if(selected_auv != null){
                    guiControlState.setGhostObject(selected_auv.getGhostAUV());
                    //guiControlState.getGhostObject().setLocalRotation(selected_auv.getAUVNode().getLocalRotation());//initial rotations et
                    selected_auv.hideGhostAUV(false);
                    rotateSelectedGhostAUV(selected_auv);
                    guiControlState.setRotateArrowVisible(true);
                    guiControlState.setRotate_auv(true);
                }
                
                SimObject selected_simob = simob_manager.getSelectedSimObject();
                if(selected_simob != null){
                    guiControlState.setGhostObject(selected_simob.getGhostSpatial());
                    //guiControlState.getGhostObject().setLocalRotation(selected_auv.getAUVNode().getLocalRotation());//initial rotations et
                    selected_simob.hideGhostSpatial(false);
                    rotateSelectedGhostSimOb(selected_simob);
                    guiControlState.setRotateArrowVisible(true);
                    guiControlState.setRotate_simob(true);
                }
                
            }else if(name.equals("rotateauv") && !keyPressed) {
                System.out.println("stop rotateauv");
                AUV selected_auv = auv_manager.getSelectedAUV();
                mars.getFlyByCamera().setEnabled(true);
                if(selected_auv != null){
                    selected_auv.getPhysicsControl().setPhysicsRotation(guiControlState.getRotation());//set end roation
                    selected_auv.hideGhostAUV(true);
                    guiControlState.setRotateArrowVisible(false);
                }
                
                SimObject selected_simob = simob_manager.getSelectedSimObject();
                if(selected_simob != null){
                    selected_simob.getPhysicsControl().setPhysicsRotation(guiControlState.getRotation());//set end roation
                    selected_simob.hideGhostSpatial(true);
                    guiControlState.setRotateArrowVisible(false);
                }
                
                guiControlState.setRotate_auv(false);
                guiControlState.setRotate_simob(false);
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
        if(guiControlState.getGhostObject() != null){
            guiControlState.getGhostObject().setLocalTranslation(auv.getAUVNode().worldToLocal(intersection,null));
            guiControlState.getGhostObject().setLocalRotation(guiControlState.getRotation());
        }
    }
    
    private void moveSelectedGhostSimOb(SimObject simob){
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);
        Vector3f intersection = Helper.getIntersectionWithPlane(simob.getSpatial().getWorldTranslation(),Vector3f.UNIT_Y,click3d, dir);
        guiControlState.setIntersection(intersection);
        //System.out.println("Intersection: " + intersection);
        if(guiControlState.getGhostObject() != null){
            //System.out.println("simob.getSpatial().worldToLocal(intersection,null): " + simob.getSimObNode().worldToLocal(intersection,null));
            guiControlState.getGhostObject().setLocalTranslation(simob.getSimObNode().worldToLocal(intersection,null));
            //guiControlState.getGhostObject().setLocalRotation(guiControlState.getRotation());
        }
    }
    
    private void moveSelectedGhostAUV(AUV auv, Vector3f position){
        auv.hideGhostAUV(false);
        auv.getGhostAUV().setLocalTranslation(auv.getAUVNode().worldToLocal(position,null));
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
        if(guiControlState.getGhostObject() != null){
            Quaternion quat = new Quaternion();
            Quaternion gquat = new Quaternion();
            
            Quaternion wQuat = auv.getAUVSpatial().getWorldRotation();
            float[] ff = wQuat.toAngles(null);
            //System.out.println("ff: " + ff[1]);
            float newAng = ff[1] - angle;
            
            quat.fromAngleNormalAxis(angle, Vector3f.UNIT_Y);
            gquat.fromAngleNormalAxis(-newAng, Vector3f.UNIT_Y);
            guiControlState.setRotation(quat);
            guiControlState.getGhostObject().setLocalRotation(gquat);
            guiControlState.setRotateArrowVectorStart(guiControlState.getGhostObject().getWorldTranslation());
            guiControlState.setRotateArrowVectorEnd(intersection);
            guiControlState.updateRotateArrow();
        }
    }
    
    private void rotateSelectedGhostSimOb(SimObject simob){
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d);
        Vector3f intersection = Helper.getIntersectionWithPlane(simob.getSpatial().getWorldTranslation(),Vector3f.UNIT_Y,click3d, dir);
        //System.out.println("Intersection: " + intersection);
        Vector3f diff = intersection.subtract(simob.getSpatial().getWorldTranslation());
        diff.y = 0f;
        diff.normalizeLocal();
        float angle = 0f;
        if(diff.z < 0f){
            angle = diff.angleBetween(Vector3f.UNIT_X);
        }else{
            angle = diff.angleBetween(Vector3f.UNIT_X)*(-1);
        }
        
        //System.out.println("angle: " + angle);
        if(guiControlState.getGhostObject() != null){
            Quaternion quat = new Quaternion();
            Quaternion gquat = new Quaternion();
            
            Quaternion wQuat = simob.getSpatial().getWorldRotation();
            float[] ff = wQuat.toAngles(null);
            //System.out.println("ff: " + ff[1]);
            float newAng = ff[1] - angle;
            
            quat.fromAngleNormalAxis(angle, Vector3f.UNIT_Y);
            gquat.fromAngleNormalAxis(-newAng, Vector3f.UNIT_Y);
            guiControlState.setRotation(quat);
            guiControlState.getGhostObject().setLocalRotation(gquat);
            guiControlState.setRotateArrowVectorStart(guiControlState.getGhostObject().getWorldTranslation());
            guiControlState.setRotateArrowVectorEnd(intersection);
            guiControlState.updateRotateArrow();
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
          //Geometry target = results.getClosestCollision().getGeometry();
          for (int i = 0; i < results.size(); i++) {
              Geometry target = results.getCollision(i).getGeometry();
              // Here comes the action:
              System.out.println("i choose you hover !, " + target.getParent().getUserData("auv_name") );
              if((String)target.getParent().getUserData("auv_name") != null){
                  BasicAUV auv = (BasicAUV)auv_manager.getAUV((String)target.getParent().getUserData("auv_name"));
                  if(auv != null){
                        auv.setSelected(true);
                        guiControlState.setLatestSelectedAUV(auv);
                        this.mars.setHoverMenuForAUV(auv,(int)inputManager.getCursorPosition().x,mars.getViewPort().getCamera().getHeight()-(int)inputManager.getCursorPosition().y);
                        return;
                    //guiControlState.setFree(false);
                  }
              }
          }
          //run through and nothing found that is worth to pick
          auv_manager.deselectAllAUVs();
          this.mars.setHoverMenuForAUV(false);
        }else{//nothing to pickRightClick
            System.out.println("Nothing to pick auv!");
            auv_manager.deselectAllAUVs();
            this.mars.setHoverMenuForAUV(false);
            //guiControlState.setFree(true);
        }

        results.clear();
        SimObNode.collideWith(ray, results);
        // Use the results -- we rotate the selected geometry.
        if (results.size() > 0) {
            for (int i = 0; i < results.size(); i++) {
              Geometry target = results.getCollision(i).getGeometry();
              // Here comes the action:
              //System.out.println("i choose you hover !, " + target.getUserData("simob_name") );
                if( ((String)target.getUserData("simob_name") != null) ){
                    Integer pickType = (Integer)target.getUserData(PickHint.PickName);
                    if( (pickType == null) || (pickType == PickHint.Pick) ){//only pick spatials who are pickable
                        SimObject simob = (SimObject)simob_manager.getSimObject((String)target.getUserData("simob_name"));
                        if(simob != null){
                            simob.setSelected(true);
                            guiControlState.setLatestSelectedSimOb(simob);
                            return;
                            //guiControlState.setFree(false);
                        }
                    }
                }
            }
            //run through and nothing found that is worth to pick
            simob_manager.deselectAllSimObs();
        }else{//nothing to pickRightClick
            System.out.println("Nothing to pick simobs!");
                simob_manager.deselectAllSimObs();
        }     
    }
    
    private void pickRightClick(){
        CollisionResults results = new CollisionResults();
        // Convert screen click to 3d position
        Vector2f click2d = inputManager.getCursorPosition();
        //System.out.println("click2d: " + click2d);
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
            for (int i = 0; i < results.size(); i++) {
                Geometry target = results.getCollision(i).getGeometry();
                guiControlState.setAuvContactPoint(results.getClosestCollision().getContactPoint());
                guiControlState.setAuvContactDirection(dir.normalize());
                  // Here comes the action:
                  if((String)target.getParent().getUserData("auv_name") != null){
                      BasicAUV auv = (BasicAUV)auv_manager.getAUV((String)target.getParent().getUserData("auv_name"));
                      if(auv != null){
                            view.initPopUpMenuesForAUV(auv.getAuv_param());
                            view.showpopupAUV((int)inputManager.getCursorPosition().x,(int)inputManager.getCursorPosition().y); 
                      }
                  }
            }
        }else{//nothing to pickRightClick but still normal context menu for split view
            //System.out.println("nothing to choose");
            auv_manager.deselectAllAUVs();
            view.hideAllPopupWindows();
            view.showpopupWindowSwitcher((int)inputManager.getCursorPosition().x,(int)inputManager.getCursorPosition().y);  
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
            auv_manager.setMARSNodes(initer.getROS_Server().getMarsNodes());
        }
        auv_manager.registerAUVs(auvs);
        //update the view in the next frame
        Future fut = mars.enqueue(new Callable() {
                public Void call() throws Exception {
                    view.initCharts();
                    view.updateTrees();
                    return null;
                }
                });
    }

    /*
     *
     */
    private void populateSim_Object_Manager(ArrayList simobs){
        simob_manager.setBulletAppState(bulletAppState);
        simob_manager.registerSimObjects(simobs);
        //update the view in the next frame
        Future fut = mars.enqueue(new Callable() {
                public Void call() throws Exception {
                    view.updateTrees();
                    return null;
                }
                });
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
        
        if(view == null){
            System.out.println("View is NULL");
        }
        
        /*if(view != null && !view_init && mars_settings!=null){
            view.setMarsSettings(mars_settings);
            view.setPenv(physical_environment);
            view.setKeyConfig(keyconfig);
            view.setXMLL(xmll);
            view.setAuv_manager(auv_manager);
            view.setSimob_manager(simob_manager);
            view.initAUVTree(auv_manager);
            view.initSimObjectTree(simob_manager);
            view.initEnvironmentTree(physical_environment);
            view.initSettingsTree(mars_settings);
            view.initKeysTree(keyconfig);
            view.initPopUpMenues();
            view.allowSimInteraction();
            Future fut = mars.enqueue(new Callable() {
                public Void call() throws Exception {
                    view.updateTrees();
                    return null;
                }
                });
            //auv_hanse.setView(view);
            //auv_monsun2.setView(view);
            view_init = true;
        }
        
        if(view != null && server_init){
            if(initer.getROS_Server().getMarsNode().isRunning()){
                view.allowServerInteraction(true);
                server_init = false;
            }
        }*/
        
        //System.out.println("time: " + tpf);
        /*if(mars_settings.isSetupWavesWater()){
            //initer.updateWavesWater(tpf);
        }*/

        if(mars_settings.isSetupProjectedWavesWater()){
            initer.updateProjectedWavesWater(tpf);
        }
        
        if(mars_settings.isSetupGrass()){
            initer.updateGrass(tpf);
        }
        
        rootNode.updateLogicalState(tpf);
        rootNode.updateGeometricState();
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
    public Node getSimObNode() {
        return SimObNode;
    }
    
    /**
     *
     * @return
     */
    public Node getSimObPickingNode() {
        return SimObPickingNode;
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

    public void prePhysicsTick(PhysicsSpace ps, final float tpf) {
        if(/*AUVPhysicsControl != null*/true){
            //only update physics if auv_hanse exists and when simulation is started
            if(auv_manager != null /*&& auv_hanse != null*/ && initial_ready){
                /*Future fut = mars.enqueue(new Callable() {
                public Void call() throws Exception {
                    auv_manager.updateAllAUVs(tpf);
                    return null;
                }
                });*/
                auv_manager.updateAllAUVs(tpf);
                /*Future fut = mars.enqueue(new Callable() {
                public Void call() throws Exception {
                    com_manager.update(tpf);
                    return null;
                }
                });*/
                com_manager.update(tpf);
                //time = time + tpf;
                //System.out.println("time: " + time);
            }            
            /*if(auv_manager != null){
                com_manager.update(tpf);
            }*/
        }
    }

    public void physicsTick(PhysicsSpace ps, float tpf) {
        if(/*AUVPhysicsControl != null*/false){
            //only update physics if auv_hanse exists and when simulation is started
            if(auv_manager != null /*&& auv_hanse != null*/ && initial_ready){
                /*Future fut = mars.enqueue(new Callable() {
                public Void call() throws Exception {
                    auv_manager.updateAllAUVs(tpf);
                    return null;
                }
                });*/
                auv_manager.updateAllAUVs(tpf);
                /*Future fut = mars.enqueue(new Callable() {
                public Void call() throws Exception {
                    com_manager.update(tpf);
                    return null;
                }
                });*/
                com_manager.update(tpf);
                //time = time + tpf;
                //System.out.println("time: " + time);
            }            
            /*if(auv_manager != null){
                com_manager.update(tpf);
            }*/
        }
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
        view.allowPhysicsInteraction(true);
        System.out.println("Simulation started...");
    }
            
    public void pauseSimulation(){
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0.0f, 0.0f, 0.0f));
        auv_manager.clearForcesOfAUVs();
        initial_ready = false;
        view.allowPhysicsInteraction(false);
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
            Vector3f direction = guiControlState.getAuvContactDirection().negate().normalize();
            System.out.println("POKE!");     
            selected_auv.getPhysicsControl().applyImpulse(direction.mult(selected_auv.getAuv_param().getMass()*5f/mars_settings.getPhysicsFramerate()), rel_pos);
            
            /*Geometry mark4 = new Geometry("Sonar_Arrow", new Arrow(direction));
            Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mark_mat4.setColor("Color", ColorRGBA.White);
            mark4.setMaterial(mark_mat4);
            mark4.setLocalTranslation(guiControlState.getAuvContactPoint());
            mark4.updateGeometricState();
            rootNode.attachChild(mark4);*/
        }
    }
    
    public void moveSelectedAUV(Vector3f new_position, boolean relative){
        //System.out.println("moveSelectedAUV" + new_position);
        AUV selected_auv = guiControlState.getLatestSelectedAUV();
        if(selected_auv != null){
            selected_auv.hideGhostAUV(true);
            if(!relative){
                selected_auv.getAuv_param().setPosition(new_position);
                selected_auv.getPhysicsControl().setPhysicsLocation(new_position);
            }else{
                selected_auv.getAuv_param().setPosition(new_position.add(selected_auv.getAuv_param().getPosition()));
                selected_auv.getPhysicsControl().setPhysicsLocation(selected_auv.getPhysicsControl().getPhysicsLocation().add(new_position));
            }
        }
    }
    
    public void rotateSelectedAUV(Vector3f new_rotation, boolean relative){
        //System.out.println("rotateSelectedAUV" + new_rotation);
        AUV selected_auv = guiControlState.getLatestSelectedAUV();
        if(selected_auv != null){
            selected_auv.hideGhostAUV(true);
            if(!relative){
                selected_auv.getAuv_param().setRotation(new_rotation);
                Quaternion quat = new Quaternion();
                quat.fromAngles(new_rotation.x, new_rotation.y, new_rotation.z);
                selected_auv.getPhysicsControl().setPhysicsRotation(quat);
            }else{
                
            }
        }
    }
    
    public void moveCamera(Vector3f new_position, boolean relative){
        //System.out.println("moveCamera" + new_position);
        viewManager.moveCamera(new_position,relative);
        if(!relative){
            mars.getCamera().setLocation(new_position);
        }else{
            mars.getCamera().setLocation(mars.getCamera().getLocation().add(new_position));
        }
    }
    
    public void rotateCamera(Vector3f new_rotation, boolean relative){
        //System.out.println("rotateCamera" + new_rotation);
        if(!relative){
            Quaternion quat = new Quaternion();
            quat.fromAngles(new_rotation.getX(), new_rotation.getY(), new_rotation.getZ());
            mars.getCamera().setRotation(quat);
        }
    }
    
    public void moveSelectedGhostAUV(Vector3f new_position){
        //System.out.println("moveSelectedGhostAUV" + new_position);
        AUV selected_auv = guiControlState.getLatestSelectedAUV();
        if(selected_auv != null){
            moveSelectedGhostAUV(selected_auv,new_position);
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
    
    public void chaseAUV(AUV auv){
        if(auv != null){
            mars.getFlyByCamera().setEnabled(false);
            mars.getChaseCam().setSpatial(auv.getAUVNode());
            mars.getChaseCam().setEnabled(true);
        }
    }
    
    public void enableSelectedAUV(boolean enable){
        AUV selected_auv = auv_manager.getSelectedAUV();
        if(selected_auv != null){
            if(!enable){
                selected_auv.getAuv_param().setEnabled(false);
                auv_manager.enableAUV(selected_auv, false);
            }else{
                selected_auv.getAuv_param().setEnabled(true);
                auv_manager.enableAUV(selected_auv, true);
            }
        }
    }
    
    public void debugSelectedAUV(int debug_mode, boolean selected){
        AUV selected_auv = auv_manager.getSelectedAUV();
        if(selected_auv != null){
            switch(debug_mode){
                case 0: selected_auv.getAuv_param().setDebugPhysicalExchanger(selected);selected_auv.setPhysicalExchangerVisible(selected);break;
                case 1: selected_auv.getAuv_param().setDebugCenters(selected);selected_auv.setCentersVisible(selected);break;
                case 2: selected_auv.getAuv_param().setDebugBuoycancy(selected);selected_auv.setBuoycancyVisible(selected);break;
                case 3: selected_auv.getAuv_param().setDebugCollision(selected);selected_auv.setCollisionVisible(selected);break;
                case 4: selected_auv.getAuv_param().setDebugDrag(selected);selected_auv.setDragVisible(selected);break;
                case 5: selected_auv.getAuv_param().setDebugWireframe(selected);selected_auv.setWireframeVisible(selected);break;
                case 6: selected_auv.getAuv_param().setDebugBounding(selected);selected_auv.setBoundingBoxVisible(selected);break;
                case 7: selected_auv.getAuv_param().setDebugVisualizers(selected);selected_auv.setVisualizerVisible(selected);break;
                default:;
            }                
        }
    }
    
    public void waypointsSelectedAUV(int debug_mode, boolean selected){
        AUV selected_auv = auv_manager.getSelectedAUV();
        if(selected_auv != null){
            switch(debug_mode){
                case 0: selected_auv.getAuv_param().setWaypoints_enabled(selected);selected_auv.setWaypointsEnabled(selected);break;
                case 1: selected_auv.getAuv_param().setWaypoints_visible(selected);selected_auv.setWayPointsVisible(selected);break;
                case 2: selected_auv.getWaypoints().reset();break;
                case 3: selected_auv.getAuv_param().setWaypoints_gradient(selected);if(!selected){selected_auv.getWaypoints().updateColor();}break;
                default:;
            }                
        }
    }
    
    public void waypointsColorSelectedAUV(java.awt.Color newColor){
        AUV selected_auv = auv_manager.getSelectedAUV();
        if(selected_auv != null){
            selected_auv.getAuv_param().setWaypoints_color(new ColorRGBA(newColor.getRed()/255f, newColor.getGreen()/255f, newColor.getBlue()/255f, 0f));
        }
    }
    
    public void resetSelectedAUV(){
        AUV selected_auv = auv_manager.getSelectedAUV();
        if(selected_auv != null){
            selected_auv.reset();
        }
    }
    
    public void selectAUV(AUV auv){
        if(auv != null){
            if(auv.getAuv_param().isEnabled()){
                auv.setSelected(true);
                guiControlState.setLatestSelectedAUV(auv);
            }
        }
    }
    
    public void deselectAUV(AUV auv){
        auv_manager.deselectAllAUVs();
    }
    
    public void deselectSimObs(SimObject simob){
        simob_manager.deselectAllSimObs();
    }
    
    public void selectSimObs(SimObject simob){
        if(simob != null){
            if(simob.isEnabled()){
                simob.setSelected(true);
                guiControlState.setLatestSelectedSimOb(simob);
            }
        }
    }
    
    public void splitView(){
        System.out.println("splitView");

        Camera cam2 = mars.getCamera().clone();
        cam2.setViewPort(0.0f,0.5f,0.0f,1.0f);
        float aspect = (float) (mars.getCamera().getWidth()) / mars.getCamera().getHeight();
        aspect = 2f ;
        cam2.setFrustum(-1000f, 1000f, -aspect * 1f, aspect * 1f, 1f, -1f);
        ViewPort viewPort2 = mars.getRenderManager().createMainView("PiP", cam2);
        viewPort2.setClearFlags(true, true, true);
        viewPort2.attachScene(rootNode);
        
        System.out.println("cam w: " + mars.getCamera().getWidth());
        System.out.println("cam h: " + mars.getCamera().getHeight());
        //float aspect = (float) (mars.getCamera().getWidth()) / mars.getCamera().getHeight();
        //mars.getCamera().resize((mars.getCamera().getWidth()/2), mars.getCamera().getHeight(), true);
        //mars.getCamera().setFrustum(-1000, 1000, -aspect * 1f, aspect * 1f, 1f, -1f);
        //mars.getCamera().setFrustumLeft(1f*-aspect);
       // mars.getCamera().setFrustumRight(1f*aspect);
        //cam2.resize((mars.getCamera().getWidth()), mars.getCamera().getHeight(), true);
        //mars.getCamera().setFrustumPerspective(90f, aspect, 0.1f, 1000f);
        //mars.getCamera().set
        
        mars.getCamera().setViewPort(0.5f,1.0f,0.0f,1.0f);
    }
}
