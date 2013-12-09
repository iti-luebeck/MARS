package org.FishSim.SwarmSimulation;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;

/**
 *
 * @author Mandy Feldvoß
 */

public class SwarmColControl extends RigidBodyControl implements PhysicsCollisionListener{
private Swarm swarm;
private int terrainCG = 1;
private int obstacleCG = 6;

    /**
     *
     * @param shape
     * @param swarm
     */
    public SwarmColControl(CollisionShape shape, Swarm swarm){
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
            if(event.getObjectB().getClass().equals(this.getClass())){
                SwarmColControl temp = (SwarmColControl) event.getObjectB();
                if(temp.getSwarm().type > this.swarm.type){
                    swarm.setCollided(event.getPositionWorldOnA());
                }else{
                    if(swarm.id == temp.swarm.id && swarm.splitTime <= 0 && temp.swarm.splitTime <= 0 && swarm.merge == false && temp.swarm.merge == false){
                        swarm.setMerge(temp.swarm);
                    }
                }
            }
            if(event.getObjectB().getCollisionGroup() == terrainCG){
                swarm.setCollided(event.getPositionWorldOnA());
            }
            if(event.getObjectB().getCollisionGroup() == obstacleCG){
                if(event.getObjectB().getClass().equals(RigidBodyControl.class)){
                    RigidBodyControl temp = (RigidBodyControl) event.getObjectB();
                    if(temp.getCollisionShape().getClass().equals(SphereCollisionShape.class)){
                        SphereCollisionShape tempShape = (SphereCollisionShape) temp.getCollisionShape();
                        SphereCollisionShape tempColShape = (SphereCollisionShape) this.getCollisionShape();
                        if(tempShape.getRadius() < tempColShape.getRadius()){
                            Vector3f moveDirect = swarm.getMoveDirection().normalize();
                            Vector3f colDirect = event.getPositionWorldOnA().subtract(swarm.getCenter()).normalize();
                            if((Math.toDegrees(moveDirect.angleBetween(colDirect)) < 15f) && (swarm.getSize() > 50) && swarm.split == false && swarm.splitTime <= 0){
                                swarm.setSplit(event.getPositionWorldOnA());
                            }else{
                                swarm.setCollided(event.getPositionWorldOnA());
                            }       
                        }
                    }
                }
            }
        }
            
        if(event.getObjectB() == this){
            if(event.getObjectA().getClass().equals(this.getClass())){
                SwarmColControl temp = (SwarmColControl) event.getObjectA();    
                if(temp.getSwarm().type > this.swarm.type){
                    swarm.setCollided(event.getPositionWorldOnB());
                }else{
                    if(swarm.id == temp.swarm.id && swarm.splitTime <= 0 && temp.swarm.splitTime <= 0 && swarm.merge == false && temp.swarm.merge == false){
                        swarm.setMerge(temp.swarm);
                    }
                }
            }
            if(event.getObjectA().getCollisionGroup() == terrainCG){
                swarm.setCollided(event.getPositionWorldOnB());
            }
            if(event.getObjectA().getCollisionGroup() == obstacleCG){                 
                if(event.getObjectA().getClass().equals(RigidBodyControl.class)){
                    RigidBodyControl temp = (RigidBodyControl) event.getObjectA();
                    if(temp.getCollisionShape().getClass().equals(SphereCollisionShape.class)){
                        SphereCollisionShape tempShape = (SphereCollisionShape) temp.getCollisionShape();
                        SphereCollisionShape tempColShape = (SphereCollisionShape) this.getCollisionShape();
                        if(tempShape.getRadius() < tempColShape.getRadius()){
                            Vector3f moveDirect = swarm.getMoveDirection().normalize();
                            Vector3f colDirect = event.getPositionWorldOnB().subtract(swarm.getCenter()).normalize();
                            if((Math.toDegrees(moveDirect.angleBetween(colDirect)) < 15f) && (swarm.getSize() > 50) && swarm.split == false && swarm.splitTime <= 0){
                                swarm.setSplit(event.getPositionWorldOnB());
                            }else{
                                swarm.setCollided(event.getPositionWorldOnB());     
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     *
     * @return
     */
    public Swarm getSwarm(){
        return this.swarm;
    }   
}
