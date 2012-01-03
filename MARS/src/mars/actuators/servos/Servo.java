/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.actuators.servos;

import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Sphere;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.KeyConfig;
import mars.Keys;
import mars.Manipulating;
import mars.Moveable;
import mars.PhysicalExchanger;
import mars.SimState;
import mars.actuators.Actuator;
import mars.ros.MARSNodeMain;
import mars.xml.HashMapAdapter;
import mars.xml.Vector3fAdapter;
import org.ros.message.MessageListener;

/**
 * This is the default servo class. It uses the Dynamixel AX-12 servos as it basis.
 * You have to set the starting position of the servo and the rotation axis(servo direction).
 * Than dont forget to link a "moveable" physical exchanger(sensor/other actor) to the servo.
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso( {Dynamixel_AX12PLUS.class,Modelcraft_ES07.class} )
public class Servo extends Actuator implements Manipulating,Keys{
    
    //servo
    private Geometry ServoStart;
    @XmlElement(name="Position")
    @XmlJavaTypeAdapter(Vector3fAdapter.class)
    private Vector3f ServoStartVector = new Vector3f(0,0,0);
    private Geometry ServoEnd;
    @XmlElement(name="ServoDirection")
    @XmlJavaTypeAdapter(Vector3fAdapter.class)
    private Vector3f ServoDirection = Vector3f.UNIT_Z;
    
    @XmlElement(name="Slaves")
    private List<String> slaves_names = new ArrayList<String>();
    private List<Moveable> slaves = new ArrayList<Moveable>();
    
    @XmlElement
    private float OperatingAngle = 5.235987f;
    
    @XmlElement
    private int ServoNeutralPosition = 0;
    
    @XmlElement
    private float Resolution = 0.005061f;
    
    @XmlElement
    private float SpeedPerDegree = 0.003266f;
    
    private int current_angle_iteration = 0;
    
    private int desired_angle_iteration = 0;
    
    private int max_angle_iteration = 1035;
    
    private float SpeedPerIteration = 0.0009473f;
    
    private float time = 0;
    
    //JAXB KEYS
    @XmlJavaTypeAdapter(HashMapAdapter.class)
    @XmlElement(name="Actions")
    private HashMap<String,String> action_mapping = new HashMap<String, String>();
    
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
        //current_angle_iteration = ServoNeutralPosition;
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
        //System.out.println("Desired: " + desired_angle_iteration + "/ Current: " + current_angle_iteration);
        if( desired_angle_iteration != current_angle_iteration){//when we are not on the desired position we have work to do
            int possible_iterations = howMuchIterations(tpf);
            if(possible_iterations > 0){//when we dont have enough time to rotate we wait till the next frame
                
                //check the angle boundary
                int diff_iteration = max_angle_iteration - current_angle_iteration;
                if( diff_iteration >= possible_iterations){//when is till fit in
                
                }else{
                    possible_iterations = diff_iteration;
                }
                
                Iterator iter = slaves.iterator();
                while(iter.hasNext() ) {
                    final Moveable moves = (Moveable)iter.next();
                    final int fin_possible_iterations = possible_iterations;
                    Future fut = this.simState.getMARS().enqueue(new Callable() {
                        public Void call() throws Exception {
                            moves.updateRotation(ServoEnd.getWorldTranslation().subtract(ServoStart.getWorldTranslation()), Resolution*(fin_possible_iterations+current_angle_iteration+ServoNeutralPosition));
                            return null;
                        }
                    });
                }
                //since we will rotate we have to update our current angle
                current_angle_iteration += possible_iterations;
                
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
        if(desired_angle_iteration > max_angle_iteration){
            this.desired_angle_iteration = max_angle_iteration;
        }else if(desired_angle_iteration <= 0){
            this.desired_angle_iteration = 0;
        }else{
            this.desired_angle_iteration = desired_angle_iteration;
        }
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
    public Moveable getSlave(String name) {
        Iterator iter = slaves.iterator();
        while(iter.hasNext() ) {
            Moveable moves = (Moveable)iter.next();
            if(moves.getSlaveName().equals(name)){
                return moves;
            }
        }
        return null;
    }
    
    @Override
    public ArrayList getSlavesNames(){
        return (ArrayList)slaves_names;
    }

    /**
     * 
     * @param slave
     */
    @Override
    public void addSlave(Moveable slave) {
        if(slave != null){
            slaves.add(slave);
            if(!slaves_names.contains(slave.getSlaveName())){
                slaves_names.add(slave.getSlaveName());
            }
        }
    }
    
    @Override
    public void addSlaves(ArrayList slaves){
        Iterator iter = slaves.iterator();
        while(iter.hasNext() ) {
            Moveable moves = (Moveable)iter.next();
            addSlave(moves);    
        }
    }
    
    /**
     * 
     * @param ros_node
     * @param auv_name
     */
    @Override
    public void initROS(MARSNodeMain ros_node, String auv_name) {
        super.initROS(ros_node, auv_name);
        final Servo self = this;
        ros_node.newSubscriber(auv_name + "/" + getPhysicalExchangerName(), "smart_e_msgs/servo",
          new MessageListener<org.ros.message.smart_e_msgs.servo>() {
            @Override
            public void onNewMessage(org.ros.message.smart_e_msgs.servo message) {
              self.setDesiredAnglePosition((int)message.data);
            }
          });
    }
    
    @Override
    public void addKeys(InputManager inputManager, KeyConfig keyconfig){
        for ( String elem : action_mapping.keySet() ){
            String action = (String)action_mapping.get(elem);
            final String mapping = elem;
            final Servo self = this;
            if(action.equals("setDesiredAnglePosition")){
                    inputManager.addMapping(mapping, new KeyTrigger(keyconfig.getKeyNumberForMapping(mapping))); 
                    ActionListener actionListener = new ActionListener() {
                        public void onAction(String name, boolean keyPressed, float tpf) {
                            if(name.equals(mapping) && !keyPressed) {
                                self.setDesiredAnglePosition(0);
                            }
                        }
                    };
                    inputManager.addListener(actionListener, elem);
            }
        }
    }
}
