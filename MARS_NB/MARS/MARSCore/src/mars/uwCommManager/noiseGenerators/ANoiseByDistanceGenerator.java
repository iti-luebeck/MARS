/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.noiseGenerators;

/**
 *
 * @author Jasper Schwinghammer
 */
public abstract class ANoiseByDistanceGenerator extends ANoiseGenerator implements IDistanceNoiseGenerator{

    public ANoiseByDistanceGenerator(String noiseName) {
        super(noiseName);
    }
    
    
    
}
