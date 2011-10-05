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
import mars.NoiseType;
import mars.PhysicalEnvironment;
import mars.SimState;

/**
 * This is a basic compass class.
 * @author Thomas Tosik
 */
public class Compass extends Sensor{

    private Geometry CompassStart;
    private Geometry CompassYawAxis;
    private Geometry CompassPitchAxis;
    private Geometry CompassRollAxis;

    private Vector3f CompassStartVector;
    private Vector3f CompassYawAxisVector;
    private Vector3f CompassPitchAxisVector;
    private Vector3f CompassRollAxisVector;

    private Vector3f magnetic_north = Vector3f.UNIT_X;
    private Vector3f magnetic_east = Vector3f.UNIT_Z;
    private Vector3f magnetic_z = Vector3f.UNIT_Y;

     /**
     *
      * @param simauv
      * @param pe
     */
    public Compass(SimState simstate, PhysicalEnvironment pe) {
        super(simstate);
        this.pe = pe;
    }

    /**
     * 
     * @param simauv
     */
    public Compass(SimState simstate) {
        super(simstate);
    }

    public void update(float tpf){

    }

    public void init(Node auv_node){
        Sphere sphere7 = new Sphere(16, 16, 0.015f);
        CompassStart = new Geometry("CompassStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", ColorRGBA.Black);
        CompassStart.setMaterial(mark_mat7);
        CompassStart.setLocalTranslation(CompassStartVector);
        CompassStart.updateGeometricState();
        PhysicalExchanger_Node.attachChild(CompassStart);

        Sphere sphere9 = new Sphere(16, 16, 0.015f);
        CompassYawAxis = new Geometry("CompassYawAxis", sphere9);
        Material mark_mat9 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat9.setColor("Color", ColorRGBA.Black);
        CompassYawAxis.setMaterial(mark_mat9);
        CompassYawAxis.setLocalTranslation(CompassStartVector.add(CompassYawAxisVector));
        CompassYawAxis.updateGeometricState();
        PhysicalExchanger_Node.attachChild(CompassYawAxis);

        Sphere sphere10 = new Sphere(16, 16, 0.015f);
        CompassPitchAxis = new Geometry("CompassPitchAxis", sphere10);
        Material mark_mat10 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat10.setColor("Color", ColorRGBA.Black);
        CompassPitchAxis.setMaterial(mark_mat10);
        CompassPitchAxis.setLocalTranslation(CompassStartVector.add(CompassPitchAxisVector));
        CompassPitchAxis.updateGeometricState();
        PhysicalExchanger_Node.attachChild(CompassPitchAxis);

        Sphere sphere11 = new Sphere(16, 16, 0.015f);
        CompassRollAxis = new Geometry("CompassRollAxis", sphere11);
        Material mark_mat11 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat11.setColor("Color", ColorRGBA.Black);
        CompassRollAxis.setMaterial(mark_mat11);
        CompassRollAxis.setLocalTranslation(CompassStartVector.add(CompassRollAxisVector));
        CompassRollAxis.updateGeometricState();
        PhysicalExchanger_Node.attachChild(CompassRollAxis);

        auv_node.attachChild(PhysicalExchanger_Node);
        magnetic_z = (magnetic_north.cross(magnetic_east)).negate().normalize();
    }

    /**
     *
     * @return
     */
    public float getYawRadiant(){
        if(getNoise_type() == NoiseType.NO_NOISE){
            return getYawRadiantRaw();
        }else if(getNoise_type() == NoiseType.UNIFORM_DISTRIBUTION){
            float noise = getUnifromDistributionNoise(getNoise_value());
            return getYawRadiantRaw()+((float)((1f/100f)*noise));
        }else if(getNoise_type() == NoiseType.GAUSSIAN_NOISE_FUNCTION){
            float noise = getGaussianDistributionNoise(getNoise_value());
            return getYawRadiantRaw() + ((float)((1f/100f)*noise));
        }else{
            return getYawRadiantRaw();
        }
    }

    /**
     * 
     * @return The yaw angle in radiant
     */
    private float getYawRadiantRaw(){
        Vector3f vec_roll = CompassRollAxis.getWorldTranslation().subtract(CompassStart.getWorldTranslation());
        vec_roll = new Vector3f(vec_roll.getX(),0,vec_roll.getZ());
        if( vec_roll.getX() == 0f && vec_roll.getY() == 0f && vec_roll.getZ() == 0f){
            return 0f;
        }
        Vector3f plus = (pe.getMagnetic_north().cross(vec_roll)).normalize();
        if( plus.getY() < 0 ){//negativ, vec_roll on the right side of the magnetic north
            return (vec_roll.normalize()).angleBetween(pe.getMagnetic_north().normalize());
        }else if( plus.getY() == 0){
            if( (pe.getMagnetic_north().add(vec_roll)).length() <= (vec_roll.length()+pe.getMagnetic_north().length()) ){//vectors point in same direction
                return 0f;
            }else{//vectors are opposite
                return (float)Math.PI;
            }
        }else{//left side
            return (float)(Math.PI + ( Math.PI - (vec_roll.normalize()).angleBetween(pe.getMagnetic_north().normalize()) ) );
        }
    }

    /**
     *
     * @return The yaw angle in degree
     */
    public float getYawDegree(){
        return (float)(getYawRadiant()*(180/Math.PI));
    }

    /**
     * 
     * @return
     */
    public float getPitchRadiant(){
        if(getNoise_type() == NoiseType.NO_NOISE){
            return getPitchRadiantRaw();
        }else if(getNoise_type() == NoiseType.UNIFORM_DISTRIBUTION){
            float noise = getUnifromDistributionNoise(getNoise_value());
            return getPitchRadiantRaw()+((float)((1f/100f)*noise));
        }else if(getNoise_type() == NoiseType.GAUSSIAN_NOISE_FUNCTION){
            float noise = getGaussianDistributionNoise(getNoise_value());
            return getPitchRadiantRaw() + ((float)((1f/100f)*noise));
        }else{
            return getPitchRadiantRaw();
        }
    }

