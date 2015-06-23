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
 * This class provides a basic pressure sensor. You can get exact depth or exact pressure + noise.
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class PressureSensor extends Sensor {

    private Geometry PressureSensorStart;

    ///ROS stuff 
    private Publisher<sensor_msgs.FluidPressure> publisher = null;
    private sensor_msgs.FluidPressure fl;
    private std_msgs.Header header;

    /**
     *
     */
    public PressureSensor() {
        super();
    }

    /**
     *
     * @param simstate
     * @param pe
     */
    public PressureSensor(SimState simstate, PhysicalEnvironment pe) {
        super(simstate);
        this.pe = pe;
    }

    /**
     *
     * @param simstate
     */
    public PressureSensor(SimState simstate) {
        super(simstate);
    }

    /**
     *
     * @param sensor
     */
    public PressureSensor(PressureSensor sensor) {
        super(sensor);
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        PressureSensor sensor = new PressureSensor(this);
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
        PressureSensorStart = new Geometry("PressureStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", ColorRGBA.White);
        PressureSensorStart.setMaterial(mark_mat7);
        PressureSensorStart.updateGeometricState();
        PhysicalExchanger_Node.setLocalTranslation(getPosition());
        Quaternion quat = new Quaternion();
        quat.fromAngles(getRotation().getX(), getRotation().getY(), getRotation().getZ());
        PhysicalExchanger_Node.setLocalRotation(quat);
        PhysicalExchanger_Node.attachChild(PressureSensorStart);
        auv_node.attachChild(PhysicalExchanger_Node);
    }

    public void update(float tpf) {

    }

    /**
     *
     * @return The exact depth of the current auv
     */
    public float getDepth() {
        if (getNoiseType() == NoiseType.NO_NOISE) {
            return getRawDepth();
        } else if (getNoiseType() == NoiseType.UNIFORM_DISTRIBUTION) {
            float noise = getUnifromDistributionNoise(getNoiseValue());
            return getRawDepth() + (((1f / 100f) * noise));
        } else if (getNoiseType() == NoiseType.GAUSSIAN_NOISE_FUNCTION) {
            float noise = getGaussianDistributionNoise(getNoiseValue());
            return getRawDepth() + (((1f / 100f) * noise));
        } else {
            return getRawDepth();
        }
    }

    /**
     *
     * @return The depth of the current auv
     */
    private float getRawDepth() {
        return PressureSensorStart.getWorldTranslation().y + Math.abs(pe.getWater_height());
    }

    /**
     * See Pascal's law.
     *
     * @return The pressure that the pressure sensor measures in Bar
     */
    public float getPressureBar() {
        if (getDepth() <= pe.getWater_height()) {//underwater
            return (pe.getPressure_water_height() / 1000f) + ((pe.getFluid_density() * pe.getGravitational_acceleration() * Math.abs(getDepth())) / 100000f);
        } else {//air
            return (pe.getPressure_water_height() / 1000f);
        }
    }

    /**
     * See Pascal's law.
     *
     * @return The pressure that the pressure sensor measures in mBar
     */
    public float getPressureMbar() {
        if (getDepth() <= pe.getWater_height()) {//underwater
            return pe.getPressure_water_height() + ((pe.getFluid_density() * pe.getGravitational_acceleration() * Math.abs(getDepth())) / 100f);
        } else {//air
            return (pe.getPressure_water_height());
        }
    }

    /**
     * See Pascal's law.
     *
     * @return The pressure that the pressure sensor measures in Pascal
     */
    public float getPressurePascal() {
        if (getDepth() <= pe.getWater_height()) {//underwater
            return (pe.getPressure_water_height() * 100f) + (pe.getFluid_density() * pe.getGravitational_acceleration() * Math.abs(getDepth()));
        } else {//air
            return (pe.getPressure_water_height() * 100f);
        }
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
        publisher = (Publisher<sensor_msgs.FluidPressure>) ros_node.newPublisher(auv_name + "/" + this.getName(), sensor_msgs.FluidPressure._TYPE);
        fl = this.mars_node.getMessageFactory().newFromType(sensor_msgs.FluidPressure._TYPE);
        header = this.mars_node.getMessageFactory().newFromType(std_msgs.Header._TYPE);
        this.rosinit = true;
    }

    /**
     *
     */
    @Override
    public void publish() {
        super.publish();
        header.setSeq(sequenceNumber++);
        header.setFrameId(this.getRos_frame_id());
        header.setStamp(Time.fromMillis(System.currentTimeMillis()));
        fl.setHeader(header);
        fl.setFluidPressure(getPressurePascal());
        fl.setVariance(0f);
        if (publisher != null) {
            publisher.publish(fl);
        }
    }

    @Override
    public void publishData() {
        super.publishData();
        MARSClientEvent clEvent = new MARSClientEvent(getAuv(), this, getPressureMbar(), System.currentTimeMillis());
        simState.getAuvManager().notifyAdvertisement(clEvent);
        AUVObjectEvent auvEvent = new AUVObjectEvent(this, getPressureMbar(), System.currentTimeMillis());
        notifyAdvertisementAUVObject(auvEvent);
    }
}
