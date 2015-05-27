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
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import mars.auv.AUV;
import mars.auv.AUV_Manager;
import mars.sensors.CommunicationDevice;
import mars.sensors.UnderwaterModem;
import mars.uwCommManager.CommunicationState;
import mars.uwCommManager.helpers.DataChunkIdentifier;
import org.openide.util.Exceptions;

/**
 * Used to merge all message chunks that were delivered within one tick to the
 * modem.
 *
 * @author Jasper Schwinghammer
 * @version 0.1
 */
public class MultiMessageMerger implements Runnable {

    private volatile Map<String, List<CommunicationComputedDataChunk>> chunks;
    //private Map<String,String> messages;
    private volatile ConcurrentLinkedQueue<CommunicationComputedDataChunk> queue;
    private CommunicationState state = null;
    private AUV_Manager auvManager = null;

    public MultiMessageMerger() {
        this.chunks = new HashMap();
        //this.messages = new HashMap();
        queue = new ConcurrentLinkedQueue<CommunicationComputedDataChunk>();
    }

    /**
     * @since 0.1 Init all nontrivial stuff
     * @param auvManager the AUV_Manager
     * @param CommuncationState the CommunicationState
     * @return if all initialization worked
     */
    public boolean init(final AUV_Manager auvManager, CommunicationState state) {
        if (auvManager == null || state == null) {
            return false;
        }
        this.auvManager = auvManager;
        this.state = state;
        return true;
    }

    /**
     * add a whole chunk of messages to be computed
     *
     * @param msgs the messages that should be converted with the next tick
     */
    private void addNewMessages() {
        while (queue.peek() != null) {
            CommunicationComputedDataChunk e = queue.poll();
            if (chunks.containsKey(e.getAUVName())) {
                chunks.get(e.getAUVName()).add(e);
            } else {
                List<CommunicationComputedDataChunk> list = new LinkedList();
                list.add(e);
                chunks.put(e.getAUVName(), list);
            }
            queue.remove(e);
        }

    }

    /**
     * Take all the messages for each AUV and compute them back into one String
     *
     * @since 0.1
     */
    private void computeMessages() {
        for (Map.Entry<String, List<CommunicationComputedDataChunk>> e : chunks.entrySet()) {
        }
        //For each AUV
        for (Map.Entry<String, List<CommunicationComputedDataChunk>> e : chunks.entrySet()) {
            String name = e.getKey();
            //Take all the mesages since the last tick
            List<CommunicationComputedDataChunk> msgs = e.getValue();
            Map<Float, TreeMap<Long, List<CommunicationComputedDataChunk>>> frequencyMap = new HashMap();

            //Sort them by the time of arrival
            for (CommunicationComputedDataChunk i : msgs) {
                if (!frequencyMap.containsKey(i.getFrequency())) {
                    frequencyMap.put(i.getFrequency(), new TreeMap<Long, List<CommunicationComputedDataChunk>>());
                }
                TreeMap<Long, List<CommunicationComputedDataChunk>> sortedChunks = frequencyMap.get(i.getFrequency());

                if (sortedChunks.get(i.getStartTime() + i.getDistanceTrigger().getTraveTimel()) == null) {
                    sortedChunks.put(i.getStartTime() + i.getDistanceTrigger().getTraveTimel(), new LinkedList<CommunicationComputedDataChunk>());
                }
                sortedChunks.get(i.getStartTime() + i.getDistanceTrigger().getTraveTimel()).add(i);

            }
            msgs.clear();
            //Set the base chunk
            for (Map.Entry<Float, TreeMap<Long, List<CommunicationComputedDataChunk>>> frequencyEntry : frequencyMap.entrySet()) {
                TreeMap<Long, List<CommunicationComputedDataChunk>> sortedChunks = frequencyEntry.getValue();

                CommunicationComputedDataChunk baseChunk = sortedChunks.firstEntry().getValue().get(0);
                sortedChunks.firstEntry().getValue().remove(0);
                if (baseChunk != null) {
                    //get parameters from base chunk
                    long baseTime = baseChunk.getDistanceTrigger().getTraveTimel() + baseChunk.getStartTime();
                    byte[] byteArray = baseChunk.getMessage();

                    while (!sortedChunks.isEmpty()) {
                        //get the next entry
                        Map.Entry<Long, List<CommunicationComputedDataChunk>> entry = sortedChunks.firstEntry();
                        sortedChunks.remove(entry.getKey());

                        for (CommunicationComputedDataChunk chunk : entry.getValue()) {
                            //test if the chunk is from the same auv and came on the same way - these should not be merged into one message
                            boolean testResult = compareChunks(baseChunk.getIdentifier(), chunk.getIdentifier());
                            //if there are no more messages in this interval
                            if ((entry.getKey() > baseTime + 50) || testResult) {
                                //return the message to the AUV and set the base chunk to the next chunk
                                returnMessage(name, byteArray);
                                baseChunk = chunk;
                                baseTime = entry.getKey();
                                byteArray = baseChunk.getMessage();

                            } else if (entry.getKey() < baseTime) {
                                java.util.logging.Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "There is an error with the sorting of the messages");
                                //merge two chunks
                            } else {
                                byte[] nextArray = chunk.getMessage();
                                for (int i = 0; i < byteArray.length; i++) {
                                    if ((nextArray.length > i)) {
                                        byteArray[i] = (byte) (nextArray[i] | byteArray[i]);
                                    }
                                }
                            }
                        }
                    }
                    returnMessage(name, byteArray);
                }
            }
        }
        chunks.clear();
    }

    /**
     * returns if two identifiers prohibit that the messages are merged true =
     * do not merge false = merge
     *
     * @param identifierOne
     * @param identifierTwo
     * @return
     */
    private boolean compareChunks(DataChunkIdentifier identifierOne, DataChunkIdentifier identifierTwo) {
        boolean[] results = new boolean[6];
        //Is the message sent by the same AUV
        results[0] = identifierOne.getAUV_Name().equals(identifierTwo.getAUV_Name());
        //is the start time the same
        results[1] = identifierOne.getStartTime() == identifierTwo.getStartTime();
        //Are the chunks part of the same message
        results[2] = identifierOne.getMessageIdentifier().equals(identifierTwo.getMessageIdentifier());
        //Are the chunks the same part of a message
        results[3] = identifierOne.getChunkNumber() == identifierTwo.getChunkNumber();
        //Have the chunks bounced the same times from the ocean floor
        results[4] = identifierOne.getFloorBounces() == identifierTwo.getFloorBounces();
        //Have the chunks bounced the same times from the ocean surface
        results[5] = identifierOne.getSurfaceBounces() == identifierTwo.getSurfaceBounces();

        //return if the messages have the same source and came on the same way - should not be merged
        return (results[0] && (results[4] && results[5]));
    }

    private void returnMessage(String name, byte[] message) {
        AUV auv = auvManager.getAUV(name);
        ArrayList uwmo = auv.getSensorsOfClass(CommunicationDevice.class.getName());
        Iterator it = uwmo.iterator();
        while (it.hasNext()) {
            UnderwaterModem mod = (UnderwaterModem) it.next();
            mod.addByteMessage(message);
        }
                    //String msg = new String(message,"UTF-8");
        //messages.put(name, msg);

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
     * three steps: Sort the messages to the corresponding AUV compute them and
     * make all byte[] of one timeframe to one String return them to the AUV
     * modems
     *
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
     *
     * @param msgs a list of computedDataChunks that should be computed and
     * returned to the modems
     */
    public void enqueueMsges(final List<CommunicationComputedDataChunk> msgs) {
        queue.addAll(msgs);

    }

}
