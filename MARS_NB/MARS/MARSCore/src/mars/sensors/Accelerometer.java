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
import mars.PhysicalEnvironment;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.events.AUVObjectEvent;
import mars.states.SimState;

/**
 * An basic Acclerometer class. Measures the accleration for all 3 axis.
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Accelerometer extends Sensor {

    private Vector3f old_velocity = new Vector3f(0f, 0f, 0f);
    private Vector3f new_velocity = new Vector3f(0f, 0f, 0f);
    private Vector3f acceleration = new Vector3f(0f, 0f, 0f);

    /**
     *
     */
    public Accelerometer() {
        super();
    }

    /**
     *
     * @param simstate
     * @param pe
     */
    public Accelerometer(SimState simstate, PhysicalEnvironment pe) {
        super(simstate);
        this.pe = pe;
    }

    /**
     *
     * @param simstate
     */
    public Accelerometer(SimState simstate) {
        super(simstate);
    }

    /**
     *
     * @param accelerometer
     */
    public Accelerometer(Accelerometer accelerometer) {
        super(accelerometer);
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        Accelerometer sensor = new Accelerometer(this);
        sensor.initAfterJAXB();
        return sensor;
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
     * @param tpf
     */
    @Override
    public void update(float tpf) {
        new_velocity = physics_control.getLinearVelocity();//get the new velocity
        Vector3f difference_velocity = new_velocity.subtract(old_velocity);
        acceleration = difference_velocity.divide(tpf);
        acceleration = acceleration.add(pe.getGravitational_acceleration_vector());//dont forget the gravitational accleration
        old_velocity = new_velocity.clone();
    }

    /**
     *
     * @return
     */
    public float getAccelerationXAxis() {
        return acceleration.x;
    }

    /**
     *
     * @return
     */
    public float getAccelerationYAxis() {
        return acceleration.y;
    }

    /**
     *
     * @return
     */
    public float getAccelerationZAxis() {
        return acceleration.z;
    }

    /**
     *
     * @return
     */
    public Vector3f getAcceleration() {
        if (getNoiseType() == NoiseType.NO_NOISE) {
            return getAccelerationRaw();
        } else if (getNoiseType() == NoiseType.UNIFORM_DISTRIBUTION) {
            float noise = getUnifromDistributionNoise(getNoiseValue());
            Vector3f noised = new Vector3f(getAccelerationRaw().x + (((1f / 100f) * noise)), getAccelerationRaw().y + (((1f / 100f) * noise)), getAccelerationRaw().z + (((1f / 100f) * noise)));
            return noised;
        } else if (getNoiseType() == NoiseType.GAUSSIAN_NOISE_FUNCTION) {
            float noise = getGaussianDistributionNoise(getNoiseValue());
            Vector3f noised = new Vector3f(getAccelerationRaw().x + (((1f / 100f) * noise)), getAccelerationRaw().y + (((1f / 100f) * noise)), getAccelerationRaw().z + (((1f / 100f) * noise)));
            return noised;
        } else {
            return getAccelerationRaw();
        }
    }

    /**
     *
     * @return
     */
    private Vector3f getAccelerationRaw() {
        return acceleration;
    }

    /**
     *
     */
    @Override
    public void reset() {
        old_velocity = new Vector3f(0f, 0f, 0f);
        new_velocity = new Vector3f(0f, 0f, 0f);
        acceleration = new Vector3f(0f, 0f, 0f);
    }

    @Override
    public void publishData() {
        super.publishData();
        AUVObjectEvent auvEvent = new AUVObjectEvent(this, getAcceleration().length(), System.currentTimeMillis());
        notifyAdvertisementAUVObject(auvEvent);
    }
}
