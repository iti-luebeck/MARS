/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import geometry_msgs.Quaternion;
import geometry_msgs.Vector3;
import org.ros.node.topic.Publisher;
import mars.PhysicalEnvironment;
import mars.states.SimState;
import mars.ros.MARSNodeMain;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import mars.PhysicalExchange.PhysicalExchanger;
import org.ros.message.Time;

/**
 * An inertial measurment unit class. Basically it consist of the Accelerometer,
 * Gyroscope and Compass class as an Mixin class.
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
    @Override
    @SuppressWarnings("unchecked")
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        publisher = (Publisher<sensor_msgs.Imu>)ros_node.newPublisher(auv_name + "/" + this.getName(), sensor_msgs.Imu._TYPE);
        fl = this.mars_node.getMessageFactory().newFromType(sensor_msgs.Imu._TYPE);
        header = this.mars_node.getMessageFactory().newFromType(std_msgs.Header._TYPE);
        this.rosinit = true;
    }

    /**
     *
     */
    @Override
    public void publish() {
        super.publish();
        header.setSeq(rosSequenceNumber++);
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
}
