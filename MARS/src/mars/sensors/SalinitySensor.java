/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import org.ros.node.topic.Publisher;
import mars.NoiseType;
import mars.PhysicalEnvironment;
import mars.SimState;

/**
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class SalinitySensor extends Sensor{
    
    private Geometry SalinitySensorStart;

    private Vector3f SalinitySensorStartVector;

    ///ROS stuff
    private Publisher<org.ros.message.std_msgs.Float32> publisher = null;
    private org.ros.message.std_msgs.Float32 fl = new org.ros.message.std_msgs.Float32(); 
    
    public SalinitySensor(){
        super();
    }
        
     /**
     *
      * @param simauv 
      * @param pe
     */
    public SalinitySensor(SimState simstate, PhysicalEnvironment pe){
        super(simstate);
        this.pe = pe;
    }

    /**
     *
     * @param simauv
     */
    public SalinitySensor(SimState simstate){
        super(simstate);
    }

    public void update(float tpf){

    }

    public void init(Node auv_node){

        Sphere sphere7 = new Sphere(16, 16, 0.025f);
        SalinitySensorStart = new Geometry("SalinitySensor", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", ColorRGBA.White);
        SalinitySensorStart.setMaterial(mark_mat7);
        SalinitySensorStart.setLocalTranslation(SalinitySensorStartVector);
        SalinitySensorStart.updateGeometricState();
        PhysicalExchanger_Node.attachChild(SalinitySensorStart);
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
    public Vector3f getSalinitySensorStartVector() {
        return SalinitySensorStartVector;
    }

    /**
     *
     * @param TemperatureSensorStartVector
     */
    public void setSalinitySensorStartVector(Vector3f SalinitySensorStartVector) {
        this.SalinitySensorStartVector = SalinitySensorStartVector;
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
    
    
    @Override
    public void initROS(org.ros.node.Node ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        publisher = ros_node.newPublisher(auv_name + "/" + this.getPhysicalExchangerName(), "std_msgs/Float32");  
    }

    @Override
    public void publish() {
        fl.data = getSalinity();
        this.publisher.publish(fl);
    }
}
