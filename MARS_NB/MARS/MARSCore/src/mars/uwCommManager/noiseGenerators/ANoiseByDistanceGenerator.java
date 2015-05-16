/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.uwCommManager.noiseGenerators;

/**
 * @version 0.1
 * @author Jasper Schwinghammer
 */
public abstract class ANoiseByDistanceGenerator extends ANoiseGenerator implements IDistanceNoiseGenerator{
    
    protected float shippingFactor;
    
    protected float windspeed;

    public ANoiseByDistanceGenerator(String noiseName, float shippingFactor, float windspeed) {
        super(noiseName);
        shippingFactor = shippingFactor;
        windspeed = windspeed;
    }
    
    
    /**
     * Set the windspeed (in meter per second)
     * @since 1.2
     * @param windspeed 
     */
    public void setWindSpeed(float windspeed) {
        this.windspeed = windspeed;
    }
    /**
     * Set the shippingFactor it must be between 0 and 1
     * @since 1.2
     * @param shippingFactor 
     */
    public void setShippingFactor(float shippingFactor) {
        if(shippingFactor < 0) this.shippingFactor = 0;
        else if(1 <shippingFactor) this.shippingFactor =1;
        else this.shippingFactor = shippingFactor;
    }
    
    
}
