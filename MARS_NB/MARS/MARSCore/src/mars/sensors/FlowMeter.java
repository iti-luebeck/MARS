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

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import mars.Helper.NoiseType;
import mars.Initializer;
import mars.PhysicalEnvironment;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.events.AUVObjectEvent;
import mars.states.SimState;

/**
 * Returns the force of the water current.
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class FlowMeter extends Sensor {

    private Geometry FlowMeterStart;

    private Initializer initer;

    /**
     *
     */
    public FlowMeter() {
        super();
    }

    /**
     *
     * @param simstate
     * @param pe
     */
    public FlowMeter(SimState simstate, PhysicalEnvironment pe) {
        super(simstate);
        this.pe = pe;
    }

    /**
     *
     * @param simstate
     */
    public FlowMeter(SimState simstate) {
        super(simstate);
    }

    /**
     *
     * @param sensor
     */
    public FlowMeter(FlowMeter sensor) {
        super(sensor);
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        FlowMeter sensor = new FlowMeter(this);
        sensor.initAfterJAXB();
        return sensor;
    }

    /**
     *
     */
    @Override
    public void init(Node auv_node) {
        super.init(auv_node);
        Sphere sphere7 = new Sphere(8, 8, 0.025f);
        FlowMeterStart = new Geometry("FlowMeterStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", ColorRGBA.White);
        FlowMeterStart.setMaterial(mark_mat7);
        FlowMeterStart.updateGeometricState();
        PhysicalExchanger_Node.attachChild(FlowMeterStart);
        PhysicalExchanger_Node.setLocalTranslation(getPosition());
        Quaternion quat = new Quaternion();
        quat.fromAngles(getRotation().getX(), getRotation().getY(), getRotation().getZ());
        PhysicalExchanger_Node.setLocalRotation(quat);
        auv_node.attachChild(PhysicalExchanger_Node);
    }

    @Override
    public void update(float tpf) {

    }

    /**
     *
     * @return The exact depth of the current auv
     */
    public Vector3f getFlowForce() {
        if (getNoiseType() == NoiseType.NO_NOISE) {
            return getRawFlowForce();
        } else if (getNoiseType() == NoiseType.UNIFORM_DISTRIBUTION) {
            float noise = getUnifromDistributionNoise(getNoiseValue());
            Vector3f noised = new Vector3f(getRawFlowForce().x + (((1f / 100f) * noise)), getRawFlowForce().y + (((1f / 100f) * noise)), getRawFlowForce().z + (((1f / 100f) * noise)));
            return noised;
        } else if (getNoiseType() == NoiseType.GAUSSIAN_NOISE_FUNCTION) {
            float noise = getGaussianDistributionNoise(getNoiseValue());
            Vector3f noised = new Vector3f(getRawFlowForce().x + (((1f / 100f) * noise)), getRawFlowForce().y + (((1f / 100f) * noise)), getRawFlowForce().z + (((1f / 100f) * noise)));
            return noised;
        } else {
            return getRawFlowForce();
        }
    }

    /**
     *
     * @return The depth of the current auv
     */
    private Vector3f getRawFlowForce() {
        return initer.getFlowVector();
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
     * @return
     */
    public Initializer getIniter() {
        return initer;
    }

    /**
     *
     * @param initer
     */
    public void setIniter(Initializer initer) {
        this.initer = initer;
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
        AUVObjectEvent auvEvent = new AUVObjectEvent(this, getFlowForce(), System.currentTimeMillis());
        notifyAdvertisementAUVObject(auvEvent);
    }
}
