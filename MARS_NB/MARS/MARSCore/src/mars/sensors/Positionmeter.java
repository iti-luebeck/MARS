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
 * Gives the exact position in world coordinates.
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Positionmeter extends Sensor {

    private Vector3f old_position = new Vector3f(0f, 0f, 0f);
    private Vector3f new_position = new Vector3f(0f, 0f, 0f);

    /**
     *
     */
    public Positionmeter() {
        super();
    }

    /**
     *
     * @param simstate
     * @param pe
     */
    public Positionmeter(SimState simstate, PhysicalEnvironment pe) {
        super(simstate);
        this.pe = pe;
    }

    /**
     *
     * @param simstate
     */
    public Positionmeter(SimState simstate) {
        super(simstate);
    }

    /**
     *
     * @param sensor
     */
    public Positionmeter(Positionmeter sensor) {
        super(sensor);
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        Positionmeter sensor = new Positionmeter(this);
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
        new_position = physics_control.getPhysicsLocation();//get the new position
        old_position = new_position.clone();
    }

    /**
     *
     * @return
     */
    public float getPositionX() {
        return new_position.x;
    }

    /**
     *
     * @return
     */
    public float getPositionY() {
        return new_position.y;
    }

    /**
     *
     * @return
     */
    public float getPositionZ() {
        return new_position.z;
    }

    /**
     *
     * @return
     */
    public Vector3f getWorldPosition() {
        if (getNoiseType() == NoiseType.NO_NOISE) {
            return getPositionRaw();
        } else if (getNoiseType() == NoiseType.UNIFORM_DISTRIBUTION) {
            float noise = getUnifromDistributionNoise(getNoiseValue());
            Vector3f noised = new Vector3f(getPositionRaw().x + (((1f / 100f) * noise)), getPositionRaw().y + (((1f / 100f) * noise)), getPositionRaw().z + (((1f / 100f) * noise)));
            return noised;
        } else if (getNoiseType() == NoiseType.GAUSSIAN_NOISE_FUNCTION) {
            float noise = getGaussianDistributionNoise(getNoiseValue());
            Vector3f noised = new Vector3f(getPositionRaw().x + (((1f / 100f) * noise)), getPositionRaw().y + (((1f / 100f) * noise)), getPositionRaw().z + (((1f / 100f) * noise)));
            return noised;
        } else {
            return getPositionRaw();
        }
    }

    /**
     *
     * @return
     */
    private Vector3f getPositionRaw() {
        return physics_control.getPhysicsLocation();
    }

    /**
     *
     */
    @Override
    public void reset() {
        old_position = new Vector3f(0f, 0f, 0f);
        new_position = new Vector3f(0f, 0f, 0f);
    }

    @Override
    public void publishData() {
        super.publishData();
        AUVObjectEvent auvEvent = new AUVObjectEvent(this, getWorldPosition(), System.currentTimeMillis());
        notifyAdvertisementAUVObject(auvEvent);
    }
}
