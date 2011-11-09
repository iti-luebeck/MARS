/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.ros;

/**
 *
 * @author Thomas Tosik
 */
public interface ROS {
    
    public String ros_msg_type = "";
    public org.ros.node.Node ros_node = null;
    /*
     * 
     */
    public void initROS();
    /*
     * 
     */
    public void initROS(org.ros.node.Node ros_node, String auv_name);
    /*
     * 
     */
    public void setROS_MSG_Type(String ros_msg_type);
    /*
     * 
     */
    public String getROS_MSG_Type();
    /*
     * 
     */
    public void setROS_Node(org.ros.node.Node ros_node);
    /*
     * 
     */
    public org.ros.node.Node getROS_Node();
}
