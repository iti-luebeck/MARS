package mars.water;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture2D;
import com.jme3.water.WaterFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import mars.core.CentralLookup;
import mars.states.AppStateExtension;
import mars.states.SimState;
import org.openide.util.lookup.ServiceProvider;

/**
 * Adds several water effects (water surface, underwater view, foam trails).
 * @author John Paul Jonte
 */
@ServiceProvider(service=AbstractAppState.class)
public class WaterState extends AbstractAppState implements AppStateExtension {
    /**
     * Current instance of the WaterState.
     */
    private static WaterState instance = null;
    /**
     * MARS application instance.
     */
    private SimpleApplication app;
    /**
     * Root node of the state.
     */
    private Node rootNode = new Node("WaterEffects Node");
    /**
     * Asset manager of the app.
     */
    private AssetManager assetManager;
    /**
     * Input manager of the app.
     */
    private InputManager inputManager;
    /**
     * Viewport of the app.
     */
    private ViewPort viewPort;
    /**
     * Camera of the app.
     */
    private Camera cam;
    /**
     * Direction of global light (sun etc).
     */
    private Vector3f lightDir = new Vector3f(1, -1, 1);
    /**
     * Grid for the water surface.
     */
    private ProjectedGrid grid;
    /**
     * Height generator for the grid.
     */
    private final WaterHeightGenerator heightGenerator;
    /**
     * Geometry of the grid.
     */
    private Geometry projectedGridGeometry;
    /**
     * Filter that creates water surface and underwater view.
     */
    private WaterGridFilter water;
    /**
     * JME's built-in WaterFilter for reference.
     */
    private WaterFilter waterJME;
    /**
     * Filter that creates particles under water.
     */
    private WaterParticleFilter particles;
    /**
     * Processor that contains all filters.
     */
    private FilterPostProcessor filterProcessor;
    /**
     * List of vehicles that are tracked for foam trails.
     */
    private final List<Spatial> tracking;
    /**
     * Scene to be reflected.
     */
    private Node reflection;
    private SedimentEmitter emitter;
    
    /**
     * Creates a new WaterState.
     */
    public WaterState() {
        super();
        heightGenerator = new WaterHeightGenerator();
        tracking = new ArrayList<Spatial>();
    }
    
    public WaterState(Node reflection) {
        this();
        this.reflection = reflection;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app          = (SimpleApplication) app;
        this.assetManager = app.getAssetManager();
        this.inputManager = app.getInputManager();
        this.viewPort     = app.getViewPort();
        this.cam          = app.getCamera();
        
        // Projected Grid
        grid = new ProjectedGrid(this.app.getTimer(), cam, 100, 70, 0.02f, heightGenerator);
        Material heightMat = new Material(assetManager, "MatDefs/Water/WaterLevel.j3md");
        projectedGridGeometry = new Geometry("Projected Grid", grid);
        projectedGridGeometry.setMaterial(heightMat);
        projectedGridGeometry.setLocalTranslation(0, 0, 0);
        rootNode.attachChild(projectedGridGeometry);
        
        // Particle Emitter
        Material particle = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
        particle.setTexture("Texture", assetManager.loadTexture("Textures/mud.png"));
        particle.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.AlphaAdditive);
        
        emitter = new SedimentEmitter();
        emitter.setParticleMaterial(particle);
        
        rootNode.attachChild(emitter.getRootNode());
        
        CentralLookup cl = CentralLookup.getDefault();
        SimState sim = cl.lookup(SimState.class);
        
        if (sim != null) {
            reflection = sim.getSceneReflectionNode();
        }
        
        // Filters
        filterProcessor = new FilterPostProcessor(assetManager);
        
        // Water Filter
        water = new WaterGridFilter(reflection, lightDir, this.app.getTimer(), heightGenerator);
        
        water.setWaveScale(0.003f);
        water.setMaxAmplitude(1f);
        water.setFoamExistence(new Vector3f(1f, 2.5f, 0.5f));
        water.setFoamTexture((Texture2D) assetManager.loadTexture("Common/MatDefs/Water/Textures/foam.jpg"));
        water.setRefractionStrength(.3f);
        water.setTrailLength(200);
        
        filterProcessor.addFilter(water);
        
        // JME water filter
        waterJME = new WaterFilter(reflection, lightDir);
        
        waterJME.setWaveScale(0.003f);
        waterJME.setMaxAmplitude(1f);
        waterJME.setFoamExistence(new Vector3f(1f, 2.5f, 0.5f));
        waterJME.setFoamTexture((Texture2D) assetManager.loadTexture("Common/MatDefs/Water/Textures/foam.jpg"));
        waterJME.setRefractionStrength(.3f);
        waterJME.setEnabled(false);
        
        filterProcessor.addFilter(waterJME);
        
