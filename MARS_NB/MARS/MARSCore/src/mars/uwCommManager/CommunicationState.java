
package mars.uwCommManager;

import mars.uwCommManager.threading.MultiMessageMerger;
import mars.uwCommManager.threading.ModemMessageRunnable;
import mars.uwCommManager.helpers.CommunicationComputedDataChunk;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.system.Timer;
import com.jme3.system.lwjgl.LwjglSmoothingTimer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;
import mars.MARS_Main;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.core.CentralLookup;
import mars.sensors.CommunicationDevice;
import mars.sensors.CommunicationMessage;
import mars.states.MapState;
import mars.states.SimState;
import mars.uwCommManager.benchmarking.CommunicationBenchmark;
import mars.uwCommManager.graphics.CommOnMap;
import mars.uwCommManager.graphics.CommunicationVisualizer;
import mars.uwCommManager.helpers.DistanceTrigger;
import static mars.uwCommManager.options.CommOptionsConstants.*;
import mars.uwCommManager.threading.DistanceTriggerCalculator;

/**
 * Entrypoint of the communications module.
 * @version 0.2.1
 * @author Jasper Schwinghammer
 */
public class CommunicationState extends AbstractAppState {
    /**
     * Reference to the core class of the project
     */
    private MARS_Main app = null;
    
    /**
     * This queue will be used to store all messages until they are processed
     */
    private ConcurrentLinkedQueue<CommunicationMessage> msgQueue = new ConcurrentLinkedQueue<CommunicationMessage>();

    /**
     * The Runnable that is used to merge all byte arrays back to one messages
     */
    private MultiMessageMerger multiPathModule;
    /**
     * The Runnable that is used to check for possible ways from one AUV to another
     */
    private DistanceTriggerCalculator distanceTraceModule;
    /**
     * Map the AUV to its runnable
     */
    private Map<String,ModemMessageRunnable> auvProcessMap;
    /**
    * The executor for multitasking
    */
    private ScheduledThreadPoolExecutor executor;
    /**
     * Used to determine how many threads the executor should use
     */
    private static int threadCount;
    /**
     * How many ticks per secound should the runnables have
     */
    public static final int RESOLUTION = 1000;
    
    /**
     * The visualization class for the minimap
     */
    private CommOnMap commOnMap = null;
    
    /**
     * is the commMap active
     */
    private boolean commOnMapActive = true;
    
    /**
     * show borders or plane of communicationdistance on minimap
     */
    private boolean commOnMapBorders = true;
    
