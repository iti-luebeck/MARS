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
import org.ros.node.topic.Subscriber;

/**
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Dynamixel_AX12PLUS extends Servo{
    
    /**
     * 
     */
    public Dynamixel_AX12PLUS(){
        super();
    }
    
    /**
     * 
     * @param simstate
     * @param MassCenterGeom
     */
    public Dynamixel_AX12PLUS(SimState simstate,Geometry MassCenterGeom) {
        super(simstate,MassCenterGeom);
        setOperatingAngle(5.235987f);
        setResolution(0.005061f);
        setSpeedPerDegree(0.003266f);
    }

    /**
     * 
     * @param simstate
     */
    public Dynamixel_AX12PLUS(SimState simstate) {
        super(simstate);
        setOperatingAngle(5.235987f);
        setResolution(0.005061f);
        setSpeedPerDegree(0.003266f);
    }
    
    public Dynamixel_AX12PLUS(Dynamixel_AX12PLUS servo){
        super(servo);
    }

    @Override
    public Dynamixel_AX12PLUS copy() {
        Dynamixel_AX12PLUS actuator = new Dynamixel_AX12PLUS(this);
        actuator.initAfterJAXB();
        return actuator;
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
        Subscriber<std_msgs.Float64> subscriber = ros_node.newSubscriber(auv_name + "/" + getPhysicalExchangerName(), std_msgs.Float64._TYPE);
        subscriber.addMessageListener(new MessageListener<std_msgs.Float64>() {
                @Override
                public void onNewMessage(std_msgs.Float64 message) {
                    System.out.println("I (" + getPhysicalExchangerName()+ ") heard: \"" + message.getData() + "\"");
                    self.setDesiredAnglePosition((double)message.getData());
                }
        });
    }
}
