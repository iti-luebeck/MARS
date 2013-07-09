/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.auv;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import mars.PhysicalEnvironment;
import mars.sensors.ModemMessage;
import mars.states.SimState;
import mars.sensors.UnderwaterModem;
import mars.server.MARS_Server;

/**
 * This class is responsible for the underwater communication. It assures that every auv gets the messages.
 * Also its responsible for the noise and realistic propagation of the messages.
 * @author Thomas Tosik
 */
public class Communication_Manager {

    private ConcurrentLinkedQueue<ModemMessage> msgQueue = new ConcurrentLinkedQueue<ModemMessage>();

    private final HashMap<String,UnderwaterModem> uws = new HashMap<String, UnderwaterModem>();

    private MARS_Server raw_server;
    private AUV_Manager auv_manager;
    
    /**
     * 
     * @param auv_manager
     * @param simstate
     * @param detectable
     * @param pe
     */
    public Communication_Manager(AUV_Manager auv_manager,SimState simstate, Node detectable,PhysicalEnvironment pe) {
        this.auv_manager = auv_manager;
    }
    
    /**
     * 
     * @param raw_server
     */
    public void setServer(MARS_Server raw_server){
        this.raw_server = raw_server;
    }
    
    /**
     * 
     * @param tpf
     */
    public void update(float tpf){
        //System.out.println("Time to communicate: " + tpf);
        ModemMessage peek = msgQueue.peek();
        if(peek != null){
            ModemMessage poll = msgQueue.poll();
            updateCommunication(poll.getAuvName(),poll.getMsg());
            sendMsgs(poll.getAuvName(),poll.getMsg());
        }
        //updateComNet();
    }
    
    private void updateNoise(){
        
    }
    
    /*
     * Seeks the underwater modems of the other auvs and send the msgs to them
     * checks for distance and noise is also made here
     */
    private void updateCommunication(String auv_name, String msg){
        AUV sender = (AUV)auv_manager.getAUV(auv_name);
        ArrayList sender_uwmo = sender.getSensorsOfClass(UnderwaterModem.class.getName());
        UnderwaterModem senderUW = (UnderwaterModem)sender_uwmo.get(0);
        Vector3f senderUWPos = senderUW.getWorldPosition();
        
        HashMap<String,AUV> auvs = auv_manager.getAUVs();
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            if(auv.getAuv_param().isEnabled() && auv.hasSensorsOfClass(UnderwaterModem.class.getName()) && !auv.getName().equals(auv_name)){
                ArrayList uwmo = auv.getSensorsOfClass(UnderwaterModem.class.getName());
                Iterator it = uwmo.iterator();
                while(it.hasNext()){
                    UnderwaterModem mod = (UnderwaterModem)it.next();
                    Vector3f modPos = mod.getWorldPosition();
                    Vector3f distance = modPos.subtract(senderUWPos);
                    //System.out.println(auv.getName() + " modPos: " + modPos + " dis: " + Math.abs(distance.length()));
                    if( Math.abs(distance.length()) <= senderUW.getPropagationDistance() ){//check if other underwatermodem isn't too far away
                        mod.publish(msg);
                    }
                }        
            }
        }    
    }
    
    private void updateComNet(){
        //update list of modems
        HashMap<String, AUV> auvs = auv_manager.getAUVs();
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            if(auv.getAuv_param().isEnabled() && auv.hasSensorsOfClass(UnderwaterModem.class.getName())){
                ArrayList sender_uwmo = auv.getSensorsOfClass(UnderwaterModem.class.getName());
                UnderwaterModem senderUW = (UnderwaterModem)sender_uwmo.get(0);
                uws.put(auv.getName(), senderUW);
            }
        }
        
        //filter them if dictance to great and send them to the modem for updates
        for ( String elem : auvs.keySet() ){
            AUV auv = (AUV)auvs.get(elem);
            if(auv.getAuv_param().isEnabled() && auv.hasSensorsOfClass(UnderwaterModem.class.getName())){
                ArrayList sender_uwmo = auv.getSensorsOfClass(UnderwaterModem.class.getName());
                UnderwaterModem senderUW = (UnderwaterModem)sender_uwmo.get(0);
                senderUW.updateComNet(uws);
            }
        }
    }
    
    @Deprecated
    private void sendMsgs(String auv_name, String msg){
        //System.out.println("Sending msg: " + msg);
        if(raw_server != null){
            raw_server.sendStringToAllConnections(msg);
        }
    }
    
    /**
     * 
     * @param auv_name
     * @param msg
     */
    public synchronized void putMsg(String auv_name,String msg){
        msgQueue.offer(new ModemMessage(auv_name, msg));
        System.out.println("Added msg to bag: " + msg + " all: " + msgQueue.size());
    }    
}
