/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import mars.ChartValue;
import mars.PhysicalEnvironment;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.ros.MARSNodeMain;
import mars.states.SimState;
import org.ros.message.Time;
import org.ros.node.topic.Publisher;

/**
 * Gives the exact Pose(Position/Orientation). Mixin class.
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Posemeter extends Sensor implements ChartValue {

    @XmlElement(name = "Positionmeter")
    Positionmeter pos = new Positionmeter();
    @XmlElement(name = "Orientationmeter")
    Orientationmeter oro = new Orientationmeter();

    ///ROS stuff
    private Publisher<geometry_msgs.PoseStamped> publisher = null;
    private geometry_msgs.PoseStamped fl;
    private std_msgs.Header header;

    /**
     *
     */
    public Posemeter() {
        super();
    }

    /**
     *
     * @param simstate
     * @param pe
     */
    public Posemeter(SimState simstate, PhysicalEnvironment pe) {
        super(simstate);
        this.pe = pe;
        pos.setPhysical_environment(pe);
        oro.setPhysical_environment(pe);
        pos.setSimState(simState);
        oro.setSimState(simState);
    }

    /**
     *
     * @param simstate
     */
    public Posemeter(SimState simstate) {
        super(simstate);
        pos.setSimState(simState);
        oro.setSimState(simState);
    }

    /**
     *
     * @param sensor
     */
    public Posemeter(Posemeter sensor) {
        super(sensor);
        oro = (Orientationmeter) sensor.getOrientationmeter().copy();
        pos = (Positionmeter) sensor.getPositionmeter().copy();
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        Posemeter sensor = new Posemeter(this);
        sensor.initAfterJAXB();
        return sensor;
    }

    /**
     *
     */
    @Override
    public void init(Node auv_node) {
        super.init(auv_node);
        pos.init(auv_node);
        oro.init(auv_node);
    }

    /**
     *
     * @param tpf
     */
    public void update(float tpf) {
        pos.update(tpf);
        oro.update(tpf);
    }

    /**
     *
     */
    public void reset() {
        pos.reset();
        oro.reset();
    }

    @Override
    public void setPhysical_environment(PhysicalEnvironment pe) {
        super.setPhysical_environment(pe);
        pos.setPhysical_environment(pe);
        oro.setPhysical_environment(pe);
    }

    /**
     *
     * @param simState
     */
    @Override
    public void setSimState(SimState simState) {
        super.setSimState(simState);
        pos.setSimState(simState);
        oro.setSimState(simState);
    }

    @Override
    public void setPhysicsControl(RigidBodyControl physics_control) {
        super.setPhysicsControl(physics_control);
        pos.setPhysicsControl(physics_control);
        oro.setPhysicsControl(physics_control);
    }

    /**
     *
     * @param visible
     */
    @Override
    public void setNodeVisibility(boolean visible) {
        super.setNodeVisibility(visible);
        pos.setNodeVisibility(visible);
        oro.setNodeVisibility(visible);
    }

    /**
     *
     * @param name
     */
    @Override
    public void setName(String name) {
        super.setName(name);
        pos.setName(name + "_positionmeter");
        oro.setName(name + "_orientationmeter");
    }

    /**
     *
     * @param enabled
     */
    @Override
    public void setEnabled(Boolean enabled) {
        super.setEnabled(enabled);
        pos.setEnabled(enabled);
        oro.setEnabled(enabled);
    }

    /**
     *
     * @return
     */
    public Positionmeter getPositionmeter() {
        return pos;
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
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        publisher = ros_node.newPublisher(auv_name + "/" + this.getName(), geometry_msgs.PoseStamped._TYPE);
        fl = this.mars_node.getMessageFactory().newFromType(geometry_msgs.PoseStamped._TYPE);
        header = this.mars_node.getMessageFactory().newFromType(std_msgs.Header._TYPE);
        this.rosinit = true;
    }

    /**
     *
     */
    @Override
    public void publish() {
        header.setSeq(rosSequenceNumber++);
        header.setFrameId(this.getRos_frame_id());
        header.setStamp(Time.fromMillis(System.currentTimeMillis()));
        fl.setHeader(header);

        geometry_msgs.Point point = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Point._TYPE);
        point.setX(pos.getWorldPosition().x);
        point.setY(pos.getWorldPosition().z);//dont forget to switch y and z!!!!
        point.setZ(pos.getWorldPosition().y);

        geometry_msgs.Quaternion orientation = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Quaternion._TYPE);
        Quaternion ter_orientation = new Quaternion();
        Quaternion ter_orientation_rueck = new Quaternion();
        ter_orientation.fromAngles(-FastMath.HALF_PI, 0f, 0f);
        ter_orientation_rueck = ter_orientation.inverse();
        float[] bla = oro.getOrientation().toAngles(null);
        com.jme3.math.Quaternion jme3_quat = new com.jme3.math.Quaternion();
        jme3_quat.fromAngles(-bla[0], bla[1], -bla[2]);
        ter_orientation.multLocal(jme3_quat.multLocal(ter_orientation_rueck));
        float[] ff = ter_orientation.toAngles(null);
        orientation.setX((ter_orientation).getX());// switching x and z!!!!
        orientation.setY((ter_orientation).getY());
        orientation.setZ((ter_orientation).getZ());
        orientation.setW((ter_orientation).getW());

        geometry_msgs.Pose pose = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Pose._TYPE);
        pose.setPosition(point);
        pose.setOrientation(orientation);
        fl.setPose(pose);

        if (publisher != null) {
            publisher.publish(fl);
        }
    }

    /**
     *
     * @return
     */
    @Override
    public Object getChartValue() {
        float[] bla = oro.getOrientation().toAngles(null);
        return new Vector3f(bla[0], bla[1], bla[2]);
    }

    /**
     *
     * @return
     */
    @Override
    public long getSleepTime() {
        return getRos_publish_rate();
    }
}
