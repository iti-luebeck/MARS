/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.noiseGenerators;

import java.util.Random;
import static mars.uwCommManager.noiseGenerators.NoiseNameConstants.*;

/**
 *
 * @author jaspe_000
 */
public class AdditiveGaussianWhiteNoise extends ANoiseGenerator{
    
    private Random random = null;
    private float standardDeviation;
    
    public AdditiveGaussianWhiteNoise(float standardDeviation) {
        super(GAUSSIAN_WHITE_NOISE);
        random = new Random();
        this.standardDeviation = standardDeviation;
    }
    
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
            //System.out.println("Result: " + result + " msg[i] " + msg[i] + " res: " + ((byte) result^msg[i]));
            res[i] = (byte) (result^msg[i]);
            
        }
        return res;
    }
    
    
}
