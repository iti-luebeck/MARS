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
package mars.actuators.SpecialManipulators;

import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.rits.cloning.Cloner;
import java.util.HashMap;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.actuators.Actuator;
import mars.states.SimState;

/**
 * Not finished. Launches a small torpedo shaped object.
 * 
 * @author Thomas Tosik
 */
public class TorpedoLauncher extends Actuator {

    //motor
    private Geometry TorpedoLauncherStart;
    private Geometry TorpedoLauncherEnd;

    private Sphere bullet;
    private SphereCollisionShape bulletCollisionShape;
    Material mat2;

    /**
     *
     */
    public TorpedoLauncher() {
        super();
    }

    /**
     *
     * @param simstate
     * @param MassCenterGeom
     */
    public TorpedoLauncher(SimState simstate, Geometry MassCenterGeom) {
        super(simstate, MassCenterGeom);
    }

    /**
     *
     * @param simstate
     */
    public TorpedoLauncher(SimState simstate) {
        super(simstate);
    }

    /**
     *
     * @param canon
     */
    public TorpedoLauncher(TorpedoLauncher torpedo) {
        super(torpedo);
        HashMap<String, String> actionsOriginal = torpedo.getAllActions();
        Cloner cloner = new Cloner();
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        TorpedoLauncher actuator = new TorpedoLauncher(this);
        actuator.initAfterJAXB();
        return actuator;
    }

    /**
     *
     * @param pe
     */
    @Override
    public void copyValuesFromPhysicalExchanger(PhysicalExchanger pe) {
        super.copyValuesFromPhysicalExchanger(pe);
        if (pe instanceof Canon) {
            HashMap<String, String> actionOriginal = ((Canon) pe).getAllActions();
            Cloner cloner = new Cloner();
        }
    }

    /**
     * DON'T CALL THIS METHOD! In this method all the initialiasing for the
     * motor will be done and it will be attached to the physicsNode.
     */
    @Override
    public void init(Node auv_node) {
        super.init(auv_node);

    }

    @Override
    public void updateForces() {

    }

    /**
     * Create a round geometry and apply a force to it.
     */
    public void shoot() {

    }

    /**
     *
     * @param tpf
     */
    @Override
    public void update(float tpf) {

    }

    @Override
    public void reset() {

    }
}
