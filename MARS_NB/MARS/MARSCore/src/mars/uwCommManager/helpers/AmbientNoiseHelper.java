/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.helpers;

/**
 *
 * @author Jasper Schwinghammer
 */
public class AmbientNoiseHelper {
    
    
    
    private AmbientNoiseHelper(){
        
    }
    
    public static float calculateAmbientNoise(float frequence, float shippingFactor, float windSpeed) {
        float nT = 1;
        float nS = 1;
        float nW = 1;
        float nTH= 1;
        return nT + nS + nW + nTH;
    }
    
}
