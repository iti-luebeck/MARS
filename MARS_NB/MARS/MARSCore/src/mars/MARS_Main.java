/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars;

import mars.misc.MyOBJLoader;
import mars.states.AdvancedFlyByCamera;
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
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.plugins.OBJLoader;
import com.jme3.system.AppSettings;
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
import mars.states.AppStateExtension;
import mars.states.MapState;
import mars.states.NiftyState;
import mars.xml.ConfigManager;
import mars.xml.XML_JAXB_ConfigReaderWriter;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.modules.InstalledFileLocator;

/**
 * This is the starting point of MARS. Its basically and extendend
 * SimpleApplication. The real MARS magic is happening in the SimState class.
 *
 * @author Thomas Tosik
 */
public class MARS_Main extends SimpleApplication {

    //needed for graphs
    private MARSTreeTopComponent TreeTopComp;
    private MARSTopComponent MARSTopComp;
    private MARSMapTopComponent MARSMapComp;
    private MARSLogTopComponent MARSLogComp;
    private boolean startstateinit = false;
    private boolean statsDarken = true;

    StartState startstate;
    MapState mapstate;
    NiftyState niftystate;

    ChaseCamera chaseCam;
    Camera map_cam;

    ViewPort MapViewPort;

    ConfigManager configManager;

    AdvancedFlyByCamera advFlyCam;

    //nifty(gui) stuff
    private boolean load = false;
    private Future simStateFuture = null;
    private ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(2);

    //the speed settings for the speed-up-simulation button
    private float[] speeds = new float[8];
    private int speedsCount = 3;//default speed

    //progress bar (nbp)
    private final ProgressHandle progr = ProgressHandleFactory.createHandle("MARS_Main");

    /**
     *
     */
    public MARS_Main() {
        super();
        speeds[0] = 0.25f;
        speeds[1] = 0.5f;
        speeds[2] = 0.75f;
        speeds[3] = 1f;
        speeds[4] = 1.5f;
        speeds[5] = 2.0f;
        speeds[6] = 3.0f;
        speeds[7] = 4.0f;
        //Logger.getLogger("").setLevel(Level.OFF);
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

        //change renderer to an own with depthbuffer access
        //renderer = new ReadableDepthRenderer();
        initAssetPaths();
        initProgressBar();
        progr.progress("Starting MARS_MAIN");
        XML_JAXB_ConfigReaderWriter xml = new XML_JAXB_ConfigReaderWriter();
        configManager = xml.loadConfigManager();
        progr.progress("Init Map ViewPort");
        initMapViewPort();
        progr.progress("Creating StartState");
        startstate = new StartState(assetManager);
        startstate.setEnabled(true);
        if (!configManager.isAutoEnabled()) {
            viewPort.attachScene(startstate.getRootNode());
            stateManager.attach(startstate);
        }

        progr.progress("Creating MapState");
        mapstate = new MapState(assetManager);
        MapViewPort.attachScene(mapstate.getRootNode());
        stateManager.attach(mapstate);

        //nifty state
        progr.progress("Creating NiftyState");
        niftystate = new NiftyState();
        stateManager.attach(niftystate);

        //attach Screenshot AppState
        progr.progress("Creating ScreenshotAppState");
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

        if (configManager.isAutoEnabled()) {
            //SimState simstate = new SimState(view,configManager);
            progr.progress("Creating SimState");
            SimState simstate = new SimState(MARSTopComp, TreeTopComp, MARSMapComp, MARSLogComp, configManager);
            simstate.setMapState(mapstate);
            stateManager.attach(simstate);
            CentralLookup.getDefault().add(simstate);
        } else {
            configManager.setConfigName("default");
        }

        progr.finish();

        //adding mars to the central lookup after some initial stuff is ready
        final MARS_Main marsfin = this;
        simStateFuture = this.enqueue(new Callable() {
            public Void call() throws Exception {
                CentralLookup.getDefault().add(marsfin);
                return null;
            }
        });
    }

    /*
     * Used to map the folders correctly with NetBeans Platform
     */
    private void initAssetPaths() {
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
        File filePol = InstalledFileLocator.getDefault().locate("Assets/Textures/Pollution", "mars.core", false);
        assetManager.registerLocator(filePol.getAbsolutePath(), FileLocator.class);
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
    private void initAssetsLoaders() {
        assetManager.unregisterLoader(OBJLoader.class);
        assetManager.registerLoader(MyOBJLoader.class, "obj");
    }

    private void initMapViewPort() {
        map_cam = cam.clone();
        float aspect = (float) map_cam.getWidth() / map_cam.getHeight();
        float frustumSize = 1f;
        map_cam.setFrustum(-1000, 1000, -aspect * frustumSize, aspect * frustumSize, frustumSize, -frustumSize);
        map_cam.setParallelProjection(true);
        MapViewPort = renderManager.createMainView("MapView", map_cam);
        MapViewPort.setClearFlags(true, true, true);
        MapViewPort.setBackgroundColor(ColorRGBA.Black);
    }

    /**
     *
     * @param state
     * @return
     */
    public ViewPort addState(final AbstractAppState state) {
        Camera stateCam = cam.clone();

        if (state instanceof AppStateExtension) {
            ((AppStateExtension) state).setCamera(stateCam);
        } else {
            Logger.getLogger(MARS_Main.class.getName()).log(Level.WARNING, "AppState: " + state + " doesn't implement the interface AppStateExtension! No RootNode found!", "");
        }

        final ViewPort StateViewPort = renderManager.createMainView("View" + state, stateCam);
        StateViewPort.setClearFlags(true, true, true);
        StateViewPort.setBackgroundColor(ColorRGBA.Black);

        stateManager.attach(state);
        return StateViewPort;
    }

    private void initProgressBar() {
        //setting up progress bar
        progr.start();
    }

    /**
     * dont update anything here, the statemanager does that for us
     *
     * @param tpf
     */
    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);

