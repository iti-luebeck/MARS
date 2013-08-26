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
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.debug.BulletDebugAppState;
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
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
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
import mars.auv.CommunicationManager;
import mars.auv.example.Hanse;
import mars.auv.example.Monsun2;
import mars.gui.ViewManager;
import mars.simobjects.SimObject;
import mars.simobjects.SimObjectManager;
import mars.xml.XML_JAXB_ConfigReaderWriter;
import javax.swing.TransferHandler;
import mars.Collider;
import mars.MyDebugAppStateFilter;
import mars.core.CentralLookup;
import mars.core.MARSLogTopComponent;
import mars.core.MARSMapTopComponent;
import mars.core.MARSTopComponent;
import mars.core.MARSTreeTopComponent;
import mars.recorder.RecordControl;
import mars.recorder.RecordManager;
import mars.xml.ConfigManager;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.util.Lookup;

/**
 *
 * @author Thomas Tosik
 */
public class SimState extends AbstractAppState implements PhysicsTickListener,AppStateExtension{

    private Node rootNode = new Node("SimState Root Node");
    private AssetManager assetManager;
    private InputManager inputManager;
    private AUV_Manager auvManager;
    private RecordManager recordManager;
    private SimObjectManager simobManager;
    private CommunicationManager comManager;
    private BulletAppState bulletAppState;
    private MARS_Main mars;
    
    private boolean initial_ready = false;

    private float time = 0f;

    //needed for graphs
    private MARSTreeTopComponent TreeTopComp;
    private MARSTopComponent MARSTopComp;
    private MARSMapTopComponent MARSMapComp;
    private MARSLogTopComponent MARSLogComp;
    private boolean view_init = false;
    private boolean server_init = false;
    private boolean debugFilter = false;
    private boolean init = false;
    
    //main settings file
    private MARS_Settings mars_settings;
    private KeyConfig keyconfig;
    private PhysicalEnvironment physical_environment;
    private Initializer initer;
    private ArrayList auvs = new ArrayList();
    private ArrayList simobs = new ArrayList();
    private XML_JAXB_ConfigReaderWriter xml;
    private ConfigManager configManager;
    
    private ChaseCamera chaseCam;
    
    //water
    private Node sceneReflectionNode = new Node("sceneReflectionNode");
    private Collider RayDetectable = new Collider();
    private Node AUVsNode = new Node("AUVNode");
    private Node SimObNode = new Node("SimObNode");
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
    
    //progress bar (nb)
    private final ProgressHandle progr = ProgressHandleFactory.createHandle("SimState");
    
    /**
     * 
     * @param assetManager
     */
    public SimState(AssetManager assetManager) {
        this.assetManager = assetManager;
    }
    
    /**
     * 
     * @param MARSTopComp
     * @param TreeTopComp
     * @param MARSMapComp
     * @param MARSLogComp
     * @param configManager  
     */
    public SimState(MARSTopComponent MARSTopComp ,MARSTreeTopComponent TreeTopComp, MARSMapTopComponent MARSMapComp, MARSLogTopComponent MARSLogComp,ConfigManager configManager) {
        this.TreeTopComp = TreeTopComp;
        this.MARSTopComp = MARSTopComp;
        this.MARSMapComp = MARSMapComp;
        this.MARSLogComp = MARSLogComp;
        this.configManager = configManager;
    }
        
    /**
     * 
     * @return
     */
    public Node getRootNode(){
        return rootNode;
    }
    
