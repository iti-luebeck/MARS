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
import mars.states.SimState;

/**
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Dynamixel_AX12PLUS extends Servo {

    /**
     *
     */
    public Dynamixel_AX12PLUS() {
        super();
    }

    /**
     *
     * @param simstate
     * @param MassCenterGeom
     */
    public Dynamixel_AX12PLUS(SimState simstate, Geometry MassCenterGeom) {
        super(simstate, MassCenterGeom);
        setOperatingAngle(5.235987f);
        setResolution(0.005061f);
        setSpeedPerDegree(0.003266f);
    }

    /**
     *
     * @param simstate
     */
    public Dynamixel_AX12PLUS(SimState simstate) {
        super(simstate);
        setOperatingAngle(5.235987f);
        setResolution(0.005061f);
        setSpeedPerDegree(0.003266f);
    }

    /**
     *
     * @param servo
     */
    public Dynamixel_AX12PLUS(Dynamixel_AX12PLUS servo) {
        super(servo);
    }

    /**
     *
     * @return
     */
    @Override
    public Dynamixel_AX12PLUS copy() {
        Dynamixel_AX12PLUS actuator = new Dynamixel_AX12PLUS(this);
        actuator.initAfterJAXB();
        return actuator;
    }
}
