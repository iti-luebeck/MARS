/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.actuators;

import com.jme3.scene.Geometry;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import org.ros.message.MessageListener;
import mars.SimState;
import mars.ros.MARSNodeMain;

/**
 * This class represents the SeaBotix Thrusters.
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class SeaBotixThruster extends Thruster{

    /**
     * 
     */
    public SeaBotixThruster(){
        super();
        motor_increment = 5f;
    }
    
    /**
     * 
     * @param simstate 
     * @param MassCenterGeom
     */
    public SeaBotixThruster(SimState simstate,Geometry MassCenterGeom){
        super(simstate,MassCenterGeom);
        motor_increment = 5f;
    }

    /**
     *
     * @param simstate 
     */
    public SeaBotixThruster(SimState simstate){
        super(simstate);
        motor_increment = 5f;
    }

    /**
     * This is the function that represents the SeaBotix measured thruster force.
     * @param speed 
     * @return
     */
    @Override
    protected float calculateThrusterForce(int speed){
        //return (Math.signum(speed))*(0.16f * (float)Math.pow(1.04f, (float)Math.abs(speed)) );
        int limited_speed = (speed <= 127) ? speed : 127;
        return (Math.signum(limited_speed))*(0.00046655f * (float)Math.pow((float)Math.abs(limited_speed), 2.02039525f) );
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
        final SeaBotixThruster self = this;
        ros_node.newSubscriber(auv_name + "/" + getPhysicalExchangerName(), "std_msgs/Int16",
          new MessageListener<org.ros.message.std_msgs.Int16>() {
            @Override
            public void onNewMessage(org.ros.message.std_msgs.Int16 message) {
              System.out.println("I heard: \"" + message.data + "\"");
              self.set_thruster_speed((int)message.data);
            }
          });
    }
    
    /**
     * 
     * @param ros_node
     * @param auv_name
     */
    @Override
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        final SeaBotixThruster self = this;
        ros_node.newSubscriber(auv_name + "/" + getPhysicalExchangerName(), "hanse_msgs/sollSpeed",
          new MessageListener<org.ros.message.hanse_msgs.sollSpeed>() {
            @Override
            public void onNewMessage(org.ros.message.hanse_msgs.sollSpeed message) {
              //System.out.println("I heard: \"" + message.data + "\"");
              self.set_thruster_speed((int)message.data);
            }
          });
    }
}
