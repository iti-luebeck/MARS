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
package mars.actuators;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import geometry_msgs.Point;
import geometry_msgs.Quaternion;
import mars.ros.MARSNodeMain;
import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;

/**
 * This class is basically a teleporter but since the hanse bag files for the
 * estimated pose dont have the depth we have to make a special teleporter for
 * subscribing to the pressure sensor.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class HanseBagPlayer extends Teleporter {

    private Vector3f pos2d = Vector3f.ZERO;
    private float depth = 0f;
    private com.jme3.math.Quaternion quat = com.jme3.math.Quaternion.IDENTITY;

    /**
     *
     * @param depth
     */
    public void setDepth(float depth) {
        this.depth = depth;
    }

    /**
     *
     * @return
     */
    public float getDepth() {
        return depth;
    }

    /**
     *
     * @param pos2d
     */
    public void setPos2d(Vector3f pos2d) {
        this.pos2d = pos2d;
    }

    /**
     *
     * @return
     */
    public Vector3f getPos2d() {
        return pos2d;
    }

    /**
     *
     * @return
     */
    public com.jme3.math.Quaternion getQuat() {
        return quat;
    }

    /**
     *
     * @param quat
     */
    public void setQuat(com.jme3.math.Quaternion quat) {
        this.quat = quat;
    }

    /**
     *
     * @return
     */
    public Integer getPressureRelative() {
        return (Integer) variables.get("PressureRelative");
    }

    /**
     *
     * @param PressureRelative
     */
    public void setPressureRelative(Integer PressureRelative) {
        variables.put("PressureRelative", PressureRelative);
    }

    /**
     *
     * @param ros_node
     * @param auv_name
     */
    @Override
    @SuppressWarnings("unchecked")
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        final HanseBagPlayer self = this;
        Subscriber<geometry_msgs.PoseStamped> subscriber = ros_node.newSubscriber(auv_name + "/" + getName(), geometry_msgs.PoseStamped._TYPE);
        subscriber.addMessageListener(new MessageListener<geometry_msgs.PoseStamped>() {
            @Override
            public void onNewMessage(geometry_msgs.PoseStamped message) {

                Point pos = message.getPose().getPosition();
                Vector3f v_pos = new Vector3f((float) pos.getX(), (float) pos.getZ(), (float) pos.getY());

                //getting from ROS Co-S to MARS Co-S
                Quaternion ori = message.getPose().getOrientation();
                com.jme3.math.Quaternion quat = new com.jme3.math.Quaternion((float) ori.getX(), (float) ori.getZ(), (float) ori.getY(), -(float) ori.getW());
                com.jme3.math.Quaternion qrot = new com.jme3.math.Quaternion();
                qrot.fromAngles(0f, FastMath.HALF_PI, 0f);
                quat.multLocal(qrot);

                v_pos.y = getDepth();

                setQuat(quat);
                setPos2d(v_pos);
                self.teleport(v_pos, quat);
            }
        }, (simState.getMARSSettings().getROSGlobalQueueSize() > 0) ? simState.getMARSSettings().getROSGlobalQueueSize() : getRos_queue_listener_size());

        Subscriber<hanse_msgs.pressure> subscriberDepth = ros_node.newSubscriber(auv_name + "/" + "pressure/depth", hanse_msgs.pressure._TYPE);
        subscriberDepth.addMessageListener(new MessageListener<hanse_msgs.pressure>() {
            @Override
            public void onNewMessage(hanse_msgs.pressure message) {
                float dep = 0f;
                int pos = (int) message.getData();

                dep = (getPressureRelative() - pos) / (pe.getFluid_density() * pe.getGravitational_acceleration()) * 100f;
                setDepth(dep);
            }
        }, (simState.getMARSSettings().getROSGlobalQueueSize() > 0) ? simState.getMARSSettings().getROSGlobalQueueSize() : getRos_queue_listener_size());
    }
}