    /**
     * show links between AUVs on minimap
     */
    private boolean commOnMapShowCommLinks = false;
    
    
    /**
     * The mainclass of the visualization functionalities of the commodule
     */
    private CommunicationVisualizer communicationGraphics = null;
    
    
    private final boolean benchmark = false;
    private CommunicationBenchmark commBenchmark;
    

//------------------------------- INIT -----------------------------------------
    /**
     * initialize all basic stuff.
     * at the end the object is added to the CentralLookup
     * @since 0.1
     * @param stateManager the stateManager of MARS
     * @param app the main application
     */
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app); 
        if(app instanceof MARS_Main){
            this.app = (MARS_Main)app;
        }
        
        
        //load settings
        if(!loadAndInitPreferenceListeners()) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,"Failed to load communications config");
        }
        //Init the threadpool
        executor = new ScheduledThreadPoolExecutor(1);
        //prepare and start the runnables for multithreading
        auvProcessMap = new HashMap();
        if (!initRunnables(CentralLookup.getDefault().lookup(SimState.class).getAuvManager())) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Something went wrong while initializing the communications minimap visualization {0}", CentralLookup.getDefault().lookup(AUV_Manager.class));
        }
        
        
        
        
        commOnMap = new CommOnMap(commOnMapActive,commOnMapBorders,commOnMapShowCommLinks);
        if(!commOnMap.init(app.getStateManager().getState(MapState.class), 
            CentralLookup.getDefault().lookup(SimState.class).getAuvManager(), CentralLookup.getDefault().lookup(SimState.class).getMARSSettings(),
            app.getAssetManager(),this.app)) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Something went wrong while initializing the communications minimap visualization{0} {1} {2}", new Object[]{app.getStateManager().getState(MapState.class), CentralLookup.getDefault().lookup(AUV_Manager.class), CentralLookup.getDefault().lookup(SimState.class).getMARSSettings()});
        }
        
        communicationGraphics = new CommunicationVisualizer(this.app, this,CentralLookup.getDefault().lookup(SimState.class).getAuvManager(),distanceTraceModule) ;
        communicationGraphics.init();
        distanceTraceModule.getEventGenerator().addListener(commOnMap);
        //Init done, add to centrallookup
        CentralLookup.getDefault().add(this);
    }
    
    
    /**
     * @since 0.2
     * Init all runnables for multithreading
     * @param auvManager the AUV_Manager
     * @return if the initialization was successful
     */
    private boolean initRunnables(final AUV_Manager auvManager) {
        if(auvManager == null) return false;
        
        HashMap<String,AUV> auvs = auvManager.getAUVs();
            for ( AUV auv : auvs.values()){
            //Check if the AUV is enabled and has a modem
                if(auv.getAuv_param().isEnabled() && auv.hasSensorsOfClass(CommunicationDevice.class.getName())&&!auvProcessMap.containsKey(auv.getName())) {
                    ModemMessageRunnable runnable = new ModemMessageRunnable(1f,RESOLUTION,auv.getName(),auv);
                    runnable.init();
                    auvProcessMap.put(auv.getName(), runnable);
                    executor.scheduleAtFixedRate(runnable, 2000000, 1000000/RESOLUTION, TimeUnit.MICROSECONDS);
                }
                
            }
        if(multiPathModule == null) {
            multiPathModule = new MultiMessageMerger();
            multiPathModule.init(auvManager, this);
            executor.scheduleAtFixedRate(multiPathModule, 2500000, 1000000/RESOLUTION/10, TimeUnit.MICROSECONDS);
        }
        if(distanceTraceModule == null) {
            distanceTraceModule = new DistanceTriggerCalculator(CentralLookup.getDefault().lookup(SimState.class),executor);
            distanceTraceModule.init(auvManager);
            for(Map.Entry<String,ModemMessageRunnable> entry : auvProcessMap.entrySet()) {
                entry.getValue().setDistanceTriggerCalculator(distanceTraceModule);
            }
            executor.scheduleAtFixedRate(distanceTraceModule, 1500000, 100000, TimeUnit.MICROSECONDS);
        }
        
        executor.schedule(new Runnable() {

            @Override
            public void run() {
                executor.setCorePoolSize(threadCount);
            }
        }, 3, TimeUnit.SECONDS);

        if(benchmark&&commBenchmark==null) {
            commBenchmark = new CommunicationBenchmark(this, executor);
            commBenchmark.init();
        }
        

        return true;
    }
    

    
    /**
     * Load the preferences from the options panel
     * @since 0.1
     * @return whether the preferences could be loaded
     */
    private boolean loadAndInitPreferenceListeners() {
        Preferences pref = Preferences.userNodeForPackage(mars.uwCommManager.options.CommunicationConfigurationOptionsPanelController.class);
        if(pref == null) return false;
        threadCount = pref.getInt(OPTIONS_THREADCOUNT_SLIDER, 5);
        commOnMapActive = pref.getBoolean(OPTIONS_SHOW_MINIMAP_RANGE_CHECKBOX, false);
        commOnMapBorders = pref.getBoolean(OPTIONS_MINIMAP_CIRCLE_BORDER_RADIOBUTTON,false);
        commOnMapShowCommLinks = pref.getBoolean(OPTIONS_MINIMAP_SHOW_ACTIVE_LINKS_CHECKBOX, false);
        
        pref.addPreferenceChangeListener(new PreferenceChangeListener() {
            @Override
            public void preferenceChange(PreferenceChangeEvent e) {
           
                //Thread Slider Event
                if(e.getKey().equals(OPTIONS_THREADCOUNT_SLIDER)) {
                    threadCount = Integer.parseInt(e.getNewValue());
                    executor.setCorePoolSize(threadCount);
                    return;
                }//Thread Slider Event closed
                
                //Show Range Event
                if(e.getKey().equals(OPTIONS_SHOW_MINIMAP_RANGE_CHECKBOX)){
                    commOnMapActive = Boolean.parseBoolean(e.getNewValue());
                    if(!(commOnMap == null)) commOnMap.setActive(commOnMapActive);
                    return;
                }//Show Range event closed
                
                //Range display Event
                if(e.getKey().equals(OPTIONS_MINIMAP_CIRCLE_BORDER_RADIOBUTTON)) {
                    commOnMapBorders = Boolean.parseBoolean(e.getNewValue());
                    if(!(commOnMap == null)) commOnMap.setBorders(commOnMapBorders);
                    return;
                }//Range display event closed
                if(e.getKey().equals(OPTIONS_MINIMAP_OPAQUE_CIRCLE)) {
                    commOnMapBorders = !Boolean.parseBoolean(e.getNewValue());
                    if(!(commOnMap == null)) commOnMap.setBorders(commOnMapBorders);
                    return;
                }
                if(e.getKey().equals(OPTIONS_MINIMAP_SHOW_ACTIVE_LINKS_CHECKBOX)) {
                    commOnMapShowCommLinks = Boolean.parseBoolean(e.getNewValue());
                    if(!(commOnMap == null)) commOnMap.setShowLinks(commOnMapShowCommLinks);
                }
            }
        });
        return true;
    }

    
