
package mars.uwCommManager;

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
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
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
import mars.uwCommManager.options.CommOptionsConstants;
import static mars.uwCommManager.options.CommOptionsConstants.*;

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
    
    /**
     * 
     */
    
    
    
    
    private static int threadCount;
    
    /**
     * The executor for multitasking
     */
     ScheduledThreadPoolExecutor executor;
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
        
        
        initRunnables();

        CentralLookup.getDefault().add(this);
    }
    
    /**
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
    
    private boolean loadAndInitPreferenceListeners() {
        Preferences pref = Preferences.userNodeForPackage(mars.uwCommManager.options.CommunicationConfigurationOptionsPanelController.class);
        if(pref == null) return false;
        threadCount = pref.getInt(OPTIONS_THREADCOUNT_SLIDER, 3);
        
        
        
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
        int counter = 0;
        while(true) {
            time += timer.getTimePerFrame();
            if(time >= 1f/60f) break;
            CommunicationMessage msg = msgQueue.poll();
            if(msg == null) break;
            /*
             *PROCESS THE MESSAGE 
             */
            runnables.get(counter).assignMessage(msg);
            counter++;
            if(counter == threadCount) counter = 0;
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
