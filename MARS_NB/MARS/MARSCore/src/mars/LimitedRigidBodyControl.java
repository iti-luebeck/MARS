/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class LimitedRigidBodyControl extends RigidBodyControl{

    /**
     *
     */
    public LimitedRigidBodyControl() {
    }

    /**
     *
     * @param shape
     */
    public LimitedRigidBodyControl(CollisionShape shape) {
        super(shape);
    }

    /**
     *
     * @param mass
     */
    public LimitedRigidBodyControl(float mass) {
        super(mass);
    }

    /**
     *
     * @param shape
     * @param mass
     */
    public LimitedRigidBodyControl(CollisionShape shape, float mass) {
        super(shape, mass);
    }
    
    /**
     *
     * @param tpf
     */
    @Override
    public void update(float tpf) {
        
        //if(super.getAngularVelocity().length() <= 100f && super.getLinearVelocity().length() <= 100f){
            super.update(tpf);
            //System.out.println(tpf + " " + "getWorldTranslation(): " + super.spatial.getWorldTranslation() + " " + super.spatial.getWorldRotation());
            //if (enabled && spatial != null) {
                //System.out.println("getLinearVelocity(): " + getLinearVelocity().length());
                //getMotionState().applyTransform(spatial);
                /*if(getPhysicsLocation().y <= -0.25f){
                    getMotionState().applyTransform(spatial);
                }else{
                    //setLinearVelocity(getLinearVelocity());//make sure that the forces dont add up too much velocity
                    spatial.setLocalTranslation(spatial.getLocalTranslation().setY(-0.25f));
                    getMotionState().applyTransform(spatial);
                }*/
            //}
        //}
    }
}
