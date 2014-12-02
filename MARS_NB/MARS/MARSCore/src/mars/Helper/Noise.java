/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.Helper;

import java.util.HashMap;
import java.util.Random;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.xml.HashMapAdapter;

/**
 * In this class should be several static methods for noises like gaussian
 * distribution.
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Noise {

    /**
     *
     */
    @XmlJavaTypeAdapter(HashMapAdapter.class)
    @XmlElement(name = "noise")
    protected HashMap<String, Object> noises;
    /**
     *
     */
    protected Random random = new Random();

    /**
     *
     * @return
     */
    public Integer getNoiseType() {
        return (Integer) noises.get("NoiseType");
    }

    /**
     *
     * @param NoiseType
     */
    public void setNoiseType(Integer NoiseType) {
        noises.put("NoiseType", NoiseType);
    }

    /**
     *
     * @return
     */
    public Float getNoiseValue() {
        return (Float) noises.get("NoiseValue");
    }

    /**
     *
     * @param NoiseValue
     */
    public void setNoiseValue(Float NoiseValue) {
        noises.put("NoiseValue", NoiseValue);
    }

    /**
     *
     * @param UnifromDeviation
     * @return
     */
    protected float getUnifromDistributionNoise(float UnifromDeviation) {
        int rand = random.nextInt((int) UnifromDeviation + 1);
        if (random.nextBoolean() == true)//change +/- with 50%P
        {
            rand = (-1) * rand;
        }
        return rand;
    }

    /**
     *
     * @param StandardDeviation
     * @return
     */
    protected float getGaussianDistributionNoise(float StandardDeviation) {
        float rand = (float) ((random.nextGaussian() * (StandardDeviation)));
        return rand;
    }

    /**
     *
     * @return
     */
    public HashMap<String, Object> getAllNoiseVariables() {
        return noises;
    }
}
