/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.noiseGenerators;

import mars.sensors.CommunicationMessage;

/**
 * @version 0.1
 * @author Jasper Schwinghammer
 */
public abstract class ANoiseGenerator {
    
    /**
     * Later we will init all the stuff every noise Generator needs (like AUV List and other stuff)
     */
    public ANoiseGenerator() {
        
    }
    
    /**
     * 
     * apply the noise to the Message. We will need some further parameters later on like the destination of the message for some calculations
     * @since 0.1
     * @param msg
     * @return 
     */
    public abstract byte[] noisify(byte[] msg);
}
