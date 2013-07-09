/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.ros;

import java.util.ArrayList;
import java.util.List;
import mars.PhysicalExchanger;
import org.ros.message.Time;
import org.ros.node.topic.Publisher;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class TF_ROS_Publisher {
    ///ROS TF stuff
    private Publisher<tf.tfMessage> tf_publisher = null;
    private tf.tfMessage tf_fl = null;
    private geometry_msgs.TransformStamped tf_tfs = null;
    private std_msgs.Header tf_header = null; 
    
    /**
     * 
     */
    protected long tf_time = 0;
    private int rosSequenceNumber = 0;
    
    private PhysicalExchanger pe;
    
    public TF_ROS_Publisher(PhysicalExchanger pe){
        this.pe = pe;
    }
    
    public void initROS(MARSNodeMain ros_node, String auv_name) { 
        tf_publisher = ros_node.newPublisher("/tf",tf.tfMessage._TYPE);  
        tf_fl = pe.getMARS_Node().getMessageFactory().newFromType(tf.tfMessage._TYPE);
        tf_tfs = pe.getMARS_Node().getMessageFactory().newFromType(geometry_msgs.TransformStamped._TYPE);
        tf_header = pe.getMARS_Node().getMessageFactory().newFromType(std_msgs.Header._TYPE);    
    }
    
    /**
     * 
     */
    public void publishTF() {
        tf_header.setSeq(rosSequenceNumber++);
        tf_header.setFrameId(pe.getAuv().getName());
        tf_header.setStamp(Time.fromMillis(System.currentTimeMillis()));
        tf_tfs.setHeader(tf_header);
        
        geometry_msgs.Transform transform = pe.getMARS_Node().getMessageFactory().newFromType(geometry_msgs.Transform._TYPE);
        
        geometry_msgs.Vector3 position = pe.getMARS_Node().getMessageFactory().newFromType(geometry_msgs.Vector3._TYPE);
        position.setX(pe.getTFPosition().getX());
        position.setY(pe.getTFPosition().getY());
        position.setZ(pe.getTFPosition().getZ());
        transform.setTranslation(position);
        
        geometry_msgs.Quaternion quat = pe.getMARS_Node().getMessageFactory().newFromType(geometry_msgs.Quaternion._TYPE);
        quat.setX(pe.getTFOrientation().getX());
        quat.setY(pe.getTFOrientation().getY());
        quat.setZ(pe.getTFOrientation().getZ());
        quat.setW(pe.getTFOrientation().getW());
        transform.setRotation(quat);
                
        tf_tfs.setTransform(transform);  
        
        tf_tfs.setChildFrameId(pe.getPhysicalExchangerName());
        
        List<geometry_msgs.TransformStamped> tfl = new ArrayList<geometry_msgs.TransformStamped>();
        tfl.add(tf_tfs);
        
        tf_fl.setTransforms(tfl);
        
        if( tf_publisher != null ){
            tf_publisher.publish(tf_fl);
        }
    }

    /**
     * 
     */
    public void publishTFUpdate() {
        long curtime = System.currentTimeMillis();
        if( ((curtime-tf_time) < pe.getTFRos_publish_rate()) || (pe.getTFRos_publish_rate() == 0) ){
            
        }else{
            tf_time = curtime;
            if(pe.getMARS_Node() != null && pe.getMARS_Node().isExisting()){
                //if(mars_node.isRunning()){
                    publishTF();
                //}
            }
        }
    }
}
