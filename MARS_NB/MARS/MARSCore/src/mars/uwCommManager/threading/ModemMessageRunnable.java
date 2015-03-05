/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.threading;

import mars.uwCommManager.helpers.CommunicationComputedDataChunk;
import mars.uwCommManager.helpers.CommunicationDataChunk;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import mars.sensors.CommunicationMessage;
import mars.uwCommManager.helpers.DistanceTrigger;
import mars.uwCommManager.noiseGenerators.ANoiseByDistanceGenerator;
import mars.uwCommManager.noiseGenerators.ANoiseGenerator;
import org.openide.util.Exceptions;

/**
 * The new runnable is to be used with the Executer class from java.util.concurrent
 * There should be one instance of this for each AUV with a modem
 * @version 0.2
 * @author Jasper Schwinghammer
 */
public class ModemMessageRunnable implements Runnable{
    
    /*
     * DEBUG VALUES
     */
    public static final float MODEM_SIGNAL_STRENGTH = 80;
    public static final float MODEM_REACH = 100;
    public static final float MODEM_FREQUENCE = 1;
    
    /**
     * The bandwidth of the modem this AUV is using
     */
    private final float MODEM_BANDWIDTH;
    /**
     * the ticks per secound
     */
    private final float RESOLUTION;
    /**
     * the bandwidth we have in each of our timeframes
     */
    private final float BANDWIDTH_PER_TICK;
    
    /**
     * the AUV this runnable represents;
     */
    private final String AUV_NAME;
    
    /**
     * chunks that could not yet be sent due to bandwidthlimitation
     */
    private LinkedList<CommunicationDataChunk> waitingChunks;
    
    /**
     * chunks that are processed at the moment
     */
    private LinkedList<CommunicationDataChunk> sentChunks;
    
    
    /**
     * interface to the CommuncationState
     */
    private ConcurrentLinkedQueue<CommunicationMessage> newMessages = null;
    /**
     * interface to the MultipathPropagationModule
     */
    private volatile List<CommunicationComputedDataChunk> computedMessages = null;
    
    /**
     * The distanceTriggers that are added to all CommuncationMessages that are moved to computeMessages
     */
    private volatile List<DistanceTrigger> distanceTriggers = null;
    
    
    private volatile List<ANoiseByDistanceGenerator> noiseGenerators = null;
    
    
    /**
     * Construct a new CommuncationExecutorRunnable for a AUV
     * @since 0.1
     * @param modem_bandwidth the maximum bandwidth the modem has in kilobyte per secound
     * @param resolution the ticks per secound
     */
    public ModemMessageRunnable(float modem_bandwidth, int resolution, String auvName) {
        this.AUV_NAME = auvName;
        MODEM_BANDWIDTH = modem_bandwidth;
        RESOLUTION = resolution;
        BANDWIDTH_PER_TICK = MODEM_BANDWIDTH/RESOLUTION;
        newMessages = new ConcurrentLinkedQueue<CommunicationMessage>();
        computedMessages = new LinkedList<CommunicationComputedDataChunk>();
        
        waitingChunks = new LinkedList();
        sentChunks = new LinkedList();
        distanceTriggers = new LinkedList();
        noiseGenerators = new LinkedList();
    }
    
    

    /**
     * three steps:
     * transform all new Communcation messages to CommunicationDataChunks
     * take the next pending DataChunk and add it to the sent chunks
     * make a tick with all sent chunks
     * @since 0.1
     */
    @Override
    public void run() {
        try {
            //devide all new messages into chunks that do not exceed the max bandwidth
            computeAllNewMessages();
            //send one chunk
            if(!waitingChunks.isEmpty()) {
                CommunicationDataChunk chunk = waitingChunks.poll(); 
                if(!distanceTriggers.isEmpty()) {
                    chunk.addDistanceTriggers(distanceTriggers);
                    sentChunks.add(chunk);
                }
            }
            //compute all chunks that are send
            computeSentChunks();
            
            //Graphics stuff
            
            
        } catch (Exception e ) {
            Exceptions.printStackTrace(e);
        }

    }
    
