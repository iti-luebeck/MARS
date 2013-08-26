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
import geometry_msgs.Quaternion;
import geometry_msgs.Vector3;
import org.ros.node.topic.Publisher;
import mars.PhysicalEnvironment;
import mars.states.SimState;
import mars.ros.MARSNodeMain;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import mars.PhysicalExchanger;
import org.ros.message.Time;

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
    @XmlElement(name="Orientationmeter")
    Orientationmeter oro = new Orientationmeter();
    
    ///ROS stuff
    private Publisher<sensor_msgs.Imu> publisher = null;
    private sensor_msgs.Imu fl;
    private std_msgs.Header header; 
    
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
        gyro.setPhysical_environment(pe);
        oro.setPhysical_environment(pe);
        acc.setSimState(simState);
        gyro.setSimState(simState);
        oro.setSimState(simState);
    }

    /**
     * 
     * @param simstate 
     */
    public IMU(SimState simstate){
        super(simstate);
        acc.setSimState(simState);
        gyro.setSimState(simState);
        oro.setSimState(simState);
    }
    
    public IMU(IMU sensor){
        super(sensor);
        oro = (Orientationmeter)sensor.getOrientationmeter().copy();
        gyro = (Gyroscope)sensor.getGyroscope().copy();
        acc = (Accelerometer)sensor.getAccelerometer().copy();
    }

    @Override
    public PhysicalExchanger copy() {
        IMU sensor = new IMU(this);
        sensor.initAfterJAXB();
        return sensor;
    }

    /**
     *
     */
    public void init(Node auv_node){
        this.auv_node = auv_node;
        acc.init(auv_node);
        gyro.init(auv_node);
        oro.init(auv_node);
        
        /*Vector3f angax = new Vector3f();
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
        rootNode.attachChild(mark4);*/
    }

    /**
     *
     * @param tpf
     */
    public void update(float tpf){
        acc.update(tpf);
        gyro.update(tpf);
        oro.update(tpf);
    }
    
    /**
     *
     */
    public void reset(){
        acc.reset();
        gyro.reset();
        oro.reset();
    }
    
    @Override
    public void setPhysical_environment(PhysicalEnvironment pe) {
        super.setPhysical_environment(pe);
        acc.setPhysical_environment(pe);
        gyro.setPhysical_environment(pe);
        oro.setPhysical_environment(pe);
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
        oro.setSimState(simState);
    }
    
    @Override
    public void setPhysicsControl(RigidBodyControl physics_control) {
        super.setPhysicsControl(physics_control);
        acc.setPhysicsControl(physics_control);
        gyro.setPhysicsControl(physics_control);
        oro.setPhysicsControl(physics_control);
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
        oro.setNodeVisibility(visible);
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
        oro.setPhysicalExchangerName(name + "_orientationmeter");
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
        oro.setEnabled(enabled);
    }

    public Gyroscope getGyroscope() {
        return gyro;
    }

    public Accelerometer getAccelerometer() {
        return acc;
    }

    public Orientationmeter getOrientationmeter() {
        return oro;
    }
    
    /**
     * 
     * @param ros_node
     * @param auv_name
     */
    @Override
    public void initROS(MARSNodeMain ros_node, String auv_name) { 
        super.initROS(ros_node, auv_name);
        publisher = ros_node.newPublisher(auv_name + "/" + this.getPhysicalExchangerName(),sensor_msgs.Imu._TYPE);  
        fl = this.mars_node.getMessageFactory().newFromType(sensor_msgs.Imu._TYPE);
        header = this.mars_node.getMessageFactory().newFromType(std_msgs.Header._TYPE);
        this.rosinit = true;
    }

    /**
     * 
     */
    @Override
    public void publish() {
        super.publish();
        header.setSeq(rosSequenceNumber++);
        header.setFrameId(this.getRos_frame_id());
        header.setStamp(Time.fromMillis(System.currentTimeMillis()));
        fl.setHeader(header);
        
        Vector3 ang_vec = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Vector3._TYPE);
        ang_vec.setX(gyro.getAngularVelocityXAxis());
        ang_vec.setY(gyro.getAngularVelocityZAxis());// y<-->z because in opengl/lwjgl/jme3 up vector is y not z!
        ang_vec.setZ(gyro.getAngularVelocityYAxis());
        fl.setAngularVelocity(ang_vec);
        
        Quaternion quat = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Quaternion._TYPE);;
        
        com.jme3.math.Quaternion ter_orientation = new com.jme3.math.Quaternion();
        com.jme3.math.Quaternion ter_orientation_rueck = new com.jme3.math.Quaternion();
        ter_orientation.fromAngles(-FastMath.HALF_PI, 0f, 0f);
        ter_orientation_rueck = ter_orientation.inverse();
        float[] bla = oro.getOrientation().toAngles(null);
        
        com.jme3.math.Quaternion jme3_quat = new com.jme3.math.Quaternion();
        //jme3_quat.fromAngles(comp.getRollRadiant(), comp.getYawRadiant(), comp.getPitchRadiant());
        //jme3_quat.fromAngles(0f,comp.getYawRadiant(),0f);
        //System.out.println("jme3_quat: " + jme3_quat);
        jme3_quat.fromAngles(-bla[0],bla[1],-bla[2]);
        
        ter_orientation.multLocal(jme3_quat.multLocal(ter_orientation_rueck));
        
        //jme3_quat.fromAngles(0f, 0f, comp.getPitchRadiant());
        quat.setX(ter_orientation.getX());// switching x and z!!!!
        quat.setY(ter_orientation.getY());
        quat.setZ(ter_orientation.getZ());
        quat.setW(ter_orientation.getW());
        
        //Vector3f acc_jme3_vec = new Vector3f(0f, 0f, 1f);
        Vector3f acc_jme3_vec = acc.getAcceleration();
        Vector3 acc_vec = this.mars_node.getMessageFactory().newFromType(geometry_msgs.Vector3._TYPE);
        com.jme3.math.Quaternion acc_quat = oro.getOrientation().inverse();
        Vector3f acc_jme3_vec2 = acc_quat.mult(acc_jme3_vec);
        acc_vec.setX(acc_jme3_vec2.getX());
        acc_vec.setY(-acc_jme3_vec2.getZ());// y<-->z because in opengl/lwjgl/jme3 up vector is y not z!
        acc_vec.setZ(acc_jme3_vec2.getY());
        //System.out.println("acc.getAcceleration(): " + acc.getAcceleration().length());
        /*acc_vec.x = acc.getAccelerationXAxis();
        acc_vec.y = acc.getAccelerationZAxis();// y<-->z because in opengl/lwjgl/jme3 up vector is y not z!
        acc_vec.z = acc.getAccelerationYAxis();*/
        fl.setLinearAcceleration(acc_vec);
        
        fl.setOrientation(quat);
        
        if( publisher != null ){
            publisher.publish(fl);
        }
    }
}