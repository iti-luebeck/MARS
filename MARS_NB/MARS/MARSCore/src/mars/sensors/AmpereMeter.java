/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

import com.jme3.scene.Node;
import com.rits.cloning.Cloner;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.misc.ChartValue;
import mars.Helper.NoiseType;
import mars.PhysicalEnvironment;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.auv.AUV;
import mars.ros.MARSNodeMain;
import mars.server.MARSClientEvent;
import mars.states.SimState;
import mars.xml.HashMapAdapter;
import org.ros.message.Time;
import org.ros.node.topic.Publisher;

/**
 * Can measure how much ampere is left in an accumulator.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlAccessorType(XmlAccessType.NONE)
public class AmpereMeter extends Sensor implements ChartValue {

    ///ROS stuff
    private Publisher<hanse_msgs.Ampere> publisher = null;
    private hanse_msgs.Ampere fl;
    private std_msgs.Header header;

    /**
     *
     */
    @XmlJavaTypeAdapter(HashMapAdapter.class)
    protected HashMap<String, String> accumulators;

    /**
     *
     */
    public AmpereMeter() {
        super();
    }

    /**
     *
     * @param simstate
     * @param pe
     */
    public AmpereMeter(SimState simstate, PhysicalEnvironment pe) {
        super(simstate);
        this.pe = pe;
    }

    /**
     *
     * @param simstate
     */
    public AmpereMeter(SimState simstate) {
        super(simstate);
    }

    /**
     *
     * @param ampereMeter
     */
    public AmpereMeter(AmpereMeter ampereMeter) {
        super(ampereMeter);

        //dont forget to clone accus hashmap
        HashMap<String, String> accumulatorsOriginal = ampereMeter.getAccumulators();
        Cloner cloner = new Cloner();
        accumulators = cloner.deepClone(accumulatorsOriginal);
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        AmpereMeter sensor = new AmpereMeter(this);
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
     * @param pe
     */
    @Override
    public void copyValuesFromPhysicalExchanger(PhysicalExchanger pe) {
        super.copyValuesFromPhysicalExchanger(pe);
        if (pe instanceof AmpereMeter) {
            HashMap<String, String> accumulatorsOriginal = ((AmpereMeter) pe).getAccumulators();
            Cloner cloner = new Cloner();
            accumulators = cloner.deepClone(accumulatorsOriginal);
        }
    }

    /**
     *
     * @return
     */
    public HashMap<String, String> getAccumulators() {
        return accumulators;
    }

    /**
     *
     * @param auv
     */
    public void setAuv(AUV auv) {
        this.auv = auv;
    }

    /**
     *
     * @return The exact temperature of the current auv enviroemnt in C°
     */
    public double getAmpere() {
        if (getNoiseType() == NoiseType.NO_NOISE) {
            return getAmpereRaw();
        } else if (getNoiseType() == NoiseType.UNIFORM_DISTRIBUTION) {
            float noise = getUnifromDistributionNoise(getNoiseValue());
            return getAmpereRaw() + ((float) ((1f / 100f) * noise));
        } else if (getNoiseType() == NoiseType.GAUSSIAN_NOISE_FUNCTION) {
            float noise = getGaussianDistributionNoise(getNoiseValue());
            return getAmpereRaw() + ((float) ((1f / 100f) * noise));
        } else {
            return getAmpereRaw();
        }
    }

    /**
     *
     * @param noise The boundary for the random generator starting always from 0
     * to noise value
     * @return The Temperature of the current auv enviroment with a random noise
     * from 0 to noise value in C°
     */
    private double getAmpereRaw() {
        HashMap<String, String> accus = getAccumulators();
        double capacity = 0f;
        for (String elem : accus.keySet()) {
            String element = (String) accus.get(elem);
            capacity = capacity + auv.getAccumulator(element).getActualCurrent();
        }
        return capacity;
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
     */
    @Override
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        publisher = ros_node.newPublisher(auv_name + "/" + this.getName(), hanse_msgs.Ampere._TYPE);
        fl = this.mars_node.getMessageFactory().newFromType(hanse_msgs.Ampere._TYPE);
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

        fl.setAmpere(getAmpere());

        if (publisher != null) {
            publisher.publish(fl);
        }
    }

    @Override
    public void publishData() {
        super.publishData();
        MARSClientEvent clEvent = new MARSClientEvent(getAuv(), this, getAmpere(), System.currentTimeMillis());
        simState.getAuvManager().notifyAdvertisement(clEvent);
    }

    /**
     *
     * @return
     */
    @Override
    public Object getChartValue() {
        return (float) getAmpere();
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
