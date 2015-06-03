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

import com.jme3.collision.CollisionResults;
import com.jme3.math.Vector3f;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import mars.PhysicalExchange.PhysicalExchanger;

/**
 * Tha base class for all lase scanners. Similiar to the sonar.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({Hakuyo.class})
public class LaserScanner extends RayBasedSensor {

    /**
     *
     */
    public LaserScanner() {
        super();
    }

    /**
     *
     * @param sonar
     */
    public LaserScanner(LaserScanner sonar) {
        super(sonar);
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        LaserScanner sensor = new LaserScanner(this);
        sensor.initAfterJAXB();
        return sensor;
    }

    /**
     *
     * @param results
     * @param i
     * @param distance
     * @param direction
     * @return
     */
    @Override
    protected float[] filterRayHitData(CollisionResults results, int i, float distance, Vector3f direction) {
        if (distance >= getMaxRange()) {//too far away
            return null;
        } else if (results.getCollision(i).getContactPoint().y <= pe.getWater_height()) {//forget hits under water
            return null;
        } else if ((distance > getMinRange())) {
            Vector3f cnormal = results.getCollision(i).getContactNormal();
            Vector3f direction_negated = direction.negate();
            float angle = cnormal.angleBetween(direction_negated);
            if (angle > Math.PI / 2) {//sometimes the normal vector isnt right and than we have to much angle
                angle = (float) Math.PI / 2;
            }
            float[] arr_ret = new float[1];
            arr_ret[0] = angle;
            return arr_ret;
        } else {
            return null;
        }
    }

    /**
     * This method is used to encapsulate the raw sonar data with header and
     * tail information. You have to overwrite it and implement you header and
     * tail if you want to use it.
     *
     * @param sondat
     * @return
     */
    @Override
    protected byte[] encapsulateWithHeaderTail(byte[] sondat) {
        return sondat;
    }

    /**
     *
     * @param x
     * @return
     */
    @Override
    protected float calculateAverageNoiseFunction(float x) {
        return ((float) Math.pow(1.1f, Math.abs(x)));
    }

    /**
     *
     * @param x
     * @return
     */
    @Override
    protected float calculateStandardDeviationNoiseFunction(float x) {
        return ((float) Math.pow(1.1f, Math.abs(x)));
    }
}
