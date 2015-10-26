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
package mars.water;

import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a vehicle to be tracked by the {@link WaterGridFilter}.
 *
 * @author John Paul Jonte
 */
public class Vehicle {

    /**
     * {@link Spatial} of the vehicle to be tracked.
     */
    private Spatial spatial;
    /**
     * The vehicle's width.
     */
    private float width;
    /**
     * List containing tracking points.
     */
    private List<Vector3f> trail;
    private ParticleEmitter emitter;

    /**
     * Creates a new vehicle.
     *
     * @param spatial {@link Spatial} of the vehicle to be tracked.
     */
    public Vehicle(Spatial spatial) {
        this.spatial = spatial;

        trail = new ArrayList<Vector3f>();

        // determine vehicle width
        BoundingVolume volume = spatial.getWorldBound();

        if (volume instanceof BoundingBox) {
            // assume object is axis-aligned and square
            width = ((BoundingBox) volume).getExtent(null).x;
        }
    }

    /**
     * Gets the vehicle's Spatial.
     *
     * @return vehicle's spatial
     */
    public Spatial getSpatial() {
        return spatial;
    }

    /**
     * Gets the list of tracking points for this vehicle.
     *
     * @return vehicle trail
     */
    public List<Vector3f> getTrail() {
        return trail;
    }

    /**
     * Gets the vehicle's width.
     *
     * @return width
     */
    public float getWidth() {
        return width;
    }

    /**
     *
     * @return
     */
    public ParticleEmitter getEmitter() {
        return emitter;
    }

    /**
     *
     * @param emitter
     */
    public void setEmitter(ParticleEmitter emitter) {
        this.emitter = emitter;
    }
}
