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

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Node;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import mars.PhysicalEnvironment;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.auv.AUV;
import mars.events.AUVObjectEvent;
import mars.misc.CTDData;
import mars.states.SimState;
import org.ros.node.topic.Publisher;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlAccessorType(XmlAccessType.NONE)
public class CTDSensor extends Sensor {

    @XmlElement(name = "PressureSensor")
    PressureSensor press = new PressureSensor();
    @XmlElement(name = "TemperatureSensor")
    TemperatureSensor temp = new TemperatureSensor();
    @XmlElement(name = "SalinitySensor")
    SalinitySensor sal = new SalinitySensor();

    ///ROS stuff
    private Publisher<sensor_msgs.Imu> publisher = null;
    private sensor_msgs.Imu fl;
    private std_msgs.Header header;

    /**
     *
     */
    public CTDSensor() {
        super();
    }

    /**
     *
     * @param simstate
     * @param pe
     */
    public CTDSensor(SimState simstate, PhysicalEnvironment pe) {
        super(simstate);
        this.pe = pe;
        press.setPhysicalEnvironment(pe);
        temp.setPhysicalEnvironment(pe);
        sal.setPhysicalEnvironment(pe);
        press.setSimState(simState);
        temp.setSimState(simState);
        sal.setSimState(simState);
    }

    /**
     *
     * @param simstate
     */
    public CTDSensor(SimState simstate) {
        super(simstate);
        press.setSimState(simState);
        temp.setSimState(simState);
        sal.setSimState(simState);
    }

    /**
     *
     * @param sensor
     */
    public CTDSensor(CTDSensor sensor) {
        super(sensor);
        press = (PressureSensor) sensor.getSalintySensor().copy();
        temp = (TemperatureSensor) sensor.getTemperatureSensor().copy();
        sal = (SalinitySensor) sensor.getPressureSensor().copy();
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        CTDSensor sensor = new CTDSensor(this);
        sensor.initAfterJAXB();
        return sensor;
    }

    /**
     *
     */
    @Override
    public void init(Node auv_node) {
        super.init(auv_node);
        press.init(auv_node);
        temp.init(auv_node);
        sal.init(auv_node);
    }

    /**
     *
     * @param tpf
     */
    public void update(float tpf) {
        press.update(tpf);
        temp.update(tpf);
        sal.update(tpf);
    }

    /**
     *
     */
    public void reset() {
        press.reset();
        temp.reset();
        sal.reset();
    }

    @Override
    public void setPhysicalEnvironment(PhysicalEnvironment pe) {
        super.setPhysicalEnvironment(pe);
        press.setPhysicalEnvironment(pe);
        temp.setPhysicalEnvironment(pe);
        sal.setPhysicalEnvironment(pe);
    }

    /**
     *
     * @param simState
     */
    @Override
    public void setSimState(SimState simState) {
        super.setSimState(simState);
        press.setSimState(simState);
        temp.setSimState(simState);
        sal.setSimState(simState);
    }

    @Override
    public void setPhysicsControl(RigidBodyControl physics_control) {
        super.setPhysicsControl(physics_control);
        press.setPhysicsControl(physics_control);
        temp.setPhysicsControl(physics_control);
        sal.setPhysicsControl(physics_control);
    }

    /**
     *
     * @param visible
     */
    @Override
    public void setNodeVisibility(boolean visible) {
        super.setNodeVisibility(visible);
        press.setNodeVisibility(visible);
        temp.setNodeVisibility(visible);
        sal.setNodeVisibility(visible);
    }

    /**
     *
     * @param name
     */
    @Override
    public void setName(String name) {
        super.setName(name);
        press.setName(name + "_pressuresensor");
        temp.setName(name + "_temperaturesensor");
        sal.setName(name + "_salinitymeter");
    }

    /**
     *
     * @param enabled
     */
    @Override
    public void setEnabled(Boolean enabled) {
        super.setEnabled(enabled);
        press.setEnabled(enabled);
        temp.setEnabled(enabled);
        sal.setEnabled(enabled);
    }

    @Override
    public void setAuv(AUV auv) {
        super.setAuv(auv);
        press.setAuv(auv);
        temp.setAuv(auv);
        sal.setAuv(auv);
    }

    /**
     *
     * @return
     */
    public TemperatureSensor getTemperatureSensor() {
        return temp;
    }

    /**
     *
     * @return
     */
    public PressureSensor getPressureSensor() {
        return press;
    }

    /**
     *
     * @return
     */
    public SalinitySensor getSalintySensor() {
        return sal;
    }

    public CTDData getCTD() {
        CTDData mat = new CTDData(sal.getSalinity(), temp.getTemperature(), press.getDepth());
        return mat;
    }

    @Override
    public void publishData() {
        super.publishData();
        AUVObjectEvent auvEvent = new AUVObjectEvent(this, getCTD(), System.currentTimeMillis());
        notifyAdvertisementAUVObject(auvEvent);
    }
}
