/*
* Copyright (c) 2015, Institute of Computer Engineering, University of Lübeck
* All rights reserved.
* 
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
* 
* * Redistributions of source code must retain the above copyright notice, this
*   list of conditions and the following disclaimer.
* 
* * Redistributions in binary form must reproduce the above copyright notice,
*   this list of conditions and the following disclaimer in the documentation
*   and/or other materials provided with the distribution.
* 
* * Neither the name of the copyright holder nor the names of its
*   contributors may be used to endorse or promote products derived from
*   this software without specific prior written permission.
* 
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
* FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
* SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
* CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
* OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package mars.actuators.thruster;

import com.jme3.scene.Geometry;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import org.ros.message.MessageListener;
import mars.states.SimState;
import mars.ros.MARSNodeMain;
import org.ros.node.topic.Subscriber;

/**
 * This class represents the SeaBotix Thrusters. A measured force fitting curve
 * is used.
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class SeaBotixThruster extends Thruster {

    /**
     *
     */
    public SeaBotixThruster() {
        super();
        motor_increment = 5f;
    }

    /**
     *
     * @param simstate
     * @param MassCenterGeom
     */
    public SeaBotixThruster(SimState simstate, Geometry MassCenterGeom) {
        super(simstate, MassCenterGeom);
        motor_increment = 5f;
    }

    /**
     *
     * @param simstate
     */
    public SeaBotixThruster(SimState simstate) {
        super(simstate);
        motor_increment = 5f;
    }

    /**
     *
     * @param thruster
     */
    public SeaBotixThruster(SeaBotixThruster thruster) {
        super(thruster);
        motor_increment = 5f;
    }

    /**
     *
     * @return
     */
    @Override
    public SeaBotixThruster copy() {
        SeaBotixThruster actuator = new SeaBotixThruster(this);
        actuator.initAfterJAXB();
        return actuator;
    }

    /**
     * This is the function that represents the SeaBotix measured thruster
     * force. It is limited to +/- 127.
     *
     * @param speed
     * @return
     */
    @Override
    protected float calculateThrusterForce(int speed) {
        //return (Math.signum(speed))*(0.16f * (float)Math.pow(1.04f, (float)Math.abs(speed)) );
        // we want to limit the maximum settable value to +/- 127
        int limited_speed = (Math.abs(speed) <= 127) ? Math.abs(speed) : 127;
        limited_speed = ((int) Math.signum(speed)) * limited_speed;
        return (Math.signum(limited_speed)) * (0.00046655f * (float) Math.pow((float) Math.abs(limited_speed), 2.02039525f));
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
        if (Math.abs(speed) > 22) {
            return 0.01f * Math.abs(speed) - 0.22f;
        } else {
            return 0f;
        }
    }

    /**
     *
     * @param ros_node
     * @param auv_name
     */
    @Override
    @SuppressWarnings("unchecked")
    public void initROS(MARSNodeMain ros_node, final String auv_name) {
        super.initROS(ros_node, auv_name);
        final SeaBotixThruster self = this;
        Subscriber<hanse_msgs.sollSpeed> subscriber = ros_node.newSubscriber(auv_name + "/" + getName(), hanse_msgs.sollSpeed._TYPE);
        subscriber.addMessageListener(new MessageListener<hanse_msgs.sollSpeed>() {
            @Override
            public void onNewMessage(hanse_msgs.sollSpeed message) {
                //System.out.println("I (" + auv_name + "/" + getName() + ") heard: \"" + message.getData() + "\"");
                self.set_thruster_speed((int) message.getData());
            }
        }, (simState.getMARSSettings().getROSGlobalQueueSize() > 0) ? simState.getMARSSettings().getROSGlobalQueueSize() : getRos_queue_listener_size());
    }
}
