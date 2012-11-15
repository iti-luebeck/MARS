/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars;

import com.jme3.app.FlyCamAppState;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mars.accumulators.Accumulator;
import mars.states.SimState;
import mars.states.StartState;
import com.jme3.font.BitmapFont;
import mars.gui.MARSView;
import mars.xml.XMLConfigReaderWriter;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.input.ChaseCamera;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.plugins.OBJLoader;
import com.jme3.system.AppSettings;
import com.jme3.system.awt.AwtPanel;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Controller;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.xml.xpp3.Attributes;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import mars.auv.AUV;
import mars.states.MapState;
import mars.states.NiftyState;


/**
 * This is the MAIN class for JME3.
 * @author Thomas Tosik
 */
public class MARS_Main extends SimpleApplication implements ScreenController,Controller{

    //needed for graphs
    private MARSView view;
    private boolean view_init = false;
    private boolean statsDarken = true;

    StartState startstate;
    MapState mapstate;
    NiftyState niftystate;
    
    ChaseCamera chaseCam;
    
    ViewPort MapViewPort;
    
    AdvancedFlyByCamera advFlyCam;
    
    //nifty(gui) stuff
    private NiftyJmeDisplay niftyDisplay;
    private Nifty nifty;
    private Element progressBarElement;
    private TextRenderer textRenderer;
    private boolean load = false;
    private Future simStateFuture = null;
    private ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(2);

    /**
     *
     */
    public MARS_Main() {
        super();
        //Logger.getLogger(this.getClass().getName()).setLevel(Level.OFF);
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
        //initNifty();
        initMapViewPort();
        //initAssetsLoaders();
        startstate = new StartState(assetManager);
        viewPort.attachScene(startstate.getRootNode());
        stateManager.attach(startstate);
        
        mapstate = new MapState(assetManager);
        MapViewPort.attachScene(mapstate.getRootNode());
        stateManager.attach(mapstate);
        
        //nifty state
        niftystate = new NiftyState();
        viewPort.attachScene(niftystate.getRootNode());
        stateManager.attach(niftystate);
        
        //attach Screenshot AppState
        ScreenshotAppState screenShotState = new ScreenshotAppState();
        stateManager.attach(screenShotState);
        
        //overrirde standard flybycam      
        flyCam.setEnabled(false);
        advFlyCam = new AdvancedFlyByCamera(cam);
        advFlyCam.setDragToRotate(true);
        advFlyCam.setEnabled(false);
        advFlyCam.registerWithInput(inputManager);
        //deactivate the state, solves maybe wasd problems
        if (stateManager.getState(FlyCamAppState.class) != null) {
            stateManager.getState(FlyCamAppState.class).setEnabled(false); 
        }
            
       /* FlyCamAppState flycamState = (FlyCamAppState)stateManager.getState(FlyCamAppState.class);
        if(flycamState != null){
            stateManager.detach(flycamState);
            AdvancedFlyCamAppState flyc = new AdvancedFlyCamAppState();
            AdvancedFlyByCamera advFlyCam = new AdvancedFlyByCamera(cam);
            flyc.setCamera(advFlyCam);
            flyCam = advFlyCam;
            stateManager.attach(flyc);
        }*/
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
        Camera map_cam = cam.clone();
        float aspect = (float) map_cam.getWidth() / map_cam.getHeight();
        float frustumSize = 1f;
        map_cam.setFrustum(-1000, 1000, -aspect * frustumSize, aspect * frustumSize, frustumSize, -frustumSize);
        map_cam.setParallelProjection(true);
        MapViewPort = renderManager.createMainView("MapView", map_cam);
        MapViewPort.setClearFlags(true, true, true);
        MapViewPort.setBackgroundColor(ColorRGBA.Black);
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
        
        /*if(view != null && !view_init && stateManager.getState(SimState.class) != null){
            stateManager.getState(SimState.class).setView(view);
            view_init = true;
        }*/
       
        /*if (load) {//we will be loading,switching appstates
            //this.setProgress(0.5f, "dfsdfsdf");
            //System.out.println("we are loading!!!");
            if (simStateFuture != null && simStateFuture.isDone()) {//cleanup
                System.out.println("simStateFuture is done!!!!");
                nifty.gotoScreen("end");
                nifty.exit();
                guiViewPort.removeProcessor(niftyDisplay);
                load = false;
            }
        }*/
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
        /*Element element = nifty.getScreen("loadlevel").findElementByName("loadingtext");
        textRenderer = element.getRenderer(TextRenderer.class);
        progressBarElement = nifty.getScreen("loadlevel").findElementByName("progressbar");
        nifty.gotoScreen("loadlevel");
        load = true;*/
        simStateFuture = this.enqueue(new Callable() {
            public Void call() throws Exception {
                SimState simstate = new SimState(view);
                viewPort.attachScene(simstate.getRootNode());
                simstate.setMapState(mapstate);
                stateManager.attach(simstate);
                return null;
            }
        });
    }
    
    private void endStart(){
        stateManager.getState(StartState.class).setEnabled(false);
        //viewPort.detachScene(startstate.getRootNode());
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
    public MARSView getView() {
        return view;
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
    
    @Override
    public void onStartScreen() {
    }
 
    @Override
    public void onEndScreen() {
    }
    
    @Override
    public void bind(Nifty nifty, Screen screen) {
        //progressBarElement = nifty.getScreen("loadlevel").findElementByName("progressbar");
        this.nifty = nifty;
    }
 
    // methods for Controller
    @Override
    public boolean inputEvent(final NiftyInputEvent inputEvent) {
        return false;
    }
 
    @Override
    public void bind(Nifty nifty, Screen screen, Element elmnt, Properties prprts, Attributes atrbts) {
        //progressBarElement = elmnt.findElementByName("progressbar");
    }
    
    @Override
    public void init(Properties prprts, Attributes atrbts) {
    }
    
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
 
    public void onFocus(boolean getFocus) {
    }
    
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
    
    public void setProgressWithoutEnq(final float progress, String loadingText) {
        final int MIN_WIDTH = 32;
        int pixelWidth = (int) (MIN_WIDTH + (progressBarElement.getParent().getWidth() - MIN_WIDTH) * progress);
        progressBarElement.setConstraintWidth(new SizeValue(pixelWidth + "px"));
        progressBarElement.getParent().layoutElements();
 
        textRenderer.setText(loadingText);
    }
    
    public Nifty getNifty() {
        return nifty;
    }
    
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
    
    public ViewPort getMapViewPort(){
        return MapViewPort;
    }

    public MapState getMapstate() {
        return mapstate;
    }
    
    @Override
    public FlyByCamera getFlyByCamera(){
        return advFlyCam;
    }
    
    public void setStatsStateDark(boolean darken){
        //we dont want a dark underlay in the stats
        if(stateManager.getState(StatsAppState.class) != null){
            StatsAppState statsState = (StatsAppState)stateManager.getState(StatsAppState.class);
            statsState.setDarkenBehind(darken);
        }
    }
    
    public void restartSimState(){
        simStateFuture = this.enqueue(new Callable() {
            public Void call() throws Exception {
                if(stateManager.getState(SimState.class) != null){
                    SimState simState = (SimState)stateManager.getState(SimState.class);
                    viewPort.detachScene(simState.getRootNode());
                    //stateManager.detach(simState);
                    //startSimState();
                    stateManager.detach(startstate);
                }
                return null;
            }
        });
    }
}