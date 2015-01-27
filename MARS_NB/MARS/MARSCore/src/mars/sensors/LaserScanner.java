/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
