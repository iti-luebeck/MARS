/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.helpers;


import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import mars.uwCommManager.noiseGenerators.ANoiseByDistanceGenerator;
import org.openide.util.Exceptions;

/**
 * This class contains a chunk of a message that was send by a modem.
 * While traveling the message will be altered by noise and other sources
 * @version 0.2.1
 * @author Jasper Schwinghammer
 */
public class CommunicationDataChunk {
    
    
    private final String IDENTIFIER;
    
    private float frequence;
    
    private float signalStrength;
    
    private byte[] messageDataChunk;
    private float distanceTraveled;
    private PriorityQueue<DistanceTrigger> triggerDistances;
    
    
    private final float MAX_DISTANCE;
    private boolean dead = false;
    
    /**
     * Create a new CommunicationDataChunk that will live as long as the distance traveled does not exceed the maximum propagation distance of the modem
     * @since 0.1
     * @param messageDataChunk The chunk of data
     * @param triggerDistances The distances of all the paths between our AUVs
     * @param maxDistance the maximum distance of the modem
     * @param signalStrength The initial strength of the soundsignal
     * @param frequence The frequence of the message
     */
    public CommunicationDataChunk(byte[] messageDataChunk, PriorityQueue<DistanceTrigger> triggerDistances, float maxDistance, float signalStrength, float frequence,String identifier) {
        this.IDENTIFIER = identifier;
        this.MAX_DISTANCE = maxDistance;
        this.messageDataChunk = messageDataChunk;
        this.distanceTraveled = 0f;
        this.triggerDistances = triggerDistances;
        this.signalStrength = signalStrength;
        this.frequence = frequence;
        if(this.triggerDistances == null) this.triggerDistances = new PriorityQueue<DistanceTrigger>();
        //System.out.println("Data: " + Arrays.toString(messageDataChunk) );
    }
    
    /**
     * Check the current head of the triggerqueue if it is within the travled distance.
     * @since 0.1
     * @return if there is a trigger within traveled distance
     */
    public boolean hasNextTrigger() {
        if(triggerDistances.peek() == null) {
            dead = true;
            return false;
        }
        return triggerDistances.peek().getDistance()<distanceTraveled;
    }
    
    /**
     * if there is a trigger within distance return it. Otherwise return null
     * hasNextTrigger should always be used first to reduce the chance of a nullpointer exception
     * @since 0.1
     * @return the next trigger within distance, or null if there is none
     */
    public CommunicationComputedDataChunk evalNextTrigger(final List<ANoiseByDistanceGenerator> noiseGenerators) {
        if(!hasNextTrigger()) return null;
        DistanceTrigger trigger = triggerDistances.poll();
        byte[] messageTemp = messageDataChunk.clone();
        for(ANoiseByDistanceGenerator gen: noiseGenerators) {
           messageTemp = gen.noisifyByDistance(messageTemp,trigger.getDistance(),frequence,signalStrength,0.05f);
        }
        CommunicationComputedDataChunk returnValue = new CommunicationComputedDataChunk(messageTemp, trigger.getAUVName(),trigger,IDENTIFIER+";"+trigger.getFloorBounces()+";"+trigger.getSurfaceBounces());
        if(triggerDistances.isEmpty()) dead = true;
        return returnValue;
    }
    
    
    
    
    
    /**
     * Should be called each tick, adds the traveled distance and checks if the maximum range is exceeded
     * @since 0.1
     * @param distance the distance since last tick
     */
    public synchronized void addDistance(float distance) {
        distanceTraveled +=distance;
        if(distanceTraveled > MAX_DISTANCE) dead = true;
    }
    
    /**
     * Add new triggers to the trigger Distances
     * @since 0.1
     * @param triggers the triggers that should be added
     */
    public synchronized void addDistanceTriggers(List<DistanceTrigger> triggers) {
        triggerDistances.clear();
        triggerDistances.addAll(triggers);
    }
    
    /**
     * If we find a new path between two AUV's that our already sent message should care about we can add it.
     * Only distances are longer then the already traveled distance will be added.
     * @since 0.1
     * @param triggerDistance the distance to the AUV
     */
    public synchronized void addtriggerDistance(float triggerDistance, String AUV) {
        if(triggerDistance>=distanceTraveled) {
            triggerDistances.add(new DistanceTrigger(triggerDistance, AUV));
        }
    }
    
    /**
     * Get the traveled distance
     * @since 0.1
     * @return the traveled distance
     */
    public synchronized float getDistanceTravled() {
        return distanceTraveled;
    }
    
    /**
     * Check if the message already exceeded its lifetime. Should be used to remove it from the processingQueue.
     * @since 0.1
     * @return if the message already exceeded its lifetime
     */
    public synchronized boolean isDead(){
        return dead;
    }
    
    /**
     * @since 0.1.1
     * @return the message as String (debug only method)
     */
    public String getMessageAsString() {
        try {
            return new String(messageDataChunk,"UTF-8");
        } catch (UnsupportedEncodingException ex) {
           Exceptions.printStackTrace(ex);
        }
        return null;
    }
    
    
    /**
     * get the message as UTF-8 Byte array
     * @since 0.2.0
     * @return 
     */
    public byte[] getMessageAsByte() {
        return messageDataChunk;
    }
    
    /**
     * Set the message to a new value
     * @since 0.1.0
     * @param msg 
     */
    public void updateMessageFromByte(byte[] msg) {
        messageDataChunk = msg;
    }
    
    /**
     * @since 0.2.1
     * @return the frequence of the message
     */
    public float getFrequence() {
        return frequence;
    }
    
    /**
     * @since 0.2.1
     * @return  the strength of the signal in dB
     */
    public float getSignalStrength() { 
        return signalStrength;
    }
    
    /**
     * get the identifier consisting of all information about the chunk
     * @since 0.2.1
     * @return the Identifier
     */
    public String getIdentifier() {
        return IDENTIFIER;
    }
}
