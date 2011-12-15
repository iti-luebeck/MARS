/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.actuators.servos;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.Manipulating;
import mars.Moveable;
import mars.PhysicalExchanger;
import mars.SimState;
import mars.actuators.Actuator;
import mars.xml.Vector3fAdapter;

/**
 * This is the default servo class. It uses the Dynamixel AX-12 servos as it basis.
 * You have to set the starting position of the servo and the rotation axis(servo direction).
 * Than dont forget to link a "moveable" physical exchanger(sensor/other actor) to the servo.
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso( {Dynamixel_AX12PLUS.class,Modelcraft_ES07.class} )
public class Servo extends Actuator implements Manipulating{
    
    //servo
    private Geometry ServoStart;
    @XmlElement(name="Position")
    @XmlJavaTypeAdapter(Vector3fAdapter.class)
    private Vector3f ServoStartVector = new Vector3f(0,0,0);
    private Geometry ServoEnd;
    @XmlElement(name="ServoDirection")
    @XmlJavaTypeAdapter(Vector3fAdapter.class)
    private Vector3f ServoDirection = Vector3f.UNIT_Z;
    
    private Moveable slave;
    
    @XmlElement
    private float OperatingAngle = 5.235987f;
    
    @XmlElement
    private float Resolution = 0.005061f;
    
    @XmlElement
    private float SpeedPerDegree = 0.003266f;
    
    private int current_angle_iteration = 0;
    
    private int desired_angle_iteration = 0;
    
    private int max_angle_iteration = 1035;
    
    private float SpeedPerIteration = 0.0009473f;
    
    private float time = 0;
    
    /**
     * 
     */
    public Servo(){
        super();
        try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
            // Add to the desired logger
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.addHandler(handler);
        } catch (IOException e) { }
        computeAngleIterations();
    }
    /**
     *
     * @param simstate 
     * @param MassCenterGeom
     */
    public Servo(SimState simstate,Geometry MassCenterGeom) {
        super(simstate,MassCenterGeom);
        try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
            // Add to the desired logger
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.addHandler(handler);
        } catch (IOException e) { }
        computeAngleIterations();
    }

    /**
     *
     * @param simstate 
     */
    public Servo(SimState simstate) {
        super(simstate);
        try {
            // Create an appending file handler
            boolean append = true;
            FileHandler handler = new FileHandler(this.getClass().getName() + ".log", append);
            // Add to the desired logger
            Logger logger = Logger.getLogger(this.getClass().getName());
            logger.addHandler(handler);
        } catch (IOException e) { }
        computeAngleIterations();
    }
    
    private void computeAngleIterations(){
        max_angle_iteration = (int)(Math.round(OperatingAngle/Resolution));
        SpeedPerIteration = (Resolution)*((SpeedPerDegree)/((float)(Math.PI*2)/360f));
    }

    public Vector3f getServoDirection() {
        return ServoDirection;
    }

    public void setServoDirection(Vector3f ServoDirection) {
        this.ServoDirection = ServoDirection;
    }

    public Vector3f getServoStartVector() {
        return ServoStartVector;
    }

    public void setServoStartVector(Vector3f ServoStartVector) {
        this.ServoStartVector = ServoStartVector;
    }
    
    public float getOperatingAngle() {
        return OperatingAngle;
    }

    public void setOperatingAngle(float OperatingAngle) {
        this.OperatingAngle = OperatingAngle;
    }

    public float getResolution() {
        return Resolution;
    }

    public void setResolution(float Resolution) {
        this.Resolution = Resolution;
    }

    public float getSpeedPerDegree() {
        return SpeedPerDegree;
    }

    public void setSpeedPerDegree(float SpeedPerDegree) {
        this.SpeedPerDegree = SpeedPerDegree;
    }
    
     /**
     * DON'T CALL THIS METHOD!
     * In this method all the initialiasing for the servo will be done and it will be attached to the physicsNode.
     */
    public void init(Node auv_node){
        Sphere sphere7 = new Sphere(16, 16, 0.025f);
        ServoStart = new Geometry("ServoStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", ColorRGBA.White);
        ServoStart.setMaterial(mark_mat7);
        ServoStart.setLocalTranslation(ServoStartVector);
        ServoStart.updateGeometricState();
        PhysicalExchanger_Node.attachChild(ServoStart);

        Sphere sphere9 = new Sphere(16, 16, 0.025f);
        ServoEnd = new Geometry("ServoEnd", sphere9);
        Material mark_mat9 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat9.setColor("Color", ColorRGBA.White);
        ServoEnd.setMaterial(mark_mat9);
        ServoEnd.setLocalTranslation(ServoStartVector.add(this.ServoDirection));
        ServoEnd.updateGeometricState();
        PhysicalExchanger_Node.attachChild(ServoEnd);

        Vector3f ray_start = ServoStartVector;
        Vector3f ray_direction = ServoDirection;
        Geometry mark4 = new Geometry("Thruster_Arrow", new Arrow(ray_direction.mult(1f)));
        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat4.setColor("Color", ColorRGBA.White);
        mark4.setMaterial(mark_mat4);
        mark4.setLocalTranslation(ray_start);
        mark4.updateGeometricState();
        PhysicalExchanger_Node.attachChild(mark4);

        auv_node.attachChild(PhysicalExchanger_Node);
    }

    @Override
    public void update(){
    }
    
    @Override
    public void update(float tpf){
        updateAnglePosition(tpf);
    }
    
    @Override
    public void reset(){
        current_angle_iteration = 0;
        desired_angle_iteration = 0;
    }
    
    private void updateAnglePosition(float tpf){
        if( desired_angle_iteration != current_angle_iteration){//when we are not on the desired position we have work to do
            int possible_iterations = howMuchIterations(tpf);
            if(possible_iterations > 0){//when we dont have enough time to rotate we wait till the next frame
                slave.updateRotation(ServoEnd.getWorldTranslation().subtract(ServoStart.getWorldTranslation()), Resolution*possible_iterations);
            }
        }
    }
    
    private int howMuchIterations(float tpf){
        time += tpf;
        if( time > SpeedPerIteration){//we have enough time to do a least one iteration
            //how much iterations can we do exactly in time?
            int possible_iterations = (int)Math.floor(time/SpeedPerIteration);
            time = 0;
            return possible_iterations;
        }else{//not enough time, we have to wait till the next frame
            return 0;
        }
    }
    
    public void setDesiredAnglePosition(int desired_angle_iteration){
        this.desired_angle_iteration = desired_angle_iteration;
    }
    
    public int getDesiredAnglePosition(){
        return this.desired_angle_iteration;
    }
    
    public int getCurentAnglePosition(){
        return this.current_angle_iteration;
    }
    
    /**
     * 
     * @return
     */
    @Override
    public Moveable getSlave() {
        return slave;
    }

    /**
     * 
     * @param slave
     */
    @Override
    public void setSlave(PhysicalExchanger slave) {
        if(slave instanceof Moveable){
            this.slave = (Moveable)slave; 
        }else{
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "The physical exchanger didn't implemented the Moveable interface!", "");
        }
    }
}
