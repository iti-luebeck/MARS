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
public class Modelcraft_ES07 extends Servo{
    
    /**
     * 
     */
    public Modelcraft_ES07(){
        super();
    }
    
    /**
     * 
     * @param simstate
     * @param MassCenterGeom
     */
    public Modelcraft_ES07(SimState simstate,Geometry MassCenterGeom) {
        super(simstate,MassCenterGeom);
        setOperatingAngle(5.235987f);
        setResolution(0.005061f);
        setSpeedPerDegree(0.003266f);
    }

    /**
     * 
     * @param simstate
     */
    public Modelcraft_ES07(SimState simstate) {
        super(simstate);
        setOperatingAngle(5.235987f);
        setResolution(0.005061f);
        setSpeedPerDegree(0.003266f);
    }
    
    public Modelcraft_ES07(Modelcraft_ES07 servo){
        super(servo);
    }

    @Override
    public Modelcraft_ES07 copy() {
        Modelcraft_ES07 actuator = new Modelcraft_ES07(this);
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
        Subscriber<smart_e_msgs.servoCam> subscriber = ros_node.newSubscriber(auv_name + "/" + getName(), smart_e_msgs.servoCam._TYPE);
        subscriber.addMessageListener(new MessageListener<smart_e_msgs.servoCam>() {
                @Override
                public void onNewMessage(smart_e_msgs.servoCam message) {
                    System.out.println("I (" + getName()+ ") heard: \"" + message.getData() + "\"");
                    self.setDesiredAnglePosition((int)message.getData());
                }
        },( simState.getMARSSettings().getROSGlobalQueueSize() > 0) ? simState.getMARSSettings().getROSGlobalQueueSize() : getRos_queue_listener_size());
    }
}
