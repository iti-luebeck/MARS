/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.threading;

import mars.uwCommManager.helpers.CommunicationComputedDataChunk;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.sensors.CommunicationDevice;
import mars.uwCommManager.CommunicationState;
import org.openide.util.Exceptions;



/**
 * Used to merge all message chunks that were delivered within one tick to the modem.
 * @author Jasper Schwinghammer
 * @version 0.1
 */
public class CommunicationMultiPathSimulator implements Runnable {
    
    
    private volatile Map<String,List<CommunicationComputedDataChunk>> chunks;
    private Map<String,String> messages;
    private volatile ConcurrentLinkedQueue<CommunicationComputedDataChunk> queue;
    private CommunicationState state = null;
    private AUV_Manager auvManager = null;
    
    public CommunicationMultiPathSimulator() {
        this.chunks = new HashMap();
        this.messages = new HashMap();
        queue = new ConcurrentLinkedQueue<CommunicationComputedDataChunk>();
        
        
    }
     /**
     * @since 0.1
     * Init all nontrivial stuff
     * @param auvManager the AUV_Manager
     * @param CommuncationState the CommunicationState
     * @return if all initialization worked 
     */
    public boolean init(final AUV_Manager auvManager, CommunicationState state) {
        if (auvManager == null || state == null) return false;
        this.auvManager = auvManager;
        this.state = state;
        return true;
    }
    
    /**
     * add a whole chunk of messages to be computed
     * @param msgs the messages that should be converted with the next tick
     */
    private void addNewMessages() {
        while(queue.peek() != null) {
            CommunicationComputedDataChunk e = queue.poll();

            if(chunks.containsKey(e.getAUVName())) {
                chunks.get(e.getAUVName()).add(e);
            } else {
                List<CommunicationComputedDataChunk> list = new LinkedList();
                list.add(e);
                chunks.put(e.getAUVName(),list);
            }
            if(e.getAUVName().equals("001000")) {
                System.out.println("Das war " + e.getMessageAsString());
                System.out.println("Chunks enthält: " + chunks.get("001000").toString());
                for(CommunicationComputedDataChunk i : chunks.get("001000")) {
                    System.out.println("Chunks are: " + i.getMessageAsString() +" "+ i);
                }
            }
            queue.remove(e);
        }
    }
    
    /**
     * Take all the messages for each AUV and compute them back into one String
     * @since 0.1
     */
    private void computeMessages() {
        for(Map.Entry<String,List<CommunicationComputedDataChunk>> e : chunks.entrySet()){
            String name = e.getKey();
            

            
            
            
            List<CommunicationComputedDataChunk> msgs = e.getValue();
            if(!msgs.isEmpty()) {
                byte[] byteArray = msgs.get(0).getMessage();
                System.out.println("Computing: " + msgs.get(0).getMessageAsString()+" Chunkname: "+ msgs.get(0)+ " with "+ msgs.get(0).getAUVName() );               
                msgs.remove(0);
                for(CommunicationComputedDataChunk chunk : msgs) {
                    byte[] nextArray = chunk.getMessage();
                    for(int i = 0; i<byteArray.length; i++) {
                        if( (nextArray.length>i))  byteArray[i] = (byte) (nextArray[i] | byteArray[i]);
                    }
                }
                try {
                    String message = new String(byteArray,"UTF-8");
                    messages.put(name, message);
                } catch (UnsupportedEncodingException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        chunks.clear();
    }
    
    /**
     * call publish on all modems that recieve a message
     * @since 0.1
     */
    private void returnMessages(){
        for(Map.Entry<String,String> e : messages.entrySet()) {
            String name = e.getKey();
            AUV auv = auvManager.getAUV(name);
            ArrayList uwmo = auv.getSensorsOfClass(CommunicationDevice.class.getName());
            Iterator it = uwmo.iterator();
            while(it.hasNext()){
                CommunicationDevice mod = (CommunicationDevice)it.next();
                mod.publish(e.getValue());
            }
                
        }
        messages.clear();
    }
    

    /**
     * three steps:
     * Sort the messages to the corresponding AUV
     * compute them and make all byte[] of one timeframe to one String
     * return them to the AUV modems
     * @since 0.1
     */
    @Override
    public void run() {
        try {
            addNewMessages();
            computeMessages();
            returnMessages();
        } catch (Exception e) {
            Exceptions.printStackTrace(e);
        }

    }
    
    
    
    
    /**
     * enqueue new Messages
     * @param msgs a list of computedDataChunks that should be computed and returned to the modems
     */
    public void enqueueMsges(final List<CommunicationComputedDataChunk> msgs) {
        queue.addAll(msgs);
        
    }


    
}
