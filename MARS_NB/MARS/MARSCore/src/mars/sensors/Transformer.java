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
package mars.sensors;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import mars.Helper.NoiseType;
import mars.PhysicalEnvironment;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.states.SimState;
import org.ros.message.Time;
import org.ros.node.topic.Publisher;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Transformer extends Sensor {

    ///ROS stuff
    private Publisher<tf.tfMessage> publisher = null;
    private tf.tfMessage fl;
    private geometry_msgs.TransformStamped tfs;
    private geometry_msgs.TransformStamped tfs2;
    private std_msgs.Header header;
    private std_msgs.Header header2;

    /**
     *
     */
    public Transformer() {
        super();
    }

    /**
     *
     * @param simstate
     * @param pe
     */
    public Transformer(SimState simstate, PhysicalEnvironment pe) {
        super(simstate);
        this.pe = pe;
    }

    /**
     *
     * @param simstate
     */
    public Transformer(SimState simstate) {
        super(simstate);
    }

    /**
     *
     * @param transformer
     */
    public Transformer(Transformer transformer) {
        super(transformer);
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        Transformer sensor = new Transformer(this);
        sensor.initAfterJAXB();
        return sensor;
    }

    /**
     *
     */
    @Override
    public void init(Node auv_node) {
        super.init(auv_node);
    }

    /**
     *
     * @param tpf
     */
    public void update(float tpf) {

    }

    /**
     *
     * @return
     */
    public Vector3f getPosition() {
        if (getNoiseType() == NoiseType.NO_NOISE) {
            return getPositionRaw();
        } else if (getNoiseType() == NoiseType.UNIFORM_DISTRIBUTION) {
            float noise = getUnifromDistributionNoise(getNoiseValue());
            Vector3f noised = new Vector3f(getPositionRaw().x + (((1f / 100f) * noise)), getPositionRaw().y + (((1f / 100f) * noise)), getPositionRaw().z + (((1f / 100f) * noise)));
            return noised;
        } else if (getNoiseType() == NoiseType.GAUSSIAN_NOISE_FUNCTION) {
            float noise = getGaussianDistributionNoise(getNoiseValue());
            Vector3f noised = new Vector3f(getPositionRaw().x + (((1f / 100f) * noise)), getPositionRaw().y + (((1f / 100f) * noise)), getPositionRaw().z + (((1f / 100f) * noise)));
            return noised;
        } else {
            return getPositionRaw();
        }
    }

    /**
     *
     * @return
     */
    private Vector3f getPositionRaw() {
        return physics_control.getPhysicsLocation();
    }

    /**
     *
     */
    public void reset() {
    }

    /**
     *
     */
    @Deprecated
    public void publish() {
        header.setSeq(sequenceNumber++);
        header.setFrameId("jme3");
        header.setStamp(Time.fromMillis(System.currentTimeMillis()));
        tfs.setHeader(header);

        geometry_msgs.Transform transform = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Transform._TYPE);

        geometry_msgs.Vector3 position = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Vector3._TYPE);
        position.setX(physics_control.getPhysicsLocation().getX());
        position.setY(physics_control.getPhysicsLocation().getY());
        position.setZ(physics_control.getPhysicsLocation().getZ());
        transform.setTranslation(position);

        geometry_msgs.Quaternion quat = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Quaternion._TYPE);
        quat.setX(auv_node.getLocalRotation().getX());
        quat.setY(auv_node.getLocalRotation().getY());
        quat.setZ(auv_node.getLocalRotation().getZ());
        quat.setW(auv_node.getLocalRotation().getW());
        transform.setRotation(quat);

        tfs.setTransform(transform);

        tfs.setChildFrameId(getAuv().getName());

        //root
        /*header2.setSeq(rosSequenceNumber++);
         header2.setFrameId("ros");
         header2.setStamp(Time.fromMillis(System.currentTimeMillis()));
         tfs2.setHeader(header2);
        
         geometry_msgs.Transform transform2 = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Transform._TYPE);
        
         geometry_msgs.Vector3 position2 = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Vector3._TYPE);
         position2.setX(0f);
         position2.setY(0f);
         position2.setZ(0f);
         transform2.setTranslation(position2);
        
         geometry_msgs.Quaternion quat2 = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Quaternion._TYPE);
         Quaternion quat_jme = new Quaternion();
         quat_jme.fromAngles(0f,FastMath.HALF_PI,FastMath.HALF_PI);
         quat2.setX(quat_jme.getX());
         quat2.setY(quat_jme.getY());
         quat2.setZ(quat_jme.getZ());
         quat2.setW(quat_jme.getW());
         transform2.setRotation(quat2);
                
         tfs2.setTransform(transform2);  
        
         tfs2.setChildFrameId("jme3");*/
        List<geometry_msgs.TransformStamped> tfl = new ArrayList<geometry_msgs.TransformStamped>();
        tfl.add(tfs);
        //tfl.add(tfs2);

        fl.setTransforms(tfl);

        if (publisher != null) {
            publisher.publish(fl);
        }
    }
}
