
package mars.uwCommManager;

import mars.uwCommManager.threading.CommunicationMultiPathSimulator;
import mars.uwCommManager.threading.CommunicationExecutorRunnable;
import mars.uwCommManager.helpers.CommunicationComputedDataChunk;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.system.Timer;
import com.jme3.system.lwjgl.LwjglSmoothingTimer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
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
import mars.uwCommManager.helpers.DistanceTrigger;
import mars.uwCommManager.options.CommOptionsConstants;
import static mars.uwCommManager.options.CommOptionsConstants.*;
import mars.uwCommManager.threading.CommunicationDistanceComputationRunnable;

/**
 * Entrypoint of the communications module.
 * @version 0.2
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
     * This list contains all objects that are used for multitasking
     * @deprecated should not be used anymore
     */
    private List<CommunicationsRunnable> runnables = null;
    
    
    
    private CommunicationMultiPathSimulator multiPathModule;
    private CommunicationDistanceComputationRunnable distanceTraceModule;
    /**
     * Map the AUV to its runnable
     */
    private Map<String,CommunicationExecutorRunnable> auvProcessMap;
    /**
    * The executor for multitasking
    */
    private ScheduledThreadPoolExecutor executor;
    /**
     * Used to determine how many threads the executor should use
     */
    private static int threadCount;
    public static final int RESOLUTION = 30;
    

//------------------------------- INIT -----------------------------------------
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app); 
        if(app instanceof MARS_Main){
            app = (MARS_Main)app;
        }
        
        if(!loadAndInitPreferenceListeners()) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,"Failed to load communications config");
        }
        
        executor = new ScheduledThreadPoolExecutor(threadCount);
        auvProcessMap = new HashMap();
        initAUVProcessMap(CentralLookup.getDefault().lookup(AUV_Manager.class));
        
        
        //initRunnables();

        CentralLookup.getDefault().add(this);
    }
    
    
    /**
     * @since 0.2
     * fill the AUVProcessMap with some data
     * @param auvMngr
     * @return 
     */
    private boolean initAUVProcessMap(final AUV_Manager auvMngr) {
        if(auvMngr == null) return false;
        
        HashMap<String,AUV> auvs = auvMngr.getAUVs();
            for ( AUV auv : auvs.values()){
            //Check if the AUV is enabled and has a modem
                if(auv.getAuv_param().isEnabled() && auv.hasSensorsOfClass(CommunicationDevice.class.getName())) {
                    CommunicationExecutorRunnable runnable = new CommunicationExecutorRunnable(5.6f,RESOLUTION);
                    auvProcessMap.put(auv.getName(), runnable);
                    executor.scheduleAtFixedRate(runnable, 1000000, 1000000/RESOLUTION, TimeUnit.MICROSECONDS);
                    System.out.println("Added Thread for " +auv.getName() + "there: " + runnable.toString() );
                }
                
            }
        multiPathModule = new CommunicationMultiPathSimulator();
        multiPathModule.init(auvMngr, this);
        executor.scheduleAtFixedRate(multiPathModule, 1500000, 1000000/RESOLUTION, TimeUnit.MICROSECONDS);
        distanceTraceModule = new CommunicationDistanceComputationRunnable();
        distanceTraceModule.init(auvMngr);
        executor.scheduleAtFixedRate(distanceTraceModule, 500000, 1000000, TimeUnit.MICROSECONDS);
        return true;
    }
    
    /**
     * @since 0.1
     * @deprecated this uses normal runnables instead of the executor
     */
    private void initRunnables() {
        runnables = new LinkedList<CommunicationsRunnable>();
        for(int i = 0; i<threadCount; i++) {
            CommunicationsRunnable comRunnable = new CommunicationsRunnable(this);
            if(comRunnable.init()); {
                Thread comThread = new Thread(comRunnable);
                runnables.add(comRunnable);
                comThread.start();
            }
        }
    }
    
    /**
     * Load the preferences from the options panel
     * @return whether the preferences could be loaded
     */
    private boolean loadAndInitPreferenceListeners() {
        Preferences pref = Preferences.userNodeForPackage(mars.uwCommManager.options.CommunicationConfigurationOptionsPanelController.class);
        if(pref == null) return false;
        threadCount = pref.getInt(OPTIONS_THREADCOUNT_SLIDER, 5);
        
        
        
        pref.addPreferenceChangeListener(new PreferenceChangeListener() {
            public void preferenceChange(PreferenceChangeEvent e) {
                //Distance Checkup Event
                if(e.getKey().equals(OPTIONS_DISTANCE_CHECKUP_CHECKBOX)) {
                    
                    for(CommunicationsRunnable runnable : runnables) {
                        if(e.getNewValue().equals("true")) runnable.activateDistanceCheckup();
                        else runnable.deactivateDistanceCheckup();
                    }//END iteration runnables
                }//Distance Checkup Event closed
                
                //Thread Slider Event
                if(e.getKey().equals(OPTIONS_THREADCOUNT_SLIDER)) {
                    threadCount = Integer.parseInt(e.getNewValue());
                    int counter = 0;
                    for(CommunicationsRunnable runnable : runnables) {
                        counter++;
                        if(counter>threadCount) runnable.stop();
                    }//End iteration runnables
                }//Thread Slider Event closed
            }
        });
        return true;
    }

    
//---------------------------END INIT-------------------------------------------    
    

    @Override
    public void update(final float tpf) {

        
        
        //TESTCODE BEGIN
        AUV_Manager auvManager = CentralLookup.getDefault().lookup(AUV_Manager.class);
        
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
        Timer timer = new LwjglSmoothingTimer();
        float time = 0f;
        //int counter = 0;
        while(true) {
            time += timer.getTimePerFrame();
            if(time >= 1f/60f) break;
            CommunicationMessage msg = msgQueue.poll();
            if(msg == null) break;
            /*
             *PROCESS THE MESSAGE OLD WAY
             */
            //runnables.get(counter).assignMessage(msg);
            //counter++;
            //if(counter == threadCount) counter = 0;
            /*
            PROCESS THE MESSAGE NEW WAY
            */
            CommunicationExecutorRunnable e1 = auvProcessMap.get(msg.getAuvName());
            if(e1 ==null ) {
                CommunicationExecutorRunnable runnable = new CommunicationExecutorRunnable(5.6f,RESOLUTION);
                auvProcessMap.put(msg.getAuvName(), runnable);
                executor.scheduleAtFixedRate(runnable, 1000000, 1000000/RESOLUTION, TimeUnit.MICROSECONDS);
                e1 = runnable;
            }
            List<DistanceTrigger> e2 = distanceTraceModule.getDistanceTriggerMap().get(msg.getAuvName());
            if(e2 != null) e1.setDistanceTriggers(e2);
            e1.assignMessage(msg);
            
            for(Map.Entry<String, CommunicationExecutorRunnable> entr: auvProcessMap.entrySet()) {
                List<CommunicationComputedDataChunk> chunks = entr.getValue().getComputedMessages();
                if(!chunks.isEmpty()) {
                    multiPathModule.enqueueMsges(chunks);
                }
            }
        }
    }
    
    
    /**
     * Add a message to the queue
     * @param msg The Message that should be processed
     */
    public void putMsg(CommunicationMessage msg) {
        this.msgQueue.add(msg);
    }
    
    
    
    
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
    
    public static int getThreadCount() {
        return threadCount;
    }
    
    
    

}
