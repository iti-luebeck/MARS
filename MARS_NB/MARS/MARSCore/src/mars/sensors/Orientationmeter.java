/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

import com.jme3.math.FastMath;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import mars.ChartValue;
import mars.NoiseType;
import mars.PhysicalEnvironment;
import mars.PhysicalExchanger;
import mars.ros.MARSNodeMain;
import mars.server.MARSClientEvent;
import mars.states.SimState;
import org.ros.message.Time;
import org.ros.node.topic.Publisher;

/**
 * Gives the exact orientation of the auv.
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Orientationmeter extends Sensor implements ChartValue{
    
    Quaternion new_orientation = new Quaternion();
    Quaternion old_orientation = new Quaternion();
        
    ///ROS stuff
    private Publisher<geometry_msgs.PoseStamped> publisher = null;
    private geometry_msgs.PoseStamped fl;
    private std_msgs.Header header; 
    
    /**
     * 
     */
    public Orientationmeter(){
        super();
    }
        
    /**
     *
     * @param simstate 
     * @param pe
     */
    public Orientationmeter(SimState simstate,PhysicalEnvironment pe){
        super(simstate);
        this.pe = pe;
    }

    /**
     * 
     * @param simstate 
     */
    public Orientationmeter(SimState simstate){
        super(simstate);
    }
    
    public Orientationmeter(Orientationmeter sensor){
        super(sensor);
    }

    @Override
    public PhysicalExchanger copy() {
        Orientationmeter sensor = new Orientationmeter(this);
        sensor.initAfterJAXB();
        return sensor;
    }

    /**
     *
     */
    public void init(Node auv_node){
        this.auv_node = auv_node;
    }

    /**
     *
     * @param tpf
     */
    public void update(float tpf){
        new_orientation = physics_control.getPhysicsRotation();//get the new velocity
        old_orientation = new_orientation.clone();
    }
    
    /**
     *
     * @param addedOrientation 
     */
    public void setAddedOrientation(Vector3f addedOrientation) {
        variables.put("addedOrientation", addedOrientation);
    }

    /**
     *
     * @return
     */
    public Vector3f getAddedOrientation() {
        return (Vector3f)variables.get("addedOrientation");
    }

    /**
     * 
     * @return
     */
    public Quaternion getOrientation(){
        if(getNoise_type() == NoiseType.NO_NOISE){
            return getOrientationRaw();
        }else if(getNoise_type() == NoiseType.UNIFORM_DISTRIBUTION){
            float noise = getUnifromDistributionNoise(getNoise_value());
            Quaternion noised = new Quaternion(getOrientationRaw().getX()+((float)((1f/100f)*noise)),getOrientationRaw().getY()+((float)((1f/100f)*noise)),getOrientationRaw().getY()+((float)((1f/100f)*noise)),getOrientationRaw().getW()+((float)((1f/100f)*noise)));
            return noised;
        }else if(getNoise_type() == NoiseType.GAUSSIAN_NOISE_FUNCTION){
            float noise = getGaussianDistributionNoise(getNoise_value());
            Quaternion noised = new Quaternion(getOrientationRaw().getX()+((float)((1f/100f)*noise)),getOrientationRaw().getY()+((float)((1f/100f)*noise)),getOrientationRaw().getZ()+((float)((1f/100f)*noise)),getOrientationRaw().getW()+((float)((1f/100f)*noise)));
            return noised;
        }else{
            return getOrientationRaw();
        }
    }

    /**
     *
     * @return
     */
    private Quaternion getOrientationRaw(){
        Quaternion quat = new Quaternion();
        quat.fromAngles(getAddedOrientation().getX(),getAddedOrientation().getY(),getAddedOrientation().getZ());
        return physics_control.getPhysicsRotation().mult(quat);
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
        publisher = ros_node.newPublisher(auv_name + "/" + this.getName(),geometry_msgs.PoseStamped._TYPE);  
        fl = this.mars_node.getMessageFactory().newFromType(geometry_msgs.PoseStamped._TYPE);
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
        
        geometry_msgs.Quaternion quat = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Quaternion._TYPE);
        quat.setX(getOrientation().getX());
        quat.setY(getOrientation().getY());
        quat.setZ(getOrientation().getZ());
        quat.setW(getOrientation().getW());
        
        geometry_msgs.Pose pose = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Pose._TYPE);
        pose.setOrientation(quat);
        fl.setPose(pose);
        
        if( publisher != null ){
            publisher.publish(fl);
        }
    }    
    
    @Override
    public void publishData() {
        super.publishData();
        MARSClientEvent clEvent = new MARSClientEvent(getAuv(), this, getOrientation(), System.currentTimeMillis());
        simState.getAuvManager().notifyAdvertisement(clEvent);
    }
        
    @Override
    public Object getChartValue() {
        float[] bla = getOrientation().toAngles(null);
        return new Vector3f(bla[0],bla[1],bla[2]);
    }

    @Override
    public long getSleepTime() {
        return getRos_publish_rate();
    }
}
