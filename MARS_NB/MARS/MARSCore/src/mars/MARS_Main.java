/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars;

import com.jme3.app.FlyCamAppState;
import java.util.logging.Level;
import java.util.logging.Logger;
import mars.states.SimState;
import mars.states.StartState;
import com.jme3.font.BitmapFont;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.ChaseCamera;
import com.jme3.input.FlyByCamera;
import com.jme3.math.ColorRGBA;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.plugins.OBJLoader;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.tools.SizeValue;
import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import mars.auv.AUV;
import mars.core.CentralLookup;
import mars.core.MARSLogTopComponent;
import mars.core.MARSMapTopComponent;
import mars.core.MARSTopComponent;
import mars.core.MARSTreeTopComponent;
import mars.states.AUVEditorAppState;
import mars.states.AppStateExtension;
import mars.states.MapState;
import mars.states.NiftyState;
import mars.xml.ConfigManager;
import mars.xml.XML_JAXB_ConfigReaderWriter;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.modules.InstalledFileLocator;


/**
 * This is the MAIN class for JME3.
 * @author Thomas Tosik
 */
public class MARS_Main extends SimpleApplication{

    //needed for graphs
    private MARSTreeTopComponent TreeTopComp;
    private MARSTopComponent MARSTopComp;
    private MARSMapTopComponent MARSMapComp;
    private MARSLogTopComponent MARSLogComp;
    private boolean startstateinit = false;
    private boolean statsDarken = true;

    StartState startstate;
    MapState mapstate;
    AUVEditorAppState editstate;
    NiftyState niftystate;
    
    ChaseCamera chaseCam;
    Camera map_cam;
    
    ViewPort MapViewPort;
    
    AdvancedFlyByCamera advFlyCam;
    
    ConfigManager configManager = new ConfigManager();
    
    //nifty(gui) stuff
    private NiftyJmeDisplay niftyDisplay;
    private Nifty nifty;
    private Element progressBarElement;
    private TextRenderer textRenderer;
    private boolean load = false;
    private Future simStateFuture = null;
    private ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(2);

    private float[] speeds = new float[8];
    private int speedsCount = 3;//default speed
    
    //progress bar (nb)
    private final ProgressHandle progr = ProgressHandleFactory.createHandle("MARS_Main");
    

