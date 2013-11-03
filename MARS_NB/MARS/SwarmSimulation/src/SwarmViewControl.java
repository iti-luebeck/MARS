
package SwarmSimulation;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;

/**
 *
 * @author Acer
 */
public class SwarmViewControl extends RigidBodyControl implements PhysicsCollisionListener{
private Swarm swarm;

    public SwarmViewControl(CollisionShape shape, Swarm swarm){
        super(shape, 1);
        this.swarm = swarm;
    }
    
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
