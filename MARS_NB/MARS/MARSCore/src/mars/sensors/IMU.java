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
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import geometry_msgs.Quaternion;
import geometry_msgs.Vector3;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import mars.PhysicalEnvironment;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.auv.AUV;
import mars.events.AUVObjectEvent;
import mars.misc.IMUData;
import mars.ros.MARSNodeMain;
import mars.server.MARSClientEvent;
import mars.states.SimState;
import org.ros.message.Time;
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
        acc.setPhysical_environment(pe);
        gyro.setPhysical_environment(pe);
        oro.setPhysical_environment(pe);
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
    public void setPhysical_environment(PhysicalEnvironment pe) {
        super.setPhysical_environment(pe);
        acc.setPhysical_environment(pe);
        gyro.setPhysical_environment(pe);
        oro.setPhysical_environment(pe);
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
     * @param ros_node
     * @param auv_name
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        publisher = (Publisher<sensor_msgs.Imu>) ros_node.newPublisher(auv_name + "/" + this.getName(), sensor_msgs.Imu._TYPE);
        fl = this.mars_node.getMessageFactory().newFromType(sensor_msgs.Imu._TYPE);
        header = this.mars_node.getMessageFactory().newFromType(std_msgs.Header._TYPE);
        this.rosinit = true;
    }

    public IMUData getIMU() {
        IMUData mat = new IMUData(acc.getAcceleration(), gyro.getAngularVelocity(), oro.getOrientation());
        return mat;
    }

    /**
     *
     */
    @Deprecated
    public void publish() {
        header.setSeq(sequenceNumber++);
        header.setFrameId(this.getRos_frame_id());
        header.setStamp(Time.fromMillis(System.currentTimeMillis()));
        fl.setHeader(header);

        Vector3 ang_vec = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Vector3._TYPE);
        ang_vec.setX(gyro.getAngularVelocityXAxis());
        ang_vec.setY(gyro.getAngularVelocityZAxis());// y<-->z because in opengl/lwjgl/jme3 up vector is y not z!
        ang_vec.setZ(gyro.getAngularVelocityYAxis());
        fl.setAngularVelocity(ang_vec);

        Quaternion quat = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Quaternion._TYPE);;

        com.jme3.math.Quaternion ter_orientation = new com.jme3.math.Quaternion();
        com.jme3.math.Quaternion ter_orientation_rueck = new com.jme3.math.Quaternion();
        ter_orientation.fromAngles(-FastMath.HALF_PI, 0f, 0f);
        ter_orientation_rueck = ter_orientation.inverse();
        float[] bla = oro.getOrientation().toAngles(null);

        com.jme3.math.Quaternion jme3_quat = new com.jme3.math.Quaternion();
        jme3_quat.fromAngles(-bla[0], bla[1], -bla[2]);

        ter_orientation.multLocal(jme3_quat.multLocal(ter_orientation_rueck));

        quat.setX(ter_orientation.getX());// switching x and z!!!!
        quat.setY(ter_orientation.getY());
        quat.setZ(ter_orientation.getZ());
        quat.setW(ter_orientation.getW());

        Vector3f acc_jme3_vec = acc.getAcceleration();
        Vector3 acc_vec = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Vector3._TYPE);
        com.jme3.math.Quaternion acc_quat = oro.getOrientation().inverse();
        Vector3f acc_jme3_vec2 = acc_quat.mult(acc_jme3_vec);
        acc_vec.setX(acc_jme3_vec2.getX());
        acc_vec.setY(-acc_jme3_vec2.getZ());// y<-->z because in opengl/lwjgl/jme3 up vector is y not z!
        acc_vec.setZ(acc_jme3_vec2.getY());
        fl.setLinearAcceleration(acc_vec);

        fl.setOrientation(quat);

        if (publisher != null) {
            publisher.publish(fl);
        }
    }

    @Override
    public void publishData() {
        super.publishData();
        MARSClientEvent clEvent = new MARSClientEvent(getAuv(), this, getIMU(), System.currentTimeMillis());
        simState.getAuvManager().notifyAdvertisement(clEvent);
        AUVObjectEvent auvEvent = new AUVObjectEvent(this, getIMU(), System.currentTimeMillis());
        notifyAdvertisementAUVObject(auvEvent);
    }
}
