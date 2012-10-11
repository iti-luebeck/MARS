/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

import com.jme3.scene.Node;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.NoiseType;
import mars.PhysicalEnvironment;
import mars.auv.AUV;
import mars.ros.MARSNodeMain;
import mars.states.SimState;
import mars.xml.HashMapAdapter;
import org.ros.message.Time;
import org.ros.node.topic.Publisher;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlAccessorType(XmlAccessType.NONE)
public class AmpereMeter extends Sensor{

    ///ROS stuff
    private Publisher<std_msgs.Float32> publisher = null;
    private std_msgs.Float32 fl;
    private std_msgs.Header header; 
    
    @XmlJavaTypeAdapter(HashMapAdapter.class)
    protected HashMap<String,String> accumulators;
    
    private AUV auv;
    
    /**
     * 
     */
    public AmpereMeter(){
        super();
    }
    
     /**
     *
     * @param simstate 
      * @param pe
     */
    public AmpereMeter(SimState simstate, PhysicalEnvironment pe){
        super(simstate);
        this.pe = pe;
    }

    /**
     *
     * @param simstate 
     */
    public AmpereMeter(SimState simstate){
        super(simstate);
    }

    public void update(float tpf){

    }

    public void init(Node auv_node){
        this.auv_node = auv_node;
    }
    
    /**
     * 
     * @return
     */
    public HashMap<String,String> getAccumulators(){
        return accumulators;
    }

    public void setAuv(AUV auv) {
        this.auv = auv;
    }
    
     /**
     *
     * @return The exact temperature of the current auv enviroemnt in C°
     */
    public double getAmpere(){
        if(getNoise_type() == NoiseType.NO_NOISE){
            return getAmpereRaw();
        }else if(getNoise_type() == NoiseType.UNIFORM_DISTRIBUTION){
            float noise = getUnifromDistributionNoise(getNoise_value());
            return getAmpereRaw()+((float)((1f/100f)*noise));
        }else if(getNoise_type() == NoiseType.GAUSSIAN_NOISE_FUNCTION){
            float noise = getGaussianDistributionNoise(getNoise_value());
            return getAmpereRaw() + ((float)((1f/100f)*noise));
        }else{
            return getAmpereRaw();
        }
    }

     /**
     *
      * @param noise The boundary for the random generator starting always from 0 to noise value
      * @return The Temperature of the current auv enviroment with a random noise from 0 to noise value in C°
     */
    private double getAmpereRaw(){
        HashMap<String, String> accus = getAccumulators();
        double capacity = 0f;
        for ( String elem : accus.keySet() ){
            String element = (String)accus.get(elem);
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
        publisher = ros_node.newPublisher(auv_name + "/" + this.getPhysicalExchangerName(),std_msgs.Float32._TYPE);  
        fl = this.mars_node.getMessageFactory().newFromType(std_msgs.Float32._TYPE);
        header = this.mars_node.getMessageFactory().newFromType(std_msgs.Header._TYPE);
    }
        
    /**
     * 
     */
    @Override
    public void publish() {
        header.setSeq(rosSequenceNumber++);
        header.setFrameId(this.getRos_frame_id());
        header.setStamp(Time.fromMillis(System.currentTimeMillis()));
        fl.setData((float)getAmpere());
        
        if( publisher != null ){
            publisher.publish(fl);
        }
    }
}
