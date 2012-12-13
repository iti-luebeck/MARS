/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.actuators;

import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Sphere;
import com.rits.cloning.Cloner;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.KeyConfig;
import mars.Keys;
import mars.Moveable;
import mars.NoiseType;
import mars.PhysicalExchanger;
import mars.annotations.MARSPublicKeyBindingMethod;
import mars.states.SimState;
import mars.xml.HashMapAdapter;

/**
 * This a basic thruster implementation that you can use for your own thrusters.
 * See SeaBotixThruster for how you can extend this class.
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso( {BrushlessThruster.class,SeaBotixThruster.class} )
public class Thruster extends Actuator implements Moveable,Keys{

    //motor
    private Geometry MotorStart;
    private Geometry MotorEnd;
    /**
     *
     */
    protected float MotorForce = 0.0f;
    /**
     *
     */
    protected float MotorCurrent = 0.0f;
    /**
     *
     */
    protected float motor_increment = 10.0f;
    
    private Vector3f local_rotation_axis = new Vector3f();
    
    private Node Rotation_Node = new Node();
    
    //JAXB KEYS
    @XmlJavaTypeAdapter(HashMapAdapter.class)
    @XmlElement(name="Actions")
    private HashMap<String,String> action_mapping = new HashMap<String, String>();

    /**
     * 
     */
    public Thruster(){
        super();
    }
    
    /**
     *
     * @param simstate 
     * @param MassCenterGeom
     */
    public Thruster(SimState simstate,Geometry MassCenterGeom) {
        super(simstate,MassCenterGeom);
    }

    /**
     *
     * @param simstate 
     */
    public Thruster(SimState simstate) {
        super(simstate);
    }
    
    public Thruster(Thruster thruster){
        super(thruster);
        HashMap<String, String> actionsOriginal = thruster.getAllActions();
        Cloner cloner = new Cloner();
        action_mapping = cloner.deepClone(actionsOriginal);
    }

    @Override
    public PhysicalExchanger copy() {
        Thruster actuator = new Thruster(this);
        actuator.initAfterJAXB();
        return actuator;
    }

    /**
     * DON'T CALL THIS METHOD!
     * In this method all the initialiasing for the motor will be done and it will be attached to the physicsNode.
     */
    public void init(Node auv_node){
        Sphere sphere7 = new Sphere(16, 16, 0.025f);
        MotorStart = new Geometry("MotorLeftStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", ColorRGBA.Orange);
        MotorStart.setMaterial(mark_mat7);
        MotorStart.updateGeometricState();
        Rotation_Node.attachChild(MotorStart);

        Sphere sphere9 = new Sphere(16, 16, 0.025f);
        MotorEnd = new Geometry("MotorLeftEnd", sphere9);
        Material mark_mat9 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat9.setColor("Color", ColorRGBA.Orange);
        MotorEnd.setMaterial(mark_mat9);
        MotorEnd.setLocalTranslation(Vector3f.UNIT_X);
        MotorEnd.updateGeometricState();
        Rotation_Node.attachChild(MotorEnd);

        Vector3f ray_start = Vector3f.ZERO;
        Vector3f ray_direction = Vector3f.UNIT_X;
        Geometry mark4 = new Geometry("Thruster_Arrow", new Arrow(ray_direction.mult(1f)));
        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat4.setColor("Color", ColorRGBA.Orange);
        mark4.setMaterial(mark_mat4);
        mark4.updateGeometricState();
        Rotation_Node.attachChild(mark4);
        
        PhysicalExchanger_Node.setLocalTranslation(getPosition());
        Quaternion quat = new Quaternion();
        quat.fromAngles(getRotation().getX(),getRotation().getY(),getRotation().getZ());
        PhysicalExchanger_Node.setLocalRotation(quat);
        PhysicalExchanger_Node.attachChild(Rotation_Node);
        auv_node.attachChild(PhysicalExchanger_Node);
    }

    public void update(){
        Vector3f left = (MotorEnd.getWorldTranslation().subtract(MotorStart.getWorldTranslation())).normalize();
        //check if thruster is under or over water, because we get different forces depending on the density of the fluid.


        if(MotorStart.getWorldTranslation().y <= this.getIniter().getCurrentWaterHeight(MotorStart.getWorldTranslation().x, MotorStart.getWorldTranslation().z)){
            physics_control.applyImpulse(left.mult(MotorForce/simauv_settings.getPhysicsFramerate()), this.getMassCenterGeom().getWorldTranslation().subtract(MotorStart.getWorldTranslation()));
        }
        //physics_control.applyForce(left.mult(MotorForce), this.getMassCenterGeom().getWorldTranslation().subtract(MotorStart.getWorldTranslation()));
        //physics_control.applyImpulse(left.mult(MotorForce/simauv_settings.getPhysicsFramerate()), this.getMassCenterGeom().getWorldTranslation().subtract(MotorStart.getWorldTranslation()));
        //physics_control.applyImpulse(Vector3f.UNIT_Z.mult(0.00001f), this.getMassCenterGeom().getWorldTranslation());
    }

   /**
     *
     * @param tpf
     */
    @Override
    public void update(float tpf){
        
    }
    
    /**
     * This is the function that represents thruster force.
     * @param speed 
     * @return
     */
    protected float calculateThrusterForce(int speed){
        return (float)(speed)*20f;
    }
    
     /**
     * This is the function that represents thruster currrent consumption.
     * @param speed 
     * @return
     */
    protected float calculateThrusterCurrent(int speed){
        return (float)(speed)*20f;
    }

    /**
     *
     * @param speed
     */
    @MARSPublicKeyBindingMethod(true)
    public void set_thruster_speed(int speed){
        if(getNoise_type() == NoiseType.NO_NOISE){
            MotorForce = calculateThrusterForce(speed);
            MotorCurrent = calculateThrusterCurrent(speed);
        }else if(getNoise_type() == NoiseType.UNIFORM_DISTRIBUTION){
            float noise = getUnifromDistributionNoise(getNoise_value());
            MotorForce = calculateThrusterForce(speed)+((float)((1f/100f)*noise));
            MotorCurrent = calculateThrusterCurrent(speed)+((float)((1f/100f)*noise));
        }else if(getNoise_type() == NoiseType.GAUSSIAN_NOISE_FUNCTION){
            float noise = getGaussianDistributionNoise(getNoise_value());
            MotorForce = calculateThrusterForce(speed)+((float)((1f/100f)*noise));
            MotorCurrent = calculateThrusterCurrent(speed)+((float)((1f/100f)*noise));
        }else{
            MotorForce = calculateThrusterForce(speed);
            MotorCurrent = calculateThrusterCurrent(speed);
        }
        
        if(MotorCurrent < 0f){//make sure that we dont "add" capacity, we only consume(with thrusters)
            MotorCurrent = 0f;
        }
    }

    /**
     *
     */
    @MARSPublicKeyBindingMethod(true)
    public void thruster_forward(){
        MotorForce = MotorForce + motor_increment;
    }

    /**
     *
     */
    @MARSPublicKeyBindingMethod(true)
    public void thruster_back(){
        MotorForce = MotorForce - motor_increment;
    }

    public void reset(){
        MotorForce = 0f;
        MotorCurrent = 0f;
    }

    /**
     * 
     * @return
     */
    public float getMotorCurrent() {
        return MotorCurrent;
    }
    
    /**
     * Don't call this anymore. You have first to call setLocalRotationAxisPoints once at the begining of the simulation
     * @param rotation_axis
     * @param alpha
     * @deprecated 
     */
    @Override
    @Deprecated
    public void updateRotation(Vector3f rotation_axis, float alpha){
        System.out.println("I(" + getPhysicalExchangerName() + ")have to update my rotation to: " + alpha + " with this rot axis: " + rotation_axis );
        Vector3f local_rotation_axis = new Vector3f();
        PhysicalExchanger_Node.worldToLocal(rotation_axis, local_rotation_axis);
        System.out.println("My local rotation axis is:" + local_rotation_axis );
        Quaternion quat = new Quaternion();
        quat.fromAngleAxis(alpha, local_rotation_axis);
        PhysicalExchanger_Node.setLocalRotation(quat);
    }
    
    /**
     * 
     * @param alpha
     */
    @Override
    public void updateRotation(float alpha){
        /*System.out.println("I(" + getPhysicalExchangerName() + ")have to update my rotation to: " + alpha + " with this rot axis: " + local_rotation_axis );
        System.out.println("My local rotation axis is:" + local_rotation_axis );
        System.out.println("My world rotation axis is:" + Rotation_Node.localToWorld(local_rotation_axis,null) );*/
        Quaternion quat = new Quaternion();
        quat.fromAngleAxis(alpha, local_rotation_axis);
        Rotation_Node.setLocalRotation(quat);
    }
    
    /**
     * 
     * @param world_rotation_axis_points
     */
    @Override
    public void setLocalRotationAxisPoints(Matrix3f world_rotation_axis_points){
        Vector3f WorldServoEnd = world_rotation_axis_points.getColumn(0);
        Vector3f WorldServoStart = world_rotation_axis_points.getColumn(1);
        Vector3f LocalServoEnd = new Vector3f();
        Vector3f LocalServoStart = new Vector3f();
        Rotation_Node.worldToLocal(WorldServoEnd, LocalServoEnd);
        Rotation_Node.worldToLocal(WorldServoStart, LocalServoStart);
        local_rotation_axis = LocalServoEnd.subtract(LocalServoStart);
        
        System.out.println("Setting rotation axis from:" + "world_rotation_axis" + " to: " + local_rotation_axis );
        System.out.println("Setting My world rotation axis is:" + Rotation_Node.localToWorld(local_rotation_axis,null) );
        System.out.println("Rotation_Node translation" + Rotation_Node.getWorldTranslation() + "rotation" + Rotation_Node.getWorldRotation() );
        System.out.println("PhysicalExchanger_Node translation" + PhysicalExchanger_Node.getWorldTranslation() + "rotation" + PhysicalExchanger_Node.getWorldRotation() );
    }
    
    /**
     * 
     * @param translation_axis
     * @param new_realative_position
     */
    @Override
    public void updateTranslation(Vector3f translation_axis, Vector3f new_realative_position){
        
    }
    
    /**
     * 
     * @return
     */
    @Override
    public String getSlaveName(){
        return getPhysicalExchangerName();
    }
    
    /**
     * 
     * @return
     */
    @Override
    public HashMap<String,String> getAllActions(){
        return action_mapping;
    }
    
    /**
     * 
     * @param inputManager
     * @param keyconfig
     */
    @Override
    public void addKeys(InputManager inputManager, KeyConfig keyconfig){
        for ( String elem : action_mapping.keySet() ){
            final String action = (String)action_mapping.get(elem);
            final String mapping = elem;
            final Thruster self = this;
            
            inputManager.addMapping(mapping, new KeyTrigger(keyconfig.getKeyNumberForMapping(mapping))); 
                    ActionListener actionListener = new ActionListener() {
                        public void onAction(String name, boolean keyPressed, float tpf) {
                            if(name.equals(mapping) && !keyPressed) {
                                try {
                                    Method method = self.getClass().getMethod(action);
                                    method.invoke(self);
                                } catch (NoSuchMethodException ex) {
                                    Logger.getLogger(Thruster.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (SecurityException ex) {
                                    Logger.getLogger(Thruster.class.getName()).log(Level.SEVERE, null, ex);
                                }catch (IllegalAccessException ex) {
                                    Logger.getLogger(Thruster.class.getName()).log(Level.SEVERE, null, ex);
                                }catch (InvocationTargetException ex) {
                                    Logger.getLogger(Thruster.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    };
            inputManager.addListener(actionListener, elem);
        }
    }
}