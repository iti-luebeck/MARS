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
            int cg = event.getObjectB().getCollisionGroup();
            if(event.getObjectB() instanceof SwarmColControl){
                SwarmColControl temp = (SwarmColControl) event.getObjectB();
                if(temp.getSwarm().type != this.swarm.type){
                    if(temp.getSwarm().type == 2){
                        swarm.setViewCollided(event.getPositionWorldOnA());
                    }else{
                        if(temp.getSwarm().type > this.swarm.type){
                            colType1(event.getPositionWorldOnA());
                        }
                    }
                }else{
                    if(swarm.splitTime <= 0 && temp.swarm.splitTime <= 0 && swarm.merge == false && temp.swarm.merge == false){
                        swarm.setMerge(temp.swarm);
                    }
                }
            }
            
            if(cg != terrainCG && cg != obstacleCG && cg != 4 && cg != 5){
                colType1(event.getPositionWorldOnA());
            }
            
            if(cg == terrainCG){
                swarm.setCollided(event.getPositionWorldOnA());
                swarm.setSolidCollisionType(2);
            }
            
            if(cg == obstacleCG){
                if(event.getObjectB() instanceof RigidBodyControl){
                    RigidBodyControl temp = (RigidBodyControl) event.getObjectB();
                    if(temp.getCollisionShape() instanceof SphereCollisionShape){
                        SphereCollisionShape tempShape = (SphereCollisionShape) temp.getCollisionShape();
                        SphereCollisionShape tempColShape = (SphereCollisionShape) this.getCollisionShape();
                        if(tempShape.getRadius() < tempColShape.getRadius()){
                            Vector3f moveDirect = swarm.getMoveDirection().normalize();
                            Vector3f colDirect = event.getPositionWorldOnA().subtract(swarm.getCenter()).normalize();
                            if((Math.toDegrees(moveDirect.angleBetween(colDirect)) < 150f) && (swarm.getSize() > 50) && swarm.split == false && swarm.splitTime <= 0){
                                swarm.setSplit(event.getPositionWorldOnA());
                            }else{
                                colType1(event.getPositionWorldOnA());
                            }     
                        }else{
                            colType1(event.getPositionWorldOnA());
                        }
                    }else{
                        colType1(event.getPositionWorldOnA());
                    }
                }else{
                    colType1(event.getPositionWorldOnA());
                }
            }
        }
            
        if(event.getObjectB() == this){
            int cg = event.getObjectA().getCollisionGroup();
            if(event.getObjectA() instanceof SwarmColControl){
                SwarmColControl temp = (SwarmColControl) event.getObjectA();    
                if(temp.getSwarm().type != this.swarm.type){
                    if(temp.getSwarm().type == 2){
                        swarm.setViewCollided(event.getPositionWorldOnB());
                    }else{
                        if(temp.getSwarm().type > this.swarm.type){
                            colType1(event.getPositionWorldOnB());
                        }    
                    }
                }else{
                    if(swarm.splitTime <= 0 && temp.swarm.splitTime <= 0 && swarm.merge == false && temp.swarm.merge == false){
                        swarm.setMerge(temp.swarm);
                    }
                }
            }
            
            if(cg != terrainCG && cg != obstacleCG && cg != 4 && cg != 5){
                colType1(event.getPositionWorldOnB());
            }
            
            if(cg == terrainCG){
                swarm.setCollided(event.getPositionWorldOnB());
                swarm.setSolidCollisionType(2);
            }
            
            if(cg == obstacleCG){                 
                if(event.getObjectA() instanceof RigidBodyControl){
                    RigidBodyControl temp = (RigidBodyControl) event.getObjectA();
                    if(temp.getCollisionShape() instanceof SphereCollisionShape){
                        SphereCollisionShape tempShape = (SphereCollisionShape) temp.getCollisionShape();
                        SphereCollisionShape tempColShape = (SphereCollisionShape) this.getCollisionShape();
                        if(tempShape.getRadius() < tempColShape.getRadius()){
                            Vector3f moveDirect = swarm.getMoveDirection().normalize();
                            Vector3f colDirect = event.getPositionWorldOnB().subtract(swarm.getCenter()).normalize();
                            if((Math.toDegrees(moveDirect.angleBetween(colDirect)) < 15f) && (swarm.getSize() > 50) && swarm.split == false && swarm.splitTime <= 0){
                                swarm.setSplit(event.getPositionWorldOnB());
                            }else{
                                colType1(event.getPositionWorldOnB());    
                            }
                        }else{
                            colType1(event.getPositionWorldOnB());
                        }
                    }else{
                        colType1(event.getPositionWorldOnB());
                    }
                }else{
                    colType1(event.getPositionWorldOnB());
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
    
    private void colType1(Vector3f location){
        if(swarm.getSolidCollisionType() < 2){
            swarm.setCollided(location);
            swarm.setSolidCollisionType(1);
        }
    }
}
