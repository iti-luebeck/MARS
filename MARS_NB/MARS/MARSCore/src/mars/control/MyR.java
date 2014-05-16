/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.control;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;

/**
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class MyR extends RigidBodyControl{

    /**
     *
     */
    public MyR() {
        super();
    }

    /**
     *
     * @param shape
     */
    public MyR(CollisionShape shape) {
        super(shape);
    }

    /**
     *
     * @param mass
     */
    public MyR(float mass) {
        super(mass);
    }

    /**
     *
     * @param shape
     * @param mass
     */
    public MyR(CollisionShape shape, float mass) {
        super(shape, mass);
    }
    
}
