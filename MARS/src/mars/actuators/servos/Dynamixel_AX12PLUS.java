/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.actuators.servos;

import com.jme3.scene.Geometry;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import mars.states.SimState;
import mars.ros.MARSNodeMain;
import org.ros.message.MessageListener;

/**
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Dynamixel_AX12PLUS extends Servo{
    
    public Dynamixel_AX12PLUS(){
        super();
        OperatingAngle = 5.235987f;
        Resolution = 0.005061f;
        SpeedPerDegree = 0.003266f;
    }
    
    public Dynamixel_AX12PLUS(SimState simstate,Geometry MassCenterGeom) {
        super(simstate,MassCenterGeom);
        OperatingAngle = 5.235987f;
        Resolution = 0.005061f;
        SpeedPerDegree = 0.003266f;
    }

    public Dynamixel_AX12PLUS(SimState simstate) {
        super(simstate);
        OperatingAngle = 5.235987f;
        Resolution = 0.005061f;
        SpeedPerDegree = 0.003266f;
    }
    
        /**
     * 
     * @param ros_node
     * @param auv_name
     */
    @Override
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        final Servo self = this;
        ros_node.newSubscriber(auv_name + "/" + getPhysicalExchangerName(), "smart_e_msgs/servo",
          new MessageListener<org.ros.message.smart_e_msgs.servo>() {
            @Override
            public void onNewMessage(org.ros.message.smart_e_msgs.servo message) {
              self.setDesiredAnglePosition((int)message.data);
            }
          });
    }
}
