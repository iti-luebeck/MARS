/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.actuators;

import com.jme3.scene.Geometry;
import org.ros.message.MessageListener;
import mars.SimState;

/**
 *
 * @author Thomas Tosik
 */
public class BrushlessThruster extends Thruster{
    /**
     * 
     * @param simauv
     * @param MassCenterGeom
     */
    public BrushlessThruster(SimState simstate,Geometry MassCenterGeom){
        super(simstate,MassCenterGeom);
        motor_increment = 0.6f;
    }

    /**
     *
     * @param simauv
     */
    public BrushlessThruster(SimState simstate){
        super(simstate);
        motor_increment = 0.6f;
    }

    /**
     * This is the function that represents the SeaBotix measured thruster force.
     * @param speed 
     * @return
     */
    @Override
    protected float calculateThrusterForce(int speed){
        return (Math.signum(speed))*(0.00046655f * (float)Math.pow((float)Math.abs(speed), 2.02039525f) );
    }
    
    @Override
    public void initROS(org.ros.node.Node ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        final BrushlessThruster self = this;
        ros_node.newSubscriber(auv_name + "/" + getPhysicalExchangerName(), "std_msgs/Int16",
          new MessageListener<org.ros.message.std_msgs.Int16>() {
            @Override
            public void onNewMessage(org.ros.message.std_msgs.Int16 message) {
              System.out.println("I heard: \"" + message.data + "\"");
              self.set_thruster_speed((int)message.data);
            }
          });
    }
}
