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

import com.jme3.scene.Node;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import mars.Initializer;
import mars.MARS_Settings;
import mars.PhysicalEnvironment;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.events.AUVObjectEvent;
import mars.misc.TerrainData;
import mars.states.SimState;

/**
 * This class takes the terrain and sends it in a special format. Can be used for visualization or debugging. Mainly for ROS for now.
 *
 * @author Thomas Tosik
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class TerrainSender extends Sensor {

    private Initializer initer;
    private MARS_Settings mars_settings;
    byte[] occupany_grid;

    /**
     *
     */
    public TerrainSender() {
        super();
    }

    /**
     *
     * @param simstate
     * @param pe
     */
    public TerrainSender(SimState simstate, PhysicalEnvironment pe) {
        super(simstate);
        this.pe = pe;
    }

    /**
     *
     * @param simstate
     */
    public TerrainSender(SimState simstate) {
        super(simstate);
    }

    /**
     *
     * @param sensor
     */
    public TerrainSender(TerrainSender sensor) {
        super(sensor);
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        TerrainSender sensor = new TerrainSender(this);
        sensor.initAfterJAXB();
        return sensor;
    }

    /**
     *
     */
    @Override
    public void init(Node auv_node) {
        super.init(auv_node);
    }

    /**
     *
     * @param tpf
     */
    @Override
    public void update(float tpf) {
    }

    /**
     *
     */
    @Override
    public void reset() {
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

    /**
     *
     * @param mars_settings
     */
    public void setMarsSettings(MARS_Settings mars_settings) {
        this.mars_settings = mars_settings;
    }
    
    public TerrainData getTerrainData(){
        TerrainData dat = new TerrainData(initer.getTerrain_image_width(), initer.getTerrain_image_heigth(),mars_settings.getTerrainScale(),mars_settings.getTerrainPosition(),initer.getTerrainChannelBuffer());
        return dat;
    }
    
    @Override
    public void publishData() {
        super.publishData();
        AUVObjectEvent auvEvent = new AUVObjectEvent(this, getTerrainData(), System.currentTimeMillis());
        notifyAdvertisementAUVObject(auvEvent);
    }
}
