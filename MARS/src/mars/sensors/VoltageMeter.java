/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

import com.jme3.scene.Node;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import mars.NoiseType;
import mars.PhysicalEnvironment;
import mars.ros.MARSNodeMain;
import mars.states.SimState;
import org.ros.message.Time;
import org.ros.node.topic.Publisher;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlAccessorType(XmlAccessType.NONE)
public class VoltageMeter extends Sensor{

    ///ROS stuff
    private Publisher<org.ros.message.std_msgs.Float32> publisher = null;
    private org.ros.message.std_msgs.Float32 fl = new org.ros.message.std_msgs.Float32(); 
    private org.ros.message.std_msgs.Header header = new org.ros.message.std_msgs.Header(); 
    
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
        publisher = ros_node.newPublisher(auv_name + "/" + this.getPhysicalExchangerName(), "std_msgs/Float32");  
    }
        
    /**
     * 
     */
    @Override
    public void publish() {
        header.frame_id = this.getRos_frame_id();
        header.stamp = Time.fromMillis(System.currentTimeMillis());
        //fl.header = header;
        fl.data = getVoltage();
        this.publisher.publish(fl);
    }
}
