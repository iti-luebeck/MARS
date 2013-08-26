/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.actuators;

import com.jme3.scene.Geometry;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import org.ros.message.MessageListener;
import mars.states.SimState;
import mars.ros.MARSNodeMain;
import org.ros.node.topic.Subscriber;

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
    
    public SeaBotixThruster(SeaBotixThruster thruster){
        super(thruster);
        motor_increment = 5f;
    }

    @Override
    public SeaBotixThruster copy() {
        SeaBotixThruster actuator = new SeaBotixThruster(this);
        actuator.initAfterJAXB();
        return actuator;
    }

    /**
     * This is the function that represents the SeaBotix measured thruster force. It is limited to +/- 127.
     * @param speed 
     * @return
     */
    @Override
    protected float calculateThrusterForce(int speed){
        //return (Math.signum(speed))*(0.16f * (float)Math.pow(1.04f, (float)Math.abs(speed)) );
        // we want to limit the maximum settable value to +/- 127
        int limited_speed = (Math.abs(speed)<= 127) ? Math.abs(speed) : 127;
        limited_speed = ((int)Math.signum(speed))*limited_speed;
        return (Math.signum(limited_speed))*(0.00046655f * (float)Math.pow((float)Math.abs(limited_speed), 2.02039525f) );
    }
    
    /**
     * This is the function that represents the SeaBotix measured thruster current.
     * @param speed 
     * @return
     */
    @Override
    protected float calculateThrusterCurrent(int speed){
        if( Math.abs(speed) > 22){
            return 0.01f*Math.abs(speed)-0.22f;
        }else{
            return 0f;
        }
    }
    
    /**
     * 
     * @param ros_node
     * @param auv_name
     */
    @Override
    public void initROS(MARSNodeMain ros_node, final String auv_name) {
        super.initROS(ros_node, auv_name);
        final SeaBotixThruster self = this;
        Subscriber<hanse_msgs.sollSpeed> subscriber = ros_node.newSubscriber(auv_name + "/" + getPhysicalExchangerName(), hanse_msgs.sollSpeed._TYPE);
        subscriber.addMessageListener(new MessageListener<hanse_msgs.sollSpeed>() {
                @Override
                public void onNewMessage(hanse_msgs.sollSpeed message) {
                    System.out.println("I (" + auv_name + "/" + getPhysicalExchangerName()+ ") heard: \"" + message.getData() + "\"");
                    self.set_thruster_speed((int)message.getData());
                }
        },( simState.getMARSSettings().getROS_Gloabl_Queue_Size() > 0) ? simState.getMARSSettings().getROS_Gloabl_Queue_Size() : getRos_queue_listener_size());
    }
}
