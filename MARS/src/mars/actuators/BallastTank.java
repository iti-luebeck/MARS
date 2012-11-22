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
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Sphere;
import com.rits.cloning.Cloner;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.KeyConfig;
import mars.Keys;
import mars.NoiseType;
import mars.PhysicalExchanger;
import mars.states.SimState;
import mars.xml.HashMapAdapter;

/**
 *
 * @author Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class BallastTank extends Actuator implements Keys{

    //motor
    private Geometry BallastStart;
    private Vector3f BallastStartVector = new Vector3f(0,0,0);
    /**
     *
     */
    protected float volumePerSecond = 0.1f;
    /**
     * 
     */
    protected float maxVolume = 1.0f;
    private float desiredVolume = 0f;
    private float currentVolume = 0f;
    private float time = 0f;
    
    private Node Rotation_Node = new Node();
    
    //JAXB KEYS
    @XmlJavaTypeAdapter(HashMapAdapter.class)
    @XmlElement(name="Actions")
    private HashMap<String,String> action_mapping = new HashMap<String, String>();

    /**
     * 
     */
    public BallastTank(){
        super();
    }
    
    /**
     *
     * @param simstate 
     * @param MassCenterGeom
     */
    public BallastTank(SimState simstate,Geometry MassCenterGeom) {
        super(simstate,MassCenterGeom);
    }

    /**
     *
     * @param simstate 
     */
    public BallastTank(SimState simstate) {
        super(simstate);
    }
    
    public BallastTank(BallastTank ballastTank){
        super(ballastTank);
        HashMap<String, String> actionsOriginal = ballastTank.getAllActions();
        Cloner cloner = new Cloner();
        action_mapping = cloner.deepClone(actionsOriginal);
    }

    @Override
    public PhysicalExchanger copy() {
        BallastTank actuator = new BallastTank(this);
        actuator.initAfterJAXB();
        return actuator;
    }

    /**
     * 
     * @return
     */
    public float getCurrentVolume() {
        return currentVolume;
    }

    /**
     * 
     * @return
     */
    public float getDesiredVolume() {
        return desiredVolume;
    }

    /**
     * 
     * @param desiredVolume
     */
    public void setDesiredVolume(float desiredVolume) {
        if(getMaxVolume() < desiredVolume){
            this.desiredVolume = getMaxVolume();
        }else if(desiredVolume <= 0f){
            this.desiredVolume = 0f;
        }else{
            this.desiredVolume = desiredVolume;
        }
    }
    
    /**
     * 
     * @param percent
     */
    public void setDesiredVolumePrecent(float percent){
        setDesiredVolume((percent)*getMaxVolume());
    }

    /**
     * 
     * @return
     */
    public float getMaxVolume() {
        return (Float)variables.get("maxVolume");
    }

    /**
     * 
     * @param maxVolume
     */
    public void setMaxVolume(float maxVolume) {
        variables.put("maxVolume", maxVolume);
    }

    /**
     * 
     * @return
     */
    public float getVolumePerSecond() {
        return (Float)variables.get("volumePerSecond");
    }

    /**
     * 
     * @param volumePerSecond
     */
    public void setVolumePerSecond(float volumePerSecond) {
        variables.put("volumePerSecond", volumePerSecond);
    }

    /**
     *
     * @param Position 
     */
    public void setBallastPosition(Vector3f Position){
        variables.put("Position", Position);
    }

    /**
     *
     * @return
     */
    public Vector3f getBallastPosition() {
        return (Vector3f)variables.get("Position");
    }

    /**
     * DON'T CALL THIS METHOD!
     * In this method all the initialiasing for the motor will be done and it will be attached to the physicsNode.
     */
    public void init(Node auv_node){
        Sphere sphere7 = new Sphere(16, 16, 0.025f);
        BallastStart = new Geometry("BallastStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", ColorRGBA.LightGray);
        BallastStart.setMaterial(mark_mat7);
        //MotorStart.setLocalTranslation(BallastStartVector);
        BallastStart.updateGeometricState();
        //PhysicalExchanger_Node.attachChild(BallastStart);
        Rotation_Node.attachChild(BallastStart);

        PhysicalExchanger_Node.setLocalTranslation(getBallastPosition());
        PhysicalExchanger_Node.attachChild(Rotation_Node);
        auv_node.attachChild(PhysicalExchanger_Node);
    }

    /*
     * See update(float tpf)
     */
    public void update(){
    }

   /**
     *
     * @param tpf
     */
    @Override
    public void update(float tpf){
        //check if ballasttank is under or over water, because we get different forces depending on the density of the fluid.
        //if(BallastStart.getWorldTranslation().y <= this.getIniter().getCurrentWaterHeight(BallastStart.getWorldTranslation().x, BallastStart.getWorldTranslation().z)){
            //time = time + tpf;
            if(getDesiredVolume() != getCurrentVolume()){
                //float VolumePerMS = getVolumePerSecond()/1000f;
                float diff = getDesiredVolume()-getCurrentVolume();
                float VolumePerTPF = getVolumePerSecond()*tpf;
                if(VolumePerTPF >= Math.abs(diff)){//not enough space, set to max/min
                    /*if(Math.signum(diff) == 1f){//positve =>add
                        currentVolume = getDesiredVolume();  
                    }else{//negative => substract
                        currentVolume = 0f;
                    }*/
                    currentVolume = getDesiredVolume(); 
                }else{//enough space, add/sub normal
                    currentVolume = currentVolume + (Math.signum(diff)*VolumePerTPF);
                }
                //System.out.println("tpf: " + tpf + " VolumePerMS" + VolumePerMS);
            }
            //System.out.println("desired: " + getDesiredVolume() + " current" + currentVolume);
            float buoyancy_force = getPhysical_environment().getFluid_density() * getPhysical_environment().getGravitational_acceleration() * getCurrentVolume();
            physics_control.applyImpulse(Vector3f.UNIT_Y.negate().mult(buoyancy_force/simauv_settings.getPhysicsFramerate()), this.getMassCenterGeom().getWorldTranslation().subtract(BallastStart.getWorldTranslation()));
        //}
    }

    public void reset(){
        currentVolume = 0f;
        setDesiredVolume(0f);
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
            String action = (String)action_mapping.get(elem);
            final String mapping = elem;
            final BallastTank self = this;
            if(action.equals("setDesiredVolumePrecent")){
                    inputManager.addMapping(mapping, new KeyTrigger(keyconfig.getKeyNumberForMapping(mapping))); 
                    ActionListener actionListener = new ActionListener() {
                        public void onAction(String name, boolean keyPressed, float tpf) {
                            if(name.equals(mapping) && !keyPressed) {
                                self.setDesiredVolumePrecent(1.0f);
                            }
                        }
                    };
                    inputManager.addListener(actionListener, elem);
            }
        }
    }
}
