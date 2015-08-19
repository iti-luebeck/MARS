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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.events.AUVObjectEvent;
import mars.server.MARSClientEvent;
import org.ros.message.Time;
import org.ros.node.topic.Publisher;

/**
 * A Hakuyo laser scanner. Ray based.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Hakuyo extends LaserScanner {

    ///ROS stuff
    /**
     *
     */
    protected Publisher<sensor_msgs.LaserScan> publisher = null;
    /**
     *
     */
    protected sensor_msgs.LaserScan fl;
    /**
     *
     */
    protected std_msgs.Header header;

    /**
     *
     */
    public Hakuyo() {
        super();
    }

    /**
     *
     */
    public Hakuyo(Hakuyo sensor) {
        super(sensor);
    }

    @Override
    protected float calculateAverageNoiseFunction(float x) {
        return 14.22898616f * ((float) Math.pow(1.03339750f, Math.abs(x)));
    }

    @Override
    protected float calculateStandardDeviationNoiseFunction(float x) {
        return 7.50837174f * ((float) Math.pow(1.02266704f, Math.abs(x)));
    }

    /**
     *
     * @param ros_node
     * @param auv_name
     *
     * @Deprecated
     * @SuppressWarnings("unchecked") public void initROS(MARSNodeMain ros_node, String auv_name) { publisher = (Publisher<sensor_msgs.LaserScan>) ros_node.newPublisher(auv_name + "/" + this.getName(), sensor_msgs.LaserScan._TYPE); fl = this.mars_node.getMessageFactory().newFromType(sensor_msgs.LaserScan._TYPE); header = this.mars_node.getMessageFactory().newFromType(std_msgs.Header._TYPE); this.rosinit = true;
    }
     */
    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        Hakuyo sensor = new Hakuyo(this);
        sensor.initAfterJAXB();
        return sensor;
    }

    /**
     *
     */
    @Deprecated
    public void publish() {
        header.setSeq(sequenceNumber++);
        header.setFrameId(this.getRos_frame_id());
        header.setStamp(Time.fromMillis(System.currentTimeMillis()));
        fl.setHeader(header);

        float[] instantData = getInstantData();
        float lastHeadPosition = getLastHeadPosition();
        //this.mars.getTreeTopComp().initRayBasedData(instantData, lastHeadPosition, this);
        fl.setAngleIncrement(getScanning_resolution());
        fl.setRangeMax(getMaxRange());
        fl.setRangeMin(getMinRange());
        fl.setScanTime(getRos_publish_rate() / 1000f);
        //fl.setTimeIncrement();
        fl.setAngleMax(getScanningAngleMax());
        fl.setAngleMin(getScanningAngleMin());

        fl.setRanges(instantData);

        if (publisher != null) {
            publisher.publish(fl);
        }
    }

    @Override
    public void publishData() {
        super.publishData();
        MARSClientEvent clEvent = new MARSClientEvent(getAuv(), this, getInstantData(), System.currentTimeMillis());
        simState.getAuvManager().notifyAdvertisement(clEvent);
        AUVObjectEvent auvEvent = new AUVObjectEvent(this, getInstantData(), System.currentTimeMillis());
        notifyAdvertisementAUVObject(auvEvent);
    }
}
