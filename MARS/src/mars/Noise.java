/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars;

import java.util.Random;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * In this class should be several static methods for noise. Nothing done yet.
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Noise {
    /**
     *
     */
    @XmlElement
    protected int noise_type = 0;
    /**
     *
     */
    @XmlElement
    protected float noise_value = 1.0f;
    /*
     *
     */
    /**
     *
     */
    protected Random random = new Random();

    /**
     *
     * @return
     */
    public int getNoise_type() {
        return noise_type;
    }

    /**
     *
     * @param noise_type
     */
    public void setNoise_type(int noise_type) {
        this.noise_type = noise_type;
    }

    /**
     * 
     * @return
     */
    public float getNoise_value() {
        return noise_value;
    }

    /**
     *
     * @param noise_value
     */
    public void setNoise_value(float noise_value) {
        this.noise_value = noise_value;
    }

    /**
     *
     * @param UnifromDeviation
     * @return
     */
    protected float getUnifromDistributionNoise(float UnifromDeviation){
        int rand = random.nextInt((int)UnifromDeviation + 1);
        if( random.nextBoolean() == true )//change +/- with 50%P
            rand = (-1)*rand;
        return rand;
    }

    /**
     *
     * @param StandardDeviation
     * @return
     */
    protected float getGaussianDistributionNoise(float StandardDeviation){
        float rand = (float)((random.nextGaussian()*(StandardDeviation)));
        return rand;
    }
}