    /**
     *
     */
    @Override
    public void cleanup() {
        super.cleanup();
        
        //deattach the state root node from the main 
        mars.getRootNode().detachChild(getRootNode());
        getRootNode().detachAllChildren();
        
        //cleanup the initer (viewport, filters)
        initer.cleanup();
        
        //clean up all auvs (offscreen view from drag for example)
        auvManager.cleanup();
        
        //deattach the input listeners
        inputManager.removeRawInputListener(mouseMotionListener);
        inputManager.removeListener(actionListener);
        
        //clean the cameras
        chaseCam.setEnabled(false);
        chaseCam = null;
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
        progr.progress( "Starting SimState" );
        
        if(!super.isInitialized()){
            if(app instanceof MARS_Main){
                mars = (MARS_Main)app;
                assetManager = mars.getAssetManager();
                inputManager = mars.getInputManager();
                nifty_load = mars.getNifty();
                mars.getRootNode().attachChild(getRootNode());
            }else{
                throw new RuntimeException("The passed application is not of type \"MARS_Main\"");
            }
            
            //enable OcullusRift support
            //StereoCamAppState stereoCamAppState = new StereoCamAppState();
            //stateManager.attach(stereoCamAppState);

            
            //mars.getViewPort().setEnabled(false);
    
            
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
        
            progr.progress( "Adding Nodes" );
            sceneReflectionNode.attachChild(AUVsNode);
            sceneReflectionNode.attachChild(SimObNode);
            rootNode.attachChild(sceneReflectionNode);
            rootNode.attachChild(currents);
            
            progr.progress( "Loading Nifty" );
            initNiftyLoading();
            progr.progress( "Starting Nifty" );
            startNiftyState();
            progr.progress( "Loading Config" );
            loadXML(configManager.getConfig());
            progr.progress( "Init Keys" );
            initPrivateKeys();// load custom key mappings
            progr.progress( "Starting Physics" );
            setupPhysics();
            progr.progress( "Starting GUI" );
            setupGUI();
            progr.progress( "Starting Cameras" );
            setupCams();
            
            progr.progress( "Creating Managers" );
            recordManager = new RecordManager(xml);
            //recordManager.loadRecordings();
            auvManager = new AUV_Manager(this);
            simobManager = new SimObjectManager(this);
            comManager = new CommunicationManager(auvManager, this, rootNode, physical_environment);
        
            progr.progress( "Creating Initializer" );
            initer = new Initializer(mars,this,auvManager,comManager,physical_environment);
            initer.init();
            
            //set camera to look to (0,0,0)
            mars.getCamera().lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
            
            comManager.setServer(initer.getRAW_Server());

            if(mars_settings.isROS_Server_enabled()){
                if(initer.checkROSServer()){//Waiting for ROS Server to be ready
                
                }
            }
            
            Lookup bag = Lookup.getDefault();
            
            Lookup.Template<AUV_Manager> pattern3 = new Lookup.Template(AUV_Manager.class);
            Lookup.Result<AUV_Manager> result3 = bag.lookup( pattern3 );
            Collection<? extends AUV_Manager> allInstances = result3.allInstances();
                
            progr.progress( "Init Map" );
            initMap();//for mars_settings
            
            progr.progress( "Populate AUVManager" );
            populateAUV_Manager(auvs,physical_environment,mars_settings,comManager,recordManager,initer);
            
            Future fut = mars.enqueue(new Callable() {
             public Void call() throws Exception {
                 CentralLookup.getDefault().add(auvManager);
                 return null;
             }
            });
   
            progr.progress( "Populate SimObjectManager" );
            populateSim_Object_Manager(simobs);
            
            //initMap();//for manager
            
            progr.progress( "Init public Keys" );
            initPublicKeys();
            
            progr.progress( "Init View" );
            initView();
            
            init = true;
            
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

            //rootNode.updateGeometricState();
        }
        progr.progress( "Init Super" );
        super.initialize(stateManager, app);
        progr.finish();
    }
    
