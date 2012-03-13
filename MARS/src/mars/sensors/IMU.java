/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import org.ros.node.topic.Publisher;
import mars.PhysicalEnvironment;
import mars.states.SimState;
import mars.ros.MARSNodeMain;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import org.ros.message.Time;
import org.ros.message.geometry_msgs.Quaternion;
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
    @XmlElement(name="Compass")
    Compass comp = new Compass();
    
    ///ROS stuff
    private Publisher<org.ros.message.sensor_msgs.Imu> publisher = null;
    private org.ros.message.sensor_msgs.Imu fl = new org.ros.message.sensor_msgs.Imu(); 
    private org.ros.message.std_msgs.Header header = new org.ros.message.std_msgs.Header(); 
    
    Arrow arrow;
    
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
        comp.setPhysical_environment(pe);
        gyro.setPhysical_environment(pe);
        acc.setSimState(simState);
        gyro.setSimState(simState);
        comp.setSimState(simState);
    }

    /**
     * 
     * @param simstate 
     */
    public IMU(SimState simstate){
        super(simstate);
        acc.setSimState(simState);
        gyro.setSimState(simState);
        comp.setSimState(simState);
    }

    /**
     *
     */
    public void init(Node auv_node){
        this.auv_node = auv_node;
        acc.init(auv_node);
        gyro.init(auv_node);
        comp.init(auv_node);
        
        Vector3f angax = new Vector3f();
        //jme3_quat.toAngleAxis(angax);
        Vector3f ray_start =  new Vector3f(0f, 0f, 0f);
        Vector3f ray_direction = angax;//new Vector3f(jme3_quat.getX(), jme3_quat.getY(), jme3_quat.getZ());
        arrow = new Arrow(ray_direction.mult(1f));
        Geometry mark4 = new Geometry("VideoCamera_Arrow_1", arrow);
        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat4.setColor("Color", ColorRGBA.Green);
        mark4.setMaterial(mark_mat4);
        mark4.setLocalTranslation(ray_start);
        mark4.updateGeometricState();
        rootNode.attachChild(mark4);
    }

    /**
     *
     * @param tpf
     */
    public void update(float tpf){
        acc.update(tpf);
        gyro.update(tpf);
        comp.update(tpf);
    }
    
    /**
     *
     */
    public void reset(){
        acc.reset();
        gyro.reset();
        comp.reset();
    }
    
    @Override
    public void setPhysical_environment(PhysicalEnvironment pe) {
        super.setPhysical_environment(pe);
        acc.setPhysical_environment(pe);
        gyro.setPhysical_environment(pe);
        comp.setPhysical_environment(pe);
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
        comp.setSimState(simState);
    }
    
    @Override
    public void setPhysicsControl(RigidBodyControl physics_control) {
        super.setPhysicsControl(physics_control);
        acc.setPhysicsControl(physics_control);
        gyro.setPhysicsControl(physics_control);
        comp.setPhysicsControl(physics_control);
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
        comp.setNodeVisibility(visible);
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
        comp.setPhysicalExchangerName(name + "_compass");
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
        comp.setEnabled(enabled);
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
        header.frame_id = this.getRos_frame_id();
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
        Quaternion quat = new Quaternion();
        com.jme3.math.Quaternion jme3_quat = new com.jme3.math.Quaternion();
        jme3_quat.fromAngles(comp.getPitchRadiant(), comp.getYawRadiant(), comp.getRollRadiant());
        
        com.jme3.math.Quaternion ter_orientation = new com.jme3.math.Quaternion();
        ter_orientation.fromAngles(-FastMath.HALF_PI, 0f, 0f);
        //jme3_quat.fromAngles(0f, 0f, comp.getPitchRadiant());
        quat.x = jme3_quat.mult(ter_orientation).getX();// switching x and z!!!!
        quat.y = jme3_quat.mult(ter_orientation).getY();
        quat.z = jme3_quat.mult(ter_orientation).getZ();
        quat.w = jme3_quat.mult(ter_orientation).getW();
        /*quat.x = jme3_quat.getZ();// switching x and z!!!!
        quat.y = jme3_quat.getY();
        quat.z = jme3_quat.getX();
        quat.w = jme3_quat.getW();*/
        System.out.println("yaw: " + comp.getYawRadiant() + " pitch: " + comp.getPitchRadiant() + " roll: " + comp.getRollRadiant());
        System.out.println("jme3_quat: " + jme3_quat);
        float[] ff = jme3_quat.toAngles(null);
        System.out.println("jme3_quat: " + ff[0] + "/" + ff[1] + "/" + ff[2]);
        /*Vector3f angax = new Vector3f();
        jme3_quat.toAngleAxis(angax);
        System.out.println("angax: " + angax);
        Vector3f ray_start =  new Vector3f(0f, 0f, 0f);
        Vector3f ray_direction = angax;//new Vector3f(jme3_quat.getX(), jme3_quat.getY(), jme3_quat.getZ());
        arrow.setArrowExtent(ray_direction.mult(1f));*/
        
        fl.orientation = quat;
        this.publisher.publish(fl);
    }
}
