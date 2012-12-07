/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import mars.NoiseType;
import mars.PhysicalEnvironment;
import mars.PhysicalExchanger;
import mars.ros.MARSNodeMain;
import mars.states.SimState;
import org.ros.message.Time;
import org.ros.node.topic.Publisher;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Transformer extends Sensor{
    
    
    ///ROS stuff
    private Publisher<tf.tfMessage> publisher = null;
    private tf.tfMessage fl;
    private geometry_msgs.TransformStamped tfs;
    private geometry_msgs.TransformStamped tfs2;
    private std_msgs.Header header; 
    private std_msgs.Header header2; 
    
    /**
     * 
     */
    public Transformer(){
        super();
    }
        
    /**
     *
     * @param simstate 
     * @param pe
     */
    public Transformer(SimState simstate,PhysicalEnvironment pe){
        super(simstate);
        this.pe = pe;
    }

    /**
     * 
     * @param simstate 
     */
    public Transformer(SimState simstate){
        super(simstate);
    }
    
    public Transformer(Transformer transformer){
        super(transformer);
    }

    @Override
    public PhysicalExchanger copy() {
        Transformer sensor = new Transformer(this);
        sensor.initAfterJAXB();
        return sensor;
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
    }
    
    /**
     * 
     * @param ros_node
     * @param auv_name
     */
    @Override
    public void initROS(MARSNodeMain ros_node, String auv_name) { 
        super.initROS(ros_node, auv_name);
        publisher = ros_node.newPublisher("/tf",tf.tfMessage._TYPE);  
        fl = this.mars_node.getMessageFactory().newFromType(tf.tfMessage._TYPE);
        tfs = this.mars_node.getMessageFactory().newFromType(geometry_msgs.TransformStamped._TYPE);
        tfs2 = this.mars_node.getMessageFactory().newFromType(geometry_msgs.TransformStamped._TYPE);
        header = this.mars_node.getMessageFactory().newFromType(std_msgs.Header._TYPE);
        header2 = this.mars_node.getMessageFactory().newFromType(std_msgs.Header._TYPE);
    }

    /**
     * 
     */
    @Override
    public void publish() {
        header.setSeq(rosSequenceNumber++);
        header.setFrameId("jme3");
        header.setStamp(Time.fromMillis(System.currentTimeMillis()));
        tfs.setHeader(header);
        
        geometry_msgs.Transform transform = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Transform._TYPE);
        
        geometry_msgs.Vector3 position = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Vector3._TYPE);
        position.setX(physics_control.getPhysicsLocation().getX());
        position.setY(physics_control.getPhysicsLocation().getY());
        position.setZ(physics_control.getPhysicsLocation().getZ());
        transform.setTranslation(position);
        
        geometry_msgs.Quaternion quat = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Quaternion._TYPE);
        quat.setX(physics_control.getPhysicsRotation().getX());
        quat.setY(physics_control.getPhysicsRotation().getY());
        quat.setZ(physics_control.getPhysicsRotation().getZ());
        quat.setW(physics_control.getPhysicsRotation().getW());
        transform.setRotation(quat);
        float[] angles = new float[3];
        //physics_control.getPhysicsRotation().toAngles(angles);
        //System.out.println("physics_control.getPhysicsRotation(): " + angles[0] + " " + angles[1] + " " + angles[2]);
                
        tfs.setTransform(transform);  
        
        tfs.setChildFrameId(getAuv().getName());
        
        
        
        //root
        header2.setSeq(rosSequenceNumber++);
        header2.setFrameId("ros");
        header2.setStamp(Time.fromMillis(System.currentTimeMillis()));
        tfs2.setHeader(header2);
        
        geometry_msgs.Transform transform2 = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Transform._TYPE);
        
        geometry_msgs.Vector3 position2 = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Vector3._TYPE);
        position2.setX(0f);
        position2.setY(0f);
        position2.setZ(0f);
        transform2.setTranslation(position2);
        
        geometry_msgs.Quaternion quat2 = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Quaternion._TYPE);
        Quaternion quat_jme = new Quaternion();
        quat_jme.fromAngles(0f,FastMath.HALF_PI,FastMath.HALF_PI);
        quat2.setX(quat_jme.getX());
        quat2.setY(quat_jme.getY());
        quat2.setZ(quat_jme.getZ());
        quat2.setW(quat_jme.getW());
        transform2.setRotation(quat2);
                
        tfs2.setTransform(transform2);  
        
        tfs2.setChildFrameId("jme3");
        
        List<geometry_msgs.TransformStamped> tfl = new ArrayList<geometry_msgs.TransformStamped>();
        tfl.add(tfs);
        tfl.add(tfs2);
        
        fl.setTransforms(tfl);
        
        if( publisher != null ){
            publisher.publish(fl);
        }
    }
}
