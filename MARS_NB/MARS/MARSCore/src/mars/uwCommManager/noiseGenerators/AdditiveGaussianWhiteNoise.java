/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.noiseGenerators;

import java.util.Random;
import mars.core.CentralLookup;
import mars.states.SimState;
import mars.uwCommManager.helpers.AmbientNoiseHelper;
import mars.uwCommManager.helpers.AttenuationHelper;
import static mars.uwCommManager.noiseGenerators.NoiseNameConstants.*;

/**
 * This class is a basic implementation of underwater noise
 * @version 1.1
 * @author Jasper Schwinghammer
 */
public class AdditiveGaussianWhiteNoise extends ANoiseByDistanceGenerator{
    /**
     * @since 0.5
     */
    private Random random = null;
    /**
     * @since 0.5
     */
    private float standardDeviation;
    
    /**
     * Init without a seed, seed is generated random by java.util.Random standards
     * @since 0.5 
     * @param standardDeviation the deviation should be less then 0.5 to be usefull
     */
    public AdditiveGaussianWhiteNoise(float standardDeviation) {
        super(GAUSSIAN_WHITE_NOISE);
        random = new Random();
        this.standardDeviation = standardDeviation;
    }
    
    /**
     * Init with a seed, helpfull for debugging purpose
     * @since 0.6
     * @param seed the seed for the java.util.Random
     * @param standardDeviation the deviation should be less then 0.5 to be usefull
     */
    public AdditiveGaussianWhiteNoise(long seed, float standardDeviation) {
        super(GAUSSIAN_WHITE_NOISE);
        random = new Random(seed);
        this.standardDeviation = standardDeviation;
    }

    @Override
    public byte[] noisify(byte[] msg) {
        byte[] res = new byte[msg.length];
        for(int i = 0; i<msg.length;i++) {
            int result = 0;
            for(int j = 1; j<=8; j++) {
                float rand = Math.abs(((float) random.nextGaussian() * standardDeviation));
                if(rand>= 1) {
                    result += (1 << j-1);
                }
            }
            res[i] = (byte)result;
            //System.out.println("Here we go: " + result + " <- result of noise " + msg[i] + " <- current byte" + res + "done");
        }
        return xORNoises(msg,res);
    }

    /**
     * Calculates the SNR of a message and determines reasonable parameters for the noise function.
     * @param message the message as byte array
     * @param distance the distance it has traveled in meters
     * @param frequence the frequence in khz
     * @param signalStrength the signal strength at start in dB
     * @param waterDepth the water depth in meters
     * @return the noisified message
     */
    @Override
    public byte[] noisifyByDistance(byte[] message, float distance, float frequence, float signalStrength, float waterDepth) {
        AttenuationHelper attHelper = new AttenuationHelper(((SimState)CentralLookup.getDefault().lookup(SimState.class)).getMARSSettings().getPhysical_environment());
        float attenuation = attHelper.carculateAttenuationInDB(distance, frequence, AttenuationHelper.SPHERICAL_SPREADING, waterDepth);
        float ambientNoise = AmbientNoiseHelper.calculateAmbientNoise(frequence, 1, 3);
        float currentSignalStrength =  (float) (10f * Math.log10( Math.pow(10,signalStrength/10) - Math.pow(10,attenuation/10)) );
       //System.out.println("Stärke: "+Math.pow(10,signalStrength/10)+ " schwäche: "+ Math.pow(10,attenuation/10) + " diff: " + (Math.pow(10,signalStrength/10) - Math.pow(10,attenuation/10)));
        float SNR = (float) (10f *( Math.log10(Math.pow(10,currentSignalStrength/10) - Math.pow(10,ambientNoise/10))));
       //System.out.println("Signal Strength: " + signalStrength + " Attenunation: " +attenuation + " Ambient noise: "+ ambientNoise +" currentSignalStrength "+ currentSignalStrength +" SNR: " + SNR);
        standardDeviation = (float) (4f - Math.log10(SNR));
        System.out.println("New standardDeviation: " + standardDeviation);
        return noisify(message);
        
    }
    
    
}
