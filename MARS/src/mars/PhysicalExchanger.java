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
import java.util.HashMap;
import javax.swing.tree.TreePath;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.actuators.Actuator;
import mars.ros.MARSNodeMain;
import mars.sensors.Sensor;
import mars.xml.HashMapAdapter;

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
    
    @XmlJavaTypeAdapter(HashMapAdapter.class)
    protected HashMap<String,Object> variables;

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
    /**
     * 
     */
    protected PhysicalEnvironment pe;
    /*
     * 
     */
    /**
     * 
     */
    protected  boolean enabled = true;
    /*
     * 
     */
    /**
     * 
     */
    protected int ros_publish_rate = 1000;
    /**
     * 
     */
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
        //PhysicalExchangerName = name;
        variables.put("name", name);
        PhysicalExchanger_Node.setName(name);
    }

    /**
     *
     * @return
     */
    public String getPhysicalExchangerName(){
        return (String)variables.get("name");
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
        return (Boolean)variables.get("enabled");
    }

    /**
     * 
     * @param enabled
     */
    public void setEnabled(boolean enabled) {
        variables.put("enabled", enabled);
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
        return (Integer)variables.get("ros_publish_rate");
    }

    /**
     * 
     * @param ros_publish_rate
     */
    public void setRos_publish_rate(int ros_publish_rate) {
        variables.put("ros_publish_rate",ros_publish_rate);
    }

    public String getRos_frame_id() {       
        return (String)variables.get("ros_frame_id");
    }

    public void setRos_frame_id(String ros_frame_id) {
        //this.ros_frame_id = ros_frame_id;
        variables.put("ros_frame_id",ros_frame_id);
    }
    
    /**
     *
     * @param tpf
     */
    public abstract void update(float tpf);
    
    /**
     *
     * @return
     */
    public PhysicalEnvironment getPhysical_environment() {
        return pe;
    }

    /**
     *
     * @param pe 
     */
    public void setPhysical_environment(PhysicalEnvironment pe) {
        this.pe = pe;
    }
    
    public HashMap<String,Object> getAllVariables(){
        return variables;
    }
    
    public HashMap<String,String> getAllActions(){
        return null;
    }
    
    public void initAfterJAXB(){
       /* variables.put("noise_type", getNoise_type());
        variables.put("noise_value", getNoise_value());
        variables.put("name",getPhysicalExchangerName());
        variables.put("enabled", isEnabled());
        variables.put("ros_publish_rate", getRos_publish_rate());
        variables.put("ros_frame_id", getRos_frame_id());*/
    };
    
    public String getIcon(){
        return (String)variables.get("icon");
    }
    
    public String getIconDND(){
        return (String)variables.get("dnd_icon");
    }
    
    public void setIcon(String icon){
        variables.put("icon",icon);
    }
    
    public void setIconDND(String dnd_icon){
        variables.put("dnd_icon",dnd_icon);
    }
    
    public abstract void updateState(TreePath path);
}
