/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package mars.actuators;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Sphere;
import mars.NoiseType;
import mars.SimState;

/**
 * This a basic thruster implementation that you can use for your own thrusters.
 * See SeaBotixThruster for how you can extend this class.
 * @author Thomas Tosik
 */
public class Thruster extends Actuator{

    //motor
    private Geometry MotorStart;
    private Vector3f MotorStartVector = new Vector3f(0,0,0);
    private Geometry MotorEnd;
    private Vector3f MotorDirection = Vector3f.UNIT_Z;
    /**
     *
     */
    protected float MotorForce = 0.0f;
    /**
     *
     */
    protected float motor_increment = 10.0f;

    /**
     *
     * @param simauv
     * @param MassCenterGeom
     */
    public Thruster(SimState simstate,Geometry MassCenterGeom) {
        super(simstate,MassCenterGeom);
    }

    /**
     *
     * @param simauv
     */
    public Thruster(SimState simstate) {
        super(simstate);
    }

    /**
     *
     * @param MotorStartVector
     */
    public void setMotorPosition(Vector3f MotorStartVector){
        this.MotorStartVector = MotorStartVector;
    }

    /**
     *
     * @param MotorDirection
     */
    public void setMotorDirection(Vector3f MotorDirection){
        this.MotorDirection = MotorDirection;
    }

    /**
     *
     * @return
     */
    public Vector3f getMotorDirection() {
        return MotorDirection;
    }

    /**
     *
     * @return
     */
    public Vector3f getMotorStartVector() {
        return MotorStartVector;
    }

    /**
     *
     * @param MotorStartVector
     */
    public void setMotorStartVector(Vector3f MotorStartVector) {
        this.MotorStartVector = MotorStartVector;
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
        MotorStart.setLocalTranslation(MotorStartVector);
        MotorStart.updateGeometricState();
        PhysicalExchanger_Node.attachChild(MotorStart);

        Sphere sphere9 = new Sphere(16, 16, 0.025f);
        MotorEnd = new Geometry("MotorLeftEnd", sphere9);
        Material mark_mat9 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat9.setColor("Color", ColorRGBA.Orange);
        MotorEnd.setMaterial(mark_mat9);
        MotorEnd.setLocalTranslation(MotorStartVector.add(this.MotorDirection));
        MotorEnd.updateGeometricState();
        PhysicalExchanger_Node.attachChild(MotorEnd);

        Vector3f ray_start = MotorStartVector;
        Vector3f ray_direction = MotorDirection;
        Geometry mark4 = new Geometry("Thruster_Arrow", new Arrow(ray_direction.mult(1f)));
        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat4.setColor("Color", ColorRGBA.Orange);
        mark4.setMaterial(mark_mat4);
        mark4.setLocalTranslation(ray_start);
        mark4.updateGeometricState();
        PhysicalExchanger_Node.attachChild(mark4);

        auv_node.attachChild(PhysicalExchanger_Node);
    }

    public void update(){
        Vector3f left = (MotorEnd.getWorldTranslation().subtract(MotorStart.getWorldTranslation())).normalize();
        //physics_control.applyForce(left.mult(MotorForce), this.getMassCenterGeom().getWorldTranslation().subtract(MotorStart.getWorldTranslation()));
        physics_control.applyImpulse(left.mult(MotorForce/simauv_settings.getPhysicsFramerate()), this.getMassCenterGeom().getWorldTranslation().subtract(MotorStart.getWorldTranslation()));
        //physics_control.applyImpulse(Vector3f.UNIT_Z.mult(0.00001f), this.getMassCenterGeom().getWorldTranslation());
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
     *
     * @param speed
     */
    public void set_thruster_speed(int speed){
        if(getNoise_type() == NoiseType.NO_NOISE){
            MotorForce = calculateThrusterForce(speed);
        }else if(getNoise_type() == NoiseType.UNIFORM_DISTRIBUTION){
            float noise = getUnifromDistributionNoise(getNoise_value());
            MotorForce = calculateThrusterForce(speed)+((float)((1f/100f)*noise));
        }else if(getNoise_type() == NoiseType.GAUSSIAN_NOISE_FUNCTION){
            float noise = getGaussianDistributionNoise(getNoise_value());
            MotorForce = calculateThrusterForce(speed)+((float)((1f/100f)*noise));
        }else{
            MotorForce = calculateThrusterForce(speed);
        }
    }

    /**
     *
     */
    public void thruster_forward(){
        MotorForce = MotorForce + motor_increment;
    }

    /**
     *
     */
    public void thruster_back(){
        MotorForce = MotorForce - motor_increment;
    }

    public void reset(){
        MotorForce = 0f;
    }
}