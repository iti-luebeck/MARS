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
package mars.sensors;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.rits.cloning.Cloner;
import java.util.HashMap;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import mars.MARS_Main;
import mars.PhysicalEnvironment;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.sensors.energy.EnergyHarvester;
import mars.states.SimState;

/**
 * This is a basic sensors interface. Extend from here to make your own sensors like an pressure sensor or light sensors.
 *
 * @author Thomas Tosik
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({Accelerometer.class, Gyroscope.class, InfraRedSensor.class, PingDetector.class, PressureSensor.class, SalinitySensor.class, TemperatureSensor.class, Velocimeter.class, VideoCamera.class, IMU.class, CTDSensor.class, Positionmeter.class, Orientationmeter.class, Posemeter.class, TerrainSender.class, GPSReceiver.class, AmpereMeter.class, VoltageMeter.class, FlowMeter.class, PollutionMeter.class, RayBasedSensor.class, CommunicationDevice.class, EnergyHarvester.class})
public abstract class Sensor extends PhysicalExchanger {
    /*
     * 
     */

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
    /**
     *
     */
    protected Node rootNode;
    /*
     * 
     */
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
    protected Sensor() {
    }

    /**
     *
     * @param simstate
     */
    protected Sensor(SimState simstate) {
        setSimState(simstate);
        variables = new HashMap<String, Object>();
    }

    /**
     *
     * @param mars
     * @param pe
     */
    protected Sensor(MARS_Main mars, PhysicalEnvironment pe) {
        this.mars = mars;
        this.pe = pe;
        this.assetManager = mars.getAssetManager();
        this.rootNode = mars.getRootNode();
        variables = new HashMap<String, Object>();
    }

    /**
     *
     * @param sensor
     */
    public Sensor(Sensor sensor) {
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
        this.rootNode = this.simState.getRootNode();
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
     *
     */
    @Override
    public void cleanup() {
        super.cleanup();
    }

}
