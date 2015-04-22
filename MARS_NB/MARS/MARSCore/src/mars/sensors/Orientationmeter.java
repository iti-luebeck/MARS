/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
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
 * Gives the exact orientation of the auv.
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Orientationmeter extends Sensor{

    Quaternion new_orientation = new Quaternion();
    Quaternion old_orientation = new Quaternion();

    ///ROS stuff
    private Publisher<geometry_msgs.PoseStamped> publisher = null;
    private geometry_msgs.PoseStamped fl;
    private std_msgs.Header header;

    /**
     *
     */
    public Orientationmeter() {
        super();
    }

    /**
     *
     * @param simstate
     * @param pe
     */
    public Orientationmeter(SimState simstate, PhysicalEnvironment pe) {
        super(simstate);
        this.pe = pe;
    }

    /**
     *
     * @param simstate
     */
    public Orientationmeter(SimState simstate) {
        super(simstate);
    }

    /**
     *
     * @param sensor
     */
    public Orientationmeter(Orientationmeter sensor) {
        super(sensor);
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        Orientationmeter sensor = new Orientationmeter(this);
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
    public void update(float tpf) {
        new_orientation = physics_control.getPhysicsRotation();//get the new velocity
        old_orientation = new_orientation.clone();
    }

    /**
     *
     * @param addedOrientation
     */
    public void setAddedOrientation(Vector3f addedOrientation) {
        variables.put("addedOrientation", addedOrientation);
    }

    /**
     *
     * @return
     */
    public Vector3f getAddedOrientation() {
        return (Vector3f) variables.get("addedOrientation");
    }

    /**
     *
     * @return
     */
    public Quaternion getOrientation() {
        if (getNoiseType() == NoiseType.NO_NOISE) {
            return getOrientationRaw();
        } else if (getNoiseType() == NoiseType.UNIFORM_DISTRIBUTION) {
            float noise = getUnifromDistributionNoise(getNoiseValue());
            Quaternion noised = new Quaternion(getOrientationRaw().getX() + (((1f / 100f) * noise)), getOrientationRaw().getY() + (((1f / 100f) * noise)), getOrientationRaw().getY() + (((1f / 100f) * noise)), getOrientationRaw().getW() + (((1f / 100f) * noise)));
            return noised;
        } else if (getNoiseType() == NoiseType.GAUSSIAN_NOISE_FUNCTION) {
            float noise = getGaussianDistributionNoise(getNoiseValue());
            Quaternion noised = new Quaternion(getOrientationRaw().getX() + (((1f / 100f) * noise)), getOrientationRaw().getY() + (((1f / 100f) * noise)), getOrientationRaw().getZ() + (((1f / 100f) * noise)), getOrientationRaw().getW() + ( ((1f / 100f) * noise)));
            return noised;
        } else {
            return getOrientationRaw();
        }
    }

    /**
     *
     * @return
     */
    private Quaternion getOrientationRaw() {
        Quaternion quat = new Quaternion();
        quat.fromAngles(getAddedOrientation().getX(), getAddedOrientation().getY(), getAddedOrientation().getZ());
        return physics_control.getPhysicsRotation().mult(quat);
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
     */
    @Override
    @SuppressWarnings("unchecked")
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        publisher = (Publisher<geometry_msgs.PoseStamped>)ros_node.newPublisher(auv_name + "/" + this.getName(), geometry_msgs.PoseStamped._TYPE);
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

        geometry_msgs.Quaternion quat = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Quaternion._TYPE);
        quat.setX(getOrientation().getX());
        quat.setY(getOrientation().getY());
        quat.setZ(getOrientation().getZ());
        quat.setW(getOrientation().getW());

        geometry_msgs.Pose pose = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Pose._TYPE);
        pose.setOrientation(quat);
        fl.setPose(pose);

        if (publisher != null) {
            publisher.publish(fl);
        }
    }

    @Override
    public void publishData() {
        super.publishData();
        float[] bla = getOrientation().toAngles(null);
        MARSClientEvent clEvent = new MARSClientEvent(getAuv(), this, new Vector3f(bla[0], bla[1], bla[2]), System.currentTimeMillis());
        simState.getAuvManager().notifyAdvertisement(clEvent);
        AUVObjectEvent auvEvent = new AUVObjectEvent(this, new Vector3f(bla[0], bla[1], bla[2]), System.currentTimeMillis());
        notifyAdvertisementAUVObject(auvEvent);
    }
}
