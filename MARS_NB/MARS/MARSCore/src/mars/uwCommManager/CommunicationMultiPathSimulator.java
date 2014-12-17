/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager;

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
import org.openide.util.Exceptions;



/**
 *
 * @author Jasper Schwinghammer
 */
public class CommunicationMultiPathSimulator implements Runnable {
    
    
    private Map<String,List<CommunicationComputedDataChunk>> chunks;
    private Map<String,String> messages;
    private ConcurrentLinkedQueue<CommunicationComputedDataChunk> queue;
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
    
    private void addNewMessages(final ConcurrentLinkedQueue<CommunicationComputedDataChunk> msgs) {
        while(queue.peek() != null) {
            CommunicationComputedDataChunk e = queue.poll();
            if(chunks.containsKey(e.getAUVName())) {
                chunks.get(e.getAUVName()).add(e);
            } else {
                List<CommunicationComputedDataChunk> list = new LinkedList();
                list.add(e);
                chunks.put(e.getAUVName(),list);
            }
            msgs.remove(e);
        }
    }
    
    
    private void computeMessages() {
            for(Map.Entry<String,List<CommunicationComputedDataChunk>> e : chunks.entrySet()){
                String name = e.getKey();
                List<CommunicationComputedDataChunk> msgs = e.getValue();
                byte[] bytearray = msgs.get(0).getMessage();
                msgs.remove(0);
                for(CommunicationComputedDataChunk chunk : msgs) {
                    byte[] nextArray = chunk.getMessage();
                    for(int i = 0; i<bytearray.length; i++) {
                        bytearray[i] = (byte) (nextArray[i] | bytearray[i]);
                    }
                }
                try {
                    String message = new String(bytearray,"UTF-8");
                    messages.put(name, message);
                } catch (UnsupportedEncodingException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
    }
    
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
    }
    


    @Override
    public void run() {
        addNewMessages(queue);
        computeMessages();
        returnMessages();
    }
    
    
    
    
    
    public void enqueueMsges(final List<CommunicationComputedDataChunk> msgs) {
        queue.addAll(msgs);
        
    }


    
}
