/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager;

import java.util.concurrent.ConcurrentLinkedQueue;
import mars.sensors.CommunicationMessage;

/**
 *
 * @author jaspe_000
 */
public class CommunicationExecutorRunnable implements Runnable{
    
    private final float MODEM_BANDWIDTH;
    private final float RESOLUTION;
    private final float BANDWIDTH_PER_TICK;
    
    public CommunicationExecutorRunnable(float modem_bandwidth, int resolution) {
        MODEM_BANDWIDTH = modem_bandwidth;
        RESOLUTION = resolution;
        BANDWIDTH_PER_TICK = MODEM_BANDWIDTH/RESOLUTION;
    }
    
    
    private ConcurrentLinkedQueue<CommunicationMessage> newMessages = null;

    @Override
    public void run() {
        
    }
   
     public void assignMessage(CommunicationMessage msg) {
        newMessages.add(msg);
    }
    
}
