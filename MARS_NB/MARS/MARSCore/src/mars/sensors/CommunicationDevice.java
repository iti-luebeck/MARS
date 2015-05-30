/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
    public abstract Float getPropagationDistance();

    /**
     *
     * @param msg
     */
    public abstract void publish(String msg);
}
