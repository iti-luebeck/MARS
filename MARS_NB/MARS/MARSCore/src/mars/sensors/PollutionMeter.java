/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import mars.ChartValue;
import mars.Initializer;
import org.ros.node.topic.Publisher;
import mars.NoiseType;
import mars.PhysicalEnvironment;
import mars.PhysicalExchanger;
import mars.states.SimState;
import mars.ros.MARSNodeMain;
import mars.server.MARSClientEvent;
import org.ros.message.Time;

/**
 * Returns "pollution" of the water.
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class PollutionMeter extends Sensor implements ChartValue{

    private Geometry PollutionMeterStart;
    
    private Initializer initer;

    ///ROS stuff
    private Publisher<geometry_msgs.Vector3Stamped> publisher = null;
    private geometry_msgs.Vector3Stamped fl;
    private std_msgs.Header header; 
    
    /**
     * 
     */
    public PollutionMeter(){
        super();
    }
        
     /**
     *
     * @param simstate 
      * @param pe
     */
    public PollutionMeter(SimState simstate, PhysicalEnvironment pe){
        super(simstate);
        this.pe = pe;
    }

    /**
     *
     * @param simstate 
     */
    public PollutionMeter(SimState simstate){
        super(simstate);
    }
    
    public PollutionMeter(PollutionMeter sensor){
        super(sensor);
    }

    @Override
    public PhysicalExchanger copy() {
        PollutionMeter sensor = new PollutionMeter(this);
        sensor.initAfterJAXB();
        return sensor;
    }

    /**
     *
     */
    public void init(Node auv_node){

        Sphere sphere7 = new Sphere(16, 16, 0.025f);
        PollutionMeterStart = new Geometry("FlowMeterStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", ColorRGBA.White);
        PollutionMeterStart.setMaterial(mark_mat7);
        //FlowMeterStart.setLocalTranslation(getFlowMeterStartVector());
        PollutionMeterStart.updateGeometricState();
        PhysicalExchanger_Node.attachChild(PollutionMeterStart);
        PhysicalExchanger_Node.setLocalTranslation(getPosition());
        Quaternion quat = new Quaternion();
        quat.fromAngles(getRotation().getX(),getRotation().getY(),getRotation().getZ());
        PhysicalExchanger_Node.setLocalRotation(quat);
        auv_node.attachChild(PhysicalExchanger_Node);
        this.auv_node = auv_node;
    }

    public void update(float tpf){

    }

     /**
     *
     * @return The exact depth of the current auv
     */
    public float getPollution(){
        float value = 0;
        if(getNoise_type() == NoiseType.NO_NOISE){
            value = getRawPollution();
        }else if(getNoise_type() == NoiseType.UNIFORM_DISTRIBUTION){
            float noise = getUnifromDistributionNoise(getNoise_value());
            value =  (getRawPollution()+((float)((1f/100f)*noise)));
        }else if(getNoise_type() == NoiseType.GAUSSIAN_NOISE_FUNCTION){
            float noise = getGaussianDistributionNoise(getNoise_value());
            value = (getRawPollution()+((float)((1f/100f)*noise)));
        }else{
            value = getRawPollution();
        }
        if(value < 0f){//no negative values through noise
            value = 0f;
        }
        return value;
    }

     /**
     *
     * @return The depth of the current auv 
     */
    private float getRawPollution(){
        Vector3f sensorLocation = PollutionMeterStart.getWorldTranslation();
        return initer.getPollution(sensorLocation);
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
     * @param pe
     */
    public void setPe(PhysicalEnvironment pe) {
        this.pe = pe;
    }
    
    /**
     *
     * @return
     */
    public Initializer getIniter() {
        return initer;
    }

    /**
     *
     * @param initer
     */
    public void setIniter(Initializer initer) {
        this.initer = initer;
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
        publisher = ros_node.newPublisher(auv_name + "/" + this.getName(),geometry_msgs.Vector3Stamped._TYPE);  
        fl = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Vector3Stamped._TYPE);
        header = this.mars_node.getMessageFactory().newFromType(std_msgs.Header._TYPE);
        this.rosinit = true;
    }

    /**
     * 
     */
    @Override
    public void publish() {
        header.setSeq(rosSequenceNumber++);
        header.setFrameId(this.getRos_frame_id());
        header.setStamp(Time.fromMillis(System.currentTimeMillis()));
        fl.setHeader(header);
        
        geometry_msgs.Vector3 vec = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Vector3._TYPE);
        vec.setX(0f);
        vec.setY(getPollution());
        vec.setZ(0f);

        fl.setVector(vec);
        if( publisher != null ){
            publisher.publish(fl);
        }
    }
    
    @Override
    public void publishData() {
        super.publishData();
        MARSClientEvent clEvent = new MARSClientEvent(getAuv(), this, getPollution(), System.currentTimeMillis());
        simState.getAuvManager().notifyAdvertisement(clEvent);
    }
    
    @Override
    public Object getChartValue() {
        return getPollution();
    }

    @Override
    public long getSleepTime() {
        return getRos_publish_rate();
    }
}
