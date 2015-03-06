/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.threading;

import com.sun.media.jfxmedia.logging.Logger;
import mars.uwCommManager.helpers.CommunicationComputedDataChunk;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.sensors.CommunicationDevice;
import mars.sensors.UnderwaterModem;
import mars.uwCommManager.CommunicationState;
import org.openide.util.Exceptions;



/**
 * Used to merge all message chunks that were delivered within one tick to the modem.
 * @author Jasper Schwinghammer
 * @version 0.1
 */
public class MultiMessageMerger implements Runnable {
    
    
    private volatile Map<String,List<CommunicationComputedDataChunk>> chunks;
    private Map<String,String> messages;
    private volatile ConcurrentLinkedQueue<CommunicationComputedDataChunk> queue;
    private CommunicationState state = null;
    private AUV_Manager auvManager = null;
    
    public MultiMessageMerger() {
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
            queue.remove(e);
        }
    }
    
    /**
     * Take all the messages for each AUV and compute them back into one String
     * @since 0.1
     */
    private void computeMessages() {
        
        //For each AUV
        for(Map.Entry<String,List<CommunicationComputedDataChunk>> e : chunks.entrySet()){
            String name = e.getKey();
            //Take all the mesages since the last tick
            List<CommunicationComputedDataChunk> msgs = e.getValue();
            TreeMap<Integer,CommunicationComputedDataChunk> sortedChunks = new TreeMap<Integer, CommunicationComputedDataChunk>();
            //Sort them by the time of arrival
            for(CommunicationComputedDataChunk i : msgs) sortedChunks.put(i.getStartTime()+i.getDistanceTrigger().getTraveTimel(), i);
            msgs.clear();
            CommunicationComputedDataChunk baseChunk = sortedChunks.firstEntry().getValue();
            if(baseChunk != null) {
                sortedChunks.remove(sortedChunks.firstEntry().getKey());
                
                int baseTime = baseChunk.getDistanceTrigger().getTraveTimel()+baseChunk.getStartTime();
                byte[] byteArray = baseChunk.getMessage();
                
                while(!sortedChunks.isEmpty()) {
                    Map.Entry<Integer,CommunicationComputedDataChunk> entry = sortedChunks.firstEntry();
                    boolean testResult = compareChunks(baseChunk.getIdentifier().split(";"),entry.getValue().getIdentifier().split(";"));
                    if((entry.getKey() > baseTime+50) || testResult ) {
                        returnMessage(name, byteArray);
                        baseChunk =entry.getValue();
                        baseTime = entry.getKey();
                        byteArray = baseChunk.getMessage();
                        sortedChunks.remove(sortedChunks.firstEntry().getKey());
                    } else if (entry.getKey()<baseTime+50) {
                        java.util.logging.Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "There is an error with the sorting of the messages");
                    } else {
                        byte[] nextArray = entry.getValue().getMessage();
                        for(int i = 0; i<byteArray.length; i++) {
                            if( (nextArray.length>i))  byteArray[i] = (byte) (nextArray[i] | byteArray[i]);
                        }    
                    }
                }
                returnMessage(name, byteArray);
            }
            
        }
        chunks.clear();
    }
    
    /**
     * returns if two identifiers prohibit that the messages are merged
     * true = do not merge
     * false = merge
     * @param identifierOne
     * @param identifierTwo
     * @return 
     */
    private boolean compareChunks(String[] identifierOne, String[] identifierTwo) {
        boolean[] results = new boolean[6];
        
        results[0] = identifierOne[0].equals(identifierTwo[0]);
        results[1] = identifierOne[1].equals(identifierTwo[1]);
        results[2] = identifierOne[2].equals(identifierTwo[2]);
        results[3] = identifierOne[3].equals(identifierTwo[3]);
        results[4] = identifierOne[4].equals(identifierTwo[4]);
        results[5] = identifierOne[5].equals(identifierTwo[5]);
        return (results[0] && (results[4] && results[5]));
    }
    
    
    private void returnMessage(String name, byte[] message) {
        try {
            AUV auv = auvManager.getAUV(name);
                ArrayList uwmo = auv.getSensorsOfClass(CommunicationDevice.class.getName());
                    Iterator it = uwmo.iterator();
                    while(it.hasNext()){
                        UnderwaterModem mod = (UnderwaterModem)it.next();
                        mod.addByteMessage(message);
                        }
                    String msg = new String(message,"UTF-8");
                    messages.put(name, msg);
                    } catch (UnsupportedEncodingException ex) {
                        Exceptions.printStackTrace(ex);
                    }
    }
    
//    /**
//     * call publish on all modems that recieve a message
//     * @since 0.1
//     */
//    private void returnMessages(){
//        for(Map.Entry<String,String> e : messages.entrySet()) {
//            System.out.println("returning message");
//            String name = e.getKey();
//            AUV auv = auvManager.getAUV(name);
//            ArrayList uwmo = auv.getSensorsOfClass(CommunicationDevice.class.getName());
//            Iterator it = uwmo.iterator();
//            while(it.hasNext()){
//                CommunicationDevice mod = (CommunicationDevice)it.next();
//                mod.publish(e.getValue());
//            }
//                
//        }
//        messages.clear();
//    }
    

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
//            returnMessages();
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