        //we have to do it here because of buggy behaviour of statsState
        if (statsDarken) {
            this.setStatsStateDark(false);
            statsDarken = false;
        }

        if (startstate != null && startstate.isInitialized() && TreeTopComp != null && MARSTopComp != null && startstateinit == false) {// little hack to allow the starting of a config only when the startstate was initialized
            MARSTopComp.allowStateInteraction();
            startstateinit = true;
        }

    }

    /**
     * we dont render(custom) anything at all. we aren't crazy.
     *
     * @param rm
     */
    @Override
    public void simpleRender(RenderManager rm) {
    }

    /**
     *
     */
    @Override
    public void stop() {
        //make sure to release ros connection
        simStateFuture = this.enqueue(new Callable() {
            public Boolean call() throws Exception {
                if (stateManager.getState(SimState.class) != null) {
                    SimState simState = (SimState) stateManager.getState(SimState.class);
                    simState.disconnectFromServer();
                    while (simState.getIniter().ServerRunning()) {

                    }
                }
                return true;
            }
        });
        super.stop();
    }

    /**
     * Create and add a SimState to MARS.
     */
    public void startSimState() {
        endStart();
        simStateFuture = this.enqueue(new Callable() {
            public Void call() throws Exception {
                SimState simstate = new SimState(MARSTopComp, TreeTopComp, MARSMapComp, MARSLogComp, configManager);
                simstate.setMapState(mapstate);
                stateManager.attach(simstate);
                return null;
            }
        });
    }

    /*
     * Disable the StartState
     */
    @Deprecated
    private void endStart() {
        Future startStateFuture = this.enqueue(new Callable() {
            public Void call() throws Exception {
                if (stateManager.getState(StartState.class) != null) {
                    stateManager.getState(StartState.class).setEnabled(false);
                }
                return null;
            }
        });
    }

    /**
     *
     * @param TreeTopComp
     */
    @Deprecated
    public void setTreeTopComp(MARSTreeTopComponent TreeTopComp) {
        this.TreeTopComp = TreeTopComp;
    }

    /**
     *
     * @return
     */
    @Deprecated
    public MARSTreeTopComponent getTreeTopComp() {
        return TreeTopComp;
    }

    /**
     *
     * @param MARSMapComp
     */
    public void setMARSMapComp(MARSMapTopComponent MARSMapComp) {
        this.MARSMapComp = MARSMapComp;
    }

    /**
     *
     * @return
     */
    public MARSMapTopComponent getMARSMapComp() {
        return MARSMapComp;
    }

    /**
     *
     * @return
     */
    public MARSLogTopComponent getMARSLogComp() {
        return MARSLogComp;
    }

    /**
     *
     * @param MARSLogComp
     */
    public void setMARSLogComp(MARSLogTopComponent MARSLogComp) {
        this.MARSLogComp = MARSLogComp;
    }

    /**
     *
     * @param MARSTopComp
     */
    public void setMARSTopComp(MARSTopComponent MARSTopComp) {
        this.MARSTopComp = MARSTopComp;
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
     * @return
     */
    public AppSettings getSettings() {
        return settings;
    }

    /**
     *
     * @return
     */
    public ChaseCamera getChaseCam() {
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
     * Set the hover menus of AUVs to a position.
     *
     * @param auv
     * @param x
     * @param y
     */
    public void setHoverMenuForAUV(final AUV auv, final int x, final int y) {
        simStateFuture = this.enqueue(new Callable() {
            public Void call() throws Exception {
                if (stateManager.getState(NiftyState.class) != null) {
                    NiftyState niftyState = (NiftyState) stateManager.getState(NiftyState.class);
                    niftyState.setHoverMenuForAUV(auv, x, y);
                }
                return null;
            }
        });
    }

    /**
     * Set the visibility of the hover menues.
     *
     * @param visible
     */
    public void setHoverMenuForAUV(final boolean visible) {
        simStateFuture = this.enqueue(new Callable() {
            public Void call() throws Exception {
                if (stateManager.getState(NiftyState.class) != null) {
                    NiftyState niftyState = (NiftyState) stateManager.getState(NiftyState.class);
                    niftyState.setHoverMenuForAUV(visible);
                }
                return null;
            }
        });
    }

    /**
     * Make the speed-up symbol appear if simulation is speeded up.
     * 
     * @param visible
     */
    public void setSpeedMenu(final boolean visible) {
        simStateFuture = this.enqueue(new Callable() {
            public Void call() throws Exception {
                if (stateManager.getState(NiftyState.class) != null) {
                    NiftyState niftyState = (NiftyState) stateManager.getState(NiftyState.class);
                    niftyState.setSpeedUp(visible);
                }
                return null;
            }
        });
    }

    /**
     *
     */
    public void startSimulation() {
        simStateFuture = this.enqueue(new Callable() {
            public Void call() throws Exception {
                if (stateManager.getState(SimState.class) != null) {
                    SimState simState = (SimState) stateManager.getState(SimState.class);
                    simState.startSimulation();
                }
                return null;
            }
        });
    }

    /**
     *
     */
    public void speedUpSimulation() {
        if (speedsCount < speeds.length - 1) {
            speedsCount++;
            speed = speeds[speedsCount];
            simStateFuture = this.enqueue(new Callable() {
                public Void call() throws Exception {
                    if (stateManager.getState(SimState.class) != null) {
                        SimState simState = (SimState) stateManager.getState(SimState.class);
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
    public void speedDownSimulation() {
        if (speedsCount > 0) {
            speedsCount--;
            speed = speeds[speedsCount];
            simStateFuture = this.enqueue(new Callable() {
                public Void call() throws Exception {
                    if (stateManager.getState(SimState.class) != null) {
                        SimState simState = (SimState) stateManager.getState(SimState.class);
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
    public void defaultSpeedSimulation() {
        speedsCount = 3;
        speed = speeds[speedsCount];
        simStateFuture = this.enqueue(new Callable() {
            public Void call() throws Exception {
                if (stateManager.getState(SimState.class) != null) {
                    SimState simState = (SimState) stateManager.getState(SimState.class);
                    simState.getMARSSettings().setPhysicsSpeed(speed);
                }
                return null;
            }
        });
        setSpeedMenu(true);
    }

    /**
     *
     * @param speed
     */
    public void setSpeed(float speed) {
        this.speed = speed;
        setSpeedMenu(true);
    }

    /**
     *
     * @return
     */
    public float getSpeed() {
        return speed;
    }

    /**
     *
     */
    public void pauseSimulation() {
        simStateFuture = this.enqueue(new Callable() {
            public Void call() throws Exception {
                if (stateManager.getState(SimState.class) != null) {
                    SimState simState = (SimState) stateManager.getState(SimState.class);
                    simState.pauseSimulation();
                }
                return null;
            }
        });
    }

    /**
     *
     */
    public void restartSimulation() {
        simStateFuture = this.enqueue(new Callable() {
            public Void call() throws Exception {
                if (stateManager.getState(SimState.class) != null) {
                    SimState simState = (SimState) stateManager.getState(SimState.class);
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
    public ViewPort getMapViewPort() {
        return MapViewPort;
    }

    /**
     *
     * @return
     */
    public MapState getMapstate() {
        return mapstate;
    }

    /**
     *
     * @return
     */
    @Override
    public FlyByCamera getFlyByCamera() {
        return advFlyCam;
    }

    /**
     *
     * @return
     */
    public Camera getMapCamera() {
        return map_cam;
    }

    /**
     * Disable the statistics state properly so it is not shown anymore.
     * 
     * @param darken
     */
    public void setStatsStateDark(boolean darken) {
        //we dont want a dark underlay in the stats
        if (stateManager.getState(StatsAppState.class) != null) {
            StatsAppState statsState = (StatsAppState) stateManager.getState(StatsAppState.class);
            statsState.setDarkenBehind(darken);
        }
    }

    /**
     *
     */
    public void restartSimState() {
        simStateFuture = this.enqueue(new Callable() {
            public Void call() throws Exception {
                if (stateManager.getState(BulletAppState.class) != null) {
                    BulletAppState bulletAppState = (BulletAppState) stateManager.getState(BulletAppState.class);
                    bulletAppState.setEnabled(false);
                    stateManager.detach(bulletAppState);
                }
                if (stateManager.getState(MapState.class) != null) {
                    MapState mapState = (MapState) stateManager.getState(MapState.class);
                    //mapState.setEnabled(false);
                    mapState.clear();
                }
                if (stateManager.getState(SimState.class) != null) {
                    SimState simState = (SimState) stateManager.getState(SimState.class);
                    simState.setEnabled(false);
                    stateManager.detach(simState);
                }
                return null;
            }
        });
        startSimState();
    }

    /**
     *
     * @param configName
     */
    public void setConfigName(String configName) {
        configManager.setConfigName(configName);
    }
}
