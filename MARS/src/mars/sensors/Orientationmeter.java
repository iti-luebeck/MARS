/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
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
 * Gives the exact orientation of the auv.
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Orientationmeter extends Sensor{
    
    Quaternion new_orientation = new Quaternion();
    Quaternion old_orientation = new Quaternion();
        
    ///ROS stuff
    private Publisher<org.ros.message.geometry_msgs.PoseStamped> publisher = null;
    private org.ros.message.geometry_msgs.PoseStamped fl = new org.ros.message.geometry_msgs.PoseStamped ();
    private org.ros.message.std_msgs.Header header = new org.ros.message.std_msgs.Header(); 
    
    /**
     * 
     */
    public Orientationmeter(){
        super();
    }
        
    /**
     *
     * @param simstate 
     * @param pe
     */
    public Orientationmeter(SimState simstate,PhysicalEnvironment pe){
        super(simstate);
        this.pe = pe;
    }

    /**
     * 
     * @param simstate 
     */
    public Orientationmeter(SimState simstate){
        super(simstate);
    }

    /**
     *
     */
    public void init(Node auv_node){
        this.auv_node = auv_node;
    }

    /**
     *
     * @param tpf
     */
    public void update(float tpf){
        new_orientation = physics_control.getPhysicsRotation();//get the new velocity
        old_orientation = new_orientation.clone();
    }

    /**
     * 
     * @return
     */
    public Quaternion getOrientation(){
        if(getNoise_type() == NoiseType.NO_NOISE){
            return getOrientationRaw();
        }else if(getNoise_type() == NoiseType.UNIFORM_DISTRIBUTION){
            float noise = getUnifromDistributionNoise(getNoise_value());
            Quaternion noised = new Quaternion(getOrientationRaw().getX()+((float)((1f/100f)*noise)),getOrientationRaw().getY()+((float)((1f/100f)*noise)),getOrientationRaw().getY()+((float)((1f/100f)*noise)),getOrientationRaw().getW()+((float)((1f/100f)*noise)));
            return noised;
        }else if(getNoise_type() == NoiseType.GAUSSIAN_NOISE_FUNCTION){
            float noise = getGaussianDistributionNoise(getNoise_value());
            Quaternion noised = new Quaternion(getOrientationRaw().getX()+((float)((1f/100f)*noise)),getOrientationRaw().getY()+((float)((1f/100f)*noise)),getOrientationRaw().getZ()+((float)((1f/100f)*noise)),getOrientationRaw().getW()+((float)((1f/100f)*noise)));
            return noised;
        }else{
            return getOrientationRaw();
        }
    }

    /**
     *
     * @return
     */
    private Quaternion getOrientationRaw(){
        return new_orientation;
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
        publisher = ros_node.newPublisher(auv_name + "/" + this.getPhysicalExchangerName(), "geometry_msgs/PoseStamped");  
    }

    /**
     * 
     */
    @Override
    public void publish() {
        header.frame_id = this.getRos_frame_id();
        header.stamp = Time.fromMillis(System.currentTimeMillis());
        fl.header = header;
        org.ros.message.geometry_msgs.Quaternion quat = new org.ros.message.geometry_msgs.Quaternion();
        quat.x = getOrientation().getX();
        quat.y = getOrientation().getY();
        quat.z = getOrientation().getZ();
        quat.w = getOrientation().getW();
        org.ros.message.geometry_msgs.Pose pose = new org.ros.message.geometry_msgs.Pose();
        pose.orientation = quat;
        fl.pose = pose;      
        this.publisher.publish(fl);
    }    
}