/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.helpers;

import java.io.UnsupportedEncodingException;
import org.openide.util.Exceptions;

/**
 * @version 0.1.4
 * @author Jasper Schwinghammer
 */
public class CommunicationComputedDataChunk {
    
    /**
     * the computed message encoded as UTF-8
     */
    private final byte[] MESSAGE;
    /**
     * The Name of the AUV that shall recieve this message
     */
    private final String AUV_NAME;
    
    private final float FREQUENCY;
    
    private final DataChunkIdentifier IDENTIFIER;
    
    private final DistanceTrigger DISTANCE_TRIGGER;
    
    private final long START_TIME;
    
    /**
     * @since 0.1
     * @param message the message  encoded as UTF-8
     * @param auvName the AUV that shall recieve this message
     */
    public CommunicationComputedDataChunk(final byte[] message, final String auvName, final DistanceTrigger distanceTigger, final DataChunkIdentifier identifier, final long startTime, final float frequence) {
        this.MESSAGE = message;
        this.AUV_NAME = auvName;
        this.DISTANCE_TRIGGER = distanceTigger;
        this.IDENTIFIER = identifier;
        this.START_TIME = startTime;
        this.FREQUENCY = frequence;
    }
    
    /**
     * get the message encoded as UTF-8
     * @since 0.1
     * @return the message encoded as UTF-8
     */
    public byte[] getMessage() {
        return MESSAGE.clone();
    }
    
    /**
     * get the AUV that shall recieve the message
     * @since 0.1
     * @return the AUVs name
     */
    public String getAUVName() {
        return AUV_NAME;
    }
    
    /**
     * @since 0.1.1
     * @return the message as String (debug only method)
     */
    public String getMessageAsString() {
        try {
            return new String(MESSAGE,"UTF-8");
        } catch (UnsupportedEncodingException ex) {
           Exceptions.printStackTrace(ex);
        }
        return null;
    }
    
    /**
     * Get the identifier consisting of: 
     * The Source AUV;
     * the system time at start; 
     * the Identifier of the Array; 
     * the array position;
     * the number of ocean-floor bounces;
     * the number of ocean-surface bounces
     * @since 0.1.2
     * @return the identifier
     */
    public DataChunkIdentifier getIdentifier() {
        return IDENTIFIER;
    }
    
    /**
     * Return the distancetrigger for this message
     * @since 0.1.2
     * @return 
     */
    public DistanceTrigger getDistanceTrigger() {
        return DISTANCE_TRIGGER;
    }
    
    /**
     * @since 0.1.3
     * @return the start time in millisecounds
     */
    public long getStartTime() {
        return START_TIME;
    }
    
    public float getFrequency() {
        return FREQUENCY;
    }
}
