/*
* Copyright (c) 2015, Institute of Computer Engineering, University of Lübeck
* All rights reserved.
* 
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* 
* * Redistributions of source code must retain the above copyright notice, this
*   list of conditions and the following disclaimer.
* 
* * Redistributions in binary form must reproduce the above copyright notice,
*   this list of conditions and the following disclaimer in the documentation
*   and/or other materials provided with the distribution.
* 
* * Neither the name of the copyright holder nor the names of its
*   contributors may be used to endorse or promote products derived from
*   this software without specific prior written permission.
* 
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
* FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
* CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
