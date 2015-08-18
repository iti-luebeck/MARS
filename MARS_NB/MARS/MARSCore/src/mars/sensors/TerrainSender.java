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

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.scene.Node;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import mars.Initializer;
import mars.MARS_Settings;
import mars.PhysicalEnvironment;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.ros.MARSNodeMain;
import mars.states.SimState;
import org.ros.message.Time;
import org.ros.node.topic.Publisher;

/**
 * This class takes the terrain and sends it in a special format. Can be used
 * for visualization or debugging. Mainly for ROS for now.
 *
 * @author Thomas Tosik
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class TerrainSender extends Sensor {

    private Initializer initer;
    private MARS_Settings mars_settings;
    byte[] occupany_grid;

    ///ROS stuff
    private Publisher<nav_msgs.OccupancyGrid> publisher = null;
    private nav_msgs.OccupancyGrid fl;
    private std_msgs.Header header;

    /**
     *
     */
    public TerrainSender() {
        super();
    }

    /**
     *
     * @param simstate
     * @param pe
     */
    public TerrainSender(SimState simstate, PhysicalEnvironment pe) {
        super(simstate);
        this.pe = pe;
    }

    /**
     *
     * @param simstate
     */
    public TerrainSender(SimState simstate) {
        super(simstate);
    }

    /**
     *
     * @param sensor
     */
    public TerrainSender(TerrainSender sensor) {
        super(sensor);
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        TerrainSender sensor = new TerrainSender(this);
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
     */
    public void reset() {
    }

    /**
     *
     * @return
     */
    public Initializer getIniter() {
        return initer;
    }

    /**
     *
     * @param initer
     */
    public void setIniter(Initializer initer) {
        this.initer = initer;
    }

    /**
     *
     * @param mars_settings
     */
    public void setMarsSettings(MARS_Settings mars_settings) {
        this.mars_settings = mars_settings;
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
        publisher = (Publisher<nav_msgs.OccupancyGrid>)ros_node.newPublisher(auv_name + "/" + this.getName(), nav_msgs.OccupancyGrid._TYPE);
        fl = this.mars_node.getMessageFactory().newFromType(nav_msgs.OccupancyGrid._TYPE);
        header = this.mars_node.getMessageFactory().newFromType(std_msgs.Header._TYPE);
        this.rosinit = true;
    }

    /**
     *
     */
    @Override
    public void publish() {
        header.setSeq(sequenceNumber++);
        header.setFrameId(this.getRos_frame_id());
        header.setStamp(Time.fromMillis(System.currentTimeMillis()));
        fl.setHeader(header);

        nav_msgs.MapMetaData info = this.mars_node.getMessageFactory().newFromType(nav_msgs.MapMetaData._TYPE);
        info.setHeight(initer.getTerrain_image_heigth());
        info.setWidth(initer.getTerrain_image_width());
        info.setResolution(mars_settings.getTerrainScale().getX());

        geometry_msgs.Point point = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Point._TYPE);
        
        float terScaleX = (mars_settings.getTerrainScale().x * initer.getTerrain_image_width()) / 2f;
        float terScaleZ = (mars_settings.getTerrainScale().z * initer.getTerrain_image_width()) / 2f;
        //float terScaleY = (mars_settings.getTerrainScale().y*initer.getTerrain_image_width())/2f;
        point.setX(mars_settings.getTerrainPosition().x - terScaleX);
        point.setY(mars_settings.getTerrainPosition().z - terScaleZ);
        point.setZ(mars_settings.getTerrainPosition().y);

        Quaternion ter_orientation = new Quaternion();
        Quaternion ter_orientation_rueck = new Quaternion();
        ter_orientation.fromAngles(-FastMath.HALF_PI, 0f, 0f);
        ter_orientation_rueck = ter_orientation.inverse();

        com.jme3.math.Quaternion jme3_quat = new com.jme3.math.Quaternion();
        jme3_quat.fromAngles(FastMath.PI, FastMath.PI, -FastMath.PI);//we have to rotate it correctly because teramonkey is a little bit different in storing
        ter_orientation.multLocal(jme3_quat.multLocal(ter_orientation_rueck));

        geometry_msgs.Quaternion orientation = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Quaternion._TYPE);
        orientation.setX(ter_orientation.getX());
        orientation.setY(ter_orientation.getY());
        orientation.setZ(ter_orientation.getZ());
        orientation.setW(ter_orientation.getW());

        geometry_msgs.Pose pose = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Pose._TYPE);
        pose.setPosition(point);
        pose.setOrientation(orientation);

        info.setOrigin(pose);
        info.setMapLoadTime(Time.fromMillis(System.currentTimeMillis()));
        fl.setInfo(info);
        fl.setData(initer.getTerrainChannelBuffer());

        if (publisher != null) {
            publisher.publish(fl);
        }
    }
}
