/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Sphere;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import mars.NoiseType;
import mars.PhysicalEnvironment;
import mars.PhysicalExchanger;
import mars.PickHint;
import mars.states.SimState;
import mars.ros.MARSNodeMain;
import org.jboss.netty.buffer.ChannelBuffers;
import org.ros.message.Time;
import org.ros.node.topic.Publisher;


/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso( {Hakuyo.class} )
public class LaserScanner extends RayBasedSensor{
  
    /**
     * 
     */
    public LaserScanner(){
        super();
        try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
            // Add to the desired logger
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.addHandler(handler);
        } catch (IOException e) { }
    }
    
    public LaserScanner(LaserScanner sonar){
        super(sonar);
    }

    @Override
    public PhysicalExchanger copy() {
        LaserScanner sensor = new LaserScanner(this);
        sensor.initAfterJAXB();
        return sensor;
    }

    @Override
    protected float[] filterRayHitData(CollisionResults results, int i, float distance, Vector3f direction){
        if(distance >= getMaxRange()){//too far away
            //System.out.println("too far away");
            return null;
        }else if(results.getCollision(i).getContactPoint().y <= pe.getWater_height()){//forget hits under water
            return null;
        }else if ((distance > getMinRange())) {
            //first = results2.getCollision(i).getContactPoint();
            Vector3f cnormal = results.getCollision(i).getContactNormal();
            Vector3f direction_negated = direction.negate();
            float angle = cnormal.angleBetween(direction_negated);
            if(angle > Math.PI/2){//sometimes the normal vector isnt right and than we have to much angle
                angle = (float)Math.PI/2;
            }

            /*System.out.println("angle: " + angle);
            System.out.println("cnor: " + cnormal);
            System.out.println("direc: " + direction_negated);*/
            //System.out.println(first);
            //ret = (first.subtract(ray_start)).length();
            float[] arr_ret = new float[1];
            arr_ret[0] = angle;
            //System.out.println(distance);
            return arr_ret;
        }else{
            return null;
        }
    }

    /**
     * This method is used to encapsulate the raw sonar data with header and 
     * tail information. You have to overwrite it and implement you header 
     * and tail if you want to use it.
     * @param sondat
     * @return
     */
    @Override
    protected byte[] encapsulateWithHeaderTail(byte[] sondat){
        return sondat;
    }

    /**
     *
     * @param x
     * @return
     */
    @Override
    protected float calculateAverageNoiseFunction(float x){
        return ((float)Math.pow(1.1f, (float)Math.abs(x)) );
    }

    /**
     *
     * @param x
     * @return
     */
    @Override
    protected float calculateStandardDeviationNoiseFunction(float x){
        return ((float)Math.pow(1.1f, (float)Math.abs(x)) );
    }
}
