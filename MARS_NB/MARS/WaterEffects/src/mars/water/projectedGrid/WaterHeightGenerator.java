/*
 * Copyright (c) 2003-2012 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package mars.water.projectedGrid;

import mars.water.projectedGrid.HeightGenerator;
import com.jme3.math.FastMath;
import com.jme3.terrain.noise.basis.ImprovedNoise;

/**
 * <code>WaterHeightGenerator</code> Sample implementation of a water height
 * generator
 *
 * @author Rikard Herlitz (MrCoder)
 */
public class WaterHeightGenerator implements HeightGenerator {

    private float scalexsmall = .04f;
    private float scaleysmall = .02f;
    private float scalexbig = .03f;
    private float scaleybig = .1f;
    private float heightsmall = .25f;
    private float heightbig = .01f;
    private float speedsmall = 1f;
    private float speedbig = 0.5f;
    private int octaves = 0;

    /**
     *
     * @param x
     * @param z
     * @param time
     * @return
     */
    public float getHeight(float x, float z, float time) {
        float zval = z * scaleybig * 4f + time * speedbig * 4f;
        float height = FastMath.sin(zval);
        height *= heightbig;

        if (octaves > 0) {
            float height2 = (float) ImprovedNoise.noise(x * scaleybig, z * scalexbig, time * speedbig) * heightbig;
            height = height * 0.4f + height2 * 0.6f;
        }
        if (octaves > 1) {
            height += ImprovedNoise.noise(x * scaleysmall, z * scalexsmall, time * speedsmall) * heightsmall;
        }
        if (octaves > 2) {
            height += ImprovedNoise.noise(x * scaleysmall * 2.0f, z * scalexsmall * 2.0f, time * speedsmall * 1.5f) * heightsmall * 0.5f;
        }
        if (octaves > 3) {
            height += ImprovedNoise.noise(x * scaleysmall * 4.0f, z * scalexsmall * 4.0f, time * speedsmall * 2.0f) * heightsmall * 0.25f;
        }

        return height; // + waterHeight
    }

    /**
     *
     * @return
     */
    public float getScalexsmall() {
        return scalexsmall;
    }

    /**
     *
     * @param scalexsmall
     */
    public void setScalexsmall(float scalexsmall) {
        this.scalexsmall = scalexsmall;
    }

    /**
     *
     * @return
     */
    public float getScaleysmall() {
        return scaleysmall;
    }

    /**
     *
     * @param scaleysmall
     */
    public void setScaleysmall(float scaleysmall) {
        this.scaleysmall = scaleysmall;
    }

    /**
     *
     * @return
     */
    public float getScalexbig() {
        return scalexbig;
    }

    /**
     *
     * @param scalexbig
     */
    public void setScalexbig(float scalexbig) {
        this.scalexbig = scalexbig;
    }

    /**
     *
     * @return
     */
    public float getScaleybig() {
        return scaleybig;
    }

    /**
     *
     * @param scaleybig
     */
    public void setScaleybig(float scaleybig) {
        this.scaleybig = scaleybig;
    }

    /**
     *
     * @return
     */
    public float getHeightsmall() {
        return heightsmall;
    }

    /**
     *
     * @param heightsmall
     */
    public void setHeightsmall(float heightsmall) {
        this.heightsmall = heightsmall;
    }

    /**
     *
     * @return
     */
    public float getHeightbig() {
        return heightbig;
    }

    /**
     *
     * @param heightbig
     */
    public void setHeightbig(float heightbig) {
        this.heightbig = heightbig;
    }

    /**
     *
     * @return
     */
    public float getSpeedsmall() {
        return speedsmall;
    }

    /**
     *
     * @param speedsmall
     */
    public void setSpeedsmall(float speedsmall) {
        this.speedsmall = speedsmall;
    }

    /**
     *
     * @return
     */
    public float getSpeedbig() {
        return speedbig;
    }

    /**
     *
     * @param speedbig
     */
    public void setSpeedbig(float speedbig) {
        this.speedbig = speedbig;
    }

    /**
     *
     * @return
     */
    public int getOctaves() {
        return octaves;
    }

    /**
     *
     * @param octaves
     */
    public void setOctaves(int octaves) {
        this.octaves = octaves;
    }
}
