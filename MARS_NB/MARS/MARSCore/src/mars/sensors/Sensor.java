/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.rits.cloning.Cloner;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import mars.PhysicalEnvironment;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.MARS_Main;
import mars.ros.ROS_Publisher;
import mars.states.SimState;

/**
 * This is a basic sensors interface. Extend from here to make your own sensors
 * like an pressure sensor or light sensors.
 *
 * @author Thomas Tosik
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({Accelerometer.class, Gyroscope.class, InfraRedSensor.class, PingDetector.class, PressureSensor.class, SalinitySensor.class, TemperatureSensor.class, Velocimeter.class, VideoCamera.class, IMU.class, Positionmeter.class, Orientationmeter.class, Posemeter.class, TerrainSender.class, GPSReceiver.class, AmpereMeter.class, VoltageMeter.class, FlowMeter.class, PollutionMeter.class, Transformer.class, RayBasedSensor.class, CommunicationDevice.class})
public abstract class Sensor extends PhysicalExchanger implements ROS_Publisher {
    /*
     * 
     */

    /**
     *
     */
    protected SimState simState;
    /**
     *
     */
    protected MARS_Main mars;
    /**
     *
     */
    protected AssetManager assetManager;
    /**
     *
     */
    protected Node rootNode;
    /*
     * 
     */
    /**
     *
     */
    protected long time = 0;
    /**
     *
     */
    protected long tf_time = 0;

    /**
     *
     */
    protected Sensor() {
    }

    /**
     *
     * @param simstate
     */
    protected Sensor(SimState simstate) {
        setSimState(simstate);
        variables = new HashMap<String, Object>();
    }

    /**
     *
     * @param mars
     * @param pe
     */
    protected Sensor(MARS_Main mars, PhysicalEnvironment pe) {
        this.mars = mars;
        this.pe = pe;
        this.assetManager = mars.getAssetManager();
        this.rootNode = mars.getRootNode();
        variables = new HashMap<String, Object>();
    }

    /**
     *
     * @param sensor
     */
    public Sensor(Sensor sensor) {
        HashMap<String, Object> variablesOriginal = sensor.getAllVariables();
        Cloner cloner = new Cloner();
        variables = cloner.deepClone(variablesOriginal);

        HashMap<String, Object> noisevariablesOriginal = sensor.getAllNoiseVariables();
        noises = cloner.deepClone(noisevariablesOriginal);
    }

    /**
     *
     * @param simState
     */
    @Override
    public void setSimState(SimState simState) {
        this.simState = simState;
        this.mars = this.simState.getMARS();
        this.assetManager = this.mars.getAssetManager();
        this.rootNode = this.simState.getRootNode();
    }

    /**
     *
     * @param pe
     */
    @Override
    public void copyValuesFromPhysicalExchanger(PhysicalExchanger pe) {
        super.copyValuesFromPhysicalExchanger(pe);
    }

    /**
     *
     */
    @Override
    public void cleanup() {
        super.cleanup();
    }

    /**
     *
     */
    @Override
    public void publish() {
        if (tf_pub != null) {
            tf_pub.publishTF();
        }
    }

    /**
     *
     */
    @Override
    public void publishUpdate() {
        if (tf_pub != null) {
            tf_pub.publishTFUpdate();
        }
        long curtime = System.currentTimeMillis();
        if (((curtime - time) < getRos_publish_rate()) || (getRos_publish_rate() == 0)) {

        } else {
            time = curtime;
            if (mars_node != null && mars_node.isExisting() && rosinit) {
                //if(mars_node.isRunning()){
                publish();
                //}
            }
        }
    }
}
