/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.core;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class LimitedRigidBodyControl extends RigidBodyControl{

    public LimitedRigidBodyControl() {
    }

    public LimitedRigidBodyControl(CollisionShape shape) {
        super(shape);
    }

    public LimitedRigidBodyControl(float mass) {
        super(mass);
    }

    public LimitedRigidBodyControl(CollisionShape shape, float mass) {
        super(shape, mass);
    }
    
    @Override
    public void update(float tpf) {
        if (enabled && spatial != null) {
            //System.out.println("getLinearVelocity(): " + getLinearVelocity().length());
            getMotionState().applyTransform(spatial);
            /*if(getPhysicsLocation().y <= -0.25f){
                getMotionState().applyTransform(spatial);
            }else{
                //setLinearVelocity(getLinearVelocity());//make sure that the forces dont add up too much velocity
                spatial.setLocalTranslation(spatial.getLocalTranslation().setY(-0.25f));
                getMotionState().applyTransform(spatial);
            }*/
        }
    }
}
