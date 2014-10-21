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
 * This class represents the Geomar Thrusters. A measured force fitting curve is
 * used.
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class GeomarThruster extends Thruster {

    /**
     *
     */
    public GeomarThruster() {
        super();
        motor_increment = 0.6f;
    }

    /**
     *
     * @param simstate
     * @param MassCenterGeom
     */
    public GeomarThruster(SimState simstate, Geometry MassCenterGeom) {
        super(simstate, MassCenterGeom);
        motor_increment = 0.6f;
    }

    /**
     *
     * @param simstate
     */
    public GeomarThruster(SimState simstate) {
        super(simstate);
        motor_increment = 0.6f;
    }

    /**
     *
     * @param thruster
     */
    public GeomarThruster(GeomarThruster thruster) {
        super(thruster);
        motor_increment = 5f;
    }

    /**
     *
     * @return
     */
    @Override
    public GeomarThruster copy() {
        GeomarThruster actuator = new GeomarThruster(this);
        actuator.initAfterJAXB();
        return actuator;
    }

    /**
     * This is the function that represents the SeaBotix measured thruster
     * force.
     *
     * @param speed
     * @return
     */
    @Override
    protected float calculateThrusterForce(int speed) {
        return (Math.signum(speed)) * (4.4950211572f * (float) Math.pow(1.0234763348f, (float) Math.abs(speed)));
    }

    /**
     * This is the function that represents the SeaBotix measured thruster
     * current.
     *
     * @param speed
     * @return
     */
    @Override
    protected float calculateThrusterCurrent(int speed) {
        return 0.4100154271f * (float) Math.pow(1.0338512063f, (float) Math.abs(speed));
    }

    /**
     *
     * @param ros_node
     * @param auv_name
     */
    @Override
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        final GeomarThruster self = this;
        Subscriber<hanse_msgs.sollSpeed> subscriber = ros_node.newSubscriber(auv_name + "/" + getName(), hanse_msgs.sollSpeed._TYPE);
        subscriber.addMessageListener(new MessageListener<hanse_msgs.sollSpeed>() {
            @Override
            public void onNewMessage(hanse_msgs.sollSpeed message) {
                //System.out.println("I (" + getName()+ ") heard: \"" + message.getData() + "\"");
                self.set_thruster_speed((int) message.getData());
            }
        }, (simState.getMARSSettings().getROSGlobalQueueSize() > 0) ? simState.getMARSSettings().getROSGlobalQueueSize() : getRos_queue_listener_size());
    }
}
