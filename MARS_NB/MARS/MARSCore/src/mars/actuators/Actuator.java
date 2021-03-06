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
package mars.actuators;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.rits.cloning.Cloner;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import mars.Initializer;
import mars.MARS_Main;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.actuators.SpecialManipulators.Canon;
import mars.actuators.cable.Cable;
import mars.actuators.servos.Servo;
import mars.actuators.thruster.Thruster;
import mars.actuators.visualizer.PointVisualizer;
import mars.actuators.visualizer.VectorVisualizer;
import mars.states.SimState;

/**
 * This is the basic class for Actuators like Thrusters. You should extend this class if you want to implement something different like a paddle.
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({Thruster.class, Servo.class, Canon.class, VectorVisualizer.class, PointVisualizer.class, BallastTank.class, Lamp.class, Teleporter.class, Animator.class, Cable.class})
public abstract class Actuator extends PhysicalExchanger {

    /**
     *
     */
    protected SimState simState;

    public SimState getSimState() {
        return simState;
    }

    /**
     *
     */
    protected MARS_Main simauv;
    /**
     *
     */
    protected AssetManager assetManager;
    /**
     *
     */
    protected Geometry MassCenterGeom;
    /**
     *
     */
    protected Node rootNode;
    /*
     * 
     */
    private Initializer initer;
    /**
     *
     */
    protected long time = 0;
    /**
     *
     */
    protected long tf_time = 0;

    /**
     *
     */
    protected Actuator() {
        super();
    }

    /**
     *
     * @param simstate
     * @param MassCenterGeom
     */
    protected Actuator(SimState simstate, Geometry MassCenterGeom) {
        setSimState(simState);
        this.MassCenterGeom = MassCenterGeom;
        variables = new HashMap<String, Object>();
    }

    /**
     *
     * @param simState
     */
    protected Actuator(SimState simState) {
        setSimState(simState);
        variables = new HashMap<String, Object>();
    }

    /**
     *
     * @param actuator
     */
    public Actuator(Actuator actuator) {
        HashMap<String, Object> variablesOriginal = actuator.getAllVariables();
        Cloner cloner = new Cloner();
        variables = cloner.deepClone(variablesOriginal);

        HashMap<String, Object> noisevariablesOriginal = actuator.getAllNoiseVariables();
        noises = cloner.deepClone(noisevariablesOriginal);
    }

    /**
     *
     * @param simState
     */
    @Override
    public void setSimState(SimState simState) {
        this.simState = simState;
        this.simauv = simState.getMARS();
        this.assetManager = simauv.getAssetManager();
        this.rootNode = simState.getRootNode();
        this.initer = simState.getIniter();
    }

    /**
     *
     * @return
     */
    public Geometry getMassCenterGeom() {
        return MassCenterGeom;
    }

    /**
     *
     * @param MassCenterGeom
     */
    public void setMassCenterGeom(Geometry MassCenterGeom) {
        this.MassCenterGeom = MassCenterGeom;
    }

    /**
     *
     * @param pe
     */
    @Override
    public void copyValuesFromPhysicalExchanger(PhysicalExchanger pe) {
        super.copyValuesFromPhysicalExchanger(pe);
    }

    /**
     * This method updates the forces for the physicsNode so the auv moves
     */
    public abstract void updateForces();

    /**
     *
     */
    @Override
    public abstract void reset();

    /**
     *
     */
    @Override
    public void cleanup() {
        super.cleanup();
    }

    /**
     *
     * @return
     */
    public Initializer getIniter() {
        return initer;
    }

    /**
     *
     * @param initer
     */
    public void setIniter(Initializer initer) {
        this.initer = initer;
    }
}
