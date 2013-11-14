package org.FishSim.SwarmSimulation;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;

/**
 *
 * @author Mandy Feldvoß
 */

public class SwarmViewControl extends RigidBodyControl implements PhysicsCollisionListener{
private Swarm swarm;

    /**
     *
     * @param shape
     * @param swarm
     */
    public SwarmViewControl(CollisionShape shape, Swarm swarm){
        super(shape, 1);
        this.swarm = swarm;
    }
    
    /**
     *
     * @param event
     */
    @Override
    public void collision(PhysicsCollisionEvent event) {
        if(event.getObjectA() == this){
            if(event.getObjectB().getClass().equals(SwarmColControl.class)){
                SwarmColControl temp = (SwarmColControl) event.getObjectB();    
                if(temp.getSwarm().type == 2 && this.swarm != temp.getSwarm()){
                    swarm.setViewCollided(temp.getSwarm().getCenter());
                }
            }
        }
        if(event.getObjectB() == this){
            if(event.getObjectA().getClass().equals(SwarmColControl.class)){
                SwarmColControl temp = (SwarmColControl) event.getObjectA();    
                if(temp.getSwarm().type == 2 && this.swarm != temp.getSwarm()){
                    swarm.setViewCollided(temp.getSwarm().getCenter());
                }
            }
        }
    }    
}
