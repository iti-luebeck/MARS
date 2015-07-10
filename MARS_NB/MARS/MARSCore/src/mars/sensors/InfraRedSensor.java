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
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import mars.misc.Collider;
import org.ros.node.topic.Publisher;
import mars.Helper.NoiseType;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.events.AUVObjectEvent;
import mars.states.SimState;
import mars.ros.MARSNodeMain;
import mars.server.MARSClientEvent;
import org.ros.message.Time;

/**
 * A simple Infrared sensor. Ray based.
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class InfraRedSensor extends RayBasedSensor{

    private Collider RayDetectable;

    //ROS stuff
    private Publisher<sensor_msgs.Range> publisher = null;
    private sensor_msgs.Range fl;
    private std_msgs.Header header;

    /**
     *
     */
    public InfraRedSensor() {
        super();
    }

    /**
     *
     * @param simstate
     */
    public InfraRedSensor(SimState simstate) {
        super(simstate);
        //set the logging
        try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
            // Add to the desired logger
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.addHandler(handler);
        } catch (IOException e) {
        }

        this.RayDetectable = simstate.getCollider();
    }

    /**
     *
     * @param sensor
     */
    public InfraRedSensor(InfraRedSensor sensor) {
        super(sensor);
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        InfraRedSensor sensor = new InfraRedSensor(this);
        sensor.initAfterJAXB();
        return sensor;
    }

    /**
     *
     * @return The exact depth of the current auv
     */
    public float getDistance() {
        if (getNoiseType() == NoiseType.NO_NOISE) {
            return getRawDistance();
        } else if (getNoiseType() == NoiseType.UNIFORM_DISTRIBUTION) {
            float noise = getUnifromDistributionNoise(getNoiseValue());
            return getRawDistance() + (((1f / 100f) * noise));
        } else if (getNoiseType() == NoiseType.GAUSSIAN_NOISE_FUNCTION) {
            float noise = getGaussianDistributionNoise(getNoiseValue());
            return getRawDistance() + (((1f / 100f) * noise));
        } else {
            return getRawDistance();
        }
    }

    private float getRawDistance() {
        Vector3f ray_start = this.SonarStart.getWorldTranslation();

        Vector3f ray_direction = (SonarEnd.getWorldTranslation()).subtract(SonarStart.getWorldTranslation());

        float[] infra_data = getRawRayData(ray_start, ray_direction);

        return infra_data[0];
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
        publisher = (Publisher<sensor_msgs.Range>)ros_node.newPublisher(auv_name + "/" + this.getName(), sensor_msgs.Range._TYPE);
        fl = this.mars_node.getMessageFactory().newFromType(sensor_msgs.Range._TYPE);
        header = this.mars_node.getMessageFactory().newFromType(std_msgs.Header._TYPE);
        this.rosinit = true;
    }

    /**
     *
     */
    @Override
    public void publish() {
        super.publish();
        header.setSeq(rosSequenceNumber++);
        header.setFrameId(this.getRos_frame_id());
        header.setStamp(Time.fromMillis(System.currentTimeMillis()));
        fl.setHeader(header);
        fl.setMinRange(getMinRange());
        fl.setMaxRange(getMaxRange());
        fl.setRange(getDistance());
        fl.setFieldOfView(0f);
        fl.setRadiationType(sensor_msgs.Range.INFRARED);
        if (publisher != null) {
            publisher.publish(fl);
        }
    }

    @Override
    public void publishData() {
        super.publishData();
        MARSClientEvent clEvent = new MARSClientEvent(getAuv(), this, getDistance(), System.currentTimeMillis());
        simState.getAuvManager().notifyAdvertisement(clEvent);
        AUVObjectEvent auvEvent = new AUVObjectEvent(this, getDistance(), System.currentTimeMillis());
        notifyAdvertisementAUVObject(auvEvent);
    }
}