    /**
     * Make a step with all sent chunks kill all chunks that have reached maximum range
     * @since 0.1
     * 
     */
    private void computeSentChunks() {
        
        try {
            //DISTANCE PER TICK DUMMY, SHOULD BE REPLACED WITH PROPER SPEED OF SOUND
            float distanceSinceLastTick = 1f;
            List<CommunicationDataChunk> deadChunks = new LinkedList();
            
        
            for(CommunicationDataChunk chunk : sentChunks) {
                chunk.addDistance(distanceSinceLastTick);
//                synchronized(this) {
//                    for (ANoiseGenerator noise : noiseGenerators) {
//                        chunk.updateMessageFromByte(noise.noisify(chunk.getMessageAsByte()));
//                    }
//                }

                while(chunk.hasNextTrigger()) {
                    CommunicationComputedDataChunk cChunk = chunk.evalNextTrigger(noiseGenerators);
                    synchronized(this) {
                        computedMessages.add(cChunk);
                    }
                }
                if(chunk.isDead()) deadChunks.add(chunk);
            }
            sentChunks.removeAll(deadChunks);
        } catch(Exception e) {
            Exceptions.printStackTrace(e);
        }
        

    }
   
    /**
     * 
     * take all new enqueued CommunicationMessages and converts them into CommuncationDataChunks
     * @since 0.1
     */
    private void computeAllNewMessages() {
        while(newMessages.peek() != null) {
            CommunicationMessage msg = newMessages.poll();
            try {
                byte[] msgByte = msg.getMsg().getBytes("UTF-8");
                int chunkCount = (int) Math.ceil(((double)msgByte.length) / (BANDWIDTH_PER_TICK*1000));
                String identifier = AUV_NAME + ";"+ System.currentTimeMillis() +";"+ msgByte.toString();
                //System.out.println("Deviding message into Chunks: "+ chunkCount + " Message length was: " +msgByte.length);
                for(int i = 0; i<chunkCount; i++) {
                    CommunicationDataChunk chunk = null;
                    if(i != chunkCount - 1) {
                        chunk = new CommunicationDataChunk(
                                Arrays.copyOfRange(msgByte, (int) (i*(BANDWIDTH_PER_TICK*1000)), (int)((i+1)*(BANDWIDTH_PER_TICK*1000))),
                                new PriorityQueue<DistanceTrigger>() , MODEM_REACH, MODEM_SIGNAL_STRENGTH, MODEM_FREQUENCE,identifier + ";" + i);
                    } else {
                        chunk = new CommunicationDataChunk(
                                Arrays.copyOfRange(msgByte, (int) (i*(BANDWIDTH_PER_TICK*1000)), msgByte.length),
                                new PriorityQueue<DistanceTrigger>(), MODEM_REACH, MODEM_SIGNAL_STRENGTH, MODEM_FREQUENCE,identifier + ";" + i);
                    }
                    //to emphasize that we will use the list as queue I use the queue methods instead of LinkedList.add
                    waitingChunks.offer(chunk);
                    
                }
            } catch (UnsupportedEncodingException ex) {
                Exceptions.printStackTrace(ex);
            } catch (Exception e) {
                Exceptions.printStackTrace(e);
            }
        }
    }
    
    /**
     * add a message to the queue
     * @since 0.1
     * @param msg 
     */
    public void assignMessage(CommunicationMessage msg) {
        newMessages.add(msg);
    }
    
    /**
     * get all computed messages since last call
     * @since 0.1
     * @return the computed messages
     */
    public synchronized List<CommunicationComputedDataChunk> getComputedMessages() {
        List<CommunicationComputedDataChunk> returnList = new LinkedList(computedMessages);
        computedMessages.clear();
        return returnList;
    }
    
    /**
     * setDistanceTriggers for the next messages that are converted
     * @since 0.1
     * @param triggers 
     */
    public void setDistanceTriggers(List<DistanceTrigger> triggers) {
        this.distanceTriggers = triggers;
    }
    
    /**
     * @since 0.2
     * @param noiseGen 
     */
    public synchronized void addANoiseByDistanceGenerator(ANoiseByDistanceGenerator noiseGen) {
        noiseGenerators.add(noiseGen);
    }
    
    /**
     * @since 0.2
     * @param noiseGen
     */
    public synchronized void removeANoiseGenerator(ANoiseGenerator noiseGen) {
        noiseGenerators.remove(noiseGen);
    }
    
    /**
     * Search for a NoiseGenerator by its name and remove it from the processing
     * @since 0.2
     * @param name the name of the noiseGen that should be removed
    */
    public synchronized void removeANoiseGeneratorByName(String name) {
        ANoiseGenerator toBeRemoved = null;
        
        for(ANoiseGenerator i : noiseGenerators) {
            if (i.getName().equals(name)) {
                toBeRemoved = i;
                break;
            }
        }
        if(toBeRemoved != null) noiseGenerators.remove(toBeRemoved);
    }
    
}
