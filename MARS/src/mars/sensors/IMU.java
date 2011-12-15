/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.scene.Node;
import org.ros.node.topic.Publisher;
import mars.PhysicalEnvironment;
import mars.SimState;
import mars.ros.MARSNodeMain;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import org.ros.message.Time;
import org.ros.message.geometry_msgs.Vector3;

/**
 * An internal measurment unit class. Basiclly it consist of the Accelerometer, Gyroscope and Compass class as an Mixin class.
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class IMU extends Sensor{
    
    @XmlElement(name="Accelerometer")
    Accelerometer acc = new Accelerometer();
    @XmlElement(name="Gyroscope")
    Gyroscope gyro = new Gyroscope();
    
    ///ROS stuff
    private Publisher<org.ros.message.sensor_msgs.Imu> publisher = null;
    private org.ros.message.sensor_msgs.Imu fl = new org.ros.message.sensor_msgs.Imu(); 
    private org.ros.message.std_msgs.Header header = new org.ros.message.std_msgs.Header(); 
    
    /**
     * 
     */
    public IMU(){
        super();
    }
        
    /**
     *
     * @param simstate 
     * @param pe
     */
    public IMU(SimState simstate,PhysicalEnvironment pe){
        super(simstate);
        this.pe = pe;
        acc.setPhysical_environment(pe);
        gyro.setPhysical_environment(pe);
        acc.setSimState(simState);
        gyro.setSimState(simState);
    }

    /**
     * 
     * @param simstate 
     */
    public IMU(SimState simstate){
        super(simstate);
        acc.setSimState(simState);
        gyro.setSimState(simState);
    }

    /**
     *
     */
    public void init(Node auv_node){
        this.auv_node = auv_node;
        acc.init(auv_node);
        gyro.init(auv_node);
    }

    /**
     *
     * @param tpf
     */
    public void update(float tpf){
        acc.update(tpf);
        gyro.update(tpf);
    }
    
    /**
     *
     */
    public void reset(){
        acc.reset();
        gyro.reset();
    }
    
    @Override
    public void setPhysical_environment(PhysicalEnvironment pe) {
        super.setPhysical_environment(pe);
        acc.setPhysical_environment(pe);
        gyro.setPhysical_environment(pe);
    }
    
    /**
     * 
     * @param simState
     */
    @Override
    public void setSimState(SimState simState) {
        super.setSimState(simState);
        acc.setSimState(simState);
        gyro.setSimState(simState);
    }
    
    @Override
    public void setPhysicsControl(RigidBodyControl physics_control) {
        super.setPhysicsControl(physics_control);
        acc.setPhysicsControl(physics_control);
        gyro.setPhysicsControl(physics_control);
    }
    
        /**
     *
     * @param visible
     */
    @Override
    public void setNodeVisibility(boolean visible){
        super.setNodeVisibility(visible);
        acc.setNodeVisibility(visible);
        gyro.setNodeVisibility(visible);
    }

    /**
     *
     * @param name
     */
    @Override
    public void setPhysicalExchangerName(String name){
        super.setPhysicalExchangerName(name);
        acc.setPhysicalExchangerName(name + "_accelerometer");
        gyro.setPhysicalExchangerName(name + "_gyroscope");
    }
    
    /**
     * 
     * @param enabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        acc.setEnabled(enabled);
        gyro.setEnabled(enabled);
    }
    
    /**
     * 
     * @param ros_node
     * @param auv_name
     */
    @Override
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        publisher = ros_node.newPublisher(auv_name + "/" + this.getPhysicalExchangerName(), "sensor_msgs/Imu");  
    }

    /**
     * 
     */
    @Override
    public void publish() {
        //header.seq = 0;
        header.frame_id = "imu";
        header.stamp = Time.fromMillis(System.currentTimeMillis());
        fl.header = header;
        Vector3 acc_vec = new Vector3();
        acc_vec.x = acc.getAccelerationXAxis();
        acc_vec.y = acc.getAccelerationZAxis();// y<-->z because in opengl/lwjgl/jme3 up vector is y not z!
        acc_vec.z = acc.getAccelerationYAxis();
        fl.linear_acceleration = acc_vec;
        Vector3 ang_vec = new Vector3();
        ang_vec.x = gyro.getAngularVelocityXAxis();
        ang_vec.y = gyro.getAngularVelocityZAxis();// y<-->z because in opengl/lwjgl/jme3 up vector is y not z!
        ang_vec.z = gyro.getAngularVelocityYAxis();
        fl.angular_velocity = ang_vec;
        this.publisher.publish(fl);
    }
}
