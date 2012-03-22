/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.actuators.weapons;

import com.jme3.asset.TextureKey;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.texture.Texture;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import mars.KeyConfig;
import mars.Keys;
import mars.Moveable;
import mars.actuators.Actuator;
import mars.states.SimState;
import mars.xml.HashMapAdapter;
import mars.xml.Vector3fAdapter;

/**
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Canon extends Actuator implements Moveable,Keys{

    //motor
    private Geometry CanonStart;
    private Vector3f CanonStartVector = new Vector3f(0,0,0);
    private Geometry CanonEnd;
    private Vector3f CanonDirection = Vector3f.UNIT_Z;
    
    private Vector3f local_rotation_axis = new Vector3f();
    
    protected float CanonForce = 10.0f;
    
    protected float RecoilForce = 10.0f;
    
    private Node Rotation_Node = new Node();
    
    //JAXB KEYS
    @XmlJavaTypeAdapter(HashMapAdapter.class)
    @XmlElement(name="Actions")
    private HashMap<String,String> action_mapping = new HashMap<String, String>();
    
    private Sphere bullet;
    private SphereCollisionShape bulletCollisionShape;
    Material mat2;

    /**
     * 
     */
    public Canon(){
        super();
    }
    
    /**
     *
     * @param simstate 
     * @param MassCenterGeom
     */
    public Canon(SimState simstate,Geometry MassCenterGeom) {
        super(simstate,MassCenterGeom);
    }

    /**
     *
     * @param simstate 
     */
    public Canon(SimState simstate) {
        super(simstate);
    }

    public float getCanonForce() {
        return (Float)variables.get("CanonForce");
    }

    public void setCanonForce(float CanonForce) {
        variables.put("CanonForce", CanonForce);
    }

    public float getRecoilForce() {
        return (Float)variables.get("RecoilForce");
    }

    public void setRecoilForce(float RecoilForce) {
        variables.put("RecoilForce", RecoilForce);
    }

    /**
     *
     * @param MotorStartVector
     */
    public void setCanonPosition(Vector3f Position){
        variables.put("Position", Position);
    }

    /**
     *
     * @param MotorDirection
     */
    public void setCanonDirection(Vector3f CanonDirection){
        variables.put("CanonDirection", CanonDirection);
    }

    /**
     *
     * @return
     */
    public Vector3f getCanonDirection() {
        return (Vector3f)variables.get("CanonDirection");
    }

    /**
     *
     * @return
     */
    public Vector3f getCanonPosition() {
        return (Vector3f)variables.get("Position");
    }

    /**
     * DON'T CALL THIS METHOD!
     * In this method all the initialiasing for the motor will be done and it will be attached to the physicsNode.
     */
    public void init(Node auv_node){
        bullet = new Sphere(32, 32, 0.1f, true, false);
        bullet.setTextureMode(TextureMode.Projected);
        bulletCollisionShape = new SphereCollisionShape(0.1f);
        mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key2 = new TextureKey("Textures/Terrain/Rock/Rock.PNG");
        key2.setGenerateMips(true);
        Texture tex2 = assetManager.loadTexture(key2);
        mat2.setTexture("ColorMap", tex2);
        
        Sphere sphere7 = new Sphere(16, 16, 0.025f);
        CanonStart = new Geometry("CanonLeftStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", ColorRGBA.Orange);
        CanonStart.setMaterial(mark_mat7);
        //MotorStart.setLocalTranslation(MotorStartVector);
        CanonStart.updateGeometricState();
        //PhysicalExchanger_Node.attachChild(MotorStart);
        Rotation_Node.attachChild(CanonStart);

        Sphere sphere9 = new Sphere(16, 16, 0.025f);
        CanonEnd = new Geometry("CanonLeftEnd", sphere9);
        Material mark_mat9 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat9.setColor("Color", ColorRGBA.Orange);
        CanonEnd.setMaterial(mark_mat9);
        //MotorEnd.setLocalTranslation(MotorStartVector.add(this.MotorDirection));
        CanonEnd.setLocalTranslation(getCanonDirection());
        CanonEnd.updateGeometricState();
        //PhysicalExchanger_Node.attachChild(MotorEnd);
        Rotation_Node.attachChild(CanonEnd);

        Vector3f ray_start = getCanonPosition();
        Vector3f ray_direction = getCanonDirection();
        Geometry mark4 = new Geometry("Canon_Arrow", new Arrow(ray_direction.mult(1f)));
        Material mark_mat4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat4.setColor("Color", ColorRGBA.Orange);
        mark4.setMaterial(mark_mat4);
        //mark4.setLocalTranslation(ray_start);
        mark4.updateGeometricState();
        //PhysicalExchanger_Node.attachChild(mark4);
        Rotation_Node.attachChild(mark4);

        PhysicalExchanger_Node.setLocalTranslation(getCanonPosition());
        PhysicalExchanger_Node.attachChild(Rotation_Node);
        auv_node.attachChild(PhysicalExchanger_Node);
    }

    public void update(){

    }
    
    public void shoot(){
        System.out.println("Shoot");
        Vector3f left = (CanonEnd.getWorldTranslation().subtract(CanonStart.getWorldTranslation())).normalize();
        physics_control.applyImpulse(left.mult(-RecoilForce), this.getMassCenterGeom().getWorldTranslation().subtract(CanonStart.getWorldTranslation()));
        
                Geometry bulletg = new Geometry("bullet", bullet);
                bulletg.setMaterial(mat2);
                bulletg.setShadowMode(ShadowMode.CastAndReceive);
                bulletg.setLocalTranslation(CanonEnd.getWorldTranslation());
                //RigidBodyControl bulletNode = new BombControl(assetManager, bulletCollisionShape, 1);
                RigidBodyControl bulletNode = new RigidBodyControl(bulletCollisionShape, 1);
                bulletNode.applyImpulse(left.mult(this.CanonForce),Vector3f.ZERO);
                bulletg.addControl(bulletNode);
                this.rootNode.attachChild(bulletg);
                this.simState.getBulletAppState().getPhysicsSpace().add(bulletNode);
    }

   /**
     *
     * @param tpf
     */
    @Override
    public void update(float tpf){
        
    }

    public void reset(){
        
    }
    
    /**
     * Don't call this anymore. You have first to call setLocalRotationAxisPoints once at the begining of the simulation
     * @param rotation_axis
     * @param alpha
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
    
    @Override
    public void updateRotation(float alpha){
        /*System.out.println("I(" + getPhysicalExchangerName() + ")have to update my rotation to: " + alpha + " with this rot axis: " + local_rotation_axis );
        System.out.println("My local rotation axis is:" + local_rotation_axis );
        System.out.println("My world rotation axis is:" + Rotation_Node.localToWorld(local_rotation_axis,null) );*/
        Quaternion quat = new Quaternion();
        quat.fromAngleAxis(alpha, local_rotation_axis);
        Rotation_Node.setLocalRotation(quat);
    }
    
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
    
    @Override
    public HashMap<String,String> getAllActions(){
        return action_mapping;
    }
    
    @Override
    public String getSlaveName(){
        return getPhysicalExchangerName();
    }
    
    @Override
    public void addKeys(InputManager inputManager, KeyConfig keyconfig){
        for ( String elem : action_mapping.keySet() ){
            String action = (String)action_mapping.get(elem);
            final String mapping = elem;
            final Canon self = this;
            if(action.equals("shoot")){
                    inputManager.addMapping(mapping, new KeyTrigger(keyconfig.getKeyNumberForMapping(mapping))); 
                    ActionListener actionListener = new ActionListener() {
                        public void onAction(String name, boolean keyPressed, float tpf) {
                            if(name.equals(mapping) && !keyPressed) {
                                self.shoot();
                            }
                        }
                    };
                    inputManager.addListener(actionListener, elem);
            }
        }
    }    
}
