/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars;

import mars.states.SimState;
import mars.states.StartState;
import com.jme3.font.BitmapFont;
import mars.gui.MARSView;
import mars.xml.XMLConfigReaderWriter;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.input.ChaseCamera;
import com.jme3.math.ColorRGBA;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.system.AppSettings;
import com.jme3.system.awt.AwtPanel;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Controller;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.input.NiftyInputEvent;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.SizeValue;
import de.lessvoid.xml.xpp3.Attributes;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import mars.states.MapState;


/**
 * This is the MAIN class for JME3.
 * @author Thomas Tosik
 */
public class MARS_Main extends SimpleApplication implements ScreenController,Controller{

    //needed for graphs
    private MARSView view;
    private boolean view_init = false;

    StartState startstate;
    MapState mapstate;
    
    ChaseCamera chaseCam;
    
    ViewPort MapViewPort;
    
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
        startstate = new StartState(assetManager);
        viewPort.attachScene(startstate.getRootNode());
        stateManager.attach(startstate);
        
        mapstate = new MapState(assetManager);
        MapViewPort.attachScene(mapstate.getRootNode());
        stateManager.attach(mapstate);
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
        if(view != null && !view_init && stateManager.getState(SimState.class) != null){
            stateManager.getState(SimState.class).setView(view);
            view_init = true;
        }
       
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
    
    public void initNifty(){
        assetManager.registerLocator("Assets/Interface", FileLocator.class.getName());
        niftyDisplay = new NiftyJmeDisplay(assetManager,
                inputManager,
                audioRenderer,
                guiViewPort);
        nifty = niftyDisplay.getNifty();
 
        //nifty.fromXml("nifty_loading.xml", "start", this);
        nifty.fromXml("nifty_loading.xml", "start");
 
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
}