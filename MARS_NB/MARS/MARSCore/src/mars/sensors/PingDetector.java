/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.sensors;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Sphere;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.ChartValue;
import mars.Helper.Helper;
import mars.PhysicalExchanger;
import mars.ros.MARSNodeMain;
import mars.states.SimState;
import mars.simobjects.SimObject;
import mars.simobjects.SimObjectManager;
import mars.xml.Vector3fAdapter;
import org.ros.message.Time;
import org.ros.node.topic.Publisher;

/**
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class PingDetector extends Sensor implements ChartValue{

    private Geometry PingStart;
    private Geometry PingDirection;

    private SimObjectManager simob_manager;

    private float detection_range = 50.0f;
    
    private Publisher<std_msgs.Float32> publisher = null;
    private std_msgs.Float32 fl;
    private std_msgs.Header header; 

    /**
     * 
     */
    public PingDetector(){
        super();
    }
        
    /**
     *
     * @param simstate 
     */
    public PingDetector(SimState simstate){
        super(simstate);
        this.simob_manager = simstate.getSimob_manager();
    }
    
    public PingDetector(PingDetector sensor){
        super(sensor);
    }

    @Override
    public PhysicalExchanger copy() {
        PingDetector sensor = new PingDetector(this);
        sensor.initAfterJAXB();
        return sensor;
    }

    public void update(float tpf){

    }

    public void init(Node auv_node){
        Sphere sphere7 = new Sphere(16, 16, 0.015f);
        PingStart = new Geometry("CompassStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", ColorRGBA.DarkGray);
        PingStart.setMaterial(mark_mat7);
        PingStart.updateGeometricState();
        PhysicalExchanger_Node.attachChild(PingStart);

        Sphere sphere9 = new Sphere(16, 16, 0.015f);
        PingDirection = new Geometry("CompassYawAxis", sphere9);
        Material mark_mat9 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat9.setColor("Color", ColorRGBA.DarkGray);
        PingDirection.setMaterial(mark_mat9);
        PingDirection.setLocalTranslation(Vector3f.UNIT_X);
        PingDirection.updateGeometricState();
        PhysicalExchanger_Node.attachChild(PingDirection);

        Vector3f ray_start = Vector3f.ZERO;
        Vector3f ray_direction = Vector3f.UNIT_X;
        Geometry mark4 = new Geometry("PingDetector_Arrow", new Arrow(ray_direction.mult(getDetection_range())));
        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat4.setColor("Color", ColorRGBA.DarkGray);
        mark4.setMaterial(mark_mat4);
        mark4.setLocalTranslation(ray_start);
        mark4.updateGeometricState();
        PhysicalExchanger_Node.attachChild(mark4);
        
        PhysicalExchanger_Node.setLocalTranslation(getPosition());
        Quaternion quat = new Quaternion();
        quat.fromAngles(getRotation().getX(),getRotation().getY(),getRotation().getZ());
        PhysicalExchanger_Node.setLocalRotation(quat);

        this.auv_node = auv_node;
        auv_node.attachChild(PhysicalExchanger_Node);
    }

    /**
     *
     * @return
     */
    public float getDetection_range() {
        return (Float)variables.get("detection_range");
    }

    /**
     *
     * @param detection_range
     */
    public void setDetection_range(float detection_range) {
        variables.put("detection_range", detection_range);
    }

    /**
     *
     * @return
     */
    public float getNearestPingerDistance(){
        HashMap<String,SimObject> simobs = simob_manager.getSimObjects();
        float ret = getDetection_range();
        for ( String elem : simobs.keySet() ){
            SimObject simob = (SimObject)simobs.get(elem);
            if(simob.isPinger()){
                float distance = Math.abs((simob.getPosition().subtract(PingStart.getWorldTranslation())).length());
                if( distance <= getDetection_range() && distance < ret ){
                   ret = distance;
                }
            }
        }
        return ret;
    }

    /**
     *
     * @param pinger
     * @return
     */
    public float getPingerDistance(String pinger){
        SimObject simob = simob_manager.getSimObject(pinger);
        if(simob != null && simob.isPinger()){
            float distance = Math.abs((simob.getPosition().subtract(PingStart.getWorldTranslation())).length());
            if(distance <= getDetection_range()){
                return distance;
            }else{
                return getDetection_range();
            }
        }
        return 0.0f;
    }

    /**
     *
     * @return
     */
    public Vector3f getNearestPingerAngle(){
        return Vector3f.ZERO;
    }

    /**
     *
     * @param pinger_vector
     * @return The yaw angle in degree
     */
    private float getYawDegree(Vector3f pinger_vector){
        return (float)(getYawRadiant(pinger_vector)*(180/Math.PI));
    }

    /**
     *
     * @param pinger_vector
     * @return The yaw angle in radiant
     */
    private float getYawRadiant(Vector3f pinger_vector){
        Vector3f vec_roll = PingDirection.getWorldTranslation().subtract(PingStart.getWorldTranslation());

        //rotate the pinger_detector and the pinger to the unitZ
        float angle = vec_roll.angleBetween(Vector3f.UNIT_Z);
        Vector3f crossed = vec_roll.cross(Vector3f.UNIT_Z);
        Matrix3f rot = Helper.getRotationMatrix(angle, crossed.normalize());
        Vector3f rotated = rot.mult(vec_roll);
        Vector3f pingrotated = rot.mult(pinger_vector);
        //System.out.println(Vector3f.UNIT_Z + "/" + vec_roll + ";" + rotated);
        //System.out.println("pinge: " + pinger_vector + ";" + pingrotated);

       /* Vector3f ray_start = PingStart.getWorldTranslation();
        Vector3f ray_direction = pingrotated;
        Geometry mark4 = new Geometry("PingDetector_Arrow", new Arrow(ray_direction.mult(getDetection_range())));
        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat4.setColor("Color", ColorRGBA.DarkGray);
        mark4.setMaterial(mark_mat4);
        mark4.setLocalTranslation(ray_start);
        mark4.updateGeometricState();
        rootNode.attachChild(mark4);*/

        vec_roll = rotated;
        pinger_vector = new Vector3f(pingrotated.getX(),0,pingrotated.getZ());
        if( vec_roll.getX() == 0f && vec_roll.getY() == 0f && vec_roll.getZ() == 0f){
            return 0f;
        }
        Vector3f plus = (pinger_vector.cross(vec_roll)).normalize();
        if( plus.getY() < 0 ){//negativ, vec_roll on the right side of the magnetic north
            return (vec_roll.normalize()).angleBetween(pinger_vector.normalize());
        }else if( plus.getY() == 0){
            if( (pinger_vector.add(vec_roll)).length() <= (vec_roll.length()+pinger_vector.length()) ){//vectors point in same direction
                return 0f;
            }else{//vectors are opposite
                return (float)Math.PI;
            }
        }else{//left side
            return (float)(Math.PI + ( Math.PI - (vec_roll.normalize()).angleBetween(pinger_vector.normalize()) ) );
        }
    }

    /**
     *
     * @param pinger
     * @return angle in radiant
     */
    public float getPingerAngleRadiant(String pinger){
        SimObject simob = simob_manager.getSimObject(pinger);
        if(simob != null && simob.isPinger()){
            Vector3f pinger_vector = (simob.getPosition().subtract(PingStart.getWorldTranslation())).normalize();

            /* debugstuff
            Vector3f ray_start = PingStart.getWorldTranslation();
            Vector3f ray_direction = pinger_vector;
            Geometry mark4 = new Geometry("PingDetector_Arrow", new Arrow(ray_direction.mult(getDetection_range())));
            Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mark_mat4.setColor("Color", ColorRGBA.DarkGray);
            mark4.setMaterial(mark_mat4);
            mark4.setLocalTranslation(ray_start);
            mark4.updateGeometricState();
            rootNode.attachChild(mark4);*/

            float yaw = getYawRadiant(pinger_vector);
            //System.out.println("Yaw!!!! " + yaw);
            return yaw;
        }
        return 0f;
    }

        /**
     *
     * @param pinger
     * @return angle in degree
     */
    public float getPingerAngleDegree(String pinger){
        SimObject simob = simob_manager.getSimObject(pinger);
        if(simob != null && simob.isPinger()){
            Vector3f pinger_vector = (simob.getPosition().subtract(PingStart.getWorldTranslation())).normalize();
            float yaw = getYawDegree(pinger_vector);
            return yaw;
        }
        return 0f;
    }

    /**
     * Nothing implemeted yet
     * @return
     */
    public String getNearestPingerName(){
        return "";
    }

    /**
     *
     */
    public void reset(){

    }

    /**
     * 
     * @param simob_manager
     */
    public void setSimObjectManager(SimObjectManager simob_manager) {
        this.simob_manager = simob_manager;
    }

    /**
     * 
     * @param ros_node
     * @param auv_name
     */
    @Override
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        publisher = ros_node.newPublisher(auv_name + "/" + this.getPhysicalExchangerName(),std_msgs.Float32._TYPE);  
        fl = this.mars_node.getMessageFactory().newFromType(std_msgs.Float32._TYPE);
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
        
        fl.setData((getPingerAngleRadiant("pingpong")));
        
        if( publisher != null ){
            publisher.publish(fl);
        }
    }
    
    @Override
    public Object getChartValue() {
        return getPingerAngleRadiant("pingpong");
    }

    @Override
    public long getSleepTime() {
        return getRos_publish_rate();
    }
}