    private void initView(){
            TreeTopComp.setMarsSettings(mars_settings);
            MARSTopComp.setMarsSettings(mars_settings);
            TreeTopComp.setPenv(physical_environment);
            MARSTopComp.setPenv(physical_environment);
            TreeTopComp.setKeyConfig(keyconfig);
            MARSTopComp.setKeyConfig(keyconfig);
            TreeTopComp.setConfigManager(configManager);
            MARSTopComp.setConfigManager(configManager);
            TreeTopComp.setAuv_manager(auvManager);
            MARSTopComp.setAuv_manager(auvManager);
            TreeTopComp.setSimob_manager(simobManager);
            MARSTopComp.setSimob_manager(simobManager);
            TreeTopComp.initAUVTree(auvManager);
            TreeTopComp.initSimObjectTree(simobManager);
            TreeTopComp.initEnvironmentTree(physical_environment);
            TreeTopComp.initSettingsTree(mars_settings);
            TreeTopComp.initKeysTree(keyconfig);
            TreeTopComp.initPopUpMenues(auvManager);
            TreeTopComp.initDND();
            MARSTopComp.initDND();
            MARSTopComp.allowSimInteraction();
            TreeTopComp.updateTrees();
            MARSMapComp.initDND();

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
                MARSTopComp.allowServerInteraction(true);
            }else{
                MARSTopComp.allowServerInteraction(false);
            }
        }else{
            MARSTopComp.allowServerInteraction(false);
        }
    }
    
    /**
     * 
     */
    public void connectToServer(){
        mars_settings.setROS_Server_enabled(true);
        initer.setupServer();
        if(initer.checkROSServer()){
                MARSTopComp.allowServerInteraction(true);
        }else{
                MARSTopComp.allowServerInteraction(false);
        }
    }
    
    /**
     * 
     */
    public void disconnectFromServer(){
        mars_settings.setROS_Server_enabled(false);
        MARSTopComp.enableServerInteraction(false);
        initer.killServer();
        MARSTopComp.allowServerInteraction(false);
    }
    
    /**
     *
     * @param enable
     */
    public void enablePublishing(boolean enable){
        mars_settings.setROS_Server_publish(enable);
    }
    
    /**
     *
     * @param enable
     */
    public void enableRecording(boolean enable){
        if(recordManager != null){
            recordManager.setEnabled(enable);
        }
    }
    
    /**
     *
     */
    public void playRecording(){
        recordManager.play();
        AUV auv = auvManager.getAUV("hanse");
        RigidBodyControl control = auv.getAUVNode().getControl(RigidBodyControl.class);
        control.setEnabled(false);
        RecordControl recordControl = new RecordControl(recordManager,auv,MARSLogComp);
        auv.getAUVNode().addControl(recordControl);
    }
    
    /**
     *
     * @param step
     */
    public void setRecord(int step){
        System.out.println("Step set to: " + step);
        recordManager.setRecord(step);
    }
    
    /**
     *
     */
    public void pauseRecording(){
        recordManager.pause();
        recordManager.setEnabled(false);
    }
    
    /**
     *
     * @param file
     */
    public void saveRecording(File file){
        recordManager.saveRecording(file);
    }
    
    /**
     *
     * @param file
     */
    public void loadRecording(File file){
        recordManager.loadRecordings(file);
    }
    
    private void initMap(){
        Future fut = mars.enqueue(new Callable() {
             public Void call() throws Exception {
                mapState.loadMap(mars_settings.getTerrainfilepath_cm());
                mapState.setMars_settings(mars_settings);
                mapState.setAuv_manager(auvManager);
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
        mars.setStatsStateDark(false);
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
        
        //setting Filter in the DebugState so we can show specific collision boxes
        /*if (mars.getStateManager().getState(BulletDebugAppState.class) != null) {
            mars.getStateManager().getState(BulletDebugAppState.class).setFilter(new MyDebugAppStateFilter()); 
        }*/ //doesnt work here because DebugAppState suuuuuucks
        
        //if(mars_settings.isPhysicsDebug()){
            bulletAppState.setDebugEnabled(true);
        //}
            
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0.0f, 0.0f, 0.0f));
        bulletAppState.getPhysicsSpace().setAccuracy(1f/mars_settings.getPhysicsFramerate());
        bulletAppState.getPhysicsSpace().addTickListener(this);
        physical_environment.setBulletAppState(bulletAppState);
        
        bulletAppState.setEnabled(false);
    }

    private void setupCams(){
        mars.getFlyByCamera().setMoveSpeed(mars_settings.getFlyCamMoveSpeed());
        mars.getFlyByCamera().setEnabled(true);
        chaseCam = new ChaseCamera(mars.getCamera(),rootNode,inputManager);
        chaseCam.setInvertVerticalAxis(true);
        chaseCam.setZoomSensitivity(mars_settings.getChaseCamZoomSensitivity());
        chaseCam.setEnabled(false);
    }
    
    /*
     *
     */
    private void loadXML(String config){
        try {
            xml = new XML_JAXB_ConfigReaderWriter(config);
             keyconfig = xml.loadKeyConfig();    
             mars_settings = xml.loadMARS_Settings();
             physical_environment = xml.loadPhysicalEnvironment();
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
             
                /*System.out.println("Recording:");
                Recording recs = new Recording();
                recs.addRecord();
                JAXBContext context = JAXBContext.newInstance( Recording.class );
                Marshaller m = context.createMarshaller();
                m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                m.marshal( recs, System.out );*/
             
             
             auvs = xml.loadAUVs();//xmll.getAuvs();
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
                bas_auv.setName(bas_auv.getAuv_param().getAuv_name());
                bas_auv.setState(this);
             }
             simobs = xml.loadSimObjects();
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
                    //System.out.println("Moveing auv to: " + inputManager.getCursorPosition());
                    AUV selected_auv = auvManager.getSelectedAUV();
                    if(selected_auv != null){
                        moveSelectedGhostAUV(selected_auv);
                    }
            }else if(guiControlState.isRotate_auv()){
                    //System.out.println("rotaing auv to: " + inputManager.getCursorPosition());
                    AUV selected_auv = auvManager.getSelectedAUV();
                    if(selected_auv != null){
                        rotateSelectedGhostAUV(selected_auv);
                    }
            }else if(guiControlState.isMove_simob()){
                    //System.out.println("Moveing simob to: " + inputManager.getCursorPosition());
                    SimObject selected_simob = simobManager.getSelectedSimObject();
                    if(selected_simob != null){
                        moveSelectedGhostSimOb(selected_simob);
                    }
            }else if(guiControlState.isRotate_simob()){
                    //System.out.println("Moveing simob to: " + inputManager.getCursorPosition());
                    SimObject selected_simob = simobManager.getSelectedSimObject();
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
                //System.out.println("context");
                pickRightClick();
            }else if(name.equals("context_menue_off") && !keyPressed) {
                //System.out.println("context_menue_off");
                //auv_manager.deselectAllSimObs();
               // view.hideAllPopupWindows();
            }else if(name.equals("depth_auv_down") && keyPressed) {
                //System.out.println("depth_auv_down");
                if(guiControlState.isMove_auv()){
                    AUV selected_auv = auvManager.getSelectedAUV();
                    if(selected_auv != null){
                        guiControlState.decrementDepthIteration();
                        guiControlState.getGhostObject().setLocalTranslation(selected_auv.getGhostAUV().getLocalTranslation().add(new Vector3f(0f,guiControlState.getDepth_factor()*guiControlState.getDepth_iteration(),0f)));
                    }
                }
            }else if(name.equals("depth_auv_up") && keyPressed) {
                if(guiControlState.isMove_auv()){
                    AUV selected_auv = auvManager.getSelectedAUV();
                    if(selected_auv != null){
                        guiControlState.incrementDepthIteration();
                        guiControlState.getGhostObject().setLocalTranslation(selected_auv.getGhostAUV().getLocalTranslation().add(new Vector3f(0f,guiControlState.getDepth_factor()*guiControlState.getDepth_iteration(),0f)));
                    }
                }
            }else if(name.equals("moveauv") && keyPressed) {
                mars.getFlyByCamera().setEnabled(false);
                AUV selected_auv = auvManager.getSelectedAUV();
                if(selected_auv != null){
                    guiControlState.setMove_auv(true);
                    guiControlState.setGhostObject(selected_auv.getGhostAUV());
                    //guiControlState.getGhostObject().setLocalTranslation(selected_auv.getAUVNode().worldToLocal(selected_auv.getAUVNode().getWorldTranslation(),null));//initial location set
                    selected_auv.hideGhostAUV(false);
                }
                SimObject selected_simob = simobManager.getSelectedSimObject();
                if(selected_simob != null){
                    guiControlState.setMove_simob(true);
                    guiControlState.setGhostObject(selected_simob.getGhostSpatial());
                    //guiControlState.getGhostObject().setLocalTranslation(selected_simob.getSpatial().worldToLocal(selected_simob.getSpatial().getWorldTranslation(),null));//initial location set
                    //guiControlState.getGhostObject().setLocalTranslation(selected_simob.getSimObNode().worldToLocal(guiControlState.getIntersection(),null));
                    selected_simob.hideGhostSpatial(false);
                }
            }else if(name.equals("moveauv") && !keyPressed) {
                //System.out.println("stop moveauv");
                AUV selected_auv = auvManager.getSelectedAUV();
                mars.getFlyByCamera().setEnabled(true);
                if(selected_auv != null){
                    selected_auv.getPhysicsControl().setPhysicsLocation(guiControlState.getIntersection().add(new Vector3f(0f,guiControlState.getDepth_factor()*guiControlState.getDepth_iteration(),0f)));//set end postion
                    guiControlState.getGhostObject().setLocalTranslation(selected_auv.getAUVNode().worldToLocal(selected_auv.getAUVNode().getWorldTranslation(),null));//reset ghost auv for rotation
                    selected_auv.hideGhostAUV(true);
                }
                
                SimObject selected_simob = simobManager.getSelectedSimObject();
                if(selected_simob != null){
                    selected_simob.getPhysicsControl().setPhysicsLocation(guiControlState.getIntersection().add(new Vector3f(0f,guiControlState.getDepth_factor()*guiControlState.getDepth_iteration(),0f)));//set end postion
                    //guiControlState.getGhostObject().setLocalTranslation(selected_simob.getSimObNode().worldToLocal(selected_simob.getSimObNode().getWorldTranslation(),null));//reset ghost auv for rotation
                    selected_simob.hideGhostSpatial(true);
                }
                guiControlState.setDepth_iteration(0);
                guiControlState.setMove_auv(false);
                guiControlState.setMove_simob(false);
            }else if(name.equals("rotateauv") && keyPressed) {
                //System.out.println("rotateauv");
                AUV selected_auv = auvManager.getSelectedAUV();
                mars.getFlyByCamera().setEnabled(false);
                if(selected_auv != null){
                    guiControlState.setGhostObject(selected_auv.getGhostAUV());
                    //guiControlState.getGhostObject().setLocalRotation(selected_auv.getAUVNode().getLocalRotation());//initial rotations et
                    selected_auv.hideGhostAUV(false);
                    rotateSelectedGhostAUV(selected_auv);
                    guiControlState.setRotateArrowVisible(true);
                    guiControlState.setRotate_auv(true);
                }
                
                SimObject selected_simob = simobManager.getSelectedSimObject();
                if(selected_simob != null){
                    guiControlState.setGhostObject(selected_simob.getGhostSpatial());
                    //guiControlState.getGhostObject().setLocalRotation(selected_auv.getAUVNode().getLocalRotation());//initial rotations et
                    selected_simob.hideGhostSpatial(false);
                    rotateSelectedGhostSimOb(selected_simob);
                    guiControlState.setRotateArrowVisible(true);
                    guiControlState.setRotate_simob(true);
                }
                
            }else if(name.equals("rotateauv") && !keyPressed) {
                //System.out.println("stop rotateauv");
                AUV selected_auv = auvManager.getSelectedAUV();
                mars.getFlyByCamera().setEnabled(true);
                if(selected_auv != null){
                    selected_auv.getPhysicsControl().setPhysicsRotation(guiControlState.getRotation());//set end roation
                    selected_auv.hideGhostAUV(true);
                    guiControlState.setRotateArrowVisible(false);
                }
                
                SimObject selected_simob = simobManager.getSelectedSimObject();
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
            //guiControlState.getGhostObject().setLocalRotation(guiControlState.getRotation());
            guiControlState.getGhostObject().setLocalRotation(auv.getAUVSpatial().getLocalRotation());
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
                  BasicAUV auv = (BasicAUV)auvManager.getAUV((String)target.getParent().getUserData("auv_name"));
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
          auvManager.deselectAllAUVs();
          this.mars.setHoverMenuForAUV(false);
        }else{//nothing to pickRightClick
            System.out.println("Nothing to pick auv!");
            auvManager.deselectAllAUVs();
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
                        SimObject simob = (SimObject)simobManager.getSimObject((String)target.getUserData("simob_name"));
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
            simobManager.deselectAllSimObs();
        }else{//nothing to pickRightClick
            System.out.println("Nothing to pick simobs!");
                simobManager.deselectAllSimObs();
        }     
    }
    
    private void pickRightClick(){
        CollisionResults results = new CollisionResults();
        // Convert screen click to 3d position
        Vector2f click2d = inputManager.getCursorPosition();
        //System.out.println("click2d: " + click2d);
        Vector3f click3d = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, mars.getViewPort().getCamera().getHeight()-click2d.y), 0f).clone();
        Vector3f dir = mars.getCamera().getWorldCoordinates(new Vector2f(click2d.x, mars.getViewPort().getCamera().getHeight()-click2d.y), 1f).subtractLocal(click3d);
        
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
                      BasicAUV auv = (BasicAUV)auvManager.getAUV((String)target.getParent().getUserData("auv_name"));
                      if(auv != null){
                            MARSTopComp.initPopUpMenuesForAUV(auv.getAuv_param());
                            MARSTopComp.showpopupAUV((int)inputManager.getCursorPosition().x,(int)inputManager.getCursorPosition().y); 
                      }
                  }
            }
        }else{//nothing to pickRightClick but still normal context menu for split view
            //System.out.println("nothing to choose");
            auvManager.deselectAllAUVs();
            MARSTopComp.hideAllPopupWindows();
            MARSTopComp.showpopupWindowSwitcher((int)inputManager.getCursorPosition().x,(int)inputManager.getCursorPosition().y);  
        }
    }
    
     /*
     *
     */
    private void populateAUV_Manager(ArrayList auvs,PhysicalEnvironment pe, MARS_Settings mars_settings, CommunicationManager com_manager, RecordManager recordManager, Initializer initer){
        auvManager.setBulletAppState(bulletAppState);
        auvManager.setPhysical_environment(pe);
        auvManager.setSimauv_settings(mars_settings);
        auvManager.setCommunicationManager(com_manager);
        auvManager.setRecManager(recordManager);
        if(mars_settings.isROS_Server_enabled()){
            auvManager.setMARSNodes(initer.getROS_Server().getMarsNodes());
        }
        auvManager.registerAUVs(auvs);
        //update the view in the next frame
        Future fut = mars.enqueue(new Callable() {
                public Void call() throws Exception {
                    TreeTopComp.updateTrees();
                    TreeTopComp.initPopUpMenues(auvManager);
                    return null;
                }
                });
    }

    /*
     *
     */
    private void populateSim_Object_Manager(ArrayList simobs){
        simobManager.setBulletAppState(bulletAppState);
        simobManager.registerSimObjects(simobs);
        //update the view in the next frame
        Future fut = mars.enqueue(new Callable() {
                public Void call() throws Exception {
                    TreeTopComp.updateTrees();
                    return null;
                }
                });
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
        if(!enabled){
            rootNode.setCullHint(Spatial.CullHint.Always);
        }else{
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
     *
     * @param tpf
     */
    @Override
    public void update(float tpf) {
        if (!super.isEnabled()) {
            return;
        }
        super.update(tpf);
        
        if(TreeTopComp == null){
            System.out.println("TreeTopComp is NULL");
        }
        if(MARSTopComp == null){
            System.out.println("MARSTopComp is NULL");
        }
        
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
        return simobManager;
    }

    public AUV_Manager getAuvManager() {
        return auvManager;
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
    public Node getAUVsNode() {
        return AUVsNode;
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
    public Node getSimObNode() {
        return SimObNode;
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
    
        if(recordManager != null){
            recordManager.update(tpf);
        }
        
        //setting Filter in the DebugState so we can show specific collision boxes
        if (mars.getStateManager().getState(BulletDebugAppState.class) != null) {
            if(!debugFilter && init){
                Logger.getLogger(SimState.class.getName()).log(Level.INFO, "Initialiazing DebugAppStateFilter...", "");
                mars.getStateManager().getState(BulletDebugAppState.class).setFilter(new MyDebugAppStateFilter(mars_settings,auvManager)); 
                debugFilter = true;
            }
        }

        //only update physics if auv_hanse exists and when simulation is started
        if(auvManager != null && initial_ready){
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
    
    /**
     * 
     */
    public void startSimulation(){
        bulletAppState.getPhysicsSpace().setGravity(physical_environment.getGravitational_acceleration_vector());
        initial_ready = true;
        MARSTopComp.allowPhysicsInteraction(true);
        bulletAppState.setEnabled(true);
        System.out.println("Simulation started...");
    }
            
    /**
     * 
     */
    public void pauseSimulation(){
        bulletAppState.setEnabled(false);
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0.0f, 0.0f, 0.0f));
        auvManager.clearForcesOfAUVs();
        initial_ready = false;
        MARSTopComp.allowPhysicsInteraction(false);
        System.out.println("Simulation stopped...");            
    }
    
    /**
     * 
     */
    public void restartSimulation(){
        System.out.println("RESET!!!");
        time = 0f;
        auvManager.resetAllAUVs();
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
     */
    public void pokeSelectedAUV(){
        AUV selected_auv = guiControlState.getLatestSelectedAUV();
        if(selected_auv != null){
            Vector3f rel_pos = selected_auv.getMassCenterGeom().getWorldTranslation().subtract(guiControlState.getAuvContactPoint());
            Vector3f direction = guiControlState.getAuvContactDirection().negate().normalize();
            System.out.println("POKE!");     
            selected_auv.getPhysicsControl().applyImpulse(direction.mult(selected_auv.getAuv_param().getMass()*5f/mars_settings.getPhysicsFramerate()), rel_pos);
        }
    }
    
    /**
     * 
     * @param new_position
     * @param relative
     */
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
    
    /**
     * 
     * @param new_rotation
     * @param relative
     */
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
    
    /**
     * 
     * @param new_position
     * @param relative
     */
    public void moveCamera(Vector3f new_position, boolean relative){
        //System.out.println("moveCamera" + new_position);
        viewManager.moveCamera(new_position,relative);
        if(!relative){
            mars.getCamera().setLocation(new_position);
        }else{
            mars.getCamera().setLocation(mars.getCamera().getLocation().add(new_position));
        }
    }
    
    /**
     * 
     * @param new_rotation
     * @param relative
     */
    public void rotateCamera(Vector3f new_rotation, boolean relative){
        //System.out.println("rotateCamera" + new_rotation);
        if(!relative){
            Quaternion quat = new Quaternion();
            quat.fromAngles(new_rotation.getX(), new_rotation.getY(), new_rotation.getZ());
            mars.getCamera().setRotation(quat);
        }
    }
    
    /**
     * 
     * @param new_position
     */
    public void moveSelectedGhostAUV(Vector3f new_position){
        //System.out.println("moveSelectedGhostAUV" + new_position);
        AUV selected_auv = guiControlState.getLatestSelectedAUV();
        if(selected_auv != null){
            moveSelectedGhostAUV(selected_auv,new_position);
        }
    }
    
    /**
     * 
     */
    public void chaseSelectedAUV(){
        AUV selected_auv = guiControlState.getLatestSelectedAUV();
        if(selected_auv != null){
            mars.getFlyByCamera().setEnabled(false);
            mars.getChaseCam().setSpatial(selected_auv.getAUVNode());
            mars.getChaseCam().setEnabled(true);
        }
    }
    
    /**
     * 
     * @param auv
     */
    public void chaseAUV(AUV auv){
        if(auv != null){
            mars.getFlyByCamera().setEnabled(false);
            mars.getChaseCam().setSpatial(auv.getAUVNode());
            mars.getChaseCam().setEnabled(true);
        }
    }
    
    /**
     * 
     * @param enable
     */
    public void enableSelectedAUV(boolean enable){
        AUV selected_auv = guiControlState.getLatestSelectedAUV();
        if(selected_auv != null){
            if(!enable){
                selected_auv.getAuv_param().setEnabled(false);
                auvManager.enableAUV(selected_auv, false);
            }else{
                selected_auv.getAuv_param().setEnabled(true);
                auvManager.enableAUV(selected_auv, true);
            }
        }
    }
    
    /**
     * 
     * @param debug_mode
     * @param selected
     */
    public void debugSelectedAUV(int debug_mode, boolean selected){
        AUV selected_auv = guiControlState.getLatestSelectedAUV();
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
    
    /**
     * 
     * @param debug_mode
     * @param selected
     */
    public void waypointsSelectedAUV(int debug_mode, boolean selected){
        AUV selected_auv = guiControlState.getLatestSelectedAUV();
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
    
    /**
     * 
     * @param newColor
     */
    public void waypointsColorSelectedAUV(java.awt.Color newColor){
        AUV selected_auv = guiControlState.getLatestSelectedAUV();
        if(selected_auv != null){
            selected_auv.getAuv_param().setWaypoints_color(new ColorRGBA(newColor.getRed()/255f, newColor.getGreen()/255f, newColor.getBlue()/255f, 0f));
        }
    }
    
    /**
     * 
     */
    public void resetSelectedAUV(){
        AUV selected_auv = guiControlState.getLatestSelectedAUV();
        if(selected_auv != null){
            selected_auv.reset();
        }
    }
    
    /**
     * 
     * @param auv
     */
    public void selectAUV(AUV auv){
        if(auv != null){
            if(auv.getAuv_param().isEnabled()){
                auv.setSelected(true);
                guiControlState.setLatestSelectedAUV(auv);
            }
        }
    }
    
    /**
     * Enables an AUV and sets it to the position. If already enabled then position change. The position is computed from he screen position.
     * @param auvName
     * @param pos
     * @param dropAction 
     * @param name  
     */
    public void enableAUV(String auvName, Point pos, int dropAction, String name){
        AUV auv = auvManager.getAUV(auvName);
        if(auv != null){
            Vector3f click3d = mars.getCamera().getWorldCoordinates(new Vector2f(pos.x, mars.getCamera().getHeight()-pos.y), 0f).clone();
            Vector3f dir = mars.getCamera().getWorldCoordinates(new Vector2f(pos.x, mars.getCamera().getHeight()-pos.y), 1f).subtractLocal(click3d);
            Vector3f intersection = Helper.getIntersectionWithPlane(new Vector3f(0f, initer.getCurrentWaterHeight(pos.x, mars.getCamera().getHeight()-pos.y), 0f),Vector3f.UNIT_Y,click3d, dir);
            if(dropAction == TransferHandler.COPY){
                AUV auvCopy = new BasicAUV(auv);
                auvCopy.getAuv_param().setAuv(auvCopy);
                auvCopy.setName(name);
                auvCopy.getAuv_param().setPosition(intersection);
                auvCopy.setState(this);
                auvManager.registerAUV(auvCopy);
                //we have to update the view AFTER the AUV register
                Future simStateFutureView = mars.enqueue(new Callable() {
                    public Void call() throws Exception {
                        if(TreeTopComp != null){
                            TreeTopComp.updateTrees(); 
                        }
                        return null;
                    }
                });           
            }else{
                if( auv.getAuv_param().isEnabled()){//check if auf auv already enabled, then only new position
                    auv.getAuv_param().setPosition(intersection);
                    auv.getPhysicsControl().setPhysicsLocation(intersection);
                }else{
                    auv.getAuv_param().setPosition(intersection);
                    auv.getAuv_param().setEnabled(true);
                    auvManager.enableAUV(auv, true);
                    auv.getPhysicsControl().setPhysicsLocation(intersection);
                }
            }
        }
    }
    
    /**
     * Enables an AUV and sets it to the position. If already enabled then position change.
     * @param auvName
     * @param pos
     * @param dropAction 
     * @param name  
     */
    public void enableAUV(String auvName, Vector3f pos, int dropAction, String name){
        AUV auv = auvManager.getAUV(auvName);
        pos.y = initer.getCurrentWaterHeight(pos.x, mars.getCamera().getHeight()-pos.y);
        if(auv != null){
            if(dropAction == TransferHandler.COPY){
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
            }else{
                if( auv.getAuv_param().isEnabled()){//check if auf auv already enabled, then only new position
                    auv.getAuv_param().setPosition(pos);
                    auv.getPhysicsControl().setPhysicsLocation(pos);
                }else{
                    auv.getAuv_param().setPosition(pos);
                    auv.getAuv_param().setEnabled(true);
                    auvManager.enableAUV(auv, true);
                    auv.getPhysicsControl().setPhysicsLocation(pos);
                }
            }
        }
    }
    
    /**
     * Enables an SimObject and sets it to the position. If already enabled then position change.
     * @param simobName
     * @param pos
     * @param dropAction 
     * @param name  
     */
    public void enableSIMOB(String simobName, Vector3f pos, int dropAction, String name){
        SimObject simob = simobManager.getSimObject(simobName);
        pos.y = initer.getCurrentWaterHeight(pos.x, mars.getCamera().getHeight()-pos.y);
        if(simob != null){
            if(dropAction == TransferHandler.COPY){
                SimObject simobCopy = simob.copy();
                simobCopy.setName(name);
                simobCopy.setPosition(pos);
                simobManager.registerSimObject(simobCopy);
                TreeTopComp.updateTrees();
                Future simStateFutureView = mars.enqueue(new Callable() {
                    public Void call() throws Exception {
                        if(TreeTopComp != null){
                            TreeTopComp.updateTrees(); 
                        }
                        return null;
                    }
                }); 
            }else{
                if( simob.isEnabled()){//check if auf simob already enabled, then only new position
                    simob.setPosition(pos);
                    simob.getPhysicsControl().setPhysicsLocation(pos);
                }else{
                    simob.setPosition(pos);
                    simob.setEnabled(true);
                    simobManager.enableSimObject(simob, true);
                    simob.getPhysicsControl().setPhysicsLocation(pos);
                }
            }
        }
    }
    
    /**
     * Enables an AUV and sets it to the position. If already enabled then position change.  The position is computed from he screen position.
     * @param simobName
     * @param pos
     * @param dropAction
     * @param name  
     */
    public void enableSIMOB(String simobName, Point pos, int dropAction, String name){
        SimObject simob = simobManager.getSimObject(simobName);
        if(simob != null){
            Vector3f click3d = mars.getCamera().getWorldCoordinates(new Vector2f(pos.x, mars.getCamera().getHeight()-pos.y), 0f).clone();
            Vector3f dir = mars.getCamera().getWorldCoordinates(new Vector2f(pos.x, mars.getCamera().getHeight()-pos.y), 1f).subtractLocal(click3d);
            Vector3f intersection = Helper.getIntersectionWithPlane(new Vector3f(0f, initer.getCurrentWaterHeight(pos.x, mars.getCamera().getHeight()-pos.y), 0f),Vector3f.UNIT_Y,click3d, dir);
            if(dropAction == TransferHandler.COPY){
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
            }else{
                if( simob.isEnabled()){//check if auf simob already enabled, then only new position
                    simob.setPosition(intersection);
                    simob.getPhysicsControl().setPhysicsLocation(intersection);
                }else{
                    simob.setPosition(intersection);
                    simob.setEnabled(true);
                    simobManager.enableSimObject(simob, true);
                    simob.getPhysicsControl().setPhysicsLocation(intersection);
                }
            }
        }
    }
    
    /**
     * 
     */
    public void deselectAllAUVs(){
        auvManager.deselectAllAUVs();
    }
    
    /**
     * 
     * @param auv
     */
    public void deselectAUV(AUV auv){
        auvManager.deselectAUV(auv);
    }
    
    /**
     * 
     * @param simob
     */
    public void deselectSimObs(SimObject simob){
        simobManager.deselectAllSimObs();
    }
    
    /**
     * 
     * @param simob
     */
    public void selectSimObs(SimObject simob){
        if(simob != null){
            if(simob.isEnabled()){
                simob.setSelected(true);
                guiControlState.setLatestSelectedSimOb(simob);
            }
        }
    }
    
    /**
     *
     */
    public void startNiftyState(){
        if (mars.getStateManager().getState(NiftyState.class) != null) {
            mars.getStateManager().getState(NiftyState.class).show(); 
        }
    }
    
    /**
     *
     * @param TreeTopComp 
     */
    public void setTreeTopComp(MARSTreeTopComponent TreeTopComp){
        this.TreeTopComp = TreeTopComp;
    }

     /**
      *
      * @return
      */
    public MARSTreeTopComponent getTreeTopComp() {
        return TreeTopComp;
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
