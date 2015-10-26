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

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import mars.Helper.NoiseType;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.events.AUVObjectEvent;
import mars.states.SimState;

/**
 * This a basic gyroscope class. It gives you the Angular velocity.
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Gyroscope extends Sensor {

    /**
     *
     */
    public Gyroscope() {
        super();
    }

    /**
     *
     * @param simstate
     */
    public Gyroscope(SimState simstate) {
        super(simstate);
    }

    /**
     *
     * @param sensor
     */
    public Gyroscope(Gyroscope sensor) {
        super(sensor);
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        Gyroscope sensor = new Gyroscope(this);
        sensor.initAfterJAXB();
        return sensor;
    }

    public void update(float tpf) {

    }

    /**
     *
     */
    @Override
    public void init(Node auv_node) {
        super.init(auv_node);
    }

    /**
     *
     * @return
     */
    public Vector3f getAngularVelocity() {
        if (getNoiseType() == NoiseType.NO_NOISE) {
            return getAngularVelocityRaw();
        } else if (getNoiseType() == NoiseType.UNIFORM_DISTRIBUTION) {
            float noise = getUnifromDistributionNoise(getNoiseValue());
            Vector3f noised = new Vector3f(getAngularVelocityRaw().x + (((1f / 100f) * noise)), getAngularVelocityRaw().y + (((1f / 100f) * noise)), getAngularVelocityRaw().z + (((1f / 100f) * noise)));
            return noised;
        } else if (getNoiseType() == NoiseType.GAUSSIAN_NOISE_FUNCTION) {
            float noise = getGaussianDistributionNoise(getNoiseValue());
            Vector3f noised = new Vector3f(getAngularVelocityRaw().x + (((1f / 100f) * noise)), getAngularVelocityRaw().y + (((1f / 100f) * noise)), getAngularVelocityRaw().z + (((1f / 100f) * noise)));
            return noised;
        } else {
            return getAngularVelocityRaw();
        }
    }

    /**
     * in radiant
     *
     * @return
     */
    private Vector3f getAngularVelocityRaw() {
        return physics_control.getAngularVelocity();
    }

    /**
     * in radiant
     *
     * @return
     */
    public float getAngularVelocityXAxis() {
        return physics_control.getAngularVelocity().x;
    }

    /**
     * in radiant
     *
     * @return
     */
    public float getAngularVelocityYAxis() {
        return physics_control.getAngularVelocity().y;
    }

    /**
     * in radiant
     *
     * @return
     */
    public float getAngularVelocityZAxis() {
        return physics_control.getAngularVelocity().z;
    }

    /**
     *
     */
    public void reset() {

    }

    @Override
    public void publishData() {
        super.publishData();
        AUVObjectEvent auvEvent = new AUVObjectEvent(this, getAngularVelocity().length(), System.currentTimeMillis());
        notifyAdvertisementAUVObject(auvEvent);
    }
}
