/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

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
 * Gives the exact position in world coordinates.
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Positionmeter extends Sensor{
    
    private Vector3f old_position = new Vector3f(0f,0f,0f);
    private Vector3f new_position = new Vector3f(0f,0f,0f);
    
    ///ROS stuff
    private Publisher<org.ros.message.geometry_msgs.PointStamped> publisher = null;
    private org.ros.message.geometry_msgs.PointStamped fl = new org.ros.message.geometry_msgs.PointStamped ();
    private org.ros.message.std_msgs.Header header = new org.ros.message.std_msgs.Header(); 
    
    /**
     * 
     */
    public Positionmeter(){
        super();
    }
        
    /**
     *
     * @param simstate 
     * @param pe
     */
    public Positionmeter(SimState simstate,PhysicalEnvironment pe){
        super(simstate);
        this.pe = pe;
    }

    /**
     * 
     * @param simstate 
     */
    public Positionmeter(SimState simstate){
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
        new_position = physics_control.getPhysicsLocation();//get the new velocity
        old_position = new_position.clone();
    }

    /**
     *
     * @return
     */
    public float getPositionX(){
        return new_position.x;
    }

    /**
     *
     * @return
     */
    public float getPositionY(){
        return new_position.y;
    }

    /**
     * 
     * @return
     */
    public float getPositionZ(){
        return new_position.z;
    }

    /**
     * 
     * @return
     */
    public Vector3f getPosition(){
        if(getNoise_type() == NoiseType.NO_NOISE){
            return getPositionRaw();
        }else if(getNoise_type() == NoiseType.UNIFORM_DISTRIBUTION){
            float noise = getUnifromDistributionNoise(getNoise_value());
            Vector3f noised = new Vector3f(getPositionRaw().x+((float)((1f/100f)*noise)),getPositionRaw().y+((float)((1f/100f)*noise)),getPositionRaw().z+((float)((1f/100f)*noise)));
            return noised;
        }else if(getNoise_type() == NoiseType.GAUSSIAN_NOISE_FUNCTION){
            float noise = getGaussianDistributionNoise(getNoise_value());
            Vector3f noised = new Vector3f(getPositionRaw().x+((float)((1f/100f)*noise)),getPositionRaw().y+((float)((1f/100f)*noise)),getPositionRaw().z+((float)((1f/100f)*noise)));
            return noised;
        }else{
            return getPositionRaw();
        }
    }

    /**
     *
     * @return
     */
    private Vector3f getPositionRaw(){
        return physics_control.getPhysicsLocation();
    }

    /**
     *
     */
    public void reset(){
        old_position = new Vector3f(0f,0f,0f);
        new_position = new Vector3f(0f,0f,0f);
    }
    
    /**
     * 
     * @param ros_node
     * @param auv_name
     * @deprecated
     */
    @Override
    @Deprecated
    public void initROS(org.ros.node.Node ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        publisher = ros_node.newPublisher(auv_name + "/" + this.getPhysicalExchangerName(), "geometry_msgs/PoseStamped");  
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
        org.ros.message.geometry_msgs.Point point = new org.ros.message.geometry_msgs.Point();
        point.x = getPosition().x;
        point.y = getPosition().z;
        point.z = getPosition().y;
        fl.point = point;      
        this.publisher.publish(fl);
    }
}
