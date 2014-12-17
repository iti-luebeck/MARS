/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import mars.sensors.CommunicationMessage;
import org.openide.util.Exceptions;

/**
 *
 * @author jaspe_000
 */
public class CommunicationExecutorRunnable implements Runnable{
    
    private final float MODEM_BANDWIDTH;
    private final float RESOLUTION;
    private final float BANDWIDTH_PER_TICK;
    
    private LinkedList<CommunicationDataChunk> waitingChunks;
    
    private LinkedList<CommunicationDataChunk> sentChunks;
    
    
    
    private ConcurrentLinkedQueue<CommunicationMessage> newMessages = null;
    private ConcurrentLinkedQueue<CommunicationComputedDataChunk> computedMessages = null;
    
    public CommunicationExecutorRunnable(float modem_bandwidth, int resolution) {
        MODEM_BANDWIDTH = modem_bandwidth;
        RESOLUTION = resolution;
        BANDWIDTH_PER_TICK = MODEM_BANDWIDTH/RESOLUTION;
        newMessages = new ConcurrentLinkedQueue<CommunicationMessage>();
        computedMessages = new ConcurrentLinkedQueue<CommunicationComputedDataChunk>();
        
        waitingChunks = new LinkedList();
        sentChunks = new LinkedList();
    }
    
    


    @Override
    public void run() {
        
        computeAllNewMessages();
        
        if(!waitingChunks.isEmpty()) sentChunks.add(waitingChunks.poll());
        
        computeSentChunks();

    }
    
    
    private void computeSentChunks() {
        
        float distanceSinceLastTick = 10f;
        
        for(CommunicationDataChunk chunk : sentChunks) {
            chunk.addDistance(distanceSinceLastTick);
            while(chunk.hasNextTrigger()) {
                CommunicationComputedDataChunk cChunk = chunk.evalNextTrigger();
                computedMessages.add(cChunk);
            }
        }
    }
   

    private void computeAllNewMessages() {
        while(newMessages.peek() != null) {
            CommunicationMessage msg = newMessages.poll();
            try {
                byte[] msgByte = msg.getMsg().getBytes("UTF-8");
                int chunkCount = (int) Math.ceil(((double)msgByte.length) / BANDWIDTH_PER_TICK);
                for(int i = 0; i<chunkCount; i++) {
                    CommunicationDataChunk chunk = null;
                    if(i != chunkCount - 1) {
                        chunk = new CommunicationDataChunk(
                                Arrays.copyOfRange(msgByte, (int) (i*BANDWIDTH_PER_TICK), (int)((i+1)*BANDWIDTH_PER_TICK)-1),
                                null, RESOLUTION);
                    } else {
                        chunk = new CommunicationDataChunk(
                                Arrays.copyOfRange(msgByte, (int) (i*BANDWIDTH_PER_TICK), msgByte.length),
                                null, RESOLUTION);
                    }
                    //to emphasize that we will use the list as queue I use the queue methods instead of LinkedList.add
                    waitingChunks.offer(chunk);
                }
            } catch (UnsupportedEncodingException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
    
    
    public void assignMessage(CommunicationMessage msg) {
        newMessages.add(msg);
    }

    
}
