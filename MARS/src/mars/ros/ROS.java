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
    
    /**
     * 
     */
    public String ros_msg_type = "";
    /**
     * 
     */
    public MARSNodeMain mars_node = null;
    /**
     * 
     */
    public void initROS();
    /**
     * 
     * @param ros_node
     * @param auv_name
     */
    public void initROS(MARSNodeMain ros_node, String auv_name);
    /**
     * 
     * @param ros_msg_type
     */
    public void setROS_MSG_Type(String ros_msg_type);
    /**
     * 
     * @return
     */
    public String getROS_MSG_Type();
    /**
     * 
     * @param ros_node
     */
    public void setROS_Node(MARSNodeMain ros_node);
    /**
     * 
     * @return
     */
    public MARSNodeMain getMARS_Node();
}
