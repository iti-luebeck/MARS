/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.sensors;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import org.ros.node.topic.Publisher;
import mars.NoiseType;
import mars.states.SimState;
import mars.ros.MARSNodeMain;

/**
 * This a basic gyroscope class. It gives you the Angular velocity.
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Gyroscope extends Sensor{

    ///ROS stuff
    private Publisher<org.ros.message.std_msgs.Float32> publisher = null;
    private org.ros.message.std_msgs.Float32 fl = new org.ros.message.std_msgs.Float32(); 
    
    /**
     * 
     */
    public Gyroscope(){
        super();
    }
        
    /**
     *
     * @param simstate 
     */
    public Gyroscope(SimState simstate){
        super(simstate);
    }

    public void update(float tpf){

    }

    /**
     * 
     */
    public void init(Node auv_node){
        this.auv_node = auv_node;
    }

    /**
     *
     * @return
     */
    public Vector3f getAngularVelocity(){
        if(getNoise_type() == NoiseType.NO_NOISE){
            return getAngularVelocityRaw();
        }else if(getNoise_type() == NoiseType.UNIFORM_DISTRIBUTION){
            float noise = getUnifromDistributionNoise(getNoise_value());
            Vector3f noised = new Vector3f(getAngularVelocityRaw().x+((float)((1f/100f)*noise)),getAngularVelocityRaw().y+((float)((1f/100f)*noise)),getAngularVelocityRaw().z+((float)((1f/100f)*noise)));
            return noised;
        }else if(getNoise_type() == NoiseType.GAUSSIAN_NOISE_FUNCTION){
            float noise = getGaussianDistributionNoise(getNoise_value());
            Vector3f noised = new Vector3f(getAngularVelocityRaw().x+((float)((1f/100f)*noise)),getAngularVelocityRaw().y+((float)((1f/100f)*noise)),getAngularVelocityRaw().z+((float)((1f/100f)*noise)));
            return noised;
        }else{
            return getAngularVelocityRaw();
        }
    }

    /**
     * in radiant
     * @return
     */
    private Vector3f getAngularVelocityRaw(){
        return physics_control.getAngularVelocity();
    }

    /**
     * in radiant
     * @return
     */
    public float getAngularVelocityXAxis(){
        return physics_control.getAngularVelocity().x;
    }

    /**
     * in radiant
     * @return
     */
    public float getAngularVelocityYAxis(){
        return physics_control.getAngularVelocity().y;
    }

    /**
     * in radiant
     * @return
     */
    public float getAngularVelocityZAxis(){
        return physics_control.getAngularVelocity().z;
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
        fl.data = getAngularVelocity().length();
        this.publisher.publish(fl);
    }
}
