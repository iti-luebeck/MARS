/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.scene.Node;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import mars.PhysicalEnvironment;
import mars.ros.MARSNodeMain;
import mars.states.SimState;
import org.ros.message.Time;
import org.ros.node.topic.Publisher;

/**
 * Gives the exact Pose(Position/Orientation). Mixin class.
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Posemeter extends Sensor{
    
    @XmlElement(name="Positionmeter")
    Positionmeter pos = new Positionmeter();
    @XmlElement(name="Orientationmeter")
    Orientationmeter oro = new Orientationmeter();
    @XmlElement(name="Compass")
    Compass comp = new Compass();
    
    ///ROS stuff
    private Publisher<org.ros.message.geometry_msgs.PoseStamped> publisher = null;
    private org.ros.message.geometry_msgs.PoseStamped fl = new org.ros.message.geometry_msgs.PoseStamped(); 
    private org.ros.message.std_msgs.Header header = new org.ros.message.std_msgs.Header(); 
    
    /**
     * 
     */
    public Posemeter(){
        super();
    }
        
    /**
     *
     * @param simstate 
     * @param pe
     */
    public Posemeter(SimState simstate,PhysicalEnvironment pe){
        super(simstate);
        this.pe = pe;
        pos.setPhysical_environment(pe);
        oro.setPhysical_environment(pe);
        comp.setPhysical_environment(pe);
        pos.setSimState(simState);
        oro.setSimState(simState);
        comp.setSimState(simState);
    }
    
    /**
     * 
     * @param simstate 
     */
    public Posemeter(SimState simstate){
        super(simstate);
        pos.setSimState(simState);
        oro.setSimState(simState);
        comp.setSimState(simState);
    }

    /**
     *
     */
    public void init(Node auv_node){
        this.auv_node = auv_node;
        pos.init(auv_node);
        oro.init(auv_node);
        comp.init(auv_node);
    }

    /**
     *
     * @param tpf
     */
    public void update(float tpf){
        pos.update(tpf);
        oro.update(tpf);
        comp.update(tpf);
    }
    
    /**
     *
     */
    public void reset(){
        pos.reset();
        oro.reset();
        comp.reset();
    }
    
    @Override
    public void setPhysical_environment(PhysicalEnvironment pe) {
        super.setPhysical_environment(pe);
        pos.setPhysical_environment(pe);
        oro.setPhysical_environment(pe);
        comp.setPhysical_environment(pe);
    }
    
    /**
     * 
     * @param simState
     */
    @Override
    public void setSimState(SimState simState) {
        super.setSimState(simState);
        pos.setSimState(simState);
        oro.setSimState(simState);
        comp.setSimState(simState);
    }
    
    @Override
    public void setPhysicsControl(RigidBodyControl physics_control) {
        super.setPhysicsControl(physics_control);
        pos.setPhysicsControl(physics_control);
        oro.setPhysicsControl(physics_control);
        comp.setPhysicsControl(physics_control);
    }
    
        /**
     *
     * @param visible
     */
    @Override
    public void setNodeVisibility(boolean visible){
        super.setNodeVisibility(visible);
        pos.setNodeVisibility(visible);
        oro.setNodeVisibility(visible);
        comp.setNodeVisibility(visible);
    }

    /**
     *
     * @param name
     */
    @Override
    public void setPhysicalExchangerName(String name){
        super.setPhysicalExchangerName(name);
        pos.setPhysicalExchangerName(name + "_positionmeter");
        oro.setPhysicalExchangerName(name + "_orientationmeter");
        comp.setPhysicalExchangerName(name + "_compass");
    }
    
    /**
     * 
     * @param enabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        pos.setEnabled(enabled);
        oro.setEnabled(enabled);
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
        publisher = ros_node.newPublisher(auv_name + "/" + this.getPhysicalExchangerName(), "geometry_msgs/PoseStamped");  
    }

    /**
     * 
     */
    @Override
    public void publish() {
        header.frame_id = this.getRos_frame_id();
        header.stamp = Time.fromMillis(System.currentTimeMillis());
        fl.header = header;
        org.ros.message.geometry_msgs.Point point = new org.ros.message.geometry_msgs.Point();
        point.x = pos.getPosition().x;
        point.y = pos.getPosition().z;//dont forget to switch y and z!!!!
        point.z = pos.getPosition().y;
        org.ros.message.geometry_msgs.Quaternion orientation = new org.ros.message.geometry_msgs.Quaternion();
        
        Quaternion ter_orientation = new Quaternion();
        //ter_orientation.fromAngles(FastMath.PI, -FastMath.HALF_PI, 0f);
        //ter_orientation.fromAngles(0f, -FastMath.HALF_PI, 0f);
        ter_orientation.fromAngles(-FastMath.HALF_PI, 0f, 0f);
        float[] bla = oro.getOrientation().toAngles(null);
        //System.out.println("oroa:" + "yaw: " + bla[0] + " roll: " + bla[1] + " pitch: " + bla[2]);
        //System.out.println("oro:" + oro.getOrientation());
        /*orientation.x = oro.getOrientation().mult(ter_orientation).getX();
        orientation.y = oro.getOrientation().mult(ter_orientation).getZ();//dont forget to switch y and z!!!!
        orientation.z = oro.getOrientation().mult(ter_orientation).getY();
        orientation.w = oro.getOrientation().mult(ter_orientation).getW();*/
        /*orientation.x = oro.getOrientation().getX();
        orientation.y = oro.getOrientation().getY();//dont forget to switch y and z!!!!
        orientation.z = oro.getOrientation().getZ();
        orientation.w = oro.getOrientation().getW();*/
        com.jme3.math.Quaternion jme3_quat = new com.jme3.math.Quaternion();
        jme3_quat.fromAngles(comp.getPitchRadiant(), comp.getYawRadiant(), comp.getRollRadiant());
        //jme3_quat.fromAngles(comp.getYawRadiant(), 0f, 0f);
        orientation.x = jme3_quat.mult(ter_orientation).getX();// switching x and z!!!!
        orientation.y = jme3_quat.mult(ter_orientation).getY();
        orientation.z = jme3_quat.mult(ter_orientation).getZ();
        orientation.w = jme3_quat.mult(ter_orientation).getW();
        org.ros.message.geometry_msgs.Pose pose = new org.ros.message.geometry_msgs.Pose();
        pose.position = point;
        pose.orientation = orientation;
        fl.pose = pose;      
        this.publisher.publish(fl);
    }    
}