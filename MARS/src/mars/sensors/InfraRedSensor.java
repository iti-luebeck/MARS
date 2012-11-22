/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.sensors;

import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Sphere;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import org.ros.node.topic.Publisher;
import mars.NoiseType;
import mars.PhysicalEnvironment;
import mars.PhysicalExchanger;
import mars.states.SimState;
import mars.ros.MARSNodeMain;

/**
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class InfraRedSensor extends Sensor{
    /**
     *
     */
    protected Geometry Start;
    /**
     *
     */
    protected Geometry End;

    private Vector3f StartVector = new Vector3f(0,0,0);
    private Vector3f Direction = new Vector3f(0,0,0);

    private Node detectable;

    //Maximum sonar range
    private float MaxRange = 50f;
    private float MinRange = 0.1f;

    private boolean angular_damping = false;
    private float angular_factor = 1.0f;
    private boolean length_damping = false;
    private float length_factor = 1.0f;
    
    //ROS stuff
    private Publisher<std_msgs.Float32> publisher = null;
    private std_msgs.Float32 fl;
    
    /**
     * 
     */
    public InfraRedSensor() {
        super();
    }
    
    /**
     * 
     * @param simstate
     * @param pe
     * @param detectable
     * @deprecated
     */
    @Deprecated
    public InfraRedSensor(SimState simstate, PhysicalEnvironment pe, Node detectable) {
        super(simstate);
        //set the logging
        try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
            // Add to the desired logger
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.addHandler(handler);
        } catch (IOException e) { }

        this.detectable = detectable;
        this.pe = pe;
    }

    /**
     * 
     * @param simstate
     * @param detectable
     */
    public InfraRedSensor(SimState simstate, Node detectable) {
        super(simstate);
        //set the logging
        try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
            // Add to the desired logger
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.addHandler(handler);
        } catch (IOException e) { }

        this.detectable = detectable;
    }
    
    public InfraRedSensor(InfraRedSensor sensor){
        super(sensor);
    }

    @Override
    public PhysicalExchanger copy() {
        InfraRedSensor sensor = new InfraRedSensor(this);
        sensor.initAfterJAXB();
        return sensor;
    }
    
    public void init(Node auv_node) {
        Sphere sphere7 = new Sphere(16, 16, 0.025f);
        Start = new Geometry("InfraStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", ColorRGBA.Red);
        Start.setMaterial(mark_mat7);
        Start.setLocalTranslation(getPosition());
        Start.updateGeometricState();
        PhysicalExchanger_Node.attachChild(Start);

        Sphere sphere9 = new Sphere(16, 16, 0.025f);
        End = new Geometry("InfraEnd", sphere9);
        Material mark_mat9 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat9.setColor("Color", ColorRGBA.Red);
        End.setMaterial(mark_mat9);
        End.setLocalTranslation(getPosition().add(getDirection()));
        End.updateGeometricState();
        PhysicalExchanger_Node.attachChild(End);

        Vector3f ray_start = getPosition();
        Vector3f ray_direction = (getPosition().add(getDirection())).subtract(ray_start);
        Geometry mark4 = new Geometry("Infra_Arrow", new Arrow(ray_direction.mult(getMaxRange())));
        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat4.setColor("Color", ColorRGBA.Red);
        mark4.setMaterial(mark_mat4);
        mark4.setLocalTranslation(ray_start);
        mark4.updateGeometricState();
        PhysicalExchanger_Node.attachChild(mark4);

        auv_node.attachChild(PhysicalExchanger_Node);
    }

    public void update(float tpf) {

    }

    public void reset() {
    }

    /**
     *
     * @return
     */
    public float getAngular_factor() {
        return (Float)variables.get("angular_factor");
    }

    /**
     *
     * @param angular_factor
     */
    public void setAngular_factor(float angular_factor) {
        variables.put("angular_factor", angular_factor);
    }

    /**
     *
     * @return
     */
    public float getLength_factor() {
        return (Float)variables.get("length_factor");
    }

    /**
     *
     * @param length_factor
     */
    public void setLength_factor(float length_factor) {
        variables.put("length_factor", length_factor);
    }

    /**
     *
     * @return
     */
    public boolean isAngular_damping() {
        return (Boolean)variables.get("angular_damping");
    }

    /**
     * 
     * @param angular_damping
     */
    public void setAngular_damping(boolean angular_damping) {
        variables.put("angular_damping", angular_damping);
    }

    /**
     *
     * @return
     */
    public boolean isLength_damping() {
        return (Boolean)variables.get("length_damping");
    }

    /**
     *
     * @param length_damping
     */
    public void setLength_damping(boolean length_damping) {
        variables.put("length_damping", length_damping);
    }

    /**
     *
     * @return
     */
    public Node getDetectable() {
        return detectable;
    }

    /**
     * 
     * @param detectable
     */
    public void setDetectable(Node detectable) {
        this.detectable = detectable;
    }

    /**
     *
     * @param Position 
     */
    public void setPosition(Vector3f Position){
        variables.put("Position", Position);
    }

    /**
     * 
     * @param Direction 
     */
    public void setDirection(Vector3f Direction){
        variables.put("Direction", Direction);
    }
    
    /**
     *
     * @return  
     */
    public Vector3f getPosition(){
        return (Vector3f)variables.get("Position");
    }

    /**
     * 
     * @return  
     */
    public Vector3f getDirection(){
        return (Vector3f)variables.get("Direction");
    }

    /**
     *
     * @return
     */
    public float getMaxRange() {
        return (Float)variables.get("MaxRange");
    }

    /**
     *
     * @param MaxRange 
     */
    public void setMaxRange(float MaxRange) {
        variables.put("MaxRange", MaxRange);
    }

    /**
     *
     * @return
     */
    public float getMinRange() {
        return (Float)variables.get("MinRange");
    }

    /**
     *
     * @param MinRange 
     */
    public void setMinRange(float MinRange) {
        variables.put("MinRange", MinRange);
    }
    
         /**
     *
     * @return The exact depth of the current auv
     */
    public float getDistance(){
        if(getNoise_type() == NoiseType.NO_NOISE){
            return getRawDistance();
        }else if(getNoise_type() == NoiseType.UNIFORM_DISTRIBUTION){
            float noise = getUnifromDistributionNoise(getNoise_value());
            return getRawDistance()+((float)((1f/100f)*noise));
        }else if(getNoise_type() == NoiseType.GAUSSIAN_NOISE_FUNCTION){
            float noise = getGaussianDistributionNoise(getNoise_value());
            return getRawDistance() + ((float)((1f/100f)*noise));
        }else{
            return getRawDistance();
        }
    }
    
    private float getRawDistance(){
        Vector3f ray_start = this.Start.getWorldTranslation();

        Vector3f ray_direction = (End.getWorldTranslation()).subtract(Start.getWorldTranslation());

        float[] infra_data = getRawRayData(ray_start, ray_direction);
        
        return infra_data[0];
    }
    
     private float[] getRawRayData(Vector3f start, Vector3f direction){
        if(detectable == null){
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "No detectable Node/Object added...", "");
            return new float[2];
        }

        CollisionResults results = new CollisionResults();
        float[] arr_ret = new float[2];
        Vector3f ray_start = start;
        Vector3f ray_direction = direction;
        //System.out.println("r " + ray_start);
        //System.out.println("r+ " + ray_direction);

        Ray ray = new Ray(ray_start, ray_direction);

        detectable.collideWith(ray, results);
        //System.out.println(results2.size());
        for (int i = 0; i < results.size(); i++) {
            float distance = results.getCollision(i).getDistance();
            //System.out.println(" d " + i + " " + distance);
            if(distance >= getMaxRange()){//too far away
                //System.out.println("too far away");
                break;
            }else if(results.getCollision(i).getContactPoint().y >= pe.getWater_height()){//forget hits over water
                break;
            }else if ((distance > getMinRange())) {
                //first = results2.getCollision(i).getContactPoint();
                Vector3f cnormal = results.getCollision(i).getContactNormal();
                Vector3f direction_negated = direction.negate();
                float angle = cnormal.angleBetween(direction_negated);
                if(angle > Math.PI/2){//sometimes the normal vector isnt right and than we have to much angle
                    angle = (float)Math.PI/2;
                }

                /*System.out.println("angle: " + angle);
                System.out.println("cnor: " + cnormal);
                System.out.println("direc: " + direction_negated);*/
                //System.out.println(first);
                //ret = (first.subtract(ray_start)).length();
                arr_ret[0] = distance;
                arr_ret[1] = angle;
                //System.out.println(distance);
                break;
            }
            //System.out.println("point too near!");
        }
        return arr_ret;
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
        fl.setData(getDistance());
        if( publisher != null ){
            publisher.publish(fl);
        }
    }
    
}