     /**
     *
     * @return The yaw angle in radiant
     */
    private float getPitchRadiantRaw(){
        Vector3f vec_roll = CompassRollAxis.getWorldTranslation().subtract(CompassStart.getWorldTranslation());
        vec_roll = new Vector3f(0,vec_roll.getY(),vec_roll.getZ());
        if( vec_roll.getX() == 0f && vec_roll.getY() == 0f && vec_roll.getZ() == 0f){
            return 0f;
        }
        Vector3f plus = (pe.getMagnetic_east().cross(vec_roll)).normalize();
        if( plus.getX() < 0 ){//negativ, vec_roll on the right side of the magnetic north
            return (vec_roll.normalize()).angleBetween(pe.getMagnetic_east().normalize());
        }else if( plus.getX() == 0){
            if( (pe.getMagnetic_east().add(vec_roll)).length() <= (vec_roll.length()+pe.getMagnetic_east().length()) ){//vectors point in same direction
                return 0f;
            }else{//vectors are opposite
                return (float)Math.PI;
            }
        }else{//left side
            return (float)(Math.PI + ( Math.PI - (vec_roll.normalize()).angleBetween(pe.getMagnetic_east().normalize()) ) );
        }
    }

    /**
     *
     * @return The yaw angle in degree
     */
    public float getPitchDegree(){
        return (float)(getPitchRadiant()*(180/Math.PI));
    }

    /**
     *
     * @return
     */
    public float getRollRadiant(){
        if(getNoise_type() == NoiseType.NO_NOISE){
            return getRollRadiantRaw();
        }else if(getNoise_type() == NoiseType.UNIFORM_DISTRIBUTION){
            float noise = getUnifromDistributionNoise(getNoise_value());
            return getRollRadiantRaw()+((float)((1f/100f)*noise));
        }else if(getNoise_type() == NoiseType.GAUSSIAN_NOISE_FUNCTION){
            float noise = getGaussianDistributionNoise(getNoise_value());
            return getRollRadiantRaw() + ((float)((1f/100f)*noise));
        }else{
            return getRollRadiantRaw();
        }
    }

     /**
     *
     * @return The yaw angle in radiant
     */
    private float getRollRadiantRaw(){
        Vector3f vec_roll = CompassRollAxis.getWorldTranslation().subtract(CompassStart.getWorldTranslation());
        vec_roll = new Vector3f(vec_roll.getX(),vec_roll.getY(),0);
        if( vec_roll.getX() == 0f && vec_roll.getY() == 0f && vec_roll.getZ() == 0f){
            return 0f;
        }
        Vector3f plus = (pe.getMagnetic_z().cross(vec_roll)).normalize();
        if( plus.getZ() < 0 ){//negativ, vec_roll on the right side of the magnetic north
            return (vec_roll.normalize()).angleBetween(pe.getMagnetic_z().normalize());
        }else if( plus.getZ() == 0){
            if( (pe.getMagnetic_z().add(vec_roll)).length() <= (vec_roll.length()+pe.getMagnetic_z().length()) ){//vectors point in same direction
                return 0f;
            }else{//vectors are opposite
                return (float)Math.PI;
            }
        }else{//left side
            return (float)(Math.PI + ( Math.PI - (vec_roll.normalize()).angleBetween(pe.getMagnetic_z().normalize()) ) );
        }
    }

    /**
     *
     * @return The yaw angle in degree
     */
    public float getRollDegree(){
        return (float)(getRollRadiant()*(180/Math.PI));
    }

    /**
     *
     * @return
     */
    public Vector3f getCompassPitchAxisVector() {
        return CompassPitchAxisVector;
    }

    /**
     *
     * @param CompassPitchAxisVector
     */
    public void setCompassPitchAxisVector(Vector3f CompassPitchAxisVector) {
        this.CompassPitchAxisVector = CompassPitchAxisVector;
    }

    /**
     *
     * @return
     */
    public Vector3f getCompassRollAxisVector() {
        return CompassRollAxisVector;
    }

    /**
     *
     * @param CompassRollAxisVector
     */
    public void setCompassRollAxisVector(Vector3f CompassRollAxisVector) {
        this.CompassRollAxisVector = CompassRollAxisVector;
    }

    /**
     *
     * @return
     */
    public Vector3f getCompassStartVector() {
        return CompassStartVector;
    }

    /**
     *
     * @param CompassStartVector
     */
    public void setCompassStartVector(Vector3f CompassStartVector) {
        this.CompassStartVector = CompassStartVector;
    }

    /**
     *
     * @return
     */
    public Vector3f getCompassYawAxisVector() {
        return CompassYawAxisVector;
    }

    /**
     *
     * @param CompassYawAxisVector
     */
    public void setCompassYawAxisVector(Vector3f CompassYawAxisVector) {
        this.CompassYawAxisVector = CompassYawAxisVector;
    }

    /**
     * 
     * @return
     */
    public Vector3f getMagnetic_north() {
        return magnetic_north;
    }

    /**
     *
     * @param magnetic_north
     */
    public void setMagnetic_north(Vector3f magnetic_north) {
        this.magnetic_north = magnetic_north;
    }

    /**
     *
     * @return
     */
    public Vector3f getMagnetic_east() {
        return magnetic_east;
    }

    /**
     *
     * @param magnetic_east
     */
    public void setMagnetic_east(Vector3f magnetic_east) {
        this.magnetic_east = magnetic_east;
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
}
