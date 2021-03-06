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
package mars.energy;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.rits.cloning.Cloner;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import mars.MARS_Main;
import mars.PhysicalEnvironment;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.states.SimState;

/**
 * This is the base class for all energy harvesting devices, e.g. solar panels.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({SolarPanel.class})
public class EnergyHarvester extends PhysicalExchanger {

    protected Geometry EnergyHarvesterStart;
    private float energy = 0f;

    /**
     *
     */
    protected SimState simState;
    /**
     *
     */
    protected MARS_Main mars;
    /**
     *
     */
    protected AssetManager assetManager;

    public EnergyHarvester(){
        
    }
    
    /**
     *
     * @param simstate
     */
    protected EnergyHarvester(SimState simstate) {
        setSimState(simstate);
        variables = new HashMap<String, Object>();
    }

    /**
     *
     * @param mars
     * @param pe
     */
    protected EnergyHarvester(MARS_Main mars, PhysicalEnvironment pe) {
        this.mars = mars;
        this.pe = pe;
        this.assetManager = mars.getAssetManager();
        variables = new HashMap<String, Object>();
    }
    
    /**
     *
     * @param sensor
     */
    public EnergyHarvester(EnergyHarvester sensor) {
        HashMap<String, Object> variablesOriginal = sensor.getAllVariables();
        Cloner cloner = new Cloner();
        variables = cloner.deepClone(variablesOriginal);

        HashMap<String, Object> noisevariablesOriginal = sensor.getAllNoiseVariables();
        noises = cloner.deepClone(noisevariablesOriginal);
    }

    /**
     *
     * @param simState
     */
    @Override
    public void setSimState(SimState simState) {
        this.simState = simState;
        this.mars = this.simState.getMARS();
        this.assetManager = this.mars.getAssetManager();
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        EnergyHarvester sensor = new EnergyHarvester(this);
        sensor.initAfterJAXB();
        return sensor;
    }

    /**
     *
     */
    @Override
    public void init(Node auv_node) {
        super.init(auv_node);
        Sphere sphere7 = new Sphere(8, 8, 0.025f);
        EnergyHarvesterStart = new Geometry("PressureStart", sphere7);
        Material mark_mat7 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mark_mat7.setColor("Color", ColorRGBA.Green);
        EnergyHarvesterStart.setMaterial(mark_mat7);
        EnergyHarvesterStart.updateGeometricState();
        PhysicalExchanger_Node.setLocalTranslation(getPosition());
        Quaternion quat = new Quaternion();
        quat.fromAngles(getRotation().getX(), getRotation().getY(), getRotation().getZ());
        PhysicalExchanger_Node.setLocalRotation(quat);
        PhysicalExchanger_Node.attachChild(EnergyHarvesterStart);
        auv_node.attachChild(PhysicalExchanger_Node);
    }

    @Override
    public void update(float tpf) {

    }

    /**
     *
     * @return
     */
    public PhysicalEnvironment getPe() {
        return pe;
    }

    /**
     *
     * @param pe
     */
    public void setPe(PhysicalEnvironment pe) {
        this.pe = pe;
    }

    /**
     *
     * @return the gathered energy im mAmpere
     */
    public float getEnergy() {
        return energy;
    }

    /**
     *
     * @param energy
     */
    public void setEnergy(float energy) {
        this.energy = energy;
    }

    /**
     *
     */
    @Override
    public void reset() {
        setEnergy(0f);
    }

    @Override
    public void publishData() {
        super.publishData();
    }
}
