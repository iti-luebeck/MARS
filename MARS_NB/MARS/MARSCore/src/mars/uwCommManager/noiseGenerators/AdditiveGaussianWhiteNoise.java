/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.noiseGenerators;

import java.util.Random;
import static mars.uwCommManager.noiseGenerators.NoiseNameConstants.*;

/**
 * @version 1.0
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

    @Override
    public byte[] noisifyByDistance(byte[] message, float distance) {
        return null;
    }
}
