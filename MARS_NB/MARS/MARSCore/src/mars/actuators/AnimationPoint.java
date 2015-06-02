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

import com.jme3.math.Transform;

/**
 * A bag class for the Animator actuator. A transformation and a time when it 
 * should be reached is saved here.
 * 
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class AnimationPoint {
    private Transform transform;
    private float time;

    /**
     *
     */
    public AnimationPoint() {
    }

    /**
     *
     * @param transform
     * @param time
     */
    public AnimationPoint(Transform transform, float time) {
        this.transform = transform;
        this.time = time;
    }

    /**
     *
     * @return
     */
    public float getTime() {
        return time;
    }

    /**
     *
     * @param time
     */
    public void setTime(float time) {
        this.time = time;
    }

    /**
     *
     * @return
     */
    public Transform getTransform() {
        return transform;
    }

    /**
     *
     * @param transform
     */
    public void setTransform(Transform transform) {
        this.transform = transform;
    }
}
