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

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import mars.FishSim.Fish;

/**
 *
 * @author Mandy Feldvoß
 * @author Thomas Tosik
 */
public class FishControl {

    Fish fish;

    /**
     *
     * @param fish
     */
    public FishControl(Fish fish) {
        this.fish = fish;
    }

    /**
     *
     * @param tpf Time per frame
     */
    public void swim(float tpf) {

        Vector3f steerVec = new Vector3f(0, 0, 0);

        steerVec = basicRules(steerVec);
        if (fish.swarm.getViewLocation() == null && fish.swarm.getSolidCollisionType() < 2) {
            steerVec = feed(steerVec, tpf);
        }
        if (fish.swarm.getSolidCollisionType() < 2) {
            steerVec = flee(steerVec);
        }
        steerVec = collision(steerVec);
        steerVec = waterHeight(steerVec);

        fish.rotation.lookAt(steerVec, fish.getLocalRotation().mult(Vector3f.UNIT_Y));
        Quaternion localRotation = fish.getLocalRotation();
        float slerpTime = tpf * (fish.swarm.rotationSpeed + fish.rotationSpeed);
        if (slerpTime > 1f) {
            slerpTime = 1f;
        }
        localRotation.slerp(fish.rotation, slerpTime);
        fish.setLocalRotation(localRotation);

        Vector3f moveVec = fish.getLocalRotation().mult(Vector3f.UNIT_Z);
        moveVec.multLocal(fish.moveSpeed + fish.swarm.moveSpeed + fish.swarm.escapeInc);
        moveVec.multLocal(steerVec.length());
        moveVec.multLocal(1f - (float) Math.toDegrees(moveVec.normalize().angleBetween(steerVec.normalize())) / 180f);
        // AnimationSpeed
        if (fish.channel_swim != null) {
            fish.channel_swim.setSpeed(10 * moveVec.length());
        }
        moveVec.multLocal(tpf);
        // Acceleration limit
        if (moveVec.length() > fish.lastMove.length() + fish.lastMove.length() * tpf && fish.lastMove.length() != 0f) {
            moveVec.normalizeLocal().multLocal(fish.lastMove.length() + fish.lastMove.length() * tpf);
        }
        fish.lastMove = moveVec;
        fish.setLocalTranslation(fish.getLocalTranslation().add(moveVec));

        fish.updateGeometricState();
    }

    private Vector3f basicRules(Vector3f steerVec) {
        Vector3f tempVec;
        float tempF;
        Vector3f avgMove = new Vector3f(0f, 0f, 0f);
        ArrayList<Fish> neigh = fish.swarm.getNearNeigh(fish);

        // Cohesion
        steerVec.addLocal(fish.swarm.getCenter().subtract(fish.getLocalTranslation()));

        for (int i = 0; i < neigh.size(); i++) {
            // Seperation
            tempVec = fish.getLocalTranslation().subtract(neigh.get(i).getLocalTranslation());
            tempF = tempVec.length();
            tempVec.normalizeLocal();
            tempVec.multLocal((float) Math.pow(1f - tempF / fish.swarm.getNear(), 1f));

            steerVec.addLocal(tempVec.multLocal(1 / fish.swarm.getNear()));

            // Allignment
            avgMove.add(neigh.get(i).getLastMove());
        }

        //Allignment
        avgMove.divide(neigh.size());
        steerVec.addLocal(avgMove.subtract(fish.lastMove));
        return steerVec;
    }

    private Vector3f feed(Vector3f steerVec, float tpf) {
        // Eating behaviour
        Vector3f tempVec;
        float tempF;
        tempVec = fish.swarm.getDirection(fish, tpf);
        tempVec.subtractLocal(fish.getLocalTranslation());
        if (tempVec.length() > fish.swarm.viewRadius) {
            steerVec.addLocal(tempVec.normalize());
        } else {
            tempF = tempVec.length() / fish.swarm.viewRadius;
            if (tempF >= fish.getLocalScale().z) {
                steerVec.multLocal(tempF);
                tempVec.normalizeLocal();
                steerVec.addLocal(tempVec.multLocal(1 + tempF));
            } else {
                steerVec.multLocal(fish.getLocalScale().z);
                if (tempVec.length() != 0f) {
                    tempVec.normalizeLocal();
                }
                steerVec.addLocal(tempVec);
            }
        }
        return steerVec;
    }

    private Vector3f flee(Vector3f steerVec) {
        // Escape
        Vector3f tempVec;
        tempVec = fish.swarm.getViewLocation();
        if (tempVec != null) {
            Vector3f tempVector = fish.getLocalTranslation().subtract(tempVec);
            tempVector.normalizeLocal();
            steerVec.addLocal(tempVector);
        }
        return steerVec;
    }

    private Vector3f collision(Vector3f steerVec) {
        // Collision
        Vector3f tempVec;
        tempVec = fish.swarm.getColLocation();
        if (tempVec != null) {
            Vector3f tempVector = fish.swarm.getCenter().subtract(tempVec);
            tempVector.normalizeLocal();
            steerVec.addLocal(tempVector);
        }
        return steerVec;
    }

    private Vector3f waterHeight(Vector3f steerVec) {
        // WaterHeight
        float waterHeight = fish.sim.getCurrentWaterHeight(fish.getLocalTranslation().x, fish.getLocalTranslation().z);
        if (fish.getLocalTranslation().y > waterHeight - fish.getLocalScale().length()) {
            steerVec = new Vector3f(0, -1, 0);
        }
        return steerVec;
    }
}
