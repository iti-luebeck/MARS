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
package mars.actuators.servos;

import com.jme3.scene.Geometry;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import mars.ros.MARSNodeMain;
import mars.states.SimState;
import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;

/**
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Modelcraft_ES07 extends Servo {

    /**
     *
     */
    public Modelcraft_ES07() {
        super();
    }

    /**
     *
     * @param simstate
     * @param MassCenterGeom
     */
    public Modelcraft_ES07(SimState simstate, Geometry MassCenterGeom) {
        super(simstate, MassCenterGeom);
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

    /**
     *
     * @param servo
     */
    public Modelcraft_ES07(Modelcraft_ES07 servo) {
        super(servo);
    }

    /**
     *
     * @return
     */
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
    @Deprecated
    @SuppressWarnings("unchecked")
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        final Servo self = this;
        Subscriber<smart_e_msgs.servoCam> subscriber = ros_node.newSubscriber(auv_name + "/" + getName(), smart_e_msgs.servoCam._TYPE);
        subscriber.addMessageListener(new MessageListener<smart_e_msgs.servoCam>() {
            @Override
            public void onNewMessage(smart_e_msgs.servoCam message) {
                self.setDesiredAnglePosition((int) message.getData());
            }
        }, (simState.getMARSSettings().getROSGlobalQueueSize() > 0) ? simState.getMARSSettings().getROSGlobalQueueSize() : getRos_queue_listener_size());
    }
}
