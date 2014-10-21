/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.sensors;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import mars.ChartValue;
import org.ros.node.topic.Publisher;
import mars.Helper.NoiseType;
import mars.PhysicalExchanger;
import mars.states.SimState;
import mars.ros.MARSNodeMain;
import mars.server.MARSClientEvent;

/**
 *  This a basis Velocimeter class. It gives you the linear velocity.
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Velocimeter extends Sensor implements ChartValue{

   ///ROS stuff
    private Publisher<std_msgs.Float32> publisher = null;
    private std_msgs.Float32 fl;
    
    /**
     * 
     */
    public Velocimeter(){
        super();
    }
    
    /**
     *
     * @param simstate 
     */
    public Velocimeter(SimState simstate){
        super(simstate);
    }
    
    /**
     *
     * @param sensor
     */
    public Velocimeter(Velocimeter sensor){
        super(sensor);
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        Velocimeter sensor = new Velocimeter(this);
        sensor.initAfterJAXB();
        return sensor;
    }

    public void update(float tpf){

    }

    /**
     *
     */
    @Override
    public void init(Node auv_node){
        super.init(auv_node);
    }

    /**
     * 
     * @return
     */
    public Vector3f getLinearVelocity(){
        if(getNoiseType() == NoiseType.NO_NOISE){
            return getLinearVelocityRaw();
        }else if(getNoiseType() == NoiseType.UNIFORM_DISTRIBUTION){
            float noise = getUnifromDistributionNoise(getNoiseValue());
            Vector3f noised = new Vector3f(getLinearVelocityRaw().x+((float)((1f/100f)*noise)),getLinearVelocityRaw().y+((float)((1f/100f)*noise)),getLinearVelocityRaw().z+((float)((1f/100f)*noise)));
            return noised;
        }else if(getNoiseType() == NoiseType.GAUSSIAN_NOISE_FUNCTION){
            float noise = getGaussianDistributionNoise(getNoiseValue());
            Vector3f noised = new Vector3f(getLinearVelocityRaw().x+((float)((1f/100f)*noise)),getLinearVelocityRaw().y+((float)((1f/100f)*noise)),getLinearVelocityRaw().z+((float)((1f/100f)*noise)));
            return noised;
        }else{
            return getLinearVelocityRaw();
        }
    }

    /**
     *
     * @return
     */
    private Vector3f getLinearVelocityRaw(){
        return physics_control.getLinearVelocity();
    }

    /**
     *
     * @return
     */
    public float getLinearVelocityXAxis(){
        return physics_control.getLinearVelocity().x;
    }

    /**
     *
     * @return
     */
    public float getLinearVelocityYAxis(){
        return physics_control.getLinearVelocity().y;
    }

    /**
     *
     * @return
     */
    public float getLinearVelocityZAxis(){
        return physics_control.getLinearVelocity().z;
    }

    /**
     *
     */
    public void reset(){

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
        fl.setData(getLinearVelocity().length());
        if( publisher != null ){
            publisher.publish(fl);
        }
    }
    
    @Override
    public void publishData() {
        super.publishData();
        MARSClientEvent clEvent = new MARSClientEvent(getAuv(), this, getLinearVelocity().length(), System.currentTimeMillis());
        simState.getAuvManager().notifyAdvertisement(clEvent);
    }
    
    /**
     *
     * @return
     */
    @Override
    public Object getChartValue() {
        return getLinearVelocity().length();
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
