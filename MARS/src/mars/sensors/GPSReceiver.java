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
import com.jme3.scene.shape.Sphere;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import mars.PhysicalEnvironment;
import mars.PhysicalExchanger;
import mars.ros.MARSNodeMain;
import mars.states.SimState;
import org.ros.message.Time;
import org.ros.node.topic.Publisher;
import sensor_msgs.NavSatFix;

/**
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class GPSReceiver extends Sensor{
    
    @XmlElement(name="Positionmeter")
    Positionmeter pos = new Positionmeter();
    
    ///ROS stuff
    private Publisher<sensor_msgs.NavSatFix> publisher = null;
    private sensor_msgs.NavSatFix fl;
    private sensor_msgs.NavSatStatus NavSatStatus; 
    private std_msgs.Header header; 
    
    private Geometry GPSReceiverGeom;
    
    /**
     * 
     */
    public GPSReceiver(){
        super();
    }
        
    /**
     *
     * @param simstate 
     * @param pe
     */
    public GPSReceiver(SimState simstate,PhysicalEnvironment pe){
        super(simstate);
        this.pe = pe;
        pos.setPhysical_environment(pe);
        pos.setSimState(simState);
    }

    /**
     * 
     * @param simstate 
     */
    public GPSReceiver(SimState simstate){
        super(simstate);
        pos.setSimState(simState);
    }
    
    public GPSReceiver(GPSReceiver sensor){
        super(sensor);
    }

    @Override
    public PhysicalExchanger copy() {
        GPSReceiver sensor = new GPSReceiver(this);
        sensor.initAfterJAXB();
        return sensor;
    }

    /**
     *
     */
    public void init(Node auv_node){
        this.auv_node = auv_node;
        pos.init(auv_node);
        
        Sphere sphere7 = new Sphere(16, 16, 0.04f);
        GPSReceiverGeom = new Geometry("PressureStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", ColorRGBA.White);
        GPSReceiverGeom.setMaterial(mark_mat7);
        GPSReceiverGeom.updateGeometricState();
        PhysicalExchanger_Node.attachChild(GPSReceiverGeom);
        PhysicalExchanger_Node.setLocalTranslation(getReferencePointWorld());
        rootNode.attachChild(PhysicalExchanger_Node);
        this.auv_node = auv_node;
    }

    /**
     *
     * @param tpf
     */
    public void update(float tpf){
        pos.update(tpf);
    }
    
    /**
     *
     */
    public void reset(){
        pos.reset();
    }
    
    /**
     *
     * @return
     */
    public Vector3f getReferencePointGPS() {
        return (Vector3f)variables.get("ReferencePointGPS");
    }

    /**
     *
     * @param ReferencePointGPS 
     */
    public void setReferencePointGPS(Vector3f ReferencePointGPS) {
        variables.put("ReferencePointGPS", ReferencePointGPS);
    }
    
    /**
     *
     * @return
     */
    public Vector3f getReferencePointWorld() {
        return (Vector3f)variables.get("ReferencePointWorld");
    }

    /**
     *
     * @param ReferencePointWorld 
     */
    public void setReferencePointWorld(Vector3f ReferencePointWorld) {
        variables.put("ReferencePointWorld", ReferencePointWorld);
    }
    
    /**
     *
     * @return
     */
    public Float getLatitudeFactor() {
        return (Float)variables.get("LatitudeFactor");
    }

    /**
     *
     * @param LatitudeFactor 
     */
    public void setLatitudeFactor(float LatitudeFactor) {
        variables.put("LatitudeFactor", LatitudeFactor);
    }
    
    @Override
    public void setPhysical_environment(PhysicalEnvironment pe) {
        super.setPhysical_environment(pe);
        pos.setPhysical_environment(pe);
    }
    
    /**
     * 
     * @param simState
     */
    @Override
    public void setSimState(SimState simState) {
        super.setSimState(simState);
        pos.setSimState(simState);
    }
    
    @Override
    public void setPhysicsControl(RigidBodyControl physics_control) {
        super.setPhysicsControl(physics_control);
        pos.setPhysicsControl(physics_control);
    }
    
        /**
     *
     * @param visible
     */
    @Override
    public void setNodeVisibility(boolean visible){
        super.setNodeVisibility(visible);
        pos.setNodeVisibility(visible);
    }

    /**
     *
     * @param name
     */
    @Override
    public void setPhysicalExchangerName(String name){
        super.setPhysicalExchangerName(name);
        pos.setPhysicalExchangerName(name + "_positionmeter");
    }
    
    /**
     * 
     * @param enabled
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        pos.setEnabled(enabled);
    }
    
    /**
     * 
     * @param ros_node
     * @param auv_name
     */
    @Override
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        publisher = ros_node.newPublisher(auv_name + "/" + this.getPhysicalExchangerName(),sensor_msgs.NavSatFix._TYPE);  
        fl = this.mars_node.getMessageFactory().newFromType(sensor_msgs.NavSatFix._TYPE);
        NavSatStatus = this.mars_node.getMessageFactory().newFromType(sensor_msgs.NavSatStatus._TYPE);
        header = this.mars_node.getMessageFactory().newFromType(std_msgs.Header._TYPE);
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
        
        NavSatStatus.setService((short)1);
        NavSatStatus.setStatus((byte)0);
        fl.setStatus(NavSatStatus);
        
        float longitudeFactor = getLatitudeFactor() * (float)Math.cos(getReferencePointGPS().y*(FastMath.PI/180f));
        Vector3f diffPosition= pos.getPosition().subtract(getReferencePointWorld());
        float latitude = (diffPosition.x/getLatitudeFactor())*(180f/FastMath.PI);
        float longitude = (diffPosition.z/longitudeFactor)*(180f/FastMath.PI);
        
        fl.setAltitude(pos.getPositionY());
        fl.setLatitude(getReferencePointGPS().x + latitude);
        fl.setLongitude(getReferencePointGPS().z + longitude); 
        
        if( publisher != null ){
            publisher.publish(fl);
        }
    }
}
