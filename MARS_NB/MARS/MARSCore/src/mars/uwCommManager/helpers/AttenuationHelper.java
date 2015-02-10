/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.helpers;

import mars.PhysicalEnvironment;

/**
 *
 * @author Jasper Schwinghammer
 */
public class AttenuationHelper {
    
    public static final float SPHERICAL_SPREADING = 2;
    public static final float CYLINDRICAL_SPREADING = 1;
    public static final float PRACTICAL_SPREADKING = 0.5f;
    
    private PhysicalEnvironment environment;
    
    
    public AttenuationHelper(PhysicalEnvironment environment) {
        this.environment = environment;
    }
    
    
    
    private float calculateAlpha(float frequence, float temperature, float salinity, float depth) {
        float alph3 = (float) (0.00049f*frequence*frequence*Math.exp(-(temperature/27f+depth/17f)));
        float f1 = (float) (0.78f*Math.sqrt((salinity/35f)*Math.exp(temperature/26)));
        float f2 = (float) (42*Math.exp(temperature/17f));
        float a1 = (float) (0.106*Math.exp(0));
        float a2 = (float) (0.52*(1+temperature/43)*(salinity/35)*Math.exp(-depth/6));
        
        float alph2 = a2*((f2*frequence)/(f2*f2+frequence*frequence));
        float alph1 = a1*((f1*frequence)/(f1*f1+frequence*frequence));
        return alph3+alph2+alph1;
    }
    
    
    public float carculateAttenuationInDB(float distance, float frequence,float spreadingModel,float depth) {
        return (float) (spreadingModel * 10 * Math.log10(distance*1000)+distance * calculateAlpha(frequence, environment.getFluid_temp(), environment.getFluid_salinity(), depth));
    }
}
