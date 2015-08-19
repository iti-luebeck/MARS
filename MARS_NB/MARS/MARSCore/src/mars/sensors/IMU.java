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

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import mars.PhysicalEnvironment;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.auv.AUV;
import mars.events.AUVObjectEvent;
import mars.misc.IMUData;
import mars.states.SimState;
import org.ros.node.topic.Publisher;

/**
 * An inertial measurment unit class. Basically it consist of the Accelerometer, Gyroscope and Compass class as an Mixin class.
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class IMU extends Sensor {

    @XmlElement(name = "Accelerometer")
    Accelerometer acc = new Accelerometer();
    @XmlElement(name = "Gyroscope")
    Gyroscope gyro = new Gyroscope();
    @XmlElement(name = "Orientationmeter")
    Orientationmeter oro = new Orientationmeter();

    ///ROS stuff
    private Publisher<sensor_msgs.Imu> publisher = null;
    private sensor_msgs.Imu fl;
    private std_msgs.Header header;

    Arrow arrow;

    /**
     *
     */
    public IMU() {
        super();
    }

    /**
     *
     * @param simstate
     * @param pe
     */
    public IMU(SimState simstate, PhysicalEnvironment pe) {
        super(simstate);
        this.pe = pe;
        acc.setPhysicalEnvironment(pe);
        gyro.setPhysicalEnvironment(pe);
        oro.setPhysicalEnvironment(pe);
        acc.setSimState(simState);
        gyro.setSimState(simState);
        oro.setSimState(simState);
    }

    /**
     *
     * @param simstate
     */
    public IMU(SimState simstate) {
        super(simstate);
        acc.setSimState(simState);
        gyro.setSimState(simState);
        oro.setSimState(simState);
    }

    /**
     *
     * @param sensor
     */
    public IMU(IMU sensor) {
        super(sensor);
        oro = (Orientationmeter) sensor.getOrientationmeter().copy();
        gyro = (Gyroscope) sensor.getGyroscope().copy();
        acc = (Accelerometer) sensor.getAccelerometer().copy();
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        IMU sensor = new IMU(this);
        sensor.initAfterJAXB();
        return sensor;
    }

    /**
     *
     */
    @Override
    public void init(Node auv_node) {
        super.init(auv_node);
        acc.init(auv_node);
        gyro.init(auv_node);
        oro.init(auv_node);
    }

    /**
     *
     * @param tpf
     */
    public void update(float tpf) {
        acc.update(tpf);
        gyro.update(tpf);
        oro.update(tpf);
    }

    /**
     *
     */
    public void reset() {
        acc.reset();
        gyro.reset();
        oro.reset();
    }

    @Override
    public void setPhysicalEnvironment(PhysicalEnvironment pe) {
        super.setPhysicalEnvironment(pe);
        acc.setPhysicalEnvironment(pe);
        gyro.setPhysicalEnvironment(pe);
        oro.setPhysicalEnvironment(pe);
    }

    /**
     *
     * @param simState
     */
    @Override
    public void setSimState(SimState simState) {
        super.setSimState(simState);
        acc.setSimState(simState);
        gyro.setSimState(simState);
        oro.setSimState(simState);
    }

    @Override
    public void setPhysicsControl(RigidBodyControl physics_control) {
        super.setPhysicsControl(physics_control);
        acc.setPhysicsControl(physics_control);
        gyro.setPhysicsControl(physics_control);
        oro.setPhysicsControl(physics_control);
    }

    /**
     *
     * @param visible
     */
    @Override
    public void setNodeVisibility(boolean visible) {
        super.setNodeVisibility(visible);
        acc.setNodeVisibility(visible);
        gyro.setNodeVisibility(visible);
        oro.setNodeVisibility(visible);
    }

    /**
     *
     * @param name
     */
    @Override
    public void setName(String name) {
        super.setName(name);
        acc.setName(name + "_accelerometer");
        gyro.setName(name + "_gyroscope");
        oro.setName(name + "_orientationmeter");
    }

    /**
     *
     * @param enabled
     */
    @Override
    public void setEnabled(Boolean enabled) {
        super.setEnabled(enabled);
        acc.setEnabled(enabled);
        gyro.setEnabled(enabled);
        oro.setEnabled(enabled);
    }

    @Override
    public void setAuv(AUV auv) {
        super.setAuv(auv);
        acc.setAuv(auv);
        gyro.setAuv(auv);
        oro.setAuv(auv);
    }

    /**
     *
     * @return
     */
    public Gyroscope getGyroscope() {
        return gyro;
    }

    /**
     *
     * @return
     */
    public Accelerometer getAccelerometer() {
        return acc;
    }

    /**
     *
     * @return
     */
    public Orientationmeter getOrientationmeter() {
        return oro;
    }

    /**
     *
     * @return
     */
    public IMUData getIMU() {
        IMUData mat = new IMUData(acc.getAcceleration(), gyro.getAngularVelocity(), oro.getOrientation());
        return mat;
    }

    @Override
    public void publishData() {
        super.publishData();
        AUVObjectEvent auvEvent = new AUVObjectEvent(this, getIMU(), System.currentTimeMillis());
        notifyAdvertisementAUVObject(auvEvent);
    }
}
