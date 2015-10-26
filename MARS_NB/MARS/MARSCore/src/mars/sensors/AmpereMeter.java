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
package mars.sensors;

import com.jme3.scene.Node;
import com.rits.cloning.Cloner;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.Helper.NoiseType;
import mars.PhysicalEnvironment;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.auv.AUV;
import mars.events.AUVObjectEvent;
import mars.states.SimState;
import mars.xml.HashMapAdapter;

/**
 * Can measure how much ampere is left in an accumulator.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlAccessorType(XmlAccessType.NONE)
public class AmpereMeter extends Sensor {

    /**
     *
     */
    @XmlJavaTypeAdapter(HashMapAdapter.class)
    protected HashMap<String, String> accumulators;

    /**
     *
     */
    public AmpereMeter() {
        super();
    }

    /**
     *
     * @param simstate
     * @param pe
     */
    public AmpereMeter(SimState simstate, PhysicalEnvironment pe) {
        super(simstate);
        this.pe = pe;
    }

    /**
     *
     * @param simstate
     */
    public AmpereMeter(SimState simstate) {
        super(simstate);
    }

    /**
     *
     * @param ampereMeter
     */
    public AmpereMeter(AmpereMeter ampereMeter) {
        super(ampereMeter);

        //dont forget to clone accus hashmap
        HashMap<String, String> accumulatorsOriginal = ampereMeter.getAccumulators();
        Cloner cloner = new Cloner();
        accumulators = cloner.deepClone(accumulatorsOriginal);
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        AmpereMeter sensor = new AmpereMeter(this);
        sensor.initAfterJAXB();
        return sensor;
    }

    public void update(float tpf) {

    }

    @Override
    public void init(Node auv_node) {
        super.init(auv_node);
    }

    /**
     *
     * @param pe
     */
    @Override
    public void copyValuesFromPhysicalExchanger(PhysicalExchanger pe) {
        super.copyValuesFromPhysicalExchanger(pe);
        if (pe instanceof AmpereMeter) {
            HashMap<String, String> accumulatorsOriginal = ((AmpereMeter) pe).getAccumulators();
            Cloner cloner = new Cloner();
            accumulators = cloner.deepClone(accumulatorsOriginal);
        }
    }

    /**
     *
     * @return
     */
    public HashMap<String, String> getAccumulators() {
        return accumulators;
    }

    /**
     *
     * @param auv
     */
    public void setAuv(AUV auv) {
        this.auv = auv;
    }

    /**
     *
     * @return The exact temperature of the current auv enviroemnt in C°
     */
    public double getAmpere() {
        if (getNoiseType() == NoiseType.NO_NOISE) {
            return getAmpereRaw();
        } else if (getNoiseType() == NoiseType.UNIFORM_DISTRIBUTION) {
            float noise = getUnifromDistributionNoise(getNoiseValue());
            return getAmpereRaw() + (((1f / 100f) * noise));
        } else if (getNoiseType() == NoiseType.GAUSSIAN_NOISE_FUNCTION) {
            float noise = getGaussianDistributionNoise(getNoiseValue());
            return getAmpereRaw() + (((1f / 100f) * noise));
        } else {
            return getAmpereRaw();
        }
    }

    /**
     *
     * @param noise The boundary for the random generator starting always from 0 to noise value
     * @return The Temperature of the current auv enviroment with a random noise from 0 to noise value in C°
     */
    private double getAmpereRaw() {
        HashMap<String, String> accus = getAccumulators();
        double capacity = 0f;
        for (String elem : accus.keySet()) {
            String element = accus.get(elem);
            capacity = capacity + auv.getAccumulator(element).getActualCurrent();
        }
        return capacity;
    }

    /**
     *
     * @return
     */
    public PhysicalEnvironment getPe() {
        return pe;
    }

    /**
     *
     * @param pe
     */
    public void setPe(PhysicalEnvironment pe) {
        this.pe = pe;
    }

    /**
     *
     */
    @Override
    public void reset() {

    }

    @Override
    public void publishData() {
        super.publishData();
        AUVObjectEvent auvEvent = new AUVObjectEvent(this, getAmpere(), System.currentTimeMillis());
        notifyAdvertisementAUVObject(auvEvent);
    }
}
