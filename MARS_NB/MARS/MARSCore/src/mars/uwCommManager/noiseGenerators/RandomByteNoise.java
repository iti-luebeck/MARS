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
public class RandomByteNoise extends ANoiseGenerator {
    
    private Random random = null;
    
    public RandomByteNoise(long seed) {
        super(RANDOM_BYTE_NOISE);
        random = new Random(seed);
    }
    
    public RandomByteNoise() {
        super(RANDOM_BYTE_NOISE);
        random = new Random();
    }

    @Override
    public byte[] noisify(byte[] msg) {
        byte[] noise = new byte[msg.length];
        random.nextBytes(noise);
        for(int i = 0; i<msg.length; i++) {
            noise[i] =(byte) (noise[i] ^ msg[i]);
            //System.out.println("New byte: "+ noise[i]+" old byte: " + msg[i]);
        }
        return noise.clone();
    }
    
}
