/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.helpers;

import mars.PhysicalEnvironment;

/**
 * @version 0.1
 * @author Jasper Schwinghammer
 */
public class AttenuationHelper {
    
    //SPREADING FACTORS
    public static final float SPHERICAL_SPREADING = 2;
    public static final float CYLINDRICAL_SPREADING = 1;
    public static final float PRACTICAL_SPREADKING = 1.5f;
    
    private PhysicalEnvironment environment;
    
    /**
     * create a new AttenuationHelper for a fixed environment
     * @param environment 
     */
    public AttenuationHelper(final PhysicalEnvironment environment) {
        this.environment = environment;
    }
    
    /**
     * Calculate the absorption coefficient with the Thorp model. Thorp is a simplified model for low-frequeny sound (below 25 khz)
     * @since 0.1
     * @param freq
     * @return the absorption coefficient alpha in dB/km
     */
    private float calculateAlphaThorp(final float freq) {
        return (float) ((((0.11f)/(1+Math.pow(freq, 2)))+(44f/(4100+Math.pow(freq, 2))))*Math.pow(freq, 2));
    }
    
    /**
     * Calculate the absorption coefficient with the Francois-Garrison model. Calculates the contributions of boric acid, magnesium sulfate and pure water to the total absorption.
     * @since 0.1
     * @param frequence the frequence in kHz
     * @param temperature the temperature in C
     * @param salinity the salinity in p.s.u.
     * @param depth the depth in m
     * @return the absorption coefficient alpha in dB/km
     */
    private float calculateAlpha(float frequence, float temperature, float salinity, float depth) {
        float p3 = (float) (1-(3.83f*Math.pow(10, -5)*(depth))+(4.9f*Math.pow(10, -10)*depth*depth));
        float a3 = 0;
        if(temperature <20) a3 = (float) ((4.937f*Math.pow(10,-4))-(2.59f*Math.pow(10,-5)*temperature)+(9.11f*Math.pow(10, -7)*temperature*temperature)-(1.5f*Math.pow(10, -8)*Math.pow(temperature, 3)));
        else a3 = (float) ((3.964f*Math.pow(10,-4))-(1.146f*Math.pow(10,-5)*temperature)+(1.45f*Math.pow(10, -7)*temperature*temperature)-(6.5f*Math.pow(10, -8)*Math.pow(temperature, 3)));
        float alpha3 = (float) (a3*p3*Math.pow(frequence, 2));
        
        float c = (float) (1412+(3.21*temperature)+(1.19f*salinity)+0.0167*depth);
        
        float a2 = (float) (21.44f*(salinity / c)*(1+0.025*temperature));
        float p2 = (float) (1-(1.37f*Math.pow(10,-4)*depth)+(6.2f*Math.pow(10, -9)*depth*depth));
        float f2 = (float) ((8.17*Math.pow(10, (8f-((1990f)/(temperature+273f)))))/(1f+(0.0018f*(salinity-35))));
        
        float alpha2 = (float) (a2*p2*((f2*frequence*frequence)/(Math.pow(frequence, 2)+Math.pow(f2, 2))));
        
        float a1 = (float) (((8.86f)/(c))*Math.pow(10, (0.78*8)-5));
        float p1 = (float) 1f;
        float f1 = (float) (2.8f*Math.sqrt(salinity/35)*Math.pow(10, (4-(1245)/(temperature+273))));
        
        float alpha1 = (float) (a1*p1*((f1*frequence*frequence)/(Math.pow(frequence, 2)+Math.pow(f1, 2))));
        return alpha1+alpha2+alpha3;
    }
    
    
    /**
     * Calculate the attentuation of a signal. Takes absorption by molecules and attenuation by geometrical spreading into account
     * @param distance the distance in meters
     * @param frequence the frequence in khz
     * @param spreadingModel the spreading model, SPHERICAL_SPREADING for short distances, CYLDRICAL_SPREADING for longer distances
     * @param depth the depth in meters
     * @return 
     */
    public float carculateAttenuationInDB(float distance, float frequence,float spreadingModel,float depth) {
        if(frequence<25) return(float) (spreadingModel * 10 * Math.log10(distance*1000)+distance * calculateAlphaThorp(frequence));
        return (float) (spreadingModel * 10 * Math.log10(distance*1000)+distance * calculateAlpha(frequence, environment.getFluid_temp(), environment.getFluid_salinity(), depth));
    }
}
