/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.actuators;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import geometry_msgs.Point;
import geometry_msgs.Quaternion;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import mars.ros.MARSNodeMain;
import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;

/**
 * This class is basically a teleporter but since the hanse bag files for the 
 * estimated pose dont have the depth we have to make a special teleporter for
 * subscribing to the pressure sensor.
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class HanseBagPlayer extends Teleporter{
    
    private Vector3f pos2d = Vector3f.ZERO;
    private float depth = 0f;
    private com.jme3.math.Quaternion quat = com.jme3.math.Quaternion.IDENTITY;

    public void setDepth(float depth) {
        this.depth = depth;
    }

    public float getDepth() {
        return depth;
    }

    public void setPos2d(Vector3f pos2d) {
        this.pos2d = pos2d;
    }

    public Vector3f getPos2d() {
        return pos2d;
    }

    public com.jme3.math.Quaternion getQuat() {
        return quat;
    }

    public void setQuat(com.jme3.math.Quaternion quat) {
        this.quat = quat;
    }
    
   /**
     * 
     * @return
     */
    public int getPressureRelative() {
        return (Integer)variables.get("PressureRelative");
    }

    /**
     * 
     * @param ros_publish_rate
     */
    public void setPressureRelative(int PressureRelative) {
        variables.put("PressureRelative",PressureRelative);
    }
    
    /**
     * 
     * @param ros_node
     * @param auv_name
     */
    @Override
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        final HanseBagPlayer self = this;
        Subscriber<geometry_msgs.PoseStamped> subscriber = ros_node.newSubscriber(auv_name + "/" + getPhysicalExchangerName(), geometry_msgs.PoseStamped._TYPE);
        subscriber.addMessageListener(new MessageListener<geometry_msgs.PoseStamped>() {
                @Override
                public void onNewMessage(geometry_msgs.PoseStamped message) {
                    //System.out.println("I (" + getPhysicalExchangerName()+ ") heard: \"" + message.getPose().getPosition() + "\"");
                    
                    Point pos = (Point)message.getPose().getPosition();
                    Vector3f v_pos = new Vector3f((float)pos.getX(), (float)pos.getZ(), (float)pos.getY());
                    
                    //getting from ROS Co-S to MARS Co-S
                    Quaternion ori = (Quaternion)message.getPose().getOrientation();
                    com.jme3.math.Quaternion quat = new com.jme3.math.Quaternion((float)ori.getX(), (float)ori.getZ(), (float)ori.getY(), -(float)ori.getW());
                    com.jme3.math.Quaternion qrot = new com.jme3.math.Quaternion();
                    qrot.fromAngles(0f, FastMath.HALF_PI, 0f);
                    quat.multLocal(qrot);

                    v_pos.y = getDepth();
                    
                    setQuat(quat);
                    setPos2d(v_pos);
                    self.teleport(v_pos,quat);
                }
        });
        
        Subscriber<hanse_msgs.pressure> subscriberDepth = ros_node.newSubscriber(auv_name + "/" + "pressure/depth", hanse_msgs.pressure._TYPE);
        subscriberDepth.addMessageListener(new MessageListener<hanse_msgs.pressure>() {
                @Override
                public void onNewMessage(hanse_msgs.pressure message) {
                    float dep = 0f;
                    int pos = (int)message.getData();
                    
                    dep = (getPressureRelative()-pos)/(float)(pe.getFluid_density() * pe.getGravitational_acceleration()) * 100f;
                    setDepth(dep);
                }
        });
    }
}