    /**
     *
     */
    public MARS_Main() {
        super();
        //Logger.getLogger(this.getClass().getName()).setLevel(Level.OFF);
        speeds[0] = 0.25f;
        speeds[1] = 0.5f;
        speeds[2] = 0.75f;
        speeds[3] = 1f;
        speeds[4] = 1.5f;
        speeds[5] = 2.0f;
        speeds[6] = 3.0f;
        speeds[7] = 4.0f;
    }

    
    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        MARS_Main app = new MARS_Main();
        app.start();
    }

    /**
     *
     */
    @Override
    public void simpleInitApp() {
        initAssetPaths();
        initProgressBar();
        progr.progress( "Starting MARS_MAIN" );
        XML_JAXB_ConfigReaderWriter xml = new XML_JAXB_ConfigReaderWriter();
        MARS_Settings mars_settings = xml.loadMARS_Settings();
        configManager.setConfig(mars_settings.getAutoConfigName());
        //initNifty();
        progr.progress( "Init Map ViewPort" );
        initMapViewPort();
        //initAssetsLoaders();
        progr.progress( "Creating StartState" );
        startstate = new StartState(assetManager);
        startstate.setEnabled(true);
        if(!mars_settings.isAutoEnabled()){
            viewPort.attachScene(startstate.getRootNode());
            //ViewPort2.attachScene(startstate.getRootNode());
            stateManager.attach(startstate);
        }

        progr.progress( "Creating MapState" );
        mapstate = new MapState(assetManager);
        MapViewPort.attachScene(mapstate.getRootNode());   
        stateManager.attach(mapstate);
        
        /*editstate = new AUVEditorAppState();
        MapViewPort.attachScene(editstate.getRootNode());   
        stateManager.attach(editstate);*/
        
        //nifty state
        progr.progress( "Creating NiftyState" );
        niftystate = new NiftyState();
        //viewPort.attachScene(niftystate.getRootNode());
        stateManager.attach(niftystate);
        
        //attach Screenshot AppState
        progr.progress( "Creating ScreenshotAppState" );
        ScreenshotAppState screenShotState = new ScreenshotAppState();
        stateManager.attach(screenShotState);
        
        //deactivate the state, solves maybe wasd problems
        if (stateManager.getState(FlyCamAppState.class) != null) {
            stateManager.getState(FlyCamAppState.class).setEnabled(false); 
        }
        //overrirde standard flybycam      
        flyCam.setEnabled(false);
        flyCam.unregisterInput();
        advFlyCam = new AdvancedFlyByCamera(cam);
        advFlyCam.setDragToRotate(true);
        advFlyCam.setEnabled(false);
        advFlyCam.registerWithInput(inputManager);
        
        if(mars_settings.isAutoEnabled()){
            //SimState simstate = new SimState(view,configManager);
            progr.progress( "Creating SimState" );
            SimState simstate = new SimState(MARSTopComp,TreeTopComp,MARSMapComp,MARSLogComp,configManager);
            simstate.setMapState(mapstate);
            stateManager.attach(simstate);
            CentralLookup.getDefault().add(simstate);
        }else{
            configManager.setConfig("default");
        }
        
        //enable OcullusRift support
        //StereoCamAppState stereoCamAppState = new StereoCamAppState();
        //stateManager.attach(stereoCamAppState);
            
       /* FlyCamAppState flycamState = (FlyCamAppState)stateManager.getState(FlyCamAppState.class);
        if(flycamState != null){
            stateManager.detach(flycamState);
            AdvancedFlyCamAppState flyc = new AdvancedFlyCamAppState();
            AdvancedFlyByCamera advFlyCam = new AdvancedFlyByCamera(cam);
            flyc.setCamera(advFlyCam);
            flyCam = advFlyCam;
            stateManager.attach(flyc);
        }*/
        progr.finish();
        
        
        //oculllus sutff
        /*DirectionalLight sun = new DirectionalLight();
        rootNode.addLight(sun);
        
        
        Sphere sphere7 = new Sphere(16, 16, 0.025f);
        Geometry PressureSensorStart = new Geometry("PressureStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", ColorRGBA.White);
        PressureSensorStart.setMaterial(mark_mat7);
        PressureSensorStart.setLocalTranslation(0f, 0f, 0f);
        PressureSensorStart.updateGeometricState();
        rootNode.attachChild(PressureSensorStart);*/
    }
    
    private void initAssetPaths(){
        File file = InstalledFileLocator.getDefault().locate("Assets/Images", "mars.core", false);
        String absolutePath = file.getAbsolutePath();
        assetManager.registerLocator(absolutePath, FileLocator.class);
        File file2 = InstalledFileLocator.getDefault().locate("Assets/Interface", "mars.core", false);
        String absolutePath2 = file2.getAbsolutePath();
        assetManager.registerLocator(absolutePath2, FileLocator.class);
        File file3 = InstalledFileLocator.getDefault().locate("Assets/Icons", "mars.core", false);
        String absolutePath3 = file3.getAbsolutePath();       
        assetManager.registerLocator(absolutePath3, FileLocator.class);
        
        File file4 = InstalledFileLocator.getDefault().locate("Assets/Textures", "mars.core", false);
        assetManager.registerLocator(file4.getAbsolutePath(), FileLocator.class);
        File file6 = InstalledFileLocator.getDefault().locate("Assets/Textures/Sky", "mars.core", false);
        assetManager.registerLocator(file6.getAbsolutePath(), FileLocator.class);
        File file7 = InstalledFileLocator.getDefault().locate("Assets/Textures/Terrain", "mars.core", false);
        assetManager.registerLocator(file7.getAbsolutePath(), FileLocator.class);
        File file10 = InstalledFileLocator.getDefault().locate("Assets/Textures/Flow", "mars.core", false);
        assetManager.registerLocator(file10.getAbsolutePath(), FileLocator.class);
        File file20 = InstalledFileLocator.getDefault().locate("Assets/Textures/Water", "mars.core", false);
        assetManager.registerLocator(file20.getAbsolutePath(), FileLocator.class);
        
        File file5 = InstalledFileLocator.getDefault().locate("Assets/Models", "mars.core", false);
        assetManager.registerLocator(file5.getAbsolutePath(), FileLocator.class);
        
        File file8 = InstalledFileLocator.getDefault().locate("Assets/shaderblowlibs", "mars.core", false);
        assetManager.registerLocator(file8.getAbsolutePath(), FileLocator.class);
        
        File file9 = InstalledFileLocator.getDefault().locate("Assets/gridwaves", "mars.core", false);
        assetManager.registerLocator(file9.getAbsolutePath(), FileLocator.class);
        
        File file11 = InstalledFileLocator.getDefault().locate("Assets/FishEye", "mars.core", false);
        assetManager.registerLocator(file11.getAbsolutePath(), FileLocator.class);
        
        File file12 = InstalledFileLocator.getDefault().locate("Assets/Forester", "mars.core", false);
        assetManager.registerLocator(file12.getAbsolutePath(), FileLocator.class);
        
        File file13 = InstalledFileLocator.getDefault().locate("Assets/LensFlare", "mars.core", false);
        assetManager.registerLocator(file13.getAbsolutePath(), FileLocator.class);
        
        File file14 = InstalledFileLocator.getDefault().locate("Assets/MatDefs", "mars.core", false);
        assetManager.registerLocator(file14.getAbsolutePath(), FileLocator.class);
        
        File fileAll = InstalledFileLocator.getDefault().locate("Assets", "mars.core", false);
        assetManager.registerLocator(fileAll.getAbsolutePath(), FileLocator.class);
        
        File file15 = InstalledFileLocator.getDefault().locate("Assets/Materials", "mars.core", false);
        assetManager.registerLocator(file15.getAbsolutePath(), FileLocator.class);
        
        File file16 = InstalledFileLocator.getDefault().locate("Assets/Rim", "mars.core", false);
        assetManager.registerLocator(file16.getAbsolutePath(), FileLocator.class);
        
        File file17 = InstalledFileLocator.getDefault().locate("Assets/ShaderBlow", "mars.core", false);
        assetManager.registerLocator(file17.getAbsolutePath(), FileLocator.class);
        
        File file18 = InstalledFileLocator.getDefault().locate("Assets/Shaders", "mars.core", false);
        assetManager.registerLocator(file18.getAbsolutePath(), FileLocator.class);
    }
    
    /*
     * We use or own OBJLoader based on the same class here because we need a special
     * material file (for the light blow shader) not the lighting mat.
     */
    private void initAssetsLoaders(){
        assetManager.unregisterLoader(OBJLoader.class);
        assetManager.registerLoader(MyOBJLoader.class,"obj");
    }
    
    private void initMapViewPort(){
        map_cam = cam.clone();
        float aspect = (float) map_cam.getWidth() / map_cam.getHeight();
        float frustumSize = 1f;
        map_cam.setFrustum(-1000, 1000, -aspect * frustumSize, aspect * frustumSize, frustumSize, -frustumSize);
        map_cam.setParallelProjection(true);
        MapViewPort = renderManager.createMainView("MapView", map_cam);
        MapViewPort.setClearFlags(true, true, true);
        MapViewPort.setBackgroundColor(ColorRGBA.Black);
    }
    
    
    public ViewPort addState(final AbstractAppState state){
        Camera stateCam = cam.clone();
        
        if(state instanceof AppStateExtension){
            ((AppStateExtension)state).setCamera(stateCam);
            //this.enqueue(new Callable<Void>(){
             //       public Void call(){
                        //StateViewPort.attachScene(((AppStateExtension)state).getRootNode());   
            //            return null;
            //        }
            //});
            
        }else{
            Logger.getLogger(MARS_Main.class.getName()).log(Level.WARNING, "AppState: " + state + " doesn't implement the interface AppStateExtension! No RootNode found!", "");
        }
        AdvancedFlyByCamera advFlyCamState = new AdvancedFlyByCamera(stateCam);
        advFlyCamState.setDragToRotate(true);
        advFlyCamState.setEnabled(true);
        advFlyCamState.registerWithInput(inputManager);
        //ChaseCamera chaseCamState = new ChaseCamera(stateCam, inputManager);
        final ViewPort StateViewPort = renderManager.createMainView("View" + state, stateCam);
        StateViewPort.setClearFlags(true, true, true);
        StateViewPort.setBackgroundColor(ColorRGBA.Black);
        
        stateManager.attach(state);
        return StateViewPort;
    }
    
    private void initProgressBar(){
        //setting up progress bar
        progr.start();
        /*Runnable tsk = new Runnable()
        {
           public void run() {
               progr.start();
           }

        };*/
        //RequestProcessor.getDefault().post(tsk);
    }

    /**
     * dont update anything here, the statemanager does that for us
     * @param tpf
     */
    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
        
        //we have to do it here because of buggy behaviour of statsState
        if(statsDarken){
            this.setStatsStateDark(false);
            statsDarken = false;
        }

        if(startstate != null && startstate.isInitialized() && TreeTopComp!=null && MARSTopComp!=null && startstateinit==false){// little hack to allow the starting of a config only when the startstate was initialized
            MARSTopComp.allowStateInteraction();
            startstateinit = true;
        }

    }

    /**
     * we dont render(custom) anything at all. we aren't crazy.
     * @param rm
     */
    @Override
    public void simpleRender(RenderManager rm) {
    }

    @Override
    public void stop() {
        //make sure to release ros connection
        simStateFuture = this.enqueue(new Callable() {
            public Boolean call() throws Exception {
                if(stateManager.getState(SimState.class) != null){
                    SimState simState = (SimState)stateManager.getState(SimState.class);
                    simState.disconnectFromServer();
                    while(simState.getIniter().ServerRunning()){
                        
                    }
                }
                return true;
            }
        });
        /*try {
            //wait till ros killed
            Object obj = simStateFuture.get(1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ex) {
            Logger.getLogger(MARS_Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(MARS_Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TimeoutException ex) {
            Logger.getLogger(MARS_Main.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        super.stop();
    }
    
    /**
     * 
     */
    public void startSimState(){
        endStart();
        simStateFuture = this.enqueue(new Callable() {
            public Void call() throws Exception {  
                SimState simstate = new SimState(MARSTopComp,TreeTopComp,MARSMapComp,MARSLogComp,configManager);
                simstate.setMapState(mapstate);
                stateManager.attach(simstate);
                return null;
            }
        });
    }
    
    private void endStart(){
        Future startStateFuture = this.enqueue(new Callable() {
            public Void call() throws Exception {
                stateManager.getState(StartState.class).setEnabled(false);
                return null;
            }
        });
    }
    
    /**
     *
     * @param view
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

    public void setMARSMapComp(MARSMapTopComponent MARSMapComp) {
        this.MARSMapComp = MARSMapComp;
    }

    public MARSMapTopComponent getMARSMapComp() {
        return MARSMapComp;
    }

    public MARSLogTopComponent getMARSLogComp() {
        return MARSLogComp;
    }

    public void setMARSLogComp(MARSLogTopComponent MARSLogComp) {
        this.MARSLogComp = MARSLogComp;
    }

    public void setMARSTopComp(MARSTopComponent MARSTopComp) {
        this.MARSTopComp = MARSTopComp;
    }

    public MARSTopComponent getMARSTopComp() {
        return MARSTopComp;
    }

     /**
      * 
      * @return
      */
    public AppSettings getSettings(){
        return settings;
    }

    /**
     *
     * @return
     */
    public ChaseCamera getChaseCam(){
        return stateManager.getState(SimState.class).getChaseCam();
    }
    
    /**
     * 
     * @param guiFont
     */
    public void setGuiFont(BitmapFont guiFont) {
        this.guiFont = guiFont;
    }
    
    /**
     * 
     * @deprecated
     */
    @Deprecated
    public void initNifty(){
        assetManager.registerLocator("Assets/Interface", FileLocator.class);
        assetManager.registerLocator("Assets/Icons", FileLocator.class);
        niftyDisplay = new NiftyJmeDisplay(assetManager,
                inputManager,
                audioRenderer,
                guiViewPort);
        nifty = niftyDisplay.getNifty();
 
        //nifty.fromXml("nifty_loading.xml", "start", this);
        //nifty.fromXml("nifty_loading.xml", "start");
        nifty.fromXml("nifty_energy_popup.xml", "start");
        
        //set logging to less spam
        Logger.getLogger("de.lessvoid.nifty").setLevel(Level.SEVERE); 
        Logger.getLogger("NiftyInputEventHandlingLog").setLevel(Level.SEVERE); 

        guiViewPort.addProcessor(niftyDisplay);
    }
    
    /**
     * 
     * @param auv
     * @param x
     * @param y
     */
    public void setHoverMenuForAUV(final AUV auv, final int x, final int y){
        simStateFuture = this.enqueue(new Callable() {
            public Void call() throws Exception {
                if(stateManager.getState(NiftyState.class) != null){
                    NiftyState niftyState = (NiftyState)stateManager.getState(NiftyState.class);
                    niftyState.setHoverMenuForAUV(auv, x, y);
                }
                return null;
            }
        });
    }

    /**
     * 
     * @param visible
     */
    public void setHoverMenuForAUV(final boolean visible){
        simStateFuture = this.enqueue(new Callable() {
            public Void call() throws Exception {
                if(stateManager.getState(NiftyState.class) != null){
                    NiftyState niftyState = (NiftyState)stateManager.getState(NiftyState.class);
                    niftyState.setHoverMenuForAUV(visible);
                }
                return null;
            }
        });
    }
    
    public void setSpeedMenu(final boolean visible){
        simStateFuture = this.enqueue(new Callable() {
            public Void call() throws Exception {
                if(stateManager.getState(NiftyState.class) != null){
                    NiftyState niftyState = (NiftyState)stateManager.getState(NiftyState.class);
                    niftyState.setSpeedUp(visible);
                }
                return null;
            }
        });
    }
 
    /**
     * 
     * @param getFocus
     */
    public void onFocus(boolean getFocus) {
    }
    
    /**
     * 
     * @param progress
     * @param loadingText
     */
    public void setProgress(final float progress, final String loadingText) {
        //since this method is called from another thread, we enqueue the changes to the progressbar to the update loop thread
        enqueue(new Callable() {
 
            public Object call() throws Exception {
                final int MIN_WIDTH = 32;
                int pixelWidth = (int) (MIN_WIDTH + (progressBarElement.getParent().getWidth() - MIN_WIDTH) * progress);
                progressBarElement.setConstraintWidth(new SizeValue(pixelWidth + "px"));
                progressBarElement.getParent().layoutElements();
 
                textRenderer.setText(loadingText);
                return null;
            }
        });
 
    }
    
    /**
     * 
     * @param progress
     * @param loadingText
     */
    public void setProgressWithoutEnq(final float progress, String loadingText) {
        final int MIN_WIDTH = 32;
        int pixelWidth = (int) (MIN_WIDTH + (progressBarElement.getParent().getWidth() - MIN_WIDTH) * progress);
        progressBarElement.setConstraintWidth(new SizeValue(pixelWidth + "px"));
        progressBarElement.getParent().layoutElements();
 
        textRenderer.setText(loadingText);
    }
    
    /**
     * 
     * @return
     */
    public Nifty getNifty() {
        return nifty;
    }
    
    /**
     * 
     */
    public void startSimulation(){
        simStateFuture = this.enqueue(new Callable() {
            public Void call() throws Exception {
                if(stateManager.getState(SimState.class) != null){
                    SimState simState = (SimState)stateManager.getState(SimState.class);
                    simState.startSimulation();
                }
                return null;
            }
        });
    }
    
    /**
     * 
     */
    public void speedUpSimulation(){
        if(speedsCount < speeds.length-1){
            speedsCount++;
            speed = speeds[speedsCount];
            simStateFuture = this.enqueue(new Callable() {
            public Void call() throws Exception {
                if(stateManager.getState(SimState.class) != null){
                    SimState simState = (SimState)stateManager.getState(SimState.class);
                    simState.getMARSSettings().setPhysicsSpeed(speed);
                }
                return null;
            }
            });
        }
        setSpeedMenu(true);
    }
    
        /**
     * 
     */
    public void speedDownSimulation(){
        if(speedsCount > 0){
            speedsCount--;
            speed = speeds[speedsCount];
            simStateFuture = this.enqueue(new Callable() {
            public Void call() throws Exception {
                if(stateManager.getState(SimState.class) != null){
                    SimState simState = (SimState)stateManager.getState(SimState.class);
                    simState.getMARSSettings().setPhysicsSpeed(speed);
                }
                return null;
            }
        });
        }
        setSpeedMenu(true);
    }
    
    public void defaultSpeedSimulation(){
        speedsCount = 3;
        speed = speeds[speedsCount];
        simStateFuture = this.enqueue(new Callable() {
            public Void call() throws Exception {
                if(stateManager.getState(SimState.class) != null){
                    SimState simState = (SimState)stateManager.getState(SimState.class);
                    simState.getMARSSettings().setPhysicsSpeed(speed);
                }
                return null;
            }
        });
        setSpeedMenu(true);
    }
    
    public void setSpeed(float speed){
        this.speed = speed;
        setSpeedMenu(true);
    }

    public float getSpeed() {
        return speed;
    }

    /**
     * 
     */
    public void pauseSimulation(){
        simStateFuture = this.enqueue(new Callable() {
            public Void call() throws Exception {
                if(stateManager.getState(SimState.class) != null){
                    SimState simState = (SimState)stateManager.getState(SimState.class);
                    simState.pauseSimulation();
                }
                return null;
            }
        });
    }
        
    /**
     * 
     */
    public void restartSimulation(){
        simStateFuture = this.enqueue(new Callable() {
            public Void call() throws Exception {
                if(stateManager.getState(SimState.class) != null){
                    SimState simState = (SimState)stateManager.getState(SimState.class);
                    simState.restartSimulation();
                }
                return null;
            }
        });
    }
    
    /**
     * 
     * @return
     */
    public ViewPort getMapViewPort(){
        return MapViewPort;
    }

    /**
     * 
     * @return
     */
    public MapState getMapstate() {
        return mapstate;
    }
    
    @Override
    public FlyByCamera getFlyByCamera(){
        return advFlyCam;
    }
    
    /**
     * 
     * @return
     */
    public Camera getMapCamera(){
        return map_cam;
    }
    
    /**
     * 
     * @param darken
     */
    public void setStatsStateDark(boolean darken){
        //we dont want a dark underlay in the stats
        if(stateManager.getState(StatsAppState.class) != null){
            StatsAppState statsState = (StatsAppState)stateManager.getState(StatsAppState.class);
            statsState.setDarkenBehind(darken);
        }
    }
    
    /**
     * 
     */
    public void restartSimState(){
        simStateFuture = this.enqueue(new Callable() {
            public Void call() throws Exception {
                if(stateManager.getState(BulletAppState.class) != null){
                    BulletAppState bulletAppState = (BulletAppState)stateManager.getState(BulletAppState.class);
                    bulletAppState.setEnabled(false);
                    stateManager.detach(bulletAppState);
                }
                if(stateManager.getState(MapState.class) != null){
                    MapState mapState = (MapState)stateManager.getState(MapState.class);
                    //mapState.setEnabled(false);
                    mapState.clear();
                }
                if(stateManager.getState(SimState.class) != null){
                    SimState simState = (SimState)stateManager.getState(SimState.class);
                    simState.setEnabled(false);
                    stateManager.detach(simState);
                }
                return null;
            }
        });
        startSimState();
    }
    
    public void setConfigName(String configName){
        configManager.setConfig(configName);
    }
    
    public void setProgressHandle(ProgressHandle progr){
        //this.progr = progr;
    }
}