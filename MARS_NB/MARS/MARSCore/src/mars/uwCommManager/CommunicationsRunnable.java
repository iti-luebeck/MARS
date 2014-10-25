/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager;

import com.jme3.system.lwjgl.LwjglTimer;
import com.jme3.system.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Jasper Schwinghammer
 * @version 0.1
 */
public class CommunicationsRunnable implements Runnable {

    private CommunicationState state = null;
    private Timer timer;
    private boolean running = true;
    
    
    public CommunicationsRunnable(final CommunicationState state) {
        this.state = state;
        timer = new LwjglTimer();
    }
    
    @Override
    public void run() {
        try{
            while(running) {
                //DO STUFF
            }
        }catch (Exception e) {
            //TODO We should do something usefull here
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "CommunicationManager failed!", e); 
        }
    }
    
    
    
}
