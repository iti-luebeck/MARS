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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.ros.node.topic.Publisher;
import mars.NoiseType;
import mars.PhysicalEnvironment;
import mars.PhysicalExchanger;
import mars.states.SimState;
import mars.ros.MARSNodeMain;
import mars.xml.Vector3fAdapter;

/**
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class SalinitySensor extends Sensor{
    
    private Geometry SalinitySensorStart;

    ///ROS stuff
    private Publisher<std_msgs.Float32> publisher = null;
    private std_msgs.Float32 fl;
    
    /**
     * 
     */
    public SalinitySensor(){
        super();
    }
        
     /**
     *
     * @param simstate 
      * @param pe
     */
    public SalinitySensor(SimState simstate, PhysicalEnvironment pe){
        super(simstate);
        this.pe = pe;
    }

    /**
     *
     * @param simstate 
     */
    public SalinitySensor(SimState simstate){
        super(simstate);
    }
    
    public SalinitySensor(SalinitySensor sensor){
        super(sensor);
    }

    @Override
    public PhysicalExchanger copy() {
        SalinitySensor sensor = new SalinitySensor(this);
        sensor.initAfterJAXB();
        return sensor;
    }

    public void update(float tpf){

    }

    public void init(Node auv_node){

        Sphere sphere7 = new Sphere(16, 16, 0.025f);
        SalinitySensorStart = new Geometry("SalinitySensor", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", ColorRGBA.White);
        SalinitySensorStart.setMaterial(mark_mat7);
        //SalinitySensorStart.setLocalTranslation(SalinitySensorStartVector);
        SalinitySensorStart.updateGeometricState();
        PhysicalExchanger_Node.attachChild(SalinitySensorStart);
        PhysicalExchanger_Node.setLocalTranslation(getPosition());
        Quaternion quat = new Quaternion();
        quat.fromAngles(getRotation().getX(),getRotation().getY(),getRotation().getZ());
        PhysicalExchanger_Node.setLocalRotation(quat);
        auv_node.attachChild(PhysicalExchanger_Node);
        this.auv_node = auv_node;
    }

     /**
     *
     * @return The exact temperature of the current auv enviroemnt in C°
     */
    public float getSalinity(){
        if(getNoise_type() == NoiseType.NO_NOISE){
            return getSalinityRaw();
        }else if(getNoise_type() == NoiseType.UNIFORM_DISTRIBUTION){
            float noise = getUnifromDistributionNoise(getNoise_value());
            return getSalinityRaw()+((float)((1f/100f)*noise));
        }else if(getNoise_type() == NoiseType.GAUSSIAN_NOISE_FUNCTION){
            float noise = getGaussianDistributionNoise(getNoise_value());
            return getSalinityRaw() + ((float)((1f/100f)*noise));
        }else{
            return getSalinityRaw();
        }
    }

     /**
     *
      * @param noise The boundary for the random generator starting always from 0 to noise value
      * @return The Temperature of the current auv enviroment with a random noise from 0 to noise value in C°
     */
    private float getSalinityRaw(){
        return pe.getFluid_salinity();
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
        publisher = ros_node.newPublisher(auv_name + "/" + this.getPhysicalExchangerName(),std_msgs.Float32._TYPE);  
        fl = this.mars_node.getMessageFactory().newFromType(std_msgs.Float32._TYPE);
    }

    /**
     * 
     */
    @Override
    public void publish() {
        fl.setData(getSalinity());
        
        if( publisher != null ){
            publisher.publish(fl);
        }
    }
}
