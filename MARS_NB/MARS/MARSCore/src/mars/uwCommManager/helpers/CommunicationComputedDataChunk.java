/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.helpers;

/**
 * @version 0.1
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
    
    /**
     * @since 0.1
     * @param message the message  encoded as UTF-8
     * @param auvName the AUV that shall recieve this message
     */
    public CommunicationComputedDataChunk(final byte[] message, final String auvName) {
        this.MESSAGE = message;
        this.AUV_NAME = auvName;
    }
    
    /**
     * get the message encoded as UTF-8
     * @since 0.1
     * @return the message encoded as UTF-8
     */
    public byte[] getMessage() {
        return MESSAGE;
    }
    
    /**
     * get the AUV that shall recieve the message
     * @return the AUVs name
     */
    public String getAUVName() {
        return AUV_NAME;
    }
}
