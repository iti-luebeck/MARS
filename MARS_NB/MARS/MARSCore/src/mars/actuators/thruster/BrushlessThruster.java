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
import mars.states.SimState;

/**
 * Plain thrusters used by the MONSUN project.
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class BrushlessThruster extends Thruster {

    /**
     *
     */
    public BrushlessThruster() {
        super();
        motor_increment = 0.6f;
    }

    /**
     *
     * @param simstate
     * @param MassCenterGeom
     */
    public BrushlessThruster(SimState simstate, Geometry MassCenterGeom) {
        super(simstate, MassCenterGeom);
        motor_increment = 0.6f;
    }

    /**
     *
     * @param simstate
     */
    public BrushlessThruster(SimState simstate) {
        super(simstate);
        motor_increment = 0.6f;
    }

    /**
     *
     * @param thruster
     */
    public BrushlessThruster(BrushlessThruster thruster) {
        super(thruster);
        motor_increment = 5f;
    }

    /**
     *
     * @return
     */
    @Override
    public BrushlessThruster copy() {
        BrushlessThruster actuator = new BrushlessThruster(this);
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
        return (Math.signum(speed)) * (0.00020655f * (float) Math.pow((float) Math.abs(speed), 2.02039525f));
    }

    /**
     * This is the function that represents the SeaBotix measured thruster current.
     *
     * @param speed
     * @return
     */
    @Override
    protected float calculateThrusterCurrent(int speed) {
        return 0.01f * Math.abs(speed);
    }
}