        // Water Particle Filter
        particles = new WaterParticleFilter();
        particles.setParticleColor(new ColorRGBA(.3f, .34f, .21f, 1));
        particles.setCoordinateScale(new Vector3f(.1f, .1f, .1f));
        particles.setMaximumIntensity(1f);
        particles.setFalloff(3f);
        particles.setTimeScale(.01f);
        particles.setOctaves(3);
        particles.setOctaveOffset(3);
        particles.setPersistence(.9f);
        
        filterProcessor.addFilter(particles);
        
        viewPort.addProcessor(filterProcessor);
        
        // add input listeners
        inputManager.addMapping("setDebug0", new KeyTrigger(KeyInput.KEY_0));
        inputManager.addMapping("setDebug1", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("setDebug2", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("setDebug3", new KeyTrigger(KeyInput.KEY_3));
        inputManager.addMapping("setDebug4", new KeyTrigger(KeyInput.KEY_4));
        inputManager.addMapping("setDebug5", new KeyTrigger(KeyInput.KEY_5));
        inputManager.addMapping("setDebug6", new KeyTrigger(KeyInput.KEY_6));
        inputManager.addMapping("setDebug7", new KeyTrigger(KeyInput.KEY_7));
        inputManager.addMapping("setDebug8", new KeyTrigger(KeyInput.KEY_8));
        inputManager.addMapping("setDebug9", new KeyTrigger(KeyInput.KEY_9));
        inputManager.addMapping("toggleFilter", new KeyTrigger(KeyInput.KEY_SPACE));
        
        ActionListener debugListener = new ActionListener() {
            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
                if (isPressed) {
                    water.setDebug(Character.getNumericValue(name.charAt(name.length() - 1)));
                }
            }
        };
        
        inputManager.addListener(new ActionListener() {

            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
                if (isPressed) {
                    water.setEnabled(!water.isEnabled());
                    waterJME.setEnabled(!waterJME.isEnabled());
                }
            }
        }, "toggleFilter");
        
        inputManager.addListener(debugListener, "setDebug0");
        inputManager.addListener(debugListener, "setDebug1");
        inputManager.addListener(debugListener, "setDebug2");
        inputManager.addListener(debugListener, "setDebug3");
        inputManager.addListener(debugListener, "setDebug4");
        inputManager.addListener(debugListener, "setDebug5");
        inputManager.addListener(debugListener, "setDebug6");
        inputManager.addListener(debugListener, "setDebug7");
        inputManager.addListener(debugListener, "setDebug8");
        inputManager.addListener(debugListener, "setDebug9");
        
        this.app.getRootNode().attachChild(getRootNode());
        
        setInstance(this);
    }
    
    @Override
    public void update(float tpf) {
        Iterator<Spatial> iterator = tracking.iterator();
        while (iterator.hasNext()) {
            water.track(iterator.next());
            iterator.remove();
        }
        
        if (water.isEnabled()) particles.setUnderwater(water.isUnderWater());
        grid.update(cam.getViewMatrix().clone());
        emitter.update(app.getRootNode());
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        viewPort.removeProcessor(filterProcessor);
        rootNode.removeFromParent();
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        
        if (enabled) {
            viewPort.addProcessor(filterProcessor);
            app.getRootNode().attachChild(rootNode);
        }
        else {
            viewPort.removeProcessor(filterProcessor);
            rootNode.removeFromParent();
        }
    }
    
    /**
     * Get the current instance.
     * @return current instance
     */
    public static WaterState getInstance() {
        return instance;
    }

    /**
     * Set the current instance.
     * @param instance New instance
     */
    public static void setInstance(WaterState instance) {
        WaterState.instance = instance;
    }
    
    /**
     * Gets the {@link HeightGenerator} used in this instance.
     * @return HeightGenerator
     */
    public WaterHeightGenerator getHeightGenerator() {
        return heightGenerator;
    }
    
    /**
     * Gets the {@link WaterGridFilter} being used.
     * @return water filter
     */
    public WaterGridFilter getWaterFilter() {
        return water;
    }
    
    public WaterFilter getJMEWaterFilter() {
        return waterJME;
    }
    
    /**
     * Gets the {@link WaterParticleFilter} being used.
     * @return water particle filter
     */
    public WaterParticleFilter getParticleFilter() {
        return particles;
    }
    
    /**
     * Adds a vehicle to the tracking list for foam trails.
     * @param object Vehicle Spatial to be tracked
     */
    public void track(Spatial object) {
        tracking.add(object);
    }

    public Vector3f getLightDir() {
        return lightDir;
    }

    public void setLightDir(Vector3f lightDir) {
        this.lightDir = lightDir;
    }
    
    public SedimentEmitter getSedimentEmitter() {
        return emitter;
    }

    @Override
    public Node getRootNode() {
        return rootNode;
    }

    @Override
    public void setCamera(Camera cam) {
    }
}
