/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors.sonar;

import com.jme3.collision.CollisionResults;
import com.jme3.math.Vector3f;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.sensors.RayBasedSensor;

/**
 * This is the main sonar class. It supports rotating and non-rotating sonars.
 * But you can also use it as a basis for other sonars(Tritech,Imaginex,...).
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({ImagenexSonar_852_Echo.class, ImagenexSonar_852_Scanning.class, TriTech.class})
public class Sonar extends RayBasedSensor {

    /**
     *
     */
    public Sonar() {
        super();
        try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
            // Add to the desired logger
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.addHandler(handler);
        } catch (IOException e) {
        }
    }

    /**
     *
     * @param sonar
     */
    public Sonar(Sonar sonar) {
        super(sonar);
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        Sonar sensor = new Sonar(this);
        sensor.initAfterJAXB();
        return sensor;
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
        } else if (results.getCollision(i).getContactPoint().y >= pe.getWater_height()) {//forget hits over water
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
     *
     * @param x
     * @return
     */
    @Override
    protected float calculateAverageNoiseFunction(float x) {
        return ((float) Math.pow(1.1f, (float) Math.abs(x)));
    }

    /**
     *
     * @param x
     * @return
     */
    @Override
    protected float calculateStandardDeviationNoiseFunction(float x) {
        return ((float) Math.pow(1.1f, (float) Math.abs(x)));
    }
}
