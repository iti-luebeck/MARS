/*
 * Copyright (c) 2015, Institute of Computer Engineering, University of Lübeck
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * 
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package mars.FishSim.control;

import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import mars.FishSim.Swarm;

/**
 *
 * @author Mandy Feldvoß
 */
public class SwarmColControl extends RigidBodyControl implements PhysicsCollisionListener {

    private final Swarm swarm;
    private final int terrainCG = 1;
    private final int obstacleCG = 6;

    /**
     *
     * @param shape
     * @param swarm
     */
    public SwarmColControl(CollisionShape shape, Swarm swarm) {
        super(shape, 1);
        this.swarm = swarm;
    }

    /**
     *
     * @param event
     */
    @Override
    public void collision(PhysicsCollisionEvent event) {
        if (event.getObjectA() == this) {
            int cg = event.getObjectB().getCollisionGroup();
            if (event.getObjectB() instanceof SwarmColControl) {
                SwarmColControl temp = (SwarmColControl) event.getObjectB();
                if (temp.getSwarm().type != this.swarm.type) {
                    if (temp.getSwarm().type == 2) {
                        swarm.setViewCollided(event.getPositionWorldOnA());
                    } else {
                        if (temp.getSwarm().type > this.swarm.type) {
                            colType1(event.getPositionWorldOnA());
                        }
                    }
                } else {
                    if (swarm.splitTime <= 0 && temp.swarm.splitTime <= 0 && swarm.merge == false && temp.swarm.merge == false) {
                        swarm.setMerge(temp.swarm);
                    }
                }
            }

            if (cg != terrainCG && cg != obstacleCG && cg != 4 && cg != 5) {
                colType1(event.getPositionWorldOnA());
            }

            if (cg == terrainCG) {
                swarm.setCollided(event.getPositionWorldOnA());
                swarm.setSolidCollisionType(2);
            }

            if (cg == obstacleCG) {
                if (event.getObjectB() instanceof RigidBodyControl) {
                    RigidBodyControl temp = (RigidBodyControl) event.getObjectB();
                    if (temp.getCollisionShape() instanceof SphereCollisionShape) {
                        SphereCollisionShape tempShape = (SphereCollisionShape) temp.getCollisionShape();
                        SphereCollisionShape tempColShape = (SphereCollisionShape) this.getCollisionShape();
                        if (tempShape.getRadius() < tempColShape.getRadius()) {
                            Vector3f moveDirect = swarm.getMoveDirection().normalize();
                            Vector3f colDirect = event.getPositionWorldOnA().subtract(swarm.getCenter()).normalize();
                            if ((Math.toDegrees(moveDirect.angleBetween(colDirect)) < 150f) && (swarm.getSize() > 50) && swarm.split == false && swarm.splitTime <= 0) {
                                swarm.setSplit(event.getPositionWorldOnA());
                            } else {
                                colType1(event.getPositionWorldOnA());
                            }
                        } else {
                            colType1(event.getPositionWorldOnA());
                        }
                    } else {
                        colType1(event.getPositionWorldOnA());
                    }
                } else {
                    colType1(event.getPositionWorldOnA());
                }
            }
        }

        if (event.getObjectB() == this) {
            int cg = event.getObjectA().getCollisionGroup();
            if (event.getObjectA() instanceof SwarmColControl) {
                SwarmColControl temp = (SwarmColControl) event.getObjectA();
                if (temp.getSwarm().type != this.swarm.type) {
                    if (temp.getSwarm().type == 2) {
                        swarm.setViewCollided(event.getPositionWorldOnB());
                    } else {
                        if (temp.getSwarm().type > this.swarm.type) {
                            colType1(event.getPositionWorldOnB());
                        }
                    }
                } else {
                    if (swarm.splitTime <= 0 && temp.swarm.splitTime <= 0 && swarm.merge == false && temp.swarm.merge == false) {
                        swarm.setMerge(temp.swarm);
                    }
                }
            }

            if (cg != terrainCG && cg != obstacleCG && cg != 4 && cg != 5) {
                colType1(event.getPositionWorldOnB());
            }

            if (cg == terrainCG) {
                swarm.setCollided(event.getPositionWorldOnB());
                swarm.setSolidCollisionType(2);
            }

            if (cg == obstacleCG) {
                if (event.getObjectA() instanceof RigidBodyControl) {
                    RigidBodyControl temp = (RigidBodyControl) event.getObjectA();
                    if (temp.getCollisionShape() instanceof SphereCollisionShape) {
                        SphereCollisionShape tempShape = (SphereCollisionShape) temp.getCollisionShape();
                        SphereCollisionShape tempColShape = (SphereCollisionShape) this.getCollisionShape();
                        if (tempShape.getRadius() < tempColShape.getRadius()) {
                            Vector3f moveDirect = swarm.getMoveDirection().normalize();
                            Vector3f colDirect = event.getPositionWorldOnB().subtract(swarm.getCenter()).normalize();
                            if ((Math.toDegrees(moveDirect.angleBetween(colDirect)) < 15f) && (swarm.getSize() > 50) && swarm.split == false && swarm.splitTime <= 0) {
                                swarm.setSplit(event.getPositionWorldOnB());
                            } else {
                                colType1(event.getPositionWorldOnB());
                            }
                        } else {
                            colType1(event.getPositionWorldOnB());
                        }
                    } else {
                        colType1(event.getPositionWorldOnB());
                    }
                } else {
                    colType1(event.getPositionWorldOnB());
                }
            }
        }
    }

    /**
     *
     * @return Swarm
     */
    public Swarm getSwarm() {
        return swarm;
    }

    private void colType1(Vector3f location) {
        if (swarm.getSolidCollisionType() < 2) {
            swarm.setCollided(location);
            swarm.setSolidCollisionType(1);
        }
    }
}
