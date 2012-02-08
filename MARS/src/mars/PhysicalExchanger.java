/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars;

import mars.states.SimState;
import mars.ros.ROS;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import mars.actuators.Actuator;
import mars.ros.MARSNodeMain;
import mars.sensors.Sensor;

/**
 * This is the basic interface for all sensors/actuators
 * @author Thomas Tosik
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso( {Actuator.class,Sensor.class} )
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
    @XmlElement(name="name")
    protected String PhysicalExchangerName = "";
    /**
     *
     */
    protected RigidBodyControl physics_control;
    /*
     * 
     */
    /**
     * 
     */
    @XmlElement
    protected  boolean enabled = true;
    /*
     * 
     */
    /**
     * 
     */
    @XmlElement
    protected int ros_publish_rate = 1000;
    /**
     * 
     */
    @XmlElement
    protected String ros_frame_id = "/map";
    /*
     * 
     */
    /**
     * 
     */
    protected String ros_msg_type = "";
    /*
     * 
     */
    /**
     * 
     * @deprecated
     */
    @Deprecated
    protected org.ros.node.Node ros_node = null;
    
    /*
     * 
     */
    /**
     * 
     */
    protected MARSNodeMain mars_node = null;
    
    /*
     * 
     */
    /**
     * 
     */
    protected  SimState simState = null;
    
    /**
     * 
     * @param simState
     */
    public void setSimState(SimState simState) {
        this.simState = simState;
    }
    
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
    
    /**
     * 
     * @return
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString(){
        return getPhysicalExchangerName();
    }
    
    /**
     * 
     * @return
     */
    public String getROS_MSG_Type() {
        return ros_msg_type;
    }

    /**
     * 
     */
    public void initROS() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * 
     * @param ros_node
     * @param auv_name
     */
    public void initROS(org.ros.node.Node ros_node, String auv_name) {
        setROS_Node(ros_node);
    }
    
    /**
     * 
     * @param ros_node
     * @param auv_name
     */
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        setROS_Node(ros_node);
    }

    /**
     * 
     * @param ros_msg_type
     */
    public void setROS_MSG_Type(String ros_msg_type) {
        this.ros_msg_type = ros_msg_type;
    }

    /**
     * 
     * @return
     * @deprecated
     */
    @Deprecated
    public org.ros.node.Node getROS_Node() {
        return ros_node;
    }

    /**
     * 
     * @param ros_node
     * @deprecated
     */
    @Deprecated
    public void setROS_Node(org.ros.node.Node ros_node) {
        this.ros_node = ros_node;
    }
    
    /**
     * 
     * @return
     */
    public MARSNodeMain getMARS_Node() {
        return mars_node;
    }
    
    /**
     * 
     * @param ros_node
     */
    public void setROS_Node(MARSNodeMain ros_node) {
        this.mars_node = ros_node;
    }
    
    /**
     * 
     * @return
     */
    public int getRos_publish_rate() {
        return ros_publish_rate;
    }

    /**
     * 
     * @param ros_publish_rate
     */
    public void setRos_publish_rate(int ros_publish_rate) {
        this.ros_publish_rate = ros_publish_rate;
    }

    public String getRos_frame_id() {
        return ros_frame_id;
    }

    public void setRos_frame_id(String ros_frame_id) {
        this.ros_frame_id = ros_frame_id;
    }
    
    /**
     *
     * @param tpf
     */
    public abstract void update(float tpf);
}
