
package mars.uwCommManager.noiseGenerators;

/**
 * @version 0.1
 * @author Jasper Schwinghammer
 */
public interface IDistanceNoiseGenerator {
    
    
    /**
     * This function will noisify a message by the distance it traveled
     * @param message the message encoded as UTF-8 byte array
     * @param distance the distance the message has traveled
     * @param the frequence of the signal
     * @param the signalStrength at the source of the signal
     * @return 
     */
    public byte[] noisifyByDistance(byte[] message, float distance, float frequence, float signalStrength, float waterDepth);
    
}
