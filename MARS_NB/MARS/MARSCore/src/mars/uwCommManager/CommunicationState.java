
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
import mars.MARS_Main;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.core.CentralLookup;
import mars.sensors.CommunicationDevice;
import mars.sensors.CommunicationMessage;

/**
 * Entrypoint of the communications module.
 * @version 0.1
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
     * This list contains all threads that are assigned to the communications
     */
    private List<Thread> threads = null;
    private List<CommunicationsRunnable> runnables = null;
    
    public static final int THREAD_COUNT = 3;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app); 
        if(app instanceof MARS_Main){
            app = (MARS_Main)app;
        }
        threads = new LinkedList<Thread>();
        runnables = new LinkedList<CommunicationsRunnable>();
        
        for(int i = 0; i<THREAD_COUNT; i++) {
            CommunicationsRunnable comRunnable = new CommunicationsRunnable(this);
            if(comRunnable.init()); {
                Thread comThread = new Thread (comRunnable);
                threads.add(comThread);
                runnables.add(comRunnable);
                comThread.start();
        }
        }
        CentralLookup.getDefault().add(this);
    }
    
    @Override
    public void update(final float tpf) {
        //should not take too much time to dispatch all messages, we use
        // this timer to stop in case of too many messages
        
        
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
            if(counter == THREAD_COUNT) counter = 0;
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
    

}
