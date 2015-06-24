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
import mars.ros.MARSNodeMain;
import mars.states.SimState;
import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;

/**
 * This class represents the Geomar Thrusters. A measured force fitting curve is used.
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
     * This is the function that represents the SeaBotix measured thruster force.
     *
     * @param speed
     * @return
     */
    @Override
    protected float calculateThrusterForce(int speed) {
        return (Math.signum(speed)) * (4.4950211572f * (float) Math.pow(1.0234763348f, (float) Math.abs(speed)));
    }

    /**
     * This is the function that represents the SeaBotix measured thruster current.
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
    @Deprecated
    @SuppressWarnings("unchecked")
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        final GeomarThruster self = this;
        Subscriber<hanse_msgs.sollSpeed> subscriber = ros_node.newSubscriber(auv_name + "/" + getName(), hanse_msgs.sollSpeed._TYPE);
        subscriber.addMessageListener(new MessageListener<hanse_msgs.sollSpeed>() {
            @Override
            public void onNewMessage(hanse_msgs.sollSpeed message) {
                self.set_thruster_speed((int) message.getData());
            }
        }, (simState.getMARSSettings().getROSGlobalQueueSize() > 0) ? simState.getMARSSettings().getROSGlobalQueueSize() : getRos_queue_listener_size());
    }
}
