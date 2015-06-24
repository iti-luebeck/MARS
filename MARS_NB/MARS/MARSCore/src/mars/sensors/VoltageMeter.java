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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import mars.Helper.NoiseType;
import mars.PhysicalEnvironment;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.events.AUVObjectEvent;
import mars.server.MARSClientEvent;
import mars.states.SimState;
import org.ros.message.Time;
import org.ros.node.topic.Publisher;

/**
 * CAn measure the voltage of an accumulator.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlAccessorType(XmlAccessType.NONE)
public class VoltageMeter extends Sensor {

    ///ROS stuff
    private Publisher<std_msgs.Float32> publisher = null;
    private std_msgs.Float32 fl;
    private std_msgs.Header header;

    /**
     *
     */
    public VoltageMeter() {
        super();
    }

    /**
     *
     * @param simstate
     * @param pe
     */
    public VoltageMeter(SimState simstate, PhysicalEnvironment pe) {
        super(simstate);
        this.pe = pe;
    }

    /**
     *
     * @param simstate
     */
    public VoltageMeter(SimState simstate) {
        super(simstate);
    }

    /**
     *
     * @param sensor
     */
    public VoltageMeter(VoltageMeter sensor) {
        super(sensor);
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        VoltageMeter sensor = new VoltageMeter(this);
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
     * @return The exact temperature of the current auv enviroemnt in C°
     */
    public float getVoltage() {
        if (getNoiseType() == NoiseType.NO_NOISE) {
            return getVoltageRaw();
        } else if (getNoiseType() == NoiseType.UNIFORM_DISTRIBUTION) {
            float noise = getUnifromDistributionNoise(getNoiseValue());
            return getVoltageRaw() + (((1f / 100f) * noise));
        } else if (getNoiseType() == NoiseType.GAUSSIAN_NOISE_FUNCTION) {
            float noise = getGaussianDistributionNoise(getNoiseValue());
            return getVoltageRaw() + (((1f / 100f) * noise));
        } else {
            return getVoltageRaw();
        }
    }

    /**
     *
     * @param noise The boundary for the random generator starting always from 0 to noise value
     * @return The Temperature of the current auv enviroment with a random noise from 0 to noise value in C°
     */
    private float getVoltageRaw() {
        return pe.getFluid_temp();
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
    public void reset() {

    }

    /**
     *
     * @param ros_node
     * @param auv_name
     *
     * @Deprecated
     * @SuppressWarnings("unchecked") public void initROS(MARSNodeMain ros_node, String auv_name) { publisher = (Publisher<std_msgs.Float32>) ros_node.newPublisher(auv_name + "/" + this.getName(), std_msgs.Float32._TYPE); fl = this.mars_node.getMessageFactory().newFromType(std_msgs.Float32._TYPE); header = this.mars_node.getMessageFactory().newFromType(std_msgs.Header._TYPE); this.rosinit = true;
    }
     */
    /**
     *
     */
    @Deprecated
    public void publish() {
        header.setSeq(sequenceNumber++);
        header.setFrameId(this.getRos_frame_id());
        header.setStamp(Time.fromMillis(System.currentTimeMillis()));
        fl.setData(getVoltage());

        if (publisher != null) {
            publisher.publish(fl);
        }
    }

    @Override
    public void publishData() {
        super.publishData();
        MARSClientEvent clEvent = new MARSClientEvent(getAuv(), this, getVoltage(), System.currentTimeMillis());
        simState.getAuvManager().notifyAdvertisement(clEvent);
        AUVObjectEvent auvEvent = new AUVObjectEvent(this, getVoltage(), System.currentTimeMillis());
        notifyAdvertisementAUVObject(auvEvent);
    }
}
