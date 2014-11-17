/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager;

import com.jme3.system.lwjgl.LwjglTimer;
import com.jme3.system.Timer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.core.CentralLookup;
import mars.sensors.CommunicationDevice;
import mars.sensors.CommunicationMessage;
import mars.uwCommManager.noiseGenerators.Distancecheckup;
import mars.uwCommManager.noiseGenerators.MsgandPossibleTargetHelper;


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
    
    private int msgCount = 0;
    
    private ConcurrentLinkedQueue<CommunicationMessage> assingedMessages = null;
    
    private boolean distanceCheckupFirst = true;
    private Distancecheckup distCheck = null;
    
    
    /**
     * Basic initializations
     * @param state the CommunicationState that handles this Runnable
     */
    public CommunicationsRunnable(final CommunicationState state) {
        this.state = state;
        assingedMessages = new ConcurrentLinkedQueue<CommunicationMessage>();
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
                timer.getTimePerFrame();
                //DO WE HAVE NEW MESSAGES?
                if(assingedMessages.peek() != null) {
                    CommunicationMessage msg = assingedMessages.poll();
                    decMsgCnt();
                    MsgandPossibleTargetHelper msgandTargets = null;
                    //Check for distance of activated
                    if(distanceCheckupFirst) {
                        msgandTargets = distCheck.checkDistanceForMessage(msg);
                    } //END IF
                    //Otherwise take all AUVs
                    else {
                        msgandTargets = new MsgandPossibleTargetHelper(msg,CentralLookup.getDefault().lookup(AUV_Manager.class).getAUVs() );
                    }// END ELSE
                    
                    handleMessages(msgandTargets);
                    
                }//END IF
                
                
                // handle sleeptimes
                float timeForThisLoop = timer.getTimePerFrame();
                int sleepTime = 1000/(Math.round(timeForThisLoop*1000)+1)*(getMsgCnt()+1);
                if (sleepTime >50) sleepTime = 50;
                Thread.sleep(sleepTime);

            }//END WHILE
        }catch (Exception e) {
            //TODO We should do something usefull here
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "CommunicationManager failed!", e); 
        }//END CATCH
    }
    
    
    /**
     * This method just delivers the message to all modems that belong to targets listed in the parameters hashmap
     * @param msgAndTargets the message and all auvs that are ought to recieve it
     * @since 0.1
     * @return if everything went fine
     */
    private boolean handleMessages(MsgandPossibleTargetHelper msgAndTargets) {
        //get all AUVs
        HashMap<String,AUV> auvs = msgAndTargets.getTargets();
        //Iterate through the AUVs
        for ( AUV auv : auvs.values()){
            //Check if the AUV is enabled and has a modem
            if(auv.getAuv_param().isEnabled() && auv.hasSensorsOfClass(CommunicationDevice.class.getName())) {
                //Get the modem(s)
                ArrayList uwmo = auv.getSensorsOfClass(CommunicationDevice.class.getName());
                //Iterate through the modems and send the msg to everyone;
                Iterator it = uwmo.iterator();
                while(it.hasNext()){
                     CommunicationDevice mod = (CommunicationDevice)it.next();
                     mod.publish(msgAndTargets.getMsg().getMsg());
                }
            }
        }
        
        return true;
    }
//--------------------------------SETTERS AND GETTERS---------------------------
    
    /**
     * Assign an CommuncationMessage to this thread.
     * @since 0.1
     * @param msg a CommuncationMessage
     */
    public void assignMessage(CommunicationMessage msg) {
        assingedMessages.add(msg);
        incMsgCnt();
    }
    
    /**
     * SHOULD NOT BE USED use getMsgCount() instead.
     * ConcurrentLinkedQueue.size() is used here. Bad runtime and inaccurate in multible thread env.
     * @deprecated 
     * @return the count of currently assinged messages
     */
    public int getQueuedMessageCount() {
        return assingedMessages.size();
    }
    /**
     * Stop this runnable
     */
    public void stop() {
        running = false;
    }
    
    /**
     * Activate the distancecheckfirstup
     */
    private void activateDistanceCheckup() {
        this.distanceCheckupFirst = true;
    }
    /**
     * Deactivate the distance checking firstup
     */
    private void deactivateDistanceCheckup() {
        this.distanceCheckupFirst = false;
    }
    
    /**
     * increase the message counter. I don't use the ConcurrentLinkedQueue.size function since its error prone in multible thread env.
     * @since 0.1
     */
    private synchronized void incMsgCnt(){
        msgCount++;
    }
    
    /**
     * decrease the message counter. I don't use the ConcurrentLinkedQueue.size function since its error prone in multible thread env.
     * @since 0.1
     */
    private synchronized void decMsgCnt() {
        msgCount--;
    }
    /**
     * get the the message counter. I don't use the ConcurrentLinkedQueue.size function since its error prone in multible thread env.
     * @since 0.1
     */
    private synchronized int getMsgCnt() {
        return msgCount;
    }
    
}
