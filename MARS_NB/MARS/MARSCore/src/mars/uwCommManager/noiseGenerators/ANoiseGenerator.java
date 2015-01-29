/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.noiseGenerators;



/**
 * @version 0.2
 * @author Jasper Schwinghammer
 */
public abstract class ANoiseGenerator {
    
    private final String NOISE_NAME;
    
    /**
     * Later we will init all the stuff every noise Generator needs (like AUV List and other stuff)
     */
    public ANoiseGenerator(String noiseName) {
        this.NOISE_NAME = noiseName;
    }
    
    /**
     * 
     * apply the noise to the Message. We will need some further parameters later on like the destination of the message for some calculations
     * @since 0.1
     * @param msg
     * @return 
     */
    public abstract byte[] noisify(byte[] msg);
    
    /**
     * @since 0.1
     * @return Noise name
     */
    public String getName() {
        return NOISE_NAME;
    }
    
    /**
     * @since 0.2
     * @param message the message as byte array
     * @param noiseMask the noisemask
     * @return 
     */
    public byte[] xORNoises(byte[] message, byte[] noiseMask) {
        if(message.length != noiseMask.length) return null;
        byte[] result = new byte[message.length];
        
        for(int i = 0; i<result.length; i++) {
            result[i] = (byte) (noiseMask[i]^message[i]);
        }
        return result;
    }
}
