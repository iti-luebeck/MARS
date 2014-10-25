
package mars.uwCommManager;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.system.Timer;
import com.jme3.system.lwjgl.LwjglSmoothingTimer;
import java.util.concurrent.ConcurrentLinkedQueue;
import mars.MARS_Main;
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
    
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app); 
        if(app instanceof MARS_Main){
            app = (MARS_Main)app;
        }
    }
    
    @Override
    public void update(final float tpf) {
        //should not take too much time to dispatch all messages, we use
        // this timer to stop in case of too many messages
        Timer timer = new LwjglSmoothingTimer();
        float time = 0f;
        while(true) {
            time += timer.getTimePerFrame();
            if(time >= 1f/60f) break;
            CommunicationMessage msg = msgQueue.poll();
            if(msg == null) break;
            /*
             *PROCESS THE MESSAGE 
             */
        }
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
