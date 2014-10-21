/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import mars.ChartValue;
import mars.Collider;
import org.ros.node.topic.Publisher;
import mars.Helper.NoiseType;
import mars.PhysicalExchanger;
import mars.states.SimState;
import mars.ros.MARSNodeMain;
import mars.server.MARSClientEvent;

/**
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class InfraRedSensor extends RayBasedSensor implements ChartValue{

    private Collider RayDetectable;
    
    //ROS stuff
    private Publisher<std_msgs.Float32> publisher = null;
    private std_msgs.Float32 fl;
    
    /**
     * 
     */
    public InfraRedSensor() {
        super();
    }

    /**
     * 
     * @param simstate
     * @param detectable
     */
    public InfraRedSensor(SimState simstate, Node detectable) {
        super(simstate,detectable);
        //set the logging
        try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
            // Add to the desired logger
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.addHandler(handler);
        } catch (IOException e) { }

        this.RayDetectable = simstate.getCollider();
    }
    
    /**
     *
     * @param sensor
     */
    public InfraRedSensor(InfraRedSensor sensor){
        super(sensor);
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        InfraRedSensor sensor = new InfraRedSensor(this);
        sensor.initAfterJAXB();
        return sensor;
    }
    
    /**
     *
     * @return The exact depth of the current auv
     */
    public float getDistance(){
        if(getNoiseType() == NoiseType.NO_NOISE){
            return getRawDistance();
        }else if(getNoiseType() == NoiseType.UNIFORM_DISTRIBUTION){
            float noise = getUnifromDistributionNoise(getNoiseValue());
            return getRawDistance()+((float)((1f/100f)*noise));
        }else if(getNoiseType() == NoiseType.GAUSSIAN_NOISE_FUNCTION){
            float noise = getGaussianDistributionNoise(getNoiseValue());
            return getRawDistance() + ((float)((1f/100f)*noise));
        }else{
            return getRawDistance();
        }
    }
    
    private float getRawDistance(){
        Vector3f ray_start = this.SonarStart.getWorldTranslation();

        Vector3f ray_direction = (SonarEnd.getWorldTranslation()).subtract(SonarStart.getWorldTranslation());

        float[] infra_data = getRawRayData(ray_start, ray_direction);
        
        return infra_data[0];
    }
    
    /**
     * 
     * @param ros_node
     * @param auv_name
     */
    @Override
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        publisher = ros_node.newPublisher(auv_name + "/" + this.getName(),std_msgs.Float32._TYPE);  
        fl = this.mars_node.getMessageFactory().newFromType(std_msgs.Float32._TYPE);
        this.rosinit = true;
    }

    /**
     * 
     */
    @Override
    public void publish() {
        fl.setData(getDistance());
        if( publisher != null ){
            publisher.publish(fl);
        }
    }
    
    @Override
    public void publishData() {
        super.publishData();
        MARSClientEvent clEvent = new MARSClientEvent(getAuv(), this, getDistance(), System.currentTimeMillis());
        simState.getAuvManager().notifyAdvertisement(clEvent);
    }
    
    /**
     *
     * @return
     */
    @Override
    public Object getChartValue() {
        return getDistance();
    }

    /**
     *
     * @return
     */
    @Override
    public long getSleepTime() {
        return getRos_publish_rate();
    }
}
