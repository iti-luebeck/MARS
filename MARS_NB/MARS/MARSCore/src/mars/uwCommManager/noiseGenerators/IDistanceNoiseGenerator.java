
package mars.uwCommManager.noiseGenerators;

/**
 * @version 0.1
 * @author Jasper Schwinghammer
 */
public interface IDistanceNoiseGenerator {
    
    
    /**
     * 
     * This function will noisify a message by the distance it traveled
     * @since 0.1
     * @param message the message encoded as UTF-8 byte array
     * @param distance the distance the message has traveled in meters
     * @param frequence the frequence of the signal in khz
     * @param signalStrength the signalStrength at the source of the signal in deciBel
     * @param waterDepth the depth of the water in meters
     * @return the noisified message
     */
    public byte[] noisifyByDistance(byte[] message, float distance, float frequence, float signalStrength, float waterDepth);
    
}
