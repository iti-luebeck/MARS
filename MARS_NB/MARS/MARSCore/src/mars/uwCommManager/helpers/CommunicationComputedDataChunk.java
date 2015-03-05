/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.helpers;

import java.io.UnsupportedEncodingException;
import org.openide.util.Exceptions;

/**
 * @version 0.1.2
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
    
    
    private final String IDENTIFIER;
    
    private final DistanceTrigger DISTANCE_TRIGGER;
    
    /**
     * @since 0.1
     * @param message the message  encoded as UTF-8
     * @param auvName the AUV that shall recieve this message
     */
    public CommunicationComputedDataChunk(final byte[] message, final String auvName, final DistanceTrigger distanceTigger, final String identifier) {
        this.MESSAGE = message;
        this.AUV_NAME = auvName;
        this.DISTANCE_TRIGGER = distanceTigger;
        this.IDENTIFIER = identifier;
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
     * @since 0.1.2
     * @return the identifier
     */
    public String getIdentifier() {
        return IDENTIFIER;
    }
}
