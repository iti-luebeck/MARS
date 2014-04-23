/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.actuators;

import com.jme3.scene.Geometry;
import hanse_msgs.sollSpeed;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import org.ros.message.MessageListener;
import mars.states.SimState;
import mars.ros.MARSNodeMain;
import org.ros.node.topic.Subscriber;

/**
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class BrushlessThruster extends Thruster{
    
    /**
     * 
     */
    public BrushlessThruster(){
        super();
        motor_increment = 0.6f;
    }
    
    /**
     * 
     * @param simstate 
     * @param MassCenterGeom
     */
    public BrushlessThruster(SimState simstate,Geometry MassCenterGeom){
        super(simstate,MassCenterGeom);
        motor_increment = 0.6f;
    }

    /**
     *
     * @param simstate 
     */
    public BrushlessThruster(SimState simstate){
        super(simstate);
        motor_increment = 0.6f;
    }
    
    public BrushlessThruster(BrushlessThruster thruster){
        super(thruster);
        motor_increment = 5f;
    }

    @Override
    public BrushlessThruster copy() {
        BrushlessThruster actuator = new BrushlessThruster(this);
        actuator.initAfterJAXB();
        return actuator;
    }

    /**
     * This is the function that represents the SeaBotix measured thruster force.
     * @param speed 
     * @return
     */
    @Override
    protected float calculateThrusterForce(int speed){
        return (Math.signum(speed))*(0.00020655f * (float)Math.pow((float)Math.abs(speed), 2.02039525f) );
    }
    
    /**
     * This is the function that represents the SeaBotix measured thruster current.
     * @param speed 
     * @return
     */
    @Override
    protected float calculateThrusterCurrent(int speed){
        return 0.01f*Math.abs(speed);
    }
    
    /**
     * 
     * @param ros_node
     * @param auv_name
     */
    @Override
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        final BrushlessThruster self = this;
        Subscriber<hanse_msgs.sollSpeed> subscriber = ros_node.newSubscriber(auv_name + "/" + getName(), hanse_msgs.sollSpeed._TYPE);
        subscriber.addMessageListener(new MessageListener<hanse_msgs.sollSpeed>() {
                @Override
                public void onNewMessage(hanse_msgs.sollSpeed message) {
                    //System.out.println("I (" + getName()+ ") heard: \"" + message.getData() + "\"");
                    self.set_thruster_speed((int)message.getData());
                }
        },( simState.getMARSSettings().getROS_Gloabl_Queue_Size() > 0) ? simState.getMARSSettings().getROS_Gloabl_Queue_Size() : getRos_queue_listener_size());
    }
}
