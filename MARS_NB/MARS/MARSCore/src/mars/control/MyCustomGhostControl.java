/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mars.control;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.GhostControl;
import com.jme3.scene.Spatial;

/**
 * Since GhostControl uses AABB we have to alter it so we get only real
 * collision events.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
public class MyCustomGhostControl extends GhostControl implements PhysicsCollisionListener, PhysicsTickListener {

    private boolean collided = false;
    private boolean col1 = false;
    private boolean col2 = false;

    /**
     *
     */
    public MyCustomGhostControl() {
        super();
    }

    /**
     *
     * @param shape
     */
    public MyCustomGhostControl(CollisionShape shape) {
        super(shape);
    }

    /**
     *
     * @param event
     */
    @Override
    public void collision(PhysicsCollisionEvent event) {
        Spatial nodeA = event.getNodeA();
        Spatial nodeB = event.getNodeB();
        collided = true;
        System.out.println(System.currentTimeMillis() + ": collision " + nodeA.getName() + " " + nodeB.getName());
    }

    /**
     *
     * @param space
     * @param tpf
     */
    @Override
    public void prePhysicsTick(PhysicsSpace space, float tpf) {
        System.out.println(System.currentTimeMillis() + ": prePhysicsTick");
    }

    /**
     *
     * @param space
     * @param tpf
     */
    @Override
    public void physicsTick(PhysicsSpace space, float tpf) {
        col2 = col1;
        col1 = collided;
        System.out.println(col2 + " " + col1);

        if (col2 && !col1) {//collision
            System.out.println("COL EXIT!: " + System.currentTimeMillis());
        }
        collided = false;
        System.out.println(System.currentTimeMillis() + ": physicsTick");
    }
}
