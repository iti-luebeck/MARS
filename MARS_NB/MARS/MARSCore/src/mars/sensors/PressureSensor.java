/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * This class provides a basic pressure sensor. You can get exact depth or exact
 * pressure + noise.
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class PressureSensor extends Sensor{

    private Geometry PressureSensorStart;

    ///ROS stuff 
    private Publisher<hanse_msgs.pressure> publisher = null;
    private hanse_msgs.pressure fl;
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
            return getRawDepth() + ( ((1f / 100f) * noise));
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
        publisher = (Publisher<hanse_msgs.pressure>)ros_node.newPublisher(auv_name + "/" + this.getName(), hanse_msgs.pressure._TYPE);
        fl = this.mars_node.getMessageFactory().newFromType(hanse_msgs.pressure._TYPE);
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
        fl.setData((short) (getPressureMbar()));
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
