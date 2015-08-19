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

import com.jme3.math.Vector3f;
import mars.PhysicalEnvironment;

/**
 * This class is basically a teleporter but since the hanse bag files for the estimated pose dont have the depth we have to make a special teleporter for subscribing to the pressure sensor.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class RosBagPlayer extends Teleporter {

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
    public PhysicalEnvironment getPe() {
        return pe;
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
}
