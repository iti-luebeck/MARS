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
import mars.auv.AUV_Manager;
import mars.core.CentralLookup;
import mars.uwCommManager.noiseGenerators.Distancecheckup;


/**
 *
 * @author Jasper Schwinghammer
 * @version 0.1
 */
public class CommunicationsRunnable implements Runnable {

    private CommunicationState state = null;
    /**
     * We will use this timer to check the runtime of the methods.
     * If it takes too long the thread can remove some tasks from it's queue itself.
     */
    private Timer timer = null;
    private boolean running = true;
    
    private boolean distanceCheckupFirst = false;
    private Distancecheckup distCheck = null;
    
    
    /**
     * Basic initializations
     * @param state the CommunicationState that handles this Runnable
     */
    public CommunicationsRunnable(final CommunicationState state) {
        this.state = state;
    }
    
    /**
     * Initialize all non-trivial stuff
     * @since 0.1
     * @return 
     */
    public boolean init() {
        timer = new LwjglTimer();
        distCheck = new Distancecheckup();
        if(!distCheck.init(CentralLookup.getDefault().lookup(AUV_Manager.class)))return false;

        
        return true;
    }
    
    

    @Override
    public void run() {
        try{
            while(running) {
                if(distanceCheckupFirst) {
                    //DO DISTANCE CHECKUP
                }
            }
        }catch (Exception e) {
            //TODO We should do something usefull here
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "CommunicationManager failed!", e); 
        }
    }
//--------------------------------SETTERS AND GETTERS---------------------------
    
    
    private void activateDistanceCheckup() {
        this.distanceCheckupFirst = true;
    }
    
    private void deactivateDistanceCheckup() {
        this.distanceCheckupFirst = false;
    }
    
    
}
