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

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import javax.swing.event.EventListenerList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import mars.MARS_Main;
import mars.PhysicalEnvironment;
import mars.PhysicalExchange.PhysicalExchanger;
import mars.auv.CommunicationManager;
import mars.states.SimState;

/**
 * The base class for all communicating sensors like underwater modems.
 *
 * @author Thomas Tosik <tosik at iti.uni-luebeck.de>
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlSeeAlso({UnderwaterModem.class, WiFi.class})
public abstract class CommunicationDevice extends Sensor {

    /**
     *
     */
    protected CommunicationManager com_manager;
    /**
     *
     */
    protected EventListenerList listeners = new EventListenerList();

    /**
     *
     */
    public CommunicationDevice() {
        super();
    }

    /**
     *
     * @param sensor
     */
    public CommunicationDevice(Sensor sensor) {
        super(sensor);
    }

    /**
     *
     * @param simstate
     */
    public CommunicationDevice(SimState simstate) {
        super(simstate);
    }

    /**
     *
     * @param mars
     * @param pe
     */
    public CommunicationDevice(MARS_Main mars, PhysicalEnvironment pe) {
        super(mars, pe);
    }

    /**
     *
     * @return
     */
    public CommunicationManager getCommunicationManager() {
        return com_manager;
    }

    /**
     *
     * @param com_manager
     */
    public void setCommunicationManager(CommunicationManager com_manager) {
        this.com_manager = com_manager;
    }

    @Override
    public void initAfterJAXB() {
        super.initAfterJAXB();
    }

    @Override
    public void init(Node auv_node) {
        super.init(auv_node);
    }

    @Override
    public void update(float tpf) {
    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalExchanger copy() {
        return null;
    }

    @Override
    public void reset() {
    }

    /**
     *
     * @return
     */
    public abstract Vector3f getWorldPosition();

    /**
     * 
     * @return 
     */
    public abstract SimState getSimState();
    
    /**
     *
     * @return
     */
    public abstract Float getPropagationDistance();

    /**
     *
     * @param msg
     */
    public abstract void sendToCommDevice(String msg);
    
    /**
     * 
     * @param msg 
     */
    public abstract void sendIntoNetwork(String msg);
}
