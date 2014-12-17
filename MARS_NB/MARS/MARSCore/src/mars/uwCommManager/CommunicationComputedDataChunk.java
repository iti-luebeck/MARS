/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager;

/**
 *
 * @author Jasper Schwinghammer
 */
public class CommunicationComputedDataChunk {
    
    
    private final byte[] MESSAGE;
    private final String AUV_NAME;
    
    public CommunicationComputedDataChunk(final byte[] message, final String auvName) {
        this.MESSAGE = message;
        this.AUV_NAME = auvName;
    }
    
    public byte[] getMessage() {
        return MESSAGE;
    }
    
    public String getAUVName() {
        return AUV_NAME;
    }
}
