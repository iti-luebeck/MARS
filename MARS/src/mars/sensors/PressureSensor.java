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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.ros.node.topic.Publisher;
import mars.NoiseType;
import mars.PhysicalEnvironment;
import mars.SimState;
import mars.ros.MARSNodeMain;
import mars.xml.Vector3fAdapter;
import org.ros.message.Time;

/**
 * This class provides a basic pressure sensor. You can get exact depth or exact pressure + noise.
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class PressureSensor extends Sensor{

    private Geometry PressureSensorStart;

    @XmlElement(name="Position")
    @XmlJavaTypeAdapter(Vector3fAdapter.class)
    private Vector3f PressureSensorStartVector;

    ///ROS stuff
    //private Publisher<org.ros.message.std_msgs.Float32> publisher = null;
    //private org.ros.message.std_msgs.Float32 fl = new org.ros.message.std_msgs.Float32(); 
    private Publisher<org.ros.message.hanse_msgs.pressure> publisher = null;
    private org.ros.message.hanse_msgs.pressure fl = new org.ros.message.hanse_msgs.pressure(); 
    private org.ros.message.std_msgs.Header header = new org.ros.message.std_msgs.Header(); 
    
    /**
     * 
     */
    public PressureSensor(){
        super();
    }
        
     /**
     *
     * @param simstate 
      * @param pe
     */
    public PressureSensor(SimState simstate, PhysicalEnvironment pe){
        super(simstate);
        this.pe = pe;
    }

    /**
     *
     * @param simstate 
     */
    public PressureSensor(SimState simstate){
        super(simstate);
    }

    /**
     *
     */
    public void init(Node auv_node){

        Sphere sphere7 = new Sphere(16, 16, 0.025f);
        PressureSensorStart = new Geometry("PressureStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", ColorRGBA.White);
        PressureSensorStart.setMaterial(mark_mat7);
        PressureSensorStart.setLocalTranslation(PressureSensorStartVector);
        PressureSensorStart.updateGeometricState();
        PhysicalExchanger_Node.attachChild(PressureSensorStart);
        auv_node.attachChild(PhysicalExchanger_Node);
        this.auv_node = auv_node;
    }

    public void update(float tpf){

    }

     /**
     *
     * @return The exact depth of the current auv
     */
    public float getDepth(){
        if(getNoise_type() == NoiseType.NO_NOISE){
            return getRawDepth();
        }else if(getNoise_type() == NoiseType.UNIFORM_DISTRIBUTION){
            float noise = getUnifromDistributionNoise(getNoise_value());
            return getRawDepth()+((float)((1f/100f)*noise));
        }else if(getNoise_type() == NoiseType.GAUSSIAN_NOISE_FUNCTION){
            float noise = getGaussianDistributionNoise(getNoise_value());
            return getRawDepth() + ((float)((1f/100f)*noise));
        }else{
            return getRawDepth();
        }
    }

     /**
     *
     * @return The depth of the current auv 
     */
    private float getRawDepth(){
        return PressureSensorStart.getWorldTranslation().y + Math.abs(pe.getWater_height());
    }

    /**
     * See Pascal's law.
     * @return The pressure that the pressure sensor measures in Bar
     */
    public float getPressureBar(){
        if( getDepth() <= pe.getWater_height()){//underwater
            return (pe.getPressure_water_height()/1000f) + (float)((pe.getFluid_density() * pe.getGravitational_acceleration() * Math.abs(getDepth()))/100000f);
        }else{//air
            return (pe.getPressure_water_height()/1000f);
        }
    }
    
    /**
     * See Pascal's law.
     * @return The pressure that the pressure sensor measures in mBar
     */
    public float getPressureMbar(){
        if( getDepth() <= pe.getWater_height()){//underwater
            return pe.getPressure_water_height() + (float)((pe.getFluid_density() * pe.getGravitational_acceleration() * Math.abs(getDepth()))/100f);
        }else{//air
            return (pe.getPressure_water_height());
        }
    }

    /**
     * See Pascal's law.
     * @return The pressure that the pressure sensor measures in Pascal
     */
    public float getPressurePascal(){
        if( getDepth() <= pe.getWater_height()){//underwater
            return (pe.getPressure_water_height()*100f) + (float)(pe.getFluid_density() * pe.getGravitational_acceleration() * Math.abs(getDepth()));
        }else{//air
            return (pe.getPressure_water_height()*100f);
        }
    }

    /**
     * 
     * @return
     */
    public Vector3f getPressureSensorStartVector() {
        return PressureSensorStartVector;
    }

    /**
     *
     * @param PressureSensorStartVector
     */
    public void setPressureSensorStartVector(Vector3f PressureSensorStartVector) {
        this.PressureSensorStartVector = PressureSensorStartVector;
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
     * @deprecated
     */
    @Override
    @Deprecated
    public void initROS(org.ros.node.Node ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        publisher = ros_node.newPublisher(auv_name + "/" + this.getPhysicalExchangerName(), "std_msgs/Float32");  
    }
    
    /**
     * 
     * @param ros_node
     * @param auv_name
     */
    @Override
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        publisher = ros_node.newPublisher(auv_name + "/" + this.getPhysicalExchangerName(), "hanse_msgs/pressure");  
    }

    /**
     * 
     */
    @Override
    public void publish() {
        //header.seq = 0;
        header.frame_id = "pressure";
        header.stamp = Time.fromMillis(System.currentTimeMillis());
        fl.header = header;
        fl.data = (int)getPressureMbar();
        this.publisher.publish(fl);
    }
}
