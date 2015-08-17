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
import com.jme3.bullet.control.RigidBodyControl;
import mars.FishSim.Swarm;

/**
 *
 * @author Mandy Feldvoß
 */
public class SwarmViewControl extends RigidBodyControl implements PhysicsCollisionListener {

    private Swarm swarm;

    /**
     *
     * @param shape
     * @param swarm
     */
    public SwarmViewControl(CollisionShape shape, Swarm swarm) {
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
            if (event.getObjectB() instanceof SwarmColControl) {
                SwarmColControl temp = (SwarmColControl) event.getObjectB();
                if (temp.getSwarm().type == 2 && swarm.type != 2 && swarm != temp.getSwarm()) {
                    float angle = (float) Math.toDegrees(swarm.getMoveDirection().normalize().angleBetween(event.getPositionWorldOnA().subtract(swarm.center).normalize()));
                    if (angle < 150) {
                        swarm.setViewCollided(temp.getSwarm().getCenter());
                    }
                }
            }
        }
        if (event.getObjectB() == this) {
            if (event.getObjectA() instanceof SwarmColControl) {
                SwarmColControl temp = (SwarmColControl) event.getObjectA();
                if (temp.getSwarm().type == 2 && swarm.type != 2 && swarm != temp.getSwarm()) {
                    float angle = (float) Math.toDegrees(swarm.getMoveDirection().normalize().angleBetween(event.getPositionWorldOnB().subtract(swarm.center).normalize()));
                    if (angle < 150) {
                        swarm.setViewCollided(temp.getSwarm().getCenter());
                    }
                }
            }
        }
    }
}
