/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

import com.jme3.scene.Node;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import mars.ChartValue;
import mars.NoiseType;
import mars.PhysicalEnvironment;
import mars.PhysicalExchanger;
import mars.ros.MARSNodeMain;
import mars.server.MARSClientEvent;
import mars.states.SimState;
import org.ros.message.Time;
import org.ros.node.topic.Publisher;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlAccessorType(XmlAccessType.NONE)
public class VoltageMeter extends Sensor implements ChartValue{

    ///ROS stuff
    private Publisher<std_msgs.Float32> publisher = null;
    private std_msgs.Float32 fl;
    private std_msgs.Header header; 
    
    /**
     * 
     */
    public VoltageMeter(){
        super();
    }
    
     /**
     *
     * @param simstate 
      * @param pe
     */
    public VoltageMeter(SimState simstate, PhysicalEnvironment pe){
        super(simstate);
        this.pe = pe;
    }

    /**
     *
     * @param simstate 
     */
    public VoltageMeter(SimState simstate){
        super(simstate);
    }
    
    public VoltageMeter(VoltageMeter sensor){
        super(sensor);
    }

    @Override
    public PhysicalExchanger copy() {
        VoltageMeter sensor = new VoltageMeter(this);
        sensor.initAfterJAXB();
        return sensor;
    }

    public void update(float tpf){

    }

    public void init(Node auv_node){
        this.auv_node = auv_node;
    }

     /**
     *
     * @return The exact temperature of the current auv enviroemnt in C°
     */
    public float getVoltage(){
        if(getNoise_type() == NoiseType.NO_NOISE){
            return getVoltageRaw();
        }else if(getNoise_type() == NoiseType.UNIFORM_DISTRIBUTION){
            float noise = getUnifromDistributionNoise(getNoise_value());
            return getVoltageRaw()+((float)((1f/100f)*noise));
        }else if(getNoise_type() == NoiseType.GAUSSIAN_NOISE_FUNCTION){
            float noise = getGaussianDistributionNoise(getNoise_value());
            return getVoltageRaw() + ((float)((1f/100f)*noise));
        }else{
            return getVoltageRaw();
        }
    }

     /**
     *
      * @param noise The boundary for the random generator starting always from 0 to noise value
      * @return The Temperature of the current auv enviroment with a random noise from 0 to noise value in C°
     */
    private float getVoltageRaw(){
        return pe.getFluid_temp();
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
    public void reset(){

    }

    /**
     * 
     * @param ros_node
     * @param auv_name
     */
    @Override
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        publisher = ros_node.newPublisher(auv_name + "/" + this.getName(),std_msgs.Float32._TYPE);  
        fl = this.mars_node.getMessageFactory().newFromType(std_msgs.Float32._TYPE);
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
        fl.setData(getVoltage());
        
        if( publisher != null ){
            publisher.publish(fl);
        }
    }
    
    @Override
    public void publishData() {
        super.publishData();
        MARSClientEvent clEvent = new MARSClientEvent(getAuv(), this, getVoltage(), System.currentTimeMillis());
        simState.getAuvManager().notifyAdvertisement(clEvent);
    }
    
    @Override
    public Object getChartValue() {
        return getVoltage();
    }

    @Override
    public long getSleepTime() {
        return getRos_publish_rate();
    }
}
