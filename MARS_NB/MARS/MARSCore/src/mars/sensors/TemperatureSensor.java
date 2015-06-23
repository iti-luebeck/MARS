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
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import mars.Helper.NoiseType;
import mars.PhysicalEnvironment;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.events.AUVObjectEvent;
import mars.ros.MARSNodeMain;
import mars.server.MARSClientEvent;
import mars.states.SimState;
import org.ros.message.Time;
import org.ros.node.topic.Publisher;

/**
 * Returns the temperature of the surrounding fluid. Not really functional. Should be mapping sensor (?).
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class TemperatureSensor extends Sensor {

    private Geometry TemperatureSensorStart;

    ///ROS stuff
    private Publisher<sensor_msgs.Temperature> publisher = null;
    private sensor_msgs.Temperature fl;
    private std_msgs.Header header;

    /**
     *
     */
    public TemperatureSensor() {
        super();
    }

    /**
     *
     * @param simstate
     * @param pe
     */
    public TemperatureSensor(SimState simstate, PhysicalEnvironment pe) {
        super(simstate);
        this.pe = pe;
    }

    /**
     *
     * @param simstate
     */
    public TemperatureSensor(SimState simstate) {
        super(simstate);
    }

    /**
     *
     * @param sensor
     */
    public TemperatureSensor(TemperatureSensor sensor) {
        super(sensor);
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        TemperatureSensor sensor = new TemperatureSensor(this);
        sensor.initAfterJAXB();
        return sensor;
    }

    public void update(float tpf) {

    }

    @Override
    public void init(Node auv_node) {
        super.init(auv_node);
        Sphere sphere7 = new Sphere(8, 8, 0.025f);
        TemperatureSensorStart = new Geometry("TemperatureStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", ColorRGBA.White);
        TemperatureSensorStart.setMaterial(mark_mat7);
        TemperatureSensorStart.updateGeometricState();
        PhysicalExchanger_Node.attachChild(TemperatureSensorStart);
        PhysicalExchanger_Node.setLocalTranslation(getPosition());
        Quaternion quat = new Quaternion();
        quat.fromAngles(getRotation().getX(), getRotation().getY(), getRotation().getZ());
        PhysicalExchanger_Node.setLocalRotation(quat);
        auv_node.attachChild(PhysicalExchanger_Node);
    }

    /**
     *
     * @return The exact temperature of the current auv enviroemnt in C°
     */
    public float getTemperature() {
        if (getNoiseType() == NoiseType.NO_NOISE) {
            return getTemperatureRaw();
        } else if (getNoiseType() == NoiseType.UNIFORM_DISTRIBUTION) {
            float noise = getUnifromDistributionNoise(getNoiseValue());
            return getTemperatureRaw() + (((1f / 100f) * noise));
        } else if (getNoiseType() == NoiseType.GAUSSIAN_NOISE_FUNCTION) {
            float noise = getGaussianDistributionNoise(getNoiseValue());
            return getTemperatureRaw() + (((1f / 100f) * noise));
        } else {
            return getTemperatureRaw();
        }
    }

    /**
     * This formula is used: http://residualanalysis.blogspot.de/2010/02/temperature-of-ocean-water-at-given.html
     *
     * @param noise The boundary for the random generator starting always from 0 to noise value
     * @return The Temperature of the current auv enviroment with a random noise from 0 to noise value in C°
     */
    private float getTemperatureRaw() {
        float depth = Math.abs(TemperatureSensorStart.getWorldTranslation().y + Math.abs(pe.getWater_height()));
        float fd = 1f + (float) Math.exp(-0.016f * depth + 1.244f);
        float td = -0.338f + ((pe.getFluid_temp() * fd) / ((0.0001485f * pe.getFluid_temp() * depth) + fd));
        return td;
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

    /**
     *
     * @param ros_node
     * @param auv_name
     */
    @Override
    @SuppressWarnings("unchecked")
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        publisher = (Publisher<sensor_msgs.Temperature>) ros_node.newPublisher(auv_name + "/" + this.getName(), sensor_msgs.Temperature._TYPE);
        fl = this.mars_node.getMessageFactory().newFromType(sensor_msgs.Temperature._TYPE);
        header = this.mars_node.getMessageFactory().newFromType(std_msgs.Header._TYPE);
        this.rosinit = true;
    }

    /**
     *
     */
    @Override
    public void publish() {
        header.setSeq(sequenceNumber++);
        header.setFrameId(this.getRos_frame_id());
        header.setStamp(Time.fromMillis(System.currentTimeMillis()));
        fl.setHeader(header);
        fl.setTemperature((getTemperature() * 10f));//*10 because of ros temp data format
        fl.setVariance(0f);
        if (publisher != null) {
            publisher.publish(fl);
        }
    }

    @Override
    public void publishData() {
        super.publishData();
        MARSClientEvent clEvent = new MARSClientEvent(getAuv(), this, getTemperature(), System.currentTimeMillis());
        simState.getAuvManager().notifyAdvertisement(clEvent);
        AUVObjectEvent auvEvent = new AUVObjectEvent(this, getTemperature(), System.currentTimeMillis());
        notifyAdvertisementAUVObject(auvEvent);
    }
}
