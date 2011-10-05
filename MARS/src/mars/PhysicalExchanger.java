/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;

/**
 * This is the basic interface for all sensors/actuators
 * @author Thomas Tosik
 */
public abstract class PhysicalExchanger extends Noise implements ROS{

    /**
     *
     * @param auv_node 
     */
    public abstract void init(Node auv_node);

    /**
     *
     */
    protected Node PhysicalExchanger_Node = new Node();
    /**
     *
     */
    protected Node auv_node;
    /**
     *
     */
    protected String PhysicalExchangerName = "";
    /**
     *
     */
    protected RigidBodyControl physics_control;
    /*
     * 
     */
    protected  boolean enabled = true;
    /*
     * 
     */
    protected int ros_publish_rate = 1000;
    /*
     * 
     */
    protected String ros_msg_type = "";
    /*
     * 
     */
    protected org.ros.node.Node ros_node = null;
    
    /**
     * 
     * @return
     */
    public RigidBodyControl getPhysicsControl() {
        return physics_control;
    }

    /**
     *
     * @param physics_control
     */
    public void setPhysicsControl(RigidBodyControl physics_control) {
        this.physics_control = physics_control;
    }

    /**
     *
     * @param visible
     */
    public void setNodeVisibility(boolean visible){
        if(visible){
            PhysicalExchanger_Node.setCullHint(CullHint.Never);
        }else{
            PhysicalExchanger_Node.setCullHint(CullHint.Always);
        }
    }

    /**
     *
     * @param name
     */
    public void setPhysicalExchangerName(String name){
        PhysicalExchangerName = name;
        PhysicalExchanger_Node.setName(name);
    }

    /**
     *
     * @return
     */
    public String getPhysicalExchangerName(){
        return PhysicalExchangerName;
    }

    /**
     * 
     */
    public abstract void reset();
    
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString(){
        return getPhysicalExchangerName();
    }
    
    public String getROS_MSG_Type() {
        return ros_msg_type;
    }

    public void initROS() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void initROS(org.ros.node.Node ros_node, String auv_name) {
        setROS_Node(ros_node);
    }

    public void setROS_MSG_Type(String ros_msg_type) {
        this.ros_msg_type = ros_msg_type;
    }

    public org.ros.node.Node getROS_Node() {
        return ros_node;
    }

    public void setROS_Node(org.ros.node.Node ros_node) {
        this.ros_node = ros_node;
    }
    
    public int getRos_publish_rate() {
        return ros_publish_rate;
    }

    public void setRos_publish_rate(int ros_publish_rate) {
        this.ros_publish_rate = ros_publish_rate;
    }
}