//---------------------------END INIT-------------------------------------------    
    
    /**
     * update loop, called by MARS Mainthread only
     * @since 0.1
     * @param tpf time since last frame
     */
    @Override
    public void update(final float tpf) {
        if(benchmark) commBenchmark.update(tpf);
        
        
        
        
        commOnMap.update(tpf);
        communicationGraphics.update(tpf);

        
        
        //TESTCODE BEGIN
        AUV_Manager auvManager = CentralLookup.getDefault().lookup(SimState.class).getAuvManager();
        
        if(auvManager != null) {
            HashMap<String,AUV> auvs = auvManager.getAUVs();
            
            for ( AUV auv : auvs.values()){
            //Check if the AUV is enabled and has a modem
                if(auv.getAuv_param().isEnabled() && auv.hasSensorsOfClass(CommunicationDevice.class.getName())) {
                    //Get the modem(s)
                    ArrayList uwmo = auv.getSensorsOfClass(CommunicationDevice.class.getName());
                    //Iterate through the modems and send the msg to everyone;
                    Iterator it = uwmo.iterator();
                    while(it.hasNext()){
                        CommunicationDevice mod = (CommunicationDevice)it.next();
                        mod.update(tpf);
                    }
                }
            }
        }
        //TESTCODE END
        
        
        
        //should not take too much time to dispatch all messages, we use
        // this timer to stop in case of too many messages
        //int counter = 0;
        
        for(Map.Entry<String, ModemMessageRunnable> entr: auvProcessMap.entrySet()) {
                List<CommunicationComputedDataChunk> chunks = entr.getValue().getComputedMessages();
                if(!chunks.isEmpty()) {
                    multiPathModule.enqueueMsges(chunks);
                }
            }
        while(true) {
            //Get the messages from the runnables and merge them in the multiPathModule
            CommunicationMessage msg = msgQueue.poll();
            if(msg == null) break;

            /*
            PROCESS THE MESSAGE 
            */
            //Check if the AUV allready has a Runnable
            ModemMessageRunnable e1 = auvProcessMap.get(msg.getAuvName());
            if(e1 ==null ) {
                //if not create a new one
                ModemMessageRunnable runnable = new ModemMessageRunnable(5.6f,RESOLUTION,msg.getAuvName(),auvManager.getAUV(msg.getAuvName()));
                runnable.init();
                runnable.setDistanceTriggerCalculator(distanceTraceModule);
                auvProcessMap.put(msg.getAuvName(), runnable);
                executor.scheduleAtFixedRate(runnable, 1000000, 1000000/RESOLUTION, TimeUnit.MICROSECONDS);
                e1 = runnable;
            }
            //Check if there are any distanceTriggers for the current AUV and load them
            List<DistanceTrigger> e2 = distanceTraceModule.getDistanceTriggerMap().get(msg.getAuvName());
            if(e2 != null) e1.setDistanceTriggers(e2);
            e1.assignMessage(msg);
        }
    }
    
    
//----------------------------END MAINLOOP BEGIN HELPERS SETTER GETTERS------------------------------

    
    /**
     * Add a message to the queue
     * @since 0.1
     * @param msg The Message that should be processed
     */
    public void putMsg(CommunicationMessage msg) {
        this.msgQueue.add(msg);
    }
    
       
    
    /**
     * Add AUV to communicationssystem
     * @since 0.2.1
     * @param auv 
     */
    public void addAUV(AUV auv) {
        commOnMap.addMapGraphicsToAUV(auv);
    }
    
    /**
     * 
     * @return the current value of threadCount
     */
    public static int getThreadCount() {
        return threadCount;
    }
    
    
    
    /**
     * cleanup and kill the executer
     */
    @Override
    public void cleanup() {
      super.cleanup();
      executor.shutdown();
    }
 
    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        if(enabled){
        } else {
        }
    }
 
    
    
    
    

}
