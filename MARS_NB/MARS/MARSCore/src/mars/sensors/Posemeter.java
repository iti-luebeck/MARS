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
import mars.misc.Pose;
import mars.states.SimState;

/**
 * Gives the exact Pose(Position/Orientation). Mixin class.
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Posemeter extends Sensor {

    @XmlElement(name = "Positionmeter")
    Positionmeter pos = new Positionmeter();
    @XmlElement(name = "Orientationmeter")
    Orientationmeter oro = new Orientationmeter();

    /**
     *
     */
    public Posemeter() {
        super();
    }

    /**
     *
     * @param simstate
     * @param pe
     */
    public Posemeter(SimState simstate, PhysicalEnvironment pe) {
        super(simstate);
        this.pe = pe;
        pos.setPhysicalEnvironment(pe);
        oro.setPhysicalEnvironment(pe);
        pos.setSimState(simState);
        oro.setSimState(simState);
    }

    /**
     *
     * @param simstate
     */
    public Posemeter(SimState simstate) {
        super(simstate);
        pos.setSimState(simState);
        oro.setSimState(simState);
    }

    /**
     *
     * @param sensor
     */
    public Posemeter(Posemeter sensor) {
        super(sensor);
        oro = (Orientationmeter) sensor.getOrientationmeter().copy();
        pos = (Positionmeter) sensor.getPositionmeter().copy();
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        Posemeter sensor = new Posemeter(this);
        sensor.initAfterJAXB();
        return sensor;
    }

    /**
     *
     */
    @Override
    public void init(Node auv_node) {
        super.init(auv_node);
        pos.init(auv_node);
        oro.init(auv_node);
    }

    /**
     *
     * @param tpf
     */
    @Override
    public void update(float tpf) {
        pos.update(tpf);
        oro.update(tpf);
    }

    /**
     *
     */
    @Override
    public void reset() {
        pos.reset();
        oro.reset();
    }

    @Override
    public void setPhysicalEnvironment(PhysicalEnvironment pe) {
        super.setPhysicalEnvironment(pe);
        pos.setPhysicalEnvironment(pe);
        oro.setPhysicalEnvironment(pe);
    }

    /**
     *
     * @param simState
     */
    @Override
    public void setSimState(SimState simState) {
        super.setSimState(simState);
        pos.setSimState(simState);
        oro.setSimState(simState);
    }

    @Override
    public void setPhysicsControl(RigidBodyControl physics_control) {
        super.setPhysicsControl(physics_control);
        pos.setPhysicsControl(physics_control);
        oro.setPhysicsControl(physics_control);
    }

    /**
     *
     * @param visible
     */
    @Override
    public void setNodeVisibility(boolean visible) {
        super.setNodeVisibility(visible);
        pos.setNodeVisibility(visible);
        oro.setNodeVisibility(visible);
    }

    /**
     *
     * @param name
     */
    @Override
    public void setName(String name) {
        super.setName(name);
        pos.setName(name + "_positionmeter");
        oro.setName(name + "_orientationmeter");
    }

    /**
     *
     * @param enabled
     */
    @Override
    public void setEnabled(Boolean enabled) {
        super.setEnabled(enabled);
        pos.setEnabled(enabled);
        oro.setEnabled(enabled);
    }

    /**
     *
     * @return
     */
    public Positionmeter getPositionmeter() {
        return pos;
    }

    /**
     *
     * @return
     */
    public Orientationmeter getOrientationmeter() {
        return oro;
    }

    @Override
    public void setAuv(AUV auv) {
        super.setAuv(auv);
        pos.setAuv(auv);
        oro.setAuv(auv);
    }

    /**
     * 
     * @return 
     */
    public Pose getPose() {
        return new Pose(pos.getWorldPosition(), oro.getOrientation());
    }

    @Override
    public void publishData() {
        super.publishData();
        Pose pose = getPose();
        AUVObjectEvent auvEvent = new AUVObjectEvent(this, pose, System.currentTimeMillis());
        notifyAdvertisementAUVObject(auvEvent);
    }
}
