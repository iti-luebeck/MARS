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
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import geometry_msgs.Point;
import geometry_msgs.Quaternion;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.ros.MARSNodeMain;
import mars.states.SimState;
import org.ros.message.MessageListener;
import org.ros.node.topic.Subscriber;

/**
 * This class is similiar to the teleporter but with an interpolation between
 * movement points.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Animator extends Actuator {

    private final Queue<AnimationPoint> waypoints = new LinkedList<AnimationPoint>();
    private boolean simple = false;


    /**
     *
     */
    public Animator() {
        super();
    }

    /**
     *
     * @param simstate
     * @param MassCenterGeom
     */
    public Animator(SimState simstate, Geometry MassCenterGeom) {
        super(simstate, MassCenterGeom);
    }

    /**
     *
     * @param simstate
     */
    public Animator(SimState simstate) {
        super(simstate);
    }

    /**
     *
     * @param teleporter
     */
    public Animator(Animator teleporter) {
        super(teleporter);
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        Animator actuator = new Animator(this);
        actuator.initAfterJAXB();
        return actuator;
    }

    /**
     * DON'T CALL THIS METHOD! In this method all the initialiasing for the
     * motor will be done and it will be attached to the physicsNode.
     */
    @Override
    public void init(Node auv_node) {
        super.init(auv_node);
    }

    @Override
    public void updateForces() {
    }

    /**
     *
     * @param tpf
     */
    @Override
    public void update(float tpf) {
        if (waypoints.peek() != null){
            final AnimationPoint poll = waypoints.poll();
            Vector3f moveTo = poll.getTransform().getTranslation();
            com.jme3.math.Quaternion rotatoTo = poll.getTransform().getRotation();  
            float timeTo = poll.getTime();
            
            Future<Void> simStateFuture = this.simauv.enqueue(new Callable<Void>() {
            public Void call() throws Exception {
                getPhysicsControl().setPhysicsLocation(poll.getTransform().getTranslation());
                getPhysicsControl().setPhysicsRotation(poll.getTransform().getRotation());
                return null;
            }
            });
        }
    }

    @Override
    public void reset() {

    }
    
    /**
     * Convience method.
     * 
     * @param transform
     * @param time
     */
    public void addNewTransform(Transform transform,float time){
        waypoints.add(new AnimationPoint(transform, time));
    }
    
    /**
     * Add a new point (time and pose) for the animation.
     * 
     * @param animationPoint
     */
    public void addNewAnimationPint(AnimationPoint animationPoint){
        waypoints.add(animationPoint);
    }
    
    /**
     *
     * @return
     */
    public Boolean getSimple() {
        return (Boolean) variables.get("simple");
    }

    /**
     *
     * @param simple
     */
    public void setSimple(Boolean simple) {
        //boolean old = getEnabled();
        variables.put("enabled", simple);
        //fire("enabled", old, simple);
    }

    /**
     *
     * @param ros_node
     * @param auv_name
     */
    @Override
    @SuppressWarnings("unchecked")
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        final Animator self = this;
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
                qrot.fromAngles(0f, FastMath.HALF_PI, 0);
                quat.multLocal(qrot);

                //self.teleport(v_pos, quat);
            }
        }, (simState.getMARSSettings().getROSGlobalQueueSize() > 0) ? simState.getMARSSettings().getROSGlobalQueueSize() : getRos_queue_listener_size());
    }
}
